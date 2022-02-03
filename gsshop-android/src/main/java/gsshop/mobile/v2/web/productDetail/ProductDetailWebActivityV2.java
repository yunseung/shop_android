/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.productDetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.apptimize.Apptimize;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.gsshop.mocha.web.WebViewProgressBar;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.ApptimizeBaseExp;
import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.home.shop.nalbang.CategoryDataHolder;
import gsshop.mobile.v2.intro.ApptimizeCommand;
import gsshop.mobile.v2.menu.BadgeTextView;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.AppFinishUtils;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.IceBergEncoder;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.view.InterceptTouchFrameLayout;
import gsshop.mobile.v2.view.ScrollViewWithStopListener;
import gsshop.mobile.v2.web.MainWebViewChromeClient;
import gsshop.mobile.v2.web.MainWebViewClient;
import gsshop.mobile.v2.web.ProductDetailBaseWebActivity;
import gsshop.mobile.v2.web.WebActivity;
import gsshop.mobile.v2.web.WebUtils;
import gsshop.mobile.v2.web.WebViewControlInherited;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailAddPmoInfoLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailBroadcastLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailCardPmoInfoLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailCouponPmoInfoLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailDeliveryLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailFreeInterLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailPriceLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailPricePersonalizationLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailPromotionInfoLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailTitleLayout;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailViewPager;
import gsshop.mobile.v2.web.productDetail.views.ProductDetailWebView;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.ServerUrls.LAST_PRD_IMAGE;
import static gsshop.mobile.v2.ServerUrls.REST.NATIVE_PRODUCT_API_VER;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_DEAL_NATIVE_HEADER_BASKET;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_DEAL_NATIVE_HEADER_HOME;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_DEAL_NATIVE_HEADER_SEARCHING;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_HEADER_BASKET;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_HEADER_HOME;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_HEADER_SEARCHING;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_LIVE;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_VOD;
import static gsshop.mobile.v2.ServerUrls.WEB.NATIVE_BOTTOM_FILE_NAME;
import static gsshop.mobile.v2.home.HomeActivity.psnlCurationType;
import static gsshop.mobile.v2.home.HomeActivity.showPsnlCuration;
import static gsshop.mobile.v2.intro.VersionCheckCommand.REFERRER_VERSION_CHECK_COMMAND;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_PRD_MINI_FROMFULL;
import static gsshop.mobile.v2.web.WebUtils.PRD_NATIVE_IS_PRELOAD;

/**
 * 단품/딜 화면일 경우 하단탭바를 숨기기 위한 activity.
 */
public class ProductDetailWebActivityV2 extends ProductDetailBaseWebActivity {
    /**
     * 템플릿 타입 정의
     */
    public enum TEMPLATE_TYPE {
        mediaInfo,
        broadInfo,
        prdNmInfo,
        saleInfo,
        promotionInfo,
        cardPmoInfo,
        cardPmoInfo2,
        addPromotionInfo,
        personalCouponPmoInfo,
        noInterestInfo,
        deliveryInfo,
        prsnlPmoInfo;
    }

    /**
     * 템플릿 타입 변수
     */
    private TEMPLATE_TYPE templateType;

    /**
     * API 호출 결과 정상코드
     */
    private static final String RESULT_CODE = "200";

    /**
     * 스크립트 함수에서 변경할 파라미터 키값
     */
    public static final String SCRIPT_PARAM = "<param>";

    /**
     * 웹뷰상단공백 높이 파라미터 키값
     */
    private static final String HEIGHT_KEY = "appH";

    /**
     * linkUrl에서 추출할 딜번호 파라미터 이름
     */
    private static final String DEALNO_KEY = "dealNo";
    public static final String PRDID_KEY = "prdid";
    private static final String AUTOPLAY_KEY = "vodPlay";
    private static final String PGMID_KEY = "pgmID";
    private static final String ISPRE_KEY = "ispre";
    private static final String REF_KEY = "ref";        // 매장 mseq

    /**
     * MSEQ 값
     */
    private static final String MSEQ_LIVE_AUTO = "418650";      // 라이브 자동 재생
    private static final String MSEQ_LIVE_MANUAL = "418649";    // 라이브 수동 재생
    private static final String MSEQ_VOD_AUTO = "418648";        // 브라이트 코브 자동 새쟁
    private static final String MSEQ_VOD_MANUAL = "418869";

    private ProductDetailWebView webView;

    private ProductDetailViewPager mViewPager;                   // 상품 동영상 및 이미지 들어가는 뷰페이저
    private ScrollViewWithStopListener mNativeScroll;                       //네이티브영역을 감싼 스크롤뷰
    private LinearLayout mNativeLayout;                      // 네이티브 영역 (전체 웹뷰 중 상단 투명한 웹 영역 위에 겹쳐서 뿌려짐)


    // 아래 뷰 들이 2개씩 있는 이유는 상단 고정 헤더 애니메이션 (알파값 전환) 을 위해 똑같지만 색이 다른 뷰를 두 개 겹쳐놓도록 구현했기 때문이다.
    private LinearLayout mLayoutTitle1;                     // 네비게이션 타이틀
    private FrameLayout mLayoutTitle2;
    private LinearLayout mLayoutTitleOver;
    private Button mBtnBack1, mBtnBack2;                    // 네비게이션 뒤로가기 버튼
    private Button mBtnSearch1, mBtnSearch2;                // 네비게이션 검색 버튼
    private Button mBtnHome1, mBtnHome2;                    // 네비게이션 홈 버튼
    private FrameLayout mBtnCart1, mBtnCart2;               // 네비게이션 장바구니 버튼

    private PagerAdapterDetailView mAdapter;

    private ShimmerFrameLayout mSkeletonFrame;

    private ProductDetailPricePersonalizationLayout mProductDetailPricePersonalizationLayout; // 최체오 add 된 후 초개인화 api 호출 후 새로고침 되어야하기 때문에 전역변수로 선언

    /**
     * 단품, 딜 페이지 주소
     */
    private String mPageUrl;
    /**
     * 단품, 딜, 후레시 구분값
     */
    private String mPrdTypeCode;
    /**
     * 상품 또는 딜 코드 cd / prdCd 보고 적당할 값을 세팅
     * 상품 또는 딜 네임 dealNo / prdCd 보고 적당할 값을 세팅
     * 상품 또는 딜 소싱 멀보지? 보고 적당할 값을 세팅
     */
    private static boolean ampIsDeal = true;
    private static String ampProductCd = "";
    private static String ampProductNm = "";
    private static String ampProductType = "";


    private String mAutoPlay;                           // 동영상 바로 플레이 여부
    protected boolean mBroadcastFinished = false;       // 방송 종료 여부
    public boolean isMoveSearch = false;                // 검색영역이 나타났는지 여부

    private boolean mIsVideoPlaying = false;            // 미니 플레이어 현재 재생중인지 여부
    private boolean mIsTopVideoPlaying = false;         // 영상 재생중 스크롤시 나타나는 상단 영상이 재생중인지 여부
    private boolean mIsMiniVideoPlaying = false;        // 미니 플레이어 재생 중이였는지 여부
    private boolean mIsMiniVideoVisibled = false;       // 미니 플레이어 보이는 중이였는지 여부
    private boolean mIsLayerOpen = false;               // 레이어 오픈 여부 확인.
    private boolean mIsMiniPlayerInvisibleNow = false;  // 미니 플레이어 스크립트를 통해 disable 되면 그 후 0.5초 이전에 다시 close 들어오면 visible 하지 않게끔 한다.( scroll이 먹는 경우가 있어)
    private boolean mIsDeal = false;                    // 미니 플레이어가 딜에서는 안나와야 한다.

    protected boolean mIsErrorOccurred = false;           // 에러 발생 여부

    private FrameLayout mNavigationArea;                // 최 상단 네비게이션 영역
    private View mTopVideoArea;                         // 상단 영상 재생 전체 영역
    private FrameLayout mFlTopVideo;                    // 상단 영상 영역
    private TextView mTvRemainTime;                     // 상단 영상 영역 중 남은 시간 텍스트
    private Button mBtnPlayPause;                       // 상단 영상 영역 중 재생 일시정지 버튼
    private Button mBtnVideoClose;                      // 상단 영상 영역 중 닫기 버튼
    private TextView mTvPagerCount;

    private ImageView mImagePre;                        // preloading 이미지 표시

    private View mViewDim;

    /**
     * 뷰페이저 영역
     */
    private FrameLayout mFlViewPagerLayout;

    /**
     * 컴포넌트들의 컨테이너 영역
     */
    private LinearLayout mLlComponentsContainer;

    /**
     * GS Fresh 상품 혜택 이미지 딱지 영역
     */
    private LinearLayout mLlGsFreshBenefitsArea;

    /**
     * 로그인 이벤트 받아서 작업 진행중인지 확인 플래그
     */
    private boolean isLoggedInProcessing = false;

    private ImageView mImageGoToPlayer;

    /**
     * 현재 웹 로딩이 완료되어 있는지 여부.
     */
    private boolean isLoadingComplete = false;

    private ArrayList<String> mListLink = new ArrayList<>();

    private InterceptTouchFrameLayout mRootView;
    private ProgressBar mProgressBar;

    private String mShoppyUrl = null;

    /**
     * 링크주소
     */
    private String linkUrl;

    /**
     * 웹뷰 상단 영역을 네이티브 영역의 길이만큼 비우는 스크립트에 필요한 변수들.
     */
    private String mEmptyHeight = "0";
    private Handler mHandlerEvent = new Handler();
    private Handler mNavigationEventHandler = new Handler();
    private Handler mResizeAppNativeDivHandler = new Handler();
    private Runnable mResizeAppNativeDivRunnable = new Runnable() {
        @Override
        public void run() {
            if (webControl.getWebView() == null) {
                return;
            }

            mEmptyHeight = Integer.toString(DisplayUtils.convertPxToDp(context, mNativeLayout.getHeight() - findViewById(R.id.bottom_space).getHeight()));
            webControl.getWebView().loadUrl(getJavaScriptValue("resizeAppNativeDiv('" + mEmptyHeight + "')"));
            mResizeAppNativeDivHandler.removeCallbacks(mResizeAppNativeDivRunnable);
        }
    };

