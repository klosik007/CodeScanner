package com.pklos.codescanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.pklos.codescanner.camera.CameraAssistant
import com.pklos.codescanner.ui.dashboard.Home
import com.pklos.codescanner.ui.theme.CodeScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraAssistant = CameraAssistant()

        setContent {
            CodeScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home(cameraAssistant)
                }
            }
        }
    }

    private lateinit var cameraAssistant: CameraAssistant
}