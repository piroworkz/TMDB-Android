package com.davidluna.tmdb.core_framework.data.local.sources

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.davidluna.tmdb.core_domain.usecases.PermissionValidator
import javax.inject.Inject

class LocationPermissionValidator @Inject constructor(private val application: Application) :
    PermissionValidator {

    override fun invoke(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            application, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            application, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }
}