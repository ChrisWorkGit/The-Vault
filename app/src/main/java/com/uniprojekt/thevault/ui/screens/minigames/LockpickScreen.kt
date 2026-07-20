// PROMPT-REFERENZ: [REF-ISSUE13-LOCKPICK-DIETRICH-SHAPE]
// PROMPT-REFERENZ: [REF-ISSUE47-MINIGAME-BUGS-FIX]
package com.uniprojekt.thevault.ui.screens.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uniprojekt.thevault.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.random.Random

/**
 * Minispiel "Lockpicking".
 *
 * Jeder Spieler sieht die Zylinder eines Schlosses und muss mit seinem Dietrich versuchen die Zylinder einrasten zu lassen.
 * Das kann mit vor und hinterkippen des Geräts bewirkt werden.
 */

private const val PIN_COUNT = 5

private const val VIRTUAL_W = 560f
private const val VIRTUAL_H = 320f

private const val HOUSING_LEFT = 40f
private const val HOUSING_RIGHT = 520f
private const val HOUSING_TOP = 30f
private const val HOUSING_BOTTOM = 160f

private const val PLUG_LEFT = 22f
private const val PLUG_RIGHT = 538f
private const val PLUG_TOP = 150f
private const val PLUG_BOTTOM = 300f

private const val SHEAR_Y = 155f
private const val REST_TIP_Y = 280f
private const val PIN_WIDTH = 46f
private const val PIN_SLOT_W = (HOUSING_RIGHT - HOUSING_LEFT) / PIN_COUNT
private const val DRIVER_HEIGHT_PX = 55f

private const val TOLERANCE_PX = 6f
private const val MAX_OFFSET_PX = 100f
private const val HOLD_TO_LOCK_MS = 550L
private const val HOLD_POLL_MS = 50L
private const val LEVER_RANGE_DEG = 55f

private data class LockPin(
    val keyHeightPx: Float,
    val targetOffsetPx: Float,
    val isLocked: Boolean = false
)

private fun pinCenterX(index: Int): Float = HOUSING_LEFT + PIN_SLOT_W * index + PIN_SLOT_W / 2f

