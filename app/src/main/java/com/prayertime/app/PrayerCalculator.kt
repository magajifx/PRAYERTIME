package com.prayertime.app

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.tan

enum class Prayer(val label: String) {
    FAJR("Fajr"),
    SUNRISE("Sunrise"),
    DHUHR("Dhuhr"),
    ASR("Asr"),
    MAGHRIB("Maghrib"),
    ISHA("Isha")
}

/** Creates a date formatter that shows time in a given time zone. */
fun fmt(pattern: String, tzId: String): SimpleDateFormat =
    SimpleDateFormat(pattern, Locale.US).apply { timeZone = TimeZone.getTimeZone(tzId) }

/**
 * Calculates the five daily prayer times from astronomical formulas
 * (position of the sun for a given date, latitude and longitude).
 * Everything is calculated on the phone, so no internet is needed.
 */
object PrayerCalculator {

    val methodNames = listOf(
        "Muslim World League",
        "Egyptian General Authority of Survey",
        "University of Islamic Sciences, Karachi",
        "Umm al-Qura University, Makkah",
        "ISNA (North America)"
    )

    // Sun angle below the horizon for Fajr and Isha, for each method above
    private val fajrAngles = doubleArrayOf(18.0, 19.5, 18.0, 18.5, 15.0)
    private val ishaAngles = doubleArrayOf(17.0, 17.5, 18.0, 0.0, 15.0)
    // Umm al-Qura: Isha is 90 minutes after Maghrib
    private val ishaMinutes = doubleArrayOf(0.0, 0.0, 0.0, 90.0, 0.0)

    /** Prayer times (epoch milliseconds) for today + [dayOffset] days at the given place. */
    fun forPlace(place: Place, method: Int, hanafi: Boolean, dayOffset: Int): Map<Prayer, Long> {
        val tz = TimeZone.getTimeZone(place.tz)
        val cal = Calendar.getInstance(tz)
        cal.add(Calendar.DAY_OF_YEAR, dayOffset)
        return compute(
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
            place.lat, place.lng, place.tz, method, hanafi
        )
    }

    fun compute(
        year: Int, month: Int, day: Int,
        lat: Double, lng: Double, tzId: String,
        method: Int, hanafi: Boolean
    ): Map<Prayer, Long> {
        val tz = TimeZone.getTimeZone(tzId)
        val cal = Calendar.getInstance(tz)
        cal.clear()
        cal.set(year, month - 1, day, 12, 0, 0)
        val tzHours = tz.getOffset(cal.timeInMillis) / 3600000.0
        cal.set(year, month - 1, day, 0, 0, 0)
        val midnight = cal.timeInMillis

        val m = method.coerceIn(0, 4)
        val jd = julian(year, month, day) - lng / (15.0 * 24.0)
        val factor = if (hanafi) 2.0 else 1.0

        fun midDay(t: Double): Double = fixHour(12.0 - sunPosition(jd + t).second)

        fun angleTime(angle: Double, t: Double, ccw: Boolean): Double {
            val decl = sunPosition(jd + t).first
            val a = (-dsin(angle) - dsin(decl) * dsin(lat)) / (dcos(decl) * dcos(lat))
            val hours = darccos(a) / 15.0
            return midDay(t) + if (ccw) -hours else hours
        }

        fun asrTime(f: Double, t: Double): Double {
            val decl = sunPosition(jd + t).first
            val angle = -darccot(f + dtan(abs(lat - decl)))
            return angleTime(angle, t, false)
        }

        // Starting guesses (hours): fajr, sunrise, dhuhr, asr, sunset, isha
        var h = doubleArrayOf(5.0, 6.0, 12.0, 13.0, 18.0, 18.0)
        repeat(2) {
            val p = DoubleArray(6) { i -> h[i] / 24.0 }
            h = doubleArrayOf(
                angleTime(fajrAngles[m], p[0], true),
                angleTime(0.833, p[1], true),
                midDay(p[2]),
                asrTime(factor, p[3]),
                angleTime(0.833, p[4], false),
                angleTime(ishaAngles[m], p[5], false)
            )
        }

        val shift = tzHours - lng / 15.0
        val fajr = h[0] + shift
        val sunrise = h[1] + shift
        val dhuhr = h[2] + shift
        val asr = h[3] + shift
        val maghrib = h[4] + shift
        val isha = if (ishaMinutes[m] > 0) maghrib + ishaMinutes[m] / 60.0 else h[5] + shift

        fun toMillis(hours: Double): Long = midnight + (hours * 60.0).roundToLong() * 60000L

        return mapOf(
            Prayer.FAJR to toMillis(fajr),
            Prayer.SUNRISE to toMillis(sunrise),
            Prayer.DHUHR to toMillis(dhuhr),
            Prayer.ASR to toMillis(asr),
            Prayer.MAGHRIB to toMillis(maghrib),
            Prayer.ISHA to toMillis(isha)
        )
    }

    // ---------- astronomy helpers (angles in degrees) ----------

    private fun dsin(d: Double) = sin(Math.toRadians(d))
    private fun dcos(d: Double) = cos(Math.toRadians(d))
    private fun dtan(d: Double) = tan(Math.toRadians(d))
    private fun darcsin(x: Double) = Math.toDegrees(asin(x))
    private fun darccos(x: Double) = Math.toDegrees(acos(x.coerceIn(-1.0, 1.0)))
    private fun darctan2(y: Double, x: Double) = Math.toDegrees(atan2(y, x))
    private fun darccot(x: Double) = Math.toDegrees(atan(1.0 / x))
    private fun fixAngle(a: Double) = ((a % 360.0) + 360.0) % 360.0
    private fun fixHour(a: Double) = ((a % 24.0) + 24.0) % 24.0

    private fun julian(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /** Returns (declination of the sun, equation of time in hours). */
    private fun sunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * dsin(g) + 0.020 * dsin(2 * g))
        val e = 23.439 - 0.00000036 * d
        val ra = fixHour(darctan2(dcos(e) * dsin(l), dcos(l)) / 15.0)
        val eqt = q / 15.0 - ra
        val decl = darcsin(dsin(e) * dsin(l))
        return Pair(decl, eqt)
    }
}
