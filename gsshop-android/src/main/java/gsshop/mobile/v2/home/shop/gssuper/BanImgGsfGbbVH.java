/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.util.StringUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;


/**
 * GS SUPER 타이틀 배너.
 */
public class BanImgGsfGbbVH extends BaseViewHolder {

    /**
     * 로고 이미지
     */
    private ImageView img_logo;

    /**
     * 우상단 탭이미지
     */
    private ImageView img_tab;

    // 2019.08.27 yun. 새벽배송 툴팁
    private static LinearLayout mLlTooltipNightDelivery = null;

    private Context mContext = null;

    public BanImgGsfGbbVH(View itemView) {
        super(itemView);

        img_logo = itemView.findViewById(R.id.img_logo);
        img_tab = itemView.findViewById(R.id.img_tab);
        mLlTooltipNightDelivery = itemView.findViewById(R.id.ll_tooltip_night_delivery);

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {
        mContext = context;
        ShopInfo.ShopItem item = info.contents.get(position);
        SectionContentList content = item.sectionContent;

        if (item.type == ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBC) {
            //새벽배송 + 당일배송
            img_logo.setImageResource(R.drawable.fresh_logo_02_and);
            img_tab.setImageResource(R.drawable.fresh_tab_02_and);
        } else if (item.type == ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBE) {
            //택배 + 새벽배송
            img_logo.setImageResource(R.drawable.fresh_logo_01_2_and);
            img_tab.setImageResource(R.drawable.fresh_tab_01_and);
        } else {
            //default 당일배송 + 새벽배송
            img_logo.setImageResource(R.drawable.fresh_logo_01_and);
            img_tab.setImageResource(R.drawable.fresh_tab_01_and);
        }

        //좌측 로고 접근성 문구 세팅
        img_logo.setContentDescription(item.sectionContent.productName);

        if (isNotEmpty(content.subProductList)) {
            SectionContentList subProduct = content.subProductList.get(0);
            //우상단 로고 접근성 문구 세팅
            img_tab.setContentDescription(subProduct.productName);

            //매장 리로딩 (당일 or 새벽)
            img_tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String linkUrl = subProduct.linkUrl;
                    if (isNotEmpty(linkUrl)) {
                        EventBus.getDefault().post(new Events.FlexibleEvent.GSSuperToogleEvent(StringUtils.addQuestionMark(linkUrl)));
                    }
                    //와이즈로그 호출
                    ((HomeActivity) context).setWiseLog(subProduct.wiseLog);
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();

    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
    }

    /**
     * GSSuperFragment 에서부터 툴팁 띄울지를 보내주는 이벤트
     *
     * @param event
     */
    public void onEvent(Events.NightDeliveryTooltipEvent event) {
        if (event.isShow) {
            MainApplication.isShownNightDeliveryTooltip = true;

            mLlTooltipNightDelivery.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_top);
            animation.reset();
            mLlTooltipNightDelivery.clearAnimation();

            mLlTooltipNightDelivery.setVisibility(View.VISIBLE);
            mLlTooltipNightDelivery.startAnimation(animation);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLlTooltipNightDelivery.setVisibility(View.GONE);
                }
            }, 3300);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mLlTooltipNightDelivery.getVisibility() == View.GONE) {
                        return;
                    }

                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_top);
                    animation.reset();
                    mLlTooltipNightDelivery.clearAnimation();
                    mLlTooltipNightDelivery.startAnimation(animation);
                }
            }, 3000);

        } else {
            mLlTooltipNightDelivery.clearAnimation();
            mLlTooltipNightDelivery.setVisibility(View.GONE);
        }


    }



    /**
     * 이벤트버스 unregister
     *
     * @param event TvLiveUnregisterEvent
     */
    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        EventBus.getDefault().unregister(this);
    }
}
