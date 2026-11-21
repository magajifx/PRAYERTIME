package com.prayertime.app

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

private const val CHANNEL_ID = "prayer_times"

fun createNotificationChannel(ctx: Context) {
    val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        CHANNEL_ID, "Prayer times", NotificationManager.IMPORTANCE_HIGH
    )
    channel.description = "Alerts when it is time for each prayer"
    nm.createNotificationChannel(channel)
}

object PrayerScheduler {

    private val reminderPrayers = listOf(
        Prayer.FAJR, Prayer.DHUHR, Prayer.ASR, Prayer.MAGHRIB, Prayer.ISHA
    )

    /** The next prayer (not sunrise) after [after], as (prayer, time in millis). */
    fun nextPrayer(place: Place, method: Int, hanafi: Boolean, after: Long): Pair<Prayer, Long>? {
        for (offset in 0..2) {
            val times = PrayerCalculator.forPlace(place, method, hanafi, offset)
            for (p in reminderPrayers) {
                val t = times[p] ?: continue
                if (t > after) return Pair(p, t)
            }
        }
        return null
    }

    private fun pendingIntent(ctx: Context, prayer: String?, time: Long): PendingIntent {
        val intent = Intent(ctx, AlarmReceiver::class.java)
        if (prayer != null) {
            intent.putExtra("prayer", prayer)
            intent.putExtra("time", time)
        }
        return PendingIntent.getBroadcast(
            ctx, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Sets an alarm for the next prayer. Called on app start, after each alarm, and after reboot. */
    fun scheduleNext(ctx: Context, after: Long = System.currentTimeMillis()) {
        val prefs = Prefs(ctx)
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!prefs.notify) {
            am.cancel(pendingIntent(ctx, null, 0L))
            return
        }
        val next = nextPrayer(prefs.place(), prefs.method, prefs.hanafi, after) ?: return
        val pi = pendingIntent(ctx, next.first.label, next.second)
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
        try {
            if (canExact) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.second, pi)
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.second, pi)
            }
        } catch (e: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.second, pi)
        }
    }

    fun canScheduleExact(ctx: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return am.canScheduleExactAlarms()
    }
}

/** Runs when a prayer alarm goes off: shows the notification, then sets the next alarm. */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayer = intent.getStringExtra("prayer") ?: return
        val time = intent.getLongExtra("time", System.currentTimeMillis())
        val prefs = Prefs(context)

        val allowed = Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

        if (prefs.notify && allowed) {
            createNotificationChannel(context)
            val open = PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val timeText = fmt("hh:mm a", prefs.place().tz).format(java.util.Date(time))
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("Time for $prayer")
                .setContentText("It is $timeText - time for $prayer prayer (${prefs.place().name})")
                .setContentIntent(open)
                .setAutoCancel(true)
                .build()
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(prayer.hashCode(), notification)
        }
        // schedule the following prayer (start looking one minute after this one)
        PrayerScheduler.scheduleNext(context, time + 60000L)
    }
}

/** Re-sets the prayer alarm after the phone restarts. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            PrayerScheduler.scheduleNext(context)
        }
    }
}
