/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.menu.navigation;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class StoreContentData {
    public String jjimCnt;
    public String jjimLinkUrl;
    public String msgCardImageUrl;
    public String msgCardClickLinkUrl;
    public String msgCardClickImageUrl;
    public String recentLinkUrl;
    public String isLogin;
    public String wiseLogUrl;
}
