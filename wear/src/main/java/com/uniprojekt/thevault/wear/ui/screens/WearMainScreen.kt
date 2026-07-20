// PROMPT-REFERENZ: [REF-FEATURE-WEAR-OS-COMPANION]
package com.uniprojekt.thevault.wear.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Watch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.*
import com.google.android.gms.wearable.Wearable
import com.uniprojekt.thevault.wear.BuildConfig
import com.uniprojekt.thevault.wear.WearGameStateProvider
import com.uniprojekt.thevault.wear.ui.theme.AlertRed
import com.uniprojekt.thevault.wear.ui.theme.CyberBlack
import com.uniprojekt.thevault.wear.ui.theme.NeonGreen
import com.uniprojekt.thevault.wear.ui.theme.TextGreen

/**
 * Hauptbildschirm für die Smartwatch-Erweiterung.
 * // AI-Generated: Wear OS Companion App & Wrist Debug Terminal
 */
@Composable
fun WearMainScreen() {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Scaffold(
        modifier = Modifier.background(CyberBlack)
    ) {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> StatusPage()
                1 -> DebugTerminalPage()
            }
        }
    }
}

/**
 * Hauptansicht mit Timer und Missions-Status.
 * Zentriert das Hintergrund-Logo und zeigt Infos nur bei aktivem Spiel.
 */
@Composable
fun StatusPage() {
    val timer by WearGameStateProvider.timer.collectAsState()
    val status by WearGameStateProvider.status.collectAsState()
    val mistakes by WearGameStateProvider.mistakes.collectAsState()
    val isGameActive by WearGameStateProvider.isGameActive.collectAsState()

    // AI-Generated: Dezent blinkender Effekt für das Vault-Logo
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // AI-Generated: Fix Background Logo Rendering (Zentrierung & Skalierung)
        Text(
            text = "VAULT",
            color = NeonGreen,
            modifier = Modifier
                .alpha(alpha)
                .fillMaxWidth(),
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isGameActive) {
                // MISSION AKTIV: Zeige Timer und Stats
                Text(
                    text = "TIME: $timer",
                    color = NeonGreen,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "STATUS: $status",
                    color = TextGreen,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "MISTAKES: $mistakes",
                    color = if (mistakes.startsWith("0")) TextGreen else AlertRed,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            } else {
                // STANDBY: Verbindung bestätigen
                Icon(
                    imageVector = Icons.Default.Watch,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "UPLINK: ONLINE",
                    color = NeonGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "AWAITING MISSION START",
                    color = TextGreen.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Debug-Terminal für Entwickler (Swipe rechts).
 */
@Composable
fun DebugTerminalPage() {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(CyberBlack),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DEBUG TERMINAL",
            color = NeonGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (BuildConfig.DEBUG) {
            Button(
                onClick = { sendDebugSignal(context, "BYPASS_NODE") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
            ) {
                Text("BYPASS NODE", fontSize = 10.sp, color = NeonGreen)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { sendDebugSignal(context, "ALARM_TEST") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
            ) {
                Text("ALARM TEST", fontSize = 10.sp, color = AlertRed)
            }
        } else {
            Text(
                text = "ACCESS DENIED\nENCRYPTED",
                color = AlertRed,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Sendet ein Signal via MessageClient an das Smartphone.
 */
private fun sendDebugSignal(context: android.content.Context, command: String) {
    val messageClient = Wearable.getMessageClient(context)
    Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->
        for (node in nodes) {
            messageClient.sendMessage(node.id, "/debug_command", command.toByteArray())
        }
    }
}
