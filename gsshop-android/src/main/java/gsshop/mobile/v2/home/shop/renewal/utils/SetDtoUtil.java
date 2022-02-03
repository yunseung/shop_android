package gsshop.mobile.v2.home.shop.renewal.utils;

import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.home.shop.schedule.model.DirectOrdInfo;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.mobilelive.MobileLivePrdsInfoList;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

public class SetDtoUtil {
    // 컴포넌트 형태
    public enum BroadComponentType {
        live,           //생방송       구매하기
        data,           //데이타방송     구매하기
        mobilelive,     //모바일라이브    구매하기
        mobilelive_prd_list_type1, //모바일라이브 메인하단 상품
        mobilelive_prd_list_type2, //모바일라이브 더보기 클릭시 상품리스트 화면
        schedule,       //TV편성표        구매하기
        vod,             //내일TV         구매하기
        product,         //상품            없다.
        product_640,     //상품 640        640일경우 혜택 영역 한줄
        product_c_b1,    // 상품 개인화 형태 (PRD_C_B1 같은) -> 할인율 없는 형태.
        product_c_b1_vip_gba,    // VIP 라운지 매장내 찜한 브랜드 컴포넌트
        signature,        //시그니처매장내 상품컴포넌트
        tv_new
    }

    public static ProductInfo setDto(ModuleList data) {
        return setDto((SectionContentList)data);
    }

    public static ProductInfo setDto(SectionContentList data) {
        ProductInfo info = new ProductInfo();
        //상품정보
        info.productName = data.productName;
        info.linkUrl = data.linkUrl;

        //가격처리
        info.salePrice = data.salePrice;
        info.basePrice = data.basePrice;
        info.discountRate = data.discountRate;
        info.exposePriceText = data.exposePriceText;

        //구매버튼
        if (isNotEmpty(data.directOrdInfo)) {
            info.directOrdInfo = new DirectOrdInfo();
            info.directOrdInfo.text = data.directOrdInfo.text;
            info.directOrdInfo.linkUrl = data.directOrdInfo.linkUrl;
        }

        //렌탈 관련
        //상품 구조체 : 일반상품 컴포넌트
        info.rentalText = data.rentalText;
        info.rentalPrice = data.mnlyRentCostVal;
        info.productType = data.productType;

        //혜택 관련
        info.allBenefit = data.allBenefit;
        info.source = data.source;

        //솔드 아웃 관련 방송중 구매가능관련
        //생방송에 현재는 없다. 하지만 편성표에는 있다.
        info.imageLayerFlag = data.imageLayerFlag;

        //상품평 관련
        info.addTextLeft = data.addTextLeft;
        info.addTextRight = data.addTextRight;

        //브렌드 관련
        info.brandNm = data.brandNm;
        info.brandLinkUrl = data.brandLinkUrl;

        // 딜 여부 (true/false)
        info.deal = data.deal;

        info.basket = data.basket;

        return info;
    }

    public static ProductInfo setDto(SchedulePrd data) {
        ProductInfo info = new ProductInfo();
        //상품 정보
        info.prdId = data.product.prdId;
        info.productName = data.product.exposPrdName;
        info.linkUrl = data.product.linkUrl;

        //가격 정보
        info.salePrice = data.product.broadPrice;
        info.basePrice = data.product.salePrice;
        info.exposePriceText = data.product.exposePriceText;
        info.discountRate = data.product.priceMarkUp;

        //렌탈관련
        //방송 구조체에는 있습니다 렌탈 프라이스가 렌탈 상품 관
        info.productType = "Y".equals(data.product.rentalYn) ? "R" : "P";
        info.rentalText = data.product.rentalText;
        info.rentalPrice = data.product.rentalPrice;

        //바로구매 여부 확인
        //편성표에서 공통
        if (isNotEmpty(data.product.directOrdInfo)) {
            info.directOrdInfo = new DirectOrdInfo();
            info.directOrdInfo.text = data.product.directOrdInfo.text;
            info.directOrdInfo.linkUrl = data.product.directOrdInfo.linkUrl;
        }

        //라이브톡 여부 확인
        //편성표에서 공통
        if (isNotEmpty(data.liveTalkInfo)) {
            info.liveTalkYn = "Y";
            info.liveTalkText = data.liveTalkInfo.text;
            info.liveTalkUrl = data.liveTalkInfo.linkUrl;
        }

        //방송알림 여부 확인
        //편성표에서 공통
        info.broadAlarmDoneYn = data.product.broadAlarmDoneYn;     //방송 알림 여부
        info.exposPrdName = data.product.exposPrdName;			    //방송 알림에 필요

        //방송 구조체에는 있습니다 렌탈 프라이스가 렌탈 상품 관련
        info.rentalText = data.product.rentalText;		// 이부분이 "월 렌탈료" 그런데 가격이 엉망이면?? rValueText / rentalText  월 렌탈료가 들어 있음
        info.rentalPrice = data.product.rentalPrice;

        //편성표는 보험 어찌 처리한대???
        info.productType = "Y".equals(data.product.insuYn) ? "I" : "P";
        info.productType = "Y".equals(data.product.rentalYn) ? "R" : info.productType;

        //혜택 관련
        info.allBenefit = data.product.allBenefit;
        info.source = data.product.source;

        //솔드 아웃 관련 방송중 구매가능관련
        //생방송에 현재는 없다. 하지만 편성표에는 있다.
        info.imageLayerFlag = data.product.imageLayerFlag;

        //상품평 관련
        info.addTextLeft = data.product.addTextLeft;
        info.addTextRight = data.product.addTextRight;

        //브렌드 관련
        info.brandNm = data.product.brandNm;

        // 딜 여부 편성표는 무조건 상품
        info.deal = "false";

        //LIVE or VOD 여부
        info.pgmLiveYn = data.pgmLiveYn;

        return info;
    }

    public static ProductInfo setDto(MobileLivePrdsInfoList data) {
        ProductInfo info = new ProductInfo();
        //상품정보
        info.productName = data.productName;
        info.linkUrl = data.linkUrl;

        //가격처리
        info.salePrice = data.salePrice;
        info.basePrice = data.basePrice;
        info.discountRate = data.discountRate;
        info.exposePriceText = data.exposStartPriceTxt;

        //혜택 관련
        info.allBenefit = data.allBenefit;
        info.source = data.source;

        //솔드 아웃 관련 방송중 구매가능관련
        //생방송에 현재는 없다. 하지만 편성표에는 있다.
        info.imageLayerFlag = data.imageLayerFlag;

        //구매버튼
        if (isNotEmpty(data.directBuyUrl)) {
            info.directOrdInfo = new DirectOrdInfo();
            info.directOrdInfo.text = data.directOrdText;
            info.directOrdInfo.linkUrl = data.directBuyUrl;
        }

        //렌탈 관련
        //상품 구조체 : 일반상품 컴포넌트
        info.rentalText = data.rentalText;
        info.rentalPrice = data.mnlyRentCostVal;
        info.productType = data.productType;

        return info;
    }
}
