/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.util.ThreadUtils;

/**
 * 앱티마이저 초기화 및 실험요소 정리
 */
public class ApptimizeIMGCommand extends ApptimizeBaseCommand {
    /**
     * 앱티마이즈 AB테스트를 위한 Command 추가
     * @param activity
     * @param chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        injectMembers(activity);

        chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> apptimizeBase(activity, chain, ApptimizeExpManager.IMG));
    }
}