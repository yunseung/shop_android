package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;
import java.util.TimerTask;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * 롤링배너 어댑터 pager adapter
 */
public class RollBannerPagerAdapter extends PagerAdapter {

    private final Context context;
    private final List<SectionContentList> list;
    private final String action;
    private final String label;
    private final boolean isAdult;
    private TimerTask task;

    protected final SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
    private final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
    private final AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

    public RollBannerPagerAdapter(Context context, List<SectionContentList> list, String action, String label, TimerTask task, boolean isAdult) {
        this.context = context;
        this.list = list;
        this.action = action;
        this.label = label;
        this.isAdult = isAdult;
        this.task = task;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.home_row_type_fx_no1_best_deal_item, null);
        titleStringBuilder.clear();
        ImageView mainImage = (ImageView) view.findViewById(R.id.image_main);
        RelativeLayout image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
        TextView nameText = (TextView) view.findViewById(R.id.text_name);
        TextView txtPrice = (TextView) view.findViewById(R.id.txt_price);
        TextView txtPriceWon = (TextView) view.findViewById(R.id.txt_price_won);
        TextView txtSalesQuantity = (TextView) view.findViewById(R.id.txt_sales_quantity);
        LinearLayout layoutSoldout = (LinearLayout) view.findViewById(R.id.sold_out);
        TextView txtSalesQuantityStr = (TextView) view
                .findViewById(R.id.txt_sales_quantity_str);
        TextView txtSalesQuantitySubStr = (TextView) view
                .findViewById(R.id.txt_sales_quantity_sub_str);
        LinearLayout layoutSalesQuantity = (LinearLayout) view
                .findViewById(R.id.layout_sales_quantity);

        View btnPlay = view.findViewById(R.id.btn_play);

        View tag_container;
        ImageView[] tagBadge = new ImageView[3];

        tag_container = view.findViewById(R.id.tag_container);
        tagBadge[0] = (ImageView) view.findViewById(R.id.tag_save);
        tagBadge[1] = (ImageView) view.findViewById(R.id.tag_cash);
        tagBadge[2] = (ImageView) view.findViewById(R.id.tag_gift);


        ImageView[] footer_badge = new ImageView[5];
        //뱃지
        footer_badge[0] = (ImageView) view.findViewById(R.id.footer_badge_1);
        footer_badge[1] = (ImageView) view.findViewById(R.id.footer_badge_2);
        footer_badge[2] = (ImageView) view.findViewById(R.id.footer_badge_3);
        footer_badge[3] = (ImageView) view.findViewById(R.id.footer_badge_4);
        footer_badge[4] = (ImageView) view.findViewById(R.id.footer_badge_5);

        // ad 구좌
        ImageView adImage = (ImageView) view.findViewById(R.id.image_item_ad);


        final ImageView adInfoImage = (ImageView) view.findViewById(R.id.image_item_ad_info);
        ViewUtils.hideViews(adInfoImage);
        /*
        add tooltip 출력 동작 삭제 (20190515)
        adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adInfoImage.getVisibility() != View.VISIBLE) {
                    ViewUtils.showViews(adInfoImage);
                } else {
                    ViewUtils.hideViews(adInfoImage);
                }
            }
        });

        adInfoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideViews(adInfoImage);
            }
        });
        */
        ImageView top_badge = (ImageView) view.findViewById(R.id.top_badge);

        TextView valueInfo = (TextView) view.findViewById(R.id.valueInfo);

        TextView txt_badge_per = (TextView) view.findViewById(R.id.txt_badge_per);
        txt_badge_per.setText("");
        valueInfo.setText("");

        top_badge.setVisibility(View.GONE);
        // main image

        DisplayUtils.resizeHeightAtViewToScreenSize(context, image_layout);
        DisplayUtils.resizeHeightAtViewToScreenSize(context, mainImage);
        final SectionContentList item = list.get(position);

