package gsshop.mobile.v2.support.media.exoplayer;



import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.gsshop.mocha.core.activity.BaseFragment;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.MLAlarm;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.MobileLiveAlarmCancelDialogFragment;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.MobileLiveAlarmDialogFragment;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.MobileLivePrdDialogFragment;
import gsshop.mobile.v2.mobilelive.ChatAdapter;
import gsshop.mobile.v2.mobilelive.MobileLiveData;
import gsshop.mobile.v2.mobilelive.MobileLivePrdsInfoList;
import gsshop.mobile.v2.mobilelive.MobileLiveResult;
import gsshop.mobile.v2.mobilelive.MobileLiveTimeLayout;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.share.ShareFactory;
import gsshop.mobile.v2.support.share.ShareInfo;
import gsshop.mobile.v2.support.share.ShareInterface;
import gsshop.mobile.v2.support.sns.SnsV2DialogFragment;
import gsshop.mobile.v2.support.tv.MobileLiveChatPlayActivity;
import gsshop.mobile.v2.support.tv.OnCspChatListener;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.support.ui.IndicatorRecyclerView;
import gsshop.mobile.v2.support.ui.PreviewRecyclerViewPager;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.KeyboardHeightObserver;
import gsshop.mobile.v2.util.KeyboardHeightProvider;
import gsshop.mobile.v2.util.MaskingUtil;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.RestClientUtils;
import gsshop.mobile.v2.util.StaticChatCsp;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.util.TimeRemaining;
import gsshop.mobile.v2.web.AndroidBridge;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.Keys.ACTION.DIRECT_ORDER_WEB;
import static gsshop.mobile.v2.ServerUrls.WEB.MOBILE_LIVE_CHAT_CLICK;
import static gsshop.mobile.v2.ServerUrls.WEB.MOBILE_LIVE_CHAT_NEW;
import static gsshop.mobile.v2.ServerUrls.WEB.MOBILE_LIVE_ENTER;
import static gsshop.mobile.v2.ServerUrls.WEB.MOBILE_LIVE_MORE_CLICK;
import static gsshop.mobile.v2.support.gtm.AMPEnum.MOBILELIVE_CLICK_EXIT;
import static gsshop.mobile.v2.support.gtm.AMPEnum.MOBILELIVE_CLICK_TALK;

/**
 * 모바일라이브 프레그먼트
 */
