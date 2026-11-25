// ui/appointments/AppointmentAdapter.kt
package com.curalink.ui.appointments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.curalink.data.model.Appointment
import com.curalink.databinding.ItemAppointmentBinding

class AppointmentAdapter(
    private var items: List<Appointment>
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvAppDoctor.text = "${item.doctorName} (${item.specialty})"
            tvAppDateTime.text = "${item.date} às ${item.time} • ${item.location}"
            tvAppStatus.text = "Status: ${item.status}"
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<Appointment>) {
        items = newList
        notifyDataSetChanged()
    }
}