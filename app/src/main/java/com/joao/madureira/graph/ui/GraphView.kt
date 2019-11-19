package com.joao.madureira.graph.ui

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.joao.madureira.graph.data.model.DataPoint
import kotlin.math.abs

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

    private val textPaint by lazy {
        TextPaint().apply {
            color = Color.GRAY
            textSize = 30f
        }
    }

    private var dataList = listOf<DataPoint>()
    private var graphPointsList = listOf<GraphPoint>()
    private var currentPoint: GraphPoint? = null
    private val conPoint1 = mutableListOf<PointF>()
    private val conPoint2 = mutableListOf<PointF>()

    fun setData(list: List<DataPoint>) {
        dataList = list
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        graphPointsList = getGraphPoints()

        canvas?.apply {
            drawAxis(this)
            drawPaths(this, graphPointsList)
            drawTouchPoint(this)
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

    private fun getGraphPoints(): List<GraphPoint> {
        val graphPointsList = mutableListOf<GraphPoint>()

        val startX = 0f + paddingBottom
        val startY = height.toFloat() - paddingBottom
        val endRight = width.toFloat() - paddingEnd
        val endTop = 0f + paddingTop

        if (dataList.isNotEmpty()) {

            //adds the graph origin to the points list
            graphPointsList.add(GraphPoint(startX, startY, 0))

            val stepX = (endRight - startX) / dataList.size
            dataList.forEachIndexed { index, dataPoint ->
                val xValue = startX + (index + 1) * stepX
                val yValue = ((100 - dataPoint.value) * (startY - endTop) / 100) + endTop

                graphPointsList.add(GraphPoint(xValue, yValue, dataPoint.value))
            }
        }

        return graphPointsList
    }

    private fun drawPaths(canvas: Canvas, pointList: List<GraphPoint>) {

        val startX = 0f + paddingBottom
        val startY = height.toFloat() - paddingBottom
        val endRight = width.toFloat() - paddingEnd

        calculateConnectionPointsForBezierCurve(pointList)

        val pathLines = Path()
        val pathGradients = Path()
        pathLines.moveTo(startX, startY)
        pathGradients.moveTo(startX, startY)

        for (i in 1 until pointList.size) {
            pathLines.cubicTo(
                conPoint1[i -1].x, conPoint1[i-1].y, conPoint2[i - 1].x, conPoint2[i - 1].y,
                pointList[i].xValue, pointList[i].yValue
            )
        }

        canvas.drawPath(pathLines, linesPathPaint)

        // we need to close the gradient path
        pathLines.lineTo(endRight, startY)
        pathLines.close()

        canvas.drawPath(pathLines, gradientPathPaint)
    }

    private fun calculateConnectionPointsForBezierCurve(pointList: List<GraphPoint>) {
        try {
            for (i in 1 until pointList.size) {
                conPoint1.add(PointF((pointList[i].xValue + pointList[i - 1].xValue) / 2, pointList[i - 1].yValue))
                conPoint2.add(PointF((pointList[i].xValue + pointList[i - 1].xValue) / 2, pointList[i].yValue))
            }
        } catch (e: Exception) {
        }
    }

    private fun drawTouchPoint(canvas: Canvas) {
        currentPoint?.let {
            canvas.apply {
                drawCircle(it.xValue, it.yValue, 10f, circlePaint)
                save()
                rotate(10f, it.xValue, it.yValue)
                val xValueStep = if (it.xValue > 50) -20 else 10
                val yValueStep = if (it.yValue > 50) -20 else 40
                drawText(it.value.toString(), it.xValue + xValueStep, it.yValue + yValueStep, textPaint)
                restore()
            }
            canvas.drawCircle(it.xValue, it.yValue, 10f, circlePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if(event.action == MotionEvent.ACTION_DOWN) {
            processEventDown(event.rawX)
        }

        return true
    }

    private fun processEventDown(rawX: Float) {
        val xValuesList = graphPointsList.map { points -> points.xValue }

        // find the closest X in the list
        val closestX = xValuesList.minBy { abs(it - rawX) }
        // find the index of the closest number
        val index = xValuesList.binarySearch(closestX)

        currentPoint = when {
            index > graphPointsList.size -1 -> graphPointsList[graphPointsList.size -1]
            else -> graphPointsList[index]
        }
        //this will redraw the all view with the correct point
        invalidate()
    }
}

data class GraphPoint(val xValue: Float, val yValue: Float, val value: Int)