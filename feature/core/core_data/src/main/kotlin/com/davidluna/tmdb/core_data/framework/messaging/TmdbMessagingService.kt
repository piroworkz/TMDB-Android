package com.davidluna.tmdb.core_data.framework.messaging

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class TmdbMessagingService : FirebaseMessagingService()