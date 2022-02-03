/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import gsshop.mobile.v2.ServerUrls;
import android.app.Activity;
import android.webkit.WebView;

/**
 * 액티비티 뒤로가기 요청 처리.
 *
 */
public class BackUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        activity.onBackPressed();
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.BACK);
    }

}
