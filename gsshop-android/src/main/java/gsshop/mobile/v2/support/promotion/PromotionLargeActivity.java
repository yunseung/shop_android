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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.Serializable;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.menu.RunningActivityManager;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

public class PromotionLargeActivity extends Activity implements Serializable {
    
	private static final long serialVersionUID = 1001L;
	
	private CheckBox chkPromotionEnd; 		// Promotion Popup 오늘은 그만보기
    private ImageButton btnPromotionClose; // Promotion Popup 닫기
    private ImageView imgPromotion;
    private PromotionInfo promotionInfo;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_l);
        
        promotionInfo = (PromotionInfo)getIntent().getSerializableExtra("promotionInfo");

        // 프로모션 팝업 오늘보지않기
        chkPromotionEnd = (CheckBox) findViewById(R.id.promotion_end);

        // 프로모션 팝업 닫기
        btnPromotionClose = (ImageButton) findViewById(R.id.promotion_close);
        btnPromotionClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PROMOTION, true);
            	//오늘은 그만보기 체크한 경우
            	if (chkPromotionEnd.isChecked()) {
            		PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.PROMOTION_DAY,
                            DateUtils.getToday("yyyyMMdd"));
            	}
            	finish();
            }
        });

        // 프로모션 이미지 클릭시
        imgPromotion = (ImageView) findViewById(R.id.promotion_img);
        imgPromotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	if (HomeActivity.getmContext() instanceof HomeActivity) {
            		((HomeActivity)HomeActivity.getmContext()).promotionLinkAction(promotionInfo.linktype);
					//GTM 클릭이벤트 전달
					GTMAction.sendEvent(PromotionLargeActivity.this, GTMEnum.GTM_AREA_CATEGORY, 
							GTMEnum.GTM_PROMOTION_CLICK_ACTION, 
							promotionInfo.bannertype + "_" + promotionInfo.link);
            	}
            	finish();
            }
        });
        
        setPromotionImageLType(imgPromotion, promotionInfo.imgurl);
        
        RunningActivityManager.addActivity(this);
        
		//GTM 노출이벤트 전달
		GTMAction.sendEvent(PromotionLargeActivity.this, GTMEnum.GTM_AREA_CATEGORY, 
				GTMEnum.GTM_PROMOTION_IMPRESSION_ACTION, 
				promotionInfo.bannertype + "_" + promotionInfo.link);
    }

    /**
     * 프로모션 이미지를 세팅한다.
     *
     * @param imgView 이미지를 표시할 뷰
     * @param url 로딩할 이미지 주소
     */
    private void setPromotionImageLType(ImageView imgView, String url) {
        runOnUiThread(() -> ImageUtil.loadImageResize(getApplicationContext(), url, imgView, R.drawable.transparent));
    }

    /**
     * 백키로 프로모션 팝업을 닫을 경우 닫기 버튼 클릭시 동작과 동일하게 처리한다.
     */
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	
    	PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PROMOTION, true);
    	//오늘은 그만보기 체크한 경우
    	if (chkPromotionEnd.isChecked()) {
    		PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.PROMOTION_DAY,
                    DateUtils.getToday("yyyyMMdd"));
    	}
    	finish();
    }
}