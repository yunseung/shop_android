/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.model;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 540 배너
 */
@Model
public class LiveBanner {
	public String imageUrl;
	public String videoid;	//brightcove 비디오번호
	public String linkUrl;
	public String imageAlt;
	public String text;
}

