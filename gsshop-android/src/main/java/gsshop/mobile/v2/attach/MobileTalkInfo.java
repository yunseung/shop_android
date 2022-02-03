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

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 모바일상담 공통영역 데이타 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkInfo {
	/**
	 * command = TalkSend (변동)
	 */
	private String command;
	/**
	 * domain_id = NODE0000000001 (고정)
	 */
	private String domain_id;
	/**
	 * service_type = SVTLK (고정)
	 */
	private String service_type;
	/**
	 * customer_id = 고객아이디 (변동)
	 */
	private String customer_id;
	
	/**
	 * API 주소
	 */
	@JsonIgnore
	private String apiUrl;
	
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	/**
	 * @return the domain_id
	 */
	public String getDomain_id() {
		return domain_id;
	}
	/**
	 * @param domain_id the domain_id to set
	 */
	public void setDomain_id(String domain_id) {
		this.domain_id = domain_id;
	}
	/**
	 * @return the service_type
	 */
	public String getService_type() {
		return service_type;
	}
	/**
	 * @param service_type the service_type to set
	 */
	public void setService_type(String service_type) {
		this.service_type = service_type;
	}
	/**
	 * @return the customer_id
	 */
	public String getCustomer_id() {
		return customer_id;
	}
	/**
	 * @param customer_id the customer_id to set
	 */
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	/**
	 * @return the apiUrl
	 */
	public String getApiUrl() {
		return apiUrl;
	}
	/**
	 * @param apiUrl the apiUrl to set
	 */
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
}
