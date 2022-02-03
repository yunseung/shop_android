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
 * 모바일상담최초신청 결과  데이타 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkStartResult extends MobileTalkResult {

	/**
	 * MobileTalkStartData
	 */
	private MobileTalkStartData data;
	
	/**
	 * @return the data
	 */
	public MobileTalkStartData getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(MobileTalkStartData data) {
		this.data = data;
	}
}


