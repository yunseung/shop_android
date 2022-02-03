/*
] * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2;

/**
 * 키(key)를 한곳에서 모아 관리한다.
 *
 */
public abstract class Keys {

    /**
     * Ln. 로그를 사용하기전 체크 하고 사용한다.
     * true 일때만 한다. 빈번하게 호출되는 String 연산을 막고자...함
     * 불필요한 연산을 안할방법이 있을까??요
     * 아아아아,, 이미 lint 해당 방식이 있다...
     */
    //public static final boolean DEBUG_GS = true;

    // 할인률 관련 문구
    public static final CharSequence DISCOUNT_RATE_TEXT_GS = "GS";
    public static final CharSequence DISCOUNT_RATE_TEXT_ENCORE = "앵콜";
    public static final CharSequence DISCOUNT_RATE_TEXT_ONE_PLUS_ONE = "1+1";
    public static final CharSequence DISCOUNT_RATE_TEXT_PRICE = "가";

    // 할인률 문구 text font size
    public static final int DISCOUNT_RATE_TEXT_FONT_SIZE_EN = 19;
    public static final int DISCOUNT_RATE_TEXT_FONT_SIZE_KR = 19;
    public static final int DISCOUNT_RATE_TEXT_FONT_SIZE_NUM = 20;

    // 할인률이 5% 미만이면 "GS가"로 표시
   //public static final int MIN_DISCOUNT_RATE = 5;
    public static final int ZERO_DISCOUNT_RATE = 0;

    /**
     * cache Time 지정 초
     */
    public static final int CACHETIMENAVIGATION = 5 * 60 * 1000;

    //public static final int LIST_ANIMATION_DURATION = 500;
    //GTM 해당 포지션의 배수를 트래킹 함 (첫번째 포지션 0도 트래킹에 포함안함)
    public static final int PRD_POSITION = 25;

    /**
     * 메모리 또는 Preference에 캐시하는데 사용되는 키.
     */
    public static class CACHE {
        public static final String BADGE = "_badge";

        public static final String USER_INFO = "_user";

        public static final String APP_VERSION = "_version";

        public static final String CATEGORIES = "_categories";

        public static final String VIDEO_PARAM = "_video_param";

        public static final String REVIEW_SAVE = "_review_save";

        public static final String HOME_INFO = "_home";

        /* 그룹매장 */
        public static final String HOME_GROUP_INFO = "_home_group";

        /* 네비 오픈 데이트 */
        public static final String HOME_NAVI_OPEN_DATE = "_home_navi_open_date";

        /* 네비 하위 탭 메뉴 brdTime */
        public static final String HOME_TAB_BRD_TIME = "_home_tab_brd_time";

        /* 앱 설치시 전달되는 referrer 값 */
        public static final String REFERRER = "_referrer";

        // 프로모션 팝업 캐쉬
        public static final String PROMOTION = "_promotion";
        public static final String PROMOTION_INFO = "_promotion_info";
        public static final String PROMOTION_DAY = "_promotion_day";

        // 매장팝업
        public static final String PMOPOPUP_TODAY = "_pmopopup_today";
        public static final String PMOPOPUP_FOREVER = "_pmopopup_forever";

        // 푸시동의 팝업
        public static final String PUSH_APPROVE = "_push_approve";
        public static final String PUSH_APPROVE_DAY = "_push_approve_day";

        public static final String PERSONALIZED_CATEGORY = "_personalized_category";

        //GCM Token
        public static final String TOKEN = "_token";

        //숏방 가이드 화면 노출여부
        public static final String SHORTBANG_GUIDE_SHOWN = "_shortbang_guide_shown";

        // 관심 카테고리 리스트
        public static final String INTEREST_CATEGORY_JSON_STRING = "_interest_category_list";

        //버전코드 저장
        public static final String VERSION_CODE = "_version_code";

        //CSP 브레이크 버전에 따른 FLAG
        public static final String CSP_BREAK_FLAG = "_csp_break_flag";

        public static final String PERSONAL_INFO = "_personal_info";

