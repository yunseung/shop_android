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
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.home.HomeGroupInfo;
import gsshop.mobile.v2.home.HomeGroupInfoAction;
import gsshop.mobile.v2.user.LoginOption;
import gsshop.mobile.v2.user.TokenCredentialsNew2;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.NetworkUtils.alertNetworkDisconnected;

/**
 * 앱 시작시 Home Menu 정보 조회
 *
 */
public class HomeMenuCommand extends BaseCommand {
    /**
     * HomeMenuCommand homeGroupInfoAction
     */
    @Inject
    private HomeGroupInfoAction homeGroupInfoAction;

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
     * 최대 대기시간(ms) - 개인화매장때문에 딜레이 필요함
     */
    private long DELAY_SECOND = 800;

    /**
     * API 중복호출 방지용 플래그
     */
    private boolean isExecuted = false;

    /**
     * HomeMenuCommand execute
     *
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        injectMembers(activity);

        EventBus.getDefault().register(this);

        this.activity = activity;
        this.chain = chain;
        this.isExecuted = false;

        ThreadUtils.INSTANCE.runInBackground(() -> {
            LoginOption option = LoginOption.get();
            TokenCredentialsNew2 tokenCredentials = null;
            try {
                tokenCredentials = TokenCredentialsNew2.get();
            } catch (Exception e) {
                Ln.e(e);
            }
            if ((option == null || !option.keepLogin)
                    || (tokenCredentials == null || tokenCredentials.authToken == null)) {
                //일반로드인 대상은 딜레이 없음
                DELAY_SECOND = 0;
            }

            //apptimizeFlag -> true : 아직 네비 호출안해서 기다려달라는 뜻
            //apptimizeFlag -> false : 이미 네비 호출했으니 기다리지말라는 뜻
            if(MainApplication.apptimizeWaitFlag){
                if(DELAY_SECOND == 0){
                    DELAY_SECOND = 300;
                }
                MainApplication.apptimizeWaitFlag = false;
            }

            executeTask(DELAY_SECOND);
        });
    }

    /**
     * 타스크를 생성한다.
     *
     * @param delay
     */
    private void executeTask(long delay) {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                getHomeGroupInfo();
            }
        };

        new Timer().schedule(timerTask, delay);
    }

    /**
     * navigation api를 호출 후 저장한다.
     */
    private synchronized void getHomeGroupInfo() {
        //자동로그인 성공시점과 또는 최대대기시간 시점의 간격이 작아 getHomeGroupInfo함수가 중복 호출되는 경우 방지용
        if (isExecuted) {
            return;
        }

        isExecuted = true;

        long startTime = System.currentTimeMillis();

        //홈네비게이션을 가져오기전에 캐시를 초기화한다.
        MainApplication.clearCache();

        HomeGroupInfo homeGroupInfo = null;
        MainApplication.isAppView = false;
        try {
            homeGroupInfo = homeGroupInfoAction.getHomeGroupInfo(activity, true);
        } catch (org.springframework.web.client.RestClientException restClientException){
            alertNetworkDisconnected(activity);
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
            homeGroupInfo = retryNavigation(activity);
        }

        if (homeGroupInfo != null && homeGroupInfo.groupSectionList != null) {
            MainApplication.isAppView = true;

            //API 오류 발생시 앱진입 안시킴
            LogUtils.printExeTime("HomeMenuCommand", startTime);

            try {
                chain.next(activity);
            } catch (IndexOutOfBoundsException e) {
                Ln.e(e);
                LunaUtils.sendToLuna(activity, e, "CommandChain");
            }
        }

        EventBus.getDefault().unregister(this);
    }

    /**
     * 최초 자동로그인 성공시.
     * @param event AutoLogInSuccessEvent
     */
    public void onEvent(Events.AutoLogInSuccessEvent event) {
        //스티키이벤트 제거
        Events.AutoLogInSuccessEvent stickyEvent =
                EventBus.getDefault().getStickyEvent(Events.AutoLogInSuccessEvent.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }

        executeTask(0);
    }

    /**
     * retryNavigation
     *
     * @param activity
     * @return HomeGroupInfo
     */
    private HomeGroupInfo retryNavigation(Activity activity){
        try {
            return homeGroupInfoAction.getHomeGroupInfo(activity, true);
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
            alertNetworkDisconnected(activity);
            return null;
        }
    }
}