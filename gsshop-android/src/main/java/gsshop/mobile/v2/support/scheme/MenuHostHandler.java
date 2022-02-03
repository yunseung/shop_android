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

import com.gsshop.mocha.core.util.ActivityUtils;

import java.util.Locale;

import co.ab180.airbridge.Airbridge;
import co.ab180.airbridge.AirbridgeCallback;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * 우리 앱을 실행하여 특정 탭메뉴의 특정페이지로 이동하는 경우 처리.
 *
 * [형식]
 * gsshopmobile://[menuname]?[url]
 *
 * (1) menuname
 * - 선택할 탭메뉴 이름 : home, search, cart, order, myshop 가능
 *
 * (2) url
 * - 이동할 웹페이지 주소
 * - 슬래시(/)로 시작하면 앞에 http://m.gsshop.com가 자동으로 붙음
 * - url이 없으면 선택된 탭 메뉴의 기본화면이 보여짐
 * - url의 한글 파라미터는 반드시 euc-kr로 인코딩해서 링크 걸어야함
 *
 * [예제]
 *
 * gsshopmobile://home
 * gsshopmobile://home?/prd/prd.gs?prdid=10446108
 * gsshopmobile://home?http://m.gsshop.com/prd/prd.gs?prdid=10446108
 * gsshopmobile://search
 * gsshopmobile://search?http://m.gsshop.com/search/searchSect.gs?tq=%B1%B8%B5%CE (%B1%B8%B5%CE은 '구두'를 euc-kr로 인코딩한 값)
 * gsshopmobile://cart
 * gsshopmobile://cart?http://m.gsshop.com/mygsshop/myWishListMain.gs
 * gsshopmobile://order?http://m.gsshop.com/mygsshop/myOrderList.gs?orderType={@literal &}dateFlag={@literal &}pageIdx=1{@literal &}fromDate=20121127{@literal &}toDate=20121227
 * gsshopmobile://myshop
 *
 * #카카오링크 추가
 * - QueryParameter에 대한 처리는 본 핸들러와 동일하므로 별도로 kakaolink 핸들러는 생성 안함
 * - 데이타 형식 : kakao[kakao_app_key]://kakaolink?url=[파라미터]
 *   예) kakao521258d48bf4d8214afe97647c9b86f6://kakaolink?url=gsshopmobile://home?http://m.gsshop.com/prd/prd.gs?prdid=10446108
 *
 * #airbridge 추가
 * -데이타 형식 : scheme://host/...
 * 예) http://my.gsshop.com/@gsshop/naver.searchad?campaign=...
 */
public class MenuHostHandler implements UriHostHandler {

    private static final String FROM_DITTO_TO_PRD = "dittoappgateway";
    private final String KAKAOLINK_HOST;
    private final static String INBOX_URL = "inbox";
    public static final String GSSHOP_SCHEMA = "gsshopmobile";
    public static final String SCHEMA_TABID = "TABID";
    public static final String GSSHOP_SCHEMA_HOME = GSSHOP_SCHEMA + "://home";
    private final String AIRBRIDGE_HOST;

    public MenuHostHandler(Activity activity) {
        this.KAKAOLINK_HOST = activity.getString(R.string.kakaolink_host);
        this.AIRBRIDGE_HOST = activity.getString(R.string.airbridge_app_host);
    }

