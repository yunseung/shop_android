package gsshop.mobile.v2.mobilelive;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ImageBadgeCorner;

/**
 * 모바일라이브 API 호출결과 데이타 모델 - 상품리스트
 */
@JsonIgnoreProperties
public class MobileLivePrdsInfoList {
    public String dealNo;
    public String onAirYn;  //이거 왜 넣었지;
    public String directBuyUrl;
    public String previewUrl;
    public String linkUrl;
    public String imageUrl;
    public String productName;
    public String basePrice;
    public String salePrice;
    public String exposePriceText;
    public String discountRate;     //안
    public String productType;
    public String tempOut;          //일시품
    public String onAirSalePsblFlg; //아직은 안보지만 나중에 볼거
    public String liveBroadFlg;     //방송상품여부 나중에 볼거
    public ImageBadgeCorner infoBadge;
    public String dealStrDate;
    public String dealEndDate;

    public String rentalText;
    public String rentalPrice;
    public String attrCharVal15;
    public String mnlyRentCostVal;
    public boolean exposStartPrice;
    public String exposStartPriceTxt;
    public String directOrdText;    //바로구매, 상담신청

    //쇼핑라이브 4차에서 추가
    public String imageLayerFlag;   //일시품절, 방송중구매
    public List<ImageBadge> allBenefit;		//혜택 리스트
    public ImageBadge source;				//소싱

    //API와 무관한 앱 내부적으로 사용할 프라퍼티
    public int type = 1;    //0:더보기, 1:상품#1(default)
    public int moreCount;    //더보기 갯수
}
