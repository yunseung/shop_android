/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.badge;

import android.content.Context;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.RestClientUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 *
 * NOTE : @Singleton으로 선언하는 경우 EventBus에 중복등록되어 에러발생.
 */
@ContextSingleton
public class BadgeAction {

    @Inject
    private Context context;

    @Inject
    private RestClient restClient;

    public void onEventBackgroundThread(Events.LoggedInEvent event) {
        flushBadgeInfo();
    }

    public void onEventBackgroundThread(Events.LoggedOutEvent event) {
        //WebView의 간편주문인증 쿠키를 RestClient에 복사
        CookieUtils.copyWebViewCookieToRestClient(context, restClient, Keys.COOKIE.QUICK_ORDER);

        flushBadgeInfo();
    }

    /**
     * 뱃지 정보를 새로 갱신한다.
     *
     * 캐시된 뱃지 정보를 지우고 서버에서 새로 조회하여 캐싱.
     * 로그아웃 상태인 경우 간편주문인증 여부에 따라
     * 간편주문 배송관련 뱃지정보가 조회됨.
     *
     * NOTE : 서버단에서 뱃지정보 조회시 에러 발생해도 무시함.
     */
    public void flushBadgeInfo() {
        BadgeInfo.clearBadgeInfoCache();

        BadgeInfo info = null;

        try {
            info = RestClientUtils.INSTANCE.get(restClient, ServerUrls.REST.API_PATH + ServerUrls.REST.BADGE, BadgeInfo.class);
        } catch (Exception e) {
            Ln.e(e, "뱃지 정보 조회 실패");
            return;
        }
//10/19 품질팀 요청
        if(info != null){
            info.cacheBadgeInfo();
        }
        EventBus.getDefault().post(new Events.BadgeInfoFlushedEvent());
        //Ln.i("뱃지 정보 갱신됨");
    }

}
