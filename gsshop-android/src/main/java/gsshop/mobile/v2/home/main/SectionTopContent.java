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
 * 각 섹션에 Top 에 위치 할 컨텐츠를 담을수 있는 구조체
 * 베스트딜존 이 아니더라도 처리할수 있게 구조 설계
 *
 * topContentType 정의
 * 
 * 1. productListForBestZone - 변수명이 곧 타입 베스트딜존 
 * 
 * 추가적인 topContentType이 나와도 API 1.3/1.4 버전 변경없어도 된다. 
 * 나한테 정의 되지 않은 topContentType 이 내려오면 안처리하면 그만,
 * 베스트딜 탭인지는 관심없다. 모델이 내려오면 처리하면 되기 떄문 
 * 변수명 자체가 ViewType 역활을 하기 때문에 복잡도는 증가하지만, API 버전 의존도는 낮아질것으로 예상된다.
 * 뭔 소리냐??
 *
 * model for 목적
 * ex) 상품리스트모델 for 베스트존(리스트)
 * ex) 배너 for 오늘마감상품(리스트)
 * 
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class SectionTopContent {

	
	/**
	 * TopContent 타입
	 * productListForBestZone 변수명이 곧 타입  ( 새로 정의해도 되지만.. 앞으로 많아 질것으로 예상되니 변수명이 곧 타입)
	 * 
	 */
	public String topContentType = "productListForBestZone";	
	/**
     * 컨텐츠 리스트
     * 메인에 뿌려진 SectionContentList Model 을 사용
     * 
     */
    public ArrayList<SectionContentList> productListForBestZone = null;
    public ArrayList<SectionContentList> productListForOpen = null;
    //public ArrayList<SectionContentList> productListForBestClose = null;
}
