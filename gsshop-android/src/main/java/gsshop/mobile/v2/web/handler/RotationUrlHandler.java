/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.webkit.WebView;

import gsshop.mobile.v2.ServerUrls;

/**
 * 현재 액티비티 종료.
 * 모달 창에서 사용됨.
 */
public class RotationUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        doRotation(activity, webview, url);

        return true;
    }

    @Override
    public boolean match(String url) {
        return (url.startsWith(ServerUrls.APP.ROTATION_PORTRAIT) || url.startsWith(ServerUrls.APP.ROTATION_LANDSCAPE));
    }

    private void doRotation(Activity activity, WebView webview, String url) {

        if (url != null && url.startsWith(ServerUrls.APP.ROTATION_PORTRAIT)) {
            // 세로 회전
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            webview.setRotation(0);
        }
        else if (url != null && url.startsWith(ServerUrls.APP.ROTATION_LANDSCAPE)) {
            // 가로 회전
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            webview.setRotation(90);
        }
    }
}
