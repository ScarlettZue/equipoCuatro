package com.univalle.dogapp.view.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentAddAppointmentBinding
import com.univalle.dogapp.model.Appointment
import com.univalle.dogapp.utils.InputFilters
import com.univalle.dogapp.viewmodel.AppointmentViewModel

/** HU 3.0 Ventana Nueva Cita. */
class AddAppointmentFragment : Fragment() {

    private var _binding: FragmentAddAppointmentBinding? = null
    private val binding get() = _binding!!

    private val appointmentViewModel: AppointmentViewModel by viewModels()

    // true mientras se guarda: evita que un segundo toque cree la cita dos veces.
    private var isSaving = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAppointmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupInputs()
        setupSymptomSpinner()
        setupBreedSuggestions()
        setupSaveButton()
        observeProgress()
        updateSaveButton()
    }

    // ------------------------------------------------------------ Toolbar

    private fun setupToolbar() {
        // La flecha rosada regresa al Home (HU 2.0).
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // ------------------------------------------------------------ Campos

    private fun setupInputs() {
        // Nombre de la mascota: solo texto (el máximo de 15 está en el layout).
        // Se suma el filtro a los existentes para no perder el maxLength.
        binding.etPetName.filters = binding.etPetName.filters + InputFilters.lettersOnly

        // Cada cambio en uno de los 4 campos recalcula si el botón se habilita.
        listOf(binding.etPetName, binding.actvBreed, binding.etOwnerName, binding.etPhone)
            .forEach { field -> field.doAfterTextChanged { updateSaveButton() } }
    }

    private fun setupSymptomSpinner() {
        // La opción 0 es el texto por defecto "Síntomas"; luego la lista fija.
        val symptoms = listOf(getString(R.string.symptom_default)) +
            resources.getStringArray(R.array.symptoms)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_symptom, symptoms)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSymptom.adapter = adapter
    }

    private fun setupBreedSuggestions() {
        appointmentViewModel.listBreeds.observe(viewLifecycleOwner) { breeds ->
            // La API devuelve las razas en minúscula; se muestran con la primera en mayúscula.
            val names = breeds.map { breed -> breed.replaceFirstChar { it.uppercase() } }
            binding.actvBreed.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            )
        }
        // Solo se vuelve a pedir si aún no hay razas (por ejemplo, no había internet).
        if (appointmentViewModel.listBreeds.value.isNullOrEmpty()) {
            appointmentViewModel.getBreeds()
        }
    }

    // ------------------------------------------------------------ Botón Guardar

    private fun areFieldsFilled(): Boolean {
        return listOf(binding.etPetName, binding.actvBreed, binding.etOwnerName, binding.etPhone)
            .all { !it.text.isNullOrBlank() }
    }

    /**
     * Deshabilitado mientras falte alguno de los 4 campos de texto. Habilitado
     * se ve con texto blanco en negrilla. El síntoma no cuenta aquí: se valida
     * al presionar el botón.
     */
    private fun updateSaveButton() {
        val filled = areFieldsFilled()
        binding.btnSave.isEnabled = filled && !isSaving
        binding.btnSave.setTypeface(null, if (filled) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (binding.spinnerSymptom.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), R.string.select_symptom, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onSaveClicked()
        }
    }

    private fun onSaveClicked() {
        val appointment = Appointment(
            dogName = binding.etPetName.text.toString().trim(),
            breed = binding.actvBreed.text.toString().trim().replaceFirstChar { it.uppercase() },
            ownerName = binding.etOwnerName.text.toString().trim(),
            phone = binding.etPhone.text.toString().trim(),
            symptom = binding.spinnerSymptom.selectedItem as String
        )

        // El repositorio consulta la imagen de la raza y guarda en Room;
        // solo al terminar se navega al Home.
        appointmentViewModel.saveAppointment(appointment) { success ->
            if (_binding == null) return@saveAppointment // la ventana ya se cerró
            if (success) {
                findNavController().navigate(R.id.action_addAppointmentFragment_to_homeFragment)
            } else {
                Toast.makeText(requireContext(), R.string.save_error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeProgress() {
        appointmentViewModel.progressState.observe(viewLifecycleOwner) { saving ->
            isSaving = saving
            binding.progressBar.isVisible = saving
            updateSaveButton()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
