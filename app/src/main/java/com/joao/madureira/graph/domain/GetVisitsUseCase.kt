package com.joao.madureira.graph.domain

import com.joao.madureira.graph.data.model.DataPoint
import kotlin.random.Random

private const val MONTH_MILLISECONDS = 2629743000
private const val NUMBER_OF_POINTS = 20
private const val MAX_VALUE = 100

class GetVisitsUseCase {

    operator fun invoke(): List<DataPoint> {
        val list = mutableListOf<DataPoint>()

        for (i in 0 until NUMBER_OF_POINTS) {
            list.add(
                DataPoint(
                    System.currentTimeMillis() - i * MONTH_MILLISECONDS,
                    Random.nextInt(MAX_VALUE)
                )
            )
        }

        return list
    }
}