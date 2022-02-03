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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

/**
 * ISP 결제 요청 처리.
 *
 */
public class IspUrlHandler implements WebUrlHandler {

    /**
     *
     */
    private static final String ISP_DOWNLOAD_URL = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        try {
            activity.startActivityIfNeeded(intent, -1);
            //SharedPreferences.Editor ed= prefs.edit();
            //ed.putString("isp_befor_caller", caller);
        } catch (ActivityNotFoundException ex) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ISP_DOWNLOAD_URL));
            activity.startActivity(intent);
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.ISP_MOBILE);
    }

}
