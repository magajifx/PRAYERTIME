package com.prayertime.app

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings as AndroidSettings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Date

private val Green = Color(0xFF0B6E4F)
private val LightGreen = Color(0xFFD8F0E5)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Green,
            primaryContainer = LightGreen,
            onPrimaryContainer = Color(0xFF05392A),
            secondaryContainer = LightGreen,
            background = Color(0xFFF7FAF8),
            surface = Color.White
        ),
        content = content
    )
}

@Composable
fun PrayerApp() {
    val ctx = LocalContext.current
    val prefs = remember { Prefs(ctx) }
    var place by remember { mutableStateOf(prefs.place()) }
    var method by remember { mutableIntStateOf(prefs.method) }
    var hanafi by remember { mutableStateOf(prefs.hanafi) }
    var notify by remember { mutableStateOf(prefs.notify) }
    var tab by remember { mutableIntStateOf(0) }

    val choosePlace: (Place, Boolean) -> Unit = { p, isAuto ->
        prefs.auto = isAuto
        prefs.setPlace(p)
        place = p
        PrayerScheduler.scheduleNext(ctx)
    }

    val notifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { PrayerScheduler.scheduleNext(ctx) }

    LaunchedEffect(Unit) {
        // Ask for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= 33 &&
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        // In automatic mode, refresh the location from the last known GPS position
        if (prefs.auto && LocationHelper.hasPermission(ctx)) {
            LocationHelper.lastKnown(ctx)?.let { loc ->
                val near = Cities.nearest(loc.latitude, loc.longitude)
                choosePlace(
                    Place("My location (near ${near.name})", loc.latitude, loc.longitude, Prefs.deviceTz()),
                    true
                )
            }
        }
        PrayerScheduler.scheduleNext(ctx)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0, onClick = { tab = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = tab == 1, onClick = { tab = 1 },
                    icon = { Icon(Icons.Filled.Place, contentDescription = null) },
                    label = { Text("Location") }
                )
                NavigationBarItem(
                    selected = tab == 2, onClick = { tab = 2 },
                    icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                    label = { Text("Azkar") }
                )
                NavigationBarItem(
                    selected = tab == 3, onClick = { tab = 3 },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (tab) {
                0 -> HomeScreen(place, method, hanafi)
                1 -> LocationScreen(place, choosePlace)
                2 -> AzkarScreen()
                else -> SettingsScreen(
                    method = method,
                    hanafi = hanafi,
                    notify = notify,
                    onMethod = {
                        method = it; prefs.method = it
                        PrayerScheduler.scheduleNext(ctx)
                    },
                    onHanafi = {
                        hanafi = it; prefs.hanafi = it
                        PrayerScheduler.scheduleNext(ctx)
                    },
                    onNotify = {
                        notify = it; prefs.notify = it
                        PrayerScheduler.scheduleNext(ctx)
                    }
                )
            }
        }
    }
}

// ------------------------------------------------------------------ HOME

