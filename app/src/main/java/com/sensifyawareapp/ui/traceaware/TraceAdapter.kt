package com.sensifyawareapp.ui.traceaware

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.LayoutTraceAwareIconsBinding

class TraceAdapter(
    private val tracesList: ArrayList<Drawable>,
    private val visibleIcons: Boolean
) :
    RecyclerView.Adapter<TraceAdapter.ViewHolder>() {

    private var greenBorderPositionAt = -1

    class ViewHolder(view: View, val binding: LayoutTraceAwareIconsBinding) :
        RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutTraceAwareIconsBinding.inflate(LayoutInflater.from(viewGroup.context))
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.index = position
        viewHolder.binding.iconImage = tracesList[position]

        viewHolder.binding.showBottomBorder = position < (tracesList.size / 2)
        viewHolder.binding.hideRightBorder =
            (position == (tracesList.size - 1) / 2) || position == tracesList.size - 1 //hide last view's right border in each row
        viewHolder.binding.visibleIcons = visibleIcons
        viewHolder.binding.showGreenBorder = greenBorderPositionAt == position
    }

    override fun getItemCount() = tracesList.size

    fun updateBackgroundBorder(position: Int) {
        notifyItemChanged(greenBorderPositionAt)
        greenBorderPositionAt = position
        notifyItemChanged(position)
    }
}
