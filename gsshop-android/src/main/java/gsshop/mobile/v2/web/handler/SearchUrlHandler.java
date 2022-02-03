/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.ACTION;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.TabMenu;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.webkit.WebView;

/**
 * 검색 화면 띄운다.
 *
 */
public class SearchUrlHandler implements WebUrlHandler {
	
	private boolean isMoveSearch = false;
	
    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
    	
    	if(!isMoveSearch){
			isMoveSearch = true;
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					isMoveSearch = false;
				}
			}, 1000);

			Intent intent = new Intent(ACTION.SEARCH);                
			intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
			intent.putExtra(Keys.INTENT.FROM_WEB, true);
			activity.startActivityForResult(intent, Keys.REQCODE.HOME_SEARCHING);
		}
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.SEARCH_MOVE);
    }

}
