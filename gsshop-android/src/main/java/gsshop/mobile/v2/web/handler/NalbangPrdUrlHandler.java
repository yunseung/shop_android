/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.webkit.WebView;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 웹뷰내에서 클릭이벤트 처리
 *
 * - 링크 형식 : toapp://movetoinpage?url=[웹뷰주소]&closeYn=[웹뷰 종료여부]
 * - 예제 : toapp://movetoinpage?url=http://sm21.gsshop.com/deal/deal.gs?dealNo=18757271&closeYn=N
 *
 * comment
 * - 2019.08.14 iOS 이슈로 기능은 동일한 신규규격("newpage") 추가
 */
public class NalbangPrdUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);

        // no tab webview 인코딩 필요
        String prdUrl = uri.getQueryParameter("url");
        WebUtils.goWeb(activity, prdUrl);

        try{
            //웹뷰를 종료함
            if ("Y".equalsIgnoreCase(uri.getQueryParameter("closeYn"))) {
                //activity.finish();
                //라이브톡 텍스트롤링 타이머 구동을 위한 이벤트 전달
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!(activity instanceof HomeActivity)) {
                            activity.finish();
                        }
                    }
                }, 1000);
            }
        }catch (Exception e)
        {

        }


        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.NALBANG_PRD) || url.startsWith(ServerUrls.APP.LIVETALK_LINK);
    }



}
