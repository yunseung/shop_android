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

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.support.version.ApptimizeAction;
import gsshop.mobile.v2.support.version.ApptimizeExpModel;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * CSP가 어떠한상태인지 체크 하는  API
 * 
 * NOTE : CSP_BREAK_VERSION_CODE 프리페어 런스에 저장
 *  
 */
public class ApptimizeRunExp extends BaseCommand {
    /**
     * CSPCommand 버전 액션
     */
    @Inject
    private ApptimizeAction versionAction;

    /**
     * state 타입 "OK"
     */
    private final String CSP_OK = "OK";

    /**
     * LunaCommand execute
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        injectMembers(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                ApptimizeExpModel apptiInfo = null;
                try {
                    apptiInfo = versionAction.getApptiInfo();
                }catch (Exception e){

                }
                if (apptiInfo != null) {
                    //Log.d("GSLOG",versionInfo.status + "" +versionInfo.AOS);
                    if (!CSP_OK.equalsIgnoreCase(apptiInfo.status)) {
                        ApptimizeExpManager.prodArray = null;
                        ApptimizeExpManager.stageArray = null;
                        chain.next(activity);
                        return;
                    }

                    if (apptiInfo.prod != null) {
                        ApptimizeExpManager.prodArray = apptiInfo.prod;
                    }

                    if (apptiInfo.stage != null) {
                        ApptimizeExpManager.stageArray = apptiInfo.stage;
                    }
                }

            } catch (Exception e) {
                Ln.e(e);
            }
            finally {
                chain.next(activity);
            }
        });
    }
}
