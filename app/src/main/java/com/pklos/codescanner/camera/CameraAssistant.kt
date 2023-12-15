package com.pklos.codescanner.camera

import androidx.camera.view.PreviewView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraAssistant {
    fun start(previewView: PreviewView) {

    }

    fun stop() {
        cameraExecutor.shutdown()
    }

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
}