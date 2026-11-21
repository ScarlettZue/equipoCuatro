package com.univalle.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentHomeBinding
import com.univalle.dogapp.view.adapter.AppointmentAdapter
import com.univalle.dogapp.view.adapter.AppointmentItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var appointmentAdapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackNavigation()
        setupRecyclerView()
        setupListeners()

        // TODO: Cuando la tarea TS 1.0 (ViewModel) esté lista, aquí observaremos el LiveData de citas.
        // Para probar la vista ahora mismo, puedes descomentar la siguiente línea con datos de prueba:
        loadMockData()
    }
    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }
    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter { appointmentId ->
            // Criterio 5: Clic en ítem navega a HU 4.0 (Detalle)
            val bundle = Bundle().apply {
                putInt("appointmentId", appointmentId)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_appointmentDetailsFragment,
                bundle
            )
        }
        binding.rvAppointments.adapter = appointmentAdapter
    }
    private fun setupListeners() {
        binding.fabAddAppointment.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addAppointmentFragment)
        }
    }

    private fun loadMockData() {
        val mockList = listOf(
            AppointmentItem(1, "Cory", "Fractura extremidad", "https://images.dog.ceo/breeds/pitbull/dog1.jpg"),
            AppointmentItem(2, "Zeus", "Solo duerme", "https://images.dog.ceo/breeds/hound/dog2.jpg"),
            AppointmentItem(3, "Rocky", "No come", "https://images.dog.ceo/breeds/akita/dog3.jpg")
        )
        appointmentAdapter.updateList(mockList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}