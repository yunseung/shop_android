/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user

import com.gsshop.mocha.network.rest.RestPost
import gsshop.mobile.v2.ServerUrls
import gsshop.mobile.v2.user.UserConnector.LoginTokenParamNew
import gsshop.mobile.v2.user.UserConnector.LoginTokenResult
import roboguice.inject.ContextSingleton

/**
 * UserConnector class 의 @Rest(resId = R.string.server_https_root) 고정값 때문에
 * 서버 정보 변경 후에도 로그인 동작등이 바라보는 서버가 고정되어 있는 문제를 해결하기 위해 만든 테스트용 클래스.
 *
 * LoginTokenResult, LoginTokenParamNew 등은 UserConnector 의 것을 가져다 쓰면 된다.
 */
@ContextSingleton
class UserConnectorForTest {
    /**
     * 테스트 환경에서 서버정보 변경 후 로그인 시 바뀐 서버 정보로 로그인 하도록 만든 테스트 코드
     * 2020.01.20 yun.
     * @param param param
     * @return LoginTokenResult
     */
    @RestPost(ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_M)
    fun loginForTokenNewForM(param: LoginTokenParamNew?): LoginTokenResult? {
        return null
    }

    /**
     * 테스트 환경에서 서버정보 변경 후 로그인 시 바뀐 서버 정보로 로그인 하도록 만든 테스트 코드
     * 2020.01.20 yun.
     * @param param param
     * @return LoginTokenResult
     */
    @RestPost(ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_SM21)
    fun loginForTokenNewForSM21(param: LoginTokenParamNew?): LoginTokenResult? {
        return null
    }

    /**
     * 테스트 환경에서 서버정보 변경 후 로그인 시 바뀐 서버 정보로 로그인 하도록 만든 테스트 코드
     * 2020.01.20 yun.
     * @param param param
     * @return LoginTokenResult
     */
    @RestPost(ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_TM14)
    fun loginForTokenNewForTM14(param: LoginTokenParamNew?): LoginTokenResult? {
        return null
    }

    @RestPost(ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_ATM)
    fun loginForTokenNewForATM(param: LoginTokenParamNew?): LoginTokenResult? {
        return null
    }

    /**
     * 테스트 환경에서 서버정보 변경 후 로그인 시 바뀐 서버 정보로 로그인 하도록 만든 테스트 코드
     * 2020.01.20 yun.
     * @param param param
     * @return LoginTokenResult
     */
    @RestPost(ServerUrls.REST.LOGIN_FOR_TOKEN_FOR_AM)
    fun loginForTokenNewForAM(param: LoginTokenParamNew?): LoginTokenResult? {
        return null
    }
}