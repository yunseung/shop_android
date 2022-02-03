/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.security.AuthenticationException;
import com.gsshop.mocha.security.InvalidLoginException;
import com.gsshop.mocha.web.WebViewCookieManager;
import com.tms.sdk.api.request.Login;
import com.tms.sdk.bean.APIResult;

import org.apache.http.NameValuePair;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractTMSService;
import gsshop.mobile.v2.BuildConfig;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Events.LoggedInEvent;
import gsshop.mobile.v2.Events.LoggedOutEvent;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeGroupInfoAction;
import gsshop.mobile.v2.menu.navigation.NavigationManager;
import gsshop.mobile.v2.push.PushSettings;
import gsshop.mobile.v2.setting.FingerPrintSettings;
import gsshop.mobile.v2.sso.SSOControl;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls;
import gsshop.mobile.v2.user.UserConnector.AuthTokenParam;
import gsshop.mobile.v2.user.UserConnector.AuthTokenResult;
import gsshop.mobile.v2.user.UserConnector.LoginResult;
import gsshop.mobile.v2.user.UserConnector.LoginTokenParamNew;
import gsshop.mobile.v2.user.UserConnector.LoginTokenResult;
import gsshop.mobile.v2.user.UserConnector.LogoutTokenParam;
import gsshop.mobile.v2.user.UserConnector.LogoutTokenResult;
import gsshop.mobile.v2.user.UserConnector.SSOQueryParam;
import gsshop.mobile.v2.user.UserConnector.SSOQueryResult;
import gsshop.mobile.v2.user.UserConnector.SSOUseParam;
import gsshop.mobile.v2.user.UserConnector.SSOUseResult;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.RestClientUtils;
import gsshop.mobile.v2.util.SSOUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.util.LunaUtils.sendAutoLoginError;
import static gsshop.mobile.v2.util.LunaUtils.sendAutoLoginSuccess;
import static gsshop.mobile.v2.util.SSOUtils.hasSSOToken;

/**
 * 사용자 관련 비즈니스 로직.
 */
@ContextSingleton
public class UserAction {

    private final Context context;

    @Inject
    private RestClient restClient;

    @Inject
    public UserAction(Context context) {
        this.context = context;
    }

    @Inject
    protected HomeGroupInfoAction homeGroupInfoAction;

    // 간편로그인 통신
    public LoginResult loginNew(SimpleCredentials data, LoginOption option) {
        return new LoginForTokenNew(option).login(data);
    }

    // 웹호출에 의한 로그인
    public LoginResult loginFromWeb(SimpleCredentials data, LoginOption option) {
        return new LoginForTokenNewFromWeb(option).login(data);
    }

    /**
     * 토큰 기반 인증.
     * <p>
     * 로그인 성공시 :
     * - 사용자 정보를 캐시
     * - 토큰인증 정보를 갱신하여 저장
     * - REST의 모든 쿠키를 WebView에 복사
     * - {@link LoggedInEvent} 이벤트 발생시킴
     *
     * @param model model
     * @return LoginResult
     * @throws InvalidLoginException   로그인 실패시 'errorMessage' 값을 사용자 메시지로 설정함.
     * @throws AuthenticationException 토큰 정보가 없거나 서버 응답 결과가 없는 경우
     */
    public LoginResult auth(TokenCredentialsNew2 model) {
        //ssoToken 값이 존재하면 model의 유효성 여부와 무관하게 무조건 자동로그인 수행
        if (!hasSSOToken()
                && (model == null || !model.sufficient())) {
            throw new AuthenticationException();
        }
        return new AuthByToken().login(model);
    }

