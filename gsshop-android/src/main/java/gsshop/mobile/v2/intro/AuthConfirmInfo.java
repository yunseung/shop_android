package gsshop.mobile.v2.intro;

import com.gsshop.mocha.pattern.mvc.Model;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * 접근권한 팝업고지 정보 모델
 */
@Model
public class AuthConfirmInfo {

    /**
     * 노출여부
     */
    public boolean isShow = false;

    /**
     * 노출결과 서버전송여부
     */
    public boolean isSendToServer = true;

    /**
     * 재노출이 필요한지 확인한다.
     *
     * @return 재노출 필요시 true 리턴
     */
    public static boolean isNeedReShow() {
        if (PrefRepositoryNamed.get(MainApplication.getAppContext(), Keys.CACHE.VERSION_CODE, Integer.class) == null) {
            return false;
        }
        return MainApplication.AUTH_NOTICE_VER_CODE > PrefRepositoryNamed.get(MainApplication.getAppContext(), Keys.CACHE.VERSION_CODE, Integer.class);
    }

    public static AuthConfirmInfo get() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), AuthConfirmInfo.class.getName(), AuthConfirmInfo.class);
    }

    public void save() {
        PrefRepositoryNamed.save(MainApplication.getAppContext(), AuthConfirmInfo.class.getName(), this);
    }

    public static void remove() {
        PrefRepositoryNamed.remove(MainApplication.getAppContext(), AuthConfirmInfo.class.getName());
    }
}
