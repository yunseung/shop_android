/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.scheme;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.web.WebActivity;

/**
 * ISP결제 앱에서 아래와 같은 URI로 우리 앱을 호출함.
 *
 * 형식) gsshopmobile://TID=xxx
 *
 * 여기서 TID=xxx 부분이 host(쿼리스트링 아님).
 */
public class IspCallbackHostHandler implements UriHostHandler {

    private static final String ISP_HOST_PREFIX = "TID=";

    @Override
    public boolean match(Uri data, String host) {
        return host.startsWith(ISP_HOST_PREFIX);
    }

    @Override
    public boolean handle(Activity activity, Uri data) {
        String host = data.getHost();
        String tid = host.substring(host.indexOf(ISP_HOST_PREFIX) + ISP_HOST_PREFIX.length());

        if (TextUtils.isEmpty(tid)) {
            Toast.makeText(activity.getApplicationContext(), "ISP 정보수신 실패.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        //Ln.i("ISP Callback TID : " + tid);

        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.PRE_ISP_CONFIRM_ORDER + "?" + host);

        // 이전 WebActivity에서 PRE_ISP_CONFIRM_ORDER 주소를 부르기 위한 플래그 설정.
        // 이전 웹 WebActivity가 destroy되지 않은 상태라면 onNewIntent가 실행됨.
        // => 이전 WebActivity의 히스토리와 선택된 탭메뉴가 유지됨.
        // => 강제 destroy되었다면 onCreate가 실행되면서 히스토리 없어지고 탭메뉴는 홈이 선택됨.
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        activity.startActivity(intent);
        return true;
    }

}
