/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2;


import android.text.TextUtils;

import static com.gsshop.mocha.BaseApplication.getAppContext;

/**
 * 모바일 서버와 연동하기 위한 각종 주소 모음.
 */
public abstract class ServerUrls {

    // PROTOCOL
    public static final String HTTP = "http://";

    public static final String HTTPS = "https://";

    public static final String MOBILE_WEB = "http://m.gsshop.com/";

    // host
    public static final String WEB_ROOT;
    // public static final String PORT;
    // HTTP ROOT
    public static final String HTTP_ROOT;
    // HTTPS ROOT / WEB_ROOT 사용하는애덜을 따로 따지 않고 HTTPS_ROOT 따랏다..
    public static final String HTTPS_ROOT;

    // 앱 내에서 수동적으로 sm21, tm14, m 을 변경할 수 있도록 하는데에 필요해서 추가한 변수.
    public static String TEMP_WEB_ROOT;
    public static String TEMP_HTTP_ROOT;
    public static String TEMP_HTTPS_ROOT;

    private static String IMAGE_SERVER_ROOT = "http://image.gsshop.com/";

    public static String getWebRoot() {
        if (TextUtils.isEmpty(TEMP_WEB_ROOT)) {
            return WEB_ROOT;
        } else {
            return TEMP_WEB_ROOT;
        }
    }

    public static String getHttpRoot() {
        if (TextUtils.isEmpty(TEMP_HTTP_ROOT)) {
            return HTTP_ROOT;
        } else {
            return TEMP_HTTP_ROOT;
        }
    }

    public static String getHttpsRoot() {
        if (TextUtils.isEmpty(TEMP_HTTPS_ROOT)) {
            return HTTPS_ROOT;
        } else {
            return TEMP_HTTPS_ROOT;
        }
    }

    public static String getImageRoot() {
        return IMAGE_SERVER_ROOT;
    }

    // CSP 서비스 루트
    public static String CSP_SERVICE_ROOT;
    // CSP 브레이커 루트
    public static final String CSP_BREAK_ROOT;

    /**
     * 최근 본 상품 이미지 주소
     */
    public static final String LAST_PRD_IMAGE;

    // 메인 주소 패턴 글로벌로 변경, HOST는 제외 하였다
    public static final String ROOT_URL_PATTERN = ".gsshop.com" + "/index";

    public static final String URL_PATTERN_GO_URL = "&gourl";

    static {
        // server.xml 파일 확인.
        WEB_ROOT = getAppContext().getString(R.string.web_http_root);
        HTTP_ROOT = getAppContext().getString(R.string.server_http_root);
        HTTPS_ROOT = getAppContext().getString(R.string.server_https_root);
        CSP_SERVICE_ROOT = getAppContext().getString(R.string.csp_service_root);
        CSP_BREAK_ROOT = getAppContext().getString(R.string.csp_break_root);
        LAST_PRD_IMAGE = HTTP + getAppContext().getString(R.string.last_prd_image);
    }

    /**
     * 웹페이지 주소
     */
    public static class WEB {

        /**
         * 위젯에서 또는 백버튼이 표시되지 않아야 되는 경우 사용
         */
        public static final String BACKBUTTON_FLAG = "backButtonFlag=N";
        public static final String BACKBUTTON_FLAG_TYPE1 = "?" + BACKBUTTON_FLAG;
        public static final String BACKBUTTON_FLAG_TYPE2 = "&" + BACKBUTTON_FLAG;

        public static final String FROM_APP = "fromApp=Y";
        public static final String FROM_APP_TYPE1 = "?" + FROM_APP;
        public static final String FROM_APP_TYPE2 = "&" + FROM_APP;

        public static final String TABID_MART = "mart";
        public static final String FROM_MARTTAB = "cartTabId" + "=" + TABID_MART;
        public static final String FROM_MARTTAB_TYPE1 = "?" + FROM_MARTTAB;
        public static final String FROM_MARTTAB_TYPE2 = "&" + FROM_MARTTAB;

        public static final String HOME = getWebRoot();

        /**
         * 앱 실행시 쿠키 생성을 위한 URL
         */
        public static final String APP_START = getWebRoot() + "/shop/shopstart.gs";

        /**
         * TODO : URL 변경 필요 (현재는 웹 URL 임시 사용)
         */
        public static final String CATEGORY = getWebRoot() + "/m/sect/category.gs?mseq=398050" + FROM_APP_TYPE2;

        /**
         * 이걸 위젯에서 쓰네 우선은 홈으로 이동하게함
         */
        public static final String SMART_CART = getWebRoot() + "/mobile/cart/viewCart.gs?mseq=398045"
                + FROM_APP_TYPE2;
        //500 에러 테스트 코드
        //public static final String SMART_CART = getWebRoot() + "/section/jbp/brandMain.gs?jbpBrandCd=1000001506" + FROM_APP_TYPE2;

        /**
         * GS Fresh 매장에서만 쓰이는 상단 장바구니 버튼 url.
         */
        public static final String FRESH_SMART_CART_TOP = getWebRoot() + "/mobile/cart/viewCart.gs?mseq=398045"
                + FROM_APP_TYPE2 + FROM_MARTTAB_TYPE2;

        /**
         * GS FRESH 탭에서 장바구니 보기 버튼 클릭시 cartTabID = mart 파라미터를 통해 장바구니 내 default 탭을 지정
         */
        public static final String FRESH_SMART_CART = getWebRoot() + "/mobile/cart/viewCart.gs?mseq=A00481-GC-1"
                + FROM_APP_TYPE2 + FROM_MARTTAB_TYPE2;

        /**
         * 이걸 위젯에서 쓰네 우선은 홈으로 이동하게함
         */
        public static final String ORDER = getWebRoot() + "/mygsshop/myOrderList.gs?mseq=398053"
                + FROM_APP_TYPE2;

        /**
         * 이걸 위젯이랑 공통으로 쓰네 (GNB)
         */
        public static final String MY_SHOP = getWebRoot() + "/mygsshop/myshopInfo.gs?mseq=412205"
                + FROM_APP_TYPE2;

        /**
         * 이걸 위젯이랑 공통으로 쓰네 (하단탭바)
         */
        public static final String MY_SHOP_TAB = getHttpsRoot() + "/mygsshop/myshopInfo.gs?mseq=398041"
                + FROM_APP_TYPE2;


        /**
         * 하단카테고리탭
         * 웹 주소 및 효율 코드 http://m.gsshop.com/m/sect/category.gs?mseq=398043
         * 네이티브 주소 및 효율 코드 http://m.gsshop.com/m/sect/category.gs?mseq=398050 (미정, 현재 앱용 효율 코드 )
         */
        public static final String BOTTOM_CATEGORY_TAB = getWebRoot() + "/m/sect/category.gs?mseq=398050";

        /**
         * 상단카테고리탭
         * 웹 주소 및 효율 코드 http://m.gsshop.com/m/sect/category.gs?mseq=412109
         */
        public static final String TOP_CATEGORY_TAB = getWebRoot() + "/m/sect/category.gs?mseq=412109";


        /**
         * 찜한상품
         */
        public static final String ZZIM_PRD = getWebRoot() + "/section/myWishList.gs?mseq=410808"
                + FROM_APP_TYPE2;

        /**
         * 최근본상품
         */
        public static final String LAST_PRD = getWebRoot() + "/section/recntView?mseq=410809"
                + FROM_APP_TYPE2;

        /**
         * 버전 체크
         * <p>
         * 구 버전 체크 :  "/apis/AndroidLatestVersion"
         */
        public static final String LATEST_VERSION = getWebRoot() + "/shop/shopAndVer";

        /**
         * 아이디 찾기.
         * history
         * 20160322 http-{@literal >} https 로 변경됨 leems
         */
        public static final String FIND_LOGIN_ID = getWebRoot() + "/member/memberIDSearch.gs"
                + FROM_APP_TYPE1;

        /**
         * 비밀번호 찾기.
         * 20160322 http-{@literal >} https 로 변경됨 leems
         */
        public static final String FIND_PASSWORD = getWebRoot() + "/member/memberPasswordSearch.gs"
                + FROM_APP_TYPE1;

