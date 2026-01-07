package com.davidluna.tmdb.media_data.framework.local.database.entities.credits

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomCrew(
    val adult: Boolean,
    val creditId: String,
    val department: String,
    val gender: Int,
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val job: String,
    val knownForDepartment: String,
    val name: String,
    val originalName: String,
    val popularity: Double,
    val profilePath: String
)