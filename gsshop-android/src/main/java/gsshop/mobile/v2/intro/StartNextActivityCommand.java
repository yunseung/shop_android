/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.content.Intent;

import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import java.util.Timer;
import java.util.TimerTask;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.web.WebUtils;

import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.web.WebUtils.isNativeProduct;

/**
 * 다음 액티비티로 이동.
 *
 */
public class StartNextActivityCommand extends BaseCommand {

	/**
	 * StartNextActivityCommand TimerTask
	 */
	private TimerTask mTask;

	/**
	 * StartNextActivityCommand Timer
	 */
	private Timer mTimer;

	/**
	 * StartNextActivityCommand
	 * 인트로 탈출하는 마지막 커맨드
	 *
	 * @param activity activity
	 * @param chain chain
     */
	@Override
	public void execute(final Activity activity, CommandChain chain) {
		final long startTime = System.currentTimeMillis();

		mTask = new TimerTask() {
			@Override
			public void run() {
				if(MainApplication.isAuthCheck && MainApplication.isPushCheck){
					mTimer.cancel();
					MainApplication.introCompleted = true;
					HomeActivity.isMainLoaded = false;
					LogUtils.printExeTime("StartNextActivityCommand", startTime);
					activity.startActivity(getCustomizedIntent(activity));
					activity.finish();
				}
			}
		};

		mTimer = new Timer();

		mTimer.schedule(mTask, 0, 100);
	}

    /**
     * push, install referrer 등 다양한 진입점으로부터 전달받은 데이터를 가공하여 인텐트를 생성한다.
     *
     * @param activity 액티비티
     */
	private Intent getCustomizedIntent(Activity activity) {
        // 선택할 탭메뉴
        int targetTabMenu = TabMenu.getTabMenu(activity.getIntent());

        // 보여줄 웹페이지
        String targetUrl = activity.getIntent().getStringExtra(Keys.INTENT.WEB_URL);

        //백버튼 클릭시 해당 액티비티를 닫고나서, 메인으로 이동할지 구분하는 플래그
        boolean backToMain = activity.getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false);
        //탭메뉴로 부터 호출되었는지 구분하는 플래그
        boolean fromTabMain = activity.getIntent().getBooleanExtra(Keys.INTENT.FROM_TAB_MENU, true);
        //위젯으로 부터 호출된 경우 메뉴 구분자
        String widgetMenu = activity.getIntent().getStringExtra(Keys.INTENT.WIDGET_TYPE);

        Intent intent;

        // 실행할 액티비티
        String action = Keys.ACTION.WEB;

        // WEB_M, APP_M 구분
        if (targetUrl != null) {
            //단품, 딜 페이지는 특정 탭 없는 activity로 가도록 함.
            if (WebUtils.isNoTabPage(targetUrl)) {
                if (useNativeProduct) {
                    if (isNativeProduct(targetUrl)) {
                        //native 단품
                        action = Keys.ACTION.PRODUCT_DETAIL_NATIVE_WEB;
                    } else {
                        //web 단품
                        action = Keys.ACTION.NO_TAB_WEB;
                    }
                } else {
                    action = Keys.ACTION.NO_TAB_WEB;
                }
            }
            //탭이 없으나 왼쪽 에서 나타나는 애니메이션 때문에 No_TAB_WEB과 구분한다 마이쇼핑
            else if(WebUtils.isMyShop(targetUrl)) {
                action = Keys.ACTION.MY_SHOP_WEB;
            }
            else if (WebUtils.isMobileLiveUrl(targetUrl)) {
                action = Keys.ACTION.MOBILELIVE_NO_TAB_WEB;
            }
            else {
                action = Keys.ACTION.WEB;
            }
        }
        // m.gsshop.com/index 라면??
        else if (MainApplication.isAppView) {
            action = Keys.ACTION.APP_HOME;
        } else {
            action = Keys.ACTION.WEB;
        }

        if (Keys.ACTION.WEB.equals(action)&& targetUrl == null) {
            targetUrl = TabMenu.getDefaultWebUrl(targetTabMenu);
        }

        intent = new Intent(action);
        intent.putExtra(Keys.INTENT.TAB_MENU, targetTabMenu);
        intent.putExtra(Keys.INTENT.FROM_TAB_MENU, fromTabMain);
        intent.putExtra(Keys.INTENT.BACK_TO_MAIN, backToMain);
        intent.putExtra(Keys.INTENT.WIDGET_TYPE, widgetMenu);

        if (targetUrl != null) {
            intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return intent;
    }
}
