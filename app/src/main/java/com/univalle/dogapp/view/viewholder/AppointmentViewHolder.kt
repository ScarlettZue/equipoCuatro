package com.univalle.dogapp.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.ItemAppointmentBinding

// Nota: Cuando esté creada la clase Appointment en el paquete model,
// se importará aquí. Mientras tanto, si aún no existe, puedes vincular campos directamente.
class AppointmentViewHolder(
    private val binding: ItemAppointmentBinding,
    private val onItemClick: (appointmentId: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        id: Int,
        dogName: String,
        symptom: String,
        photoUrl: String?
    ) {
        binding.tvDogName.text = dogName
        binding.tvSymptom.text = symptom
        binding.tvAppointmentTurn.text = "# $id"

        // Carga de la imagen de la mascota usando Glide
        Glide.with(binding.root.context)
            .load(photoUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(binding.ivDogPhoto)

        // Criterio 5: Clic en el ítem
        binding.cardAppointment.setOnClickListener {
            onItemClick(id)
        }
    }
}
