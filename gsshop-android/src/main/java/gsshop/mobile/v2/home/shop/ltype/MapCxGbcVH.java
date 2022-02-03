/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.EmptyUtils;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;

/**
 * GS X Brand 배너
 */
@SuppressLint("NewApi")
public class MapCxGbcVH extends BaseViewHolderV2 {

    private static final int COLUMN_COUNT = 2;

    private final View mBtnViewAll;
//    private final RecyclerView mRecycler;
    private final View mProduct01, mProduct02;


//    /**
//     * 상품 리스트 영역
//     */
//    private final View mLayGoods;
//
//    /**
//     * NO DATA 표시 영역
//     */
//    private final View mLayNoData;

    private final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
    private final AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

    /**
     * @param itemView itemView
     */
    public MapCxGbcVH(View itemView) {
        super(itemView);
        mBtnViewAll = itemView.findViewById(R.id.view_go_brand_shop);
//        mLayGoods = itemView.findViewById(R.id.lay_goods);
//        mLayNoData = itemView.findViewById(R.id.lay_no_data);
        mProduct01 = itemView.findViewById(R.id.product_01);
        mProduct02 = itemView.findViewById(R.id.product_02);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);

        setItems(context, moduleList.get(position));
    }

    private void setItems(final Context context, final ModuleList item) {

        if (item.subProductList == null) {
            return;
        }
        try {
            if (item.subProductList.size() > 0) {
                setProcuct(context, mProduct01, item.subProductList.get(0));
            }

            View viewPrd = (LinearLayout) mProduct02.findViewById(R.id.view_product);
            if (item.subProductList.size() > 1) {
                setProcuct(context, mProduct02, item.subProductList.get(1));
                viewPrd.setVisibility(View.VISIBLE);
            } else {
                viewPrd.setVisibility(View.GONE);
            }
        }
        catch (IndexOutOfBoundsException e) {
            Ln.e(e.getMessage());
        }

        View.OnClickListener goWebListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(context, item.moreBtnUrl);
            }
        };

        if (EmptyUtils.isEmpty(item.moreBtnUrl)) {
            mBtnViewAll.setVisibility(View.GONE);
        }
        else {
            mBtnViewAll.setVisibility(View.VISIBLE);
            mBtnViewAll.setOnClickListener(goWebListener);
        }

    }

    private void setProcuct(Context context, View product, SectionContentList item) {
        View viewPrd = (LinearLayout) product.findViewById(R.id.view_product);
        // 영역 확인
//            view_product.setBackgroundColor(Color.LTGRAY);
        ImageView image_prd = (ImageView) product.findViewById(R.id.image_prd);
        ImageView top_badge = (ImageView) product.findViewById(R.id.top_badge);
        TextView text_name = (TextView) product.findViewById(R.id.text_name);
        TextView text_value = (TextView) product.findViewById(R.id.text_value);
        TextView text_base = (TextView) product.findViewById(R.id.text_base);
        TextView txt_badge_per = (TextView) product.findViewById(R.id.txt_badge_per);
        TextView text_price = (TextView) product.findViewById(R.id.text_price);
        TextView text_notation = (TextView) product.findViewById(R.id.text_notation);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), viewPrd);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), image_prd);

        ImageUtil.loadImageFit(context, item.imageUrl, image_prd, R.drawable.noimg_logo);

        String departName = null;
        String departNameColor = null;
        final String productName = item.productName;
        //방어로직 추가
        if (item.infoBadge != null && item.infoBadge.TF != null && !item.infoBadge.TF.isEmpty()) {
            departName = item.infoBadge.TF.get(0).text;
            departNameColor = item.infoBadge.TF.get(0).type;
        }

        if (viewPrd != null) {
            viewPrd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, item.linkUrl);
                }
            });
        }

        final TextView nameText = text_name;
        if ((departName == null || departName.length() <= 0) && nameText != null) {
//            nameText.setText(productName);
            nameText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int textWidth = nameText.getWidth();
                    int paddingLeft = nameText.getPaddingLeft();
                    int paddingRight = nameText.getPaddingRight();
                    if (textWidth > 0) {
                        // line break;
                        nameText.getViewTreeObserver().removeOnPreDrawListener(this);

                        int availableWidth = textWidth - (paddingLeft + paddingRight);
                        // first line
                        int end = nameText.getPaint().breakText(productName, true, availableWidth, null);
                        Spannable wordtoSpan;
                        if (end < productName.length()) {
                            String firstLine = productName.substring(0, end);
                            String nextLine = productName.substring(end);
                            // second line
                            end = nameText.getPaint().breakText(nextLine, true, availableWidth, null);
                            if (end < nextLine.length()) {
                                nextLine = nextLine.substring(0, end) + '\n' + nextLine.substring(end);
                            }
                            wordtoSpan = new SpannableString(firstLine + '\n' + nextLine.trim());
                        } else {
                            wordtoSpan = new SpannableString(productName);
                        }

                        nameText.setText(wordtoSpan);
                    }
                    return true;
                }
            });
        } else {
            // line break;
            final String fullName = departName + productName;
            final int departLength = departName.length();
            int departColor = 0;
            if(StringUtils.checkHexColor("#" + departNameColor)) {
                departColor = Color.parseColor("#" + departNameColor);
            }

            final int finalDepartColor = departColor;
            nameText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int textWidth = nameText.getWidth();
                    int paddingLeft = nameText.getPaddingLeft();
                    int paddingRight = nameText.getPaddingRight();
                    if (textWidth > 0) {
                        nameText.getViewTreeObserver().removeOnPreDrawListener(this);

                        int availableWidth = textWidth - (paddingLeft + paddingRight);
                        // first line
                        int end = nameText.getPaint().breakText(fullName, true, availableWidth, null);
                        Spannable wordtoSpan;
                        if (end < productName.length()) {
                            String firstLine = fullName.substring(0, end);
                            String nextLine = fullName.substring(end);
                            // second line
                            end = nameText.getPaint().breakText(nextLine, true, availableWidth, null);
                            if (end < nextLine.length()) {
                                nextLine = nextLine.substring(0, end) + '\n' + nextLine.substring(end);
                            }
                            wordtoSpan = new SpannableString(firstLine + '\n' + nextLine.trim());
                        } else {
                            wordtoSpan = new SpannableString(fullName);
                        }

                        wordtoSpan.setSpan(new ForegroundColorSpan(finalDepartColor), 0, departLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        nameText.setText(wordtoSpan);
                    }
                    return true;
                }
            });
        }