        //PREF 마이그레이션 수행여부
        public static final String PREF_MIGRATION_DONE = "_pref_migration_done";
    }

    /**
     * 쿠키 내 주요 key 정보
     */
    public static class COOKIE {
        /**
         * 간편주문 설정 쿠키 key이름.
         */
        public static final String QUICK_ORDER = "mci";

        public static final String LOGIN_SESSION = "ecid";

        //wa_pcid
        public static final String WA_PCID = "wa_pcid";

        //pcid
        public static final String PCID = "pcid";

        //
        public static final String ADULT  = "adult";

        //appmediatype
        public static final String APP_MEDIA_TYPE = "appmediatype";

        //ad_media
        public static final String AD_MEDIA = "ad_media";

        // 최근검색어
        public static final String SEARCH = "search";

        // 최근검색어 신규(날짜 표시까지 함께
        public static final String SEARCH_ADD_DATE = "searchAddDate";

        //ecid내에 mediatype
        public static final String MEDIA_TYPE = "mediatype";

        //ecid내에 cusclass
        public static final String CUSCLASS = "cusclass";

        //ecid내에 ecuserid
        public static final String EC_USERID = "ecuser";

        //wa_catvid (케이블TV ID)
        public static final String WA_CATVID = "wa_catvid";

    }

    /**
     * 앱 종료 후에도 유지해야할 데이터를 Preference에
     * 저장할 때 사용하는 키.
     * NOTE : 문자열을 변경하면 하위버전 호환성 유지되지 않으므로 주의.
     */
    public static class PREF {

        public static final String LOGIN_OPTION = "_login_option";

        public static final String TOKEN_CREDENTIALS = "_token_login";

        //TOKEN_CREDENTIALS 마이그레이션 이후 사용할 신규 이름
        public static final String TOKEN_CREDENTIALS_NEW = "_token_login_new";

        public static final String BADGE_ORDER_CONSUMED_DATE = "_badge_order_date";

        public static final String BADGE_CART_CONSUMED_DATE = "_badge_cart_date";

        public static final String BADGE_MYSHOP_CONSUMED_DATE = "_badge_myshop_date";

        public static final String BADGE_MESSAGE_CONSUMED_DATE = "_badge_message_date";

        public static final String INTRO_IMAGE_INFO = "_intro_image_info";

        // Finger Print 연동을 위한 팝업 보여주기 여부
        public static final String PREF_IS_SHOW_CHECK_FP = "_popup_is_show_check_fp";

        //Preference 파일이름 (GSSuper)


        public static final String FILE_PREF_GSSUPER = "_gssuper";
        // GSSUper 에서 토스트 팝업이 하루에 한번만 보여주기때문에 날짜 체크 저장 위한 pref
        public static final String PREF_GS_SUPER_TOAST_CHECH_TIME = "_check_toast_gssuper";

        public static final String PREF_SHOW_ORDER_TAB_HELP = "_show_order_tab_help";

        public static final String PREF_TEST_SERVER = "_test_server";

        public static final String PREF_NAME_TEST_SERVER = "_pref_name_test_server";

        public static final String PREF_SHOPPY_LIVE_TODAY_PASS = "_shoppy_live_today_pass";
    }

    /**
     * intent 에 값을 넣어서 전달할 때 사용되는 키
     *
     */
    public static class INTENT {

        /**
         * 이동할 웹주소
         */
        public static final String WEB_URL = "_intent_web_url";

        /**
         * 전달될 메세지 규격
         */
        public static final String WEB_TARGET_MSG = "_intent_target_msg";

        /**
         * 모달웹뷰에서 사용할 상단 타이틀
         */
        public static final String WEB_TITLE = "_intent_web_title";

        /**
         * 상품평 작성화면에 넘겨줄 쿼리스트링
         */
        public static final String REVIEW_QUERY_STRING = "_review_query";

        /**
         * 파일첨부 화면에 넘겨줄 쿼리스트링
         */
        public static final String ATTACH_QUERY_STRING = "_attach_query";

        /**
         * 수신된 PUSH 메시지
         */
        public static final String PUSH_MESSAGE = "_push_message";

