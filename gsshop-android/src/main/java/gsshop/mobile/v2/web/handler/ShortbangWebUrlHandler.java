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

/**
 * 숏방 요청.
 *
 * - 링크 형식 : toapp://movetoshortbang?targetApi=[숏방 API 주소]{@literal &}selectedcate=[카테고리번호]{@literal &}selectedvideo=[비디오번호]
 * - 예제 : toapp://movetoshortbang?targetApi=http://m.gsshop.com/test/app/shortbangdetail.jsp{@literal &}selectedcate=CATE00{@literal &}selectedvideo=CATE00AV0
 *
 */
public class ShortbangWebUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
//        Uri uri = Uri.parse(url);

        //푸시나 SNS공유 등 외부에서 앱을 호출할 경우만 closeYn=Y 값을 가진다.
        //boolean isExternalCall = "Y".equalsIgnoreCase(uri.getQueryParameter("backtomain"));
//        boolean isExternalCall = true;

        // 숏방상세로 이동
        Intent intent = new Intent(Keys.ACTION.SHORT_BANG);
        intent.putExtra(Keys.INTENT.SHORTBANG_LINK, url);

        //외부에서 접속 후 백키 클릭시 메인을 띄움
//        if (isExternalCall) {
//            //intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
//        }
        activity.startActivity(intent);

        activity.finish();

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.SHORTBANG_WEB + "?");
    }



}
