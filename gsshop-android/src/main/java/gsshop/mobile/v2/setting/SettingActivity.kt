/*

 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.ajalt.reprint.core.Reprint
import com.google.inject.Inject
import com.gsshop.mocha.device.AppInfo
import com.gsshop.mocha.pattern.mvc.BaseAsyncController
import com.gsshop.mocha.pattern.mvc.Model
import com.gsshop.mocha.ui.util.ViewUtils
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.usermgmt.LoginButton
import com.kakao.util.exception.KakaoException
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.data.OAuthErrorCode
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import gsshop.mobile.v2.*
import gsshop.mobile.v2.menu.BaseTabMenuActivity
import gsshop.mobile.v2.menu.TabMenu
import gsshop.mobile.v2.push.PushSettings
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog
import gsshop.mobile.v2.support.version.VersionAction
import gsshop.mobile.v2.user.*
import gsshop.mobile.v2.user.UserConnector.LogoutTokenResult
import gsshop.mobile.v2.util.*
import gsshop.mobile.v2.util.ThreadUtils.runInUiThread
import kotlinx.android.synthetic.main.fragment_flexible_shop_common.view.*
import kotlinx.android.synthetic.main.setting.*
import roboguice.inject.InjectExtra
import roboguice.util.Ln
import java.util.concurrent.TimeUnit

/**
 * 환경 설정 화면.
 *
 *
 * NOTE: 페이스북 연동정보는 향후 포함예정.
 */
class SettingActivity : BaseTabMenuActivity() {
    @InjectExtra(optional = true, value = Keys.INTENT.TAB_MENU)
    private val mTabMenu = TabMenu.MYSHOP

    private lateinit var mTvLoginStatus: TextView
    private lateinit var mCbPushStatus: CheckBox
//    private lateinit var mCbPushStatusAd: CheckBox
    private lateinit var mCbSsoStatus: CheckBox
    private lateinit var mViewSso: View
    private lateinit var mTvVersion: TextView
    private lateinit var mLlLoginAction: LinearLayout
    private lateinit var mTvUserId: TextView
    private lateinit var mBtnVersionUpdate: ImageButton
    private lateinit var mLlVersionArea: LinearLayout
    private lateinit var mTvNotLatestVer: TextView
    private lateinit var mClLogoutArea: ConstraintLayout
    private lateinit var mViewSns: View
    private lateinit var mCbAutoLoginCheck: CheckBox
    private lateinit var mCbNaverCheck: CheckBox
    private lateinit var mCbKakaoCheck: CheckBox
    private lateinit var mCbFingerprint: CheckBox
    private lateinit var mLlFingerprintLayout: LinearLayout

    private lateinit var mOAuthLoginButton: OAuthLoginButton
    private lateinit var mKakaoLoginButton: LoginButton

    @Inject
    private val mPackageInfo: PackageInfo? = null
    @Inject
    private val mUserAction: UserAction? = null
    @Inject
    private val mContext: Context? = null
    @Inject
    private val mVersionAction: VersionAction? = null

    private lateinit var mActivity: Activity
    private lateinit var mCallback: SessionCallback
    private lateinit var mOAuthLoginInstance: OAuthLogin        // naver client 정보를 넣어준다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)
        mActivity = this

        initUi()

        setHeaderView(R.string.setting_title)

        setupVersionFields()

