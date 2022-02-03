/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;

import com.appboy.Appboy;
import com.google.inject.Inject;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import org.apache.http.NameValuePair;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.airbridge.ABAction;
import gsshop.mobile.v2.user.LoginOption;
import gsshop.mobile.v2.user.TokenCredentialsNew2;
import gsshop.mobile.v2.user.UserAction;
import gsshop.mobile.v2.user.UserConnector;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.MainWebViewClient;
import gsshop.mobile.v2.web.WebViewControlInherited;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static gsshop.mobile.v2.util.LunaUtils.autoLoginState;
import static gsshop.mobile.v2.util.NetworkUtils.getNetworkType;
import static gsshop.mobile.v2.util.SSOUtils.hasSSOToken;

/**
 * 로그인 유지 설정된 경우 자동로그인 실행.
 */
public class LoginCommand extends BaseCommand {
    /**
     * LoginCommand userAction
     */
    @Inject
    private UserAction userAction;

    /**
     * LoginCommand mWebControl
     */
    private WebViewControlInherited webControl;

    /**
     * mActivity
     */
    private Activity mActivity;

    /**
     * 자동 로그인 재시도 플레그
     */
    private boolean isRetry = false;

    /**
     * LoginCommand execute
     *
     * @param activity activity
     * @param chain    chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        long startTime = System.currentTimeMillis();

        injectMembers(activity);

        this.mActivity = activity;

        ThreadUtils.INSTANCE.runInBackground(() -> {
            //자동로그인 여부와 관계없이 남아있는 로그인세션 정보 모두 제거
            userAction.invalidateUserSession();

            LoginOption option = LoginOption.get();
            //기존 자동로그인 수행조건에 추가하여 ssoToken 값이 존해하는 경우도 자동로그인 수행
            if ((option != null && option.keepLogin)
                    || hasSSOToken()) {
                try {
                    autoLoginState = LunaUtils.AutoLoginState.FIRST_TRYING;
                    doAutoLogin(option);
                } catch (Exception e) {
                    //사용자 네트워크 타입 확인
                    String networkType = getNetworkType(activity);
                    //자동로그인 실패 로그전송
                    LunaUtils.sendToLuna(activity, e, LunaUtils.PREFIX_AUTO_LOGIN_ERROR + " [" + networkType + "]CMD_" + DeviceUtils.getGsuuid(activity));

                    Ln.i(e);
                    isRetry = true;
                    chain.next(activity);

                    try {
                        autoLoginState = LunaUtils.AutoLoginState.RE_TRYING;
                        doAutoLogin(option);
                    } catch (Exception er) {
                        //doAutoLogin 안에서 잘 처리 했기 대문에 여기를 탈 개연성은 없는가?
                        //도대체 여기는 무슨 에러가 날까?? by leems
                        Ln.i(er);
                        // 20181121 fix by hklim 재시도시에 oops로 autologin_retry Exception 전송하지 않도록 수정
                        // 20190816 by yh.park 로그인실패 원인분석 위해
                        LunaUtils.sendToLuna(activity, er, LunaUtils.PREFIX_AUTO_LOGIN_RETRY_ERROR + " [" + networkType + "]CMD_" + DeviceUtils.getGsuuid(activity));

                        // 로그인 실패시 메시지 보여주고 다음 커맨드 계속 진행
                        alertLoginFailed();
                        userAction.invalidateUserSession();
                    }
                }
            } else {
                //자동로그인 체크 해제하고 로그인 -> 앱종료 -> 앱실행시 로그인 상태가 유지되지 않도록 한다.
                //userAction.invalidateUserSession();
                ((IntroActivity) mActivity).displayIntroGradeMessage(null, null);
            }

            //앱 실행시 pcid 쿠키를 생성하기 위해 호출 (ecid 쿠키가 생성된 이후 시점에 호출)
            try {
                final String url = ServerUrls.WEB.APP_START;
                String param = "";
                if (!TextUtils.isEmpty(DeviceUtils.getAdvertisingId())) {
                    param = "?aid=" + DeviceUtils.getAdvertisingId();
                }
                callAppStartUrl(url + param);
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }

            if (!isRetry) {
                chain.next(activity);
            }

            LogUtils.printExeTime("LoginCommand", startTime);
        });
    }

    /**
     * doAutoLogin
     * <p>
     * TODO: 2016. 10. 21. MSLEE
     * 다른곳에서 복사하면서 option 유지된듯, 추후 수정
     *
     * @param option 미사용하네
     */
    private void doAutoLogin(LoginOption option) throws InterruptedException {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TokenCredentialsNew2 tokenCredentials = TokenCredentialsNew2.get();
                if ((tokenCredentials != null && tokenCredentials.authToken != null)
                        || hasSSOToken()) {
                    UserConnector.LoginResult result = null;
                    try {
                        result = userAction.auth(tokenCredentials);
                    } catch (Exception e) {
                        Ln.e(e);
                        alertLoginFailed();
                        return;
                    }

                    if (isEmpty(result) || !result.isSuccs()) {
                        //토스트로 실패 노티
                        alertLoginFailed();
                    } else {
                        ((IntroActivity) mActivity).displayIntroGradeMessage(result.getGrade(), result.getCustNm());

                        // ARS 가입 비밀번호 변경이 필요한 경우
                        if ("Y".equalsIgnoreCase(result.getPasswdTvNeedChgYn())) {
                            EventBus.getDefault().post(new Events.LoggedInUpdatePassEvent(result.getpasswdTvChgUrl(), true));
                        }
                        //비밀번호 변경이 필요한 경우
                        // 20181017 ARS 가입 비밀번호 변경 확인하면 기존 비밀번호 변경 확인 로직은 확인하지 않음 - hklm -
                        else if ("Y".equalsIgnoreCase(result.getPasswdNeedChgYn())) {
                            EventBus.getDefault().post(new Events.LoggedInUpdatePassEvent(result.getPasswdChgUrl(), false));
                        }

                        EventBus.getDefault().postSticky(new Events.AutoLogInSuccessEvent());

                        try {
                            //appboy
                            //Custom Attributes 세팅
                            if (!TextUtils.isEmpty(result.getCustNo())) {
                                if (!"true".equals(mActivity.getString(R.string.skip_appboy))) {
                                    Appboy.getInstance(mActivity).getCurrentUser().setCustomUserAttribute("catvid", result.getCustNo());
                                }
                            }
                            String gsuuid = DeviceUtils.getGsuuid(mActivity);
                            if (!TextUtils.isEmpty(gsuuid)) {
                                if (!"true".equals(mActivity.getString(R.string.skip_appboy))) {
                                    Appboy.getInstance(mActivity).getCurrentUser().setCustomUserAttribute("token", gsuuid);
                                }
                            }
                            String gender = "";
                            NameValuePair gdPair = CookieUtils.getWebviewCookie(mActivity, "gd");   //성별
                            if (gdPair != null) {
                                gender = gdPair.getValue();
                                if (!TextUtils.isEmpty(gender)) {
                                    if (!"true".equals(mActivity.getString(R.string.skip_appboy))) {
                                        Appboy.getInstance(mActivity).getCurrentUser().setCustomUserAttribute("gd", gender);
                                    }
                                }
                            }
                            String age = "";
                            NameValuePair ydPair = CookieUtils.getWebviewCookie(mActivity, "yd");   //연령대
                            if (ydPair != null) {
                                age = ydPair.getValue();
                                if (!TextUtils.isEmpty(age)) {
                                    if (!"true".equals(mActivity.getString(R.string.skip_appboy))) {
                                        Appboy.getInstance(mActivity).getCurrentUser().setCustomUserAttribute("yd", age);
                                    }
                                }
                            }

                            //airbridge
                            ABAction.measureABSignIn(result.getCustNo());

                        } catch (Exception e) {
                            Ln.e(e);
                        }
                    }
                } else {
                    ((IntroActivity) mActivity).displayIntroGradeMessage(null, null);
                }
            }
        });
    }

    /**
     * alertLoginFailed
     */
    private void alertLoginFailed() {
        ThreadUtils.INSTANCE.runInUiThread(() -> Toast.makeText(mActivity, R.string.login_intro_fail, Toast.LENGTH_LONG).show());
    }

    /**
     * callAppStartUrl
     * 이주영 대리(퇴사) 최초 구동에 대한 쿠키 세팅을 위해 호출
     *
     * @param url 호출주소
     */
    private void callAppStartUrl(final String url) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webControl = new WebViewControlInherited.Builder(mActivity).target((WebView) mActivity.findViewById(R.id.webView))
                        .with(new MainWebViewClient(mActivity) {

                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                Ln.i("[LoginCommand onPageStarted]" + url);
                                super.onPageStarted(view, url, favicon);
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                //쿠기 동기화
                                CookieUtils.syncWebViewCookies(mActivity, null);
                                super.onPageFinished(view, url);
                            }

                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                Ln.i("[LoginCommand shouldOverrideUrlLoading]" + url);
                                return super.shouldOverrideUrlLoading(view, url);
                            }
                        }).build();

                try {
                    webControl.clearCache();
                    webControl.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            webControl.loadUrl(url, MainApplication.customHeaders);
                        }
                    });
                } catch (Exception e) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e);
                }
            }
        });
    }
}