        /**
         * 회원가입
         */
        public static final String JOIN_USER = getHttpsRoot()
                + "/member/gSShopJoinFormAndConditionAgree.gs" + FROM_APP_TYPE1;

        /**
         * 인증이메일 재발송
         * 20160322 http-{@literal >} https 로 변경됨 leems
         */
        public static final String EMAIL_CERT = getWebRoot() + "/cert/emailCert/form.gs"
                + FROM_APP_TYPE1;

        /**
         * 휴대폰 로그인
         * 08/24 MSLEE HTTPS 서버에서 강제로 http->https 강제로 변경시 주소?gourl=인코딩주소 형태가 들어오면 문제가 발생됨 홍K<->이민수 확인
         */
        public static final String PHONE_LOGIN = getHttpsRoot() + "/member/loginMcctCert.gs?titleNm=mcct" + FROM_APP_TYPE2;

        /**
         * TV Members 로그인
         * history
         */
        public static final String URL_TV_MEMBERS_LOGIN = getHttpsRoot() + "/member/tvLogIn.gs" + FROM_APP_TYPE1;

        /**
         * 이용약관 및 개인정보 취급방침.
         */
        public static final String PROVISION = getWebRoot() + "/m/mygsshop/articleMain.gs"
                + FROM_APP_TYPE1;

        /**
         * 회사소개.
         */
        public static final String COMPANY = getWebRoot() + "/m/mygsshop/companyInfo.gs"
                + FROM_APP_TYPE1;

        /**
         * 상품 검색 결과. 파라미터 : ?tq=검색어
         */
        public static final String SEARCH = getWebRoot() + "/search/searchSect.gs" + FROM_APP_TYPE1;

        /**
         * ISP 결제 콜백. 파라미터 : ?TID=TID값.
         */
        public static final String PRE_ISP_CONFIRM_ORDER = getWebRoot()
                + "/mobile/order/main/preISPConfirmOrder.gs";

        /**
         * KAKAO 결제 콜백. 파라미터 : ?KID=KID값.
         */
        public static final String PRE_KAKAO_CONFIRM_ORDER = getWebRoot()
                + "/mobile/order/main/preKAKAOConfirmOrder.gs";

        /**
         * PAYNOW 결제 콜백. 파라미터 : ?NID=NID값.
         */
        public static final String PRE_PAYNOW_CONFIRM_ORDER = getWebRoot()
                + "/mobile/order/paynow/prePaynowConfirmOrder.gs";

        public static final String BLANK = "about:blank";

        /**
         * 생방송 상품 보기(앱위젯용)
         */
        public static final String LIVE_TV = getWebRoot() + "/main/tv/tvLiveMain" + FROM_APP_TYPE1;

        /**
         * 파라미터 : ?prdid=상품ID
         */
        public static final String PRODUCT_DETAIL = getWebRoot() + "/prd/prd.gs" + FROM_APP_TYPE1;

        /**
         * 비회원 배송조회. 파라미터 : ?returnurl=xxx
         * history
         * 20160322 비회원 배송 조회 화면 https 로 변경 leems
         * 2018 / 08/24 MSLEE HTTPS 서버에서 강제로 http->https 강제로 변경시 주소?gourl=인코딩주소 형태가 들어오면 문제가 발생됨 홍K<->이민수 확인
         */
        public static final String NON_MEMBER_SHIPPING = getHttpsRoot() + "/member/noMemberOrderList.gs"
                + FROM_APP_TYPE1;

        /**
         * 비회원 배송조회. 파라미터 : ?returnurl=xxx
         * history
         * 20160322 비회원 배송 조회 화면 https 로 변경 leems
         * 2018 / 08/24 MSLEE HTTPS 서버에서 강제로 http->https 강제로 변경시 주소?gourl=인코딩주소 형태가 들어오면 문제가 발생됨 홍K<->이민수 확인
         */
        public static final String NON_MEMBER_ORDER = getHttpsRoot() + "/member/certifyNoMember.gs"
                + FROM_APP_TYPE1;

        /**
         * 비회원 성인인증. 파라미터 : ?returnurl=xxx
         */
        public static final String ADULT_CHECK = getWebRoot() + "/member/adultCheck.gs"
                + FROM_APP_TYPE1;

        /**
         * 모바일라이브 네이티브 공유하기 url에서 쓰이는 appRedirect
         */
        public static final String APPREDIRECT = getWebRoot() + "/product/appRedirect.gs?rtnUrl=";

        /**
         * 와이즈로그 파라미터 : ?lseq==xxx{@literal &}mseq=xxx
         * TAB_CATEGORY / TAB_MYSHOP 해당 페이지가 url 직접 호출로 변경됨에 따라 url + mseq 로 변경됨
         */
        public static final String WISE_URL = getWebRoot() + "/app/statistic/wiseLog"; // 와이즈로그 URL
        public static final String WISE_CLICK_URL = getWebRoot() + "/mobile/commonClickTrac.jsp"; // 와이즈로그 클릭 URL
        //상단 GNB
        public static final String MAINTOP_NAVI = WISE_CLICK_URL + "?mseq=412109"; // 메인상단 네비 click
        public static final String MAINTOP_LOGO = WISE_CLICK_URL + "?mseq=A00000"; // 메인상단 로고 click
        public static final String MAINTOP_SEARCH = WISE_CLICK_URL + "?mseq=398051"; // 메인상단  검색입력박스 click

        //네비게이션화면
        public static final String NAVI_LOGO = WISE_CLICK_URL + "?mseq=413111"; // 로고 click
        public static final String NAVI_SEARCH = WISE_CLICK_URL + "?mseq=413112"; // 검색입력박스 click
        public static final String NAVI_CLOSE = WISE_CLICK_URL + "?mseq=413113"; // 닫기 click
        public static final String NAVI_POSITION_CHANGE = WISE_CLICK_URL + "?mseq=412158"; // 위아래 이동 click
        public static final String NAVI_THEME_COLOR = WISE_CLICK_URL + "?mseq=412156"; // 배경색 컬러 click
        public static final String NAVI_THEME_BLACK = WISE_CLICK_URL + "?mseq=412157"; // 배경색 블랙 click
        public static final String NAVI_SETTING = WISE_CLICK_URL + "?mseq=418031"; // 설정 click
        public static final String NAVI_LOGIN = WISE_CLICK_URL + "?mseq=418029"; // 로그인 click

        //하단탭바
        public static final String TAB_HOME = WISE_CLICK_URL + "?mseq=398049"; // 홈 click
        public static final String TAB_NAVI = WISE_CLICK_URL + "?mseq=398050"; // 카테고리 click

        //footer
        public static final String FOOTER_TEL = WISE_CLICK_URL + "?mseq=415448"; // 상담/주문 전화번호 클릭

        //설정
        public static final String SETTING_SHOW = WISE_CLICK_URL + "?mseq=416298"; // 설정화면 진입시
        public static final String SETTING_LOGIN = WISE_CLICK_URL + "?mseq=416287"; // 로그인 클릭
        public static final String SETTING_LOGOUT = WISE_CLICK_URL + "?mseq=416288"; // 로그아웃 클릭
        public static final String SETTING_KEEP_LOGIN_ON = WISE_CLICK_URL + "?mseq=416291"; // 자동로그인 ON
        public static final String SETTING_KEEP_LOGIN_OFF = WISE_CLICK_URL + "?mseq=416357"; // 자동로그인 OFF
        public static final String SETTING_NA_LOGIN_ON = WISE_CLICK_URL + "?mseq=416292"; // 네이버로그인 ON
        public static final String SETTING_NA_LOGIN_OFF = WISE_CLICK_URL + "?mseq=416358"; // 네이버로그인 OFF
        public static final String SETTING_KA_LOGIN_ON = WISE_CLICK_URL + "?mseq=416293"; // 카카오로그인 ON
        public static final String SETTING_KA_LOGIN_OFF = WISE_CLICK_URL + "?mseq=416359"; // 카카오로그인 OFF
        public static final String SETTING_FP_LOGIN_ON = WISE_CLICK_URL + "?mseq=416294"; // 지문로그인 ON
        public static final String SETTING_FP_LOGIN_OFF = WISE_CLICK_URL + "?mseq=416360"; // 지문로그인 OFF
        public static final String SETTING_PUSH_ON = WISE_CLICK_URL + "?mseq=416295"; // 푸시수신 ON
        public static final String SETTING_PUSH_OFF = WISE_CLICK_URL + "?mseq=416361"; // 푸시수신 OFF
        public static final String SETTING_PUSH_AD_ON = WISE_CLICK_URL + "?mseq=421238"; // 광고수신 ON
        public static final String SETTING_PUSH_AD_OFF = WISE_CLICK_URL + "?mseq=421239"; // 광고수신 OFF
        public static final String SETTING_VERSION_UPDATE = WISE_CLICK_URL + "?mseq=416297"; // 버전업데이트

