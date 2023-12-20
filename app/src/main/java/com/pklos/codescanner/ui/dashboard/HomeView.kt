package com.pklos.codescanner.ui.dashboard

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.pklos.codescanner.ml.BarcodeScanner
import com.pklos.codescanner.utils.Screen
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home() {
    val cameraPermissionsState = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(key1 = Unit) {
        if (!cameraPermissionsState.status.isGranted && !cameraPermissionsState.status.shouldShowRationale) {
            cameraPermissionsState.launchPermissionRequest()
        }
    }

    if (cameraPermissionsState.status.isGranted) {
        CameraPreviewScreen()
    } else {
        NoCameraPermissionScreen(cameraPermissionState = cameraPermissionsState)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoCameraPermissionScreen(cameraPermissionState: PermissionState) {
    //TODO: view
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CameraPreviewScreen() {
    val ctx = LocalContext.current
    val cameraController = remember { LifecycleCameraController(ctx) }
    var barcodeText by remember { mutableStateOf( "Barcode value here") }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding: PaddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { ctx ->
                val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
                val previewView = PreviewView(ctx).apply {
                    setBackgroundColor(Color.White.toArgb())
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    val imageCapture = ImageCapture.Builder().build()
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, BarcodeScanner {
                                barcodeText = if(BarcodeScanner.changeBarcodeText)
                                    BarcodeScanner.barcodeValue ?: "Something's wrong with barcode"
                                else
                                    "Barcode value here"
                            })
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            ctx as ComponentActivity,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalyzer)
                    } catch(e: Exception) {
                        println("Exception: camera bind to lifecycle failed")
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            onReset = {},
            onRelease = {
                cameraController.unbind()
            }
        )
    }

    BarcodeBox()

    BarcodeText(barcodeText)
}

@Composable
fun BarcodeBox() {
    val boxWidth = (Screen.width / 3).dp
    val boxHeight = (Screen.height / 12).dp

    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(text = "Place barcode in the box",
             textAlign = TextAlign.Center,
             color = Color.White
        )

        Canvas(modifier = Modifier
            .size(width = boxWidth, height = boxHeight)
        ) {
            drawRoundRect(
                color = Color.Magenta,
                topLeft = Offset.Zero,
                style = Stroke(),
                cornerRadius = CornerRadius(10.dp.toPx())
            )
        }
    }
}

@Composable
fun BarcodeText(barcodeText: String) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = barcodeText,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun BarcodeBoxPreview() {
    Surface(color = Color.Black) {
        BarcodeBox()
    }
}

