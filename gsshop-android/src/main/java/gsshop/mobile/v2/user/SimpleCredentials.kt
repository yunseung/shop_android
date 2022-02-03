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
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import javax.annotation.ParametersAreNullableByDefault

/**
 * 사용자 로그인 ID/PW.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
class SimpleCredentials {
    /**
     * 로그인 아이디
     */
    @JvmField
    var loginId: String? = null

    /**
     * 로그인 패스워드.
     */
    @JvmField
    var password: String? = null

    /**
     * 로그인 고객번호.
     */
    @JvmField
    var customerNumber: String? = null

    /**
     * 로그인 토큰값.
     */
    @JvmField
    var authToken: String? = null

    /**
     * sns snsAccess 토큰
     */
    @JvmField
    var snsAccess: String? = null

    /**
     * sns snsRefresh 토큰
     */
    @JvmField
    var snsRefresh: String? = null

    /**
     * 시도되는 로그인에 관련된 snsType
     * KA - 카카오톡
     * NA - 네이버
     * GA - 구글 ( 비 대상 )
     */
    @JvmField
    var snsTyp: String? = null

    /**
     * 시도되는 로그인에 관련된 snsType
     * TYPE_LOGIN	일반 로그인 시도
     * TYPE_OAUTH	ID/PW 없이 SNS 인증 정보만 던져서 매핑 여부를 확인하는 경우(로그인이 될수도 있고, 에러 가 떨어질수도 있다)
     * TYPE_MAPPING 입력된 ID/PW 와 SNS 정보를 던져 매핑 시켜달라는 경우(로그인됨)
     */
    @JvmField
    var loginTyp: String? = null

    /**
     * 토큰인증을 위한 모든 정보가 충분한가?
     * @return boolean
     */
    fun simpleSufficient(): Boolean {
        return (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(customerNumber)
                && !TextUtils.isEmpty(loginId))
    }
}