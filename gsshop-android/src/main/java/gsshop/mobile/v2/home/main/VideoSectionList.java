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

import java.util.ArrayList;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 메인 list의 상단 배너 
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class VideoSectionList {
    public String sectionType; //STFCLIST 숏방 / NTFCLIST 날방
    public SectionContentList product;
    public TvLiveDealBanner tvLiveDealBanner;
    public ArrayList<SectionContentList> productList;
}
