/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;

import com.gsshop.mocha.pattern.mvc.Model;

import java.util.ArrayList;

/**
 * 생방송 상단에 라이브톡 배너
 *
 */
@Model
public class LiveTalkBanner {
    public String linkUrl;
    public ArrayList<String> talkList;
    public int totalCnt;
}
