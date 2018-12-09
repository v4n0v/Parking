package com.v4n0v.memgan.parking.utils

import android.animation.ArgbEvaluator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AnimationSet
import android.widget.ImageView


class Animator {
    companion object {

        fun zoomOutAnimation(): AnimationSet {
            val showAnimationSet = AnimationSet(false)
            showAnimationSet.addAnimation(scaleAnimation(1f, 1.1f, 200, 0))
            showAnimationSet.addAnimation(scaleAnimation(1.1f, 0f, 300, 200))
            return showAnimationSet

        }

        fun zoomInAnimation(): AnimationSet {
            val showAnimationSet = AnimationSet(false)
            showAnimationSet.addAnimation(scaleAnimation(0f, 1.1f, 400, 100))
            showAnimationSet.addAnimation(scaleAnimation(1.1f, 1f, 200, 500))
            return showAnimationSet
        }

        private fun scaleAnimation(from: Float, to: Float, duration: Long, offset: Long): Animation {
            val animation = ScaleAnimation(
                    from, to, from, to,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
            animation.duration = duration
            animation.startOffset = offset

            return animation
        }

        fun colorAnimation(view: ImageView, colorFrom: Int, colorTo: Int) {
            val animator = ValueAnimator.ofInt(colorFrom, colorTo)
            animator.addUpdateListener { animation -> view.setBackgroundColor((animation.animatedValue as Int)) }
            animator.start()
        }


        fun changeColorAnimation(view: View, colorFrom: Int, colorTo: Int) {
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 1250 // milliseconds
            colorAnimation.addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()

        }
    }
}