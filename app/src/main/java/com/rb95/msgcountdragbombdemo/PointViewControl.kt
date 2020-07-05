package com.rb95.msgcountdragbombdemo

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.AnimationDrawable
import android.view.*
import android.widget.ImageView

/**
 * @des
 * @author RenBing
 * @date 2020/7/2 0002
 */
class PointViewControl(private val context: Context,private val mDragView:View,private val showView:View,private val dragStatusListener: DragStatusListener) : View.OnTouchListener,DragViewStatusListener{
    private lateinit var windowManager: WindowManager
    private var params : WindowManager.LayoutParams
    private lateinit var pointView : PointView
    private var dragView: View
    private var statusBarHeight = 0


    init {
        showView.setOnTouchListener(this)
        params = WindowManager.LayoutParams()
        params.format = PixelFormat.TRANSPARENT
        dragView = mDragView
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            val parent = v?.parent ?: return false
            //子view处理事件
            parent.requestDisallowInterceptTouchEvent(true)
            statusBarHeight = Utils.getStatusBarHeight(showView)
            showView.visibility  = View.INVISIBLE
//            dragView = LayoutInflater.from(context).inflate(mDragViewId,null,false)
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            pointView = PointView(context,dragView,windowManager)
            initData()
            pointView.setDragViewStatusListener(this)
            windowManager.addView(pointView,params)
            windowManager.addView(dragView,params)
        }
        pointView.onTouchEvent(event)
        return true
    }

    private fun initData() {
        val points = IntArray(2)
        showView.getLocationOnScreen(points)
        val x = points[0] + showView.width/2
        val y = points[1] + showView.height/2
        pointView.setStatusBarHeight(statusBarHeight)
        pointView.setFixedDragPonit(x.toFloat(), y.toFloat())
    }

    override fun dargOutMove(dragPoint: PointF) {

    }

    override fun dragOutUp(dragPoint: PointF) {
        removeView()
        showBombImage(dragPoint)
        dragStatusListener.outScope()
    }

    override fun dragInUp(dragPoint: PointF) {
        removeView()
        dragStatusListener.inScope()
    }

    override fun recoverCenterPoint(centerPoint: PointF) {
        removeView()
        dragStatusListener.inScope()
    }

    private fun removeView(){
        if (windowManager != null && pointView.parent != null && dragView.parent != null){
            windowManager.removeView(pointView)
            windowManager.removeView(dragView)
        }
    }

    private fun showBombImage(dragPoint: PointF){
        val iv = ImageView(context)
        iv.setImageResource(R.drawable.anim_out)
        val aniDrawable = iv.drawable as AnimationDrawable
        params.gravity = Gravity.TOP or Gravity.LEFT
        val inWidth = iv.drawable.intrinsicWidth
        val inHeight = iv.drawable.intrinsicHeight

        params.x = (dragPoint.x - inWidth/2).toInt()
        params.y = (dragPoint.y - inHeight/2 - statusBarHeight).toInt()
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        val duration = getAniDuration(aniDrawable)
        windowManager.addView(iv,params)
        aniDrawable.start()
        iv.postDelayed({
            aniDrawable.stop()
            iv.clearAnimation()
            windowManager.removeView(iv)
        },duration)
    }

    private fun getAniDuration(aniDrawable : AnimationDrawable) : Long{
        var duration = 0L
        for (i in 0..aniDrawable.numberOfFrames){
            duration += aniDrawable.getDuration(i)
        }
        return duration
    }

    interface DragStatusListener{
        fun inScope()

        fun outScope()
    }
}