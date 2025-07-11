package com.example.beyondbark.model

data class MoodRequest(
    val model: String = "meta-llama/Llama-3.2-90B-Vision-Instruct",
    val messages: List<Message>,
    val max_tokens: Int = 3072
)

data class Message(
    val role: String,
    val content: String
)

data class Content(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

data class ImageUrl(
    val url: String
)
