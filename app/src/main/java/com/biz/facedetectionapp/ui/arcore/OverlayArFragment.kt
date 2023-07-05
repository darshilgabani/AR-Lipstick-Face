package com.biz.facedetectionapp.ui.arcore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ar.core.CameraConfig
import com.google.ar.core.CameraConfigFilter
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
//import com.google.ar.sceneform.ux.ArFrontFacingFragment
//import com.gorisse.thomas.sceneform.light.LightEstimationConfig
//import com.gorisse.thomas.sceneform.lightEstimationConfig
import java.util.EnumSet

//class OverlayArFragment : ArFrontFacingFragment() {}

//class OverlayArFragment : ArFragment() {
//
//    override fun onCreateSessionConfig(session: Session?): Config {
//        val filter = CameraConfigFilter(session).setFacingDirection(CameraConfig.FacingDirection.FRONT)
//        val cameraConfig = session?.getSupportedCameraConfigs(filter)?.get(0)
//        if (cameraConfig != null) {
//            session.cameraConfig = cameraConfig
//        }
//
//        val config = super.onCreateSessionConfig(session)
//        config.planeFindingMode = Config.PlaneFindingMode.DISABLED
//        config.augmentedFaceMode = Config.AugmentedFaceMode.MESH3D
//        config.lightEstimationMode = Config.LightEstimationMode.DISABLED
//
//        return config
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        instructionsController.isEnabled = false
//        arSceneView.lightEstimationConfig = LightEstimationConfig.DISABLED
//        arSceneView.planeRenderer.isVisible = false
//        arSceneView.planeRenderer.isEnabled = false
//    }
//
//}


// When Extend ArFragment()
class OverlayArFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {
        val config = Config(session)
        config.augmentedFaceMode = Config.AugmentedFaceMode.MESH3D
        config.updateMode
        arSceneView.setupSession(session)
        return config
    }

    override fun getSessionFeatures(): MutableSet<Session.Feature> {
        return EnumSet.of(Session.Feature.FRONT_CAMERA)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val frameLayout = super.onCreateView(inflater, container, savedInstanceState) as FrameLayout?
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        return frameLayout
    }
}