// PROMPT-REFERENZ: [REF-ISSUE27-NOTIFICATION-OVERLOAD]
// PROMPT-REFERENZ: [REF-ISSUE-SYNC-ANALYSIS-AND-FIX]
// PROMPT-REFERENZ: [REF-ISSUE47-MINIGAME-BUGS-FIX]
package com.uniprojekt.thevault.ui.screens.minigames

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.uniprojekt.thevault.ui.theme.CyberBackground
import com.uniprojekt.thevault.ui.theme.DarkGreen
import com.uniprojekt.thevault.ui.theme.NeonGreen
import com.uniprojekt.thevault.ui.theme.TextGreen
import com.uniprojekt.thevault.ui.theme.WaitingForTeamOverlay
import com.uniprojekt.thevault.ui.theme.crtOverlay
import com.uniprojekt.thevault.ui.theme.neonGlow
import kotlinx.coroutines.delay
import kotlin.random.Random

// AI-Generated: Immersive Android System Notification Overload Game - V2 Multi-User & Speed Scaling

private const val ACTION_GOLDEN_KEY = "com.uniprojekt.thevault.GOLDEN_KEY"
private const val ACTION_KEY_DISMISSED = "com.uniprojekt.thevault.KEY_DISMISSED"
private const val CHANNEL_ID = "vault_security_channel"
private const val NOTIF_TAG_PREFIX = "VAULT_OVERLOAD_"

@Composable
fun NotificationOverloadScreen(
    role: String?,
    content: String?,
    isCompleted: Boolean = false,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    onMistake: () -> Unit,
    onReady: () -> Unit = {},
    isGameActive: Boolean = true
) {
    val context = LocalContext.current
    val notificationManager = remember { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    var hasPermission by remember {
        mutableStateOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        })
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasPermission = isGranted }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onReady()
        }
        createNotificationChannel(context)
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) onReady()
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (isCompleted || !isGameActive) return
                when (intent?.action) {
                    ACTION_GOLDEN_KEY -> onSuccess()
                    ACTION_KEY_DISMISSED -> {
                        // AI-Generated: Game fails if the Golden Key is swiped away
                        onFail()
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(ACTION_GOLDEN_KEY)
            addAction(ACTION_KEY_DISMISSED)
        }

        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)

        onDispose {
            context.unregisterReceiver(receiver)
            notificationManager.cancelAll()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .crtOverlay(),
        contentAlignment = Alignment.Center
    ) {
        if (!hasPermission) {
            // Permission Gate
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOTIFICATIONS REQUIRED", color = NeonGreen, fontFamily = FontFamily.Monospace)
                androidx.compose.material3.Button(onClick = { 
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) {
                    Text("GRANT PERMISSION")
                }
            }
        } else if (!isGameActive) {
            // AI-Generated: Globales Spiel-Start-Gate (Warten auf alle Spieler)
            WaitingForTeamOverlay()
        } else if (role == "NEURAL_RELAY" && content != null) {
            val parts = content.split("|")
            if (parts.size >= 3) {
                NeuralRelayUI(
                    hasPermission = hasPermission,
                    notificationManager = notificationManager,
                    myKeyToFind = parts[0],
                    agentToHelp = parts[1],
                    keyToTellAgent = parts[2],
                    isCompleted = isCompleted,
                    onFail = onFail,
                    onSuccess = onSuccess
                )
            }
        } else if (role == "TARGET") {
            // Legacy/Debug Mode
            TargetNodeUI(hasPermission, notificationManager, content ?: "", isCompleted, onFail, onSuccess)
        } else {
            AnalystUI(content ?: "SCANNING FOR UPLINK...", isCompleted)
        }
    }
}

