package com.example.beyondbark.ui.disease

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beyondbark.R
import com.example.beyondbark.ui.components.TopBar
import com.example.beyondbark.ui.home.MenuCardWithImage

// Custom theme colors
private val BackgroundColor = Color(0xFF1B262C)
private val AccentColor = Color(0xFFBBE1FA)

@Composable
fun DiseaseDetectionChoiceScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Disease Identification",
                navController = navController,
                showBackButton = true,
                showAboutButton = false
            )
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Input Method",
                fontSize = 24.sp,
                color = AccentColor,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            MenuCardWithImage(
                title = "Identify by Image",
                imageResId = R.drawable.identify_by_image
            ) {
                navController.navigate("disease_by_image")
            }

            Spacer(modifier = Modifier.height(20.dp))

            MenuCardWithImage(
                title = "Identify by Symptoms Text",
                imageResId = R.drawable.identify_by_text
            ) {
                navController.navigate("disease_by_text")
            }
        }
    }
}
