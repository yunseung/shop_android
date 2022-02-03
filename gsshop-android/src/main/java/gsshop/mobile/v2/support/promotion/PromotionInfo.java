/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.promotion;

import java.io.Serializable;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 메뉴탭에 표시할 뱃지 정보.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class PromotionInfo implements Serializable {

    /**
	 * serial version unique id
	 */
	private static final long serialVersionUID = 1000L;

	/**
     * Link Type
     */
    public String linktype;

    /**
     * Image Click시 이동 웹페이지
     */
    public String link;

    /**
     * Promotion Banner 크기(Big, Middle, Small) 
     */
    public String bannertype;

    /**
     * Promotion Image Url;
     */
    public String imgurl;
    public String result;
}
