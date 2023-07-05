package com.biz.facedetectionapp.ui.arcore.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.biz.facedetectionapp.R
import com.biz.facedetectionapp.databinding.ActivityArLandMarkBinding
import com.biz.facedetectionapp.ui.arcore.CustomFaceNode
import com.biz.facedetectionapp.ui.arcore.OverlayArFragment
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.rendering.Renderable

class ARLandMarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArLandMarkBinding

    private lateinit var customArFragment: OverlayArFragment
    private var faceNodeMap = HashMap<AugmentedFace, CustomFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArLandMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVar()

        val sceneView = customArFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener {
            sceneView.session
                ?.getAllTrackables(AugmentedFace::class.java)?.let {
                    for (f in it) {
                        if (!faceNodeMap.containsKey(f)) {
                            val faceNode = CustomFaceNode(f, this)
                            faceNode.setParent(scene)
                            faceNodeMap[f] = faceNode
                        }
                    }
                    // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
                    val iter = faceNodeMap.entries.iterator()
                    while (iter.hasNext()) {
                        val entry = iter.next()
                        when (it.size) {
                            0 -> {
                                val faceNode = entry.value
                                faceNode.setParent(null)
                                iter.remove()
                            }
                        }

//                        val face = entry.key
//                        if (face.trackingState == TrackingState.STOPPED) {
//                            val faceNode = entry.value
//                            faceNode.setParent(null)
//                            iter.remove()
//                        }

                    }
                }
        }

    }

    private fun initVar() {
        customArFragment =
            (supportFragmentManager.findFragmentById(R.id.arFragment) as OverlayArFragment?)!!
        customArFragment.arSceneView?.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
    }
}