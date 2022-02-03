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

import java.util.ArrayList;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * CSP 관련 버전 정보.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class ApptimizeExpModel {

    /**
     * 상태
     * OK
     * 또는??
     */
    public String status;

    /**
     * 운영에 나가있는 run중인 AB테스트 key값
     */
    public ArrayList<String> prod;

    /**
     * sm, tm 에 나가있는 run중인 AB테스트 key값
     */
    public ArrayList<String> stage;

}