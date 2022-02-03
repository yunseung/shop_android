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
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.WebView;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 현재 액티비티 종료.
 * 모달 창에서 사용됨.
 */
public class CloseUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://close
        // ex) toapp://close?http://m.gsshop.com/xxx

        // 모달창 닫은 후 부모 창에서 띄워줄 타겟 웹페이지
        String targetUrl = null;
        //10/19 품질팀 요청
        if (url != null && url.startsWith("?", ServerUrls.APP.CLOSE.length())) {
            targetUrl = url.substring((ServerUrls.APP.CLOSE + "?").length());
        }

        if (!TextUtils.isEmpty(targetUrl)) {
            Intent data = new Intent();
            data.putExtra(Keys.INTENT.WEB_URL, targetUrl);
            activity.setResult(Activity.RESULT_OK, data);

            //새벽배송, GS프레시몰등 매장내 장바구니버튼 클릭 -> 팝업액티비티(PopupBasicActivity) -> 배송지선택의 경우
            //위 setResult로 HomeActivity의 onActivityResult 콜백 호출 안됨 (이유는 홈액티비티가 아닌 팝업액티비티가 웹뷰를 띄우기때문에)
            //따라서 이벤트를 사용하여 매장 갱신을 수행함
            if (!targetUrl.contains(ServerUrls.HTTP) && !targetUrl.contains(ServerUrls.HTTPS)) {
                if (targetUrl.contains(WebUtils.TABID_PARAM_KEY)) {
                    new Handler().postDelayed(() -> {
                        //L타입 매장갱신 이벤트
                        EventBus.getDefault().post(new Events.RefreshShopEvent());
                        //GS프레시몰 매장갱신 이벤트
                        EventBus.getDefault().post(new Events.FlexibleEvent.UpdateGSSuperEvent());
                    }, 500);
                }
            }
        }

        if (!(activity instanceof HomeActivity)) {
            activity.finish();
        }
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.CLOSE);
    }

}
