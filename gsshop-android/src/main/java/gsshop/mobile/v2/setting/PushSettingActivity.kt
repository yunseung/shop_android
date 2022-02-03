/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.setting

import android.app.Activity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.CompoundButton
import gsshop.mobile.v2.AbstractTMSService
import gsshop.mobile.v2.R
import gsshop.mobile.v2.menu.BaseTabMenuActivity
import gsshop.mobile.v2.push.PushSettings
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog
import gsshop.mobile.v2.util.DateUtils
import kotlinx.android.synthetic.main.setting_push.*
import gsshop.mobile.v2.util.ThreadUtils

/**
 * PUSH설정 화면.
 */
class PushSettingActivity : BaseTabMenuActivity() {
    private var chkboxReceive: CheckBox = checkbox_show_popup
    private var chkboxShowPopup: CheckBox = checkbox_show_popup
    private var chkboxScreenOn: CheckBox = checkbox_screen_on
    private var chkboxSound: CheckBox = checkbox_sound

    private var chkboxVibration: CheckBox = checkbox_vibration
    private lateinit var mActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_push)
        mActivity = this
        setHeaderView(R.string.push_setting_title)
        setupFields()
    }

    private fun setupFields() {
        val model = PushSettings.get()
        // non null 보장하지 않지만 가독 성을 위해서 null safe ? 쓰지 않음.
        chkboxReceive.isChecked = model.approve

        //메시지 수신시 알림 팝업 / 알림 팝업시 화면 켜짐 항상 안함으로
        chkboxShowPopup.isChecked = false
        chkboxScreenOn.isChecked = false
        chkboxSound.isChecked = model.sound
        chkboxVibration.isChecked = model.vibration

        //알림 메세지 수신 설정값을 사용자가 변경하였을때
        //chkboxSound // chkboxVibration
        chkboxSound.setOnCheckedChangeListener(mCheckedChangeListener)
        chkboxVibration.setOnCheckedChangeListener(mCheckedChangeListener)
        chkboxReceive.setOnCheckedChangeListener { _, isChecked ->
            setPushSetting()
            if (isChecked) {
                showMsgDialog(mActivity.resources.getString(R.string.msg_push_on) + "GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")")
            } else {
                showMsgDialog(mActivity.resources.getString(R.string.msg_push_off) + "GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")")
            }
        }

        //onReceiveChanged();
    }

    private val mCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ -> setPushSetting() }

    private fun setPushSetting() {
        val model = PushSettings.get()
        if (model != null) {
            model.approve = chkboxReceive.isChecked
            model.showPopup = chkboxShowPopup.isChecked
            model.screenOn = chkboxScreenOn.isChecked
            model.sound = chkboxSound.isChecked
            model.vibration = chkboxVibration.isChecked
            model.save()

            //TMS 설정 정보 세팅
            AbstractTMSService.pushSettings(this, model)
        }
    }

    private fun showMsgDialog(massage: String) {
        ThreadUtils.runInUiThread(Runnable {
            CustomOneButtonDialog(mActivity).message(massage)
                    .buttonClick(CustomOneButtonDialog.DISMISS).show()
        })
    }
}