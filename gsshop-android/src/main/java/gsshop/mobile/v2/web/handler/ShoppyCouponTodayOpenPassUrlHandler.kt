/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler

import android.app.Activity
import android.net.Uri
import android.text.format.DateFormat
import android.webkit.WebView
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.ServerUrls
import gsshop.mobile.v2.home.HomeActivity
import gsshop.mobile.v2.util.PrefRepositoryNamed
import java.util.*

/**
 * shoppy 팝업 하루동안 보지 않기 여부 확인
 */
class ShoppyCouponTodayOpenPassUrlHandler : WebUrlHandler {
    override fun handle(activity: Activity, webview: WebView, url: String): Boolean {
        val uri = Uri.parse(url)

        PrefRepositoryNamed.saveString(activity, Keys.PREF.PREF_SHOPPY_LIVE_TODAY_PASS, DateFormat.format("yyyyMMdd", Date()).toString())

        // 웹뷰를 종료함
        if ("Y".equals(uri.getQueryParameter("closeYn"), ignoreCase = true)) {
            if (activity !is HomeActivity) {
                activity.finish()
            }
        }
        return true
    }

    override fun match(url: String): Boolean {
        return url.startsWith(ServerUrls.APP.SHOPPY_COUPON_POPUP_TODAY_PASS)
    }
}