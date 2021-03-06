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
 * ?????????????????? ???????????????
 */
public class ExoMobileLiveMediaPlayerControllerFragment extends BaseFragment
        implements OnMediaPlayerController, OnCspChatListener, KeyboardHeightObserver {

    private static final String TAG = "MobileLiveFragment";
    public static final String MOBILE_LIVE_PLAYER_CALLER = "MOBILE_LIVE_PLAYER"; //???????????? ????????? ?????? ????????? ???????????? or ??????????????? ????????? ?????? ?????? ?????? ?????? ????????? ?????????

    /**
     * ????????????
     */
    public enum ScreenState {
        //CHAT_VIEW,      //???????????? ??????
        //PRODUCT_VIEW,   //?????????????????? ??????
        CHAT_ERROR,      //???????????? ???????????? ??????

        VISIBLE_CHAT,
        INVISIBLE_CHAT
    }
    ScreenState screenState = ScreenState.VISIBLE_CHAT; //???????????? VISIBLE, configData.CO.CHAT ??? N????????? INVISIBLE

    /**
     * ????????? ?????? ??????
     */
    private boolean isKeyboardShow = false;

    /**
     * The keyboard height provider
     */
    private KeyboardHeightProvider keyboardHeightProvider;

    /**
     * ???????????? divider width (dp)
     */
    private static final int VIEPAGER_PRODUCT_DIVIDER_WIDTH = 12;

    /**
     * ???????????? ??????????????? width (dp)
     */
    private static final int VIEPAGER_PRODUCT_PREVIEW_WIDTH = 37;

    /**
     * ????????? ??????????????? height
     */
    private static final int FADE_LENGTH = 25;

    /**
     * ??????????????? ???????????? ???????????? ???????????? (ms)
     */
    private static final int NAVI_SHOW_TIME = 2000;

    /**
     * ???????????? ????????? ?????? ??????
     */
    private boolean isProductEmpty = false;

    /**
     * ?????? ??????
     */
    private float currentVolume;

    /**
     * ????????? ?????? ???????????????
     */
    private OnMediaPlayerListener callback;

    /**
     * ??????????????????
     */
    private ProgressBar loadingProgress;

    /**
     * ????????? ??????
     */
    private MediaInfo mediaInfo;

    /**
     * ???????????? ?????????
     * ??????, ????????? ??????
     */
    private StaticChatCsp mChat = null;

    // exo player
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    /**
     * ????????? ????????? URL
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
     * ??????????????? ??????
     */
    private RecyclerView chatRecycler;
    private ChatAdapter chatAdapter;

    /**
     * for tap event
     */
    private View tapView;

    /**
     * ????????? ?????? ??????
     */
    private int chatRecyclerMaxHeight;

    /**
     * ????????????
     */
    private SnsV2DialogFragment snsDialog;
    private final String KAKAOTALK = "kakaotalk";
    private final String KAKAOSTORY = "kakaostory";
    private final String LINE = "line";
    private final String FACEBOOK = "facebook";
    private final String TWITTER = "twitter";
    private final String ETC = "etc";

    private final String OGIMG = "&ogImage="; //appRedirect?????? ogImg??????????????? ????????? ??????????????? ???????????? ????????? ???????????????
    private String mShareUrl; //??????????????? ????????? url
    private String mPromotionName; //????????? ???????????????
    private String mImgUrl; //????????? ?????????url
    private String mNoticeMoreUrl; //???????????? ????????? ??????

    /**
     * UI ????????? ??????
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
     * ????????? ?????? ???????????? 2????????? ???????????? ?????? ?????????????????? ????????? ??????
     */
//    private final static int REOPEN_MESSAGE = 1;
    private final static int REOPEN_DELAY_TIME = 2000;

    /**
     * ????????? ?????? ???????????? ?????????????????? ??????
     */
    private boolean isChatListNewVisible = false;

    /**
     * ???????????? ?????????
     */
    private IndicatorRecyclerView indicatorRecycler;
    private PreviewRecyclerViewPager prdViewPager;
    private int currentPrdIndex = 0;

    /**
     * ?????? ????????? ????????? ?????? ?????? ??????
     */
    private static final long TEXT_ROLLING_OFFSET_PERIOD = 2000;	//????????? ?????? ??????????????? ?????? ??????(2???)
    private static final long TEXT_ROLLING_INTERVAL = 3000;	//?????? ?????? ??????(3???)
    private Timer notiTimer = null;
    private String[] notiText;	//?????? ??????
    private int notiIdx = 0;	//?????? ????????? ?????????
    private int notiCnt = 0;	//?????? ??? ??????
    private TextSwitcher notiSwitcher;
    private ViewSwitcher.ViewFactory notiFactory;

    /**
     * ?????? ?????? ?????? onair/????????? ?????? ?????? ????????? ????????? ( ?????? ?????? ????????? ) X ??????
     */
    public MobileLiveTimeLayout layoutMobileLiveTime;
    private TextView txtRemainTime;//onair_endtime_current
    private TextView nowPeople;//people_text
    private View layPeople;//people icon
    private Button backButton;//right_back_button_layout
    private Button shareButton; //share_button
    private CheckBox chkMute;   //????????? ??????
    private LinearLayout lay_check_mute;
    private ImageView alarmOn; //???????????? On
    private ImageView alarmOff; //???????????? Off
    private View layAttend; //??????????????? ????????????

    /**
     * mAfterShowChat
     */
    private Handler mAfterShowChat;
    private Runnable mAfterShowChatRunnable;

    /**
     * ???????????? ??? ?????? ????????? ????????? ????????? ??????.
     * ?????? ?????? ??? ????????? ?????? ????????? UI ????????? ?????? ?????????
     */
    private static final long TAP_DISMISS_TIME = 3000;

    @Inject
    protected RestClient restClient;

    /**
     * ?????????
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

        //??????!!
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
         * ?????? ??????
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
                    //????????????
                    //todo ??????????????? ????????? ??????. ?????????????????? ????????? ????????? ??????????????? ?????? ?????? callback null??????. ????????????.
                    //????????? ??????????????? ???????????? WebUtils.goWeb(getContext(), gatePageUrl); ?????? ????????? ???????????? ?????? ?????? ?????????
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

        alarmOn.setContentDescription(getString(R.string.shpping_live_alarm_btn_off) + "?????? ??????");
        alarmOff.setContentDescription(getString(R.string.shpping_live_alarm_btn_off) + "??????");

        // X ???????????? ???????????? ??????
        View rightBackLayout = rootView.findViewById(R.id.right_back_layout);
        ViewGroup.LayoutParams params = rightBackLayout.getLayoutParams();
        rightBackLayout.setTouchDelegate(new TouchDelegate(new Rect(0,0,params.width,params.height),rootView.findViewById(R.id.right_back_button)));

        // ???????????? ?????? ???????????? ??????
        View shareLayout = rootView.findViewById(R.id.lay_share);
        ViewGroup.LayoutParams paramsShare = shareLayout.getLayoutParams();
        shareLayout.setTouchDelegate(new TouchDelegate(new Rect(0,0,paramsShare.width,paramsShare.height),rootView.findViewById(R.id.lay_share)));

        //????????? ??????
        chkMute.setChecked(MainApplication.isMute);
        chkMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //????????? O -> ????????? X
                if(MainApplication.isMute){
                    MainApplication.isMute = false;
                    chkMute.setChecked(MainApplication.isMute);
                    setMute(MainApplication.isMute);
                    EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
                }else{
                    //????????? X -> ????????? O
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
                //SNS ?????? ??????
                snsDialog = new SnsV2DialogFragment();
                snsDialog.setSnsListener(snsListener);
                snsDialog.show(getChildFragmentManager(), ExoMobileLiveMediaPlayerControllerFragment.class.getSimpleName());

                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_SHARE_BUTTON);
            }
        });

        /**
         * ?????? UI
         */
        indicatorRecycler = (IndicatorRecyclerView) view.findViewById(R.id.recycler_indicator);
        prdViewPager = view.findViewById(R.id.recycler_view_pager);

        playerView = view.findViewById(R.id.exoplayer_video_view);
        playerView.setUseController(false);

        loadingProgress = view.findViewById(R.id.mc_webview_progress_bar);

        chatRecycler = view.findViewById(R.id.chatRecycler);
        chatRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER); // ????????? ???????????? ????????? ?????? ????????? ????????? ?????? ??????
        chatRecycler.setVerticalFadingEdgeEnabled(true); //???????????? ????????? ?????? ????????? ???????????? ?????? ??????????????? ??????
        chatRecycler.setFadingEdgeLength(FADE_LENGTH);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        ((LinearLayoutManager)layoutManager).setStackFromEnd(true);
        chatRecycler.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(getActivity());
        chatRecycler.setAdapter(chatAdapter);

        //????????? ???????????? ????????? ?????? ??? tapview??? ?????????????????? ???????????? ????????? ?????????
        view.findViewById(R.id.dummy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //??? ????????? ??????????????? ????????? ?????? ??????
        ClickUtils.work(TAP_DISMISS_TIME);

        //????????? ?????????
        tapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??? ????????? ??????????????? ????????? ?????? ??????
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
                    // ????????? ??????
                    User user = User.getCachedUser();
                    if (user == null || user.customerNumber.length() < 2) {
                        //????????? activity ??????.
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

        //????????? ????????? ?????? mseq??????
        ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(MOBILE_LIVE_ENTER);
    }

    /**
     * UI ????????? ??????
     */
    private void initializeUI() {
        //????????? ??????
        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());
        rootView.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });
        //The height is fixed and the width is increased or decreased to obtain the desired aspect ratio.
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);

        //??????????????? API ??????
        ThreadUtils.INSTANCE.runInUiThread(() -> getWelcomeMessage(), 3000);
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        int marginBottom;
        if (height >= 1) {
            //????????? ??????
            isKeyboardShow = true;
            chatMsgLayout.setVisibility(View.VISIBLE);
            marginBottom = (int) getResources().getDimension(R.dimen.mobile_live_right_menu_margin_bottom_key_show);
        } else {
            //????????? ?????????
            isKeyboardShow = false;
            chatMsgLayout.setVisibility(View.GONE);
            marginBottom = (int) getResources().getDimension(R.dimen.mobile_live_right_menu_margin_bottom_key_hide);
        }
        //????????? ????????? ???????????? ?????? (?????????????????? ????????????)
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lay_alarm_share.getLayoutParams();
        params.bottomMargin = marginBottom;
        lay_alarm_share.setLayoutParams(params);
    }

    /**
     * ????????? ?????? ?????? ????????? ?????????.
     * @param result
     */
    public void setBroadCast(MobileLiveResult result)
    {
        //??????????????? ????????? ????????????
        if(DisplayUtils.isValidString(result.shareUrl) && DisplayUtils.isValidString(result.imgUrl)){
            //??????????????? ????????? url ?????? (appRedirect + ??????????????? ?????????????????? ????????? url + ogImg)
            mShareUrl = ServerUrls.WEB.APPREDIRECT + getBase64Encode(result.shareUrl) + OGIMG + result.imgUrl;
            mPromotionName = result.promotionName;
            mImgUrl = result.imgUrl;
        }

        //???????????? ??????
        ViewUtils.showViews(layoutMobileLiveTime, layPeople, chkMute);

        //????????? ????????? ?????? ??????
        if (result != null && !TextUtils.isEmpty(result.gatePageUrl)) {
            gatePageUrl = result.gatePageUrl;
        }

        //???????????? ????????? ??????
        if (result != null && !TextUtils.isEmpty(result.notiMoreLinkUrl)) {
            mNoticeMoreUrl = result.notiMoreLinkUrl;
        }

        String endTime = "";
        if (result != null && !TextUtils.isEmpty(result.endDate) && !"0".equals(result.endDate)) {
            //????????????(?????????) ??????
            if (getResources().getString(R.string.home_tv_live_view_replay_air).equalsIgnoreCase(result.broadType) ||
                    getResources().getString(R.string.home_tv_live_view_replay_air_eng).equalsIgnoreCase(result.broadType)) {
                ViewUtils.hideViews(mobilelive_onair_badge);
                //??????????????? ??????, ????????? ?????? ??????
                int padding = getResources().getDimensionPixelOffset(R.dimen.mobilelive_replay_remain_time_padding_horizontal);
                txtRemainTime.setPadding(padding, 0, padding, 0);
            }

            layoutMobileLiveTime.setDisplayType(TimeRemaining.DisplayType.MINUTE_MM); //default mm:ss

            //????????????????????? 60??? ????????? ??????
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

        //???????????? ???????????? ??????
        if("Y".equals(result.alarmYn)){
            alarmOn.setVisibility(View.VISIBLE);
            alarmOff.setVisibility(View.GONE);
        }else{
            alarmOn.setVisibility(View.GONE);
            alarmOff.setVisibility(View.VISIBLE);
        }

        //???????????? ??????
        lay_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ????????? ??????
                User user = User.getCachedUser();
                if (user == null || user.customerNumber.length() < 2) {
                    //????????? activity ??????.
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
                        //???????????? ????????? ????????? ????????? ?????????????????? api??????
                        MLAlarm.deleteQuery(getContext(), MOBILE_LIVE_PLAYER_CALLER);
                    } else {
                        //???????????? ????????? ????????? ????????? ?????????????????? api??????
                        MLAlarm.addQuery(getContext(), MOBILE_LIVE_PLAYER_CALLER);
                    }
                }

                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_ALARM);
            }
        });

        //?????? ?????????
        ViewUtils.hideViews(layAttend);
        if (isNotEmpty(result.attendanceUrl)) {
            ViewUtils.showViews(layAttend);
            layAttend.setOnClickListener(v -> {
                // ????????? ??????
                User user = User.getCachedUser();
                if (user == null || user.customerNumber.length() < 2) {
                    //????????? activity ??????.
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

        // ?????? ?????? ??????
        if (isNotEmpty(result.couponPopupUrl)) {
            try {
                // ????????? ?????? ????????? ????????? ?????? ?????? ???????????? ???????????? ?????? ???????????? ?????????.
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
     * ????????? ???????????? ????????? ???????????? ????????????.
     */
    private void stopTvLiveTimer() {
        layoutMobileLiveTime.stopTimer();
    }
    /**
     * ?????????????????? ????????????.
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

        //????????? ??????
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
        //???????????? ???????????? ?????? ??????????????? ?????? ???????????? ????????? ????????? ????????? ?????? ??????
        String replacedMsg = msg.replace("\n\n", " ")
                .replace("\n", " ");
        mChat.messageSend(replacedMsg);
    }

    /**
     * ?????? ??? ?????????
     */
    private void onTap() {
        //????????? ?????? ??????????????? ????????? ?????????
        if (isKeyboardShow) {
            hideKeyboard(txtChatMsg);
            return;
        }
        //??????????????? ???????????? ?????? ?????????
        if(ScreenState.VISIBLE_CHAT.equals(screenState)){
            if(chatMsg.getVisibility() == View.VISIBLE || chatProduct.getVisibility() == View.VISIBLE){
                ViewUtils.hideViews(chatMsg, chatList, chatProduct, dimBottom, lay_alarm_share);
                setNotiLong();
            }else{
                setNotiShort(); //???????????? wrap_content

                //????????? ???????????? ????????? chatList???????????????
                if(chatAdapter.getItemCount() > 0){
                    ViewUtils.showViews(chatMsg, chatList, dimBottom, lay_alarm_share);
                }else{
                    ViewUtils.showViews(chatMsg, lay_alarm_share);
                }

                //????????? ????????? ??????
                if (!isProductEmpty) {
                    ViewUtils.showViews(chatProduct);
                }

                //??????????????? ?????? ??? ?????????
                try {
                    if (chatAdapter.getItemCount() > 0) {
                        chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                } catch (IllegalArgumentException e) {
                    //????????? ?????? ????????? ???????????? ??????  (java.lang.IllegalArgumentException: Invalid target position)
                    Ln.e(e);
                }
            }

        }else{
            setNotiLong(); //???????????? match_parent

            //?????????????????? ??????(????????????)?????? ?????? ?????????
            if(chatProduct.getVisibility() == View.VISIBLE){
                if (!isProductEmpty) {
                    ViewUtils.hideViews(chatProduct);
                }
            }else{
                //????????? ????????? ??????
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
        //pause ???????????? ????????? ??????
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

        //??????????????? ??????
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

        //??????????????? ??????
        startRollingTextTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mChat != null) {
            mChat.globalSocketOff();
        }

        //????????? ?????? ????????? ???????????? ??????  (java.lang.NullPointerException)
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

        //????????? ?????? ????????? ?????????
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
     * ???????????? ????????????.
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
                    //?????? ?????? ?????????
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
     * ???????????? ????????????.
     */
    @Override
    public void playPlayer() {
        if (isEmpty(mediaInfo)) {
            throw new IllegalArgumentException("The media info is null!!");
        }

        if (!TextUtils.isEmpty(mediaInfo.contentUri)) {
            if( !this.isPlaying() ) {
                initializePlayer(mediaInfo.contentUri);
                initChatCsp(mediaInfo.liveNo); // mediaInfo && mediaInfo.liveNo ?????? ????????????
            }

        } else {
            //?????????????????? ?????? ??????
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
     * ????????? ????????? ????????????.
     *
     * @param on if true, ?????????
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
            //?????? ??????
            //ex)
            //initUI();
            //pauseImage.setVisibility(View.GONE);
            //playImage.setVisibility(View.VISIBLE);
            if (callback != null) {
                callback.onError(null);
            }

            // TODO: 2019. 2. 8. mslee
            // ???????????? ?????????
            //playImage.setEnabled(false);

            // TODO: 2019. 2. 8. mslee
            //??????????????? ?????? ???????????? ??????
            if (mediaInfo.playerMode == MediaInfo.MODE_FULL) {
                //?????? ?????? ????????? ???????????? ?????? ?????? ????????????
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
     * ??????????????? ?????? ??????
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
                     * ????????? ???????????? ?????? ????????? ??????(????????? ?????? 08/14)
                     */
                    chatAdapter.add(chatData);

                    if (isChatListNewVisible) {
                        //??????????????? ????????? ????????? ?????? -> ?????? ????????? ????????? ???????????? ????????? ????????? ?????? ??? ????????? ?????????
                        User user = User.getCachedUser();
                        if(user != null && user.customerNumber.equals(chatData.UO.U)){
                            chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                            chatListNewText.setText(chatData.MO.MG);
                        }
                        else {
                            if ("N".equals(chatData.MO.VI)) {
                                // ???????????????, ?????? ????????? ??????
                            } else {
                                chatListNewText.setText(chatData.MO.MG);
                            }
                        }
                        ViewUtils.showViews(chatListNewLayout);
                    } else {
                        chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                    }
                }

                //???????????? ?????? ????????? ???????????? ????????? ??????????????? ???????????? ?????? ??????
                if(lay_alarm_share.getVisibility() == View.VISIBLE) {
                    setNotiShort(); //???????????? ??????????????? ??????????????? wrap_content
                }
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

    /**
     * ??????????????? ?????? ??????
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
                //????????? ??????????????????
                MobileLiveData.ChatDataList chatDataList = new Gson().fromJson(data.toString(), MobileLiveData.ChatDataList.class);

                if (chatDataList != null && isNotEmpty(chatDataList.CA)) {
                    chatRecyclerMaxHeight = getResources().getDimensionPixelSize(R.dimen.chatRecyclerMaxHeight);

                    //????????? ???????????? ??????????????? ??????????????? || ????????? ?????????????????? ????????? 6???????????? -> ????????? ??????
                    if(chatRecycler.getHeight() > chatRecyclerMaxHeight || chatDataList.CA.size() > 5){
                        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, chatRecyclerMaxHeight);
                        chatRecycler.setLayoutParams(param);
                    }

                    /**
                     * 0508 ???????????? ?????? ????????? ????????? ?????? ????????? setinfo??? ????????? addList ????????? ???????????????
                     * ????????? ?????? 10000??? ???????????? ?????? ???????????? ???????????? ???????????? ?????????
                     * ???????????? ????????????
                     */


                    //?????? ???????????? ????????? ?????????????? ???????????? ??? ????????? ?????????. ?????? ???????????? ?????????... ??????????????? ????????????
                    //???????????? ??????????????? ??????????????? wrap_content ??????????????? ?????? ??????

                    chatAdapter.setInfo(chatDataList.CA);
                    chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }

                setNotiShort(); //???????????? ??????????????? ??????????????? wrap_content
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

    /**
     * ???????????? ?????? ??????
     *
     * @param data ???????????????
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
                nowPeople.setContentDescription("????????? ???, ??? " + people + "???");
            }
        });
    }
    /**
     * ???????????? ????????? ?????? ?????? ( ????????? ?????? ???
     *
     * @param data
     */
    @Override
    public void onStated(JSONObject data) {
        if (data != null) {
            Log.i("CHATCSP", "onStated " + data.toString());
            try {
                //???????????? ???????????????
                MobileLiveData.StateData temp = new Gson().fromJson(data.toString(), MobileLiveData.StateData.class);
                if (temp != null) {
                    if (temp.SO != null) {
                        if (temp.SO.PV != null) {
                            //?????? ????????? ?????????
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
     * CSP??? Config ????????? ????????? ????????? ?????? ????????? ??????
     * ex) ??????????????????, ????????????????????? -> configData.CHAT, Y or N
     *
     * @param data configData.CHAT -> Y, N NULL?????? ????????? ??? ????????? ????????? ?????????
     *             ????????? ???????????? ????????? ????????? setPrdList(null, null)
     */
    @Override
    public void onReceivedConfig(JSONObject data) {
        if (data == null) {
            return;
        }
        try {
            MobileLiveData.ConfigData configData = new Gson().fromJson(data.toString(), MobileLiveData.ConfigData.class);
            if (configData != null) {
                //?????? ???????????? ??????????
                if (configData.CO != null) {
                    if(mAfterShowChat!=null){
//                        if(mAfterShowChat.hasMessages(REOPEN_MESSAGE)){
//                            mAfterShowChat.removeMessages(REOPEN_MESSAGE);
//                        }
                        mAfterShowChat.removeCallbacks(mAfterShowChatRunnable);
                    }
                    if ("N".equals(configData.CO.CHAT)) {
                        screenState = ScreenState.INVISIBLE_CHAT;
                        setConfigN(); // N?????? UI ??????
                        setNotiLong(); // ???????????? match_parent

                    } else if ("Y".equals(configData.CO.CHAT)) {
                        screenState = ScreenState.VISIBLE_CHAT;
                        setConfigY(); // Y?????? UI ??????
                        setNotiShort(); // ???????????? wrap_content
                    }
                }

                //... Config ??? ?????????????????? ?????? ????????????
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }


    /**
     * Config N?????? UI??????
     */
    private void setConfigN() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            ViewUtils.hideViews(chatMsg, chatList, dimBottom, lay_alarm_share);
        });
    }

    /**
     * Config Y?????? UI??????
     */
    private void setConfigY() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {

            if (chatProduct.getVisibility() == View.GONE) {

            } else {
                //????????? ???????????? ????????? chatList???????????????
                if (chatAdapter.getItemCount() > 0) {
                    ViewUtils.showViews(chatMsg, chatList, dimBottom, lay_alarm_share);
                } else {
                    ViewUtils.showViews(chatMsg, lay_alarm_share);
                }
            }
        });
    }

    /**
     * ???????????? ????????? ???????????????.
     *
     * @param notiData NotiData
     */
    private void initializeNotiMessage(MobileLiveData.NotiData notiData) {
        if (!isEmpty(notiData) && notiData.MA != null && notiData.MA.size() > 0) {
            notiCnt = notiData.MA.size();
            notiText = notiData.MA.toArray(new String[notiCnt]);

            if (notiCnt > 1) {
                //????????? ??????
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

            //??????????????? ??????????????? ????????? ?????? ?????? ????????? ???????????? ????????? ?????? ??????
            if (notiTimer == null) {
                setNotiRollingText();
            }

            //???????????? ??????
            ViewUtils.showViews(chatNoti);
            ViewUtils.hideViews(chatNotiTopMargin);

            startRollingTextTimer();
        } else {
            //???????????? ?????????
            ViewUtils.hideViews(chatNoti);
            ViewUtils.showViews(chatNotiTopMargin);
        }
    }

    /**
     * ???????????? ????????? ?????? ???????????? ????????????.
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
     * ??????????????? ???????????????.
     */
    private void setNotiRollingText() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            notiIdx = notiIdx < notiCnt ? notiIdx : 0;
            if (notiCnt > 1) {
                //????????? ????????? textview??? ???????????? ???????????? ?????????????????? ??????????????? textview??? ????????????.
                notiSwitcher.setText(notiText[notiIdx]);
            } else {
                //?????? ????????? textview??? ???????????? ????????????. ?????? ?????????????????? ???????????? ?????????.
                notiSwitcher.setCurrentText(notiText[notiIdx]);
            }
            notiIdx++;
        });
    }

    /**
     * ???????????? ????????? ?????? ???????????? ????????????.
     */
    private void stopRollingTextTimer() {
        if (notiTimer != null) {
            notiTimer.cancel();
            // ??????????????? ????????? ????????? ?????? ?????? ?????? ??????
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
     * ????????? ??????????????? ?????? Switcher width??? ????????????.
     *
     * @param isMore ????????? ???????????? ?????? true
     */
    public void setNotiSwitcher(boolean isMore){
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) notiSwitcher.getLayoutParams();
        lp.setMargins(0, 0, DisplayUtils.convertDpToPx(getContext(), isMore ? 72 : 12), 0);
        notiSwitcher.setLayoutParams(lp);
    }

    //???????????? width match_parent
    public void setNotiLong(){
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chatNoti.getLayoutParams();
            params.setMargins(setNotiMargin(12), 0,setNotiMargin(12), setNotiMargin(17));
            chatNoti.setLayoutParams(params);
        });
    }

    //???????????? width wrap_content
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
     * ????????? ?????? ??? ?????????????????? ????????????.
     *
     * @param event Events.MobileLiveAfterLoginEvent
     */
    public void onEvent(Events.MobileLiveAfterLoginEvent event) {
        getWelcomeMessage();
    }

    /**
     * ?????????????????? ???????????? UI????????????
     * @param event
     */
    public void onEvent(Events.AlarmUpdatetMLEvent event) {
        //?????????????????? ????????? ??????????????? if ???????????? ???????????????
        try {
            //???????????? ?????? ????????? UI????????????
            if (Events.AlarmUpdatetMLEvent.MAKE_ALARM_CANCEL.equals(event.eventType)) {
                alarmOn.setVisibility(View.GONE);
                alarmOff.setVisibility(View.VISIBLE);
            }
            //???????????? ?????? ????????? UI????????????
            else if (Events.AlarmUpdatetMLEvent.MAKE_ALARM_OK.equals(event.eventType)) {
                alarmOn.setVisibility(View.VISIBLE);
                alarmOff.setVisibility(View.GONE);
            }
        } catch (Exception e){
            Ln.e(e);
        }
    }

    /**
     * ?????????????????? ???????????? ??????/?????? ??????????????? ?????????
     */
    public void onEvent(Events.AlarmRegistMLEvent event) {
        try {
            //?????????????????? ???????????? FlexibleFragment????????? ????????????
            if(MOBILE_LIVE_PLAYER_CALLER.equals(event.caller)){
                //?????? ?????? ?????????
                if(MobileLiveAlarmDialogFragment.MOBILELIVE_ADD.equals(event.type)){
                    MLAlarm.add(getContext(), event.isNightAlarm, event.caller);
                }
                //?????? ?????? ?????????
                else if(MobileLiveAlarmCancelDialogFragment.MOBILELIVE_DELETE.equals(event.type)){
                    MLAlarm.delete(getContext(), event.caller);
                }
            }
        }catch (Exception e){
            Ln.e(e);
        }

    }

    /**
     * ???????????? ???????????? SNS ?????? ??????
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
     * SMS ????????????
     * @param shareType ????????????
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
     * URL?????? ????????????
     * @param shareType ????????????
     */
    private void onSnsSelectedUrl(SnsV2DialogFragment.SHARE_TYPE shareType) {
        try {
            if(DisplayUtils.isValidString(mShareUrl)){
                new AndroidBridge(getContext()).sendClipData(mShareUrl);
            }
        } catch (Exception e) {
            // 10/19 ????????? ??????
            // - ????????? ????????? ?????? ?????? ????????? ???????????? ???????????? ??????
            // - ???????????? ????????????(Exception e) ?????? : ???????????? ?????? ???????????? ???????????? ?????? ??????????????? ????????? ????????? ??????
            // - ??????????????? ????????? ?????? ????????? ????????????, ?????????????????? ?????? ???????????? ????????? ??????????????? ???????????? ?????? ??????
            Ln.e(e);
        }
    }


    /**
     * ????????????, ??????????????????, ?????????, ??????, ????????????, ?????? ????????????
     * @param shareType ????????????
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

            //????????? ????????????
            shareInfo.setLink(mShareUrl);
            shareInfo.setSubject(mPromotionName); //?????????????????????????????? ????????? ??????(????????? ????????? ??????)
            shareInfo.setMessage(mPromotionName);
            shareInfo.setImageurl(mImgUrl);
            shareInfo.setShareImageType(ShareInfo.ShareImageType.TYPE_DEFAULT);//???????????????????????? ????????? ????????? ??????(????????? ?????????)

            ShareInterface shareInterface = ShareFactory.getShareProvider(shareInfo);
            if (shareInterface != null) {
                shareInterface.share(getActivity());
            }
        }
    }

    /**
     * Base64 ???????????? ????????? url
     * @param url
     * @return
     */
    public String getBase64Encode(String url){
        return Base64.encodeToString(url.getBytes(), Base64.NO_WRAP);
    }

    /**
     * ??????????????? ?????????????????? ????????????.
     */
    private void getWelcomeMessage() {
        User user = User.getCachedUser();
        if (isEmpty(user) || user.customerNumber.length() < 2) {
            return;
        }

        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                MobileLiveResult result = null;
                //WebView ????????? RestClient??? ??????
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

        //onPreExecute doInbackground ?????? ?????? ?????? ?????????
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

        //doInBackground ?????? ??????????????? ?????? ??????
        @Override
        protected MobileLiveResult process() throws Exception {
            return restClient.getForObject(url, MobileLiveResult.class);
        }

        //postExecute background ????????? ???????????? ???????????? ???????????? ?????????
        @Override
        protected void onSuccess(final MobileLiveResult result) throws Exception {
            setMoreViewDialog(result.mobileLivePrdsInfoList);
        }
    }

    /**
     * ?????????????????? ????????????.
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
