package com.sensifyawareapp.ui.glanceaware.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensifyawareapp.databinding.RowGlanceAwareOptionsBinding
import com.sensifyawareapp.listeners.ClickListener

class OptionsAdapter(
    private val optionsList: ArrayList<OptionsModel>,
    private val clickListener: ClickListener
) :
    RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    class ViewHolder(view: View, val binding: RowGlanceAwareOptionsBinding) :
        RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowGlanceAwareOptionsBinding.inflate(LayoutInflater.from(viewGroup.context))
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.value = optionsList[position].value
        viewHolder.binding.isSelected = optionsList[position].isSelected

        viewHolder.binding.llValue.setOnClickListener { clickListener.onItemClick(viewHolder.absoluteAdapterPosition) }
    }

    override fun getItemCount() = optionsList.size

}
