package gsshop.mobile.v2.home.shop.renewal.utils.dto

import com.gsshop.mocha.pattern.mvc.Model
import gsshop.mobile.v2.home.main.Basket
import gsshop.mobile.v2.home.main.ImageBadge
import gsshop.mobile.v2.home.shop.schedule.model.DirectOrdInfo
import javax.annotation.ParametersAreNullableByDefault

@Model
@ParametersAreNullableByDefault
class ProductInfo {
    @JvmField
    var prdId: String? = null
    @JvmField
    var pgmLiveYn: String? = null //PGM Y 일때 wiseLog 가 다르다.
    @JvmField
    var productName: String? = null
    @JvmField
    var exposEngPrdNm: String? = null
    @JvmField
    var linkUrl: String? = null

    //가격처리
    @JvmField
    var salePrice: String? = null
    @JvmField
    var basePrice: String? = null
    @JvmField
    var discountRate: String? = null
    @JvmField
    var exposePriceText: String? = null

    //상품 바로구매 표시
    @JvmField
    var directOrdInfo: DirectOrdInfo? = null

    //렌탈 관련
    //상품 구조체 : 일반상품 컴포넌트
    @JvmField
    var rentalText: String? = null
    @JvmField
    var rentalPrice: String? = null
    @JvmField
    var productType: String? = null

    //혜택 관련
    @JvmField
    var allBenefit: List<ImageBadge>? = null
    @JvmField
    var source: ImageBadge? = null

    //솔드 아웃 관련 방송중 구매가능관련
    //생방송에 현재는 없다. 하지만 편성표에는 있다.
    @JvmField
    var imageLayerFlag: String? = null

    //상품평 관련
    @JvmField
    var addTextLeft: String? = null
    @JvmField
    var addTextRight: String? = null

    //브렌드 관련
    @JvmField
    var brandNm: String? = null
    @JvmField
    var brandLinkUrl: String? = null

    // 딜 여부 (true/false)
    @JvmField
    var deal: String? = null

    //방송 라이브톡 여부 확인
    @JvmField
    var liveTalkYn: String? = null
    @JvmField
    var liveTalkText: String? = null
    @JvmField
    var liveTalkUrl: String? = null

    //방송 방송알림 여부 확인 및 방송알림을 위한 컬럼
    @JvmField
    var broadAlarmDoneYn: String? = null
    @JvmField
    var exposPrdName: String? = null //방송알림때 필요한 값
    @JvmField
    var basket: Basket? = null // 장바구니 버튼
}