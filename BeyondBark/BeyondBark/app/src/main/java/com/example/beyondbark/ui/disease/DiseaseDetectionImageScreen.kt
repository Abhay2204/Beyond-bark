package com.example.beyondbark.ui.disease

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beyondbark.repository.NetmindRepository
import com.example.beyondbark.ui.components.SpeciesDropdown
import com.example.beyondbark.ui.components.TopBar
import com.example.beyondbark.ui.moodDetection.getBitmapFromUri
import kotlinx.coroutines.launch

// Custom theme colors
private val BackgroundColor = Color(0xFF1B262C)
private val TextColorPrimary = Color(0xFFE0E0E0)
private val AccentColor = Color(0xFFBBE1FA)

@Composable
fun DiseaseImageInputScreen(navController: NavController) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var loading by remember { mutableStateOf(false) }
    var predictionResult by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }
    val speciesOptions = listOf("Dog", "Cat", "Bird", "Other")
    var selectedSpecies by remember { mutableStateOf(speciesOptions[0]) }
    var otherSpeciesText by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        imageBitmap = it?.let { uri -> getBitmapFromUri(context, uri) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        imageBitmap = it
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                title = "Disease by Image",
                navController = navController,
                showBackButton = true,
                showAboutButton = false
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),  // Enable full screen scroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpeciesDropdown(
                expanded = expanded,
                selectedSpecies = selectedSpecies,
                speciesOptions = speciesOptions,
                otherSpeciesText = otherSpeciesText,
                onExpandedChange = { expanded = it },
                onSpeciesSelected = { selectedSpecies = it },
                onOtherTextChanged = { otherSpeciesText = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Upload Image")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = { cameraLauncher.launch(null) }) {
                    Text("Use Camera")
                }
            }

            imageBitmap?.let {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(220.dp)
                        .background(Color.DarkGray)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val uri = imageUri
                    if (uri != null) {
                        loading = true
                        scope.launch {
                            val species = if (selectedSpecies == "Other") otherSpeciesText else selectedSpecies
                            val repo = NetmindRepository()
                            val imageUrl = repo.uploadImageToImgur(uri, context)
                            predictionResult = imageUrl?.let { repo.getDiseasePrediction(it, species) }
                            loading = false
                        }
                    }
                },
                enabled = imageUri != null && (selectedSpecies != "Other" || otherSpeciesText.isNotBlank())
            ) {
                Text("Identify Disease")
            }

            if (loading) {
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator(color = AccentColor)
            }

            predictionResult?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Prediction:",
                    fontSize = 18.sp,
                    color = AccentColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable box for long prediction text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp, max = 300.dp)
                        .background(Color(0xFF2C3E50), shape = MaterialTheme.shapes.medium)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = TextColorPrimary,
                    )
                }
            }
        }
    }
}
