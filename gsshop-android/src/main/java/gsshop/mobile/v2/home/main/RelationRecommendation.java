/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;

import java.util.ArrayList;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * [이민수]
 * 연관 추천 상품
 * 
 * 
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class RelationRecommendation {
	
	/**
	 * productListForBestZone 변수명이 곧 타입 
	 */
	public String recommendationType = "";
	public ArrayList<SectionContentList> productListForBestRecommendation = null;
	//public ArrayList<SectionContentList> productListForTVRecommendation = null;
}