        //SNS로그인
        public static final String SNS_NA_POPUP_LOGIN = WISE_CLICK_URL + "?mseq=413068"; // 네이버 인증완료 팝업에서 로그인 click
        public static final String SNS_KA_POPUP_LOGIN = WISE_CLICK_URL + "?mseq=413070"; // 카카오 인증완료 팝업에서 로그인 click

        public static final String FINGERPRINT_LOGIN_CLICK = WISE_CLICK_URL + "?mseq=414558"; // 지문등록안내 확인
        public static final String FINGERPRINT_MAPPING = WISE_CLICK_URL + "?mseq=414541"; // 지문 로그인 맵핑 완료
        public static final String FINGERPRINT_LOGIN_SUCCESS = WISE_CLICK_URL + "?mseq=414542"; // 지문 로그인 성공

        public static final String FINGERPRINT_CHECK_AGREE = WISE_CLICK_URL + "?mseq=416703"; // 지문등록안내 확인
        public static final String FINGERPRINT_CHECK_DONT_SEE_AGAIN = WISE_CLICK_URL + "?mseq=416702"; // 지문등록안내 확인

        public static final String LOGIN_ID_PASSWORD_FAILURE = WISE_CLICK_URL + "?mseq=413564"; // 로그인 ID, PASSWORD 불일치
        public static final String LOGIN_PASSWORD_CHANGE_TARGET = WISE_CLICK_URL + "?mseq=413565"; // 비밀번호 강제 변경 대상

        public static final String LOGIN_SHOW = WISE_CLICK_URL + "?mseq=416325"; // 로그인화면 진입시
        public static final String SNS_NA_LOGIN = WISE_CLICK_URL + "?mseq=416114"; // 네이버 로그인 click
        public static final String SNS_KA_LOGIN = WISE_CLICK_URL + "?mseq=416115"; // 카카오 로그인 click
        public static final String FINGERPRINT_LOGIN = WISE_CLICK_URL + "?mseq=416116"; // 지문 로그인 click
        public static final String TV_MEMBERS_LOGIN = WISE_CLICK_URL + "?mseq=416609"; // TV 회원 로그인 click

        public static final String FINGERPRINT_UNHARDWARD = WISE_CLICK_URL + "?mseq=416553"; // 지문 미지원 단말
        public static final String FINGERPRINT_UNREGISTER = WISE_CLICK_URL + "?mseq=416552"; // 지문 미등록 단말

        public static final String PHONE_LOGIN_CLICK = WISE_CLICK_URL + "?mseq=416117"; // 휴대폰 로그인 click
        public static final String LOGIN_CLICK = WISE_CLICK_URL + "?mseq=416118"; // 로그인버튼 클릭시
        public static final String KEEP_LOGIN_UNCHECK = WISE_CLICK_URL + "?mseq=416119"; // 자동로그인 해제 click
        public static final String LOGIN_PW_SHOW = WISE_CLICK_URL + "?mseq=416121"; // 비밀번호 보이기 클릭
        public static final String LOGIN_FIND_ID = WISE_CLICK_URL + "?mseq=416122"; // 아이디 찾기 클릭시
        public static final String LOGIN_FIND_PWD = WISE_CLICK_URL + "?mseq=416123"; // 비밀번호 찾기 클릭시
        public static final String LOGIN_EMAIL_CERT = WISE_CLICK_URL + "?mseq=416124"; // 인증메일 받기 클릭시
        public static final String LOGIN_JOIN_USER = WISE_CLICK_URL + "?mseq=416125"; // 회원가입 클릭시
        public static final String LOGIN_NO_MEM_ORDER = WISE_CLICK_URL + "?mseq=416126"; // 비회원 주문/배송 조회

        //매장
        public static final String TVSHOP_LIVE_PLAY = WISE_CLICK_URL + "?mseq=A00053-C-PLAY"; // TV쇼핑탭 GS SHOP LIVE 플레이버튼 click
        public static final String TVSHOP_DATA_PLAY = WISE_CLICK_URL + "?mseq=A00053-C-DPLAY"; // TV쇼핑탭 GS MY SHOP 플레이버튼 click
        public static final String TVSHOP_DUAL_LIVE_PLAY = WISE_CLICK_URL + "?mseq=A00053-C-2_PLAY"; // TV쇼핑탭 GS SHOP LIVE 플레이버튼 click (dual)
        public static final String TVSHOP_DUAL_DATA_PLAY = WISE_CLICK_URL + "?mseq=A00053-C-2_DPLAY"; // TV쇼핑탭 GS MY SHOP 플레이버튼 click (dual)
        public static final String BESTSHOP_LIVE_PLAY = WISE_CLICK_URL + "?mseq=A00054-C-PLAY";// 오늘추천 LIVE 플레이버튼 click
        public static final String BESTSHOP_DATA_PLAY = WISE_CLICK_URL + "?mseq=A00054-C-DPLAY"; // 오늘추천 GS MY SHOP 플레이버튼 click
        public static final String BESTSHOP_DUAL_LIVE_PLAY = WISE_CLICK_URL + "?mseq=A00054-C-2_PLAY"; // 오늘추천 GS SHOP LIVE 플레이버튼 click (dual)
        public static final String BESTSHOP_DUAL_DATA_PLAY = WISE_CLICK_URL + "?mseq=A00054-C-2_DPLAY"; // 오늘추천 GS MY SHOP 플레이버튼 click (dual)

        //라이브톡, 날방
        public static final String LIVETALK_ROLLING_BANNER = WISE_CLICK_URL + "?mseq=A00053-B-10002"; // 라이브톡 롤링텍스트 click
        public static final String LIVETALK_PLAY = WISE_CLICK_URL + "?mseq=408802"; // 라이브톡 플레이버튼 click
        public static final String LIVETALK_SNS = WISE_CLICK_URL + "?mseq=409211"; // 라이브톡 공유버튼 click
        public static final String LIVETALK_SCREEN_VIEW = WISE_URL + "?mseq=408799"; // 라이브톡 화면 노출시 (WISE_CLICK_URL 사용이 정상이나, 잘못 배포되어 서버에서 예외처리 했음)
        public static final String NALBANG_SNS = WISE_CLICK_URL + "?mseq=409972"; // 날방 공유버튼 click
        public static final String NALBANG_LIVE_PLAY = WISE_CLICK_URL + "?mseq=409973"; // 날방 생방 플레이버튼 click
        public static final String NALBANG_VOD_PLAY = WISE_CLICK_URL + "?mseq=409974"; // 날방 VOD 플레이버튼 click

