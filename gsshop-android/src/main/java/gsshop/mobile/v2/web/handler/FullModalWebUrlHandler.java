/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.web.FullModalWebActivity;

/**
 * 상단 타이틀 영역 없는 모달 웹 화면 요청.
 *
 */
public class FullModalWebUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://fullwebview?targetUrl=http://m.gsshop.com/xxx

        // 모달웹에서 띄워줄 페이지 추출
        Uri uri = Uri.parse(url);
        String targetUrl = uri.getQueryParameter("targetUrl");

        //Uri uriTarget = Uri.parse(targetUrl);

        // 웹뷰를 종료함
        if ("Y".equalsIgnoreCase(uri.getQueryParameter("closeYn"))) {
            if (!(activity instanceof HomeActivity)) {
                activity.finish();
            }
        }

        Intent intent = new Intent(activity, FullModalWebActivity.class);
        intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);
        activity.startActivityForResult(intent, Keys.REQCODE.MODAL);

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.FULL_MODAL_WEB) || url.startsWith(ServerUrls.APP.NOTAB_FULL_MODAL_WEB);
    }
}
