package gsshop.mobile.v2.support.gtm;

/**
 * Created by cools on 2017. 7. 21..
 * Amp 정의값
 */

public class AMPEnum {

    //메인 프리픽스
    public static final String AMP_ACTION_VISIT_PREFIX      = "View-메인매장-";

    //카테고리내 클릭
    public static final String AMP_CLICK_LEFTNAVI_CATEGORYGROUP    = "Click-매장-카테고리그룹";

    //페이지 뷰
    public static final String AMP_VIEW_MAIN_SEARCH         = "View-매장-검색진입";
    //public static final String AMP_VIEW_LEFTNAVI    = "View-매장-카테고리";
    public static final String AMP_VIEW_LOGIN    = "View-기타-로그인진입";


    //라이브톡 글 등록 버튼
    public static final String AMP_CLICK_LIVETALK_REGBUTTON    = "Click-라이브톡-등록";

    //날방(심야방송) 글 등록 버튼
    public static final String AMP_CLICK_NALBANG_REGBUTTON    = "Click-심야방송-등록";

    //로그인화면에 로그인종류별 클릭
    public static final String AMP_CLICK_LOGIN_TYPE    = "Click_로그인_기능";
    public static final String AMP_CLICK_LOGIN_TYPE_KEY    = "action";
    public static final String AMP_CLICK_LOGIN_TYPE_VAL_NAVER    = "네이버";
    public static final String AMP_CLICK_LOGIN_TYPE_VAL_KAKAO    = "카카오";
    public static final String AMP_CLICK_LOGIN_TYPE_VAL_ARS    = "로그인번호";
    public static final String AMP_CLICK_LOGIN_TYPE_VAL_FINGER_PRINT    = "지문";
    public static final String AMP_CLICK_LOGIN_TYPE_VAL_PHONE    = "휴대폰";
    public static final String AMP_CLICK_LOGIN_TYPE_VAL_NORMAL = "일반로그인";

    //로그인 성공시 이벤트 6/16일 제거
    //public static final String AMP_ACTION_LOGIN_SUCCESS_MANUAL      = "로그인_EC회원";
    //public static final String AMP_ACTION_LOGIN_SUCCESS_AUTO      = "자동로그인_EC회원";

    //모바일 라이브 방송시청/참여 페이지 정의
    public static final String MOBILELIVE_VIEW_NALBANG = "View-모바일라이브-생방송페이지";
    public static final String MOBILELIVE_CLICK_EXIT = "Click-모바일라이브-생방송나가기";
    public static final String MOBILELIVE_CLICK_SHOWPRD ="Click-모바일라이브-상품보기";
    public static final String MOBILELIVE_CLICK_TALK = "Click-모바일라이브-톡등록";
    //모바일라이브 UI개편후 생긴 알림버튼
    public static final String MOBILELIVE_CLICK_ALARM = "Click-모바일라이브-방송알림신청";

    //모바일 라이브 상품조회 페이지 정의
    public static final String MOBILELIVE_CLICK_SHOWCHAT = "Click-모바일라이브-채팅보기";
    public static final String MOBILELIVE_CLICK_BUY = "Click-모바일라이브-바로구매";

    //모바일라이브 방송알림 공통
    public static final String AMP_CUSTOMER_NUM = "고객번호";
    public static final String AMP_LIVE_NUMBER = "라이브번호";
    public static final String AMP_START_TIME = "지금방송시작시간";

    //내일 TV 메인 매장
    public static final String AMP_CLICK_TOMORROW_TV = "Click-메인매장-내일TV";
    //public static final String TOMORROW_TV_AUTOPLAY = "자동재생시작";
    public static final String TOMORROW_TV_BUTTONPLAY = "재생시작";
    //public static final String TOMORROW_TV_FULL_BUTTONPLAY = "전체보기재생시작";
    public static final String TOMORROW_TV_LIST_PRD_CLICK = "리스트상품클릭";
    public static final String TOMORROW_TV_FULL_PRD_CLICK = "전체보기상품클릭";

