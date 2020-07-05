package com.rb95.msgcountdragbombdemo

import android.graphics.Rect
import android.view.View

/**
 * @des
 * @author RenBing
 * @date 2020/7/2 0002
 */
class Utils {
    companion object{
        fun getStatusBarHeight(view: View?) : Int{
            if (view == null) {
                return 0
            }
            val frame = Rect()
            view.getWindowVisibleDisplayFrame(frame)
            return frame.top
        }
    }
}