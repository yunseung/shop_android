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

import java.io.File;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * FileAttachInfoV2
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class FileAttachInfoV2 {

	/**
	 * 호출자 정보 (쇼미카페, 위클리 이벤트 등)
	 */
	private String caller;
	
	/**
	 * 사용자 입력 내용
	 */
	private String comment;
	
	/**
	 * 첨부한 파일
	 */
	private File[] imageFile;

	/**
	 * 이미지 가로/세로 비율정보 (S:정사각 H:가로>세로 V:세로>가로)
	 */
	private String imageType;

	/**
	 * @return the caller
	 */
	public String getCaller() {
		if(caller == null){
			return "";
		}
		return caller;
	}
	/**
	 * @param caller the caller to set
	 */
	public void setCaller(String caller) {
		this.caller = caller;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return the imageFile
	 */
	public File[] getImageFile() {
		return imageFile;
	}
	/**
	 * @param imageFile the imageFile to set
	 */
	public void setImageFile(File[] imageFile) {
		this.imageFile = imageFile;
	}

	/**
	 * getImageType
	 * @return
     */
	public String getImageType() {
		return imageType;
	}

	/**
	 * setImageType
	 * @param imageType
     */
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
}
