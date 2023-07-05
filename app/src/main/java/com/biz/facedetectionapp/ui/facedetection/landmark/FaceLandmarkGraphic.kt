package com.biz.facedetectionapp.ui.facedetection.landmark

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import com.biz.facedetectionapp.R
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark

class FaceLandmarkGraphic(
    overlay: LandMarkGraphicOverlay,
    private val face: Face,
    private val imageRect: Rect,
    val context: Context
) : LandMarkGraphicOverlay.Graphic(overlay) {
    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    private val landmarks: Array<PointF?>
    private val rect: RectF


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

        landmarks = arrayOfNulls(NUM_LANDMARKS)

        rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )

        landmarks[0] = getLandmarkPosition(face, FaceLandmark.LEFT_CHEEK)
        landmarks[1] = getLandmarkPosition(face, FaceLandmark.LEFT_EAR)
        landmarks[2] = getLandmarkPosition(face, FaceLandmark.LEFT_EYE)
        landmarks[3] = getLandmarkPosition(face, FaceLandmark.MOUTH_BOTTOM)
        landmarks[4] = getLandmarkPosition(face, FaceLandmark.MOUTH_LEFT)
        landmarks[5] = getLandmarkPosition(face, FaceLandmark.MOUTH_RIGHT)
        landmarks[6] = getLandmarkPosition(face, FaceLandmark.NOSE_BASE)
        landmarks[7] = getLandmarkPosition(face, FaceLandmark.RIGHT_CHEEK)
        landmarks[8] = getLandmarkPosition(face, FaceLandmark.RIGHT_EAR)
        landmarks[9] = getLandmarkPosition(face, FaceLandmark.RIGHT_EYE)
    }

    override fun draw(canvas: Canvas?) {
        drawFaceBox(canvas!!)
        drawDot(canvas)
    }

    private fun getLandmarkPosition(face: Face, landmarkType: Int): PointF? {
        val landmark = face.getLandmark(landmarkType)
        return if (landmark != null) {
            PointF(translateX(landmark.position.x), translateY(landmark.position.y))
        } else null
    }


    private fun drawDot(canvas: Canvas) {

        for (landmark in landmarks) {

            if (landmark != null) {
                canvas.drawCircle(landmark.x, landmark.y, DOT_RADIUS, facePositionPaint)

//                canvas.drawBitmap(bitmap!!,null,rect,boxPaint)

                if (landmark == landmarks[8] || landmark == landmarks[1]) {
                    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.earrings)
                    val bitmapWidth = bitmap?.width ?: 0
                    val bitmapHeight = bitmap?.height ?: 0

                    // Calculate the position to draw the bitmap
                    val left = landmark.x - bitmapWidth / 2
                    val top = landmark.y - bitmapHeight / 2

                    // Draw the bitmap
                    canvas.drawBitmap(bitmap, left, top, null)
                }

                if (landmark == landmarks[6]){
                    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.joker)
                    val bitmapWidth = bitmap?.width ?: 0
                    val bitmapHeight = bitmap?.height ?: 0

                    // Calculate the position to draw the bitmap
                    val left = landmark.x - bitmapWidth / 2
                    val top = landmark.y - bitmapHeight / 2

                    // Draw the bitmap
                    canvas.drawBitmap(bitmap, left, top, null)
                }
            }
        }
    }

    private fun drawFaceBox(canvas: Canvas) {
        canvas.drawRect(rect, boxPaint)
    }

    companion object {
        private const val DOT_RADIUS = 10.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val BOX_STROKE_WIDTH = 5.0f
        private const val NUM_LANDMARKS = 10
    }

}
