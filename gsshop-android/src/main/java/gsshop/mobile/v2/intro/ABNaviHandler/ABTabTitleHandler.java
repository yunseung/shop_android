/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro.ABNaviHandler;

import android.app.Activity;

import ai.evolv.AscendClient;

/**
 * 탭 타이틀 핸들러 속성으로 네비를 갖는다.
 *
 */
public class ABTabTitleHandler  {

    private String TAG = "ABTabTitleHandler";

    /**
     * 네비 아이디
     */
    public String naviID = "";
    /**
     * 탭 타이틀
     */
    public String tabTitle = "";
    /**
     * AB 대상 여부 초기값은 false
     */
    public boolean targetFlag = false;

    public ABTabTitleHandler(String _naviID) {
        this.naviID = _naviID;
    }

    public void ABsubscribeRequest(Activity activity,String preFix, AscendClient client) {
        //todo
        //추후 각 네비의 타이틀 변경을 ??? 왜 만들었어

    }
}
