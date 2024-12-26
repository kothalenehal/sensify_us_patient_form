package com.sensifyawareapp.utils.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class SpeechRecognizer {
    private lateinit var speechRecognizer: SpeechRecognizer

    fun speechRecognizer(activity:Activity) {
        var longest: String
        var smallest: String
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizer.startListening(intent)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(p0: Int) {
                Log.e("Error", "" + p0)
            }

            override fun onResults(p0: Bundle) {
                val data = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.e("Speech", "" + (data?.get(0)))
                if (data != null) {
                    val words = data[0].split(" ")

                    longest = words[0]
                    smallest = words[0]
                    for (i in 1 until words.size) {
                        if (words[i].length > longest.length) {
                            longest = words[i]
                        }
                        if (words[i].length < smallest.length) {
                            smallest = words[i]
                        }
                    }
//                    uniqueWord = "${words.size}"
//                    repeatWord = "${words.groupingBy { it }.eachCount().filter { it.value > 1 }.values.size}"
//                    binding.tvSample.text =longest
                }
            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }
        })
        return
    }

}