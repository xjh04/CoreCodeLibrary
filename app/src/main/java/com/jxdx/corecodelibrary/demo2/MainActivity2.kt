package com.jxdx.corecodelibrary.demo2


import com.jxdx.corecodelibrary.R
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.data.PieChartData
import com.jxdx.corecodelibrary.databinding.ActivityMain2Binding
import com.jxdx.corecodelibrary.widget.PieChart


class MainActivity2 : BaseActivity<ActivityMain2Binding>() {

    private lateinit var mChart: PieChart
    override fun initView() {
        mChart = findViewById(R.id.chart1)
        mChart.updateData(listOf(
            PieChartData(0.5f, "Category 1"),
            PieChartData(0.1f, "Category 2"),
            PieChartData(0.3f, "Category 3"),
            PieChartData(0.1f, "Category 4"),
        ))
    }

    override fun subscribeUi() {

    }

    override fun bindLayout(): ActivityMain2Binding {
        return ActivityMain2Binding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }
}