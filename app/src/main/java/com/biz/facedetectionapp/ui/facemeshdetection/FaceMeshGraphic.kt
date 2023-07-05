package com.biz.facedetectionapp.ui.facemeshdetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshPoint

class FaceMeshGraphic(overlay: GraphicOverlay, private val faceMesh: FaceMesh, private val imageRect: Rect) :
    GraphicOverlay.Graphic(overlay) {

    private val positionPaint: Paint
    private val boxPaint: Paint

    companion object {
        private const val DOT_RADIUS = 8.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }

    init {
        val selectedColor = Color.WHITE
        positionPaint = Paint()
        positionPaint.color = selectedColor
        positionPaint.style = Paint.Style.FILL_AND_STROKE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    private val faceMeshList =
        intArrayOf(
            FaceMesh.FACE_OVAL,
            FaceMesh.LEFT_EYEBROW_TOP,
            FaceMesh.LEFT_EYEBROW_BOTTOM,
            FaceMesh.RIGHT_EYEBROW_TOP,
            FaceMesh.RIGHT_EYEBROW_BOTTOM,
            FaceMesh.LEFT_EYE,
            FaceMesh.RIGHT_EYE,
            FaceMesh.UPPER_LIP_TOP,
            FaceMesh.UPPER_LIP_BOTTOM,
            FaceMesh.LOWER_LIP_TOP,
            FaceMesh.LOWER_LIP_BOTTOM,
            FaceMesh.NOSE_BRIDGE
        )

    override fun draw(canvas: Canvas?) {

        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            faceMesh.boundingBox
        )

//        canvas?.drawRect(rect, boxPaint)

        // Draw face mesh
        val points = getContourPoints(faceMesh)
        val triangles = faceMesh.allTriangles

        triangles.forEach {faceMeshTriangle->

                val point1 = faceMeshTriangle.allPoints[0].position
                val point2 = faceMeshTriangle.allPoints[1].position
                val point3 = faceMeshTriangle.allPoints[2].position

                drawLine(canvas!!, point1, point2)
                drawLine(canvas, point1, point3)
                drawLine(canvas, point2, point3)

        }

//        points.forEach {
//            canvas?.drawCircle(
//                translateX(it.position.x),
//                translateY(it.position.y),
//                DOT_RADIUS,
//                positionPaint)
//        }

        faceMesh.allPoints.forEach {
           canvas?.drawCircle(translateX(it.position.x),translateY(it.position.y),DOT_RADIUS, positionPaint)
        }

    }

    private fun drawLine(canvas: Canvas, point1: PointF3D, point2: PointF3D) {
        canvas.drawLine(
            translateX(point1.x),
            translateY(point1.y),
            translateX(point2.x),
            translateY(point2.y),
            positionPaint
        )
    }

    private fun getContourPoints(faceMesh: FaceMesh): List<FaceMeshPoint> {
        val contourPoints: MutableList<FaceMeshPoint> = ArrayList()
        faceMeshList.forEach {type->
            contourPoints.addAll(faceMesh.getPoints(type))
        }
        return contourPoints
    }

}