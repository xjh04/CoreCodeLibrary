package com.jxdx.corecodelibrary.demo1

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Property
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.jxdx.corecodelibrary.R
import kotlin.math.abs

/**
 * 带回弹效果的RecyclerView。
 * @author xjh
 */

class ReboundRecyclerView2(context: Context, attrs: AttributeSet? = null) :
    RecyclerView(context, attrs), View.OnTouchListener {
    val tag = "ReboundRecyclerView.State"
    lateinit var textView: TextView

    companion object {
        // 定义下拉与上拉的移动比例
        const val DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD = 2f
        const val DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK = 1f

        // 定义减速系数
        const val DEFAULT_DECELERATE_FACTOR = -2f

        // 定义最大反弹时间
        const val MAX_BOUNCE_BACK_DURATION_MS = 800
        const val MIN_BOUNCE_BACK_DURATION_MS = 200
        const val SLIDING_THRESHOLD = 200f
    }

    // 定义状态类
    private var mCurrentState: VariousState //目前的状态
    private var mNormalState: NormalState // 正常状态
    private var mOverScrollingState: OverScrollingState //过度滑动状态
    private var mBounceBackState: BounceBackState//退弹状态

    private val mRecyclerView: RecyclerView = this

    // 记录开始滑动时的属性
    private val mStartAttr = StartMotionAttributes()

    // 速度
    private var mVelocity = 0f

    init {
        mBounceBackState = BounceBackState()
        mOverScrollingState = OverScrollingState()
        mCurrentState = NormalState().also {
            mNormalState = it
        }

        //本类实现了触摸监听器
        mRecyclerView.setOnTouchListener(this)

        /**
         * OVER_SCROLL_NEVER意味着用户在滚动到内容的开始或结束时，不会有任何额外的滚动效果。
         */
        mRecyclerView.overScrollMode = OVER_SCROLL_NEVER
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val parent = parent as MotionLayout
        textView = parent.findViewById(R.id.text)
        if (parent.currentState == parent.startState) {
            when (event?.action) {
                MotionEvent.ACTION_MOVE -> {
                    return mCurrentState.handleMoveTouchEvent(event)
                }

                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    return mCurrentState.handleUpTouchEvent(event)
                }
            }
            return false
        } else {
            return false
        }

    }

    /**
     * 这个方法用于清理资源，停止动画，以及执行其他与视图分离相关的清理工作。
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mRecyclerView.setOnTouchListener(null)
        mRecyclerView.overScrollMode = OVER_SCROLL_ALWAYS
    }

    /**
     * 状态转变
     */
    private fun issueStateTransition(state: VariousState) {
        mCurrentState = state
        // 处理动画
        mCurrentState.handleTransitionAnim()
    }

    /**
     * 初始化运动属性，并检测当前触摸事件是否符合移动（move）操作的要求。
     *
     * 该方法用于设置与触摸事件相关的运动属性，并判断事件是否主要表现为垂直移动。
     * 如果事件符合移动要求，则更新运动属性并返回 true；否则返回 false。
     *
     * @param view 当前操作的视图，即用户触摸的视图。
     * @param attributes 保存运动属性的对象，用于存储视图的偏移量和移动方向等信息。
     * @param event 当前触摸事件，包含了触摸的位置、时间戳等详细信息。
     *
     * @return 如果事件是有效的移动操作，则返回 true；否则返回 false。
     */
    private fun initMotionAttributes(
        view: View,
        attributes: MotionAttributes,
        event: MotionEvent
    ): Boolean {
        // 检查事件的历史记录大小是否为0，如果为0，则没有历史事件数据，直接返回false
        if (event.historySize == 0) {
            return false
        }
        // 计算触摸点在Y轴的像素偏移量，即当前触摸点的Y坐标减去历史记录中第一个触摸点的Y坐标
        val dy = event.y - event.getHistoricalY(0, 0)
        // 计算触摸点在X轴的像素偏移量，即当前触摸点的X坐标减去历史记录中第一个触摸点的X坐标
        val dx = event.x - event.getHistoricalX(0, 0)

        // 如果X轴的偏移量大于Y轴的偏移量，说明用户主要是水平移动，不是垂直移动，因此返回false
        if (abs(dy) < abs(dx)) {
            return false
        }

        // 将视图当前的垂直平移量保存到attributes对象的mAbsOffset属性中
        // translationY视图相对于其原始位置的垂直偏移量。
        attributes.mAbsOffset = view.translationY
        // 将计算出的Y轴偏移量保存到attributes对象的mDeltaOffset属性中
        attributes.mDeltaOffset = dy
        // 根据Y轴偏移量的正负，设置attributes对象的mDir属性，表示移动的方向
        // 如果偏移量大于0，表示向下移动，否则表示向上移动
        attributes.mDir = attributes.mDeltaOffset > 0

        // 如果以上条件都满足，返回true，表示初始化运动属性成功
        return true
    }


    /**
     * @param view 要平移的视图
     * @param offset 视图在垂直方向上的平移量
     * @param event 当前触摸事件
     */
    private fun translateViewAndEvent(view: View, offset: Float, event: MotionEvent) {
        // 设置视图的垂直平移量，使视图在垂直方向上移动 offset 指定的像素数
        view.translationY = offset

        // 调用 MotionEvent 的 offsetLocation 方法来调整触摸事件的位置。
        // 参数 0f 表示水平方向的偏移量为 0，即不改变水平位置。
        // offset - event.getY(0) 计算垂直方向的偏移量。
        // event.getY(0) 获取触摸事件在当前视图坐标系统中的初始 Y 坐标。
        // 通过减去这个初始 Y 坐标，我们实际上是在告诉系统，触摸事件的位置已经相对于视图的新位置进行了调整。
        // 这样做是为了确保触摸事件与视图的新位置保持一致。
        event.offsetLocation(0f, offset - event.getY(0))
    }


    private fun translateView(view: View, offset: Float) {
        view.translationY = offset
        textView.alpha -=0.08f
    }

    /**
     * 是否已经到达顶部
     */
    private fun isInAbsoluteTop(view: View): Boolean {
        return !view.canScrollVertically(-1)
    }

    /**
     * 是否已经到达底部
     */
    private fun isInAbsoluteBottom(view: View): Boolean {
        return !view.canScrollVertically(1)
    }

    /**
     * @param view 将要进行动画的视图
     * @param attributes 保存动画属性的对象
     */
    private fun initAnimationAttributes(
        view: View,
        attributes: AnimationAttributes
    ) {
        // 设置动画属性对象的 mProperty 属性为 TRANSLATION_Y，
        // 这意味着动画将改变视图的垂直平移量
        attributes.mProperty = TRANSLATION_Y

        //translationY是视图相对于其原始位置的垂直偏移量
        attributes.mAbsOffset = view.translationY

        // 表示动画可以达到的最大垂直偏移量
        attributes.mMaxOffset = view.height.toFloat()
    }


    //状态接口
    interface VariousState {
        /**
         * 处理move事件
         */
        fun handleMoveTouchEvent(event: MotionEvent): Boolean

        /**
         * 处理up事件
         */
        fun handleUpTouchEvent(event: MotionEvent): Boolean

        /**
         * 事件结束后的动画处理
         */
        fun handleTransitionAnim()
    }


    /**
     * 状态记录的数据类，用于保存和跟踪视图的移动属性。
     *
     * @property mAbsOffset 相对于父视图的绝对偏移量。这个值通常通过调用 view.getTranslationY() 获取。
     * @property mDeltaOffset 视图移动过程中的偏移量变化。这个值表示视图从初始位置移动了多少。
     * @property mDir 表示移动的方向。true 表示下拉动作，false 表示上拉动作。
     */
    data class MotionAttributes(
        var mAbsOffset: Float = 0f,
        var mDeltaOffset: Float = 0f,
        var mDir: Boolean = false
    )

    /**
     * @property mPointerId Motion 的id
     * @property mAbsOffset 相对于父视图的绝对偏移量。这个值通常通过调用 view.getTranslationY() 获取。
     * @property mDir 表示移动的方向。true 表示下拉动作，false 表示上拉动作。
     */
    private data class StartMotionAttributes(
        var mPointerId: Int = 0,
        var mAbsOffset: Float = 0f,
        var mDir: Boolean = false
    )

    /**
     * @param mProperty 代表动画作用的属性，这是一个类型为Property的对象，它关联了一个View和一个Float类型的值。
     * 在动画中，这个属性将会被用来指定动画应该作用在View的哪个属性上。
     * @param mAbsOffset 表示动画的绝对偏移量。这个值是动画开始时View的初始位置。
     * @param mMaxOffset 表示动画的最大偏移量。这个值定义了动画能够达到的最大范围
     */
    private data class AnimationAttributes(
        var mProperty: Property<View, Float> = TRANSLATION_Y,
        var mAbsOffset: Float = 0f,
        var mMaxOffset: Float = 0f
    )


    /**
     * 实现状态接口
     * 本类实现了过度滑动的情况
     */
    inner class OverScrollingState : VariousState {
        // 触摸拖动比例因子，用于下拉动作
        private var mTouchDragRatioFwd = DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD

        // 触摸拖动比例因子，用于上拉动作
        private var mTouchDragRatioBck = DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK

        // 动作属性，用于存储和传递触摸事件的移动信息
        private var mMoveAttr = MotionAttributes()

        override fun handleMoveTouchEvent(event: MotionEvent): Boolean {

            Log.d(tag, "OverScrollingState.Touch")
            val startAttr = mStartAttr
            // 不是一个触摸点事件，则直接切到回弹状态
            if (startAttr.mPointerId != event.getPointerId(0)) {
                issueStateTransition(mBounceBackState)
                return true
            }

            // 是否符合move要求
            if (!initMotionAttributes(mRecyclerView, mMoveAttr, event)) {
                return true
            }

            // mDeltaOffset: 实际要移动的像素，可以为下拉和上拉设置不同移动比
            val deltaOffset =
                mMoveAttr.mDeltaOffset / if (mMoveAttr.mDir == startAttr.mDir) mTouchDragRatioFwd else mTouchDragRatioBck
            // 计算偏移
            val newOffset = mMoveAttr.mAbsOffset + deltaOffset

            // 上拉下拉状态与滑动方向不符，则回到初始状态，并将视图归位
            if (startAttr.mDir && !mMoveAttr.mDir && newOffset <= startAttr.mAbsOffset || !startAttr.mDir && mMoveAttr.mDir && newOffset >= startAttr.mAbsOffset) {
                translateViewAndEvent(mRecyclerView, startAttr.mAbsOffset, event)
                issueStateTransition(mNormalState)
                return true
            }

            // 不让父类截获move事件
            if (mRecyclerView.parent != null) {
                mRecyclerView.parent.requestDisallowInterceptTouchEvent(true)
            }

            // 计算速度
            val dt = event.eventTime - event.getHistoricalEventTime(0)
            if (dt > 0) {
                mVelocity = deltaOffset / dt
            }

            // 改变控件位置
            translateView(mRecyclerView, newOffset)
            return true
        }

        override fun handleUpTouchEvent(event: MotionEvent): Boolean {
            // 事件up切换状态
            issueStateTransition(mBounceBackState)
            return false
        }

        override fun handleTransitionAnim() {
            //滑动过程中不需要动画
        }

    }

    /**
     * 实现处理接口
     * 本类实现了回弹状态的情况
     * @property mBounceBackInterpolator 产生一个减速的效果的插值器
     */
    inner class BounceBackState : VariousState, Animator.AnimatorListener,
        AnimatorUpdateListener {

        private val mBounceBackInterpolator: Interpolator = DecelerateInterpolator()
        private var mDecelerateFactor = 0f
        private var mDoubleDecelerateFactor = 0f
        private var mAnimAttributes: AnimationAttributes
        private var isExpend = false

        init {
            mDecelerateFactor = DEFAULT_DECELERATE_FACTOR
            mDoubleDecelerateFactor = DEFAULT_DECELERATE_FACTOR
            mAnimAttributes = AnimationAttributes()
        }

        override fun handleMoveTouchEvent(event: MotionEvent): Boolean {
            Log.d(tag, "BounceBackState.Touch")
            return true
        }

        override fun handleUpTouchEvent(event: MotionEvent): Boolean {
            return true
        }

        override fun handleTransitionAnim() {
            val bounceBackAnim: Animator = createAnimator()
            bounceBackAnim.addListener(this)
            bounceBackAnim.start()
        }

        //动画监听接口
        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationEnd(animation: Animator) {
            // 动画结束改变状态
            if (isExpend) {
                issueStateTransition(mOverScrollingState)
            } else {
                textView.alpha =1f
                issueStateTransition(mNormalState)
            }

        }

        override fun onAnimationCancel(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }

        override fun onAnimationUpdate(animation: ValueAnimator) {

        }

        private fun createAnimator(): Animator {
            initAnimationAttributes(mRecyclerView, mAnimAttributes)

            // 速度为0了或手势记录的状态与mDir不符合，直接回弹
            if (mVelocity == 0f || mVelocity < 0 && mStartAttr.mDir || mVelocity > 0 && !mStartAttr.mDir) {
                return createBounceBackAnimator(mAnimAttributes.mAbsOffset)
            }

            // 速度减到0，即到达最大距离时，需要的动画事件
            var slowdownDuration = (0 - mVelocity) / mDecelerateFactor
            slowdownDuration = if (slowdownDuration < 0) 0F else slowdownDuration

            // 速度减到0，动画的距离，dx = (Vt^2 - Vo^2) / 2a
            val slowdownDistance = -mVelocity * mVelocity / mDoubleDecelerateFactor
            val slowdownEndOffset = mAnimAttributes.mAbsOffset + slowdownDistance

            // 开始动画，减速->回弹
            val slowdownAnim: ObjectAnimator = createSlowdownAnimator(
                mRecyclerView, slowdownDuration.toInt(), slowdownEndOffset
            )
            val bounceBackAnim: ObjectAnimator = createBounceBackAnimator(slowdownEndOffset)
            var expandAnim: ObjectAnimator? = null
            if (mStartAttr.mAbsOffset >= -50) {
                expandAnim = createExpandAnimator()
            }
            val wholeAnim = AnimatorSet()
            if (!isExpend) {
                isExpend = true
                wholeAnim.playSequentially(slowdownAnim, expandAnim)
            } else {
                isExpend = false
                wholeAnim.playSequentially(slowdownAnim, bounceBackAnim)
            }

            return wholeAnim
        }

        private fun createExpandAnimator(): ObjectAnimator {
            val expandAnimator = ObjectAnimator.ofFloat(
                mRecyclerView, mAnimAttributes.mProperty, 450f
            )
            expandAnimator.interpolator = mBounceBackInterpolator
            return expandAnimator
        }

        /**
         * 创建一个减速动画，用于在指定的持续时间内将视图的指定属性缓慢减速到指定的结束偏移量。
         *
         * @param view 需要应用动画的视图。
         * @param slowdownDuration 减速动画的持续时间，单位为毫秒。
         * @param slowdownEndOffset 动画结束时视图属性的偏移量。
         * @return 返回一个配置好的减速动画[ObjectAnimator]。
         */
        private fun createSlowdownAnimator(
            view: View,
            slowdownDuration: Int,
            slowdownEndOffset: Float
        ): ObjectAnimator {
            // 创建一个属性动画，用于改变视图的指定属性到指定的结束偏移量
            val slowdownAnim = ObjectAnimator.ofFloat(
                view, mAnimAttributes.mProperty, slowdownEndOffset
            )
            // 设置动画的持续时间
            slowdownAnim.setDuration(slowdownDuration.toLong())
            // 设置动画的插值器，这里使用了一个弹跳回弹的插值器
            slowdownAnim.interpolator = mBounceBackInterpolator
            // 添加一个更新监听器，以便在动画更新时执行特定的操作
            slowdownAnim.addUpdateListener(this)
            // 返回配置好的动画对象
            return slowdownAnim
        }


        /**
         * 创建一个具有弹跳效果的动画，该动画将视图的指定属性从起始偏移量弹回到初始位置。
         *
         * @param startOffset 动画开始时的偏移量，即视图属性当前的偏移值。
         * @return 返回一个配置好的具有弹跳效果的动画[ObjectAnimator]。
         */
        private fun createBounceBackAnimator(startOffset: Float): ObjectAnimator {
            // 计算弹跳动画的持续时间
            if (isExpend){
                textView.alpha = 1f
            }

            val bounceBackDuration: Float =
                (abs(startOffset) / mAnimAttributes.mMaxOffset) * MAX_BOUNCE_BACK_DURATION_MS

            // 动画化到起始属性的绝对偏移量(mStartAttr.mAbsOffset)。
            val bounceBackAnim: ObjectAnimator = ObjectAnimator.ofFloat(
                mRecyclerView, mAnimAttributes.mProperty, mStartAttr.mAbsOffset
            )

            // 设置动画的持续时间，确保它至少为最小弹跳持续时间的毫秒数。
            bounceBackAnim.setDuration(
                bounceBackDuration.toInt().coerceAtLeast(MIN_BOUNCE_BACK_DURATION_MS).toLong()
            )

            // 设置动画的插值器为mBounceBackInterpolator
            bounceBackAnim.interpolator = mBounceBackInterpolator

            // 添加一个更新监听器，以便在动画的每一帧更新时执行某些操作。
            bounceBackAnim.addUpdateListener(this)

            // 返回配置好的动画对象。
            return bounceBackAnim
        }
    }

    /**
     * 实现处理接口
     * 本类实现了正常滑动的情况
     */
    inner class NormalState : VariousState {
        //保存状态
        private val mMoveAttr = MotionAttributes()
        override fun handleMoveTouchEvent(event: MotionEvent): Boolean {
            Log.d(tag, "NormalState.Touch")
            // 是否符合move要求，不符合不拦截事件
            if (!initMotionAttributes(mRecyclerView, mMoveAttr, event)) {
                return false
            }

            // 在RecyclerView顶部但不是下拉 或 在RecyclerView底部但不是上拉
            if (!(isInAbsoluteTop(mRecyclerView) && mMoveAttr.mDir || isInAbsoluteBottom(
                    mRecyclerView
                ) && !mMoveAttr.mDir)
            ) {
                return false
            }

            // 保存当前Motion信息
            mStartAttr.mPointerId = event.getPointerId(0)
            mStartAttr.mAbsOffset = mMoveAttr.mAbsOffset
            mStartAttr.mDir = mMoveAttr.mDir


            // 正常状态->滑动状态
            issueStateTransition(mOverScrollingState)
            return mOverScrollingState.handleMoveTouchEvent(event)
        }

        override fun handleUpTouchEvent(event: MotionEvent): Boolean {
            return false
        }

        override fun handleTransitionAnim() {

        }
    }
}