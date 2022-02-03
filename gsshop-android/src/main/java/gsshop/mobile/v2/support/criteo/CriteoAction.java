/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.criteo;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 * 크리테오 이벤트 전달 클래스
 * http://www.criteo.com/
 */
@ContextSingleton
public class CriteoAction {

    //end-point address
    private static final String END_POINT = "http://widget.as.criteo.com/m/event";

    //Account 별 정보
    private static final String ACCOUNT_NAME = "com.gsshop";  //도메인
    private static final String CONTRY_CODE = "kr";  //국가코드
    private static final String LANG_CODE = "ko";  //언어코드

    //모바일 디바이스의 OS 타입 정보 (안드로이드)
    private static final String SITE_TYPE = "aa";

    //Protocol Version
    private static final String VERSION = "s2s_v1.0.0";

    //이벤트 타입 정의
    private static final String VIEW_HOME = "viewHome";

    @Inject
    private RestClient restClient;

    /**
     * 크리테오 end-point로 모바일 리타겟팅 이벤트 정보를 전송한다.
     */
    public void sendEvent() {
        String url = "";

        try {
            CriteoData criteoData = new CriteoData();

            //Account 정보 세팅
            criteoData.setAccountData(ACCOUNT_NAME, CONTRY_CODE, LANG_CODE);

            //모바일 디바이스의 OS 타입을 안드로이드로 세팅
            criteoData.setSite_type(SITE_TYPE);

            //구글 광고아이디 세팅
            criteoData.setIdData(DeviceUtils.getAdvertisingId());

            //이벤트 정보 세팅 (View Home)
            criteoData.setEventsData(VIEW_HOME);

            //프로토콜 버전 세팅
            criteoData.setVersion(VERSION);

            String json = new Gson().toJson(criteoData);
            //Ln.i("Criteo Data json : " + json);

            //appendQueryParameter 함수내에서 json에 대한 인코딩을 수행하기 때문에 별도 인코딩을 하지 않음
            url = Uri.parse(ServerUrls.END_POINT).buildUpon()
                    .appendQueryParameter("data", json)
                    .build().toString();
            //Ln.i("Criteo url call : " + url);

            CookieUtils.syncAndcopyWebViewCookiesToRestClient(MainApplication.getAppContext(), restClient, url, String.class);
            //Ln.i("Criteo response : " + ret);

        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }
}
