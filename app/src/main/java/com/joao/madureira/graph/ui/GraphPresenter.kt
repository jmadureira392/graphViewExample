package com.joao.madureira.graph.ui

import com.joao.madureira.graph.data.model.DataPoint
import com.joao.madureira.graph.domain.GetVisitsUseCase

class GraphPresenter(
    private val getVisitsUseCase: GetVisitsUseCase,
    private val view: View
) {

    fun onCreate() {
        view.showVisits(getVisitsUseCase())
    }

    interface View {
        fun showVisits(visitList: List<DataPoint>)
    }
}