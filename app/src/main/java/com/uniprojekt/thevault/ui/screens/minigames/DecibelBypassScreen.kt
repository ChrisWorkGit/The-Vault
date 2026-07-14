// PROMPT-REFERENZ: [REF-ISSUE05-DECIBEL-BYPASS]
package com.uniprojekt.thevault.ui.screens.minigames

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.ToneGenerator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.uniprojekt.thevault.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

// AI-Generated: Decibel Bypass Co-op Microphone Game

/**
 * Kooperatives Minispiel "Decibel Bypass".
 * Nutzt das Mikrofon zur Alarm-Erkennung beim Partner und Gesten zur Wellen-Korrektur.
 */
@Composable
fun DecibelBypassScreen(
    onComplete: () -> Unit,
    onFail: () -> Unit
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val scope = rememberCoroutineScope()

    // --- State Management ---
    var hasPermission by remember { 
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) 
    }
    var isVolumeMax by remember { mutableStateOf(false) }
    var errors by remember { mutableIntStateOf(0) }
    val maxErrors = 3
    
    // Spiel-Logik State
    var waveDirection by remember { mutableIntStateOf(1) } // 1 für Oben, -1 für Unten
    var lastReactionTime by remember { mutableLongStateOf(0L) }
    var isPhaseActive by remember { mutableStateOf(false) }
    var micAmplitude by remember { mutableFloatStateOf(0f) }

    // AI-Generated: Procedural Wave & Antiphase Feedback States
    var playerAmplitude by remember { mutableFloatStateOf(0f) }
    var smoothedPlayerAmplitude by remember { mutableFloatStateOf(0f) }
    var waveBaseAmplitude by remember { mutableFloatStateOf(80f) }
    var isSpiking by remember { mutableStateOf(false) }
    var boxHeight by remember { mutableFloatStateOf(0f) }
    
    // AI-Generated: Balancing & Grace Period States
    var lastErrorTimestamp by remember { mutableLongStateOf(0L) }
    val gracePeriodMs = 1500L
    val toleranceThreshold = 55f // Erhöhte Toleranz für faireres Gameplay (~20-25% Abweichung)

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
    }

    // --- Volume Check Loop ---
    LaunchedEffect(Unit) {
        while (true) {
            val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            isVolumeMax = current >= max
            if (isVolumeMax && hasPermission && !isPhaseActive) {
                isPhaseActive = true
            }
            delay(500)
        }
    }

    // --- Mikrofon Überwachung (Dezibel-Bypass Logik) ---
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            withContext(Dispatchers.IO) {
                val bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
                val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
                
                audioRecord.startRecording()
                val buffer = ShortArray(bufferSize)
                
                while (isPhaseActive) {
                    val read = audioRecord.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        var maxAmp = 0f
                        for (i in 0 until read) {
                            val amp = Math.abs(buffer[i].toInt()).toFloat()
                            if (amp > maxAmp) maxAmp = amp
                        }
                        micAmplitude = maxAmp
                        
                        // Schwellenwert für Alarm-Erkennung (Partner hat Fehler gemacht)
                        if (maxAmp > 25000) { 
                            val now = System.currentTimeMillis()
                            if (now - lastErrorTimestamp > gracePeriodMs) {
                                withContext(Dispatchers.Main) {
                                    errors++
                                    lastErrorTimestamp = now
                                    // Kurzer visueller Feedback-Effekt hier möglich
                                }
                            }
                            delay(gracePeriodMs) // Cooldown für Fehler
                        }
                    }
                    delay(50)
                }
                audioRecord.stop()
                audioRecord.release()
            }
        }
    }

    // --- Game Loop (Wellen-Animation & Richtungswechsel) ---
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    LaunchedEffect(isPhaseActive) {
        if (isPhaseActive) {
            while (errors < maxErrors) {
                // Zufällige organische Änderungen der Wellen-Charakteristik
                delay(Random.nextLong(1500, 4000))
                
                // Neue Richtung und Grund-Amplitude würfeln
                waveDirection = if (Random.nextBoolean()) 1 else -1
                waveBaseAmplitude = Random.nextFloat() * 60f + 40f
                
                // Kurze Amplituden-Spikes triggern (simuliert Signalstörungen)
                if (Random.nextFloat() > 0.75f) {
                    isSpiking = true
                    delay(400)
                    isSpiking = false
                }
                
                lastReactionTime = System.currentTimeMillis()
                
                // Zeitfenster für die Korrektur durch den Spieler
                delay(1200)
                
                // Erfolgskontrolle: Die rote Antiphase muss die grüne Welle annähernd auslöschen
                val targetAmp = -(waveBaseAmplitude * waveDirection)
                val diff = abs(smoothedPlayerAmplitude - targetAmp)
                val now = System.currentTimeMillis()
                
                // Nur Fehler triggern, wenn außerhalb der Toleranz UND nach der Gnadenfrist (Grace Period)
                if (diff > toleranceThreshold && (now - lastErrorTimestamp > gracePeriodMs)) {
                    triggerAlarm()
                    errors++
                    lastErrorTimestamp = now
                }
            }
            // Bei zu vielen Fehlern wurde die Schleife beendet
            onFail()
        }
    }

    // Siegbedingung: Wenn eine bestimmte Zeit überlebt wurde oder Minispiel-Kette weitergeht
    // Da es kooperativ ist, könnte hier auch ein Signal vom Partner kommen.
    // Für diesen Task implementieren wir eine Zeit-Siegbedingung von 30 Sekunden.
    LaunchedEffect(isPhaseActive) {
        if (isPhaseActive) {
            delay(30000)
            if (errors < maxErrors) {
                onComplete()
            }
        }
    }

    // Pulse-Effekt für die "Sichere" Welle
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Bestimmung ob der Spieler aktuell "Safe" ist (innerhalb der Toleranz)
    val isSafe = abs(smoothedPlayerAmplitude - (-(waveBaseAmplitude * waveDirection))) <= toleranceThreshold

    // --- UI Layout ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .crtOverlay()
            .background(CyberBackground)
    ) {
        if (!hasPermission) {
            PermissionOverlay { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
        } else if (!isVolumeMax) {
            VolumeWarningOverlay {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0
                )
            }
        } else {
            // Aktiver Spielbildschirm
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "DECIBEL BYPASS",
                    style = cyberpunkGlowStyle(NeonGreen),
                    color = NeonGreen,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Fehler-Matrix [X] [X] [X]
                ErrorMatrix(errors, maxErrors)

                Spacer(modifier = Modifier.weight(1f))

                // Wellen-Visualisierung
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .border(1.dp, DarkGreen, CyberpunkShape())
                        .onGloballyPositioned { boxHeight = it.size.height.toFloat() }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    playerAmplitude = offset.y - (boxHeight / 2f)
                                },
                                onDrag = { change, _ ->
                                    // Mapping: Finger-Y-Position zu relativer Amplitude zur Mitte
                                    playerAmplitude = change.position.y - (boxHeight / 2f)
                                    // Low-Pass-Filter / Smoothing: Reduziert Micro-Jitter für stabilere Eingabe
                                    smoothedPlayerAmplitude = smoothedPlayerAmplitude * 0.7f + playerAmplitude * 0.3f
                                },
                                onDragEnd = { 
                                    playerAmplitude = 0f
                                    smoothedPlayerAmplitude = 0f
                                },
                                onDragCancel = { 
                                    playerAmplitude = 0f
                                    smoothedPlayerAmplitude = 0f
                                }
                            )
                        }
                ) {
                    WaveCanvas(
                        phase = phase, 
                        baseAmplitude = waveBaseAmplitude * waveDirection,
                        playerAmplitude = smoothedPlayerAmplitude,
                        isSpiking = isSpiking,
                        isSafe = isSafe,
                        pulseScale = pulseScale
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "REAGIERE ENTGEGEN DER WELLENRICHTUNG",
                    color = TextGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Triggert einen schrillen Alarm-Sound über den ToneGenerator.
 */
private fun triggerAlarm() {
    val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
    toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500)
}

/**
 * Zeichnet die prozedurale grüne Welle und die rote Feedback-Gegenwelle des Spielers.
 */
@Composable
fun WaveCanvas(
    phase: Float, 
    baseAmplitude: Float,
    playerAmplitude: Float,
    isSpiking: Boolean,
    isSafe: Boolean,
    pulseScale: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val midY = size.height / 2f
        
        // 1. Baseline (gestrichelte Null-Linie zur Orientierung)
        drawLine(
            color = DarkGreen.copy(alpha = 0.5f),
            start = Offset(0f, midY),
            end = Offset(size.width, midY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
        )

        // 2. Grüne Welle (Superposition aus zwei Frequenzen für organisches Signalrauschen)
        val greenPath = Path()
        greenPath.moveTo(0f, midY)
        
        for (x in 0..size.width.toInt() step 4) {
            val relX = x / size.width
            // Mathematische Überlagerung: Haupt-Sinus + schnellere Interferenzwelle
            var yOffset = sin(relX * 2 * PI.toFloat() + phase) * baseAmplitude
            yOffset += sin(relX * 5 * PI.toFloat() + phase * 1.5f) * (baseAmplitude * 0.2f)
            
            // Prozeduraler Spike-Effekt in der Mitte der Welle
            if (isSpiking && x > size.width * 0.45f && x < size.width * 0.55f) {
                yOffset *= 1.8f
            }
            
            greenPath.lineTo(x.toFloat(), midY + yOffset)
        }
        
        drawPath(
            path = greenPath,
            color = NeonGreen,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // 3. Spieler-Gegenwelle (Antiphase basierend auf Finger-Tracking)
        if (playerAmplitude != 0f) {
            val redPath = Path()
            redPath.moveTo(0f, midY)
            
            for (x in 0..size.width.toInt() step 4) {
                val relX = x / size.width
                // Die Spieler-Welle nutzt die gleiche Grundfrequenz wie die Signal-Welle
                val yOffset = sin(relX * 2 * PI.toFloat() + phase) * playerAmplitude
                redPath.lineTo(x.toFloat(), midY + yOffset)
            }
            
            // Dynamischer Farbwechsel: Neongrün/Gelb wenn Safe, Rot wenn außerhalb der Toleranz
            val waveColor = if (isSafe) NeonGreen else Color.Red
            val glowAlpha = if (isSafe) 0.5f else 0.7f
            
            // Zeichne Spieler-Welle mit Glow-Effekt und Puls-Skalierung bei "Safe"
            drawPath(
                path = redPath,
                color = waveColor.copy(alpha = glowAlpha),
                style = Stroke(
                    width = (if (isSafe) 4.dp.toPx() * pulseScale else 4.dp.toPx()), 
                    cap = StrokeCap.Round
                )
            )
            // Äußerer Glow-Ring
            drawPath(
                path = redPath,
                color = waveColor.copy(alpha = 0.2f),
                style = Stroke(
                    width = (if (isSafe) 15.dp.toPx() * pulseScale else 12.dp.toPx()), 
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Composable
fun ErrorMatrix(errors: Int, max: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(max) { index ->
            val isActive = index < errors
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, if (isActive) Color.Red else DarkGreen, CyberpunkShape()),
                contentAlignment = Alignment.Center
            ) {
                // Nur wenn aktiv, zeichnen wir einen dezenten, runden Glow hinter dem X
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Red.copy(alpha = 0.25f), CircleShape)
                    )
                }
                
                Text(
                    text = if (isActive) "X" else "·",
                    color = if (isActive) Color.Red else DarkGreen,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    style = if (isActive) cyberpunkGlowStyle(Color.Red, radius = 8f) else LocalTextStyle.current
                )
            }
        }
    }
}

@Composable
fun VolumeWarningOverlay(onSetMax: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "WARNUNG: MAXIMALES AUDIO-VOLUMEN ERFORDERLICH",
            color = Color.Red,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSetMax,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
            shape = CyberpunkShape(),
            modifier = Modifier.border(1.dp, Color.Red, CyberpunkShape())
        ) {
            Text(text = "AUDIO MAXIMIEREN", color = Color.Red)
        }
    }
}

@Composable
fun PermissionOverlay(onRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MIKROFON-ZUGRIFF BENÖTIGT",
            color = NeonGreen,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequest, shape = CyberpunkShape()) {
            Text(text = "BERECHTIGUNG ERTEILEN")
        }
    }
}
