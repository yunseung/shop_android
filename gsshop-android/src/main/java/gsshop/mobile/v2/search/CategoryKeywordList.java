/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gsshop.mocha.pattern.mvc.Model;

@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class CategoryKeywordList {
	
	/**
	 * 카테고리 이름
	 */
	 public String title = "";
	 /**
	  * 카테고리 url
	  */
	 public String url  = "";
	 /**
	  * 카테고리 숫자
	  */
	 public String count  = "";   
}
