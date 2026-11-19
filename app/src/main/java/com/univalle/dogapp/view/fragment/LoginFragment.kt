package com.univalle.dogapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBiometria()
        controladores()
    }

    private fun controladores() {
        binding.lavFingerprint.setOnClickListener {
            mostrarAutenticacion()
        }
    }

    private fun configurarBiometria() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // La navegación al Home se agrega con el criterio 6.
            }
            // Si la huella no coincide, la ventana del dispositivo muestra su propio
            // mensaje de error y el usuario se queda en el login.
        }
        biometricPrompt = BiometricPrompt(this, executor, callback)
    }

    private fun mostrarAutenticacion() {
        val estado = BiometricManager.from(requireContext()).canAuthenticate(BIOMETRIC_WEAK)
        if (estado != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(context, R.string.biometric_unavailable, Toast.LENGTH_LONG).show()
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setNegativeButtonText(getString(R.string.biometric_cancel))
            .setAllowedAuthenticators(BIOMETRIC_WEAK)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}
