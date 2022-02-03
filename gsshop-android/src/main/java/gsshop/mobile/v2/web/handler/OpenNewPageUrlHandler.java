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
import gsshop.mobile.v2.web.WebUtils;

/**
 * 새로운 페이지 열기 Handler
 *
 * - 링크 형식 : toapp://openNewPage?url=[웹뷰주소]&closeYn=[웹뷰 종료여부]&method=[post 여부]
 */
public class OpenNewPageUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);

        // no tab webview
        String goUrl = uri.getQueryParameter("url");

        Intent data = null;

        if ("post".equalsIgnoreCase(uri.getQueryParameter("method"))) {
            data = new Intent();
            data.putExtra(Keys.INTENT.POST_DATA, goUrl);
        }

        WebUtils.goWeb(activity, goUrl, data);

        //웹뷰를 종료함
        if ("Y".equalsIgnoreCase(uri.getQueryParameter("closeYn"))) {
            if (!(activity instanceof HomeActivity)) {
                activity.finish();
            }
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.OPEN_NEW_PAGE);
    }

}
