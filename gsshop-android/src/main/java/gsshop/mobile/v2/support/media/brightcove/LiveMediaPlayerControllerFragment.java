package gsshop.mobile.v2.support.media.brightcove;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.brightcove.player.appcompat.BrightcovePlayerFragment;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.mediacontroller.ShowHideController;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.SoundUtils;
import roboguice.util.Ln;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.brightcove.player.event.EventType.DID_PAUSE;

/**
 * 라이브 스트리밍, VOD 스트리밍, MP4 재생용 프레그먼트
 * -라이브 스트리밍 : 시크바 비활성화, 재생진행시간 대신 "실시간" 표시
 * -VOD 스트리밍, MP4 : 시크바 활성화, 재생진행시간 표시
 * <p>
 * <p>
 * 참고
 * -brightcove 컨트롤러는 hls인 경우 seekbar를 노출하지 않기 때문에 seekbar를 별도로 만들었음
 */
@SuppressLint("NewApi")
public class LiveMediaPlayerControllerFragment extends BrightcovePlayerFragment implements OnMediaPlayerController {

    /**
     * 액티비티로부터 전달받는 파라미터 정의
     */
    protected static final String ARG_PARAM_VIDEO_ID = "_arg_param_video_id";   //비디오번호
    protected static final String ARG_PARAM_START_TIME = "_arg_param_start_time";     // 비디오 시작 시간 밀리세컨드
    protected static final String ARG_PARAM_IS_PLAYING = "_arg_param_is_playing";     // 실행 유무
    protected static final String ARG_PARAM_ORIENTATION = "_arg_param_orientation";     // 가로, 세로 종류

    protected OnMediaPlayerListener mListener;

    protected MediaInfo mediaInfo;

    //로딩바
    private ProgressBar loadingProgress;

    // 재생,정지 버튼
    protected CheckBox chkPlay;

    // 사운드 버튼
    protected CheckBox chkMute;

    private View mViewBtnArea;
    // 플레이 버튼
    protected ImageView mPlayButton;
    // 일시 정지 버튼
    protected View mPauseButton;
    // 다시 재생 버튼
    protected View mReplayButton;

    // 닫기 버튼
    private View btnClose;
    private View outView;

    private View mobileDataView;
    private Button mobileDataCancelButton;


    protected BrightcoveMediaController controller;
    protected EventEmitter eventEmitter;

    /**
     * 화면 가로/세로 타입
     */
    protected int orientation;

    /**
     * 불필요한 이벤트 전송을 막기 위한 플래그
     */
    private boolean skipPlayerActionEvent = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static Fragment newInstance(String videoId, int startTime, boolean isPlaying, int orientation) {
        LiveMediaPlayerControllerFragment fragment = new LiveMediaPlayerControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_VIDEO_ID, videoId);
        args.putInt(ARG_PARAM_START_TIME, startTime);
        args.putBoolean(ARG_PARAM_IS_PLAYING, isPlaying);
        args.putInt(ARG_PARAM_ORIENTATION, orientation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaInfo = new MediaInfo();
        if (getArguments() != null) {
            mediaInfo.videoId = getArguments().getString(ARG_PARAM_VIDEO_ID);
            mediaInfo.currentPosition = getArguments().getInt(ARG_PARAM_START_TIME, 0);
            mediaInfo.isPlaying = getArguments().getBoolean(ARG_PARAM_IS_PLAYING, false);
            orientation = getArguments().getInt(ARG_PARAM_ORIENTATION, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_live_media_player_controller, container, false);

        baseVideoView = (BaseVideoView) result.findViewById(R.id.brightcove_video_view);
        if(orientation == SCREEN_ORIENTATION_PORTRAIT) {
            baseVideoView.setMediaController(new BrightcoveMediaController(baseVideoView, R.layout.brightcove_media_controller));
        } else {
            baseVideoView.setMediaController(new BrightcoveMediaController(baseVideoView, R.layout.brightcove_media_controller_land));
        }

        controller = baseVideoView.getBrightcoveMediaController();

        controller.setShowHideTimeout(1000);
        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        eventEmitter = baseVideoView.getEventEmitter();

        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingProgress = view.findViewById(R.id.mc_webview_progress_bar);
        chkPlay = view.findViewById(R.id.chk_play);
        chkPlay.setVisibility(View.GONE);

        mViewBtnArea = view.findViewById(R.id.view_player_controller_area);
        /**
         * controller buttons
         */
        mPlayButton = view.findViewById(R.id.image_vod_controller_play);
        mPauseButton = view.findViewById(R.id.image_vod_controller_pause);
        mReplayButton = view.findViewById(R.id.image_vod_controller_replay);

        mPlayButton.setContentDescription(getString(R.string.common_play));
        mPauseButton.setContentDescription(getString(R.string.common_pause));
        mReplayButton.setContentDescription(getString(R.string.common_restart));

        mPlayButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.GONE);
        mReplayButton.setVisibility(View.GONE);

        chkMute = view.findViewById(R.id.chk_mute);
        btnClose = view.findViewById(R.id.btn_close);
        outView = view.findViewById(R.id.view_controller_out);

        chkMute.setContentDescription(getString(R.string.common_mute));
        btnClose.setContentDescription(getString(R.string.common_close));
        outView.setContentDescription(getString(R.string.common_full_screen) + getString(R.string.common_cancel));

        mobileDataView = view.findViewById(R.id.view_vod_full_player_mobile_data);
        mobileDataCancelButton = mobileDataView.findViewById(R.id.button_mobile_data_cancel);

        ViewUtils.hideViews(mobileDataView);
        initController();
        initBrightCoveEvent();
        initMuteFromGlobal();

        playPlayer();
    }

