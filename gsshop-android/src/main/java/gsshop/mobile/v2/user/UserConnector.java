/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user;

import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestGet;
import com.gsshop.mocha.network.rest.RestPost;
import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import roboguice.inject.ContextSingleton;

/**
 * 사용자 관련 서버연동 작업.
 *
 * 서버연결 주소에 SSL 적용함.
 * 
 */
@ContextSingleton
@Rest(resId = R.string.server_https_root)
public class UserConnector {

//    private final String ROOT = ServerUrls.HTTPS_ROOT;

    /**
     * 로그인 리턴 결과.
     *  2014.01.17 parksegun EC통합 재구축으로 모델 Getter/Setter 적용
     * 표준화에 따른 변수명 변경 (다른 모델에서 상속)
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class LoginResult {
        /**
         * 사용자 이름
         */
        private String custNm;

        /**
         * 고객번호
         */
        private String custNo;

        /**
         * 고객타입
         */
        private String custType;

        /**
         * 고객등급
         */
        private String grade;

        /**
         * 거래거절 고객 여부
         */
        private String txnRfuseCustYn;

        /**
         * 사용자 아이디
         */
        private String loginId;

        /**
         * @return the custNm
         */
        public String getCustNm() {
            return custNm;
        }

        /**
         * @param custNm the custNm to set
         */
        public void setCustNm(String custNm) {
            this.custNm = custNm;
        }

        /**
         * @return the custNo
         */
        public String getCustNo() {
            return custNo;
        }

        /**
         * @param custNo the custNo to set
         */
        public void setCustNo(String custNo) {
            this.custNo = custNo;
        }

        /**
         *
         * @return the custType
         */
        public String getCustType() {
            return custType;
        }

        /**
         *
         * @param custType the custType to set
         */
        public void setCustType(String custType) {
            this.custType = custType;
        }

        /**
         *
         * @return the grade
         */
        public String getGrade() {
            return grade;
        }

        /**
         *
         * @param grade the grade to set
         */
        public void setGrade(String grade) {
            this.grade = grade;
        }

        /**
         * @return the loginId
         */
        public String getLoginId() {
            return loginId;
        }

        /**
         * @param loginId the loginId to set
         */
        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        /**
         * @return the succs
         */
        public boolean isSuccs() {
            return succs;
        }

        /**
         * @param succs the succs to set
         */
        public void setSuccs(boolean succs) {
            this.succs = succs;
        }

        /**
         * @return the succMsg
         */
        public String getSuccMsg() {
            return succMsg;
        }

        /**
         * @param succMsg the succMsg to set
         */
        public void setSuccMsg(String succMsg) {
            this.succMsg = succMsg;
        }

        /**
         * @return the errMsg
         */
        public String getErrMsg() {
            return errMsg;
        }

