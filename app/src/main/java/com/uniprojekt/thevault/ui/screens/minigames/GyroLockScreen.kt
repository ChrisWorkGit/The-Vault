package com.uniprojekt.thevault.ui.screens.minigames

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uniprojekt.thevault.ui.theme.DarkGreen
import com.uniprojekt.thevault.ui.theme.HousingColor
import com.uniprojekt.thevault.ui.theme.LockedColor
import com.uniprojekt.thevault.ui.theme.NeonGreen
import com.uniprojekt.thevault.ui.theme.NeutralColor
import com.uniprojekt.thevault.ui.theme.TextGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Minispiel "GyroLock".
 *
 * Nachbau eines physischen Tresorschlosses. Der Spieler dreht das Handy nach
 * links/rechts wodurch sich das Schloss synchron mitdreht.
 */

private const val DIAL_NUMBERS = 40
private const val DEG_PER_NUMBER = 360f / DIAL_NUMBERS
private const val TARGET_COUNT = 3
private const val HOLD_TO_LOCK_MS = 550L
private const val HOLD_POLL_MS = 50L
private const val GYRO_DEADZONE = 0.015f

@Composable
fun GyroLockScreen(
    onComplete: () -> Unit,
    onFail: () -> Unit,
    onReady: () -> Unit = {},
    isGameActive: Boolean = true
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val gyroSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }
    val hasSensor = gyroSensor != null

    val gameActiveState by rememberUpdatedState(isGameActive)

    val targets = remember { (0 until DIAL_NUMBERS).shuffled().take(TARGET_COUNT) }

    var dialAngleDeg by remember { mutableFloatStateOf(0f) }
    var activeTargetIndex by remember { mutableIntStateOf(0) }
    var lockedCount by remember { mutableIntStateOf(0) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var isSolved by remember { mutableStateOf(false) }

    fun numberAt(angle: Float): Int {
        val n = (angle / DEG_PER_NUMBER).roundToInt()
        return ((n % DIAL_NUMBERS) + DIAL_NUMBERS) % DIAL_NUMBERS
    }

    val currentNumber = numberAt(dialAngleDeg)

    DisposableEffect(hasSensor) {
        onReady()

        if (!hasSensor) return@DisposableEffect onDispose { }

        var lastTimestamp = 0L
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isSolved || !gameActiveState) {
                    lastTimestamp = 0L
                    return
                }

                if (lastTimestamp == 0L) {
                    lastTimestamp = event.timestamp
                    return
                }

                val dt = (event.timestamp - lastTimestamp) * 1e-9f   // ns zu s
                lastTimestamp = event.timestamp

                var omegaZ =
                    event.values[2]             // z Achse
                if (abs(omegaZ) < GYRO_DEADZONE) omegaZ = 0f

                dialAngleDeg -= Math.toDegrees((omegaZ * dt).toDouble()).toFloat()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }


    LaunchedEffect(activeTargetIndex, isSolved, isGameActive) {
        if (isSolved || !isGameActive) return@LaunchedEffect

        holdProgress = 0f
        var elapsed = 0L

        while (isActive) {
            val target = targets[activeTargetIndex]
            if (numberAt(dialAngleDeg) == target) {
                elapsed += HOLD_POLL_MS
                holdProgress = (elapsed.toFloat() / HOLD_TO_LOCK_MS).coerceAtMost(1f)

                if (elapsed >= HOLD_TO_LOCK_MS) {
                    lockedCount = activeTargetIndex + 1
                    holdProgress = 0f
                    if (activeTargetIndex >= targets.lastIndex) {
                        isSolved = true
                    } else {
                        activeTargetIndex += 1
                    }
                    break
                }
            } else {
                elapsed = 0L
                holdProgress = 0f
            }
            delay(HOLD_POLL_MS)
        }
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            delay(800)
            onComplete()
        }
    }

    val numberPaint = remember {
        Paint().apply {
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tresor knacken",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = NeonGreen
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Handy nach links/rechts drehen",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = TextGreen
        )
        Spacer(modifier = Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = size.minDimension / 2f * 0.9f
            val numberRadius = radius * 0.78f
            val textSize = radius * 0.13f
            numberPaint.textSize = textSize

            // Gehäuse
            drawCircle(color = HousingColor, radius = radius, center = Offset(cx, cy))
            drawCircle(
                color = NeonGreen.copy(alpha = 0.55f),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = DarkGreen,
                radius = radius * 0.30f,
                center = Offset(cx, cy)
            )

            for (k in 0 until DIAL_NUMBERS) {       // Ziffernkranz + Striche
                val displayDeg = k * DEG_PER_NUMBER - dialAngleDeg
                val rad = Math.toRadians(displayDeg.toDouble())
                val sinV = sin(rad).toFloat()
                val cosV = cos(rad).toFloat()

                val major = k % 5 == 0
                val tickOuter = radius
                val tickInner = radius - (if (major) radius * 0.10f else radius * 0.055f)
                drawLine(
                    color = NeonGreen.copy(alpha = if (major) 0.7f else 0.35f),
                    start = Offset(cx + tickOuter * sinV, cy - tickOuter * cosV),
                    end = Offset(cx + tickInner * sinV, cy - tickInner * cosV),
                    strokeWidth = if (major) 3f else 1.5f
                )

                if (major) {
                    val nx = cx + numberRadius * sinV
                    val ny = cy - numberRadius * cosV
                    val lockedIdx = targets.indexOf(k)
                    val col = when {
                        lockedIdx in 0 until lockedCount -> LockedColor       // bereits eingerastet
                        !isSolved && k == targets[activeTargetIndex] -> NeonGreen   // aktuelles Ziel
                        else -> NeutralColor
                    }
                    numberPaint.color = col.toArgb()
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawText(
                            k.toString(),
                            nx,
                            ny + textSize / 3f,
                            numberPaint
                        )
                    }
                }
            }

            val pointer = Path().apply {        // Zeiger
                moveTo(cx - radius * 0.07f, 2f)
                lineTo(cx + radius * 0.07f, 2f)
                lineTo(cx, radius * 0.15f)
                close()
            }
            drawPath(pointer, color = NeonGreen)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = currentNumber.toString().padStart(2, '0'),
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            color = NeonGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            targets.forEachIndexed { i, t ->
                val locked = i < lockedCount
                val active = i == activeTargetIndex && !isSolved
                Text(
                    text = t.toString().padStart(2, '0'),
                    fontSize = 20.sp,
                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                    color = when {
                        locked -> LockedColor
                        active -> NeonGreen
                        else -> NeutralColor
                    }
                )
                if (i < targets.lastIndex) {
                    Text(
                        text = "  ->  ",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NeutralColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { holdProgress },
            color = NeonGreen,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .alpha(if (holdProgress > 0f) 1f else 0f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Position halten...",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = TextGreen,
            modifier = Modifier.alpha(if (holdProgress > 0f) 1f else 0f)
        )

        if (!hasSensor) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Kein Gyroskop gefunden.",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = NeutralColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = { if (!isSolved && isGameActive) dialAngleDeg -= DEG_PER_NUMBER }) {
                    Text(text = "< Links")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { if (!isSolved && isGameActive) dialAngleDeg += DEG_PER_NUMBER }) {
                    Text(text = "Rechts >")
                }
            }
        }
    }
}