        public static final String PUSH_SCREEN_ON = "_push_screen";


        /**
         * 선택된 탭메뉴 정보를 전달
         */
        public static final String TAB_MENU = "_tab_menu";

        /**
         * 앱 오픈이 무슨 이유로 되었는가의 타입
         * ex) callafter 등등
         */
        public static final String OPEN_AFTER_TYPE = "_open_after_type";

        /**
         * 인턴트에 전달된 ARS 전화 번호
         */
        public static final String ARS_NUMBER_VALUE = "_ars_number_value";

        /**
         * 비밀번호 변경 팝업 tv 회원 여부
         */
        public static final String ARS_MEMBER_VALUE = "_ars_member_value";

        /**
         * 위젯의 메뉴별 구분자
         */
        public static final String WIDGET_TYPE = "_widget_type";

        /**
         * 액티비티가 탭메뉴를 통해 시작된 것인지 판단
         */
        public static final String FROM_TAB_MENU = "_from_tab_menu";

        /**
         * ZZIM 버튼을 눌러서 LoginActivity 를 호출했는지 구분하기 위한 값
         */
        public static final String FROM_ZZIM_BUTTON = "_from_zzim_button";

        /**
         * 메시지함에서 LoginActivity 를 호출했는지 구분하기 위한 값
         */
        public static final String GOTO_INBOX = "_goto_inbox";

        /**
         * 맞출딜에서 LoginActivity 를 호출했는지 구분하기 위한 값
         */
        public static final String FROM_PERSONAL_DEAL = "_from_persional_deal";

        /**
         * 설정화면에서 LoginActivity 를 호출했는지 구분하기 위한 값
         */
        public static final String FROM_SETTING = "_from_setting";

        /**
         * 모바일라이브 전체화면(채팅)에서 LoginActivity 를 호출했는지 구분하기 위한 값
         */
        public static final String FROM_MOBILE_LIVE = "_from_mobile_live";

        /**
         * 액티비티가 startActivityForResult()에 의해 시작된 것인지
         * 알려줄 필요가 있는 경우에 사용.
         */
        public static final String FOR_RESULT = "_for_result";

        /**
         * Fragment refresh가 필요할 경우 사용.
         */
        public static final String FOR_GS_SUPER = "_for_gs_super";
        //public static final String SEARCH_TYPE_DEFAULT = "SEARCH_TYPE_DEFAULT";


        /**
         * 상품평 등록 후 결과 메시지
         */
        public static final String RESULT_MESSAGE = "_result_msg";

        /**
         * 상품평 등록하기 위해 카메라로 새로 찍은 이미지 경로
         */
        public static final String REVIEW_IMAGE = "_review_image";

        /**
         * 댓글을 등록하기 위해 카메라로 새로 찍은 이미지 경로
         */
        public static final String ATTACH_IMAGE = "_attach_image";

        /**
         * 커스텀 카메라 프리뷰에서 촬영 후 저장할 이미지 경로, 이름
         */
        public static final String CAMERA_CUSTOM_PREVIEW_PATH = "_camera_custom_preview_path";
        public static final String CAMERA_CUSTOM_PREVIEW_NAME = "_camera_custom_preview_name";


        /**
         * 동영상 재싱시 전달할 파라미터들
         */
//        public static final String VIDEO_PARAM = "_video_param";
        public static final String VIDEO_ID = "_video_id";
        public static final String VIDEO_URL = "_video_url";
//        public static final String VIDEO_TIME = "_video_time";
        public static final String VIDEO_START_TIME = "_video_start_time";
        public static final String VIDEO_END_TIME = "_video_end_time";
        public static final String VIDEO_PRD_URL = "_video_prdurl";
        public static final String VIDEO_IMAGE_URL = "_video_image_url";
        public static final String VIDEO_ORIENTATION = "_video_orientation";
        public static final String VIDEO_IS_PLAYING = "_video_is_playing";
//        public static final String VIDEO_MUTE = "_video_mute";

        public static final String VIDEO_PRD_WISELOG_URL = "_video_prd_wiselog_url";

