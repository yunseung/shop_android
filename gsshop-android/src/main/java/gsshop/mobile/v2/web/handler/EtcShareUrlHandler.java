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

import com.pinterest.pinit.PinIt;
import com.pinterest.pinit.PinItListener;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;

/**
 * 공유하기(기타) url을 처리한다.
 *
 * sample)
 * toapp://etc_shere?sourceurl=http://m.gsshop.com{@literal &}description=설명0
 */

public class EtcShareUrlHandler implements WebUrlHandler {
    @Override
    public boolean handle(Activity activity, WebView webview, String url) {

    	
        Uri uri = Uri.parse(url);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, uri.getQueryParameter("sourceurl"));
        intent.putExtra(Intent.EXTRA_TEXT, uri.getQueryParameter("description"));
        Intent chooser = Intent.createChooser(intent, activity.getString(R.string.shere));
        activity.startActivity(chooser);

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.ETC_SHARE);
    }
}
