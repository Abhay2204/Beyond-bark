package com.example.beyondbark.model

data class MoodSuggestionResponse(
    val choices: List<Choice1>
)

data class Choice1(
    val message: MessageContent
)

data class MessageContent(
    val content: String
)
