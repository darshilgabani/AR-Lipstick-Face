package com.biz.facedetectionapp.ui.facemeshdetection

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.CameraSelector
import kotlin.math.ceil

open class GraphicOverlay(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val graphics: MutableList<Graphic> = ArrayList()

    var mScale: Float? = null
    var mOffsetX: Float? = null
    var mOffsetY: Float? = null

    var cameraSelector: Int = CameraSelector.LENS_FACING_FRONT

    abstract class Graphic(private val overlay: GraphicOverlay) {

        abstract fun draw(canvas: Canvas?)

        open fun calculateRect(height: Float, width: Float, boundingBoxT: Rect): RectF {

            // for land scape
            fun isLandScapeMode(): Boolean {
                return overlay.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            }

            fun whenLandScapeModeWidth(): Float {
                return when (isLandScapeMode()) {
                    true -> width
                    false -> height
                }
            }

            fun whenLandScapeModeHeight(): Float {
                return when (isLandScapeMode()) {
                    true -> height
                    false -> width
                }
            }

            val scaleX = overlay.width.toFloat() / whenLandScapeModeWidth()
            val scaleY = overlay.height.toFloat() / whenLandScapeModeHeight()
            val scale = scaleX.coerceAtLeast(scaleY)
            overlay.mScale = scale

            // Calculate offset (we need to center the overlay on the target)
            val offsetX = (overlay.width.toFloat() - ceil(whenLandScapeModeWidth() * scale)) / 2.0f
            val offsetY = (overlay.height.toFloat() - ceil(whenLandScapeModeHeight() * scale)) / 2.0f

            overlay.mOffsetX = offsetX
            overlay.mOffsetY = offsetY

            val mappedBox = RectF().apply {
                left = boundingBoxT.right * scale + offsetX
                top = boundingBoxT.top * scale + offsetY
                right = boundingBoxT.left * scale + offsetX
                bottom = boundingBoxT.bottom * scale + offsetY
            }

            // for front mode
            if (overlay.isFrontMode()) {
                val centerX = overlay.width.toFloat() / 2
                mappedBox.apply {
                    left = centerX + (centerX - left)
                    right = centerX - (right - centerX)
                }
            }

            return mappedBox
        }

        fun translateX(horizontal: Float): Float {
            return if (overlay.mScale != null && overlay.mOffsetX != null && !overlay.isFrontMode()) {
                (horizontal * overlay.mScale!!) + overlay.mOffsetX!!
            } else if (overlay.mScale != null && overlay.mOffsetX != null && overlay.isFrontMode()) {
                val centerX = overlay.width.toFloat() / 2
                centerX - ((horizontal * overlay.mScale!!) + overlay.mOffsetX!! - centerX)
            } else {
                horizontal
            }
        }

        fun translateY(vertical: Float): Float {
            return if (overlay.mScale != null && overlay.mOffsetY != null) {
                (vertical * overlay.mScale!!) + overlay.mOffsetY!!
            } else {
                vertical
            }
        }

    }

    fun isFrontMode() = cameraSelector == CameraSelector.LENS_FACING_FRONT

    fun toggleSelector() {
        cameraSelector =
            if (cameraSelector == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
    }

    fun clear() {
            graphics.clear()
        postInvalidate()
    }

    fun add(graphic: Graphic) {
            graphics.add(graphic)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
            graphics.forEach {
                it.draw(canvas)
            }
    }

}