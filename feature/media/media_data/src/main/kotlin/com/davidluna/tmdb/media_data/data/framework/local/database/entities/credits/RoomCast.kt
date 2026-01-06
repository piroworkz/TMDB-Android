package com.davidluna.tmdb.media_data.data.framework.local.database.entities.credits

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.details.RoomMediaDetails

@Entity(
    primaryKeys = ["castId", "mediaId"],
    foreignKeys = [
        ForeignKey(
            entity = RoomMediaDetails::class,
            parentColumns = ["id"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["mediaId"])]
)
data class RoomCast(
    val character: String,
    val castId: Int,
    val mediaId: Int,
    val name: String,
    val profilePath: String,
)