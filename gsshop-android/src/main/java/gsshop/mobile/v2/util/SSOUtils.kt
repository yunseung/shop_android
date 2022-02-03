/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import com.blankj.utilcode.util.EmptyUtils
import com.google.gson.Gson
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.sso.SSOControl
import gsshop.mobile.v2.user.UserConnector.SSOSdkParam

/**
 * SSO 유틸
 *
 */
object SSOUtils {

    /**
     * SSO SDK 함수 호출시 필요한 파라미터를 JSON 포맷으로 생성한다.
     * (getToken 용 파라미터)
     *
     * @return json 스트링
     */
    @JvmStatic
    fun getSSOParamType1(): String? {
        val param = SSOSdkParam()
        param.chnnlCode = MainApplication.channelCode
        return Gson().toJson(param)
    }

    /**
     * SSO SDK 함수 호출시 필요한 파라미터를 JSON 포맷으로 생성한다.
     * (saveToken 용 파라미터)
     *
     * @param ssoAuthToken
     * @return json 스트링
     */
    @JvmStatic
    fun getSSOParamType2(ssoAuthToken: String): String? {
        val param = SSOSdkParam()
        param.chnnlCode = MainApplication.channelCode
        param.ssoAuthToken = ssoAuthToken
        return Gson().toJson(param)
    }

    /**
     * ssoToken 존재여부를 확인한다.
     *
     * @return 존재하면 true 린턴
     */
    @JvmStatic
    fun hasSSOToken(): Boolean {
        return EmptyUtils.isNotEmpty(SSOControl.getInstance().localToken)
    }
}