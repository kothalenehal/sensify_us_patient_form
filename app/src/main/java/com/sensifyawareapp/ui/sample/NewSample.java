package com.sensifyawareapp.ui.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sensifyawareapp.databinding.FragmentWordsAwareListeningBinding;
import com.sensifyawareapp.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.Locale;

public class NewSample extends BaseFragment {

    FragmentWordsAwareListeningBinding binding;
    CountDownTimer countDownTimer;
    long timeMilliSec = 60000;
    boolean isTimeRunning;
    SpeechRecognizer speechRecognizer;
    StringBuilder builder = new StringBuilder();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWordsAwareListeningBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext());
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
        intent.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                10000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, new Long(70000));
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizer.startListening(intent);

//        speechRecognizer.stopListening();
        /*binding.startButton.setOnClickListener(view -> {
            if (isTimeRunning) {
                stopTimer();
                speechRecognizer.stopListening();
                binding.startButton.setText("Listening");
            } else {
                binding.startButton.setText("Stop Recording");
                startTimer();
                speechRecognizer.startListening(intent);
            }
        });
        timeUpdate(timeMilliSec);*/

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                assert binding.nameText1 != null;
                builder.append(binding.nameText1.getText().toString());
                Log.e("onEndOfSpeech ", builder.toString());
                binding.nameText1.setText("");
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && data.get(0).length() > 0) {
                    Log.e("Data ", data.get(0));
                    assert binding.nameText1 != null;
                    binding.nameText1.setText(data.get(0));
                    Log.e("Data 22", binding.nameText1.getText().toString());
//                    binding.nameText1?.text = data[0]
                    speechRecognizer.startListening(intent);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                if (bundle != null) {
                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (data != null && data.size() > 0) {
//                        Log.e("Data 111", data.get(0));
                        assert binding.nameText1 != null;
                        binding.nameText1.setText(data.get(0));
                        Log.e("Data 22", binding.nameText1.getText().toString());
//                    speechRecognizer.startListening(intent);
                    }
                }

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

}
