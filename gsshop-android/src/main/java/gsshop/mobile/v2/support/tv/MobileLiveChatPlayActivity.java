/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.tv;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;

import com.google.android.exoplayer2.Player;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import java.util.Date;

import javax.inject.Inject;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.mobilelive.MobileLiveResult;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.exoplayer.ExoMobileLiveMediaPlayerControllerFragment;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * 세로 전체 화면의 채팅 기능이 포함된 네이티브 모바일 라이브 플레이어 엑티비티
 */
public class MobileLiveChatPlayActivity extends AbstractBaseActivity implements OnMediaPlayerListener {


    @Inject
    private RestClient restClient;

    @InjectView(R.id.layout_tab_menu)
    private View tabMenuView;

    private OnMediaPlayerController playerController;

    private MediaInfo mediaInfo;

    private OnCspChatController mChatController;

    private MobileLiveResult mobileLiveResult = null;

    /**
     * 생방송인지 확인.
     */
    private boolean onAirOn = false;
    /**
     * 생방송 종료시간.
     */
    private String endDate;

    private Context mContext;

    private Activity mActivity;

    /**
     * API 주소
     */
    private String apiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mobile_live);
        mContext = this;
        mActivity = this;

        tabMenuView.setVisibility(View.GONE);

        // playerController에 null exception이 발생하여 layout xml 파일에 fragment 삽입
        playerController = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.container_media);

        setupMobileLive(getIntent());
        AMPAction.sendAmpEvent(AMPEnum.MOBILELIVE_VIEW_NALBANG);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 현재 영상 컨트롤러가 null 이 아닌상태 일때에 새로 인텐트가 들어오면 (컨트롤러를 재설정하는것이 아니어서) 팝업 띄워준다.
        if (!TextUtils.isEmpty(mobileLiveResult.couponPopupUrl) &&
                playerController != null) {
            try {
                // 더이상 보지 않기한 날짜와 현재 날짜 비교하여 지났으면 팝업 보여줘도 괜찮다.
                String prevDate = PrefRepositoryNamed.getString(mContext, Keys.PREF.PREF_SHOPPY_LIVE_TODAY_PASS);
                if (isEmpty(prevDate) || DateFormat.format("yyyyMMdd", new Date()).toString().compareTo(prevDate) > 0) {
                    WebUtils.goWeb(mContext, mobileLiveResult.couponPopupUrl);
                }
            }
            catch (NullPointerException e){
                Ln.e(e.getMessage());
            }
        }
    }

    private void setupMobileLive(Intent intent) {
        String url = intent.getStringExtra(Keys.INTENT.MOBILELIVE_LINK);
        Ln.i("url : " + url);
        Uri uri = Uri.parse(url);
        String top = uri.getQueryParameter("topapi");
        apiUrl = top;
        new MobileLiveChatPlayActivity.MobileLiveApiontroller(this).execute(top);
    }

    /**
     * API 주소를 반환한다.
     * @return
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * 모바일 라이브  streaming
     */

    private class MobileLiveApiontroller extends BaseAsyncController<MobileLiveResult> {
        private String url;
        private final Context mContext;

        public MobileLiveApiontroller(Context context) {
            super(context);
            mContext = context;
        }
        //onPreExecute doInbackground 작업 하기 전에 전처리
        @Override
        protected void onPrepare(Object... params) throws Exception {
            CookieUtils.syncWebViewCookiesToRestClient(context, restClient);

            //super.onPrepare(params);
            if (this.dialog != null) {
                //this.dialog.dismiss();
                this.dialog.setCancelable(false);
                this.dialog.show();
            }

            url = (String) params[0];
        }
        //doInBackground 실제 쓰레드에서 하는 작업
        @Override
        protected MobileLiveResult process() throws Exception {
            return restClient.getForObject(url, MobileLiveResult.class);
        }
        //postExecute background 작업이 완벽하게 완료되면 호출되는 메소드
        @Override
        protected void onSuccess(final MobileLiveResult result) throws Exception {
            mobileLiveResult = result;

            if (result != null) {

                onAirOn = "Y".equalsIgnoreCase(result.onAirYn);
                endDate = result.endDate;

                playVideo(mContext, result);

                //상품정보 세팅
                ((ExoMobileLiveMediaPlayerControllerFragment)playerController).setProductList(result, result.currentPrdIndex);

                //방송정보 세팅
                ((ExoMobileLiveMediaPlayerControllerFragment)playerController).setBroadCast(result);


            } else {
            }
        }
    }

    private void playVideo(Context context, MobileLiveResult result) {
        // 쇼핑라이브에서 팝업 띄우지 않게 변경됨. (210406)
//        if (!NetworkStatus.isWifiConnected(context) && !MainApplication.isNetworkApproved) {
        if(false) {
            NetworkUtils.confirmNetworkBillingAndShowPopup(this, new NetworkUtils.OnConfirmNetworkListener() {
                @Override
                public void isConfirmed(boolean isConfirmed) {
                    if (isConfirmed) {
                        MainApplication.isNetworkApproved = true;
                        if (result != null && result.liveUrl != null) {
                            playMedia(result, true);
                        } else {
                            alertMessage();
                        }
                    }
                    else {
                        if( mActivity != null)
                            mActivity.finish();
                    }
                }

                @Override
                public void inCanceled() {
                    if( mActivity != null)
                        mActivity.finish();
                }
            });
        } else {
            if (result != null && result.liveUrl != null) {
                playMedia(result, true);
            } else {
                alertMessage();
            }
        }
    }

    /**
     * 영상 정보가 올바르지 않을까??
     */
    private void alertMessage() {
        new CustomOneButtonDialog(MobileLiveChatPlayActivity.this).message(R.string.moblielive_none).buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
                if( mActivity != null)
                    mActivity.finish();
                // TODO: 2019. 2. 21. 진입시 영상 정보가 또는 기타 기준이 되는 값이 올바르지 않을떄 탈출??
            }
        }).show();
    }

    public void playMedia(MobileLiveResult info, boolean playWhenReady) {

        mediaInfo = new MediaInfo();
        mediaInfo.title = info.title;  // 쓰이지 않음
        mediaInfo.playerMode = MediaInfo.MODE_SIMPLE;
        mediaInfo.videoId = null ; //쓰이지 않음
        mediaInfo.contentUri=info.liveUrl;
        mediaInfo.liveNo=info.liveNo;
        mediaInfo.lastPlaybackState = Player.STATE_IDLE;
        mediaInfo.currentPosition = 0;
        mediaInfo.isPlaying = true;
        mediaInfo.startTime = info.strDate;

        mediaInfo.channel = MediaInfo.CHANNEL_NALBANG;

        playerController.showPlayControllerView(false);
        playerController.setMediaInfo(mediaInfo);
        playerController.playPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // forground
        if (playerController != null) {
            if(mediaInfo != null ) {
                mediaInfo.playerMode = MediaInfo.MODE_FULL;
                playerController.setMediaInfo(mediaInfo);
                playerController.playPlayer();
            }
        }
    }

    /**
     * OnMediaPlayerListener Methods
     */
    @Override
    public void onFullScreenClick(MediaInfo media) {
        finish();
    }

    @Override
    public void onPlayed() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onFinished(MediaInfo media) {
        finish();
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTap(boolean show) {

    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    public void backPressed() {
        ThreadUtils.INSTANCE.runInUiThread(() -> finish(), 2000);
    }

    @Override
    public void finish() {
        //앱이 실행되지 않은 상태에서 외부로부터 호출된 액티비티(푸시, 카카오링크 등)는 백버튼 클릭시
        //이동할 곳이 없으므로 메인화면으로 이동하도록 처리함
        if (getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false)) {
            Intent intent = new Intent(Keys.ACTION.APP_HOME);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
            intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true);
            //intent.putExtra(Keys.INTENT.SECTION_CODE, TARGET_SECTION_CODE);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        super.finish();
    }

    /**DirectOrderAfterLoginEvent
     * 로그인 완료 후 수신한 url로 웹뷰를 띄운다.
     *
     * @param event Events.DirectOrderAfterLoginEvent
     */
    public void onEventMainThread(Events.DirectOrderAfterLoginEvent event) {
        WebUtils.goWeb(this, event.url, getIntent(), true, TabMenu.fromTabMenu(getIntent()));
    }
}