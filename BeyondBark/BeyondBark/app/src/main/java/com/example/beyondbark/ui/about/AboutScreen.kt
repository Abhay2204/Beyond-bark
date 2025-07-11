package com.example.beyondbark.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About BeyondBark") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "“Empathy in Action: Rescue, Rehome, Rejoice!”",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to BeyondBark 🐾",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Thank you for being a part of this life-saving mission. BeyondBark is a community-driven platform designed to make rescuing and rehabilitating abandoned pets seamless and impactful.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Our Mission")
            BodyText("To build a compassionate digital network for animal rescue, registration, and rehoming — connecting volunteers, shelters, and animal lovers.")

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader("Features")
            BulletList(
                items = listOf(
                    "🐶 Register abandoned animals in seconds",
                    "📍 Track location and condition of rescued pets",
                    "📂 View and manage rescued animals",
                    "✅ Accept and commit to rescue with one click",
                    "🔒 Secure login system powered by Firebase"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader("Advantages")
            BulletList(
                items = listOf(
                    "✅ Seamless and intuitive UI built with Jetpack Compose",
                    "🔄 Real-time data integration using Firebase",
                    "🌐 Community-first platform with verified rescues",
                    "📊 Transparent rescue history and ownership logs"
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Tech Stack")
            BodyText("✨ Developed using the latest technologies:")
            BulletList(
                items = listOf(
                    "🛠 **Frontend**: Kotlin + Jetpack Compose",
                    "🌐 **Backend**: Firebase Authentication, Firestore Database",
                    "☁️ **Hosting & Infra**: Firebase Cloud",
                    "🎨 **UI**: Material You Design with Compose 1.6+",
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Developed By")
            BodyText("💡 Bit Final students - 2025\nDedicated to building impactful and meaningful tech for society.")

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "You're not just using an app. You're becoming hope for a life in need. 🐾",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun BodyText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun BulletList(items: List<String>) {
    Column {
        items.forEach {
            Text(
                text = "• $it",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