    @Override
    public boolean match(Uri data, String host) {
        if (!TextUtils.isEmpty(host)) {
            if (host.equalsIgnoreCase(TabMenu.HOME_NAME)) {
                return true;
            }

            if (host.equalsIgnoreCase(SCHEMA_TABID)) {
                return true;
            }

            if (host.equalsIgnoreCase(TabMenu.MYSHOP_NAME)) {
                return true;
            }
            //카카오링크 매칭 추가
            if (host.equalsIgnoreCase(KAKAOLINK_HOST)) {
                return true;
            }
            //airbridge
            if (host.equalsIgnoreCase(AIRBRIDGE_HOST)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean handle(Activity activity, Uri data) {
        Uri dataUri = data;

        //카카오링크인 경우 카카오링크 스키마를 제거한 후 GSSHOP 스키마만 전달함
        final String scheme = dataUri.getScheme();
        if (scheme.equalsIgnoreCase(activity.getString(R.string.kakao_scheme)) || scheme.equalsIgnoreCase(activity.getString(R.string.kakao_scheme_new))) {
            String uriString = dataUri.getQueryParameter("url");
            if (uriString != null) {
                dataUri = Uri.parse(uriString);
            } else {
                //카카오링크 공유 후 최하단 버튼 클릭한 경우
                //링크정보가 "kakao521258d48bf4d8214afe97647c9b86f6://kakaolink" 여기까지만 들어옴
                //그래서 이때는 앱홈으로 보내도록 처리
                dataUri = Uri.parse(GSSHOP_SCHEMA_HOME);
            }
        }

        //airbridge 경우
        if ((scheme.equalsIgnoreCase(activity.getString(R.string.airbridge_app_scheme_http)) || scheme.equalsIgnoreCase(activity.getString(R.string.airbridge_app_scheme_https)))
            && AIRBRIDGE_HOST.equalsIgnoreCase(data.getHost())) {

            //딥링크로 앱이 열렸으면 onSuccess 함수가 호출된 다음 onComplete 함수가 호출됩니다.
            //딥링크 없이 앱이 열렸으면 onComplete 함수만 호출됩니다.
            Airbridge.getDeeplink(activity.getIntent(), new AirbridgeCallback<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Process deeplink data
                    startActivity(activity, uri);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    // Error
                    activity.finish();
                }

                @Override
                public void onComplete() {
                    // After process deeplink data
                    activity.finish();
                }
            });
        } else {
            startActivity(activity, dataUri);
        }

        return true;
    }

    /**
     * 스키마 URI를 분석하여 해당하는 액티비티를 실행한다.
     *
     * @param activity 액티비티
     * @param dataUri 스키마 URI
     */
    private void startActivity(Activity activity, Uri dataUri) {
        // getEncodedQuery : 링크 걸린 문자열 그대로 사용
        // getQuery : 인코딩된 문자열은 디코딩됨
        String url = dataUri.getQuery();

        // #뒤에 붙은 모든 string. navigationId에 해당
        String encodedFragment = dataUri.getEncodedFragment();

        //Datahub에서 사용할 media값 추출 (본 값은 ecid의 mediatype값에 우선한다.)
        if (!TextUtils.isEmpty(url)) {
            try {
                String media = Uri.parse(url).getQueryParameter(Keys.PARAM.MEDIA);
                if (!TextUtils.isEmpty(media)) {
                    DatahubAction.MEDIA_TYPE = media;
                }
            } catch(Exception e) {
                Ln.e(e);
            }
        }

        // 홈의 특정 section으로 이동
        Intent intent;
        if (encodedFragment != null && DisplayUtils.isValidNumberStringExceptZero(encodedFragment) &&
                url != null && url.contains(ServerUrls.ROOT_URL_PATTERN) ) {
            if (MainApplication.isAppView) {
                //앱이 이미 실행되어 있는 경우 인트로 화면 로딩없이 바로 홈화면으로 이동
                intent = new Intent(Keys.ACTION.APP_HOME);
            } else {
                //앱이 실행중이 아닐 경우 인트로부터 정상적인 프로세스로 실행
                intent = ActivityUtils.getMainActivityIntent(activity.getApplicationContext());
            }
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
            intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true);
            intent.putExtra(Keys.INTENT.NAVIGATION_ID, encodedFragment);
        } else if (!TextUtils.isEmpty(url)) {
            // path만 존재하는 경우 도메인(http://m.gsshop.com) 추가
            // 2월 2일 이걸 왜 에러로 잡지 그래서 url != null
            if (url != null && url.startsWith("/")) {
                url = ServerUrls.getHttpRoot() + url;
            }

            if (url != null && url.toLowerCase(Locale.getDefault()).contains(FROM_DITTO_TO_PRD)) {
                // ditto의 구매하기로 gs app으로 온 경우에 대한 예외처리(쿠키 보존을 위해서 바로 NoTabWeb으로 이동).
                // 1. gs app이 구동된 상태
                // 1-1. header back icon : gs app을 종료하지 않고 ditto app으로 복귀
                // 1-2. HW back key : 는 gs app 홈으로 이동
                // 2. 구동되지 않은 상태
                // 2-1. header back icon : gs app을 종료하지 않고 ditto app으로 복귀
                // 2-2. HW back key : gs app을 종료하고 ditto app으로 복귀
                // [참고] ditto에서 스마트카드,마이쇼핑 등으로 이동하는 경우에는 default web으로 처리됨.
                intent = new Intent(Keys.ACTION.NO_TAB_WEB);
            } else if (WebUtils.isNoTabPage(url)) {
                // 단품, 딜
                intent = ActivityUtils.getMainActivityIntent(activity.getApplicationContext());
                intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
                intent.putExtra(Keys.INTENT.FROM_TAB_MENU, false);
            } else if (WebUtils.isMyShop(url)) {
                // 애니메이션 때문에 마이쇼핑 구분
                intent = ActivityUtils.getMainActivityIntent(activity.getApplicationContext());
                intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
                intent.putExtra(Keys.INTENT.FROM_TAB_MENU, false);

            } else {
                intent = ActivityUtils.getMainActivityIntent(activity.getApplicationContext());
                intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
                intent.putExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING, true);
            }
            // to 민수 매니저 (여기는 match에 모바일라이브 해당하는 너석 없으니 모바일라이브 확인 추가 하지 않았습니다.)

            intent.putExtra(Keys.INTENT.WEB_URL, url);
        } else {
            intent = ActivityUtils.getMainActivityIntent(activity.getApplicationContext());
        }

        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.convertMenuNameToIndex(dataUri.getHost()));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
