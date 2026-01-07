package com.davidluna.tmdb.media_data.framework.local.database.entities.images

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomMediaDetails

@Entity(
    primaryKeys = ["filePath", "mediaId"],
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
data class RoomImage(
    val filePath: String,
    val mediaId: Int,
)