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

import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.ThreadUtils;

/**
 * Background 커맨드로 따로 AdvertisingId 를 얻어오기 위함.
 */
public class GetAdvIDCommand extends BaseCommand {
    /**
     * BadgeCommand execute
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        injectMembers(activity);

        // 비동기로 돌아야 한다. 다음 command 실행.
        chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> DeviceUtils.setAdvertisingId(activity));
    }
}
