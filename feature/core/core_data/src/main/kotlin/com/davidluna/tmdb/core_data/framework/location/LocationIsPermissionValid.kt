package com.davidluna.tmdb.core_data.framework.location

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.davidluna.tmdb.core_domain.usecases.IsPermissionValid

class LocationIsPermissionValid(private val application: Application) : IsPermissionValid {

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