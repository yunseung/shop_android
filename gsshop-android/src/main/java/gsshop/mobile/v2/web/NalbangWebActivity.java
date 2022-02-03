/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.network.rest.RestPost;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.pattern.mvc.Model;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.gsshop.mocha.web.WebViewControl;
import com.gsshop.mocha.web.WebViewProgressBar;

import org.apache.http.NameValuePair;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.DirectOrderView;
import gsshop.mobile.v2.home.shop.nalbang.ShortbangActivity;
import gsshop.mobile.v2.menu.BadgeTextView;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.sns.SnsV2DialogFragment;
import gsshop.mobile.v2.support.ui.ClearButtonTextWatcher;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.util.RestClientUtils;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static gsshop.mobile.v2.support.gtm.GTMEnum.GTM_ACTION_NALBANG_LIVE;
import static gsshop.mobile.v2.support.gtm.GTMEnum.GTM_ACTION_NALBANG_VOD;
import static gsshop.mobile.v2.support.gtm.GTMEnum.GTM_LABEL_NALBANG;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 웹페이지를 보여주는 액티비티.
 * 인텐트에 Keys.INTENT.WEB_URL 키를 사용하여 로딩할 웹페이지 주소를
 * 전달할 수 있다.
 */
@SuppressLint("NewApi")
public class NalbangWebActivity extends BaseWebActivity
        implements OnMediaPlayerListener {

    public static final int MIN_TALK_LENGTH = 1;
    public static final int MAX_TALK_LENGTH = 50;
    public static final long HEADER_HIDE_INTERVAL = 3000;

    @Inject
    private RestClient restClient;

    //해더 영역 (타이틀, 아이콘)
    @InjectView(R.id.view_header)
    private View viewHeader;

    // cart count
    @InjectView(R.id.basketcnt_badge)
    private BadgeTextView badgeTextView;

    @InjectView(R.id.view_media)
    private View mediaView;

    @InjectView(R.id.view_preview)
    private View previewView;


    @InjectView(R.id.image_thumb)
    private ImageView thumbImage;

    @InjectView(R.id.image_replay)
    private ImageView replayImage;

    @InjectView(R.id.text_duration)
    private TextView durationText;

    @InjectView(R.id.text_nb_title)
    private TextView titleText;

    /**
     * 날톡 입력.
     */
    @InjectView(R.id.view_talk)
    private View talkView;

    @InjectView(R.id.text_talk)
    private EditText talkEdit;

    @InjectView(R.id.button_clear_talk)
    private ImageButton clearTalkButton;

    @InjectView(R.id.layout_tab_menu)
    private View tabMenuView;

    private OnMediaPlayerController playerController;

    // 날톡 보낼 때 사용하는 전화번호.
    private String phoneNo = "";
    // 생방송인지 확인.
    private boolean onAirOn = false;
    // 생방송 종료시간.
    private String endDate;
    private String naltalkNo;

    private WebView webView;

    //해더영역을 노출/숨김처리 하기위한 핸들러, 러너블
    private Handler headerHandler;
    private Runnable headerRunnable;

    private NalbangResult nalbangResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_nalbang);

        tabMenuView.setVisibility(View.GONE);

        // playerController에 null exception이 발생하여 layout xml 파일에 fragment 삽입
        playerController = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.container_media);
        DisplayUtils.resizeHeightAtViewToScreenSize(this, mediaView);

        // webView 영역 늘리기
        webView = (WebView) findViewById(R.id.webview);
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) webView.getLayoutParams();
        p.bottomMargin = 0;
        webView.setLayoutParams(p);

        setupWebControl();

        setupNalbang(getIntent());

        // 클릭리스너 등록
        setClickListener();

        //해더영역을 노출/숨김처리 하기위한 핸들러, 러너블 인스턴스 생성
        headerHandler = new Handler();
        headerRunnable = new Runnable() {
            @Override
            public void run() {
                showHeaderView(false);
            }
        };

        //바로구매
        directOrderLayout = (DirectOrderView) findViewById(R.id.direct_order_layout);
        directOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrderDirectWebView();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setupNalbang(intent);
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.image_nb_back).setOnClickListener((View v) -> {
                    backOnClick();
                }
        );
        findViewById(R.id.image_sns).setOnClickListener((View v) -> {
                    snsOnClick();
                }
        );
        findViewById(R.id.image_search).setOnClickListener((View v) -> {
                    searchOnClick();
                }
        );
        findViewById(R.id.gs_cart).setOnClickListener((View v) -> {
                    cartOnClick();
                }
        );
        findViewById(R.id.button_clear_talk).setOnClickListener((View v) -> {
                    clearTalk();
                }
        );
        findViewById(R.id.button_submit).setOnClickListener((View v) -> {
                    clickSubmitButton(findViewById(R.id.button_submit));
                }
        );
    }

    private void setupNalbang(Intent intent) {
        String url = intent.getStringExtra(Keys.INTENT.NALBANG_LINK);
        //Ln.i("url : " + url);
        Uri uri = Uri.parse(url);
        String top = uri.getQueryParameter("topapi");
        new NalbangMediaController(this).execute(top);
        String bottom = uri.getQueryParameter("bottomurl");
        if (!TextUtils.isEmpty(bottom)) {
            bottom = Uri.parse(bottom).buildUpon().appendQueryParameter("noHeaderFlag", "N").build().toString();
            webControl.loadUrl(bottom);
        } else {
            handleWebException();
        }

        //플레이어의 MediaInfo 초기화
        //초기화를 하지 않으면 onResume의 ~.playMedia 수행시 이전 동영상의 MediaInfo가 남아있어 onFinished 콜백이 수행됨
        playerController.setMediaInfo(null);

        ViewUtils.hideViews(clearTalkButton);
        talkEdit.addTextChangedListener(new MyClearButtonTextWatcher(clearTalkButton));

        // focus를 받을 때 로그인 확인.
        talkEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ViewUtils.hideSoftInput(view);
                    // 로그인 확인
                    User user = User.getCachedUser();
                    if (user == null || user.customerNumber.length() < 2) {
                        //로그인 activity 호출.
                        new CustomTwoButtonDialog(NalbangWebActivity.this).message(R.string.nalbang_login).positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                Intent intent = new Intent(Keys.ACTION.LOGIN);
                                intent.putExtra(Keys.INTENT.FOR_RESULT, true);
                                startActivity(intent);
                            }
                        }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();

                        view.clearFocus();
                    }
                }
            }
        });

        onAirOn = false;
        endDate = "";
        naltalkNo = "";

        ViewUtils.hideViews(previewView, talkView);

    }

    @Override
    protected void setupWebControl() {
        webControl = new WebViewControlInherited.Builder(this)
                .target((WebView) findViewById(R.id.webview))
                .with(new MainWebViewClient(this) {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[NalbangWebActivity shouldOverrideUrlLoading] " + url);
                        if (handleUrl(WEB_TYPE_NALBANG, url)) {
                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Ln.i("[NalbangWebActivity onPageStarted]" + url);
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Ln.i("[NalbangWebActivity onPageFinished]" + url);
                        super.onPageFinished(view, url);

                        //날방매장->생방 or 하단상품->바로구매/장바구니->로그인->플레이어 하단영역 흰화면
                        // ->주문서 상태에서 백키 클릭시 나타나는 흰화면 미리 제거
                        removeBlankPage(url, BLANK_REMOVE_DELAY);
                    }
                })
                .with(new MainWebViewChromeClient(this))
                .with(new WebViewProgressBar((ProgressBar) findViewById(R.id.mc_webview_progress_bar))).build();
        super.setupWebControl();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // cart count
        setBasketcnt();

        // forground
        MediaInfo media = playerController.getMediaInfo();
        if (media != null) {
            playerController.setMediaInfo(media);
            playerController.playPlayer();
        }

        // 날톡 입력창 흰트 설정.
        User user = User.getCachedUser();
        if (user == null || user.customerNumber.length() < 2) {
            talkEdit.setHint(R.string.nalbang_talk_no_login_hint);
        } else {
            talkEdit.setHint(R.string.nalbang_talk_hint);
        }


    }

    @Override
    protected void onPause() {
        //웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.
        //웹뷰쿠키 싱크를 수행하면 쿠키 손실을 방지할 수 있다.
        CookieUtils.syncWebViewCookies(context, restClient);
        super.onPause();

        //해더영역 노출 (일정시간 후 자동으로 숨기지 않음)
        showHeaderView(true, false);
    }

    @Override
    public void onBackPressed() {
        if (DirectOrderView.isVisibleDirectOrder) {
            hideOrderDirectWebView();
            return;
        }

        onBackPressedBefore();
        super.onBackPressed();
    }

    /**
     * 백버튼 클릭시 필요한 선행처리를 수행한다.
     */
    @Override
    protected void onBackPressedBefore() {
        //SNS공유, 푸시 등 외부에서 호출된 경우
        if (getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false)) {
            if (MainApplication.isHomeCommandExecuted) {
                //메인화면이 떠있는 상태면 나만 종료
                getIntent().putExtra(Keys.INTENT.BACK_TO_MAIN, false);
            } else {
                //메인화면이 떠있지 않는 상태면 나종료 && 메인 띄움
                backHandler = null;
            }
        } else {
            finish();
        }
    }

    /**
     * OnMediaPlayerListener Methods
     */
    @Override
    public void onFullScreenClick(MediaInfo media) {
        media.playerMode = MediaInfo.MODE_FULL;
        String param = new Gson().toJson(media);

        Intent intent = new Intent(Keys.ACTION.NALBANG_FULL_VIDEO_PLAYER);
        intent.putExtra(Keys.INTENT.VIDEO_URL, param);
        startActivityForResult(intent, Keys.REQCODE.FULL_VIDEO);
    }

    @Override
    public void onFinished(MediaInfo media) {
        //해더 노출
        showHeaderView(true, false);

        showReloadLayout();
    }

    //플레이어 영역 클릭시 발생하는 콜백
    @Override
    public void onTap(boolean show) {
        showHeaderView(show);
    }

    //플레이 버튼 클릭시 발생하는 콜백
    @Override
    public void onPlayed() {
        showHeaderView(false);
    }

    //일시정지 버튼 클릭시 발생하는 콜백
    @Override
    public void onPaused() {
        headerHandler.removeCallbacks(headerRunnable);
    }

    @Override
    public void onPlayButtonClicked() {
        //날방 라이브 플레이버튼 클릭시 와이즈로그 전송
        if (onAirOn) {
            setWiseLogRest(ServerUrls.WEB.NALBANG_LIVE_PLAY);
        } else {
            setWiseLogRest(ServerUrls.WEB.NALBANG_VOD_PLAY);
        }
    }

    @Override
    public void onSetVideoType(boolean isVod) {
        //날방 API 호출결과가 정상이 아니면 콜백을 통한 UI 세팅 스킵
        if (nalbangResult == null) {
            return;
        }

        // 동영상 바로 실행.
        if (!isVod) {
            // hls}
            // 날톡 입력창 보여줌.
            ViewUtils.showViews(talkView);
            ViewUtils.hideViews(durationText);
            //날방 라이브 플레이버튼 클릭시 와이즈로그 전송
            setWiseLogRest(ServerUrls.WEB.NALBANG_LIVE_PLAY);
            //GTM 노출이벤트 전달 (LIVE)
            GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, GTM_ACTION_NALBANG_LIVE, GTM_LABEL_NALBANG);
        } else {
            // vod
            ViewUtils.showViews(durationText);
            //날방 VOD 플레이버튼 클릭시 와이즈로그 전송
            setWiseLogRest(ServerUrls.WEB.NALBANG_VOD_PLAY);
            //GTM 노출이벤트 전달 (VOD)
            GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, GTM_ACTION_NALBANG_VOD, GTM_LABEL_NALBANG);
        }
    }

    @Override
    public void onError(Exception e) {
        showHeaderView(true, false);
    }

    /**
     * 해더영역에 대한 노출/숨김을 제어한다.
     *
     * @param show     true면 노출하고, false면 숨긴다.
     * @param autoHide true면 일정시간 후 자동으로 숨긴다.
     */
    private void showHeaderView(boolean show, boolean autoHide) {
        int height = -(viewHeader.getHeight()); //디폴트값은 숨김으로 세팅
        if (show) {
            height = 0;
            //아직 수행되지 않은 러너블을 취소한다.
            headerHandler.removeCallbacks(headerRunnable);
            //일정시간 후에 자동으로 숨김처리한다.
            if (autoHide) {
                headerHandler.postDelayed(headerRunnable, HEADER_HIDE_INTERVAL);
            }
        }
        //애니메이션 작업을 수행한다.
        viewHeader.animate().translationY(height).setDuration(100).setListener(
                new AnimatorListenerAdapter() {
                });
    }

    /**
     * 해더영역에 대한 노출/숨김을 제어한다.
     *
     * @param show true면 노출하고, false면 숨긴다.
     */
    private void showHeaderView(boolean show) {
        boolean defaultAutoHide = true;
        showHeaderView(show, defaultAutoHide);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Keys.REQCODE.FULL_VIDEO && resultCode == RESULT_OK) {
            String param = data.getStringExtra(Keys.INTENT.VIDEO_URL);
            //Ln.i("requestCode : " + requestCode + ", param : " + param);
            MediaInfo media = new Gson().fromJson(param, MediaInfo.class);
            playerController.setMediaInfo(media);
        }
    }

    /**
     * top gnb event listner
     */
    public void backOnClick() {
        onBackPressedBefore();
        super.onBackPressed();
    }

    public void snsOnClick() {
        //SNS 팝업 뜰때 동영상이 플레이중이면 일시정지
        playerController.stopPlayer();

        //SNS 팝업 띄움
        SnsV2DialogFragment dialog = new SnsV2DialogFragment();
        dialog.show(getSupportFragmentManager(), ShortbangActivity.class.getSimpleName());

        //SNS 버튼 클릭 정보를 웹뷰에 전달한다. (이벤트 트래킹용)
        webView.loadUrl("javascript:trackingSnsClick()");

        //SNS 버튼 클릭시 와이즈로그 전송
        setWiseLogRest(ServerUrls.WEB.NALBANG_SNS);
    }

    public void searchOnClick() {
        Intent intent = new Intent(Keys.ACTION.SEARCH);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
        intent.putExtra(Keys.INTENT.FROM_WEB, true);
        startActivity(intent);

        //GTM Datahub 이벤트 전달
        DatahubAction.sendDataToDatahub(context, DatahubUrls.D_1016, "");
    }

    public void cartOnClick() {
        WebUtils.goWeb(context, ServerUrls.WEB.SMART_CART);
    }

    private void clearTalk() {
        talkEdit.getText().clear();
    }

    /**
     * cart count
     */
    /**
     * 쿠키의 정보를 바탕으로
     * 장바구니 카운트를 표시한다
     */
    public void setBasketcnt() {
        if (badgeTextView != null) {
            badgeTextView.setVisibility(View.GONE);
            NameValuePair pair = CookieUtils.getWebviewCookie(context, "cartcnt");
            if (pair != null) {
                String value = pair.getValue();

                try {
                    int count = Integer.parseInt(value.trim());
                    if (count > 99) {
                        count = 99;
                    }
                    if (count > 0) {
                        badgeTextView.setText(String.valueOf(count));
                        badgeTextView.setVisibility(View.VISIBLE);
                    } else {
                        badgeTextView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e);
                    badgeTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * event listener
     *
     * @param event event
     */
    // 날톡 입력창 보이기/감추기..
    public void onEventMainThread(Events.NalTalkEvent event) {
        if (event.show) {
            if (onAirOn && !talkView.isShown()) {
                ViewUtils.showViews(talkView);
            }
        } else {
            if (onAirOn && talkView.isShown()) {
                ViewUtils.hideSoftInput(talkView);
                ViewUtils.hideViews(talkView);
                webControl.loadUrl("javascript:refreshBasketCnt()");
            }
        }
    }


    /**
     * 날톡 입력창
     */
    public class MyClearButtonTextWatcher extends ClearButtonTextWatcher {

        /**
         * @param clearButtonView (X) 표시의 클리어 버튼뷰
         */
        public MyClearButtonTextWatcher(View clearButtonView) {
            super(clearButtonView);
        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            Ln.i("s : " + s + ", length : " + s.length());
            if (s.length() > MAX_TALK_LENGTH) {
                new CustomOneButtonDialog(NalbangWebActivity.this).message(R.string.nalbang_talk_over).buttonClick(CustomOneButtonDialog.DISMISS).show();
                s.delete(MAX_TALK_LENGTH, s.length());
            }
        }
    }

    /**
     * 날방 동영상 (vod, streaming)
     */

    private class NalbangMediaController extends BaseAsyncController<NalbangResult> {
        private String url;
        private final Context mContext;

        public NalbangMediaController(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            //super.onPrepare(params);
            if (this.dialog != null) {
                //this.dialog.dismiss();
                this.dialog.setCancelable(false);
                this.dialog.show();
            }

            url = (String) params[0];
        }

        @Override
        protected NalbangResult process() throws Exception {
            return restClient.getForObject(url, NalbangResult.class);
        }

        @Override
        protected void onSuccess(final NalbangResult result) throws Exception {
            nalbangResult = result;

            if (result != null) {

                //for test
                //mp4
                /*result.onAirYn = "N";
                result.liveUrl = "http://mobilevod.gsshop.com/livedeal/20180111162257786043.mp4";*/
//                //live
                /*result.onAirYn = "Y";
                result.liveUrl = "http://livem.gsshop.com:80/gsshop/_definst_/gsshop.sdp/playlist.m3u8";*/

                onAirOn = "Y".equalsIgnoreCase(result.onAirYn);
                endDate = result.endDate;
                naltalkNo = result.naltalkNo;
                Glide.with(context).load(trim(result.thumImg)).into(thumbImage);

                titleText.setText("");
                if (!TextUtils.isEmpty(result.mainTitle)) {
                    titleText.setText(result.mainTitle);
                }
                durationText.setText(result.videoTime);

                //프리뷰화면(섬네일+플레이버튼) 노출
                showPreviewLayout();

                // 재생 또는 다시보기.
                replayImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playVideo(mContext, result);
                    }
                });

                playVideo(mContext, result);
            } else {
            }
        }
    }

    private void playVideo(Context context, NalbangResult result) {
        NetworkUtils.confirmNetworkBillingAndShowPopup(NalbangWebActivity.this, new NetworkUtils.OnConfirmNetworkListener() {
            @Override
            public void isConfirmed(boolean isConfirmed) {
                if (isConfirmed) {
                    if (isValidLiveUrl(result.videoid, result.liveUrl)) {
                        showHeaderView(false);
                        //프리뷰화면(섬네일+플레이버튼) 숨김
                        hidePreviewLayout();
                        playMedia(result, true);
                    } else {
                        alertMessage();
                    }
                }
            }

            @Override
            public void inCanceled() {}
        });
    }

    /**
     * 동영상 주소가 유효한지 체크한다.
     *
     * @param videoId 동영상 번호
     * @param liveUrl 동영상 주소
     * @return 유효하면 true 리턴
     */
    private boolean isValidLiveUrl(String videoId, String liveUrl) {
        return !TextUtils.isEmpty( videoId) || (liveUrl != null && liveUrl.length() >= 4);
    }

    public void clickSubmitButton(View view) {
        ViewUtils.hideSoftInput(view);

        // 로그인 확인
        User user = User.getCachedUser();
        if (user == null || user.customerNumber.length() < 2) {
            //로그인 activity 호출.
            new CustomTwoButtonDialog(NalbangWebActivity.this).message(R.string.nalbang_login).positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    dialog.dismiss();
                    Intent intent = new Intent(Keys.ACTION.LOGIN);
                    intent.putExtra(Keys.INTENT.FOR_RESULT, true);
                    startActivity(intent);
                }
            }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();

            view.clearFocus();
            return;
        }

        // 날톡 문자 보내기.
        String msg = talkEdit.getText().toString();
        if (msg.length() < MIN_TALK_LENGTH) {
            // 데이터 크기 확인.
            new CustomOneButtonDialog(NalbangWebActivity.this).message(R.string.nalbang_talk_under).buttonClick(CustomOneButtonDialog.DISMISS).show();
            return;
        }

        new BaseAsyncController<TalkResult>(this) {
            private String msg;

            @Override
            protected void onPrepare(Object... params) throws Exception {
                msg = (String) params[0];
            }

            @Override
            protected TalkResult process() throws Exception {
                TalkParam param = new TalkParam();
                param.blttgCntnt = msg;
                param.phoneNo = phoneNo;
                param.endDate = endDate;

                return RestClientUtils.INSTANCE.post(restClient, param, ServerUrls.REST.NALBANG_TALK_WRITE, TalkResult.class, naltalkNo);
            }

            @Override
            protected void onSuccess(TalkResult talk) throws Exception {
                super.onSuccess(talk);
                if (talk.success) {
                    phoneNo = talk.resultCode;
                    talkEdit.setText("");
                    Toast.makeText(NalbangWebActivity.this, talk.resultMessage, Toast.LENGTH_SHORT).show();
                } else {
                    String errMsg = talk.resultMessage;
                    if (talk.resultCode == null || talk.resultCode.length() < 2) {
                        errMsg = getString(R.string.nalbang_network_error);
                    }
                    new CustomOneButtonDialog(NalbangWebActivity.this).message(errMsg).buttonClick(CustomOneButtonDialog.DISMISS).show();
                }
            }
        }.execute(msg);

        //앰플리 튜드
        AMPAction.sendAmpEvent(AMPEnum.AMP_CLICK_NALBANG_REGBUTTON);

    }

    /**
     * 프리뷰 화면을 노출한다.
     */
    private void showPreviewLayout() {
        //플레이 버튼으로 변경
        replayImage.setImageResource(R.drawable.btn_live_play_720);
        ViewUtils.showViews(previewView);
    }

    /**
     * 프리뷰 화면을 숨긴다.
     */
    private void hidePreviewLayout() {
        //리로드 버튼으로 변경
        replayImage.setImageResource(R.drawable.btn_replay_720);
        ViewUtils.hideViews(previewView);
    }

    /**
     * 리로드 화면을 노출한다.(동영상 재생 완료시 표시)
     */
    private void showReloadLayout() {
        //플레이 버튼으로 변경
        replayImage.setImageResource(R.drawable.btn_replay_720);
        ViewUtils.showViews(previewView);
    }

    /**
     * 동영상 정보가 없는 경우 알림메시지를 띄운다.
     */
    private void alertMessage() {
        new CustomOneButtonDialog(NalbangWebActivity.this).message(R.string.nalbang_none).buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    public void playMedia(NalbangResult info, boolean playWhenReady) {
        MediaInfo media = new MediaInfo();
        media.title = info.title;
        media.playerMode = MediaInfo.MODE_SIMPLE;
        media.videoId = info.videoid;
        media.contentUri = info.liveUrl;
        media.lastPlaybackState = Player.STATE_IDLE;
        media.currentPosition = 0;
        media.isPlaying = playWhenReady;

        media.channel = MediaInfo.CHANNEL_NALBANG;
        playerController.setMediaInfo(media);
        playerController.playPlayer();
    }


    @ContextSingleton
    @Rest(resId = R.string.server_http_root)
    private static class TalkConnector {
        @RestPost(ServerUrls.REST.NALBANG_TALK_WRITE)
        public TalkResult addTalk(TalkParam param, String talkNo) {
            return null;
        }
    }

    /**
     * 날방
     */
    @Model
    private static class NalbangResult {
        public String liveNo;   // "484",
        public String dealNo;   // "17529517",
        public String title;    // "세척이 쉬우면 말 다했지! 윤남텍 가습기",
        public String mainTitle; // 좌상단 백버튼 오른쪽에 표시할 텍스트
        public String thumImg;  // "http://image.gsshop.com/mi09/livedeal/20151127181236100179.jpg",
        public String videoid;  //brightcove 비디오번호
        public String liveUrl;  // "http://mobilevod.gsshop.com/livedeal/20151127181311330643.mp4",
        public String onAirYn;  // "N",
        public String strDate;
        public String endDate;
        public String naltalkNo;    // 날톡 번호.
        public String videoTime;    // 동영상 시간.
    }

    /**
     * 날톡.
     */
    @Model
    private static class TalkParam {
        public String blttgCntnt; // 메세지
        public String phoneNo;  // 폰넘버
        public String endDate;  //  "20151210105010"
    }

    @Model
    private static class TalkResult {
        public String resultMessage;    //:"0글이 없거나 너무 깁니다.",
        public String resultCode;
        public String linkUrl;
        public boolean success;
    }


}
