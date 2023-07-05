package com.biz.facedetectionapp.ui.facedetection.contour

import android.graphics.*
import androidx.annotation.ColorInt
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour

class FaceContourGraphic(overlay: ContourGraphicOverlay, private val face: Face, private val imageRect: Rect) : ContourGraphicOverlay.Graphic(overlay) {

    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    init {
        val selectedColor = Color.WHITE

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor
        idPaint.textSize = ID_TEXT_SIZE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
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

    override fun draw(canvas: Canvas?) {

        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )

//      Draw Box of Face
        canvas?.drawRect(rect, boxPaint)

        val contours = face.allContours

        /**
         * Draw Dot Point on Face and Face's Part
         */
        contours.forEach {
            it.points.forEach { point ->
                val px = translateX(point.x)
                val py = translateY(point.y)
                canvas?.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint)
            }
        }

        // face
        canvas?.drawFace(FaceContour.FACE, Color.parseColor("#BF280FBF"),"FACE")

        // left eye
        canvas?.drawFace(FaceContour.LEFT_EYEBROW_TOP, Color.RED, "LEFT_EYEBROW_TOP")
        canvas?.drawFace(FaceContour.LEFT_EYE, Color.BLACK, "LEFT_EYE")
        canvas?.drawFace(FaceContour.LEFT_EYEBROW_BOTTOM, Color.CYAN, "LEFT_EYEBROW_BOTTOM")

        // right eye
        canvas?.drawFace(FaceContour.RIGHT_EYE, Color.DKGRAY, "RIGHT_EYE")
        canvas?.drawFace(FaceContour.RIGHT_EYEBROW_BOTTOM, Color.GRAY, "RIGHT_EYEBROW_BOTTOM")
        canvas?.drawFace(FaceContour.RIGHT_EYEBROW_TOP, Color.GREEN, "RIGHT_EYEBROW_TOP")

        // nose
        canvas?.drawFace(FaceContour.NOSE_BOTTOM, Color.LTGRAY, "NOSE_BOTTOM")
        canvas?.drawFace(FaceContour.NOSE_BRIDGE, Color.MAGENTA, "NOSE_BRIDGE")

        // rip
        canvas?.drawFace(FaceContour.LOWER_LIP_BOTTOM, Color.WHITE, "LOWER_LIP_BOTTOM")
        canvas?.drawFace(FaceContour.LOWER_LIP_TOP, Color.YELLOW, "LOWER_LIP_TOP")
        canvas?.drawFace(FaceContour.UPPER_LIP_BOTTOM, Color.GREEN, "UPPER_LIP_BOTTOM")
        canvas?.drawFace(FaceContour.UPPER_LIP_TOP, Color.CYAN, "UPPER_LIP_TOP")
    }

    companion object {
        private const val FACE_POSITION_RADIUS = 4.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }

}