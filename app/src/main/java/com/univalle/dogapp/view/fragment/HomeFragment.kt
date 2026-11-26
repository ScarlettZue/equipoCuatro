package com.univalle.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentHomeBinding
import com.univalle.dogapp.view.adapter.AppointmentAdapter
import com.univalle.dogapp.view.adapter.AppointmentItem
import com.univalle.dogapp.viewmodel.AppointmentViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointmentViewModel: AppointmentViewModel by viewModels()

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
        getListAppointments()
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

    private fun getListAppointments() {
        appointmentViewModel.listAppointments.observe(viewLifecycleOwner) { list ->
            appointmentAdapter.updateList(list.map {
                AppointmentItem(it.id, it.dogName, it.symptom, it.photoUrl)
            })
        }
        appointmentViewModel.getListAppointments()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}