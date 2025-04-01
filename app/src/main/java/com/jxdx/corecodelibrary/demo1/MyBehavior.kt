package com.jxdx.corecodelibrary.demo1

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.jxdx.corecodelibrary.R

class MyBehavior(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    private var maxScrollDistance = 50
    private var totalScrollDistance = 0
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val imageView = parent.findViewById<ImageView>(R.id.background)
        child.y = dependency.y + dependency.height + imageView.height
        imageView.alpha = 0f
        totalScrollDistance = 0

        val al = (dependency.y+dependency.height)/dependency.height
        val textView =
            child.findViewById<TextView>(R.id.text)
        textView.alpha = al
        return true
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        val imageView = coordinatorLayout.findViewById<ImageView>(R.id.background)
        val nestedScrollView =
            coordinatorLayout.findViewById<NestedScrollView>(R.id.nestedScrollView)
        maxScrollDistance = nestedScrollView.height
        return axes == View.SCROLL_AXIS_VERTICAL && imageView.height - child.translationY <= maxScrollDistance
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val nestedScrollView =
            coordinatorLayout.findViewById<NestedScrollView>(R.id.nestedScrollView)

        val imageView = coordinatorLayout.findViewById<ImageView>(R.id.background)
        if (dy > 0) {
            if(imageView.alpha < 1){
                imageView.alpha +=0.1f
            }else if(imageView.alpha>1){
                imageView.alpha =1f
            }

            if (totalScrollDistance + dy <= maxScrollDistance) {
                nestedScrollView.translationY -= dy
                totalScrollDistance += dy
                consumed[1] = dy
            } else if (totalScrollDistance != maxScrollDistance) {
                val distance = maxScrollDistance - totalScrollDistance
                nestedScrollView.translationY -= distance
                totalScrollDistance += distance
                consumed[1] = distance
            }
        } else {

            if (totalScrollDistance + dy >= 0) {
                nestedScrollView.translationY -= dy
                totalScrollDistance += dy
                consumed[1] = dy
            } else {
                nestedScrollView.translationY -= totalScrollDistance
                consumed[1] = totalScrollDistance
                totalScrollDistance = 0
            }
        }
    }
}
