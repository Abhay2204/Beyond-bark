package com.example.beyondbark.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesDropdown(
    expanded: Boolean,
    selectedSpecies: String,
    speciesOptions: List<String>,
    otherSpeciesText: String,
    onExpandedChange: (Boolean) -> Unit,
    onSpeciesSelected: (String) -> Unit,
    onOtherTextChanged: (String) -> Unit = {}
) {
    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            TextField(
                value = selectedSpecies,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select species") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                speciesOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSpeciesSelected(option)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }

        if (selectedSpecies == "Other") {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = otherSpeciesText,
                onValueChange = { onOtherTextChanged(it) },
                label = { Text("Enter species") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
