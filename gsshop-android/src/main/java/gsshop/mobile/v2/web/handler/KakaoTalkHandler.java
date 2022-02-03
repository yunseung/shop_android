/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.web.AndroidBridge;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * 카카오톡이 카카오페이 사용가능 버전인지 확인 후 호출한다.
 *
 * - 형식 : kakaotalk://kakaopay/submit?txn_id=T141006004567661{@literal &}return_url=gsshopmobile://KID=T141006004567661
 * 					{@literal &}cancel_url=gsshopmobile://KID=T141006004567661$payReqType-ORD_SHT$cancelYn-Y
 * - 참고 : cns 가이드 확인
 */
public class KakaoTalkHandler implements WebUrlHandler {
	
	private static final String updateMsg = "kakaotalk 어플을 최신 버전으로 설치해 주십시오.";
	private static final String installMsg = "kakaotalk 어플을 설치해 주십시오.";
	
    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        
    	try {
    		int retVal = new AndroidBridge(activity).checkKAKAOPayAvailable("com.kakao.talk");
    		
    		if (retVal == 1) {
    			activity.startActivityIfNeeded(intent, -1);
    		} else if (retVal == -1) {	//이하버전
            	Toast.makeText(activity.getApplicationContext(), updateMsg, Toast.LENGTH_SHORT)
                .show();
    		} else if (retVal == 0) {	//미섶치
            	Toast.makeText(activity.getApplicationContext(), installMsg, Toast.LENGTH_SHORT)
                .show();
    		}
        } catch (ActivityNotFoundException ex) {
        	Toast.makeText(activity.getApplicationContext(), updateMsg, Toast.LENGTH_SHORT)
            .show();
        }
    	
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.KAKAO_MOBILE);
    }

}
