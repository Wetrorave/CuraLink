package com.curalink.ui.doctor

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.curalink.data.model.Appointment
import com.curalink.data.repository.CuraLinkRepository
import com.curalink.databinding.FragmentDoctorDetailBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.util.Calendar

class DoctorDetailFragment : Fragment() {

    private var _binding: FragmentDoctorDetailBinding? = null
    private val binding get() = _binding!!
    private val repository = CuraLinkRepository()

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDoctorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val doctorId = arguments?.getString("doctorId").orEmpty()
        val doctorName = arguments?.getString("doctorName").orEmpty()
        val specialty = arguments?.getString("specialty").orEmpty()
        val location = arguments?.getString("location").orEmpty()
        val biography = arguments?.getString("biography").orEmpty()
        val slots = arguments?.getStringArray("slots").orEmpty()

        binding.tvDetailName.text = doctorName
        binding.tvDetailSpecialty.text = specialty
        binding.tvDetailLocation.text = location
        binding.tvDetailBio.text = biography

        binding.btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedDate = "%02d/%02d/%04d".format(day, month + 1, year)
                    binding.btnPickDate.text = "Data: $selectedDate"
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH],
            ).show()
        }

        slots.forEach { slot ->
            val chip = Chip(requireContext()).apply {
                text = slot
                isCheckable = true
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) selectedTime = buttonView.text.toString()
                    else if (selectedTime == buttonView.text.toString()) selectedTime = ""
                }
            }
            binding.chipGroupSlots.addView(chip)
        }

        binding.btnConfirmAppointment.setOnClickListener {
            val userId = repository.currentUserId()
            if (userId == null) {
                Toast.makeText(requireContext(), "Faça login novamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Escolha a data e o horário da consulta",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }

            val appointment = Appointment(
                patientId = userId,
                patientEmail = repository.currentUserEmail(),
                doctorId = doctorId,
                doctorName = doctorName,
                specialty = specialty,
                location = location,
                date = selectedDate,
                time = selectedTime,
                status = "Agendado",
            )

            viewLifecycleOwner.lifecycleScope.launch {
                repository.createAppointment(appointment).onSuccess {
                    Toast.makeText(
                        requireContext(),
                        "Consulta confirmada para $selectedDate às $selectedTime",
                        Toast.LENGTH_LONG,
                    ).show()
                    findNavController().popBackStack()
                }.onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        error.localizedMessage ?: "Erro ao agendar",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
