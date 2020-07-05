package com.rb95.msgcountdragbombdemo

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.*
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import kotlin.math.*

/**
 * @des
 * @author RenBing
 * @date 2020/6/29 0029
 */
class PointView @JvmOverloads constructor(context: Context,private val dragView: View,private val windowManager: WindowManager,attr: AttributeSet? = null,defStyleAttr : Int = 0) : View(context,attr,defStyleAttr){
    private lateinit var mCirclePaint : Paint //固定圆画笔
    private lateinit var mLinePaint : Paint //拖拽圆画笔
    private val mFixedCircleCenterP = PointF(300f,400f) //固定圆圆心点
    private val mDragCircleCenterP = PointF(300f,400f) //拖拽圆圆心点
    private var mFixedCircleRadius = 15f //固定圆半径
    private var mDragCircleRadius = 15f //拖拽圆半径
    private var mLinePath : Path //两圆之间的二次贝塞尔线条
    private val minRadius = 12f //圆的最小半径
    private val maxDistance = 200 // 最大拖拽距离
    private var isOut = false //标识 拖拽距离是否大于规定的拖拽范围
    private var isOverStep = false //标识 如果超出拖拽范围
    private var isOverandUp = false //标识 超出范围并且抬起手指
    private lateinit var bombImg : ImageView //爆炸img
    private lateinit var bombImgs : ArrayList<Int> //爆炸图片集

    private var mDragViewWidth = 0//拖拽圆的宽度
    private var mDragViewHeight = 0//拖拽圆的高度
    private var params : WindowManager.LayoutParams //windowmanager 参数
    private var dragViewStatusListener: DragViewStatusListener? = null
    private var statusBarHeight = 0

    init {
        initPaint() // 初始化画笔
//        initBombImg()
        mLinePath = Path() //实例化一条线

        /*
            dragView是从外部传进来的拖拽圆
            自定义控件内部的两个圆（拖拽圆和固定圆）只是用来确定连接部分的二次贝塞尔曲线
            实际上控件内部的圆都被外部的控件覆盖了
         */
        dragView.measure(1,1) //获取dragView尺寸
        /*
            获取外部传进来的dragView的宽度 后面更新拖拽圆的时候要用到
            因为安卓的渲染机制是从左上角计算 而不是从中心点计算
            所以需要获取到dragView的宽高和触碰点坐标计算出dragView的起始坐标
         */
        mDragViewWidth = dragView.measuredWidth
        mDragViewHeight = dragView.measuredHeight

        /*
            通过WindowManager将控件加到window中去处理拖动事件 实现气泡能在全屏拖动的效果 因为这个控件最终是在recyclerview的item中使用 涉及到事件分发
         */
        params = WindowManager.LayoutParams()
        params.format = PixelFormat.TRANSPARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.TOP or Gravity.LEFT
    }

//    private fun initBombImg() {
//        bombImg = ImageView(context)
//        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT)
//        bombImg.layoutParams = params
//        bombImg.setImageResource(R.drawable.explode_1)
//        bombImg.visibility = View.INVISIBLE
//        addView(bombImg)
//
//        bombImgs = arrayListOf()
//        bombImgs.add(R.drawable.explode_1)
//        bombImgs.add(R.drawable.explode_2)
//        bombImgs.add(R.drawable.explode_3)
//        bombImgs.add(R.drawable.explode_4)
//        bombImgs.add(R.drawable.explode_5)
//    }


