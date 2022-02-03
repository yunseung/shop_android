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
 * 메인의 섹션 TV 생방송 컨텐츠
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class TvLiveContent {

    /**
     * 메인 섹션중 생방송 컨텐츠 
     */
    public TvLiveBanner appTvLiveBanner = null;
    public TvLiveBanner tvLiveBanner = null;
    public TvLiveBanner dataLiveBanner = null;
    public MobileLiveBanner mobileLiveBanner = null;
    public MobileLiveBanner mobileLiveDefaultBanner = null;
    public ArrayList<SectionContentList> tvLiveBannerList = null;

}
