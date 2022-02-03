/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.clevertap;

import android.content.Context;

//import com.clevertap.android.sdk.CleverTapAPI;

import org.apache.http.NameValuePair;

import java.util.HashMap;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.inject.ContextSingleton;

/**
 * CleverTap 프로파일 업데이트 및 이벤트 전달 클래스
 */
@ContextSingleton
public class CleverTapAction {

    /**
     * 사용자별 프로파일을 업데이트한다.
     *
     * @param context
     */
    public void updateProfile(Context context) {
        //고객번호
        String custNo = User.getCachedUser() != null ? User.getCachedUser().customerNumber : "";

        //성별
        String gender = "";
        NameValuePair gdPair = CookieUtils.getWebviewCookie(context, "gd");   //성별
        if (gdPair != null) {
            gender = gdPair.getValue();
        }

        //연령대
        String age = "";
        NameValuePair ydPair = CookieUtils.getWebviewCookie(context, "yd");   //연령대
        if (ydPair != null) {
            age = ydPair.getValue() + "'s";
        }

        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Identity", custNo);
        profileUpdate.put("Gender", gender);
        profileUpdate.put("Age", age);

        //동일기기 다중사용자 대응
//        CleverTapAPI.getDefaultInstance(MainApplication.getAppContext()).onUserLogin(profileUpdate);
    }
}
