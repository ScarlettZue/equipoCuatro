package com.univalle.dogapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.univalle.dogapp.view.adapter.AppointmentItem

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val _listAppointments = MutableLiveData<MutableList<AppointmentItem>>()
    val listAppointments: LiveData<MutableList<AppointmentItem>> = _listAppointments

    fun getListAppointments() {
        // En la tarea TS 1.0 se conectará con el Repository y Room
    }
}
