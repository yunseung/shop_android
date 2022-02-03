/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import com.github.ajalt.reprint.core.Reprint
import gsshop.mobile.v2.AbstractBaseActivity
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.R
import gsshop.mobile.v2.ServerUrls
import gsshop.mobile.v2.setting.FingerPrintSettings.Companion.get
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog
import gsshop.mobile.v2.util.PrefRepositoryNamed
import kotlinx.android.synthetic.main.check_fingerprint.*

class CheckFPActivity : AbstractBaseActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_fingerprint)

        //배경 DIM 처리
        val param = window.attributes
        param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        param.dimAmount = 0.6f
        window.attributes = param

        // 클릭리스너 등록
        setClickListener()
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private fun setClickListener() {
        btn_not_show.setOnClickListener { hide() }
        btn_agree.setOnClickListener { agree() }
    }

    /**
     * 더이상 보지 않기
     */
    private fun hide() {
        PrefRepositoryNamed.saveBoolean(this, Keys.PREF.PREF_IS_SHOW_CHECK_FP,
                Keys.PREF.PREF_IS_SHOW_CHECK_FP, false)
        setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_CHECK_DONT_SEE_AGAIN)
        finish()
    }

    /**
     * 닫기
     */
    private fun agree() {
        setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_CHECK_AGREE)
        fingerprintLoginCheck()
    }

    /**
     * 디바이스의 지문로그인 가능여부를 체크하고, 가능한 경우 플래그를 업데이트한다.
     * 20181127 fingerprintLoginCheck이와 유사한 기능이 LoginActivity들어 있습니다
     * 그래서 관련 mseq 제거하고 fingerprintLoginCheck이것을 호출하는쪽에 mseq를 추가 하였습니다.
     *
     */
    fun fingerprintLoginCheck() {
        Reprint.initialize(applicationContext)
        //휴대폰에 지문인식 기능이 없으면 지문로그인 불가 팝업 노출
        if (!Reprint.isHardwarePresent()) {
            CustomOneButtonDialog(this as Activity).message(R.string.fp_unregistered).buttonClick { dialog ->
                dialog.dismiss()
                finish()
            }.show()
        }

        //휴대폰에 저장된 지문이 없다면
        if (!Reprint.hasFingerprintRegistered()) {
            CustomOneButtonDialog(this as Activity).message(R.string.fp_unsetting).buttonClick { dialog ->
                dialog.dismiss()
                finish()
            }.show()
        }
        val fingerPrintSettings = get()
        fingerPrintSettings.isFingerprintMapping = true
        fingerPrintSettings.save()
        finish()
    }
}