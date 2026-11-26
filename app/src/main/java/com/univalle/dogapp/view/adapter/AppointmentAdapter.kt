package com.univalle.dogapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.univalle.dogapp.databinding.ItemAppointmentBinding
import com.univalle.dogapp.view.viewholder.AppointmentViewHolder

// Usamos una data class temporal interna o la entidad Appointment cuando esté lista
data class AppointmentItem(
    val id: Int,
    val dogName: String,
    val symptom: String,
    val photoUrl: String?
)

class AppointmentAdapter(
    private val onItemClick: (appointmentId: Int) -> Unit
) : RecyclerView.Adapter<AppointmentViewHolder>() {

    private var appointments: List<AppointmentItem> = emptyList()

    fun updateList(newList: List<AppointmentItem>) {
        this.appointments = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val item = appointments[position]
        holder.bind(item.id, item.dogName, item.symptom, item.photoUrl)
    }

    override fun getItemCount(): Int = appointments.size
}