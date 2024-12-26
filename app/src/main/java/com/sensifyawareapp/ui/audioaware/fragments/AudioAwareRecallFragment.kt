package com.sensifyawareapp.ui.audioaware.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.sensifyawareapp.databinding.FragmentAudioAwareRecallBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.listeners.ClickListener
import com.sensifyawareapp.ui.audioaware.AudioAwareResultActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class AudioAwareRecallFragment : BaseFragment() {

    private lateinit var adapter: WordsAdapter
    private lateinit var binding: FragmentAudioAwareRecallBinding
    private var selectedWords = 0
    private var page = 1

    private lateinit var wordsToDisplayList: ArrayList<String>
    private lateinit var wordsToSpeakList: ArrayList<String>
    lateinit var displayList: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordsToSpeakList = it.getStringArrayList("wordsToSpeakList")!!
            wordsToDisplayList = it.getStringArrayList("wordsToDisplayList")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioAwareRecallBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.currentPage = page
        binding.wordsSelected = selectedWords
        Log.e("TAG", "onViewCreated: $wordsToDisplayList")
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.AUDIO_START_TIME,
            Calendar.getInstance().timeInMillis
        )

        setupList()
        setupRecyclerView()

        binding.btnNext.setOnClickListener {
            if (selectedWords == 5) {
                if (page == 1) {
                    setupPage2()
                } else {
                    var correctWordsCount = 0

                    listPage1.addAll(listPage2)
                    for (e in listPage1)
                        if (e.isSelected && e.isCorrect)
                            correctWordsCount++

                    var greenWords = ""//words users have selected
                    for (element in listPage1) {
                        if (element.isSelected)
                            greenWords += ",${element.word}"
                    }
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.AUDIO_END_TIME,
                        Calendar.getInstance().timeInMillis
                    )

                    startActivity(
                        Intent(
                            requireActivity(),
                            AudioAwareResultActivity::class.java
                        ).putExtra("level", requireActivity().intent.getIntExtra("level", 0))
                            .putExtra("correctWordsCount", correctWordsCount)
                            .putExtra(
                                "selectedWords",
                                greenWords.substring(1, greenWords.length)
                            )//remove first comma
                            .putStringArrayListExtra("wordsToSpeakList", wordsToSpeakList)
                            .putExtra("wordsToDisplayList", displayList)
                    )
                }
            } else
                showError("Please select 5 words")
        }
    }

    val listPage1 = ArrayList<WordsModel>()
    val listPage2 = ArrayList<WordsModel>()

    private fun setupList() {

        Log.e("TAG", "setupList: ${wordsToDisplayList.size} // ${wordsToSpeakList.size}")
        val hs = HashSet<String>()
        hs.addAll(wordsToSpeakList)
        wordsToSpeakList.clear()
        wordsToSpeakList.addAll(hs)

        val hs2 = HashSet<String>()
        hs2.addAll(wordsToDisplayList)
        wordsToDisplayList.clear()
        wordsToDisplayList.addAll(hs2)

        wordsToSpeakList.shuffle()
        Log.e("TAG", "setupList: 11 $wordsToDisplayList // $wordsToSpeakList")

        for (i in 0..4)
            listPage1.add(WordsModel(wordsToSpeakList[i], false, true))
        for (e in listPage1)
            Log.e("---", "correct p1 ${e.word}")

        for (i in 0..9)
            listPage1.add(WordsModel(wordsToDisplayList[i], false, false))

        for (i in 5..9)
            listPage2.add(WordsModel(wordsToSpeakList[i], false, true))
        for (e in listPage2)
            Log.e("---", "correct p2 ${e.word}")

        for (i in 10..19)
            listPage2.add(WordsModel(wordsToDisplayList[i], false, false))

        listPage1.shuffle()
        listPage2.shuffle()
    }

    private fun setupPage2() {
        page = 2
        binding.currentPage = page
        selectedWords = 0
        binding.wordsSelected = selectedWords
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val wordsTo = ArrayList<WordsModel>()
        wordsTo.addAll(listPage1)
        wordsTo.addAll(listPage2)
        Log.e("TAG", "setupRecyclerView: ${wordsTo.size}", )
        var displaywords = ""
        for (w in wordsTo.indices)
            displaywords += ",${wordsTo[w].word}"
        displayList = displaywords.substring(1, displaywords.length)
        Log.e("TAG", "displayList: $displayList  //  ")

        val list = if (page == 1) listPage1 else listPage2
        binding.rvWords.layoutManager = GridLayoutManager(requireActivity(), 3)
        Log.e("TAG", "setup : ${list.size}", )
        adapter = WordsAdapter(list, object : ClickListener {
            override fun onItemClick(position: Int) {
                list[position].isSelected = !list[position].isSelected
                adapter.notifyItemChanged(position)
                if (list[position].isSelected) selectedWords++ else selectedWords--
                binding.wordsSelected = selectedWords
            }
        })
        binding.rvWords.adapter = adapter
    }

    class WordsModel(var word: String, var isSelected: Boolean, var isCorrect: Boolean) :
        Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(word)
            parcel.writeByte(if (isSelected) 1 else 0)
            parcel.writeByte(if (isCorrect) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<WordsModel> {
            override fun createFromParcel(parcel: Parcel): WordsModel {
                return WordsModel(parcel)
            }

            override fun newArray(size: Int): Array<WordsModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}