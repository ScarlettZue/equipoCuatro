package com.univalle.dogapp.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de breeds/list/all. Cada llave del mapa es una raza y su valor
 * es la lista de subrazas (puede estar vacía).
 */
data class BreedsResponse(
    @SerializedName("message")
    val breeds: Map<String, List<String>>,
    @SerializedName("status")
    val status: String
)