    private void initController() {
        chkPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chkPlay.isChecked()) {
                    Ln.i("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration() + ", isPlaying: " + mediaInfo.isPlaying);
                    MainApplication.isAutoPlay = "N";
                    confirmNetworkBilling(getContext());
                } else {
                    mediaInfo.isPlaying = false;
                    baseVideoView.pause();
                    Ln.i("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration() + ", isPlaying: " + mediaInfo.isPlaying);
                }
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.isAutoPlay = "N";
                confirmNetworkBilling(getContext());
            }
        });
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                mediaInfo.isPlaying = false;
                baseVideoView.pause();
                mPlayButton.setVisibility(View.VISIBLE);
                mPauseButton.setVisibility(View.GONE);
                mReplayButton.setVisibility(View.GONE);
                if (isNotEmpty(mListener)) {
                    mListener.sendWiseLog(PlayerAction.PAUSE, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
                Ln.i("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration() + ", isPlaying: " + mediaInfo.isPlaying);
            }
        });
        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaInfo.isPlaying = true;
                mediaInfo.currentPosition = 0;
                baseVideoView.seekTo(0);
                playPlayer();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNotEmpty(mListener)) {
                    mListener.onClosed();
                    mListener.onFinished(mediaInfo);
                    mListener.sendWiseLog(PlayerAction.EXIT, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

        outView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNotEmpty(mListener)) {
                    mListener.onZoomed();
                    mListener.onFinished(mediaInfo);
                    mListener.sendWiseLog(PlayerAction.MINI, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

        //사운드 On/Off
        chkMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.isMute = chkMute.isChecked();
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
                setMute(chkMute.isChecked());
                if (isNotEmpty(mListener)) {
                    mListener.onMute(MainApplication.isMute);
                    PlayerAction playerAction = PlayerAction.SOUND_ON;
                    if (chkMute.isChecked()) {
                        playerAction = PlayerAction.SOUND_OFF;
                    }
                    mListener.sendWiseLog(playerAction, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

        /**
         * mobile data
         */
        mobileDataCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideViews(mobileDataView);
                mPlayButton.setVisibility(View.VISIBLE);
                mPauseButton.setVisibility(View.GONE);
                mReplayButton.setVisibility(View.GONE);
                controller.setShowControllerEnable(true);
                controller.setHideControllerEnable(false);
                controller.show();

                if (isNotEmpty(mListener)) {
                    mListener.sendWiseLog(PlayerAction.LTE_N, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

        mobileDataView.findViewById(R.id.button_mobile_data_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideViews(mobileDataView);
                MainApplication.isNetworkApproved = true;
                mediaInfo.isPlaying = true;
                baseVideoView.start();
                controller.setShowControllerEnable(true);
                controller.show();
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MOBILE_DATA, -1));

                if (isNotEmpty(mListener)) {
                    mListener.sendWiseLog(PlayerAction.LTE_Y, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

    }

    protected void confirmNetworkBilling(Context context) {
        if ((!NetworkStatus.isWifiConnected(context) && !MainApplication.isNetworkApproved)) {
            controller.setShowControllerEnable(false);
            controller.setHideControllerEnable(true);
            controller.hide();
            ViewUtils.showViews(mobileDataView);
        } else {
            mediaInfo.isPlaying = true;
            baseVideoView.start();
        }
    }

    protected Handler mHandlerHide = new Handler();
    protected Runnable mRunnableHide = new Runnable() {
        @Override
        public void run() {
            controller.hide();
        }
    };

    /**
     * bright cove events
     */
    private void initBrightCoveEvent() {
        /**
         * controller button state event
         */
        eventEmitter.on(EventType.DID_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.i("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration() + ", isPlaying: " + mediaInfo.isPlaying);
                //다른 앱 사운드 중지
                if (!MainApplication.isMute) {
                    SoundUtils.requestAudioFocus();
                }
                controller.setHideControllerEnable(true);

                // hide 시간을 iOS와 맞춰서 3초 후에 사라지게 한다.
                mHandlerHide.removeCallbacks(mRunnableHide);
                mHandlerHide.postDelayed(mRunnableHide, 1000);

                initMuteFromGlobal();
                mPlayButton.setVisibility(View.GONE);
                mPauseButton.setVisibility(View.VISIBLE);
                mReplayButton.setVisibility(View.GONE);
                mediaInfo.isPlaying = true;
            }
        });

        eventEmitter.on(EventType.PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (isNotEmpty(mListener)) {
                    mListener.sendWiseLog(PlayerAction.PLAY, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

        eventEmitter.on(DID_PAUSE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.i("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration() + ", isPlaying: " + mediaInfo.isPlaying);
                controller.setHideControllerEnable(false);
                controller.setShowControllerEnable(true);
                controller.show();
            }
        });

        eventEmitter.on(EventType.COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.i("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration() + ", isPlaying: " + mediaInfo.isPlaying);
                setSkipPlayerActionEvent();
                if (isNotEmpty(mListener)) {
                    mListener.sendWiseLog(PlayerAction.PAUSE, baseVideoView.getDuration(), baseVideoView.getDuration());
                }
                mediaInfo.isPlaying = false;
//                baseVideoView.pause();
                mPlayButton.setVisibility(View.GONE);
                mPauseButton.setVisibility(View.GONE);
                mReplayButton.setVisibility(View.VISIBLE);
            }
        });

        eventEmitter.on(EventType.READY_TO_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                playPlayer();
            }
        });

        eventEmitter.on(EventType.VIDEO_DURATION_CHANGED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                initMuteFromGlobal();
                int currentPosition = (int) mediaInfo.currentPosition;
                if (currentPosition > 0) {
                    baseVideoView.seekTo(currentPosition);
                }
            }
        });

        eventEmitter.on(EventType.DID_SEEK_TO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                int seekPosition = (int) event.getProperties().get("seekPosition");
                Ln.i("currentPosition: " + seekPosition + ", isPlaying: " + mediaInfo.isPlaying);
                if (seekPosition > 0 && !mediaInfo.isPlaying) {
                    baseVideoView.pause();
                } else if (mediaInfo.isPlaying) {
                    baseVideoView.start();
                }
            }
        });

        eventEmitter.on(EventType.SET_VIDEO_STILL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                int currentPosition = (int) mediaInfo.currentPosition;
                event.preventDefault();
                event.stopPropagation();
            }
        });

        eventEmitter.on(EventType.PROGRESS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
            }
        });

        eventEmitter.on(ShowHideController.DID_HIDE_MEDIA_CONTROLS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (mHandlerHide != null && mRunnableHide != null) {
                    mHandlerHide.removeCallbacks(mRunnableHide);
                }
                if (isNotEmpty(mListener)) {
                    mListener.onTap(false);
                }
            }
        });

        eventEmitter.on(ShowHideController.DID_SHOW_MEDIA_CONTROLS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (isNotEmpty(mListener)) {
                    mListener.onTap(true);
                }
            }
        });

        eventEmitter.on(ShowHideController.HIDE_MEDIA_CONTROLS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if(!isPlaying()) {
                    eventEmitter.emit(DID_PAUSE);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });
        eventEmitter.on(EventType.ERROR, new EventListener() {
            @Override
            public void processEvent(Event event) {
                //비디오번호가 유효하지 않는 등 에러 발생시
                Map<String, Object> map = event.getProperties();
                String errCode = (String) map.get("error_code");
                if ("RESOURCE_NOT_FOUND".equals(errCode) || "VIDEO_NOT_FOUND".equals(errCode)) {
                    if (mListener != null) {
                        mListener.onError(new Exception());
                    }
                }
            }
        });
    }

    protected void initMuteFromGlobal() {
        chkMute.setChecked(MainApplication.isMute);
        setMute(MainApplication.isMute);
    }

    /**
     * 플래그를 1초후에 초기화한다.
     */
    private void setSkipPlayerActionEvent() {
        skipPlayerActionEvent = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                skipPlayerActionEvent = false;
            }
        }, 1000);
    }

    /**
     * 미디어 정보를 반환한다.
     *
     * @return MediaInfo
     */
    @Override
    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    @Override
    public void setMediaInfo(MediaInfo media) {
        this.mediaInfo = media;
    }

    @Override
    public View getPlayerView() {
        return getView();
    }

    /**
     * 사운드를 재생/뮤트 시킨다.
     *
     * @param on - on 이면 mute
     */
    @Override
    public void setMute(boolean on) {
        // audio
        float volume = on ? 0 : 100;
        Map properties = new HashMap<>();
        properties.put(Event.VOLUME, volume);
        baseVideoView.getEventEmitter().emit(EventType.SET_VOLUME, properties);
        MainApplication.isMute = on;

        //다른 앱 사운드 중지
        if (!MainApplication.isMute && baseVideoView.isPlaying()) {
            SoundUtils.requestAudioFocus();
        }
    }

    /**
     * player state
     *
     * @return boolean
     */
    @Override
    public boolean isPlaying() {
        return baseVideoView.isPlaying();
    }

    @Override
    public void playPlayer() {
        showLoadingProgress(false);
        String videoId = mediaInfo.videoId;
        //videoId가 videoUrl보다 우선순위
        if (!TextUtils.isEmpty(videoId) && videoId.length() > 4) {
            Ln.i("video : " + videoId);
            Catalog catalog = new Catalog(eventEmitter, getString(R.string.account), getString(R.string.policy));
            catalog.findVideoByID(videoId, new VideoListener() {

                // Add the video found to the queue with add().
                // Start playback of the video with start().
                @Override
                public void onVideo(Video video) {
                    Ln.i("onVideo: video = " + video);
                    baseVideoView.clear();
                    baseVideoView.add(video);
                    eventEmitter.emit(DID_PAUSE);
                    ViewUtils.hideViews(mobileDataView);

                }
            });

            //내일 TV에만 동작 해야 하는 Amplitude  이벤트 입니다
            /*
            try {
                Log.e("MSLEE", "FullPlayer");
                JSONObject eventProperties = new JSONObject();
                eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.TOMORROW_TV_FULL_BUTTONPLAY);
                if(mediaInfo != null ) {
                    eventProperties.put(AMPEnum.AMP_VIDEO_ID, mediaInfo.videoId);
                }
                Amplitude.getInstance().logEvent( AMPEnum.AMP_CLICK_TOMORROW_TV, eventProperties);
            } catch (JSONException exception){

            }
            */

        } else {
            Toast.makeText(getActivity(), R.string.video_play_error, Toast.LENGTH_SHORT)
                    .show();
            if (isNotEmpty(mListener)) {
                mListener.onFinished(mediaInfo);
                mListener.onClosed();
            }
        }
    }

    @Override
    public void stopPlayer() {
        mediaInfo.isPlaying = false;
        baseVideoView.pause();
        baseVideoView.stopPlayback();
    }

    /**
     * Internal player
     *
     * @param show show
     */
    private void showLoadingProgress(boolean show) {
        show = false;
        if (show) {
            ViewUtils.showViews(loadingProgress);
        } else {
            ViewUtils.hideViews(loadingProgress);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMediaPlayerListener) {
            this.mListener = (OnMediaPlayerListener) context;
        } else
            throw new ClassCastException(
                    "Parent container must implement the OnMediaPlayerListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
