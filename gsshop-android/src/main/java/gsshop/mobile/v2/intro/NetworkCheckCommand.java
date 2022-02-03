/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.util.LogUtils;

import static gsshop.mobile.v2.util.NetworkUtils.alertNetworkDisconnected;

/**
 * 단말기 네트워크 연결상태 체크.
 * 연결된 네트워크 없으면 앱 종료.
 *
 */
public class NetworkCheckCommand extends BaseCommand {

    /**
     * NetworkCheckCommand execute
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(final Activity activity, CommandChain chain) {
    	long startTime = System.currentTimeMillis();
    	
        if (NetworkStatus.isNetworkConnected(activity.getApplicationContext())) {
            chain.next(activity);
            LogUtils.printExeTime("NetworkCheckCommand", startTime);
            return;
        }

        alertNetworkDisconnected(activity);
    }
}