public class ExoMobileLiveMediaPlayerControllerFragment extends BaseFragment
        implements OnMediaPlayerController, OnCspChatListener, KeyboardHeightObserver {

    private static final String TAG = "MobileLiveFragment";
    public static final String MOBILE_LIVE_PLAYER_CALLER = "MOBILE_LIVE_PLAYER"; //등록확정 팝업이 모라 생방송 플레이어 or 쇼핑라이브 탭매장 중에 어디 위에 뜰지 구분할 구분자

    /**
     * 화면상태
     */
    public enum ScreenState {
        //CHAT_VIEW,      //채팅보기 상태
        //PRODUCT_VIEW,   //방송상품보기 상태
        CHAT_ERROR,      //채팅서버 접속에러 상태

        VISIBLE_CHAT,
        INVISIBLE_CHAT
    }
    ScreenState screenState = ScreenState.VISIBLE_CHAT; //디폴트를 VISIBLE, configData.CO.CHAT 이 N일때만 INVISIBLE

    /**
     * 키패드 노출 여부
     */
    private boolean isKeyboardShow = false;

    /**
     * The keyboard height provider
     */
    private KeyboardHeightProvider keyboardHeightProvider;

    /**
     * 방송상품 divider width (dp)
     */
    private static final int VIEPAGER_PRODUCT_DIVIDER_WIDTH = 12;

    /**
     * 방송상품 미리노출할 width (dp)
     */
    private static final int VIEPAGER_PRODUCT_PREVIEW_WIDTH = 37;

    /**
     * 채팅창 페이드효과 height
     */
    private static final int FADE_LENGTH = 25;

    /**
     * 네비게이터 자동으로 접히기전 노출시간 (ms)
     */
    private static final int NAVI_SHOW_TIME = 2000;

    /**
     * 방송상품 데이타 유무 여부
     */
    private boolean isProductEmpty = false;

    /**
     * 현재 불륨
     */
    private float currentVolume;

    /**
     * 동영상 액션 인터페이스
     */
    private OnMediaPlayerListener callback;

    /**
     * 프로그레스바
     */
    private ProgressBar loadingProgress;

    /**
     * 동영상 정보
     */
    private MediaInfo mediaInfo;

    /**
     * 커넥티드 서비스
     * 채팅, 가이드 화면
     */
    private StaticChatCsp mChat = null;

    // exo player
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    /**
     * 게이트 페이지 URL
     */
    private String gatePageUrl = "";

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private ComponentListener componentListener;

    private int currentWindow;
    private boolean playWhenReady = true;
    private boolean isPrepared = false;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private View rootView;

    /**
     * 채팅리스트 용도
     */
    private RecyclerView chatRecycler;
    private ChatAdapter chatAdapter;

    /**
     * for tap event
     */
    private View tapView;

    /**
     * 채팅창 최대 길이
     */
    private int chatRecyclerMaxHeight;

    /**
     * 공유하기
     */
    private SnsV2DialogFragment snsDialog;
    private final String KAKAOTALK = "kakaotalk";
    private final String KAKAOSTORY = "kakaostory";
    private final String LINE = "line";
    private final String FACEBOOK = "facebook";
    private final String TWITTER = "twitter";
    private final String ETC = "etc";

    private final String OGIMG = "&ogImage="; //appRedirect에서 ogImg파라미터로 넘기면 공유하기시 미리보기 이미지 보이도록함
    private String mShareUrl; //해시처리된 공유될 url
    private String mPromotionName; //공유될 프로그램명
    private String mImgUrl; //공유될 이미지url
    private String mNoticeMoreUrl; //공지사항 더보기 주소

    /**
     * UI 컨트롤 변수
     */
    private LinearLayout chatProduct;
    private TextView txtWelcomeMsg;
    private FrameLayout chatList;
    private LinearLayout chatListNewLayout;
    private TextView chatListNewText;
    private FrameLayout chatNoti;
    private LinearLayout chatNotiMore;
    private View chatNotiTopMargin;
    private FrameLayout chatMsg;
    private View dimBottom;
    private ImageView chatSend;
    private EditText txtChatMsg;
    private TextView txtShowKeyboard;
    private TextView txtChatUnavailable;
    private LinearLayout chatMsgLayout;
    private ImageView mobilelive_onair_badge;
    private LinearLayout lay_chat_send;
    private FrameLayout lay_alarm;
    private LinearLayout lay_alarm_share;
    private LinearLayout chatProductMoreLayout;
    private TextView chatProductMoreCount;

    /**
     * 콜백이 오지 않을경우 2초동안 기다리고 후에 채팅숨김여부 이벤트 실행
     */
//    private final static int REOPEN_MESSAGE = 1;
    private final static int REOPEN_DELAY_TIME = 2000;

    /**
     * 새로운 채팅 메시지가 수신되었는지 여부
     */
    private boolean isChatListNewVisible = false;

    /**
     * 방송상품 표시용
     */
    private IndicatorRecyclerView indicatorRecycler;
    private PreviewRecyclerViewPager prdViewPager;
    private int currentPrdIndex = 0;

    /**
     * 공지 텍스트 롤링을 위한 변수 세팅
     */
    private static final long TEXT_ROLLING_OFFSET_PERIOD = 2000;	//타이머 최초 구동시까지 대기 시간(2초)
    private static final long TEXT_ROLLING_INTERVAL = 3000;	//문구 교체 주기(3초)
    private Timer notiTimer = null;
    private String[] notiText;	//공지 문구
    private int notiIdx = 0;	//현재 노출된 인덱스
    private int notiCnt = 0;	//문구 총 갯수
    private TextSwitcher notiSwitcher;
    private ViewSwitcher.ViewFactory notiFactory;

    /**
     * 상단 헤더 영역 onair/생방송 남은 시간 실시간 접속자 ( 누적 또는 실시간 ) X 버튼
     */
    public MobileLiveTimeLayout layoutMobileLiveTime;
    private TextView txtRemainTime;//onair_endtime_current
    private TextView nowPeople;//people_text
    private View layPeople;//people icon
    private Button backButton;//right_back_button_layout
    private Button shareButton; //share_button
    private CheckBox chkMute;   //음소거 버튼
    private LinearLayout lay_check_mute;
    private ImageView alarmOn; //방송알림 On
    private ImageView alarmOff; //방송알림 Off
    private View layAttend; //출첵이벤트 레이아웃

    /**
     * mAfterShowChat
     */
    private Handler mAfterShowChat;
    private Runnable mAfterShowChatRunnable;

    /**
     * 화면진입 후 처음 사용자 클릭을 무시할 시간.
     * 화면 세팅 중 사용자 클릭 발생시 UI 비정상 동작 방지용
     */
    private static final long TAP_DISMISS_TIME = 3000;

    @Inject
    protected RestClient restClient;

    /**
     * 생성자
     */
    public ExoMobileLiveMediaPlayerControllerFragment() {
        mediaInfo = new MediaInfo();
        mediaInfo.currentPosition = -1;
        mediaInfo.lastPlaybackState = Player.STATE_IDLE;
        mediaInfo.playerMode = MediaInfo.MODE_SIMPLE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        componentListener = new ComponentListener();

        //된다!!
        //mChat = StaticChatCsp.getInstance(getContext());
        mChat = new StaticChatCsp(getContext());
        mChat.setOnChatDataListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exo_mobilelive_media_player_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view.findViewById(R.id.root_view);

        tapView = view.findViewById(R.id.tapview);
        chatProduct = view.findViewById(R.id.chatProduct);
        txtWelcomeMsg = view.findViewById(R.id.txtWelcomeMsg);
        chatList = view.findViewById(R.id.chatList);
        chatListNewLayout = view.findViewById(R.id.chatListNewLayout);
        chatListNewText = view.findViewById(R.id.chatListNewText);
        chatNoti = view.findViewById(R.id.chatNoti);
        chatNotiMore = view.findViewById(R.id.chatNotiMore);
        chatNotiTopMargin = view.findViewById(R.id.chatNotiTopMargin);
        chatMsg = view.findViewById(R.id.chatMsg);
        dimBottom = view.findViewById(R.id.dimBottom);
        chatSend = view.findViewById(R.id.chatSend);
        txtChatMsg = view.findViewById(R.id.txtChatMsg);
        txtShowKeyboard = view.findViewById(R.id.txtShowKeyboard);
        txtChatUnavailable = view.findViewById(R.id.txtChatUnavailable);
        chatMsgLayout = view.findViewById(R.id.chatMsgLayout);
        notiSwitcher = view.findViewById(R.id.notiSwitcher);
        mobilelive_onair_badge = view.findViewById(R.id.mobilelive_onair_badge);
        lay_chat_send = view.findViewById(R.id.lay_chat_send);
        lay_check_mute = view.findViewById(R.id.lay_check_mute);
        lay_alarm = view.findViewById(R.id.lay_alarm);
        lay_alarm_share = view.findViewById(R.id.lay_alarm_share);
        chatProductMoreLayout = view.findViewById(R.id.lay_more);
        chatProductMoreCount = view.findViewById(R.id.txt_more_count);

        EventBus.getDefault().register(this);

        /**
         * 헤더 영역
         * if(tvLiveBanner.broadType != null && tvLiveBanner.broadType.equals(context.getResources().getString(R.string.home_tv_live_view_on_the_air))){
         layoutTvLiveProgress.setBackgroundResource(R.drawable.tv_tag_onair_android);
         }else if(tvLiveBanner.broadType != null && tvLiveBanner.broadType.equals(context.getResources().getString(R.string.home_tv_live_view_best))){
         layoutTvLiveProgress.setBackgroundResource(R.drawable.tv_tag_best_android);
         }
         */
        layoutMobileLiveTime = view.findViewById(R.id.onair_info);
        layoutMobileLiveTime.setOnTvLiveFinishedListener(new MobileLiveTimeLayout.OnTvLiveFinishedListener() {
            @Override
            public void onTvLiveFinished() {

                try{
                    WebUtils.goWeb(getContext(), gatePageUrl);

                    if(callback != null) {
                        callback.onFinished(getMediaInfo());
                    }
                    if(mChat != null) {
                        mChat.globalSocketOff();
                    }
                }catch (Exception e)
                {
                    //무시한다
                    //todo 방어처리를 꼮해야 한다. 백그라운드로 내려간 다음에 해당로직이 돌때 하면 callback null이다. 발생한다.
                    //그리고 백그라운드 상태에서 WebUtils.goWeb(getContext(), gatePageUrl); 해당 로직이 수행되면 앱이 뜬다 ㅋㅋㅋ
                }


            }
        });
        txtRemainTime = view.findViewById(R.id.txt_remain_time);
        layPeople = view.findViewById(R.id.peolpe_info);
        nowPeople = view.findViewById(R.id.people_text);
        backButton = view.findViewById(R.id.right_back_button);
        shareButton = view.findViewById(R.id.share_button);
        chkMute = view.findViewById(R.id.check_mute);
        alarmOn = view.findViewById(R.id.alarm_button_on);
        alarmOff = view.findViewById(R.id.alarm_button_off);
        layAttend = view.findViewById(R.id.lay_attend);

        alarmOn.setContentDescription(getString(R.string.shpping_live_alarm_btn_off) + "해제 버튼");
        alarmOff.setContentDescription(getString(R.string.shpping_live_alarm_btn_off) + "버튼");

        // X 닫기버튼 터치영역 확장
        View rightBackLayout = rootView.findViewById(R.id.right_back_layout);
        ViewGroup.LayoutParams params = rightBackLayout.getLayoutParams();
        rightBackLayout.setTouchDelegate(new TouchDelegate(new Rect(0,0,params.width,params.height),rootView.findViewById(R.id.right_back_button)));

        // 공유하기 버튼 터치영역 확장
        View shareLayout = rootView.findViewById(R.id.lay_share);
        ViewGroup.LayoutParams paramsShare = shareLayout.getLayoutParams();
        shareLayout.setTouchDelegate(new TouchDelegate(new Rect(0,0,paramsShare.width,paramsShare.height),rootView.findViewById(R.id.lay_share)));

        //음소거 세팅
        chkMute.setChecked(MainApplication.isMute);
        chkMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //음소거 O -> 음소거 X
                if(MainApplication.isMute){
                    MainApplication.isMute = false;
                    chkMute.setChecked(MainApplication.isMute);
                    setMute(MainApplication.isMute);
                    EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
                }else{
                    //음소거 X -> 음소거 O
                    MainApplication.isMute = true;
                    chkMute.setChecked(MainApplication.isMute);
                    setMute(MainApplication.isMute);
                    EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
                }
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SOUND);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onFinished(getMediaInfo());
                if(mChat != null) {
                    mChat.globalSocketOff();
                }
                AMPAction.sendAmpEvent(MOBILELIVE_CLICK_EXIT);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SNS 팝업 띄움
                snsDialog = new SnsV2DialogFragment();
                snsDialog.setSnsListener(snsListener);
                snsDialog.show(getChildFragmentManager(), ExoMobileLiveMediaPlayerControllerFragment.class.getSimpleName());

                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_BUTTON);
            }
        });

        /**
         * 상품 UI
         */
        indicatorRecycler = (IndicatorRecyclerView) view.findViewById(R.id.recycler_indicator);
        prdViewPager = view.findViewById(R.id.recycler_view_pager);

        playerView = view.findViewById(R.id.exoplayer_video_view);
        playerView.setUseController(false);

        loadingProgress = view.findViewById(R.id.mc_webview_progress_bar);

        chatRecycler = view.findViewById(R.id.chatRecycler);
        chatRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER); // 끝까지 스크롤시 검은색 막이 보이는 스크롤 효과 제거
        chatRecycler.setVerticalFadingEdgeEnabled(true); //채팅화면 상하단 부분 끝까지 스크롤시 점점 흐리게하는 효과
        chatRecycler.setFadingEdgeLength(FADE_LENGTH);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        ((LinearLayoutManager)layoutManager).setStackFromEnd(true);
        chatRecycler.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(getActivity());
        chatRecycler.setAdapter(chatAdapter);

        //이벤트 설정하면 키패드 노출 후 tapview가 오렌지색으로 깜박이는 현상이 방지됨
        view.findViewById(R.id.dummy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //뷰 세팅이 완료되기전 사용자 클릭 무시
        ClickUtils.work(TAP_DISMISS_TIME);

        //화면탭 이벤트
        tapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //뷰 세팅이 완료되기전 사용자 클릭 무시
                if (ClickUtils.work(0)) {
                    return;
                }
                onTap();
            }
        });

        chatRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!chatRecycler.canScrollVertically(-1)) {
                    Ln.i("Top of list : " + newState);
                } else if (!chatRecycler.canScrollVertically(1)) {
                    Ln.i("End of list : " + newState);
                    if (newState == SCROLL_STATE_DRAGGING) {
                        isChatListNewVisible = true;
                    } else if (newState == SCROLL_STATE_IDLE) {
                        ViewUtils.hideViews(chatListNewLayout);
                        isChatListNewVisible = false;
                    }
                } else {
                    Ln.i("idle : " + newState);
                }
            }
        });

        txtChatMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    chatSend.setBackgroundResource(R.drawable.ic_send_active);
                } else {
                    chatSend.setBackgroundResource(R.drawable.ic_send_default);
                }
            }
        });

        txtChatMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ViewUtils.hideSoftInput(view);
                    // 로그인 확인
                    User user = User.getCachedUser();
                    if (user == null || user.customerNumber.length() < 2) {
                        //로그인 activity 호출.
                        new CustomTwoButtonDialog(getActivity()).message(R.string.moblielive_login).positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                Intent intent = new Intent(Keys.ACTION.LOGIN);
                                intent.putExtra(Keys.INTENT.FROM_MOBILE_LIVE, true);
                                startActivity(intent);
                            }
                        }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();

                        view.clearFocus();
                    }
                }
            }
        });

        txtChatMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        lay_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                AMPAction.sendAmpEvent(MOBILELIVE_CLICK_TALK);
            }
        });

        txtShowKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(txtChatMsg);
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(MOBILE_LIVE_CHAT_CLICK);
            }
        });

        chatListNewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideViews(chatListNewLayout);
                chatListNewText.setText("");
                chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(MOBILE_LIVE_CHAT_NEW);
            }
        });


        initializeUI();
