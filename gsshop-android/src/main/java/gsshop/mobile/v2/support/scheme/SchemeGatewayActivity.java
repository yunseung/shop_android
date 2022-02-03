/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.scheme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.R;

/**
 * 커스텀 URI 스키마(gsshopmobile)를 통해 우리앱을 실행하고자 하는 경우
 * 관문 역할을 하는 액티비티.
 *
 * 웹브라우저 등 다른 앱에서 우리 앱을 호출하기 위해 gsshopmobile 스키마를
 * 사용하는데, 이때 전달된 데이터 URI를 분석하여(host, query 등)
 * 적절한 액티비티를 실행하거나 작업을 수행한다.
 *
 * 참고 :
 * http://blog.daum.net/mailss/36
 * http://underclub.tistory.com/361
 *
 * AndroidManifest.xml에 아래와 같이 인텐트 필터 설정.
 *
    {@literal <}intent-filter{@literal >}
        {@literal <}action android:name="android.intent.action.VIEW" /{@literal >}
        {@literal <}category android:name="android.intent.category.DEFAULT" /{@literal >}
        {@literal <}category android:name="android.intent.category.BROWSABLE" /{@literal >}
        {@literal <}data android:scheme="gsshopmobile" /{@literal >}
    {@literal <}/intent-filter{@literal >}
 *
 */
public class SchemeGatewayActivity extends AbstractBaseActivity {

    private final ArrayList<UriHostHandler> handlers = new ArrayList<UriHostHandler>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data == null || TextUtils.isEmpty(data.getHost())) {
            finish();
            return;
        }

        if (handlers.isEmpty()) {
        	//Host값이 없는 경우 결제 관련 핸들러는 체크할 필요 없음
        	if (!TextUtils.isEmpty(data.getHost())) {
	            handlers.add(new IspCallbackHostHandler());
	            handlers.add(new KakaoCallbackHostHandler());
	            handlers.add(new PaynowCallbackHostHandler());
        	}

            handlers.add(new MenuHostHandler(this));
        }

        UriHostHandler h = findDataUriHandler(handlers, data);
        if (h != null) {
            h.handle(this, data);
        }

        //airbirdge 경우 AirBridge.getDeeplink().fetch 콜백에서 finish 처리
        //이유는 광고 클릭시 앱이 열리고 바로 닫혔다 콜백 호출되면서 다시 열리기 때문
        String scheme = data.getScheme();
        if (!((scheme.equalsIgnoreCase(getString(R.string.airbridge_app_scheme_http)) || scheme.equalsIgnoreCase(getString(R.string.airbridge_app_scheme_https)))
                && getString(R.string.airbridge_app_host).equalsIgnoreCase(data.getHost()))) {
            //airbridge 아닌 경우
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private static UriHostHandler findDataUriHandler(ArrayList<UriHostHandler> handlers, Uri data) {
        String host = data.getHost();
        for (UriHostHandler h : handlers) {
            if (h.match(data, host)) {
                return h;
            }
        }

        return null;
    }

}