    /**
     * 웹 <-> 앱 간에 터치 전달 동작 (스크롤, 스와이프 등) 을 원활하게 하기 위한 javascript Inject 및 Flag 처리
     */
    private Handler mWebAppTouchInteractionHandler = new Handler();
    private Runnable mWebAppTouchInteractionRunnable = new Runnable() {
        @Override
        public void run() {
            webView.loadUrl(
                    "javascript:(function() { " +
                            "document.querySelector('body').addEventListener('touchstart', function(e){" +
                            "    if (!e) {" +
                            "        var e = window.event;" +
                            "    }" +
                            "    if (e.target) {" +
                            "        targ=e.target;" +
                            "    } else if (e.srcElement) {" +
                            "        targ=e.srcElement;" +
                            "    }" +
                            "    var tname;" +
                            "    tname = targ.tagName;" +
                            "    window.AppInterface.webTouchCoordinatesId(targ.id);" +
                            "    }, false)" +
                            "})()");
            mRootView.onWebPageFinished(true);
            //프로그레스바 비노출 (pageFinished or 2초 중 빠른 타임에 수핻됨)
            webControl.getProgress().hide();
            //터치방지 설정 초기화
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mWebAppTouchInteractionHandler.removeCallbacks(mWebAppTouchInteractionRunnable);
        }
    };

    /**
     * 레이아웃 변경완료 리스너
     */
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (mLlComponentsContainer.getHeight() > 0) {
                mEmptyHeight = Integer.toString(DisplayUtils.convertPxToDp(context, mNativeLayout.getHeight() - findViewById(R.id.bottom_space).getHeight()));
                loadUrl();
                mNativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    private String mStrPrdDeal = "prdAppController.";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_product_detail_web_v2);

        // 장바구니 카운트 셋팅
        setBasketCnt();

        mRootView = findViewById(R.id.root);
        /**
         * 네이티브 영역 (뷰페이저, 네비게이션바, 상단 영상 영역, 어뎁터 셋팅
         */
        mProgressBar = findViewById(R.id.mc_webview_progress_bar);
        mFlViewPagerLayout = findViewById(R.id.fl_view_pager_area);
        mViewPager = findViewById(R.id.viewpager);
        mNativeScroll = findViewById(R.id.native_scroll);
        mNativeLayout = findViewById(R.id.native_layout);
        mLayoutTitle1 = findViewById(R.id.layout_navigation_1);
        mLayoutTitle2 = findViewById(R.id.layout_navigation_2);
        mLayoutTitleOver = findViewById(R.id.layout_navigation_over);
        mBtnBack1 = findViewById(R.id.btn_back_1);
        mBtnBack2 = findViewById(R.id.btn_back_2);
        mBtnSearch1 = findViewById(R.id.btn_search_1);
        mBtnSearch2 = findViewById(R.id.btn_search_2);
        mBtnHome1 = findViewById(R.id.btn_home_1);
        mBtnHome2 = findViewById(R.id.btn_home_2);
        mBtnCart1 = findViewById(R.id.btn_cart_1);
        mBtnCart2 = findViewById(R.id.btn_cart_2);
        mBtnBack1.setOnClickListener(mOnBackClickListener);
        mBtnBack2.setOnClickListener(mOnBackClickListener);
        mBtnSearch1.setOnClickListener(mOnSearchClickListener);
        mBtnSearch2.setOnClickListener(mOnSearchClickListener);
        mBtnHome1.setOnClickListener(mOnHomeClickListener);
        mBtnHome2.setOnClickListener(mOnHomeClickListener);
        mBtnCart1.setOnClickListener(mOnCartClickListener);
        mBtnCart2.setOnClickListener(mOnCartClickListener);
        mNavigationArea = findViewById(R.id.navigation_area);
        // 상단 스크롤에 따라 보이는 영상 영역
        mTopVideoArea = findViewById(R.id.top_video_area);
        mFlTopVideo = findViewById(R.id.fl_top_video);
        mTvRemainTime = findViewById(R.id.tv_remain_time);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        mBtnVideoClose = findViewById(R.id.btn_video_close);
        mImageGoToPlayer = findViewById(R.id.image_move_to_player);
        mLlGsFreshBenefitsArea = findViewById(R.id.ll_gs_fresh_benefits_area);

        mImagePre = findViewById(R.id.img_pre);

        mViewDim = findViewById(R.id.view_dim);

        mBtnPlayPause.setOnClickListener(mClickListener);
        mBtnVideoClose.setOnClickListener(mClickListener);
        mImageGoToPlayer.setOnClickListener(mClickListener);

        mSkeletonFrame = findViewById(R.id.root_skeleton);
        mSkeletonFrame.startShimmer();

        // 상단 비디오영역의 터치가 웹뷰쪽으로 넘어가 겹치는 현상을 해결하기 위해 달아놓은 리스너.
        mTopVideoArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        mTvPagerCount = findViewById(R.id.tv_pager_count);
        mLlComponentsContainer = findViewById(R.id.ll_components_container);

        // 링크주소
        String linkUrl = getIntent().getStringExtra(Keys.INTENT.WEB_URL);

        // 캐시된 이미지 (매장 등 native 화면에서 올 때 캐싱되어 온 이미지)
        String strImgUrl = getIntent().getStringExtra(Keys.INTENT.IMAGE_URL);

        // Url 에서 ispre 추출
        // 프리로드 여부 확인 추가
        String preloadYN = null;
        if (isNotEmpty(linkUrl)) {
            preloadYN = Uri.parse(linkUrl).getQueryParameter(PRD_NATIVE_IS_PRELOAD);
        }

        // 여기서 strImgUrl 이 없다는 건, 매장에서 들어온 경우가 아닌 것으로 간주.
        if (TextUtils.isEmpty(strImgUrl)) {
            // ispre 값이 Y일 경우에만 url 을 통한 이미지 pre loading 을 수행하고,
            // 그 이외의 경우에는 pre loading 하지 않고 no image 노출. (성인상품의 경우)
            if ("Y".equalsIgnoreCase(preloadYN)) {
                strImgUrl = makePreloadImageUrl(linkUrl);
            }
        }

        if (linkUrl != null && isDeal(linkUrl)) {
            strImgUrl = null;
        }

        ImageUtil.loadImageFromCache(this, strImgUrl, mImagePre, R.drawable.noimage_166_166, ImageUtil.ScaleType.SCALE_TYPE_FIT_XY);

//        mAdapter = new PagerAdapterDetailView(ProductDetailWebActivityV2.this, strImgUrl);

        asyncNativeProduct(this);
        /**
         * 네이티브 영역 끝
         */

