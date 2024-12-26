package com.sensifyawareapp.fragment

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentTestsBinding
import com.sensifyawareapp.ui.MainViewModel
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.ui.abouttests.AboutCognitiveTestsActivity
import com.sensifyawareapp.ui.abouttests.AboutScentAwareActivity
import com.sensifyawareapp.ui.abouttests.VideoViewActivity
import com.sensifyawareapp.ui.audioaware.AudioAwareActivity
import com.sensifyawareapp.ui.auth.AuthActivity
import com.sensifyawareapp.ui.glanceaware.GlanceAwareActivity
import com.sensifyawareapp.ui.glanceaware.PatientActivityGlanceAware
import com.sensifyawareapp.ui.patient.AddPatientActivity
import com.sensifyawareapp.ui.scentaware.HealthsQuestionsActivity
import com.sensifyawareapp.ui.scentaware.PatientActivity
import com.sensifyawareapp.ui.traceaware.TraceAwareTutorialActivity
import com.sensifyawareapp.ui.trackprogress.TrackProgressViewModel
import com.sensifyawareapp.utils.Utils
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.Locale

class TestsFragment : BaseFragment() {

    lateinit var binding: FragmentTestsBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var trackViewModel: TrackProgressViewModel
    private var isClinicalTestVersion: Boolean = false
    private var canTestScentAware: Boolean = true
    private var canTestTraceAware: Boolean = true
    private var canTestAudioAware: Boolean = true
    private var canTestGlanceAware: Boolean = true
    private var canTestWordsAware: Boolean = true
    private var canTestGrammarAware: Boolean = true
    private var canTestRenAware: Boolean = true
    lateinit var requestLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClinicalTestVersion =
            prefUtils.getBooleanData(
                requireContext(),
                AppConstant.SharedPreferences.CLINICAL_TEST_VERSION
            )
        Log.e("TAG+", "Language : ${Locale.getDefault().displayLanguage}")
        binding.lifecycleOwner = this
        trackViewModel = ViewModelProviders.of(this)[TrackProgressViewModel::class.java]
        prefUtils.getStringData(requireContext(), AppConstant.SharedPreferences.AGE)
            ?.let {
                Log.e("TAG", "onViewCreated: ${it.isEmpty()} ")
                Log.e("Age ", it)
            }

        /* if (BuildConfig.DEBUG) {
             Log.e("TAG", "onViewCreated: 123 Call", )
         }
         FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);*/
        getAlternateData()

        requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                Toast.makeText(
                    requireContext(),
                    "Request Post Notification Permission Not Given",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        binding.scentAwareLast8Test = getString(R.string.not_tested_yet)
        binding.scentAwareLast16Test = getString(R.string.not_tested_yet)
        binding.audioAwareLastTest = getString(R.string.not_tested_yet)
        binding.traceAwareLastTest = getString(R.string.not_tested_yet)
        binding.glanceAwareLastTest = getString(R.string.not_tested_yet)
        binding.wordsAwareLastTest = getString(R.string.not_tested_yet)
        binding.grammarAwareLastTest = getString(R.string.not_tested_yet)
        binding.renAwareLastTest = getString(R.string.not_tested_yet)

        binding.canTestScentAware = true
        binding.canTestTraceAware = true
        binding.canTestAudioAware = true
        binding.canTestGlanceAware = true
        binding.canTestWordsAware = true
        binding.canTestGramarAware = true
        binding.canTestRenAware = true
        binding.training16 = false
        binding.training8 = false
        binding.audioAware = true
        binding.traceAware = true
        binding.glanceAware = true
        binding.scent16 = false
        binding.scent8 = false

        /*if (Locale.getDefault().displayLanguage.equals("English")) {
            binding.llGrammarAware.cardMenu.visibility = View.VISIBLE
            binding.llWordsAware.cardMenu.visibility = View.VISIBLE
        } else {
            binding.llGrammarAware.cardMenu.visibility = View.GONE
            binding.llWordsAware.cardMenu.visibility = View.GONE
        }*/

        binding.isClinicalTestVersion = isClinicalTestVersion
        binding.userName =
            prefUtils.getStringData(
                requireContext(),
                AppConstant.SharedPreferences.USER_NAME
            )
                ?.split(" ")
                ?.get(0) ?: "User"



        binding.txWatchVideo.setOnClickListener {
            if (!networkUtils.isConnected) {
                showError(getString(R.string.internet_not_available))
            } else {
                startActivity(Intent(requireContext(), VideoViewActivity::class.java))
            }

        }

        binding.llScentAware16.cardMenu.setOnClickListener {
            if (canTestScentAware) {
                if (!networkUtils.isConnected) {
                    showError(getString(R.string.internet_not_available))
                } else {
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        1
                    )
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                        16
                    )
                    if (!isClinicalTestVersion) {
                        if (prefUtils.getBooleanData(
                                requireContext(),
                                AppConstant.SharedPreferences.IS_MODERATOR
                            )
                        ) {
                            startActivity(
                                Intent(
                                    requireContext(), PatientActivity::class.java

                                )
                            )
                        } else {

                            prefUtils.saveData(
                                requireContext(),
                                AppConstant.SharedPreferences.SITE_ID,
                                0
                            )

                            prefUtils.saveData(
                                requireContext(),
                                AppConstant.SharedPreferences.PATIENT_ID,
                                null
                            )

                            prefUtils.saveData(
                                requireContext(),
                                AppConstant.SharedPreferences.SITE_NAME,
                                null
                            )
                            prefUtils.saveData(
                                requireContext(),
                                AppConstant.SharedPreferences.STUDY_NUMBER,
                                null
                            )

                            startActivity(
                                Intent(
                                    requireContext(),
                                    HealthsQuestionsActivity::class.java
                                )
                            )

                        }
                    } else {
                        SensifyAwareApplication.initTestData()
                        startActivity(Intent(requireContext(), TutorialActivity::class.java))
                    }
                }
            } else {
                showError("Please come back later..!")
            }
        }

        binding.llScentAware8.cardMenu.setOnClickListener {
            if (canTestScentAware) {
                if (!networkUtils.isConnected) {
                    showError(getString(R.string.internet_not_available))
                } else {
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        1
                    )
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                        8
                    )
                    if (!isClinicalTestVersion) {

                        if (prefUtils.getBooleanData(
                                requireContext(),
                                AppConstant.SharedPreferences.IS_MODERATOR
                            )
                        ) {
                            startActivity(
                                Intent(
                                    requireContext(), PatientActivity::class.java

                                )
                            )
                        } else {

                            prefUtils.saveData(
                                requireContext(),
                                AppConstant.SharedPreferences.SITE_ID,
                                0
                            )

                            prefUtils.saveData(
                                requireContext(),
                                AppConstant.SharedPreferences.PATIENT_ID,
                                null
                            )

                            prefUtils.saveData(
                                requireContext(),
                                AppConstant.SharedPreferences.SITE_NAME,
                                null
                            )
                            prefUtils.saveData(
                                requireContext(),
                                AppConstant.SharedPreferences.STUDY_NUMBER,
                                null
                            )

                            startActivity(
                                Intent(
                                    requireContext(),
                                    HealthsQuestionsActivity::class.java
                                )
                            )

                        }
                    } else {
                        SensifyAwareApplication.initTestData()
                        startActivity(Intent(requireContext(), TutorialActivity::class.java))
                    }
                }
            } else {
                showError("Please come back later..!")
            }
        }

        binding.llTraining8.cardMenu.setOnClickListener {
            if (!networkUtils.isConnected) {
                showError(getString(R.string.internet_not_available))
            } else {
                prefUtils.saveData(
                    requireContext(),
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    2
                )
                prefUtils.saveData(
                    requireContext(),
                    AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                    8
                )
                SensifyAwareApplication.initTestData()
                startActivity(Intent(requireContext(), TutorialActivity::class.java))
                /*if (!isClinicalTestVersion) startActivity(
                    Intent(
                        requireContext(),
                        HealthsQuestionsActivity::class.java
                    )
                )
                else {
                    SensifyAwareApplication.initTestData()
                    startActivity(Intent(requireContext(), TutorialActivity::class.java))
                }*/
            }
        }

        binding.llTraining16.cardMenu.setOnClickListener {
            if (!networkUtils.isConnected) {
                showError(getString(R.string.internet_not_available))
            } else {
                prefUtils.saveData(
                    requireContext(),
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    2
                )
                prefUtils.saveData(
                    requireContext(),
                    AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                    16
                )
                SensifyAwareApplication.initTestData()
                startActivity(Intent(requireContext(), TutorialActivity::class.java))
                /* if (!isClinicalTestVersion) startActivity(
                     Intent(
                         requireContext(),
                         HealthsQuestionsActivity::class.java
                     )
                 )
                 else {
                     SensifyAwareApplication.initTestData()
                     startActivity(Intent(requireContext(), TutorialActivity::class.java))
                 }*/
            }
        }

        binding.btnNewPatientForm.setOnClickListener {
            if (!networkUtils.isConnected) {
                // Show error if not connected to the internet
                showError(getString(R.string.internet_not_available))
            } else {
                // Optionally, save any data or preferences here
                prefUtils.saveData(
                    requireContext(),
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    2
                )

                // Navigate to AddPatientActivity
                val intent = Intent(requireContext(), AddPatientActivity::class.java)
                startActivity(intent)
            }
        }


        /*binding.llScentAwareReTraining.cardMenu.setOnClickListener {
            if (!networkUtils.isConnected) {
                showError(getString(R.string.internet_not_available))
            } else {
                prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.SELECTED_MENU, 2)
                startActivity(Intent(requireContext(), SelectKitActivity::class.java))
            }
        }*/

        binding.llTraceAware.cardMenu.setOnClickListener {
            if (canTestTraceAware) {
                if (!networkUtils.isConnected) {
                    showError(getString(R.string.internet_not_available))
                } else {
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        3
                    )
                    startActivity(
                        Intent(
                            requireContext(),
                            TraceAwareTutorialActivity::class.java
                        )
                    )
                }
            } else {
                showError("Please come back later..!")
            }
        }

        binding.llAudioAware.cardMenu.setOnClickListener {
            if (canTestAudioAware) {
                if (!networkUtils.isConnected) {
                    showError(getString(R.string.internet_not_available))
                } else {
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        4
                    )
                    if (!isClinicalTestVersion) startActivity(
                        Intent(requireContext(), AudioAwareActivity::class.java).putExtra(
                            "level",
                            1
                        )
                    )
                    /*startActivity(
                        Intent(
                            requireContext(),
                            SelectAwareLevelActivity::class.java
                        ).putExtra("specificQuestion", "LossOfMemory")
                    )*/
                    else startActivity(
                        Intent(requireContext(), AudioAwareActivity::class.java).putExtra(
                            "level",
                            2
                        )
                    )
                }
            } else {
                showError("Please come back later..!")
            }
        }


