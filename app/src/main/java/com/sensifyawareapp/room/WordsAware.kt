package com.sensifyawareapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordsAware(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "start_date") val startDate: Long?,
    @ColumnInfo(name = "end_date") val endDate: Long?,
    @ColumnInfo(name = "total_words") val totalWords: Int?,
    @ColumnInfo(name = "reference_words") val referenceWords: Int?
)