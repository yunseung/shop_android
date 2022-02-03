package gsshop.mobile.v2.setting

import android.content.Context
import android.text.TextUtils
import com.google.inject.Inject
import com.gsshop.mocha.network.rest.RestClient
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.ServerUrls
import gsshop.mobile.v2.push.PushSettings
import gsshop.mobile.v2.setting.VideoAutoSettingConnector.VideoAutoSettingParam
import gsshop.mobile.v2.util.DeviceUtils
import gsshop.mobile.v2.util.PrefRepositoryNamed
import gsshop.mobile.v2.util.RestClientUtils
import roboguice.inject.ContextSingleton
import roboguice.util.Ln

/**
 *
 * 동영상 자동 플레이 설정 히스토리 쌓기
 * Created by jhkim on 15. 12. 24..
 */
@ContextSingleton
class AutoPlaySettingAction {
    @Inject
    private val restClient: RestClient? = null
    fun saveVideoAutoSetting(isAuto: Boolean, context: Context) {
        val param = VideoAutoSettingParam()
        try {
            val deviceToken = PrefRepositoryNamed.get(context, Keys.CACHE.TOKEN, Keys.CACHE.TOKEN, String::class.java)
            param.deviceToken = if (TextUtils.isEmpty(deviceToken)) "" else deviceToken
            param.deviceId = DeviceUtils.getUuid(context)
            param.custNo = PushSettings.get().customerNumber
            param.videoAutoPlayYn = if (isAuto) "Y" else "N"
            if (restClient != null) {
                RestClientUtils.post(restClient, param, ServerUrls.REST.VIDEO_AUTO_YN, Any::class.java)
            }
        } catch (e: Exception) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e)
        }
    }
}