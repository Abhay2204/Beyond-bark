package com.example.beyondbark.ui.rescue

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beyondbark.viewmodel.RescueViewModel
import com.google.firebase.auth.FirebaseAuth

private val CustomBlue = Color(0xFF0F4C75)      // your main blue color
private val CardBackground = Color(0xFFF7FAFC)  // light blue card bg

@Composable
fun RegisterRescueScreen(viewModel: RescueViewModel) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Slogan at the top
        Text(
            text = "Help Us Rescue, One Paw at a Time 🐾",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = CustomBlue, // keeps from theme
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground) // your custom card bg
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Common Name") },
                    placeholder = { Text("e.g., Buddy, Luna") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Breed (optional)") },
                    placeholder = { Text("e.g., Labrador, Mixed") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    placeholder = { Text("e.g., Near Central Park") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text("Condition") },
                    placeholder = { Text("e.g., Injured leg, looks malnourished") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (name.trim().isEmpty() || location.trim().isEmpty() || condition.trim().isEmpty()) {
                            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (user != null) {
                            viewModel.registerPet(
                                name.trim(), breed.trim(), location.trim(), condition.trim(),
                                user.displayName ?: "", user.email ?: ""
                            )
                            Toast.makeText(context, "Rescue Pet Registered Successfully", Toast.LENGTH_SHORT).show()
                            name = ""; breed = ""; location = ""; condition = ""
                        } else {
                            Toast.makeText(context, "Please login to register a pet", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CustomBlue) // custom blue bg
                ) {
                    Text("Register Pet", fontSize = 16.sp) // no explicit color, so text uses theme.onPrimary automatically
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your kindness could change a life today 💙",
            fontSize = 14.sp,
            color = Color.Gray, // This is okay, it's just a subtle text color
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
