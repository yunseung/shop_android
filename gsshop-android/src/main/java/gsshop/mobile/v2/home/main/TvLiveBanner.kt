/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main

import com.gsshop.mocha.pattern.mvc.Model
import gsshop.mobile.v2.home.shop.schedule.model.DirectOrdInfo
import gsshop.mobile.v2.home.shop.schedule.model.Product
import gsshop.mobile.v2.home.shop.tvshoping.LivePlayInfo
import gsshop.mobile.v2.home.shop.tvshoping.LiveSalesCountInfo
import gsshop.mobile.v2.home.shop.tvshoping.TvShopFragment.SubTabMenu
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import java.util.*
import javax.annotation.ParametersAreNullableByDefault

/**
 * 메인 TV생방송 정보
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
open class TvLiveBanner {
    @JvmField
    var broadType: String? = null // TV생방송 or TV베스트
    @JvmField
    var imageUrl: String? = null
    @JvmField
    var linkUrl: String? = null
    @JvmField
    var playUrl: String? = null
    @JvmField
    var productName: String? = null
    @JvmField
    var salePrice: String? = null
    @JvmField
    var exposePriceText: String? = null // default "원"
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
    @JvmField
    var rentalEtcText: String? = null
    var accmText: String? = null // 적립금 텍스트
    var accmUrl: String? = null // 적립금 url
    var orderQuantity: String? = null
    var noIntYn: String? = null
    var noIntMmCnt: String? = null
    var accAmtYn: String? = null
    var accAmt: String? = null
    var liveBenefitsYn: String? = null
    var liveBenefitsText: String? = null
    @JvmField
    var brodScheduleYn: String? = null
    @JvmField
    var brodScheduleText: String? = null
    @JvmField
    var rightNowBuyYn: String? = null
    @JvmField
    var rightNowBuyUrl: String? = null
    @JvmField
    var priceMarkUp: String? = null //"생방송/n모바일가",
    @JvmField
    var priceMarkUpType: String? = null //: "LIVEPRICE",
    var cardChrDcPrice: String? = null //: 64600,
    var cardChrDcName: String? = null //: "삼성",
    var cardChrDcRate: String? = null //: 5,
    var cardChrDcText: String? = null //: "청구할인가",
    @JvmField
    var liveTalkYn: String? = null
    @JvmField
    var liveTalkText: String? = null
    @JvmField
    var liveTalkUrl: String? = null
    @JvmField
    var rentalText: String? = null
    @JvmField
    var rentalPrice: String? = null
    @JvmField
    var basePrice: String? = null
    @JvmField
    var rwImgList: ArrayList<String>? = null

    /*
    jhkim 16.03.22
    기존 accAmt , brodSchedule, rightNowBuy 버튼 확장성을 위해 api 수정
    이미지 아래 버튼 정보
     */
    @JvmField
    var btnInfo3: TvLiveBtnInfo? = null
    @JvmField
    var isTvShoping = false
    @JvmField
    var currentSubTabMenu: SubTabMenu? = null
    @JvmField
    var tvLiveUrl: String? = null

    //20150708 parksegun add -start
    var liveBenefitText: String? = null //생방송 해택 정보텍스트
    var isaccm: String? = null //적립금 여부
    var isLive: String? = null //생방송 보기 여부
    var liveText: String? = null //생방송 버튼 텍스트
    var liveUrl: String? = null //생방송 스키마 URL
    var isbroadSchedule: String? = null //편성표 노출 여부
    var broadScheduleText: String? = null //편성표 버튼 텍스트
    var accmValue: String? = null //적립금
    var isInterestFree: String? = null // 무이자 여부
    var interestFreeMonths: String? = null //무이자 개월수
    @JvmField
    var banner: SectionBanner? = null //생방속 영역과 최근 편성 내용 중간에 들어가는 베너
    @JvmField
    var salesInfo: LiveSalesCountInfo? = null
    @JvmField
    var livePlay: LivePlayInfo? = null

    /*
    *생방송 해택 정보텍스트
    *적립금 여부
	*적립금 금액
	*생방송 보기 여부
	*생방송 버튼 텍스트
	*생방송 스키마 URL (toapp://livestreaming?type=동영상구분자&url=동영상파일주소&targeturl=상품페이지주소) type값은 동영상 위에 표시할 UI가 상이할 경우를 대비한 구분자임 (값이 넘어오지 않을 경우 디폴트 값은 "PRD_PLAY")
							- 예제 : toapp://livestreaming?type=PRD_PLAY&url=http://mobilevod.gsshop.com/beauty/5416.mp4&targeturl=http://m.gsshop.com/prd/prd.gs?fromApp=Y&prdid=329323
							- 주의 : 웹에서 targeturl을 인코딩해서 호출해야 함 (인코딩 안할 경우 두번째 파라미터부터 누락됨)
	*편성표 노출 여부
	*편성표 버튼 텍스트
	*무이자 여부
	*무이자 개월수

	*판매수량조회
	{
		*판매수량 정보 노출 여부
		*현재판매수량
		*공급수량
		*현재판매율
	}

	* 베너 -> 생방송 영역과 최근 편성표 사이에 들어가는 베너 없다면 null
	*/
    var liveTalkBanner: LiveTalkBanner? = null //라이브톡 배너
    @JvmField
    var totalPrdViewLinkUrl: String? = null //전체상품보기 링크

    //renewal 에 의해 추가되는값
    var badgeRTType: String? = null // MORE : 더보기 표시 (선택시 linkUrl 로 이동), AD : '광고' 표시
    var moreText // 가장 마지막 상품이 더보기 인지 여부 (API_SRL_PRD / PRD_C_SQ_PRD : 상품, API_SRL_MORE / PRD_C_SQ_MORE : 더보기)
            : String? = null
    @JvmField
    var allBenefit //혜택을 리스트
            : List<ImageBadge>? = null
    @JvmField
    var source //소싱
            : ImageBadge? = null
    var broadTimeText //방송 시간 표시//상품평 관련
            : String? = null
    @JvmField
    var brandNm //브렌드 네임
            : String? = null

    /**
     * //상품구조체(productList)와 방송정보 구조체(tvLiveBanner) 서로 매핑되지 않는 요소 새로 정의
     */
    //브렌드 네임 관련
    @JvmField
    var rBrandNm //브렌드 네임
            : String? = null

    //상품 햬택 영역을 위해 존재
    @JvmField
    var rAllBenefit: List<ImageBadge>? = null
    @JvmField
    var rSource: ImageBadge? = null

    //상품  영역 표시
    @JvmField
    var rProductName: String? = null
    @JvmField
    var rLinkUrl: String? = null

    //상품 바로구매 표시
    @JvmField
    var rDirectOrdInfo: DirectOrdInfo? = null

    //방송 라이브톡 여부 확인
    @JvmField
    var rLiveTalkYn //: null,
            : String? = null
    @JvmField
    var rLiveTalkText //: null,
            : String? = null
    @JvmField
    var rLiveTalkUrl: String? = null

    //방송 방송알림 여부 확인 및 방송알림을 위한 컬럼
    @JvmField
    var rBroadAlarmDoneYn //방송알림 가능 여부
            : String? = null
    @JvmField
    var rExposPrdName //방송알림때 필요한 값
            : String? = null

    //방송중 구매가능, 솔드아웃 처리
    @JvmField
    var rImageLayerFlag //솔드아웃 / 방송중 구매가능
            : String? = null
    @JvmField
    var addTextLeft //판매수량or상품평
            : String? = null
    @JvmField
    var addTextRight //판매수량or상품평
            : String? = null

    /**
     * 안쓰일수 있다
     */
    //public String rDiscountRateText;        //방송기준 : "RATE" 일떄 나머지는 이용하지 않음
    //효율 나의 상태값에 의해 효율들이 달라져야 하는아이들
    @JvmField
    var rPrdId //PGM Y 일때 wiseLog 가 다르다. 조합시 필요
            : String? = null
    @JvmField
    var rPgmLiveYn //PGM Y 일때 wiseLog 가 다르다.
            : String? = null

    //효율 내가 어느 매장에 있느냐의 따라 바꾸어야 하는 호율들
    //...
    //상품 가격 관련
    @JvmField
    var rSalePrice: String? = null
    @JvmField
    var rBasePrice: String? = null
    @JvmField
    var rExposePriceText: String? = null
    @JvmField
    var rProductType //렌탈 여부가 아닌 상품 여부로 바꾸자 P일반 R렌탈 C휴대폰 I보험 없으면 일반 아하그렇구나 같은 공영방송애들은 정의 할 예정
            : String? = null
    @JvmField
    var rRentalText //렌탈을 구분하기 위한 정보 "월 렌탈료" 가 붙으면 화면에서는 "월" + 가격 or 가격이 없다면 뭉게고 "상담전용상품입니다
            : String? = null
    @JvmField
    var rRentalPrice //이놈을 모르겟네
            : String? = null
    @JvmField
    var rDiscountRate //방송기준 " RATETEXT=="RATE" 일때만 ,숫자 입니다
            : String? = null

    //tvLiveBanner.rRentalEtcText = data.product.rentalEtcText;	//이거 살리지 않습니다 ..
    //tvLiveBanner.rIsCellPhone =  "Y".equals(data.product.cellPhoneYn) ? "true" : "false";
    //상품평점 관련
    @JvmField
    var rAddTextLeft: String? = null
    @JvmField
    var rAddTextRight: String? = null

    // isDeal 을 바라보는 로직이 추가 되어, 공통으로 쓰는곳에서 사용하기 위해 isDeal 추가.
    @JvmField
    var deal: String? = null

    //홈 생방 부상품 승자적용
    @JvmField
    var currBroadSubPrdList: MutableList<Product>? = null

    //모바일라이브 방송전후 1시간 동안에 표시할 텍스트
    @JvmField
    var broadInfoText: String? = null

    //모바일라이브 시청자 수
    @JvmField
    var streamViewCount: String? = null
}