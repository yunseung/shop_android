/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.share;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class ShareInfo {

	/**
	 * 링크
	 */
	private String link;

	/**
	 * 제목
	 */
	private String subject;

	/**
	 * 메세지
	 */
	private String message;

	/**
	 * 이미지 주소
	 */
	private String imageurl;

	/**
	 * 호출대상
	 */
	private String target;

	/**
	 * 이미지 타입
	 */
	public enum ShareImageType {
		TYPE_DEFAULT,	//디폴트
		TYPE_SB_A,	//숏방 가로가 긴 이미지
		TYPE_SB_B    //숏방 세로가 긴 이미지
	}

	public String getShare_title() {
		return share_title;
	}

	public void setShare_title(String shared_title) {
		this.share_title = shared_title;
	}

	private String share_title;

	private ShareImageType shareImageType;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		String linkUrl = link;
		if(linkUrl == null){
			linkUrl = "";
		}
		this.link = linkUrl;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		String shereSubject = subject;
		if(shereSubject == null){
			shereSubject = "";
		}
		this.subject = shereSubject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		String shereMessage = message;
		if(shereMessage == null){
			shereMessage = "";
		}
		this.message = shereMessage;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		String shereImageurl = imageurl;
		if(shereImageurl == null){
			shereImageurl = "";
		}
		this.imageurl = shereImageurl;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		String shereTarget = target;
		if(shereTarget == null){
			shereTarget = "";
		}
		this.target = shereTarget;
	}

	public ShareImageType getShareImageType() {
		return shareImageType;
	}

	public void setShareImageType(ShareImageType shareImageType) {
		this.shareImageType = shareImageType;
	}
}