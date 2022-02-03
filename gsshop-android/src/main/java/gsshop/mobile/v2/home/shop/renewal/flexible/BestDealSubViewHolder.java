package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.apptimize.Apptimize;

import org.json.JSONException;
import org.json.JSONObject;

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.shop.schedule.model.Product;
import gsshop.mobile.v2.intro.ApptimizeCommand;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * 홈생방 부상품 뷰홀더
 * TV편성표 3차 - A타입 부상품 뷰홀더
 */
public class BestDealSubViewHolder extends RenewalPriceInfoBottomViewHolder_HOMESUB {
    /**
     * 부상품 섬네일
     */
    private ImageView prdImage;
    private View view;
    private Context subContext;
    private View prdView;
    private String broadType; //LIVE or SCHEDULE (홈생방, 편성표AB테스트 A타입 부상품 구분)

    /**
     * 홈생방 or 편성표 A타입 영역 구분
     */
    private String SCHEDULE_TYPE = "SCHEDULE";
    private String LIVE_TYPE = "LIVE";

    public BestDealSubViewHolder(View itemView, Context context, String broadType) {
        super(itemView);
        this.view = itemView;
        this.subContext = context;
        this.broadType = broadType;
        prdImage = view.findViewById(R.id.image_prd);
        prdView = view.findViewById(R.id.view_prd);
    }

    public View onBindViewHolder(int position, Product data){
        ImageUtil.loadImage(subContext, data.subPrdImgUrl, prdImage, R.drawable.noimage_166_166);

        //부상품 전체영역에 대한 링크
        if (isNotEmpty(data.linkUrl)) {
            prdView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(subContext, data.linkUrl);

                    if(SCHEDULE_TYPE.equals(broadType)){
                        // TV편성표 AB테스트 주상품 효율
                        // AB대상이면서 O타입, AB대상이면서 A타입만 해당되도록
                        if( ApptimizeCommand.ABINFO_VALUE.contains(ApptimizeExpManager.SCHEDULE)){
                            //앰플리튜드를 위한 코드
                            try {
                                JSONObject eventProperties = new JSONObject();
                                eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.AMP_CLICK_SCHEDULE_PRD);

                                if(data != null) {
                                    eventProperties.put(AMPEnum.AMP_PRD_CODE, data.prdId);
                                    eventProperties.put(AMPEnum.AMP_PRD_NAME, data.prdName);
                                    eventProperties.put(AMPEnum.AMP_AB_DETAIL_TYPE, ApptimizeExpManager.SUB_PRD);
                                    eventProperties.put(AMPEnum.AMP_AB_INFO, ApptimizeCommand.ABINFO_VALUE);
                                }

                                AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_SCHEDULE_PRD,eventProperties);
                            } catch (JSONException exception){

                            }
                            //앱티마이즈를 위한 코드
                            Apptimize.track(AMPEnum.AMP_CLICK_SCHEDULE_PRD);
                        }
                    }
                }
            });
        }

        //가격표시용 공통모듈에 맞게 데이타 변경
        TvLiveBanner tvLiveBanner = new TvLiveBanner();
        tvLiveBanner.rProductName = data.exposPrdName;
        tvLiveBanner.rSalePrice = data.broadPrice;
        tvLiveBanner.rBasePrice = data.salePrice;
        tvLiveBanner.rExposePriceText = data.exposePriceText;
        tvLiveBanner.rDiscountRate = data.priceMarkUp;
        tvLiveBanner.rLinkUrl = data.linkUrl;
        tvLiveBanner.rSource = data.source;
        tvLiveBanner.rAllBenefit = data.allBenefit;

        //무형상품
        tvLiveBanner.rProductType = "Y".equals(data.insuYn) ? "I" : "P";
        tvLiveBanner.rProductType = "Y".equals(data.rentalYn) ? "R" : tvLiveBanner.rProductType;
        tvLiveBanner.rRentalPrice = data.rentalPrice;
        tvLiveBanner.rRentalText = data.rentalText;
        tvLiveBanner.deal = "false"; //홈 부상품은 항상 상품
        tvLiveBanner.rImageLayerFlag = data.imageLayerFlag;

        super.bindViewHolder(tvLiveBanner, position,null);

        return view;
    }
}