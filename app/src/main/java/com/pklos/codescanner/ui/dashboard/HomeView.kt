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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.pklos.codescanner.camera.CameraAssistant
import com.pklos.codescanner.ml.BarcodeScanner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home(cameraAssistant: CameraAssistant) {
    val cameraPermissionsState = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(key1 = Unit) {
        if (!cameraPermissionsState.status.isGranted && !cameraPermissionsState.status.shouldShowRationale) {
            cameraPermissionsState.launchPermissionRequest()
        }
    }

    if (cameraPermissionsState.status.isGranted) {
        CameraPreviewScreen(cameraAssistant)
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
fun CameraPreviewScreen(cameraAssistant: CameraAssistant) {
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
                            it.setAnalyzer(cameraExecutor, BarcodeScanner { barcodeText = "Barcode Found" })
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

