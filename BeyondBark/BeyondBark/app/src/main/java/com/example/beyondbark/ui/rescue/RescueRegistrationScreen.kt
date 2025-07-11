package com.example.beyondbark.ui.rescue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// Custom Colors (match your global theme)
private val CustomBlue = Color(0xFF0F4C75)
private val CardBackground = Color(0xFFC4E1FF)
private val ButtonContainerColor = Color(0xFF0F4C75)
private val ButtonTextColor = Color.White

@Composable
fun RescueRegistrationScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(color = Color.White), // white background explicitly
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "🐾 Rescue Dashboard",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = CustomBlue,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = "“They may be lost, hurt, or scared—but your love can lead them home.”",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic
            ),
            color = CustomBlue,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        MenuItemCard(
            title = "Rescue Abandoned Pets",
            description = "View and accept abandoned pets in need of help.",
            icon = Icons.AutoMirrored.Filled.List,
            iconBackgroundColor = Color(0xFFE3F2FD),
            iconTint = CustomBlue,
            onClick = { navController.navigate("rescue_abandoned_pets") }
        )

        MenuItemCard(
            title = "Register a Rescue",
            description = "Submit details of a rescued animal and report its condition.",
            icon = Icons.Default.AddCircle,
            iconBackgroundColor = Color(0xFFD1F2EB),
            iconTint = CustomBlue,
            onClick = { navController.navigate("register_rescue") }
        )

        MenuItemCard(
            title = "View Rescued Animals",
            description = "See a list of animals that have been successfully rescued.",
            icon = Icons.Default.CheckCircle,
            iconBackgroundColor = Color(0xFFF9E79F),
            iconTint = CustomBlue,
            onClick = { navController.navigate("rescued_animals") }
        )
    }
}

@Composable
fun MenuItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(iconBackgroundColor, shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = CustomBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomBlue.copy(alpha = 0.75f)
                )
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonContainerColor,
                    contentColor = ButtonTextColor
                ),
                modifier = Modifier.defaultMinSize(minWidth = 70.dp)
            ) {
                Text(
                    text = "Go",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
