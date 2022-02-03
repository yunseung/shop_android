/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user

import android.text.TextUtils
import com.gsshop.mocha.pattern.mvc.Model
import com.orhanobut.hawk.Hawk
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import javax.annotation.ParametersAreNullableByDefault

/**
 * 토큰기반 자동로그인을 위해 폰에 저장된 (복호화된) 정보.
 *
 *
 * 자동로그인이 아니더라도 폰에 아이디를 저장하는 용도로도 사용됨.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
class TokenCredentialsNew2 {
    /**
     * 로그인 아이디.
     */
    @JvmField
    var loginId: String? = null

    /**
     * 시리즈 키.
     */
    @JvmField
    var seriesKey: String? = null

    /**
     * 인증 토큰.
     */
    @JvmField
    var authToken: String? = null

    /**
     * 디바이스 아이디.
     */
    @JvmField
    var deviceId: String? = null

    /**
     * 시도되는 로그인에 관련된 snsType
     * KA - 카카오톡
     * NA - 네이버
     * GA - 구글 ( 비 대상 )
     */
    @JvmField
    var snsTyp: String? = null

    /**
     * 토큰로그인 정보를 폰에 저장.
     */
    fun save() {
        Hawk.put(LOGIN_ID_KEY, loginId)
        Hawk.put(SERIES_KEY_KEY, seriesKey)
        Hawk.put(AUTH_TOKEN_KEY, authToken)
        Hawk.put(DEVICE_ID_KEY, deviceId)
        Hawk.put(SNS_TYP_KEY, snsTyp)
    }

    /**
     * 토큰인증을 위한 모든 정보가 충분한가?
     *
     * @return boolean
     */
    fun sufficient(): Boolean {
        return (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(seriesKey)
                && !TextUtils.isEmpty(loginId) && !TextUtils.isEmpty(deviceId))
    }

    companion object {
        private const val LOGIN_ID_KEY = "_login_id_key"
        private const val SERIES_KEY_KEY = "_series_key_key"
        private const val AUTH_TOKEN_KEY = "_auth_token_key"
        private const val DEVICE_ID_KEY = "_device_id_key"
        private const val SNS_TYP_KEY = "_sns_typ_key"

        /**
         * 폰에 저장된 토큰로그인 정보 삭제.
         */
        @JvmStatic
        fun remove() {
            Hawk.delete(LOGIN_ID_KEY)
            Hawk.delete(SERIES_KEY_KEY)
            Hawk.delete(AUTH_TOKEN_KEY)
            Hawk.delete(DEVICE_ID_KEY)
            Hawk.delete(SNS_TYP_KEY)
        }

        /**
         * 폰에 저장된 토큰로그인 정보 복원.
         * 저장된 정보가 없으면 null 리턴.
         *
         * @return TokenCredentialsNew
         */
        @JvmStatic
        fun get(): TokenCredentialsNew2? {
            val tokenCredentials = TokenCredentialsNew2()
            tokenCredentials.loginId = Hawk.get(LOGIN_ID_KEY)
            tokenCredentials.seriesKey = Hawk.get(SERIES_KEY_KEY)
            tokenCredentials.authToken = Hawk.get(AUTH_TOKEN_KEY)
            tokenCredentials.deviceId = Hawk.get(DEVICE_ID_KEY)
            tokenCredentials.snsTyp = Hawk.get(SNS_TYP_KEY)
            return if (TextUtils.isEmpty(tokenCredentials.authToken) || TextUtils.isEmpty(tokenCredentials.seriesKey)) {
                null
            } else tokenCredentials
        }
    }
}