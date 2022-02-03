/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2

import android.app.Activity
import android.content.Context
import com.blankj.utilcode.util.EmptyUtils
import com.google.inject.Singleton
import com.gsshop.mocha.core.exception.DefaultExceptionHandler
import com.gsshop.mocha.core.exception.WarningException
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog
import gsshop.mobile.v2.util.LunaUtils
import gsshop.mobile.v2.util.ThreadUtils
import org.springframework.web.client.RestClientException
import roboguice.util.Ln
import java.io.PrintWriter
import java.io.StringWriter

/**
 * 메인 에러핸들러.
 *
 */
@Singleton
class MainExceptionHandler : DefaultExceptionHandler() {

    public override fun doMessage(context: Context, t: Throwable, message: String) {
        var varMessage = message
        val errDescription = getErrorDescription(t)

        //Luna 서버로 Throwable 전달
        //사용자에게 메시지를 띄우는 WarningException은 로깅안함(예:아이디 또는 비밀번호 불일치 등)
        if (t !is WarningException) {
            //자동로그인 실패시 로그 전송
            if (errDescription.contains(LunaUtils.AUTO_LOGIN_EXCEPTION_KEYWORD)) {
                LunaUtils.sendAutoLoginError(context, t, null, true)
            } else {
                LunaUtils.sendToLuna(context, t, null)
            }
        }

        // context가 액티비티 컨텍스트인 경우 Toast대신 Dialog를 띄움
        //주석 하면 다이얼로그도 안띄움
        if (context is Activity) {
            //android.view.WindowManager$BadTokenException: 오류가 발생할 개연성이 있음
            val activity = context

            //WindowManager$BadTokenException 오류 대응
            if (!activity.isFinishing) {
                try {
                    //RestClient 관련 익셉션은 팝업노출 안함 (리프레시 레이아웃으로 대처)
                    if (t !is RestClientException) {
                        // 파라미터로 전달받은 메시지 내용이 없는 경우 익셉션 별 정의된 스태틱 메시지 사용
                        if (EmptyUtils.isEmpty(varMessage)) {
                            varMessage = messageFinder.find(t)
                        }
                        val fMessage = varMessage
                        ThreadUtils.runInUiThread ( Runnable {
                            CustomOneButtonDialog(activity).message(fMessage).buttonClick(
                                    CustomOneButtonDialog.DISMISS).show()
                        })
                    }
                } catch (e: Exception) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e)
                    LunaUtils.sendToLuna(context, e, null)
                }
            }
        }

        //주석하면 Activity 가 아닌경우 토스트 에러 팝업 안나타남
        //super.doMessage(context, t, message);
    }

    companion object {
        /**
         * 익셉션의 ErrorDescription을 반환한다,
         *
         * @param t Throwable
         * @return ErrorDescription
         */
        fun getErrorDescription(t: Throwable): String {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            return sw.toString()
        }
    }
}