        /**
         * @param errMsg the errMsg to set
         */
        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }

        /**
         * @return the retTyp
         */
        public String getRetTyp() {
            return retTyp;
        }

        /**
         * @param retTyp the retTyp to set
         */
        public void setRetTyp(String retTyp) {
            this.retTyp = retTyp;
        }

        /**
         * @return the retUrl
         */
        public String getRetUrl() {
            return retUrl;
        }

        public String getSnsTyp() {
            return snsTyp;
        }

        public void setSnsTyp(String snsTyp) {
            this.snsTyp = snsTyp;
        }

        /**
         * @param retUrl the retUrl to set
         */
        public void setRetUrl(String retUrl) {
            this.retUrl = retUrl;
        }

        public RetOauthState getRetOauthState() {
            return retOauthState;
        }

        public void setRetOauthState(RetOauthState retOauthState) {
            this.retOauthState = retOauthState;
        }

        /**
         * 로그인 성공여부
         */
        private boolean succs = false;

        /**
         * 로그인 성공시 표시할 메시지
         */
        private String succMsg = "";

        /**
         * 로그인 실패시 에러메시지
         */
        private String errMsg = "";

        /**
         * 로그인 결과 타임 타입
         */
        private String retTyp = "";

        /**
         * 로그인 타입별 URL 리턴
         */
        private String retUrl = "";

        /**
         * sns로그인 유형 URL 리턴
         * KA
         * NA
         * GA
         */
        private String snsTyp = "";

        /**
         * sns로그인 관련 실패 유형
         *
         * succs 가 아닌경우
         * retTyp = 40인경우 협의 예정
         * 봐야될 클래스
        */
        private RetOauthState retOauthState;

        /**
         * 비밀번호 변경 필요 여부 "Y" or "N"
         */
        private String passwdNeedChgYn;

        /**
         * 비밀번호 최근 수정일자
         */
        private String passwdLastModDt;

        /**
         * 비밀번호 변경 Url
         */
        private String passwdChgUrl;

        public String getPasswdNeedChgYn() {
            return passwdNeedChgYn;
        }

        public void setPasswdNeedChgYn(String passwdNeedChgYn) {
            this.passwdNeedChgYn = passwdNeedChgYn;
        }

        public String getPasswdLastModDt() {
            return passwdLastModDt;
        }

        public void setPasswdLastModDt(String passwdLastModDt) {
            this.passwdLastModDt = passwdLastModDt;
        }

        public String getPasswdChgUrl() {
            return passwdChgUrl;
        }

        public void setPasswdChgUrl(String passwdChgUrl) {
            this.passwdChgUrl = passwdChgUrl;
        }

        /**
         * 비밀번호 변경 필요 여부 "Y" or "N"
         */
        private String tvCustPasswdNeedChgYn;

        /**
         * 비밀번호 최근 수정일자
         */
        private String tvCustPasswdLastModDt;

        /**
         * 비밀번호 변경 Url
         */
        private String tvCustPasswdChgUrl;

        public String getPasswdTvNeedChgYn() {
            return tvCustPasswdNeedChgYn;
        }

        public void setpasswdTvNeedChgYn(String passwdTvNeedChgYn) {
            this.tvCustPasswdNeedChgYn = passwdTvNeedChgYn;
        }

        public String getpasswdTvLastModDt() {
            return tvCustPasswdLastModDt;
        }

        public void setpasswdTvLastModDt(String passwdTvLastModDt) {
            this.tvCustPasswdLastModDt = passwdTvLastModDt;
        }

        public String getpasswdTvChgUrl() {
            return tvCustPasswdChgUrl;
        }

        public void setpasswdTvChgUrl(String passwdTvChgUrl) {
            this.tvCustPasswdChgUrl = passwdTvChgUrl;
        }

        public String getTxnRfuseCustYn() {
            return txnRfuseCustYn;
        }

        public void setTxnRfuseCustYn(String txnRfuseCustYn) {
            this.txnRfuseCustYn = txnRfuseCustYn;
        }

        /**
         * SSO 토큰
         */
        private String ssoToken;

        /**
         * 통합회원 안내 URL
         */
        private String intgMemGuideUrl;

        /**
         * SSO 응답코드
         * -00 : 정상 (응답 SSO 토큰 으로 업데이트)
         * -10 : 통합회원 아님 (기존 SSO 토큰 삭제)
         * -20 : SSO 서버 연결 실패 (기존 SSO 토큰 유지)
         * -30 : SSO 사용여부 N (기존 SSO 토큰 삭제)
         * -40 : SSO 토큰 비정상 (기존 SSO 토큰 삭제)
         */
        private String ssoRetCd;

        public String getSsoToken() {
            return ssoToken;
        }

        public void setSsoToken(String ssoToken) {
            this.ssoToken = ssoToken;
        }

        public String getIntgMemGuideUrl() {
            return intgMemGuideUrl;
        }

        public void setIntgMemGuideUrl(String intgMemGuideUrl) {
            this.intgMemGuideUrl = intgMemGuideUrl;
        }

        public String getSsoRetCd() {
            return ssoRetCd;
        }

        public void setSsoRetCd(String ssoRetCd) {
            this.ssoRetCd = ssoRetCd;
        }

        @Override
        public String toString() {
            return String.format("succs:%s\nsuccMsg:%s\nerrMsg:%s\nretTyp:%s\nretUrl:%s\nsnsTyp:" +
                            "%s\ncustNm:%s\ncustNo:%s\nloginId:%s\nretOauthState:%s\nssoRetCd:%s\nssoToken:%s\nintgMemGuideUrl:%s",
                    succs, succMsg, errMsg, retTyp, retUrl, snsTyp, custNm, custNo,
                    loginId, retOauthState != null ? retOauthState.toString() : null, ssoRetCd, ssoToken, intgMemGuideUrl);
        }
    }

    /**
     * 토큰기반 간편로그인 : 신규 로그인
     * 2014.01.17 parksegun EC통합 재구축으로 인한 URL 변경
     * @param param param
     * @return LoginTokenResult
     */
    @RestPost(ServerUrls.REST.LOGIN_FOR_TOKEN)
    public LoginTokenResult loginForTokenNew(LoginTokenParamNew param) {
        return null;
    }

    /**
     * 디바이스아이디 기반 로그인
     * @param param param
     * @return LoginTokenResult
     */
    @RestPost(ServerUrls.REST.LOGIN_FOR_TOKEN_WITH_DEVICE)
    public LoginTokenResult loginForTokenNewFromWeb(LoginTokenParamNew param) {
        return null;
    }

    /**
     * 토큰기반 자동로그인 : 토큰을 통한 인증
     * 2014.01.17 parksegun EC통합 재구축으로 인한 URL 변경
     * @param param param
     * @return AuthTokenResult
     */
    @RestPost(ServerUrls.REST.AUTH_BY_TOKEN)
    public AuthTokenResult authByToken(AuthTokenParam param) {
        return null;
    }

    /**
     * 토큰기반 자동로그인 : 로그아웃
     * 2014.01.16 parksegun EC통합 재구축으로 인한 URL 변경
     * Retrun 추가
     * @param param param
     * @return LogoutTokenResult
     */
    @RestPost(ServerUrls.REST.LOGOUT_FOR_TOKEN)
    public LogoutTokenResult logoutForToken(LogoutTokenParam param) {
        return null;
    }

    /**
     * 설정 조회
     * @return UserSetting
     */
    @RestGet(ServerUrls.REST.API_PATH + ServerUrls.REST.USER_SETTING)
    public UserSetting getUserSetting() {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////// Model /////////////////////////////////////////////////////////////////////////////

    // 간편 로그인
    /**
     *  2014.01.17 parksegun EC통합 재구축으로 모델 Getter/Setter 적용
     * 표준화에 따른 변수명 변경
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class LoginTokenParamNew {

        /**
         * @return the deviceId
         */
        public String getDeviceId() {
            return deviceId;
        }

        /**
         * @param deviceId the deviceId to set
         */
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        /**
         * @return the loginId
         */
        public String getLoginId() {
            return loginId;
        }

        /**
         * @param loginId the loginId to set
         */
        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        /**
         * @return the passwd
         */
        public String getPasswd() {
            return passwd;
        }

        /**
         * @param passwd the passwd to set
         */
        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        /**
         * @return the easiLogin
         */
        public String getEasiLogin() {
            return easiLogin;
        }

        /**
         * @param easiLogin the easiLogin to set
         */
        public void setEasiLogin(String easiLogin) {
            this.easiLogin = easiLogin;
        }

        public String getSnsAccess() {
            return snsAccess;
        }

        public void setSnsAccess(String snsAccess) {
            this.snsAccess = snsAccess;
        }

        public String getSnsRefresh() {
            return snsRefresh;
        }

        public void setSnsRefresh(String snsRefresh) {
            this.snsRefresh = snsRefresh;
        }

        public String getSnsTyp() {
            return snsTyp;
        }

        public void setSnsTyp(String snsTyp) {
            this.snsTyp = snsTyp;
        }

        public String getLoginTyp() {
            return loginTyp;
        }

        public void setLoginTyp(String loginTyp) {
            this.loginTyp = loginTyp;
        }

        public String getSsoToken() {
            return ssoToken;
        }

        public void setSsoToken(String ssoToken) {
            this.ssoToken = ssoToken;
        }

        /**
         * 디바이스 아이디.
         */
        private String deviceId;

        /**
         * 로그인 아이디.
         */
        private String loginId;

        /**
         * 로그인 패스워드.
         */
        private String passwd;

        /**
         * 간편로그인 신청. -> 자동로그인 여부 ?
         */
        private String easiLogin;

        /**
         * sns snsAccess 토큰
         */
        private String snsAccess;

        /**
         * sns snsRefresh 토큰
         */
        private String snsRefresh;

        /**
         * 시도되는 로그인에 관련된 snsType
         * KA - 카카오톡
         * NA - 네이버
         * GA - 구글 ( 비 대상 )
         */
        private String snsTyp;

        /**
         * 시도되는 로그인에 관련된 snsType
         * TYPE_LOGIN	일반 로그인 시도
         * TYPE_OAUTH	ID/PW 없이 SNS 인증 정보만 던져서 매핑 여부를 확인하는 경우(로그인이 될수도 있고, 에러 가 떨어질수도 있다)
         * TYPE_MAPPING 입력된 ID/PW 와 SNS 정보를 던져 매핑 시켜달라는 경우(로그인됨)
         */
        private String loginTyp;

        /**
         * SSO 토큰
         */
        private String ssoToken;

        @Override
        public String toString() {
            return String.format("deviceId:%s\nloginId:%s\npasswd:%s\neasiLogin:%s\nsnsAccess:%s\nsnsRefresh:%s\nsnsTyp:%s\nloginTyp:%s\nssoToken:%s\n",
                    deviceId, loginId, passwd, easiLogin, snsAccess, snsRefresh, snsTyp, loginTyp, ssoToken);
        }
    }

    /**
     * 2014.01.17 parksegun EC통합 재구축으로 모델 Getter/Setter 적용
     * 표준화에 따른 변수명 변경
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class AuthTokenParam {

        /**
         * 디바이스 아이디.
         */
        private String deviceId;

        /**
         * 로그인 아이디.
         */
        private String loginId;

        /**
         * 시리즈 키.
         */
        private String serisKey;

        /**
         * 인증 토큰
         */
        private String certToken;

        /**
         * 자동로그인 타입
         * 없거나
         * NA
         * KA
         * GA
         */
        private String snsTyp;

        /**
         * SSO 토큰
         */
        private String ssoToken;

        /**
         * @return the deviceId
         */
        public String getDeviceId() {
            return deviceId;
        }

        /**
         * @param deviceId the deviceId to set
         */
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        /**
         * @return the loginId
         */
        public String getLoginId() {
            return loginId;
        }

        /**
         * @param loginId the loginId to set
         */
        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        /**
         * @return the serisKey
         */
        public String getSerisKey() {
            return serisKey;
        }

        /**
         * @param serisKey the serisKey to set
         */
        public void setSerisKey(String serisKey) {
            this.serisKey = serisKey;
        }

        /**
         * @return the certToken
         */
        public String getCertToken() {
            return certToken;
        }

        /**
         * @param certToken the certToken to set
         */
        public void setCertToken(String certToken) {
            this.certToken = certToken;
        }

        /**
         * @return the snsTyp
         */
        public String getSnsTyp() {
            return snsTyp;
        }

        /**
         * @param snsTyp the snsTyp to set
         */
        public void setSnsTyp(String snsTyp) {
            this.snsTyp = snsTyp;
        }

        public String getSsoToken() {
            return ssoToken;
        }

        public void setSsoToken(String ssoToken) {
            this.ssoToken = ssoToken;
        }

        @Override
        public String toString() {
            return String.format("deviceId:%s\nloginId:%s\nserisKey:%s\ncertToken:%s\nsnsTyp:%s\nssoToken:%s",
                    deviceId, loginId, serisKey, certToken, snsTyp, ssoToken);
        }
    }

    /**
     * 2014.01.16 parksegun EC통합 재구축으로 모델 Getter/Setter 적용
     * 표준화에 따른 변수명 변경
     *
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class LogoutTokenParam {

        /**
         * 디바이스 아이디.
         */
        private String deviceId;

        /**
         * 로그인 아이디.
         */
        private String loginId;

        /**
         * 시리즈 키.
         */
        private String serisKey;

        /**
         * SSO 토큰
         */
        private String ssoToken;

        /**
         * @return the deviceId
         */
        public String getDeviceId() {
            return deviceId;
        }

        /**
         * @param deviceId the deviceId to set
         */
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        /**
         * @return the loginId
         */
        public String getLoginId() {
            return loginId;
        }

        /**
         * @param loginId the loginId to set
         */
        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        /**
         * @return the serisKey
         */
        public String getSerisKey() {
            return serisKey;
        }

        /**
         * @param serisKey the serisKey to set
         */
        public void setSerisKey(String serisKey) {
            this.serisKey = serisKey;
        }

        public String getSsoToken() {
            return ssoToken;
        }

        public void setSsoToken(String ssoToken) {
            this.ssoToken = ssoToken;
        }
    }

    /**
     * 토큰기반 자동로그인 : 신규 로그인 리턴 결과.
      * 2014.01.17 parksegun EC통합 재구축으로 모델 Getter/Setter 적용
     * 표준화에 따른 변수명 변경
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class LoginTokenResult extends LoginResult {

        /**
         * 시리즈 키.
         */
        private String serisKey;

        /**
         * 인증 토큰
         */
        private String certToken;

        /**
         * @return the serisKey
         */
        public String getSerisKey() {
            return serisKey;
        }

        /**
         * @param serisKey the serisKey to set
         */
        public void setSerisKey(String serisKey) {
            this.serisKey = serisKey;
        }

        /**
         * @return the certToken
         */
        public String getCertToken() {
            return certToken;
        }

        /**
         * @param certToken the certToken to set
         */
        public void setCertToken(String certToken) {
            this.certToken = certToken;
        }
    }

    /**
     * 토큰기반 자동로그인 : 토큰기반 인증 리턴 결과.
     * 2014.01.17 parksegun EC통합 재구축으로 모델 Getter/Setter 적용
     * 표준화에 따른 변수명 변경
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class AuthTokenResult extends LoginResult {

        /**
         * 인증 토큰
         */
        private String certToken;

        /**
         * @return the certToken
         */
        public String getCertToken() {
            return certToken;
        }

        /**
         * @param certToken the certToken to set
         */
        public void setCertToken(String certToken) {
            this.certToken = certToken;
        }
    }


    /**
     *
     * @param s s
     * @return String
     */
    public String getName(String s){
        return "";
    }

    /**
     *  간편로그인 인증 Param
     *  2014.01.17 parksegun EC통합 재구축으로 모델 Getter/Setter 적용
     * 표준화에 따른 변수명 변경
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class SimpleLoginParam {

        /**
         * 디바이스 아이디.
         */
        private String deviceId;

        /**
         * 로그인 아이디.
         */
        private String loginId;

        /**
         * @return the deviceId
         */
        public String getDeviceId() {
            return deviceId;
        }

        /**
         * @param deviceId the deviceId to set
         */
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        /**
         * @return the loginId
         */
        public String getLoginId() {
            return loginId;
        }

        /**
         * @param loginId the loginId to set
         */
        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        /**
         * @return the custNo
         */
        public String getCustNo() {
            return custNo;
        }

        /**
         * @param custNo the custNo to set
         */
        public void setCustNo(String custNo) {
            this.custNo = custNo;
        }

        /**
         * @return the certToken
         */
        public String getCertToken() {
            return certToken;
        }

        /**
         * @param certToken the certToken to set
         */
        public void setCertToken(String certToken) {
            this.certToken = certToken;
        }

        /**
         * 고객번호.
         */
        private String custNo;

        /**
         * 토큰값.
         */
        private String certToken;

    }

    /**
     * 2014.01.16 parksegun EC통합 재구축
     * 토큰 로그아웃시 Return 추가됨. 
     *
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class LogoutTokenResult {

        /**
         * 로그인 성공시 true 실패시 false
         */
        private boolean succs = false;
        /**
         * "실패시 에러메시지" 
         */
        private String errMsg = "";

        /**
         * @return the succs
         */
        public boolean isSuccs() {
            return succs;
        }

        /**
         * @param succs the succs to set
         */
        public void setSuccs(boolean succs) {
            this.succs = succs;
        }

        /**
         * @return the errMsg
         */
        public String getErrMsg() {
            return errMsg;
        }

        /**
         * @param errMsg the errMsg to set
         */
        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }
    }

    /**
     * SSO 조회 API 요청 파라미터
     */
    @Model
    public static class SSOQueryParam {
        /**
         * 디바이스 아이디
         */
        private String deviceId;

        /**
         * 고객번호
         */
        private String custNo;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getCustNo() {
            return custNo;
        }

        public void setCustNo(String custNo) {
            this.custNo = custNo;
        }
    }

    /**
     * SSO 변경 API 요청 파라미터
     */
    @Model
    public static class SSOUseParam extends SSOQueryParam {
        /**
         * SSO 사용여부 (Y: 사용, N: 미사용)
         */
        private String ssoUseYn;

        public String getSsoUseYn() {
            return ssoUseYn;
        }

        public void setSsoUseYn(String ssoUseYn) {
            this.ssoUseYn = ssoUseYn;
        }
    }

    /**
     * SSO 변경 API 응답 결과
     */
    @Model
    public static class SSOUseResult {
        /**
         * 성공여부
         */
        private boolean succs = false;

        /**
         * 에러메시지
         */
        private String errMsg = "";

        public boolean isSuccs() {
            return succs;
        }

        public void setSuccs(boolean succs) {
            this.succs = succs;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }
    }

    /**
     * SSO 조회 API 응답 결과
     */
    @Model
    public static class SSOQueryResult extends SSOUseResult {
        /**
         * SSO 사용여부 (Y: 사용, N: 미사용, E: 통합회원아님)
         */
        private String ssoUseYn;

        public String getSsoUseYn() {
            return ssoUseYn;
        }

        public void setSsoUseYn(String ssoUseYn) {
            this.ssoUseYn = ssoUseYn;
        }
    }

    /**
     * SSO SDK 호출 및 응답 파라미터
     */
    @Model
    public static class SSOSdkParam {

        private String result;

        private String chnnlCode;

        private String ssoAuthToken;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getChnnlCode() {
            return chnnlCode;
        }

        public void setChnnlCode(String chnnlCode) {
            this.chnnlCode = chnnlCode;
        }

        public String getSsoAuthToken() {
            return ssoAuthToken;
        }

        public void setSsoAuthToken(String ssoAuthToken) {
            this.ssoAuthToken = ssoAuthToken;
        }
    }
}
