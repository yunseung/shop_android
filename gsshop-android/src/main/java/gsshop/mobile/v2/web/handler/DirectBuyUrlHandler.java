package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.webkit.WebView;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.web.BaseWebActivity;

/**
 * Created by jhkim on 16. 3. 23..
 */
public class DirectBuyUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://directOrd?http://xxx.com/xxx.html

        //url에서 toapp://directOrd? 제거
        //10/19 품질팀 요청
        if(url != null) {
            String externalUrl = url.substring((ServerUrls.APP.DIRECTBUY + "?").length());

            /**
             * 1026/11/24
             * crash 오류 발생
             * 오류: java.lang.ClassCastException: gsshop.mobile.v2.home.HomeActivity cannot be cast to gsshop.mobile.v2.web.BaseWebActivity
             */
            if(activity instanceof  BaseWebActivity) {
                ((BaseWebActivity) activity).visibleOrderDirectWebView(externalUrl);
            }
        }
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.DIRECTBUY);
    }

}
