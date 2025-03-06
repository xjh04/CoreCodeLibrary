package com.jxdx.corecodelibrary.demo4

import android.graphics.Color
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.jxdx.corecodelibrary.common.BaseActivity
import com.jxdx.corecodelibrary.data.ColorfulText
import com.jxdx.corecodelibrary.databinding.ActivityMain4Binding
import com.jxdx.corecodelibrary.widget.BulletCommentView

class MainActivity4 : BaseActivity<ActivityMain4Binding>() {

    private lateinit var videoView : ImageView
    private lateinit var container: ConstraintLayout
    private lateinit var button1 : Button
    private lateinit var button2 : Button


    private var videoTop = 0f
    private var videoBottom = 0f

    private var curBulletTransparency = 1f
    private var isClick1 = false

    private var isClick2 = false
    private var curBulletTextSize = 50f
    override fun initView() {

        videoView = binding.video
        container = binding.container
        button1 = binding.button1
        button2 = binding.button2

        videoView.post {
            val videoHeight = videoView.drawable.intrinsicHeight * videoView.width /videoView.drawable.intrinsicWidth
            videoTop = (videoView.height - videoHeight) / 2f
            videoBottom = videoTop + videoHeight

            for (i in 1..5) {
                val delay = i * 1000L // 每个弹幕延迟1秒
                videoView.postDelayed({
                    val bulletCommentView = BulletCommentView(this)
                    bulletCommentView.y = videoTop + (Math.random() * (videoBottom - videoTop)).toFloat()
                    bulletCommentView.setText(ColorfulText(arrayListOf("hello","\n","world"), arrayListOf(Color.RED, Color.RED,Color.GREEN)))
                    if (isClick1){
                        bulletCommentView.transparency = curBulletTransparency
                    }
                    else{
                        bulletCommentView.transparency = 1f
                    }

                    if (isClick2){
                        bulletCommentView.setTextSize(40f)
                    }
                    else{
                        bulletCommentView.setTextSize(50f)
                    }

                    container.addView(bulletCommentView)
                    bulletCommentView.advance(videoView.width.toFloat(), 0f)
                }, delay)
            }
        }

        button1.setOnClickListener{
            if (curBulletTransparency == 1f){
                curBulletTransparency = 0.5f
                container.children.forEach { it.alpha = 0.5f }
                curBulletTransparency = 0.5f
                isClick1 = true
            }
            else{
                curBulletTransparency = 1f
                container.children.forEach { it.alpha = 1f }
                curBulletTransparency = 1f
                isClick1 = false
            }
        }

        button2.setOnClickListener{
            container.children.forEach{
                it as BulletCommentView
                it.setTextSize(40f)
                isClick2 = true
            }
        }
    }

    override fun subscribeUi() {

    }

    override fun bindLayout(): ActivityMain4Binding {
        return ActivityMain4Binding.inflate(layoutInflater)
    }

    override fun setStatusBar(): Int {
        return TRANSPARENT_STATUS_BAR_LIGHT
    }
}