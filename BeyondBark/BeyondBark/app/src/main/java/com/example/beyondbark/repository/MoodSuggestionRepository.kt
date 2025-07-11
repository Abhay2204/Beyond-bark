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

class MoodSuggestionRepository {

    companion object {
        private const val TAG = "MoodSuggestionRepo"
    }

    suspend fun getMoodSuggestions(mood: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Act as an expert veterinary assistant with 10+ years of experience.
            A pet is showing mood: "$mood".
            Provide well-formatted and detailed guidance in the following exact structure, using clear section headings:

            Mood Explanation:
            [Explain the mood in simple terms]

            What to Do:
            [Actionable steps the owner should take]

            What to Feed:
            [Dietary suggestions appropriate for this mood]

            Any Additional Care Tips:
            [Extra tips, precautions, or monitoring advice]

            Make sure to use the above headings exactly and ensure clarity.
        """.trimIndent()

        val request = MoodSuggestionRequest(
            model = "meta-llama/Llama-4-Scout-17B-16E-Instruct",
            messages = listOf(
                Message1(role = "system", content = "Act like you are a helpful assistant."),
                Message1(role = "user", content = prompt)
            ),
            max_tokens = 10240
        )

        Log.d(TAG, "Sending request with mood: $mood")

        return@withContext try {
            val response: Response<MoodSuggestionResponse> = RetrofitInstance.api.getMoodSuggestions(request)

            Log.d(TAG, "Response Code: ${response.code()}")
            Log.d(TAG, "Response Message: ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                val suggestion = body?.choices?.firstOrNull()?.message?.content

                Log.d(TAG, "Suggestion Content: $suggestion")
                suggestion ?: "No suggestion content found."
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "API call failed with code ${response.code()} and message ${response.message()}")
                Log.e(TAG, "Error Body: $errorBody")
                "Failed to fetch suggestions. Error code: ${response.code()}"
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
