/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user

import com.gsshop.mocha.pattern.mvc.Model
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import javax.annotation.ParametersAreNullableByDefault

/**
 * 사용자의 각종 설정정보.
 *
 * - 로그인
 * - 간편결제 인증
 * - 페이스북/트위터
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
class UserSetting {
    /**
     * 로그인한 경우 로그인 아이디
     */
    var loginId: String? = null

    /**
     * 간편주문 인증 유지 여부
     */
    var quickOrder = false
    /**
     * 페이스북 연동 여부
     */
    // NOTE : 나중에 적용
    //public boolean facebook;
    /**
     * 트위터 연동 여부
     */
    var twitter = false
}