        // webView 영역 늘리기
        webView = findViewById(R.id.webview);
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) webView.getLayoutParams();
        p.bottomMargin = 0;
        webView.setLayoutParams(p);

        webView.setBackgroundColor(0);

        webView.setVerticalScrollBarEnabled(true);

        /**
         * 웹뷰 스크롤 리스너
         * 웹뷰가 스크롤 될 때 마다 네이티브 영역을 웹뷰가 움직인 만큼 같이 움직여준다.
         * 스크롤 될 때 영상이 재생중인지 여부를 판단해서 영상 영역을 상단에 붙여주는 작업도 한다.
         */
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // 스크롤중인지 알리기 위함.
                if (Math.abs(scrollY - oldScrollY) < 10 || scrollY < 10) {
                    mRootView.setIsScrolling(false);
                } else {
                    mRootView.setIsScrolling(true);
                }

                //웹뷰기준으로 네이티브 스크롤을 동기화
                mNativeScroll.scrollTo(0, scrollY);

                // 상단 네비게이션 투명 그라데이션을 위한 코드.
                mLayoutTitle2.setAlpha((float) scrollY * 0.001f);

                // 네이티브 영역이 보이지 않을때.
                try {
                    if (!DisplayUtils.isVisible(mFlViewPagerLayout)) {
                        // 상단 미니 비디오 영역을 보여준다.
                        if (!DisplayUtils.isVisible(mTopVideoArea)) {
                            mIsVideoPlaying = mAdapter.isPlayingVideo();
                            showMiniVideoView();
                        }
                        // 상단 미니 비디오 영역이 보인다면.
                    } else {
                        // 상단 미니 비디오 영역을 사라지게 한다.
                        if (DisplayUtils.isVisible(mTopVideoArea)) {
                            mIsVideoPlaying = mAdapter.isPlayingVideo();
                            hideMiniVideoView();
                        }
                        // 돌아올 때에 영상 종료 상태이면 이미지 일부 가림 현상 있어 adapter에 돌아올 때 알려준다.
                        mAdapter.showPlayerArea();
                    }
                } catch (NullPointerException e) {
                    Ln.e(e.getMessage());
                }
            }
        });

        setupWebControl();

        mNativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        //터치방지 설정 (진입하자마자 스크롤시(down->up) 네이티브영역 아래 흰화면 표시되는 현상 개선용
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * 단품네이티브 API 호출
     *
     * @param context Context
     */
    private void asyncNativeProduct(Context context) {
        mProgressBar.setVisibility(View.VISIBLE);
        //API 주소
        String apiUrl;

        //링크주소
        linkUrl = getIntent().getStringExtra(Keys.INTENT.WEB_URL);

        String pgmId;

        if (TextUtils.isEmpty(StringUtils.getUriParameterWithKey(linkUrl, PGMID_KEY))) {
            pgmId = "";
        } else {
            pgmId = StringUtils.getUriParameterWithKey(linkUrl, PGMID_KEY);
        }

        if (isEmpty(linkUrl)) {
            return;
        }

        if (isDeal(linkUrl)) {
            mIsDeal = true;
            mStrPrdDeal = "dealAppController.";
            //딜
            mPageUrl = ServerUrls.WEB.NATIVE_DEAL_BOTTOM;
            apiUrl = ServerUrls.getHttpRoot() + ServerUrls.REST.NATIVE_DEAL + StringUtils.getUriParameter(linkUrl, DEALNO_KEY) + "?" +
                    NATIVE_PRODUCT_API_VER + "&" + pgmId;
        } else if (isProduct(linkUrl)) {
            mIsDeal = false;
            mStrPrdDeal = "prdAppController.";
            //단품
            mPageUrl = ServerUrls.WEB.NATIVE_PRODUCT_BOTTOM;

            ApptimizeBaseExp apptimizeBaseExp = ApptimizeExpManager.findExpInstance(ApptimizeExpManager.CONTENTS);
            if(apptimizeBaseExp != null ){
                apiUrl = ServerUrls.getHttpRoot() + ServerUrls.REST.NATIVE_PRODUCT + StringUtils.getUriParameter(linkUrl, PRDID_KEY) + "?" +
                            NATIVE_PRODUCT_API_VER + "&" + "abinfo=" + apptimizeBaseExp.getAll() + "&" + pgmId;
            }else{
                apiUrl = ServerUrls.getHttpRoot() + ServerUrls.REST.NATIVE_PRODUCT + StringUtils.getUriParameter(linkUrl, PRDID_KEY) + "?" +
                        NATIVE_PRODUCT_API_VER + "&" + pgmId;
            }
        } else {
            return;
        }

        mAutoPlay = StringUtils.getUriParameter(linkUrl, AUTOPLAY_KEY);

        //linkUrl에 adid추가
        Uri uri = Uri.parse(linkUrl);
        if (uri != null && uri.getQueryParameterNames().contains("adid")) {
            //이미 adid 있으면 replace만
            linkUrl = StringUtils.replaceUriParameter(uri, "adid", DeviceUtils.getAdvertisingId()).toString();
        } else {
            if (linkUrl.indexOf('?') == -1) {
                //linkUrl에 ? 없을경우
                linkUrl = linkUrl + "?" + "adid=" + DeviceUtils.getAdvertisingId();
            } else {
                linkUrl = StringUtils.replaceUriParameter(uri, "adid", DeviceUtils.getAdvertisingId()).toString();
            }
        }

        // 뒤에 파라메터가 있을 경우 파라메터를 더해준다.
        String strLinkParams[] = linkUrl.split(".gs\\?");
        if (strLinkParams.length > 1) {
            mPageUrl += strLinkParams[1];
        }

        //단품api - 모듈화컨텐츠AB 실험대상이면 api에 abinfo붙임
        ApptimizeBaseExp apptimizeBaseExp = ApptimizeExpManager.findExpInstance(ApptimizeExpManager.CONTENTS);
        if(apptimizeBaseExp != null ){
            mPageUrl = mPageUrl + "&" + "abinfo=" + apptimizeBaseExp.getAll();
        }else{

        }

        new BaseAsyncController<NativeProductV2>(context) {
            private String url;

            @Override
            protected void onPrepare(Object... params) throws Exception {
                url = (String) params[0];
            }

            @Override
            protected NativeProductV2 process() throws Exception {
                //배송지변경시 쿠키동기화 문제로 웹뷰쿠키를 복사해준다.
                CookieUtils.syncWebViewCookiesToRestClient(MainApplication.getAppContext(), restClient);
                return restClient.getForObject(url, NativeProductV2.class);
            }

            @Override
            protected void onSuccess(NativeProductV2 result) throws Exception {
                if (isNotEmpty(result)) {
                    mSkeletonFrame.stopShimmer();
                    mSkeletonFrame.setVisibility(View.GONE);
                    //프리미엄 단품은 웹뷰로 띄움
                    mPrdTypeCode = result.prdTypeCode;
                    if (!"PRD".equalsIgnoreCase(mPrdTypeCode) && !"DEAL".equalsIgnoreCase(mPrdTypeCode)
                            && !"PRD_FRESH".equalsIgnoreCase(mPrdTypeCode) && !"PRD_THEBANCHAN".equalsIgnoreCase(mPrdTypeCode)) {
                        mResizeAppNativeDivHandler.removeCallbacks(mResizeAppNativeDivRunnable);
                        finish();
                        Intent intent = new Intent(Keys.ACTION.NO_TAB_WEB);
                        intent.putExtra(Keys.INTENT.WEB_URL, linkUrl);
                        startActivity(intent);
                        return;
                    }
                    //amp를 위한 세이터 초기화 setTemplate 보다 먼져 되어야함 저 안에 setData있음
                    initBaseData(result);
                    setTemplate(result);

                    //resultCode 값이 "200" 아닌 경우 루나 전송
                    if (isNotEmpty(result.resultCode) && isNotEmpty(result.resultMessage) && !RESULT_CODE.equals(result.resultCode)) {
                        LunaUtils.sendToLuna(context, new Exception(result.resultCode + " : " + result.resultMessage), LunaUtils.PREFIX_NATIVE_PRODUCT);
                    }

                    if(result.prdCd != null){
                        MainApplication.lastPrdId = result.prdCd;
                    }
                }
            }

            @Override
            protected void onError(Throwable e) {
                //로그인완료 이벤트 받은 경우 웹단품 띄우는 로직 스킵
                //why1 : 네이티브 단품 -> 로그인 -> 복귀시 이슈 해결용
                //why2 : 네이티브 단품 여러개가 떠있을 경우 다수의 웹단품이 뜰 수 있음
                if (!isLoggedInProcessing) {
                    mSkeletonFrame.stopShimmer();
                    mSkeletonFrame.setVisibility(View.GONE);
                    // api error 일 경우 일반 단품 웹으로 보낸다.
                    mResizeAppNativeDivHandler.removeCallbacks(mResizeAppNativeDivRunnable);
                    mWebAppTouchInteractionHandler.removeCallbacks(mWebAppTouchInteractionRunnable);
                    finish();
                    Intent intent = new Intent(Keys.ACTION.NO_TAB_WEB);
                    intent.putExtra(Keys.INTENT.WEB_URL, linkUrl);
                    startActivity(intent);
                }
            }
        }.execute(apiUrl);
    }

    /**
     * 단품 초개인화 api 호출
     * @param context
     * @param apiUrl 초개인화 api
     */
    private void asyncNativeProductPersonalization(Context context, String apiUrl) {
        mProgressBar.setVisibility(View.VISIBLE);

        new BaseAsyncController<NativeProductV2.PersonalizationResult>(context) {
            private String url;

            @Override
            protected void onPrepare(Object... params) throws Exception {
                url = (String) params[0];
            }

            @Override
            protected NativeProductV2.PersonalizationResult process() throws Exception {
                return restClient.getForObject(url, NativeProductV2.PersonalizationResult.class);
            }

            @Override
            protected void onSuccess(NativeProductV2.PersonalizationResult result) throws Exception {
                if (isNotEmpty(result)) {
                    //amp를 위한 세이터 초기화 setTemplate 보다 먼져 되어야함 저 안에 setData있음
                    setPersonalizationTemplate(result);

                    //resultCode 값이 "200" 아닌 경우 루나 전송
//                    if (isNotEmpty(result.resultCode) && isNotEmpty(result.resultMessage) && !RESULT_CODE.equals(result.resultCode)) {
//                        LunaUtils.sendToLuna(context, new Exception(result.resultCode + " : " + result.resultMessage), LunaUtils.PREFIX_NATIVE_PRODUCT);
//                    }
                }
            }

            @Override
            protected void onError(Throwable e) {
                Ln.e(e);
            }
        }.execute(apiUrl);
    }

    /**
     * 유지되어야할 데이터를 관리한다.
     *
     * @param result
     */
    private void initBaseData(NativeProductV2 result) {
        if (result != null) {
            //딜
            if ("DEAL".equalsIgnoreCase(result.prdTypeCode)) {
                ampIsDeal = true;
                if (result.dealNo != null)
                    ampProductCd = result.dealNo;

                ampProductNm = "";
                ampProductType = "";
            } else    //단품
            {
                ampIsDeal = false;
                if (result.prdCd != null)
                    ampProductCd = result.prdCd;

                ampProductNm = "";
                ampProductType = "";
            }
        }

    }

    /**
     * amp를 위한 상품소싱 및 상품명 셋팅
     *
     * @param component
     */
    private void setProductInfo(NativeProductV2.Component component) {
        if (component != null && component.expoName != null && component.expoName.size() > 0) {
            //0번째 상품명
            if (component.expoName.size() == 1) {
                if (component.expoName.get(0) != null) {
                    //상품
                    ampProductNm = component.expoName.get(0).textValue;
                    ampProductType = "";
                }
            }
            //0번째 소싱 1번째 상품명
            else if (component.expoName.size() > 1) {
                if (component.expoName.get(0) != null && component.expoName.get(1) != null) {
                    //소싱 & 상품
                    ampProductType = component.expoName.get(0).textValue;
                    ampProductNm = component.expoName.get(1).textValue;
                }

            }
        }
    }


    private void setTemplate(NativeProductV2 result) {
        /**
         * 성인 인증 후 성인 상품 / 비인증 상태 일 경우 성인인증 URL로 이동.
         */

//        String json = JsonUtils.loadJSONFromAsset(context, "json/WebPrd_nonlogin.json");
//        Gson gson = new Gson();
//        result = gson.fromJson(json, NativeProductV2.class);

        if ("N".equals(result.checkAdultPrdYN)) {
            // 주류 상품인 경우에는 adult cookie 의 값에 상관 없이 무조건 성인인증 페이지로 보낸다
            // ios 의 경우 rest server cookie 와 webView cookie 가 다른것도 있고, 성인 인증 후에도 checkAdultPrdYN 값이 N으로 내려옴
            // 그래서 ios 는 cookie 값 중 alcoholAdult 를 바라보고 검사한다. (안드로이드랑 로직이 다름)
            if (result.adultCertRetUrl.contains("isAlcolAdultPrd=Y")) {
                String returnUrl = getIntent().getStringExtra(Keys.INTENT.WEB_URL);
                returnUrl = IceBergEncoder.encode(returnUrl);
                goToLink(result.adultCertRetUrl + returnUrl);
                finish();
                return;
            }
            // 성인 인증 체크가 정상적이지 않을 때에는 쿠키 값을 직접 확인하여 true / temp 아닐때에 성인 요청.
            String adult = CookieUtils.getAdult(MainApplication.getAppContext());
            if (!("true".equals(adult) || "temp".equals(adult))) {
                String returnUrl = getIntent().getStringExtra(Keys.INTENT.WEB_URL);
                returnUrl = IceBergEncoder.encode(returnUrl);
                goToLink(result.adultCertRetUrl + returnUrl);
                finish();
                return;
            }
        }

        // 해당 URL 있을 시에 클릭 하면 이동하게끔 하기위함.
        mShoppyUrl = result.shoppygateurl;

        result.autoPLay = mAutoPlay;
        if (isNotEmpty(result.components.get(0).imgUrlList)) {
            result.chachedImgUrl = result.components.get(0).imgUrlList.get(0);
        }

        // GS Fresh 상품의 경우 pager 왼쪽 하단에 혜택 이미지 그려져야함.
        mLlGsFreshBenefitsArea.removeAllViews();
        if (result.components.get(0).benefits != null) {
            for (String benefitsImgUrl : result.components.get(0).benefits) {
                ImageView iv = new ImageView(getApplicationContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics())));
                iv.setLayoutParams(lp);
                ImageUtil.loadImage(this, benefitsImgUrl, iv, 0);
                mLlGsFreshBenefitsArea.addView(iv);
            }
        }

        // api 받아 온 후에는 이미지 preview를 안보이게끔 하고 페이저를 보이게끔
        ViewUtils.hideViews(mImagePre);
        ViewUtils.showViews(mViewPager);

        // 초기 이미지 어댑터에 셋 한 후 나중에 setData 하는 것이 아닌 선언할 때에 result 값을 던져줌.
        mAdapter = new PagerAdapterDetailView(ProductDetailWebActivityV2.this, result);

        if (mAdapter != null) {
            mAdapter.setListener(onMediaPlayerListener);
            mViewPager.setAdapter(mAdapter);
            mAdapter.setPager(mViewPager);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int i) {
                    // 비디오가 존재할 때에 플레이 버튼 유무 확인.
                    if (mAdapter.isVideoExist) {
                        try {
                            if (mAdapter.getListData().get(i).contentType == PagerAdapterDetailView.ContentType.TYPE_IMAGE &&
                                    !mBroadcastFinished) {// && !mIsErrorOccurred && !mAdapter.isVideoStarted()) {
                                showMiniPlayButton(true);
                            } else {
                                showMiniPlayButton(false);
                            }
                        } catch (NullPointerException e) {
                            Ln.e(e.getMessage());
                        }
                    }
                    if (mAdapter.getCount() > 1) {
                        mTvPagerCount.setText((i + 1) + " / " + mAdapter.getCount());
                    }
                    mAdapter.setVideoPlay(i);
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });
            if (mAdapter.getCount() > 1) {
                mTvPagerCount.setVisibility(View.VISIBLE);
                mTvPagerCount.setText((1 + " / " + mAdapter.getCount()));
            } else {
                mTvPagerCount.setVisibility(View.GONE);
            }
            //        mAdapter.notifyDataSetChanged();
        }

        mLlComponentsContainer.removeAllViews();

        // 개인화 영역 다음에 오는 template 은 상단 divider 를 없애기 위함.
        // prsnlPmoInfo 다음에 올 templateType 을 저장한다.
        Iterator<NativeProductV2.Component> itr1 = result.components.iterator();
        String nextPrsnlTemplate = "";
        while (itr1.hasNext()) {
            if ("prsnlPmoInfo".equalsIgnoreCase(itr1.next().templateType)) {
                if (itr1.hasNext()) {
                    nextPrsnlTemplate = itr1.next().templateType;
                }
            }
        }

        // 가격 영역 다음에 오는 template 은 상단 divider 를 없애기 위함.
        // saleInfo 다음에 올 templateType 을 저장한다.
        Iterator<NativeProductV2.Component> itr2 = result.components.iterator();
        String nextSaleInfoTemplate = "";
        while (itr2.hasNext()) {
            if ("saleInfo".equalsIgnoreCase(itr2.next().templateType)) {
                if (itr2.hasNext()) {
                    nextSaleInfoTemplate = itr2.next().templateType;
                }
            }
        }

        for (int i = 0; i < result.components.size(); i++) {
            NativeProductV2.Component component = result.components.get(i);
            try {
                templateType = TEMPLATE_TYPE.valueOf(component.templateType);
            } catch (IllegalArgumentException e) {
                //정의되지 않은 templateType 값이 들어올 경우
                continue;
            }
            switch (templateType) {
                case mediaInfo:
                    break;
                case broadInfo:
                    ProductDetailBroadcastLayout productDetailBroadcastLayout = new ProductDetailBroadcastLayout(this);
                    mLlComponentsContainer.addView(productDetailBroadcastLayout);
                    productDetailBroadcastLayout.setProductDetailBroadcastArea(ProductDetailWebActivityV2.this, component);
                    break;
                case prdNmInfo:
                    ProductDetailTitleLayout productDetailTitleLayout = new ProductDetailTitleLayout(this);
                    mLlComponentsContainer.addView(productDetailTitleLayout);
                    productDetailTitleLayout.setProductDetailTitleArea(component);
                    //amp를 위한 상품정보 세팅 생 하드코딩
                    setProductInfo(component);
                    break;

                case saleInfo:
                    ProductDetailPriceLayout productDetailPriceLayout = new ProductDetailPriceLayout(this);
                    mLlComponentsContainer.addView(productDetailPriceLayout);
                    productDetailPriceLayout.setProductDetailPriceArea(component);

                    for (NativeProductV2.Component componentObj : result.components) {
                        if (TEMPLATE_TYPE.promotionInfo.equals(componentObj.templateType)) {
                            productDetailPriceLayout.setBottomMargin();
                        }
                    }
                    break;
                case promotionInfo:
                    ProductDetailPromotionInfoLayout productDetailPromotionInfoLayout = new ProductDetailPromotionInfoLayout(this);
                    mLlComponentsContainer.addView(productDetailPromotionInfoLayout);
                    productDetailPromotionInfoLayout.setProductDetailPromotionArea(component);
                    break;
                case cardPmoInfo:
                case cardPmoInfo2:
                    ProductDetailCardPmoInfoLayout productDetailCardPmoInfoLayout = new ProductDetailCardPmoInfoLayout(this);
                    mLlComponentsContainer.addView(productDetailCardPmoInfoLayout);
                    // 가격 영역 및 가격 개인화 다음에 오는 뷰는 상단 1px 라인을 없앤다
                    if ("cardPmoInfo".equalsIgnoreCase(nextPrsnlTemplate) ||
                            "cardPmoInfo2".equalsIgnoreCase(nextPrsnlTemplate) ||
                            "cardPmoInfo".equalsIgnoreCase(nextSaleInfoTemplate) ||
                            "cardPmoInfo2".equalsIgnoreCase(nextSaleInfoTemplate)) {
                        productDetailCardPmoInfoLayout.hideTopLine();
                    }

                    productDetailCardPmoInfoLayout.setProductDetailCardPmoArea(component);
                    break;
                case addPromotionInfo:
                    ProductDetailAddPmoInfoLayout productDetailAddPmoInfoLayout = new ProductDetailAddPmoInfoLayout(this);
                    mLlComponentsContainer.addView(productDetailAddPmoInfoLayout);
                    // 가격 영역 및 가격 개인화 다음에 오는 뷰는 상단 1px 라인을 없앤다
                    if ("addPromotionInfo".equalsIgnoreCase(nextPrsnlTemplate) ||
                            "addPromotionInfo".equalsIgnoreCase(nextSaleInfoTemplate)) {
                        productDetailAddPmoInfoLayout.hideTopLine();
                    }
                    productDetailAddPmoInfoLayout.setProductDetailAddPmoArea(component);
                    break;
                case personalCouponPmoInfo:
                    ProductDetailCouponPmoInfoLayout productDetailCouponPmoInfoLayout = new ProductDetailCouponPmoInfoLayout(this);
                    mLlComponentsContainer.addView(productDetailCouponPmoInfoLayout);
                    productDetailCouponPmoInfoLayout.setProductDetailCouponAreaPmo(component);
                    break;
                case noInterestInfo:
                    ProductDetailFreeInterLayout productDetailFreeInterLayout = new ProductDetailFreeInterLayout(this);
                    mLlComponentsContainer.addView(productDetailFreeInterLayout);
                    productDetailFreeInterLayout.setProductDetailFreeInterArea(component);
                    // 가격 영역 및 가격 개인화 다음에 오는 뷰는 상단 1px 라인을 없앤다
                    if ("noInterestInfo".equalsIgnoreCase(nextPrsnlTemplate) ||
                            "noInterestInfo".equalsIgnoreCase(nextSaleInfoTemplate)) {
                        productDetailFreeInterLayout.hideTopLine();
                    }
                    break;
                case deliveryInfo:
                    ProductDetailDeliveryLayout productDetailDeliveryLayout = new ProductDetailDeliveryLayout(this);
                    mLlComponentsContainer.addView(productDetailDeliveryLayout);
                    productDetailDeliveryLayout.setProductDetailDeliveryArea(component);
                    // 가격 영역 및 가격 개인화 다음에 오는 뷰는 상단 1px 라인을 없앤다
                    if ("deliveryInfo".equalsIgnoreCase(nextPrsnlTemplate) ||
                            "deliveryInfo".equalsIgnoreCase(nextSaleInfoTemplate)) {
                        productDetailDeliveryLayout.hideTopLine();
                    }
                    break;
                case prsnlPmoInfo:
                    mProductDetailPricePersonalizationLayout = new ProductDetailPricePersonalizationLayout(this);
                    mLlComponentsContainer.addView(mProductDetailPricePersonalizationLayout);
                    mProductDetailPricePersonalizationLayout.setProductDetailPricePersonInfoLayout(component);
                    if (!TextUtils.isEmpty(component.prsnlApiUrl)) {
                        asyncNativeProductPersonalization(context, component.prsnlApiUrl);
                    }
                    break;
                default:
                    break;
            }
        }

        // 모듈화컨텐츠 AB테스트 - 대상단품페이지 진입시 (실험대상이면서 실험대상상품이면 효율보냄)
        if(ApptimizeCommand.ABINFO_VALUE.contains(ApptimizeExpManager.CONTENTS)){
            if("Y".equals(result.isContentAB)) {
                //앰플리튜드를 위한 코드
                try {
                    JSONObject eventProperties = new JSONObject();
                    eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.AMP_VIEW_CONTENTS_PRD);

                    eventProperties.put(AMPEnum.AMP_PRD_CODE, mAdapter.getData().prdCd);
                    eventProperties.put(AMPEnum.AMP_AB_INFO, ApptimizeCommand.ABINFO_VALUE);
                    AMPAction.sendAmpEventProperties(AMPEnum.AMP_VIEW_CONTENTS_PRD,eventProperties);
                }catch (Exception e){
                    Ln.e(e);
                }

                //앱티마이즈를 위한 코드
                Apptimize.track(AMPEnum.APPTI_VIEW_CONTENTS_PRD);
                //mseq전달
                setWiseLogHttpClient(ServerUrls.WEB.MSEQ_VIEW_CONTENTS_AB);
            }
        }
    }

    private void setPersonalizationTemplate(NativeProductV2.PersonalizationResult result) {
        mProductDetailPricePersonalizationLayout.setPrsnlApiTemplate(result);
    }

    /**
     * 뒤로가기 버튼 리스너
     */
    private View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    /**
     * 검색 버튼 리스너
     */
    private View.OnClickListener mOnSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isMoveSearch) {
                isMoveSearch = true;
                EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(false, -1));

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        isMoveSearch = false;
                    }
                }, 300);

                Intent intent = new Intent(Keys.ACTION.SEARCH);
                intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
                startActivityForResult(intent, Keys.REQCODE.HOME_SEARCHING);

                //GTM Datahub 이벤트 전달
                DatahubAction.sendDataToDatahub(ProductDetailWebActivityV2.this, DatahubUrls.D_1016, "");

                prdAmpSend(AMPEnum.AMP_PRD_HEADER_SEARCH);

                // mseq
                if (mIsDeal) {
                    setWiseLogHttpClient(MSEQ_DEAL_NATIVE_HEADER_SEARCHING);
                } else {
                    setWiseLogHttpClient(MSEQ_PRD_NATIVE_HEADER_SEARCHING);
                }
            }
        }
    };

    /**
     * 장바구니 버튼 리스너
     */
    private View.OnClickListener mOnCartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // GS Fresh 매장일 경우에만 장바구니 클릭 시 장바구니 내에 GS Fresh tab 선택된 화면 노출하기 위한 분기
            if ("PRD_FRESH".equalsIgnoreCase(mPrdTypeCode)) {
                WebUtils.goWeb(context, ServerUrls.WEB.FRESH_SMART_CART_TOP);
            } else {
                WebUtils.goWeb(context, ServerUrls.WEB.SMART_CART);
            }
            //GTM 노출이벤트 전달
            String action = String.format("%s_%s", GTMEnum.GTM_ACTION_HEADER,
                    GTMEnum.GTM_ACTION_CART_TAIL);
            GTMAction.sendEvent(ProductDetailWebActivityV2.this, GTMEnum.GTM_AREA_CATEGORY,
                    action,
                    ServerUrls.WEB.SMART_CART);

            //상단 Cart 버튼 단품내 효율로 변경
            //AMPAction.sendAmpEvent(AMPEnum.AMP_ACTION_MAIN_SMARTCART);
            prdAmpSend(AMPEnum.AMP_PRD_HEADER_CART);

            // mseq
            if (mIsDeal) {
                setWiseLogHttpClient(MSEQ_DEAL_NATIVE_HEADER_BASKET);
            } else {
                setWiseLogHttpClient(MSEQ_PRD_NATIVE_HEADER_BASKET);
            }
        }
    };

    /**
     * 홈 버튼 리스너
     */
    private View.OnClickListener mOnHomeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ClickUtils.work(3000)) {
                return;
            }

            //Hyper-Pesonalized Curation 대상으로 설정
            showPsnlCuration = true;
            psnlCurationType = HomeActivity.PsnlCurationType.PRD;

            //Hyper Personalized Curation(DT과제)
            //백키로 돌아왔을때 -> 지연없이 이벤트 보내고
            //홈버튼 돌아왔을때 -> 2초 지연후 이벤트 보내는
            //구분용 플래그
            MainApplication.calledFromBackKey = false;

            //숏방데이타 초기화
            CategoryDataHolder.putCategoryData(null);

            GetHomeGroupListInfo(false);

            prdAmpSend(AMPEnum.AMP_PRD_HEADER_HOME);

            if (mIsDeal) {
                setWiseLogHttpClient(MSEQ_DEAL_NATIVE_HEADER_HOME);
            } else {
                setWiseLogHttpClient(MSEQ_PRD_NATIVE_HEADER_HOME);
            }
        }
    };


    /**
     * 상단 영상영역 재생 일시정지 버튼 리스너 (뷰페이저 동영상 아님)
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_play_pause:
                    if (mIsVideoPlaying) {
                        mBtnPlayPause.setBackgroundResource(R.drawable.play_arrow);
                        if (mAdapter != null) {
                            mAdapter.pausePlayer();
                        }
                        mIsVideoPlaying = false;
                    } else {
                        mBtnPlayPause.setBackgroundResource(R.drawable.pause);
                        if (mAdapter != null) {
                            mAdapter.startPlayer();
                        }
                        mIsVideoPlaying = true;
                    }
                    break;

                case R.id.btn_video_close:
                    mBtnPlayPause.setBackgroundResource(R.drawable.play_arrow);
                    if (mAdapter != null) {
                        mAdapter.pausePlayer();
                    }
                    mIsVideoPlaying = false;
                    mIsTopVideoPlaying = false;
                    hideMiniVideoView();
                    if (mAdapter != null) {
                        mAdapter.showPlayControllerView(true);
                    }
                    break;

                case R.id.image_move_to_player:
                    if (mAdapter != null) {
                        mAdapter.moveToPlayerView();
                    }
                    break;
            }
        }
    };

    /**
     * 영상 재생 관련 리스너
     */
    private OnMediaPlayerListener onMediaPlayerListener = new OnMediaPlayerListener() {
        @Override
        public void onRemainedTime(String remainedTime) {
            // 영상 플레이 중이 아니더라도 시간 갱신은 되어야 한다.
            mTvRemainTime.setText(remainedTime);
        }

        @Override
        public void onTap(boolean show) {
            // 탭 했을 때 탑 비디오가 보이고 있는 상태야.
            if (DisplayUtils.isVisible(mTopVideoArea)) {

                //기존에 받은 샤피 URL이 있으면 이동
                if(!TextUtils.isEmpty(mShoppyUrl)) {
                    WebUtils.goWeb(context, mShoppyUrl);
                }
                else {
                    // 아니면 비디오 위로 올리고 비디오 플레이 상태로 변환
                    mIsVideoPlaying = true;
                    mNativeScroll.fling(0);
                    mNativeScroll.smoothScrollTo(0, 0);
                    webView.scrollTo(0, 0);
                }
            }
        }

        @Override
        public void onPlayed() {
            // play 되었을 떄에 web으로 script 전달.
            mIsVideoPlaying = true;
            goToLink(getJavaScriptValue("playStatus('Y')"));
        }

        @Override
        public void onMute(boolean on) {
            if (on) {
                goToLink(getJavaScriptValue("muteStatus('Y')"));
            } else {
                goToLink(getJavaScriptValue("muteStatus('N')"));
            }
        }

        @Override
        public void onPaused() {
            // pause 면 플레이 안해
            mIsVideoPlaying = false;
            mBtnPlayPause.setBackgroundResource(R.drawable.play_arrow);
        }

        @Override
        public void onFinished(MediaInfo media) {
            if (media.videoMode == NativeProductV2.VIDEO_MODE_LIVE) {
                ViewUtils.hideViews(mBtnPlayPause);
                mBroadcastFinished = true;
            } else {
                setMiniVideoReplay();
            }
        }

        @Override
        public void onNoDataWarningApproved() {
            MainApplication.isNetworkApproved = true;
            goToLink(getJavaScriptValue("setNoDataWarningFlag('Y')"));
        }

        @Override
        public void onError(Exception e) {
            mIsVideoPlaying = false;
            if (mIsTopVideoPlaying) {
                // 에러나면 플레이 안해
                mIsTopVideoPlaying = false;
                hideMiniVideoView();
            }
            if (!mIsErrorOccurred) {
                // 한번만 보여준다.
                mIsErrorOccurred = true;
                // 에러 났을 때에 다시 로딩 하기 위한 다이얼로그
                new CustomOneButtonDialog(ProductDetailWebActivityV2.this)
                        .message(R.string.network_unstable_detail_Vew)
                        .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                if (mAdapter != null) {
                                    mAdapter.reloadPlayer();
                                }
                                dialog.dismiss();
                            }
                        }).cancelable(false).show();
            }
        }

        /**
         * 네이티브 단품 용 효율 코드 mseq
         * @param action
         * @param isLive
         * @param isAuto
         * @param videoId
         * @param playTime
         * @param totalTime
         */
        @Override
        public void sendWiseLogPrdNative(PlayerAction action, boolean isLive, boolean isAuto, String videoId, int playTime, int totalTime) {
            // 단품 네이티브용 wiselog 호출

            String linkUrl = getIntent().getStringExtra(Keys.INTENT.WEB_URL);

            String url = null;
            String prdId = "";
            String ref = "";

            videoId = isNotEmpty(videoId) ? videoId : "";

            if (isNotEmpty(linkUrl)) {
                prdId = StringUtils.getUriParameter(linkUrl, PRDID_KEY);
                prdId = prdId == null ? "" : prdId;

                ref = StringUtils.getUriParameter(linkUrl, REF_KEY);
                ref = ref == null ? "" : ref;
            }

            if (isLive) {
                String strMseq = MSEQ_LIVE_MANUAL;
                if (isAuto) {
                    strMseq = MSEQ_LIVE_AUTO;
                }
                url = MSEQ_PRD_NATIVE_LIVE.replace("#MSEQ", strMseq);
            } else {
                String strMseq = MSEQ_VOD_MANUAL;
                if (isAuto) {
                    strMseq = MSEQ_VOD_AUTO;
                }
                url = MSEQ_PRD_NATIVE_VOD.replace("#PT", Integer.toString(playTime / 1000))
                        .replace("#TT", Integer.toString(totalTime / 1000))
                        .replace("#MSEQ", strMseq);
                if (isEmpty(videoId)) {
                    url = url.replace("&vid=#VID", "");
                }
                url = url.replace("#VID", videoId);
            }

            if (isEmpty(ref)) {
                url = url.replace("&ref=#REF", "");
            }

            url = url.replace("#ACTION", action.toString())
                    .replace("#PRDID", prdId)
                    .replace("#REF", ref);

//            Ln.d("hklim send wiselog url : " + url);

            if (action == PlayerAction.LTE_Y) {
                //LTE_Y와 PLAY 액션이 30ms 이내에 같이 호출되어 먼저 호출된 액션(LTE_Y)이 누락되는 현상 발생
                //LTE_Y는 일정시간 딜레이 후 호출
                sendWiseLogWithDelay(url);
            } else {
                setWiseLogHttpClient(url);
            }
        }
    };


    /**
     * 딜 단품 구매하기 버튼 눌렀을때 나오는 장바구니 클릭시 상단 장바구니 update 해주기 위함.
     *
     * @param event
     */
    public void onEvent(Events.FlexibleEvent.RefreshCart event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setBasketCnt();
            }
        });
    }

    /**
     * 딜 -> 하위 상품 자세히보기 레이어 팝업일때 네이티브 상단 헤더를 감추고 보이는 함수.
     *
     * @param event
     */
    public void onEvent(Events.EventProductDetail.HideHeaderEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.isHide) {
                    mLayoutTitle1.setVisibility(View.GONE);
                    mLayoutTitle2.setVisibility(View.GONE);
                } else {
                    mLayoutTitle1.setVisibility(View.VISIBLE);
                    mLayoutTitle2.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 영상을 정지하기 위한 이벤트
     *
     * @param event
     */
    public void onEventMainThread(Events.EventProductDetail.PlayStopEvent event) {
        if (mAdapter != null) {
            mAdapter.pausePlayer();
        }
    }

    /**
     * 영상 음소거 이벤트
     *
     * @param event
     */
    public void onEvent(Events.EventProductDetail.MutePlayerEvent event) {
        if (mAdapter != null) {
            mAdapter.setMute(event.mIsMute);
        }
    }

    /**
     * LayerPopupEvent 가 enableTitleDim true 상태로 오면 미니 비디오 뷰 숨김
     *
     * @param event
     */
    public void onEvent(Events.EventProductDetail.LayerPopupEvent event) {
        setMiniPlayer(event.isOpen);

        // getChildAt 을 해올 때 별도의 thread 가 생성되는 현상을 해결하기 위해 handler 로 original Thread 이용하도록 함.
        mNavigationEventHandler.post(new Runnable() {
            @Override
            public void run() {
                if (event.isOpen) {
                    if (event.enableTitleDim) {
                        //딜 자세히보기 외 모든 레이어는 딤 노출
                        mViewDim.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < mLayoutTitle1.getChildCount(); i++) {
                        mLayoutTitle1.getChildAt(i).setEnabled(false);
                        mLayoutTitleOver.getChildAt(i).setEnabled(false);
                    }
                } else {
                    mViewDim.setVisibility(View.GONE);
                    for (int i = 0; i < mLayoutTitle1.getChildCount(); i++) {
                        mLayoutTitle1.getChildAt(i).setEnabled(true);
                        mLayoutTitleOver.getChildAt(i).setEnabled(true);
                    }
                }
            }
        });
    }

    /**
     * NoDimmLayerOpenEvent 가 다른 경우에 비슷하게 발생.
     * (해당 이벤트는 클릭이벤트가 정상 동작 해야 한다. 따라서 미니 비디오 뷰만 숨김.)
     *
     * @param event
     */
    public void onEvent(Events.EventProductDetail.NoDimmLayerOpenEvent event) {
        setMiniPlayer(event.isOpen);
    }

    /**
     * 미니 플레이어를 웹에서 브릿지를 통해 호출했을 때 상태값에 따라 동작하게 함.
     *
     * @param isOpen
     */
    private void setMiniPlayer(boolean isOpen) {
        mHandlerEvent.post(new Runnable() {
            @Override
            public void run() {
                if (isOpen) {
                    mIsLayerOpen = true;
                    mIsMiniVideoVisibled = false;
                    // 미니 비디오 보이는 중이였는지 여부 저장
                    if (DisplayUtils.isVisible(mTopVideoArea)) {
                        mIsMiniVideoVisibled = true;
                    }
                    // 비디오 재생 중이었는지 저장
                    mIsMiniVideoPlaying = mIsVideoPlaying;
                    // 플레이 멈춰야 하니. hide 한다.
                    mIsVideoPlaying = false;
                    hideMiniVideoView();
                    // 미니 플레이어 invisible 상태에서 바로 바뀌지 않게끔 한다.(scorll 불려 바로 visible 불리는 경우가 있어서)
                    mIsMiniPlayerInvisibleNow = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsMiniPlayerInvisibleNow = false;
                        }
                    }, 500);
                } else {
                    mIsLayerOpen = false;
                    // 미니 비디오가 보이는 중이 었다면.
                    if (mIsMiniVideoVisibled) {
                        // 미니 플레이 재생 중이었으면 재생 관련 변수에 값을 넣어주고 미니 비디오 보여준다.
                        mIsVideoPlaying = mIsMiniVideoPlaying;
                        showMiniVideoView();
                    }
                }
            }
        });
    }

    /**
     * 쿠키의 정보를 바탕으로
     * 장바구니 카운트를 표시한다
     */
    private void setBasketCnt() {
        BadgeTextView badgeTextView1 = (BadgeTextView) findViewById(R.id.basketcnt_badge_1);
        BadgeTextView badgeTextView2 = (BadgeTextView) findViewById(R.id.basketcnt_badge_2);
        if (badgeTextView1 != null && badgeTextView2 != null) {
            badgeTextView1.setVisibility(View.GONE);
            badgeTextView2.setVisibility(View.GONE);

            NameValuePair pair = CookieUtils.getWebviewCookie(this, "cartcnt");
            if (pair != null) {
                String value = pair.getValue();

                try {
                    int count = Integer.parseInt(value.trim());
                    if (count > 99) {
                        count = 99;
                    }
                    if (count > 0) {
                        badgeTextView1.setText(String.valueOf(count));
                        badgeTextView2.setText(String.valueOf(count));
                        badgeTextView1.setVisibility(View.VISIBLE);
                        badgeTextView2.setVisibility(View.VISIBLE);
                    } else {
                        badgeTextView1.setVisibility(View.GONE);
                        badgeTextView2.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    badgeTextView1.setVisibility(View.GONE);
                    badgeTextView2.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 상단 미니 비디오 영역을 보여주고 영상 뷰를 옮긴다.
     */
    private void showMiniVideoView() {
        // 비디오 재생중일 때에 혹은 미니 비디오가 보이지 않고 있었을 때만, 레이어 오픈 상태가 아닐때만.
        if ((mIsVideoPlaying || mIsMiniVideoVisibled) && !mIsTopVideoPlaying && !mIsMiniPlayerInvisibleNow && !mIsLayerOpen && !mIsDeal) {
            // 해당 변수는 show 하면 false 로 초기화 해주어야 한다.
            mIsMiniVideoVisibled = false;

            mBtnPlayPause.setBackgroundResource(R.drawable.pause);
            // 계속 확인해서 remove 하고 추가하고 있어 문제 발생. 한번만 remove
            // 영상 재생 영역이 보이지 않는다면.
            if (!DisplayUtils.isVisible(mTopVideoArea) && mAdapter != null) {
                mAdapter.setPlayerSize(true);
                // 현재 플레이어 영역 뷰를 가져와서
                View viewPlayer = mAdapter.getPlayerMainView();
                // 플레이어 뷰가 null 이 아니라면
                if (viewPlayer != null) {
                    int currentVideoHeight = mAdapter.getCurrentVideoHeight();
                    int currentVideoWidth = mAdapter.getCurrentVideoWidth();

                    if (currentVideoHeight > -1 && currentVideoWidth > -1) {
                        // 세로로 긴 영상 여부 확인
                        if (currentVideoHeight > currentVideoWidth) {
                            FrameLayout.LayoutParams parmas = (FrameLayout.LayoutParams) viewPlayer.getLayoutParams();
                            // 마진 계산 후에 height 값계산해서 적용 예정.
                            int margin = 25;
                            parmas.topMargin = -DisplayUtils.convertDpToPx(context, margin);
                            parmas.bottomMargin = -DisplayUtils.convertDpToPx(context, margin);
                            viewPlayer.setLayoutParams(parmas);
                        }
                    }

                    // 뷰의 Parent를 가져와서
                    ViewGroup viewParent = (ViewGroup) viewPlayer.getParent();
                    // 뷰를 제거해 주고
                    try {
                        viewParent.removeView(viewPlayer);
                    } catch (NullPointerException e) {
                        Ln.e(e.getMessage());
                    }

                    // 미니 영역에다가 playerView 를 추가.
                    try {
                        mFlTopVideo.addView(viewPlayer);
                    } catch (IllegalStateException e2) {
                        Ln.e(e2.getMessage());
                    }

                    // 그런 후 상단 비디오 영역을 Visible.
                    mTopVideoArea.setVisibility(View.VISIBLE);

                    mAdapter.setControllerVisible(false);
                    //adapter 에서 사용하는 interface controller 역시 false 로 설정 (이건 단지 현재 visible 되고 있는 플레이어 내부 컨트롤러 showing을 false로)
                    mAdapter.showPlayControllerView(false);

                    if (mIsVideoPlaying) {
                        mAdapter.startPlayer();
                        mIsTopVideoPlaying = true;
                    }

                    // 미니 디스플레이의 y값을 보내준다.
                    goToLink(getJavaScriptValue("setShowMiniPlayer('108')"));
                }
            }
        }
    }

    /**
     * 상단 미니 비디오 영역을 사라지게 하고 비디오 뷰의 위치를 기존으로 돌린다.
     */
    private void hideMiniVideoView() {
        // 네이티브 영역이 보이고 현재 미니 비디오 영역이 보일경우.
        if (DisplayUtils.isVisible(mTopVideoArea) && mAdapter != null) {
            mAdapter.setPlayerSize(false);
            // 현재 미니 영역에서 remove
            goToLink(getJavaScriptValue("setHideMiniPlayer('44')"));
            View viewPlayer = mAdapter.getPlayerMainView();
            ViewGroup viewParent = (ViewGroup) mAdapter.getPlayerParentView();
            // 저장해 놓은 adapter의 parent가 null 이 아니면
            if (viewParent != null && viewPlayer != null) {
                ViewGroup tempView = (ViewGroup) viewPlayer.getParent();
                if (tempView != null) {
                    tempView.removeView(viewPlayer);
                }
                // View를 다시 돌려놓는다.
                viewParent.addView(viewPlayer);

                FrameLayout.LayoutParams parmas = (FrameLayout.LayoutParams) viewPlayer.getLayoutParams();
                parmas.topMargin = 0;
                parmas.bottomMargin = 0;
                viewPlayer.setLayoutParams(parmas);

                mTopVideoArea.setVisibility(View.GONE);
                mAdapter.setControllerVisible(true);

                //mIsVideoPlaying 이 true 이면 재생 중이었기 떄문에 플레이어 스타트 한다.
                if (mAdapter != null) {
                    if (mBroadcastFinished) {
                        mAdapter.setShowBroadcastFinishView();
                    }

                    if (mIsVideoPlaying && !mBroadcastFinished) {
                        mAdapter.startPlayer();
                    } else {
                        mAdapter.pausePlayer();
                    }
                }
                mIsTopVideoPlaying = false;
            }
        }
    }

    private void setMiniVideoReplay() {
        mBtnPlayPause.setBackgroundResource(R.drawable.replay);
        mIsVideoPlaying = false;
        // 방송 종료 되서 리플레이로 표시하면 0:00 으로 표시
        mTvRemainTime.setText("00:00");
    }

    /**
     * 웹뷰에 url을 로딩한다.
     */
    @Override
    public void loadUrl() {
        super.loadUrl();

        // 인텐트로 전달된 url이 있으면 그 url 로드. 없으면 홈 url 로드.
        String url = getIntent().getStringExtra(Keys.INTENT.WEB_URL);

        if (REFERRER_VERSION_CHECK_COMMAND.equalsIgnoreCase(getIntent().getStringExtra(Keys.INTENT.REFERRER))) {
            loadUrlWebView();
            //공사중 페이지인 경우 버튼 노출
            findViewById(R.id.btn_restart).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //앱 재시작
                    AppFinishUtils.finishApp(ProductDetailWebActivityV2.this, true);
                }
            });
        } else {
            loadUrlWebView();
        }
    }

    /**
     * 웹뷰에 url을 로딩한다.
     * url정보가 유효하지 않으면 홈액티비티를 띄운다.
     */
    private void loadUrlWebView() {
        // 2020.04.24 yun. onPageFinished 가 호출되지 않는 경우를 대비해 2초 딜레이 후 web 으로 스크립트를 쏘도록 함.
        mResizeAppNativeDivHandler.postDelayed(mResizeAppNativeDivRunnable, 2000);
        mWebAppTouchInteractionHandler.postDelayed(mWebAppTouchInteractionRunnable, 2000);

        Uri bottomUrl = StringUtils.replaceUriParameter(Uri.parse(mPageUrl), HEIGHT_KEY, mEmptyHeight);
        webControl.loadUrl(bottomUrl.toString());
        webView.clearHistory();
    }

    @Override
    protected void setupWebControl() {
        webControl = new WebViewControlInherited.Builder(this)
                .target(webView)
                .with(new MainWebViewClient(this) {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[ProductDetailWebActivityV2 shouldOverrideUrlLoading]" + url);

                        if (handleUrl(WEB_TYPE_NO_TAB, url)) {
                            // [예외처리] 성인인증을 위해 WebActivity로  분기했을 때에는 NoTabActivity 종료
                            if (url.toLowerCase(Locale.getDefault()).contains("chkadult")) {
                                finish();
                            }
                            // [예외처리] 단품에서 dealList WebActivity로  분기했을 때에는 NoTabActivity 종료
                            if (url.toLowerCase(Locale.getDefault()).contains("deallist.gs")) {
                                finish();
                            }

                            if (url.toLowerCase(Locale.getDefault()).contains("facebook.com")) {
                                finish();
                            }
                            if (url.toLowerCase(Locale.getDefault()).contains("twitter.com/share")) {
                                finish();
                            }

                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // 구매하기.장바구니->로그인->흰화면->주문서.장바구니 상태에서 백키 클릭시 나타나는 흰화면 미리 제거
                        removeBlankPage(url, BLANK_REMOVE_DELAY);

                        // page Finished 이후에 호출해야만 하는 필수 스크립트
                        mResizeAppNativeDivHandler.post(mResizeAppNativeDivRunnable);
                        mWebAppTouchInteractionHandler.post(mWebAppTouchInteractionRunnable);

                        super.onPageFinished(view, url);

                        // 초기 스크립트 설정.
                        String strMuteStatus = MainApplication.nativeProductIsMute ? "Y" : "N";
                        mListLink.add(getJavaScriptValue("muteStatus('" + strMuteStatus + "')")); // mute 여부 전달.
                        String strNetworkApproved = MainApplication.isNetworkApproved ? "Y" : "N";
                        mListLink.add(getJavaScriptValue("setNoDataWarningFlag('" + strNetworkApproved + "')"));  // 데이터 사용자들 네트워크 승인 여부 전달
                        // 로딩 완료 전에 쌓였던 javaScript 함수 들 과 pageFinished 에서 추가 한 script 들 순서대로 실행.
                        goToLinkSequentially();
                        // 로딩 완료 여부 설정.
                        isLoadingComplete = true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Ln.i("[ProductDetailWebActivityV2 onPageStarted]" + url);

                        super.onPageStarted(view, url, favicon);

                        //GA 화면 로깅시 url을 전달함
                        try {
                            //GTM 화면로깅
                            GTMAction.openScreen(context, url);
                        } catch (Exception e) {
                            // 10/19 품질팀 요청
                            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                            Ln.e(e);
                        }
                    }

                })
                .with(new MainWebViewChromeClient(this) {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        //웹뷰공통 60% 이상 로딩시 프로그레스바 비노출 로직 스킵 (웹뷰가 로딩되기 전에 프로그레스 사라진 후 스크롤하면 버벅이는 문제)
                    }
                })
                .with(new WebViewProgressBar(mProgressBar)).build();

        super.setupWebControl();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //홈과 대상 중간에 다른 액티비티가 있는 경우 Hyper-Pesonalized Curation 대상에서 제외
        showPsnlCuration = false;
    }

    @Override
    protected void onPause() {
        //웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.
        //웹뷰쿠키 싱크를 수행하면 쿠키 손실을 방지할 수 있다.
        CookieUtils.syncWebViewCookies(context, restClient);
        if (!mOnBackPressed) {
            mBtnPlayPause.setBackgroundResource(R.drawable.play_arrow);
            if (mAdapter != null) {
                mAdapter.pausePlayer();
            }
            mIsVideoPlaying = false;
        }
        super.onPause();
    }

    // 백 버튼 선택 여부에 따라 onPause에서 수행 하지 말아야 할 동작 수행 하지 않음.
    private boolean mOnBackPressed = false;

    @Override
    public void onBackPressed() {
        //Hyper-Pesonalized Curation 대상으로 설정
        showPsnlCuration = true;
        psnlCurationType = HomeActivity.PsnlCurationType.PRD;

        WebActivity.isAdultClose = true;
        mProgressBar.setVisibility(View.VISIBLE);

        if (webControl != null && webControl.getWebChromeClient() != null) {
            if (((MainWebViewChromeClient) webControl.getWebChromeClient()).isShowCustomView()) {
                webControl.getWebChromeClient().onHideCustomView();
            } else {
                mOnBackPressed = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WebActivity.isAdultClose = false;
                    }
                }, 1000);
                super.onBackPressed();
            }
        }

    }

    /**
     * 화면을 갱신한다.
     */
    private void refresh() {
        // 장바구니 카운트 셋팅
        setBasketCnt();
        //API 다시 호출하여 네이티브 영역 변경
        asyncNativeProduct(this);
        //웹뷰 다시 로딩
        mNativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Keys.REQCODE.VIDEO:
                // 영상 full화면에서 단품으로 돌아올 경우 AMP
                prdAmpSend(AMP_PRD_MINI_FROMFULL);
                boolean isBroadcastFinished = false;
                if (intent != null) {
                    isBroadcastFinished = intent.getBooleanExtra("BRD_FINISH", false);
                }
                if (mAdapter != null) {
                    mAdapter.onFullscreenDisabled(isBroadcastFinished);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
                break;
        }
    }

    /**
     * 로그인 완료시 필요한 작업을 수행한다.
     *
     * @param event LoggedInEvent
     */
    @Override
    public void onEventMainThread(Events.LoggedInEvent event) {
        isLoggedInProcessing = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Timeout + 1초
                isLoggedInProcessing = false;
            }
        }, 6000);

        refresh();
    }

    /**
     * 웹뷰를 반환한다.
     *
     * @return ProductDetailWebView
     */
    public ProductDetailWebView getWebView() {
        return webView;
    }

    private void goToLinkSequentially() {
        Runnable runListUrl = new Runnable() {
            @Override
            public void run() {
                if (mListLink != null & mListLink.size() > 0) {
                    goToLink(mListLink.get(0));
                    mListLink.remove(0);
                    if (mListLink.size() > 0) {
                        new Handler().postDelayed(this, 500);
                    }
                }
            }
        };
        new Handler().post(runListUrl);
    }

    /**
     * 자바스크립트로 호출 하기 위해 deal 일 경우와 상품일 경우를 나누어서 값을 넣어준다.
     *
     * @param value
     * @return
     */
    private String getJavaScriptValue(String value) {
        // 자바 스크립트 호출이 변경됨.
        boolean prev = false;
        if (prev) {
            return "javascript:" + value;
        }
        return "javascript:" + mStrPrdDeal + value;
    }

    public void resize() {
        mResizeAppNativeDivHandler.post(mResizeAppNativeDivRunnable);
    }

    /**
     * 리스트로 받아온 텍스트뷰의 속성을 세팅한다.
     * 단일 단위가 아닌 List 로 들어온 텍스트들의 속성을 적용해서 append 하는 함수.
     *
     * @param tv          세팅할 텍스트뷰
     * @param txtInfoList API에서 전달받은 정보
     */
    public void setTextInfoList(TextView tv, List<NativeProductV2.TxtInfo> txtInfoList) {
        if (txtInfoList == null || txtInfoList.size() < 1) {
            tv.setVisibility(View.GONE);
            return;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        // txtList 별로 텍스트 속성을 정해주기 위해 순차적으로 0부터 어디까지 적용됐는지 저장하는 int
        int lastCharIndex = 0;
        for (NativeProductV2.TxtInfo textInfo : txtInfoList) {
            if (!TextUtils.isEmpty(textInfo.textValue)) {
                // android 에서 자동으로 라인이 넘어가는 현상을 무시하고 텍스트뷰의 끝까지 텍스트를 채우기 위함.
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < textInfo.textValue.length(); i++) {
                    sb.append(textInfo.textValue.charAt(i));
                    sb.append("\u200B");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                textInfo.textValue = sb.toString();
                ///////////////////////////////////////////////////////////////////////////////////////

                // font size span
                AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Integer.parseInt(textInfo.size), true);
                // font style span
                StyleSpan styleSpan = new StyleSpan("Y".equalsIgnoreCase(textInfo.boldYN) ? Typeface.BOLD : Typeface.NORMAL);
                // font color span
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(DisplayUtils.parseColor(textInfo.textColor));
                ssb.append(textInfo.textValue);
                ssb.setSpan(foregroundColorSpan, lastCharIndex, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(sizeSpan, lastCharIndex, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(styleSpan, lastCharIndex, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                lastCharIndex = lastCharIndex + textInfo.textValue.length();
                if (ssb.toString().length() > 0) {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(ssb);
                } else {
                    tv.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 링크주소에 따라 이동한다.
     *
     * @param linkUrl 링크주소
     */
    public void goToLink(String linkUrl) {
        goToLinkWithParam(linkUrl, null);
    }

    /**
     * 링크주소에 따라 이동한다.
     *
     * @param linkUrl 링크주소
     * @param params  치환할 리스트
     */
    public void goToLinkWithParam(final String linkUrl, List<String> params) {
        if (isEmpty(linkUrl)) {
            return;
        }

        webView.post(new Runnable() {
            @Override
            public void run() {
                String tempLinkUrl = linkUrl;
                if (linkUrl.startsWith("javascript")) {
                    if (isNotEmpty(params)) {
                        for (String param : params) {
                            tempLinkUrl = linkUrl.replaceFirst(SCRIPT_PARAM, param);
                        }
                    }
                    if (isNotEmpty(webView)) {
                        if (isLoadingComplete) {
                            webView.loadUrl(tempLinkUrl);
                        } else {
                            mListLink.add(tempLinkUrl);
                        }
                    }
                } else {
                    WebUtils.goWeb(context, tempLinkUrl);
                }
            }
        });


    }

    /**
     * 로그인 화면으로 이동한다.
     *
     * @param context 컨텍스트
     */
    public void goToLogin(Context context) {
        Intent intent = new Intent(Keys.ACTION.LOGIN);
        intent.putExtra(Keys.INTENT.FROM_NATIVE_PRODUCT, true);
        context.startActivity(intent);
    }

    /**
     * 로그인 상태인지 확인한다.
     *
     * @return 로그인상태면 true 리턴
     */
    public boolean isLoggedIn() {
        User user = User.getCachedUser();
        return isNotEmpty(user) && isNotEmpty(user.customerNumber);
    }

    /**
     * 딜 여부를 반환한다.
     *
     * @param url 딜 or 단품 주소
     * @return 딜이면 true 리턴
     */
    private boolean isDeal(String url) {
        return url.contains("/deal.gs?");
    }

    /**
     * 단품 여부를 반환한다.
     *
     * @param url 딜 or 단품 주소
     * @return 딜이면 true 리턴
     */
    private boolean isProduct(String url) {
        return url.contains("/prd.gs?");
    }

    protected void showMiniPlayButton(boolean isShow) {
        mImageGoToPlayer.setVisibility(isShow == true ? View.VISIBLE : View.GONE);
    }

    /**
     * 프리로드 이미지 URL을 생성한다.
     *
     * @param linkUrl 링크주소
     * @return 생성된 주소
     */
    private String makePreloadImageUrl(String linkUrl) {
        String preloadImageUrl = "";
        String firstDir;
        String secondDir;
        String thirdDir;
        String prdid = StringUtils.getUriParameter(linkUrl, PRDID_KEY);
        if (isNotEmpty(prdid)) {
            if (prdid.length() == 8 || prdid.length() == 11) {
                firstDir = prdid.substring(0, 2);
                secondDir = prdid.substring(2, 4);
                thirdDir = prdid.substring(4, 6);
                preloadImageUrl = String.format("%s/%s/%s/%s/%s%s", LAST_PRD_IMAGE, firstDir, secondDir, thirdDir, prdid, "_L1.jpg");
            } else {
                firstDir = prdid.substring(0, 2);
                secondDir = prdid.substring(2, 4);
                preloadImageUrl = String.format("%s/%s/%s/%s%s", LAST_PRD_IMAGE, firstDir, secondDir, prdid, "_L1.jpg");
            }
        }
        return preloadImageUrl;
    }

    /**
     * 로그인 완료 후 수신한 url로 웹뷰를 띄운다.
     *
     * @param event GoWebAfterLoginEvent
     */
    public void onEventMainThread(Events.EventProductDetail.GoWebAfterLoginEvent event) {
        if (isEmpty(event.url) || event.url.contains(NATIVE_BOTTOM_FILE_NAME)) {
            //신고하기 등 url값이 단품 하단 url인 경우는 새로운 웹액티비티를 띄우지 않음
            return;
        }
        goWeb(event.url);
    }

    /**
     * 액티비티가 다수 누적되어 있을 경우 최상위 액티비티만 goWeb을 수행하도록 한다.
     *
     * @param url 이동할 주소
     */
    private void goWeb(String url) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            if (!((Activity) context).hasWindowFocus()) {
                return;
            }
            WebUtils.geWebDelay(context, url, getIntent(), true, TabMenu.fromTabMenu(getIntent()));
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressBar.setVisibility(View.GONE);
        mResizeAppNativeDivHandler.removeCallbacks(mResizeAppNativeDivRunnable);
        mWebAppTouchInteractionHandler.removeCallbacks(mWebAppTouchInteractionRunnable);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 쉽게 가자자 딜구분은 내부에서 하자,
     *
     * @param action 액션명
     */
    public static void prdAmpSend(String action) {
        JSONObject eventProperties = new JSONObject();

        //Log.e("AAAAA", "ampIsDeal : " + ampIsDeal);
        //Log.e("AAAAA", "action : " + action);
        //Log.e("AAAAA", "ampProductCd : " + ampProductCd);
        //Log.e("AAAAA", "ampProductNm : " + ampProductNm);
        //Log.e("AAAAA", "ampProductType : " + ampProductType);

        if (ampIsDeal) {
            try {
                //{ action: prdCd: prdNm(타이틀): type (TV쇼핑/백화점): }
                eventProperties.put(AMPEnum.AMP_DEAL_ACTION_KEY, action);
                eventProperties.put(AMPEnum.AMP_DEAL_DEALNO_KEY, ampProductCd);
                eventProperties.put(AMPEnum.AMP_DEAL_DEALNM_KEY, ampProductNm);
                eventProperties.put(AMPEnum.AMP_DEAL_TYPE_KEY, ampProductType);

                AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_DEAL, eventProperties);
            } catch (Exception e) {

            }
        } else {
            try {
                //{ action: prdCd: prdNm(타이틀): type (TV쇼핑/백화점): }
                eventProperties.put(AMPEnum.AMP_PRD_ACTION_KEY, action);
                eventProperties.put(AMPEnum.AMP_PRD_PRDCD_KEY, ampProductCd);
                eventProperties.put(AMPEnum.AMP_PRD_PRDNM_KEY, ampProductNm);
                eventProperties.put(AMPEnum.AMP_PRD_TYPE_KEY, ampProductType);

                AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_PRD, eventProperties);
            } catch (Exception e) {

            }
        }
    }

    public boolean getIsDeal() {
        return mIsDeal;
    }

    public void sendWiseLogWithDelay(String url) {
        ThreadUtils.INSTANCE.runInUiThread(() -> setWiseLogHttpClient(url), 1000);
    }
}
