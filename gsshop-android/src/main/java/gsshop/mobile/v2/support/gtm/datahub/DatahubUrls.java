package gsshop.mobile.v2.support.gtm.datahub;


/**
 * 데이타허브 URL 정의
 *
 */
public class DatahubUrls {
    
	public static final String HTTP_HOST = "http://gtm.gsshop.com";
	
	public static final String WRAPPER_URL = HTTP_HOST + "/index.htm?uri=";
		
	//최초 앱 실행(재설치 제외) > GSSHOP 광고 푸시알림 설정 안내 팝업 > 승인
    public static final String D_1001 = HTTP_HOST + "/mgs/app/4020609";
    
    //최초 앱 실행(재설치 제외) > GSSHOP 광고 푸시알림 설정 안내 팝업 > 허용안함
    public static final String D_1002 = HTTP_HOST + "/mgs/app/4020610";
    
    //상단 메뉴 > 검색입력박스, 날방/라이브톡 검색아이콘 클릭
    public static final String D_1016 = HTTP_HOST + "/mgs/app/1020103";
    
    //하단 메뉴 마이쇼핑 > 상단 설정 아이콘 > 알림(PUSH) 메시지 수신 체크 > 팝업창 확인 클릭
    public static final String D_1030 = HTTP_HOST + "/mgs/app/4020609";
    
    //하단 메뉴 마이쇼핑 > 상단 설정 아이콘 > 알림(PUSH) 메시지 수신 체크 해제 > 팝업창 확인 클릭
    public static final String D_1031 = HTTP_HOST + "/mgs/app/4020610";
    
    //로그인화면 > 로그인버큰 클릭 (로그인 시작시점)
    public static final String D_1032 = HTTP_HOST + "/mgs/app/D1032/1050101";
    
    //하단 메뉴 마이쇼핑 > 상단 설정 아이콘 > 로그인 정보 > 로그아웃 > 확인
    public static final String D_1033 = HTTP_HOST + "/mgs/app/4060201";
    
    //로그인화면 > 로그인화면 노출시점
    public static final String D_1038 = HTTP_HOST + "/mgs/app/D1038/1050101";

    //마이쇼핑 > 1:1모바일상담 > 접수하기버튼 클릭 (접수하기 완료 시점)
    public static final String D_1039 = HTTP_HOST + "/mgs/app/4030101";
}
