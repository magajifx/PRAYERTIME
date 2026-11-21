package com.prayertime.app

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Place(val name: String, val lat: Double, val lng: Double, val tz: String)

object Cities {
    private const val NG = "Africa/Lagos"

    val all: List<Place> = listOf(
        Place("Abuja", 9.0765, 7.3986, NG),
        Place("Azare", 11.6740, 10.1940, NG),
        Place("Bauchi", 10.3158, 9.8442, NG),
        Place("Benin City", 6.3350, 5.6037, NG),
        Place("Bida", 9.0833, 6.0167, NG),
        Place("Birnin Kebbi", 12.4539, 4.1975, NG),
        Place("Calabar", 4.9757, 8.3417, NG),
        Place("Dambatta", 12.4264, 8.5156, NG),
        Place("Damaturu", 11.7470, 11.9608, NG),
        Place("Dutse", 11.7594, 9.3389, NG),
        Place("Enugu", 6.4584, 7.5464, NG),
        Place("Gombe", 10.2897, 11.1673, NG),
        Place("Gusau", 12.1704, 6.6641, NG),
        Place("Hadejia", 12.4500, 10.0422, NG),
        Place("Ibadan", 7.3775, 3.9470, NG),
        Place("Ilorin", 8.4966, 4.5421, NG),
        Place("Jalingo", 8.8937, 11.3596, NG),
        Place("Jos", 9.8965, 8.8583, NG),
        Place("Kaduna", 10.5222, 7.4383, NG),
        Place("Kano", 12.0022, 8.5920, NG),
        Place("Katsina", 12.9908, 7.6018, NG),
        Place("Lafia", 8.4939, 8.5153, NG),
        Place("Lagos", 6.5244, 3.3792, NG),
        Place("Lokoja", 7.8023, 6.7333, NG),
        Place("Maiduguri", 11.8333, 13.1500, NG),
        Place("Makurdi", 7.7337, 8.5214, NG),
        Place("Minna", 9.6139, 6.5569, NG),
        Place("Nguru", 12.8750, 10.4500, NG),
        Place("Osogbo", 7.7827, 4.5418, NG),
        Place("Port Harcourt", 4.8156, 7.0498, NG),
        Place("Potiskum", 11.7139, 11.0786, NG),
        Place("Sokoto", 13.0059, 5.2476, NG),
        Place("Suleja", 9.1806, 7.1792, NG),
        Place("Yola", 9.2035, 12.4954, NG),
        Place("Zaria", 11.0855, 7.7199, NG),
        Place("Accra, Ghana", 5.6037, -0.1870, "Africa/Accra"),
        Place("Cairo, Egypt", 30.0444, 31.2357, "Africa/Cairo"),
        Place("London, UK", 51.5072, -0.1276, "Europe/London"),
        Place("Madinah, Saudi Arabia", 24.5247, 39.5692, "Asia/Riyadh"),
        Place("Makkah, Saudi Arabia", 21.3891, 39.8579, "Asia/Riyadh"),
        Place("N'Djamena, Chad", 12.1348, 15.0557, "Africa/Ndjamena"),
        Place("Niamey, Niger", 13.5116, 2.1254, "Africa/Niamey")
    )

    /** Distance in kilometres between two coordinates (haversine formula). */
    fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2) * sin(dLng / 2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun nearest(lat: Double, lng: Double): Place =
        all.minByOrNull { distanceKm(lat, lng, it.lat, it.lng) } ?: all[0]
}
