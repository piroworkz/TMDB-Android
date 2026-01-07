package com.davidluna.tmdb.media_data.framework.local.database.entities.details

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class RoomGenre(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
)