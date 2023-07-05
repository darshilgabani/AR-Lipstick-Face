package com.biz.facedetectionapp.ui.arcore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.biz.facedetectionapp.databinding.ActivityArcoreBinding

class ARCoreActivity : AppCompatActivity() {

    lateinit var binding: ActivityArcoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArcoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClick()
    }

    private fun onClick() {
        binding.apply {
            threeDObjectBtn.setOnClickListener {
                startActivity(
                    Intent(
                        this@ARCoreActivity,
                        OverlayActivity::class.java
                    ).putExtra("type", "3D_Object")
                )
            }

            imageBtn.setOnClickListener {
                startActivity(
                    Intent(
                        this@ARCoreActivity,
                        OverlayActivity::class.java
                    ).putExtra("type", "JPG_Image")
                )
            }

            layoutBtn.setOnClickListener {
                startActivity(
                    Intent(
                        this@ARCoreActivity,
                        OverlayActivity::class.java
                    ).putExtra("type", "Custom_Layout")
                )
            }

            landMarkBtn.setOnClickListener {
                startActivity(
                    Intent(
                        this@ARCoreActivity,
                        ARLandMarkActivity::class.java
                    )
                )
            }

        }
    }
}