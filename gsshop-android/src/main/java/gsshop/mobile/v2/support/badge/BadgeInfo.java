/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.badge;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * 메뉴탭에 표시할 뱃지 정보.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class BadgeInfo {

    /**
     * 스마트카트 탭에 New 표시를 할 것인가?
     */
    public boolean cart;

    /**
     * 주문배송 탭에 표시할 주문건수.
     */
    public int order;

    /**
     * 마이쇼핑 탭에 New 표시를 할 것인가?
     */
    public boolean myshop;

    @Override
    public String toString() {
        return "BadgeInfo [cart=" + cart + ", order=" + order + ", myshop=" + myshop + "]";
    }

    /**
     * 현재 뱃지정보 객체를 캐시한다.
     */
    public void cacheBadgeInfo() {
        PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.BADGE, this);
    }

    /**
     * 캐시된 뱃지 정보를 조회한다.
     * (앱 종료시 캐시를 지우거나 앱 시작시 지운후 새로 조회할 것)
     *
     * @return 캐시에 없으면 null 리턴.
     */
    public static BadgeInfo getCachedBadgeInfo() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), Keys.CACHE.BADGE, BadgeInfo.class);
    }

    /**
     * 캐시된 뱃지 정보를 지운다.
     */
    public static void clearBadgeInfoCache() {
        PrefRepositoryNamed.remove(MainApplication.getAppContext(), Keys.CACHE.BADGE);
    }

}
