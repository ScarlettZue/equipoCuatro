package com.univalle.dogapp.utils

object Constants {
    const val NAME_BD: String = "dog_app.db"
    const val BASE_URL: String = "https://dog.ceo/api/"

    // Rutas de la API de Dog CEO
    const val END_POINT_BREEDS: String = "breeds/list/all"
    const val END_POINT_RANDOM_IMAGE: String = "breeds/image/random"
    const val END_POINT_BREED_IMAGE: String = "breed/{breed}/images/random"

    // Argumentos de navegación entre Home, Detalle y Editar Cita
    const val ARG_APPOINTMENT_ID: String = "appointmentId"
    const val ARG_APPOINTMENT: String = "appointment"
}
