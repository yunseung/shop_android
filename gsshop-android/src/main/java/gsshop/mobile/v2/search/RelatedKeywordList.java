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

import java.util.ArrayList;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 연관 검색어 결과데이터.
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class RelatedKeywordList {

    /**
     * 입력검색어
     */
    public String query;

    public int frontSize;

    public int backSize;
    
    public int categorySize;
    

    /**
     * 앞부분 일치 검색어목록
     */
    public ArrayList<String> front;

    /**
     * 뒷부분 일치 검색어목록
     */
    public ArrayList<String> back;
    
    public ArrayList<CategoryKeywordList> category;
}