//        mAfterShowChat.sendEmptyMessageDelayed(REOPEN_MESSAGE, REOPEN_DELAY_TIME);
        mAfterShowChat = new Handler();
        mAfterShowChatRunnable = () -> {
            //initHideStart();
        };
        mAfterShowChat.postDelayed(mAfterShowChatRunnable, REOPEN_DELAY_TIME);

        //모바일 라이브 진입 mseq추가
        ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(MOBILE_LIVE_ENTER);
    }

    /**
     * UI 초기화 수행
     */
    private void initializeUI() {
        //키패드 세팅
        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());
        rootView.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });
        //The height is fixed and the width is increased or decreased to obtain the desired aspect ratio.
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);

        //웰컴메시지 API 호출
        ThreadUtils.INSTANCE.runInUiThread(() -> getWelcomeMessage(), 3000);
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        int marginBottom;
        if (height >= 1) {
            //키패드 노출
            isKeyboardShow = true;
            chatMsgLayout.setVisibility(View.VISIBLE);
            marginBottom = (int) getResources().getDimension(R.dimen.mobile_live_right_menu_margin_bottom_key_show);
        } else {
            //키패드 비노출
            isKeyboardShow = false;
            chatMsgLayout.setVisibility(View.GONE);
            marginBottom = (int) getResources().getDimension(R.dimen.mobile_live_right_menu_margin_bottom_key_hide);
        }
        //키보드 노출시 메뉴겹침 방지 (보내기버튼과 공유버튼)
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lay_alarm_share.getLayoutParams();
        params.bottomMargin = marginBottom;
        lay_alarm_share.setLayoutParams(params);
    }

    /**
     * 타이머 시작 종료 시간만 넣는다.
     * @param result
     */
    public void setBroadCast(MobileLiveResult result)
    {
        //공유하기시 전달될 컨텐츠들
        if(DisplayUtils.isValidString(result.shareUrl) && DisplayUtils.isValidString(result.imgUrl)){
            //공유하기시 공유될 url 형태 (appRedirect + 해시처리된 게이트페이지 형태의 url + ogImg)
            mShareUrl = ServerUrls.WEB.APPREDIRECT + getBase64Encode(result.shareUrl) + OGIMG + result.imgUrl;
            mPromotionName = result.promotionName;
            mImgUrl = result.imgUrl;
        }

        //해더영역 노출
        ViewUtils.showViews(layoutMobileLiveTime, layPeople, chkMute);

        //게이트 페이지 주소 세팅
        if (result != null && !TextUtils.isEmpty(result.gatePageUrl)) {
            gatePageUrl = result.gatePageUrl;
        }

        //공지사항 더보기 주소
        if (result != null && !TextUtils.isEmpty(result.notiMoreLinkUrl)) {
            mNoticeMoreUrl = result.notiMoreLinkUrl;
        }

        String endTime = "";
        if (result != null && !TextUtils.isEmpty(result.endDate) && !"0".equals(result.endDate)) {
            //리플레이(재방송) 일때
            if (getResources().getString(R.string.home_tv_live_view_replay_air).equalsIgnoreCase(result.broadType) ||
                    getResources().getString(R.string.home_tv_live_view_replay_air_eng).equalsIgnoreCase(result.broadType)) {
                ViewUtils.hideViews(mobilelive_onair_badge);
                //리플레이면 왼쪽, 오른쪽 패딩 조정
                int padding = getResources().getDimensionPixelOffset(R.dimen.mobilelive_replay_remain_time_padding_horizontal);
                txtRemainTime.setPadding(padding, 0, padding, 0);
            }

            layoutMobileLiveTime.setDisplayType(TimeRemaining.DisplayType.MINUTE_MM); //default mm:ss

            //방송종료시간이 60분 이상인 경우
            try {
                TimeRemaining timeRemaining = new TimeRemaining(result.endDate);
                timeRemaining.parseTime();
                int mHour = timeRemaining.getHour();
                int mMinute = timeRemaining.getMinute();
                if (mHour >= 1 || mMinute >= 60) {
                    layoutMobileLiveTime.setDisplayType(TimeRemaining.DisplayType.HOUR); // hh:mm:ss
                }

                endTime = result.endDate;
                layoutMobileLiveTime.updateTvLiveTime(endTime);
            } catch (Exception e) {
                Ln.e(e);
            }
        } else {
            stopTvLiveTimer();
        }

        //알람버튼 초기상태 세팅
        if("Y".equals(result.alarmYn)){
            alarmOn.setVisibility(View.VISIBLE);
            alarmOff.setVisibility(View.GONE);
        }else{
            alarmOn.setVisibility(View.GONE);
            alarmOff.setVisibility(View.VISIBLE);
        }

        //방송알림 클릭
        lay_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 확인
                User user = User.getCachedUser();
                if (user == null || user.customerNumber.length() < 2) {
                    //로그인 activity 호출.
                    new CustomTwoButtonDialog(getActivity()).message(R.string.moblielive_login).positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            Intent intent = new Intent(Keys.ACTION.LOGIN);
                            intent.putExtra(Keys.INTENT.FROM_MOBILE_LIVE, true);
                            startActivity(intent);
                        }
                    }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();
                }else{
                    if (View.VISIBLE == alarmOn.getVisibility()){
                        //알람해제 팝업에 필요한 정보를 가져오기위해 api호출
                        MLAlarm.deleteQuery(getContext(), MOBILE_LIVE_PLAYER_CALLER);
                    } else {
                        //알람등록 팝업에 필요한 정보를 가져오기위해 api호출
                        MLAlarm.addQuery(getContext(), MOBILE_LIVE_PLAYER_CALLER);
                    }
                }

                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_ALARM);
            }
        });

        //출첵 이벤트
        ViewUtils.hideViews(layAttend);
        if (isNotEmpty(result.attendanceUrl)) {
            ViewUtils.showViews(layAttend);
            layAttend.setOnClickListener(v -> {
                // 로그인 확인
                User user = User.getCachedUser();
                if (user == null || user.customerNumber.length() < 2) {
                    //로그인 activity 호출.
                    new CustomTwoButtonDialog(getActivity()).message(R.string.moblielive_login).positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            Intent intent = new Intent(Keys.ACTION.LOGIN);
                            intent.putExtra(Keys.INTENT.FROM_MOBILE_LIVE, true);
                            startActivity(intent);
                        }
                    }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();
                } else {
                    WebUtils.goWeb(getContext(), result.attendanceUrl);
                }
            });
        }

        // 쿠폰 팝업 노출
        if (isNotEmpty(result.couponPopupUrl)) {
            try {
                // 더이상 보지 않기한 날짜와 현재 날짜 비교하여 지났으면 팝업 보여줘도 괜찮다.
                String prevDate = PrefRepositoryNamed.getString(getContext(), Keys.PREF.PREF_SHOPPY_LIVE_TODAY_PASS);
                if (isEmpty(prevDate) || DateFormat.format("yyyyMMdd", new Date()).toString().compareTo(prevDate) > 0) {
                    WebUtils.goWeb(getContext(), result.couponPopupUrl);
                }
            }
            catch (NullPointerException e){
                Ln.e(e.getMessage());
            }
        }
    }

    /**
     * 생방송 남은시간 노출용 타이머를 정지한다.
     */
    private void stopTvLiveTimer() {
        layoutMobileLiveTime.stopTimer();
    }
    /**
     * 상품리스트를 세팅한다.
     *
     */
    public void setProductList(MobileLiveResult result, String currentPrdIndex) {
        ArrayList<MobileLivePrdsInfoList> prdList = result.mobileLivePrdsInfoList;
        if (isEmpty(prdList)) {
            isProductEmpty = true;
            return;
        }

        DisplayMetrics metrics = getDisplayMetrics();
        indicatorRecycler.setIndicator(R.layout.recycler_item_mobile_live_indicator, R.id.radio_indicator, prdList.size());
        PreviewMobileLivePrdAdapter prdAdapter = new PreviewMobileLivePrdAdapter(getContext(), metrics, prdList, currentPrdIndex);
        prdViewPager.setAdapter(prdAdapter);

        //더보기 세팅
        ViewUtils.hideViews(chatProductMoreLayout);

        if (prdList.size() > 1) {
            ViewUtils.showViews(chatProductMoreLayout);
            chatProductMoreCount.setText("+" + (prdList.size() - 1));
            chatProductMoreLayout.setOnClickListener(v -> {
                new MobileLiveApiontroller(getContext()).execute(result.moreBtnUrl);
            });
        }
    }

    public void setMoreViewDialog(ArrayList<MobileLivePrdsInfoList> prdList) {
        MobileLivePrdDialogFragment prdDialog = new MobileLivePrdDialogFragment();
        prdDialog.setData(prdList);
        prdDialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), MobileLivePrdDialogFragment.class.getSimpleName());
        ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(MOBILE_LIVE_MORE_CLICK);
    }

    private DisplayMetrics getDisplayMetrics() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return metrics;
    }

    private void sendMessage() {
        String msg = txtChatMsg.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            return;
        } else if (msg.trim().length() == 0) {
            txtChatMsg.setText("");
            return;
        }
        txtChatMsg.setText("");
        hideKeyboard(txtChatMsg);
        //주문번호 복붙하는 경우 개행문자가 함께 붙여져서 하단에 공백이 생기는 현상 개선
        String replacedMsg = msg.replace("\n\n", " ")
                .replace("\n", " ");
        mChat.messageSend(replacedMsg);
    }

    /**
     * 화면 탭 이벤트
     */
    private void onTap() {
        //키패드 노출 상태에서는 키패드 내리기
        if (isKeyboardShow) {
            hideKeyboard(txtChatMsg);
            return;
        }
        //채팅보이는 상태에서 탭을 누르면
        if(ScreenState.VISIBLE_CHAT.equals(screenState)){
            if(chatMsg.getVisibility() == View.VISIBLE || chatProduct.getVisibility() == View.VISIBLE){
                ViewUtils.hideViews(chatMsg, chatList, chatProduct, dimBottom, lay_alarm_share);
                setNotiLong();
            }else{
                setNotiShort(); //공지사항 wrap_content

                //채팅이 하나라도 있을때 chatList보여지도록
                if(chatAdapter.getItemCount() > 0){
                    ViewUtils.showViews(chatMsg, chatList, dimBottom, lay_alarm_share);
                }else{
                    ViewUtils.showViews(chatMsg, lay_alarm_share);
                }

                //상품이 있는지 체크
                if (!isProductEmpty) {
                    ViewUtils.showViews(chatProduct);
                }

                //탭할때마다 채팅 맨 밑으로
                try {
                    if (chatAdapter.getItemCount() > 0) {
                        chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                } catch (IllegalArgumentException e) {
                    //키바나 수집 로그로 앱크래쉬 방지  (java.lang.IllegalArgumentException: Invalid target position)
                    Ln.e(e);
                }
            }

        }else{
            setNotiLong(); //공지사항 match_parent

            //채팅안보이는 상태(의료용품)에서 탭을 누르면
            if(chatProduct.getVisibility() == View.VISIBLE){
                if (!isProductEmpty) {
                    ViewUtils.hideViews(chatProduct);
                }
            }else{
                //상품이 있는지 체크
                if (!isProductEmpty) {
                    ViewUtils.showViews(chatProduct);
                }
            }
        }
    }

    private void showKeyboard(View view) {
        view.requestFocusFromTouch();
        InputMethodManager lManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        lManager.showSoftInput(view, 0);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (callback != null) {
            return;
        }

        try {
            callback = (OnMediaPlayerListener) context;
        } catch (ClassCastException e) {
            callback = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onPause() {

        super.onPause();

        if (Util.SDK_INT <= 23) {
            releasePlayer();
            if(mChat != null) {
                mChat.globalSocketOff();
            }
        }
        //pause 키보드를 죽이고 가자
        hideKeyboard(txtChatMsg);
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
            if(mChat != null) {
                mChat.globalSocketOff();
            }
        }

        //공지타이머 정지
        stopRollingTextTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        //공지타이머 시작
        startRollingTextTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mChat != null) {
            mChat.globalSocketOff();
        }

        //키바나 수집 로그로 앱크래쉬 방지  (java.lang.NullPointerException)
        if (isNotEmpty(keyboardHeightProvider)) {
            keyboardHeightProvider.close();
        }

        stopTvLiveTimer();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);

        super.onDestroyView();
    }

    private void initializePlayer(String videoUrl) {
        if (simpleExoPlayer == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // using a DefaultTrackSelector with an adaptive video selection factory
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());
            simpleExoPlayer.addListener(componentListener);
            playerView.setPlaybackPreparer(componentListener);
            playerView.setControllerVisibilityListener(componentListener);
            playerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.setPlayWhenReady(playWhenReady);
            simpleExoPlayer.seekTo(currentWindow, 0);
        }

        //음소거 여부 매장과 동기화
        setMute(MainApplication.isMute);
        MediaSource mediaSource = buildMediaSource(videoUrl);
        simpleExoPlayer.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            setMute(true);
            mediaInfo.currentPosition = simpleExoPlayer.getCurrentPosition();
            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.removeListener(componentListener);
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            isPrepared = false;
        }
    }

    private MediaSource buildMediaSource(String uriString) {
        Uri uri = Uri.parse(uriString);
        int type = Util.inferContentType(uri);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("ua");
        switch (type) {
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private boolean isVod() {
        if(isEmpty(simpleExoPlayer) || simpleExoPlayer.getDuration() == C.TIME_UNSET) return true;
        return !simpleExoPlayer.isCurrentWindowDynamic();
    }

    private void onFullScreenClick(boolean isPlaying) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            if (callback != null) {
                if (mediaInfo != null) {
                    mediaInfo.isPlaying = isPlaying;
                }
                callback.onFullScreenClick(getMediaInfo());
            }
        }, 200);
    }

    /**
     * 미디어를 재생한다.
     */
    private void play() {
        mediaInfo.isPlaying = true;
        initializePlayer(mediaInfo.contentUri);
        initChatCsp(mediaInfo.liveNo);

        callback.onPlayed();
    }

    private boolean isPause() {
        // TODO: 2019. 2. 8. mslee

        if (simpleExoPlayer == null) {
            return false;
        }
        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_READY && !playWhenReady;
    }

    private boolean isEnded() {
        // TODO: 2019. 2. 8. mslee

        if (simpleExoPlayer == null) {
            return false;
        }

        int playState = simpleExoPlayer.getPlaybackState();
        return playState == Player.STATE_ENDED;
    }

    private void showLoadingProgress(boolean show) {
        if (show) {
            ViewUtils.showViews(loadingProgress);
        } else {
            ViewUtils.hideViews(loadingProgress);
        }
    }

    @Override
    public MediaInfo getMediaInfo() {
        if (mediaInfo == null
                || (TextUtils.isEmpty(mediaInfo.contentUri) )){//&& TextUtils.isEmpty(mediaInfo.videoId))) {
            return null;
        } else {
            if (simpleExoPlayer != null) {
                mediaInfo.lastPlaybackState = simpleExoPlayer.getPlaybackState();
                long currentPosition = simpleExoPlayer.getCurrentPosition();
                if (isVod() && currentPosition > mediaInfo.currentPosition) {
                    mediaInfo.currentPosition = currentPosition;
                }
            }
            return mediaInfo;
        }
    }

    @Override
    public void setMediaInfo(MediaInfo media)  {
        mediaInfo = media;
    }

    @Override
    public View getPlayerView() {
        return getView();
    }

    /**
     * background
     *
     * @param resid
     */
    @Override
    public void setBackgroundResource(int resid) {
        getView().setBackgroundResource(resid);
    }

    public void setBackground(Drawable background) {
        getView().setBackground(background);
    }

    public void setBackgroundColor(int color) {
        getView().setBackgroundColor(color);
    }

    @Override
    public boolean isPlaying() {
        if (simpleExoPlayer == null) {
            return false;
        }
        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_READY && playWhenReady;
    }

    public void initChatCsp(String liveNo) {
        if (isEmpty(mediaInfo)) {
            throw new IllegalArgumentException("The media info is null!!");
        }
        if (!TextUtils.isEmpty(liveNo)) {
            if (!mChat.isSocketConnect()) {
                mChat.initSocket(liveNo);
                if( chatAdapter != null ) {
                    chatAdapter.reMove();
                    chatAdapter.notifyDataSetChanged();
                    //채팅 목록 초기화
                    FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    chatRecycler.setLayoutParams(param);

                    if (chatListNewLayout != null) {
                        ViewUtils.hideViews(chatListNewLayout);
                        isChatListNewVisible = false;
                    }
                }
            }
        }
    }

    /**
     * 동영상을 재생한다.
     */
    @Override
    public void playPlayer() {
        if (isEmpty(mediaInfo)) {
            throw new IllegalArgumentException("The media info is null!!");
        }

        if (!TextUtils.isEmpty(mediaInfo.contentUri)) {
            if( !this.isPlaying() ) {
                initializePlayer(mediaInfo.contentUri);
                initChatCsp(mediaInfo.liveNo); // mediaInfo && mediaInfo.liveNo 필수 정보이다
            }

        } else {
            //비디오정보가 없는 경우
            Toast.makeText(getActivity(), R.string.video_play_error, Toast.LENGTH_SHORT)
                    .show();
            if (callback != null) {
                callback.onError(null);
            }
        }

    }

    @Override
    public void stopPlayer() {
        releasePlayer();
        if(mChat != null) {
            mChat.globalSocketOff();
        }
    }

    /**
     * 음소거 여부를 설정한다.
     *
     * @param on if true, 음소거
     */
    @Override
    public void setMute(boolean on) {
        // audio
        if (simpleExoPlayer != null) {
            if (on) {
                if(simpleExoPlayer.getVolume() > 0) {
                    currentVolume = simpleExoPlayer.getVolume();
                }
                simpleExoPlayer.setVolume(0f);
            } else {
                if (currentVolume > 0) {
                    simpleExoPlayer.setVolume(currentVolume);
                }
            }
        }
    }

    private class ComponentListener extends Player.DefaultEventListener implements PlaybackPreparer, PlayerControlView.VisibilityListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    showLoadingProgress(true);
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    if(!isPrepared) {
                        // TODO: 2019. 2. 8. mslee
                        // ex)
                        //initUI();

                        if (callback != null) {
                            callback.onSetVideoType(isVod());
                        }
                        if(isVod() && mediaInfo.currentPosition > 0) {
                            simpleExoPlayer.seekTo(mediaInfo.currentPosition);
                        }

                        isPrepared = true;
                        setMute(MainApplication.isMute);
                    }
                    showLoadingProgress(false);
                    break;
                case Player.STATE_ENDED:

                    stateString = "ExoPlayer.STATE_ENDED     -";
                    /*
                    if (callback != null) {
                        callback.onFinished(getMediaInfo());
                    }
                    releasePlayer();
                    */
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }

            if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED ||
                    !playWhenReady) {

                rootView.setKeepScreenOn(false);
            } else { // STATE_IDLE, STATE_ENDED
                // This prevents the screen from getting dim/lock
                rootView.setKeepScreenOn(true);
            }
            Ln.d("changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            Toast.makeText(getActivity(), R.string.video_play_error, Toast.LENGTH_SHORT)
                    .show();
            // TODO: 2019. 2. 8. mslee
            //에러 상황
            //ex)
            //initUI();
            //pauseImage.setVisibility(View.GONE);
            //playImage.setVisibility(View.VISIBLE);
            if (callback != null) {
                callback.onError(null);
            }

            // TODO: 2019. 2. 8. mslee
            // 클릭할수 없도록
            //playImage.setEnabled(false);

            // TODO: 2019. 2. 8. mslee
            //전체화면인 경우 액티비티 종료
            if (mediaInfo.playerMode == MediaInfo.MODE_FULL) {
                //아래 접수 건으로 액티비티 종료 부분 주석처리
                //https://jira.gsenext.com/browse/SQA-2657
                //getActivity().finish();
                if(mChat != null) {
                    mChat.globalSocketOff();
                }
            }
        }

        @Override
        public void preparePlayback() {

        }

        @Override
        public void onVisibilityChange(int visibility) {
            if (visibility == View.VISIBLE) {
                ViewUtils.showViews(chatList, chatMsg, dimBottom, lay_alarm_share);
            } else {
                ViewUtils.hideViews(chatList, chatMsg, dimBottom, lay_alarm_share);
            }

            if (callback != null) {
                callback.onTap(visibility == View.VISIBLE);
            }
        }
    }

    /**
     * 채팅메시지 수신 콜백
     *
     * @param data
     */
    @Override
    public void onMessaged(JSONObject data) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            if(screenState == ScreenState.INVISIBLE_CHAT){
                chatList.setVisibility(View.GONE);
                dimBottom.setVisibility(View.GONE);
            }else{
                if(chatProduct.getVisibility() == View.GONE && chatList.getVisibility() == View.GONE){
                    chatList.setVisibility(View.GONE);
                    dimBottom.setVisibility(View.GONE);
                }else{
                    chatList.setVisibility(View.VISIBLE);
                    dimBottom.setVisibility(View.VISIBLE);
                }
            }

            if (data == null) {
                return;
            }
            try {
                MobileLiveData.ChatData chatData = new Gson().fromJson(data.toString(), MobileLiveData.ChatData.class);
                if (chatData != null) {

                    chatRecyclerMaxHeight = getResources().getDimensionPixelSize(R.dimen.chatRecyclerMaxHeight);

                    if(chatRecycler.getHeight() > chatRecyclerMaxHeight){
                        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, chatRecyclerMaxHeight);
                        chatRecycler.setLayoutParams(param);
                    }

                    /**
                     * 사이즈 체크하는 로직 이후로 변경(테스트 필요 08/14)
                     */
                    chatAdapter.add(chatData);

                    if (isChatListNewVisible) {
                        //고객번호로 본인이 쓴건지 확인 -> 본인 맞으면 말풍선 안띄우고 채팅창 스크롤 바로 맨 밑으로 내리기
                        User user = User.getCachedUser();
                        if(user != null && user.customerNumber.equals(chatData.UO.U)){
                            chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                            chatListNewText.setText(chatData.MO.MG);
                        }
                        else {
                            if ("N".equals(chatData.MO.VI)) {
                                // 블랙리스트, 채팅 비노출 처리
                            } else {
                                chatListNewText.setText(chatData.MO.MG);
                            }
                        }
                        ViewUtils.showViews(chatListNewLayout);
                    } else {
                        chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                    }
                }

                //공지사항 폭이 확장된 상태에서 메시지 수신받으면 줄어드는 현상 개선
                if(lay_alarm_share.getVisibility() == View.VISIBLE) {
                    setNotiShort(); //메세지가 왔다는것은 공지사항이 wrap_content
                }
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

    /**
     * 채팅메시지 수신 콜백
     *
     * @param data
     */
    @Override
    public void onReceivedChats(JSONObject data) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            if(screenState == ScreenState.INVISIBLE_CHAT){
                chatList.setVisibility(View.GONE);
                dimBottom.setVisibility(View.GONE);
            }else{
                if(chatProduct.getVisibility() == View.GONE && chatList.getVisibility() == View.GONE){
                    chatList.setVisibility(View.GONE);
                    dimBottom.setVisibility(View.GONE);
                }else{
                    chatList.setVisibility(View.VISIBLE);
                    dimBottom.setVisibility(View.VISIBLE);
                }
            }

            if (data == null) {
                return;
            }
            try {
                //받아온 채팅히스토리
                MobileLiveData.ChatDataList chatDataList = new Gson().fromJson(data.toString(), MobileLiveData.ChatDataList.class);

                if (chatDataList != null && isNotEmpty(chatDataList.CA)) {
                    chatRecyclerMaxHeight = getResources().getDimensionPixelSize(R.dimen.chatRecyclerMaxHeight);

                    //추가된 채팅으로 최대길이를 초과하거나 || 받아온 채팅리스트의 갯수가 6개부터면 -> 사이즈 줄이
                    if(chatRecycler.getHeight() > chatRecyclerMaxHeight || chatDataList.CA.size() > 5){
                        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, chatRecyclerMaxHeight);
                        chatRecycler.setLayoutParams(param);
                    }

                    /**
                     * 0508 어버이날 이거 페이징 처리를 해야 한다면 setinfo가 아니라 addList 형태로 변경해야함
                     * 현재는 최근 10000개 가져와서 그냥 리스트의 아탑터를 변경하는 로직임
                     * 리버스는 해줄예정
                     */


                    //채팅 리스트가 보이는 상태이면?? 스크롤을 맨 밑으로 그런데. 처음 진입시에 올덴데... 그런상황이 있을까???
                    //메세지가 채워졌으니 공지사항은 wrap_content 수행되도록 아래 확인

                    chatAdapter.setInfo(chatDataList.CA);
                    chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }

                setNotiShort(); //메세지가 왔다는것은 공지사항이 wrap_content
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

    /**
     * 공지사항 수신 콜백
     *
     * @param data 공지데이타
     */
    @Override
    public void onNotied(JSONObject data) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            if (data == null) {
                return;
            }

            try {
                MobileLiveData.NotiData notiData = new Gson().fromJson(data.toString(), MobileLiveData.NotiData.class);
                if (notiData != null) {
                    initializeNotiMessage(notiData);
                }
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

    private void showNowPeople(int people) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            if (nowPeople != null && people > 0) {
                nowPeople.setText(DisplayUtils.getFormattedNumber(people + ""));
                nowPeople.setContentDescription("시청자 수, 총 " + people + "명");
            }
        });
    }
    /**
     * 스테이트 변경에 대한 콜백 ( 현재는 사람 수
     *
     * @param data
     */
    @Override
    public void onStated(JSONObject data) {
        if (data != null) {
            Log.i("CHATCSP", "onStated " + data.toString());
            try {
                //펑션으로 처리하도록
                MobileLiveData.StateData temp = new Gson().fromJson(data.toString(), MobileLiveData.StateData.class);
                if (temp != null) {
                    if (temp.SO != null) {
                        if (temp.SO.PV != null) {
                            //누적 데이터 입니다
                            Log.i("CHATCSP", temp.SO.PV + "");
                            showNowPeople(temp.SO.PV);
                        }
                        if (temp.SO.UV != null) {
                            Log.i("CHATCSP", temp.SO.UV + "");
                        }
                    }

                    if (temp.TS != null)
                        Log.i("CHATCSP", temp.TS);

                }
            } catch (Exception e) {
                Ln.e(e);
            }
        }
    }

    /**
     * CSP의 Config 목록을 받아와 각각에 맞는 세팅을 한다
     * ex) 채팅보여주기, 채팅안보여주기 -> configData.CHAT, Y or N
     *
     * @param data configData.CHAT -> Y, N NULL일때 그리고 이 세가지 경우가 아닐때
     *             상품이 없을경우 테스트 방법은 setPrdList(null, null)
     */
    @Override
    public void onReceivedConfig(JSONObject data) {
        if (data == null) {
            return;
        }
        try {
            MobileLiveData.ConfigData configData = new Gson().fromJson(data.toString(), MobileLiveData.ConfigData.class);
            if (configData != null) {
                //채팅 설정값이 있다면?
                if (configData.CO != null) {
                    if(mAfterShowChat!=null){
//                        if(mAfterShowChat.hasMessages(REOPEN_MESSAGE)){
//                            mAfterShowChat.removeMessages(REOPEN_MESSAGE);
//                        }
                        mAfterShowChat.removeCallbacks(mAfterShowChatRunnable);
                    }
                    if ("N".equals(configData.CO.CHAT)) {
                        screenState = ScreenState.INVISIBLE_CHAT;
                        setConfigN(); // N일때 UI 세팅
                        setNotiLong(); // 공지사항 match_parent

                    } else if ("Y".equals(configData.CO.CHAT)) {
                        screenState = ScreenState.VISIBLE_CHAT;
                        setConfigY(); // Y일때 UI 세팅
                        setNotiShort(); // 공지사항 wrap_content
                    }
                }

                //... Config 가 추가될때마다 유형 추가된다
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }


    /**
     * Config N일때 UI처리
     */
    private void setConfigN() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            ViewUtils.hideViews(chatMsg, chatList, dimBottom, lay_alarm_share);
        });
    }

    /**
     * Config Y일때 UI처리
     */
    private void setConfigY() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {

            if (chatProduct.getVisibility() == View.GONE) {

            } else {
                //채팅이 하나라도 있을때 chatList보여지도록
                if (chatAdapter.getItemCount() > 0) {
                    ViewUtils.showViews(chatMsg, chatList, dimBottom, lay_alarm_share);
                } else {
                    ViewUtils.showViews(chatMsg, lay_alarm_share);
                }
            }
        });
    }

    /**
     * 공지사항 영역을 초기화한다.
     *
     * @param notiData NotiData
     */
    private void initializeNotiMessage(MobileLiveData.NotiData notiData) {
        if (!isEmpty(notiData) && notiData.MA != null && notiData.MA.size() > 0) {
            notiCnt = notiData.MA.size();
            notiText = notiData.MA.toArray(new String[notiCnt]);

            if (notiCnt > 1) {
                //더보기 노출
                ViewUtils.showViews(chatNotiMore);
                setNotiSwitcher(true);
                chatNotiMore.setOnClickListener(v -> {
                    if (isEmpty(mNoticeMoreUrl)) {
                        return;
                    }
                    Uri uri = Uri.parse(mNoticeMoreUrl);
                    if (WebUtils.DIRECT_ORD_HOST.equals(uri.getHost())) {
                        Intent intent = new Intent(DIRECT_ORDER_WEB);
                        intent.putExtra(Keys.INTENT.WEB_URL, uri.getEncodedQuery());
                        getContext().startActivity(intent);
                        return;
                    }
                    WebUtils.goWeb(getContext(), mNoticeMoreUrl);
                });
            } else {
                ViewUtils.hideViews(chatNotiMore);
                setNotiSwitcher(false);
            }

            if (notiFactory == null) {
                notiFactory = new ViewSwitcher.ViewFactory() {
                    @Override
                    public View makeView() {
                        return LayoutInflater.from(getActivity()).inflate(R.layout.mobilelive_noti_text, null);
                    }
                };
                notiSwitcher.setFactory(notiFactory);
            }

            notiSwitcher.setInAnimation(getActivity(), R.anim.mobilelive_noti_up_in);
            notiSwitcher.setOutAnimation(getActivity(), R.anim.mobilelive_noti_up_out);

            //텍스트영역 공백상태를 최소화 하기 위해 타이머 수행전에 테스트 먼저 세팅
            if (notiTimer == null) {
                setNotiRollingText();
            }

            //공지영역 노출
            ViewUtils.showViews(chatNoti);
            ViewUtils.hideViews(chatNotiTopMargin);

            startRollingTextTimer();
        } else {
            //공지영역 비노출
            ViewUtils.hideViews(chatNoti);
            ViewUtils.showViews(chatNotiTopMargin);
        }
    }

    /**
     * 공지문구 교체를 위한 타이머를 시작한다.
     */
    private void startRollingTextTimer() {
        if (notiCnt < 2) {
            return;
        }
        stopRollingTextTimer();
        notiTimer = new Timer();
        notiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setNotiRollingText();
            }
        }, TEXT_ROLLING_OFFSET_PERIOD, TEXT_ROLLING_INTERVAL);
    }

    /**
     * 공지문구를 롤링시킨다.
     */
    private void setNotiRollingText() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            notiIdx = notiIdx < notiCnt ? notiIdx : 0;
            if (notiCnt > 1) {
                //다음에 표시될 textview에 텍스트를 세팅하고 애니메이션을 수행하면서 textview가 교체된다.
                notiSwitcher.setText(notiText[notiIdx]);
            } else {
                //현재 노출된 textview에 텍스트를 세팅한다. 이때 애니메이션은 수행되지 않는다.
                notiSwitcher.setCurrentText(notiText[notiIdx]);
            }
            notiIdx++;
        });
    }

    /**
     * 공지문구 교체를 위한 타이머를 중지한다.
     */
    private void stopRollingTextTimer() {
        if (notiTimer != null) {
            notiTimer.cancel();
            // 대기중이던 취소된 행위가 있는 경우 모두 제거
            notiTimer.purge();
            notiTimer = null;
        }
    }