        /**
         * 풀스크린 플레이어 호출자
         */
        public static final String FULL_SCREEN_CALLER = "_full_screen_caller";

        /**
         * 비회원 로그인 유형구분
         */
        public static final String NON_MEMBER_LOGIN_TYPE = "_nonmember_login";

        /**
         * SimpleLogin 액티비티에서 호출한경우 
         */
        public static final String FOR_SIMPLE_LOGIN = "_for_simple_login";

        /**
         * 배경화면 DIM 적용여부 플래그
         */
        public static final String BACKGROUND_DIM_ON = "_background_dim_on";

        /**
         * 백키 클릭시 해당 액티비티를 닫고 말지, 아니면 닫고 메인으로 이동할지를 구분하는 플래그
         */
        public static final String BACK_TO_MAIN = "_back_to_main";

        /**
         * 웹에서 호출되었는지 여부 플래그
         */
        public static final String FROM_WEB = "_from_web";

        /**
         * type이 다른 web activity로 redirecting될 수 있는 경우를 표시하기 위한 플래그.
         * 이 경우에 redirecting 시킨 activity는 종료.<br>
         * (ex) 축약 url로 단품을 갈 경우 WebActivity를 거쳐 NoTabWebActivity로 이동하고
         * WebActivity는 종료.
         */
        public static final String CAN_CAUSE_REDIRECTING = "_can_cause_redirecting";

        /**
         * 특정 그룹의 특정 섹션으로 가기 위한 section code 값
         */
        public static final String SECTION_CODE = "_section_code";

        /**
         * 오픈 데이터 저장 키캆
         */
        public static final String NAVI_OPEN_DATE = "_open_date";

        /**
         * 특정 그룹의 특정 섹션으로 가기 위한 navigation id 값
         */
        public static final String NAVIGATION_ID = "_navigation_id";

        /**
         * 특정 그룹의 특정 섹션으로 가기 위한 groupCode id 값
         */
        public static final String GROUP_CODE_ID = "_group_code_id";

        /**
         * 스트리밍 / VOD : PRD_PLAY
         */
        public static final String LIVE_PLAY = "LIVE_PLAY"; //스트리밍,VOD 상품 플레이어 타입

        public static final String PRD_PLAY = "PRD_PLAY"; //스트리밍,VOD 상품 플레이어 타입
        /**
         * 일반 영상재생 : BASE_PLAY
         */
        public static final String BASE_PLAY = "BASE_PLAY"; //쇼미등 전체 화면이 아닌 일반 재생

        /**
         * 날방 link
         */
        public static final String NALBANG_LINK = "_nalbang_link"; //쇼미등 전체 화면이 아닌 일반 재생

        /**
         * 날방 link
         */
        public static final String MOBILELIVE_LINK = "_mobilelive_link"; //쇼미등 전체 화면이 아닌 일반 재생


        /**
         * 라이브톡 link
         */
        public static final String LIVETALK_LINK = "_livetalk_link";

        /**
         * 숏방 link
         */
        public static final String SHORTBANG_LINK = "_shortbang_link";

        /**
         * 숏방 link
         */
        public static final String SHORTBANG_EVENT_LINK = "_shortbang_event_link";

        /**
         * 숏방 이미지
         */
        public static final String SHORTBANG_PRELOAD_IMAGE = "_shortbang_preload_image";

        /**
         * 숏방 data
         */
        public static final String SHORTBANG_PRODUCT_DATA = "shorbangProductData";

        /**
         * 숏방 data
         */
        public static final String SHORTBANG_PRODUCT_INDEX = "shorbangProductIndex";

        /**
         * 호출한 주체를 구분하기 위한 값
         */
        public static final String REFERRER = "_referrer";

        /**
         * 호출한 주체를 구분하기 위한 값
         */
        public static final String FROM_INSTANCE_IDL = "_from_instance_idl";

        /**
         * 스크린샷
         */
        public static final String SCREEN_SHOT = "screen_shot";

        public static final String LEFT_NAVIGATION = "left_navigation";

