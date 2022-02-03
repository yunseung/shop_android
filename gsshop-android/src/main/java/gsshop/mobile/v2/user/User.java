/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.Keys.CACHE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * 로그인한 사용자 정보.
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class User {

    /**
     * 사용자명
     */
    private String userName;

    /**
     * 로그인 아이디
     */
    public String loginId;

    /**
     * 사용자 등급
     */
    private String userGrade;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the user grade
     */
    public String getUserGrade() {
        return userGrade;
    }

    /**
     * @param userGrade the user grade to set
     */
    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    /**
     * 고객번호
     */
    public String customerNumber;

    /**
     * 자동로그인 인지 판별
     */
    public String isAutoLogin = "N";

    public UserConnector.LoginResult loginResult;

    public UserConnector.LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(UserConnector.LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    @Override
    public String toString() {
        return "User [userName=" + userName + ", loginId=" + loginId + ", customerNumber="
                + customerNumber + "]";
    }

    /**
     * 로그인한 사용자 정보를 캐시한다.
     */
    public void cacheUser() {
        PrefRepositoryNamed.save(MainApplication.getAppContext(), CACHE.USER_INFO, this);
    }

    /**
     * 캐시된 로그인 사용자 정보를 조회한다.
     *
     * @return 없으면 null 리턴.
     */
    public static User getCachedUser() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), CACHE.USER_INFO, User.class);
    }

    /**
     * 캐시된 로그인 사용자 정보를 제거한다.
     * (앱 종료시 캐시를 지우거나 앱 시작시 지운후 새로 조회할 것)
     */
    public static void clearUserCache() {
        PrefRepositoryNamed.remove(MainApplication.getAppContext(), CACHE.USER_INFO);
    }

}