/*
    private Handler mAfterShowChat = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            //if(msg.what)
            if(msg!=null){
                Log.e(TAG, "handleMessage: what "+msg.what );
                if(msg.what == REOPEN_MESSAGE){
                    initHideStart();
                }

            }
        }
    };
    */

    /**
     * 더보기 노출여부에 따라 Switcher width를 조절한다.
     *
     * @param isMore 더보기 노출하는 경우 true
     */
    public void setNotiSwitcher(boolean isMore){
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) notiSwitcher.getLayoutParams();
        lp.setMargins(0, 0, DisplayUtils.convertDpToPx(getContext(), isMore ? 72 : 12), 0);
        notiSwitcher.setLayoutParams(lp);
    }

    //공지사항 width match_parent
    public void setNotiLong(){
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chatNoti.getLayoutParams();
            params.setMargins(setNotiMargin(12), 0,setNotiMargin(12), setNotiMargin(17));
            chatNoti.setLayoutParams(params);
        });
    }

    //공지사항 width wrap_content
    public void setNotiShort(){
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chatNoti.getLayoutParams();
            params.setMargins(setNotiMargin(12), 0, setNotiMargin(83), setNotiMargin(16));
            chatNoti.setLayoutParams(params);
        });
    }

    public int setNotiMargin(int dp){
        int size = dp;
        float scale = getResources().getDisplayMetrics().density;
        int margin = (int)(size * scale);
        return margin;
    }

    /**
     * 로그인 완료 후 웰컴메시지를 가져온다.
     *
     * @param event Events.MobileLiveAfterLoginEvent
     */
    public void onEvent(Events.MobileLiveAfterLoginEvent event) {
        getWelcomeMessage();
    }

    /**
     * 모바일라이브 방송알림 UI업데이트
     * @param event
     */
    public void onEvent(Events.AlarmUpdatetMLEvent event) {
        //모바일라이브 방송이 비정상일땐 if 예외처리 필요해보임
        try {
            //방송알림 해제 상태로 UI업데이트
            if (Events.AlarmUpdatetMLEvent.MAKE_ALARM_CANCEL.equals(event.eventType)) {
                alarmOn.setVisibility(View.GONE);
                alarmOff.setVisibility(View.VISIBLE);
            }
            //방송알림 등록 상태로 UI업데이트
            else if (Events.AlarmUpdatetMLEvent.MAKE_ALARM_OK.equals(event.eventType)) {
                alarmOn.setVisibility(View.VISIBLE);
                alarmOff.setVisibility(View.GONE);
            }
        } catch (Exception e){
            Ln.e(e);
        }
    }

    /**
     * 모바일라이브 방송알림 등록/해제 확정했을때 이벤트
     */
    public void onEvent(Events.AlarmRegistMLEvent event) {
        try {
            //모바일라이브 탭매장의 FlexibleFragment에서만 보내도록
            if(MOBILE_LIVE_PLAYER_CALLER.equals(event.caller)){
                //등록 확정 했을때
                if(MobileLiveAlarmDialogFragment.MOBILELIVE_ADD.equals(event.type)){
                    MLAlarm.add(getContext(), event.isNightAlarm, event.caller);
                }
                //해제 확정 했을때
                else if(MobileLiveAlarmCancelDialogFragment.MOBILELIVE_DELETE.equals(event.type)){
                    MLAlarm.delete(getContext(), event.caller);
                }
            }
        }catch (Exception e){
            Ln.e(e);
        }

    }

    /**
     * 공유하기 팝업에서 SNS 종류 선택
     */
    private SnsV2DialogFragment.OnSnsSelectedListener snsListener = new SnsV2DialogFragment.OnSnsSelectedListener() {
        @Override
        public void onSnsSelected(SnsV2DialogFragment.SHARE_TYPE shareType) {
            switch (shareType) {
                case SMS:
                    onSnsSelectedSms(shareType);
                    ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_SMS);
                    break;
                case Url:
                    onSnsSelectedUrl(shareType);
                    ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_URL);
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
        }

        @Override
        public void onCloseSelected() {
        }
    };


    /**
     * SMS 공유하기
     * @param shareType 공유타입
     */
    private void onSnsSelectedSms(SnsV2DialogFragment.SHARE_TYPE shareType) {
        try {
            if(DisplayUtils.isValidString(mPromotionName) && DisplayUtils.isValidString(mShareUrl)){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("sms_body", mPromotionName == null ? "" : mPromotionName + "\n" + mShareUrl);

                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.sms_not_support, Toast.LENGTH_SHORT).show();
            Ln.e(e);
        }
    }

    /**
     * URL복사 공유하기
     * @param shareType 공유타입
     */
    private void onSnsSelectedUrl(SnsV2DialogFragment.SHARE_TYPE shareType) {
        try {
            if(DisplayUtils.isValidString(mShareUrl)){
                new AndroidBridge(getContext()).sendClipData(mShareUrl);
            }
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }


    /**
     * 카카오톡, 카카오스토리, 트위터, 라인, 페이스북, 기타 공유하기
     * @param shareType 공유타입
     */
    private void onSnsSelectedOthers(SnsV2DialogFragment.SHARE_TYPE shareType) {
        if (DisplayUtils.isValidString(mShareUrl)) {
            ShareInfo shareInfo = new ShareInfo();

            if(SnsV2DialogFragment.SHARE_TYPE.KakaoTalk.equals(shareType)){
                shareInfo.setTarget(KAKAOTALK);
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_KAKAOTALK);
            }else if(SnsV2DialogFragment.SHARE_TYPE.KakaoStory.equals(shareType)){
                shareInfo.setTarget(KAKAOSTORY);
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_KAKAOSTORY);
            }else if(SnsV2DialogFragment.SHARE_TYPE.Twitter.equals(shareType)){
                shareInfo.setTarget(TWITTER);
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_TWITTER);
            }else if(SnsV2DialogFragment.SHARE_TYPE.Line.equals(shareType)){
                shareInfo.setTarget(LINE);
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_LINE);
            }else if(SnsV2DialogFragment.SHARE_TYPE.Facebook.equals(shareType)){
                shareInfo.setTarget(FACEBOOK);
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_FACEBOOK);
            }else if(SnsV2DialogFragment.SHARE_TYPE.Etc.equals(shareType)){
                shareInfo.setTarget(ETC);
            }

            //공유될 컨텐츠들
            shareInfo.setLink(mShareUrl);
            shareInfo.setSubject(mPromotionName); //카카오스토리공유에서 쓰이는 제목(없어도 에러는 안남)
            shareInfo.setMessage(mPromotionName);
            shareInfo.setImageurl(mImgUrl);
            shareInfo.setShareImageType(ShareInfo.ShareImageType.TYPE_DEFAULT);//카카오입공유에서 쓰이는 이미지 타입(없으면 에러남)

            ShareInterface shareInterface = ShareFactory.getShareProvider(shareInfo);
            if (shareInterface != null) {
                shareInterface.share(getActivity());
            }
        }
    }

    /**
     * Base64 인코딩된 공유될 url
     * @param url
     * @return
     */
    public String getBase64Encode(String url){
        return Base64.encodeToString(url.getBytes(), Base64.NO_WRAP);
    }

    /**
     * 방문횟수별 웹컴메시지를 가져온다.
     */
    private void getWelcomeMessage() {
        User user = User.getCachedUser();
        if (isEmpty(user) || user.customerNumber.length() < 2) {
            return;
        }

        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                MobileLiveResult result = null;
                //WebView 쿠키를 RestClient에 복사
                CookieUtils.syncWebViewCookiesToRestClient(getContext(), restClient);
                String apiUrl = ((MobileLiveChatPlayActivity) getContext()).getApiUrl();
                result = RestClientUtils.INSTANCE.get(restClient, apiUrl, MobileLiveResult.class);
                if (isNotEmpty(result) && isNotEmpty(result.message)) {
                    setWelcomeMessage(result.message);
                }
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

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
            setMoreViewDialog(result.mobileLivePrdsInfoList);
        }
    }

    /**
     * 웰컴메시지를 노출한다.
     *
     * @param message String
     */
    private void setWelcomeMessage(String message) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            txtWelcomeMsg.setText(MaskingUtil.maskLoginIdForShoppyLive(User.getCachedUser().loginId, true) + message);
            ViewUtils.showViews(txtWelcomeMsg);
            ThreadUtils.INSTANCE.runInUiThread(() -> ViewUtils.hideViews(txtWelcomeMsg), 3000);
        });
    }
}
