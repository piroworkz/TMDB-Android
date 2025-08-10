package com.davidluna.tmdb.media_framework.data.local.database.entities.media

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeys(
    val lastPage: Int,
    @PrimaryKey(autoGenerate = false)
    val category: String,
    val reachedEndOfPagination: Boolean,
    val savedOnTimeMillis: Long
)