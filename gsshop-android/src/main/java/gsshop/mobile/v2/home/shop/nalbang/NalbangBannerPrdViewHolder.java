/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenfrvr.hashtagview.HashtagView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;


/**
 *
 *
 */
public class NalbangBannerPrdViewHolder extends BaseViewHolder {


    private final HashtagView hashtagView;

    //상품정보
    private final LinearLayout rowGoods;
    private final TextView txtTitle;
    private final TextView txt_base_price;
    private final TextView txt_base_price_unit;
    private final TextView txtPrice;
    private final TextView txt_price_unit;
    private final TextView txt_badge_per;
    private final TextView txt_sales_quantity;
    private final ImageView mainImg;
    private final TextView valueInfo;
    private final TextView txtPlayTime;
    private final ImageView delivery_image;
    private final SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
    private final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
    private final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
    private final AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

    /**
     * @param itemView
     */
    public NalbangBannerPrdViewHolder(View itemView) {
        super(itemView);

        hashtagView = (HashtagView) itemView.findViewById(R.id.hashtags);
        hashtagView.setTypeface(Typeface.SANS_SERIF);

        //상품정보
        rowGoods = (LinearLayout) itemView.findViewById(R.id.row_tv_goods);
        txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
        txt_badge_per = (TextView) itemView.findViewById(R.id.txt_badge_per);
        mainImg = (ImageView) itemView.findViewById(R.id.main_img);
        valueInfo = (TextView) itemView.findViewById(R.id.valueInfo);

        //생방송 영역 우하단 재생시간
        txtPlayTime = (TextView) itemView.findViewById(R.id.txt_play_time);

        txt_base_price = (TextView) itemView.findViewById(R.id.txt_base_price);
        txt_base_price_unit = (TextView) itemView.findViewById(R.id.txt_base_price_unit);

        txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        txt_price_unit = (TextView) itemView.findViewById(R.id.txt_price_unit);
        txt_sales_quantity = (TextView) itemView.findViewById(R.id.txt_sales_quantity);

        delivery_image = (ImageView) itemView.findViewById(R.id.delivery_image);

        if(mainImg != null) {
            DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), mainImg);
        }

    }

    /**
     * @param context
     * @param position
     * @param info
     * @param action
     * @param label
     * @param sectionName
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        final List<String> tags = info.contents.get(position).sectionContent.productHashTags;
        final List<String> wiseLogs = info.contents.get(position).sectionContent.productHashTagsWiseLog;

        hashtagView.removeAllViews();
        if(tags.isEmpty()) {
            return;
        }

        List<String> keywords = new ArrayList<>();
        for(String tag: tags) {
            keywords.add(tag);
        }

        // keyword list
        hashtagView.setData(keywords, new HashtagView.DataTransform<String>() {

            @Override
            public CharSequence prepare(String s, View view) {
                return "#" + s;
            }
        });

        // link click
        hashtagView.removeListeners();
        hashtagView.addOnTagClickListener(new HashtagView.TagsClickListener() {
            @Override
            public void onItemClicked(Object o) {
                if(tags == null || tags.isEmpty()) {
                    return;
                }

                int i = 0;
                for(String tag: tags) {
                    if(tag.equals(o)) {
                        Ln.i("url : " + tag);
                        info.tabIndex = 0;
                        EventBus.getDefault().post(new Events.FlexibleEvent.NalbangHashTagUpdate(tag));

                        //와이즈로그 호출
                        if (wiseLogs != null) {
                            try {
                                ((HomeActivity) context).setWiseLog(wiseLogs.get(i));
                            } catch (IndexOutOfBoundsException e) {
                                //서버에서 해쉬태그/와이즈로그 쌍을 맞추지 않고 내리는 경우
                                // 10/19 품질팀 요청
                                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                                Ln.e(e);

                            }
                        }

                        break;
                    }
                    i++;
                }
            }
        });


        //상품정보
        final SectionContentList item = info.contents.get(position).sectionContent;
        titleStringBuilder.clear();
        priceStringBuilder.clear();
        txtTitle.setText("");
        txtPrice.setText("");
        txt_badge_per.setText("");
        valueInfo.setText("");
        txt_sales_quantity.setText("");
        txt_base_price.setVisibility(View.GONE);
        txt_base_price_unit.setVisibility(View.GONE);

        // row 클릭하면 상품으로 이동
        rowGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(context, item.linkUrl);
                GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
            }
        });

        ImageUtil.loadImage(context, item.imageUrl, mainImg, R.drawable.noimg_tv);

        //상품명 세팅
        if (DisplayUtils.isValidString(item.productName)) {
            titleStringBuilder.append(item.productName);
        }
        txtTitle.append(titleStringBuilder);

        //화면에 표시할 공간이 없어 노출 안하기로 함
        /*if(item.valueInfo != null){
            valueInfo.setText(item.valueInfo);
        }

        //상품 명 옆 이미지(1개만 표현)
        //총알배송 "quickDlv"
        //해외직배송 "worldDlv"
        delivery_image.setVisibility(View.GONE);
        if(item.infoBadge != null && item.infoBadge.VT != null && item.infoBadge.VT.size() > 0){
            if(item.infoBadge.VT.get(0).type.equals("quickDlv")){
                delivery_image.setVisibility(View.VISIBLE);
                delivery_image.setImageResource(R.drawable.q_delivery_720);
            }else if(item.infoBadge.VT.get(0).type.equals("worldDlv")){
                delivery_image.setVisibility(View.VISIBLE);
                delivery_image.setImageResource(R.drawable.f_delivery_720);
            }
        }*/

        // 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
        // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
