package com.example.beyondbark.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beyondbark.ui.components.TopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditProfileScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    var favoriteAnimal by remember { mutableStateOf("") }
    var favoritePet by remember { mutableStateOf("") }
    var favoriteBird by remember { mutableStateOf("") }
    val defaultPhotoUrl = "default_pp"

    // Load current user data from Firestore to populate the fields
    LaunchedEffect(userId) {
        userId?.let {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(it)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    favoriteAnimal = document.getString("favoriteAnimal") ?: ""
                    favoritePet = document.getString("favoritePet") ?: ""
                    favoriteBird = document.getString("favoriteBird") ?: ""
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Edit Profile",
                navController = navController,
                showBackButton = true,
                showAboutButton = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Favorite Animal
            Text("Favorite Animal", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            OutlinedTextField(
                value = favoriteAnimal,
                onValueChange = { favoriteAnimal = it },
                label = { Text("Favorite Animal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Favorite Pet
            Text("Favorite Pet", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            OutlinedTextField(
                value = favoritePet,
                onValueChange = { favoritePet = it },
                label = { Text("Favorite Pet") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Favorite Bird
            Text("Favorite Bird", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            OutlinedTextField(
                value = favoriteBird,
                onValueChange = { favoriteBird = it },
                label = { Text("Favorite Bird") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Changes Button
            Button(
                onClick = {
                    userId?.let {
                        val db = FirebaseFirestore.getInstance()
                        val userRef = db.collection("users").document(it)

                        val updatedData = hashMapOf(
                            "favoriteAnimal" to favoriteAnimal,
                            "favoritePet" to favoritePet,
                            "favoriteBird" to favoriteBird,
                            "photoUrl" to defaultPhotoUrl
                        )

                        userRef.update(updatedData as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(navController.context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack() // Go back to the Profile screen
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(navController.context, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
            ) {
                Text("Save Changes", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel Button
            Button(
                onClick = {
                    navController.popBackStack() // Go back to the Profile screen without saving
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    }
}
