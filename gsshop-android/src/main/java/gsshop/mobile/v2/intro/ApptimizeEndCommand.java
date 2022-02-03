/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import java.util.TimerTask;

import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.ThreadUtils;


/**
 * 앱티마이저 초기화 및 실험요소 정리
 */
public class ApptimizeEndCommand extends BaseCommand {

    /**
     * 지정시간 뒤에 nextChain이 이루어지도록
     */
    private TimerTask timerTask = null;
    /**
     * 딜레이 시간
     */
    private static final int NEXT_CHAIN_DELAY = 1500;

    /**
     * 앱티마이즈 AB테스트를 위한 Command 추가
     * @param activity
     * @param chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        long startTime = System.currentTimeMillis();

        injectMembers(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            IntroActivity.parallelHandler.setState(IntroActivity.parallelHandler.POOL_FULL);

            //남은 작업 갯수가 0보다 크면 1초 기다렸다가 chain.next
            if (IntroActivity.parallelHandler.getPoolSize() > 0) {
                IntroActivity.parallelHandler.executeTask(NEXT_CHAIN_DELAY);
            } else {
                if(IntroActivity.parallelHandler.executeFlag){
                    IntroActivity.parallelHandler.executeFlag = false;
                    chain.next(activity);
                }
            }

            LogUtils.printExeTime("ApptimizeEndCommand", startTime);
        });
    }
}
