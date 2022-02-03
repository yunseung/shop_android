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
import android.webkit.WebView;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.web.ModalWebActivity;

/**
 * 모달 웹 화면 요청.
 *
 */
public class ModalWebUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://modal?http://m.gsshop.com/xxx

        // 모달웹에서 띄워줄 페이지
        //10/19 품질팀 요청
        if(url != null) {
            String targetUrl = url.substring((ServerUrls.APP.MODAL_WEB + "?").length());

            Intent intent = new Intent(activity, ModalWebActivity.class);
            intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);

            // activity 중복 실행방지
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivityForResult(intent, Keys.REQCODE.MODAL);
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.MODAL_WEB);
    }
}
