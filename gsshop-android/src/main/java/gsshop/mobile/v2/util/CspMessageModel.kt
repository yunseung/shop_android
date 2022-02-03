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
class CspMessageModel {
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
     * 보여줄것이냐 말것이냐 눈깔만 Y면 보여줘 아니면 보여주지마
     */
	@JvmField
	var VI: String? = null
    ////////////////////////////////////////////////
    // meesage 타입
    ////////////////////////////////////////////////
    /**
     * 그릴 아이콘 타입
     * TP : view     눈
     * TP : save     돈
     * TP : tip      티비
     * TP : alert    종
     */
	@JvmField
	var TP: String? = null

    /**
     * 화이트 타이틀
     */
	@JvmField
	var M0: String? = null

    /**
     * 컬러 타이틀
     */
	@JvmField
	var M1: String? = null

    /**
     * 화이트 타이틀
     */
	@JvmField
	var M2: String? = null
}