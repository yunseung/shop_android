/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import gsshop.mobile.v2.ServerUrls;

/**
 * toapp://browser?url 프로토콜 처리.
 *
 * toapp://browser? 이후의 주소를 외부 앱을 통해 처리한다.
 * (Intent.ACTION_VIEW를 통해 외부앱 호출)
 */
public class ExternalWebHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://browser?http://xxx.com/xxx.html

        //url에서 toapp://browser? 제거
        //10/19 품질팀 요청
        if(url != null) {
            String externalUrl = url.substring((ServerUrls.APP.EXTERNAL_WEB + "?").length());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl));

            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // TODO alert 처리? (이 주소를 처리할 앱을 찾을 수 없습니다...)
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.EXTERNAL_WEB);
    }

}
