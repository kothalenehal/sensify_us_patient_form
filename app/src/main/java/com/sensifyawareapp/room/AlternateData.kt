package com.sensifyawareapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlternateData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "tube_id") val tubeId: Int?,
    @ColumnInfo(name = "tube_value") val tubeValue: String?,
    @ColumnInfo(name = "tube_alternate_value") val tubeAlternateValue: String?,
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "updated_at") val updatedAt: String?,
    @ColumnInfo(name = "location_id") val locationId: Int?,
)