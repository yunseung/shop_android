/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user;

import com.gsshop.mocha.pattern.mvc.Model;

import gsshop.mobile.v2.Keys.PREF;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * 로그인 옵션.
 *
 */
@Model
public class LoginOption {

    /**
     * 아이디 기억. 기본값 true.
     */
    public boolean rememberLoginId = true;

    /**
     * 로그인 유지. 기본값 true.
     */
    public boolean keepLogin = true;

    /**
     * 간편로그인 허용여부. 기본값 false.
     */
    public boolean easyLogin = true;

    /**
     * 폰에 저장된 로그인 옵션 정보를 복원.
     * 저장된 정보가 없으면 null 리턴.
     * @return LoginOption
     */
    public static LoginOption get() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), PREF.LOGIN_OPTION, LoginOption.class);
    }

    public void save() {
        PrefRepositoryNamed.save(MainApplication.getAppContext(), PREF.LOGIN_OPTION, this);
    }

    public static void remove() {
        PrefRepositoryNamed.remove(MainApplication.getAppContext(), PREF.LOGIN_OPTION);
    }
}
