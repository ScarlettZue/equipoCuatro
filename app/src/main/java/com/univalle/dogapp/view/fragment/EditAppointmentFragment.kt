package com.univalle.dogapp.view.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentEditAppointmentBinding
import com.univalle.dogapp.model.Appointment
import com.univalle.dogapp.utils.Constants.ARG_APPOINTMENT
import com.univalle.dogapp.utils.Constants.ARG_APPOINTMENT_ID
import com.univalle.dogapp.viewmodel.AppointmentViewModel

/** HU 5.0 Ventana Editar Cita. */
class EditAppointmentFragment : Fragment() {

    private var _binding: FragmentEditAppointmentBinding? = null
    private val binding get() = _binding!!

    private val appointmentViewModel: AppointmentViewModel by viewModels()

    private var currentAppointment: Appointment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAppointmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        loadInitialData()
    }

    // ------------------------------------------------------------ Carga inicial

    private fun loadInitialData() {
        // 1. Obtener el objeto Appointment enviado desde AppointmentDetailsFragment (HU 4.0)
        val passedAppointment = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_APPOINTMENT, Appointment::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_APPOINTMENT) as? Appointment
        }

        if (passedAppointment != null) {
            currentAppointment = passedAppointment
            fillFields(passedAppointment)
        } else {
            // Respaldo: si solo llegó el ID, lo consultamos en Room mediante el ViewModel
            val id = arguments?.getInt(ARG_APPOINTMENT_ID, -1) ?: -1
            if (id != -1) {
                appointmentViewModel.getAppointmentById(id)
            } else {
                findNavController().popBackStack()
                return
            }
        }

        // Cargar lista de razas desde la API para el AutoCompleteTextView
        appointmentViewModel.getBreeds()
    }

    private fun fillFields(appointment: Appointment) {
        binding.etDogName.setText(appointment.dogName)
        binding.actvBreed.setText(appointment.breed, false)
        binding.etOwnerName.setText(appointment.ownerName)
        binding.etPhone.setText(appointment.phone)
        validateFields()
    }

    // ------------------------------------------------------------ Listeners

    private fun setupListeners() {
        // Criterio 1: Flecha rosada atrás vuelve a HU 4.0 (Detalle de la Cita)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Criterios 6 y 7: Validar que los 4 campos tengan texto en tiempo real
        binding.etDogName.addTextChangedListener { validateFields() }
        binding.actvBreed.addTextChangedListener { validateFields() }
        binding.etOwnerName.addTextChangedListener { validateFields() }
        binding.etPhone.addTextChangedListener { validateFields() }

        // Criterio 8: Guardar cambios y volver al Home (HU 2.0)
        binding.btnEditAppointment.setOnClickListener {
            saveChanges()
        }
    }

    // ------------------------------------------------------------ Observers

    private fun setupObservers() {
        // Respaldo en caso de haber consultado la cita por ID
        appointmentViewModel.appointment.observe(viewLifecycleOwner) { appointment ->
            if (appointment != null && currentAppointment == null) {
                currentAppointment = appointment
                fillFields(appointment)
            }
        }

        // Criterio 3: Razas para el AutoCompleteTextView
        appointmentViewModel.listBreeds.observe(viewLifecycleOwner) { breeds ->
            if (!breeds.isNullOrEmpty()) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    breeds
                )
                binding.actvBreed.setAdapter(adapter)
            }
        }

        // Loading mientras consulta o actualiza en Room / Retrofit
        appointmentViewModel.progressState.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            if (loading) {
                binding.btnEditAppointment.isEnabled = false
            } else {
                validateFields()
            }
        }
    }

    // ------------------------------------------------------------ Validación y Guardado

    private fun validateFields() {
        val dogName = binding.etDogName.text?.toString()?.trim().orEmpty()
        val breed = binding.actvBreed.text?.toString()?.trim().orEmpty()
        val ownerName = binding.etOwnerName.text?.toString()?.trim().orEmpty()
        val phone = binding.etPhone.text?.toString()?.trim().orEmpty()

        val allFilled = dogName.isNotEmpty() &&
                breed.isNotEmpty() &&
                ownerName.isNotEmpty() &&
                phone.isNotEmpty()

        binding.btnEditAppointment.isEnabled = allFilled
    }

    private fun saveChanges() {
        val original = currentAppointment ?: return

        val updatedAppointment = original.copy(
            dogName = binding.etDogName.text.toString().trim(),
            breed = binding.actvBreed.text.toString().trim(),
            ownerName = binding.etOwnerName.text.toString().trim(),
            phone = binding.etPhone.text.toString().trim()
            // id, symptom y photoUrl se conservan intactos con .copy()
            // (el Repository actualiza photoUrl automáticamente si breed cambió)
        )

        appointmentViewModel.updateAppointment(updatedAppointment) { success ->
            if (_binding == null) return@updateAppointment
            if (success) {
                findNavController().navigate(R.id.action_editAppointmentFragment_to_homeFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.edit_appointment_error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}