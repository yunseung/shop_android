/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.gsshop.mocha.core.util.ActivityUtils
import com.gsshop.mocha.device.AppInfo
import gsshop.mobile.v2.R

/**
 * 마켓 이동 유틸.
 *
 */
object MarketUtils {
    /**
     * 구글플레이 또는 T스토어 마켓으로 이동.
     *
     * @param activity
     */
    @JvmStatic
    fun goMarket(activity: Activity) {
        // T store 배포용으로 빌드된 앱이면서 폰에 T store가 설치돼있으면 T store로 이동.
        if (activity.resources.getBoolean(R.bool.market_tstore)
                && ActivityUtils.isTStoreInstalled(activity)) {
            val productId = activity.getString(R.string.tstore_product_id)
            ActivityUtils.goTStoreMarketDetail(activity, productId)
            return
        }

        // 일반적으로는 Google Play 마켓으로 이동
        val pi = AppInfo.getPackageInfo(activity.applicationContext)
        ActivityUtils.goMarketDetail(activity, pi.packageName)
    }

    /**
     * Google Play Store의 해당 앱검색 화면으로 이동.
     *
     * @param activity
     * @param packageName
     */
    @JvmStatic
    fun goMarketSearch(activity: Activity, packageName: String) {
        val uri = Uri.parse("market://search?q=pname:$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
    }
}