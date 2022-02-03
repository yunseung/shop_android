/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.gsshop.mocha.core.async.Background;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.util.LogUtils;

/**
 * chain.next를 수행하지 않는 더미 커맨드
 */
public class DummyCommand extends BaseCommand {

    /**
     * DummyCommand execute
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
    	long startTime = System.currentTimeMillis();
        LogUtils.printExeTime("DummyCommand", startTime);
    }
}
