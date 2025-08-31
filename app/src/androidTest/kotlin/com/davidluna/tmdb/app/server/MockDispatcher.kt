package com.davidluna.tmdb.app.server

import com.davidluna.tmdb.test_shared.reader.Reader
import com.davidluna.tmdb.test_shared.reader.Reader.AUTH_GUEST_SESSION
import com.davidluna.tmdb.test_shared.reader.Reader.AUTH_SESSION_NEW
import com.davidluna.tmdb.test_shared.reader.Reader.AUTH_TOKEN_NEW
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_CREDITS
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_DETAIL
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_IMAGES
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_LIST
import com.davidluna.tmdb.test_shared.reader.Reader.MOVIE_VIDEOS
import com.davidluna.tmdb.test_shared.reader.Reader.REMOTE_ERROR
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_CREDITS
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_DETAIL
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_IMAGES
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_LIST
import com.davidluna.tmdb.test_shared.reader.Reader.TV_SHOW_VIDEOS
import com.davidluna.tmdb.test_shared.reader.Reader.USER_ACCOUNT
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockDispatcher : Dispatcher() {

    private val movieId = 1022789
    private val tvShowId = 209374

    override fun dispatch(request: RecordedRequest): MockResponse {
        val fileName = when {
            request.path.isNullOrEmpty() -> REMOTE_ERROR
            request.path?.contains("authentication/token/new") == true -> AUTH_TOKEN_NEW
            request.path?.contains("authentication/session/new") == true -> AUTH_SESSION_NEW
            request.path?.contains("authentication/guest_session/new") == true -> AUTH_GUEST_SESSION
            request.path?.contains("account") == true -> USER_ACCOUNT
            request.path?.contains("movie/popular") == true
                    or (request.path?.contains("movie/top_rated") == true)
                    or (request.path?.contains("movie/upcoming") == true)
                    or (request.path?.contains("movie/now_playing") == true) -> MOVIE_LIST

            request.path?.contains("tv/airing_today") == true
                    or (request.path?.contains("tv/on_the_air") == true)
                    or (request.path?.contains("tv/popular") == true)
                    or (request.path?.contains("tv/top_rated") == true) -> TV_SHOW_LIST

            request.path?.contains("movie/$movieId/credits") == true -> MOVIE_CREDITS
            request.path?.contains("movie/$movieId/images") == true -> MOVIE_IMAGES
            request.path?.contains("movie/$movieId/videos") == true -> MOVIE_VIDEOS
            request.path?.contains("movie/$movieId/recommendations") == true -> MOVIE_LIST
            request.path?.contains("movie/$movieId/similar") == true -> MOVIE_LIST
            request.path?.contains("movie/$movieId") == true -> MOVIE_DETAIL
            request.path?.contains("tv/$tvShowId/credits") == true -> TV_SHOW_CREDITS
            request.path?.contains("tv/$tvShowId/images") == true -> TV_SHOW_IMAGES
            request.path?.contains("tv/$tvShowId/videos") == true -> TV_SHOW_VIDEOS
            request.path?.contains("tv/$tvShowId/recommendations") == true -> TV_SHOW_LIST
            request.path?.contains("tv/$tvShowId/similar") == true -> TV_SHOW_LIST
            request.path?.contains("tv/$tvShowId") == true -> TV_SHOW_DETAIL

            else -> REMOTE_ERROR
        }
        return fromFile(fileName)
    }

    private fun fromFile(fileName: String): MockResponse {
        return try {
            MockResponse().setBody(Reader.fromFile(fileName))
        } catch (e: Exception) {
            MockResponse().setBody(Reader.fromFile(REMOTE_ERROR))
        }
    }
}