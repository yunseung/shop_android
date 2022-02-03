/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * 단품/딜 SMS 공유하기 처리. 네이티브 SMS 앱 런칭.
 *
 */
public class SmsUrlHandler implements WebUrlHandler {

    private static final String SMS_PARAM = "msg";

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // deconding된 sms body string 가져오기
        Uri data = Uri.parse(url);
        String smsBody = data.getQueryParameter(SMS_PARAM);

        // launching sms app
        // jhkim "vnd.android-dir/mms-sms"을 지원하지 않는 기기(nexus)에 대한 예외 처리 추가
        try{
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("sms_body", smsBody);

            activity.startActivity(smsIntent);
            return true;
        }catch(ActivityNotFoundException e){
            return false;
        }

    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.MOVE_SMS);
    }

}
