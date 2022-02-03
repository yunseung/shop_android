/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.model;

import com.gsshop.mocha.pattern.mvc.Model;

@Model
public class SchedulePrd implements Cloneable{

	public String viewType;
	public String pgmId;
	public String broadStartDate;
	public String broadEndDate;
	public String startTime;
	public String specialPgmYn;
	public LiveBanner specialPgmInfo;
	public LiveBanner liveBanner;
	public String publicBroadYn;
	public LiveBanner liveTalkInfo;
	public String pgmLiveYn;
	public LiveBanner livePlayInfo;
	public String liveBenefitLText;
	public String liveBenefitRText;
	public Product product;
	public TVScheduleModel.BroadLink broadLink;
	//상단 날짜 포지션 저장
	public int datePosition = -1 ;
	//서브상품 펼침상태 저장
	public boolean expandState = false;

	//서브상품 초기화가 되었냐 안되었냐
	public boolean expandInitState = false;

	//공영방송 시 최상단으로 포커싱 되는 부분을 "Y" 인 셀로 포커싱 하기 위함
	public String pgmAnchorYn;

	/**
	 * TV편성표 AB테스트
	 */
	//특정날짜 ~이전, 이후~ 물결 처리위한 변수
	public boolean preNoResult = false;
	public boolean nextNoResult = false;

	//(TV편성표 AB테스트) 특정날짜 ~이전, 이후~ 뷰 숨김 처리위한 변수
	public boolean hideNoResultView = false;

	//dateList의 정보(월,일,요일) 담을 변수
	public String month;
	public String day;
	public String week;

	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}
