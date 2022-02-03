/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.setting.SettingActivity;
import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;

/**
 * 환경설정 앱 화면 요청.
 *
 */
public class SettingUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Intent intent = new Intent(activity, SettingActivity.class);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
        activity.startActivity(intent);
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.SETTING);
    }

}
