package gsshop.mobile.v2.util

import android.graphics.drawable.AnimationDrawable
import android.os.Handler

abstract class CustomAnimationDrawable(aniDrawable: AnimationDrawable) : AnimationDrawable() {
    /** Handles the animation callback.  */
    private lateinit var mAnimationHandler: Handler
    override fun start() {
        super.start()
        /*
         * Call super.start() to call the base class start animation method.
         * Then add a handler to call onAnimationFinish() when the total
         * duration for the animation has passed
         */
        mAnimationHandler = Handler()
        mAnimationHandler.post { onAnimationStart() }
        mAnimationHandler.postDelayed({ onAnimationFinish() }, totalDuration.toLong())
    }

    /**
     * Gets the total duration of all frames.
     *
     * @return The total duration.
     */
    private val totalDuration: Int
        get() {
            var iDuration = 0
            for (i in 0 until this.numberOfFrames) {
                iDuration += getDuration(i)
            }
            return iDuration
        }

    /**
     * Called when the animation finishes.
     */
    abstract fun onAnimationFinish()

    /**
     * Called when the animation starts.
     */
    abstract fun onAnimationStart()

    init {
        /* Add each frame to our animation drawable */
        for (i in 0 until aniDrawable.numberOfFrames) {
            addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i))
        }
    }
}