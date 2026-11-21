package com.univalle.dogapp.repository

import android.content.Context
import com.univalle.dogapp.data.AppointmentDB
import com.univalle.dogapp.data.AppointmentDao
import com.univalle.dogapp.model.Appointment
import com.univalle.dogapp.webservice.ApiService
import com.univalle.dogapp.webservice.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppointmentRepository(val context: Context) {
    private val appointmentDao: AppointmentDao =
        AppointmentDB.getDatabase(context).appointmentDao()
    private val apiService: ApiService = RetrofitClient.apiService

    // ------------------------------------------------------------ Room

    /**
     * Guarda la cita con la imagen de su raza y devuelve el id autogenerado.
     * Si no hay internet la cita se guarda igual, sin imagen.
     */
    suspend fun saveAppointment(appointment: Appointment): Long {
        return withContext(Dispatchers.IO) {
            val photoUrl = getBreedImage(appointment.breed)
            appointmentDao.saveAppointment(appointment.copy(photoUrl = photoUrl))
        }
    }

    suspend fun getListAppointments(): MutableList<Appointment> {
        return withContext(Dispatchers.IO) {
            appointmentDao.getListAppointments()
        }
    }

    suspend fun getAppointmentById(id: Int): Appointment? {
        return withContext(Dispatchers.IO) {
            appointmentDao.getAppointmentById(id)
        }
    }

    /**
     * Actualiza la cita. Solo pide una imagen nueva si cambió la raza o si la
     * cita no tenía imagen; así la foto no cambia al editar otros campos.
     */
    suspend fun updateAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            val saved = appointmentDao.getAppointmentById(appointment.id)
            val keepPhoto = saved != null && saved.photoUrl.isNotEmpty() &&
                normalizeBreed(saved.breed) == normalizeBreed(appointment.breed)
            val photoUrl = if (keepPhoto) saved!!.photoUrl else getBreedImage(appointment.breed)
            appointmentDao.updateAppointment(appointment.copy(photoUrl = photoUrl))
        }
    }

    suspend fun deleteAppointment(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentDao.deleteAppointment(appointment)
        }
    }

    // ------------------------------------------------------------ API

    /** Lista de razas en orden alfabético. Lanza una excepción si no hay internet. */
    suspend fun getBreeds(): List<String> {
        return withContext(Dispatchers.IO) {
            apiService.getBreeds().breeds.keys.sorted()
        }
    }

    /**
     * URL de una imagen de la raza. Si la raza no existe en la API usa la
     * imagen aleatoria del enunciado; si no hay internet devuelve "".
     */
    suspend fun getBreedImage(breed: String): String {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getBreedImage(normalizeBreed(breed)).imageUrl
            } catch (e: Exception) {
                try {
                    apiService.getRandomImage().imageUrl
                } catch (e: Exception) {
                    ""
                }
            }
        }
    }

    // La API espera la raza en minúscula y sin espacios alrededor.
    private fun normalizeBreed(breed: String): String = breed.trim().lowercase()
}
