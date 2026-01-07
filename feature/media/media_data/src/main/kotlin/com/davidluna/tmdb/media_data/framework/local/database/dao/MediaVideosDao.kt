package com.davidluna.tmdb.media_data.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davidluna.tmdb.media_data.framework.local.database.entities.videos.RoomVideo

@Dao
interface MediaVideosDao {

    @Query("SELECT * FROM RoomVideo WHERE mediaId = :mediaId")
    suspend fun getVideo(mediaId: Int): List<RoomVideo>

    @Query("DELETE FROM RoomVideo WHERE mediaId = :mediaId")
    suspend fun deleteVideo(mediaId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<RoomVideo>)

    @Transaction
    suspend fun cacheVideos(videos: List<RoomVideo>, isCacheExpired: Boolean): List<RoomVideo> {
        if (videos.isEmpty()) {
            return emptyList()
        }
        val mediaId = videos.first().mediaId
        if (isCacheExpired) {
            deleteVideo(mediaId)
        }
        insertVideos(videos)
        return getVideo(mediaId)
    }
}