        //숏방전용 와이즈로그 주소
        public static final String WISE_SHORTBANG_URL = getWebRoot() + "/app/static/shortbang";
        //숏방상세
        public static final String SHORTBANG_HOME = WISE_SHORTBANG_URL + "?mseq=410375"; // 숏방 좌상단 로고 click
        public static final String SHORTBANG_TITLE = WISE_SHORTBANG_URL + "?mseq=410376"; // 숏방 중앙상단 타이틀 click
        public static final String SHORTBANG_CART = WISE_SHORTBANG_URL + "?mseq=410377"; // 숏방 우상단 장바구니 click
        public static final String SHORTBANG_CATEGORY = WISE_SHORTBANG_URL + "?mseq=410378"; // 숏방 우상단 카테고리 click
        public static final String SHORTBANG_SOUND_OFF = WISE_SHORTBANG_URL + "?mseq=410381"; // 숏방 Sound Off click
        public static final String SHORTBANG_SOUND_ON = WISE_SHORTBANG_URL + "?mseq=410382"; // 숏방 Sound On click
        public static final String SHORTBANG_SNS = WISE_SHORTBANG_URL + "?mseq=410384"; // 숏방 공유버튼 click
        //숏방공유하기팝업
        public static final String SHORTBANG_SNS_KAKAOTALK = WISE_SHORTBANG_URL + "?mseq=410385"; // 숏방 공유팝업내 카카오톡 click
        public static final String SHORTBANG_SNS_KAKAOSTORY = WISE_SHORTBANG_URL + "?mseq=410386"; // 숏방 공유팝업내 카카오스토리 click
        public static final String SHORTBANG_SNS_LINE = WISE_SHORTBANG_URL + "?mseq=410387"; // 숏방 공유팝업내 라인 click
        public static final String SHORTBANG_SNS_SMS = WISE_SHORTBANG_URL + "?mseq=410388"; // 숏방 공유팝업내 SMS click
        public static final String SHORTBANG_SNS_FACEBOOK = WISE_SHORTBANG_URL + "?mseq=410389"; // 숏방 공유팝업내 페이스북 click
        public static final String SHORTBANG_SNS_TWITTER = WISE_SHORTBANG_URL + "?mseq=410390"; // 숏방 공유팝업내 트위터 click
        public static final String SHORTBANG_SNS_PINTEREST = WISE_SHORTBANG_URL + "?mseq=410391"; // 숏방 공유팝업내 핀터레스트 click
        public static final String SHORTBANG_SNS_URL = WISE_SHORTBANG_URL + "?mseq=410392"; // 숏방 공유팝업내 URL복사 click
        public static final String SHORTBANG_SNS_ETC = WISE_SHORTBANG_URL + "?mseq=410394"; // 숏방 공유팝업내 다른앱보기 click
        //숏방카테고리
        public static final String SHORTBANG_CATEGORY_VIDEO = WISE_SHORTBANG_URL + "?mseq=410401"; // 숏방 카테고리 동영상 click
        public static final String SHORTBANG_CATEGORY_CLOSE = WISE_SHORTBANG_URL + "?mseq=410402"; // 숏방 카테고리 닫기 click

        //TV 편성표 클릭 효율
        //네비게이션 영역 5
        public static final String SCH_NAVI_TODAY = WISE_CLICK_URL + "?mseq=A00323-C_SCH[TAIL]-TODAY"; //편성표 네비 오늘 클릭
        public static final String SCH_NAVI_PRE_DAY = WISE_CLICK_URL + "?mseq=A00323-C_SCH[TAIL]-PD_"; //편성표 네비 어제 클릭 1~부터
        public static final String SCH_NAVI_NEXT_DAY = WISE_CLICK_URL + "?mseq=A00323-C_SCH[TAIL]-ND_"; // 편성표 네비 내일 클릭 1~부터

        //생방송인 상품인 경우 7
        public static final String SCH_LIVE_MAINPRD_VIDEO_PLAY = WISE_CLICK_URL + "?mseq=A00323-L_L[TAIL]-PLAY"; // 생방송 메인상품 영상 재생 버튼 클릭
        public static final String SCH_LIVE_MAINPRD_VIDEO_PAUSE = WISE_CLICK_URL + "?mseq=A00323-L_L[TAIL]-STOP"; //생방송 메인상품 영상 일시정지 버튼 클릭
        public static final String SCH_LIVE_MAINPRD_ALRAM_REG = WISE_CLICK_URL + "?mseq=A00323-L_L[TAIL]-AR"; //생방송 메인상품 알람등록 버튼 클릭
        public static final String SCH_LIVE_MAINPRD_ALRAM_CANCEL = WISE_CLICK_URL + "?mseq=A00323-L_L[TAIL]-AC"; //생방송 메인상품 알람취소 버튼 클릭
        public static final String SCH_LIVE_MAINPRD_LIVETALK = WISE_CLICK_URL + "?mseq=A00323-L_L[TAIL]-LIVET"; //생방송 메인상품 라이브톡 버튼 클릭
        public static final String SCH_LIVE_SUBPRD_ALRAM_REG = WISE_CLICK_URL + "?mseq=A00323-L_L[TAIL]-SAR"; //생방송 복수 상품 알람등록 버튼 클릭
        public static final String SCH_LIVE_SUBPRD_ALRAM_CANCEL = WISE_CLICK_URL + "?mseq=A00323-L_L[TAIL]-SAC"; //생방송 복수 상품 알람취소 버튼 클릭

        //생방송이 아닌 상품인 경우 4
        public static final String SCH_MAINPRD_ALRAM_REG = WISE_CLICK_URL + "?mseq=A00323-L_LETC[TAIL]-AR"; //메인상품 알람등록 버튼 클릭
        public static final String SCH_MAINPRD_ALRAM_CANCEL = WISE_CLICK_URL + "?mseq=A00323-L_LETC[TAIL]-AC"; //메인상품 알람취소 버튼 클릭
        public static final String SCH_SUBPRD_ALRAM_REG = WISE_CLICK_URL + "?mseq=A00323-L_LETC[TAIL]-SAR"; //복수 상품 알람등록 버튼 클릭
        public static final String SCH_SUBPRD_ALRAM_CANCEL = WISE_CLICK_URL + "?mseq=A00323-L_LETC[TAIL]-SAC"; //복수 상품 알람취소 버튼 클릭

        //오른쪽 리스트 4
        public static final String SCH_RIGHT_ONAIR_PRD = WISE_CLICK_URL + "?mseq=A00323-L_S[TAIL]-LP"; //오른쪽 생방송 상품 클릭

        //부상품 더보기/닫기
        public static final String SCH_SUBPRD_EXPAND = WISE_CLICK_URL + "?mseq=A00323-C_SCH[TAIL]-SOPEN"; //복수 상품 더보기
        public static final String SCH_SUBPRD_COLLAPSE = WISE_CLICK_URL + "?mseq=A00323-C_SCH[TAIL]-SCLOSE"; //복수 상품 닫기

        //편성표 전환버튼
        public static final String SCH_LIVE_CLICK = WISE_CLICK_URL + "?mseq=A00323-SWT-L"; //라이브 편성표
        public static final String SCH_MYSHOP_CLICK = WISE_CLICK_URL + "?mseq=A00323-SWT-D"; //마이샵 편성표

        //푸시유도팝업
        public static final String PUSH_MAIN = WISE_CLICK_URL + "?mseq=415588"; // 팝업 노출
        public static final String PUSH_MAIN_OK = WISE_CLICK_URL + "?mseq=415589"; // 팝업 확인
        public static final String PUSH_MAIN_CANCEL = WISE_CLICK_URL + "?mseq=415590"; // 팝업 취소
        public static final String PUSH_POPUP = WISE_CLICK_URL + "?mseq=415591"; // 팝업내 얼랏 노출
        public static final String PUSH_POPUP_OK = WISE_CLICK_URL + "?mseq=415592"; // 팝업내 얼랏 확인
        public static final String PUSH_POPUP_CANCEL = WISE_CLICK_URL + "?mseq=415593"; // 팝업내 얼랏 취소

        // GS Fresh
        public static final String GS_SUPER_MORE_PRD = WISE_CLICK_URL + "?mseq=A00481-LM-1"; // 상품 더보기
        public static final String GS_SUPER_VIEW_ALL = WISE_CLICK_URL + "?mseq=A00481-BA-1"; // 전체보기
        public static final String GS_SUPER_GO_BRANDSHOP = WISE_CLICK_URL + "?mseq=A00481-BA-2"; // 매장 바로가기
        //public static final String GS_SUPER_GO_CART = WISE_CLICK_URL + "?mseq=A00481-GC-1"; // 장바구니 가기
        public static final String GS_SUPER_GO_CART = WISE_CLICK_URL + "?mseq=418824";

