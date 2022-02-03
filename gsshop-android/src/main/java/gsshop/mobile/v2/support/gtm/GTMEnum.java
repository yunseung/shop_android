package gsshop.mobile.v2.support.gtm;


/**
 * GTM 로깅을 위한 카테고리, 액션, 라벨 정의 클래스
 *
 */
public class GTMEnum {

	
	//GTM 공백이 발생할 경우
    public static final String GTM_NONE = "NONE";
	
    //GTM 카테고리 (모든 트래킹에 공통으로 사용)
    public static final String GTM_AREA_CATEGORY = "Area_Tracking";
    
    //GTM 액션 해더
    public static final String GTM_ACTION_HEADER = "MC";
    
    //GTM 액션에 포함될 서비스 종류 (App or Web and so on...)
    public static final String GTM_ACTION_SERVICE_APP = "App";
    public static final String GTM_ACTION_SERVICE_WEB = "Web";

    //GTM 액션에 포함될 서비스 헤더 + 종류 ex) MC_APP
    public static final String GTM_ACTION_HEADER_APP  = GTM_ACTION_HEADER + "_" + GTM_ACTION_SERVICE_APP;

    //GTM 액션에 포함될 위젯 테일  
    public static final String GTM_ACTION_WIDGET_TAIL = "Widget";
    
    //GTM 액션에 포함될 공통 상단 띠배너  
    public static final String GTM_ACTION_BANNER_TAIL = "Banner";
 
    //GTM 액션에 포함될 TV 쇼핑탭 생방송(데이터홈쇼핑포함) 바로 아래있는 배너
    public static final String GTM_ACTION_ONAIR_BANNER_TAIL = "OnAir_Banner";    
    
    //GTM 액션에 포함될 3depth 테일
    public static final String GTM_ACTION_3DEPTH_TAIL = "Tab";
    
    //GTM 액션에 포함될 3depth 테일
    public static final String GTM_ACTION_LIVE_CATEGORY_TAIL = "Category";
    
    //GTM 액션에 포함될 TV 현재방송, 다음 방송 테일
    public static final String GTM_ACTION_TVTAB_NEXTV_TAIL = "NextTV";
    
    //GTM 생방송 영역 테일
    public static final String GTM_ACTION_LIVE_TAIL = "Main_Live";

    //GTM 생방송 영역 라이브톡 테일
    public static final String GTM_ACTION_LIVE_LIVETALK_TAIL = "Main_Live_LiveTalk";

    //GTM 생방송 영역 날톡 테일
    public static final String GTM_ACTION_LIVE_NALTALK_TAIL = "Main_Live_NalTalk";

    //GTM 생방송 영역 편성표버튼 테일
    public static final String GTM_ACTION_LIVE_SCHEDULE_TAIL = "Main_Schedule";

    //GTM 생방송 영역 바로구매버튼 테일
    public static final String GTM_ACTION_DIRECT_ORD_TAIL = "DirectOrd";
    
    //GTM 오늘의딜 영역 테일
    public static final String GTM_ACTION_TODAY_TAIL = "Sec_TodayDeal";
    
    //GTM 주간딜 영역 테일
    public static final String GTM_ACTION_WEEKLY_TAIL = "Main_WeeklyBestDealNo1";
    
    //GTM 홈상단 로고
    public static final String GTM_ACTION_LOGO_TAIL = "Main_Top_Logo";
    
    //GTM 홈상단 카트
    public static final String GTM_ACTION_CART_TAIL = "Main_Top_Cart";

    //GTM 심야방송 LIVE 영역 액션 정보
    public static final String GTM_ACTION_NALBANG_LIVE = "LiveBC_Live" + "_" + GTM_ACTION_SERVICE_APP;

    //GTM 심야방송 VOD 영역 액션 정보
    public static final String GTM_ACTION_NALBANG_VOD = "LiveBC_VOD" + "_" + GTM_ACTION_SERVICE_APP;

    //GTM 심야방송 LAVEL
    public static final String GTM_LABEL_NALBANG = "Open";

    //GTM 1depth 액션 정보
    public static final String GTM_1DEPTH_ACTION = GTM_ACTION_HEADER + "_1depth";
    
    //GTM 2depth 액션 정보
    public static final String GTM_2DEPTH_ACTION = GTM_ACTION_HEADER + "_2depth";
    
    //GTM 하단푸터 액션 정보
    public static final String GTM_FOOTER_ACTION = GTM_ACTION_HEADER + "_Footer";
    
    //GTM 리스트에서 상품위치 노출  액션 정보 (리스트뷰의 25,50,75... 위치 노출)
    public static final String GTM_IMPRESSION_TAIL = "Impression";

	//GTM 리스트에서 상품위치 노출  액션 정보 (리스트뷰의 25,50,75... 위치 노출)
	public static final String GTM_IMPRESSION_SRL_TAIL = "SRS_Impression";
    
    //GTM 프로모션 팝업 노출 액션 정보
    public static final String GTM_PROMOTION_IMPRESSION_ACTION = GTM_ACTION_HEADER + "_" + GTM_ACTION_SERVICE_APP + "_main_popup_impression";

    //GTM 프로모션 팝업 클릭 액션 정보
    public static final String GTM_PROMOTION_CLICK_ACTION = GTM_ACTION_HEADER + "_" + GTM_ACTION_SERVICE_APP + "_main_popup_click";

    //GTM 라벨 (라벨에 특별히 표시할 상품코드나 url등의 정보가 없는 경우 사용)
    public static final String GTM_LABEL_CLICK = "Click";
    
    //GTM FHOME, FCATEGORY...(탭메뉴) label 정보
    public enum GTM_TABMENU_LABEL {
    	Home("홈"),
    	Category("카테고리"),
    	Search("검색"),
    	Cart("스마트카트"),
    	Order("주문배송"),
    	MyShop("마이쇼핑");
    	
    	private String label = "";
    	
    	GTM_TABMENU_LABEL(String label) {
    		this.label = label;
    	}
    	
    	public String getLabel() {
    		return label;
    	}
    }

    //GTM Search action 정보
    public enum GTM_SEARCH_ACTION {
    	SEARCH_DIRECT, 		//직접검색
    	SEARCH_VOICE, 		//음성검색
    	SEARCH_QRCODE, 		//QR코드검색
    	SEARCH_POPULAR, 	//인기검색어 클릭
    	SEARCH_RECENT, 		//최근검색어 클릭
    	SEARCH_AUTO, 		//연관검색어 클릭
    	SEARCH_PROMOTION, 	//프로모션검색
    	MAIN_SEARCH_PROMOTION, 	//프로모션검색(메인화면)
    	MAIN_SEARCH_DIRECT, 	//직접검색(메인화면)
    	SEARCH, 	//메인화면 상단 검색입력창 클릭
        SEARCH_RECOMMAND //추천검색어(최근검색어 기반으로한 연관검색어)
    }
    
    //위젯내에 메뉴별 label 정보
    public enum GTM_WIDGET_LABEL {
    	LiveTV("생방송"),	//생방송 보기
    	LiveGoods("바로주문"),	//생방송 상품 바로 주문
    	Search("검색"),	//검색
    	Cart("장바구니"),	//스마트카트
    	Order("주문배송");	//주문배송
    	
    	private String label = "";
    	
    	GTM_WIDGET_LABEL(String label) {
    		this.label = label;
    	}
    	
    	public String getLabel() {
    		return label;
    	}
    }
}
