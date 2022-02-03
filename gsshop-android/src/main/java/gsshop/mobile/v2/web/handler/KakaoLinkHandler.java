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

/**
 * 카카오톡으로 URL 링크 보내기.
 *
 * - 형식 : kakaolink://sendurl?msg=[message]{@literal &}url=[url]&appid=[appid]&appver=[appver]
 * - 참고 : https://www.kakao.com/link/ko/api?tab=android
 */
public class KakaoLinkHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse(url));

        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // TODO alert 카톡이 설치돼 있지 않습니다.
            return false;
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith("kakaolink://");
    }

}
