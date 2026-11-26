package com.univalle.dogapp.webservice

import com.univalle.dogapp.model.BreedImageResponse
import com.univalle.dogapp.model.BreedsResponse
import com.univalle.dogapp.utils.Constants.END_POINT_BREEDS
import com.univalle.dogapp.utils.Constants.END_POINT_BREED_IMAGE
import com.univalle.dogapp.utils.Constants.END_POINT_RANDOM_IMAGE
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    /** Lista de todas las razas (campo Raza de Nueva Cita y Editar Cita). */
    @GET(END_POINT_BREEDS)
    suspend fun getBreeds(): BreedsResponse

    /** Imagen aleatoria de una raza específica. */
    @GET(END_POINT_BREED_IMAGE)
    suspend fun getBreedImage(@Path("breed") breed: String): BreedImageResponse

    /** Imagen aleatoria de cualquier raza (la ruta que da el enunciado). */
    @GET(END_POINT_RANDOM_IMAGE)
    suspend fun getRandomImage(): BreedImageResponse
}
