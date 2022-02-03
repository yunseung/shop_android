/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.gsshop.mocha.core.util.ActivityUtils;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.gtm.GTMEnum;

/**
 * 메인 앱위젯 프로바이더.
 *
 */
public class MainAppWidgetProvider extends AppWidgetProvider {

    /**
     * Send different "requestCode" for different PendingIntents.
     *
     * http://stackoverflow.com/questions/6891620/multiple-onclick-on-widget-for-the-same-intent
     * http://stackoverflow.com/questions/7645949/two-buttons-of-android-widgets-calling-same-activity-with-diffrent-intents
     */
    private int pendingRequestCode = 789621;
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);

        // 생방송 보기
        Intent intent = new Intent(context, LiveTVPopupActivity.class);
        // taskAffinity 설정과 함께 task 분리
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, pendingRequestCode++, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_play_tv, pi);

        // 생방송 상품 바로 주문
        views.setOnClickPendingIntent(R.id.btn_order_tv_product,
                getClickPendingIntent(context, ServerUrls.WEB.LIVE_TV + ServerUrls.WEB.BACKBUTTON_FLAG_TYPE2, 
                		TabMenu.HOME, GTMEnum.GTM_WIDGET_LABEL.LiveGoods));

        // 검색/카테고리 : 검색탭이 역활을 하지 않기 때문에 
        views.setOnClickPendingIntent(R.id.btn_tab_search,
        		getClickPendingIntent(context, null, TabMenu.HOME, GTMEnum.GTM_WIDGET_LABEL.Search));

        // 스마트 카트
        views.setOnClickPendingIntent(R.id.btn_tab_cart,
                getClickPendingIntent(context, ServerUrls.WEB.SMART_CART, TabMenu.HOME, GTMEnum.GTM_WIDGET_LABEL.Cart));

        // 주문 배송
        views.setOnClickPendingIntent(R.id.btn_tab_order,
                getClickPendingIntent(context, ServerUrls.WEB.ORDER, TabMenu.HOME, GTMEnum.GTM_WIDGET_LABEL.Order));

        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }

    private PendingIntent getClickPendingIntent(Context context, String url, int tabMenu, 
    		GTMEnum.GTM_WIDGET_LABEL label) {
        Intent intent = ActivityUtils.getMainActivityIntent(context);

        // 앱을 띄운 상태에서 위젯 클릭시 IntroActivity가 새로 실행되지 않는 문제.
        // => http://stackoverflow.com/questions/1937236/launching-activity-from-widget
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.INTENT.WEB_URL, url);
        intent.putExtra(Keys.INTENT.TAB_MENU, tabMenu);
        intent.putExtra(Keys.INTENT.WIDGET_TYPE, label.toString());
        intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
        
//        PendingIntent pi = PendingIntent.getActivity(context, pendingRequestCode++, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        return PendingIntent.getActivity(context, pendingRequestCode++, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
