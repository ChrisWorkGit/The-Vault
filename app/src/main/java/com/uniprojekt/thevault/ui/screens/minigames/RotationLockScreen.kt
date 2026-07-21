// PROMPT-REFERENZ: [REF-ISSUE47-MINIGAME-BUGS-FIX]
package com.uniprojekt.thevault.ui.screens.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uniprojekt.thevault.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Minispiel "RotationLock".
 *
 * Der Spieler sieht die Frontalansicht eines Schlosses und muss mittels Wischen einen
 * Dietrich in eine ihm unbekannte Lösungsposition platzieren. Durch Drehen des Geräts
 * wird das Schloss gespannt und somit getestet ob die gewählte Position die richtige ist.
 *
 * Zusätzlich wird der Dietrich, falls dieser nicht richtig steht, bei zu hoher Belastung
 * zerstört. Jeder Spieler hat MAX_ATTEMPTS Versuche um das Minispiel zu lösen, sonst gilt
 * es als fehlgeschlagen.
 */
private const val MAX_ATTEMPTS = 3
private const val FULL_OPEN_DEG = 90f
private const val TENSION_RANGE_DEG = 45f
private const val SWEET_TOLERANCE_DEG = 10f
private const val BIND_RANGE_DEG = 70f
private const val MIN_ALLOWED_FRAC = 0.06f
private const val SUCCESS_FRAC = 0.95f

private const val STRESS_GAIN = 1.7f
private const val STRESS_RELAX = 1.2f

private const val PICK_MIN_DEG = 0f
private const val PICK_MAX_DEG = 180f
private const val TOUCH_SENSITIVITY = 0.25f  // Grad Dietrich Drehung pro gewischtem Pixel

private const val ROLL_SIGN = 1f    // rechts = 1, links = -1
private const val LOOP_MS = 16L

