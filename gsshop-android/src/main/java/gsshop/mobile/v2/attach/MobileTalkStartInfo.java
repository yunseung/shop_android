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
 * 모바일 상담시작 데이타 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkStartInfo extends MobileTalkInfo {
	/**
	 * ref_talk_id = 확인 후 기재
	 */
	private String ref_talk_id;
	/**
	 * node_id = 상동
	 */
	private String node_id;
	/**
	 * in_channel_id = 상동
	 */
	private String in_channel_id;
	/**
	 * message = 사용자 입력 메시지 (변동)
	 */
	private String message;
	/**
	 * customer_name = 옵션
	 */
	private String customer_name;
	
	/**
	 * @return the ref_talk_id
	 */
	public String getRef_talk_id() {
		return ref_talk_id;
	}
	/**
	 * @param ref_talk_id the ref_talk_id to set
	 */
	public void setRef_talk_id(String ref_talk_id) {
		this.ref_talk_id = ref_talk_id;
	}
	/**
	 * @return the node_id
	 */
	public String getNode_id() {
		return node_id;
	}
	/**
	 * @param node_id the node_id to set
	 */
	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}
	/**
	 * @return the in_channel_id
	 */
	public String getIn_channel_id() {
		return in_channel_id;
	}
	/**
	 * @param in_channel_id the in_channel_id to set
	 */
	public void setIn_channel_id(String in_channel_id) {
		this.in_channel_id = in_channel_id;
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
	 * @return the customer_name
	 */
	public String getCustomer_name() {
		return customer_name;
	}
	/**
	 * @param customer_name the customer_name to set
	 */
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
}
