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

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 모바일 상담삭제 데이타 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkDeleteInfo extends MobileTalkInfo {

	/**
	 * talk_id = TalkStart 커맨드로 생성된 톡의 고유번호
	 */
	private String talk_id;

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
	
}
