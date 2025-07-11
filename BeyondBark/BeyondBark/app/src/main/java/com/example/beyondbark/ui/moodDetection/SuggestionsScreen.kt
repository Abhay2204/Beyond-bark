package com.example.beyondbark.ui.moodDetection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.beyondbark.repository.MoodSuggestionRepository
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionsScreen(encodedMood: String?, navController: NavHostController) {
    val suggestionRepo = remember { MoodSuggestionRepository() }

    var shouldRetry by remember { mutableStateOf(false) }
    var rawSuggestion by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val decodedMood = remember(encodedMood) {
        encodedMood?.let {
            try {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                null
            }
        }
    }

    LaunchedEffect(decodedMood) {
        if (!decodedMood.isNullOrBlank()) {
            isLoading = true
            rawSuggestion = null
            errorText = null
            try {
                rawSuggestion = suggestionRepo.getMoodSuggestions(decodedMood)
            } catch (e: Exception) {
                errorText = "Failed to fetch suggestions: ${e.message}"
                shouldRetry = true
            }
            isLoading = false
        } else {
            errorText = "Mood data is missing or invalid."
        }
    }

    LaunchedEffect(shouldRetry) {
        if (shouldRetry && !decodedMood.isNullOrBlank()) {
            shouldRetry = false
            kotlinx.coroutines.delay(2000)
            isLoading = true
            rawSuggestion = null
            errorText = null
            try {
                rawSuggestion = suggestionRepo.getMoodSuggestions(decodedMood)
            } catch (e: Exception) {
                errorText = "Failed to fetch suggestions: ${e.message}"
                shouldRetry = true
            }
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mood Suggestions",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> {
                    Spacer(modifier = Modifier.height(40.dp))
                    CircularProgressIndicator(color = Color(0xFF03A9F4))
                }

                errorText != null -> {
                    ErrorCard(errorText!!)
                }

                rawSuggestion != null -> {
                    val parsed = parseSuggestion(rawSuggestion!!)
                    SuggestionCard("Mood Explanation", parsed["Mood Explanation"] ?: "", Color(0xFF263238))
                    SuggestionCard("What to Do", parsed["What to Do"] ?: "", Color(0xFF1E3A5F))
                    SuggestionCard("What to Feed", parsed["What to Feed"] ?: "", Color(0xFF2E7D32))
                    SuggestionCard("Additional Care Tips", parsed["Any Additional Care Tips"] ?: "", Color(0xFF4A148C))
                }

                else -> {
                    Text(
                        "No suggestions available.",
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorCard(errorMessage: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Error", fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.White)
        }
    }
}

@Composable
fun SuggestionCard(title: String, content: String, backgroundColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}


// Parses a raw suggestion string into a structured map
fun parseSuggestion(text: String): Map<String, String> {
    val result = mutableMapOf<String, String>()

    // Normalize line endings and collapse extra whitespace
    val cleanedText = text.replace("\r", "").trim()

    // Use more flexible regex patterns to handle variations
    val moodExplanationRegex = "(?i)(?:1\\.|\\*|-)?\\s*Mood explanation[:\\-\\s]*(.*?)(?=\\n\\s*(?:2\\.|What to do[:\\-])|$)".toRegex(RegexOption.DOT_MATCHES_ALL)
    val whatToDoRegex = "(?i)(?:2\\.|\\*|-)?\\s*What to do[:\\-\\s]*(.*?)(?=\\n\\s*(?:3\\.|What to feed[:\\-])|$)".toRegex(RegexOption.DOT_MATCHES_ALL)
    val whatToFeedRegex = "(?i)(?:3\\.|\\*|-)?\\s*What to feed[:\\-\\s]*(.*?)(?=\\n\\s*(?:4\\.|Any additional care tips[:\\-])|$)".toRegex(RegexOption.DOT_MATCHES_ALL)
    val additionalTipsRegex = "(?i)(?:4\\.|\\*|-)?\\s*Any additional care tips[:\\-\\s]*(.*)".toRegex(RegexOption.DOT_MATCHES_ALL)

    result["Mood Explanation"] = moodExplanationRegex.find(cleanedText)?.groupValues?.get(1)?.trim().orEmpty()
    result["What to Do"] = whatToDoRegex.find(cleanedText)?.groupValues?.get(1)?.trim().orEmpty()
    result["What to Feed"] = whatToFeedRegex.find(cleanedText)?.groupValues?.get(1)?.trim().orEmpty()
    result["Any Additional Care Tips"] = additionalTipsRegex.find(cleanedText)?.groupValues?.get(1)?.trim().orEmpty()

    return result
}
