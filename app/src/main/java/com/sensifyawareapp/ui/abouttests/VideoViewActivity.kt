package com.sensifyawareapp.ui.abouttests

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import com.sensifyawareapp.databinding.ActivityAboutSmellLossBinding
import com.sensifyawareapp.databinding.ActivityVideoViewBinding
import com.sensifyawareapp.ui.BaseActivity

class VideoViewActivity : BaseActivity() {
    lateinit var binding: ActivityVideoViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoUrl1 = "https://s3.amazonaws.com/www.sensifyaware.com/sensify_low_res.mp4"
//        val videoUrl1 = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"

        val videoUri = Uri.parse(videoUrl1)
        binding.videoView.setVideoURI(videoUri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        mediaController.setMediaPlayer(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        binding.videoView.start()
//        binding.videoView.setOnPreparedListener {  }

    }

}