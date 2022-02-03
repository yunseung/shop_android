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
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.menu.TabMenu;

/**
 * 라이브톡 요청.
 *
 * - 링크 형식 : toapp://movetolivetalk?topapi=[라이브톡 동영상 주소]{@literal &}bottomurl=[라이브톡 주소]{@literal &}closeYn=[gateway웹뷰 종료여부]
 *                  closeYn은 푸시나 SNS공유 등 외부에서 앱을 호출할 경우 gateway웹뷰를 종료하기 위해 필요함
 * - 예제 : toapp://movetolivetalk?topapi=http://sm21.gsshop.com/app/section/nalbang/484/17529517?onAirYn=N{@literal &}bottomurl=http://sm21.gsshop.com/section/nalbang/484/17529517?onAirYn=N{@literal &}closeYn=Y
 *
 */
public class LiveTalkWebUrlHandler implements WebUrlHandler {

    //NO_TAB_WEB에서 라이브톡으로 부터 호출되었는지 구분하기 위한 값
    private final static String LIVETALK_HOST = "livetalk";
    //라이브톡 api url 파라미터명
    private final static String LIVETALK_API_URL = "topapi";

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);

        //푸시나 SNS공유 등 외부에서 앱을 호출할 경우만 closeYn=Y 값을 가진다.
        boolean isExternalCall = "Y".equalsIgnoreCase(uri.getQueryParameter("closeYn"));

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            // no tab webview
            String apiUrl = uri.getQueryParameter(LIVETALK_API_URL);
            Intent intent = new Intent(Keys.ACTION.NO_TAB_WEB);
            intent.putExtra(Keys.INTENT.WEB_URL, apiUrl);
            intent.putExtra(Keys.INTENT.REFERRER, LIVETALK_HOST);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
            //외부에서 접속 후 백키 클릭시 메인을 띄움
            if (isExternalCall) {
                intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
            }
            activity.startActivity(intent);
        } else {
            // 날방 webview
            Intent intent = new Intent(Keys.ACTION.LIVETALK_WEB);
            intent.putExtra(Keys.INTENT.LIVETALK_LINK, url);
            //외부에서 접속 후 백키 클릭시 메인을 띄움
            if (isExternalCall) {
                intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
            }
            activity.startActivity(intent);
        }

        //푸시나 SNS공유 등 외부에서 앱을 호출할 경우 gateway웹뷰를 종료함
        if (isExternalCall) {
            if (!(activity instanceof HomeActivity)) {
                activity.finish();
            }
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.LIVETALK_WEB);
    }



}
