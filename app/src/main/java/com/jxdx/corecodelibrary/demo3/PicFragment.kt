package com.jxdx.corecodelibrary.demo3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jxdx.corecodelibrary.R
import com.jxdx.corecodelibrary.databinding.FragmentPicBinding

class PicFragment(val data: Int) : Fragment(data) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPicBinding.inflate(inflater, container, false)
        binding.pic.setImageResource(when (data) {
            1 -> R.drawable.pic
            2 -> R.drawable.pic2
            else -> R.drawable.pic3
        })
        return binding.root
    }
}