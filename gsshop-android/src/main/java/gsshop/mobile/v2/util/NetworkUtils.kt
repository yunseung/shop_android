/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import gsshop.mobile.v2.R
import gsshop.mobile.v2.ServerUrls
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog


/**
 * 네트워크 관련 유틸리티
 */
object NetworkUtils {
    /**
     * 네트워크 타입을 반환한다.
     *
     * @return 네트워크 타입
     * @param context 컨텍스트
     */
    @JvmStatic
    fun getNetworkType(context: Context): String {
        var networkType = ""
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return networkType
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return networkType
            networkType = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "MOBILE"
                else -> "OTHER"
            }
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo ?: return networkType
            networkType = when (activeNetwork.type) {
                ConnectivityManager.TYPE_WIMAX, ConnectivityManager.TYPE_MOBILE -> "MOBILE"
                ConnectivityManager.TYPE_WIFI -> "WIFI"
                else -> "OTHER"
            }
        }

        return networkType
    }

    /**
     * 현재 네트워크에 연결되어 있는지 여부를 확인한다.
     *
     * @param context 컨텍스트
     * @return 연결되어 있다면 true 리턴
     */
    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork= connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnectedOrConnecting
        }
    }

    /**
     * 네트워크 안내팝업을 노출한다.
     *
     * @param activity 액티비티
     */
    @JvmStatic
    fun alertNetworkDisconnected(activity: Activity) {
        activity.runOnUiThread {
            CustomTwoButtonDialog(activity)
                    .message(R.string.app_refresh)
                    .positiveButtonLabel(R.string.go_web)
                    .negativeButtonLabel(R.string.common_close)
                    .positiveButtonClick { dialog ->
                        dialog.dismiss()
                        val goWebIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ServerUrls.MOBILE_WEB))
                        activity.startActivity(goWebIntent)
                        activity.finish()
                    }.negativeButtonClick { dialog ->
                        dialog.dismiss()
                        activity.finish()
                    }.cancelled { dialog ->
                        dialog.dismiss()
                        activity.finish()
                    }.show()
        }
    }

    /**
     * 3G 과금 안내 팝업
     * @param activity activity
     */
    @JvmStatic
    fun confirmNetworkBillingAndShowPopup(activity: Activity, listener: OnConfirmNetworkListener) {
        //WIFI 상태인경우 바로실행
        if ("WIFI" == getNetworkType(activity)) {
            listener.isConfirmed(true)
        } else {
            val dialog: Dialog = CustomTwoButtonDialog(activity)
                    .message(R.string.network_billing_confirm)
                    .positiveButtonClick { dialog ->
                        listener.isConfirmed(true)
                        dialog.dismiss()
                    }.negativeButtonClick { dialog ->
                        listener.isConfirmed(false)
                        dialog.dismiss()
                    }
            (dialog as CustomTwoButtonDialog).cancelled { listener.inCanceled() }
            dialog.show()
        }
    }

    /**
     * 서비스 원활하지 않다.. 는 팝업을 띄운다.
     *
     * @param activity activity
     */
    @JvmStatic
    fun showUnstableAlert(activity: Activity) {
        CustomOneButtonDialog(activity)
                .message(R.string.network_unstable_detail_Vew)
                .buttonClick { dialog -> dialog.dismiss() }.cancelable(false).show()
    }

    /**
     * 네트워크 팝업 출력 콜백
     */
    interface OnConfirmNetworkListener {
        fun isConfirmed(isConfirmed: Boolean)
        fun inCanceled()
    }
}