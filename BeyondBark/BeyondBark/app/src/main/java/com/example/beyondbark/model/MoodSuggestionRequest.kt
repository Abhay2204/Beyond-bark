package com.example.beyondbark.model

data class MoodSuggestionRequest(
    val model: String,
    val messages: List<Message1>,
    val max_tokens: Int
)

data class Message1(
    val role: String,
    val content: String
)
