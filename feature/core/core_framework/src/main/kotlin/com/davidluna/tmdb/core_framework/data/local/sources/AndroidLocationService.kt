package com.davidluna.tmdb.core_framework.data.local.sources

import android.annotation.SuppressLint
import com.davidluna.tmdb.core_domain.usecases.IsPermissionValid
import com.davidluna.tmdb.core_framework.data.local.model.Coordinates
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AndroidLocationService @Inject constructor(
    private val client: FusedLocationProviderClient,
    private val permissionsGranted: IsPermissionValid
) : LocationService {

    @SuppressLint("MissingPermission")
    override suspend operator fun invoke(): Coordinates? = suspendCancellableCoroutine { cont ->
        if (!permissionsGranted()) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        } else {
            client.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    cont.resume(Coordinates(task.result.latitude, task.result.longitude))
                } else {
                    cont.resume(null)
                }
            }
        }
    }
}