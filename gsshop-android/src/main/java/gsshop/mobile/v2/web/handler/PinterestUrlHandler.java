/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import gsshop.mobile.v2.ServerUrls;
import android.app.Activity;
import android.net.Uri;
import android.webkit.WebView;

import com.pinterest.pinit.PinIt;
import com.pinterest.pinit.PinItListener;

/**
 * 핀터레스트 url을 처리한다.
 *
 * sample)
 * toapp://pinterest?imageurl=http://image.gsshop.com/mi09/deal/dealno/80058_LBimg_1_3.jpg&sourceurl=http://m.gsshop.com&description=설명
 */
public class PinterestUrlHandler implements WebUrlHandler {
	
	private PinIt pinIt;
	
    @Override
    public boolean handle(Activity activity, WebView webview, String url) {

    	
        Uri uri = Uri.parse(url);
        
        PinIt.setPartnerId("1442421"); // required
        PinIt.setDebugMode(false); // optional
        
        pinIt = new PinIt();
        pinIt.setImageUrl(uri.getQueryParameter("imageurl"));
        pinIt.setUrl(uri.getQueryParameter("sourceurl"));
        pinIt.setDescription(uri.getQueryParameter("description"));
        pinIt.setListener(_listener);
        pinIt.doPinIt(activity);
        return true;
    }


    PinItListener _listener = new PinItListener() {

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onComplete(boolean completed) {
            super.onComplete(completed);
        }

        @Override
        public void onException(Exception e) {
            super.onException(e);
        }

    };
    
    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.PINTEREST);
    }
}
