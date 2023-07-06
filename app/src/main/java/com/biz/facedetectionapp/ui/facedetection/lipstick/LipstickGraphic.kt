package com.biz.facedetectionapp.ui.facedetection.lipstick

import android.graphics.*
import androidx.annotation.ColorInt
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour


class LipstickGraphic(
    overlay: LipstickGraphicOverlay,
    private val face: Face,
    private val imageRect: Rect
) : LipstickGraphicOverlay.Graphic(overlay) {

    private val facePositionPaint: Paint

    private val path: Path

    private var firstPx: Float
    private var firstPy: Float

    private var lastPx: Float
    private var lastPy: Float


    init {
        val selectedColor = Color.WHITE

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        path = Path()

        firstPx = 0f
        firstPy = 0f

        lastPx = 0f
        lastPy = 0f

    }

    private fun Canvas.drawFace(facePosition: Int, @ColorInt selectedColor: Int, faceType: String) {

        val contour = face.getContour(facePosition)

        val path = Path()

        contour?.points?.forEachIndexed { index, pointF ->

            if (index == 0) {
                path.moveTo(translateX(pointF.x), translateY(pointF.y))
            }
            path.lineTo(translateX(pointF.x), translateY(pointF.y))
        }

        val paint = Paint().apply {
            color = selectedColor
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }
        drawPath(path, paint)
    }

    private fun drawPoints(face: Face, contourType: Int, canvas: Canvas?, selectedColor: String) {
        val faceContour = face.getContour(contourType)


        faceContour?.points?.forEachIndexed { index, point ->
            val px = translateX(point.x)
            val py = translateY(point.y)

            canvas?.drawCircle(px, py, CIRCLE_RADIUS, facePositionPaint)

            if (contourType == FaceContour.UPPER_LIP_TOP) {
                if (index == 0) {
                    path.moveTo(px, py)
                    firstPx = px
                    firstPy = py
                } else {
                    path.lineTo(px, py)
                }
            } else if (contourType == FaceContour.UPPER_LIP_BOTTOM) {
                path.lineTo(px, py)
            }

            if (contourType == FaceContour.UPPER_LIP_TOP && index == faceContour.points.size - 1) {
                lastPx = px
                lastPy = py
                path.moveTo(firstPx, firstPy)
            }

            if (contourType == FaceContour.UPPER_LIP_BOTTOM && index == faceContour.points.size - 1){
                path.moveTo(lastPx, lastPy)
                path.lineTo(px, py)
            }

        }

//        faceContour?.points?.forEachIndexed { index, point ->
//            val px = translateX(point.x)
//            val py = translateY(point.y)
//            canvas?.drawCircle(px, py, CIRCLE_RADIUS, facePositionPaint)
//
//            if (index == 0) {
//                path.moveTo(px, py)
//            } else {
//                path.lineTo(px, py)
//            }
//
//        }

        val paint = Paint().apply {
            color = Color.parseColor(selectedColor)
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }

        canvas?.drawPath(path, paint)
    }

    override fun draw(canvas: Canvas?) {

        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )

        drawPoints(face, FaceContour.UPPER_LIP_TOP, canvas, "#99a8203f")
        drawPoints(face, FaceContour.UPPER_LIP_BOTTOM, canvas, "#99a8203f")
//        drawPoints(face, FaceContour.LOWER_LIP_TOP, canvas, "#a8203f")
//        drawPoints(face, FaceContour.LOWER_LIP_BOTTOM, canvas, "#a8203f")

    }

    companion object {
        private const val CIRCLE_RADIUS = 4.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }

}