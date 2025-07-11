package com.example.beyondbark.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.beyondbark.model.Message
import com.example.beyondbark.model.MoodRequest
import com.example.beyondbark.network.NetmindApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class NetmindRepository {

    private val api: NetmindApiService

    companion object {
        private const val BASE_URL = "https://api.netmind.ai/inference-api/openai/v1/"
        private const val IMGUR_CLIENT_ID = "208f437e9c253c3"
        private const val API_KEY = "Bearer 60a051b94e6b40c18b10faf6a79c4e2c"
    }

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", API_KEY)
                    .build()
                Log.d("NetmindRepo", "Request Intercepted: ${request.url}")
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NetmindApiService::class.java)
        Log.d("NetmindRepo", "Retrofit initialized with BASE_URL = $BASE_URL")
    }

    suspend fun uploadImageToImgur(uri: Uri, context: Context): String? =
        withContext(Dispatchers.IO) {
            try {
                Log.d("ImgurUpload", "Converting URI to file...")
                val file = uriToFile(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

                Log.d("ImgurUpload", "Uploading file: ${file.name}, size: ${file.length()} bytes")

                val request = Request.Builder()
                    .url("https://api.imgbb.com/1/upload?key=0a0dccaf0897f0a4cff80064fdf54160")
                    .post(
                        MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", file.name, requestFile)
                            .build()
                    )
                    .build()

                val response = OkHttpClient().newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("ImgurUpload", "Response Code: ${response.code}")
                Log.d("ImgurUpload", "Response Body: $responseBody")

                if (response.isSuccessful) {
                    return@withContext parseImgbbResponse(responseBody)
                } else {
                    Log.e("ImgurUpload", "Upload failed: ${response.message}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ImgurUpload", "Upload error: ${e.message}", e)
                null
            }
        }

    private fun parseImgbbResponse(response: String?): String? {
        Log.d("ImgurResponse", "Parsing response: $response")
        val regex = Regex(""""url":"(http[^"]+)"""")
        val link = response?.let { regex.find(it)?.groups?.get(1)?.value?.replace("\\/", "/") }
        Log.d("ImgurResponse", "Parsed link: $link")
        return link
    }


//    private fun parseImgurResponse(response: String?): String? {
//        Log.d("ImgurResponse", "Parsing response: $response")
//        val regex = Regex(""""link":"(http[^"]+)"""")
//        val link = response?.let { regex.find(it)?.groups?.get(1)?.value?.replace("\\/", "/") }
//        Log.d("ImgurResponse", "Parsed link: $link")
//        return link
//    }

    suspend fun getPetMoodPrediction(imageUrl: String, selectedSpecies: String): String =
        withContext(Dispatchers.IO) {
            Log.d("MoodPrediction", "Generating prompt for image: $imageUrl and species: $selectedSpecies")

            val prompt = """
                You are a highly intelligent vision-language model. Analyze the image at the following URL: $imageUrl

                The user has indicated that the species is: $selectedSpecies.

                First, confirm if this is consistent with the image.

                Then, determine whether the subject is a pet or a wild animal.

                Finally, describe the emotional or behavioral state of the subject in **one clear, concise sentence** based only on visible cues such as posture, facial expression, or activity.

                Respond only with your final analysis sentence.
            """.trimIndent()

            val request = MoodRequest(messages = listOf(Message("user", prompt)))
            val response = api.getMoodPrediction(request)

            val result = response.body()?.choices?.firstOrNull()?.message?.content
            Log.d("MoodPrediction", "API Response: $result")

            result ?: "No prediction found"
        }

    private fun uriToFile(context: Context, uri: Uri): File {
        Log.d("UriToFile", "Converting URI to File: $uri")
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile("upload", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        Log.d("UriToFile", "File created at: ${file.absolutePath}")
        return file
    }

    suspend fun getDiseasePrediction(imageUrl: String, species: String): String =
        withContext(Dispatchers.IO) {
            Log.d("DiseasePrediction", "Predicting disease for image: $imageUrl and species: $species")

            val prompt = """
                You are a veterinary assistant AI.
                Analyze this pet image: $imageUrl
                The species is: $species.
                Predict any disease or visible health issue and explain in one concise sentence.
            """.trimIndent()

            val request = MoodRequest(messages = listOf(Message("user", prompt)))
            val response = api.getMoodPrediction(request)
            val result = response.body()?.choices?.firstOrNull()?.message?.content

            Log.d("DiseasePrediction", "API Response: $result")

            result ?: "No prediction found"
        }

    suspend fun getSpeciesIdentification(imageUrl: String): String = withContext(Dispatchers.IO) {
        Log.d("SpeciesIdentification", "Identifying species from image: $imageUrl")

        val prompt = """
            You are a wildlife biologist AI with expertise in zoology and veterinary science and have 20 years of experice in identifying any living creature.
            Given this image: $imageUrl
            The species is likely a dog.

            Identify the species shown in the image as accurately as possible.

            Then provide the following structured details:
            Species Name:
            [Scientific and common name]

            breed Name:
            [breed name]

            Ideal Climate:
            [Temperature range, humidity, and general environment preferences]

            What It Eats:
            [Primary diet, feeding behavior, and special dietary needs]

            Habitat & Behavior:
            [Preferred habitat, social behavior, and activity patterns]

            Care or Observation Tips:
            [If domesticated or observed in the wild, include tips for care or safe observation]

            Respond clearly using the above headings. Avoid disclaimers or vague statements.
        """.trimIndent()

        val request = MoodRequest(messages = listOf(Message("user", prompt)))
        val response = api.getMoodPrediction(request)
        val result = response.body()?.choices?.firstOrNull()?.message?.content

        Log.d("SpeciesIdentification", "API Response: $result")

        result ?: "No species info found"
    }






}
