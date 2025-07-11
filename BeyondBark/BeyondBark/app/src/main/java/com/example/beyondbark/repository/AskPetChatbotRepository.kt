package com.example.beyondbark.repository

import android.util.Log
import com.example.beyondbark.model.Message1
import com.example.beyondbark.model.MoodSuggestionRequest
import com.example.beyondbark.model.MoodSuggestionResponse
import com.example.beyondbark.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class AskPetChatbotRepository {

    companion object {
        private const val TAG = "AskPetChatbotRepo"
    }

    suspend fun askChatbot(userQuery: String): String = withContext(Dispatchers.IO) {
        val prompt = """
    Act as an expert veterinary assistant with 10+ years of experience.
    A pet owner asked: "$userQuery".
    Provide a brief, clear, and valuable answer in 2-3 sentences or bullet points.
    Avoid lengthy explanations, but make sure the answer is informative and helpful.
""".trimIndent()

        val request = MoodSuggestionRequest(
            model = "meta-llama/Llama-4-Scout-17B-16E-Instruct",
            messages = listOf(
                Message1(role = "system", content = "Act like you are a helpful assistant."),
                Message1(role = "user", content = prompt)
            ),
            max_tokens = 10240
        )

        Log.d(TAG, "Sending chatbot query: $userQuery")

        return@withContext try {
            val response: Response<MoodSuggestionResponse> = RetrofitInstance.api.getMoodSuggestions(request)

            Log.d(TAG, "Response Code: ${response.code()}")
            Log.d(TAG, "Response Message: ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                val reply = body?.choices?.firstOrNull()?.message?.content

                Log.d(TAG, "Chatbot Reply: $reply")
                reply ?: "No response from chatbot."
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "API call failed with code ${response.code()} and message ${response.message()}")
                Log.e(TAG, "Error Body: $errorBody")
                "Failed to fetch chatbot response. Error code: ${response.code()}"
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network I/O error: ${e.localizedMessage}", e)
            "Network error: ${e.localizedMessage}"
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception during API call: ${e.localizedMessage}", e)
            "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }
}
