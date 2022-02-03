/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.appwidget;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.inject.Inject;
import com.gsshop.mocha.core.activity.BaseActivity;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.network.rest.RestClient;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeGroupInfo;
import gsshop.mobile.v2.home.HomeGroupInfoAction;
import gsshop.mobile.v2.home.main.TvLiveContent;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * 생방송 바로보기 스트리밍 열기 전 3G 요금부과 안내 팝업을 띄우기 위한 액티비티.
 *
 * NOTE: AppWidget에서는 {@link Dialog}를 직접적으로 띄울 수 없다.
 * - http://stackoverflow.com/questions/10351115/android-widget-popup
 *
 * NOTE : launcheMode="singleInstance" and taskAffinity=""를 사용하여
 * (우리 앱의 다른 액티비티가 아닌) 홈 스크린 위에 팝업을 띄울 수 있다.
 * - http://stackoverflow.com/questions/2147144/android-how-to-display-a-dialog-over-a-native-screen
 *
 * NOTE : android:style/Theme.Translucent.NoTitleBar 테마를 적용할 것.
 *
 *
 *
 * 2018.11.05
 * -singleInstance 사용시 팝업에 터치이벤트가 동작하지 않는 경우가 있어 방식을 변경함
 *  FLAG_ACTIVITY_NEW_TASK + taskAffinity 설정하여 테스크만 분리
 */
@SuppressWarnings("unused")
public class LiveTVPopupActivity extends BaseActivity {

    @Inject
    private RestClient restClient;

    @Inject
    private HomeGroupInfoAction homeGroupInfoAction;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appwidget_dummy);

        /**
         * tensera
         */
//        try{
//            TenseraApi.preloader().onUserEnter("LiveTVPopupActivity");
//        }catch (Exception e)
//        {
//            //어떤 에러가 날지 모르겠다.
//        }

        boolean wifiConnected = NetworkStatus.isWifiOrEthernetConnected(getApplicationContext());

        // wifi 연결상태라면 팝업띄우지 않고 곧바로 동영상 재생함
        if (wifiConnected) {
            showLiveTV();
        } else {
            dialog = new CustomTwoButtonDialog(this)
                    .message(R.string.network_billing_confirm)
                    .positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            showLiveTV();
                            dialog.dismiss();
                        }
                    }).negativeButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    });

            dialog.show();
        }
    }

    /**
     * 동영상 플레이어 실행
     *
     * @param videoId 비디오번호
     * @param videoUrl 비디오주소
     */
    private void launchLivePlayer(String videoId, String videoUrl) {
        Intent intent = new Intent(Keys.ACTION.LIVE_VIDEO_PLAYER);
        intent.putExtra(Keys.INTENT.VIDEO_ID, videoId);
        intent.putExtra(Keys.INTENT.VIDEO_URL, videoUrl);
        intent.putExtra(Keys.INTENT.VIDEO_PRD_URL, ServerUrls.WEB.LIVE_TV);
        intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        startActivity(intent);

        //GTM 클릭이벤트 전달
        String action = String.format("%s_%s", GTMEnum.GTM_ACTION_HEADER,
                GTMEnum.GTM_ACTION_WIDGET_TAIL);
        GTMAction.sendEvent(this, GTMEnum.GTM_AREA_CATEGORY,
                action,
                GTMEnum.GTM_WIDGET_LABEL.LiveTV.getLabel());
    }

    /**
     * API 호출하여 생방송정보 추출 후 동영상 플레이어 실행
     */
    private void showLiveTV() {
        ThreadUtils.INSTANCE.runInBackground(() -> {
            String tvLiveUrl = "";
            HomeGroupInfo homeGroupInfo = null;

            try {
                //navigation api 호출
                homeGroupInfo = homeGroupInfoAction.getHomeGroupInfo(LiveTVPopupActivity.this, true);
            } catch (Exception e) {
                Ln.e(e);
            }

            if (homeGroupInfo != null
                    && homeGroupInfo.appUseUrl != null
                    && !TextUtils.isEmpty(homeGroupInfo.appUseUrl.tvLiveUrl)) {

                //tvLiveUrl api 호출
                TvLiveContent result = restClient.getForObject(homeGroupInfo.appUseUrl.tvLiveUrl, TvLiveContent.class);
                if (result != null && result.appTvLiveBanner!= null && result.appTvLiveBanner.livePlay != null) {
                    if (TextUtils.isEmpty(result.appTvLiveBanner.livePlay.videoid)
                            && TextUtils.isEmpty(result.appTvLiveBanner.livePlay.livePlayUrl)) {
                        showMessage();
                    } else {
                        //비디오번호 또는 비디어주소가 존재하면 플레이어 실행
                        launchLivePlayer(result.appTvLiveBanner.livePlay.videoid, result.appTvLiveBanner.livePlay.livePlayUrl);
                        finish();
                    }
                } else {
                    showMessage();
                }
            } else {
                showMessage();
            }
        });
    }

    /**
     * 동영상 실행불가 메시지 표시 후 종료
     */
    private void showMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.video_play_error,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}