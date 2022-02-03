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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.Serializable;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

public class PromotionSmallActivity extends Activity implements Serializable {

	private static final long serialVersionUID = 1002L;
	
	private Button btnPromotionEnd; 		// Promotion Popup 오늘은 그만보기
    private Button btnPromotionClose; // Promotion Popup 닫기
    private ImageView imgPromotion;
    private PromotionInfo promotionInfo;
    private Handler mHandler;
    private LinearLayout pmLayout;
    
    private Animation animPromotionShow;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_s);
        
        initAnimation();
        
        promotionInfo = (PromotionInfo)getIntent().getSerializableExtra("promotionInfo");
        
        //팝업 외 영역 클릭시 액티비티 종료시킴
        pmLayout = (LinearLayout) findViewById(R.id.promotion_lay);
        pmLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	finish();
            }
        });
        pmLayout.setVisibility(View.GONE);
        
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
					GTMAction.sendEvent(PromotionSmallActivity.this, GTMEnum.GTM_AREA_CATEGORY, 
							GTMEnum.GTM_PROMOTION_CLICK_ACTION, 
							promotionInfo.bannertype + "_" + promotionInfo.link);
            	}
            	finish();
            }
        });
        setPromotionImageSType(imgPromotion, promotionInfo.imgurl);
        
        //이미지 다운로드 이후에 애니메내선 시작
        new Handler().postDelayed(() -> mHandler.sendEmptyMessage(0), 100);
        
        mHandler = new Handler() {
            @Override
			public void handleMessage(Message msg) {
            	pmLayout.setVisibility(View.VISIBLE);
            	pmLayout.startAnimation(animPromotionShow);
            }
        }; 
    	
        //getWindow().getAttributes().windowAnimations = R.style.PromotionAni;
        
        //배경 DIM 처리
        WindowManager.LayoutParams param = getWindow().getAttributes();
        param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        param.dimAmount = 0.6f;
        getWindow().setAttributes(param);
        
		//GTM 노출이벤트 전달
		GTMAction.sendEvent(PromotionSmallActivity.this, GTMEnum.GTM_AREA_CATEGORY, 
				GTMEnum.GTM_PROMOTION_IMPRESSION_ACTION, 
				promotionInfo.bannertype + "_" + promotionInfo.link);
    }

    /**
     * 프로모션 애니메이션을 세팅한다.
     */
    private void initAnimation() {
    	animPromotionShow = AnimationUtils.loadAnimation(this, R.anim.anim_pm_slide_up);
    	animPromotionShow.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
    }
    
    /**
     * 프로모션 이미지를 세팅한다.
     *
     * @param imgView 이미지를 표시할 뷰
     * @param url 로딩할 이미지 주소
     */
    private void setPromotionImageSType(ImageView imgView, String url) {
        runOnUiThread(() -> ImageUtil.loadImageResize(getApplicationContext(), url, imgView, R.drawable.transparent));
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