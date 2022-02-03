/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.home.shop.flexible.SubMenuList;
import gsshop.mobile.v2.support.pmopopup.PmoPopupInfo;

/**
 * 대분류매장 정보.
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class TopSectionList {

    /* 대분류 매장 구분아이디 */
    public String sectionId = "";

    /* viewPager가 표시할 page를 설정할 때 사용하는 id */
    public String navigationId = "";

    /* 대분류 매장 표시이름 */
    public String sectionName = "";

    /* 대분류 매장 상단 텍스트 */
    public String sectionTopName = "";

    /*앱티마이즈 전용 섹션네임*/
    public String apptiSectionName = "";

    /* 대분류 매장 눌렀을때, 데이터 가져올 url */
    public String sectionLinkUrl = "";

    /* 매장이름 앞에 표시할 아이콘 url (구)*/
    public String sectionImgUrl = "";

    /* 매장이름 앞에 표시할 아이콘 url (신규)*/
    public String sectionNewImgUrl = "";

    /* link parameter */
    public String sectionLinkParams = "";

    public String sectionCode = "";

    /*
     * 대분류 매장 타입(D: 딜, 기본선택으로 처리, L: list)
     * 그룹매장 눌렀을때, 대분류 매장의 sectionType 이 "D" 인 항목을 기본선택으로 처리하면 됨.
     */
    public String sectionType = "";

    /* wise log url */
    public String wiseLogUrl = "";

    /* 카테고리 화면 URL */
    public String categorySearchUrl = "";
    
    /* View 타입 */
    public String viewType = "";

    /* 각 탭의 서뷰 메뉴 발생시 사용하게될 UI */
    public ArrayList<SubMenuList> subMenuList = null;

    /**
     * 매장팝업 대상여부 조회 API
     */
    public String pmoPopupCheckUrl = null;

    /**
     * 매장팝업 정보
     */
    public PmoPopupInfo pmoPopupInfo = null;

    /**
     * 고정매장 여부 (고정매장:true, 개인화매장:false)
     */
    public boolean isPublicSection = true;

}