@Composable
fun HomeScreen(place: Place, method: Int, hanafi: Boolean) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }

    val dayKey = fmt("yyyyMMdd", place.tz).format(Date(now))
    val times = remember(place, method, hanafi, dayKey) {
        PrayerCalculator.forPlace(place, method, hanafi, 0)
    }
    val next = PrayerScheduler.nextPrayer(place, method, hanafi, now)
    val timeFormat = fmt("hh:mm a", place.tz)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(place.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Green)
        Text(fmt("EEEE, d MMMM yyyy", place.tz).format(Date(now)), fontSize = 14.sp)

        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Green)
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (next != null) {
                    val remain = ((next.second - now) / 1000).coerceAtLeast(0)
                    Text("Next prayer", color = Color.White, fontSize = 14.sp)
                    Text(next.first.label, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(timeFormat.format(Date(next.second)), color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        String.format("%02d:%02d:%02d", remain / 3600, (remain % 3600) / 60, remain % 60),
                        color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Medium
                    )
                    Text("remaining", color = Color.White, fontSize = 12.sp)
                } else {
                    Text("No prayer time available", color = Color.White)
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(vertical = 8.dp)) {
                for (prayer in Prayer.values()) {
                    val t = times[prayer] ?: continue
                    val isNext = next != null && next.first == prayer && next.second == t
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(if (isNext) LightGreen else Color.Transparent)
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            prayer.label, fontSize = 18.sp,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            timeFormat.format(Date(t)), fontSize = 18.sp,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
        Text(
            "Times are calculated on your phone using the selected method (see Settings).",
            fontSize = 12.sp
        )
    }
}

// ------------------------------------------------------------------ LOCATION

@Composable
fun LocationScreen(place: Place, onSelect: (Place, Boolean) -> Unit) {
    val ctx = LocalContext.current
    var status by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val detect: () -> Unit = {
        loading = true
        status = "Detecting your location..."
        LocationHelper.requestFresh(ctx) { loc ->
            loading = false
            if (loc == null) {
                status = "Could not get your location. Turn on Location (GPS) and try again, preferably outdoors."
            } else {
                val near = Cities.nearest(loc.latitude, loc.longitude)
                onSelect(
                    Place("My location (near ${near.name})", loc.latitude, loc.longitude, Prefs.deviceTz()),
                    true
                )
                status = "Location updated."
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) detect()
        else status = "Location permission was denied. You can still choose a city below."
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Current location", fontSize = 14.sp)
        Text(place.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Green)
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (LocationHelper.hasPermission(ctx)) detect()
                else permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Detect my location automatically (GPS)") }

        if (status.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(status, fontSize = 13.sp)
        }

        Spacer(Modifier.height(16.dp))
        Text("Or choose a city manually", fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search city") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))

        val cities = Cities.all.filter { it.name.contains(query.trim(), ignoreCase = true) }
        LazyColumn(Modifier.weight(1f)) {
            items(cities) { city ->
                val selected = city.name == place.name
                Text(
                    city.name,
                    fontSize = 17.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (selected) LightGreen else Color.Transparent)
                        .clickable {
                            onSelect(city, false)
                            status = "Location set to ${city.name}."
                        }
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                )
            }
        }
    }
}

// ------------------------------------------------------------------ AZKAR

@Composable
fun AzkarScreen() {
    var tab by remember { mutableIntStateOf(0) }
    val titles = listOf("Morning", "Evening", "After prayer")
    val list = when (tab) {
        0 -> AzkarData.morning
        1 -> AzkarData.evening
        else -> AzkarData.afterPrayer
    }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            titles.forEachIndexed { i, title ->
                Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title) })
            }
        }
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list) { dhikr ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                dhikr.title, fontWeight = FontWeight.Bold,
                                color = Green, modifier = Modifier.weight(1f)
                            )
                            Text(dhikr.repeat, fontSize = 13.sp, color = Green)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            dhikr.arabic,
                            fontSize = 24.sp,
                            lineHeight = 42.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        Text("Hausa:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(dhikr.hausa, fontSize = 15.sp, lineHeight = 22.sp)
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------ SETTINGS

@Composable
fun SettingsScreen(
    method: Int,
    hanafi: Boolean,
    notify: Boolean,
    onMethod: (Int) -> Unit,
    onHanafi: (Boolean) -> Unit,
    onNotify: (Boolean) -> Unit
) {
    val ctx = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Settings", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Green)

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Prayer notifications", fontSize = 17.sp)
            Switch(checked = notify, onCheckedChange = onNotify)
        }

        if (notify && !PrayerScheduler.canScheduleExact(ctx)) {
            Text(
                "For on-time alerts, allow \"Alarms & reminders\" for this app.",
                fontSize = 13.sp
            )
            Button(onClick = {
                try {
                    ctx.startActivity(
                        Intent(
                            AndroidSettings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse("package:" + ctx.packageName)
                        )
                    )
                } catch (e: Exception) {
                    // ignore if the settings screen is not available
                }
            }) { Text("Allow exact alarms") }
        }

        Spacer(Modifier.height(8.dp))
        Text("Calculation method", fontWeight = FontWeight.Bold)
        PrayerCalculator.methodNames.forEachIndexed { i, name ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onMethod(i) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = method == i, onClick = null)
                Text(name, modifier = Modifier.padding(start = 10.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Asr calculation", fontWeight = FontWeight.Bold)
        Row(
            Modifier.fillMaxWidth().clickable { onHanafi(false) }.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = !hanafi, onClick = null)
            Text("Standard (Shafi'i, Maliki, Hanbali)", modifier = Modifier.padding(start = 10.dp))
        }
        Row(
            Modifier.fillMaxWidth().clickable { onHanafi(true) }.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = hanafi, onClick = null)
            Text("Hanafi", modifier = Modifier.padding(start = 10.dp))
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "Prayer Time and Azkar App, version 1.0\nDesign and Implementation of an Android-Based Prayer Time Application.",
            fontSize = 12.sp
        )
    }
}