        /**
         * 웹뷰 URL 호출시 POST 방식인지 여부
         */
        public static final String POST_DATA = "_post_data";

        /**
         * Finger Print 연동을 위한 팝업 보여주기 여부
         */
        public static final String INTENT_IS_SHOW_CHECK_FP = "_is_check_fp";

        /**
         * GS Fresh 이미지 팝업 URL 데이터를 보내기 위한 인텐트
         */
        public static final String INTENT_DATA_GSSUPER_POPUP = "_data_gs_super_popup";

        /**
         * GS Fresh 선택 시 이동할 URL
         */
        public static final String INTENT_OPEN_URL = "_open_url";

        /**
         * 기본 팝업 데이터 인텐트
         */
        public static final String INTENT_POPUP_BASIC = "_popup_basic";

        /**
         * 아래에서 올라오는 애니메이션 여부
         */
        public static final String INTENT_FROM_BOTTOM = "_from_bottom";

        /**
         * 네비게이션 리프레시 여부 체크
         */
        public static final String INTENT_REFRESH_NAVI = "_refresh_navi";

        /**
         * L 시퀀스 파라메터
         */
        public static final String INTENT_LSEQ_PARAM = "_lseq_param";
        /**
         * 미디어 파라메터 (BZ)
         */
        public static final String INTENT_MEDIA_PARAM = "_media_param";
        /**
         * 커스텀 카메라를 HomeActivity에서 직접 사용함에 따라 결과를 알려줄 때에 Gallery 여부 넘겨줌.
         */
        public static final String INTENT_GALLERY_PARAM = "_is_from_gallery";
        /**
         * 커스텀 카메라가 Searching bar 에서 실행되는지 여부
         */
        public static final String INTENT_IS_FROM_SEARCHING = "_is_from_searching";
        /**
         * 커스텀 카메라가 HomeActivity 에서 실행되는지 여부
         */
        public static final String INTENT_IS_FROM_CAMERA = "_is_from_cameara";

        /**
         * 단품에서 노출할 이미지 주소
         */
        public static final String IMAGE_URL = "_intent_image_url";

        /**
         * 단품에서 재생버튼 노출 여부
         */
        public static final String HAS_VOD = "_intent_has_vod";

        /**
         * 단품네이티브에서 호출되었는지 여부
         */
        public static final String FROM_NATIVE_PRODUCT = "_from_native_product";

        /**
         * 최근검색어 기반으로한 연관검색어 리스트
         */
        public static final String KEYWORD_LIST = "recent_recommand_list";
    }

    /**
     *
     * NOTE : AndroidManifest.xml의 각 Activity 부분 참고.
     *
     * NOTE : 액티비티 사이의 명시적인 의존관계를 해소하기 위해
     * 탭메뉴 액티비티를 intent action을 통해 간접적으로 호출
     */
    public static class ACTION {

        /**
         * HomeActiviy를 실행하기 위한 액셕
         */
        public static final String APP_HOME = "gsshop.intent.action.APP_HOME";

        /**;
         * WebActivity를 실행하기 위한 액션.
         */
        public static final String WEB = "gsshop.intent.action.WEB";

        /**;
         * NalbangWebActivity를 실행하기 위한 액션.
         */
        public static final String NALBANG_WEB = "gsshop.intent.action.WEB_NALBANG";

        /**;
         * LiveTalkWebActivity를 실행하기 위한 액션.
         */
        public static final String LIVETALK_WEB = "gsshop.intent.action.WEB_LIVETALK";

        /**
         * SearchActivity를 실행하기 위한 액션.
         */
        public static final String SEARCH = "gsshop.intent.action.SEARCH";

        /**
         * LoginActivity를 실행하기 위한 액션.
         */
        public static final String LOGIN = "gsshop.intent.action.LOGIN";

        /**
         * 로그인 정보 화면 실행하기 위한 액션.
         */
        public static final String SIMPLE_LOGIN_SETTING = "gsshop.intent.action.SIMPLE_LOGIN_SETTING";

        /**
         * PushPopupActivity를 실행하기 위한 액션.
         */
        public static final String PUSH_POPUP = "gsshop.intent.action.PUSH_POPUP";

