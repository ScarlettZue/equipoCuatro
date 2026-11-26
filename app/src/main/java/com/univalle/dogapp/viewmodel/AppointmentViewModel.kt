package com.univalle.dogapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.dogapp.model.Appointment
import com.univalle.dogapp.repository.AppointmentRepository
import kotlinx.coroutines.launch

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {
    val context = getApplication<Application>()
    private val appointmentRepository = AppointmentRepository(context)

    // Lista de citas del Home
    private val _listAppointments = MutableLiveData<MutableList<Appointment>>()
    val listAppointments: LiveData<MutableList<Appointment>> get() = _listAppointments

    // Cita que se muestra en el Detalle o se carga en Editar
    private val _appointment = MutableLiveData<Appointment?>()
    val appointment: LiveData<Appointment?> get() = _appointment

    // Razas para el AutoCompleteTextView de Nueva Cita y Editar Cita
    private val _listBreeds = MutableLiveData<List<String>>()
    val listBreeds: LiveData<List<String>> get() = _listBreeds

    // true mientras hay una operación en curso (para mostrar el loading)
    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> = _progressState

    /**
     * Guarda la cita (el repositorio le pone la imagen de la raza).
     * onFinished avisa cuando terminó, para navegar al Home solo entonces.
     */
    fun saveAppointment(appointment: Appointment, onFinished: (Boolean) -> Unit = {}) {
        runWithProgress(onFinished) {
            appointmentRepository.saveAppointment(appointment)
        }
    }

    fun getListAppointments() {
        runWithProgress {
            _listAppointments.value = appointmentRepository.getListAppointments()
        }
    }

    fun getAppointmentById(id: Int) {
        runWithProgress {
            _appointment.value = appointmentRepository.getAppointmentById(id)
        }
    }

    fun updateAppointment(appointment: Appointment, onFinished: (Boolean) -> Unit = {}) {
        runWithProgress(onFinished) {
            appointmentRepository.updateAppointment(appointment)
        }
    }

    fun deleteAppointment(appointment: Appointment, onFinished: (Boolean) -> Unit = {}) {
        runWithProgress(onFinished) {
            appointmentRepository.deleteAppointment(appointment)
        }
    }

    /** Sin internet la lista queda vacía y el campo Raza no sugiere nada. */
    fun getBreeds() {
        viewModelScope.launch {
            _listBreeds.value = try {
                appointmentRepository.getBreeds()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Ejecuta la operación mostrando el loading y avisa si terminó bien o mal.
    private fun runWithProgress(
        onFinished: (Boolean) -> Unit = {},
        operation: suspend () -> Unit
    ) {
        viewModelScope.launch {
            _progressState.value = true
            val success = try {
                operation()
                true
            } catch (e: Exception) {
                false
            }
            _progressState.value = false
            onFinished(success)
        }
    }
}
