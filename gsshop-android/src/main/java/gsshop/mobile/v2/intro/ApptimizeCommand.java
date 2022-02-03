/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.apptimize.Apptimize;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * 앱티마이저 초기화 및 실험요소 정리
 */
public class ApptimizeCommand extends BaseCommand {

    //앱티마이저 어플리케이션 키
    public static final String APPLICATION_KEY = "AaaGScbTDtgWbWpYzbgEdfzhafTu8d8";

    //key값 + value값 모두 합친 변수
    public static String ABINFO_VALUE = "";

    /**
     * 앱티마이즈 AB테스트를 위한 Command 추가
     * @param activity
     * @param chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        injectMembers(activity);


        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                IntroActivity.parallelHandler.setState(IntroActivity.parallelHandler.POOL_INPUT);
                Apptimize.setup(activity, ApptimizeCommand.APPLICATION_KEY);
            } catch (Exception e) {
                Ln.e(e);
            }finally {
                chain.next(activity);
            }
        });
    }
}
