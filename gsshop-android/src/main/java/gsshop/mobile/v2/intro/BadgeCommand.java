/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.support.badge.BadgeAction;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.ThreadUtils;

/**
 * 뱃지 정보 조회.
 *
 * 로그인/로그아웃 상태 관계 없이 뱃지정보를 조회한다.
 */
public class BadgeCommand extends BaseCommand {
    /**
     * badgeAction
     */
    @Inject
    private BadgeAction badgeAction;

    /**
     * restClient
     */
    @Inject
    private RestClient restClient;

    /**
     * BadgeCommand execute
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
    	long startTime = System.currentTimeMillis();
    	
        injectMembers(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            // 인트로에서는 로그아웃 상태인 경우에만 뱃지정보 조회함.
            // 자동로그인시 로그인처리 후 이벤트에 의해 뱃지정보가 조회되기 때문.
            // NOTE 캐시된 user 존재여부로 판단하는게 적합한가?
            if (User.getCachedUser() == null) {
                flushBadgeInfo(activity);
            }

            LogUtils.printExeTime("BadgeCommand", startTime);
        });
    }

    /**
     * 뱃지정보 조회를 비동기로 처리
     *
     * @param activity activity
     */
    private void flushBadgeInfo(Activity activity) {
        //로그아웃 상태인 경우 WebView의 간편주문인증 여부에 따라
        //간편주문 배송관련 뱃지정보가 조회되므로 간편주문 쿠키를 RestClient에 복사해줌
        CookieUtils.copyWebViewCookieToRestClient(activity, restClient, Keys.COOKIE.QUICK_ORDER);

        badgeAction.flushBadgeInfo();
    }
}
