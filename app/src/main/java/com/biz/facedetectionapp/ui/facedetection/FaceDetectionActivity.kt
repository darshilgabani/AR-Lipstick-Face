package com.biz.facedetectionapp.ui.facedetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.biz.facedetectionapp.R
import com.biz.facedetectionapp.databinding.ActivityFaceDetectionBinding
import com.biz.facedetectionapp.ui.facedetection.contour.ContourActivity
import com.biz.facedetectionapp.ui.facedetection.landmark.LandMarkActivity
import com.biz.facedetectionapp.ui.facedetection.lipstick.LipstickActivity

class FaceDetectionActivity : AppCompatActivity() {

    lateinit var binding: ActivityFaceDetectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClick()

    }

    private fun onClick() {
        binding.apply {
            contourButton.setOnClickListener {
                startActivity(Intent(this@FaceDetectionActivity, ContourActivity::class.java))
            }

            landMarkButton.setOnClickListener {
                startActivity(Intent(this@FaceDetectionActivity, LandMarkActivity::class.java))
            }

            lipstickButton.setOnClickListener {
                startActivity(Intent(this@FaceDetectionActivity, LipstickActivity::class.java))
            }
        }
    }
}