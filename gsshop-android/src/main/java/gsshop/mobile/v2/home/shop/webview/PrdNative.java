package gsshop.mobile.v2.home.shop.webview;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

import javax.annotation.ParametersAreNullableByDefault;

@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class PrdNative {
    public static class PrdImgInfo{
        public String imgUrl;
    }
    public static class Prd {
        public String dlvCoNm;
        public String imgRcmdCtgrNo;
        public String dlvPackTxt;
        public String tvAlarmBtnFlg;
        public String vipCpnAmt;
        public String dlvPackLink;
        public String dlvAddInfo;
        public String tvDepartLabelNm;
        public String dlvAddAmt;
        public String dlvInfo;
        public String videoId;
        public String tvAlarmBtnOn;
        public String ordQty;
        public String tvAccmPmoFlg;
        public String pmoNm;
        public String brandShopLink;
        public String videoUrl;
        public String tvCashbackPmoFlg;
        public String onairFlg;
        public String broadType;
        public String ordQtyExposFlg;
        public String nextBroadTime;
        public String tvGiftPmoFlg;
        public String prdNm;
        public String vipCpnNo;
        public String adidasCd;
        public String vipPrice;
        public List<PrdImgInfo> prdImgInfo;
    }

    public static class Pmo {
        public int salePrc;
        public int gsPrc;
        public String cmmBannerImg;
        public String prcLabel;
        public String preCalcBtnFlg;
        public String cmmBannerLink;
        public String pmoBanner;
        public String noIntCardEvtFlg;
        public String cpnBtnFlg;
        public String cmmBannerBg;
        public String maxCardCoShrNointMm;
        public String noIntMmCnt;
        public String accmSumryInfo;

    }

    public Prd prd;
    public Pmo pmo;
}
