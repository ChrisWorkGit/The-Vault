package com.uniprojekt.thevault.ui.screens.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uniprojekt.thevault.ui.theme.NeonGreen
import com.uniprojekt.thevault.ui.theme.NeutralColor
import com.uniprojekt.thevault.ui.theme.TextGreen
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Minispiel "LaserBarrier".
 *
 * Der Spieler muss eine Laserschranke deaktivieren indem er den Lichtsensor des Handys
 * abdunkelt. Fällt der Lux-Wert für HOLD_TO_SOLVE_MS unter LUX_THRESHOLD, deaktivieren
 * sich die Laser und das Spiel gilt als gelöst.
 */

private const val LUX_THRESHOLD = 8f
private const val HOLD_TO_SOLVE_MS = 2000L
private const val POLL_MS = 50L
private const val LASER_COUNT = 5

@Composable
fun LaserBarrierScreen(
    onComplete: () -> Unit,
    onFail: () -> Unit,
    onReady: () -> Unit = {},
    isGameActive: Boolean = true
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val lightSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }
    val hasSensor = lightSensor != null

    var currentLux by remember { mutableFloatStateOf(-1f) }
    var simulateCovered by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var isSolved by remember { mutableStateOf(false) }

    val effectiveLux = if (simulateCovered) 0f else currentLux
    val isDark = effectiveLux in 0f..LUX_THRESHOLD

    DisposableEffect(hasSensor) {
        onReady()

        if (!hasSensor) return@DisposableEffect onDispose { }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                currentLux = event.values[0]
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    LaunchedEffect(isGameActive, isSolved) {
        if (isSolved || !isGameActive) {
            holdProgress = 0f
            return@LaunchedEffect
        }
        var elapsed = 0L
        while (true) {
            val darkNow = (if (simulateCovered) 0f else currentLux) in 0f..LUX_THRESHOLD
            if (darkNow) {
                elapsed += POLL_MS
                holdProgress = (elapsed.toFloat() / HOLD_TO_SOLVE_MS).coerceAtMost(1f)
                if (elapsed >= HOLD_TO_SOLVE_MS) {
                    isSolved = true
                    break
                }
            } else {
                elapsed = 0L
                holdProgress = 0f
            }
            delay(POLL_MS)
        }
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            delay(800)
            onComplete()
        }
    }

    val pulse by rememberInfiniteTransition(label = "laserPulse").animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Laserschranke umgehen",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = NeonGreen
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Hand über den Lichtsensor  legen und dunkel halten",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = TextGreen
        )

        if (!isGameActive) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "WARTE AUF TEAM...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = NeutralColor
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val w = size.width
            val h = size.height
            val margin = h * 0.08f
            val gap = (h - 2 * margin) / (LASER_COUNT - 1)

            for (i in 0 until LASER_COUNT) {
                val y = margin + i * gap
                drawCircle(color = NeutralColor, radius = 8f, center = Offset(6f, y))
                drawCircle(color = NeutralColor, radius = 8f, center = Offset(w - 6f, y))

                val beamColor = lerp(Color.Red, NeonGreen, holdProgress)
                val beamAlpha = if (isDark) (pulse * (1f - holdProgress)).coerceIn(0.15f, 1f) else pulse

                drawLine(
                    color = beamColor.copy(alpha = beamAlpha * 0.25f),
                    start = Offset(12f, y),
                    end = Offset(w - 12f, y),
                    strokeWidth = 14f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = beamColor.copy(alpha = beamAlpha),
                    start = Offset(12f, y),
                    end = Offset(w - 12f, y),
                    strokeWidth = 3.5f,
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                isSolved -> "LASER DEAKTIVIERT"
                currentLux < 0f && !simulateCovered -> "Sensor wird gelesen..."
                isDark -> "ABGEDUNKELT - halten!"
                else -> "Zu hell (${effectiveLux.roundToInt()} lx)"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (isDark || isSolved) NeonGreen else Color.Red
        )

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { holdProgress },
            color = NeonGreen,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${(holdProgress * 100).roundToInt()}% umgangen",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = TextGreen
        )

        if (!hasSensor) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Kein Lichtsensor gefunden.",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = NeutralColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { if (isGameActive && !isSolved) simulateCovered = !simulateCovered }) {
                Text(text = if (simulateCovered) "Sensor freigeben" else "Sensor abdecken")
            }
        }
    }
}