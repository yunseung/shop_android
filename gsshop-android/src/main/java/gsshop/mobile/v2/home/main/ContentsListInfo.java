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
import java.util.List;

import javax.annotation.ParametersAreNullableByDefault;

@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class ContentsListInfo {

    public SectionBanner banner; // 상단배너

    public String abType;	//A:수평UI(tv쇼핑탭과 동일형태), B:수직UI

    public TvLiveBanner tvLiveBanner; // TV생방송

    public TvLiveBanner dataLiveBanner; // 데이타방송

    public MobileLiveBanner mobileLiveBanner; // 모바일라이브

    public MobileLiveBanner mobileLiveDefaultBanner; // 모바일라이브 (방송안내 표시)

    public TvLiveDealBanner tvLiveDealBanner; // 날방생방송

    public ArrayList<SectionContentList> tvLiveBannerList;

    /* 동영상 매장 (숏방 + 날방) */
    public ArrayList<VideoSectionList> videoSectionList;

    /**
     * 각 섹션에 Top 에 위치 할 컨텐츠를 담을수 있는 구조체
     * 베스트딜존 이 아니더라도 처리할수 있게 구조 설계
     */
    public SectionTopContent sectionTopContent = null;

    /**
     * Section에 부가적인 컨텐츠를 가져 올수 있는 정보
     * 각각의 타입이 존재 해야 할것으로 판단
     * 베스트딜 연관 추천 아니더라도 처리할수 있게 구조 설계
     * *리스트 구성이후에 추가적으로 구성될 정보
     */
    public AdditorySectionAction additorySection = null;

    /**
     * 컨텐츠 리스트
     */
    public ArrayList<SectionContentList> productList = null;

    /**
     * 베스트 리스트
     */
    public ArrayList<SectionContentList> no1DealList = null;

    /**
     * 넘베딜 리스트
     */
    public ArrayList<SectionContentList> no1DealZone = null;

    /**
     * 헤더 리스트.
     */
    public List<SectionContentList> headerList = null;

    /**
     * 브랜드 리스트
     */
    public ArrayList<SectionContentList> brandBanner = null;

    public GroupSortFillterInfo groupSortFillterInfo;

    /**
     * 페이징 url
     */
    public String ajaxPageUrl = null;

    public String ajaxfullUrl = null;

    /**
     * 쿼리 요청 시간
     */
    public long saveTime = 0;

    /**
     * 내일TV 자동재생 여부
     */
    public String brigCoveAutoPlayYn = "N";

    // 페이지 호출될 때에 호출 할 commonClickUrl
    public String commonClickUrl;

    // 쿠폰 팝업 호출을 위한 URL
    public String couponPopupUrl;
}
