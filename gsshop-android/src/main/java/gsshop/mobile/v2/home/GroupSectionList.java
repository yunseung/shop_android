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

/**
 * 그룹매장의 각 섹션 구조체.
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class GroupSectionList {

    /**
     * 그룹매장 구분 아이디
     */
    public String groupSectionId = "";

    /**
     * 그룹매장 표시 이름
     */
    public String groupSectionName = "";

    /**
     * 메인 홈버튼 클릭 시 이동할 매장 코드
     */
    public String defaultNavigationId = "";

    /**
     * 앱 구동 후 최초 랜딩 시 이동할 매장 코드 (개인화 매장 코드 )
     */
    public String homeNavigationId = "";

    /**
     * 대분류매장 정보
     */
    public ArrayList<TopSectionList> sectionList = null;

    /**
     * wiseLogUrl
     */
    public String wiseLogUrl = "";
}
