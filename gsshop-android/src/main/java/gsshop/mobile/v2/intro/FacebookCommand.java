/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.facebook.FacebookSdk;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.ThreadUtils;

/**
 * Facebook SDK 광고 적용
 */
public class FacebookCommand extends BaseCommand {
    /**
     * FacebookCommand execute
     *
     * @param activity activity
     * @param chain    chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        long startTime = System.currentTimeMillis();

        injectMembers(activity);

        chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            // Initialize the SDK before executing any other operations,
            // especially, if you're using Facebook UI elements.
            // https://developers.facebook.com/docs/android/upgrading-4x
            // Android 4.19에서 Auto initialized 되어서 deprecated 됨.
            FacebookSdk.sdkInitialize(activity.getApplicationContext());
            // AppEventsLogger.activateApp(activity);

            //for debug
            /*FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);*/

            LogUtils.printExeTime("FacebookCommand", startTime);
        });
    }
}