//        int salePrice = DisplayUtils.getNumberFromString(item.salePrice);
//        int basePrice = DisplayUtils.getNumberFromString(item.basePrice);
        // 할인률
        if (item.discountRate == null) {
            item.discountRate = "0";
        }

        //화면에 표시할 공간이 없어 노출 안하기로 함
        /*if (Integer.parseInt(item.discountRate) > Keys.MIN_DISCOUNT_RATE) {
            if (DisplayUtils.isValidNumberString(item.basePrice)
                    && (salePrice < basePrice)) {
                txt_base_price.setText(DisplayUtils.getFormattedNumber(item.basePrice));
                txt_base_price_unit.setText(item.exposePriceText);
                txt_base_price.setVisibility(View.VISIBLE);
                txt_base_price_unit.setVisibility(View.VISIBLE);
                txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }else {
            if (item.basePrice != null && !item.basePrice.equals("")
                    && !item.basePrice.equals("0")) {
                txt_base_price.setText(DisplayUtils.getFormattedNumber(item.basePrice));
                txt_base_price_unit.setText(item.exposePriceText);
                txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txt_base_price.setVisibility(View.VISIBLE);
                txt_base_price_unit.setVisibility(View.VISIBLE);
            }
        }*/

        // price
        if (DisplayUtils.isValidString(item.salePrice)) {
            // 가격
            String salePriceText = DisplayUtils.getFormattedNumber(item.salePrice);
            txt_price_unit.setText(item.exposePriceText);
            priceStringBuilder.append(salePriceText);
            txtPrice.setVisibility(View.VISIBLE);
            txt_price_unit.setVisibility(View.VISIBLE);
        } else {
            txtPrice.setVisibility(View.GONE);
            txt_price_unit.setVisibility(View.GONE);
        }
        txtPrice.append(priceStringBuilder);
        if(item.discountRateText == null || "".equals(item.discountRateText)){
            if(item.valueText != null && !"".equals(item.valueText)){
                final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
                priceStringBuilder.append(item.valueText);
                try {
                    priceStringBuilder.setSpan(valueSizeSpan, 0, item.valueText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    priceStringBuilder.setSpan(discountColorSpan, 0, item.valueText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }catch(Exception e){
                    Ln.e(e);
                }
                txt_badge_per.append(priceStringBuilder);
                txt_badge_per.setPadding(0, 0, 0, context.getResources().getDimensionPixelSize(R.dimen.badge_text_padding));
            }else{
                // 할인률
                txt_badge_per.append(ProductInfoUtil.getDiscountText(context, item));
            }
        }else{
            // 할인률
            txt_badge_per.append(ProductInfoUtil.getDiscountText(context, item));
        }

        txt_sales_quantity.setText(ProductInfoUtil.getSaleQuantityText(item));

        if (!TextUtils.isEmpty(item.videoTime)) {
            txtPlayTime.setText(item.videoTime);
        }
    }

}
