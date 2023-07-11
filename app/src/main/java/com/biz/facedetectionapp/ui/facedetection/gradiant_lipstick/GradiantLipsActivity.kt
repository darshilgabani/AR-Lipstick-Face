package com.biz.facedetectionapp.ui.facedetection.gradiant_lipstick

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.biz.facedetectionapp.databinding.ActivityGradiantLipsBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

class GradiantLipsActivity : AppCompatActivity() {

    lateinit var binding: ActivityGradiantLipsBinding

    private lateinit var cameraProvider: ProcessCameraProvider

    private lateinit var graphicOverlay: GradiantLipsGraphicOverlay

    private var gradientColors: IntArray =
        intArrayOf(Color.parseColor("#b92b27"), Color.parseColor("#1565C0"))

    private var cameraSelectorOption = CameraSelector.DEFAULT_FRONT_CAMERA

    companion object {
        const val CAMERA_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGradiantLipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        graphicOverlay = binding.graphicOverlayFinder

        onClick()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
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

        binding.apply {
            eveningColor.setOnClickListener {
                onColorSelect("eveningColor")
            }

            gradegreyColor.setOnClickListener {
                onColorSelect("gradegreyColor")
            }

            harveyColor.setOnClickListener {
                onColorSelect("harveyColor")
            }

            jshineColor.setOnClickListener {
                onColorSelect("jshineColor")
            }

            magicColor.setOnClickListener {
                onColorSelect("magicColor")
            }

            metapolisColor.setOnClickListener {
                onColorSelect("metapolisColor")
            }

            sulphurColor.setOnClickListener {
                onColorSelect("sulphurColor")
            }

            witchingColor.setOnClickListener {
                onColorSelect("witchingColor")
            }

        }
    }

