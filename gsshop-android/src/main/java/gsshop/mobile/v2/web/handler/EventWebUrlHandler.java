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
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.web.WebActivity;
import roboguice.inject.InjectResource;

/**
 * Event Receive Handler
 *
 */
public class EventWebUrlHandler implements WebUrlHandler {
	
	@InjectResource(R.integer.update_check_connection_timeout)
    private int connectionTimeout;

    @InjectResource(R.integer.update_check_read_timeout)
    private int readTimeout;

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://eventrecv?http://m.gsshop.com/testEvent.

        // 호출될 웹페이지
        //10/19 품질팀 요청
        if(url != null) {
            String targetUrl = url.substring((ServerUrls.APP.EVENT_WEB + "?").length());

            Intent intent = new Intent(activity, WebActivity.class);
            targetUrl = Uri.parse(targetUrl).buildUpon().
                    appendQueryParameter("devId", DeviceUtils.getUuid(activity)).build().toString();
            intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);
            activity.startActivityForResult(intent, Keys.REQCODE.EVENT);
        }
        /*try {
			setEventMsg(targetUrl + "&devId=" + DeviceUtils.getDeviceId());
		} catch (IOException e) {
			e.printStackTrace();
		}*/

        // 스르륵 열리는 효과
        //activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        
        

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.EVENT_WEB);
    }
    
    /*public void setEventMsg(String eventUrl) throws IOException {
        URL url = new URL(eventUrl);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.connect();
        // byte[] contents = FileCopyUtils.copyToByteArray(conn.getInputStream());
    }*/
}
