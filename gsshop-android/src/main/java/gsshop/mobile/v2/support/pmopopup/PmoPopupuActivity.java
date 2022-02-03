/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */

package gsshop.mobile.v2.support.pmopopup;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.menu.RunningActivityManager;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 매장팝업
 */
public class PmoPopupuActivity extends AbstractBaseActivity {

    private static final long serialVersionUID = 1000L;

    private LinearLayout promotionLayout;
    private Button btnPromotionEnd;
    private Button btnPromotionClose;
    private ImageView imgPromotion;
    private PmoPopupInfo pmoPopupInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pmo_popup);

        pmoPopupInfo = (PmoPopupInfo)getIntent().getSerializableExtra("pmoPopupInfo");

        promotionLayout = (LinearLayout) findViewById(R.id.promotion_layout);
        promotionLayout.setVisibility(View.INVISIBLE);

        // 다시보지않기
        btnPromotionEnd = (Button) findViewById(R.id.promotion_end);
        btnPromotionEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PmoNoShowForeverPrefInfo.save(pmoPopupInfo.dsplSeq);
                finish();
            }
        });

        // 닫기(하루동안 표시안함)
        btnPromotionClose = (Button) findViewById(R.id.promotion_close);
        btnPromotionClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PmoNoShowTodayPrefInfo.save(DateUtils.getToday("yyyyMMdd"), pmoPopupInfo.dsplSeq);
                finish();
            }
        });

        // 이미지 클릭시
        imgPromotion = (ImageView) findViewById(R.id.promotion_img);
        imgPromotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (HomeActivity.getmContext() instanceof HomeActivity) {
                    PmoNoShowForeverPrefInfo.save(pmoPopupInfo.dsplSeq);
                    WebUtils.goWeb(PmoPopupuActivity.this, pmoPopupInfo.linkUrl, getIntent());
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

        ImageUtil.loadImagePromotion(this, pmoPopupInfo.imageUrl, imgPromotion, R.drawable.transparent, promotionLayout);

        RunningActivityManager.addActivity(this);
    }

    /**
     * 백키로 프로모션 팝업을 닫을 경우 닫기 버튼 클릭시 동작과 동일하게 처리한다.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PmoNoShowTodayPrefInfo.save(DateUtils.getToday("yyyyMMdd"), pmoPopupInfo.dsplSeq);
        finish();
    }
}