        if(item.infoBadge != null && item.infoBadge.TF != null && !item.infoBadge.TF.isEmpty()){
            titleStringBuilder.append(item.infoBadge.TF.get(0).text);
            titleStringBuilder.append(" ");
            try {
                if(StringUtils.checkHexColor("#" + item.infoBadge.TF.get(0).type)) {
                    titleStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#" + item.infoBadge.TF.get(0).type)), 0, titleStringBuilder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }catch(Exception e){
                Ln.e(e);
            }
        }

        for (int i=0; i<tagBadge.length; i++) {
            tagBadge[i].setVisibility(View.GONE);
        }

        if (item.rwImgList != null && !item.rwImgList.isEmpty()){
            for(int i=0; i< item.rwImgList.size() ; i++) {
                //3개를 초과하는 뱃지는 무시
                if (i >= tagBadge.length) {
                    break;
                }
                //TODO 확인필요(테블릿에서 상하 스크롤시 resource.getIntrinsicWidth()값이 작아져서 이미지도 작아짐)
//				//ImageUtil.loadImageBadge(context, data.product.rwImgList.get(i),badge[i] , 0, QHD);
//				ImageUtil.loadImageFit(context, item.rwImgList.get(i),badge[i] , 0);

                tagBadge[i].layout(0, 0, 0, 0);
                ImageUtil.loadImage(context, item.rwImgList.get(i),tagBadge[i] , 0);

                tagBadge[i].setVisibility(View.VISIBLE);
            }
            tag_container.setVisibility(View.VISIBLE);
        }else{
            tag_container.setVisibility(View.INVISIBLE);
        }

        //오른쪽 하단 뱃지(최대 5개 이민수)
        footer_badge[0].setVisibility(View.GONE);
        footer_badge[1].setVisibility(View.GONE);
        footer_badge[2].setVisibility(View.GONE);
        footer_badge[3].setVisibility(View.GONE);
        footer_badge[4].setVisibility(View.GONE);

        //GS가 표시하지 않았더니 공백 생김
        //txtPrice.setPadding(context.getResources().getDimensionPixelSize(R.dimen.price_text_left_padding),0,0,0);


        boolean isAdultViewVisible;
        if (item.adultCertYn != null && "Y".equalsIgnoreCase(item.adultCertYn) && !isAdult) {
            mainImage.setImageResource(R.drawable.s19_720);
            isAdultViewVisible = true;
        } else {
            mainImage.setEnabled(false);
            isAdultViewVisible = false;
            ImageUtil.loadImageFit(context, item.imageUrl, mainImage, R.drawable.noimg_logo);
        }




        if (item.imgBadgeCorner != null && item.imgBadgeCorner.RB != null && !item.imgBadgeCorner.RB.isEmpty()  && !isAdultViewVisible){

            int index = 0;
            for( ImageBadge badge : item.imgBadgeCorner.RB){
                if("freeDlv".equals(badge.type)){
                    footer_badge[index].setVisibility(View.VISIBLE);
                    footer_badge[index].setBackgroundResource(R.drawable.dc_badge_01);
                } else if ("freeInstall".equals(badge.type)){
                    footer_badge[index].setVisibility(View.VISIBLE);
                    footer_badge[index].setBackgroundResource(R.drawable.dc_badge_05);
                } else if ("todayClose".equals(badge.type)){
                    footer_badge[index].setVisibility(View.VISIBLE);
                    footer_badge[index].setBackgroundResource(R.drawable.dc_badge_02);
                }else if("Reserves".equals(badge.type)){
                    footer_badge[index].setVisibility(View.VISIBLE);
                    footer_badge[index].setBackgroundResource(R.drawable.dc_badge_04);
                }else if("interestFree".equals(badge.type)){
                    footer_badge[index].setVisibility(View.VISIBLE);
                    footer_badge[index].setBackgroundResource(R.drawable.dc_badge_03);
                }
                if(index == 4){
                    break;
                }
                index++;
            }
        }

        // ad 구좌
        if("Y".equals(item.adDealYn)) {
            ViewUtils.showViews(adImage);
            final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
            if(badge != null && !"".equals(badge.imgUrl)){
                ImageUtil.loadImageFit(context, badge.imgUrl, adImage, R.drawable.ic_ad_and);
            }
        } else {
            ViewUtils.hideViews(adImage);
            /**
             * 왼쪽 상단 뱃지(URL만 처리함) 1개의 아이템만 표현
             */
            if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty() && !isAdultViewVisible ){
                final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
                if(badge != null && !"".equals(badge.imgUrl)){
                    ImageUtil.loadImageFit(context, badge.imgUrl, top_badge, R.drawable.transparent);
                    top_badge.setVisibility(View.VISIBLE);
                }
            }
        }

