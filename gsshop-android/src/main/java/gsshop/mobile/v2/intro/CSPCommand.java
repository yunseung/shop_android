/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.content.pm.PackageInfo;

import com.google.inject.Inject;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.support.version.CSPVersionAction;
import gsshop.mobile.v2.support.version.CSPVersionModel;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * CSP가 어떠한상태인지 체크 하는  API
 * 
 * NOTE : CSP_BREAK_VERSION_CODE 프리페어 런스에 저장
 *  
 */
public class CSPCommand extends BaseCommand {
    /**
     * CSPCommand 버전 액션
     */
    @Inject
    private CSPVersionAction versionAction;

    /**
     * 패키지 정보
     */
    @Inject
    private PackageInfo packageInfo;

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

        //일단 흘려 보내고
        chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                CSPVersionModel versionInfo = null;

                try {
                    //버전정보 API를 호출하여 취득
                    versionInfo = versionAction.getCSPVersionInfo();

                } catch (Exception e) {
                    PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.CSP_BREAK_FLAG, false);
                }

                // 추후에도 버전정보가 사용되므로 캐시처리 그런데 다 저장할 필요 없다.
                //versionAction.cacheCSPVersionInfo(versionInfo);
                //커맨드에서 버전체크에 실패(네트워크에러 등)한 경우 version값이 null임
                if (versionInfo != null)
                {
                    //Log.d("GSLOG",versionInfo.status + "" +versionInfo.AOS);
                    if( CSP_OK.equals(versionInfo.status) )
                    {
                        if (versionInfo.isVisible(AppInfo.getAppVersionCode(packageInfo))) {
                            PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.CSP_BREAK_FLAG,true);
                            return;
                        } else {
                            PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.CSP_BREAK_FLAG,false);
                        }
                    }else{
                        PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.CSP_BREAK_FLAG,false);
                    }
                } else {
                    //version 값이 서비스 비활성화
                    PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.CSP_BREAK_FLAG, false);
                }

            } catch (Exception e) {
                Ln.e(e);
                //익셉션 발생시 서비스 비활성화
                PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.CSP_BREAK_FLAG, false);
            }
            PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.CSP_BREAK_FLAG,false);
        });
    }

}