    //메인매장 더보기/접기
    public static final String AMP_CLICK_MAINTAB = "Click-개인화매장-더보기";
    public static final String MAINTAB_MORE = "더보기";
    public static final String MAINTAB_CLOSE = "접기";

    //event propert key 내일 TV 관련 또는 공통
    public static final String AMP_ACTION_NAME = "action";
    public static final String AMP_PRD_CODE = "prdCd";
    public static final String AMP_PRD_NAME = "prdNm";
    public static final String AMP_PRD_PRICE = "price";
    public static final String AMP_VIDEO_ID = "videoId";
    public static final String AMP_URL_MSEQ = "urlMseq";
    public static final String AMP_VIDEO_DUR = "duration";
    public static final String AMP_EXP_NAME  = "expId"; //실험을 위해 추가 201907/23 내일만 쓰자 나중에 종한씨와 이야기해서 전체 반영
    public static final String AMP_VIEW_TYPE = "viewtype";
    public static final String AMP_INDEX = "index";
    public static final String AMP_EXP_TYPE = "abtype";
    public static final String AMP_ACTION_PRD = "action_prd"; //홈생방 AB 골을위해 추가됨(단품페이지 or 멀티페이지 구분)
    public static final String AMP_CLICK_WHERE = "clickWhere"; //홈생방 AB 골을위해 추가됨(주상품영역 or 부상품영역 구분)


    /**
     * AB테스트 앰플리튜드 property
     */
    public static final String AMP_AB_KEY = "abKey";
    public static final String AMP_AB_VALUE = "abValue";
    public static final String AMP_AB_DETAIL_TYPE = "abDetailType";
    public static final String AMP_AB_INFO = "abinfo";

    /**
     * TV편성표 AB테스트(01.12배포)
     */
    public static final String AMP_CLICK_SCHEDULE_PRD = "Click-편성표-상품";
    public static final String AMP_CLICK_SCHEDULE_ALARM = "Click-편성표-방송알림";
    public static final String AMP_CLICK_SCHEDULE_CATEGORY = "Click-편성표-카테고리";

    /**
     * 모듈화컨텐츠 AB테스트(02.04배포)
     */
    public static final String AMP_VIEW_CONTENTS_PRD = "amp-View-대상상품-단품상세";
    public static final String AMP_CLICK_CONTENTS_PRD = "amp-Click-대상상품-재생";
    public static final String APPTI_VIEW_CONTENTS_PRD = "appti-View-대상상품-단품상세";
    public static final String APPTI_CLICK_CONTENTS_PRD = "appti-Click-대상상품-재생";

    /**
     * 탭명변경 AB테스트
     */
    public static final String AMP_VIEW_TABNAME = "amp-View-메인탭명-서브탭명";
    public static final String APPTI_VIEW_TABNAME = "appti-View-메인탭명-서브탭명";

    /**
     * 홈 이미지 캐로셀 AB테스트
     */
    public static final String AMP_CLICK_IMGBANNER = "amp-Click-이미지배너";
    public static final String APPTI_CLICK_IMGBANNER = "appti-Click-이미지배너";

    //내일 TV내 바로구매
    public static final String DIRECT_BUY  = "바로구매"; //상담하기 포함 2019 07/23 내일만 쓰자 나중에 종한씨와 이야기해서 전체 반영
    public static final String DIRECT_CART  = "장바구니";

    //앰플리튜드와 브레이즈 연동 제외대상
    //1. Click-단품-상세설명
    //2. Click-딜-상세설명
    public static final String APPBOY_EXCEPTION_PRD = "Click-단품-상세설명";
    public static final String APPBOY_EXCEPTION_DEAL = "Click-딜-상세설명";

    //6/16일 제거
    //public static final String APPBOY_EXCEPTION_MANUAL_LOGIN = AMP_ACTION_LOGIN_SUCCESS_MANUAL;
    //public static final String APPBOY_EXCEPTION_AUTO_LOGIN = AMP_ACTION_LOGIN_SUCCESS_AUTO;

    private static String[] exceptionLists = {APPBOY_EXCEPTION_PRD,APPBOY_EXCEPTION_DEAL};


