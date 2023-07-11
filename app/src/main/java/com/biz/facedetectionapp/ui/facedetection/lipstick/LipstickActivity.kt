package com.biz.facedetectionapp.ui.facedetection.lipstick

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.biz.facedetectionapp.R
import com.biz.facedetectionapp.databinding.ActivityLipstickBinding
import com.biz.facedetectionapp.ui.facedetection.contour.ContourActivity
import com.biz.facedetectionapp.ui.facedetection.contour.ContourGraphicOverlay
import com.biz.facedetectionapp.ui.facedetection.contour.FaceContourGraphic
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

class LipstickActivity : AppCompatActivity() {

    lateinit var binding: ActivityLipstickBinding

    private lateinit var cameraProvider: ProcessCameraProvider

    private lateinit var graphicOverlay: LipstickGraphicOverlay

    private var lipsColor : Int? = Color.parseColor("#AE2320")

    lateinit var faceGraphic: LipstickGraphic

    private var cameraSelectorOption = CameraSelector.DEFAULT_FRONT_CAMERA

    companion object {
        const val CAMERA_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLipstickBinding.inflate(layoutInflater)
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
        binding.apply {
            cameraChangeBtn.setOnClickListener {
                cameraSelectorOption =
                    if (cameraSelectorOption == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    } else {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }
                graphicOverlay.toggleSelector()
                startCamera()
            }

            scarletColor.setOnClickListener {
                onColorSelect("scarletColor")
            }

            magentaColor.setOnClickListener {
                onColorSelect("magentaColor")
            }

            claycrushColor.setOnClickListener {
                onColorSelect("claycrushColor")
            }

            almondpinkColor.setOnClickListener {
                onColorSelect("almondpinkColor")
            }

            bonappetitbabyColor.setOnClickListener {
                onColorSelect("bonappetitbabyColor")
            }

            breakalegColor.setOnClickListener {
                onColorSelect("breakalegColor")
            }

            itsadateColor.setOnClickListener {
                onColorSelect("itsadateColor")
            }

            naileditColor.setOnClickListener {
                onColorSelect("naileditColor")
            }

            peptalkColor.setOnClickListener {
                onColorSelect("peptalkColor")
            }

            trafficjamminColor.setOnClickListener {
                onColorSelect("trafficjamminColor")
            }

        }
    }

    private fun onColorSelect(gradiantType: String) {
        val visibilityMap = mapOf(
            "scarletColor" to View.VISIBLE,
            "magentaColor" to View.VISIBLE,
            "claycrushColor" to View.VISIBLE,
            "almondpinkColor" to View.VISIBLE,
            "bonappetitbabyColor" to View.VISIBLE,
            "breakalegColor" to View.VISIBLE,
            "itsadateColor" to View.VISIBLE,
            "naileditColor" to View.VISIBLE,
            "peptalkColor" to View.VISIBLE,
            "trafficjamminColor" to View.VISIBLE,
        )

        val tickVisibility = visibilityMap.mapValues { View.GONE }.toMutableMap()
        tickVisibility[gradiantType] = View.VISIBLE

        val colorMap = mapOf(
            "scarletColor" to Color.parseColor("#AE2320"),
            "magentaColor" to Color.parseColor("#E90D53"),
            "claycrushColor" to Color.parseColor("#D56A56"),
            "almondpinkColor" to Color.parseColor("#BF5957"),
            "bonappetitbabyColor" to Color.parseColor("#C4424A"),
            "breakalegColor" to Color.parseColor("#9D4253"),
            "itsadateColor" to Color.parseColor("#70213E"),
            "naileditColor" to Color.parseColor("#672830"),
            "peptalkColor" to Color.parseColor("#D14E76"),
            "trafficjamminColor" to Color.parseColor("#A4516B"),
        )

        lipsColor = colorMap[gradiantType]!!

        binding.apply {
            scarletTick.visibility = tickVisibility["scarletColor"] ?: View.GONE
            magentaTick.visibility = tickVisibility["magentaColor"] ?: View.GONE
            claycrushTick.visibility = tickVisibility["claycrushColor"] ?: View.GONE
            almondpinkTick.visibility = tickVisibility["almondpinkColor"] ?: View.GONE
            bonappetitbabyTick.visibility = tickVisibility["bonappetitbabyColor"] ?: View.GONE
            breakalegTick.visibility = tickVisibility["breakalegColor"] ?: View.GONE
            itsadateTick.visibility = tickVisibility["itsadateColor"] ?: View.GONE
            naileditTick.visibility = tickVisibility["naileditColor"] ?: View.GONE
            peptalkTick.visibility = tickVisibility["peptalkColor"] ?: View.GONE
            trafficjamminTick.visibility = tickVisibility["trafficjamminColor"] ?: View.GONE
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

                faceGraphic = LipstickGraphic(graphicOverlay, it, img.cropRect,lipsColor!!)
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