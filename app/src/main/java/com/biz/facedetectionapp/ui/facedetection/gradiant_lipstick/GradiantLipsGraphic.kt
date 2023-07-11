package com.biz.facedetectionapp.ui.facedetection.gradiant_lipstick

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour

class GradiantLipsGraphic(
    overlay: GradiantLipsGraphicOverlay,
    private val face: Face,
    private val imageRect: Rect,
    private val gradientColors: IntArray
) : GradiantLipsGraphicOverlay.Graphic(overlay) {
    data class PointsDataClass(val px: Float, val py: Float)

    private val upperLipPath: Path = Path()
    private val bottomLipPath: Path = Path()

    private val lipsColor: Int = Color.parseColor("#a41313")
    private val circleColor: Int = Color.WHITE

//    private val gradientColors = intArrayOf(Color.parseColor("#A84441"), Color.parseColor("#41A84B"))
//    private val gradientColors = intArrayOf(Color.BLACK, Color.WHITE,Color.RED)

    private val lipsPaint: Paint = Paint().apply {

//        shader = LinearGradient(
//            upperLipFirstUtPx,
//            upperLipFirstUtPy,
//            bottomLipFirstLbPx,
//            bottomLipFirstLbPy,
//            gradientColors,
//            null,
//            Shader.TileMode.MIRROR
//        )
//        color = lipsColor

        style = Paint.Style.FILL
        strokeWidth = BOX_STROKE_WIDTH
    }

    private val circlePaint: Paint = Paint().apply {
        color = circleColor
    }

    private var upperLipFirstUtPx: Float = 0f
    private var upperLipFirstUtPy: Float = 0f

    private var bottomLipFirstLbPx: Float = 0f
    private var bottomLipFirstLbPy: Float = 0f

    private var bottomLipLastLbPx: Float = 0f
    private var bottomLipLastLbPy: Float = 0f

    private var bottomMiddleLtPx: Float = 0f
    private var bottomMiddleLtPy: Float = 0f

    private var bottomMiddleLbPx: Float = 0f
    private var bottomMiddleLbPy: Float = 0f

    private var upperLastUtPx: Float = 0f
    private var upperLastUtPy: Float = 0f

    private var upperMiddleUtPx: Float = 0f
    private var upperMiddleUtPy: Float = 0f

    private var upperMiddleUbPx: Float = 0f
    private var upperMiddleUbPy: Float = 0f

    private val upperLipArrayList: ArrayList<PointsDataClass> = ArrayList()
    private val bottomLipArrayList: ArrayList<PointsDataClass> = ArrayList()

    private fun getUpperLipsPoints(
        face: Face,
        contourType: Int,
    ): Boolean {

        val faceContour = face.getContour(contourType)

        var completed = false

        faceContour?.points?.forEachIndexed { index, point ->

            val px = translateX(point.x)
            val py = translateY(point.y)

//            canvas?.drawCircle(px, py, CIRCLE_RADIUS, circlePaint)

            if (contourType == FaceContour.UPPER_LIP_TOP) {
                val pointsDataClass = PointsDataClass(px, py)
                upperLipArrayList.add(pointsDataClass)
            } else {
                val pointsDataClass = PointsDataClass(px, py)
                upperLipArrayList.add(pointsDataClass)
            }

            if (contourType == FaceContour.UPPER_LIP_TOP && index == faceContour.points.size - 1) {
                upperLipArrayList.add(PointsDataClass(upperLipFirstUtPx, upperLipFirstUtPy))
                upperLastUtPx = px
                upperLastUtPy = py
            }

            if (contourType == FaceContour.UPPER_LIP_BOTTOM && index == faceContour.points.size - 1) {
                upperLipArrayList.add(PointsDataClass(upperLastUtPx, upperLastUtPy))
                completed = true
            }

            if (contourType == FaceContour.UPPER_LIP_TOP && index == 0) {
                upperLipFirstUtPx = px
                upperLipFirstUtPy = py
            }

            if (contourType == FaceContour.UPPER_LIP_TOP && index == 5) {
                upperMiddleUtPx = px
                upperMiddleUtPy = py
            }

            if (contourType == FaceContour.UPPER_LIP_BOTTOM && index == 4) {
                upperMiddleUbPx = px
                upperMiddleUbPy = py
            }

        }

        return completed
    }

    private fun getBottomLipsPoints(face: Face, contourType: Int): Boolean {

        val faceContour = face.getContour(contourType)

        var completed = false

        faceContour?.points?.forEachIndexed { index, point ->

            val px = translateX(point.x)
            val py = translateY(point.y)

//            canvas?.drawCircle(px, py, CIRCLE_RADIUS, circlePaint)

            if (contourType == FaceContour.LOWER_LIP_BOTTOM) {
                val pointsDataClass = PointsDataClass(px, py)
                bottomLipArrayList.add(pointsDataClass)
            } else {
                val pointsDataClass = PointsDataClass(px, py)
                bottomLipArrayList.add(pointsDataClass)
            }

            if (contourType == FaceContour.LOWER_LIP_BOTTOM && index == 0) {
                bottomLipFirstLbPx = px
                bottomLipFirstLbPy = py
            }

            if (contourType == FaceContour.LOWER_LIP_BOTTOM && index == faceContour.points.size - 1) {
                bottomLipArrayList.add(PointsDataClass(bottomLipFirstLbPx, bottomLipFirstLbPy))
                bottomLipLastLbPx = px
                bottomLipLastLbPy = py
            }

            if (contourType == FaceContour.LOWER_LIP_TOP && index == faceContour.points.size - 1) {
                bottomLipArrayList.add(PointsDataClass(bottomLipLastLbPx, bottomLipLastLbPy))
                completed = true
            }

            if (contourType == FaceContour.LOWER_LIP_TOP && index == 4) {
                bottomMiddleLtPx = px
                bottomMiddleLtPy = py
            }

            if (contourType == FaceContour.LOWER_LIP_BOTTOM && index == 4) {
                bottomMiddleLbPx = px
                bottomMiddleLbPy = py
            }

        }

        return completed
    }

    override fun draw(canvas: Canvas?) {

        calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )

        getUpperLipsPoints(face, FaceContour.UPPER_LIP_TOP)
        if (getUpperLipsPoints(face, FaceContour.UPPER_LIP_BOTTOM)) {
            drawUpperLip(canvas)
        }

        getBottomLipsPoints(face, FaceContour.LOWER_LIP_BOTTOM)
        if (getBottomLipsPoints(face, FaceContour.LOWER_LIP_TOP)) {
            drawBottomLip(canvas)
        }

    }

    private fun drawBottomLip(canvas: Canvas?) {

        val px = (bottomMiddleLtPx + bottomMiddleLbPx) / 2
        val py = (bottomMiddleLtPy + bottomMiddleLbPy) / 2

        bottomLipArrayList.forEachIndexed { index, p ->

            if (index == 0) {
                bottomLipPath.moveTo(px, py)
                bottomLipPath.lineTo(p.px, p.py)
            } else if (p.px == bottomLipLastLbPx && p.py == bottomLipLastLbPy && index != (bottomLipArrayList.size - 1)) {
                bottomLipPath.lineTo(p.px, p.py)
                bottomLipPath.moveTo(px, py)
            } else {
                bottomLipPath.lineTo(p.px, p.py)
            }

            if (index == bottomLipArrayList.size - 1) {
                bottomLipPath.moveTo(px, py)
                bottomLipPath.close()
            }

        }

        lipsPaint.apply {
            shader = LinearGradient(
                bottomLipFirstLbPx,
                0f,
                bottomLipLastLbPx,
                0f,
                gradientColors,
                null,
                Shader.TileMode.MIRROR
            )
        }

        canvas?.drawPath(bottomLipPath, lipsPaint)
    }


    private fun drawUpperLip(canvas: Canvas?) {

        val px = (upperMiddleUtPx + upperMiddleUbPx) / 2
        val py = (upperMiddleUtPy + upperMiddleUbPy) / 2

        upperLipArrayList.forEachIndexed { index, p ->

            if (index == 0) {
                upperLipPath.moveTo(px, py)
                upperLipPath.lineTo(p.px, p.py)
            } else if (p.px == upperLastUtPx && p.py == upperLastUtPy && index != (upperLipArrayList.size - 1)) {
                upperLipPath.lineTo(p.px, p.py)
                upperLipPath.moveTo(px, py)
            } else {
                upperLipPath.lineTo(p.px, p.py)
            }

            if (index == upperLipArrayList.size - 1) {
                upperLipPath.moveTo(px, py)
                upperLipPath.close()
            }

        }

        lipsPaint.apply {
            shader = LinearGradient(
                upperLastUtPx,
                0f,
                upperLipFirstUtPx,
                0f,
                gradientColors,
                null,
                Shader.TileMode.MIRROR
            )
        }

        canvas?.drawPath(upperLipPath, lipsPaint)
    }

    companion object {
        private const val CIRCLE_RADIUS = 4.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }

}

//        path.fillType = Path.FillType.INVERSE_WINDING


