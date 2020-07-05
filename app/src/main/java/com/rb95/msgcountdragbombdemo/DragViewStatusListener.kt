package com.rb95.msgcountdragbombdemo

import android.graphics.PointF

/**
 * @des
 * @author RenBing
 * @date 2020/7/2 0002
 */
interface DragViewStatusListener {

    /**
     * 在最大距离外移动
     */
    fun dargOutMove(dragPoint : PointF)

    /**
     * 在最大距离外抬起来
     */
    fun dragOutUp(dragPoint: PointF)

    /**
     * 在最大距离内抬起来
     */
    fun dragInUp(dragPoint: PointF)

    /**
     * 从最大距离外移动到最大距离内
     */
    fun recoverCenterPoint(centerPoint : PointF)
}