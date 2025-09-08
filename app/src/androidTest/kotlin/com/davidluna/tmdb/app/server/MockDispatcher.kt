package com.davidluna.tmdb.app.server

import com.davidluna.tmdb.test_shared.reader.Reader
import com.davidluna.tmdb.test_shared.reader.Reader.AIRING_TODAY_TV_SHOWS
import com.davidluna.tmdb.test_shared.reader.Reader.AUTH_GUEST_SESSION
import com.davidluna.tmdb.test_shared.reader.Reader.AUTH_SESSION_NEW
import com.davidluna.tmdb.test_shared.reader.Reader.AUTH_TOKEN_NEW
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_CREDITS
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_DETAIL
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_IMAGES
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_LIST
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_VIDEOS
import com.davidluna.tmdb.test_shared.reader.Reader.NOW_PLAYING_MOVIES
import com.davidluna.tmdb.test_shared.reader.Reader.ON_THE_AIR_TV_SHOWS
import com.davidluna.tmdb.test_shared.reader.Reader.POPULAR_MOVIES
import com.davidluna.tmdb.test_shared.reader.Reader.POPULAR_TV_SHOWS
import com.davidluna.tmdb.test_shared.reader.Reader.REMOTE_ERROR
import com.davidluna.tmdb.test_shared.reader.Reader.TOP_RATED_MOVIES
import com.davidluna.tmdb.test_shared.reader.Reader.TOP_RATED_TV_SHOWS
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_CREDITS
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_DETAIL
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_IMAGES
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_LIST
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_VIDEOS
import com.davidluna.tmdb.test_shared.reader.Reader.UPCOMING_MOVIES
import com.davidluna.tmdb.test_shared.reader.Reader.USER_ACCOUNT
import com.davidluna.tmdb.test_shared.reader.Reader.VALIDATE_WITH_LOGIN
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockDispatcher() : Dispatcher() {

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path: String = request.path.orEmpty()
        val fileName = when {
            path.isEmpty() -> REMOTE_ERROR
            path.contains("authentication/token/new") -> AUTH_TOKEN_NEW
            path.contains("authentication/session/new") -> AUTH_SESSION_NEW
            path.contains("authentication/guest_session/new") -> AUTH_GUEST_SESSION
            path.contains("authentication/token/validate_with_login") -> VALIDATE_WITH_LOGIN
            path.contains("account") -> USER_ACCOUNT
            path.contains("movie/popular") -> POPULAR_MOVIES
            path.contains("movie/top_rated") -> TOP_RATED_MOVIES
            path.contains("movie/upcoming") -> UPCOMING_MOVIES
            path.contains("movie/now_playing") -> NOW_PLAYING_MOVIES
            path.contains("tv/airing_today") -> AIRING_TODAY_TV_SHOWS
            path.contains("tv/on_the_air") -> ON_THE_AIR_TV_SHOWS
            path.contains("tv/popular") -> POPULAR_TV_SHOWS
            path.contains("tv/top_rated") -> TOP_RATED_TV_SHOWS
            path.matchesRegex(".*/movie/\\d+/credits.*") -> MOVIE_CREDITS
            path.matchesRegex(".*/movie/\\d+/images.*") -> MOVIE_IMAGES
            path.matchesRegex(".*/movie/\\d+/videos.*") -> MOVIE_VIDEOS
            path.matchesRegex(".*/movie/\\d+(/recommendations|/similar).*") -> MOVIE_LIST
            path.matchesRegex(".*/movie/\\d+.*") -> MOVIE_DETAIL
            path.matchesRegex(".*/tv/\\d+/credits.*") -> TV_SHOW_CREDITS
            path.matchesRegex(".*/tv/\\d+/images.*") -> TV_SHOW_IMAGES
            path.matchesRegex(".*/tv/\\d+/videos.*") -> TV_SHOW_VIDEOS
            path.matchesRegex(".*/tv/\\d+(/recommendations|/similar).*") -> TV_SHOW_LIST
            path.matchesRegex(".*/tv/\\d+.*") -> TV_SHOW_DETAIL
            else -> REMOTE_ERROR
        }
        return fromFile(fileName)
    }

    private fun String.matchesRegex(regex: String) = Regex(regex).containsMatchIn(this)

    private fun fromFile(fileName: String): MockResponse {
        return try {
            val body = Reader.fromFile(fileName)
            MockResponse().setBody(body)
        } catch (_: Exception) {
            MockResponse().setBody(Reader.fromFile(REMOTE_ERROR))
        }
    }
}