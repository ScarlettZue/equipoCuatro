package com.univalle.dogapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Cita de una mascota. El id autogenerado es el turno de atención que se
 * muestra en el Home y en el Detalle.
 */
@Entity
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dogName: String,
    val breed: String,
    val ownerName: String,
    val phone: String,
    val symptom: String,
    // URL de la imagen de la raza. Se guarda para que el Home y el Detalle
    // muestren la misma imagen (la API devuelve una distinta en cada llamada).
    val photoUrl: String = ""
) : Serializable
