package com.biz.facedetectionapp.ui.arcore.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.biz.facedetectionapp.R
import com.biz.facedetectionapp.databinding.ActivityOverlayBinding
import com.biz.facedetectionapp.ui.arcore.OverlayArFragment
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.AugmentedFaceNode
import java.util.ArrayList


class OverlayActivity : AppCompatActivity() {

    lateinit var binding: ActivityOverlayBinding

    private var modelrenderable: ModelRenderable? = null
    private var viewrenderable: ViewRenderable? = null
    private var faceMeshTexture: Texture? = null

    lateinit var augmentedFaceNode: AugmentedFaceNode
    private var glasses: ArrayList<ModelRenderable> = ArrayList()
    private var index: Int = 0
    private var changeModel: Boolean = false

    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    private lateinit var customArFragment: OverlayArFragment

    private var isAdded = false

    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVar()

        onClick()

        loadModels()

        customArFragment.arSceneView?.scene?.addOnUpdateListener {

            val frame = customArFragment.arSceneView.arFrame
            val augmentedFaces = frame!!.getUpdatedTrackables(AugmentedFace::class.java)

            for (augmentedFace in augmentedFaces) {
//                    if (isAdded) return@addOnUpdateListener

                val p = augmentedFace.centerPose
                val p1 = augmentedFace.getRegionPose(AugmentedFace.RegionType.NOSE_TIP)
                val p2 = augmentedFace.getRegionPose(AugmentedFace.RegionType.FOREHEAD_RIGHT)
                val p3 = augmentedFace.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT)

                if (!faceNodeMap.containsKey(augmentedFace)) {
                    if (augmentedFace.trackingState == TrackingState.TRACKING) {

                        augmentedFaceNode = AugmentedFaceNode(augmentedFace)

//                    augmentedFaceNode.parent = customArFragment.arSceneView.scene
                        augmentedFaceNode.setParent(customArFragment.arSceneView.scene)

//                            augmentedFaceNode.faceRegionsRenderable = modelrenderable


                        when (type) {
                            "3D_Object" -> {
                                augmentedFaceNode.faceRegionsRenderable = modelrenderable
                            }

                            "JPG_Image" -> {
                                augmentedFaceNode.faceMeshTexture = faceMeshTexture
                            }

                            "Custom_Layout" -> {
                                val lightBulb = Node()
                                val localPosition = Vector3()
                                localPosition.set(0.0f, 0.17f, 0.0f)
                                lightBulb.localPosition = localPosition
                                lightBulb.setParent(augmentedFaceNode)
                                lightBulb.renderable = viewrenderable
                            }
                        }

                        faceNodeMap[augmentedFace] = augmentedFaceNode
//                            isAdded = true

                    }
                } else if (changeModel) {
                    faceNodeMap.getValue(augmentedFace).faceRegionsRenderable = modelrenderable
                }
            }

            changeModel = false

            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
            val iter = faceNodeMap.entries.iterator()
            while (iter.hasNext()) {
                val entry = iter.next()
                when (augmentedFaces.size) {
                    0 -> {
                        val faceNode = entry.value
                        faceNode.setParent(null)
                        iter.remove()
                    }
                }

//                val face = entry.key
//                if (face.trackingState == TrackingState.STOPPED) {
//                    val faceNode = entry.value
//                    faceNode.setParent(null)
//                    iter.remove()
//                }
            }

        }
    }

    private fun loadModels() {
        ModelRenderable.builder()
            .setSource(this, Uri.parse("green_sunglasses.sfb"))
            .build()
            .thenAccept {
                glasses.add(it)
                modelrenderable = it
            }

        ModelRenderable.builder()
            .setSource(this, Uri.parse("fox_face.sfb"))
            .build()
            .thenAccept {
                glasses.add(it)
            }

//        ModelRenderable.builder()
////                .setSource(this, R.raw.specses)
////                .setIsFilamentGltf(true)
//            .setSource(this, Uri.parse("green_sunglasses.sfb"))
//            .build()
//            .thenAccept {
//                glasses.add(it)
//                modelrenderable = it
////                augmentedFaceNode.faceRegionsRenderable = modelrenderable
//            }

        ModelRenderable.builder()
            .setSource(this, Uri.parse("yellow_sunglasses.sfb"))
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, Uri.parse("brown_sunglasses.sfb"))
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, Uri.parse("modified_specs.sfb"))
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, Uri.parse("offwhite_sunglasses.sfb"))
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, Uri.parse("strawberry.sfb"))
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, Uri.parse("tophat.sfb"))
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        Texture.builder()
            .setSource(this, R.drawable.lips_makeup)
            .build()
            .thenAccept { texture ->
                faceMeshTexture = texture
            }

        ViewRenderable.builder().setView(this, R.layout.idea_view)
            .build()
            .thenAccept {
                viewrenderable = it
            }
    }

    private fun onClick() {
        binding.changeButton.setOnClickListener {
            changeModel = !changeModel
            index++
            if (index > glasses.size - 1) {
                index = 0
            }
            modelrenderable = glasses[index]
        }
    }

    private fun initVar() {
        customArFragment =
            (supportFragmentManager.findFragmentById(R.id.arFragment) as OverlayArFragment?)!!
        customArFragment.arSceneView?.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        type = intent.getStringExtra("type")
    }
}