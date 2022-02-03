/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.amplitude.api.Amplitude;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.util.ThreadUtils;

/**
 *  amp..(앰플리튜드) SDK 적용
 *
 */
public class AmpCommand extends BaseCommand {

    /**
     * AmpCommand execute
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
    	long startTime = System.currentTimeMillis();

        injectMembers(activity);

    	chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            //커맨드 순서상 요기가 틀릴수도 잇다
            // Note: if your app has multiple entry points/exit points, you should make a Amplitude.getInstance().initialize() at every onCreate() entry point.
            //
            // 프로가드 설정 참고 - 설정 해놨음
            // -keep class com.google.android.gms.ads.** { *; }
            // -dontwarn okio.**
            //Custom Device IDs 구글 광고 아이디 사용 활성화
            Amplitude.getInstance().useAdvertisingIdForDeviceId();

            //초기화 엑티비티를 사용하려면 프리젠테이션 레이어에서 사용해야 합니다
            Amplitude.getInstance().initialize(activity, "56d54c6d801092b4e8718b40c87a5441").enableForegroundTracking(activity.getApplication());

            //세션 트래킹 여부 웹은 30분 앱은 5분
            AMPAction.ampTrackSessionEvents(true);
        });
    }
}
