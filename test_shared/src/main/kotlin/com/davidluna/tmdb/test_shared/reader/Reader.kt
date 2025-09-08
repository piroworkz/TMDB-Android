@file:Suppress("unused")

package com.davidluna.tmdb.test_shared.reader

import kotlinx.serialization.json.Json
import java.io.InputStream

object Reader {
    const val USER_ACCOUNT = "user_account.json"
    const val MOVIE_LIST = "movie_list.json"
    const val MOVIE_DETAIL = "movie_detail.json"
    const val MOVIE_CREDITS = "movie_credits.json"
    const val MOVIE_VIDEOS = "movie_videos.json"
    const val MOVIE_IMAGES = "movie_images.json"
    const val TV_SHOW_LIST = "tv_show_list.json"
    const val TV_SHOW_DETAIL = "tv_show_detail.json"
    const val TV_SHOW_CREDITS = "tv_show_credits.json"
    const val TV_SHOW_VIDEOS = "tv_show_videos.json"
    const val TV_SHOW_IMAGES = "tv_show_images.json"
    const val AUTH_TOKEN_NEW = "auth_token_new.json"
    const val AUTH_SESSION_NEW = "auth_session_new.json"
    const val AUTH_GUEST_SESSION = "auth_guest_session.json"
    const val VALIDATE_WITH_LOGIN = "validate_with_login.json"
    const val REMOTE_ERROR = "remote_error_message.json"

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    inline fun <reified T> fromJson(fileName: String): T =
        this::class.java.getResourceAsStream("/raw/$fileName").use { stream: InputStream? ->
            requireNotNull(stream) { "File not found" }
            val jsonString = stream.bufferedReader().use { it.readText() }
            json.decodeFromString<T>(jsonString)
        }

    fun fromFile(fileName: String): String =
        this::class.java.getResourceAsStream("/raw/$fileName").use { stream: InputStream? ->
            requireNotNull(stream) { "File not found" }
            stream.bufferedReader().use { it.readText() }
        }

}