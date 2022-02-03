/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 메인 list의 상단 배너 
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class FilterInfo {
    public String[] categoryNo;
    public String categoryName;
    public String totalCnt;
    public String mseqUrl;
}
