/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import java.net.URISyntaxException;

import gsshop.mobile.v2.util.MarketUtils;
import roboguice.util.Ln;

/**
 * 일반적인 카드결제 요청 처리.
 *
 */
public class GeneralCardUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Intent intent = null;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException ex) {
            return false;
        }
        
        if(url!= null && url.startsWith("intent")){
            try {
                if(activity.getPackageManager().resolveActivity(intent, 0) == null) {
                    String packagename = intent.getPackage();
                    if (packagename != null) {
                    	MarketUtils.goMarketSearch(activity, packagename);
                        return true;
                    }
                }
                Uri uri = Uri.parse(intent.getDataString());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            } catch (Exception e) {
                Ln.e(e);
                return false;
            }
        } else {
            try {
                Uri uri = Uri.parse(url);
                intent = new Intent(Intent.ACTION_VIEW, uri); 
                activity.startActivity(intent);
            } catch (Exception e) {
                Ln.e(e);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean match(String url) {
        // 안될 경우 주석해제할 것
        //if (url.contains("vguard"))//삼성,신한
        //    return true;
        //if (url.contains("droidxantivirus"))//현대카드
        //    return true;

        if (url.contains("v3mobile")) {
            return true;
        }

        if (url.contains("ansimclick")) { //현대안심클릭
            return true;
        }
        
        if (url.contains("intent")) {
            return true;
        }

        return false;
    }

}
