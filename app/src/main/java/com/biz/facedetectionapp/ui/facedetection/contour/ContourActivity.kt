package com.biz.facedetectionapp.ui.facedetection.contour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.biz.facedetectionapp.databinding.ActivityContourBinding
import java.util.concurrent.Executors

class ContourActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContourBinding

    private lateinit var cameraProvider: ProcessCameraProvider

    private lateinit var graphicOverlay: ContourGraphicOverlay

    private var cameraSelectorOption = CameraSelector.DEFAULT_FRONT_CAMERA

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContourBinding.inflate(layoutInflater)
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

            faces.forEach {faces ->

//                Log.d("@@@_Smile",faces.smilingProbability.toString())
//                Log.d("@@@_RightEye",faces.rightEyeOpenProbability.toString())
                Log.d("@@@_LeftEye", faces.leftEyeOpenProbability.toString())

                val faceGraphic = FaceContourGraphic(graphicOverlay, faces, img.cropRect)
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


//            val sourceBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.makeup)

//            val paint = Paint()
//            val faceBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
//        val overlayBitmap = Bitmap.createBitmap(sourceBitmap.width, sourceBitmap.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(faceBitmap)
//        canvas.drawBitmap(sourceBitmap, 0f, 0f, null)

//            for (face in faces) {
//                val faceContour = face.getContour(FaceContour.FACE)
//                val lowerLipContour = face.getContour(FaceContour.LOWER_LIP_TOP)
//
//                LipShapeView(this,null)
////                binding.faceShapeView.updateFaceContour(faceContour)
//
////                val canvas = Canvas(faceBitmap)
////                val myPaint = Paint(Paint.ANTI_ALIAS_FLAG)
////                myPaint.color = Color.parseColor("#99259944")
////
////                val path = Path()
////                path.moveTo(upperLipContour!!.points[0].x, upperLipContour.points[0].y)
////                upperLipContour.points.forEach {
////                    path.lineTo(it.x, it.y)
////                }
////                path.close()
////
////                canvas.drawPath(path, paint)
////                binding.imageView.setImageBitmap(faceBitmap)
//
//
//
////                val overlayX = facePoint?.get(0)?.x
////                val overlayY = facePoint?.get(0)?.y
////
////                val boundingBox = face.boundingBox
////                val faceWidth = boundingBox.width()
////                val faceHeight = boundingBox.height()
////                runOnUiThread {
////
////                val scaledOverlayBitmap = Bitmap.createScaledBitmap(overlayBitmap, faceWidth, faceHeight, false)
////
////                canvas.drawBitmap(scaledOverlayBitmap, overlayX!!, overlayY!!, paint)
////
////                canvas.drawBitmap(scaledOverlayBitmap, overlayX - faceWidth / 2, overlayY - faceHeight / 2, paint)
////
////                }
//
//
//
//
//
//
////                if (noseRegion != null) {
////
////                    val noseBitmap = BitmapFactory.decodeResource(resources, R.drawable.makeup)
////
////                    val overlayBitmap = Bitmap.createBitmap(inputImage.width, inputImage.height, Bitmap.Config.ARGB_8888)
////                    val canvas = Canvas(overlayBitmap)
////
////                    val noseWidth = face.boundingBox.width()
////                    val noseHeight = face.boundingBox.height()
////                    val left = noseRegion.x - (noseWidth / 2)
////                    val top = noseRegion.y - (noseHeight / 2)
////                    val right = left + noseWidth
////                    val bottom = top + noseHeight
////
////                    val leftEarRect = Rect(left.toInt(), top.toInt(), right.toInt(), left.toInt())
////                    canvas.drawBitmap(noseBitmap, null, leftEarRect, null)
////
//////                    val scaledNoseBitmap = Bitmap.createScaledBitmap(noseBitmap, noseWidth, noseHeight, true)
////
//////                    canvas.drawBitmap(scaledNoseBitmap, left, top, null)
////
////                    val combinedBitmap = Bitmap.createBitmap(inputImage.width, inputImage.height, Bitmap.Config.ARGB_8888)
////                    val combinedCanvas = Canvas(combinedBitmap)
////
////                    combinedCanvas.drawBitmap(inputImage.bitmapInternal!!, 0f, 0f, null)
////
////                    combinedCanvas.drawBitmap(overlayBitmap, 0f, 0f, null)
////
////                    runOnUiThread {
////                        // Update the UI with the combined bitmap
////                        // Set the combined bitmap to an ImageView or similar view in your layout
////                    }
////                }
//            }


//class MainActivity : AppCompatActivity() {
//    lateinit var binding: ActivityMainBinding
//
//    lateinit var cameraSelector : CameraSelector
//    lateinit var processCameraProvider: ProcessCameraProvider
//    lateinit var cameraPreview: Preview
//    lateinit var imageAnalysis: ImageAnalysis
//
//    var cameraxViewModel = viewModels<CameraxViewModel>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
//
//        cameraxViewModel.value.processCameraProvider.observe(this) {provider->
//            processCameraProvider = provider
//
//        }
//
//    }
//
//
//    private fun detectFaces(image: InputImage) {
//        // [START set_detector_options]
//        val options = FaceDetectorOptions.Builder()
//            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//            .setMinFaceSize(0.15f)
//            .enableTracking()
//            .build()
//        // [END set_detector_options]
//
//        // [START get_detector]
//        val detector = FaceDetection.getClient(options)
//        // Or, to use the default option:
//        // val detector = FaceDetection.getClient();
//        // [END get_detector]
//
//        // [START run_detector]
//        val result = detector.process(image)
//            .addOnSuccessListener { faces ->
//                // Task completed successfully
//                // [START_EXCLUDE]
//                // [START get_face_info]
//                for (face in faces) {
//                    val bounds = face.boundingBox
//                    val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
//                    val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
//
//                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
//                    // nose available):
//                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
//                    leftEar?.let {
//                        val leftEarPos = leftEar.position
//                    }
//
//                    // If classification was enabled:
//                    if (face.smilingProbability != null) {
//                        val smileProb = face.smilingProbability
//                    }
//                    if (face.rightEyeOpenProbability != null) {
//                        val rightEyeOpenProb = face.rightEyeOpenProbability
//                    }
//
//                    // If face tracking was enabled:
//                    if (face.trackingId != null) {
//                        val id = face.trackingId
//                    }
//                }
//                // [END get_face_info]
//                // [END_EXCLUDE]
//            }
//            .addOnFailureListener { e ->
//                // Task failed with an exception
//                // ...
//            }
//        // [END run_detector]
//    }

//}