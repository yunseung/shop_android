/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.tvshoping;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gsshop.mocha.pattern.mvc.Model;


@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class LiveSalesCountInfo {

	/*
	*판매수량 정보 노출 여부
	*현재판매수량
	*공급수량
	*현재판매율
	*/
	public String broadStrDtm;
	public String ordQty;
	public String suplyQty;
	public String saleRate;
	public String saleRateExposeYn;
	
	
}

