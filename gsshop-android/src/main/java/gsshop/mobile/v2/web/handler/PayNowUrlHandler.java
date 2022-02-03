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
import android.provider.Browser;
import android.webkit.WebView;

import gsshop.mobile.v2.util.MarketUtils;
import gsshop.mobile.v2.web.AndroidBridge;
import roboguice.util.Ln;

/**
 * LGU+ Paynow 앱에 결제를 요청한다.<br>
 * Paynow 앱에서 결제 처리 후 GSSHOP 앱을 스키마로 호출하면 PaynowCallbackHostHandler에서 처리한다.<br>
 * 
 * <pre>
 * url sample)
 * lguthepay://reqpaySimpleShop?UPAY_FORM_NO=2.0
 * &UPAY_MER_ID=xxx
 * &UPAY_MER_TID=xxx
 * &UPAY_SUB_MID=xxx
 * &UPAY_SUB_MNAME=xxx
 * &UPAY_PROD_CODE=
 * &UPAY_PROD_NAME=xxx
 * &UPAY_AMOUNT=xxx
 * &UPAY_BUYER=xxx
 * &UPAY_BUYERPHONE=xxx
 * &UPAY_CARD_INSTALLRANGE=0
 * &UPAY_CARD_NOINTINF=
 * &UPAY_CARD_USABLECARD=000
 * &UPAY_SELECTED_CARD_YN=Y
 * &RETURN_URL=http://.../.../xxx.gs
 * &UPAY_JOINNO=
 * &UPAY_CLIENT_IP=x.x.x.x
 * &UPAY_REDIRECT_TYPE=2
 * &REDIRECT_URL=gsshopmobile://NID=0000000000
 * &UPAY_CARD_AFTER_DC=null
 * &UPAY_ENC_TYPE=00
 * </pre>
 */
public class PayNowUrlHandler extends GeneralCardUrlHandler {

	private static final String TAG = "PayNowUrlHandler";
	private static final String PAYNOW_PACKAGE_NAME = "com.lguplus.paynow";
	private static final String PAYNOW_SCHEME_NAME = "lguthepay://";
	
    @Override
    public boolean match(String url) {
        if (url.startsWith(PAYNOW_SCHEME_NAME)) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Intent intent;
//        try {
//            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
//        } catch (URISyntaxException ex) {
//            Ln.e(ex.toString());
//            return false;
//        }

        try {
        	boolean installFlag = new AndroidBridge(activity).isInstalledApp(PAYNOW_PACKAGE_NAME);
        	//paynow 앱이 설치된 경우 결제정와 함께 페이나우 앱 호출
            if (installFlag) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            	intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.putExtra(Browser.EXTRA_APPLICATION_ID, activity.getPackageName());
                activity.startActivity(intent);
            //paynow 앱이 설치되지 않은 경우 마켓으로 이동
            } else {
                MarketUtils.goMarketSearch(activity, PAYNOW_PACKAGE_NAME);
            }
        } catch (ActivityNotFoundException e) {
            Ln.e(e.toString());
            return false;
        }
        
        return true;
    }    
}
