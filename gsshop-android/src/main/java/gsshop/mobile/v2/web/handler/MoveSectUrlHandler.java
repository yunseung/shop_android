/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.webkit.WebView;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.web.WebUtils;

/**
 * [매장찾기 페이지 앱-웹 연동 규격]
 * 매장 찾기 연동 이후 [상품보기] [매장] 클릭시 Web -{@literal >} toapp 규격 호출 후 -{@literal >} App에서 해당 매장으로 이동 하게 처리 필요
 * toapp://movesect?http://dm14.gsshsp.com/sect/sectM.gs?sectid=1088087{@literal &}lsectid=1079903…
 * [설명]
 * toapp://         Web <-> 스키마 연동규격
 * movesect?        App 에서 매장(페이지) 이동하라
 * 현재 http://dm14.gsshsp.com/sect/sectM.gs?sectid=1088087{@literal &}lsectid=1079903….    이동될 매장 페이지 주소 (host 포함 full 주소)
 */
public class MoveSectUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://movesect?http://xxx.com/xxx.html
        //url에서 toapp://movesect? 제거
        //10/19 품질팀 요청
        if(url != null) {
            String moveSectUrl = url.substring((ServerUrls.APP.MOVE_SECT + "?").length());
            try {
                activity.finish();
                WebUtils.goWeb(activity, moveSectUrl);
            } catch (ActivityNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.MOVE_SECT);
    }

}
