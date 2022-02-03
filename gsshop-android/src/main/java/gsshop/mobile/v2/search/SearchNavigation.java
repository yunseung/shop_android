/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import android.app.Activity;
import android.content.Intent;

import com.google.inject.Inject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.ACTION;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.gtm.GTMEnum.GTM_SEARCH_ACTION;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 * 검색 네이게이션 처리.
 *
 * ※ SearchAtion과 SearchNavigation으로 분리한 이유 :
 * 검색어필드 입력 검색, QR코드 검색, 음성 검색 등을 통해
 * 검색결과 페이지로 이동하는 네비게이션 작업은
 * 프리젠테이션(Activity) 담당이므로 비즈니스레이어에 속한 SearchAction에서
 * 네비게이션을 포함하는 기능은 SearchNavigation으로 분리하였다.
 *
 */
@ContextSingleton
public class SearchNavigation {

    @Inject
    private SearchAction searchAction;

    //검색어 인코딩 변경 (euc-kr -> utf-8)
    private static final String uriEncoding = "utf-8";

    /**
     * 검색기록에 추가하고 검색 웹페이지로 이동한다.
     * @param activity
     * @param keyword
     * @param inputType 검색 타입을 지정하지 않으면 검색기록에 추가하지 않음.
     * @param searchType
     * @param ab           자신의 AB 타입
     * @param action GTM 로깅을 위한 카테고리 정보
     */
    public void search(Activity activity, String keyword, RecentKeyword.InputType inputType,String searchType,String ab,GTM_SEARCH_ACTION action) {
        //        if (inputType != null) {
        //            RecentKeyword k = new RecentKeyword(keyword, inputType);
        //            searchAction.addRecentKeyword(activity, k);
        //        }


        // 시작부분의 %, / 특수문자 제거
        String searchKey = keyword.replaceFirst("^[/%]+", "");
        if( searchType != null && searchType.length() > 1 )
        {
        	startWebActivity(activity, ServerUrls.WEB.SEARCH + searchType + ab +"&tq=" + encodingQuery(searchKey));
        }
        else
        {
        	startWebActivity(activity, ServerUrls.WEB.SEARCH + ServerUrls.WEB.SEARCH_DIRECT + ab + "&tq=" + encodingQuery(searchKey));
        }
        
        //GTM 클릭이벤트 전달
        GTMAction.sendEvent(activity, GTMEnum.GTM_AREA_CATEGORY, action.toString(), searchKey);


        /**
         * tensera
         */
//        try{
//            TenseraReporterHelper.reportSearch(encodingQuery(searchKey), inputType.toString(), searchType, ab, action.toString());
//        }catch (Exception e)
//        {
//
//        }

    }

    /**
     * 해당 웹주소를 보여주기 위해 WebActivity를 시작한다.
     */
    public void startWebActivity(Activity activity, String url) {
        Intent intent = new Intent(ACTION.WEB);
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
        activity.startActivity(intent);
    }

    public static String encodingQuery(String q) {
        try {
            // NOTE : 현재 서버에서 euc-kr로 인코딩된 파라미터를 받음
            return URLEncoder.encode(q, uriEncoding);
        } catch (UnsupportedEncodingException e) {
            Ln.e(e);
            return q;
        }
    }

}
