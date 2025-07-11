package com.example.beyondbark.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beyondbark.R
import com.example.beyondbark.ui.components.TopBar

// Custom color scheme (can be adapted or moved to theme)
private val BackgroundColor = Color(0xFF1B262C) // dark blue-gray
private val CardDefaultColor = Color(0xFF0F4C75) // deep blue
private val CardPressedColor = Color(0xFF3282B8) // lighter blue
private val TextColorPrimary = Color(0xFFE0E0E0) // off-white
private val AccentColor = Color(0xFFBBE1FA) // soft sky blue
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopBar(
                title = "BeyondBark",
                navController = navController,
                showBackButton = false,
                showAboutButton = true
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to BeyondBark 🐾",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AccentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Your trusted companion for pet health and happiness",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextColorPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            // Using your own logos in drawable folder
            MenuCard(
                title = "Pet Mood Detection",
                logoRes = R.drawable.ic_pet_mood, // replace with your image
                onClick = { navController.navigate("mood_detection") }
            )
            MenuCard(
                title = "Pet Chatbot",
                logoRes = R.drawable.ic_pet_chatbot,
                onClick = { navController.navigate("pet_chatbot") }
            )
            MenuCard(
                title = "Disease Detection",
                logoRes = R.drawable.ic_disease_detection,
                onClick = { navController.navigate("disease_detection") }
            )
            MenuCard(
                title = "Species Identification",
                logoRes = R.drawable.ic_species_identification,
                onClick = { navController.navigate("species_identification") }
            )
            MenuCard(
                title = "Rescue Registration",
                logoRes = R.drawable.ic_rescue_registration,
                onClick = { navController.navigate("rescue_registration") }
            )
            MenuCard(
                title = "Profile",
                logoRes = R.drawable.ic_profile,
                onClick = { navController.navigate("profile") }
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    logoRes: Int,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (pressed) CardPressedColor else CardDefaultColor,
        label = "CardBackgroundColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        label = "CardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(72.dp)
            .scale(scale)
            .clickable {
                pressed = true
                onClick()
                pressed = false
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color = AccentColor.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = title,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextColorPrimary,
                maxLines = 1
            )
        }
    }
}
