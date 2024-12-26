package com.sensifyawareapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GlanceAware(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "start_date") val startDate: Long?,
    @ColumnInfo(name = "end_date") val endDate: Long?,
    @ColumnInfo(name = "correct_answer") val correctAnswer: Int?,
    @ColumnInfo(name = "total_q") val totalQ: Int?,
    @ColumnInfo(name = "level") val level: String?
)