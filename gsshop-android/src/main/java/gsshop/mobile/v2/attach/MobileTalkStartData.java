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
 * 모바일상담최초신청 결과  데이타 모델 (data 영역)
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkStartData {
	/**
	 * talk_id
	 */
	private String talk_id;
	/**
	 * is_receipt_outoftime
	 */
	private boolean is_receipt_outoftime;
	/**
	 * ArrayList MobileTalkStartDataMsg messages
	 */
	private ArrayList<MobileTalkStartDataMsg> messages;
	
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
	 * @return the is_receipt_outoftime
	 */
	public boolean isIs_receipt_outoftime() {
		return is_receipt_outoftime;
	}
	/**
	 * @param is_receipt_outoftime the is_receipt_outoftime to set
	 */
	public void setIs_receipt_outoftime(boolean is_receipt_outoftime) {
		this.is_receipt_outoftime = is_receipt_outoftime;
	}
	/**
	 * @return the messages
	 */
	public ArrayList<MobileTalkStartDataMsg> getMessages() {
		return messages;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(ArrayList<MobileTalkStartDataMsg> messages) {
		this.messages = messages;
	}
	
}


