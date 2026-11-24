package com.curalink.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.curalink.data.model.Doctor
import com.curalink.databinding.ItemDoctorBinding

class DoctorAdapter(
    private var items: List<Doctor>,
    private val onClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(val binding: ItemDoctorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemDoctorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = items[position]
        with(holder.binding) {
            tvDoctorName.text = doctor.name
            tvSpecialty.text = doctor.specialty
            tvLocation.text = doctor.location
            root.setOnClickListener { onClick(doctor) }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<Doctor>) {
        items = newList
        notifyDataSetChanged()
    }
}
