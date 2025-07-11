@file:Suppress("DEPRECATION")

package com.example.beyondbark.ui.moodDetection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beyondbark.repository.MoodSuggestionRepository
import com.example.beyondbark.repository.NetmindRepository
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetMoodDetectionScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { NetmindRepository() }
    val suggestionRepository = remember { MoodSuggestionRepository() }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var moodPrediction by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var logMessages by remember { mutableStateOf<List<String>>(emptyList()) }
    var moodSuggestions by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }
    val speciesOptions = listOf("Dog", "Cat", "Bird", "Other")
    var selectedSpecies by remember { mutableStateOf(speciesOptions[0]) }
    var otherSpeciesText by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            imageBitmap = getBitmapFromUri(context, it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            imageBitmap = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Mood Detection", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0288D1))
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212)) // Dark blue-gray background
                    .padding(padding)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                            ) {
                                Text("Choose Image", color = Color.White)
                            }
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                            ) {
                                Text("Take Photo", color = Color.White)
                            }
                        }
                    }

                    imageBitmap?.let { bitmap ->
                        item {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Selected Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }

                    item {
                        Text("Species", fontWeight = FontWeight.SemiBold, color = Color.White)
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            TextField(
                                value = selectedSpecies,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.textFieldColors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White,
                                    focusedContainerColor = Color(0xFF1E1E1E),
                                    unfocusedContainerColor = Color(0xFF1E1E1E),
                                    cursorColor = Color.White
                                )

                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                speciesOptions.forEach { species ->
                                    DropdownMenuItem(
                                        text = { Text(species) },
                                        onClick = {
                                            selectedSpecies = species
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (selectedSpecies == "Other") {
                        item {
                            TextField(
                                value = otherSpeciesText,
                                onValueChange = { otherSpeciesText = it },
                                label = { Text("Enter Species", color = Color.White) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1E1E1E),
                                    unfocusedContainerColor = Color(0xFF1E1E1E),
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White
                                )
                            )

                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (imageBitmap == null) {
                                    Toast.makeText(context, "Please select or capture an image", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isLoading = true
                                scope.launch {
                                    try {
                                        val imageUrl = repository.uploadImageToImgur(imageUri ?: return@launch, context)
                                        val species = if (selectedSpecies == "Other") otherSpeciesText else selectedSpecies
                                        val result = repository.getPetMoodPrediction(imageUrl ?: return@launch, species)
                                        moodPrediction = result
                                    } catch (e: Exception) {
                                        moodPrediction = "Error: ${e.localizedMessage}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                        ) {
                            Text("Detect Mood", color = Color.White)
                        }
                    }

                    moodPrediction?.let {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Prediction Result:", fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(Modifier.height(6.dp))
                                    Text(it, color = Color.White)
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val suggestions = suggestionRepository.getMoodSuggestions(it)
                                            moodSuggestions = suggestions
                                            val encoded = URLEncoder.encode(suggestions, StandardCharsets.UTF_8.toString())
                                            navController.navigate("suggestionsScreen/$encoded")
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                            ) {
                                Text("Show Suggestions", color = Color.White)
                            }
                        }
                    }

                    if (logMessages.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Logs", fontWeight = FontWeight.SemiBold, color = Color.White)
                                    logMessages.forEach {
                                        Text("• $it", fontSize = 13.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }

        }
    )
}


fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun encodeImageToBase64(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val byteArray = baos.toByteArray()
    return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
}
