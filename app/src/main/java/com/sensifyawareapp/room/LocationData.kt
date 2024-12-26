package com.sensifyawareapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class LocationData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "language_id") val languageId: Int?,
    @ColumnInfo(name = "language_name") val language: String?,
)