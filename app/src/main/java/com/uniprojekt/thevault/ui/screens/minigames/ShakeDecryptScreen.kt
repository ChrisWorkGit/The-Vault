package com.uniprojekt.thevault.ui.screens.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Minispiel "ShakeDecrypt".
 *
 * Jeder Spieler bekommt einen Teil des Tresorcodes, der zu Beginn "verschlüsselt" dargestellt wird.
 * Durch Schütteln des Handys (erkannt über Beschleunigungssensor) steigt der Fortschritt, der nach und nach die echten Ziffern aufdeckt.
 * Je schneller/öfter geschüttelt wird, desto schneller ist der Code vollständig "entschlüsselt".
 */

private const val SHAKE_THRESHOLD = 2.7f
private const val MIN_SHAKE_INTERVALS_MS = 150L
private const val PROGRESS_PER_SHAKE = 0.05f

@Composable
fun ShakeDecryptScreen(
    onComplete: () -> Unit,
    onFail: () -> Unit,
    onReady: () -> Unit = {},
    isGameActive: Boolean = true
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    val hasSensor = accelerometer != null

    val secretCode = remember { (1000..9999).random().toString() }

    var progress by remember { mutableFloatStateOf(0f) }
    var isComplete by remember { mutableStateOf(false) }
    var lastShakeTime by remember { mutableLongStateOf(0L) }


    DisposableEffect(hasSensor) {
        if (!hasSensor) {
            onReady() // Auch ohne Sensor bereit
            return@DisposableEffect onDispose { }
        }

        // AI-Generated: Signalisiere Bereitschaft sofort beim Laden
        onReady()

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isComplete || !isGameActive) return

                val gX = event.values[0] / SensorManager.GRAVITY_EARTH
                val gY = event.values[1] / SensorManager.GRAVITY_EARTH
                val gZ = event.values[2] / SensorManager.GRAVITY_EARTH
                val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

                val now = System.currentTimeMillis()
                if (gForce > SHAKE_THRESHOLD && now - lastShakeTime > MIN_SHAKE_INTERVALS_MS) {
                    lastShakeTime = now
                    progress = (progress + PROGRESS_PER_SHAKE).coerceAtMost(1f)
                    if (progress >= 1f) {
                        isComplete = true
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    LaunchedEffect(isComplete) {
        if (isComplete) {
            delay(800)
            onComplete()
        }
    }

    // "Verschlüsselte" Darstellung
    var glitchTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (!isComplete) {
            delay(80)
            glitchTick++
        }
    }

    val revealedCount = (progress * secretCode.length).toInt().coerceIn(0, secretCode.length)
    val displayedCode = remember(revealedCount, glitchTick, isComplete) {
        secretCode.mapIndexed { index, digit ->
            if (index < revealedCount) digit else Random.nextInt(0, 10).digitToChar()
        }.joinToString("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Code entschlüsseln", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Schüttle das Handy so schnell wie möglich!", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = displayedCode,
            fontSize = 48.sp,
        )
        Spacer(modifier = Modifier.height(32.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${(progress * 100).toInt()}% entschlüsselt")

        if (!hasSensor) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Kein Bewegungssensor gefunden - Simulation für Tests:")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (!isComplete) {
                    progress = (progress + PROGRESS_PER_SHAKE).coerceAtMost(1f)
                    if (progress >= 1f) isComplete = true
                }
            }) {
                Text(text = "Schütteln simulieren")
            }
        }
    }
}