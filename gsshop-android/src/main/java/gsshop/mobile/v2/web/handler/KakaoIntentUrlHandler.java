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

import java.net.URISyntaxException;

/**
 * 카카오 이쫘식들이 지들 한테 맞추라는 인텐트, 확인용
 */
public class KakaoIntentUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        try {
            Uri intentUri = Uri.parse(url);
            if (intentUri == null) {
                return false;
            }

            if ("intent".equals(intentUri.getScheme())) {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                if (intent == null) {
                    return false;
                }

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intent);
                    return true;
                }

                String extraData = intent.getStringExtra("browser_fallback_url");
                if (extraData != null) {
                    webview.loadUrl(extraData);
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean match(String url) {
        return false;
    }
}
