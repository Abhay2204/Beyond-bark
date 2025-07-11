package com.example.beyondbark.ui.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beyondbark.R
import com.example.beyondbark.ui.components.TopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    val email = currentUser?.email ?: ""

    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var favoriteAnimal by remember { mutableStateOf("") }
    var favoritePet by remember { mutableStateOf("") }
    var favoriteBird by remember { mutableStateOf("") }
    val defaultPhoto = R.drawable.default_pp

    // Fetch or initialize user data
    LaunchedEffect(userId) {
        userId?.let {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(it)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    name = document.getString("name") ?: "Anonymous"
                    val dobValue = document.get("dob")
                    dob = when (dobValue) {
                        is String -> dobValue
                        is com.google.firebase.Timestamp -> {
                            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                            sdf.format(dobValue.toDate())
                        }
                        else -> "01/01/1990"
                    }

                    gender = document.getString("gender") ?: ""
                    phone = document.getString("phone") ?: ""
                    favoriteAnimal = document.getString("favoriteAnimal") ?: ""
                    favoritePet = document.getString("favoritePet") ?: ""
                    favoriteBird = document.getString("favoriteBird") ?: ""
                } else {
                    val defaultData = hashMapOf(
                        "name" to "Anonymous",
                        "dob" to "01/01/1990",
                        "gender" to "Unknown",
                        "phone" to "N/A",
                        "favoriteAnimal" to "Dog",
                        "favoritePet" to "Golden Retriever",
                        "favoriteBird" to "Parrot",
                        "photoUrl" to "default_pp"
                    )
                    userRef.set(defaultData)
                    name = "Anonymous"
                    dob = "01/01/1990"
                    gender = "Unknown"
                    phone = "N/A"
                    favoriteAnimal = "Dog"
                    favoritePet = "Golden Retriever"
                    favoriteBird = "Parrot"
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Profile",
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
                .verticalScroll(rememberScrollState())
        ) {
            // Profile header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0288D1), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = defaultPhoto),
                    contentDescription = "User Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(50.dp))
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = email,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection("Date of Birth", dob)
            ProfileSection("Gender", gender)
            ProfileSection("Phone Number", phone)
            ProfileSection("Favorite Animal", favoriteAnimal)
            ProfileSection("Favorite Pet", favoritePet)
            ProfileSection("Favorite Bird", favoriteBird)

            Spacer(modifier = Modifier.height(24.dp))

            // Edit button
            Button(
                onClick = {
                    navController.navigate("edit_profile")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
            ) {
                Text("Edit Profile", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout button
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(navController.context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
    }
}
