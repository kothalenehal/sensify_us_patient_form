package com.sensifyawareapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioAware(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "start_date") val startDate: Long?,
    @ColumnInfo(name = "end_date") val endDate: Long?,
    @ColumnInfo(name = "score") val score: Int?,
    @ColumnInfo(name = "total") val total: Int?,
    @ColumnInfo(name = "level") val level: String?,
    @ColumnInfo(name = "evaluation") val evaluation: String?
)