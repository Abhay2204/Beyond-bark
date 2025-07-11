package com.example.beyondbark.ui.chatbot

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beyondbark.R
import com.example.beyondbark.repository.AskPetChatbotRepository
import com.example.beyondbark.ui.components.TopBar
import kotlinx.coroutines.launch

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PetChatbotScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var userInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var loading by remember { mutableStateOf(false) }
    val repo = remember { AskPetChatbotRepository() }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopBar(
                title = "Pet Chatbot",
                navController = navController,
                showBackButton = true,
                showAboutButton = false
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask me anything...") },
                    enabled = !loading,
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            val question = userInput
                            userInput = ""
                            loading = true
                            scope.launch {
                                try {
                                    chatHistory = (chatHistory + (question to "typing...")).toMutableList()
                                    val answer = repo.askChatbot(question)
                                    chatHistory = (chatHistory.dropLast(1).toMutableList() + (question to answer)).toMutableList()
                                } catch (e: Exception) {
                                    chatHistory = (chatHistory.dropLast(1).toMutableList() +
                                            (question to "Error: ${e.localizedMessage}")).toMutableList()
                                } finally {
                                    loading = false
                                }
                            }
                        }
                    },
                    enabled = !loading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Send")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                reverseLayout = false
            ) {
                items(chatHistory) { (question, answer) ->
                    ChatBubble(text = question, isUser = true)
                    Spacer(modifier = Modifier.height(4.dp))
                    ChatBubble(text = answer, isUser = false, isTyping = answer == "typing...")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            LaunchedEffect(chatHistory.size) {
                listState.animateScrollToItem(chatHistory.size)
            }
        }
    }
}
@Composable
fun ChatBubble(text: String, isUser: Boolean, isTyping: Boolean = false) {
    val bgColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    val alignment = if (isUser) Arrangement.End else Arrangement.Start
    val avatarResId = if (isUser) R.drawable.chat else R.drawable.petchat

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            AvatarIcon(drawableResId = avatarResId)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            if (isTyping) {
                TypingDots()
            } else {
                Text(text = text, color = textColor, fontSize = 16.sp)
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            AvatarIcon(drawableResId = avatarResId)
        }
    }
}


@Composable
fun AvatarIcon(drawableResId: Int) {
    Image(
        painter = painterResource(id = drawableResId),
        contentDescription = null,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
    )
}


@Composable
fun TypingDots() {
    Row(modifier = Modifier.width(40.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f))
            )
        }
    }
}
