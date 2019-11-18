package com.joao.madureira.graph.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.joao.madureira.graph.data.model.DataPoint

class GraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val axisPaint by lazy {
        Paint().apply {
            strokeWidth = 5f
            color = Color.GRAY
        }
    }

    private val circlePaint by lazy {
        Paint().apply {
            color = Color.GRAY
        }
    }

    private val linesPathPaint by lazy {
        Paint().apply {
            strokeWidth = 6f
            color = Color.GREEN
            style = Paint.Style.STROKE
        }
    }

    private val gradientPathPaint by lazy {
        Paint().apply {
            shader = LinearGradient(0f, 0f, 0f, height.toFloat(), Color.GREEN, Color.WHITE, Shader.TileMode.CLAMP)
        }
    }

    private var dataList = listOf<DataPoint>()
    private val conPoint1 = mutableListOf<PointF>()
    private val conPoint2 = mutableListOf<PointF>()

    fun setData(list: List<DataPoint>) {
        dataList = list
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val pointsList = getGraphPoints()

        canvas?.apply {
            drawAxis(this)
            drawPaths(this, pointsList)
            //drawCircles(this, pointsList)
        }

    }

    private fun drawAxis(canvas: Canvas) {
        canvas.apply {
            drawLine(
                0f + paddingBottom,
                height.toFloat() - paddingBottom,
                0f + paddingBottom,
                0f + paddingTop,
                axisPaint
            )
            drawLine(
                0f + paddingBottom,
                height.toFloat() - paddingBottom,
                width.toFloat() - paddingEnd,
                height.toFloat() - paddingBottom,
                axisPaint
            )
        }
    }

    private fun getGraphPoints(): List<GraphPoints> {
        val graphPointsList = mutableListOf<GraphPoints>()

        val startX = 0f + paddingBottom
        val startY = height.toFloat() - paddingBottom
        val endRight = width.toFloat() - paddingEnd
        val endTop = 0f + paddingTop

        if (dataList.isNotEmpty()) {

            //adds the graph origin to the points list
            graphPointsList.add(GraphPoints(startX, startY))

            val stepX = (endRight - startX) / dataList.size
            dataList.forEachIndexed { index, dataPoint ->
                val xValue = startX + (index + 1) * stepX
                val yValue = (dataPoint.value * (startY - endTop) / 100) + endTop

                graphPointsList.add(GraphPoints(xValue, yValue))
            }
        }

        return graphPointsList
    }

    private fun drawPaths(canvas: Canvas, pointsList: List<GraphPoints>) {

        val startX = 0f + paddingBottom
        val startY = height.toFloat() - paddingBottom
        val endRight = width.toFloat() - paddingEnd

        calculateConnectionPointsForBezierCurve(pointsList)

        val pathLines = Path()
        val pathGradients = Path()
        pathLines.moveTo(startX, startY)
        pathGradients.moveTo(startX, startY)

        for (i in 1 until pointsList.size) {
            pathLines.cubicTo(
                conPoint1[i -1].x, conPoint1[i-1].y, conPoint2[i - 1].x, conPoint2[i - 1].y,
                pointsList[i].xValue, pointsList[i].yValue
            )
        }

        canvas.drawPath(pathLines, linesPathPaint)

        // we need to close the gradient path
        pathLines.lineTo(endRight, startY)
        pathLines.close()

        canvas.drawPath(pathLines, gradientPathPaint)
    }

    private fun calculateConnectionPointsForBezierCurve(pointsList: List<GraphPoints>) {
        try {
            for (i in 1 until pointsList.size) {
                conPoint1.add(PointF((pointsList[i].xValue + pointsList[i - 1].xValue) / 2, pointsList[i - 1].yValue))
                conPoint2.add(PointF((pointsList[i].xValue + pointsList[i - 1].xValue) / 2, pointsList[i].yValue))
            }
        } catch (e: Exception) {
        }
    }

    private fun drawCircles(canvas: Canvas, pointsList: List<GraphPoints>) {
        pointsList.drop(1).forEach {
            canvas.drawCircle(it.xValue, it.yValue, 10f, circlePaint)
        }
    }
}

data class GraphPoints(val xValue: Float, val yValue: Float)