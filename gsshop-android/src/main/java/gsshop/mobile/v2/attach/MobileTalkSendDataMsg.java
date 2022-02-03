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
 * 모바일상담최초신청 결과  데이타 모델 (message 영역)
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class MobileTalkSendDataMsg {
	/*
	sample
	{
	"type": "MSG",
	"ext": ".jpg",
	"msg": "질문있습니다",
	"sender": "TTTKMEqm3JBK1Tsx4/NHAw==",
	"seq": 0,
	"flag": "C",
	"agent_read_flag": "N",
	"cdate": "20150119102945",
	"customer_read_flag": "Y",
	"agent": "{NO-AGENT}",
	"talk_id": "TCKT0000000097"
	},
	{
	"type": "REG",
	"ext": ".jpg",
	"msg": "상담이 접수되었습니다. 빠른 안내 드리도록 하겠습니다.",
	"sender": "SYSTEM",
	"seq": 1,
	"flag": "S",
	"agent_read_flag": "Y",
	"cdate": "20150119102945",
	"customer_read_flag": "Y",
	"agent": "{NO-AGENT}",
	"talk_id": "TCKT0000000097"
	}
	 */

	/**
	 * type
	 */
	private String type;
	/**
	 * ext
	 */
	private String ext;
	/**
	 * msg
	 */
	private String msg;
	/**
	 * sender
	 */
	private String sender;
	/**
	 * seq
	 */
	private int seq;
	/**
	 * flag
	 */
	private String flag;
	/**
	 * agent_read_flag
	 */
	private String agent_read_flag;
	/**
	 * cdate
	 */
	private String cdate;
	/**
	 * customer_read_flag
	 */
	private String customer_read_flag;
	/**
	 * agent
	 */
	private String agent;
	/**
	 * talk_id
	 */
	private String talk_id;
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the ext
	 */
	public String getExt() {
		return ext;
	}
	/**
	 * @param ext the ext to set
	 */
	public void setExt(String ext) {
		this.ext = ext;
	}
	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}
	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}
	/**
	 * @return the seq
	 */
	public int getSeq() {
		return seq;
	}
	/**
	 * @param seq the seq to set
	 */
	public void setSeq(int seq) {
		this.seq = seq;
	}
	/**
	 * @return the flag
	 */
	public String getFlag() {
		return flag;
	}
	/**
	 * @param flag the flag to set
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	/**
	 * @return the agent_read_flag
	 */
	public String getAgent_read_flag() {
		return agent_read_flag;
	}
	/**
	 * @param agent_read_flag the agent_read_flag to set
	 */
	public void setAgent_read_flag(String agent_read_flag) {
		this.agent_read_flag = agent_read_flag;
	}
	/**
	 * @return the cdate
	 */
	public String getCdate() {
		return cdate;
	}
	/**
	 * @param cdate the cdate to set
	 */
	public void setCdate(String cdate) {
		this.cdate = cdate;
	}
	/**
	 * @return the customer_read_flag
	 */
	public String getCustomer_read_flag() {
		return customer_read_flag;
	}
	/**
	 * @param customer_read_flag the customer_read_flag to set
	 */
	public void setCustomer_read_flag(String customer_read_flag) {
		this.customer_read_flag = customer_read_flag;
	}
	/**
	 * @return the agent
	 */
	public String getAgent() {
		return agent;
	}
	/**
	 * @param agent the agent to set
	 */
	public void setAgent(String agent) {
		this.agent = agent;
	}
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


