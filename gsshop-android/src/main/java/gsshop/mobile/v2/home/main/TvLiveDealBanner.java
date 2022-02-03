/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;


import com.gsshop.mocha.pattern.mvc.Model;


/**
 * 날방 생방송 정보
 *
 */
@Model
public class TvLiveDealBanner {
    public String imageUrl;
    public String linkUrl;
    public String productName;
    public String promotionName;
    public String broadStartTime;   //VOD or 생방송 시작시간 (onAirYn=Y인 경우 생방송 시작시간)
    public String broadCloseTime;   //VOD or 생방송 종료시간
    public String liveBroadStartTime;   //생방송 시작시간
    public String videoTime;
    public String onAirYn;
    public String tvLiveDealUrl;
    public SectionContentList product;
    public LiveTalkBanner nalTalkBanner;   //날톡 배너
}
