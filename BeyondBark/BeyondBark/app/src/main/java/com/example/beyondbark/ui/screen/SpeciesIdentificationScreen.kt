package com.example.beyondbark.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.beyondbark.repository.NetmindRepository
import kotlinx.coroutines.launch

@Composable
fun SpeciesIdentificationScreen(repository: NetmindRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var result by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            result = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B)) // Dark blue-gray background
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Species Identification",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFBBDEFB), // Light blue text
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (selectedImageUri != null) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263449)), // Slightly lighter card bg
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF2E3B55))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "No image selected",
                    tint = Color(0xFF90CAF9),
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Button(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0288D1),
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Upload", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Select Image", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    selectedImageUri?.let {
                        loading = true
                        result = null
                        try {
                            val uploadedUrl = repository.uploadImageToImgur(it, context)
                            imageUrl = uploadedUrl

                            if (uploadedUrl != null) {
                                val response = repository.getSpeciesIdentification(uploadedUrl)
                                result = response
                            } else {
                                result = "Image upload failed"
                            }
                        } catch (e: Exception) {
                            result = "Something went wrong: ${e.localizedMessage}"
                        } finally {
                            loading = false
                        }
                    }
                }
            },
            enabled = selectedImageUri != null && !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedImageUri != null && !loading) Color(0xFF0288D1) else Color(0xFF90A4AE),
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Search, contentDescription = "Identify", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Identify Species", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        AnimatedVisibility(visible = loading, enter = fadeIn(), exit = fadeOut()) {
            CircularProgressIndicator(
                color = Color(0xFF81D4FA),
                strokeWidth = 4.dp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        AnimatedVisibility(visible = result != null && !loading, enter = fadeIn(), exit = fadeOut()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 420.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263449)),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    val scroll = rememberScrollState()
                    Column(modifier = Modifier.verticalScroll(scroll)) {
                        Text(
                            text = result ?: "",
                            fontSize = 16.sp,
                            color = Color(0xFFCFD8DC),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
