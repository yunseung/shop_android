/*
 * Copyright(C) 2013 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import com.gsshop.mocha.pattern.mvc.Model

@Model
class CspBannerModel {
    ////////////////////////////////////////////////
    //공통적으로 적용되는 타입
    ////////////////////////////////////////////////
    /**
     * 1. message : 홈등의 매장 에 보여질 타입
     * 2. banner : 배너의 타입
     */
    var type: String? = null

    /**
     * 누르면 이동 타켓 링크
     */
	@JvmField
	var LN: String? = null

    /**
     * 노출 직전 효율 URL ( 그리던 안그리던 던져야 한다)
     */
    var TL: String? = null

    /**
     * 캠페인 각 페이즈별 측정을 위한 유니크값 ( 내려줄때마다 다르다 )
     * 준대로 바로 던진다
     */
	@JvmField
	var AID: String? = null

    /**
     * 보여줄것이냐 말것이냐 눈깔만 Y면 보여줘 아니면 보여주지마
     */
	@JvmField
	var VI: String? = null
    ////////////////////////////////////////////////
    // banner 타입
    ////////////////////////////////////////////////
    /**
     * 이미지 링크 주소
     */
	@JvmField
	var I: String? = null

    /**
     * 네비게이션 아이디
     */
	@JvmField
	var P: String? = null
}