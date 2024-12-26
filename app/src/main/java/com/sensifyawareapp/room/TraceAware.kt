package com.sensifyawareapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TraceAware(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "start_date") val startDate: Long?,
    @ColumnInfo(name = "end_date") val endDate: Long?,
    @ColumnInfo(name = "avg_time") val avgTime: Float?,
    @ColumnInfo(name = "tracing_accuracy") val tracingAccuracy: Int?,
    @ColumnInfo(name = "recall_accuracy") val recallAccuracy: Int?,
    @ColumnInfo(name = "total_q") val totalQ: Int?,
    @ColumnInfo(name = "level") val level: String?
)