//        binding.llGlanceAware.cardMenu.setOnClickListener {
//            if (canTestGlanceAware) {
//                if (!networkUtils.isConnected) {
//                    showError(getString(R.string.internet_not_available))
//                } else {
//                    prefUtils.saveData(
//                        requireContext(),
//                        AppConstant.SharedPreferences.SELECTED_MENU,
//                        5
//                    )
//                    if (!isClinicalTestVersion)
//
//                        startActivity(
//                            Intent(
//                                requireContext(), PatientActivity::class.java
//
//                            )
//                        )
//
//
//                        startActivity(
//
//
//                            Intent(requireContext(), GlanceAwareTutorialActivity::class.java)/*.putExtra(
//                                "level",
//                                1
//                            )*/
//                        )
//                    /* startActivity(
//                         Intent(
//                             requireContext(),
//                             GlanceAwareTutorialActivity::class.java
//                         )
//                     )*/
//                    else
//                        startActivity(
//                            Intent(requireContext(), GlanceAwareActivity::class.java).putExtra(
//                                "level",
//                                2
//                            )
//                        )
//                }
//            } else {
//                showError("Please come back later..!")
//            }
//        }


        //new

        binding.llGlanceAware.cardMenu.setOnClickListener {
            if (canTestGlanceAware) {
                // Check for internet connectivity
                if (!networkUtils.isConnected) {
                    showError(getString(R.string.internet_not_available))
                } else {
                    // Save the selected menu value
                    // Save Patient ID and Study Number
                    if (prefUtils.getBooleanData(
                            requireContext(),
                            AppConstant.SharedPreferences.IS_MODERATOR
                        )
                    ) {
                        startActivity(
                            Intent(
                                requireContext(), PatientActivity::class.java

                            )
                        )
                    }
                    // Start the first activity: PatientActivity
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        5
                    )
                    // Determine which tutorial or activity to start based on conditions
                    if (!isClinicalTestVersion) {
                        startActivity(
                            Intent(
                                requireContext(),
                                PatientActivityGlanceAware::class.java
                            )
                        )
//                        startActivity(
//                            Intent(
//                                requireContext(),
//                                GlanceAwareTutorialActivity::class.java
//                            ) // Optionally add .putExtra if needed
//                        )
                    } else {

                        startActivity(
                            Intent(
                                requireContext(),
                                GlanceAwareActivity::class.java
                            ).putExtra(
                                "level",
                                2 // Pass level 2 for clinical test version
                            )
                        )
                    }
                }
            } else {
                // Show an error if the test cannot proceed
                showError("Please come back later..!")
            }
        }

        /*binding.llWordsAware.cardMenu.setOnClickListener {
            if (canTestWordsAware) {
                if (!networkUtils.isConnected) {
                    showError(getString(R.string.internet_not_available))
                } else {
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        6
                    )
//                    if (!isClinicalTestVersion)
                    startActivity(
                        Intent(requireContext(), WordsAwareActivity::class.java)
                    )
                    *//* else
                         startActivity(
                             Intent(this, GlanceAwareActivity::class.java).putExtra(
                                 "level",
                                 2
                             )
                         )*//*
                }
            } else {
                showError("Please come back later..!")
            }
        }

        binding.llGrammarAware.cardMenu.setOnClickListener {
            if (canTestGrammarAware) {
                if (!networkUtils.isConnected) {
                    showError(getString(R.string.internet_not_available))
                } else {
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        7
                    )
                    if (!isClinicalTestVersion)
                        startActivity(
                            Intent(
                                requireContext(),
                                GrammarAwareLevelActivity::class.java
                            )
                        )
                    else
                        startActivity(
                            Intent(
                                requireContext(),
                                GrammarAwareIntroActivity::class.java
                            ).putExtra(
                                "level",
                                2
                            )
                        )
                }
            } else {
                showError("Please come back later..!")
            }
        }

        binding.llRenAware.cardMenu.setOnClickListener {
            if (canTestRenAware) {
                if (!networkUtils.isConnected) {
                    showError(getString(R.string.internet_not_available))
                } else {
                    prefUtils.saveData(
                        requireContext(),
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        7
                    )
//                    if (!isClinicalTestVersion)
                    startActivity(
                        Intent(requireContext(), RenAwareQRCodeActivity::class.java)
                    )
                    *//* else
                         startActivity(
                             Intent(this, GlanceAwareActivity::class.java).putExtra(
                                 "level",
                                 2
                             )
                         )*//*
                }
            } else {
                showError("Please come back later..!")
            }
        }*/

        /* binding.llTrackProgress.cardMenu.setOnClickListener {
             startActivity(Intent(requireContext(), TrackProgressActivity::class.java))
         }*/

        binding.tvLearn.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AboutScentAwareActivity::class.java
                )
            )
        }

        binding.tvLearn2.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AboutScentAwareActivity::class.java
                )
            )
        }

        binding.tvLearn3.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AboutCognitiveTestsActivity::class.java
                )
            )

        }
    }

    override fun onResume() {
        super.onResume()
        clearScentAwarePrefData()
        clearTraceAwarePrefData()
        clearGlanceAwarePrefData()

        getLastTestDates()
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.SELECTED_MENU, 0)
        SensifyAwareApplication.tubeTestsList.clear()
    }
    private fun getAlternateData() {
        showLoader()

        val query = JSONObject()
        query.put("Url", "/get-tubeid-alternate-data")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val query3 = JSONObject()
        query3.put("Url", "/get-studynumber")
        query3.put(
            "userId",
            prefUtils.getIntData(requireContext(), AppConstant.SharedPreferences.USER_ID)
        )

        val body3: RequestBody = query3.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val query2 = JSONObject()
        query2.put(
            "EmailAddress",
            prefUtils.getStringData(requireContext(), AppConstant.SharedPreferences.EMAIL)
        )
        query2.put("Url", "/get-location")

        val body2: RequestBody = query2.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.getStudyNumber(body3).subscribe({
            val jsonList2 = Gson().toJson(it.data?.studyNumber)
            prefUtils.saveData(
                requireActivity(),
                AppConstant.SharedPreferences.STUDY_NUMBER,
                jsonList2
            )
        }, {
            handleError(it)
        })

        apiManager.getLocation(body2).subscribe({
            if (it.data.isUserOld) {
                Utils.logout(requireActivity(), prefUtils)
                startActivity(
                    Intent(
                        requireActivity(),
                        AuthActivity::class.java
                    ).putExtra("showLogin", true)
                )
                requireActivity().finishAffinity()
            } else {
                trackViewModel.insertLocationData(it.data.locationList)
            }
        }, {
            handleError(it)
        })

        apiManager.getAlternateData(body).subscribe({
            hideLoader()
            val alternateDataList = it.data.tubeIdAlternateDatas
            trackViewModel.insertAlternateData(alternateDataList)
        }, {
            handleError(it)
        })

        trackViewModel.insertAltDataFinish.observe(viewLifecycleOwner) {
            getData()


        }

        trackViewModel.locationDataList.observe(viewLifecycleOwner) {
            var languageId = 2 //default to english
            for (language in it) {
                Log.e("TAG", "languageId: ${language.languageId}")
                languageId = language.languageId!!
                break
            }
            trackViewModel.getAlternateData(languageId)
        }

        trackViewModel.alternateDataList.observe(viewLifecycleOwner) {
            SensifyAwareApplication.alternateDataList.clear()
            for (tubes in it) {
                val dataMap = Pair(
                    tubes.tubeValue,
                    tubes.tubeAlternateValue
                )
                SensifyAwareApplication.alternateDataList.add(dataMap)
            }
            Log.e(
                "TAG",
                "alternateDataList Size : ${SensifyAwareApplication.alternateDataList.size} ",
            )
        }

    }

    private fun getData() {
        val currentLanguage: String
        when (Locale.getDefault().language) {
            "de" -> {
                currentLanguage = "German"
            }

            "es" -> {
                currentLanguage = "Spanish"
            }

            "fr" -> {
                currentLanguage = "French"
            }

            "it" -> {
                currentLanguage = "Italian"
            }

            "ja" -> {
                currentLanguage = "Japanese"
            }

            "nl" -> {
                currentLanguage = "Dutch"
            }

            "sw" -> {
                currentLanguage = "Swahili"
            }

            else -> {
                currentLanguage = "English"
            }

        }
        trackViewModel.getLocationData(currentLanguage)

    }

    private fun getLastTestDates() {
        viewModel.scentAware8Date.observe(viewLifecycleOwner) {
            if (canTestScentAware && isClinicalTestVersion)
                binding.scentAwareLast8Test = getString(R.string.ready_to_test)
            else
                binding.scentAwareLast8Test = it
        }

        viewModel.scentAware16Date.observe(viewLifecycleOwner) {
            if (canTestScentAware && isClinicalTestVersion)
                binding.scentAwareLast16Test = getString(R.string.ready_to_test)
            else
                binding.scentAwareLast16Test = it
        }
        viewModel.traceAwareDate.observe(viewLifecycleOwner) {
            if (canTestTraceAware && isClinicalTestVersion)
                binding.traceAwareLastTest = getString(R.string.ready_to_test)
            else binding.traceAwareLastTest = it
        }
        viewModel.audioAwareDate.observe(viewLifecycleOwner) {
            if (canTestAudioAware && isClinicalTestVersion)
                binding.audioAwareLastTest = getString(R.string.ready_to_test)
            else binding.audioAwareLastTest = it
        }
        viewModel.glanceAwareDate.observe(viewLifecycleOwner) {
            if (canTestGlanceAware && isClinicalTestVersion)
                binding.glanceAwareLastTest = getString(R.string.ready_to_test)
            else binding.glanceAwareLastTest = it
        }

        viewModel.wordsAwareDate.observe(viewLifecycleOwner) {
            if (canTestWordsAware && isClinicalTestVersion)
                binding.wordsAwareLastTest = getString(R.string.ready_to_test)
            else binding.wordsAwareLastTest = it
        }
        viewModel.grammarAwareDate.observe(viewLifecycleOwner) {
            if (canTestGrammarAware && isClinicalTestVersion)
                binding.grammarAwareLastTest = getString(R.string.ready_to_test)
            else binding.grammarAwareLastTest = it
        }

        if (isClinicalTestVersion) {
            viewModel.canTestScentAware.observe(viewLifecycleOwner) {
                binding.canTestScentAware = it
                canTestScentAware = it
            }
            viewModel.canTestTraceAware.observe(viewLifecycleOwner) {
                binding.canTestTraceAware = it
                canTestTraceAware = it
            }
            viewModel.canTestAudioAware.observe(viewLifecycleOwner) {
                binding.canTestAudioAware = it
                canTestAudioAware = it
            }
            viewModel.canTestGlanceAware.observe(viewLifecycleOwner) {
                binding.canTestGlanceAware = it
                canTestGlanceAware = it
            }
            viewModel.canTestWordsAware.observe(viewLifecycleOwner) {
                binding.canTestWordsAware = it
                canTestWordsAware = it
            }

            viewModel.canTestGrammarAware.observe(viewLifecycleOwner) {
                binding.canTestGlanceAware = it
                canTestGrammarAware = it
            }
        }
    }

    private fun clearGlanceAwarePrefData() {
        SensifyAwareApplication.clearGlanceAwareObjectFromJSON()
    }

    private fun clearTraceAwarePrefData() {
        SensifyAwareApplication.glanceModelList = null
        SensifyAwareApplication.dsstScore = 0.0f
        SensifyAwareApplication.recallDsstScore = 0.0f
        val folder = File(requireContext().cacheDir, "/traceAwareImages")
        if (folder.exists()) {
            folder.deleteRecursively()
        }
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.GENERATED_RANDOM_QUESTION_NUMBERS,
            null
        )
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.GENERATED_RANDOM_IMAGES_INDEX,
            null
        )
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.CURRENT_QUESTION, 0)
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.COMPLETED_TRACES, 0)
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.SHOW_RECALL_INTRO_MSG,
            false
        )
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.START_TIME, null)
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.END_TIME, null)
        SensifyAwareApplication.clearTraceAwareObjectFromJSON()
    }

    private fun clearScentAwarePrefData() {
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.SELECTED_KIT_SIZE, null)
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.PREF_SCANNED_RESULT,
            null
        )
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.PREF_SCANNED_RESULT_WITH_TIMER,
            null
        )
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.WITH_TIMER, false)
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.ODOR_DIFF_QUESTION, 0)
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.CORRECT_ANSWER_COUNT, 0)
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.ODOR_INTENSITY_QUESTION,
            0
        )
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.PREF_SELECTED_ANSWERS,
            ""
        )
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.START_TIME, null)
        prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.END_TIME, null)
        SensifyAwareApplication.clearScentAwareObjectFromJSON()
        SensifyAwareApplication.clearScentAwareTrainingObjectFromJSON()
    }
}