        // 모바일라이브
        public static final String MOBILE_LIVE_PRD_VIEW = WISE_CLICK_URL + "?mseq=417320"; // 상품보기
        public static final String MOBILE_LIVE_CHAT_VIEW = WISE_CLICK_URL + "?mseq=417319"; // 채팅보기
        public static final String MOBILE_LIVE_CHAT_CLICK = WISE_CLICK_URL + "?mseq=417318"; // 채팅하기(입력창노출)
        public static final String MOBILE_LIVE_CHAT_NEW = WISE_CLICK_URL + "?mseq=417317"; // 최근채팅글이동
        public static final String MOBILE_LIVE_ENTER = WISE_CLICK_URL + "?mseq=418019"; // 진입
        public static final String MOBILE_LIVE_PLAY_FROM_BANNER = WISE_CLICK_URL + "?mseq=A00054-C-MPLAY";
        public static final String MOBILE_LIVE_FULL_SCREEN = WISE_CLICK_URL + "?mseq=A00054-C-MWIMG";
        public static final String MOBILE_LIVE_SOUND = WISE_CLICK_URL + "?mseq=420591";
        public static final String MOBILE_LIVE_ALARM = WISE_CLICK_URL + "?mseq=420635";
        public static final String MOBILE_LIVE_MORE_CLICK = WISE_CLICK_URL + "?mseq=420991";
        public static final String MOBILE_LIVE_PRD_LIST_CLICK = WISE_CLICK_URL + "?mseq=420995";

        //모바일라이브 공유하기
        public static final String MOBILE_LIVE_SHARE_BUTTON = WISE_CLICK_URL + "mseq=420610";
        public static final String MOBILE_LIVE_SHARE_KAKAOTALK = WISE_CLICK_URL + "mseq=420613";
        public static final String MOBILE_LIVE_SHARE_KAKAOSTORY = WISE_CLICK_URL + "mseq=420614";
        public static final String MOBILE_LIVE_SHARE_LINE = WISE_CLICK_URL + "mseq=420615";
        public static final String MOBILE_LIVE_SHARE_SMS = WISE_CLICK_URL + "mseq=420616";
        public static final String MOBILE_LIVE_SHARE_FACEBOOK = WISE_CLICK_URL + "mseq=420617";
        public static final String MOBILE_LIVE_SHARE_TWITTER = WISE_CLICK_URL + "mseq=420618";
        public static final String MOBILE_LIVE_SHARE_URL = WISE_CLICK_URL + "mseq=420619";

        //모바일라이브 탭매장 신설 - 방송알림효율
        public static final String MOBILE_LIVE_ALARM_CLICK = WISE_CLICK_URL + "?mseq=A00577-M_AR"; //방송알림버튼 선택시
        public static final String MOBILE_LIVE_ALARM_ADD = WISE_CLICK_URL + "?mseq=420489"; //방송알림 등록팝업 -> 등록버튼
        public static final String MOBILE_LIVE_ALARM_DELETE = WISE_CLICK_URL + "?mseq=420490"; //방송알림 취소팝업 -> 취소확인버튼


        // 매장 순서 편집
        public static final String PERSONAL_TAB_ENTER = WISE_CLICK_URL + "?mseq=417309"; // 개인화탭 화면 진입
        public static final String PERSONAL_TAB_SELECT = WISE_CLICK_URL + "?mseq=417370"; // 개인화탭 리스트 선택(Long Press)
        public static final String PERSONAL_TAB_SHOW_DEFAULT = WISE_CLICK_URL + "?mseq=417311"; // 개인화탭 리스트 선택(Long Press)
        public static final String PERSONAL_TAB_SAVE = WISE_CLICK_URL + "?mseq=417310"; // 개인화탭 리스트 선택(Long Press)

        //VOD 매장 플레이어
        public static final String VOD_BRIGHTCOVE_PLAYER = WISE_CLICK_URL + "?mseq=A00492-V-CTL&bhrGbn=bcPlayer_#ACTION&vid=#VID&autoplay=#AUTOPLAY&pt=#PT&tt=#TT&prdid=#PRDID";
        public static final String LIVE_BRIGHTCOVE_PLAYER = WISE_CLICK_URL + "?mseq=A00492-C-CTL&bhrGbn=bcPlayer_#ACTION&vid=#VID&autoplay=#AUTOPLAY&pt=#PT&tt=#TT&prdid=#PRDID";
        public static final String VOD_GOPRD_CLICK = WISE_CLICK_URL + "?mseq=A00492-V-PRD";

        //단품 MSEQ : 영상
        public static final String MSEQ_PRD_NATIVE_VOD = WISE_CLICK_URL + "?bhrGbn=bcPlayer_#ACTION&prdid=#PRDID&vid=#VID&mseq=#MSEQ&pt=#PT&tt=#TT&ref=#REF";
        public static final String MSEQ_PRD_NATIVE_LIVE = WISE_CLICK_URL + "?bhrGbn=Live_#ACTION&mseq=#MSEQ&prdid=#PRDID&ref=#REF";
        // 단품 MSEQ : 그 외
        public static final String MSEQ_PRD_NATIVE_CAL_ALREADY = WISE_CLICK_URL + "?mseq=397024"; // 미리계산
        public static final String MSEQ_PRD_NATIVE_SHARE = WISE_CLICK_URL + "?mseq=397081"; // 공유하기
        public static final String MSEQ_PRD_NATIVE_ZZIM = WISE_CLICK_URL + "?mseq=397029"; // 찜하기
        public static final String MSEQ_PRD_NATIVE_GO_SAVE_SHIPPING = WISE_CLICK_URL + "?mseq=418865"; // 배송비절약매장 가기
        public static final String MSEQ_PRD_NATIVE_HEADER_BASKET = WISE_CLICK_URL + "?mseq=398045"; // 헤더 장바구니
        public static final String MSEQ_PRD_NATIVE_HEADER_HOME = WISE_CLICK_URL + "?mseq=409206"; // 헤더 홈
        public static final String MSEQ_PRD_NATIVE_HEADER_SEARCHING = WISE_CLICK_URL + "?mseq=398044"; // 헤더 검색
        public static final String MSEQ_PRD_NATIVE_ALARM = WISE_CLICK_URL + "?mseq=397031"; // 방송알림
        public static final String MSEQ_DEAL_NATIVE_HEADER_BASKET = WISE_CLICK_URL + "?mseq=418920"; // 헤더 장바구니 (딜)
        public static final String MSEQ_DEAL_NATIVE_HEADER_HOME = WISE_CLICK_URL + "?mseq=408522"; // 헤더 홈 (딜)
        public static final String MSEQ_DEAL_NATIVE_HEADER_SEARCHING = WISE_CLICK_URL + "?mseq=418921"; // 헤더 검색 (딜)
        public static final String MSEQ_DEAL_NATIVE_SHARE = WISE_CLICK_URL + "?mseq=397207"; // 공유하기 (딜)
        public static final String MSEQ_DEAL_NATIVE_ZZIM = WISE_CLICK_URL + "?mseq=408656"; // 찜하기 (딜)
        public static final String MSEQ_DEAL_NATIVE_ALARM = WISE_CLICK_URL + "?mseq=411083"; // 방송알림 (딜)

        //시그니처매장 플레이어
        public static final String MSEQ_SIGNATURE_VOD = WISE_CLICK_URL + "?bhrGbn=bcPlayer_#ACTION&vid=#VID&mseq=A00538-V-PLAY&pt=#PT&tt=#TT";

        //시그니처매장 플레이어
        public static final String MSEQ_PRD_VOD_LIST = WISE_CLICK_URL + "?bhrGbn=bcPlayer_#ACTION&vid=#VID&mseq=A00564-V-PLAY&pt=#PT&tt=#TT";

        //이미지 검색
        public static final String IMAGE_SEARCH_TAKE_PHOTO = WISE_CLICK_URL + "?mseq=418140"; // 이미지 검색시 사진 찍기