@Composable
fun RotationLockScreen(
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

    val gameActiveState by rememberUpdatedState(isGameActive)

    val sweetAngleDeg = remember { PICK_MIN_DEG + Random.nextFloat() * (PICK_MAX_DEG - PICK_MIN_DEG) }

    var pickAngleDeg by remember { mutableFloatStateOf(90f) }
    var tensionFrac by remember { mutableFloatStateOf(0f) }
    var stress by remember { mutableFloatStateOf(0f) }
    var attemptsLeft by remember { mutableIntStateOf(MAX_ATTEMPTS) }
    var isSolved by remember { mutableStateOf(false) }
    var isFailed by remember { mutableStateOf(false) }

    fun allowedFrac(): Float {
        val error = abs(pickAngleDeg - sweetAngleDeg)
        return if (error <= SWEET_TOLERANCE_DEG) 1f
        else (1f - (error - SWEET_TOLERANCE_DEG) / BIND_RANGE_DEG).coerceIn(MIN_ALLOWED_FRAC, 1f)
    }

    val allowed = allowedFrac()
    val cylinderFrac = minOf(tensionFrac, allowed)
    val cylinderAngle = cylinderFrac * FULL_OPEN_DEG

    DisposableEffect(hasSensor) {
        onReady()
        Log.d("RotationLock", "onReady() hasSensor=$hasSensor sweetAngle=$sweetAngleDeg")
        if (!hasSensor) return@DisposableEffect onDispose { }

        var rollNeutral = 0f
        var calibrated = false
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isSolved || isFailed || !gameActiveState) return

                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val rollDeg = Math.toDegrees(orientation[2].toDouble()).toFloat()

                if (!calibrated) {
                    rollNeutral = rollDeg
                    calibrated = true
                    Log.d("RotationLock", "kalibriert rollNeutral=$rollNeutral")
                    return
                }

                // Nur Drehung nach rechts vom Ursprung zählt
                val diff = (ROLL_SIGN * (rollDeg - rollNeutral)).coerceAtLeast(0f)
                tensionFrac = (diff / TENSION_RANGE_DEG).coerceIn(0f, 1f)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    LaunchedEffect(isGameActive) {
        while (isActive) {
            if (!isGameActive || isSolved || isFailed) {
                delay(LOOP_MS)
                continue
            }

            val dt = LOOP_MS / 1000f
            val curAllowed = allowedFrac()

            // Erfolg
            if (curAllowed >= 1f && tensionFrac >= SUCCESS_FRAC) {
                stress = 0f
                isSolved = true
                Log.d("RotationLock", "GELOEST pick=$pickAngleDeg sweet=$sweetAngleDeg tension=$tensionFrac")
                continue
            }

            // Überspannung
            val over = (tensionFrac - curAllowed).coerceAtLeast(0f)
            stress = if (over > 0f) {
                (stress + over * STRESS_GAIN * dt).coerceAtMost(1f)
            } else {
                (stress - STRESS_RELAX * dt).coerceAtLeast(0f)
            }

            // Dietrich bricht
            if (stress >= 1f) {
                attemptsLeft -= 1
                stress = 0f
                tensionFrac = 0f
                Log.d("RotationLock", "Dietrich gebrochen verbleibend=$attemptsLeft pick=$pickAngleDeg sweet=$sweetAngleDeg")
                if (attemptsLeft <= 0) {
                    isFailed = true
                    Log.d("RotationLock", "alle Dietriche gebrochen -> isFailed=true")
                }
            }

            delay(LOOP_MS)
        }
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            Log.d("RotationLock", "onComplete()")
            delay(800)
            onComplete()
        }
    }

    LaunchedEffect(isFailed) {
        if (isFailed) {
            Log.d("RotationLock", "onFail()")
            delay(800)
            onFail()
        }
    }

    // UI
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
            Text(
                text = "Schloss stechen",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = NeonGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dietrich wischen · Handy nach rechts drehen zum Spannen",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = TextGreen
            )
            Spacer(modifier = Modifier.height(16.dp))

            val pickColor = lerp(Color.White, NeonRed, stress)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .pointerInput(isSolved, isFailed, isGameActive) {
                        if (isSolved || isFailed || !isGameActive) return@pointerInput
                        detectDragGestures { change, drag ->
                            change.consume()
                            pickAngleDeg = (pickAngleDeg - drag.x * TOUCH_SENSITIVITY).coerceIn(PICK_MIN_DEG, PICK_MAX_DEG)
                        }
                    }
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val center = Offset(cx, cy)
                val outerR = size.minDimension / 2f * 0.92f
                val plugR = outerR * 0.72f

                // Gehäuse
                drawCircle(color = HousingColor, radius = outerR, center = center)
                drawCircle(
                    color = NeonGreen.copy(alpha = 0.55f),
                    radius = outerR,
                    center = center,
                    style = Stroke(width = 3f)
                )

                // Anfangsstellung
                drawCircle(
                    color = NeonGreen.copy(alpha = 0.5f),
                    radius = 5f,
                    center = Offset(cx, cy + outerR - 2f)
                )

                rotate(degrees = cylinderAngle, pivot = center) {
                    // Schlosszylinder
                    drawCircle(color = DarkGreen, radius = plugR, center = center)
                    drawCircle(
                        color = NeonGreen.copy(alpha = 0.7f),
                        radius = plugR,
                        center = center,
                        style = Stroke(width = 3f)
                    )

                    // Schlüsselloch
                    drawCircle(color = Color.Black, radius = plugR * 0.14f, center = center)
                    drawLine(
                        color = Color.Black,
                        start = center,
                        end = Offset(cx, cy + plugR * 0.55f),
                        strokeWidth = plugR * 0.16f,
                        cap = StrokeCap.Round
                    )

                    // Spannhebel
                    val leverColor = NeutralColor
                    val leverTop = Offset(cx, cy + plugR * 0.45f)
                    val leverBottom = Offset(cx, cy + outerR * 1.02f)
                    drawLine(
                        color = leverColor,
                        start = leverTop,
                        end = leverBottom,
                        strokeWidth = 16f,
                        cap = StrokeCap.Round
                    )

                    // Dietrich
                    val rad = Math.toRadians(pickAngleDeg.toDouble())
                    val dirX = cos(rad).toFloat()
                    val dirY = -sin(rad).toFloat()
                    val pickLen = plugR * 1.55f

                    // Zittern bei Belastung
                    val shake = if (stress > 0.25f) (Random.nextFloat() - 0.5f) * stress * 6f else 0f
                    val tip = Offset(cx + dirX * pickLen + shake, cy + dirY * pickLen)
                    val hookMid = Offset(cx + dirX * pickLen * 0.82f, cy + dirY * pickLen * 0.82f)

                    val pickPath = Path().apply {
                        moveTo(cx, cy)
                        lineTo(hookMid.x, hookMid.y)
                        lineTo(tip.x, tip.y)
                    }
                    drawPath(
                        path = pickPath,
                        color = pickColor,
                        style = Stroke(width = 7f, cap = StrokeCap.Round)
                    )
                    drawCircle(color = pickColor, radius = 5f, center = center)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verbleibende Dietriche/Versuche
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Dietriche: ",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextGreen
                )
                repeat(MAX_ATTEMPTS) { i ->
                    Text(
                        text = "|",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = if (i < attemptsLeft) NeonGreen else NeutralColor.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Belastungsanzeige
            Text(
                text = when {
                    isSolved -> "GEKNACKT"
                    isFailed -> "ALLE DIETRICHE GEBROCHEN"
                    stress > 0.66f -> "!! DIETRICH GLEICH GEBROCHEN !!"
                    stress > 0.15f -> "Spannung zu hoch"
                    else -> "Dietrich ausrichten und vorsichtig spannen"
                },
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = lerp(TextGreen, NeonRed, stress)
            )

            if (!hasSensor) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Kein Rotationssensor.",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NeutralColor
                )
                Slider(
                    value = tensionFrac,
                    onValueChange = { if (!isSolved && !isFailed && isGameActive) tensionFrac = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }

        if (!isGameActive) {
            WaitingForTeamOverlay()
        }
    }
}
