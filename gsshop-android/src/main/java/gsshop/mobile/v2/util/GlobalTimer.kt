package gsshop.mobile.v2.util

import android.content.Context
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events.TimerEvent
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * One Second Global Timer
 */
class GlobalTimer private constructor() {

    fun startTimer() {
        stopTimer()
        task = object : TimerTask() {
            override fun run() {
                EventBus.getDefault().post(TimerEvent())
            }
        }
        Timer().schedule(task, ONE_SECOND, ONE_SECOND)
    }

    fun stopTimer() {
        if (task != null) {
            task!!.cancel()
            task = null
        }
    }

    companion object {
        private val ONE_SECOND = TimeUnit.SECONDS.toMillis(1)
        private var task: TimerTask? = null

        @Volatile private var instance: GlobalTimer? = null

        @JvmStatic fun getInstance(): GlobalTimer =
                instance ?: synchronized(this) {
                    instance ?: GlobalTimer().also {
                        instance = it
                    }
                }
    }
}