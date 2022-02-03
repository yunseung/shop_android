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

import gsshop.mobile.v2.support.criteo.CriteoAction;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.ThreadUtils;

/**
 * Criteo 리마케팅 적용
 *
 */
public class CriteoCommand extends BaseCommand {
    /**
     * CriteoCommand criteoAction
     */
    @Inject
    protected CriteoAction criteoAction;

    /**
     * CriteoCommand execute
     *
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
    	long startTime = System.currentTimeMillis();

        injectMembers(activity);

    	chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            criteoAction.sendEvent();
            LogUtils.printExeTime("CriteoCommand", startTime);
        });
    }
}
