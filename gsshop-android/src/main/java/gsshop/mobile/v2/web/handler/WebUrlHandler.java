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
 * {@link WebView}에서 정해진 규칙에 따라 url을 다른 방식으로
 * 처리하기 위한 핸들러.
 *
 * NOTE : 핸들러 인스턴스는 여러 WebActivity 및 url에 대해서 계속
 * 재사용되므로 상태를 가지면 안된다.
 */
public interface WebUrlHandler {

    /**
     * 해당 url을 처리한다.
     *
     * @param activity
     * @param webview
     * @param url
     * @return 처리한 경우 true, 처리하지 않은 경우 false.
     */
    public boolean handle(Activity activity, WebView webview, String url);

    /**
     * 이 핸들러가 해당 url을 처리할 수 있는가.
     *
     * @param url
     * @return
     */
    public boolean match(String url);
}
