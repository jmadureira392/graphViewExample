package com.joao.madureira.graph

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.joao.madureira.graph.data.model.DataPoint
import com.joao.madureira.graph.domain.GetVisitsUseCase
import com.joao.madureira.graph.ui.GraphPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GraphPresenter.View {

    private val presenter by lazy {
        GraphPresenter(GetVisitsUseCase(), this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.onCreate()
    }

    override fun showVisits(visitList: List<DataPoint>) {
        graphLayout.setData(visitList)
    }
}
