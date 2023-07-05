package com.biz.facedetectionapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.biz.facedetectionapp.databinding.ActivityMainBinding
import com.biz.facedetectionapp.ui.arcore.activity.ARCoreActivity
import com.biz.facedetectionapp.ui.facedetection.FaceDetectionActivity
import com.biz.facedetectionapp.ui.facemeshdetection.FaceMeshDetectionActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClick()

    }

    private fun onClick() {
        binding.apply {

            arCoreBtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, ARCoreActivity::class.java))
            }

            faceDetectionBtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, FaceDetectionActivity::class.java))
            }

            faceMeshDetectionBtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, FaceMeshDetectionActivity::class.java))
            }

        }
    }
}