        //renewal 효율코드
        public static final String RN_BESTSHOP_LIVE_TALK = WISE_CLICK_URL + "?mseq=A00054-C_LIVE-TALK"; // 홈 LIVE 라이브톡 click
        public static final String RN_SCH_LIVE_TALK = WISE_CLICK_URL + "?mseq=A00323-C_LIVE-TALK"; // 편성표 LIVE 라이브톡 click
        public static final String RN_SCH_LIVE_ALRAM = WISE_CLICK_URL + "?mseq=A00323-C_LIVE-AR"; // 편성표 LIVE 방송알림 click
        public static final String RN_SCH_DATA_ALRAM = WISE_CLICK_URL + "?mseq=A00323-C_MYSHOP-AR"; // 편성표 MY SHOP 방송알림 click
        public static final String RN_SCH_VOD_ALRAM = WISE_CLICK_URL + "?mseq=A00323-V-AR"; // 편성표 VOD 방송알림 click

        // GSX 브랜드 개인화 효율코드
        public static final String MSEQ_GSX_BRAND_PERSONAL = WISE_CLICK_URL + "?mseq=419574"; // GSX브랜드에서 개인화 탭 Click

        // 홈 C_SQ (홈 개인화 이런상품 어떠세요 슬라이드)
        public static final String MSEQ_HOME_C_SQ = WISE_CLICK_URL + "?mseq=419669";
        public static final String MSEQ_HOME_C_SQ_SCROLLED = WISE_CLICK_URL + "?mseq=419670";

        //홈 MOLOCO_PRD_C_SQ (몰로코 이런상품 어떠세요)
        public static final String MSEQ_HOME_MOLOCO_C_SQ = WISE_CLICK_URL + "?mseq=420082"; //진입시
        public static final String MSEQ_HOME_MOLOCO_C_SQ_SCROLLED = WISE_CLICK_URL + "?mseq=420084"; //스크롤시

        //모듈화 컨텐츠 AB테스트
        public static final String MSEQ_VIEW_CONTENTS_AB = WISE_CLICK_URL + "?mseq=420505"; //실험대상상품 단품페이지 진입시
        public static final String MSEQ_CLICK_CONTENTS_AB = WISE_CLICK_URL + "?mseq=420506"; //실험대상상품 재생버튼 클릭시

        public static final String MSEQ_CLICK_INAPP_MESSAGE = WISE_CLICK_URL + "?mseq=421361";

        /**
         * 검색 AB 추가를 위한 파리미터 정보
         */
        public static final String SEARCH_ABPARAM = "&ab=";
        /**
         * 직접 검색
         */
        public static final String SEARCH_DIRECT = "&mseq=401172";
        /**
         * 음성 검색
         */
        public static final String SEARCH_VOICE = "&mseq=403460";
        /**
         * 음성 검색
         */
        public static final String SEARCH_QRCODE = "&mseq=403461";
        /**
         * 인기 검색어 검색 시
         */
        public static final String SEARCH_POPULAR = "&mseq=402848";

        /**
         * 추천 검색어 검색 시
         */
        public static final String SEARCH_RECOMMAND = "&mseq=419271";

        /**
         * 메임검색창 연관검색어 돋보기로 검색시
         */
        public static final String SEARCH_MAIN_RECOMMAND = "&mseq=419321";

        /**
         * 최근 검색어 검색 시
         */
        public static final String SEARCH_RECENT = "&mseq=401170";
        /**
         * 자동완성 검색 시
         */
        public static final String SEARCH_AUTO = "&mseq=401171";

        /**
         * 프로모션 검색 시
         */
        public static final String SEARCH_PROMOTION = "&mseq=403589";

        /**
         * 고객 센터 웹 링크
         */
        public static final String CUSTOMER_CENTER = getWebRoot() + "/mygsshop/customerCenter.gs"
                + FROM_APP_TYPE1;

        /**
         * 하단 풋터 이용약관 URL
         */
        public static final String HOME_FOOTER_MANUAL = getWebRoot() + "/m/mygsshop/articleMain.gs"
                + FROM_APP_TYPE1; // 이용약관
        /**
         * 개인정보 취급방침
         */
        public static final String HOME_FOOTER_MANUAL_PRIVATE = getWebRoot()
                + "/m/mygsshop/articleProtect.gs?div=person&gsid=MCfooter" + FROM_APP_TYPE2;


        /**
         * 하단 풋터 공지사항
         */
        public static final String HOME_FOOTER_NOTICE = getWebRoot() + "/mygsshop/notice.gs?mseq=411855";

        /**
         * 하단 풋터 고객서비스
         */
        public static final String HOME_FOOTER_CUSTOMER_SERVICE = getWebRoot() + "/m/mygsshop/qnaMain.gs?mseq=411850";

        /**
         * 청소년 보호정책
         */
        public static final String HOME_FOOTER_MANUAL_TEEN = getWebRoot()
                + "/m/mygsshop/articleProtect.gs?div=youth&gsid=MCfooter" + FROM_APP_TYPE2;

        /**
         * 사업자정보확인
         */
        public static final String TRADE_COMMISSION_INFO = "https://www.ftc.go.kr/bizCommPop.do?wrkr_no=1168118745";

        /**
         * 기업은행 채무지급 보증안내
         */
        public static final String DEBT_GUARANTEE_INFO = getWebRoot() + "/mobile/etc/etc_loan.jsp" // 모달변경할 경우 /mobile/etc/etc_loan.gs 로 변경해야함
                + FROM_APP_TYPE1;

        /**
         * 회원 탈퇴
         */
        public static final String MEMBER_LEAVE = getWebRoot()
                + "/member/personInfoManagement.gs?isWthdr=Y" + FROM_APP_TYPE2;

        /**
         * GS사이렌을 울려라
         */
        public static final String EMP_CIREN = getWebRoot() + "/event/siren/empSirenDeclr.jsp";

        /**
         * tv 편성표 검색 주소
         */
        public static final String TV_SCHEDULE_SEARCH_URL = getWebRoot() + "/section/broad/broadSchedule/search/LIVE/";

        /**
         * CDN_PARKING_URL 공사중 URL
         */
        public static final String CDN_PARKING_URL = "http://apperror.gsshop.com/mc_parking.html";

        /**
         * 숏방 게이트웨이
         */
        public static final String SHORTBANG_GATEWAY = getWebRoot() + "/main/shortbang/view";

        /**
         * 모바일 생활 백서
         */
        public static final String MOBILE_FIRSTBUY_DIR = "http://event.gsshop.com/event/2015_03/apply_firstBuy_movie.jsp";

        /**
         * 패밀리 사이트https 10x10
         */
        //public static final String FAMILY_10 = "https://m.10x10.co.kr/apps/link/gate.asp";
        public static final String FAMILY_10 = "https://m.10x10.co.kr";

        /**
         * gs&point
         */
        public static final String FAMILY_NPOINT = "https://m.gsnpoint.com";

        /**
         * 탭아이디로 매장 이동용 주소
         */
        public static final String MOVE_SHOP_FROM_TABID_URL = getWebRoot() + "/index.gs?tabId=";

        /**
         * 네이티브 단품액티비티의 하단 웹영역
         */
        public static final String NATIVE_BOTTOM_FILE_NAME = "nativeApp.gs";
        public static final String NATIVE_PRODUCT_BOTTOM = getWebRoot() + "/product/" + NATIVE_BOTTOM_FILE_NAME + "?appH=&" + REST.NATIVE_PRODUCT_API_VER + "&";
        public static final String NATIVE_DEAL_BOTTOM = getWebRoot() + "/deal/" + NATIVE_BOTTOM_FILE_NAME + "?appH=&" + REST.NATIVE_PRODUCT_API_VER + "&";
    }

    /**
     * REST 통신 주소
     */
    public static class REST {
        public static final String API_PATH = "/apis/v2.6";
        public static final String API_PATH_V30 = "/apis/v3.0"; // 통합포인트 로그인을 위한 api path 버전
        public static final String API_PATH_SHOP = "/shop";

        /**
         * 네이티브 단품 API 버전
         */
        public static final String NATIVE_PRODUCT_API_VER = "ver=1.1";

        public static final String HTTP_PREFIX = getHttpRoot();
        //사용처가 없음 주석 0322 leems 살릴떄 다른 영역을 고려해야함
        //public static final String HTTPS_PREFIX = HTTPS_ROOT;
        // -------- 이하 PREFIX 주소를 제외한 나머지 주소만 적음

