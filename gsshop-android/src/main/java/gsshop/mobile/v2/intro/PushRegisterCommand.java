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

import gsshop.mobile.v2.push.PushSenderConnector;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * push/register API에 디바이스 정보를 전송하기 위한 작업.
 * 위 API를 호출하는 경우는 본 커맨드와 사용자가 수동으로 로그인 한 경우 2곳이다.
 *
 */
public class PushRegisterCommand extends BaseCommand {
    /**
     * 로그 태그
     */
	private static final String TAG = "PushRegisterCommand";

    /**
     * 푸시 레지스터 커넥터(디바이스 정보) senderConnector
     */
    @Inject
    protected PushSenderConnector senderConnector;

    /**
     * PushRegisterCommand execute
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
            try {

                senderConnector.registerOnServer();
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }
            LogUtils.printExeTime(TAG, startTime);
        });
    }
}
