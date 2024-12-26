package com.sensifyawareapp.ui.scentaware

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensifyawareapp.databinding.LayoutScentAnswerBinding
import com.sensifyawareapp.ui.scentaware.model.TubeTest

class ScentAnswerAdapter(private val tubeTest: List<TubeTest>, private val isSkipRetest: Boolean) :
    RecyclerView.Adapter<ScentAnswerAdapter.ViewHolder>() {
    private var onClickListener: OnItemClickListener? = null

    class ViewHolder(view: View, val binding: LayoutScentAnswerBinding) :
        RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutScentAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.binding.accuracy = tubeTest[position].averagePercentage.toInt()
         holder.binding.ans8Tube = tubeTest[position].tubeTestsRecords[0].selectedAnswer
         holder.binding.scent = tubeTest[position].tubeTestsRecords[0].correctAnswer
         holder.binding.isAnswerCorrect =
             tubeTest[position].tubeTestsRecords[0].isAnswerCorrect
        holder.binding.isSkipRetest = isSkipRetest

         if (tubeTest[position].tubeTestsRecords.size == 2) {
             holder.binding.ans16Tube = tubeTest[position].tubeTestsRecords[1].selectedAnswer
             holder.binding.isAnswerCorrect2 =
                 tubeTest[position].tubeTestsRecords[1].isAnswerCorrect
         }
        /*holder.binding.isSkipRetest = isSkipRetest
        holder.binding.ans8Tube = tubeTest[position].selectedAnswer
        holder.binding.scent = tubeTest[position].correctAnswer
        holder.binding.isAnswerCorrect = tubeTest[position].isAnswerCorrect*/

        /* for (i in tubeTest[position].tubeTestsRecords.indices){

         }*/

        holder.itemView.setOnClickListener {
            onClickListener?.onItemClick()
        }
    }


    override fun getItemCount() = tubeTest.size

    interface OnItemClickListener {
        fun onItemClick()
    }

    fun setOnClickListener(onClickListener: OnItemClickListener) {
        this.onClickListener = onClickListener
    }


}