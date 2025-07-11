package com.example.beyondbark.repository

import com.example.beyondbark.model.RescuePet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RescueRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val rescueCollection = firestore.collection("rescue_pets")

    suspend fun registerPet(pet: RescuePet) {
        rescueCollection.document(pet.petId).set(pet).await()
    }

    suspend fun getAbandonedPets(): List<RescuePet> {
        val snapshot = rescueCollection.whereEqualTo("rescued", false).get().await() // updated field name
        return snapshot.toObjects(RescuePet::class.java)
    }

    suspend fun getRescuedPets(): List<RescuePet> {
        val snapshot = rescueCollection.whereEqualTo("rescued", true).get().await() // updated field name
        return snapshot.toObjects(RescuePet::class.java)
    }

    suspend fun rescuePet(petId: String, rescuerName: String, rescuerEmail: String) {
        val updates = mapOf(
            "rescued" to true, // updated field name
            "rescuedBy" to rescuerName,
            "rescuedByEmail" to rescuerEmail,
            "rescueDate" to System.currentTimeMillis()
        )
        rescueCollection.document(petId).update(updates).await()
    }
}
