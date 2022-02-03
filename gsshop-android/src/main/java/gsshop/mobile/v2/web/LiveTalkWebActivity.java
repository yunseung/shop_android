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
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.network.rest.RestPost;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.pattern.mvc.Model;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.gsshop.mocha.web.WebViewProgressBar;

import org.apache.http.NameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.greenrobot.event.EventBus;
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
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.CustomToast;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.RestClientUtils;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 라이브톡 액티비티(상단 동영상 + 중앙 웹뷰 + 하단 톡입력박스)
 */
@SuppressLint("NewApi")
public class LiveTalkWebActivity extends BaseWebActivity
        implements OnMediaPlayerListener {

    public static final int MIN_TALK_LENGTH = 1;
    public static final int MAX_TALK_LENGTH = 50;
    public static final long HEADER_HIDE_INTERVAL = 2400;

    public static final String LIVETALKVER = "2.0"; //웹에서 메시지를 내려줘서 띄우는 "작성하신~ " 팝업의 내용을 LIVETALKVER 2.0 부터는 메세지를 빈값으로 내려준다. 구앱 처리를 위해 추가

    //카메라 이미지 클릭시 호출할 자바스크립트 함수명 정의
    private static final String JAVASCRIPT_FUNCTION_NAME = "javascript:gs.liveTalkWrite.attachImageClick()";

    @Inject
    private RestClient restClient;

    //해더 영역 (타이틀, 아이콘)
    @InjectView(R.id.view_header)
    private View viewHeader;

    @InjectView(R.id.layout_tab_menu)
    private View tabMenuView;

    @InjectView(R.id.text_livetalk_title)
    private TextView titleText;

    // cart count
    @InjectView(R.id.basketcnt_badge)
    private BadgeTextView badgeTextView;

    @InjectView(R.id.view_media)
    private View mediaView;

    @InjectView(R.id.view_preview)
    private View previewView;

    @InjectView(R.id.view_preview_play)
    private View previewPlayView;

    @InjectView(R.id.view_preview_end)
    private View previewEndView;

    @InjectView(R.id.image_thumb)
    private ImageView thumbImage;

    @InjectView(R.id.image_livetalk_play)
    private ImageView playImage;

    /**
     * 날톡 입력.
     */
    @InjectView(R.id.view_talk)
    private View talkView;

    @InjectView(R.id.text_talk)
    private EditText talkEdit;


    @InjectView(R.id.button_submit)
    private Button submitButton;

    private OnMediaPlayerController playerController = null;

    // 날톡 보낼 때 사용하는 전화번호.
    private String phoneNo = "";

    // 생방송 종료시간.
    private String endDate;
    private String liveTalkNo;
    private String strDate;


    private WebView webView;

    //해더영역을 노출/숨김처리 하기위한 핸들러, 러너블
    private Handler headerHandler;
    private Runnable headerRunnable;
    //방송종료 처리를 위한 핸들러
    private Handler liveEndHandler;
    private Runnable liveEndRunnable;
    //방송종료 여부 플래그
    private boolean isLiveEnded = false;
    private MediaInfo media;

    //카메라, 갤러리 버튼
    public ImageButton btn_take_photo;
    public ImageButton btn_gallery;
    public ImageButton btn_plus;

    //라이브톡 토스트에 필요한 변수들
    public CustomToast customToast;
    private int toast_bottom_margin = 0; //입력창과의 기본여백 20px (팝업에 입력창 높이만큼의 여백을 줘서 띄운다.)
    public FrameLayout root_view; //키보드 높이 변화 알기위해
    public boolean isKeyboardShowing = false;
    public int keypadHeight; //토스트를 키보드위에 띄우기위해

    public LinearLayout lay_private_popup; //타인에게 노출될 수 있는 개인정보(주민번호, 카드정보 등) 입력에 주의해 주세요. 팝
    public LinearLayout lay_txtover_popup; //최대 글자 수(50자)를 초과하여 작성할 수 없습니다. 팝업


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_livetalk);

        btn_take_photo = findViewById(R.id.btn_take_photo);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_plus = findViewById(R.id.btn_plus);
        root_view = findViewById(R.id.root_view);

        tabMenuView.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);

        lay_private_popup = findViewById(R.id.lay_private_popup);
        lay_txtover_popup = findViewById(R.id.lay_txtover_popup);

        // playerController에 null exception이 발생하여 layout xml 파일에 fragment 삽입
        playerController = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.container_media);
        playerController.setOnMediaPlayerListener(this);

        DisplayUtils.resizeHeightAtViewToScreenSize(this, mediaView);

        // webView 영역 늘리기
        webView = (WebView) findViewById(R.id.webview);
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) webView.getLayoutParams();
        p.bottomMargin = 0;
        webView.setLayoutParams(p);

        setupWebControl();
        setupLiveTalk(getIntent());
        customToast = new CustomToast(getApplicationContext());

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

        //갤러리 클릭시
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 확인
                User user = User.getCachedUser();
                if (user == null || user.customerNumber.length() < 2) {
                    showLoginConfirmPopup();
                    return;
                }else{
                    if(isLiveEnded){
                        //라이브톡 방송종료후 클릭시 팝업 띄움
                        showCustomToast(getApplicationContext().getResources().getString(R.string.livetalk_talk_end), Toast.LENGTH_SHORT);
                    }else{
                        if (webControl.getWebView() != null) {
                            webControl.getWebView().loadUrl(JAVASCRIPT_FUNCTION_NAME);
                        }
                    }
                }
            }
        });

        //카메라 클릭시 스크립트 함수를 호출하여 "toapp://attach.."가 호출되도록 한다.
        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 확인
                User user = User.getCachedUser();
                if (user == null || user.customerNumber.length() < 2) {
                    showLoginConfirmPopup();
                    return;
                }else{
                    if(isLiveEnded){
                        //라이브톡 방송종료후 클릭시 팝업 띄움
                        showCustomToast(getApplicationContext().getResources().getString(R.string.livetalk_talk_end), Toast.LENGTH_SHORT);
                    }else{
                        if (webControl.getWebView() != null) {
                            webControl.getWebView().loadUrl(JAVASCRIPT_FUNCTION_NAME);
                        }
                    }
                }
            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_plus.setVisibility(View.GONE);
                btn_gallery.setVisibility(View.VISIBLE);
                btn_take_photo.setVisibility(View.VISIBLE);
            }
        });

        lay_private_popup.setVisibility(View.GONE);
        lay_txtover_popup.setVisibility(View.GONE);

        //키보드 상태에 따라 팝업 위치 및 버튼위치 초기화를 위해 추가
        root_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                root_view.getWindowVisibleDisplayFrame(r);
                int screenHeight = root_view.getRootView().getHeight();

                keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // 키보드 올라옴
                    if (!isKeyboardShowing) {
                        isKeyboardShowing = true;
                        //"타인에게~" 팝업 항상 키패드위에 뜨도록
                        lay_private_popup.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    // 키보드 내려감
                    if (isKeyboardShowing) {
                        isKeyboardShowing = false;
                        lay_private_popup.setVisibility(View.GONE);
                        //아무것도 안친상태에서 키보드만 내려도 초기상태로 돌아가도록
                        if(talkEdit.getText().length() == 0){
                            btn_plus.setVisibility(View.GONE);
                            btn_gallery.setVisibility(View.VISIBLE);
                            btn_take_photo.setVisibility(View.VISIBLE);
                            submitButton.setVisibility(View.GONE);
                            talkEdit.clearFocus();
                        }else{

                        }
                    }
                }
            }
        });

        //라이브톡 최초 진입시 1회 띄움
        customToast.showLiveTalkToast(getApplicationContext().getResources().getString(R.string.livetalk_private), Toast.LENGTH_LONG, 176);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setupLiveTalk(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //방송종료 전에 액티비티를 종료할 경우 메시지큐에서 러너블 제거
        if (liveEndHandler != null) {
            liveEndHandler.removeCallbacks(liveEndRunnable);
        }
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.image_livetalk_back).setOnClickListener((View v) -> {
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
        findViewById(R.id.btn_take_photo).setOnClickListener((View v) -> {
                    addPhoto();
                }
        );
        findViewById(R.id.button_submit).setOnClickListener((View v) -> {
                    clickSubmitButton(findViewById(R.id.button_submit));
                }
        );
    }

    private void setupLiveTalk(Intent intent) {
        String url = intent.getStringExtra(Keys.INTENT.LIVETALK_LINK);
        //Ln.i("url : " + url);
        Uri uri = Uri.parse(url);
        String top = uri.getQueryParameter("topapi");
        new LiveTalkMediaController(this).execute(top);

        talkEdit.addTextChangedListener(new MyClearButtonTextWatcher());

        //생방송 종료 후 클릭시 -> talkEdit.setInputType(0) 된다음에 onClickListener가 호출된다
        //"생방송 중에만 입력 가능"팝업을 누를때마다 띄우기 위해 OnClickListener추가함
        talkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLiveEnded){
                    //라이브톡 방송종료후 클릭시 팝업 띄움
                    showCustomToast(getApplicationContext().getResources().getString(R.string.livetalk_talk_end), Toast.LENGTH_SHORT);
                }
            }
        });

        // focus를 받을 때 로그인 확인.
        talkEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    // 로그인 확인
                    User user = User.getCachedUser();
                    if (user == null || user.customerNumber.length() < 2) {
                        showLoginConfirmPopup();
                        view.clearFocus();
                    }else{
                        //생방송이 종료된 상태에서 포커스 될 경우 알림메시지 띄움
                        if (isLiveEnded) {
                            //라이브톡 방송종료후 클릭시 팝업 띄움
                            showCustomToast(getApplicationContext().getResources().getString(R.string.livetalk_talk_end), Toast.LENGTH_SHORT);
                            talkEdit.setInputType(0);
                            return;
                        }else{
                            submitButton.setVisibility(View.VISIBLE);
                            btn_take_photo.setVisibility(View.GONE);
                            btn_gallery.setVisibility(View.GONE);
                            btn_plus.setVisibility(View.VISIBLE);
                        }

                    }
                }else{ //아웃포커스 됐을때
                    if(talkEdit.getText().length() == 0){
                        submitButton.setVisibility(View.GONE);
                        btn_take_photo.setVisibility(View.VISIBLE);
                        btn_gallery.setVisibility(View.VISIBLE);
                        btn_plus.setVisibility(View.GONE);
                        lay_private_popup.setVisibility(View.GONE);
                    }
                }
            }
        });

        endDate = "";
        liveTalkNo = "";

        hideTVLivePreview();
        ViewUtils.hideViews(talkView);
    }

    @Override
    protected void setupWebControl() {
        webControl = new WebViewControlInherited.Builder(this)
                .target((WebView) findViewById(R.id.webview))
                .with(new MainWebViewClient(this) {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[LiveTalkWebActivity shouldOverrideUrlLoading] " + url);
                        if (handleUrl(WEB_TYPE_LIVETALK, url)) {
                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Ln.i("[LiveTalkWebActivity onPageStarted]" + url);
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Ln.i("[LiveTalkWebActivity onPageFinished]" + url);
                        super.onPageFinished(view, url);

                        //라이브톡->생방 or 하단상품->바로구매/장바구니->로그인->플레이어 하단영역 흰화면
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        //라이브톡 화면 노출시 와이즈로그 전송
        EventBus.getDefault().postSticky(new Events.WiseLogEvent(ServerUrls.WEB.LIVETALK_SCREEN_VIEW));
    }

    @Override
    protected void onPause() {
        //웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.
        //웹뷰쿠키 싱크를 수행하면 쿠키 손실을 방지할 수 있다.
        CookieUtils.syncWebViewCookies(context, restClient);
        super.onPause();

        //해더영역 노출 (일정시간 후 자동으로 숨기지 않음)
        showHeaderView(true, false);

        //TabId를 포함한 링크 클릭시 동영상 재생이 바로 중지되지 않아 명시적 중지명령 내림.
        stopTVLive();
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
     * OnMediaPlayerListener Methods
     */
    @Override
    public void onFullScreenClick(MediaInfo media) {
        media.playerMode = MediaInfo.MODE_FULL;
        String param = new Gson().toJson(media);

        Intent intent = new Intent(Keys.ACTION.LIVETALK_FULL_VIDEO_PLAYER);
        intent.putExtra(Keys.INTENT.VIDEO_URL, param);
        startActivityForResult(intent, Keys.REQCODE.FULL_VIDEO);
    }

    @Override
    public void onFinished(MediaInfo media) {
        //해더 노출
        showHeaderView(true, false);
        //방송종료 화면 노출
        showTVLiveStopLayout();
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
        //라이브톡 플레이버튼 클릭시 와이즈로그 전송
        setWiseLogRest(ServerUrls.WEB.LIVETALK_PLAY);
    }

    @Override
    public void onError(Exception e) {
        playImage.setEnabled(false);
        showHeaderView(true, false);
        ViewUtils.showViews(previewView, previewPlayView);
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
            media = new Gson().fromJson(param, MediaInfo.class);
            media.channel = MediaInfo.CHANNEL_LIVE_TALK;
            playerController.setMediaInfo(media);
        }
    }

    //상단 뒤로가기 이미지 이벤트
    public void backOnClick() {
        onBackPressedBefore();
        super.onBackPressed();
    }

    //상단 공유하기 아이콘 이벤트
    public void snsOnClick() {
        //SNS 팝업 뜰때 동영상이 플레이중이면 일시정지
        //stopTVLive();
        pauseTVLive();

        //해더영역 노출 (일정시간 후 자동으로 숨기지 않음)
        showHeaderView(true, false);

        //SNS 팝업 띄움
        SnsV2DialogFragment dialog = new SnsV2DialogFragment();
        dialog.show(getSupportFragmentManager(), ShortbangActivity.class.getSimpleName());

        //SNS 버튼 클릭 정보를 웹뷰에 전달한다. (이벤트 트래킹용)
        webView.loadUrl("javascript:trackingSnsClick()");

        //SNS 버튼 클릭시 와이즈로그 전송
        setWiseLogRest(ServerUrls.WEB.LIVETALK_SNS);
    }

    //상단 검색 아이콘 이벤트
    public void searchOnClick() {
        Intent intent = new Intent(Keys.ACTION.SEARCH);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
        intent.putExtra(Keys.INTENT.FROM_WEB, true);
        startActivity(intent);

        //GTM Datahub 이벤트 전달
        DatahubAction.sendDataToDatahub(context, DatahubUrls.D_1016, "");
    }

    //상단 장바구니 아이콘 이벤트
    public void cartOnClick() {
        WebUtils.goWeb(context, ServerUrls.WEB.SMART_CART);
    }

    //라이브톡 입력시 노출되는 x버튼 이벤트
    private void clearTalk() {
        talkEdit.getText().clear();
    }

    //카메라 버튼 클릭시 스크립트 함수를 호출하여 "toapp://attach.."가 호출되도록 한다.
    private void addPhoto() {
        //생방송이 종료된 상태에서 이미지를 등록할 경우 알림메시지 띄움
        if (isLiveEnded) {
            new CustomOneButtonDialog(LiveTalkWebActivity.this).message(R.string.livetalk_talk_end).buttonClick(CustomOneButtonDialog.DISMISS).show();
            return;
        }

        // 로그인 확인
        User user = User.getCachedUser();
        if (user == null || user.customerNumber.length() < 2) {
            showLoginConfirmPopup();
            return;
        }

        if (webControl.getWebView() != null) {
            webControl.getWebView().loadUrl(JAVASCRIPT_FUNCTION_NAME);
        }
    }

    /**
     * 로그인 확인 팝업창을 띄운다.
     */
    private void showLoginConfirmPopup() {
        //로그인 activity 호출.
        new CustomTwoButtonDialog(LiveTalkWebActivity.this).message(R.string.nalbang_login).positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(Keys.ACTION.LOGIN);
                intent.putExtra(Keys.INTENT.FOR_RESULT, true);
                startActivity(intent);
            }
        }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();
    }

    /**
     * 쿠키의 정보를 바탕으로 장바구니 카운트를 표시한다
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
     * 라이브톡 입력창 보이기/감추기 (브릿지 함수에서 호출)
     *
     * @param event event
     */
    public void onEventMainThread(Events.LiveTalkEvent event) {
        if (event.show) {
            if (!talkView.isShown()) {
                ViewUtils.showViews(talkView);
            }
        } else {
            if (talkView.isShown()) {
                ViewUtils.hideSoftInput(talkView);
                ViewUtils.hideViews(talkView);
                webControl.loadUrl("javascript:refreshBasketCnt()");
            }
        }
    }

    /**
     * 라이브톡 입력창
     */
    public class MyClearButtonTextWatcher extends ClearButtonTextWatcher {
        public MyClearButtonTextWatcher() {
            super();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > MAX_TALK_LENGTH) {
                //50자 초과시 팝업
                lay_txtover_popup.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //50자 초과 팝업은 3초동안만
                        lay_txtover_popup.setVisibility(View.GONE);
                    }
                }, 3000);

                //MAX_TALK_LENGTH를 초과하여 복사->붙여넣기 하면 MAX_TALK_LENGTH 까지만 복사됨
                s.delete(MAX_TALK_LENGTH, s.length());
            }
        }
    }

    /**
     * 라이브톡 API 호출
     */
    private class LiveTalkMediaController extends BaseAsyncController<LiveTalkResult> {
        private String url;
        private final Context mContext;

        public LiveTalkMediaController(Context context) {
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
        protected LiveTalkResult process() throws Exception {
            return restClient.getForObject(url, LiveTalkResult.class);
        }

        @Override
        protected void onSuccess(final LiveTalkResult result) throws Exception {
            if (result != null) {
                // 동영상 정보가 없으면 라이브톡 액티비티 종료
                //for test
                /*if (result.liveUrl == null || result.liveUrl.length() < 4) {
                    result.videoid = "";
                    result.liveUrl = "http://livem.gsshop.com:80/gsshop/_definst_/gsshop.sdp/playlist.m3u8";
                    result.title = "[라니] 썬버너 와이드그릴";
                    result.imgUrl = "http://image.gsshop.com/planprd/banner_MAINCORNER/28095189_01.jpg";
                    result.strDate = "20190118094000";
                    result.endDate = "20190130094000";
                    result.btmUrl = "http://sm21.gsshop.com/section/livetalk/past/201811021040";
                }*/
                if (TextUtils.isEmpty(result.videoid) && TextUtils.isEmpty(result.liveUrl)) {
                    new CustomOneButtonDialog(LiveTalkWebActivity.this).message(R.string.livetalk_none).buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            //메인화면이 떠있지 않는 경우만 메인화면 띄움 (메인화면이 떠있는 경우는 본 액티비티만 종료)
                            if (!MainApplication.isHomeCommandExecuted) {
//                                moveToHome(ServerUrls.ROOT_URL_PATTERN);
                                checkMoveToHome(ServerUrls.ROOT_URL_PATTERN);
                            }
                            finish();
                        }
                    }).show();

                    return;
                }

                //하단 웹뷰 url 로딩
                String bottomUrl = result.btmUrl;
                if (!TextUtils.isEmpty(bottomUrl)) {
                    bottomUrl = Uri.parse(bottomUrl).buildUpon().appendQueryParameter("noHeaderFlag", "N").build().toString();
                    webControl.loadUrl(bottomUrl);
                } else {
                    handleWebException();
                }

                endDate = result.endDate;
                strDate = result.strDate;
                liveTalkNo = result.liveTalkNo;

                //섬네일 이미지 표시
                Glide.with(context).load(trim(result.imgUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).into(thumbImage);

                //방송종료 시간 체크
                checkLiveFinished(endDate);

                //생방송 플레이 전 섬네일 화면 노출
                showTVLivePlayLayout();
                //해더 노출
                showHeaderView(true, false);
                // 날톡 입력창 보여줌.
                ViewUtils.showViews(talkView);

                // 재생하기 버튼 이벤트
                playImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //라이브톡 플레이버튼 클릭시 와이즈로그 전송
                        setWiseLogRest(ServerUrls.WEB.LIVETALK_PLAY);

                        if (!NetworkStatus.isWifiConnected(mContext) && !MainApplication.isNetworkApproved) {
                            NetworkUtils.confirmNetworkBillingAndShowPopup(LiveTalkWebActivity.this, new NetworkUtils.OnConfirmNetworkListener() {
                                @Override
                                public void isConfirmed(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        playMedia(result, true);
                                        showHeaderView(false);
                                        hideTVLivePreview();
                                        MainApplication.isNetworkApproved = true;
                                    }
                                }

                                @Override
                                public void inCanceled() {}
                            });
                        }else{
                            playMedia(result, true);
                            showHeaderView(false);
                            hideTVLivePreview();
                        }
                    }
                });

            }
        }
    }

    public void clickSubmitButton(View view) {
        //2초 이내의 클릭액션 무시
        if (ClickUtils.work(2000)) {
            return;
        }
        submitButton.setEnabled(false);
        ViewUtils.hideSoftInput(view);
        // 로그인 확인
        User user = User.getCachedUser();
        if (user == null || user.customerNumber.length() < 2) {
            showLoginConfirmPopup();
            view.clearFocus();
            submitButton.setEnabled(true);
            return;
        }else{
            //생방송이 종료된 상태에서 라이브톡을 등록할 경우 알림메시지 띄움
            if (isLiveEnded) {
                showCustomToast(getApplicationContext().getResources().getString(R.string.livetalk_talk_end), Toast.LENGTH_SHORT);
                submitButton.setEnabled(true);
                return;
            }else{
                submitButton.setVisibility(View.GONE);
                btn_plus.setVisibility(View.GONE);
                btn_gallery.setVisibility(View.VISIBLE);
                btn_take_photo.setVisibility(View.VISIBLE);
                talkEdit.clearFocus();
                lay_private_popup.setVisibility(View.GONE);
            }
        }

        // 날톡 문자 보내기.
        String msg = talkEdit.getText().toString().trim();
        if (msg.length() < MIN_TALK_LENGTH) {
            // 데이터 크기 확인.
            new CustomOneButtonDialog(LiveTalkWebActivity.this).message(R.string.nalbang_talk_under).buttonClick(CustomOneButtonDialog.DISMISS).show();
            talkEdit.getText().clear();
            submitButton.setEnabled(true);
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
                param.livetalkVer = LIVETALKVER;

                // TM 에서 라이브 톡 주소가 정상적으로 설정되지 않는 문제 수정
                String url = "http://m.gsshop.com" + ServerUrls.REST.LIVETALK_TALK_WRITE;

                switch (PrefRepositoryNamed.getString(LiveTalkWebActivity.this, Keys.PREF.PREF_NAME_TEST_SERVER, Keys.PREF.PREF_TEST_SERVER)) {
                    case "m":
                    case "am":
                    default:
                        url = "http://m.gsshop.com" + ServerUrls.REST.LIVETALK_TALK_WRITE;
                        break;
                    case "asm":
                        url = "http://asm.gsshop.com" + ServerUrls.REST.LIVETALK_TALK_WRITE;
                        break;
                    case "atm":
                        url = "http://atm.gsshop.com" + ServerUrls.REST.LIVETALK_TALK_WRITE;
                        break;
                }

                return RestClientUtils.INSTANCE.post(restClient, param, url, TalkResult.class, liveTalkNo);
            }

            @Override
            protected void onSuccess(TalkResult talk) throws Exception {
                super.onSuccess(talk);

                submitButton.setEnabled(true);

                if (talk.success) {
                    //웹뷰 리프레시를 위한 스크립트 호출
                    webView.loadUrl("javascript:gs.liveTalk.reFresh('" + msg + "')");
                    phoneNo = talk.resultCode;
                    talkEdit.setText("");
                    showCustomToast(talk.resultMessage, Toast.LENGTH_SHORT);
                } else {
                    String errMsg = talk.resultMessage;
                    if (talk.resultCode == null || talk.resultCode.length() < 2) {
                        errMsg = getString(R.string.livetalk_network_error);
                    }
                    new CustomOneButtonDialog(LiveTalkWebActivity.this).message(errMsg).buttonClick(CustomOneButtonDialog.DISMISS).show();
                }
            }

            @Override
            protected void onError(Throwable e) {
                super.onError(e);
                submitButton.setEnabled(true);
            }
        }.execute(msg);

        //앰플리 튜드
        AMPAction.sendAmpEvent(AMPEnum.AMP_CLICK_LIVETALK_REGBUTTON);

    }

    /**
     * 방송졸료까지 남은 시간을 계산하여 방송종료시 재생을 중지하고 방송종료 화면을 노출한다.
     *
     * @param endDate 방송종료시간 ex)"20160126174000"
     */
    private void checkLiveFinished(String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        long endTime;   //방송종료시간
        long remainTime;    //종료까지 남은시간

        try {
            endTime = sdf.parse(endDate).getTime();
        } catch (ParseException e) {
            //endDate 포맷이 맞지 않는 경우 방송종료를 시점을 체크할 수 없다.
            //이 경우 다음 방송이 연속하여 재생될 수 있다.
            return;
        }

        long currentTime = System.currentTimeMillis();
        remainTime = endTime - currentTime;

        //방송종료시 UI처리를 위한 핸들러, 러너블 정의
        liveEndHandler = new Handler();
        liveEndRunnable = new Runnable() {
            @Override
            public void run() {
                //방송종료 여부 플래그 세팅
                isLiveEnded = true;
                //생방송 중지
                stopTVLive();
                //해더영역 노출 (일정시간 후 자동으로 숨기지 않음)
                showHeaderView(true, false);
                //방송종료 화면 노출
                showTVLiveStopLayout();
            }
        };

        //생방송 남은시간 후에 방송종료 UI처리를 위해 메시지큐에 러너블 등록
        liveEndHandler.postDelayed(liveEndRunnable, remainTime);
    }

    //라이브톡 팝업 띄우기
    public void showCustomToast(String message, int duration){

        if(DisplayUtils.isValidString(message)){
            toast_bottom_margin = 20; //입력창과의 기본여백
            //키보드
            if(isKeyboardShowing){
                toast_bottom_margin += keypadHeight;
            }else{
                toast_bottom_margin += talkView.getHeight();
            }
            customToast.showLiveTalkToast(message, duration, toast_bottom_margin);
        }else{
           //팝업 안띄움
        }

    }

    /**
     * 방송종료 안내 화면을 노출한다. (딤처리 + 방송 종료 텍스트 표시)
     */
    private void showTVLiveStopLayout() {
        hideTVLivePreview();
        ViewUtils.showViews(previewView, previewEndView);
    }

    /**
     * 방송재생 준비화면을 노출한다. (섬네일 이미지 + 플레이 버튼)
     */
    private void showTVLivePlayLayout() {
        hideTVLivePreview();
        ViewUtils.showViews(previewView, previewPlayView);
    }

    /**
     * 동영상 플레이어 영역 위의 프리뷰영역을 숨김처리한다.
     */
    private void hideTVLivePreview() {
        ViewUtils.hideViews(previewEndView, previewView, previewPlayView);
    }

    /**
     * 동영상을 재생한다.
     *
     * @param info          LiveTalkResult
     * @param playWhenReady true면 재생, false면 정지
     */
    public void playMedia(LiveTalkResult info, boolean playWhenReady) {
        MediaInfo media = new MediaInfo();
        media.title = info.title;
        media.playerMode = MediaInfo.MODE_SIMPLE;
        media.videoId = info.videoid;
        media.contentUri = info.liveUrl;
        media.lastPlaybackState = Player.STATE_IDLE;
        media.currentPosition = 0;
        media.isPlaying = playWhenReady;

        media.startTime = info.strDate;
        media.endTime = info.endDate;

        media.channel = MediaInfo.CHANNEL_LIVE_TALK;
        playerController.setMediaInfo(media);
        playerController.playPlayer();
    }

    /**
     * 동영상을 정지한다.
     */
    private void stopTVLive() {
        if(playerController != null){
            playerController.stopPlayer();
        }
    }

    /**
     * 동영상을 일시정지한다.
     */
    private void pauseTVLive(){
        if(playerController != null){
            playerController.setVideoPause();
        }
    }

    /**
     * 라이브톡 전송 어뎁터
     */
    @ContextSingleton
    @Rest(resId = R.string.server_http_root)
    private static class TalkConnector {
        @RestPost(ServerUrls.REST.LIVETALK_TALK_WRITE)
        public TalkResult addTalk(TalkParam param, String talkNo) {
            return null;
        }
    }

    /**
     * 라이브톡 전송시 파라미터 모델
     */
    @Model
    private static class TalkParam {
        public String blttgCntnt; // 메세지
        public String phoneNo;  // 폰넘버
        public String endDate;  //  "20151210105010"
        public String livetalkVer; //값이 없으면 1.0 (default), 값이 있으면 2.0(20/7/17)
    }

    /**
     * 라이브톡 전송결과 모델
     */
    @Model
    private static class TalkResult {
        public String resultMessage;    //:"0글이 없거나 너무 깁니다.",
        public String resultCode;
        public String linkUrl;
        public boolean success;
    }
}
