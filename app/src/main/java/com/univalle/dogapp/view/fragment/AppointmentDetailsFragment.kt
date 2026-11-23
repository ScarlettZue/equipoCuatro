package com.univalle.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentAppointmentDetailsBinding
import com.univalle.dogapp.model.Appointment
import com.univalle.dogapp.utils.Constants.ARG_APPOINTMENT
import com.univalle.dogapp.utils.Constants.ARG_APPOINTMENT_ID
import com.univalle.dogapp.viewmodel.AppointmentViewModel

/** HU 4.0 Ventana Detalle de la Cita. */
class AppointmentDetailsFragment : Fragment() {

    private var _binding: FragmentAppointmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val appointmentViewModel: AppointmentViewModel by viewModels()

    // Cita que se está mostrando; la usan los botones de eliminar y editar.
    private var currentAppointment: Appointment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeAppointment()
        observeProgress()
        loadAppointment()
    }

    // ------------------------------------------------------------ Listeners

    private fun setupListeners() {
        // La flecha rosada regresa al Home (HU 2.0).
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabDelete.setOnClickListener { deleteAppointment() }
        binding.fabEdit.setOnClickListener { goToEdit() }
    }

    // ------------------------------------------------------------ Consulta

    /** El Home envía el id de la cita en los argumentos de navegación. */
    private fun loadAppointment() {
        val id = arguments?.getInt(ARG_APPOINTMENT_ID, -1) ?: -1
        if (id == -1) {
            findNavController().popBackStack()
            return
        }
        appointmentViewModel.getAppointmentById(id)
    }

    private fun observeAppointment() {
        appointmentViewModel.appointment.observe(viewLifecycleOwner) { appointment ->
            if (appointment == null) {
                // La cita ya no está en Room: no hay nada que mostrar.
                Toast.makeText(requireContext(), R.string.appointment_not_found, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@observe
            }
            currentAppointment = appointment
            showAppointment(appointment)
        }
    }

    private fun showAppointment(appointment: Appointment) {
        binding.tvToolbarTitle.text = appointment.dogName
        binding.tvBreed.text = appointment.breed
        binding.tvTurn.text = getString(R.string.detail_turn, appointment.id)
        binding.tvSymptom.text = appointment.symptom
        binding.tvOwner.text = getString(R.string.detail_owner, appointment.ownerName)
        binding.tvPhone.text = getString(R.string.detail_phone, appointment.phone)

        // Misma imagen que se guardó con la cita (la que ve el Home).
        Glide.with(binding.root)
            .load(appointment.photoUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(binding.ivPetPhoto)
    }

    /** Mientras hay una operación en curso los botones quedan deshabilitados. */
    private fun observeProgress() {
        appointmentViewModel.progressState.observe(viewLifecycleOwner) { loading ->
            binding.fabDelete.isEnabled = !loading
            binding.fabEdit.isEnabled = !loading
        }
    }

    // ------------------------------------------------------------ Acciones

    /** Elimina el registro de Room de inmediato (sin confirmación) y vuelve al Home. */
    private fun deleteAppointment() {
        val appointment = currentAppointment ?: return
        appointmentViewModel.deleteAppointment(appointment) { success ->
            if (_binding == null) return@deleteAppointment // la ventana ya se cerró
            if (success) {
                findNavController().navigate(R.id.action_appointmentDetailsFragment_to_homeFragment)
            } else {
                Toast.makeText(requireContext(), R.string.delete_error, Toast.LENGTH_LONG).show()
            }
        }
    }

    /** Abre Editar Cita (HU 5.0) enviando los datos actuales de la cita. */
    private fun goToEdit() {
        val appointment = currentAppointment ?: return
        val bundle = Bundle().apply {
            putInt(ARG_APPOINTMENT_ID, appointment.id)
            putSerializable(ARG_APPOINTMENT, appointment)
        }
        findNavController().navigate(
            R.id.action_appointmentDetailsFragment_to_editAppointmentFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
