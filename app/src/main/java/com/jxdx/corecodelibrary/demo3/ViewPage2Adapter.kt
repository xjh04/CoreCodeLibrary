package com.jxdx.corecodelibrary.demo3

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.databinding.ActivityMain3Binding

class ViewPage2Adapter(activity : BaseActivity<ActivityMain3Binding>,val data :List<Int>) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return data.size
    }

    override fun createFragment(position: Int): Fragment {
        return PicFragment(data[position])
    }
}