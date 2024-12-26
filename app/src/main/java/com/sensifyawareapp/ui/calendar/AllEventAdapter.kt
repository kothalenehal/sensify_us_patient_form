package com.sensifyawareapp.ui.calendar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.LayoutTestEventListBinding
import com.sensifyawareapp.room.ReminderList
import java.util.Locale

class AllEventAdapter(
    private var daysWithDateTimeList: ArrayList<ReminderList>,
    private var context: Context,
) :
    RecyclerView.Adapter<AllEventAdapter.ViewHolder>() {
    private var onClickListener: OnItemClickListener? = null

    class ViewHolder(view: View, val binding: LayoutTestEventListBinding) :
        RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutTestEventListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setImage(holder,position)
        holder.binding.title = localizeText(daysWithDateTimeList[position].testName)
        holder.binding.time = daysWithDateTimeList[position].dateTime
        Log.e("TAG", "onBindViewHolder: 1 ${daysWithDateTimeList[position].selectedInterval}", )
        Log.e("TAG", "onBindViewHolder: 2 ${context.getString(R.string.every, daysWithDateTimeList[position].number.toString(), localizeText(daysWithDateTimeList[position].selectedInterval))}", )
        if (daysWithDateTimeList[position].repeatTime == "every") {

            holder.binding.repeat =
                context.getString(R.string.every, daysWithDateTimeList[position].number.toString(), localizeText(daysWithDateTimeList[position].selectedInterval))

        }else{
            holder.binding.repeat = localizeText(daysWithDateTimeList[position].repeatTime)

        }

        holder.itemView.setOnClickListener {
            onClickListener?.onItemClick(position)
        }

    }

    private fun localizeText(text: String?): String? {

        if (text != null) {
            val resourceId =
                context.resources.getIdentifier("${text}", "string", context.packageName)
            return if (resourceId != 0) {
                context.getString(resourceId)
            } else {
                text // Return original text if no localized version is found
            }
        }
        return null
    }


    override fun getItemCount() = daysWithDateTimeList.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnItemClickListener) {
        this.onClickListener = onClickListener
    }

    private fun setImage(holder: ViewHolder, position: Int) {
        when ( localizeText(daysWithDateTimeList[position].testName)) {
            holder.itemView.context.getString(R.string.scentaware_8_scent_test) -> {
                holder.binding.ivIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.scent_aware
                    )
                )
            }

            holder.itemView.context.getString(R.string.scentaware_16_scent_test) -> {
                holder.binding.ivIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.scent_aware
                    )
                )
            }

            holder.itemView.context.getString(R.string.scentaware_8_smell_training) -> {
                holder.binding.ivIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.training_icon
                    )
                )
            }

            holder.itemView.context.getString(R.string.scentaware_16_smell_training) -> {
                holder.binding.ivIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.training_icon
                    )
                )
            }

            holder.itemView.context.getString(R.string.trace_aware) -> {
                holder.binding.ivIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.trace_aware
                    )
                )
            }

            holder.itemView.context.getString(R.string.audio_aware) -> {
                holder.binding.ivIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.audio_aware
                    )
                )
            }

            holder.itemView.context.getString(R.string.glance_aware) -> {
                holder.binding.ivIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.ic_face
                    )
                )
            }
        }
    }

}