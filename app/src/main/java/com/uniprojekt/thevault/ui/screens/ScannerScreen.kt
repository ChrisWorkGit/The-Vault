// PROMPT-REFERENZ: [REF-ISSUE17-QR-CONNECT]
// PROMPT-REFERENZ: [REF-ISSUE20-CYBERPUNK-THEME]
package com.uniprojekt.thevault.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.uniprojekt.thevault.ui.theme.*
import java.util.concurrent.Executors

// AI-Generated: QR-Code P2P Onboarding Layer with Manual Fallback
// AI-Generated: Cyberpunk Design System & Neon UI Layer

/**
 * Screen zum Scannen von QR-Codes im Cyberpunk-Stil ("INFILTRATE").
 */
@Composable
fun ScannerScreen(
    onResult: (String) -> Unit,
    onManualInput: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }
    
    var manualIp by remember { mutableStateOf("") }

    // Berechtigungs-Handling
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .crtOverlay()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header: SYS::ONLINE & ENCRYPTED
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SYS::ONLINE",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonGreen
                )
                Text(
                    text = "🟢 ENCRYPTED",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigationszeile
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonGreen
                    )
                }
                Text(
                    text = "INFILTRATE",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Black
                    ).merge(cyberpunkGlowStyle(NeonGreen, radius = 4f)),
                    color = NeonGreen
                )
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Secure",
                    tint = NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info-Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonGreen)
                    .background(DarkGreen.copy(alpha = 0.2f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "SCAN ACCESS TOKEN OR ENTER HOST IP MANUALLY",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonGreen,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Kamera-Sucher Bereich
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RectangleShape)
                    .border(2.dp, DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.surfaceProvider = previewView.surfaceProvider
                                }

                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()

                                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                    processImageProxy(barcodeScanner, imageProxy, onResult)
                                }

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageAnalysis
                                    )
                                } catch (exc: Exception) {
                                    Log.e("ScannerScreen", "Use case binding failed", exc)
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Cyberpunk Viewfinder Overlay
                ScannerOverlay()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Untere Sucher-Meldung
            Text(
                text = "🟢 SCANNING... CAMERA FEED LIVE",
                style = MaterialTheme.typography.bodySmall,
                color = NeonGreen,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Manueller Bereich
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DarkGreen)
                Text(
                    text = " OR MANUAL ",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGreen,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = DarkGreen)
            }

            // IP Eingabefeld
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, DarkGreen)
                    .background(CyberBackground)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "IP:// ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                        color = TextGreen
                    )
                    BasicTextField(
                        value = manualIp,
                        onValueChange = { manualIp = it },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = NeonGreen,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(NeonGreen),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Connect Button
            Button(
                onClick = { onManualInput(manualIp) },
                shape = CyberpunkShape(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextGreen,
                    contentColor = CyberBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = "-> CONNECT TO VAULT",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}

/**
 * Zeichnet das Cyberpunk-Overlay für den Scanner (Laser, Ecken, Grid).
 */
@Composable
fun ScannerOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "Laser")
    
    // Animation für den Laser-Strahl
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LaserPosition"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val cornerSize = 40.dp.toPx()
        val strokeWidth = 3.dp.toPx()

        // 1. Zeichne Fadenkreuz-Ecken
        // Oben Links
        drawLine(NeonGreen, Offset(0f, 0f), Offset(cornerSize, 0f), strokeWidth)
        drawLine(NeonGreen, Offset(0f, 0f), Offset(0f, cornerSize), strokeWidth)
        
        // Oben Rechts
        drawLine(NeonGreen, Offset(width, 0f), Offset(width - cornerSize, 0f), strokeWidth)
        drawLine(NeonGreen, Offset(width, 0f), Offset(width, cornerSize), strokeWidth)
        
        // Unten Links
        drawLine(NeonGreen, Offset(0f, height), Offset(cornerSize, height), strokeWidth)
        drawLine(NeonGreen, Offset(0f, height), Offset(0f, height - cornerSize), strokeWidth)
        
        // Unten Rechts
        drawLine(NeonGreen, Offset(width, height), Offset(width - cornerSize, height), strokeWidth)
        drawLine(NeonGreen, Offset(width, height), Offset(width, height - cornerSize), strokeWidth)

        // 2. Zeichne Laser-Strahl mit Glow
        val yPos = height * laserY
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, NeonGreen, Color.Transparent),
                startY = yPos - 10f,
                endY = yPos + 10f
            ),
            topLeft = Offset(0f, yPos - 10f),
            size = Size(width, 20f)
        )
        drawLine(
            color = NeonGreen,
            start = Offset(0f, yPos),
            end = Offset(width, yPos),
            strokeWidth = 2.dp.toPx()
        )
    }
    
    // Mittiges Symbol
    Box(
        modifier = Modifier
            .size(150.dp)
            .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = NeonGreen.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "ALIGN TOKEN",
                style = MaterialTheme.typography.bodySmall,
                color = NeonGreen.copy(alpha = 0.5f)
            )
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onResult: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    barcodes[0].rawValue?.let { 
                        onResult(it)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("ScannerScreen", "Barcode scanning failed", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
