package com.example.beyondbark.ui.disease

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.beyondbark.repository.MoodSuggestionRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseTextInputScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    var symptomText by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf("Dog") }
    val speciesOptions = listOf("Dog", "Cat", "Bird", "Other")
    var otherSpeciesText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disease by Text") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()

                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = symptomText,
                onValueChange = { symptomText = it },
                label = { Text("Enter symptoms") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = if (selectedSpecies == "Other") otherSpeciesText else selectedSpecies,
                    onValueChange = {
                        if (selectedSpecies == "Other") otherSpeciesText = it
                    },
                    readOnly = selectedSpecies != "Other",
                    label = {
                        Text(if (selectedSpecies == "Other") "Enter species" else "Select species")
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth()
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
                                if (species != "Other") otherSpeciesText = ""
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    resultText = ""
                    coroutineScope.launch {
                        try {
                            val repo = MoodSuggestionRepository()
                            val prompt = buildDiseasePrompt(symptomText, selectedSpecies, otherSpeciesText)
                            resultText = repo.getMoodSuggestions(prompt)
                        } catch (e: Exception) {
                            resultText = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = symptomText.isNotBlank() && (selectedSpecies != "Other" || otherSpeciesText.isNotBlank()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Diagnose Disease")
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            if (resultText.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Diagnosis Result:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp, max = 300.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

fun buildDiseasePrompt(symptomText: String, selectedSpecies: String, otherSpeciesText: String): String {
    val species = if (selectedSpecies == "Other") otherSpeciesText else selectedSpecies
    return """
        Act as a senior veterinary diagnostics AI with over a decade of field experience.
        Analyze the following symptoms described for a $species:
        
        "$symptomText"
        
        Respond with a structured report in the following format:

        Suspected Disease(s):
        [List the most likely conditions]

        Reasoning:
        [Explain how the symptoms lead to these suspicions]

        Immediate Actions:
        [Steps to take right away]

        Veterinary Advice:
        [When to consult a vet, recommended tests, urgency]

        Home Care Suggestions:
        [Practical tips for care until veterinary help is available]
        
        Always remain concise, compassionate, and clear in communication.
    """.trimIndent()
}