// basePrice
        // discountRate
        TextView baseText = text_base;
        if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
            ViewUtils.showViews(baseText);
            baseText.setPaintFlags(baseText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            baseText.setText(item.basePrice + context.getString(R.string.won));
        } else {
            ViewUtils.hideViews(baseText);
        }

        // valueText
        TextView valueText = text_value;
        String value = item.valueText;
        if (value != null && value.length() > 0) {
            ViewUtils.showViews(valueText);
            valueText.setText(value);
        } else {
            ViewUtils.hideViews(valueText);
        }

        // salePrice
        TextView priceText = text_price;
        priceText.setText(item.salePrice);

        // exposePriceText
        TextView notationText = text_notation;
        notationText.setText(item.exposePriceText);

        txt_badge_per.setText("");

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
//				txt_badge_per.setPadding(0, 0, 0, context.getResources().getDimensionPixelSize(R.dimen.badge_text_padding));
            }else{
                // 할인률
                txt_badge_per.append(ProductInfoUtil.getDiscountText(context, item, 13, 13));
            }
        }else{
            // 할인률
            txt_badge_per.append(ProductInfoUtil.getDiscountText(context, item, 13, 13));
        }

        //"GS가"는 표시안함
        String gsprice = Keys.DISCOUNT_RATE_TEXT_GS + "" + Keys.DISCOUNT_RATE_TEXT_PRICE;
        if (gsprice.equalsIgnoreCase(txt_badge_per.getText().toString())) {
            txt_badge_per.setText("");
            ViewUtils.hideViews(txt_badge_per);
        }

        if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()){
            final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
            if(badge != null && badge.imgUrl != null && !"".equals(badge.imgUrl)){
                ImageUtil.loadImageBadge(context, badge.imgUrl, top_badge, R.drawable.transparent, HD);
                top_badge.setVisibility(View.VISIBLE);
            }
        }
    }
}
