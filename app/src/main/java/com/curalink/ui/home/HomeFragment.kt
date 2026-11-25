package com.curalink.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.curalink.R
import com.curalink.data.model.Doctor
import com.curalink.data.repository.CuraLinkRepository
import com.curalink.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val repository = CuraLinkRepository()
    private var allDoctors: List<Doctor> = emptyList()
    private var selectedSpecialty: String? = null
    private lateinit var adapter: DoctorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DoctorAdapter(emptyList()) { doctor ->
            val bundle = bundleOf(
                "doctorId" to doctor.id,
                "doctorName" to doctor.name,
                "specialty" to doctor.specialty,
                "location" to doctor.location,
                "biography" to doctor.biography,
                "slots" to doctor.availableSlots.toTypedArray()
            )
            findNavController().navigate(R.id.action_home_to_doctor, bundle)
        }

        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = adapter

        binding.btnMyAppointments.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_appointments)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = applyFilters()
            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.chipGroupSpecialty.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedSpecialty = when (checkedIds.firstOrNull()) {
                R.id.chipCardio -> "Cardiologia"
                R.id.chipDerma -> "Dermatologia"
                R.id.chipGeral -> "Clínica Geral"
                else -> null
            }
            applyFilters()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repository.getDoctors().onSuccess { doctors ->
                allDoctors = doctors
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        val query = binding.etSearch.text?.toString().orEmpty().trim().lowercase()
        val filtered = allDoctors.filter { doctor ->
            val matchesSpecialty = selectedSpecialty == null ||
                    doctor.specialty.equals(selectedSpecialty, ignoreCase = true)
            val matchesQuery = query.isEmpty() ||
                    doctor.name.lowercase().contains(query) ||
                    doctor.specialty.lowercase().contains(query) ||
                    doctor.location.lowercase().contains(query)
            matchesSpecialty && matchesQuery
        }
        adapter.updateList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}