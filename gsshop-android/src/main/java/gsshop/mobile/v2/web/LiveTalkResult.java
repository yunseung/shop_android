/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 라이브톡 API 호출 결과 데이타 모델
 */
@Model
public class LiveTalkResult {
	public String liveTalkNo;   // "201601201340",
	public String videoid;
	public String liveUrl;  // "http://livem.gsshop.com:80/gsshop/_definst_/gsshop.sdp/playlist.m3u8",
	public String title;    // "세척이 쉬우면 말 다했지! 윤남텍 가습기",
	public String imgUrl;  // "http://image.gsshop.com/mi09/livedeal/20151127181236100179.jpg",
	public String strDate;  // "20160202094000"
	public String endDate;  // "20160202104000"
	public String btmUrl;    // 플레이어 하단 웹뷰에 로딩할 url.
}


