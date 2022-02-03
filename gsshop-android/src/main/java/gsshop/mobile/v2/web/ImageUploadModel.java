/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 이미지 업로드 결과 정보.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class ImageUploadModel {

    /**
     * HTTP 응답 코드
     */
    private int responseCode = -100;

    /**
     * 웹뷰내에 세팅된 섬네일 아이디
     */
    private String thumbnailId = "";
    
    /**
     * 업로드된 이미지 URL
     */
    private String imageUrl = "";

	/**
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the thumbnailId
	 */
	public String getThumbnailId() {
		return thumbnailId;
	}

	/**
	 * @param thumbnailId the thumbnailId to set
	 */
	public void setThumbnailId(String thumbnailId) {
		this.thumbnailId = thumbnailId;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}



    
}