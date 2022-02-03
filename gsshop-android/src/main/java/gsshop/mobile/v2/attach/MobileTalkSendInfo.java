/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.File;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 모바일 파일첨부 데이타 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkSendInfo extends MobileTalkInfo {
	/**
	 * talk_id = TalkStart 커맨드로 생성된 톡의 고유번호
	 */
	private String talk_id;
	/**
	 * message
	 */
	private String message;
	/**
	 * seq
	 */
	private long seq;

	/**
	 * 첨부한 파일
	 */
	@JsonIgnore
	private File[] imageFile;
	
	/**
	 * @return the talk_id
	 */
	public String getTalk_id() {
		return talk_id;
	}

	/**
	 * @param talk_id the talk_id to set
	 */
	public void setTalk_id(String talk_id) {
		this.talk_id = talk_id;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the seq
	 */
	public long getSeq() {
		return seq;
	}

	/**
	 * @param seq the seq to set
	 */
	public void setSeq(long seq) {
		this.seq = seq;
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

}