        /**
         * 설정 정보 조회
         */
        public static final String USER_SETTING = "/member/setting";

        /**
         * 상품평 조회(신규/수정)
         */
        // 2013.12.23 parksegun 신규조회
        public static final String REVIEW_READ_INIT = "/knownew/estimate/estimateWrite.gs";
        // 2013.12.30 parksegun 수정조회
        public static final String REVIEW_READ_EDIT = "/knownew/estimate/estimateModify.gs";

        /**
         * 상품평 저장
         */
        // 2013.12.30 parksegun 신규 저장
        public static final String REVIEW_NEW_SAVE = "/knownew/estimate/estimateWriteProc.gs?format=json";
        // 2013.12.31 parksegun 업데이트
        public static final String REVIEW_UPDATE = "/knownew/estimate/estimateModifyProc.gs?format=json";

        /**
         * 연관 검색어 목록. ?query=검색어
         * , value = "/apis/v2.8"
         */
        public static final String RELATED_KEYWORD_LIST = "/section/apis/v2.8/search/autoComplete";

        /**
         * 인기 검색어 목록.
         */
        public static final String POPULAR_KEYWORD_LIST = "/apis/v2.6/search/hotKeyword?rownum=20";

        /**
         * 검색 A/B 상태 가져오기
         */
        public static final String SEARCH_AB = "/section/search/abTestValue";

        /**
         * 뱃지 정보.
         */
        public static final String BADGE = "/tabbar/badge/list";

        /**
         * 토큰기반 자동로그인 : 신규 로그인 2014.01.17 parksegun EC통합 재구축 URL 변경 AS-IS URL: /member/loginToken
         */
        public static final String LOGIN_FOR_TOKEN = "/customer/token-login";

        /**
         * 토큰기반 자동로그인 : 2020.01.20 yun. 테스트용 앱에서 서버정보 변경 후 로그인 액션때문에 만든 테스트용 상수.
         */
        public static final String LOGIN_FOR_TOKEN_FOR_M = "https://m.gsshop.com/customer/token-login";
        public static final String LOGIN_FOR_TOKEN_FOR_SM21 = "https://asm.gsshop.com/customer/token-login";
        public static final String LOGIN_FOR_TOKEN_FOR_TM14 = "https://tm14.gsshop.com/customer/token-login";
        public static final String LOGIN_FOR_TOKEN_FOR_ATM = "https://atm.gsshop.com/customer/token-login";
        public static final String LOGIN_FOR_TOKEN_FOR_AM = "https://asm.gsshop.com/customer/token-login";

        /**
         * 디바이스아이디 기반 자동로그인
         */
        public static final String LOGIN_FOR_TOKEN_WITH_DEVICE = "/customer/token-login-reg";

        /**
         * 토큰기반 자동로그인 : 로그아웃 2014.01.16 parksegun EC통합 재구축 URL 변경 AS-IS URL: /member/logoutToken
         */
        public static final String LOGOUT_FOR_TOKEN = "/customer/token-log-out";

        /**
         * 토큰기반 자동로그인 : 토큰을 통한 인증 2014.01.16 parksegun EC통합 재구축 URL 변경 AS-IS URL: /member/authToken
         */
        public static final String AUTH_BY_TOKEN = "/customer/token-certification";

        /**
         *  SSO 사용여부 조회
         */
        public static final String SSO_USE_QUERY = "/customer/sso-use-yn-qry";

        /**
         *  SSO 사용여부 설정
         */
        public static final String SSO_USE_SET = "/customer/sso-use-yn-set";

        /**
         * sns 로그인 link 정보
         */
        public static final String SNS_LINK_LIST = "/customer/cust-sns-link";

        /**
         * sns 로그인 unlink
         */
        public static final String SNS_UNLINK = "/customer/cust-sns-link-close";

        /**
         * sns 로그인 link
         */
        public static final String SNS_LINK = "/customer/cust-sns-link-open";

        /**
         * 홈 구조 API
         * Navigation 버전 수정되었을 때 수정 포인트 hklim
         */
        public static final String NAVIGATION_VER = "10.6";
        public static String TEMP_NAVIGATION_VER;

        public static String getNavigationVer() {
            if (TextUtils.isEmpty(TEMP_NAVIGATION_VER)) {
                return NAVIGATION_VER;
            } else {
                return TEMP_NAVIGATION_VER;
            }
        }

        public static String getHomeNavigation() {
            return "/app/navigation?version=" + ServerUrls.REST.getNavigationVer();
        }

        /**
         * 오픈 데이트 포맷
         */
        public static final String OPEN_DATE_FORMAT = "openDate=";

        /**
         * 생방송 타임 포맷
         */
        public static final String BRD_TIME_FORMAT = "brdTime=";
        /**
         * Promotion 조회 (테스트시 ?today=20140624 추가하여 호출)
         */
        public static final String PROMOTION = "/shop/mobilePromotionInfo";

        /**
         * 홈 검색창 추천연관검색어
         */
        public static final String RECENT_RECOMMAND = "/app/shop/main/search/extentionKeyWord";

        /**
         * 오늘 추천등 자동플레이 동영상 auto y/n 설정
         */
        public static final String VIDEO_AUTO_YN = "/app/log/videoAutoPlay";

        /**
         * 인트로 이미지 교체를 위한 이미지
         */
        public static final String INTRO_IMAGE_API = "/app/intro/image";

        /**
         * /app/log/build/info/check // 체크
         * <p>
         * /app/log/build/info/add?appGbn=01{@literal &}verNo=150{@literal &}verNm=5.6.0{@literal &}signVal=AAAAAA{@literal &}hashVal=BBBBBB  추가
         * ex)xxxx :9999/app/log/build/info/add?appGbn=01{@literal &}verNo=150{@literal & }verNm=5.6.0{@literal &}signVal=AAAAAA{@literal &}hashVal=BBBBBB
         * *
         * /app/log/build/info/list // 조회
         * <p>
         * /app/log/build/info/delete?appGbn=01{@literal &}verNo=150
         * <p>
         * check 기능
         * <p>
         * insert 추가 예정
         */
        public static final String BUILD_LOG_CHECK_API = "/app/log/build/info/check";

        /**
         * 사용자 등급정보
         */
        public static final String CUSTOMER_GRADE = "/app/customer/rlmemshp.gs";

        /**
         * 인트로화면에서 접근권한 고지 확인
         */
        public static final String AUTH_CONFIRM = "/app/main/personalChk";

        /**
         * TV편성표 OnAir 남은시간 표시용
         */
        public static final String TV_SCHEDULE_LIVE_BROAD_INFO = "/main/liveBroadInfo";

        /**
         * TV편성표 방송알림 조회/등록/제거
         */
        public static final String TV_SCHEDULE_ALARM_QUERY = "/app/broad/alarm/addPage";
        public static final String TV_SCHEDULE_ALARM_ADD = "/app/broad/alarm/add";
        public static final String TV_SCHEDULE_ALARM_DELETE = "/app/broad/alarm/selectDelete";

        /**
         * 모바일라이브 탭매장 신설 방송알림 조회/등록/제거
         */
        public static final String MOBILE_LIVE_ALARM_ADD_QUERY = "/app/mobilelive/alarm/addPage";
        public static final String MOBILE_LIVE_ALARM_ADD = "/app/mobilelive/alarm/add";
        public static final String MOBILE_LIVE_ALARM_DELETE = "/app/mobilelive/alarm/selectDelete";
        public static final String MOBILE_LIVE_ALARM_DELETE_QUERY = "/app/mobilelive/alarm/deletePage";

        /**
         * 비밀번호변경 그만보기 전송용
         */
        public static final String NOTICE_UPDATE_PASS = "/app/customer/saveNtcCnfDPwd";

        /**
         * TV 회원 비밀번호 변경 그만보기
         */
        public static final String NOTICE_UPDATE_TV_MEMBERS_PASS = "/member/updatentccnfdtvpwd.gs";

        /**
         * 네이티브 단품정보
         */
        public static final String NATIVE_PRODUCT = "/product/api/";

