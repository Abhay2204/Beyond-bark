package com.example.beyondbark.model

data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Long,
    val model: String,
    val usage: Usage
)





data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
