/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * Mocha 프레임워크를 실제 프로젝트에 사용하는 경우
 * Mocha 프레임워크 담당자에게 프로젝트 정식명칭, 담당자 연락처 등을
 * 곧바로 알려야 한다.
 *
 * 소스를 변경하여 사용하는 경우 Mocha 프레임워크 담당자에게
 * 변경된 소스 전체와 변경된 사항을 알려야 하며,
 * Mocha 프레임워크 담당자는 제공된 소스가 유용하다고
 * 판단되는 경우 해당 사항을 반영할 수 있다.
 *
 * Mocha 프레임워크 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.os.Handler
import android.os.Looper
import roboguice.util.Ln
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException

/**
 * <pre>
 * 쓰레드 및 [Runnable] 실행 관련한 기능 제공.
 *
 * ※ NOTE
 * - Singleton 클래스이므로 인스턴스 메소드 사용시 &#64;Inject를 통해 주입받아 호출할 것.
</pre> *
 */
object ThreadUtils {

    /**
     * UI 핸들러
     */
    private var uiHandler = Handler(Looper.getMainLooper())

    /**
     * Thread Excutor
     */
    private val threadExecutor: Executor = Executors.newCachedThreadPool()

    /**
     * [Runnable]을 백그라운드에서 실행한다.
     * @param run
     */
    fun runInBackground(run: Runnable?) {
        try {
            threadExecutor.execute(run)
        } catch (re: RejectedExecutionException) {
            Ln.e(re)
        } catch (ne: NullPointerException) {
            Ln.e(ne)
        }
    }

    /**
     * [Runnable]을 UI쓰레드에서 실행한다.
     * @param run
     */
    fun runInUiThread(run: Runnable) {
        if (inUiThread()) {
            run.run()
        } else {
            uiHandler.post(run)
        }
    }

    /**
     * [Runnable]을 UI쓰레드에서 실행한다.
     *
     * @param run
     * @param delayMillis 지연시간. 밀리초
     */
    fun runInUiThread(run: Runnable, delayMillis: Long) {
        if (delayMillis <= 0) {
            runInUiThread(run)
            return
        }
        uiHandler.postDelayed(run, delayMillis)
    }

    /**
     * 현재 쓰레드가 UI쓰레드인가?
     * @return
     */
    private fun inUiThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }
}