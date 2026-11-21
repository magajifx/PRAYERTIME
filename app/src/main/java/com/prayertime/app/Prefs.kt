package com.prayertime.app

import android.content.Context
import java.util.TimeZone

/** Saves the user's settings on the phone (location, calculation method, notifications). */
class Prefs(context: Context) {
    private val sp = context.applicationContext
        .getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)

    var auto: Boolean
        get() = sp.getBoolean("auto", true)
        set(v) = sp.edit().putBoolean("auto", v).apply()

    var method: Int
        get() = sp.getInt("method", 0)
        set(v) = sp.edit().putInt("method", v).apply()

    /** true = Hanafi Asr (shadow twice its length), false = standard Asr. */
    var hanafi: Boolean
        get() = sp.getBoolean("hanafi", false)
        set(v) = sp.edit().putBoolean("hanafi", v).apply()

    var notify: Boolean
        get() = sp.getBoolean("notify", true)
        set(v) = sp.edit().putBoolean("notify", v).apply()

    fun place(): Place {
        val name = sp.getString("p_name", null)
        val lat = sp.getString("p_lat", null)?.toDoubleOrNull()
        val lng = sp.getString("p_lng", null)?.toDoubleOrNull()
        val tz = sp.getString("p_tz", null)
        return if (name != null && lat != null && lng != null && tz != null) {
            Place(name, lat, lng, tz)
        } else {
            Cities.all.first { it.name == "Kano" }
        }
    }

    fun setPlace(p: Place) {
        sp.edit()
            .putString("p_name", p.name)
            .putString("p_lat", p.lat.toString())
            .putString("p_lng", p.lng.toString())
            .putString("p_tz", p.tz)
            .apply()
    }

    companion object {
        fun deviceTz(): String = TimeZone.getDefault().id
    }
}
