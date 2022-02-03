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
import gsshop.mobile.v2.web.OutsiteModalWebActivity;
import roboguice.util.Ln;

/**
 * 모달 웹 화면 요청.(상단 타이틀 변경 가능)
 *
 * ex) toapp://outsitemodal?title=xxx{@literal &}url=xxx
 */
public class OutSiteModalWebUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {

        String title = "";
        String targetUrl = "";

        try {
            Uri uri = Uri.parse(url);
            title = uri.getQueryParameter("title");
            targetUrl = uri.getQueryParameter("url");
        } catch (Exception e) {
            //title, targetUrl 값이 존재하지 않는 상태로 ModalWebActivity를 호출하면
            //title은 "상품설명", webview에는 ServerUrls.WEB.HOME url을 노출한다.
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }

        Intent intent = new Intent(activity, OutsiteModalWebActivity.class);
        intent.putExtra(Keys.INTENT.WEB_TITLE, title);
        intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);

        // activity 중복 실행방지
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        activity.startActivityForResult(intent, Keys.REQCODE.MODAL);

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.OUTSITE_MODAL_WEB);
    }
}
