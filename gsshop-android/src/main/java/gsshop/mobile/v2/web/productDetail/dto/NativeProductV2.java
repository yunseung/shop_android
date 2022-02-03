package gsshop.mobile.v2.web.productDetail.dto;

import java.util.List;

/**
 * 단품, 딜 네이티브 모델
 */
public class NativeProductV2 {
    /**
     * 상품번호 혹은 딜번호
     */
    public String prdCd;

    /**
     * 'PRD' or 'DEAL' (Product / Deal)
     */
    public String prdTypeCode;

    /**
     * 성인인증
     */
    // 성인 미인증 시 이동해야할 URL
    public String adultCertRetUrl;
    /**
     * "N"인 경우 성인인증으로 이동
     */
    public String checkAdultPrdYN;

    /**
     * 딜 관련
     */
    public String resultCode;   // 결과 코드
    public String dealNo;       // 딜 상품 ID
    public String resultMessage; // 결과 메세지
    public String version;      // 버전 정보

    /**
     * 모듈화컨텐츠AB 대상상품 여부
     */
    public String isContentAB;

    public List<Component> components;

    public static final int VIDEO_MODE_LIVE = 0;  // 라이브
    public static final int VIDEO_MODE_VOD = 1;  // VOD
    public static final int VIDEO_MODE_MP4 = 2;  // VOD

    // 샤피라이브 gate URL
    public String shoppygateurl;

    /**
     * 컴포넌트 모델
     */
    public static class Component {
        /**
         * 템플릿 구분자
         */
        public String templateType;

        /**
         * 상품 이미지 배열
         */
        public List<String> imgUrlList;

        /**
         * GS Fresh 상품 혜택 이미지 배열
         */
        public List<String> benefits;

        /**
         * 영상 정보 (기존 broad가 따로 나와 있다가 videoList로 합친듯?
         */
        public List<VideoList> videoList;

        /**++++++++++++++++++++++++++ 방송, 브랜드, 상품정보 ++++++++++++++++++++++/
        /**
         * 방송타입 이미지 url
         */
        public String broadChannelImgUrl;

        /**
         * 방송 상태 텍스트
         */
        public List<TxtInfo> broadText;

        /**
         * 방송알림 체크박스 Y/N
         */
        public String applyBroadAlamYN;

        /**
         * 방송알림 버튼 노출여부 Y/N
         */
        public String tvAlarmBtnFlg;

        /**
         * 방송알림 스크립트 이름
         */
        public String runUrl;

        /**
         * 브랜드 정보
         */
        public BrandInfo brandInfo;

        /**
         * 리뷰 정보
         */
        public ReviewInfo reviewInfo;

        /**
         * 상품 정보 TEXT
         */
        public List<TxtInfo> promotionText;
        public List<TxtInfo> expoName;
        public List<TxtInfo> engInfoText;
        public List<TxtInfo> subInfoText;
        public List<String> badgeInfo;

        /**
         * 가격 정보
         */
        public String preCalcurateUrl;
        public String shareRunUrl;
        public String favoriteYN;
        public String favoriteRunUrl;
        public List<TxtInfo> priceInfo;

        /**
         * 할인 정보
         */
        public DiscountInfo discountInfo;

        /**
         * 가격 영역 추가 정보
         */
        public List<List<TxtInfo>> additionalList;

        /**
         * 프로모션 정보 (vip, vvip 등)
         */
        public List<GradePmoInfo> gradePmoInfo;

        /**
         * 카드 적립금 등 혜택 정보
         */
        public List<CardList> cardList;
        public String cardImgUrl;

        /**
         * 2회 적립금 등 앞으로 추가될 영역에 대한 정보
         */
        public List<ItemList> itemList;

        /**
         * 개인 쿠폰 프로모션 정보
         */
        public List<TxtInfo> pmoText;
        public List<TxtInfo> pmoPrc;
        public String pmoUrl;

        /**++++++++++++++++++++++++++ 배송정보 ++++++++++++++++++++++/
         /**
         * 배송정보
         */
        public List<DeliveryList> deliveryList;

        /**
         * 배송정보 상세내역
         */
        public List<DeliveryInfoList> deliveryInfoList;

        /**++++++++++++++++++++++++++ 무이자할부 ++++++++++++++++++++++/
         /**
         * 이미지 주소
         */
        public String iConImgUrl;

        /**
         * 텍스트 영역
         */
        public List<TxtInfo> interestTxt;

        /**
         * 더보기 링크 주소
         */
        public String addInfoUrl;

        public PrsnlPmoInfo prsnlName;

        public List<PrsnlTxtInfoList> saleMaxList;

        public List<PrsnlTxtInfoList> addSaleInfo;

