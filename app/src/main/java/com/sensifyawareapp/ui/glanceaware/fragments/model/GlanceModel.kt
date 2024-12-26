package com.sensifyawareapp.ui.glanceaware.fragments.model

class GlanceModel(
    val imageA: String,
    val imageB: String,

    var isFistShownA: Boolean,

    val correctName: String,
    val correctProfession: String,

    // English Properties
    val extraNames: ArrayList<String>,
    val extraProfession: ArrayList<String>,

    // Hindi Properties
    val correctNameHindi: String,
    val correctProfessionHindi: String,
    val extraNamesHindi: ArrayList<String>,
    val extraProfessionHindi: ArrayList<String>
)
