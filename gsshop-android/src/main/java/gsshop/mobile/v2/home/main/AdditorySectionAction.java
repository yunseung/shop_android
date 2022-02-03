/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * [이민수]
 * 각 섹션에 발생될 Action을 정의하는 모델 
 * 각 섹션에 AdditorySectionAction 값이 내려오면 그 탭에는 특화된 기능이 추가하게 됨
 * 
 * 
 * actionType 정의
 * 
 * "RELATION_RECOMMENDATION"  (연관 추천 가져올 정보)
 *  Relation Recommendation
 * 
 * 
 * 
 * 
 * 베스트딜 탭인지는 관심없다. 항목이 있으면 베스트딜인것이다. 
 * 추가적인 액션이 나오면 API 1.3/1.4 버전 변경없어도 된다.
 * 나한테 정의 되지 않은 ActionType 이 내려오면 안처리하면 그만, 
 *  
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class AdditorySectionAction {
	
	/**
	 * 내가 무슨 액션을 하는 Type 인가 나한테 없는 Type 이 내려오면 안처리하면 된다. 
	 * 단점은 actionType 이 추가 될때마다 모델이 나와야 한다 어쩔수있냐
	 * actionLinkUrl + actionLinkParams + 내가본 상품번호가 될듯
	 */
	public String actionType = "";
	/**
	 * actionLinkUrl
	 */
    public String actionLinkUrl ="";
	/**
	 * actionLinkParams
	 */
	public String actionLinkParams ="";
	
}