    private fun onColorSelect(gradiantType: String) {
        val visibilityMap = mapOf(
            "eveningColor" to View.VISIBLE,
            "gradegreyColor" to View.VISIBLE,
            "harveyColor" to View.VISIBLE,
            "jshineColor" to View.VISIBLE,
            "magicColor" to View.VISIBLE,
            "metapolisColor" to View.VISIBLE,
            "sulphurColor" to View.VISIBLE,
            "witchingColor" to View.VISIBLE
        )

        val tickVisibility = visibilityMap.mapValues { View.GONE }.toMutableMap()
        tickVisibility[gradiantType] = View.VISIBLE

        val colorMap = mapOf(
            "eveningColor" to intArrayOf(Color.parseColor("#b92b27"), Color.parseColor("#1565C0")),
            "gradegreyColor" to intArrayOf(Color.parseColor("#bdc3c7"), Color.parseColor("#2c3e50")),
            "harveyColor" to intArrayOf(Color.parseColor("#1f4037"), Color.parseColor("#99f2c8")),
            "jshineColor" to intArrayOf(
                Color.parseColor("#12c2e9"),
                Color.parseColor("#c471ed"),
                Color.parseColor("#f64f59")
            ),
            "magicColor" to intArrayOf(
                Color.parseColor("#59C173"),
                Color.parseColor("#a17fe0"),
                Color.parseColor("#5D26C1")
            ),
            "metapolisColor" to intArrayOf(Color.parseColor("#659999"), Color.parseColor("#f4791f")),
            "sulphurColor" to intArrayOf(Color.parseColor("#CAC531"), Color.parseColor("#F3F9A7")),
            "witchingColor" to intArrayOf(Color.parseColor("#c31432"), Color.parseColor("#240b36"))
        )

        gradientColors = colorMap[gradiantType] ?: intArrayOf()

        binding.apply {
            eveningTick.visibility = tickVisibility["eveningColor"] ?: View.GONE
            gradegreyTick.visibility = tickVisibility["gradegreyColor"] ?: View.GONE
            harveyTick.visibility = tickVisibility["harveyColor"] ?: View.GONE
            jshineTick.visibility = tickVisibility["jshineColor"] ?: View.GONE
            magicTick.visibility = tickVisibility["magicColor"] ?: View.GONE
            metapolisTick.visibility = tickVisibility["metapolisColor"] ?: View.GONE
            sulphurTick.visibility = tickVisibility["sulphurColor"] ?: View.GONE
            witchingTick.visibility = tickVisibility["witchingColor"] ?: View.GONE
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
    private fun detectFaceAndOverlay(
        imageProxy: ImageProxy
    ) {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val img = imageProxy.image!!
        val inputImage = InputImage.fromMediaImage(img, rotationDegrees)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .enableTracking()
            .build()

        val faceDetector = FaceDetection.getClient(options)
        val task = faceDetector.process(inputImage)


        task.addOnSuccessListener { faces ->

            graphicOverlay.clear()

            faces.forEach {

                val faceGraphic =
                    GradiantLipsGraphic(graphicOverlay, it, img.cropRect, gradientColors)
                graphicOverlay.add(faceGraphic)
            }

            graphicOverlay.postInvalidate()
        }

        task.addOnCompleteListener {
            img.close()
            imageProxy.close()
        }

        task.addOnFailureListener { exception ->
            exception.printStackTrace()
        }
    }
}


//    private fun onColorSelect(gradiantType: String) {
//        when (gradiantType) {
//            "eveningColor" -> {
//                gradientColors =
//                    intArrayOf(Color.parseColor("#b92b27"), Color.parseColor("#1565C0"))
//                binding.apply {
//                    eveningTick.visibility = View.VISIBLE
//                    gradegreyTick.visibility = View.GONE
//                    harveyTick.visibility = View.GONE
//                    jshineTick.visibility = View.GONE
//                    magicTick.visibility = View.GONE
//                    metapolisTick.visibility = View.GONE
//                    sulphurTick.visibility = View.GONE
//                    witchingTick.visibility = View.GONE
//                }
//            }
//
//            "gradegreyColor" -> {
//                gradientColors =
//                    intArrayOf(Color.parseColor("#bdc3c7"), Color.parseColor("#2c3e50"))
//                binding.apply {
//                    gradegreyTick.visibility = View.VISIBLE
//                    eveningTick.visibility = View.GONE
//                    harveyTick.visibility = View.GONE
//                    jshineTick.visibility = View.GONE
//                    magicTick.visibility = View.GONE
//                    metapolisTick.visibility = View.GONE
//                    sulphurTick.visibility = View.GONE
//                    witchingTick.visibility = View.GONE
//                }
//            }
//
//            "harveyColor" -> {
//                gradientColors =
//                    intArrayOf(Color.parseColor("#1f4037"), Color.parseColor("#99f2c8"))
//                binding.apply {
//                    harveyTick.visibility = View.VISIBLE
//                    gradegreyTick.visibility = View.GONE
//                    eveningTick.visibility = View.GONE
//                    jshineTick.visibility = View.GONE
//                    magicTick.visibility = View.GONE
//                    metapolisTick.visibility = View.GONE
//                    sulphurTick.visibility = View.GONE
//                    witchingTick.visibility = View.GONE
//                }
//            }
//
//            "jshineColor" -> {
//                gradientColors =
//                    intArrayOf(Color.parseColor("#12c2e9"), Color.parseColor("#c471ed"),Color.parseColor("#f64f59"))
//                binding.apply {
//                    jshineTick.visibility = View.VISIBLE
//                    harveyTick.visibility = View.GONE
//                    gradegreyTick.visibility = View.GONE
//                    eveningTick.visibility = View.GONE
//                    magicTick.visibility = View.GONE
//                    metapolisTick.visibility = View.GONE
//                    sulphurTick.visibility = View.GONE
//                    witchingTick.visibility = View.GONE
//                }
//            }
//
//            "magicColor" -> {
//                gradientColors =
//                    intArrayOf(Color.parseColor("#59C173"), Color.parseColor("#a17fe0"),Color.parseColor("#5D26C1"))
//                binding.apply {
//                    magicTick.visibility = View.VISIBLE
//                    jshineTick.visibility = View.GONE
//                    harveyTick.visibility = View.GONE
//                    gradegreyTick.visibility = View.GONE
//                    eveningTick.visibility = View.GONE
//                    metapolisTick.visibility = View.GONE
//                    sulphurTick.visibility = View.GONE
//                    witchingTick.visibility = View.GONE
//                }
//            }
//
//            "metapolisColor" -> {
//                gradientColors =
//                    intArrayOf(Color.parseColor("#659999"), Color.parseColor("#f4791f"))
//                binding.apply {
//                    metapolisTick.visibility = View.VISIBLE
//                    magicTick.visibility = View.GONE
//                    jshineTick.visibility = View.GONE
//                    harveyTick.visibility = View.GONE
//                    gradegreyTick.visibility = View.GONE
//                    eveningTick.visibility = View.GONE
//                    sulphurTick.visibility = View.GONE
//                    witchingTick.visibility = View.GONE
//                }
//            }
//
//            "sulphurColor" -> {
//                gradientColors =
//                    intArrayOf(Color.parseColor("#CAC531"), Color.parseColor("#F3F9A7"))
//                binding.apply {
//                    sulphurTick.visibility = View.VISIBLE
//                    metapolisTick.visibility = View.GONE
//                    magicTick.visibility = View.GONE
//                    jshineTick.visibility = View.GONE
//                    harveyTick.visibility = View.GONE
//                    gradegreyTick.visibility = View.GONE
//                    eveningTick.visibility = View.GONE
//                    witchingTick.visibility = View.GONE
//                }
//            }
//
//            "witchingColor" -> {
//                gradientColors =
//                    intArrayOf(Color.parseColor("#c31432"), Color.parseColor("#240b36"))
//                binding.apply {
//                    witchingTick.visibility = View.VISIBLE
//                    sulphurTick.visibility = View.GONE
//                    metapolisTick.visibility = View.GONE
//                    magicTick.visibility = View.GONE
//                    jshineTick.visibility = View.GONE
//                    harveyTick.visibility = View.GONE
//                    gradegreyTick.visibility = View.GONE
//                    eveningTick.visibility = View.GONE
//                }
//            }
//
//        }
//    }