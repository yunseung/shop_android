package gsshop.mobile.v2.support.media.brightcove;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brightcove.player.appcompat.BrightcovePlayerFragment;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.display.VideoDisplayComponent;
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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.SoundUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.brightcove.player.event.EventType.FRAGMENT_PAUSED;
import static gsshop.mobile.v2.Events.VodShopPlayerEvent.VodPlayerAction.VOD_INIT_OTHERS;
import static gsshop.mobile.v2.home.shop.vod.VodShopFragment.prevPlayingPosition;

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
public class VodMediaPlayerControllerFragment extends BrightcovePlayerFragment implements OnMediaPlayerController {
    /**
     * 액티비티로부터 전달받는 파라미터 정의
     */
    private static final String ARG_PARAM_VIDEO_ID = "_arg_param_video_id";   //비디오번호
    private static final String ARG_PARAM_START_TIME = "_arg_param_start_time";     // 비디오 시작 시간 밀리세컨드

    private OnMediaPlayerListener mListener;

    private MediaInfo mediaInfo;

    //for tap event
    private View tapView;

    // 사운드 버튼
    private CheckBox chkMute;

    private View fullScreenView;

    private ImageView playButton;
    private View pauseButton;
    private View replayButton;
    private TextView remainedTimeText;
    private View bottomView;
    private View mobileDataView;
    private Button mobileDataCancelButton;
    private Button mobileDataOkButton;

    private LinearLayout goPrdLayout;

    private BrightcoveMediaController controller;
    private EventEmitter eventEmitter;

    private String currentVideoId;
    private ImageView close_vod;

    /**
     * 불필요한 이벤트 전송을 막기 위한 플래그
     */
    private boolean skipPlayerActionEvent = false;

