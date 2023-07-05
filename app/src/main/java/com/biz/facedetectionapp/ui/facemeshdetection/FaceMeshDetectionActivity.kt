package com.biz.facedetectionapp.ui.facemeshdetection

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.biz.facedetectionapp.databinding.ActivityFaceMeshDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import java.util.concurrent.Executors

class FaceMeshDetectionActivity : AppCompatActivity() {

    lateinit var binding: ActivityFaceMeshDetectionBinding

    private lateinit var cameraProvider: ProcessCameraProvider

    private lateinit var graphicOverlay: GraphicOverlay

    private var cameraSelectorOption = CameraSelector.DEFAULT_FRONT_CAMERA

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceMeshDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        graphicOverlay = binding.graphicOverlayFinder

        onClick()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun onClick() {
        binding.cameraChangeBtn.setOnClickListener {
            cameraSelectorOption =
                if (cameraSelectorOption == CameraSelector.DEFAULT_FRONT_CAMERA) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }
            graphicOverlay.toggleSelector()
            startCamera()
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val preview = createPreviewUseCase()
        val imageAnalysis = createImageAnalysisUseCase()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelectorOption,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createPreviewUseCase(): Preview {
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        return preview
    }

    private fun createImageAnalysisUseCase(): ImageAnalysis {

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
            detectFaceAndOverlay(imageProxy)
        }

        return imageAnalysis
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun detectFaceAndOverlay(imageProxy: ImageProxy) {

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val img = imageProxy.image!!
        val inputImage = InputImage.fromMediaImage(img, rotationDegrees)

        val options = FaceMeshDetectorOptions.Builder()
            .setUseCase(FaceMeshDetectorOptions.FACE_MESH)
            .build()

        val faceMeshDetector = FaceMeshDetection.getClient(options)
        val task = faceMeshDetector.process(inputImage)

        task.addOnSuccessListener { faces ->
            graphicOverlay.clear()

            faces.forEach {
                val faceGraphic = FaceMeshGraphic(graphicOverlay, it, img.cropRect)
                graphicOverlay.add(faceGraphic)
            }

            graphicOverlay.postInvalidate()
        }

        task.addOnCompleteListener {
            img.close()
            imageProxy.close()
        }
    }
}