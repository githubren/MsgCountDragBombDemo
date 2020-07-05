package com.rb95.msgcountdragbombdemo

import android.view.animation.Interpolator
import kotlin.math.pow
import kotlin.math.sin


/**
 * @des
 * @author RenBing
 * @date 2020/7/1 0001
 */
class SpringInterpolator(private val factor : Float) : Interpolator {

    override fun getInterpolation(input: Float): Float {
        return (2.0.pow(-10.0 * input) * sin((input - factor / 4) * (2 * Math.PI) / factor) + 1).toFloat()
    }
}