    /**
     * 로그 아웃.
     * <p>
     * - WebView에서 세션쿠키를 삭제
     * - {@link RestClient}에서는 모든 쿠키를 삭제
     * - 캐시된 사용자 정보 삭제
     * - 저장된 로그인 정보 삭제(로그인 유지를 위한 토큰 등)
     * - {@link LoggedOutEvent} 이벤트 발생시킴
     *
     * @param bRefresh bRefresh
     * @return LogoutTokenResult
     */
    public LogoutTokenResult logout(boolean bRefresh) {

        //로그아웃 경우 PMS API 호출 요구에 따라 캐쉬된 DB 지울수 있도록 추후 요청
        AbstractTMSService.logoutPMS(context);

        invalidateUserSession();

        //홈 네비게이션 다시 로딩 (VIP라운지의 경우 로그아웃 시 노출되면 안됨)
        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                homeGroupInfoAction.getHomeGroupInfo(context, true);
            } catch (Exception e) {
                Ln.e(e);
            }
        });

        TokenCredentialsNew2 loginModel = TokenCredentialsNew2.get();
        LoginOption option = LoginOption.get();
        LogoutTokenResult result = null;

        FingerPrintSettings fingerPrintSettings = FingerPrintSettings.get();
        //로그아웃에서 지문인식이 맵핑되있을경우에는 서버에 로그아웃을 보내지 않고 앱에서 처리한다.
        if (fingerPrintSettings.isFingerprintMapping) {
            if (bRefresh) {
                postLoggedOutEventWithDelay();
            }
            option.keepLogin = false;
            option.save();
            //로그아웃시 네비게이션 카테고리 데이터 삭제
            NavigationManager.isNavigationLogin = true;
            EventBus.getDefault().post(new Events.NavigationReflashEvent());
            Ln.i("로그아웃 완료.");
            LogoutTokenResult rsult = new LogoutTokenResult();
            rsult.setSuccs(true);

            //2019 01 14 캐쉬 삭제 추가
            //로그인 성공시 캐쉬 처리
            //(개인화 데이터가 갱신되도록 5분 캐쉬는 유지
            // 상하단 리플래쉬는 과거 개인화탭(디폴트/홈) 네비게이션이였다.
            MainApplication.clearCache();

            return rsult;
        }

        try {
            if (option != null && option.keepLogin) {
                result = processLogoutWithKeepLogin(loginModel, option);
                // 해더크기 한도 초과시 해더일부만 앱으로 전달되어 result 값이 null 상태로 됨
                // 이때도 ssoToken 제거
                if (isEmpty(result) || (isNotEmpty(result) && result.isSuccs())) {
                    SSOControl.getInstance().saveToken(SSOUtils.getSSOParamType2(""));
                }
            }
        } catch (Exception e) {
            Ln.e(e); // 서버통신 오류 발생시 무시
        }

        removeTokenLoginModel(loginModel, option);

        if (bRefresh) {
            postLoggedOutEventWithDelay();
        }

        //로그아웃시 네비게이션 카테고리 데이터 삭제
        NavigationManager.isNavigationLogin = true;
        EventBus.getDefault().post(new Events.NavigationReflashEvent());
        Ln.i("로그아웃 완료.");

        //로그인 성공시 캐쉬 처리
        //(개인화 데이터가 갱신되도록 5분 캐쉬는 유지
        // 상하단 리플래쉬는 과거 개인화탭(디폴트/홈) 네비게이션이였다.
        MainApplication.clearCache();

        return result;
    }

    /**
     * 로그아웃 이벤트를 일정시간 딜레이 후 전송한다.
     * (마이샵웹뷰가 로그인접근으로 변경되면서 불필요한 로그인화면을 띄우는 것을 방지하기 위함)
     */
    private void postLoggedOutEventWithDelay() {
        ThreadUtils.INSTANCE.runInUiThread(() -> EventBus.getDefault().post(new LoggedOutEvent()), 1500);
    }

    /**
     * 토큰 로그아웃 처리.
     *
     * @param loginModel loginModel
     * @param option     option
     * @return LogoutTokenResult
     */
    private LogoutTokenResult processLogoutWithKeepLogin(TokenCredentialsNew2 loginModel,
                                                         LoginOption option) {
        if (loginModel == null) {
            return null;
        }
        LogoutTokenParam param = new LogoutTokenParam();
        param.setDeviceId(loginModel.deviceId);
        param.setLoginId(loginModel.loginId);
        param.setSerisKey(loginModel.seriesKey);
        param.setSsoToken(SSOControl.getInstance().getLocalToken());

        return RestClientUtils.INSTANCE.post(restClient, param,
                ServerUrls.getHttpsRoot() + ServerUrls.REST.LOGOUT_FOR_TOKEN, LogoutTokenResult.class);
    }

    /**
     * 로그인된 사용자의 세션을 무효화.
     * <p>
     * - WebView에서 세션쿠키를 삭제
     * - {@link RestClient}에서는 모든 쿠키를 삭제
     * - 캐시된 사용자 정보 삭제
     */
    public void invalidateUserSession() {
        // 기존 웹뷰 세션쿠키 삭제.
        // 앱을 종료해도 세션쿠키는 자동으로 제거되지 않음
        WebViewCookieManager.removeAllSessionCookies(context);

        // RestClient의 모든 쿠키 삭제.
        // 사용자에 의한 명시적인 앱 종료 후 앱을 다시 실행시
        // 기존 RestClient 인스턴스가 재사용될 수 있음
        if (restClient != null) {
            restClient.removeAllCookies();
        }

        User.clearUserCache();
    }

    /**
     * 폰에 저장된 로그인 정보 제거.
     * 단, '아이디 저장' 상태가 true이면 아이디는 지우지 않는다.
     *
     * @param token  token
     * @param option option
     */
    private void removeTokenLoginModel(TokenCredentialsNew2 token, LoginOption option) {
        if (token == null) {
            return;
        }
        if (option != null && option.rememberLoginId) {
            //loginId만 남겨두고 나머지 항목 제거
            token.authToken = null;
            token.deviceId = null;
            token.seriesKey = null;
            token.snsTyp = null;
            token.save();
        } else {
            TokenCredentialsNew2.remove();
        }
        Ln.i("저장된 로그인 정보 제거됨.");
    }

    /**
     * 토근기반 자동 로그인 처리.
     */
    private class AuthByToken extends
            LoginTemplate<TokenCredentialsNew2, AuthTokenParam, AuthTokenResult> {

        @Override
        protected AuthTokenParam prepare(TokenCredentialsNew2 data) {
            AuthTokenParam param = new AuthTokenParam();
            if (isNotEmpty(data)) {
                param.setCertToken(data.authToken);
                param.setDeviceId(data.deviceId);
                param.setLoginId(data.loginId.trim());
                param.setSerisKey(data.seriesKey);
                param.setSnsTyp(data.snsTyp);
            }
            param.setSsoToken(SSOControl.getInstance().getLocalToken());
            return param;
        }

        @Override
        protected AuthTokenResult connect(AuthTokenParam param) {
            //WebView 쿠키를 RestClient에 복사
            CookieUtils.syncWebViewCookiesToRestClient(context, restClient);
            return RestClientUtils.INSTANCE.post(restClient, param,
                    ServerUrls.getHttpsRoot() + ServerUrls.REST.AUTH_BY_TOKEN, AuthTokenResult.class);
        }

        @Override
        protected User handle(TokenCredentialsNew2 data, AuthTokenParam param, AuthTokenResult result) {
            // 인증토큰 갱신
            if (isNotEmpty(data)) {
                data.authToken = result.getCertToken();
                data.save();
            }

            User user = new User();
            user.loginId = param.getLoginId();
            user.setUserName(result.getCustNm());
            user.setUserGrade(result.getGrade());
            user.customerNumber = result.getCustNo();
            user.isAutoLogin = "Y";
            user.setLoginResult(result);

            sendAutoLoginSuccess(context, new Exception());

            return user;
        }

        @Override
        protected void error(AuthTokenResult result) {
            sendAutoLoginError(context, new Exception(), result, false);
        }
    }

    private abstract class LoginTemplate<Data, Param, Result extends LoginResult> {

        private Dialog dialog;

        private void init() {
            restClient.removeAllCookies();
        }

        protected abstract Param prepare(Data data);

        protected abstract Result connect(Param param);

        /**
         * 로그인 성공시 응답결과를 처리.
         *
         * @param data   data
         * @param param  param
         * @param result result
         * @return User
         */
        protected abstract User handle(Data data, Param param, Result result);

        /**
         * 로그인 성공시 마무리 작업.
         *
         * @param user   user
         * @param result LoginResult
         */
        private void complete(User user, Result result) {
            // 사용자정보 캐시
            if (user != null) {
                // REST 쿠키를 웹뷰에 복사(앱-웹 로그인 유지)
                CookieUtils.copyRestClientCookiesToWebView(context, restClient);
//                NavigationManager.getInstance().clearNavigationCategory();
                EventBus.getDefault().post(new Events.LoggedInEvent(user));

                user.cacheUser();
                //로그인 완료시 PMS API 호출 (고객 아이디 로컬 셋팅)
                AbstractTMSService.setCustID(user.customerNumber);

                //google-play-services.jar를 최신버전으로 업데이트하면서, 65536 메서드 초과 문제가 발생하여 facebook.jar 제거 후 아래코드 주석처리
                /*try {
                    AppEventsLogger logger = AppEventsLogger.newLogger(context);
                    //logger.logEvent("login");
                } catch (Exception e) {
                    // TODO: handle exception
                }*/
                // facebook app login event
                if ("N".equals(user.isAutoLogin)) {

                    Ln.d("OnMessageReceived, isAutoLogin is N");
                    try {
                        AbstractTMSService.loginPMS(context, user.customerNumber, new Login.Callback() {
                            @Override
                            public void response(APIResult apiResult, String s, String s1) {
                                ThreadUtils.INSTANCE.runInUiThread(() -> {
                                    //푸시 설정 정보 업데 이트
                                    PushSettings settings = PushSettings.get();

                                    String rejectedCust = result.getTxnRfuseCustYn();
                                    // 거래거절 대상 고객 이며 최초 1회만 체크 해야 하기 때문에 setting 값 내부 데이터로 저장해놓는다.
                                    if ("Y".equalsIgnoreCase(rejectedCust) && !settings.isCheckedRejectMember) {
                                        // 거래거절 고객 확인 하였으니 이 후로는 N 이어도 무조건 체크하지 않는다.
                                        settings.isCheckedRejectMember = true;
                                        // 설정 값 변경 후 저장.
                                        settings.approve = false;
                                        settings.save();
                                    }

                                    AbstractTMSService.pushSettings(context, settings);
                                }, 500);
                            }
                        });
                    } catch (Exception e) {
                        // 10/19 품질팀 요청
                        // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                        // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                        // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                        Ln.e(e);
                    }

                    //GTM Datahub 이벤트 전달 (수동 로그인 완료시)
                    DatahubAction.sendDataToDatahub(context, DatahubUrls.D_1032, "");

                } else {
                    Ln.d("OnMessageReceived, isAutoLogin is not N");
                    user.isAutoLogin = "N";
                }

                /**
                 * Amplitude 고객정보 처리 부분 실제 로그인 이벤트는 아니다
                 *
                 */
                if (!TextUtils.isEmpty(user.customerNumber)) {
                    String gender = "";
                    NameValuePair gdPair = CookieUtils.getWebviewCookie(context, "gd");   //성별
                    if (gdPair != null) {
                        gender = gdPair.getValue();
                    }
                    String age = "";
                    NameValuePair ydPair = CookieUtils.getWebviewCookie(context, "yd");   //연령대
                    if (ydPair != null) {
                        //나이가 정상적일때만 입력 받음
                        try {
                            Integer ageInt = Integer.valueOf(ydPair.getValue());
                            if (10 <= ageInt && ageInt <= 80) {
                                age = ageInt.toString();
                            }
                        } catch (Exception e) {
                            //죽지만 말아라
                            age = "";
                        }
                    }
                    //Amp 고객번호 세팅 (AMP상의  UserID)
                    Amplitude.getInstance().setUserId(user.customerNumber);
                    //폰트 크기
                    String scaleString;
                    //성별/나이/폰트크기 세팅
                    try {
                        float scale = context.getResources().getConfiguration().fontScale;
                        if (scale < 0.5 || scale > 1.6) {
                            scaleString = "알수없음";
                        } else {
                            scaleString = Float.toString(scale);
                        }
                    } catch (Exception e) {
                        //죽지만 말아라
                        scaleString = "알수없음";
                    }

                    //앰플리튜드
                    Identify identify = new Identify().set("gd", gender)
                            .set("yd", age)
                            .set("fs", scaleString)
                            .set("custType", result.getCustType())
                            .set("grade", result.getGrade());
                    Amplitude.getInstance().identify(identify);
                }

                setSsoToken(result);

            }

            // 로그인 성공시에도 네비게이션 리프레시 해주어야 한다.
            NavigationManager.isNewHomeGroup = true;
            EventBus.getDefault().post(new Events.NavigationReflashEvent());
            // 로그인 성공시에 프레시 배송 정보 refresh 를 위해 갱신 이벤트 수행
            EventBus.getDefault().post(new Events.FlexibleEvent.UpdateGSSuperEvent());

            //로그인 API 속도 측정 측정
            //MainApplication mainApp = (MainApplication) context.getApplicationContext();
            //long valueTime = mainApp.getTiming(MainApplication.LOGIN_USERACTION);
            //AbstractBaseActivity.setNativePageTiming(valueTime, this.getClass().getSimpleName(), MainApplication.LOGIN_USERACTION);
            Ln.i("로그인 완료.");

            //로그인 성공시 캐쉬 처리
            //(개인화 데이터가 갱신되도록 5분 캐쉬는 유지
            // 상하단 리플래쉬는 과거 개인화탭(디폴트/홈) 네비게이션이였다.
            MainApplication.clearCache();
        }

        protected void error(Result result) {
        }

        public LoginResult login(Data data) {
            //로그인 API 속도 측정 측정
            //MainApplication mainApp = (MainApplication) context.getApplicationContext();
            //mainApp.setStartTiming(MainApplication.LOGIN_USERACTION);

            init();

            // init 할때 Cookie 날리기 때문에 ecid 추가. (mediaType 이 유지되어야 하는 이유로)
            CookieUtils.copyWebViewCookieToRestClientCookieDomain(context, restClient, Keys.COOKIE.LOGIN_SESSION);

            Param param = prepare(data);
            Result result = connect(param);

            if (result == null) {
                throw new AuthenticationException();
            }

            // 로그인 후 ui 관련된 내용은 로그인을 호출한 곳에서 처리하도록 함
            // LoginActivity, Logincommand, BaseTabMenuActivity
            if (result.isSuccs()) {
                User user = handle(data, param, result);
                complete(user, result);
            } else {
                invalidateUserSession();
                error(result);
            }

            return result;

        }

        private void setSsoToken(Result result) {
            String ssoRetCd = result.getSsoRetCd();
            if (isNotEmpty(ssoRetCd)) {
                switch (ssoRetCd) {
                    case "00":
                        SSOControl.getInstance().saveToken(SSOUtils.getSSOParamType2(result.getSsoToken()));
                        break;
                    case "10":
                    case "30":
                    case "40":
                        SSOControl.getInstance().saveToken(SSOUtils.getSSOParamType2(""));
                        break;
                    default:
                        break;
                }
            }
            //통합회원 안내 URL 값이 존재하면 웹부로 노출
            String url = result.getIntgMemGuideUrl();
            if (isNotEmpty(url)) {
                ThreadUtils.INSTANCE.runInUiThread(() -> {
                    //배경이 투명한 웹뷰
                    Intent intent = new Intent(Keys.ACTION.TRANSPARENT_MODAL_WEB);
                    intent.putExtra(Keys.INTENT.WEB_URL, url);
                    context.startActivity(intent);
                }, 4000);
            }
        }
    }

    /**
     * SSO 사용여부 조회
     *
     * @return SSOQueryResult
     */
    public SSOQueryResult querySSOUse() {
        User user = User.getCachedUser();
        SSOQueryParam param = new SSOQueryParam();
        param.setDeviceId(DeviceUtils.getUuid(context));
        if (isNotEmpty(user)) {
            param.setCustNo(user.customerNumber);
        }
        return RestClientUtils.INSTANCE.post(restClient, param,
                ServerUrls.getHttpsRoot() + ServerUrls.REST.SSO_USE_QUERY, SSOQueryResult.class);
    }

    /**
     * SSO 사용여부 설정
     *
     * @return SSOUseResult
     */
    public SSOUseResult setSSOUse(String useYn) {
        User user = User.getCachedUser();
        SSOUseParam param = new SSOUseParam();
        param.setDeviceId(DeviceUtils.getUuid(context));
        if (isNotEmpty(user)) {
            param.setCustNo(user.customerNumber);
        }
        param.setSsoUseYn(useYn);
        return RestClientUtils.INSTANCE.post(restClient, param,
                ServerUrls.getHttpsRoot() + ServerUrls.REST.SSO_USE_SET, SSOUseResult.class);
    }

    // 수동 로그인 (로그인화면에서 사용자가 직접 로그인)
    private class LoginForTokenNew extends
            LoginTemplate<SimpleCredentials, LoginTokenParamNew, LoginTokenResult> {
        LoginOption option;

        public LoginForTokenNew(LoginOption option) {
            this.option = option;
        }

        @Override
        protected LoginTokenParamNew prepare(SimpleCredentials data) {
            LoginTokenParamNew param = new LoginTokenParamNew();
            param.setLoginId(data.loginId != null ? data.loginId.trim() : data.loginId);
            param.setPasswd(data.password);
            //로그인 시점에 deviceId 필드에 gsuuid 세팅
            param.setDeviceId(DeviceUtils.getUuid(context));
            param.setSnsAccess(data.snsAccess);
            param.setSnsRefresh(data.snsRefresh);
            param.setSnsTyp(data.snsTyp);
            param.setLoginTyp(data.loginTyp);
            param.setSsoToken(SSOControl.getInstance().getLocalToken());
            return param;
        }

        @Override
        protected LoginTokenResult connect(LoginTokenParamNew param) {
            if (option.easyLogin) {
                param.setEasiLogin("Y");
            } else {
                param.setEasiLogin("N");
            }

            String url = null;
            if (!BuildConfig.FLAVOR_stage.equalsIgnoreCase("m")) {
                switch (PrefRepositoryNamed.getString(context, Keys.PREF.PREF_NAME_TEST_SERVER, Keys.PREF.PREF_TEST_SERVER)) {
                    case "m":
                        url = ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_M;
                        break;
                    case "asm":
                        url = ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_SM21;
                        break;
                    case "tm14":
                        url = ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_TM14;
                        break;
                    case "atm":
                        url = ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_ATM;
                        break;
                    case "am":
                        url = ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_AM;
                        break;
                    default:
                        url = ServerUrls.getHttpsRoot() + ServerUrls.REST.LOGIN_FOR_TOKEN;
                        break;
                }
            } else {
                url = ServerUrls.getHttpsRoot() + ServerUrls.REST.LOGIN_FOR_TOKEN;
            }

            //WebView 쿠키를 RestClient에 복사
            CookieUtils.syncWebViewCookiesToRestClient(context, restClient);
            return RestClientUtils.INSTANCE.post(restClient, param, url, LoginTokenResult.class);
        }

        @Override
        protected User handle(SimpleCredentials data, LoginTokenParamNew param,
                              LoginTokenResult result) {
            //새로 받은 인증토큰 저장
            TokenCredentialsNew2 token = new TokenCredentialsNew2();
            token.deviceId = param.getDeviceId();
            token.loginId = result.getLoginId();
            token.authToken = result.getCertToken();
            token.seriesKey = result.getSerisKey();
            token.snsTyp = result.getSnsTyp();
            token.save();

            //로그인 옵션 저장
            if (option != null) {
                option.save();
            }

            User user = new User();
            user.loginId = result.getLoginId();
            user.setUserName(result.getCustNm());
            user.setUserGrade(result.getGrade());
            user.customerNumber = result.getCustNo();
            user.setLoginResult(result);

            return user;
        }
    }

    // 웹에서 호출시 로그인 수행
    private class LoginForTokenNewFromWeb extends
            LoginTemplate<SimpleCredentials, LoginTokenParamNew, LoginTokenResult> {
        LoginOption option;

        public LoginForTokenNewFromWeb(LoginOption option) {
            this.option = option;
        }

        @Override
        protected LoginTokenParamNew prepare(SimpleCredentials data) {
            LoginTokenParamNew param = new LoginTokenParamNew();
            param.setDeviceId(DeviceUtils.getUuid(context));
            param.setEasiLogin("N");
            return param;
        }

        @Override
        protected LoginTokenResult connect(LoginTokenParamNew param) {
            //WebView 쿠키를 RestClient에 복사
            CookieUtils.syncWebViewCookiesToRestClient(context, restClient);
            return RestClientUtils.INSTANCE.post(restClient, param,
                    ServerUrls.getHttpsRoot() + ServerUrls.REST.LOGIN_FOR_TOKEN_WITH_DEVICE, LoginTokenResult.class);
        }

        @Override
        protected User handle(SimpleCredentials data, LoginTokenParamNew param,
                              LoginTokenResult result) {
            //새로 받은 인증토큰 저장
            TokenCredentialsNew2 token = new TokenCredentialsNew2();
            token.deviceId = param.getDeviceId();
            token.loginId = result.getLoginId();
            token.authToken = result.getCertToken();
            token.seriesKey = result.getSerisKey();
            token.snsTyp = result.getSnsTyp();
            token.save();

            //로그인 옵션 저장
            if (option != null) {
                option.save();
            }

            User user = new User();
            user.loginId = result.getLoginId();
            user.setUserName(result.getCustNm());
            user.setUserGrade(result.getGrade());
            user.customerNumber = result.getCustNo();
            user.setLoginResult(result);

            return user;
        }
    }
}
