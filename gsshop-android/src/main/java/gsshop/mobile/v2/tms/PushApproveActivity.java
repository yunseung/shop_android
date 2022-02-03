/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */

package gsshop.mobile.v2.tms;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieListener;

import java.io.Serializable;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.AbstractTMSService;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.RunningActivityManager;
import gsshop.mobile.v2.push.PushSettings;
import gsshop.mobile.v2.support.promotion.PromotionInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;


/**
 * 푸시유도 팝업
 */
public class PushApproveActivity extends AbstractBaseActivity implements Serializable {

    private static final long serialVersionUID = 1000L;

    private Activity activity;
    private LinearLayout push_layout;
    private RelativeLayout push_popup;
    private Button btnPushOk;
    private Button btnPushClose;
    private ImageView imgPush;
    private PromotionInfo pushInfo;
    private LottieAnimationView imgAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_approve);

        overridePendingTransition(0, 0);  //애니메이션 없이 액티비티가 빵 뜨도록

        activity = this;
        pushInfo = (PromotionInfo)getIntent().getSerializableExtra("promotionInfo");

        push_layout = (LinearLayout) findViewById(R.id.push_layout);
        push_popup = (RelativeLayout)findViewById(R.id.push_popup);

        //첫 진입시 팝업이 밑에서 위로 뜨도록
        push_popup.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                TranslateAnimation animate = new TranslateAnimation(
                        0,                 // fromXDelta
                        0,                 // toXDelta
                        push_popup.getHeight(),                 // fromYDelta
                        0); // toYDelta
                animate.setDuration(400);
                animate.setFillAfter(true);
                animate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //push_popup.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        push_popup.setVisibility(View.VISIBLE);
                        //팝업 아이콘 로띠 설정
                        setLogoAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                push_popup.startAnimation(animate);
            }
        }, 500);// 0.3초 정도 딜레이를 준 후 팝업띄움 ( 300-1000으로 변경 )



        // 확인
        btnPushOk = (Button) findViewById(R.id.push_ok);
        btnPushOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    //팝업 밑으로 사라지기
                    TranslateAnimation animate = new TranslateAnimation(
                            0,                 // fromXDelta
                            0,                 // toXDelta
                            0,                 // fromYDelta
                            push_popup.getHeight()); // toYDelta
                    animate.setDuration(300);
                    animate.setFillAfter(true);
                    animate.setAnimationListener(new Animation.AnimationListener(){
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //딤 제거
                            push_layout.setBackgroundColor(Color.TRANSPARENT);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    push_popup.startAnimation(animate);
                }catch (Exception e){
                    e.printStackTrace();
                }


                PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PUSH_APPROVE, true);
                updatePushApproveSetting(true);
                setWiseLogHttpClient(ServerUrls.WEB.PUSH_MAIN_OK);
                finish_push();
            }
        });

        // 취소
        btnPushClose = (Button) findViewById(R.id.push_close);
        btnPushClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try{
                    //팝업 밑으로 사라지기
                    TranslateAnimation animate = new TranslateAnimation(
                            0,                 // fromXDelta
                            0,                 // toXDelta
                            0,                 // fromYDelta
                            push_popup.getHeight()); // toYDelta
                    animate.setDuration(300);
                    animate.setFillAfter(true);
                    animate.setAnimationListener(new Animation.AnimationListener(){
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //딤 제거
                            push_layout.setBackgroundColor(Color.TRANSPARENT);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    push_popup.startAnimation(animate);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PUSH_APPROVE, true);
                PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.PUSH_APPROVE_DAY, DateUtils.getToday("yyyyMMdd"));
                updatePushApproveSetting(false);
                setWiseLogHttpClient(ServerUrls.WEB.PUSH_MAIN_CANCEL);
                finish_push();
            }
        });

        // 안내 이미지
        imgPush = (ImageView) findViewById(R.id.push_img);
        // 안내 이미지 애니메이션
        imgAnimation = (LottieAnimationView) findViewById(R.id.img_animation);

        setWiseLogHttpClient(ServerUrls.WEB.PUSH_MAIN);

        RunningActivityManager.addActivity(this);
    }

    /**
     * 조르기 팝업을 띄운다.
     */
    private void confirmPushApprove() {
        new CustomTwoButtonDialog(activity)
                .cancelable(false)
                .message(R.string.push_popup_notice)
                .positiveButtonLabel(R.string.common_good)
                .negativeButtonLabel(R.string.common_no)
                .positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PUSH_APPROVE, true);
                        updatePushApproveSetting(true);
                        setWiseLogHttpClient(ServerUrls.WEB.PUSH_POPUP_OK);
                        dialog.dismiss();
                        finish();
                    }
                }).negativeButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PUSH_APPROVE, true);
                        updatePushApproveSetting(false);
                        PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.PUSH_APPROVE_DAY,
                                DateUtils.getToday("yyyyMMdd"));
                        setWiseLogHttpClient(ServerUrls.WEB.PUSH_POPUP_CANCEL);
                        dialog.dismiss();
                        finish();
                    }
        }).show();

        sendPushPopupLog();
    }

    /**
     * updatePushApproveSetting
     *
     * @param approve 수신 여부
     */
    private void updatePushApproveSetting(boolean approve) {
        PushSettings settings = PushSettings.get();
        settings.approve = approve;
        settings.save();

        if(approve){
            Toast.makeText(this, getResources().getString(R.string.msg_push_on)+"GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getResources().getString(R.string.msg_push_off)+"GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")", Toast.LENGTH_SHORT).show();
        }

        AbstractTMSService.pushSettings(this, settings);
    }

    /**
     * 백키기능은 스킵한다.
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * 와이즈로그를 호출한다.(조르기팝업노출)
     * 두개의 호출(유도팝업취소, 조르기팝업노출)이 동시에 호출되면 하나가 누락될 수 있어 딜레이 준다.
     */
    private void sendPushPopupLog() {
        ThreadUtils.INSTANCE.runInUiThread(() -> setWiseLogHttpClient(ServerUrls.WEB.PUSH_POPUP), 1000);
    }

    /**
     * 푸시 아이콘 애니메이션 적용 (애니메이션 완료 후 기존 로고 보이도록)
     */
    private void setLogoAnimation() {
        if (imgPush == null)
            imgPush = findViewById(R.id.push_img);

        if (imgAnimation == null)
            imgAnimation = (LottieAnimationView)findViewById(R.id.img_animation);

        imgAnimation.setRepeatCount(0);
        imgAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                imgPush.setVisibility(View.INVISIBLE);
                imgAnimation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imgPush.setVisibility(View.VISIBLE);
                imgAnimation.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                imgPush.setVisibility(View.VISIBLE);
                imgAnimation.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        imgAnimation.setFailureListener(new LottieListener<Throwable>() {
            @Override
            public void onResult(Throwable result) {
                // fail 나면 이쪽으로 떨어진다.
                imgPush.setVisibility(View.VISIBLE);
                imgAnimation.setVisibility(View.INVISIBLE);
            }
        });

        try {
            imgAnimation.playAnimation();
            imgAnimation.setVisibility(View.VISIBLE);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            imgPush.setVisibility(View.VISIBLE);
            imgAnimation.setVisibility(View.INVISIBLE);
            Ln.e(e.getMessage());
        }
    }
}