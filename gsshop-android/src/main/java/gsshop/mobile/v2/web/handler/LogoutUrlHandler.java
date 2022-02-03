/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.app.Dialog;
import android.webkit.WebView;

import com.google.inject.Inject;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.user.LoginOption;
import gsshop.mobile.v2.user.UserAction;
import gsshop.mobile.v2.user.UserConnector.LogoutTokenResult;

/**
 * 로그아웃 처리.
 */
public class LogoutUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(final Activity activity, final WebView webview, String url) {
        String alert;
        LoginOption option = LoginOption.get();
        if (option != null) {
            if(option.easyLogin){
                alert = activity.getString(R.string.login_confirm_logout_simple);
            } else {
                alert = activity.getString(R.string.login_confirm_logout_simple_cancel);
            }
        } else {
            alert = activity.getString(R.string.login_confirm_logout);
        }
        new CustomTwoButtonDialog(activity).message(alert)
                .positiveButtonClick(new ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        new LogoutController(activity).execute();
                    }
                }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.LOGOUT);
    }

    private class LogoutController extends BaseAsyncController<LogoutTokenResult> {

        @Inject
        private UserAction userAction;

        protected LogoutController(Activity activity) {
            super(activity);
        }

        @Override
        protected LogoutTokenResult process() {
            return userAction.logout(true);
        }
        
        @Override
        protected void onSuccess(LogoutTokenResult t) {
            if(t != null){
                if(!t.isSuccs()){
                    // 로그아웃 에러이지만 처리 하지 않음.
                    
                }
                }
            } 
        }
        
    

}
