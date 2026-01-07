package com.davidluna.tmdb.media_data.framework.local.database.entities.details

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomMediaDetails(
    @PrimaryKey
    val id: Int,
    val title: String,
    val releaseDate: String,
    val runtime: Int,
    val posterPath: String,
    val backdropPath: String,
    val overview: String,
    val tagline: String,
    val hasVideo: Boolean,
    val voteAverage: Double,
    val genres: List<RoomGenre>,
    val savedOnTimeMillis: Long
)