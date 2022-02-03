/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.URLUtil;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.inject.Inject;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.pattern.mvc.Model;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import activitytransition.ActivityTransition;
import activitytransition.ExitActivityTransition;
import activitytransition.core.TransitionData;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.CustomVerticalViewPager;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.brightcove.MinMediaPlayerControllerFragment;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.share.ShareFactory;
import gsshop.mobile.v2.support.share.ShareInfo;
import gsshop.mobile.v2.support.share.ShareInterface;
import gsshop.mobile.v2.support.sns.SnsV2DialogFragment;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.CustomToast;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.AndroidBridge;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static gsshop.mobile.v2.R.id.viewpager;
import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;


/**
 * 숏방 동영상 플레이 화면
 */
public class ShortbangEventActivity extends BaseTabMenuActivity
        implements SnsV2DialogFragment.OnSnsSelectedListener, OnMediaPlayerListener {

    private SnsV2DialogFragment snsDialog;

    private Context mContext;

    @Inject
    private RestClient restClient;

    //뷰페이저
    @InjectView(viewpager)
    private CustomVerticalViewPager viewPager;

    //해더영역
    @InjectView(R.id.lay_header)
    private RelativeLayout layHeader;

    //카테고리에서 동영상 선택시 깜박이는 현상 개선을 위한 뷰
    @InjectView(R.id.img_title)
    private ImageView img_title;

    //재생버튼 딤처리용 레이아웃
    @InjectView(R.id.lay_play)
    private LinearLayout layPlay;

    //뮤트영역
    @InjectView(R.id.lay_mute)
    private LinearLayout layMute;

    //뮤트버튼
    @InjectView(R.id.chk_mute)
    private CheckBox chkMute;

    //바로구매
    @InjectView(R.id.lay_direct_order)
    private RelativeLayout layDirectOrder;

    //가격
    @InjectView(R.id.txt_price)
    private TextView txtPrice;

    //가격단위
    @InjectView(R.id.txt_price_unit)
    private TextView txtPriceUnit;

    //바로구매
    @InjectView(R.id.txt_direct_order)
    private TextView txtDirectOrder;

    //액티비티 첫 실행시 보이는 이미지
    @InjectView(R.id.preload_image)
    private ImageView preload_image;

    //액티비티 첫 실행시 보이는 로딩바
    @InjectView(R.id.mc_webview_progress_bar)
    private ProgressBar loadingProgress;

    //카테고리에서 동영상 선택시 깜박이는 현상 개선을 위한 뷰
    @InjectView(R.id.fake_image)
    private ImageView fake_image;

    //토스트 형태의 가이드 이미지
    @InjectView(R.id.toast_image)
    private ImageView toast_image;

    //SMS공유시 제목앞에 붙는 prefix
    private String PROMOTION_PREFIX = "";

    //링크주소에서 제거할 부분 정의
    private static final String EXTERNAL_PREFIX = "external://shortbang?";

    private ExitActivityTransition exitTransition;

    private List<OnMediaPlayerController> players = new ArrayList<>();

    private ArrayList<SectionContentList> productList = null;

    //외부에서 호출되(SNS, 공유 등)었는지 여부
    private boolean isExternalCall = false;

    //비디오 링크 정보
    VideoLinkInfo videoLinkInfo;
    //카테고리 번호
    private String categoryNum;
    //비디오 번호
    private String videoNum;

    //데이타 요금 동의
    private boolean isNetworkApproved = false;
    //데이타요금 팝업 노출여부 판단에 사용할 변수
    private int vpSelectedCount = 0;

    private int productListCount = -1;

    private GestureDetector gestureScanner;

    //가이드화면 노출여부 플래그
    private boolean isGuideShown = true;
    private boolean isToastGuideShown = false;

    private boolean isBackPress;

    //동영상 버퍼링중 바로구매, SNS공유 등 클릭시 동영상이 재생되지 않도록 하기 위한 플래그
    private boolean isSkipPlay = false;

    //카테고리 화면에서 선택한 카테고리 정보
    private String categoryNumFromCate = "";

    //숏방 에러타입 정의
    public enum SHORTBANG_ERROR_TYPE {
        INVALID_VIDEO,    //유효하지 않은 숏방동영상
        INVALID_OS_VERSION  //동영상 재생을 위한 최소 OS버전 미만인 경우
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MinMediaPlayerControllerFragment.isMute = true;

        overridePendingTransition(R.anim.zoom_enter, 0);

        setContentView(R.layout.shortbang_event);

        // 클릭리스너 등록
        setClickListener();

        this.mContext = this;
        //PROMOTION_PREFIX = getResources().getString(R.string.shortbang_promotion_prefix) + " ";
        PROMOTION_PREFIX = "";

        //롤리팝 이상인 경우 상태바 투명 및 타이틀영역 마진 세팅
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //상태바 높이만큼 마진 세팅
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layHeader.getLayoutParams();
            lp.setMargins(0, DisplayUtils.getStatusBarHeight(this), 0, 0);
            layHeader.setLayoutParams(lp);
        }

        //장면전환 애니메이션 동작을 위해 추가
        TransitionData transitionData = new TransitionData(context, getIntent().getExtras());
        if (transitionData.imageFilePath != null) {
            preload_image.setImageBitmap(BitmapFactory.decodeFile(transitionData.imageFilePath));
            exitTransition = ActivityTransition.with(getIntent()).to(preload_image, "image").start(savedInstanceState);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                showLoadingProgress(true);
                setMuteListener();
                setupShortbang();
            }, 800);
        }else{
            ViewUtils.hideViews(preload_image);
            setMuteListener();
            setupShortbang();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        finish();
        startActivity(intent);
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.img_logo).setOnClickListener((View v) -> {
                    onClickGsLogo();
                }
        );
        findViewById(R.id.img_play).setOnClickListener((View v) -> {
                    onClickPlay();
                }
        );
        findViewById(R.id.lay_direct_order).setOnClickListener((View v) -> {
                    onClickDirectOrd();
                }
        );
        findViewById(R.id.img_sns).setOnClickListener((View v) -> {
                    onClickSns();
                }
        );
    }

    /**
     * 숏방 API 호출
     */
    private void setupShortbang() {

        if (getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false)) {
            isExternalCall = true;
        }

        //동영상 플레이어 최소 지원버전 체크
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            alertInvalidMsg(SHORTBANG_ERROR_TYPE.INVALID_OS_VERSION, isExternalCall, false, false);
            return;
        }

        String url = getIntent().getStringExtra(Keys.INTENT.SHORTBANG_EVENT_LINK);
        videoLinkInfo = parseVideoLink(url);
        if (videoLinkInfo == null) {
            alertInvalidMsg(SHORTBANG_ERROR_TYPE.INVALID_VIDEO, isExternalCall, false, false);
            return;
        }

        if (!videoLinkInfo.isValidVideo()) {
            alertInvalidMsg(SHORTBANG_ERROR_TYPE.INVALID_VIDEO, isExternalCall, false, true);
            return;
        }

        categoryNum = videoLinkInfo.categoryNum;
        videoNum = videoLinkInfo.videoNum;

        String targetApi = videoLinkInfo.targetApi;
        new ShortbangMediaController(this).execute(targetApi);
    }

    /**
     * 뷰페이저 setup
     *
     * @param result 숏방 API 호출 결과 데이타
     * @param fromCategory 카테고리메뉴에서 진입했는지 여부
     */
    public void setUpVideoViewPager(List<SectionContentList> result, boolean fromCategory) {

        //상단중앙 로고이미지
        ImageUtil.loadImageBadge(context, result.get(0).imageUrl, img_title, 0, HD);

        int selectedVideoIdx = -1;
        players.clear();

        for (SectionContentList category : result) {
            //카테고리 정보 없는 경우 첫번째 카테고리 세팅
            if (TextUtils.isEmpty(categoryNum)) {
                categoryNum = category.sbCateGb;
            }
            //일치하는 카테고리를 찾아서
            if (categoryNum.equals(category.sbCateGb)) {
                if (category.subProductList != null) {
                    productList = (ArrayList<SectionContentList>) category.subProductList.clone();
                }

                //상품을 담는다.
                int i = 0;
                if (productList != null) {
                    for (SectionContentList product : productList) {
                        //동영상 정보 없는 경우 첫번째 동영상 세팅
                        if (TextUtils.isEmpty(videoNum)) {
                            videoNum = product.sbVideoNum;
                        }
                        if (videoNum.equals(product.sbVideoNum)) {
                            selectedVideoIdx = i;
                            //섬네일 이미지 깜박임 방지
                            showFakeImage(product.imageUrl);
                        }
                        players.add(MinMediaPlayerControllerFragment.newInstance(product.videoid, product.dealMcVideoUrlAddr, product.imageUrl, Integer.toString(i++)));
                    }
                }

                //목록중에 해당 숏방이 존재하지 않는 경우
                if (selectedVideoIdx == -1) {
                    alertInvalidMsg(SHORTBANG_ERROR_TYPE.INVALID_VIDEO, isExternalCall, fromCategory, true);
                    return;
                }

                //공유나 배너등을 통해 들어온 경우 카테고리 메뉴 클릭시 전달받은 카테고리 정보를 디폴트로 선택
                categoryNumFromCate = categoryNum;

                /**
                 * 리스트 갯수가 8개 미만이면 dummy data 넣기.
                 */
                productListCount = productList.size();

                while (productList.size() <= 8) {
                    for (int j = 0; j < productListCount; j++) {
                        SectionContentList product = productList.get(j);
                        productList.add(product);
                        players.add(MinMediaPlayerControllerFragment.newInstance(product.videoid, product.dealMcVideoUrlAddr, product.imageUrl, "" + Integer.toString(i++)));
                    }
                }

                break;
            }
        }

        //목록중에 해당 숏방이 존재하지 않는 경우
        if (selectedVideoIdx == -1) {
            alertInvalidMsg(SHORTBANG_ERROR_TYPE.INVALID_VIDEO, isExternalCall, fromCategory, true);
            return;
        }

        for (OnMediaPlayerController player : players) {
            player.setOnMediaPlayerListener(this);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                vpSelectedCount++;
                Ln.i("onPageSelected > vpSelectedCount : " + vpSelectedCount);

                //뷰페이저에 더미포지션 세팅중인 경우는 아래 작업수행을 스킵함
                if (vpSelectedCount <= 1) {
                    return;
                }

                //가이드화면 노출여부(isGuideShown) true인 경우는 "더이상 보지않기" 클릭한 경우 (닫기를 클릭한 경우는 false)
                //가이드화면 노출전인 경우는 가이드화면 노출 후 사용자가 화면을 닫는 시점에 네트웍체크 수행
                //토스트가이드가 기 노출된 경우, 가이드화면 노출여부와 관계없이 네트웍체크 및 재생 수행
                if (isGuideShown || isToastGuideShown) {
                    checkNetworkAndPlay(position, false, true);
                }

                //선택된 상품정보 세팅
                setProductInfo();

                //첫숏방, 마지막숏방 토스트 노출
                //showToast();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        PagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return (Fragment) players.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                // ignore exception
                try {
                    super.destroyItem(container, position, object);
                } catch (IndexOutOfBoundsException e) {
                    Ln.e(e);
                } catch (IllegalStateException e) {
                    Ln.e(e);
                }
            }

            @Override
            public int getCount() {
                return players.size();
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        };

        InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);

        if (productListCount < 2) {
            viewPager.enableSwipe = false;
        } else {
            viewPager.enableSwipe = true;
        }

        //동영상 일시정지를 위한 리스너 등록
        viewPager.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!viewPager.enableSwipe) {
                    //동영상이 1개인 경우 플리킹 방지
                    gestureScanner.onTouchEvent(event);
                    return true;
                } else {
                    return gestureScanner.onTouchEvent(event);
                }
            }
        });

        gestureScanner = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                togglePlayAndPause();
                return false;
            }
        });

        setViewpagerItem(wrappedAdapter, selectedVideoIdx);
    }

    /**
     * 뷰페이저 포지션을 세팅한다.
     * (딜레이를 주는 이유는 더미포지션과 실제포지션을 모두 세팅하면서
     * 섬네일 이미지가 순간 교체되는 현상이 발생하여 이 깜박임 현상을 제거하기 위함)
     *
     * @param wrappedAdapter 어뎁터
     * @param selectedVideoIdx 이동할 포지션
     */
    private void setViewpagerItem(InfinitePagerAdapter wrappedAdapter, int selectedVideoIdx) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            viewPager.setAdapter(wrappedAdapter);

            //최초 노출할 비디오 세팅
            int dummyIdx = (selectedVideoIdx + 3) % players.size();
            //dummyIdx값이 0인 경우 onPageSelected 콜백이 수행되지 않아 vpSelectedCount증가 예외처리
            if (dummyIdx == 0) {
                vpSelectedCount++;
            }

            viewPager.setCurrentItem(dummyIdx);
            Ln.i("viewPager.setCurrentItem(dummyIdx) : " + dummyIdx);
            setCurrentItem(selectedVideoIdx);
            Ln.i("setCurrentItem(selectedVideoIdx) : " + selectedVideoIdx);

            //섬네일 이미지 깜박임 방지
            hideFakeImage();
        }, 1000);
    }

    /**
     * 변수를 초기화한다.
     */
    private void initParams() {
        categoryNum = "";
        videoNum = "";
        vpSelectedCount = 0;
        categoryNumFromCate = "";
    }

    /**
     * 선택된 동영상의 상품정보를 세팅한다.
     */
    private void setProductInfo() {
        SectionContentList product = productList.get(getCurrentPosition());

        categoryNum = product.sbCateGb;
        videoNum = product.sbVideoNum;

        //판매가격 세팅
        /*if (!TextUtils.isEmpty(product.salePrice)) {
            txtPrice.setText(product.salePrice);
            txtPriceUnit.setText(getResources().getString(R.string.won));
        }
        txtDirectOrder.setText(getResources().getString(R.string.home_tv_live_btn_tv_pay_text));*/

        //바로구매 버튼 노출/숨김 세팅
        setDirectOrderLayout(TextUtils.isEmpty(product.linkUrl));

        //와이즈로그 전송
        //setWiseLogRest(product.wiseLog2);
    }

    public void setCurrentItem(int item) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            boolean smoothScroll = true;
            int currentItem = item;
            if (viewPager.getAdapter().getCount() == 0) {
                viewPager.setCurrentItem(currentItem, smoothScroll);
                return;
            }
            currentItem = getOffsetAmount() + (currentItem % viewPager.getAdapter().getCount());
            viewPager.setCurrentItem(currentItem, smoothScroll);
        }, 200);
    }

    private int getOffsetAmount() {
        if (viewPager.getAdapter().getCount() == 0) {
            return 0;
        }
        if (viewPager.getAdapter() instanceof InfinitePagerAdapter) {
            InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) viewPager.getAdapter();
            // allow for 100 back cycles from the beginning
            // should be enough to create an illusion of infinity
            // warning: scrolling to very high values (1,000,000+) results in
            // strange drawing behaviour
            return infAdapter.getRealCount() * 100;
        } else {
            return 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSkipPlay = false;
        if (vpSelectedCount > 1) {
            //SNS, 카테고리가 노출된 상태에서 백그라운드->포그라운드 전환시 동영상 자동재생 안함
            if ((snsDialog == null || !snsDialog.isVisible())) {
                //숏방 동영상 자동재생
                performAutoPlay();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewPager.getAdapter() != null) {
            pauseVideo(getCurrentPosition());
        }
    }

    /**
     * 동영상을 재생시킨다.
     *
     * @param position 동영상위치
     */
    private void playVideo(int position) {
        int realCount = ((InfinitePagerAdapter) viewPager.getAdapter()).getRealCount();
        int currentItem = viewPager.getCurrentItem() % realCount;
        int newPosition = position % realCount;

        if(newPosition == currentItem) {
            OnMediaPlayerController player = players.get(newPosition);
            if(!player.isPlaying() && !isSkipPlay) {
                player.playPlayer();
            }
        }
    }

    private void playVideoWithDelay(int position) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            if (viewPager != null && position == viewPager.getCurrentItem()) {
                playVideo(position);
            }
        }, 1700);
    }

    /**
     * 동영상을 일시정지시킨다.
     *
     * @param position 동영상위치
     */
    private void pauseVideo(int position) {
        players.get(position).stopPlayer();
        if(!isBackPress) {
            layPlay.setVisibility(View.VISIBLE);
        }
    }


    private void pauseVideoWithDelay(int position) {
        ThreadUtils.INSTANCE.runInUiThread(() -> pauseVideo(position), 200);
    }

    /**
     * 뮤트 리스너를 등록한다.
     */
    private void setMuteListener() {
        //뮤트 클릭영역 확장을 위해
        layMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkMute.performClick();
            }
        });
        // mute
        chkMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                players.get(getCurrentPosition()).setMute(!isChecked);
                //와이즈로그 전송
                if (isChecked) {
                    setWiseLogRest(ServerUrls.WEB.SHORTBANG_SOUND_ON);
                } else {
                    setWiseLogRest(ServerUrls.WEB.SHORTBANG_SOUND_OFF);
                }
            }
        });
    }

    /**
     * 동영상 재생시 네트웍을 먼저 체크한 후 재생한다.
     * 뷰페이저 onPageSelected 호출
     *
     * @param position 뷰페이저 포지션
     * @param isPlayButton 재생버튼 클릭여부
     * @param isDelay 동영상재생시 딜레이부여 여부
     */
    private void checkNetworkAndPlay(int position, boolean isPlayButton, boolean isDelay) {
        //LTE, WIFI 체크
        if (!NetworkStatus.isWifiConnected(mContext) && !isNetworkApproved) {
            layPlay.setVisibility(View.VISIBLE);
            if (vpSelectedCount <= 2 || isPlayButton) {
                //데이타팝업 노출 케이스
                //1.액티비티 최초 로딩시 동영상 자동플레이 직전 (플리킹때는 노출 안함)
                //2.재생버튼을 클릭한 경우
                NetworkUtils.confirmNetworkBillingAndShowPopup(ShortbangEventActivity.this, new NetworkUtils.OnConfirmNetworkListener() {
                    @Override
                    public void isConfirmed(boolean isConfirmed) {
                        if(isConfirmed) {
                            layPlay.setVisibility(View.INVISIBLE);
                            playVideo(position);
                            isNetworkApproved = true;
                        }
                        else {
                            showLoadingProgress(false);
                            showToastGuide();
                        }
                    }

                    @Override
                    public void inCanceled() {
                        showLoadingProgress(false);
                        showToastGuide();
                    }
                });
            }
            ViewUtils.hideViews(preload_image);
        } else {
            layPlay.setVisibility(View.INVISIBLE);
            if (isDelay) {
                playVideoWithDelay(position);
            } else {
                playVideo(position);
            }
        }
    }

    /**
     * resume시 동영상을 자동 재생한다.
     *
     */
    private void performAutoPlay() {
        if (!NetworkStatus.isWifiConnected(mContext) && !isNetworkApproved) {
            layPlay.setVisibility(View.VISIBLE);
        } else {
            layPlay.setVisibility(View.INVISIBLE);
            playVideo(getCurrentPosition());
        }
    }

    /**
     * 재생버튼 레이아웃를 노출/숨김 처리와 함께 재생/일시정지를 수행한다.
     * 뷰페이저 화면 클릭시 호출됨
     */
    private void togglePlayAndPause() {
        int currentItem = getCurrentPosition();
        if (players.get(currentItem).isPlaying()) {
            //일시정지
            pauseVideo(currentItem);
        } else {
            if (layPlay.isShown()) {
                //재생
                checkNetworkAndPlay(getCurrentPosition(), true, false);
            }
        }
    }

    /**
     * 좌상단 이전버튼 클릭 이벤트
     */
    private void onClickGsLogo() {
        super.onBackPressed();

        //와이즈로그 전송
        //setWiseLogRest(ServerUrls.WEB.SHORTBANG_HOME);
    }

    /**
     * 중앙 재생버튼 클릭 이벤트
     */
    private void onClickPlay() {
        //재생
        checkNetworkAndPlay(getCurrentPosition(), true, false);
    }

    /**
     * 바로구매 클릭 이벤트
     */
    private void onClickDirectOrd() {
        //뷰페이저에 더미포지션 세팅중인 경우는 바로구매 수행 안함
        //바로구매 수행시 더미포지션의 바로구매가 수행됨
        if (vpSelectedCount < 2) {
            return;
        }

        //1초 이내의 클릭액션 무시
        if (ClickUtils.work(1000)) {
            return;
        }

        String directOrdUrl = productList.get(getCurrentPosition()).linkUrl;
        if (URLUtil.isValidUrl(directOrdUrl)) {
            //상품 상세페이지로 이동
            WebUtils.goWeb(context, directOrdUrl);

            isSkipPlay = true;

            //일시정지
            pauseVideoWithDelay(getCurrentPosition());
        }
    }

    /**
     * 공유하기 클릭 이벤트
     */
    private void onClickSns() {
        isSkipPlay = true;

        //SNS 팝업 띄움
        snsDialog = new SnsV2DialogFragment();
        snsDialog.show(getSupportFragmentManager(), ShortbangEventActivity.class.getSimpleName());

        //일시정지
        pauseVideoWithDelay(getCurrentPosition());

        //SNS 버튼 클릭시 와이즈로그 전송
        //setWiseLogRest(ServerUrls.WEB.SHORTBANG_SNS);
    }

    /**
     * 동영상 타입별로 UI를 저정한다.
     *
     * @param isOnlyVideo true:상품없는 동영상, false:상품있는 동영상
     */
    private void setDirectOrderLayout(boolean isOnlyVideo) {
        if (isOnlyVideo) {
            //상품 없는 동영상인 경우
            changeMutePosition(mContext, 4);
            layDirectOrder.setVisibility(View.INVISIBLE);
        } else {
            //상품 있는 동영상인 경우
            changeMutePosition(mContext, 50);
            layDirectOrder.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 뮤트버튼 포지션을 이동한다.
     *
     * @param context 컨텍스트
     * @param dp 하단마진값(단위는 dp)
     */
    private void changeMutePosition(Context context, int dp) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layMute.getLayoutParams();
        params.bottomMargin = DisplayUtils.convertDpToPx(context, dp);
        layMute.setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        if (getDrawerLayout() != null && getDrawerLayout().isDrawerOpen(getNavigationView())) {
            closeNavigationView();
            return;
        }
        isBackPress = true;
        layPlay.setVisibility(View.INVISIBLE);
        if(exitTransition != null){
            ViewUtils.showViews(preload_image);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                ViewUtils.hideViews(viewPager);
            }
            Handler handler = new Handler();
            handler.postDelayed(() -> exitTransition.exit(ShortbangEventActivity.this), 500);
        }else{
            //게이트웨이에서 공유시 backtomain=Y 플래그 세팅해주면 아래코드 필요없음
            if (!MainApplication.isHomeCommandExecuted) {
                //getIntent().putExtra(Keys.INTENT.BACK_TO_MAIN, true);
            }
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.zoom_exit);
    }

    /**
     * SNS팝업에서 SNS를 선택했을때 호출되는 콜백
     *
     * @param shareType SNS 종류
     */
    @Override
    public void onSnsSelected(SnsV2DialogFragment.SHARE_TYPE shareType) {
        isSkipPlay = false;
        switch(shareType) {
            case SMS:
                onSnsSelectedSms(shareType);
                break;
            case Url:
                onSnsSelectedUrl(shareType);
                //재생
                checkNetworkAndPlay(getCurrentPosition(), true, false);
                break;
            case KakaoTalk:
            case KakaoStory:
            case Line:
            case Facebook:
            case Twitter:
            case Pinterest:
            case Etc:
                onSnsSelectedOthers(shareType);
                break;
            default:
                break;
        }

        //SNS 클릭시 와이즈로그 전송
        //setWiseLogRest(shareType.getWiseLogUrl());
    }

    /**
     * SNS공유 중 SMS 처리함수
     *
     * @param shareType 공유타입
     */
    private void onSnsSelectedSms(SnsV2DialogFragment.SHARE_TYPE shareType) {
        try {
            SectionContentList product = productList.get(getCurrentPosition());
            String promotionName = product.promotionName;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("sms_body", PROMOTION_PREFIX + (promotionName == null ? "" : promotionName) + "\n" + makeGatewayUrl(product.snsLinkUrl, shareType.getUtmParam()));
            startActivity(intent);
        } catch(Exception e) {
            Toast.makeText(mContext, R.string.sms_not_support, Toast.LENGTH_SHORT).show();
            Ln.e(e);
        }
    }

    /**
     * SNS공유 중 URL복사 처리함수
     *
     * @param shareType 공유타입
     */
    private void onSnsSelectedUrl(SnsV2DialogFragment.SHARE_TYPE shareType) {
        try {
            SectionContentList product = productList.get(getCurrentPosition());
            new AndroidBridge(mContext).sendClipData(makeGatewayUrl(product.snsLinkUrl, shareType.getUtmParam()));
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * SNS공유 중 SMS, URL복사 외 나머지 처리함수
     *
     * @param shareType 공유타입
     */
    private void onSnsSelectedOthers(SnsV2DialogFragment.SHARE_TYPE shareType) {
        SectionContentList product = productList.get(getCurrentPosition());
        String url = makeShareUrl(shareType.toString().toLowerCase(), product.snsImageUrl, product.snsLinkUrl, product.imageUrl,
                PROMOTION_PREFIX + (product.promotionName == null ? "" : product.promotionName), shareType.getUtmParam());
        Ln.i("sns url : " + url);

        if (!TextUtils.isEmpty(url)) {

            Uri data = Uri.parse(url);
            ShareInfo shareInfo = new ShareInfo();
            shareInfo.setTarget(data.getQueryParameter("target"));
            shareInfo.setLink(data.getQueryParameter("link"));
            shareInfo.setSubject(data.getQueryParameter("subject"));
            shareInfo.setMessage(data.getQueryParameter("message"));
            shareInfo.setImageurl(data.getQueryParameter("imageurl"));
            if (ShareInfo.ShareImageType.TYPE_SB_A.toString().equalsIgnoreCase(data.getQueryParameter("imagetype"))) {
                shareInfo.setShareImageType(ShareInfo.ShareImageType.TYPE_SB_A);
            } else if (ShareInfo.ShareImageType.TYPE_SB_B.toString().equalsIgnoreCase(data.getQueryParameter("imagetype"))) {
                shareInfo.setShareImageType(ShareInfo.ShareImageType.TYPE_SB_B);
            } else {
                shareInfo.setShareImageType(ShareInfo.ShareImageType.TYPE_DEFAULT);
            }

            ShareInterface shareInterface = ShareFactory.getShareProvider(shareInfo);
            if(shareInterface != null) {
                shareInterface.share(this);
            }
        }
    }

    /**
     * 공유용 URI를 생성한다.
     *
     * @param target SNS 종류
     * @param snsImageurl 공유용 이미지주소
     * @param snsLinkUrl 공유용 랜딩주소
     * @param imageurl 이미지주소
     * @param message 메시지
     * @param utmParam UTM 파라미터
     */
    private String makeShareUrl(String target, String snsImageurl, String snsLinkUrl, String imageurl, String message, String utmParam) {
        Uri.Builder builder =  Uri.parse(ServerUrls.APP.SHARE).buildUpon();
        builder.appendQueryParameter("target", target);
        //페북의 경우 subject 값이 없으면 link의 title 정보를 가져와 자동으로 보여줌(message는 안보여줌)
        builder.appendQueryParameter("subject", "");
        builder.appendQueryParameter("message", message);
        if (!TextUtils.isEmpty(snsImageurl)) {
            builder.appendQueryParameter("imageurl", snsImageurl);
            builder.appendQueryParameter("imagetype", ShareInfo.ShareImageType.TYPE_SB_A.toString());
        } else {
            builder.appendQueryParameter("imageurl", imageurl);
            builder.appendQueryParameter("imagetype", ShareInfo.ShareImageType.TYPE_SB_B.toString());
        }
        builder.appendQueryParameter("link", makeGatewayUrl(snsLinkUrl, utmParam));
        return builder.build().toString();
    }

    /**
     * 게이트웨이 URL 스트링을 생성한다.
     *
     * @param snsLinkUrl 공유용 랜딩주소
     * @param utmParam UTM 파라미터
     */
    private String makeGatewayUrl(String snsLinkUrl, String utmParam) {
        Uri.Builder builder =  Uri.parse(snsLinkUrl).buildUpon();
        builder.appendQueryParameter("selectedcate", categoryNum);
        builder.appendQueryParameter("selectedvideo", videoNum);
        //매장(이벤트, 오늘추천) 배너를 통해 진입한 경우는 백키 클릭시 숏방만 종료처리 하기 위한 플래그
        //builder.appendQueryParameter("backtomain", "Y");
        return builder.build().toString() + utmParam;
    }

    /**
     * SNS팝업에서 닫기버튼을 선택했을때 호출되는 콜백
     */
    @Override
    public void onCloseSelected() {
        isSkipPlay = false;
        //재생
        checkNetworkAndPlay(getCurrentPosition(), true, false);
    }


    /**
     * OnMediaPlayerListener Methods
     */
    /**
     * 동영상 재생완료 콜백
     */
    @Override
    public void onFinished(MediaInfo media) {
        //재생완료 시 다음 동영상 자동플레이
        if (viewPager != null && !layPlay.isShown()) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTap(boolean show) {

    }

    @Override
    public void onFullScreenClick(MediaInfo media) {

    }

    @Override
    public void onPlayed() {
        showLoadingProgress(false);
        ViewUtils.hideViews(preload_image);
        if (vpSelectedCount == 2) {
            showToastGuide();
        }
    }

    @Override
    public void onPaused() {

    }

    /**
     * 뷰페이저의 현재 포지션을 반환한다.
     *
     * @return 뷰페이저의 현재 포지션
     */
    private int getCurrentPosition() {
        return viewPager.getCurrentItem() % getRealCount();
    }

    /**
     * 뷰페이저의 실제 아이템 갯수를 반환한다.
     *
     * @return 뷰페이저의 아이템 갯수
     */
    private int getRealCount() {
        return ((InfinitePagerAdapter) viewPager.getAdapter()).getRealCount();
    }

    /**
     * 첫영상, 마지막 영상의 경우 토스트메시지를 노출한다.
     */
    private void showToast() {
        //동영상 갯수가 1개인 경우 토스트 노출 안함
        if (productListCount == 1) {
            return;
        }
        int currentPosition = getCurrentPosition();
        if (currentPosition == 0 || currentPosition % productListCount == 0) {
            //첫 숏방영상
            CustomToast toast = new CustomToast(mContext);
            toast.showToast(getResources().getString(R.string.shortbang_first_toast), Toast.LENGTH_SHORT);
        } else if (currentPosition % productListCount == productListCount - 1) {
            //마지막 숏방영상
            CustomToast toast = new CustomToast(mContext);
            toast.showToast(getResources().getString(R.string.shortbang_last_toast), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 숏방이 유효하지 않은 경우 또는 안드로이드 OS버전이 낮은 경우
     * case1) 숏방매장에서 숏방상세 접근 시, alert 처리 후 백키
     * case2) 공유 또는 외부 링크로 숏방상세 접근 시, alert 처리 후 숏방매장으로 이동
     *
     * @param errType 에러타입
     * @param fromExternal  외부에서 호출되었는지 여부
     * @param fromCategory 카테고리메뉴에서 진입했는지 여부
     * @param showDefaultVideo 첫번째 카테고리의 첫번째 동영상을 보여줄지 여부
     */
    private void alertInvalidMsg(SHORTBANG_ERROR_TYPE errType, final boolean fromExternal, final boolean fromCategory, final boolean showDefaultVideo) {
        int msgResourceId;
        showLoadingProgress(false);

        switch (errType) {
            case INVALID_VIDEO:
                msgResourceId = fromExternal ? R.string.shortbang_invalid_video_from_external : R.string.shortbang_invalid_video_from_internal;
                break;
            case INVALID_OS_VERSION:
                msgResourceId = R.string.shortbang_invalid_os_version;
                break;
            default:
                msgResourceId = R.string.shortbang_invalid_video_from_internal;
                break;
        }

        new CustomOneButtonDialog(ShortbangEventActivity.this).message(msgResourceId)
                .cancelable(false)
                .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        if (showDefaultVideo) {
                            initParams();
                            new ShortbangMediaController(ShortbangEventActivity.this).execute(videoLinkInfo.targetApi);
                        } else {
                            //카테고리메뉴에서 동영상 선택하여 들어온 경우, 에러메시지 띄우고 본 화면이 닫히면 안됨
                            if (!fromCategory) {
                                onBackPressed();
                            }
                        }
                    }
                }).show();
    }

    /**
     * 숏방 API 호출
     */
    private class ShortbangMediaController extends BaseAsyncController<ShortbangResult> {
        private String url;
        private final Context mContext;

        public ShortbangMediaController(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            //super.onPrepare(params);
            url = (String) params[0];
        }

        @Override
        protected ShortbangResult process() throws Exception {
            return (ShortbangResult) DataUtil.getData(context, restClient, ShortbangResult.class,
                    true, false, url);
        }

        @Override
        protected void onSuccess(final ShortbangResult result) throws Exception {
            if (result != null && result.shortBangProductList != null && !result.shortBangProductList.isEmpty()) {
                setUpVideoViewPager(result.shortBangProductList, false);
            } else {
                alertInvalidMsg(SHORTBANG_ERROR_TYPE.INVALID_VIDEO, isExternalCall, false, false);
            }
        }

        @Override
        protected void onError(Throwable e) {
            Ln.e(e);
            alertInvalidMsg(SHORTBANG_ERROR_TYPE.INVALID_VIDEO, isExternalCall, false, false);
        }
    }

    /**
     * 숏방 모델
     */
    @Model
    private static class ShortbangResult {
        public ArrayList<SectionContentList> shortBangProductList;
    }

    /**
     * 비디오정보 모델
     */
    @Model
    private static class VideoLinkInfo {
        public String categoryNum;
        public String videoNum;
        public String targetApi;

        public boolean isValidVideo() {
            return !TextUtils.isEmpty(this.categoryNum) && !TextUtils.isEmpty(this.videoNum);
        }
    }

    private void showLoadingProgress(boolean show) {
        if (show) {
            ViewUtils.showViews(loadingProgress);
        } else {
            ViewUtils.hideViews(loadingProgress);

        }
    }

    /**
     * 링크주소에서 비디오정보를 추출한다.
     *
     * @param linkUrl 링크주소
     * @return 비디오정보
     */
    private VideoLinkInfo parseVideoLink(String linkUrl) {
        if (TextUtils.isEmpty(linkUrl)) {
            return null;
        }

        Uri uri = Uri.parse(linkUrl.replace(EXTERNAL_PREFIX, ""));
        if (uri == null) {
            return null;
        }

        VideoLinkInfo videoLinkInfo  = new VideoLinkInfo();
        videoLinkInfo.categoryNum = uri.getQueryParameter("selectedcate");
        videoLinkInfo.videoNum = uri.getQueryParameter("selectedvideo");
        videoLinkInfo.targetApi = uri.getQueryParameter("targetApi");

        return videoLinkInfo;
    }

    /**
     * 토스트 형태의 가이드 이미지를 노출한다.
     */
    private void showToastGuide() {
        if (isToastGuideShown) {
            return;
        }
        isToastGuideShown = true;

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setStartOffset(0);
        fadeIn.setDuration(500);

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(2000);
        fadeOut.setDuration(500);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                toast_image.startAnimation(fadeOut);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                toast_image.setVisibility(View.VISIBLE);
            }
        });
        toast_image.startAnimation(fadeIn);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                toast_image.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
    }

    /**
     * 카테고리에서 동영상 선택시 깜박임 현상 방지를 위해 페이크 이미지를 노출한다.
     *
     * @param thumbnail 섬네일 이미지 주소
     */
    private void showFakeImage(String thumbnail) {
        ImageUtil.loadImageTvLive(mContext, thumbnail, fake_image, android.R.color.transparent);
        fake_image.setVisibility(View.VISIBLE);
    }

    /**
     * 페이크 이미지를 숨김처리한다.
     */
    private void hideFakeImage() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            fake_image.setImageResource(android.R.color.transparent);
            fake_image.setVisibility(View.GONE);
        }, 500);
    }

    /**
     * 로그인 완료 후 수신한 url로 웹뷰를 띄운다.
     *
     * @param event Events.DirectOrderAfterLoginEvent
     */
    public void onEventMainThread(Events.DirectOrderAfterLoginEvent event) {
        WebUtils.goWeb(this, event.url, getIntent(), true, TabMenu.fromTabMenu(getIntent()));
    }
}
