/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main

import com.google.common.collect.ImmutableSortedSet
import com.gsshop.mocha.pattern.mvc.Model
import gsshop.mobile.v2.home.shop.schedule.model.DirectOrdInfo
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import javax.annotation.ParametersAreNullableByDefault

/**
 * 메인 화면 상품 리스트
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
open class SectionContentList {
    @JvmField
    var dealNo: String? = null

    @JvmField
    var imageUrl: String? = null

    @JvmField
    var linkUrl: String? = null

    @JvmField
    var playUrl: String? = null

    @JvmField
    var productName: String? = null

    @JvmField
    var promotionName: String? = null

    @JvmField
    var discountRate: String? = null // 할인률(숫자)

    @JvmField
    var cartDiscountRateText: String? = null // VIP 장바구니 할인률 (3% 가격내림 등)
    
    @JvmField
    var discountRateText: String? = null // 할인관련 텍스트(GS가,1+1,앵콜 등)

    @JvmField
    var salePrice: String? = null // 할인가

    @JvmField
    var basePrice: String? = null // 기존가

    @JvmField
    var saleQuantity: String? = null // 판매수량 or 관련텍스트

    @JvmField
    var saleQuantityText: String? = null

    @JvmField
    var saleQuantitySubText: String? = null // "판매중"

    @JvmField
    var exposePriceText: String? = null // "원", "원~", "원부터~" 등등

    @JvmField
    var productType: String? = null // 상품유형(P-일반,F-식품,R-렌탈)

    @JvmField
    var isTempOut: String? = null  // 일시품절여부

    @JvmField
    var prdid: String? = null

    @JvmField
    var hasVod: String? = null

    @JvmField
    var sbCateGb: String? = null

    @JvmField
    var sbVideoNum: String? = null

    /**
     * 사용 불가 ( 안드로이드에서 사용하기 위해 title이라는 이름을
     * 만들어놓은걸로 추정... title은 일반적인 네이밍이라 안드로이드에서 사용하기 위해서 쓰는
     * 특정 네이밍(:gsAccessibilityVariable) 으로 변경 )
     */
    @JvmField
    var gsAccessibilityVariable: String? = null

    @JvmField
    var cateGb: String? = null

    @JvmField
    var wiseLog: String? = null

    @JvmField
    var wiseLog2: String? = null

    @JvmField
    var viewType: String? = null // 광고상품 관련 추가 (L:이미지 + 하단 상품명/프로모션명 표시, I:이미지만 표시)
    var dealType:String? = null // 딜타입, 맞춤추천 아이콘 표시위해 추가

    @JvmField
    var valueText: String? = null
    var isNo1Schedule: String? = null
    var no1ScheduleUrl: String? = null

    @JvmField
    var valueInfo: String? = null

    @JvmField
    var videoTime: String? = null //VOD 재생시간
    
    @JvmField
    var startDate: Long? = null // 쇼핑라이브 방송 시작 시간

    @JvmField
    var etcText1: String? = null

    @JvmField
    var adultCertYn: String? = null

    @JvmField
    var videoid: String? = null //brightcove에 등록된 비디오번호

    @JvmField
    var vodImg: String? = null //브라이트코드 섬네일 이미지

    @JvmField
    var dealMcVideoUrlAddr: String? = null

    /* 엄마에 딸려진 자식들 내가 본상품을 본사람들 추천 상품 */ // navigation v1.6부터 subContentChild에서 subProductList로 이름 변경됨
    @JvmField
    var subProductList: ArrayList<SectionContentList>? = null

    @JvmField
    var subContentChild: ArrayList<SectionContentList>? = null

    @JvmField
    var isMore = true // gs x brand 배너 더보기 버튼

    @JvmField
    var isGoBrandShop = false // GS Fresh 바로가기 버튼 노출 여부

    @JvmField
    var adDealYn: String? = null  // ad 구좌 표시 여부

    @JvmField
    var randomYn: String? = null

    @JvmField
    var isRandomComplete = false

    @JvmField
    var rollingDelay = 0f

    /* 상품이미지에 표기되는 벳지 구조체 LT(Left Top),RT(Right Top), LB(Left Bottom),RB(Right Bottom)을 구성하여 위치에 따라
	 * 벳지 노출되는 벳지의 성격에 따라 type을 생성하여 로컬이미지 혹은 imgUrl, text를 노출한다. 벳지는 동일 위치에 N개 이상이 노출 될수 있으며, 좌,우
	 * 정렬기준으로 순서대로 노출한다. <이벤트 탭에서 사용되는 벳지 타입> 커뮤니티=comm, 핫이슈=hot, 혜택=bene */
    @JvmField
    var imgBadgeCorner: ImageBadgeCorner? = null

    @JvmField
    var infoBadge: ImageBadgeCorner? = null

    @JvmField
    var index = 0

    @JvmField
    var productList: ArrayList<SectionContentList>? = null

    /**
     * tv쇼핑
     */
    @JvmField
    var broadType: String? = null // TV생방송 or TV베스트

    @JvmField
    var broadScheduleLinkUrl: String? = null // 편성표

    @JvmField
    var broadStartTime: String? = null

    @JvmField
    var broadCloseTime: String? = null

    @JvmField
    var isCellPhone: String? = null // 폰이나 렌탈이면 가격 대신 "상담전용 상품입니다" 표시

    @JvmField
    var isRental: String? = null
    var accmText: String? = null // 적립금 텍스트
    var accmUrl: String? = null // 적립금 url
    var orderQuantity: String? = null
    var noIntYn: String? = null
    var noIntMmCnt: String? = null
    var accAmtYn: String? = null
    var accAmt: String? = null

    @JvmField
    var liveBenefitsYn: String? = null

    @JvmField
    var liveBenefitsText: String? = null
    var brodScheduleYn: String? = null
    var brodScheduleText: String? = null
    var rightNowBuyYn: String? = null
    var rightNowBuyUrl: String? = null

    @JvmField
    var imageList: ImageList? = null

    @JvmField
    var isWish = false // zzim = false

    @JvmField
    var isWishEnable = false // zzim 노출 여부 = false

    @JvmField
    var brandWishAddUrl: String? = null

    @JvmField
    var brandWishRemoveUrl: String? = null

    @JvmField
    var myWishUrl: String? = null

    @JvmField
    var isShow = false

    @JvmField
    var dealProductType: String? = null

    // nalbang's product hashtags
    @JvmField
    var productHashTags: List<String>? = null

    //hashtags wiselog url
    @JvmField
    var productHashTagsWiseLog: List<String?>? = null

    // sorted hash tags of nalbang product type
    @Transient
    @JvmField
    var sortedProductHashTags: ImmutableSortedSet<String?>? = null

    // 방송중 구매 상픔 - AIR_BUY, 일시품절 - SOLD_OUT
    @JvmField
    var imageLayerFlag: String? = null

    //shortbang
    @JvmField
    var directOrdUrl: String? = null

    @JvmField
    var snsImageUrl //공유용 이미지 주소
            : String? = null

    @JvmField
    var snsLinkUrl //이벤트숏방 공유시 랜딩 주소
            : String? = null

    @JvmField
    var height: String? = null

    @JvmField
    var rwImgList: ArrayList<String?>? = null

    // GSSuper 장바구니 params
    @JvmField
    var basket: Basket? = null

    //상품 대제목 (GS Choice 에서 추가 되었는데, ProductList에 들어 온다.)
    @JvmField
    var exposPrSntncNm: String? = null

    //바로구매 AB 테스트를 위해 상품구조체에서 추가
    @JvmField
    var directOrdInfo: DirectOrdInfo? = null

    //방송알림 AB 테스트 by hanna ( Y, N, H)
    var broadAlarmDoneYn: String? = null

    //lnk_gbb
    @JvmField
    var bdrBottomYn: String? = null

    @JvmField
    var tabImg: String? = null

    @JvmField
    var textColor: String? = null

    //renewal 추가되는 값
    // badgeRTType, moreText 괜히 왜 만듦?
    @JvmField
    var badgeRTType: String? = null // MORE : 더보기 표시 (선택시 linkUrl 로 이동), AD : '광고' 표시

    @JvmField
    var moreText: String? = null // 더보기 텍스트

    @JvmField
    var allBenefit: List<ImageBadge>? = null //혜택을 리스트

    @JvmField
    var source: ImageBadge? = null //소싱

    @JvmField
    var broadTimeText: String? = null //방송 시간 표시
    
    @JvmField
    var alarmText: String? = null // VIP Lounge에 새로 적용된 방송 예정 노출

    @JvmField
    var brandNm: String? = null //브렌드 네임

    @JvmField
    var brandImg: String? = null //브렌드 이미지

    @JvmField
    var brandLinkUrl: String? = null //브렌드 링크 주소

    @JvmField
    var addTextLeft: String? = null //상품평점

    @JvmField
    var addTextRight: String? = null //상품평 갯수

    @JvmField
    var moreBtnUrl: String? = null //새 DTO 에서 이쪽으로 이사옴.

    //렌탈 관련된 상품 처리를 위한 방송과 동일하게 만들기
    //==>
    @JvmField
    var rentalText: String? = null //편성/생방 "월 렌탈료"

    @JvmField
    var mnlyRentCostVal: String? = null //편성/생방 "mnlyRentCostVal"

    @JvmField
    var deal: String? = null

    // *PMO_T1_PREVIEW_B * ModuleList와 SectionContentList에 동일한 View가 생김에 따라
    // 함꼐 사용 위해 함꼐 사용하는 부분은 SectionContentList로 옮김
    @JvmField
    var name: String? = null // 헤드카피

    @JvmField
    var subName: String? = null // 서브카피

    @JvmField
    var bgColor: String? = null // 백그라운드 Color

    @JvmField
    var tabBgImg: String? = null // 백그라운드 이미지

    @JvmField
    var textImageModule: TextImageModule? = null // 프로모션 카피 1,2, 텍스트 색상 (A/B/C)

    @JvmField
    var ampEventName: String? = null //홈T3이미지 AB테스트 & 팀장님 이미지 AB테스트를 구별하기 위한 변수

    @JvmField
    var useName: String? = null // 홈매장 타이틀배너 사용자 이름 노출 (로그인 -> OOO님 / 비로그인 -> 고객님)

    // 2020-06-19 yun. GS X Brand 의 경우 api 가공을 하는데, prd2 뒤에 상품 더보기 항목을 만들어서 붙이기 때문에
    // prd2 viewHolder 하단에 margin 을 1로 설정하냐 10으로 설정하냐 하는 isSameToNext 를 강제로 셋팅해주는 변수.
    @JvmField
    var isSameItem = false

    @JvmField
    var wishCnt: String? = null // PMO_T2_A 에서 추가된 찜 카운트

    @JvmField
    var tabSeq: String? = null

    @JvmField
    var isShowAllForPrdMltGba = false

    companion object {
        /**
         * deal type, - Product, Deal
         */
        const val DEAL_TYPE_PRODUCT = "Product"
        const val DEAL_TYPE_DEAL = "Deal"
    }

    @JvmField
    var customTitle: ArrayList<CustomTitle>? = null

    @JvmField
    var customBenefit: ArrayList<ArrayList<CustomTitle>>? = null

    class CustomTitle {
        /**
        text: ${출력될 텍스트}
        point: ${밑줄 표시, 선언되지 않으면 N}
        pointColor: ${밑줄 색상, 선언되지 않으면 기본 point 색상}
        textColor: ${강조 색상 선언되지 않으면 기본 색상}
        boldYN: ${bold여부, 선언되지 않으면 N}
        size: ${text 크기, 선언되지 않으면 기본 사이즈}
        linkUrl: ${해당 text에 link가 걸려있을 시에 이동 URL 없으면 이동하지 않는다.}
         **/
        @JvmField
        var text: String? = null
        @JvmField
        var point: String? = null
        @JvmField
        var pointColor: String? = null
        @JvmField
        var textColor: String? = null
        @JvmField
        var boldYN: String? = null
        @JvmField
        var size: String? = null
        @JvmField
        var linkUrl: String? = null
    }

    @JvmField
    var customSubTitle: ArrayList<CustomSubTitle>? = null //CustomTitleDTLayout의 서브타이틀  ex)#편한 #신발 #나이키

    class CustomSubTitle {
        @JvmField
        var type: String? = null //텍스트의 타입 ex)sharp이면 "#텍스트"
        @JvmField
        var text: String? = null
    }

    @JvmField
    var mktInfo: MktInfo? = null

    @JvmField
    var titleImgUrl: String? = null

    @JvmField
    var vipCardDiscount: ArrayList<VipCardDiscount>? = null
    // VIP 카드 할인 표시
    class VipCardDiscount {
        @JvmField
        var text: String? = null
        @JvmField
        var imgUrl: String? = null
        @JvmField
        var addInfoList: ArrayList<String>? = null
    }

    @JvmField
    var pgmLiveYn: String? = null

    @JvmField
    var streamViewCount: String? = null

    // 쇼핑 라이브 매장용 타이틀 이미지 표시 여부
    @JvmField
    var showLiveIcon: String? = null

    // 쇼핑 라이브 매장용 타이틀 아래 라인 표시 여부
    @JvmField
    var showUnderLine: String? = null

    // 쇼핑 라이브 매장용 알람 기간문구
    @JvmField
    var alarmPeriod: String? = null

    @JvmField
    var newYN: String? = null

    @JvmField
    var hotYN: String? = null

    /**
     * 와인 정보 추가됨.
     */
    // 평점
    @JvmField
    var reviewAverage: String? = null
    // 평점 수
    @JvmField
    var reviewCount: String? = null
    // 와인 타입
    @JvmField
    var type: String? = null
    // 와인 원산지
    @JvmField
    var nation: String? = null
    // 와인 브랜드 (subName)
    @JvmField
    var exposEngPrdNm: String? = null

    //종료 날짜 표시
    @JvmField
    var endDate: Long? = null

    // mesq 값
    @JvmField
    var mseq: String? = null

    // 갱신 URL (moduleList 에서 부모로 옮겨옴)
    @JvmField
    var ajaxPageUrl: String? = null

    // 이벤트 하면서 새로운 key를 또 만들어 낸다.(moduleList 에서 부모로 옮겨옴)
    @JvmField
    var title1: String? = null
    @JvmField
    var title2: String? = null
    @JvmField
    var moduleList: ArrayList<ModuleList>? = null
}