    /**
     * 해당 영상 재생 완료시 새로 버퍼링 했을 때에 콜백을 막기 위한 플래그.
     */
    private boolean mIsFinished = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaInfo = new MediaInfo();
        mediaInfo.currentPosition = -1;
        mediaInfo.isPlaying = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_vod_media_player_controller, container, false);
        baseVideoView = (BaseVideoView) result.findViewById(R.id.brightcove_video_view_vod_player);

        baseVideoView.setMediaController(new BrightcoveMediaController(baseVideoView, R.layout.brightcove_vod_media_controller));

        controller = baseVideoView.getBrightcoveMediaController();
        // 시간 3초 변경.
        controller.setShowHideTimeout(3000);
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

        tapView = view.findViewById(R.id.tapview);
        remainedTimeText = view.findViewById(R.id.text_vod_controller_remained_time);

        goPrdLayout = view.findViewById(R.id.goPrdLayout);

        bottomView = view.findViewById(R.id.view_vod_player_bottom_controller);
        mobileDataView = view.findViewById(R.id.view_vod_player_mobile_data);
        mobileDataCancelButton = mobileDataView.findViewById(R.id.button_mobile_data_cancel);
        mobileDataOkButton = mobileDataView.findViewById(R.id.button_mobile_data_ok);

        /**
         * controller buttons
         */
        playButton = view.findViewById(R.id.image_vod_controller_play);
        pauseButton = view.findViewById(R.id.image_vod_controller_pause);
        replayButton = view.findViewById(R.id.image_vod_controller_replay);
        chkMute = view.findViewById(R.id.check_vod_player_mute);
        fullScreenView = view.findViewById(R.id.view_vod_player_zoom);
        close_vod = view.findViewById(R.id.close_vod);

        ViewUtils.hideViews(chkMute, fullScreenView);
        ViewUtils.showViews(remainedTimeText);

        initController();
        initBrightCoveEvent();

        ViewUtils.hideViews(playButton, pauseButton, replayButton, mobileDataView);

        Ln.d("onViewCreated");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Ln.d("onDestroyView");
        EventBus.getDefault().unregister(this);
    }

    private void initController() {

        goPrdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNotEmpty(mListener)) {
                    mListener.onProductInfo();
                }
               /*  WebUtils.goWeb(getActivity(), "http://sm21.gsshop.com/prd/prd.gs?prdid=34519807&mseq=A00492-N-R1&fromApp=Y");
                //vod stop event
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_STOP, mediaInfo.listPosition));

               //내일 TV에만 동작 해야 하는 Amplitude  이벤트 입니다
                try {
                    JSONObject eventProperties = new JSONObject();
                    eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.TOMORROW_TV_LIST_PRD_CLICK);
                    if(content != null) {
                        if(prdItem != null) {
                            eventProperties.put(AMPEnum.AMP_PRD_CODE, prdItem.dealNo);
                            eventProperties.put(AMPEnum.AMP_PRD_NAME, prdItem.productName);
                            eventProperties.put(AMPEnum.AMP_PRD_PRICE, prdItem.salePrice);
                        }
                        eventProperties.put(AMPEnum.AMP_VIDEO_ID, content.videoid);
                        eventProperties.put(AMPEnum.AMP_VIDEO_DUR, content.videoTime);

                        if(content.linkUrl != null)
                            eventProperties.put(AMPEnum.AMP_URL_MSEQ, getMseqFromLinkUrl(content.linkUrl));
                    }
                    Amplitude.getInstance().logEvent( AMPEnum.AMP_CLICK_TOMORROW_TV, eventProperties);
                } catch (JSONException exception){

                }*/
            }
        });


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNetworkBilling(getContext());
            }
        });

        close_vod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().unregister(this);
                if (isNotEmpty(mListener)) {
                    try {
                        mListener.sendWiseLog(PlayerAction.EXIT, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                    }
                    catch (NullPointerException e) {
                        Ln.e(e.getMessage());
                    }
                    mListener.onStoped();
                }

            }
        });



        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseVideoView.pause();
                if (isNotEmpty(mListener)) {
                    mListener.sendWiseLog(PlayerAction.PAUSE, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseVideoView.seekTo(0);
                confirmNetworkBilling(getContext());
            }
        });

        fullScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNotEmpty(mListener)) {
                    mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                    mediaInfo.isPlaying = isPlaying();
                    baseVideoView.pause();
                    if (isNotEmpty(mListener)) {
                        mListener.onFullScreenClick(mediaInfo);
                        mListener.sendWiseLog(PlayerAction.FULL, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                    }
                }
            }
        });

        tapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ln.d("onTapClicked");
                boolean isShow = controller.isShowing();
                if (isShow) {
                    controller.show();
                } else {
                    if (isPlaying()) {
                        controller.hide();
                    }
                }
            }
        });

        //사운드 On/Off
        chkMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMute(chkMute.isChecked());
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
                if (isNotEmpty(mListener)) {
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
                cancelMobileDataView();
                if (isNotEmpty(mListener)) {
                    mListener.sendWiseLog(PlayerAction.LTE_N, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

        mobileDataOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaInfo.isLandscape) {
                    if (isNotEmpty(mListener)) {
                        mListener.showMobileData(false);
                    }
                } else {
                    ViewUtils.hideViews(mobileDataView);
                    ViewUtils.showViews(bottomView);
                }
                MainApplication.isNetworkApproved = true;
                mediaInfo.isPlaying = true;
                baseVideoView.start();
                controller.show();

                if (isNotEmpty(mListener)) {
                    mListener.sendWiseLog(PlayerAction.LTE_Y, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });
    }

    private void confirmNetworkBilling(Context context) {
        if ((!NetworkStatus.isWifiConnected(context) && !MainApplication.isNetworkApproved)) {
            if (mediaInfo.currentPosition <= 0) {
                controller.setHideControllerEnable(true);
            }
            if (!mediaInfo.isLandscape) {
                if (isNotEmpty(mListener)) {
                    mListener.showMobileData(true);
                }
            } else {
                ViewUtils.hideViews(bottomView);
                ViewUtils.showViews(mobileDataView);
            }
            controller.hide();

            if (isNotEmpty(mListener)) {
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MOBILE_DATA, mListener.getListPosition()));
            }
        } else {
            mediaInfo.isPlaying = true;
            baseVideoView.start();
        }
    }

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
                //다른 앱 사운드 중지
                if (!MainApplication.isMute) {
                    SoundUtils.requestAudioFocus();
                }

                // 동영상이 실행하면 나머지 실행중인 동영상들은 init 상태를 만든다.

                EventBus.getDefault().post(new Events.VodShopPlayerEvent(VOD_INIT_OTHERS, mediaInfo.listPosition));
                prevPlayingPosition = mediaInfo.listPosition;

                controller.setHideControllerEnable(true);
                //자동재생시 세로영상 불투명영역 깜박임 방지용
                if (isNotEmpty(event.getProperties().get("duration"))) {
                    controller.hide();
                    if (isNotEmpty(mListener)) {
                        mListener.onPlayed();
                    }
                }
                LoadSoundMuteFromGlobal();
                ViewUtils.showViews(pauseButton);
                ViewUtils.hideViews(playButton, replayButton);
            }
        });

        /*
        일시 정지
         */
        eventEmitter.on(EventType.DID_PAUSE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.d("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration());
                controller.setHideControllerEnable(false);
                controller.show();
                if (replayButton.getVisibility() != View.VISIBLE) {
                    if (baseVideoView.getDuration() > 0) {
                        mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                    }
                    ViewUtils.showViews(playButton);
                    ViewUtils.hideViews(pauseButton, replayButton);
                }
                skipPlayerActionEvent = false;
            }
        });

        eventEmitter.on(EventType.DID_STOP, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.d("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration());
                cancelMobileDataView();
            }
        });

        eventEmitter.on(EventType.COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.d("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration());
                mListener.sendWiseLog(PlayerAction.PAUSE, baseVideoView.getDuration(), baseVideoView.getDuration());
                mediaInfo.currentPosition = 0;
                mediaInfo.isPlaying = false;
//                ViewUtils.showViews(replayButton);
                mIsFinished = true;

                ViewUtils.hideViews(playButton, pauseButton);

                if (isNotEmpty(mListener)) {
                    mListener.onFinished(mediaInfo);
                }

                playPlayer();
            }
        });

        eventEmitter.on(EventType.READY_TO_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.i("Bundle current position: " + mediaInfo.currentPosition + ", isPlaying: " + isPlaying());
                if (!mediaInfo.isPlaying && mediaInfo.currentPosition < 200) {
                    // replay 화면일때만 다시 표시한다.
                    playPlayer();
                }
            }
        });

        eventEmitter.on(EventType.VIDEO_DURATION_CHANGED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                int duration = (int) event.getProperties().get("duration");
                if (!mediaInfo.isPlaying) {
                    duration = 0;
                }
                if (isNotEmpty(mListener)) {
                    mListener.onRemainedTime(stringForHHMM(duration, true));
                }
            }
        });

        eventEmitter.on(EventType.DID_SEEK_TO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                int seekPosition = (int) event.getProperties().get("seekPosition");
                Ln.d("currentPosition: " + seekPosition);
                if (seekPosition > 0) {
                    baseVideoView.pause();
                }
                if (isNotEmpty(mListener)) {
                    long remainedTime = baseVideoView.getDuration() - seekPosition;
                    remainedTimeText.setText(stringForHHMM(remainedTime, true));
                    mListener.onRemainedTime(stringForHHMM(remainedTime, true));
                }
            }
        });

        eventEmitter.on(EventType.SET_VIDEO_STILL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (mediaInfo.isPlaying) {
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        eventEmitter.on(EventType.DID_SET_VIDEO_STILL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (getActivity() != null && isAdded()) {
                    if (!mediaInfo.isPlaying) {
                        // 지정된 post image로 변경.
                        baseVideoView.getStillView().setImageResource(android.R.color.transparent);
//                        Glide.with(getContext()).load(mediaInfo.posterImageUrl).into(baseVideoView.getStillView());
                        ImageUtil.loadImageResizeToHeight(getContext(), mediaInfo.posterImageUrl, baseVideoView.getStillView(), 0);
                    }

                }
            }
        });

        eventEmitter.on(EventType.PROGRESS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                long remainedTime = baseVideoView.getDuration() - baseVideoView.getCurrentPosition();
                remainedTimeText.setText(stringForHHMM(remainedTime, false));
                if (isNotEmpty(mListener)) {
                    mListener.onRemainedTime(stringForHHMM(remainedTime, false));
                }
            }
        });

        eventEmitter.on(EventType.VIDEO_SIZE_KNOWN, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (mediaInfo.isPlaying) {
                    playButton.performClick();
                }
//                if (isNotEmpty(mListener)) {
//                    mListener.onVideoSizeKnown();
//                }
            }
        });

        eventEmitter.on(EventType.PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (!mediaInfo.isPlaying) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                if (isNotEmpty(mListener) && !skipPlayerActionEvent) {
                    mListener.sendWiseLog(PlayerAction.PLAY, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                    skipPlayerActionEvent = true;
                }
            }
        });


        eventEmitter.on(EventType.BUFFERING_COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (isNotEmpty(mListener) && !mIsFinished) {
                    mListener.onVideoSizeKnown();
                }
            }
        });

        eventEmitter.on(EventType.ERROR, new EventListener() {
            @Override
            public void processEvent(Event event) {
                //비디오번호가 유효하지 않는 등 에러 발생시
                Map<String, Object> map = event.getProperties();
                String errCode = (String) map.get("error_code");
                if ("RESOURCE_NOT_FOUND".equals(errCode)
                        || "VIDEO_NOT_FOUND".equals(errCode)
                        || "VIDEO_NOT_PLAYABLE".equals(errCode)) {
                    if (isNotEmpty(mListener)) {
                        mListener.onError(new Exception());
                    }
                }
            }
        });
        eventEmitter.on(ShowHideController.DID_HIDE_MEDIA_CONTROLS, new EventListener() {
            @Override
            public void processEvent(Event event) {
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

                // 남은 시간 갱신.
                if (mediaInfo.currentPosition > 0 && baseVideoView.getDuration() > 0) {
                    Ln.i("did current position: " + mediaInfo.currentPosition + ", get current: " + baseVideoView.getCurrentPosition() + ", dur: " + baseVideoView.getDuration());
                    long remainedTime = baseVideoView.getDuration() - baseVideoView.getCurrentPosition();
                    remainedTimeText.setText(stringForHHMM(remainedTime, false));
                    if (isNotEmpty(mListener)) {
                        mListener.onRemainedTime(stringForHHMM(remainedTime, false));
                    }
                }
            }
        });

        eventEmitter.on(FRAGMENT_PAUSED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.i("Bundle duration: " + baseVideoView.getDuration() + ", position: " + baseVideoView.getCurrentPosition());
                if (baseVideoView.isPlaying()) {
                    baseVideoView.pause();
                }
                mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                mediaInfo.isPlaying = false;
            }
        });
    }

    private static String stringForHHMM(long milliseconds, boolean up) {
        if (milliseconds <= 0L) {
            return "00:00";
        } else {
            long totalSeconds = milliseconds / 1000L;
            long seconds = totalSeconds % 60L + (up ? (milliseconds % 1000L != 0 ? 1 : 0) : 0);
            long minutes = totalSeconds / 60L;
            Formatter formatter = new Formatter(Locale.getDefault());
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void LoadSoundMuteFromGlobal() {
        chkMute.setChecked(MainApplication.isMute);
        setMute(MainApplication.isMute);
    }

    @Override
    public void setOnMediaPlayerListener(OnMediaPlayerListener callback) {
        if (callback != null) {
            this.mListener = callback;
        }
    }

    /**
     * 미디어 정보를 반환한다.
     *
     * @return MediaInfo
     */
    @Override
    public MediaInfo getMediaInfo() {
        mediaInfo.isPlaying = baseVideoView.isPlaying();
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
        VideoDisplayComponent videoDisplayComponent = baseVideoView.getVideoDisplay();
        if (videoDisplayComponent instanceof ExoPlayerVideoDisplayComponent) {
            // Get ExoPlayer
            ExoPlayer exoPlayer = ((ExoPlayerVideoDisplayComponent) videoDisplayComponent).getExoPlayer();
            if (!isEmpty(exoPlayer)) {
                // audio
                float volume = on ? 0 : 1;
                ((SimpleExoPlayer) exoPlayer).setVolume(volume);
                MainApplication.isMute = on;

                //다른 앱 사운드 중지
                if (!MainApplication.isMute && baseVideoView.isPlaying()) {
                    SoundUtils.requestAudioFocus();
                }
            }
        }


    }

    @Override
    public void onTapClicked() {
        Ln.d("onTapClicked");
        boolean isShow = controller.isShowing();
        if (!isShow) {
            controller.show();
        } else {
            if (isPlaying()) {
                controller.hide();
            }
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
        if (getActivity() == null || !isAdded()) {
            return;
        }
        String videoId = mediaInfo.videoId;
        //videoId가 videoUrl보다 우선순위
        if (!TextUtils.isEmpty(videoId) && videoId.length() > 4) {
            // 새로운 동영상이거나, 같은 동영상이라도 처음, replay 상태일때는 동영상을 다시 받아온다.
            if (mediaInfo.currentPosition <= 0) {
                Ln.d("video : " + videoId);
                baseVideoView.clear();
                Catalog catalog = new Catalog(eventEmitter, getString(R.string.account), getString(R.string.policy));
                catalog.findVideoByID(videoId, new VideoListener() {

                    // Add the video found to the queue with add().
                    // Start playback of the video with start().
                    @Override
                    public void onVideo(Video video) {
                        Ln.d("onVideo: video = " + video);
                        baseVideoView.clear();
                        baseVideoView.add(video);
                        ViewUtils.showViews(chkMute, fullScreenView, remainedTimeText);

                        if (!videoId.equals(currentVideoId)) {
                            // 새로운 동영상
                            currentVideoId = videoId;
                            remainedTimeText.setText("");
                            if (isNotEmpty(mListener)) {
                                mListener.onRemainedTime("");
                            }
                            ViewUtils.hideViews(playButton, pauseButton, replayButton, mobileDataView);
                        } else {
                            // 같은 동영상 브라이언트 썸네일 표시
                            cancelMobileDataView();
                        }

                        if (mediaInfo.isPlaying) {
                            baseVideoView.getEventEmitter().emit(EventType.DID_PLAY);
                        } else {
                            baseVideoView.getEventEmitter().emit(EventType.DID_PAUSE);
                        }
                    }
                });
            } else {
                cancelMobileDataView();
            }
        } else {
            Toast.makeText(getActivity(), R.string.video_play_error, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void replayPlayer() {
        replayButton.performClick();
    }

    @Override
    public void stopPlayer() {
    }

    @Override
    public void performMobileDataButton(boolean isOK) {
        if (isOK) {
            mobileDataOkButton.performClick();
        } else {
            cancelMobileDataView();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.mListener != null) {
            return;
        }

        try {
            this.mListener = (OnMediaPlayerListener) context;
        } catch (ClassCastException e) {
            this.mListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (isNotEmpty(mListener)) {
            mListener.onDetached();
        }

        mListener = null;
    }


    public void onEvent(Events.VodShopPlayerEvent event) {
        switch (event.action) {
            case VOD_PAUSE:
                if (event.position != mediaInfo.listPosition && baseVideoView.isPlaying()) {
                    baseVideoView.pause();
                }
                break;
            case VOD_MOBILE_DATA:
                if (isNotEmpty(mListener)) {
                    if (mListener.getListPosition() != event.position) {
                        cancelMobileDataView();
                    }
                }
                break;

            case VOD_INIT_OTHERS:
                if (event.position != mediaInfo.listPosition && baseVideoView.isPlaying()) {
                    baseVideoView.pause();
                    if (isNotEmpty(mListener)) {
                        mListener.onStoped();
                    }
                }
                break;

            case VOD_MUTE:
                chkMute.setChecked(event.on);
                setMute(event.on);
                break;
            case VOD_BACK_TO_FOR:
                baseVideoView.getEventEmitter().emit(EventType.DID_PAUSE);
                break;
        }
    }

    /**
     * 과금팝업 취소버튼 클릭한 경우
     */
    private void cancelMobileDataView() {
        if (!mediaInfo.isLandscape) {
            if (isNotEmpty(mListener)) {
                mListener.showMobileData(false);
            }
        } else {
            ViewUtils.hideViews(mobileDataView);
            ViewUtils.showViews(bottomView);
        }

        if (mediaInfo.currentPosition <= 0) {
            controller.setHideControllerEnable(false);
        }
        controller.show();
    }
}
