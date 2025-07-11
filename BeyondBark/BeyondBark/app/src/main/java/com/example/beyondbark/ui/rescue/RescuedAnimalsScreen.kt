package com.example.beyondbark.ui.rescue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.beyondbark.viewmodel.RescueViewModel
import java.text.SimpleDateFormat
import java.util.*

// Custom theme colors
private val CustomBlue = Color(0xFF0F4C75)      // Dark blue for text and accents
private val CardBackground = Color(0xFFF7FAFC)  // Light card background

@Composable
fun RescuedAnimalsScreen(viewModel: RescueViewModel) {
    val pets by viewModel.rescuedPets.collectAsState()

    LaunchedEffect(true) {
        viewModel.fetchRescuedPets()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Rescued Animals ❤️",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = CustomBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "“Saving one animal won’t change the world, but surely, for that one animal, the world will change forever.”",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic
            ),
            color = CustomBlue,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.CenterHorizontally) // 👈 centers it in Column
        )


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(pets) { pet ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = pet.commonName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = CustomBlue
                        )
                        Spacer(Modifier.height(6.dp))

                        InfoLine("🐶 Breed:", pet.breed.ifBlank { "Unknown" })
                        InfoLine("📍 Location:", pet.location)
                        InfoLine("🩺 Condition:", pet.condition)
                        InfoLine("📩 Registered by:", "${pet.registeredBy} (${pet.registeredByEmail})")

                        val registerDateFormatted = remember(pet.registerDate) {
                            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                .format(Date(pet.registerDate))
                        }
                        InfoLine("🗓 Registered on:", registerDateFormatted)

                        InfoLine("🛟 Rescued by:", "${pet.rescuedBy} (${pet.rescuedByEmail})")

                        val rescueDateFormatted = remember(pet.rescueDate) {
                            pet.rescueDate?.let {
                                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                    .format(Date(it))
                            } ?: "Not available"
                        }
                        InfoLine("🚑 Rescue Date:", rescueDateFormatted)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Text(
        text = "$label $value",
        style = MaterialTheme.typography.bodyMedium,
        color = CustomBlue,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
