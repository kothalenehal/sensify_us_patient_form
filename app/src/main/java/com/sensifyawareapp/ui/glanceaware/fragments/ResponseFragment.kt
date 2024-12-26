package com.sensifyawareapp.ui.glanceaware.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.JsonArray
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentResponseBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.listeners.ClickListener
import com.sensifyawareapp.ui.glanceaware.GlanceAwareResultActivity
import com.sensifyawareapp.ui.glanceaware.GlanceAwareViewModel
import org.json.JSONObject

class ResponseFragment : BaseFragment() {
    private lateinit var optionsAdapter: OptionsAdapter

    private lateinit var binding: FragmentResponseBinding
    private lateinit var viewModel: GlanceAwareViewModel

    val list = ArrayList<OptionsModel>()

    var isNameShowing = true

    private var currentIndex = 0
    private var isNameCorrect = false
    private var correctAnsCount = 0

    private var trueNames = ""
    private var selectedNames = ""
    private var trueProfession = ""
    private var selectedProfession = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentIndex = it.getInt("currentIndex", 0)
            correctAnsCount = it.getInt("correctAnsCount", 0)
            trueNames = it.getString("trueNames", "")
            selectedNames = it.getString("selectedNames", "")
            trueProfession = it.getString("trueProfession", "")
            selectedProfession = it.getString("selectedProfession", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResponseBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(requireActivity())[GlanceAwareViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timerLivedata.value = "${currentIndex + 1}/12"
        if (SensifyAwareApplication.glanceModelList!![currentIndex].isFistShownA)
            binding.photo =
                getResource(SensifyAwareApplication.glanceModelList!![currentIndex].imageB)
        else
            binding.photo =
                getResource(SensifyAwareApplication.glanceModelList!![currentIndex].imageA)


        binding.rvOptions.layoutManager = GridLayoutManager(requireActivity(), 2)

        val extraNames = SensifyAwareApplication.glanceModelList!![currentIndex].extraNames
        for (i in 0 until extraNames.size)
            list.add(OptionsModel(extraNames[i]))

        setupRecyclerView()
        var name = ""
        binding.cardNext.setOnClickListener {

            if (list.none { it.isSelected }) {
                showError(getString(R.string.please_select_answer))
                return@setOnClickListener
            }

            var profession = ""
            if (isNameShowing) {
                trueNames += "," + SensifyAwareApplication.glanceModelList!![currentIndex].correctName
                selectedNames += "," + list.first { it.isSelected }.value
                name = list.first { it.isSelected }.value
                isNameCorrect =
                    list.first { it.isSelected }.value == SensifyAwareApplication.glanceModelList!![currentIndex].correctName

                list.clear()
                val extraProfession =
                    SensifyAwareApplication.glanceModelList!![currentIndex].extraProfession
                for (i in 0 until extraProfession.size)
                    list.add(OptionsModel(extraProfession[i]))

                setupRecyclerView()

                binding.tvName.text =
                    getString(R.string.what_is_the_profession_of_the_person_in_nthe_picture)

                isNameShowing = false
            } else {
                trueProfession += "," + SensifyAwareApplication.glanceModelList!![currentIndex].correctProfession
                selectedProfession += "," + list.first { it.isSelected }.value
                profession = list.first { it.isSelected }.value

                val b =
                    list.first { it.isSelected }.value == SensifyAwareApplication.glanceModelList!![currentIndex].correctProfession
                if (b && isNameCorrect) {
                    correctAnsCount++
                }

                val bundle = Bundle()
                bundle.putInt("currentIndex", ++currentIndex)
                bundle.putInt("correctAnsCount", correctAnsCount)
                bundle.putString("trueNames", trueNames)
                bundle.putString("selectedNames", selectedNames)
                bundle.putString("trueProfession", trueProfession)
                bundle.putString("selectedProfession", selectedProfession)

                Log.e(
                    "TAG ",
                    "onViewCreated: ${binding.photo} //  ${SensifyAwareApplication.glanceModelList!![currentIndex - 1].correctName} // " +
                            " $name // ${SensifyAwareApplication.glanceModelList!![currentIndex - 1].correctProfession} //" +
                            " $profession",
                )
                val trueName =
                    SensifyAwareApplication.glanceModelList!![currentIndex - 1].correctName
                val trueProfession =
                    SensifyAwareApplication.glanceModelList!![currentIndex - 1].correctProfession
                var isNameCorrect = false
                var isProfessionCorrect = false
                if (trueName == name) {
                    isNameCorrect = true
                }
                if (trueProfession == profession) {
                    isProfessionCorrect = true
                }
                val jsonObject = JSONObject()
                jsonObject.put("TrueNames", trueName)
                jsonObject.put("TrueProfession", trueProfession)
                jsonObject.put("SelectedNames", name)
                jsonObject.put("SelectedProfession", profession)
                jsonObject.put("ShownName", trueName)
                jsonObject.put("ShownProfession", trueProfession)
                jsonObject.put("IsNameCorrect", isNameCorrect)
                jsonObject.put("IsProfessionCorrect", isProfessionCorrect)
                jsonObject.put(
                    "ReferencePhotoId",
                    SensifyAwareApplication.glanceModelList!![currentIndex - 1].imageA
                )
                SensifyAwareApplication.addGlanceAwareObjectInJSON(jsonObject)

                if (currentIndex == SensifyAwareApplication.glanceModelList!!.size) {
                    bundle.putInt("level", viewModel.level)
                    startActivity(
                        Intent(
                            requireActivity(),
                            GlanceAwareResultActivity::class.java
                        ).putExtras(bundle)
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_ResponseFragment_self,
                        bundle
                    )
                }
            }
        }
    }

    private fun setupRecyclerView() {
        list.shuffle()
        optionsAdapter = OptionsAdapter(list, object : ClickListener {
            override fun onItemClick(position: Int) {
                list.filterIndexed { index, optionsModel ->
                    if (optionsModel.isSelected) {
                        optionsAdapter.notifyItemChanged(index)
                        list[index].isSelected = false
                        true
                    } else {
                        false
                    }
                }

                list[position].isSelected = true
                optionsAdapter.notifyItemChanged(position)
            }
        })
        binding.rvOptions.adapter = optionsAdapter
    }

    private fun getResource(name: String): Drawable? {
        val resID = this.resources.getIdentifier(name, "drawable", requireActivity().packageName)
        return ActivityCompat.getDrawable(requireActivity(), resID)
    }
}

class OptionsModel(val value: String, var isSelected: Boolean = false)