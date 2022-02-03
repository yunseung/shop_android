/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 추천연관검색어를 받아올 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class RecentRecommandInfo implements Serializable {

    public String tq;
    public String rtq;
    public int cocnt;
    public int sim;
    public int pageNo;
    public int topCount;

}
