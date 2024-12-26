package com.sensifyawareapp.ui.glanceaware.fragments.model

class GlanceModel(
    val imageA: String,
    val imageB: String,

    var isFistShownA: Boolean,

    val correctName: String,
    val correctProfession: String,

    val extraNames: ArrayList<String>,
    val extraProfession: ArrayList<String>
)