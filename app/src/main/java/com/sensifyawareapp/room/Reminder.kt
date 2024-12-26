package com.sensifyawareapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "test_name") val testName: String?,
    @ColumnInfo(name = "start_date") val startDate: String?,
    @ColumnInfo(name = "end_date") val endDate: String?,
    @ColumnInfo(name = "reminder_time") val reminderTime: String?,
    @ColumnInfo(name = "repeat_selection") val repeatSelection: Int?,
    @ColumnInfo(name = "custom_selection") val customSelection: String?,
    @ColumnInfo(name = "interval_number") val intervalNumber: Int?,
    @ColumnInfo(name = "custom_days_of_week") val customDaysOfWeek: String?,
    @ColumnInfo(name = "repeat_time") val repeatTime: String?,
    @ColumnInfo(name = "icon_number") val iconNumber: Int?,
    @ColumnInfo(name = "selected_interval") val selectedInterval: String?
)

