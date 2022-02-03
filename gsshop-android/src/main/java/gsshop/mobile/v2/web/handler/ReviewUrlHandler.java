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
import gsshop.mobile.v2.Keys.INTENT;
import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.review.ReviewActivity;

/**
 * 상품평 작성 앱화면 요청 처리.
 *
 */
public class ReviewUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {

        // ex) "toapp://estimate?prdid=7166410&order_num=553295879&lineNum=1&save_root=androOrd&messageId=84581341&modify=N"
        //10/19 품질팀 요청
        if(url != null) {
            String queryString = url.substring((ServerUrls.APP.REVIEW + "?").length());// 상품평 조회 파라미터

            Intent intent = new Intent(activity, ReviewActivity.class);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
            intent.putExtra(INTENT.REVIEW_QUERY_STRING, queryString);
            activity.startActivityForResult(intent, REQCODE.REVIEW);
        }
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.REVIEW);
    }

}
