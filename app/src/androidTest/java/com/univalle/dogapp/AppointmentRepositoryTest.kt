package com.univalle.dogapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.univalle.dogapp.model.Appointment
import com.univalle.dogapp.repository.AppointmentRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba la capa de datos en un dispositivo con internet: Room y la API.
 * Cada prueba borra las citas que crea.
 */
@RunWith(AndroidJUnit4::class)
class AppointmentRepositoryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val repository = AppointmentRepository(context)

    private val cita = Appointment(
        dogName = "Cory",
        breed = "Akita",
        ownerName = "Ana Pérez",
        phone = "3001234567",
        symptom = "No come"
    )

    @Test
    fun guardarListarEditarYEliminarUnaCita() = runBlocking {
        val id = repository.saveAppointment(cita).toInt()
        assertTrue(id > 0)

        // Guardar: queda con el turno autogenerado y la imagen de su raza
        val guardada = repository.getAppointmentById(id)
        assertNotNull(guardada)
        assertEquals("Cory", guardada!!.dogName)
        assertTrue(guardada.photoUrl.contains("/breeds/akita/"))
        assertTrue(repository.getListAppointments().any { it.id == id })

        // Editar sin cambiar la raza: conserva la misma imagen
        repository.updateAppointment(guardada.copy(symptom = "Tiene pulgas", photoUrl = ""))
        val editada = repository.getAppointmentById(id)!!
        assertEquals("Tiene pulgas", editada.symptom)
        assertEquals(guardada.photoUrl, editada.photoUrl)

        // Editar cambiando la raza: trae una imagen de la raza nueva
        repository.updateAppointment(editada.copy(breed = "beagle"))
        val otraRaza = repository.getAppointmentById(id)!!
        assertNotEquals(guardada.photoUrl, otraRaza.photoUrl)
        assertTrue(otraRaza.photoUrl.contains("/breeds/beagle/"))

        // Eliminar
        repository.deleteAppointment(otraRaza)
        assertNull(repository.getAppointmentById(id))
        assertFalse(repository.getListAppointments().any { it.id == id })
    }

    @Test
    fun elTurnoEsAutoincremental() = runBlocking {
        val primero = repository.saveAppointment(cita).toInt()
        val segundo = repository.saveAppointment(cita.copy(dogName = "Zeus")).toInt()
        assertTrue(segundo > primero)

        repository.deleteAppointment(cita.copy(id = primero))
        repository.deleteAppointment(cita.copy(id = segundo))
    }

    @Test
    fun laApiDevuelveLaListaDeRazas() = runBlocking {
        val razas = repository.getBreeds()
        assertTrue(razas.size > 50)
        assertTrue(razas.contains("akita"))
        assertEquals(razas.sorted(), razas)
    }

    @Test
    fun unaRazaQueNoExisteRecibeUnaImagenAleatoria() = runBlocking {
        val url = repository.getBreedImage("raza inventada")
        assertTrue(url.startsWith("https://images.dog.ceo/"))
    }
}
