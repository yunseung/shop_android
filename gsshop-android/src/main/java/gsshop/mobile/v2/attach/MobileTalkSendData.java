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

import java.util.ArrayList;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 모바일상담 파일전송 결과  데이타 모델 (data 영역)
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkSendData {
	/**
	 * ArrayList MobileTalkSendDataMsg messages
	 */
	private ArrayList<MobileTalkSendDataMsg> messages;
	
	/**
	 * @return the messages
	 */
	public ArrayList<MobileTalkSendDataMsg> getMessages() {
		return messages;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(ArrayList<MobileTalkSendDataMsg> messages) {
		this.messages = messages;
	}
	
}