@Composable
private fun NeuralRelayUI(
    hasPermission: Boolean,
    notificationManager: NotificationManager,
    myKeyToFind: String,
    agentToHelp: String,
    keyToTellAgent: String,
    isCompleted: Boolean,
    onFail: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var activeNotifs by remember { mutableIntStateOf(0) }
    var speedFactor by remember { mutableStateOf(1.0f) }

    // AI-Generated: Fix minigame lifecycle permissions, notification overload mechanics, and decibel bypass exploit
    // 30s Mission Timer
    LaunchedEffect(isCompleted) {
        if (isCompleted) return@LaunchedEffect
        delay(30000)
        if (!isCompleted) {
            onSuccess() // Erfolgreich überlebt
        }
    }

    // Spam Generator mit Speed Scaling
    LaunchedEffect(hasPermission, isCompleted) {
        if (!hasPermission || isCompleted) return@LaunchedEffect

        while (!isCompleted) {
            val currentNotifs = notificationManager.activeNotifications
                .count { it.tag?.startsWith(NOTIF_TAG_PREFIX) == true }
            activeNotifs = currentNotifs

            // AI-Generated: Trigger failure if buffer overflows
            if (currentNotifs >= 10) {
                onFail()
                break
            }

            val isGolden = Random.nextInt(100) < 15
            val id = Random.nextInt(1000, 9999)
            val tag = if (isGolden) "${NOTIF_TAG_PREFIX}GOLDEN" else "${NOTIF_TAG_PREFIX}$id"

            // AI-Generated: Anonymisierte Benachrichtigungstitel
            val title = "VAULT_SECURITY_ALERT"
            val text = if (isGolden) "intercept_id: #$myKeyToFind" else "buffer_overflow_id: #$id"

            sendNotification(context, notificationManager, tag, title, text, isGolden)

            // Geschwindigkeit erhöht sich langsam (Delay wird kleiner)
            val delayMs = (Random.nextLong(1800, 2800) * speedFactor).toLong()
            delay(delayMs)
            speedFactor = (speedFactor * 0.97f).coerceAtLeast(0.4f)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Text(
            text = "!!! NEURAL RELAY !!!",
            color = NeonGreen,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.neonGlow()
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Info für den Partner
        Box(modifier = Modifier.border(1.dp, DarkGreen).padding(16.dp).fillMaxWidth()) {
            Column {
                Text(text = "PARTNER DATA UPLINK:", color = TextGreen, fontSize = 12.sp)
                Text(
                    text = "AGENT $agentToHelp REQUIRES CODE:",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                Text(
                    text = "#$keyToTellAgent",
                    color = NeonGreen,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.neonGlow()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "YOUR BUFFER LOAD:", color = TextGreen, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress Bar
        Box(modifier = Modifier.fillMaxWidth().height(20.dp).border(1.dp, DarkGreen)) {
            Box(modifier = Modifier.fillMaxWidth(activeNotifs / 10f).fillMaxHeight().background(if (activeNotifs > 7) Color.Red else NeonGreen))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "LISTEN TO YOUR TEAM!\nFIND YOUR ID IN THE\nNOTIFICATION BAR.",
            color = TextGreen,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TargetNodeUI(
    hasPermission: Boolean,
    notificationManager: NotificationManager,
    goldenKey: String,
    isCompleted: Boolean,
    onFail: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var activeNotifs by remember { mutableIntStateOf(0) }
    var speedFactor by remember { mutableStateOf(1.0f) }

    LaunchedEffect(isCompleted) {
        if (isCompleted) return@LaunchedEffect
        delay(30000)
        if (!isCompleted) onSuccess()
    }

    LaunchedEffect(hasPermission, isCompleted) {
        if (!hasPermission || isCompleted) return@LaunchedEffect
        while (!isCompleted) {
            val currentNotifs = notificationManager.activeNotifications
                .count { it.tag?.startsWith(NOTIF_TAG_PREFIX) == true }
            activeNotifs = currentNotifs
            if (currentNotifs >= 10) { onFail(); break }
            val isGolden = Random.nextInt(100) < 20
            val id = Random.nextInt(1000, 9999)
            val tag = if (isGolden) "${NOTIF_TAG_PREFIX}GOLDEN" else "${NOTIF_TAG_PREFIX}$id"
            
            val title = "VAULT_SECURITY_ALERT"
            val text = if (isGolden) "intercept_id: #$goldenKey" else "buffer_overflow_id: #$id"
            
            sendNotification(context, notificationManager, tag, title, text, isGolden)
            delay((Random.nextLong(1500, 2500) * speedFactor).toLong())
            speedFactor = (speedFactor * 0.96f).coerceAtLeast(0.4f)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("NEURAL RELAY", color = Color.Red, fontSize = 32.sp, modifier = Modifier.neonGlow(Color.Red))
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth(0.8f).height(10.dp).border(1.dp, DarkGreen)) {
            Box(modifier = Modifier.fillMaxWidth(activeNotifs / 10f).fillMaxHeight().background(Color.Red))
        }
    }
}

@Composable
private fun AnalystUI(info: String, isCompleted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Text("> TERMINAL", color = NeonGreen, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = info, color = NeonGreen, fontSize = 24.sp, modifier = Modifier.neonGlow())
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(CHANNEL_ID, "Vault Security", NotificationManager.IMPORTANCE_HIGH).apply {
            enableVibration(true)
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }
}

private fun sendNotification(context: Context, notificationManager: NotificationManager, tag: String, title: String, text: String, isGolden: Boolean) {
    val clickIntent = Intent(ACTION_GOLDEN_KEY).apply { `package` = context.packageName }
    val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    val deleteIntent = Intent(ACTION_KEY_DISMISSED).apply { `package` = context.packageName }
    val deletePendingIntent = PendingIntent.getBroadcast(context, 1, deleteIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.stat_sys_warning)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    if (isGolden) {
        builder.setContentIntent(clickPendingIntent)
        builder.setDeleteIntent(deletePendingIntent)
    }
    notificationManager.notify(tag, 0, builder.build())
}
