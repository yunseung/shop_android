/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.menu;

import android.content.Intent;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;

/**
 * 탭메뉴 구분 인덱스.
 *
 */
public abstract class TabMenu {

    // NOTE : 이 값을 함부로 변경하지 말 것.	
    private static int tabIndex = 0;
    
    public static int maxTabIndex = 5;

    public static int tabStartIndex = -1;    
    public static int tabBackIndex = -1;
    // 각 탭 메뉴를 구분하는 정수값
    public static final int HOME = tabIndex++;

    public static final int CATEGORY = tabIndex++;
    
    public static final int MYSHOP = tabIndex++;

    public static final int ZZIMPRD = tabIndex++;

    public static final int LASTPRD = tabIndex++;

    // 이제 포함되지 않는다. 
    public static final int SEARCH = tabIndex + 1;

    //public static final int SEARCH = tabIndex++;

    //public static final int CART = tabIndex++;

    //public static final int ORDER = tabIndex++;

    

    // 각 탭 메뉴의 고유한 이름(scheme를 통해 호출시 host 구분)
    public static final String HOME_NAME = "home";

    public static final String CATEGORY_NAME = "category";

    //public static final String SEARCH_NAME = "search";

    //public static final String CART_NAME = "cart";

    //public static final String ORDER_NAME = "order";

    public static final String MYSHOP_NAME = "myshop";

    /**
    * 탭메뉴에서 호출되었는가?
    *
    * @param intent intent
    * @return fromTabMenu
    */
    public static boolean fromTabMenu(Intent intent) {
        return intent.getBooleanExtra(Keys.INTENT.FROM_TAB_MENU, false);
    }

    /**
     * 주어진 인테트에 담긴 현재 선택 탭메뉴 정보.
     *
     * 인텐트에 선택된 탭메뉴 정보가 없으면 {TabMenu.HOME} 리턴.
     *
     * @param intent intent
     * @return getTabMenu
     */
    public static int getTabMenu(Intent intent) {
        return intent.getIntExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
    }

    /**
     * home, search, cart, order, myshop 문자열에
     * 해당하는 메뉴 인덱스 리턴.
     *
     * @param menuName menuName
     * @return null인 경우 HOME 리턴.
     */
    public static int convertMenuNameToIndex(String menuName) {
        if (menuName == null) {
            return HOME;
        }

        if (HOME_NAME.equalsIgnoreCase(menuName)) {
            return HOME;
        }

        if (CATEGORY_NAME.equalsIgnoreCase(menuName)) {
            return CATEGORY;
        }

//        if (SEARCH_NAME.equalsIgnoreCase(menuName)) {
//            return SEARCH;
//        }

//        if (CART_NAME.equalsIgnoreCase(menuName)) {
//            return CART;
//        }
//
//        if (ORDER_NAME.equalsIgnoreCase(menuName)) {
//            return ORDER;
//        }

        if (MYSHOP_NAME.equalsIgnoreCase(menuName)) {
            return MYSHOP;
        }

        return HOME;
    }

    /**
     * 웹페이지로 표현되는 탭메뉴의 기본 주소.
     *
     * @param tabMenu tabMenu
     * @return getDefaultWebUrl
     */
    public static String getDefaultWebUrl(int tabMenu) {
        if (tabMenu == TabMenu.HOME) {
            return ServerUrls.WEB.HOME;
        }

//        if (tabMenu == TabMenu.CART) {
//            return ServerUrls.WEB.SMART_CART;
//        }
//
//        if (tabMenu == TabMenu.ORDER) {
//            return ServerUrls.WEB.ORDER;
//        }

        if (tabMenu == TabMenu.MYSHOP) {
            return ServerUrls.WEB.MY_SHOP;
        }

        return ServerUrls.WEB.HOME;
    }

}
