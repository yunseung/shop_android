/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 기획전 - 주차별 테마에 따른 기획전.
 */
@SuppressLint("NewApi")
public class BpOVH extends BaseViewHolder {
    private final ImageView bannerImage;
    private final ImageView arrowImage;
    private final View prdView;
    private final ImageView prdImage;
    private final ImageView dealImage;

    private final View percentView;
    private final TextView gsText;

    private final TextView titleText;
    private final TextView percentText;
    private final TextView valueText;
    private final TextView baseText;
    private final TextView priceText;
    private final TextView wonText;


    /**
     * @param itemView itemView
     */
    public BpOVH(View itemView) {
        super(itemView);

        bannerImage = (ImageView) itemView.findViewById(R.id.image_banner);
        arrowImage = (ImageView) itemView.findViewById(R.id.image_arrow);
        prdView = itemView.findViewById(R.id.view_prd);
        percentView = itemView.findViewById(R.id.view_percent);
        gsText = (TextView) itemView.findViewById(R.id.text_gs);
        prdImage = (ImageView) itemView.findViewById(R.id.image_prd);
        dealImage = (ImageView) itemView.findViewById(R.id.image_deal);
        titleText = (TextView) itemView.findViewById(R.id.text_title);
        percentText = (TextView) itemView.findViewById(R.id.text_percent);
        valueText = (TextView) itemView.findViewById(R.id.text_value);
        baseText = (TextView) itemView.findViewById(R.id.text_base);
        priceText = (TextView) itemView.findViewById(R.id.text_price);
        wonText = (TextView) itemView.findViewById(R.id.text_won);


        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), bannerImage);
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), prdImage);
        DisplayUtils.resizeWidthAtViewToScreenSize(MainApplication.getAppContext(), prdImage);
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), dealImage);
        DisplayUtils.resizeWidthAtViewToScreenSize(MainApplication.getAppContext(), dealImage);
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), arrowImage);
        DisplayUtils.resizeWidthAtViewToScreenSize(MainApplication.getAppContext(), arrowImage);

    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {

        final SectionContentList item = info.contents.get(position).sectionContent;

        ImageUtil.loadImageFit(context, item.imageUrl, bannerImage, R.drawable.noimg_logo);
        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GTM AB Test 클릭이벤트 전달
                if (item.linkUrl != null && item.linkUrl.length() > 4) {
                    WebUtils.goWeb(context, item.linkUrl);
                }
            }
        });


        // 기획전내 노출가능한 상품이 0개 인 경우 상품롤링 영역 전체 비노출.
        if (item.subProductList.size() <= 0) {
            ViewUtils.hideViews(prdView, arrowImage);
            return;
        }


        ViewUtils.showViews(prdView, arrowImage);


        //get(0) 에 대한 방어로직 추가
        SectionContentList tempItem = null;
        if(!item.subProductList.isEmpty())
        {
            tempItem = item.subProductList.get(0);
        }

        //subItem null 개연성 있는가? 있다면 어떻게 처리 할까. 10/05
        final SectionContentList subItem = tempItem;

        if(subItem != null) {
            prdView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, subItem.linkUrl);
                }
            });

            // product image
            ImageUtil.loadImageFit(context, subItem.imageUrl, prdImage, R.drawable.noimg_logo);


            // TODO: 2019-05-24 처음부터 gsText를 숨겨도 되지 않을까요? by최한나 
            // discount & base price


            if (!subItem.discountRate.equals("") && Integer.parseInt(subItem.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
                percentText.setText(subItem.discountRate);
                ViewUtils.hideViews(gsText);
                ViewUtils.showViews(percentView);
                // base price
                baseText.setPaintFlags(baseText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                baseText.setText(subItem.basePrice + context.getString(R.string.won));
                ViewUtils.showViews(baseText);
            } else {
                ViewUtils.hideViews(percentView);
                ViewUtils.hideViews(baseText);
                ViewUtils.hideViews(gsText);
                ViewUtils.hideViews(percentView);
            }

            String value = subItem.valueText;
            if (value != null && value.length() > 0) {
                ViewUtils.showViews(valueText);
                valueText.setText(value);
            } else {
                ViewUtils.hideViews(valueText);
            }

            // productName
            final String productName = subItem.productName;
            String departName = null;
            String departNameColor = null;

            //get.(0) 방어로직
            if (subItem.infoBadge != null && subItem.infoBadge.TF != null && !subItem.infoBadge.TF.isEmpty()) {
                departName = subItem.infoBadge.TF.get(0).text;
                departNameColor = subItem.infoBadge.TF.get(0).type;
            }

            if (departName == null || departName.length() <= 0) {
                titleText.setText(productName);
            } else {
                final int departLength = departName.length();
                Spannable wordtoSpan = new SpannableString(departName + ' ' + productName);

                if(StringUtils.checkHexColor("#" + departNameColor)) {
                    final int departColor = Color.parseColor("#" + departNameColor);
                    wordtoSpan.setSpan(new ForegroundColorSpan(departColor), 0, departLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                titleText.setText(wordtoSpan);
            }


            // price
            priceText.setText(subItem.salePrice);
            wonText.setText(subItem.exposePriceText);

            if (subItem.dealProductType.equalsIgnoreCase(SectionContentList.DEAL_TYPE_DEAL)) {
                ViewUtils.showViews(dealImage);
            } else {
                ViewUtils.hideViews(dealImage);
            }
        }
    }


}