        /**
         * 네이티브 딜정보
         */
        public static final String NATIVE_DEAL = "/deal/api/";

        /**
         * 날방 톡 전송
         */
        public static final String NALBANG_TALK_WRITE = "/app/section/nalbang/talk/{talkNo}/write";

        /**
         * 라이브톡 톡 전송
         */
        public static final String LIVETALK_TALK_WRITE = "/app/section/livetalk/talk/{talkNo}/write";

        /**
         * 푸시 등록
         */
        public static final String PUSH_REGISTER = API_PATH_SHOP + "/push/register";

        /**
         * Hyper Personalized Curation
         * rcm타입 포맷
         * rcmtype(PRD, CART)
         */
        public static final String RCMTYPE = "rcmtype=";

        /**
         * Hyper Personalized Curation
         * lastprdid(최근본상품  prdid)
         */
        public static final String LASTPRD = "lastprdid=";

        /**
         * Hyper Personalized Curation 케이스
         * R4(단품 -> 홈)
         * R5(장바구니 -> 홈)
         * S1(지금베스트탭에 스트리밍)
         */
        public static final String HyperPersonalizedUrl = "/app/main/person/dtRcmd?";
    }

    /**
     * 웹 소켓 관련 주소들
     */
    public static class WS {
        /**
         * CSP 브레이크용
         * 이와 같은 모양이며
         * 해당 버전보다 높을때만 동작하도록
         * 빌드 환경 확인
         * m : http://image.gsshop.com/ui/gsshop/event
         * sm21 :  http://image.gsshop.com/ui/stage/gsshop/event
         * {"status":"OK","AOS":205,"IOS":189}
         */
        public static final String CSP_BREAK_URL = CSP_BREAK_ROOT + "/gsshop/event/csp/check.html";
        public static final String CSP_SERVICE_URL = CSP_SERVICE_ROOT + "/global";
        public static final String CSP_CHAT_SERVICE_URL = CSP_SERVICE_ROOT + "/chat";
    }


    /**
     * 웹 - 앱 호출 주소
     */
    public static class APP {

        // ------- http로 시작하지 않고 아래 문자열로 시작하는 주소(소문자로 비교)
        public static final String TOAPP = "toapp://";

        public static final String BACK = TOAPP + "back";
        public static final String REVIEW = TOAPP + "estimate";// 상품평 쓰기 화면
        public static final String FILEATTACH = TOAPP + "attach";// 파일첨부기능 포함 화면 (이벤트, 쇼미더카페 등)
        public static final String LOGIN = TOAPP + "login"; // 로그인 화면
        public static final String AUTO_LOGIN = TOAPP + "toautologin"; // 자동로그인 수행
        public static final String SETTING = TOAPP + "setting";// 환경설정 화면
        public static final String MODAL_WEB = TOAPP + "modal";// 모달 웹액티비티
        public static final String EXT_MODAL_WEB = TOAPP + "extmodal";// 모달 웹액티비티 (타이틀 추가)
        public static final String FULL_MODAL_WEB = TOAPP + "fullwebview";// 타이틀 영역 없는 모달 웹액티비티
        public static final String NOTAB_FULL_MODAL_WEB = TOAPP + "notabfullweb";// 타이틀 영역 없는 모달 웹액티비티
        public static final String CLOSE = TOAPP + "close";// 현재 액티비티 종료
        public static final String LOGOUT = TOAPP + "logout";
        public static final String EXTERNAL_WEB = TOAPP + "browser"; // 브라우저로 웹페이지 열기
        public static final String VOD = TOAPP + "vod"; // VOD 동영상 재생
        public static final String DEALVOD = TOAPP + "dealvod"; // DEAL VOD 동영상 재생
        public static final String BASEVOD = TOAPP + "basevod"; // 기본 VOD 동영상 재생 ( 상품 연계 아님 )
        public static final String LIVE_STREAMING = TOAPP + "livestreaming"; // live streaming 동영상
        public static final String OUTSITE_MODAL_WEB = TOAPP + "outsitemodal";// 모달 웹액티비티 (타이틀 추가)
        public static final String PHOTOEDIT = TOAPP + "photoedit"; // 이미지 검색 크롭

        // 재생

        public static final String ISP_MOBILE = "ispmobile://"; // ISP결제 앱 호출
        public static final String KAKAO_MOBILE = "kakaotalk://"; // 카카오톡결제 앱 호출
        public static final String LIVE_TV = "rtsp://"; // TV 생방송 재생
        // 개인화 영역 Meesage 함 이동 // toapp://movemessage
        public static final String MOVEMESSAGEBOX = TOAPP + "movemessage"; // TV 생방송 재생

        // 럭키백 이벤트 응모 현황 ( 커밋 하지 않음 소스 추가 요망 )
        // public static final String LUCKYBAG = TOAPP + "luckybag";
        public static final String EVENT_WEB = TOAPP + "eventrecv"; // deviceID 전달
        public static final String MOVE_SECT = TOAPP + "movesect"; // App 에서 매장(페이지) 이동
        public static final String MOVE_SMS = TOAPP + "sms"; // Web -> App sms 연동

        public static final String PINTEREST = TOAPP + "pinterest";// 핀터레스트 // Web -> App 연동
        public static final String EXTERNAL = "external://";// 외부 actionView 연동
        public static final String ETC_SHARE = TOAPP + "etc_share";

        public static final String SEARCH_MOVE = TOAPP + "search";
        // 날방(UA-138)
        public static final String NALBANG_WEB = TOAPP + "movetonalbang";
        public static final String NALBANG_PRD = TOAPP + "movetoinpage";
        //라이브톡(UA-139)
        public static final String LIVETALK_WEB = TOAPP + "movetolivetalk";

        //바로구매(UA-139) 캐스팅예외처리(UA-142)
        public static final String DIRECTBUY = TOAPP + "directord";

        //공유하기(UA-143) ,라인 (UA-145)
        public static final String SHARE = TOAPP + "share";

        //숏방(UA-146)
        public static final String SHORTBANG_WEB = TOAPP + "movetoshortbang";

        //공유하기 스크립트 클릭 연동(UA-146)
        public static final String SNSPUPOP_WEB = TOAPP + "snsshow";

        //왼쪽 슬라이드 네비 호출 규격(UA-151 예정)
        public static final String NAVI_SHOW = TOAPP + "leftnavi";

        //숏방(UA-157 예정)
        public static final String SHORTBANG_EVENT_WEB = TOAPP + "movetoshortbangevent";

        //SNS 로그인
        public static final String SNS_LOGIN = TOAPP + "snslogin";

        //방향 전환 세로
        public static final String ROTATION_PORTRAIT = TOAPP + "portrait";

        //방향 전환 가로
        public static final String ROTATION_LANDSCAPE = TOAPP + "landscape";

        //네이티브 신규 모바일 라이브
        public static final String MOBILE_LIVE_APP = TOAPP + "movetomobilelive";

        // 현재 페이지 닫고 새페이지를 연다.
        public static final String OPEN_NEW_PAGE = TOAPP + "opennewpage";

        //라이브톡 하단 웹뷰 내 링크이동 규격
        public static final String LIVETALK_LINK = TOAPP + "newpage";

        //주소록 APP 연동을 위한 규격 192부터 적용됨
        public static final String OPEN_ADDRESS = TOAPP + "address";

        // 샤피 라이브에서 쿠폰팝업 하루동안 보지 않기
        public static final String SHOPPY_COUPON_POPUP_TODAY_PASS = TOAPP + "shoppycoupontodaypass";// 현재 액티비티 종료
    }
    /**
     * 외부 연동 주소
     *
     */
    /**
     * end-point address
     */
    public static final String END_POINT = "http://widget.as.criteo.com/m/event";

    /**
     * 공사중 일때 OOPS_URL
     */
    public static final String OOPS_URL = "http://m.gsshop.com/oops.html";
    //public static final String url = "http://mt.gsshop.com/oops.html";

    /**
     * CDN 앱 에러 웁스
     */
    public static final String APPERROR_OOPS_URL = "http://apperror.gsshop.com/oops.html";
}