        if(item.discountRateText == null || "".equals(item.discountRateText)){
            if(item.valueText != null && !"".equals(item.valueText)){
                final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
                priceStringBuilder.append(item.valueText);
                priceStringBuilder.setSpan(valueSizeSpan, 0, item.valueText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                priceStringBuilder.setSpan(discountColorSpan, 0, item.valueText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt_badge_per.append(priceStringBuilder);

                txt_badge_per.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.delivery_image_default_padding),0,0);
            }else{
                // 할인률
                txt_badge_per.append(ProductInfoUtil.getDiscountBannerText(context, item));
                if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
//                    txt_badge_per.setPadding(0, 0, 0, context.getResources().getDimensionPixelSize(R.dimen.small_badge_text_discount_single_padding));
//                    txt_badge_per.setPadding(0, 0, 0, 0);
                    txt_badge_per.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.small_badge_text_discount_single_padding),0,0);
                }
                else {
                   txt_badge_per.setText("");
                   txt_badge_per.setVisibility(View.GONE);
                   txt_badge_per.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.delivery_image_default_padding),0,0);
                }
            }
        }else{
            // 할인률
            txt_badge_per.append(ProductInfoUtil.getDiscountBannerText(context, item));
            if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
//                txt_badge_per.setPadding(0, 0, 0, context.getResources().getDimensionPixelSize(R.dimen.small_badge_text_discount_single_padding));
//                txt_badge_per.setPadding(0, 0, 0, 0);
                txt_badge_per.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.small_badge_text_discount_single_padding),0,0);
            }else{
                // TODO: 2019-05-22 스크롤할때 갑자기 없어지지는 않을까?
                txt_badge_per.setText("");
                txt_badge_per.setVisibility(View.GONE);
                txt_badge_per.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.delivery_image_default_padding),0,0);
            }
        }



        if(item.valueInfo != null){
            valueInfo.setText(item.valueInfo);
        }

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WebUtils.goWeb(context, item.linkUrl);
                stopTimer();

                //GTM 클릭이벤트 전달
                String tempLabel = String.format("%s_%s_%s", label, position, item.linkUrl);
                GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
            }
        });


        if (DisplayUtils.isValidString(item.productName)) {
            titleStringBuilder.append(item.productName);
        }

        nameText.append(titleStringBuilder);
        view.setContentDescription(item.productName);
        // vod icon
        if (DisplayUtils.isTrue(item.hasVod)) {
            btnPlay.setVisibility(View.VISIBLE);
        } else {
            btnPlay.setVisibility(View.GONE);
        }

        // 렌탈상품(렌탈이면 가격앞에 월자 붙임)
        if (DisplayUtils.isTrue(item.isRental)) {
            if(item.salePrice == null || "".equals(item.salePrice) || "0".equals(item.salePrice)){
                txtPrice.setText("");
                txtPriceWon.setText("");
            }else {
                txtPrice.setText(context.getString(R.string.month) + DisplayUtils.getFormattedNumber(item.salePrice));
                txtPriceWon.setText(item.exposePriceText);
                txtPrice.setVisibility(View.VISIBLE);
                txtPriceWon.setVisibility(View.VISIBLE);
            }
        } else if (item.productType != null && "C".equals(item.productType)) {
            txt_badge_per.setText("");
            txtPrice.setText(DisplayUtils.getFormattedNumber(item.salePrice));
            txtPriceWon.setText(item.exposePriceText);
            txtPrice.setVisibility(View.VISIBLE);
            txtPriceWon.setVisibility(View.VISIBLE);

            txtPrice.setPadding(0,0,0,0);
        }else {
            // 아니면 가격 보이게
            // price
            if (DisplayUtils.isValidString(item.salePrice)) {
                // 가격
                txtPrice.setText(DisplayUtils.getFormattedNumber(item.salePrice));
                txtPriceWon.setText(item.exposePriceText);
                txtPrice.setVisibility(View.VISIBLE);
                txtPriceWon.setVisibility(View.VISIBLE);
            } else {
                txtPrice.setVisibility(View.GONE);
                txtPriceWon.setVisibility(View.GONE);
            }
        }


        // sold out & 판매수량
        if (DisplayUtils.isTrue(item.isTempOut)) {
            // sold out이면 판매수량 보여주는 layout 숨김
            layoutSoldout.setVisibility(View.VISIBLE);
        } else {
            layoutSoldout.setVisibility(View.GONE);
            // 판매수량
            ProductInfoUtil.setSaleQuantity(item, layoutSalesQuantity, txtSalesQuantity,
                    txtSalesQuantityStr, txtSalesQuantitySubStr);
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        // must be overridden else throws exception as not overridden.
        collection.removeView((View) view);
    }
    /* 자동 롤링 종료. */
    public void stopTimer() {
        // timer
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}