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
 * CSP 관련 버전 정보.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class CSPVersionModel {

    /**
     * 상태
     * OK
     * 또는??
     */
    public String status;
    /**
     * 안드
     * 이것보다 같거나 높을때
     */
    public String AOS;

    /**
     * 아이오
     * 이것보다 같거나 높을때
     * 그런데 안볼꺼니까
     */
    public String IOS;

    /**
     * Apptimize 사용한 AB테스트
     * 앱버전 256이상이면 ApptimizeCommand 실행
     */
    public String AB;


    /**
     * 현재가 CSP 보여져야 될 버전인가
     * @param versionCode versionCode
     * @return boolean
     */
    @JsonIgnore
    public boolean isVisible(int versionCode) {
        if(status != null && AOS != null){
            try{
                return Integer.parseInt(AOS) <= versionCode;
            }catch (Exception e)
            {
                return false;
            }

        }
        return false;
    }
}