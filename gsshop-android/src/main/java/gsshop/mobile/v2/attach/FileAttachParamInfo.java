/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 웹뷰에서 앱스키마 호출시 전달하는 파라미터 규격 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class FileAttachParamInfo implements Serializable {

	/**
	 * 유니크한 아이디
	 */
	private static final long serialVersionUID = 10002345L;

	/**
	 * 호출자 (쇼미카페, 위클리 이벤트 등 구뷴자)
	 */
	private String caller;
	
	/**
	 * 파일저장 Url
	 */
	private String uploadUrl;
	
	/**
	 * 파일저장 후 호출할  Url
	 */
	private String returnUrl;
	
	/**
	 * 파일저장 후 호출할  콜백함수 (모바일상담 전용)
	 */
	private String callback;
	
	/**
	 * 파일첨부 타입
	 */
	private String mediatype;

	/**
	 * 이미지 타입
	 */
	private String imageType;

	/**
	 * 첨부가능 이미지 갯수
	 */
	private String imageCount;

	/**
	 * 현재 페이지 history back 여부
	 */
	private String historyBack;

	/**
	 * 동영상 최대용량(MB)
	 */
	private int maxVideoSize;

	/**
	 * 모바일상담 입력창 구분
	 * talkui=A : 기본 입력창
	 * talkui=B : 신규 입력창
	 * User-Agent 150 이상에서만 동작
	 */
	private String talkui;

	/**
	 * @return the caller
	 */
	public String getCaller() {
		return caller;
	}

	/**
	 * @param caller the caller to set
	 */
	public void setCaller(String caller) {
		this.caller = caller;
	}

	/**
	 * @return the uploadUrl
	 */
	public String getUploadUrl() {
		return uploadUrl;
	}

	/**
	 * @param uploadUrl the uploadUrl to set
	 */
	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	/**
	 * @return the returnUrl
	 */
	public String getReturnUrl() {
		return returnUrl;
	}

	/**
	 * @param returnUrl the returnUrl to set
	 */
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	/**
	 * @return the callback
	 */
	public String getCallback() {
		return callback;
	}

	/**
	 * @param callback the callback to set
	 */
	public void setCallback(String callback) {
		this.callback = callback;
	}

	/**
	 * @return the mediatype
	 */
	public String getMediatype() {
		return mediatype;
	}

	/**
	 * @param mediatype the mediatype to set
	 */
	public void setMediatype(String mediatype) {
		this.mediatype = mediatype;
	}

	/**
	 * @return the imageType
	 */
	public String getImageType() {
		return imageType;
	}

	/**
	 * @param imageType the imageType to set
	 */
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	/**
	 * @return the imageCount
	 */
	public String getImageCount() {
		return imageCount;
	}

	/**
	 * @param imageCount the imageCount to set
	 */
	public void setImageCount(String imageCount) {
		this.imageCount = imageCount;
	}

	/**
	 * @return the historyBack
	 */
	public String getHistoryBack() {
		return historyBack;
	}

	/**
	 * @param historyBack the historyBack to set
	 */
	public void setHistoryBack(String historyBack) {
		this.historyBack = historyBack;
	}

	/**
	 * getMaxVideoSize
	 *
	 * @return maxVideoSize
     */
	public int getMaxVideoSize() {
		return maxVideoSize;
	}

	/**
	 * setMaxVideoSize
	 *
	 * @param maxVideoSize maxVideoSize
     */
	public void setMaxVideoSize(int maxVideoSize) {
		this.maxVideoSize = maxVideoSize;
	}

	public String getTalkui() {
		return talkui;
	}

	public void setTalkui(String talkui) {
		this.talkui = talkui;
	}
}