        public static final String LIVE_VIDEO_PLAYER = "gsshop.intent.action.LIVE_VIDEO_PLAYER";
        /**
         * NalbangFullVideoPlayerActivity를 실행하기 위한 액션.
         */
        public static final String NALBANG_FULL_VIDEO_PLAYER = "gsshop.intent.action.NALBANG_FULL_VIDEO_PLAYER";

        /**
         * NalbangFullVideoPlayerActivity를 실행하기 위한 액션.
         */
        public static final String MOBILELIVE_FULL_VIDEO_PLAYER = "gsshop.intent.action.MOBILELIVE_FULL_VIDEO_PLAYER";

        /**
         * LiveTalkFullVideoPlayerActivity를 실행하기 위한 액션.
         */
        public static final String LIVETALK_FULL_VIDEO_PLAYER = "gsshop.intent.action.LIVETALK_FULL_VIDEO_PLAYER";

        /**
         * InboxActivity를 실행하기 위한 액션.
         */
        public static final String MESSAGE = "gsshop.intent.action.MESSAGE";

        public static final String NOTIFICATION = "gsshop.intent.action.NOTIFICATION";

        public static final String NOTIFICATION_CLASS = "gsshop.mobile.v2.pms.PushNotiReceiver";
        /**
         * 탭 메뉴가 없는 Web 호출
         */
        public static final String NO_TAB_WEB = "gsshop.intent.action.NO_TAB_WEB";
        public static final String PRODUCT_DETAIL_NATIVE_WEB = "gsshop.intent.action.PRODUCT_DETAIL_NATIVE_WEB";

        /**
         * TvMember ARS 가입을 위한 Webview Activity Action
         */
        public static final String TV_MEMBERS_WEB = "gsshop.intent.action.TV_MEMBERS_WEB";

        /**
         * 마이쇼핑 web 호출
         */
        public static final String MY_SHOP_WEB = "gsshop.intent.action.MY_SHOP_WEB";

        /**
         * 주문서 web 호출
         */
        public static final String ORDER_WEB = "gsshop.intent.action.ORDER_WEB";

        /**
         * 주문서 하단탭 web 호출
         */
        public static final String ORDER_NOTAB_WEB = "gsshop.intent.action.ORDER_NOTAB_WEB";

        /**
         * 바로구매 web 호출
         */
        public static final String DIRECT_ORDER_WEB = "gsshop.intent.action.DIRECT_ORDER_WEB";

        /**
         * 모바일 라이브 용 no tab web
         */
        public static final String MOBILELIVE_NO_TAB_WEB = "gsshop.intent.action.MOBILELIVE_NOTAB_WEB";

        /**
         * 배경이 투명한 모달 웹뷰
         */
        public static final String TRANSPARENT_MODAL_WEB = "gsshop.intent.action.TRANSPARENT_MODAL_WEB";

        /**
         * GalleryActiviy를 실행하기 위한 액션.
         */
        public static final String GALLERY = "gsshop.intent.action.GALLERY";

        /**;
         * ShortBangActivity를 실행하기 위한 액션.
         */
        public static final String SHORT_BANG = "gsshop.intent.action.SHORT_BANG";

        /**
         * ShortBangEventActivity를 실행하기 위한 액션.
         */
        public static final String SHORT_BANG_EVENT = "gsshop.intent.action.SHORT_BANG_EVENT";

        /**
         * 왼쪽 네비게이션 메뉴
         */
        public static final String LEFT_NAVIGATION = "gsshop.intent.action.LEFT_NAVIGATION";

        /**
         * 비밀번호 변경 화면 실행하기 위한 액션.
         */
        public static final String UPDATE_PASS = "gsshop.intent.action.UPDATE_PASS";

        /**
         * 지문인식 확인 화면 실행하기 위한 액션
         */
        public static final String ACTION_CHECK_FP = "gsshop.intent.action.CHECK_FP";

        /**
         * 기본 팝업 화면 실행하기 위한 액션
         */
        public static final String ACTION_POPUP_BASIC = "gsshop.intent.action.POPUP_BASIC";

    }

