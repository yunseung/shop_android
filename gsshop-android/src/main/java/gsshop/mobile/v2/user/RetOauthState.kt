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
 * SNS 로그인 관련 결과값이 전달될 클래스
 * 로그인이 실패하고 에러코드가 40번인경우만 봐야됨 ( 4x 에대한 에러코드 타입을 정의 한다면 4x 로 변경될수 있음)
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
class RetOauthState {
    /**
     * 로그인 실패("40")에 대한 디테일
     */
    enum class LOGIN_RESULT_TYPE {
        TYPE_EQUAL,  //SNS 이메일과 동일한 이메일이 GS SHOP 있음
        TYPE_OAUTH,  //SNS 인증완료
        TYPE_MAPPING,  //매핑 이력이 이미 있음
        TYPE_TEL,  //탈퇴이력이 있는 경우
        TYPE_ERROR //정의되지 않은 경우
    }

    /**
     * 로그인 실패 && 특정 에러코드인경우
     * TYPE_EQUAL   :  SNS 이메일과 동일한 이메일이 GS SHOP 있음  , 체크 / 볼드타이틀 / 이메일 / 일반글자 / 버튼 2개
     * TYPE_MAPPING : 매핑 이력이 이미 있음 , 체크 / 볼드타이틀 / 일반글자 2개 / 이메일 / 버튼 2개, 일반글자 / 문의 버튼 1개
     * TYPE_OATH    : SNS 인증완료 , 체크 / 볼드타이틀 / 일반글자 2개 / 이메일 / 버튼 2개, 일반글자 / 문의 버튼 1개
     * TYPE_TEL     : 탈퇴 이력이 있는 경우, 일반글자 / 문의 버튼 1개
     * TYPE_ERROR   : TYPE_TEL 동일하게 화면 UI입니다 디폴트 상태로 기획/디자인 변경 소지 있음
     *
     * 모든 켄텐츠는 디자인은 type에 종속 될수 있습니다
     * ex)
     * TYPE_EQUAL / TYPE_MAPPING 타입에 따라 email 의 위치가 달라짐
     * TYPE_OATH의 경우 groundMsg 라인 하단에 표시되어야함
     * TYPE_OATH의 경우 라인이 그려져야함
     */
    @JvmField
    var type: String? = null

    /**
     * 로그인 결과 팝업창 상단의 체크 박스의 유무이다
     * Y
     * N
     * 디폴튼 미표시
     */
    @JvmField
    var confirmYN: String? = null

    /**
     * "인증한 네이버… GS SHOP 회원 가입 이력이 있습니다"
     * 볼드체의 상단 타이틀로 줄바뀜이 포함되어 내려옵니다.
     * 줄바꿈에 대한 정책 필요 - 이민수대리
     */
    @JvmField
    var title: String? = null

    /**
     * 이메일
     */
    @JvmField
    var email: String? = null

    /**
     * 이메일 기준으로, 이메일 상단에 표시될 내용
     */
    @JvmField
    var msgTop: String? = null

    /**
     * 이메일 기준으로, 이메일 하단에 표시될 내용
     */
    @JvmField
    var msgBottom: String? = null

    /**
     * TYPE_OATH 과 같은 경우 라인 하단에 위치
     */
    @JvmField
    var groundMsg: String? = null

    /**
     * 회원가입 주소
     */
    @JvmField
    var joinUrl: String? = null

    /**
     * 패스워드 찾기 주소
     */
    @JvmField
    var searchUrl: String? = null

    /**
     * SNS 매핑 및 로그인 처리를 위한 주소
     */
    @JvmField
    var loginUrl: String? = null

    override fun toString(): String {
        return String.format("type:%s\nconfirmYN:%s\ntitle:%s\nemail:%s\nmsgTop:%s\nmsgBottom:%s\ngroundMsg:%s\njoinUrl:%s\nsearchUrl:%s\nloginUrl:%s",
                type, confirmYN, title, email, msgTop, msgBottom, groundMsg, joinUrl, searchUrl, loginUrl)
    }
}