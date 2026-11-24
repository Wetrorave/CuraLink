package com.curalink.data.repository

import com.curalink.data.model.Appointment
import com.curalink.data.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CuraLinkRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun currentUserId(): String? = auth.currentUser?.uid
    fun currentUserEmail(): String = auth.currentUser?.email.orEmpty()

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
        Unit
    }

    suspend fun register(email: String, password: String): Result<Unit> = runCatching {
        auth.createUserWithEmailAndPassword(email.trim(), password).await()
        Unit
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getDoctors(): Result<List<Doctor>> = runCatching {
        val snapshot = db.collection("doctors").get().await()
        val doctors = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Doctor::class.java)?.copy(id = doc.id)
        }
        if (doctors.isNotEmpty()) {
            doctors
        } else {
            sampleDoctors()
        }
    }

    suspend fun createAppointment(appointment: Appointment): Result<String> = runCatching {
        val docRef = db.collection("appointments").document()
        val item = appointment.copy(id = docRef.id)
        docRef.set(item).await()
        docRef.id
    }

    suspend fun getAppointmentsForUser(userId: String): Result<List<Appointment>> = runCatching {
        val snapshot = db.collection("appointments")
            .whereEqualTo("patientId", userId)
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            doc.toObject(Appointment::class.java)?.copy(id = doc.id)
        }
    }

    private fun sampleDoctors(): List<Doctor> = listOf(
        Doctor(
            id = "doc-1",
            name = "Dra. Mariana Lima",
            specialty = "Cardiologia",
            location = "Centro Médico Manaíra",
            biography = "Especialista em prevenção cardiovascular e acompanhamento clínico contínuo.",
            availableSlots = listOf("08:30", "10:00", "14:30", "16:00")
        ),
        Doctor(
            id = "doc-2",
            name = "Dr. Rafael Almeida",
            specialty = "Dermatologia",
            location = "Clínica Tambaú",
            biography = "Atendimento dermatológico clínico, estético e diagnóstico preventivo.",
            availableSlots = listOf("09:00", "11:00", "15:00")
        ),
        Doctor(
            id = "doc-3",
            name = "Dra. Beatriz Rocha",
            specialty = "Clínica Geral",
            location = "Policlínica Epitácio",
            biography = "Avaliação geral, check-up de rotina e encaminhamento para exames.",
            availableSlots = listOf("08:00", "09:30", "13:30", "17:00")
        )
    )
}
