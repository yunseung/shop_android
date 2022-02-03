/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.home.GroupSectionList;
import gsshop.mobile.v2.home.HomeGroupInfo;
import gsshop.mobile.v2.home.HomeGroupInfoAction;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * 디폴트 매장 API 호출 후 결과 캐싱
 * -홈화면 진입 후 로딩바가 오래 노출(API 응답시간동안)되어 커맨드에서 미리 API를 캐싱함
 * -key값: http://[server]/app/main/bestdealversion=9.9&pageIdx=1&naviId=54&varnishYn=N&adid=&reorder=true&reqtype=FIRST
 *
 */
public class DefaultShopCommand extends BaseCommand {
    /**
     * HomeMenuCommand homeGroupInfoAction
     */
    @Inject
    private HomeGroupInfoAction homeGroupInfoAction;

    /**
     * rest client
     */
    @Inject
    private RestClient restClient;

    /**
     * 액티비티
     */
    private Activity activity;

    /**
     * 커맨드체인
     */
    private CommandChain chain;

    /**
     * 타이머 타스트
     */
    private TimerTask timerTask;

    /**
     * API 중복호출 방지용 플래그
     */
    private boolean isExecuted = false;

    /**
     * 최대 대기시간(ms)
     */
    private long DELAY_SECOND = 800;

    @Override
    public void execute(Activity activity, CommandChain chain) {
        long startTime = System.currentTimeMillis();

        injectMembers(activity);

        this.activity = activity;
        this.chain = chain;

        //최대 대기시간 후 다음 커맨드 수행
        goNextAfterMaxDelay();

        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                //HomeMenuCommand에서 캐싱한 정보 추출
                HomeGroupInfo homeGroupInfo = homeGroupInfoAction.getHomeGroupInfo();
                GroupSectionList groupSectionList = homeGroupInfo.groupSectionList.get(0);
                ArrayList<TopSectionList> sectionList = groupSectionList.sectionList;
                //디폴트매장 번호
                String defaultNavigationId = groupSectionList.defaultNavigationId;

                for (int i = 0; i < sectionList.size(); i++) {
                    if (defaultNavigationId.equals(sectionList.get(i).navigationId)) {
                        //디폴트매장 API 응답시간 : 1200ms ~ 2000ms 소요
                        TopSectionList topSectionList = sectionList.get(i);
                        DataUtil.getData(activity, restClient, ContentsListInfo.class,
                                true, true, topSectionList.sectionLinkUrl,
                                topSectionList.sectionLinkParams + "&reorder=true&reqtype="
                                        + MainApplication.homeLoaded, topSectionList.sectionName);
                        //최대 대기시간보다 API 응답이 빠른 경우 바로 다음 커맨드 수행
                        executeTask(0);
                        break;
                    }
                }
            } catch (Exception e) {
                //ignore
                Ln.e(e);
            }

            LogUtils.printExeTime("DefaultShopCommand", startTime);
        });
    }

    /**
     * 최대 대기시간 후 다음커맨드를 수행한다.
     */
    private void goNextAfterMaxDelay() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            executeTask(DELAY_SECOND);
        }, DELAY_SECOND);
    }

    /**
     * 딜레이 후 다음커맨드를 수행한다.
     *
     * @param delay 딜레이 타임(ms)
     */
    private synchronized void executeTask(long delay) {
        if (isExecuted) {
            return;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    isExecuted = true;
                    chain.next(activity);
                } catch (IndexOutOfBoundsException e) {
                    //ignore
                }
            }
        };

        new Timer().schedule(timerTask, delay);
    }
}