    /**
     * startActivityForResult에서 사용하는 request code
     *
     */
    public static class REQCODE {
        public static final int QRCODE = 0x1001;
        public static final int VOICE = 0x1002;
        public static final int LOGIN = 0x1003;
        public static final int CAMERA = 0x1004;
        public static final int PHOTO = 0x1005;
        public static final int REVIEW = 0x1006;
        public static final int SETTING = 0x1007;
        public static final int VIDEO = 0x1008;
        public static final int MODAL = 0x1009;
        public static final int PMS = 0x1010;
        public static final int EVENT = 0x1011;
        public static final int LAUNCH_OTHER_APP = 0x1012;
        public static final int ZZIM = 0x1013;
        public static final int FULL_VIDEO = 0x1014;
        //웹뷰 파일첨부기능
        public static final int IMAGE_PICKER = 0x1014;
        public static final int KITKAT_IMAGE_PICKER = 0x1015;
        public static final int IMAGE_PICKER_PERMISSION_REQUEST = 0x1021;
        public static final int IMAGE_PICKER_LOLLIPOP = 0x1022;
        //모바일상담 파일첨부기능
        public static final int MOBILETALK = 0x1016;
        //GPS
        public static final int GPS = 0x1017;
        public static final int WEBVIEW = 0x1018;
        //HomeSearchingActivity 호출
        public static final int HOME_SEARCHING = 0x1019;
        // 시스템 갤러리 - 비디오
        public static final int PHOTO_VIDEO = 0x1020;
        //네이티브 파일첨부기능
        public static final int FILEATTACH = 0x2100;
        public static final int ATTACH_CAMERA = 0x2101;
        public static final int ATTACH_PHOTO = 0x2201;
        public static final int ATTACH_CUSTOM_CAMERA = 0x2202;

        public static final int GALLERY = 0x2301;
        public static final int GALLERY_THUNMBNAIL = 0x23012;

        //맞춤딜
        public static final int PERSONAL_DEAL = 0x2302;

        // 이미지 크롭 기능
        public static final int PHOTO_EDIT = 0x2303;

        public final static int SHOW_SEARCH_DELETE_POPUP = 0x2401;

        public final static int PERMISSIONS_REQUEST_CAMERA = 0x11;
        public final static int PERMISSIONS_REQUEST_STORAGE = 0x12;
        public final static int PERMISSIONS_REQUEST_PHONE = 0x13;
        public final static int PERMISSIONS_REQUEST_STORAGE_GALLERY = 0x14;
        public final static int PERMISSIONS_REQUEST_STORAGE_CAMERA = 0x15;
        public final static int PERMISSIONS_REQUEST_LOCATION = 0x16;
        public final static int PERMISSIONS_REQUEST_SMS = 0x17;
        public final static int PERMISSIONS_REQUEST_STORAGE_GALLERY_FROM_IMG_SEARCH = 0x18;
        public final static int PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM = 0x19;
        public final static int PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM_FROM_SEARCH = 0x20;

        //주소록 띄우기
        public final static int ADDRESS = 0x21;
    }

    /**
     * 비회원 및 성인 처리를 위한 상태값
     */
    public static class NonMemberLoginType {

        public static final int NORMAL = 0;

        /**
         * 성인인증 체크를 위해 진입
         */
        public static final int ADULT_CHECK = 1;

        /**
         * 바로주문을 통한 로그인 화면 진입
         */
        public static final int DIRECT_ORDER = 2;
    }

    /**
     * GET, POST, 앱설치시 referrer값 등에서 사용되는 파라미터명 정의 (파라미터명 하드코딩 방지용)
     *
     */
    public static class PARAM {
        /**
         * install-referrer 또는 scheme로 접속시 접속매체를 구분하기 위한 값
         */
        public static final String MEDIA = "media";

        /**
         * 재생버튼 링크에 추가할 파라미터 (LIVE:생방송영역, VOD:브라이트코브)
         */
        public static final String VOD_PLAY = "vodPlay";
    }
}
