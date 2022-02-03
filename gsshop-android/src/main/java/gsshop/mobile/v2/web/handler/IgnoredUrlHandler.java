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

/**
 * 아무처리도 하지 않고 무시할 주소.
 *
 */
public class IgnoredUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // 아무 작업도 하지 않는다.
        return true;
    }

    @Override
    public boolean match(String url) {
        // 삼성카드 결제시 백신실행후 아래 URL 실행됨. 이로 인해 정상 결재 불가능.
        if (url.startsWith("mpocketansimclick://install")) {
            return true;
        }

        // NOTE : 필요시 여기에 계속 추가한다.

        return false;
    }

}