    //단품(prd) 네이티브 관련 정의
    //단품 내 클릭 이벤트명
    public static final String AMP_CLICK_PRD = "Click-단품-상세설명";
    public static final String AMP_CLICK_DEAL = "Click-딜-상세설명";


    //상품 내 클릭 프로퍼티 종류
    //{ action: prdCd: prdNm(타이틀): type (TV쇼핑/백화점): }
    public static final String AMP_PRD_ACTION_KEY = "action";
    public static final String AMP_PRD_PRDCD_KEY = "prdCd";
    public static final String AMP_PRD_PRDNM_KEY = "prdNm";
    public static final String AMP_PRD_TYPE_KEY = "type";

    //상품 내 클릭 프로퍼티 종류
    //action      dealNo  dealPrdNm     type
    public static final String AMP_DEAL_ACTION_KEY = "action";
    public static final String AMP_DEAL_DEALNO_KEY = "dealNo";
    public static final String AMP_DEAL_DEALNM_KEY = "dealPrdNm";
    public static final String AMP_DEAL_TYPE_KEY = "type";


    //단품 내 클릭 Action 명 종류
    //public static final String AMP_PRD_HEADER_BACK = "header_back";     //단품 내 헤더 백키 클릭 action
    public static final String AMP_PRD_HEADER_HOME = "header_home";     //단품 내 헤더 홈 클릭 action
    public static final String AMP_PRD_HEADER_SEARCH = "header_검색";     //단품 내 헤더 홈 클릭 action
    public static final String AMP_PRD_HEADER_CART = "header_장바구니";    //단품 내 헤더 홈 클릭 action

    public static final String AMP_PRD_PRDNMINFO_BRANDINFO = "매장명(파트너스/JBP)";    //단품 내 헤더 홈 클릭 action
    public static final String AMP_PRD_NOINTERESTINFO_INTERESTTXT = "구매/배송_무이자";
    //public static final String AMP_PRD_deliveryInfo_deliveryInfoList = "구매/배송_묶음배송"; //textValue 사용
    public static final String AMP_PRD_deliveryInfo_addressTooltip = "구매/배송_배송안내";
    public static final String AMP_PRD_cardPmoInfo_addInfoUrl = "구매/배송_즉시할인";

    //가격개인화 펼치기 버튼
    public static final String AMP_VIEW_MORE_BENEFIT = "할인적립자세히보기";
    public static final String AMP_TYPE_B = "B";



    public static final String AMP_PRD_saleInfo_preCalcurateUrl = "판매가_미리계산";
    public static final String AMP_PRD_saleInfo_discountAddInfoUrl = "판매가_할인혜택보기";

    public static final String AMP_PRD_saleInfo_favoriteRunUrl = "찜";
    public static final String AMP_PRD_broadInfo_runUrl = "TV_방송알림";
    public static final String AMP_PRD_prdNmInfo_reviewInfo = "상품평_상품명하단";
    //public static final String AMP_PRD_broadInfo_changeAddress = "더반찬_배송지변경";  // textValue 사용

    //플레이어 관련
    public static final String AMP_PRD_LIVE_MANUAL_PLAY = "대표_동영상_Live_수동재생_Play"; //play 버튼
    public static final String AMP_PRD_LIVE_AUTO_PLAY = "대표_동영상_Live_자동재생";
    public static final String AMP_PRD_VOD_MANUAL_PLAY = "대표_동영상_VOD_수동재생_Play";
    public static final String AMP_PRD_VOD_AUTO_PLAY = "대표_동영상_VOD_자동재생";
    public static final String AMP_PRD_MINI_FROMFULL = "대표_동영상_Mini";


    /**
     * 예외가 필요한 이벤트라면 true / 아니면 false
     * @param _eventName
     * @return
     */
    public static boolean appBoyExceptionEvent(String _eventName)
    {
        for (int i = 0; i < exceptionLists.length; i++) {
            String eventName = exceptionLists[i];
            if(_eventName != null){
                if (eventName.equals(_eventName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
