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
 * 모바일상담 파일첨부 결과  데이타 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkSendResult extends MobileTalkResult {

	/**
	 * MobileTalkSendData data
	 */
	private MobileTalkSendData data;
	/**
	 * @return the data
	 */
	public MobileTalkSendData getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(MobileTalkSendData data) {
		this.data = data;
	}
}