@Composable
fun LockpickScreen(
    onComplete: () -> Unit,
    onFail: () -> Unit,
    onReady: () -> Unit = {},
    isGameActive: Boolean = true
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val rotationSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }
    val hasSensor = rotationSensor != null

    val pins = remember {
        mutableStateListOf<LockPin>().apply {
            repeat(PIN_COUNT) {
                val key = 30f + Random.nextFloat() * 60f
                val target = (REST_TIP_Y - key) - SHEAR_Y
                add(LockPin(keyHeightPx = key, targetOffsetPx = target))
            }
        }
    }

    var activePinIndex by remember { mutableIntStateOf(PIN_COUNT - 1) }
    var leverDeg by remember { mutableFloatStateOf(0f) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var calibrated by remember { mutableStateOf(false) }
    var allLocked by remember { mutableStateOf(false) }

    fun currentOffsetPx(index: Int): Float {
        val pin = pins[index]
        return when {
            pin.isLocked -> pin.targetOffsetPx
            index == activePinIndex -> (leverDeg.coerceIn(0f, LEVER_RANGE_DEG) / LEVER_RANGE_DEG) * MAX_OFFSET_PX
            else -> 0f
        }
    }

    DisposableEffect(hasSensor) {
        if (!hasSensor) return@DisposableEffect onDispose { }

        // AI-Generated: Signalisiere Bereitschaft sofort beim Laden
        onReady()

        var pitch0 = 0f
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (allLocked) return

                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientation)

                val pitchDeg = Math.toDegrees(orientation[1].toDouble()).toFloat()

                if (!calibrated) {
                    // Aktuelle Neigung des Geräts wird als "Neutralstellung" definiert
                    pitch0 = pitchDeg
                    calibrated = true
                    return
                }

                val diff = pitchDeg - pitch0
                leverDeg = diff.coerceIn(-LEVER_RANGE_DEG * 1.3f, LEVER_RANGE_DEG * 1.3f)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    LaunchedEffect(activePinIndex, allLocked, isGameActive) {
        if (allLocked || !isGameActive) return@LaunchedEffect
        holdProgress = 0f
        var elapsed = 0L

        while (isActive) {
            val pin = pins[activePinIndex]
            if (pin.isLocked) {
                holdProgress = 0f
                break
            }

            val offset = currentOffsetPx(activePinIndex)
            if (abs(offset - pin.targetOffsetPx) <= TOLERANCE_PX) {
                elapsed += HOLD_POLL_MS
                holdProgress = (elapsed.toFloat() / HOLD_TO_LOCK_MS).coerceAtMost(1f)

                if (elapsed >= HOLD_TO_LOCK_MS) {
                    pins[activePinIndex] = pin.copy(isLocked = true)
                    holdProgress = 0f

                    if (activePinIndex == 0) {
                        allLocked = true
                    } else {
                        leverDeg = 0f
                        activePinIndex -= 1
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

    LaunchedEffect(allLocked) {
        if (allLocked) {
            delay(800)
            onComplete()
        }
    }

    // UI
    // AI-Generated: Globales Spiel-Start-Gate (Warten auf alle Spieler)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .crtOverlay()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Schloss knacken", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Handy vor/zurück kippen bis der Zylinder einrastet",
                fontSize = 12.sp,
                color = TextGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(220.dp)
            ) {
                scale(scaleX = size.width / VIRTUAL_W, scaleY = size.height / VIRTUAL_H, pivot = Offset.Zero) {
                    drawRoundRect(      // Gehäuse
                        color = HousingColor,
                        topLeft = Offset(HOUSING_LEFT, HOUSING_TOP),
                        size = Size(HOUSING_RIGHT - HOUSING_LEFT, HOUSING_BOTTOM - HOUSING_TOP),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(width = 2f)
                    )
                    drawRoundRect(      // Schließzylinder
                        color = HousingColor,
                        topLeft = Offset(PLUG_LEFT, PLUG_TOP),
                        size = Size(PLUG_RIGHT - PLUG_LEFT, PLUG_BOTTOM - PLUG_TOP),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(width = 2f)
                    )

                    for (i in 0 until pins.size) {
                        val pin = pins[i]
                        val offset = currentOffsetPx(i)
                        val total = pin.keyHeightPx + DRIVER_HEIGHT_PX
                        val top = REST_TIP_Y - total - offset
                        val cx = pinCenterX(i)
                        val left = cx - PIN_WIDTH / 2f

                        // Treiberstift (blau, oben) und Schlüsselstift (orange, unten)
                        drawRect(
                            color = DriverColor,
                            topLeft = Offset(left, top),
                            size = Size(PIN_WIDTH, DRIVER_HEIGHT_PX)
                        )
                        drawRect(
                            color = KeyColor,
                            topLeft = Offset(left, top + DRIVER_HEIGHT_PX),
                            size = Size(PIN_WIDTH, pin.keyHeightPx)
                        )

                        // grün = eingerastet, blau = aktiv, grau = noch offen
                        val borderColor = when {
                            pin.isLocked -> LockedColor
                            i == activePinIndex -> ActiveColor
                            else -> NeutralColor
                        }
                        drawRoundRect(
                            color = borderColor,
                            topLeft = Offset(left, top),
                            size = Size(PIN_WIDTH, total),
                            cornerRadius = CornerRadius(4f, 4f),
                            style = Stroke(width = if (i == activePinIndex && !pin.isLocked) 4f else 2.5f)
                        )
                    }

                    // AI-Generated: Dietrich-Haken-Form nach Referenzbild (langer Griff + abgewinkelte Spitze) statt gerader Linie - Kurve über quadraticTo() bildet den charakteristischen Knick nach.
                    val activeOffset = currentOffsetPx(activePinIndex)
                    val tipY = REST_TIP_Y - activeOffset + 3f
                    val ax = pinCenterX(activePinIndex)
                    val bendX = ax + 22f
                    val bendY = tipY + 26f
                    val handleX = VIRTUAL_W + 30f
                    val handleY = bendY + 4f
                    val pickPath = Path().apply {
                        moveTo(ax, tipY)
                        quadraticTo(bendX, bendY, handleX, handleY)
                    }
                    drawPath(
                        path = pickPath,
                        color = PickColor,
                        style = Stroke(width = 7f, cap = StrokeCap.Round)
                    )
                    drawCircle(color = PickColor, radius = 4.5f, center = Offset(ax, tipY))
                    // Ende AI-Generated-Block
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Zylinder ${PIN_COUNT - activePinIndex} / ${pins.size} · ${pins.count { it.isLocked }} eingerastet",
                fontSize = 13.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { holdProgress },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .alpha(if (holdProgress > 0f) 1f else 0f),
                color = NeonGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Position halten zum Einrasten...",
                fontSize = 12.sp,
                modifier = Modifier.alpha(if (holdProgress > 0f) 1f else 0f),
                color = NeonGreen
            )
        }

        if (!isGameActive) {
            WaitingForTeamOverlay()
        }
    }
}
