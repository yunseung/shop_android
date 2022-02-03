package gsshop.mobile.v2.setting

import com.gsshop.mocha.network.rest.Rest
import com.gsshop.mocha.network.rest.RestPost
import com.gsshop.mocha.pattern.mvc.Model
import gsshop.mobile.v2.R
import gsshop.mobile.v2.ServerUrls
import roboguice.inject.ContextSingleton

/**
 * Created by jhkim on 15. 12. 9..
 *
 * 동영상 자동 플레이 설정 값 서버에 전달하는 클래스
 */
@ContextSingleton
@Rest(resId = R.string.server_http_root)
class VideoAutoSettingConnector {
    @RestPost(value = ServerUrls.REST.VIDEO_AUTO_YN)
    fun saveVideoAutoSetting(param: VideoAutoSettingParam?) {
    }

    @Model
    class VideoAutoSettingParam {
        @JvmField
        var deviceToken: String? = null
        @JvmField
        var deviceId: String? = null
        @JvmField
        var custNo: String? = null
        @JvmField
        var videoAutoPlayYn: String? = null
    }
}