package com.example.beyondbark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beyondbark.model.RescuePet
import com.example.beyondbark.repository.RescueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class RescueViewModel : ViewModel() {

    private val repository = RescueRepository()

    private val _abandonedPets = MutableStateFlow<List<RescuePet>>(emptyList())
    val abandonedPets: StateFlow<List<RescuePet>> = _abandonedPets

    private val _rescuedPets = MutableStateFlow<List<RescuePet>>(emptyList())
    val rescuedPets: StateFlow<List<RescuePet>> = _rescuedPets

    fun registerPet(
        commonName: String,
        breed: String,
        location: String,
        condition: String,
        registeredBy: String,
        registeredByEmail: String
    ) {
        val pet = RescuePet(
            petId = UUID.randomUUID().toString(),
            commonName = commonName,
            breed = breed,
            location = location,
            condition = condition,
            registeredBy = registeredBy,
            registeredByEmail = registeredByEmail,
            registerDate = System.currentTimeMillis(),
            rescued = false // instead of isRescued

        )

        viewModelScope.launch {
            repository.registerPet(pet)
            fetchAbandonedPets()
        }
    }

    fun fetchAbandonedPets() {
        viewModelScope.launch {
            _abandonedPets.value = repository.getAbandonedPets()
        }
    }

    fun fetchRescuedPets() {
        viewModelScope.launch {
            _rescuedPets.value = repository.getRescuedPets()
        }
    }

    fun rescuePet(petId: String, rescuerName: String, rescuerEmail: String) {
        viewModelScope.launch {
            repository.rescuePet(petId, rescuerName, rescuerEmail)
            fetchAbandonedPets()
            fetchRescuedPets()
        }
    }
}