        // 클릭리스너 등록
        setClickListener()

        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.SETTING_SHOW)
    }

    private fun initUi() {
        mTvLoginStatus = txt_login_status
        mCbPushStatus = check_push_status
//        mCbPushStatusAd = check_push_status_ad
        mCbSsoStatus = check_sso_status
        mViewSso = layout_sso
        mTvVersion = tv_version
        mLlLoginAction = ll_login_action
        mTvUserId = tv_user_id
        mBtnVersionUpdate = btn_version_update
        mLlVersionArea = ll_version_area
        mTvNotLatestVer = tv_not_latest_ver
        mClLogoutArea = cl_logout_area
        mViewSns = view_sns
        mCbAutoLoginCheck = check_auto_login
        mCbNaverCheck = check_sns_naver
        mCbKakaoCheck = check_sns_kakao
        mCbFingerprint = check_fingerprint
        mLlFingerprintLayout = fingerprint_layout

        mOAuthLoginButton = btn_naver_sdk_login
        mKakaoLoginButton = btn_kakao_sdk_login
    }

    override fun onResume() {
        super.onResume()

        //SNS 로그인 콜백 초기화(콜백등록)
        initKakao()
        initNaverLogin()

        // 다른 화면으로 이동했다가 돌아오면 상태가
        // 바뀔 수 있는 항목은 onResume에서 구성.
        setupAuthFields()
        setupPushFields()
        setupSsoFields()
    }

    override fun onPause() {
        super.onPause()

        //SNS 로그인 콜백 제거
        Session.getCurrentSession().removeCallback(mCallback)
        mOAuthLoginButton.setOAuthLoginHandler(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(navigationView)) {
            closeNavigationView()
            return
        } else {
            MainApplication.clearCache()
            //백키 클릭시 홈으로 이동
            GetHomeGroupListInfo(false)
        }
    }

    /**
     * 해더 백키 클릭시 액션 정의 (설정화면은 홈으로 이동)
     */
    override fun onClickHeaderBackBtn() {
        MainApplication.clearCache()
        GetHomeGroupListInfo(false)
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private fun setClickListener() {
        button_push.setOnClickListener { clickPushStatus() }
//        layout_push_ad.setOnClickListener { clickPushStatusAd() }
        button_sso.setOnClickListener { clickSsoStatus() }
        layout_login_status.setOnClickListener { clickLogin() }
        layout_company.setOnClickListener { clickCompany() }
        button_auto_login.setOnClickListener { onButtonAutoLogin() }
        button_sns_naver.setOnClickListener { onButtonSnsNaver() }
        button_sns_kakao.setOnClickListener { onButtonSnsKakao() }
        button_fingerprint.setOnClickListener { onButtonFingerprint() }
        mLlLoginAction.setOnClickListener { doLogin() }
    }

    /**
     * 버전정보 항목
     */
    @SuppressLint("SetTextI18n")
    private fun setupVersionFields() {
        val version = mVersionAction?.cachedAppVersionInfo

        //버전커맨드에서 버전체크에 실패(네트워크에러 등)한 경우 version값이 null임
        if (version != null) {
            if (version.isUpdate(AppInfo.getAppVersionCode(mPackageInfo))) {
                mTvVersion.text = "V" + version.currentVersionName + " (최신)"
                mBtnVersionUpdate.visibility = View.GONE
                mTvNotLatestVer.visibility = View.GONE
            } else {
                mBtnVersionUpdate.visibility = View.VISIBLE
                mTvNotLatestVer.visibility = View.VISIBLE
                mTvNotLatestVer.text = "V" + version.currentVersionName
                mTvVersion.text = getString(R.string.update_now)
                mLlVersionArea.setOnClickListener {
                    MarketUtils.goMarket(mActivity)
                    //와이즈로그 호출
                    setWiseLogHttpClient(ServerUrls.WEB.SETTING_VERSION_UPDATE)
                }
            }
        } else {
            //version 값이 null인 경우 최신버전으로 표시
            val currentVersionName = AppInfo.getAppVersionName(mPackageInfo)
            mTvVersion.text = "V$currentVersionName (최신)"
            mBtnVersionUpdate.visibility = View.GONE
            mTvNotLatestVer.visibility = View.GONE
        }

        //자동로그인 설정 세팅
        val option = LoginOption.get()
        mCbAutoLoginCheck.isChecked = option?.keepLogin ?: true

        //지문인식 불가폰에서는 해당레이아웃을 숨김
        Reprint.initialize(applicationContext)

        //휴대폰에 지문인식 기능이 없으면 레이아웃 숨김
        if (!Reprint.isHardwarePresent()) {
            mLlFingerprintLayout.visibility = View.GONE
        } else {
            mLlFingerprintLayout.visibility = View.VISIBLE
        }
        val fingerPrintSettings = FingerPrintSettings.get()
        mCbFingerprint.isChecked = fingerPrintSettings.isFingerprintMapping
    }

    /**
     * 알림메시지 항목.
     *
     *
     * NOTE : PUSH 설정 변경사항이 즉각 반영되지 않아
     * 약간의 delay후 PUSH 설정을 읽도록 함.
     */
    private fun setupPushFields() {
        ThreadUtils.runInUiThread(Runnable {
            val push = PushSettings.get()
            mCbPushStatus.isChecked = push.approve
//            mCbPushStatusAd.isChecked = push.approveAd == "Y"
        }, 1000)
    }

    private fun setupSsoFields() {
        object : BaseAsyncController<UserConnector.SSOQueryResult>(mContext) {
            @Throws(Exception::class)
            override fun process(): UserConnector.SSOQueryResult? {
                return mUserAction?.querySSOUse()
            }

            @Throws(Exception::class)
            override fun onSuccess(result: UserConnector.SSOQueryResult) {
                super.onSuccess(result)
                if (result != null) {
                    // 통합 회원 아닐경우
                    if (result.ssoUseYn == "E") {
                        mViewSso.visibility = View.GONE
                    }
                    else {
                        mViewSso.visibility = View.VISIBLE
                        // SSO 체크.
                        mCbSsoStatus.isChecked = result.ssoUseYn == "Y"
                    }
                }
            }

            @Throws(Exception::class)
            override fun onError(e: Throwable) {
                Ln.e(e)
            }
        }.execute()
    }

    /**
     * 로그인, 간편주문인증, 페이스북, 트위터 항목.
     * 2014.01.17 parksegun EC통합 재구축 설정화면에서 사용자 정보를 서버에서 가지고오지 않음.
     * 2014.02.28 parksegun EC통합 재구축 사용자 정보 서버에서 조회 복구
     */
    private fun setupAuthFields() {
        val userSetting = UserSetting()
        val user:User? = User.getCachedUser()
        val loggedIn = !TextUtils.isEmpty(user?.customerNumber)
        userSetting.loginId = user?.loginId
        userSetting.loginId = MaskingUtil.maskLoginId(userSetting.loginId)

        if (loggedIn) {
            mTvLoginStatus.text = getString(R.string.setting_login_status)
            mLlLoginAction.visibility = View.GONE
            mTvUserId.visibility = View.VISIBLE
            mTvUserId.text = userSetting.loginId
            mClLogoutArea.visibility = View.VISIBLE
            mClLogoutArea.setOnClickListener {
                CustomTwoButtonDialog(this@SettingActivity).message(R.string.login_confirm_logout)
                        .positiveButtonClick { dialog ->
                            dialog.dismiss()
                            LogoutController(this@SettingActivity).execute()
                            //와이즈로그 호출
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_LOGOUT)
                            //GTM Datahub 이벤트 전달
                            DatahubAction.sendDataToDatahub(this@SettingActivity, DatahubUrls.D_1033, "")
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            }
        } else {
            mTvLoginStatus.text = getString(R.string.setting_login_off)
            mLlLoginAction.visibility = View.VISIBLE
            mTvUserId.visibility = View.GONE
            mClLogoutArea.visibility = View.GONE
        }
        mTvLoginStatus.tag = loggedIn
        if (loggedIn) {
            object : BaseAsyncController<SnsLink?>(mContext) {
                @Throws(Exception::class)
                override fun process(): SnsLink? {
                    return RestClientUtils.post(restClient, user?.customerNumber?.let { LinkParam(it) }, ServerUrls.REST.SNS_LINK_LIST, SnsLink::class.java)
                }

                @Throws(Exception::class)
                override fun onSuccess(link: SnsLink?) {
                    super.onSuccess(link)
                    ViewUtils.showViews(mViewSns)
                    if (link != null) {
                        mCbNaverCheck.isChecked = link.naver
                        mCbKakaoCheck.isChecked = link.kakao
                    }
                }
            }.execute()
        } else {
            ViewUtils.hideViews(mViewSns)
        }
    }

    private fun clickPushStatus() {
//        var isApprovedAd = PushSettings.get().approveAd == "Y"
//        if (mCbPushStatus.isChecked) {
//            isApprovedAd = false
//        }

        // 클릭했을 때에 동작
        if (PushSettings.get().approve) {
            showMsgDialog(resources.getString(R.string.msg_push_off) + "GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")")
            //GTM Datahub 이벤트 전달
            DatahubAction.sendDataToDatahub(mActivity, DatahubUrls.D_1031, "")
            //와이즈로그 호출
            setWiseLogHttpClient(ServerUrls.WEB.SETTING_PUSH_OFF)
        } else {
            showMsgDialog(resources.getString(R.string.msg_push_on) + "GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")")
            //GTM Datahub 이벤트 전달
            DatahubAction.sendDataToDatahub(mActivity, DatahubUrls.D_1030, "")
            //와이즈로그 호출
            setWiseLogHttpClient(ServerUrls.WEB.SETTING_PUSH_ON)
        }

        setPushSetting(!PushSettings.get().approve)// , isApprovedAd)
    }

    // 광고 설정 삭제.
//    private fun clickPushStatusAd() {
//        var isApproved = PushSettings.get().approve
//        if (!mCbPushStatus.isChecked) {
//            isApproved = true
//        }
//
//        var isApprovedAd = PushSettings.get().approveAd == "Y"
//
//        if (isApprovedAd) {
//            showMsgDialog(resources.getString(R.string.msg_push_off_ad) + "GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")")
//            //와이즈로그 호출
//            setWiseLogHttpClient(ServerUrls.WEB.SETTING_PUSH_AD_OFF)
//        } else {
//            showMsgDialog(resources.getString(R.string.msg_push_on_ad) + "GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")")
//            //와이즈로그 호출
//            setWiseLogHttpClient(ServerUrls.WEB.SETTING_PUSH_AD_ON)
//        }
//
//        setPushSetting(isApproved, !isApprovedAd)
//    }
    private fun clickSsoStatus() {
        mCbSsoStatus.isChecked.let {
            if (it) {
                setSsoSetting(false)
            } else {
                setSsoSetting(true)
            }
        }
    }

    private fun clickLogin() {
        if (!(mTvLoginStatus.tag as Boolean)) {
            goLogin()
        }
    }

    /**
     * 메시지 팝업을 노출한다.
     */
    private fun showOldVerMsg() {
        CustomOneButtonDialog(this).message(R.string.sns_login_version_old)
                .cancelable(false)
                .buttonClick { dialog -> dialog.dismiss() }.show()
    }

    private fun goLogin() {
        val intent = Intent(Keys.ACTION.LOGIN)
        intent.putExtra(Keys.INTENT.TAB_MENU, mTabMenu)
        intent.putExtra(Keys.INTENT.FROM_SETTING, true)
        startActivity(intent)
    }

    private fun clickCompany() {
        val intent = Intent(Keys.ACTION.WEB)
        intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.COMPANY)
        intent.putExtra(Keys.INTENT.TAB_MENU, mTabMenu)
        startActivity(intent)
    }

    private fun doLogin() {
        val userSetting = UserSetting()
        val user = User.getCachedUser()
        userSetting.loginId = MaskingUtil.maskLoginId(user?.loginId)
        goLogin()
        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.SETTING_LOGIN)
    }

    /**
     * 자동 로그인 활성/비활성.
     */
    private fun onButtonAutoLogin() {
        Ln.i("autoLogin isChecked: " + mCbAutoLoginCheck.isChecked)
        mCbAutoLoginCheck.isChecked.let {
            if (it) {
                CustomTwoButtonDialog(mContext as Activity?).message(R.string.auto_login_unlink_message)
                        .positiveButtonClick { dialog ->
                            mCbAutoLoginCheck.isChecked = false
                            val option = LoginOption.get() ?: LoginOption()
                            option.keepLogin = false
                            option.save()
                            dialog.dismiss()
                            //와이즈로그 호출
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_KEEP_LOGIN_OFF)
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            } else {
                CustomTwoButtonDialog(mContext as Activity?).message(R.string.auto_login_link_message)
                        .positiveButtonClick { dialog ->
                            mCbAutoLoginCheck.isChecked = true
                            val option = LoginOption.get() ?: LoginOption()
                            option.keepLogin = true
                            option.save()
                            dialog.dismiss()
                            //와이즈로그 호출
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_KEEP_LOGIN_ON)
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            }
        }
    }

    /**
     * SNS 로그인 연동.
     */
    private fun onButtonSnsNaver() {
        Ln.i("naver isChecked: " + mCbNaverCheck.isChecked)
        mCbNaverCheck.isChecked.let {
            if (it) {
                CustomTwoButtonDialog(mContext as Activity?).message(R.string.naver_unlink_message)
                        .positiveButtonClick { dialog ->
                            dialog.dismiss()
                            UnlinkAsyncController(mContext).execute(User.getCachedUser().customerNumber, LoginActivity.SNS_TYPE.NA.name)
                            //와이즈로그 호출
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_NA_LOGIN_OFF)
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            } else {
                //SNS로그인은 LOLLIPOP 부터 가능
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    showOldVerMsg()
                    return
                }
                CustomTwoButtonDialog(mContext as Activity?).message(R.string.naver_link_message)
                        .positiveButtonClick { dialog ->
                            dialog.dismiss()
                            mOAuthLoginButton.performClick()
                            //와이즈로그 호출
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_NA_LOGIN_ON)
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            }
        }
    }

    private fun onButtonSnsKakao() {
        Ln.i("kakao isChecked: " + mCbKakaoCheck.isChecked)
        mCbKakaoCheck.isChecked.let {
            if(it) {
                CustomTwoButtonDialog(mContext as Activity?).message(R.string.kakao_unlink_message)
                        .positiveButtonClick { dialog ->
                            dialog.dismiss()
                            UnlinkAsyncController(mContext).execute(User.getCachedUser().customerNumber, LoginActivity.SNS_TYPE.KA.name)
                            //와이즈로그 호출
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_KA_LOGIN_OFF)
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            } else {
                //SNS로그인은 LOLLIPOP 부터 가능
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    showOldVerMsg()
                    return
                }
                CustomTwoButtonDialog(mContext as Activity?).message(R.string.kakao_link_message)
                        .positiveButtonClick { dialog ->
                            dialog.dismiss()
                            if (Session.getCurrentSession().isOpened) {
                                Session.getCurrentSession().close()
                            }
                            /**
                             * kakao 웹뷰에서 간편로그인 html을 그러주지 못하는 kakao 자체 버그로 먼저 naver 웹뷰를 실행하고 그 후에 kakao 웹뷰를 실행한다
                             */
                            /**
                             * kakao 웹뷰에서 간편로그인 html을 그러주지 못하는 kakao 자체 버그로 먼저 naver 웹뷰를 실행하고 그 후에 kakao 웹뷰를 실행한다
                             */
                            object : BaseAsyncController<Void?>(mContext) {
                                @Throws(Exception::class)
                                override fun onPrepare(vararg params: Any) {
                                    super.onPrepare(*params)
                                    context.startActivity(Intent(context, DummyAppBrowserActivity::class.java))
                                }

                                @Throws(Exception::class)
                                override fun process(): Void? {
                                    TimeUnit.MILLISECONDS.sleep(400)
                                    return null
                                }

                                @Throws(Exception::class)
                                override fun onSuccess(aVoid: Void?) {
                                    mKakaoLoginButton.performClick()
                                    super.onSuccess(aVoid)
                                }
                            }.execute()

                            //와이즈로그 호출
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_KA_LOGIN_ON)
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            }
        }
    }

    private fun onButtonFingerprint() {
        mCbFingerprint.isChecked.let {
            if(it) {
                CustomTwoButtonDialog(mContext as Activity?).message(R.string.fp_unlink_message)
                        .positiveButtonClick { dialog ->
                            dialog.dismiss()
                            val fingerPrintSettings = FingerPrintSettings.get()
                            fingerPrintSettings.isFingerprintMapping = false
                            fingerPrintSettings.save()
                            mCbFingerprint.isChecked = false
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_FP_LOGIN_OFF)
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            } else {
                CustomTwoButtonDialog(mContext as Activity?).message(R.string.fp_link_message)
                        .positiveButtonClick { dialog ->
                            dialog.dismiss()
                            fingerprintLoginCheck()
                            setWiseLogHttpClient(ServerUrls.WEB.SETTING_FP_LOGIN_ON)
                        }.negativeButtonClick(CustomOneButtonDialog.DISMISS).show()
            }
        }
    }

    /**
     * 디바이스의 지문로그인 가능여부를 체크하고, 가능한 경우 플래그를 업데이트한다.
     */
    private fun fingerprintLoginCheck() {
        //지문로그인 관련
        Reprint.initialize(applicationContext)
        //휴대폰에 지문인식 기능이 없으면 지문로그인 불가 팝업 노출
        if (!Reprint.isHardwarePresent()) {
            CustomOneButtonDialog(mContext as Activity?).message(R.string.fp_unregistered).buttonClick { dialog -> dialog.dismiss() }.show()
            return
        }

        //휴대폰에 저장된 지문이 없다면
        if (!Reprint.hasFingerprintRegistered()) {
            CustomOneButtonDialog(mContext as Activity?).message(R.string.fp_unsetting).buttonClick { dialog -> dialog.dismiss() }.show()

            /*
            new FingerPrintDialog((Activity) mContext).setNotSupportmessage().buttonLabel(R.string.common_ok).buttonClick(new FingerPrintDialog.ButtonClickListener() {
                @Override
                public void onClick(Dialog mDialog) {
                    mDialog.dismiss();
                }
            }).show();
            */return
        }
        val fingerPrintSettings = FingerPrintSettings.get()
        fingerPrintSettings.isFingerprintMapping = true
        fingerPrintSettings.save()
        mCbFingerprint.isChecked = true
    }

    private inner class LogoutController(activity: Activity?) : BaseAsyncController<LogoutTokenResult?>(activity) {
        @Throws(Exception::class)
        override fun process(): LogoutTokenResult? {
            mUserAction?.logout(true)
            return null
        }

        @Throws(Exception::class)
        override fun onSuccess(t: LogoutTokenResult?) {
            if (t != null) {
                if (!t.isSuccs) {
                    Toast.makeText(context, t.errMsg, Toast.LENGTH_LONG).show()
                }
            }
            goHome()
        }
    }

    private fun setPushSetting(isChecked: Boolean) { // , isCheckedAd: Boolean) {

        val model = PushSettings.get()
        model.approve = isChecked
//        model.approveAd = if (isCheckedAd) { "Y" } else { "N" }
        model.save()

        // 푸시 세팅에 ad 변경에 따라 앱보이에도 전달 해주어야 한다.
        AbstractTMSService.pushSettingAppboy(isChecked)
        // PMS 설정 정보 세팅 call back 안받아도 설정은 바뀌어야 한다.
        AbstractTMSService.pushSettings(this, model) { apiResult, _, _ ->
//            Ln.d("hklim pushSettings result : " + isChecked + " / " + apiResult.msg)
        }

        mCbPushStatus.isChecked = isChecked
//        mCbPushStatusAd.isChecked = isCheckedAd
    }


    private fun setSsoSetting(isChecked: Boolean) {
        val useYn = if (isChecked) {"Y"} else {"N"}
        object : BaseAsyncController<UserConnector.SSOUseResult>(mContext) {
            @Throws(Exception::class)
            override fun process(): UserConnector.SSOUseResult? {
                return mUserAction?.setSSOUse(useYn)
            }

            @Throws(Exception::class)
            override fun onSuccess(result: UserConnector.SSOUseResult) {
                super.onSuccess(result)
                if (result.isSuccs) {
                    // 변경 완료 실제 변경 한다. 추후 가이드 나온 후 수정
                        mCbSsoStatus.isChecked = isChecked
                    Toast.makeText(mContext, "변경 완료", Toast.LENGTH_SHORT).show()
                }
                else {
                    // 변경 실패 hklim
                    mCbSsoStatus.isChecked = !isChecked
                    Toast.makeText(mContext, "변경 실패, error code : " + result.errMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }

    private fun showMsgDialog(massage: String) {
        ThreadUtils.runInUiThread(Runnable {
            val dialog = CustomOneButtonDialog(mContext as Activity?).message(massage)
                    .buttonClick(CustomOneButtonDialog.DISMISS)
            dialog.show()
        })
    }

    private fun goHome() {
        //로그아웃 시 getHomeGroupInfo 수행을 위한 딜레이 추가
        runInUiThread(Runnable {
            //로그아웃시 홈으로  이동하는 로직 개선 (web->app)
            val intent = Intent(Keys.ACTION.APP_HOME)
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME)
            intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }, 1000)
    }

    private fun initKakao() {
        mCallback = SessionCallback()
        // removes kakao's login keys
        if (Session.getCurrentSession().isOpened) {
            Session.getCurrentSession().close()
        }
        Session.getCurrentSession().addCallback(mCallback)
        Session.getCurrentSession().checkAndImplicitOpen()
    }

    /**
     * naver simple login
     */
    private fun initNaverLogin() {
        mOAuthLoginInstance = OAuthLogin.getInstance()
        // removes naver's login keys
//        mOAuthLoginInstance.mTvLoginText(context);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler)
    }

    /**
     * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    private val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {
                val accessToken = mOAuthLoginInstance.getAccessToken(mContext)
                val refreshToken = mOAuthLoginInstance.getRefreshToken(mContext)
                val builder = StringBuilder()
                builder.append(accessToken).append("\n").append(refreshToken)
                val model = SimpleCredentials()
                model.snsAccess = accessToken
                model.snsRefresh = refreshToken
                model.snsTyp = LoginActivity.SNS_TYPE.NA.toString()
                LinkAsyncController(mContext).execute(User.getCachedUser().customerNumber, model.snsTyp, model.snsAccess, model.snsRefresh)
            } else {
                val authError = mOAuthLoginInstance.getLastErrorCode(mContext)
                Ln.i("authError: $authError")
                // ignore hardware back button & accept cancel button
                if (authError != OAuthErrorCode.CLIENT_USER_CANCEL
                        && authError != OAuthErrorCode.SERVER_ERROR_ACCESS_DENIED) {
                    val errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).code
                    val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)
                    Toast.makeText(mContext, "errorCode:$errorCode, errorDesc:$errorDesc", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private inner class SessionCallback : ISessionCallback {
        override fun onSessionOpened() {
            val accessToken = Session.getCurrentSession().tokenInfo.accessToken
            val refreshToken = Session.getCurrentSession().tokenInfo.refreshToken
            val builder = StringBuilder()
            builder.append(accessToken)
            builder.append("\n")
            builder.append(refreshToken)
            Ln.i("access token: [$accessToken]")
            Ln.i("refresh token [$refreshToken]")
            val model = SimpleCredentials()
            model.snsAccess = accessToken
            model.snsRefresh = refreshToken
            model.snsTyp = LoginActivity.SNS_TYPE.KA.toString()
            LinkAsyncController(mContext).execute(User.getCachedUser().customerNumber, model.snsTyp, model.snsAccess, model.snsRefresh)
        }

        override fun onSessionOpenFailed(exception: KakaoException) {
            // ignore hardware back key & link deny button
            if (exception.errorType != KakaoException.ErrorType.CANCELED_OPERATION) {
                Ln.e(exception)
            }
        }
    }

    /**
     * sns 로그인 연동.
     */
    private inner class LinkAsyncController(activityContext: Context?) : BaseAsyncController<SnsLink>(activityContext) {
        private var link: Link? = null
        private var snsTyp: String? = null
        @Throws(Exception::class)
        override fun onPrepare(vararg params: Any) {
            super.onPrepare(*params)
            snsTyp = params[1] as String
            link = Link(params[0] as String, params[1] as String, params[2] as String, params[3] as String)
        }

        @Throws(Exception::class)
        override fun process(): SnsLink? {
            return RestClientUtils.post(restClient, link!!, ServerUrls.REST.SNS_LINK, SnsLink::class.java)
        }

        @Throws(Exception::class)
        override fun onSuccess(link: SnsLink) {
            super.onSuccess(link)
            if (link.succs) {
                if (LoginActivity.SNS_TYPE.NA.name.equals(snsTyp, ignoreCase = true)) {
                    mCbNaverCheck.isChecked = true
                } else if (LoginActivity.SNS_TYPE.KA.name.equals(snsTyp, ignoreCase = true)) {
                    mCbKakaoCheck.isChecked = true
                }
            } else {
                showSnsAlertDialog(link)
            }
        }
    }

    /**
     * sns 로그인 연동해제.
     */
    private inner class UnlinkAsyncController(activityContext: Context?) : BaseAsyncController<SnsLink>(activityContext) {
        private var unlink: Unlink? = null

        @Throws(Exception::class)
        override fun onPrepare(vararg params: Any) {
            super.onPrepare(*params)
            unlink = Unlink(params[0] as String, params[1] as String)
        }

        @Throws(Exception::class)
        override fun process(): SnsLink? {
            return RestClientUtils.post(restClient, unlink!!, ServerUrls.REST.SNS_UNLINK, SnsLink::class.java)
        }

        @Throws(Exception::class)
        override fun onSuccess(link: SnsLink) {
            super.onSuccess(link)
            if (link.succs) {
                if (unlink?.snsTyp == LoginActivity.SNS_TYPE.NA.name) {
                    mCbNaverCheck.isChecked = false
                } else {
                    mCbKakaoCheck.isChecked = false
                }
            } else {
                CustomOneButtonDialog(mContext as Activity?).message(link.errMsg).buttonClick(CustomOneButtonDialog.DISMISS).show()
            }
        }
    }

    /**
     * SNS 링크오픈 실패시 팝업을 띄운다.
     */
    fun showSnsAlertDialog(link: SnsLink) {
        val dialog = Dialog(mContext!!, R.style.CustomDialog)
        dialog.setCancelable(true) // 백키 통한 취소 허용.
        dialog.setContentView(R.layout.setting_alert_sns_login)

        val colorSpan = ForegroundColorSpan(Color.parseColor("#00a4b3"))
        val sizeSpan = AbsoluteSizeSpan(MainApplication.getAppContext().resources
                .getDimensionPixelSize(R.dimen.setting_sns_popup_TextSize), false)
        val start = link.msgTop?.length
        val end = start!! + link.email?.length!!
        val builder = SpannableStringBuilder()
        builder.append(link.msgTop).append(link.email).append(link.msgBottom)
        builder.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(sizeSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val txtTop = dialog.findViewById<View>(R.id.txt_top) as TextView
        txtTop.text = builder
        val txtBottom = dialog.findViewById<View>(R.id.txt_bottom) as TextView
        txtBottom.text = link.groundMsg
        dialog.findViewById<View>(R.id.button_close).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    @Model
    private class LinkParam(val custNo: String)

    @Model
    private class Link(val custNo: String?, val snsTyp: String?, val snsAccess: String?, val snsRefresh: String?)

    @Model
    private class Unlink(val custNo: String?, val snsTyp: String)

    @Model
    class SnsLink {
        var kakao = false
        var naver = false
        var succs = false
        var errMsg: String? = null

        /**
         * 제목
         */
        var title: String? = null

        /**
         * 이메일
         */
        var email: String? = null

        /**
         * 이메일 기준으로, 이메일 이전에 표시될 내용
         */
        var msgTop: String? = null

        /**
         * 이메일 기준으로, 이메일 다음에 표시될 내용
         */
        var msgBottom: String? = null

        /**
         * 하단 메시지
         */
        var groundMsg: String? = null
    }
}