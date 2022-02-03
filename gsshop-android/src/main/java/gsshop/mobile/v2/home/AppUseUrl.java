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

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 기본 앱 네비게이션 정보를 제외한 하드코딩될 URl 구조체
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class AppUseUrl {
    /**
     * wishUrl
     */
    public String wishUrl = null;
    /**
     * tvLiveUrl
     */
    public String tvLiveUrl = null;
    /**
     * tvLiveDealBannerUrl
     */
    public String tvLiveDealBannerUrl = null;
    /**
     * 홈매장 liveBrodUrl
     */
    public String homeLiveBrodUrl;
    /**
     * 홈매장 dataBrodUrl
     */
    public String homeDataBrodUrl;
    /**
     * liveBrodUrl
     */
    public String liveBrodUrl;
    /**
     * dataBrodUrl
     */
    public String dataBrodUrl;
    /**
     * 모바일라이브 방송갱신 주소
     */
    public String homeMobileliveUrl;
    /**
     * snsInfo
     */
    public String snsInfo;
    /**
     * tabPrsnlAb 개인화 매장 버튼 A/B 테스트
     */
    public String tabPrsnlAb;
    /**
     * 이미지 서칭 기능 페이지 URL
     */
    public String imgSearchUrl;
    /**
     * 이미지 서칭 업로드 페이지 URL
     */
    public String imgSearchUploadUrl;
    /**
     * 이미지 서칭 결과 페이지 URL
     */
    public String imgSearchResultUrl;
    /**
     * 딜/단품 페이지 Native 적용 여부
     */
    public String applyNativeWeb = "N";

    /**
     * Hyper Personalized Curation(DT과제) 적용 여부
     */
    public String dtReqAcceptYn = "Y";

    // 로고 애니메이션 이미지 url
    public String biImgUrl;
    // 로고 애니메이션 이미지 노출 기간.
    public String biImgEndDate;
}
