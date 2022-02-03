/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.gsshop.mocha.device.SystemInfo
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.util.EncryptUtils.encrypt
import gsshop.mobile.v2.util.PermissionUtils.isPermissionGrantedForPhone
import roboguice.util.Ln
import java.util.*

/**
 *
 *
 */
object DeviceUtils {
    private var mAdvID = ""

    /**
     * 단말기를 고유값을 반환한다.(android_id + serial + model -&gt; uuid)
     * 참고)
     * - android_id : 디바이스 최초 부팅시 값이 생성됨, 공장초기화시 변경됨 (Added in API level 3)
     * 디바이스가 멀티유저를 가진 경우 유저별로 다른값을 가진다.
     * - serial : 디바이스별 유니크한 값이지만, 모든 기기가 값을 준다고 보장하지는 않는다. (Added in API level 9)
     * - model : 디바이스 모델명 (Added in API level 1)
     *
     * @param context 컨텍스트
     * @return 디바이스 고유값(36바이트)
     */
    @JvmStatic
    fun getUuid(context: Context): String {
        return UUID.nameUUIDFromBytes((Settings.Secure.getString(context.contentResolver, "android_id")
                + Build.SERIAL + Build.MODEL).toByteArray()).toString()
    }

    /**
     * 단말기를 고유값을 반환한다.(getUuid() + "_GS" -&gt; sha-256)
     *
     * @param context 컨텍스트
     * @return 디바이스 고유값(64바이트)
     */
    @JvmStatic
    fun getGsuuid(context: Context): String {
        return encrypt(getUuid(context) + "_GS", "SHA-256")
    }

    /**
     * 단말기를 고유하게 구분하기 위한 값.
     * @return
     */
    val deviceId: String
        get() {
            val context = MainApplication.getAppContext()
            return if (!isPermissionGrantedForPhone(context)) {
                // device id가 없는 단말기인 경우 android id를 사용
                SystemInfo.getAndroidId()
                //SystemInfo.getMacAddress();
            } else {
                val id = SystemInfo.getDeviceId()

                // device id가 없는 단말기인 경우 android id를 사용
                if (TextUtils.isEmpty(id)) {
                    SystemInfo.getAndroidId()
                    //SystemInfo.getMacAddress();
                } else id
            }
        }

    /**
     * Advertising ID를 취득한다.<br></br>
     * Advertising ID는 UUID 포맷을 따른다.
     * 예)96bd03b6-defc-4203-83d3-dc1c730801f7
     *
     * 주의) getAdvertisingIdInfo은 blocking call이기 때문에 메인스레드에서 호출하면 익셉션 발생한다.
     *
     * @param context 컨텍스트
     */
    @JvmStatic
    fun setAdvertisingId(context: Context) {
        object : Thread() {
            override fun run() {
                try {
                    val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                    mAdvID = adInfo.id
                } catch (e: Exception) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e)
                }
            }
        }.start()
    }

    @JvmStatic
    fun getAdvertisingId(): String {
        return mAdvID
    }

    /**
     * 디바이스의 가로 픽셀정보를 반환한다.
     *
     * @param context 컨텍스트
     * @return 가로픽셀
     */
    @JvmStatic
    fun getDeviceWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 디바이스의 세로 픽셀정보를 반환한다.
     *
     * @param context 컨텍스트
     * @return 세로픽셀
     */
    @JvmStatic
    fun getDeviceHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}