package com.v4n0v.memgan.parking.utils

import android.view.animation.Animation
import android.view.animation.ScaleAnimation

class Animator {
    companion object {


          fun showScaleAnimation(): Animation {
            val showScaleAnimation = ScaleAnimation(
                    0f, 1.1f, 0f, 1.1f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
              showScaleAnimation.startOffset = 200
            showScaleAnimation.duration = 400
            return showScaleAnimation
        }

          fun toNormalScaleAnimation(): Animation {
            val toNormalScaleAnimation = ScaleAnimation(
                    1.1f, 1.0f, 1.1f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
            toNormalScaleAnimation.duration = 200
            toNormalScaleAnimation.startOffset = 600
            return toNormalScaleAnimation
        }
    }
}