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
import kotlin.math.sqrt

/**
 * Minispiel "ShakeDecrypt".
 *
 * Jeder Spieler bekommt einen Teil des Tresorcodes, der zu Beginn "verschlüsselt" dargestellt wird.
 * Durch Schütteln des Handys (erkannt über Beschleunigungssensor) steigt der Fortschritt, der nach und nach die echten Ziffern aufdeckt.
 * Je schneller/öfter geschüttelt wird, desto schneller ist der Code vollständig "entschlüsselt".
 * ACHTUNG: Diese Implementierung ist für ein einzelnes Gerät. Eine echte Mehrspielerfunktionalität ist hier noch nicht enthalten.
 */
@Composable
fun ShakeDecryptScreen() {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    val hasSensor = accelerometer != null

    var progress by remember { mutableFloatStateOf(0f) }
    var isComplete by remember { mutableStateOf(false) }
    var lastShakeTime by remember { mutableLongStateOf(0L) }

    val shakeThreshold = 2.7f
    val minShakeIntervalMs = 150L
    val progressPerShake = 0.05f

    DisposableEffect(hasSensor) {
        if (!hasSensor) {
            return@DisposableEffect onDispose { }
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isComplete) return

                val gX = event.values[0] / SensorManager.GRAVITY_EARTH
                val gY = event.values[1] / SensorManager.GRAVITY_EARTH
                val gZ = event.values[2] / SensorManager.GRAVITY_EARTH
                val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

                val now = System.currentTimeMillis()
                if (gForce > shakeThreshold && now - lastShakeTime > minShakeIntervalMs) {
                    lastShakeTime = now
                    progress = (progress + progressPerShake).coerceAtMost(1f)
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
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${(progress * 100).toInt()}% entschlüsselt")
    }
}