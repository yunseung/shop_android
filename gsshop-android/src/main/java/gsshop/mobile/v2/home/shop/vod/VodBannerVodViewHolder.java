package gsshop.mobile.v2.home.shop.vod;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * vod common view holder
 */
public class VodBannerVodViewHolder extends BaseViewHolder {

    protected SectionContentList content = null;

    /**
     * subProduct에 있는 상품정보
     */
    protected SectionContentList prdItem = null;

    /**
     * 루트뷰
     */
    private View lay_top_root;

    /**
     * 섬네일 이미지
     */
    private ImageView img_top_sumbnail;

    /**
     * 타이틀
     */
    private TextView txt_top_title;

    /**
     * 하단 상품정보
     */

    /**
     * AB테스트 -> 바로구매, 장바구니 할때는 private FrameLayout rowGoods로 해야함
     */
    private LinearLayout rowGoods;

    private ImageView img_sumbnail;
    private TextView promotionName;
    private TextView txtTitle;
    private TextView txt_base_price;
    private TextView txt_base_price_unit;
    private TextView txtPrice;
    private TextView txt_price_unit;
    private TextView txt_badge_per;
    private TextView txt_sales_quantity;
    private LinearLayout layoutSoldout;
    private TextView valueInfo;
    private ImageView img_brd_kind;
    private View tag_container;
    private ImageView[] badge = new ImageView[3];

    private TextView txt_buy;
    private String BUY_NOW = "구매하기";
    private String GO_CONSULT = "상담신청";


    private SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
    private SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
    private ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
    private AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

    protected OnMediaPlayerController player;

    /**
     * 노출정보
     */
    private int visiblePercent = 0;

    /**
     * @param itemView
     */
    public VodBannerVodViewHolder(View itemView) {
        super(itemView);

        /**
         * 상단 타이틀 영역
         */
        lay_top_root = itemView.findViewById(R.id.lay_top_root);
        img_top_sumbnail = itemView.findViewById(R.id.img_top_sumbnail);
        txt_top_title = itemView.findViewById(R.id.txt_top_title);

        /**
         * 하단 상품정보
         */
        rowGoods = itemView.findViewById(R.id.product_layout);
        img_sumbnail = itemView.findViewById(R.id.img_sumbnail);
        txtTitle = itemView.findViewById(R.id.txt_title);
        promotionName = itemView.findViewById(R.id.promotionName);
        txt_badge_per = itemView.findViewById(R.id.txt_badge_per);
        layoutSoldout = itemView.findViewById(R.id.sold_out);
        valueInfo = itemView.findViewById(R.id.valueInfo);

        txt_base_price = itemView.findViewById(R.id.txt_base_price);
        txt_base_price_unit = itemView.findViewById(R.id.txt_base_price_unit);

        txtPrice = itemView.findViewById(R.id.txt_price);
        txt_price_unit = itemView.findViewById(R.id.txt_price_unit);
        txt_sales_quantity = itemView.findViewById(R.id.txt_sales_quantity);

        img_brd_kind = itemView.findViewById(R.id.img_brd_kind);

        txt_buy = itemView.findViewById(R.id.txt_buy);



        //혜택 태그
        tag_container = itemView.findViewById(R.id.tag_container);
        badge[0] = itemView.findViewById(R.id.tag_save);
        badge[1] = itemView.findViewById(R.id.tag_cash);
        badge[2] = itemView.findViewById(R.id.tag_gift);
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);

        content = info.contents.get(position).sectionContent;

