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

import gsshop.mobile.v2.menu.navigation.NavigationModel;

/**
 * 그룹매장 정보.
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class HomeGroupInfo {

    /**
     * 그룹매장 Section.
     */
    public ArrayList<GroupSectionList> groupSectionList = null;

    /**
     * 그룹매장 Section. 외에 하드코드 안할려고 쓰는 Url들
     */
    public AppUseUrl appUseUrl = null;

    public NavigationModel leftNavigation = null;

    /**
     * Cache 시점 저장(시스템 밀리세컨)
     */
    public long saveTime = 0;
}
