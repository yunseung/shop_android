/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.gsshop.luna.Luna;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * 익셉션 로그 수집 모듈
 * 
 * NOTE : Luna 관련 작업이 더이상 필요없는 경우 이 클래스와 libs/luna*.jar 파일을 제거할 것.
 *  
 */
public class LunaCommand extends BaseCommand {
    /**
     * LunaCommand execute
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        long startTime = System.currentTimeMillis();

        injectMembers(activity);

        chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                // 라이브러리 안에서 uuid 생성 ( 고객번호 수집 이슈로 변경 )
                String customerNumber = "";
                Luna.initialize(customerNumber, activity.getApplicationContext());
            } catch (Exception e) {
                Ln.e(e);
            }
            LogUtils.printExeTime("LunaCommand", startTime);
        });
    }

}
