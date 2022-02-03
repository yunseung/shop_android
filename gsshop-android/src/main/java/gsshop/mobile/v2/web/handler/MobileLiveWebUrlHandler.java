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

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * 네이티브 모바일 라이브 요청.
 *
 * - 링크 형식 : toapp://movetomovilelive?topapi=[날방 동영상 주소]&closeYn=[gateway웹뷰 종료여부]
 *                  closeYn은 푸시나 SNS공유 등 외부에서 앱을 호출할 경우 gateway웹뷰를 종료하기 위해 필요함
 * - 예제 : toapp://movetomovilelive?topapi=  ?onAirYn=N & ?onAirYn=N&closeYn=Y
 *
 */
public class MobileLiveWebUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);

        //푸시나 SNS공유 등 외부에서 앱을 호출할 경우만 closeYn=Y 값을 가진다.
        boolean isExternalCall = "Y".equalsIgnoreCase(uri.getQueryParameter("closeYn"));

        Intent intent = new Intent(Keys.ACTION.MOBILELIVE_FULL_VIDEO_PLAYER);
        intent.putExtra(Keys.INTENT.MOBILELIVE_LINK, url);
        //모라가 홈화면 배너로 추가되면서 closeYn 파라미터를 사용하여 BACK_TO_MAIN을 세팅할 경우 이슈발생.
        //웹에서 별도 파라미터를 전달받아도 가능하지만, 일단은 isHomeCommandExecuted 앱내부 파라미터를 사용하여 세팅.
        if (!MainApplication.isHomeCommandExecuted) {
            intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
        }
        activity.startActivity(intent);


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
        return url.startsWith(ServerUrls.APP.MOBILE_LIVE_APP);
    }



}
