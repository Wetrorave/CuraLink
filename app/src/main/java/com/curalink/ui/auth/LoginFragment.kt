package com.curalink.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.curalink.R
import com.curalink.data.repository.CuraLinkRepository
import com.curalink.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val repository = CuraLinkRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (repository.currentUserId() != null) {
            findNavController().navigate(R.id.action_login_to_home)
            return
        }

        binding.btnLogin.setOnClickListener { authenticate(isRegister = false) }
        binding.btnRegister.setOnClickListener { authenticate(isRegister = true) }
    }

    private fun authenticate(isRegister: Boolean) {
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()

        if (email.isEmpty() || password.length < 6) {
            Toast.makeText(
                requireContext(),
                "Informe um e-mail válido e senha com ao menos 6 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            val result = if (isRegister) {
                repository.register(email, password)
            } else {
                repository.login(email, password)
            }
            binding.progressBar.visibility = View.GONE

            result.onSuccess {
                findNavController().navigate(R.id.action_login_to_home)
            }.onFailure { error ->
                Toast.makeText(
                    requireContext(),
                    error.localizedMessage ?: "Falha na autenticação",
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