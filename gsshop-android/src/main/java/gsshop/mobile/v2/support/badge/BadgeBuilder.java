/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.badge;

import android.content.Context;
import android.view.View;

import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.Keys.PREF;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.menu.BadgeTextView;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import roboguice.util.Ln;

/**
 * 뱃지 UI 구성 담당.
 *
 */
public class BadgeBuilder {

    // 현재 액티비티의 뱃지 뷰
    //private final BadgeTextView cartBadge;
    //private final BadgeTextView orderBadge;
    private final View myshopBadge;
    private final BadgeTextView messageBadge = null;
    private final Context context;

    //    private int msgCnt;

    //public BadgeBuilder(BadgeTextView cartBadge, BadgeTextView orderBadge,BadgeTextView myshopBadge, Context context) {
    public BadgeBuilder(View myshopBadge, Context context) {
        //this.cartBadge = cartBadge;
        //this.orderBadge = orderBadge;
        this.myshopBadge = myshopBadge;
        this.context = context;
    }

    /**
     * 뱃지 정보 존재시 뱃지를 보여주며,
     * 정보 없으면 뱃지를 감춘다.
     */
    public void buildBadges() {
        //ViewUtils.hideViews(cartBadge, orderBadge, myshopBadge);
		if (myshopBadge != null) {
			ViewUtils.hideViews(myshopBadge);

			BadgeInfo badge = BadgeInfo.getCachedBadgeInfo();
			if (badge != null) {
				// if (badge.cart) {
				// buildNewMarkBadge(cartBadge, PREF.BADGE_CART_CONSUMED_DATE);
				// }

				// if (badge.order > 0) {
				// buildNumberMarkBadge(orderBadge, PREF.BADGE_ORDER_CONSUMED_DATE, badge.order);
				// }

				if (badge.myshop) {
					buildNewMarkBadge(myshopBadge, PREF.BADGE_MYSHOP_CONSUMED_DATE);
				}
			}
		}
    }

    /**
     * 모든 뱃지를 감춘다.
     */
    public void hideBadges() {
        //hideCartBadge(false);
        //hideOrderBadge(false);
        hideMyshopBadge(false);
    }

    /**
     * 스마트카드,주문배송,마이쇼핑 탭의 뱃지는
     * 마지막 확인 후에 '당일' 재접속시 더이상 보여주지 않음.
     * 마지막 확인이후 날짜 바뀌고 나서 처음 접속시에 보여줌.
     *
     * @param view view
     * @param lastConsumedDateKey lastConsumedDateKey
     */
    private void buildNumberMarkBadge(BadgeTextView view, String lastConsumedDateKey, int number) {
        String lastConsumedDate = PrefRepositoryNamed.get(MainApplication.getAppContext(), lastConsumedDateKey, String.class);
        if (lastConsumedDate == null || today().compareTo(lastConsumedDate) > 0) {
            view.setContents(number);
        }
    }

    /**
     * 스마트카드,주문배송,마이쇼핑 탭의 뱃지는
     * 마지막 확인 후에 '당일' 재접속시 더이상 보여주지 않음.
     * 마지막 확인이후 날짜 바뀌고 나서 처음 접속시에 보여줌.
     *
     * @param view view
     * @param lastConsumedDateKey lastConsumedDateKey
     */
    private void buildNewMarkBadge(View view, String lastConsumedDateKey) {
        String lastConsumedDate = PrefRepositoryNamed.get(MainApplication.getAppContext(), lastConsumedDateKey, String.class);
        if (lastConsumedDate == null || today().compareTo(lastConsumedDate) > 0) {
            if(view instanceof BadgeTextView) {
                try {
                    ((BadgeTextView) view).setContents(context.getString(R.string.badge_new));
                }
                catch (ClassCastException e) {
                    Ln.e(e.getMessage());
                }
            }
            else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     *
     * @param consumed 사용자가 확인하여 뱃지를 감추는 경우인가.
     */
//    public void hideCartBadge(boolean consumed) {
//        if (cartBadge.getVisibility() == View.GONE) {
//            return;
//        }
//
//        cartBadge.setVisibility(View.GONE);
//
//        BadgeInfo badge = BadgeInfo.getCachedBadgeInfo();
//        if (badge == null) {
//            return;
//        }
//
//        // 최종 확인일자를 설정에 저장.
//        if (badge.cart && consumed) {
//            PrefRepositoryNamed.save(PREF.BADGE_CART_CONSUMED_DATE, today());
//        }
//
//        badge.cart = false;
//    }

    /**
     *
     * @param consumed 사용자가 확인하여 뱃지를 감추는 경우인가.
     */
//    public void hideOrderBadge(boolean consumed) {
//        if (orderBadge.getVisibility() == View.GONE) {
//            return;
//        }
//
//        orderBadge.setVisibility(View.GONE);
//
//        BadgeInfo badge = BadgeInfo.getCachedBadgeInfo();
//        if (badge == null) {
//            return;
//        }
//
//        // 최종 확인일자를 설정에 저장.
//        if (badge.order > 0 && consumed) {
//            PrefRepositoryNamed.save(PREF.BADGE_ORDER_CONSUMED_DATE, today());
//        }
//
//        badge.order = -1;
//    }

    /**
     *
     * @param consumed 사용자가 확인하여 뱃지를 감추는 경우인가.
     */
    public void hideMyshopBadge(boolean consumed) {
        if (myshopBadge.getVisibility() == View.GONE) {
            return;
        }

        myshopBadge.setVisibility(View.GONE);

        BadgeInfo badge = BadgeInfo.getCachedBadgeInfo();
        if (badge == null) {
            return;
        }

        // 최종 확인일자를 설정에 저장.
        if (badge.myshop && consumed) {
            PrefRepositoryNamed.save(MainApplication.getAppContext(), PREF.BADGE_MYSHOP_CONSUMED_DATE, today());
        }

        badge.myshop = false;
    }

    private static String today() {
        return (String) android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date());
    }

}
