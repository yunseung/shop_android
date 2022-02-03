/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.webkit.WebView;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.ServerUrls;

/**
 * left Navi Url Handler
 * Web -> App Navi 호출 규격
 *
 * - 링크 형식 : toapp://leftnavi?caller=호출 페이지?
 * - 예제 : toapp://leftnavi?caller=
 *
 */
public class NaviUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {

        if(activity instanceof AbstractBaseActivity){
            //((AbstractBaseActivity)activity).callLeftNavigation();
        }
        return true;

    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.NAVI_SHOW);
    }

}
