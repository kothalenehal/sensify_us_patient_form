package com.sensifyawareapp.ui.audioaware.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensifyawareapp.databinding.RowWordBinding
import com.sensifyawareapp.listeners.ClickListener

class WordsAdapter(
    private val wordsList: ArrayList<AudioAwareRecallFragment.WordsModel>,
    private val clickListener: ClickListener
) :
    RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

    class ViewHolder(view: View, val binding: RowWordBinding) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowWordBinding.inflate(LayoutInflater.from(viewGroup.context))
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.word = wordsList[position].word
        viewHolder.binding.isSelected = wordsList[position].isSelected
//        Log.e("TAG", "onBindViewHolder: ${wordsList[position].word}")
        viewHolder.binding.tvWord.setOnClickListener { clickListener.onItemClick(viewHolder.absoluteAdapterPosition) }
    }

    override fun getItemCount() = wordsList.size

}
