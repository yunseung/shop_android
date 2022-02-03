/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.version;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 앱 버전 정보.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class AppVersionModel {

    /**
     * 현재 설치버전
     */
    public String currentVersionName;

    /**
     * 최신 버전
     */
    public String vername;

    /**
     * 선택 업데이트 내용
     */
    public String choicemsg;

    /**
     * 강제 업데이트 내용
     */
    public String forcemsg;

    /**
     * 강제 업데이트 팝업 버전 코드
     */
    public String force;

    /**
     * 선택 업데이트 팝업 버전 코드
     */
    public String choice;

    /**
     * 최신 버전 코드
     */
    public String vercode;

    /**
     * 현재 설치된 앱이 최신버전인가?
     * @param versionCode versionCode
     * @return boolean
     */
    @JsonIgnore
    public boolean isUpdate(int versionCode) {
        return Integer.parseInt(vercode) <= versionCode;
    }
}