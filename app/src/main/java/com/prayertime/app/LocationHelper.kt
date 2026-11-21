package com.prayertime.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper

/** Detects the phone's location using GPS / network (no Google services needed). */
object LocationHelper {

    fun hasPermission(ctx: Context): Boolean =
        ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ctx.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /** The most recent location the phone already knows (may be null). */
    @SuppressLint("MissingPermission")
    fun lastKnown(ctx: Context): Location? {
        if (!hasPermission(ctx)) return null
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var best: Location? = null
        for (provider in lm.getProviders(true)) {
            val loc = try {
                lm.getLastKnownLocation(provider)
            } catch (e: Exception) {
                null
            }
            if (loc != null && (best == null || loc.time > best.time)) best = loc
        }
        return best
    }

    /** Asks GPS/network for a fresh location. Calls [onResult] once (null if it fails). */
    @SuppressLint("MissingPermission")
    fun requestFresh(ctx: Context, onResult: (Location?) -> Unit) {
        if (!hasPermission(ctx)) {
            onResult(null)
            return
        }
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = lm.getProviders(true).filter {
            it == LocationManager.GPS_PROVIDER || it == LocationManager.NETWORK_PROVIDER
        }
        if (providers.isEmpty()) {
            onResult(lastKnown(ctx))
            return
        }

        val handler = Handler(Looper.getMainLooper())
        var finished = false

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (finished) return
                finished = true
                lm.removeUpdates(this)
                onResult(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            for (p in providers) {
                lm.requestLocationUpdates(p, 0L, 0f, listener, Looper.getMainLooper())
            }
        } catch (e: Exception) {
            finished = true
            onResult(lastKnown(ctx))
            return
        }

        // Give up after 20 seconds and use the last known location instead
        handler.postDelayed({
            if (!finished) {
                finished = true
                lm.removeUpdates(listener)
                onResult(lastKnown(ctx))
            }
        }, 20000L)
    }
}