        /**
         * 타이틀 영역
         */
        lay_top_root.setVisibility(View.GONE);
        if (content.subProductList != null && content.subProductList.size() >= 2 && content.subProductList.get(1) != null) {
            lay_top_root.setVisibility(View.VISIBLE);

            SectionContentList topItem = content.subProductList.get(1);
            ImageUtil.loadImage(context, topItem.imageUrl, img_top_sumbnail, 0);
            txt_top_title.setText(topItem.productName);
//            txt_top_title.setContentDescription(topItem.productName);

            lay_top_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, topItem.linkUrl);
                }
            });
        }

        /**
         * 상품영역
         */
        if (content.subProductList == null || content.subProductList.size() == 0 || content.subProductList.get(0) == null) {
            return;
        }

        prdItem = content.subProductList.get(0);

        titleStringBuilder.clear();
        priceStringBuilder.clear();
        txtTitle.setText("");
        txtPrice.setText("");
        txt_badge_per.setText("");
        valueInfo.setText("");
        txt_sales_quantity.setText("");
        txt_badge_per.setVisibility(View.GONE);
        txt_base_price.setVisibility(View.GONE);
        txt_base_price_unit.setVisibility(View.GONE);

        //방송종류
        if ("LIVE".equals(content.cateGb)) {
            //img_brd_kind.setText("라이브");
            img_brd_kind.setImageResource(R.drawable.tit_live_android);
        }else if("DATA".equals(content.cateGb)){
            //img_brd_kind.setText("마이샵");
            img_brd_kind.setImageResource(R.drawable.tit_myshop_android);
        }else{
            img_brd_kind.setVisibility(View.GONE);
        }

        if(prdItem.infoBadge != null && prdItem.infoBadge.TF != null && !prdItem.infoBadge.TF.isEmpty()){
            titleStringBuilder.append(prdItem.infoBadge.TF.get(0).text);
            titleStringBuilder.append(" ");
            try {
                if(StringUtils.checkHexColor("#" + prdItem.infoBadge.TF.get(0).type)) {
                    titleStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#" + prdItem.infoBadge.TF.get(0).type)), 0, titleStringBuilder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }catch(Exception e){
                //Ln.e(e);
            }
        }

        if(prdItem.valueInfo != null){
            valueInfo.setText(prdItem.valueInfo);
        }

        for (int i=0; i<badge.length; i++) {
            badge[i].setVisibility(View.GONE);
        }

        if (prdItem.rwImgList != null && !prdItem.rwImgList.isEmpty()){
            for(int i=0; i< prdItem.rwImgList.size() ; i++) {
                //3개를 초과하는 뱃지는 무시
                if (i >= badge.length) {
                    break;
                }

                badge[i].layout(0, 0, 0, 0);
                ImageUtil.loadImage(context, prdItem.rwImgList.get(i),badge[i] , 0);

                badge[i].setVisibility(View.VISIBLE);
            }
            tag_container.setVisibility(View.VISIBLE);
        } else {
            tag_container.setVisibility(View.GONE);
        }

        if (DisplayUtils.isValidString(prdItem.productName)) {
            titleStringBuilder.append(prdItem.productName);
        }

        ImageUtil.loadImageFit(context, prdItem.imageUrl, img_sumbnail, R.drawable.noimg_list);

        txtTitle.append(titleStringBuilder);
        promotionName.setText(prdItem.promotionName);
        // 보험상품인 경우

        // 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
        // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
        int salePrice = DisplayUtils.getNumberFromString(prdItem.salePrice);
        int basePrice = DisplayUtils.getNumberFromString(prdItem.basePrice);
        // 할인률 예외

        if (Integer.parseInt(prdItem.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
            if (DisplayUtils.isValidNumberStringExceptZero(prdItem.basePrice)
                    && (salePrice < basePrice)) {
                txt_base_price.setText(DisplayUtils.getFormattedNumber(prdItem.basePrice));
                txt_base_price_unit.setText(prdItem.exposePriceText);
                txt_base_price.setVisibility(View.VISIBLE);
                txt_base_price_unit.setVisibility(View.VISIBLE);
                txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }else {
            if (prdItem.basePrice != null && !"".equals(prdItem.basePrice)
                    && !"0".equals(prdItem.basePrice)) {
                txt_base_price.setText(DisplayUtils.getFormattedNumber(prdItem.basePrice));
                txt_base_price_unit.setText(prdItem.exposePriceText);
                txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txt_base_price.setVisibility(View.VISIBLE);
                txt_base_price_unit.setVisibility(View.VISIBLE);
            }
            txt_badge_per.setVisibility(View.GONE);
        }

        // price
        if (DisplayUtils.isValidString(prdItem.salePrice)) {
            // 가격
            String salePriceText = DisplayUtils.getFormattedNumber(prdItem.salePrice);
            txt_price_unit.setText(prdItem.exposePriceText);
            priceStringBuilder.append(salePriceText);
            txtPrice.setVisibility(View.VISIBLE);
            txt_price_unit.setVisibility(View.VISIBLE);
        } else {
            txtPrice.setVisibility(View.GONE);
            txt_price_unit.setVisibility(View.GONE);
        }
        txtPrice.append(priceStringBuilder);
        if(prdItem.discountRateText == null || "".equals(prdItem.discountRateText)){
            if(prdItem.valueText != null && !"".equals(prdItem.valueText)){
                final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
                priceStringBuilder.append(prdItem.valueText);
                try {
                    priceStringBuilder.setSpan(valueSizeSpan, 0, prdItem.valueText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    priceStringBuilder.setSpan(discountColorSpan, 0, prdItem.valueText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }catch(Exception e){
                    //Ln.e(e);
                }
                txt_badge_per.append(priceStringBuilder);
                txt_badge_per.setVisibility(View.VISIBLE);
            }else{
                // 할인률
                SpannableStringBuilder ret = ProductInfoUtil.getDiscountTextV2(context, prdItem);
                if (isNotEmpty(ret)) {
                    txt_badge_per.append(ret);
                    txt_badge_per.setVisibility(View.VISIBLE);
                }
            }
        }else{
            // 할인률
            SpannableStringBuilder ret = ProductInfoUtil.getDiscountTextV2(context, prdItem);
            if (isNotEmpty(ret)) {
                txt_badge_per.append(ret);
                txt_badge_per.setVisibility(View.VISIBLE);
            }
        }

        // sold out & 판매수량
        if (DisplayUtils.isTrue(prdItem.isTempOut)) {
            // sold out이면 판매수량 보여주는 layout 숨김
            layoutSoldout.setVisibility(View.VISIBLE);
        } else {
            layoutSoldout.setVisibility(View.GONE);
            txt_sales_quantity.setText(ProductInfoUtil.getSaleQuantityText(prdItem));
        }

        // row 클릭하면 상품으로 이동
        rowGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(context, prdItem.linkUrl);
                //vod stop event
               // EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_STOP, position));

                //내일 TV에만 동작 해야 하는 Amplitude  이벤트 입니다
                try {
                    JSONObject eventProperties = new JSONObject();
                    eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.TOMORROW_TV_LIST_PRD_CLICK);
                    if(content != null) {
                        if(prdItem != null) {
                            eventProperties.put(AMPEnum.AMP_PRD_CODE, prdItem.dealNo);
                            eventProperties.put(AMPEnum.AMP_PRD_NAME, prdItem.productName);
                            eventProperties.put(AMPEnum.AMP_PRD_PRICE, prdItem.salePrice);
                        }
                        eventProperties.put(AMPEnum.AMP_VIDEO_ID, content.videoid);
                        eventProperties.put(AMPEnum.AMP_VIDEO_DUR, content.videoTime);

                        if(content.linkUrl != null)
                            eventProperties.put(AMPEnum.AMP_URL_MSEQ, getMseqFromLinkUrl(content.linkUrl));
                    }
                    AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_TOMORROW_TV, eventProperties);
                } catch (JSONException exception){

                }

            }
        });

        //바로구매 버튼과 장바구니가 findId가 안될경우 대비(그룹핑 UI에서는 두개의 버튼이 없으니까)
        if(txt_buy != null)
        {
            if(prdItem.directOrdInfo != null){

                if(DisplayUtils.isValidString(prdItem.directOrdInfo.linkUrl) && DisplayUtils.isValidString(prdItem.directOrdInfo.text)) {
                    if(BUY_NOW.equals(prdItem.directOrdInfo.text)||GO_CONSULT.equals(prdItem.directOrdInfo.text)){

                        //상담신청시에 "상담 전용 상품입니다." 문구 크기가 작아져서 txt_badge_per에 top padding을 주어 위치변경
                        if(GO_CONSULT.equals(prdItem.directOrdInfo.text)){
                            txt_badge_per.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.badge_consult_text_padding),0,0);
                        }

                        //버튼 보이기
                        txt_buy.setText(prdItem.directOrdInfo.text);
                        txt_buy.setVisibility(View.VISIBLE);
                        txt_buy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WebUtils.goWeb(context, prdItem.directOrdInfo.linkUrl);
                                //vod stop event
                                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_STOP, position));


                                //내일 TV에만 동작 해야 하는 Amplitude  이벤트 입니다
                                try {
                                    JSONObject eventProperties = new JSONObject();
                                    eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.DIRECT_BUY);

                                    if(content != null) {
                                        if(prdItem != null) {
                                            eventProperties.put(AMPEnum.AMP_PRD_CODE, prdItem.dealNo);
                                            eventProperties.put(AMPEnum.AMP_PRD_NAME, prdItem.productName);
                                            eventProperties.put(AMPEnum.AMP_VIDEO_ID, prdItem.videoid);
                                            if(prdItem.linkUrl != null){
                                                eventProperties.put(AMPEnum.AMP_EXP_NAME, Uri.parse(prdItem.linkUrl).getQueryParameter("expId"));
                                            }
                                        }
                                    }
                                    AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_TOMORROW_TV, eventProperties);
                                } catch (JSONException exception){

                                }


                            }
                        });
                    }else{
                        txt_buy.setVisibility(View.GONE);
                    }

                }else{
                    txt_buy.setVisibility(View.GONE);
                }

            }else{
                txt_buy.setVisibility(View.GONE);
            }
        }else{
            txt_buy.setVisibility(View.GONE);
        }
    }





    private String getMseqFromLinkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        Uri uri = Uri.parse(url);
        String mseq = uri.getQueryParameter("mseq");
        return mseq == null ? uri.getLastPathSegment() : mseq;
    }

    public void onViewAttachedToWindow() {
        EventBus.getDefault().register(this);
    }
    public void onViewDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
    }


    /**
     * mm:ss를 milliseconds로 변환한다.
     *
     * @param mmss
     * @return milliseconds
     */
    protected int timeToMills(String mmss) {
        SimpleDateFormat simpleDateFormate = new SimpleDateFormat("mm:ss");
        simpleDateFormate.setTimeZone(TimeZone.getTimeZone("UTC"));
        int totalTime = 0;
        try {
            Date date = simpleDateFormate.parse(mmss);
            totalTime = (int) date.getTime();
        } catch (ParseException e) {
            totalTime = 0;
        }

        return totalTime;
    }

    /**
     * 노출정도를 세팅한다.
     *
     * @param percent 백분률
     */
    public void setVisiblePercent(int percent) {
        visiblePercent = percent;
    }

    /**
     * 노출정도를 반환한다.
     *
     * @return 노출정도
     */
    public int getVisiblePercent() {
        return visiblePercent;
    }
}
