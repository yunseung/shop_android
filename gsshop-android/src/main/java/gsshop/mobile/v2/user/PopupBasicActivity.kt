/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import gsshop.mobile.v2.AbstractBaseActivity
import gsshop.mobile.v2.Events.BasicPopupEvent
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.R
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.popup_basic.*
import roboguice.util.Ln

/**
 * 기본 팝업 노출을 위한 Activity
 */
class PopupBasicActivity : AbstractBaseActivity() {
    private var mUrl: String? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.popup_basic)

        //배경 DIM 처리
        val param = window.attributes
        param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        param.dimAmount = 0.6f
        window.attributes = param

        // 클릭리스너 등록
        setClickListener()
        try {
            val event = intent.getSerializableExtra(Keys.INTENT.INTENT_POPUP_BASIC) as BasicPopupEvent

            val textContent = txt_contents
            val btnLeft = btn_cancel
            val btnRight = btn_agree
            textContent.text = event.contents
            btnLeft.text = getString(R.string.common_ok)
            btnRight.text = getString(R.string.common_ok)

            if (event.strBtnLeft != null) btnLeft.text = event.strBtnLeft
            if (event.strBtnRight != null) btnRight.text = event.strBtnRight
            mUrl = event.url
            // 버튼 갯수 설정이 2개 이하 이면서 이동하는 url이 비어있으면 확인 버튼만 노출
            if (event.btnNumber < 2 && TextUtils.isEmpty(event.url)) {
                btnRight.visibility = View.GONE
                val viewDivider = view_divider
                viewDivider.visibility = View.GONE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    btnLeft.background = getDrawable(R.drawable.bg_dialog_round_bottom_one)
                }
                else {
                    btnLeft.background = ContextCompat.getDrawable(this@PopupBasicActivity, R.drawable.bg_dialog_round_bottom_one)
                }
            }
        } catch (e: ClassCastException) {
            Ln.e(e.message)
            finish()
        } catch (e: NullPointerException) {
            Ln.e(e.message)
            finish()
        }
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private fun setClickListener() {
        btn_cancel.setOnClickListener { cancel() }
        btn_agree.setOnClickListener { agree() }
    }

    /**
     * cancel 버튼 클릭
     */
    private fun cancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    /**
     * 확인
     */
    private fun agree() {
        if (mUrl != null) {
            WebUtils.goWeb(this, mUrl)
        }
        setResult(RESULT_OK)
        finish()
    }
}