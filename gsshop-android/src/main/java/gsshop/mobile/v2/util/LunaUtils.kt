/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.app.Activity
import android.content.Context
import com.blankj.utilcode.util.EmptyUtils
import com.gsshop.luna.Luna
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events.AutoLogInRetryEvent
import gsshop.mobile.v2.user.UserConnector
import gsshop.mobile.v2.util.NetworkUtils.getNetworkType
import roboguice.util.Ln

/**
 * 루나함수 호출을 위한 클래스
 *
 */
object LunaUtils {
    @JvmField
    var autoLoginState: AutoLoginState? = null

    //자동로그인 용 prefix
    private const val PREFIX_AUTO_LOGIN = "autologin"

    //자동로그인 수행
    const val PREFIX_AUTO_LOGIN_SUCCESS = PREFIX_AUTO_LOGIN + "_success"
    const val PREFIX_AUTO_LOGIN_ERROR = PREFIX_AUTO_LOGIN + "_error"

    //자동로그인 RETRY 수행
    private const val PREFIX_AUTO_LOGIN_RETRY_SUCCESS = PREFIX_AUTO_LOGIN + "_retry_success"
    const val PREFIX_AUTO_LOGIN_RETRY_ERROR = PREFIX_AUTO_LOGIN + "_retry_error"

    //자동로그인 CUSTOM TRY 수행
    private const val PREFIX_AUTO_LOGIN_CUSTOM_TRY_SUCCESS = PREFIX_AUTO_LOGIN + "_custom_try_success"
    private const val PREFIX_AUTO_LOGIN_CUSTOM_TRY_ERROR = PREFIX_AUTO_LOGIN + "_custom_try_error"

    //자동로그인 익셉션 필터링용 키워드
    const val AUTO_LOGIN_EXCEPTION_KEYWORD = "authByToken"

    //앱 위변조 확인 중 에러발생시 사용할 prefix (네트웍/서버 에러 등등, 위변조 여부 확인 안됨)
    const val PREFIX_BUILD_ERROR = "build_error"

    //네이티브단품
    const val PREFIX_NATIVE_PRODUCT = "native_product"

    /**
     * HomeActivity로 이벤트 전송하여 자동로그인을 재시도한다.
     */
    private fun sendEvent(context: Context) {
        (context as Activity).runOnUiThread { EventBus.getDefault().post(AutoLogInRetryEvent()) }
    }

    /**
     * 자동로그인 성공로그를 전송한다.
     *
     * @param context 컨텍스트
     * @param t Throwable
     */
    @JvmStatic
    fun sendAutoLoginSuccess(context: Context, t: Throwable?) {
        //사용자 네트워크 타입 확인
        val networkType = getNetworkType(context)
        var prefix = ""
        when (autoLoginState) {
            AutoLoginState.FIRST_TRYING -> {
                //최초 자동로그인 성공은 로그 남기지 않음
                //prefix = LunaUtils.PREFIX_AUTO_LOGIN_SUCCESS;
            }
            AutoLoginState.RE_TRYING -> {
                //재시도 성공시
                prefix = PREFIX_AUTO_LOGIN_RETRY_SUCCESS
            }
            AutoLoginState.CUSTOM_TRYING -> {
                //네트워크 연결 콜백을 통한 성공시
                prefix = PREFIX_AUTO_LOGIN_CUSTOM_TRY_SUCCESS
            }
            //초기화
            else -> {}
        }
        if (EmptyUtils.isNotEmpty(prefix)) {
            val gsuuid = DeviceUtils.getGsuuid(context)
            sendToLuna(context, t, "$prefix [$networkType]_$gsuuid")
        }
        //초기화
        autoLoginState = AutoLoginState.IDLE
    }

    /**
     * 자동로그인 실패로그를 전송한다.
     *
     * @param context 컨텍스트
     * @param t Throwable
     * @param result LoginResult
     * @param fromExceptionHandler if true, MainExceptionHandler로부터 호출
     */
    @JvmStatic
    fun sendAutoLoginError(context: Context, t: Throwable?, result: UserConnector.LoginResult?, fromExceptionHandler: Boolean) {
        //사용자 네트워크 타입 확인
        val networkType = getNetworkType(context)
        var prefix = ""
        if (autoLoginState == AutoLoginState.FIRST_TRYING) {
            prefix = PREFIX_AUTO_LOGIN_ERROR
            if (fromExceptionHandler) {
                sendEvent(context)
            }
        } else if (autoLoginState == AutoLoginState.RE_TRYING) {
            prefix = PREFIX_AUTO_LOGIN_RETRY_ERROR
        } else if (autoLoginState == AutoLoginState.CUSTOM_TRYING) {
            prefix = PREFIX_AUTO_LOGIN_CUSTOM_TRY_ERROR
        }
        if (EmptyUtils.isNotEmpty(prefix)) {
            val gsuuid = DeviceUtils.getGsuuid(context)
            if (result == null) {
                //서버결과를 못받고 익셉션 발생한 경우
                sendToLuna(context, t, "$prefix [$networkType]_$gsuuid")
            } else {
                //서버에서 로그인실패 결과를 받은 경우
                sendToLuna(context, t, prefix + " [" + networkType + "]_" + result.retTyp + "_" + result.errMsg + "_" + gsuuid)
            }
        }
        //초기화
        autoLoginState = AutoLoginState.IDLE
    }

    @JvmStatic
    fun sendToLuna(context: Context?, t: Throwable?, prefix: String?) {
        Thread(Runnable {
            try {
                Luna.sendToServer(context, t, prefix)
            } catch (e: Exception) {
                Ln.i(e)
            }
        }).start()
    }

    /**
     * 자동로그인 상태
     */
    enum class AutoLoginState {
        IDLE,  //디폴트 상태
        FIRST_TRYING,  //앱구동시 최초 시도
        RE_TRYING,  //최초시도 실패 후 재시도
        CUSTOM_TRYING //네트워크 연결 콜백 발생시 시도
    }
}