    /**
     * 初始化画笔
     */
    private fun initPaint() {
        //固定圆的画笔
        mCirclePaint = Paint()
        mCirclePaint.color =Color.RED
        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.FILL
        //拖拽圆的画笔
        mLinePaint = Paint()
        mLinePaint.color =Color.RED
        mLinePaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        /*
            因为屏幕上坐标原点是在左上角可见范围内 不包含状态栏
            所以这里的获取到状态栏高度 后面在计算的时候得减去这部分
         */
        statusBarHeight = Utils.getStatusBarHeight(this)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (canvas == null) return
        canvas.save()
        /*
            因为window包含了状态栏 view内是不包含的 起初画布的位置不包含状态栏 现在引入window去绘制控件
            所以画布也要往上移  否则window中绘制的控件整体偏下了
         */
        canvas.translate(0f, (-statusBarHeight).toFloat())
        if (!isOut){
            mFixedCircleRadius = changeCenterRadius()
            canvas.drawCircle(mFixedCircleCenterP.x,mFixedCircleCenterP.y,mFixedCircleRadius,mCirclePaint)
            canvas.drawCircle(mDragCircleCenterP.x,mDragCircleCenterP.y,mDragCircleRadius,mCirclePaint)

            val x = mFixedCircleCenterP.x - mDragCircleCenterP.x
            val y = mDragCircleCenterP.y - mFixedCircleCenterP.y
            val a = atan(y/x)

            val offsetX1 = mFixedCircleRadius * sin(a)
            val offsetY1 = mFixedCircleRadius * cos(a)

            val offsetX2 = mDragCircleRadius * sin(a)
            val offsetY2 = mDragCircleRadius * cos(a)

            val p1X = mFixedCircleCenterP.x - offsetX1
            val p1Y = mFixedCircleCenterP.y - offsetY1

            val p2X = mFixedCircleCenterP.x + offsetX1
            val p2Y = mFixedCircleCenterP.y + offsetY1

            val p3X = mDragCircleCenterP.x - offsetX2
            val p3Y = mDragCircleCenterP.y - offsetY2

            val p4X = mDragCircleCenterP.x + offsetX2
            val p4Y = mDragCircleCenterP.y + offsetY2

            val cX = (mFixedCircleCenterP.x + mDragCircleCenterP.x)/2
            val cY = (mFixedCircleCenterP.y + mDragCircleCenterP.y)/2

            mLinePath.reset()
            mLinePath.moveTo(p1X,p1Y)
            mLinePath.quadTo(cX,cY,p3X,p3Y)
            mLinePath.lineTo(p4X,p4Y)
            mLinePath.quadTo(cX,cY,p2X,p2Y)
            mLinePath.lineTo(p1X,p1Y)
            mLinePath.close()
            canvas.drawPath(mLinePath,mLinePaint)
        }

        if (isOut && getDistanceTwo(mFixedCircleCenterP,mDragCircleCenterP) < 100 && isOverandUp){
            canvas.drawCircle(mFixedCircleCenterP.x,mFixedCircleCenterP.y,mFixedCircleRadius,mCirclePaint)
            isOut = false
            isOverandUp = false
            dragViewStatusListener?.recoverCenterPoint(mFixedCircleCenterP)
        }

        if (!isOverStep && !isOverandUp && isOut){
            canvas.drawCircle(mDragCircleCenterP.x,mDragCircleCenterP.y,mDragCircleRadius,mCirclePaint)
            dragViewStatusListener?.dargOutMove(mDragCircleCenterP)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                updateDragPoint(event.rawX,event.rawY)
            }

            MotionEvent.ACTION_MOVE -> {
                updateDragPoint(event.rawX,event.rawY)
                val d = getDistanceTwo(mFixedCircleCenterP,mDragCircleCenterP)
                if (d > maxDistance)
                    isOut = true
                else
                    isOverStep = false
            }

            MotionEvent.ACTION_UP ->{
                if (!isOut)
                    bounceBack()
                if (isOut){
                    isOverandUp = true
//                    bombImg.x = event.x - bombImg.width/2
//                    bombImg.y = event.y - bombImg.height/2
                    if (getDistanceTwo(mFixedCircleCenterP,mDragCircleCenterP) > 100)
//                        showExplodeImage()
                        dragViewStatusListener?.dragOutUp(mDragCircleCenterP)
                }
                postInvalidate()
            }
        }
        return true
    }

    fun setStatusBarHeight(statusBarHeight : Int){
        this.statusBarHeight = statusBarHeight
    }

    fun setFixedDragPonit(x:Float,y:Float){
        mFixedCircleCenterP.set(x, y)
        mDragCircleCenterP.set(x, y)
        invalidate()
    }
    fun setDragViewStatusListener(dragViewStatusListener: DragViewStatusListener){
        this.dragViewStatusListener = dragViewStatusListener
    }

    private fun showExplodeImage(){
        val animator = ValueAnimator.ofInt(0,bombImgs.size-1)
        animator.addUpdateListener {
            bombImg.setBackgroundResource(bombImgs[it.animatedValue as Int])
        }
        animator.doOnStart {
            bombImg.visibility = VISIBLE
        }
        animator.doOnEnd {
//            bombImg.visibility = View.GONE
            dragViewStatusListener?.dragInUp(mDragCircleCenterP)
        }
        animator.duration = 500
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = OvershootInterpolator()
        animator.start()
    }

    private fun bounceBack(){
        val startP = PointF(mDragCircleCenterP.x,mDragCircleCenterP.y)
        val endP = PointF(mFixedCircleCenterP.x,mFixedCircleCenterP.y)
        val animator = ValueAnimator.ofFloat(0f,1f)
        animator.addUpdateListener {
            val f = it.animatedFraction
            val newDragCircleP = getPoint(startP,endP,f)
            updateDragPoint(newDragCircleP.x,newDragCircleP.y)
        }
        animator.doOnEnd {
            dragViewStatusListener?.dragInUp(mDragCircleCenterP)
        }
        animator.interpolator = SpringInterpolator(0.2f)
        animator.duration = 500
        animator.start()
    }

    private fun getPoint(startP : PointF,endP : PointF,percent : Float) : PointF{
        return PointF(getValue(startP.x,endP.x,percent),getValue(startP.y,endP.y,percent))
    }

    private fun getValue(start : Float,end : Float,fraction : Float):Float{
        return start + (end - start)*fraction
    }

    private fun getDistanceTwo(mFixedCircleP : PointF,mDragCircleP : PointF) : Float{
        return sqrt((mFixedCircleP.x - mDragCircleP.x).pow(2) + (mFixedCircleP.y - mDragCircleP.y).pow(2))
    }

    private fun updateDragPoint(x : Float,y : Float){
        mDragCircleCenterP.set(x, y)
        changeManagerView(x,y)
        postInvalidate()
    }

    private fun changeManagerView(x: Float, y: Float) {
        params.x = (x - mDragViewWidth/2).toInt()
        params.y = (y - mDragViewHeight/2 - statusBarHeight).toInt()
        windowManager.updateViewLayout(dragView,params)
    }

    private fun changeCenterRadius():Float{
        val xd = mDragCircleCenterP.x - mFixedCircleCenterP.x
        val yd = mDragCircleCenterP.y - mFixedCircleCenterP.y
        val d = sqrt(xd.pow(2.0f) + yd.pow(2.0f))
        var r = mDragCircleRadius - minRadius* (d/maxDistance)
        if (r < minRadius)
            r = minRadius
        return r
    }
}