        public List<PrsnlTxtInfoList> addPmoInfo;

        public List<PrsnlTxtInfoList> juklibMaxList;

        public List<PrsnlTxtInfoList> moreInfoList;

        public String prsnlApiUrl;

        public CardPmo cardPmo;
    }

    /**
     * 비디오 리스트 (첫 개편 이후 여러 비디오로 올 가능성 농후)
     */
    public static class VideoList {
        public String videoPreImgUrl;       // 섬네일 이미지
        public String videoUrl;             // MP4 url?
        public String videoId;              // 브라이트코브 비디오 아이디값
        public String liveYN;               // 생방송 여부
        public String startTime;            // 방송 시작시간
        public String endTime;              // 방송 종료 시간
        public String videoMode;            // L,P,S -> 각 가로 세로 정사각 영상을 의미 합니다.
    }

    /**
     * 상품 정보 영역 리뷰 모델
     */

    public class ReviewInfo {
        public String reviewPoint;
        public List<TxtInfo> reviewText;
        public String linkUrl;
        public String userName;
        public List<TxtInfo> gradeInfoText;
    }

    /**
     * 초개인화 사용자 정보
     */
    public class PrsnlPmoInfo {
        public String iConUrl;
        public List<TxtInfo> gradeText;
        public List<TxtInfo> gradeInfoText;
        public String useName;
    }

    /**
     * 초개인화 left right item
     */
    public class PrsnlTxtInfoList {
        public List<TxtInfo> leftItem;
        public List<TxtInfo> rightItem;
    }

    /**
     * 초개인화에서 쓰이는 카드정보
     */
    public class CardPmo {
        public List<CardList> cardList;
        public String cardImgUrl;
        public String addInfoUrl;
    }

    /**
     * 브랜드 모델
     */
    public class BrandInfo {
        public String brandLogoUrl;
        public List<TxtInfo> brandTitle;
        public String brandLinkUrl;
    }

    /**
     * 가격 영역 할인 정보
     */
    public class DiscountInfo {
        public List<TxtInfo> discountText;
        public List<TxtInfo> originPrc;
        public String discountAddInfoUrl;
    }

    /**
     * 프로모션 영역 정보 (vip, vvip 등)
     */
    public class GradePmoInfo {
        public String gradeImgUrl;
        public List<TxtInfo> gradeText;
    }

    /**
     * 카드 적립금 등 혜택 정보
     */
    public class CardList {
        public String templateType;
        public List<TxtInfo> pmoText;
        public List<TxtInfo> pmoPrc;
    }

    public class ItemList {
        public String templateType;
        public String iConUrl;
        public String linkUrl;
        public List<TxtInfo> pmoText;
        public List<TxtInfo> pmoPrc;
    }

    /**
     * 배송정보 리스트 모델
     */
    public static class DeliveryList {
        /**
         * 이미지 주소
         */
        public String mainImgUrl;

        /**
         * 더보기 링크 주소
         */
        public String addInfoUrl;

        /**
         * 툴팁 표시여부 ("Y" or "N")
         */
        public String addressTooltip;

        /**
         * 배송지변경 정보
         */
        public List<TxtInfo> changeAddress;

        /**
         * 배송정보 상세내역
         */
        public List<DeliveryInfoList> deliveryInfoList;
    }

    /**
     * 배송정보 노출할 텍스트 리스트 모델
     */
    public static class DeliveryInfoList {
        /**
         * 이미지 주소
         */
        public String preImgUrl;

        /**
         * 텍스트 정보
         */
        public List<TxtInfo> textList;

        /**
         * 도착확률 정보
         */
        public List<PrsnlTxtInfoList> percentTextList;
    }

    public class PersonalizationResult {

        public PrsnlPmoInfo prsnlName;

        public String tooltipYN;

        public List<PrsnlTxtInfoList> saleMaxList;

        public List<PrsnlTxtInfoList> juklibMaxList;

        public List<PrsnlTxtInfoList> moreInfoList;

        public List<PrsnlTxtInfoList> addSaleInfo;
    }

    /**
     * 텍스트정보 모델
     */
    public static class TxtInfo {
        /**
         * 타입
         */
        public String type = "";
        /**
         * 텍스트
         */
        public String textValue = "";

        /**
         * 폰트색
         */
        public String textColor = "";

        /**
         * 볼드여부 ("Y" or "N")
         */
        public String boldYN = "";

        /**
         * 폰트사이즈
         */
        public String size = "";

        /**
         * underline
         */
        public String point = "";

        /**
         * 링크 주소
         */
        public String linkUrl = "";
    }

    /**
     * 캐시된 이미지 URL
     */
    public String chachedImgUrl = null;

    /**
     * autoPlay 여부
     */
    public String autoPLay = "";
}

