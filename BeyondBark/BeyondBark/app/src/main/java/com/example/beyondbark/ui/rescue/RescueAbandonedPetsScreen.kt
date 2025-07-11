package com.example.beyondbark.ui.rescue

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.beyondbark.viewmodel.RescueViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
private val CustomBlue = Color(0xFF0F4C75)
private val CardBackground = Color(0xFFC4E1FF)
private val ScreenBackground = Color(0xFFF7FAFC) // soft light background

@Composable
fun RescueAbandonedPetsScreen(viewModel: RescueViewModel) {
    val pets by viewModel.abandonedPets.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchAbandonedPets()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(20.dp)
    ) {
        Text(
            "Abandoned Pets Needing Rescue 🐾",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(bottom = 20.dp),
            color = CustomBlue
        )
        Text(
            text = "“Every rescued animal is a soul saved. You are their hope.”",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(pets) { pet ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = pet.commonName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = CustomBlue
                        )
                        Spacer(Modifier.height(6.dp))
                        Text("🐕 Breed: ${pet.breed.ifBlank { "Unknown" }}", style = MaterialTheme.typography.bodyMedium.copy(color = CustomBlue.copy(alpha = 0.85f)))
                        Text("📍 Location: ${pet.location}", style = MaterialTheme.typography.bodyMedium.copy(color = CustomBlue.copy(alpha = 0.85f)))
                        Text("🩺 Condition: ${pet.condition}", style = MaterialTheme.typography.bodyMedium.copy(color = CustomBlue.copy(alpha = 0.85f)))
                        Text("📩 Registered by: ${pet.registeredBy} (${pet.registeredByEmail})", style = MaterialTheme.typography.bodyMedium.copy(color = CustomBlue.copy(alpha = 0.85f)))

                        val formattedDate = remember(pet.registerDate) {
                            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                .format(Date(pet.registerDate))
                        }
                        Text("🗓 Registered on: $formattedDate", style = MaterialTheme.typography.bodyMedium.copy(color = CustomBlue.copy(alpha = 0.85f)))

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val user = FirebaseAuth.getInstance().currentUser
                                if (user != null) {
                                    viewModel.rescuePet(pet.petId, user.displayName ?: "", user.email ?: "")
                                    Toast.makeText(context, "Pet accepted for rescue", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Login required to accept a pet", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .defaultMinSize(minWidth = 130.dp),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
                        ) {
                            Text("Rescue This Pet 💖", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}
