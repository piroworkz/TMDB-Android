package com.davidluna.tmdb.media_data.framework.local.database.entities.details

import androidx.room.Embedded
import androidx.room.Relation
import com.davidluna.tmdb.media_data.framework.local.database.entities.credits.RoomCast
import com.davidluna.tmdb.media_data.framework.local.database.entities.images.RoomImage

data class RoomMediaDetailsRelations(
    @Embedded val details: RoomMediaDetails,
    @Relation(
        parentColumn = "id",
        entityColumn = "mediaId"
    )
    val images: List<RoomImage>,
    @Relation(
        parentColumn = "id",
        entityColumn = "mediaId"
    )
    val cast: List<RoomCast>
)