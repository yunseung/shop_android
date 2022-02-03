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
 * 날방 요청.
 *
 * - 링크 형식 : toapp://movetonalbang?topapi=[날방 동영상 주소]&bottomurl=[날톡 주소]&closeYn=[gateway웹뷰 종료여부]
 *                  closeYn은 푸시나 SNS공유 등 외부에서 앱을 호출할 경우 gateway웹뷰를 종료하기 위해 필요함
 * - 예제 : toapp://movetonalbang?topapi=http://sm21.gsshop.com/app/section/nalbang/484/17529517?onAirYn=N&bottomurl=http://sm21.gsshop.com/section/nalbang/484/17529517?onAirYn=N&closeYn=Y
 *
 */
public class NalbangWebUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);

        //푸시나 SNS공유 등 외부에서 앱을 호출할 경우만 closeYn=Y 값을 가진다.
        boolean isExternalCall = "Y".equalsIgnoreCase(uri.getQueryParameter("closeYn"));

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            // no tab webview
            String bottomUrl = uri.getQueryParameter("bottomurl");
            Intent intent = new Intent(Keys.ACTION.NO_TAB_WEB);
            intent.putExtra(Keys.INTENT.WEB_URL, bottomUrl);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
            //외부에서 접속 후 백키 클릭시 메인을 띄움
            if (isExternalCall) {
                intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
            }
            activity.startActivity(intent);
        } else {
            // 날방 webview
            Intent intent = new Intent(Keys.ACTION.NALBANG_WEB);
            intent.putExtra(Keys.INTENT.NALBANG_LINK, url);
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
        return url.startsWith(ServerUrls.APP.NALBANG_WEB);
    }



}
