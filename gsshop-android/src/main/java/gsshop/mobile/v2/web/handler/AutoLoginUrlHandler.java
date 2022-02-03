/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;

import com.google.inject.Inject;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.setting.FingerPrintSettings;
import gsshop.mobile.v2.user.LoginOption;
import gsshop.mobile.v2.user.SimpleCredentials;
import gsshop.mobile.v2.user.UserAction;
import gsshop.mobile.v2.user.UserConnector;

/**
 * 회원가입 완료시, 로그인 수행하고 자동로그인 체크상태로 설정
 *
 * ex) toapp://toautologin?auto=[Y|N]
 *
 * NOTE :
 * - 회원가입 페이지에 "자동로그인유지" 버튼 활성화된 상태인 경우만 수행됨
 * - 2018-07-20
 *   >설정화면에 자동로그인 ON/OFF 기능이 추가되면서 auto 파라미터 추가됨
 *   >서버에서 login_interface 호출 대신 본 핸들러 호출함
 *   >"자동로그인유지" 체크|해제 : auto=Y|N
 */
public class AutoLoginUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);
        String auto = uri.getQueryParameter("auto");
        //default "Y"
        if (TextUtils.isEmpty(auto)) {
            auto = "Y";
        }

        SimpleCredentials model = new SimpleCredentials();
        LoginOption option = new LoginOption();
        option.keepLogin = "Y".equalsIgnoreCase(auto);    //자동로그인
        option.easyLogin = false;   //간편로그인(현재 사용안함)

        new LoginController(activity).execute(model, option);

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.AUTO_LOGIN);
    }

    /**
     * 로그인 수행
     */
    private class LoginController extends BaseAsyncController<UserConnector.LoginResult> {
        @Inject
        private UserAction userAction;

        private SimpleCredentials model;
        private LoginOption option;
        private final Context context;

        public LoginController(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            model = (SimpleCredentials) params[0];
            option = (LoginOption) params[1];
        }

        @Override
        protected UserConnector.LoginResult process() throws Exception {
            return userAction.loginFromWeb(model, option);
        }

        @Override
        protected void onSuccess(UserConnector.LoginResult result) throws Exception {
            if (result.isSuccs()) {
                //지문인식 매핑 해제
                FingerPrintSettings fingerPrintSettings = FingerPrintSettings.get();
                fingerPrintSettings.isFingerprintMapping = false;
                fingerPrintSettings.loginId = "";
                fingerPrintSettings.save();
            }
        }

        @Override
        protected void onError(Throwable e) {
            //로그인 수행중 에러발생시 에러관련 팝업을 띄우지 않고 침묵한다.
        }
    }
}
