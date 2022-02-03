package gsshop.mobile.v2.util;

import android.content.SharedPreferences;

import java.io.File;
import java.util.Map;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.intro.AuthConfirmInfo;
import gsshop.mobile.v2.push.PushSettings;
import gsshop.mobile.v2.setting.AutoPlaySettings;
import gsshop.mobile.v2.setting.FingerPrintSettings;
import roboguice.util.Ln;

import static android.content.Context.MODE_PRIVATE;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * 마이그레이션 관련 유틸
 */
public class MigrationUtils {

    /**
     * 2개로 분리된 프리퍼런스 파일을 하나로 통합한다.
     */
    public static void migratePref() {
        //마이그레이션 프로세스를 이미 수행했으면 스킵
        boolean prefMigrationDone = PrefRepositoryNamed.getBoolean(MainApplication.getAppContext(), Keys.CACHE.PREF_MIGRATION_DONE, false);
        if (prefMigrationDone) {
            return;
        }

        PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PREF_MIGRATION_DONE, true);

        String versionCode = "";
        //.xml 파일에서 조회 (276~278 사용자는 여기에 정보 존재)
        versionCode = PrefRepositoryNamed.getString(MainApplication.getAppContext(), "", Keys.CACHE.VERSION_CODE);
        if (isEmpty(versionCode)) {
            //275 버전까지 사용자
            versionCode = PrefRepositoryNamed.getString(MainApplication.getAppContext(), Keys.CACHE.VERSION_CODE);
        }
        //276, 277 대상 프리퍼런스값 마이그레이션
        if (!"276".equals(versionCode) && !"277".equals(versionCode)) {
            return;
        }

        migratePrefProc();
    }

    /**
     * 프리퍼런스 파일 통합 수행로직
     */
    private static void migratePrefProc() {
        File prefDir = new File(MainApplication.getAppContext().getApplicationInfo().dataDir,"shared_prefs");
        if (prefDir.exists() && prefDir.isDirectory()) {
            //.xml 파일내 항목을 pkg_preferences.xml 파일로 복사
            SharedPreferences spx = MainApplication.getAppContext().getSharedPreferences("", MODE_PRIVATE);
            if (isNotEmpty(spx)) {
                Map<String, ?> mapx = spx.getAll();
                for (Map.Entry<String, ?> entry : mapx.entrySet()) {
                    String key = "", value = "";
                    try {
                        key = entry.getKey();
                        value = entry.getValue().toString();
                    } catch (Exception e) {
                        Ln.e(e);
                    }

                    //스트링타입 항목 migration
                    if (isNotEmpty(key) && isNotEmpty(value)) {
                        if (key.equalsIgnoreCase(AutoPlaySettings.class.getName())
                                || key.equalsIgnoreCase(FingerPrintSettings.class.getName())
                                || key.equalsIgnoreCase(Keys.CACHE.VERSION_CODE)
                                || key.equalsIgnoreCase(PushSettings.class.getName())
                                || key.equalsIgnoreCase(Keys.CACHE.BADGE)
                                || key.equalsIgnoreCase(AuthConfirmInfo.class.getName())
                                || key.equalsIgnoreCase(Keys.PREF.INTRO_IMAGE_INFO)
                                || key.equalsIgnoreCase(Keys.CACHE.USER_INFO)
                                || key.equalsIgnoreCase(Keys.PREF.LOGIN_OPTION)
                                || key.equalsIgnoreCase(Keys.CACHE.HOME_GROUP_INFO)
                                || key.equalsIgnoreCase(Keys.CACHE.PROMOTION_INFO)
                                || key.equalsIgnoreCase(Keys.CACHE.PROMOTION_DAY)) {
                            PrefRepositoryNamed.saveString(MainApplication.getAppContext(), key, value);
                        }

                        //볼린타입 항목 migration
                        if (key.equalsIgnoreCase(Keys.CACHE.CSP_BREAK_FLAG)
                                || key.equalsIgnoreCase(Keys.CACHE.PUSH_APPROVE)
                                || key.equalsIgnoreCase(Keys.CACHE.PROMOTION)) {
                            PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), key, Boolean.parseBoolean(value));
                        }
                    }
                }
            }
        }
    }
}
