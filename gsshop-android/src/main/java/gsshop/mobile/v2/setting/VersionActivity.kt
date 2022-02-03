/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.setting

import android.content.pm.PackageInfo
import android.os.Bundle
import com.google.inject.Inject
import com.gsshop.mocha.device.AppInfo
import gsshop.mobile.v2.R
import gsshop.mobile.v2.menu.BaseTabMenuActivity
import gsshop.mobile.v2.support.version.VersionAction
import gsshop.mobile.v2.util.MarketUtils
import kotlinx.android.synthetic.main.setting_version.*

/**
 * 앱 버전정보 화면.
 *
 */
class VersionActivity : BaseTabMenuActivity() {
    private val txtCurrentVersion = txt_current_version
    private val btnVersionUpdate = btn_version_update

    @Inject
    private val versionAction: VersionAction? = null

    @Inject
    private val packageInfo: PackageInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_version)
        setHeaderView(R.string.version_setting_title)
        setupFields()
        // 클릭리스너 등록
        setClickListener()
    }

    private fun setupFields() {
        val version = versionAction?.cachedAppVersionInfo

        //버전커맨드에서 버전체크에 실패(네트워크에러 등)한 경우 version값이 null임
        if (version != null) {
            txtCurrentVersion.text = version.currentVersionName
            //txtCurrentVersion.setText("v" + version.currentVersionName);
            if (version.isUpdate(AppInfo.getAppVersionCode(packageInfo))) {
                btnVersionUpdate.setText(R.string.version_uptodate)
                btnVersionUpdate.isClickable = false
            } else {
                btnVersionUpdate.text = getString(R.string.version_update, version.vername)
            }
        } else {
            //version 값이 null인 경우 최신버전으로 표시
            val currentVersionName = AppInfo.getAppVersionName(packageInfo)
            txtCurrentVersion.text = currentVersionName
            btnVersionUpdate.setText(R.string.version_uptodate)
            btnVersionUpdate.isClickable = false
        }
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private fun setClickListener() {
        btn_version_update.setOnClickListener { clickUpdate() }
    }

    /**
     * 업데이트하러 마켓으로 이동.
     */
    private fun clickUpdate() {
        MarketUtils.goMarket(this)
    }
}