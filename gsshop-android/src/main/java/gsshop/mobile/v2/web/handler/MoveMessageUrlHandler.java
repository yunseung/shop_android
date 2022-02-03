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

import gsshop.mobile.v2.AbstractTMSService;
import gsshop.mobile.v2.ServerUrls;
import roboguice.util.Ln;

/**
 * MessageBOX 이동.
 * GS SHOP 홈에서 사용.
 */
public class MoveMessageUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://close
        // ex) toapp://close?http://m.gsshop.com/xxx

        try {
            AbstractTMSService.showMessageBox(activity);
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }        
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.MOVEMESSAGEBOX);
    }

}
