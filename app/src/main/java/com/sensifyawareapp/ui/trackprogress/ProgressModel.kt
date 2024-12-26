package com.sensifyawareapp.ui.trackprogress

class ProgressModel(
    val startDate: Long,
    val endDate: Long,
    val score: Int,
    val totalScore: Int,
    val isTraceAware: Boolean = false,
    val isWordsAware: Boolean = false,
    val tracingAccuracy: Int? = null,
    val recallAccuracy: Int? = null,
    val avgTime: String? = null
)