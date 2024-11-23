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
            PieChartData(0.2F, "Category 1"),
            PieChartData(0.1f, "Category 2"),
            PieChartData(0.4f, "Category 3"),
            PieChartData(0.3f, "Category 4"),
        ))
//        initChat()
//        setChatData()
    }

    override fun subscribeUi() {

    }

    override fun bindLayout(): ActivityMain2Binding {
        return ActivityMain2Binding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }

//    private fun initChat() {
//        mChart.setBackgroundColor(resources.getColor(R.color.white))
//        mChart.description.isEnabled = false
//
//        // 2. X 轴样式
//        val xAxis: XAxis = mChart.xAxis
//        xAxis.setDrawGridLines(false)
//        // xAxis.enableGridDashedLine(10f, 10f, 0f);
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//
//        // 3. Y轴样式
//        mChart.axisRight.isEnabled = false  // disable dual axis (only use LEFT axis)
//        val yAxis: YAxis = mChart.axisLeft
//        yAxis.axisMaximum = 200f
//        yAxis.axisMinimum = 0f
//    }

//    private fun setChatData() {
//        val entries = mutableListOf<Entry>()
//        val entries1 = mutableListOf<Entry>()
//        entries.add(Entry(3f, 20f))
//        entries1.add(Entry(5f, 30f))
//        entries1.add(Entry(6f, 40f))
//        entries1.add(Entry(7f, 50f))
//
//        val dataSet = LineDataSet(entries, "数据一")
//        val dataSet1 = LineDataSet(entries1, "数据二")
//        val list = mutableListOf<ILineDataSet>()
//        list.add(dataSet)
//        list.add(dataSet1)
//        val lineData = LineData(list)
//
//        mChart.data = lineData // 将模拟数据用于线形图，在线形图显示
//    }
}