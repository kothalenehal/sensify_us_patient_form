package com.sensifyawareapp.ui.glanceaware.fragments

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentMemorizeBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.glanceaware.GlanceAwareViewModel
import com.sensifyawareapp.ui.glanceaware.fragments.model.GlanceModel
import org.json.JSONObject
import java.util.Locale
import kotlin.random.Random

class MemorizeFragment : BaseFragment() {

    companion object {
        private const val TIMER_DURATION = 7000L
        private const val MAX_GLANCES = 12
        private const val TOTAL_AVAILABLE_GLANCES = 120
    }

    private lateinit var binding: FragmentMemorizeBinding
    private lateinit var viewModel: GlanceAwareViewModel

    private var currentIndex = 0

    private fun getJsonFilePath(): String {
        val languageCode = Locale.getDefault().language
        val filePath = "$languageCode/GlanceAware.json"
        return if (fileExists(filePath)) filePath else "en/GlanceAware.json"
    }

    private fun fileExists(filePath: String): Boolean {
        return try {
            context?.assets?.open(filePath)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun readJsonFromFile() {
        try {
            val jsonPath = getJsonFilePath()
            context?.assets?.open(jsonPath)?.bufferedReader().use { reader ->
                reader?.readText()?.let { pickRandom12Glances(it) }
            }
        } catch (e: Exception) {
            Log.e("MemorizeFragment", "Error reading JSON", e)
            showError(getString(R.string.unknown_error_occurred))
        }
    }


    // Modified click listener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentIndex = it.getInt("currentIndex", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemorizeBinding.inflate(inflater)
        binding.timerProgress = 7
        viewModel = ViewModelProviders.of(requireActivity())[GlanceAwareViewModel::class.java]
        return binding.root
    }

    lateinit var timer: CountDownTimer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (SensifyAwareApplication.glanceModelList == null) {
            readJsonFromFile()
            showInXml()
        } else {
            showInXml()
        }

        timer = object : CountDownTimer(7000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isVisible) {
                    viewModel.timerLivedata.value = "00:0${millisUntilFinished / 1000}"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.progressTimer.setProgress(
                            (millisUntilFinished / 1000).toInt(),
                            true
                        )
                    } else binding.timerProgress = (millisUntilFinished / 1000).toInt()
                }
            }

            override fun onFinish() {
                onNext()
            }
        }
        timer.start()

        binding.btnNext.setOnClickListener {
            onNext()
        }
    }


    private fun onNext() {
        if (isVisible) {
            if (currentIndex == 11) {
//                    if (currentIndex == 1) {
                SensifyAwareApplication.glanceModelList!!.shuffle()
                findNavController().navigate(MemorizeFragmentDirections.actionMemorizeFragmentToReadyToBeginFragment())
            } else {
                val bundle = Bundle()
                bundle.putInt("currentIndex", ++currentIndex)
                findNavController().navigate(
                    R.id.action_MemorizeFragment_self,
                    bundle
                )
            }
        }
    }

    private fun showInXml() {
        binding.name = SensifyAwareApplication.glanceModelList!![currentIndex].correctName
        binding.profession = SensifyAwareApplication.glanceModelList!![currentIndex].correctProfession
        viewModel.IndexLivedata.value = "${currentIndex + 1}"
        Log.e("TAG", "showInXml: ${SensifyAwareApplication.glanceModelList!![currentIndex].imageA}", )
        Log.e("TAG", "showInXml: 11 ${SensifyAwareApplication.glanceModelList!![currentIndex].imageB}", )
        if (SensifyAwareApplication.glanceModelList!![currentIndex].isFistShownA)
            binding.photo =
                getResource(SensifyAwareApplication.glanceModelList!![currentIndex].imageA)
        else
            binding.photo =
                getResource(SensifyAwareApplication.glanceModelList!![currentIndex].imageB)

    }

    override fun onPause() {
        super.onPause()
        viewModel.IndexLivedata.value = ""
        viewModel.timerLivedata.value = ""
    }



    private fun pickRandom12Glances(allGlances: String) {
        val glancesJson = JSONObject(allGlances)

        val arrayListTemp = java.util.ArrayList<Int>()
        val random = Random(System.nanoTime())
        while (arrayListTemp.size < 12) {
            val nextInt = (0..119).random(random)
//            while (arrayListTemp.size < 2) {
//                val nextInt = (0..4).random(random)
            if (!arrayListTemp.contains(nextInt)) {
                arrayListTemp.add(nextInt)

                val glance = glancesJson.getJSONArray("glaces")[nextInt] as JSONObject
//                Log.e("TAG", "pickRandom12Glances: $glance", )
                val extraNamesJson = glance.getJSONArray("extraNames")
                val extraNames = ArrayList<String>()
                for (i in 0 until extraNamesJson.length()) {
                    extraNames.add(extraNamesJson.getString(i))
                }

                val extraProfessionJson = glance.getJSONArray("extraProfession")
                val extraProfession = ArrayList<String>()
                for (i in 0 until extraProfessionJson.length()) {
                    extraProfession.add(extraProfessionJson.getString(i))
                }
                val isFistShownA = (0..1).random(random) == 0
                SensifyAwareApplication.addGlanceData(
                    GlanceModel(
                        glance.getString("imageA"),
                        glance.getString("imageB"),
                        isFistShownA,
                        glance.getString("correctName"),
                        glance.getString("correctProfession"),
                        extraNames,
                        extraProfession
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        timer.cancel();
        super.onDestroy()
    }

    private fun getResource(name: String): Drawable? {
        val resID = this.resources.getIdentifier(name, "drawable", requireActivity().packageName)
        return ActivityCompat.getDrawable(requireActivity(), resID)
    }
}