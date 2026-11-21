package com.univalle.dogapp.model

import com.google.gson.annotations.SerializedName

/** Respuesta de las rutas de imagen aleatoria: message trae la URL. */
data class BreedImageResponse(
    @SerializedName("message")
    val imageUrl: String,
    @SerializedName("status")
    val status: String
)
