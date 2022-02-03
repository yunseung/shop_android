/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */

package gsshop.mobile.v2.support.promotion;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.Serializable;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.menu.RunningActivityManager;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

public class PromotionMediumActivity extends Activity implements Serializable {

    private static final long serialVersionUID = 1000L;

    private LinearLayout promotionLayout;
    private LinearLayout promotionBottom;
    private Button btnPromotionEnd; 		// Promotion Popup 오늘은 그만보기
    private Button btnPromotionClose; // Promotion Popup 닫기
    private ImageView imgPromotion;
    private PromotionInfo promotionInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_m);

        promotionInfo = (PromotionInfo)getIntent().getSerializableExtra("promotionInfo");

        promotionLayout = (LinearLayout) findViewById(R.id.promotion_layout);
        promotionBottom = (LinearLayout) findViewById(R.id.promotion_bottom);
        promotionLayout.setVisibility(View.INVISIBLE);

        // 프로모션 팝업 오늘보지않기
        btnPromotionEnd = (Button) findViewById(R.id.promotion_end);
        btnPromotionEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PROMOTION, true);
                PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.PROMOTION_DAY, DateUtils.getToday("yyyyMMdd"));
                finish();
            }
        });

        // 프로모션 팝업 닫기
        btnPromotionClose = (Button) findViewById(R.id.promotion_close);
        btnPromotionClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PROMOTION, true);
                finish();
            }
        });

        // 프로모션 이미지 클릭시
        imgPromotion = (ImageView) findViewById(R.id.promotion_img);
        imgPromotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (HomeActivity.getmContext() instanceof HomeActivity) {
                    PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PROMOTION, true);
                    ((HomeActivity)HomeActivity.getmContext()).promotionLinkAction(promotionInfo.linktype);
                    //GTM 클릭이벤트 전달
                    GTMAction.sendEvent(PromotionMediumActivity.this, GTMEnum.GTM_AREA_CATEGORY,
                            GTMEnum.GTM_PROMOTION_CLICK_ACTION,
                            promotionInfo.bannertype + "_" + promotionInfo.link);
                }
                finish();
            }
        });

        //배경 DIM 처리
        WindowManager.LayoutParams param = getWindow().getAttributes();
        param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        param.dimAmount = 0.6f;
        getWindow().setAttributes(param);

        //좌우 마진 설정
        float margin = DeviceUtils.getDeviceWidth(this) * 0.3f;
        int halfMargin = (int)margin / 2;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) promotionLayout.getLayoutParams();
        params.setMargins(halfMargin, 0, halfMargin, 0);
        promotionLayout.setLayoutParams(params);

        ImageUtil.loadImagePromotion(this, promotionInfo.imgurl, imgPromotion, R.drawable.transparent, promotionLayout);

        RunningActivityManager.addActivity(this);

        //GTM 노출이벤트 전달
        GTMAction.sendEvent(PromotionMediumActivity.this, GTMEnum.GTM_AREA_CATEGORY,
                GTMEnum.GTM_PROMOTION_IMPRESSION_ACTION,
                promotionInfo.bannertype + "_" + promotionInfo.link);
    }

    /**
     * 백키로 프로모션 팝업을 닫을 경우 닫기 버튼 클릭시 동작과 동일하게 처리한다.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PROMOTION, true);
        finish();
    }
}