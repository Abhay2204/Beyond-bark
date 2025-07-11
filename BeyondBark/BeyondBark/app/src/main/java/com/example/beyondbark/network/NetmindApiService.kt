package com.example.beyondbark.network

import com.example.beyondbark.model.ChatResponse
import com.example.beyondbark.model.MoodRequest
import com.example.beyondbark.model.MoodResponse
import com.example.beyondbark.model.MoodSuggestionRequest
import com.example.beyondbark.model.MoodSuggestionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NetmindApiService {

    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun getMoodPrediction(@Body request: MoodRequest): Response<MoodResponse>

    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun getMoodSuggestions(@Body request: MoodSuggestionRequest): Response<MoodSuggestionResponse>

    // ✅ Correct method for AskPetRepository (uses ChatResponse model)
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun askPetChatbot(@Body request: MoodSuggestionRequest): Response<ChatResponse>
}
