package gsshop.mobile.v2.web.productDetail.video.brightcove;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.SoundUtils;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.brightcove.player.event.EventType.FRAGMENT_PAUSED;
import static gsshop.mobile.v2.home.shop.vod.VodShopFragment.prevPlayingPosition;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_PRD_VOD_MANUAL_PLAY;

/**
 * mp4 video play controller fragment
 */
@SuppressLint("NewApi")
public class FragmentDetailViewBrightcoveController extends BrightcovePlayerFragment implements OnMediaPlayerController, View.OnClickListener, EventListener {

    private OnMediaPlayerListener mListener;

    /**
     * ?????????????????? ?????? ??? ????????? ??????
     */
    private MediaInfo mediaInfo;

    //for tap event
    private View tapView;
    // ????????? ????????? ??????
    private View playAreaView;
    // ????????? ??????
    private CheckBox chkMute;
    // ???????????? ??????
    private View fullScreenView;
    // ????????? ??????
    private ImageView playButton;
    // ?????? ?????? ??????
    private View pauseButton;
    // ?????? ?????? ??????
    private View replayButton;

    // ?????? ?????? ?????????
    private TextView remainedTimeText;
    // ?????? ?????? / ????????? ?????? ??????
    private View bottomView;

    // ?????? Controller Fragment ?????? ????????? controller
//    private BrightcoveMediaController controller;
    // ????????? ??????
    private EventEmitter eventEmitter;
    // ?????? ?????? ?????? ????????? ID
    private String currentVideoId;

    // contoller??? ????????? ?????? ????????? ????????? ???????????? ???????????? ????????? Show / hide??? ???????????? ??????.
    private int duration = -1;

    // Fragment ?????? ????????? ?????? ?????? ????????? ????????? ?????????.
    private boolean mIsMute = true; // mute

    /**
     * ???????????? ????????? ????????? ?????? ?????? ?????????
     */
    private boolean skipPlayerActionEvent = false;

    /**
     * ?????? ?????? ?????? ????????? ?????? ????????? ?????? ?????? ????????? ?????? ?????? ?????????.
     */
    private boolean mIsFinished = false;

    private TimerTask playAreaHideTimerTask;
    private final Timer playAreaHideTimer = new Timer();

    // ?????? ????????? ?????? ?????? ??????????????? ????????? ???????????? ????????? ????????? ????????? ?????? flag
    private boolean mControllerVisible = true;

    /**
     * ?????? ?????? ?????? ?????????????????? ???????????? ?????? ?????????
     */
    public enum CALLER { PRODUCT, SIGNATURE }
    private CALLER mCaller = CALLER.PRODUCT;
    private static final String ARG_PARAM_CALLER = "_arg_param_caller";

    public static Fragment newInstance(CALLER caller) {
        FragmentDetailViewBrightcoveController fragment = new FragmentDetailViewBrightcoveController();
        if (CALLER.SIGNATURE == caller) {
            Bundle args = new Bundle();
            args.putString(ARG_PARAM_CALLER, CALLER.SIGNATURE.name());
            fragment.setArguments(args);
        }
        return fragment;
    }

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
        //default (??????)
        int resId = R.layout.fragment_controller_detail_view;
        if (getArguments() != null) {
            if (CALLER.SIGNATURE.name().equalsIgnoreCase(getArguments().getString(ARG_PARAM_CALLER))) {
                //?????????????????? ???
                resId = R.layout.fragment_controller_signature_view;
                mCaller = CALLER.SIGNATURE;
            }
        }

        // Inflate the layout for this fragment
        View result = inflater.inflate(resId, container, false);
        baseVideoView = result.findViewById(R.id.brightcove_video_view);

        baseVideoView.setMediaController((BrightcoveMediaController) null);

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

        playAreaHideTimerTask = new TimerTask() {
            public void run() {
                updatePlayArea();
            }

            public void updatePlayArea() {
                new Handler(Looper.getMainLooper()).post(() -> showPlayControllerView(false));
            }
        };

        tapView = view.findViewById(R.id.tapview);
        remainedTimeText = view.findViewById(R.id.text_vod_controller_remained_time);

        playAreaView = view.findViewById(R.id.view_player_controller_area);
        bottomView = view.findViewById(R.id.view_vod_player_bottom_controller);
        /**
         * controller buttons
         */
        playButton = view.findViewById(R.id.image_vod_controller_play);
        pauseButton = view.findViewById(R.id.image_vod_controller_pause);
        replayButton = view.findViewById(R.id.image_vod_controller_replay);
        chkMute = view.findViewById(R.id.check_vod_player_mute);
        fullScreenView = view.findViewById(R.id.view_vod_player_zoom);

        playButton.setContentDescription(getString(R.string.common_play));
        pauseButton.setContentDescription(getString(R.string.common_pause));
        replayButton.setContentDescription(getString(R.string.common_restart));
        chkMute.setContentDescription(getString(R.string.common_mute));
        fullScreenView.setContentDescription(getString(R.string.common_full_screen));

        ViewUtils.hideViews(playAreaView, playButton, replayButton);

        tapView.setOnClickListener(this);
        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        replayButton.setOnClickListener(this);
        fullScreenView.setOnClickListener(this);
        chkMute.setOnClickListener(this);

        // set event of controllers
        baseVideoView.getEventEmitter().on(EventType.DID_PLAY, this);
        baseVideoView.getEventEmitter().on(EventType.DID_PAUSE, this);
        baseVideoView.getEventEmitter().on(EventType.DID_STOP, this);
        baseVideoView.getEventEmitter().on(EventType.COMPLETED, this);
        baseVideoView.getEventEmitter().on(EventType.READY_TO_PLAY, this);
        baseVideoView.getEventEmitter().on(EventType.VIDEO_DURATION_CHANGED, this);
        baseVideoView.getEventEmitter().on(EventType.DID_SEEK_TO, this);
        baseVideoView.getEventEmitter().on(EventType.SET_VIDEO_STILL, this);
        baseVideoView.getEventEmitter().on(EventType.DID_SET_VIDEO_STILL, this);
        baseVideoView.getEventEmitter().on(EventType.PROGRESS, this);
        baseVideoView.getEventEmitter().on(EventType.VIDEO_SIZE_KNOWN, this);
        baseVideoView.getEventEmitter().on(EventType.PLAY, this);
        baseVideoView.getEventEmitter().on(EventType.BUFFERING_COMPLETED, this);
        baseVideoView.getEventEmitter().on(EventType.ERROR, this);
        baseVideoView.getEventEmitter().on(ShowHideController.DID_SHOW_MEDIA_CONTROLS, this);
        baseVideoView.getEventEmitter().on(ShowHideController.DID_HIDE_MEDIA_CONTROLS, this);
        baseVideoView.getEventEmitter().on(FRAGMENT_PAUSED, this);
        baseVideoView.getEventEmitter().emit(ShowHideController.HIDE_MEDIA_CONTROLS);
    }

    @Override
    public void setOnMediaPlayerListener(OnMediaPlayerListener callback) {
        if (callback != null) {
            this.mListener = callback;
        }
    }

    /**
     * ????????? ????????? ????????????.
     *
     * @return MediaInfo
     */
    @Override
    public MediaInfo getMediaInfo() {
        mediaInfo.isPlaying = baseVideoView.isPlaying();
        mediaInfo.videoMode = NativeProductV2.VIDEO_MODE_VOD;
        return mediaInfo;
    }

    @Override
    public void setMediaInfo(MediaInfo media) {
        this.mediaInfo = media;
        if (media == null || media.endTime == null) {
            remainedTimeText.setVisibility(View.GONE);
        }
        else {
            try {
                if (mCaller == CALLER.SIGNATURE) {
                    remainedTimeText.setText(StringUtils.stringForHHMMSS(Long.parseLong(media.endTime), true));
                } else {
                    remainedTimeText.setText(StringUtils.stringForHHMM(Long.parseLong(media.endTime), true));
                }
                if (isNotEmpty(mListener)) {
                    mListener.onRemainedTime(StringUtils.stringForHHMM(Long.parseLong(media.endTime), true));
                }
            }
            catch (NumberFormatException e) {
                Ln.e(e.getMessage());
            }
        }
    }

    @Override
    public View getPlayerView() {
        return getView();
    }

    /**
     * ???????????? ??????/?????? ?????????.
     *
     * @param mute - true ?????? mute
     */
    @Override
    public void setMute(boolean mute) {
        VideoDisplayComponent videoDisplayComponent = baseVideoView.getVideoDisplay();
        if (videoDisplayComponent instanceof ExoPlayerVideoDisplayComponent) {
            // Get ExoPlayer
            ExoPlayer exoPlayer = ((ExoPlayerVideoDisplayComponent) videoDisplayComponent).getExoPlayer();
            if (!isEmpty(exoPlayer)) {
                // audio
                float volume = !mute ? 1 : 0;
                ((SimpleExoPlayer) exoPlayer).setVolume(volume);
                mIsMute = MainApplication.nativeProductIsMute = mute;

                //?????? ??? ????????? ??????
                if (!mIsMute && baseVideoView.isPlaying()) {
                    SoundUtils.requestAudioFocus();
                }
            }
        }
    }

    @Override
    public void onTapClicked() {
        Ln.d("onTapClicked");

        // ????????? tap ?????? visible true

        if (mListener != null) {
            mListener.onTap(true);
        }

        showPlayControllerView(true);
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
        //videoId??? videoUrl?????? ????????????
        if (!TextUtils.isEmpty(videoId) && videoId.length() > 4) {
            // ????????? ??????????????????, ?????? ?????????????????? ??????, replay ??????????????? ???????????? ?????? ????????????.
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
                            // ????????? ?????????
                            currentVideoId = videoId;
                            remainedTimeText.setText("");
                            if (isNotEmpty(mListener)) {
                                mListener.onRemainedTime("");
                            }
                            ViewUtils.hideViews(playButton, pauseButton, replayButton);
                        } else {
                            // ?????? ????????? ??????????????? ????????? ??????
//                            cancelMobileDataView();
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
            if (mListener != null) {
                mListener.onError(new Exception());
            }
            if (isNotEmpty(getActivity())) {
                Toast.makeText(getActivity(), R.string.video_play_error, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void replayPlayer() {
        replayButton.performClick();
    }

    @Override
    public void stopPlayer() {
        mControllerVisible = true;
        mediaInfo.isPlaying = false;
        stopPlayAreaHideTask();
        baseVideoView.pause();
    }

    @Override
    public void reloadPlayer() {
        baseVideoView.start();
    }

    /**
     * ?????? ?????? ????????? ??????
     */
    @Override
    public void onFullscreenDisabled(boolean isBroadcastFinished) {
        mIsMute = MainApplication.nativeProductIsMute;
        chkMute.setChecked(!mIsMute);
//        setMute(mIsMute);
        mediaInfo.currentPosition = MainApplication.gVideoCurrentPosition;
        mediaInfo.isPlaying = MainApplication.gVideoIsPlaying;

        new Handler().postDelayed(() -> {
            baseVideoView.seekTo((int) MainApplication.gVideoCurrentPosition);
            if (mediaInfo.isPlaying) {
                baseVideoView.start();
            }
        }, 500);

        if (mediaInfo.isPlaying) {
            playAreaView.setVisibility(View.GONE);
        }
        else {
            // ?????? ?????? ?????? ?????? ?????? ?????? UI ??????????????? ??????.
            ViewUtils.showViews(playButton);
            ViewUtils.hideViews(pauseButton, replayButton);
        }
        sendWiseLogPrdNative(PlayerAction.MINI, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_vod_controller_play :
                // ????????? ?????? ???????????? ???????????? ????????? confirmNetworkBilling ????????????
                // VOD ????????? ?????? ????????? AMP
                mediaInfo.isPlaying = true;
                baseVideoView.start();
                ProductDetailWebActivityV2.prdAmpSend(AMP_PRD_VOD_MANUAL_PLAY);
                // ???????????? ????????? ???????????? ?????? resume
                sendWiseLogPrdNative(PlayerAction.RESUME, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                break;

            case R.id.image_vod_controller_pause :
                mControllerVisible = true;
                mediaInfo.isPlaying = false;
                stopPlayAreaHideTask();
                baseVideoView.pause();

                sendWiseLogPrdNative(PlayerAction.PAUSE, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                break;

            case R.id.image_vod_controller_replay :
                MainApplication.gVideoCurrentPosition = 0;
                baseVideoView.seekTo(0);
                mediaInfo.currentPosition = 0;
                remainedTimeText.setVisibility(View.VISIBLE);
                mediaInfo.isPlaying = true;
                baseVideoView.start();
                sendWiseLogPrdNative(PlayerAction.RESUME, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                break;

            case R.id.tapview:
                if (!mControllerVisible) {
                    if (mListener != null) {
                        mListener.onTap(true);
                    }
                    return;
                }
                onTapClicked();
                break;

            case R.id.view_vod_player_zoom :
                if (isNotEmpty(mListener)) {
                    MainApplication.gVideoCurrentPosition = mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                    MainApplication.nativeProductIsMute = mIsMute;

                    mediaInfo.isPlaying = isPlaying();

                    mediaInfo.isLandscape = true;
                    if (getVideoHeight() >= getVideoWidth()) {
                        mediaInfo.isLandscape = false;
                    }

                    mControllerVisible = true;
                    baseVideoView.pause();
                    if (isNotEmpty(mListener)) {
                        mListener.onFullScreenClick(mediaInfo);
//                        mListener.sendWiseLog(PlayerAction.FULL, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                    }

                    sendWiseLogPrdNative(PlayerAction.FULL, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
                break;

            case R.id.check_vod_player_mute :
                setMute(!chkMute.isChecked());
                if (isNotEmpty(mListener)) {
                    PlayerAction playerAction = PlayerAction.SOUND_ON;
                    if (chkMute.isChecked()) {
                        playerAction = PlayerAction.SOUND_OFF;
                    }
                    mListener.onMute(!chkMute.isChecked());
                    mListener.sendWiseLogPrdNative(playerAction, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }

                break;
        }
    }

    private void startPlayAreaHideTimerTask() {
        // public void schedule (TimerTask task, long delay, long period)
        playAreaHideTimerTask = new TimerTask() {
            public void run() {
                updatePlayArea();
            }

            public void updatePlayArea() {
                new Handler(Looper.getMainLooper()).post(() -> showPlayControllerView(false));
            }
        };
        playAreaHideTimer.schedule(playAreaHideTimerTask, 1000);
    }

    private void stopPlayAreaHideTask() {
        if (playAreaHideTimerTask != null) {

            playAreaHideTimerTask.cancel();
            playAreaHideTimerTask = null;
        }
    }

    /**
     * play controller view
     *
     * @param show show
     */
    @Override
    public void showPlayControllerView(final boolean show) {
        if (show) {
            if (!mControllerVisible || playAreaView == null) {
                return;
            }
            stopPlayAreaHideTask();
            if (!playAreaView.isShown()) {
                playAreaView.setVisibility(View.VISIBLE);
            }
            // ?????? ??????????????? ???????????????
            if (isPlaying()) {
                startPlayAreaHideTimerTask();
            }
        }
        else {
            stopPlayAreaHideTask();
            if (playAreaView.isShown()) {
                playAreaView.animate().alphaBy(1).alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        playAreaView.setVisibility(View.GONE);
                        playAreaView.setAlpha(1);
                    }
                });
            }
        }
    }

    // MainApplication ????????? isMute ??????.
    private void LoadSoundMuteFromGlobal() {
        mIsMute = MainApplication.nativeProductIsMute;
        chkMute.setChecked(!mIsMute);
        setMute(MainApplication.nativeProductIsMute);
    }

    private void LoadSoundMuteFromLocal() {
        chkMute.setChecked(!mIsMute);
        setMute(mIsMute);
    }

    /**
     * ???????????? ???????????? ????????? ?????? ???????????? ?????? ?????? ???????????? ?????????. ???????????? ???????????? ????????? ????????? ??????.
     */
    private void cancelMobileDataView() {
        if (!mediaInfo.isLandscape) {
            if (isNotEmpty(mListener)) {
                mListener.showMobileData(false);
            }
        } else {
            ViewUtils.showViews(bottomView);
        }

        showPlayControllerView(true);
    }

    /**
     * DID_PLAY ?????????.
     * @param event
     */
    private void emiterEventDidPlay(Event event) {
        //?????? ??? ????????? ??????
        if (!mIsMute) {
            SoundUtils.requestAudioFocus();
        }

        prevPlayingPosition = mediaInfo.listPosition;

        //??????????????? ???????????? ??????????????? ????????? ?????????
        if (isNotEmpty(event.getProperties().get("duration"))) {
            showPlayControllerView(false);
            if (isNotEmpty(mListener)) {
                mListener.onPlayed();
            }
        }
        LoadSoundMuteFromGlobal();
        ViewUtils.showViews(pauseButton);
        ViewUtils.hideViews(playButton, replayButton);
    }

    private void emiterEventDidPause(Event event) {
        Ln.d("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration());
        showPlayControllerView(true);
        if (replayButton.getVisibility() != View.VISIBLE) {
            if (baseVideoView.getDuration() > 0) {
                mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                ViewUtils.showViews(playButton);
                ViewUtils.hideViews(pauseButton, replayButton);
            }
        }
        skipPlayerActionEvent = false;
    }

    private void emiterEventComplete() {
        Ln.d("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration());
//        mListener.sendWiseLog(PlayerAction.PAUSE, baseVideoView.getDuration(), baseVideoView.getDuration());
        baseVideoView.pause();
        setRemainedTimeText();
        mediaInfo.isPlaying = false;
        mIsFinished = true;

        if (isNotEmpty(mListener)) {
            mediaInfo.videoMode = NativeProductV2.VIDEO_MODE_VOD;
            mListener.onFinished(mediaInfo);
        }

        ViewUtils.hideViews(playButton, pauseButton, remainedTimeText);
        if (mControllerVisible) {
            ViewUtils.showViews(replayButton, playAreaView);
        }
    }

    @Override
    public void processEvent(Event event) {
        long remainedTime;
        switch (event.getType()) {
            case EventType.DID_PLAY :
                emiterEventDidPlay(event);
                break;

            case EventType.DID_PAUSE :
                emiterEventDidPause(event);
                break;

            case EventType.DID_STOP:
                Ln.d("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration());
                cancelMobileDataView();
                break;

            case EventType.COMPLETED :
                emiterEventComplete();
                break;

            case EventType.READY_TO_PLAY :
                Ln.i("Bundle current position: " + mediaInfo.currentPosition + ", isPlaying: " + isPlaying());
                // ????????? ????????? ????????? ?????? ????????? ?????? ?????????.
//                if (!mediaInfo.isPlaying && mediaInfo.currentPosition < 200) {
                    // replay ??????????????? ?????? ????????????.
//                    playPlayer();
//                }
                break;

            case EventType.VIDEO_DURATION_CHANGED :
                int duration = (int) event.getProperties().get("duration");
                if (!mediaInfo.isPlaying) {
                    duration = 0;
                }
                if (isNotEmpty(mListener)) {
                    mListener.onRemainedTime(StringUtils.stringForHHMM(duration, true));
                    mListener.videoDurationChanged((int)event.getProperties().get("duration"));
                }
                break;

            case EventType.DID_SEEK_TO :
                int seekPosition = (int) event.getProperties().get("seekPosition");
                Ln.d("currentPosition: " + seekPosition);
                remainedTime = baseVideoView.getDuration() - seekPosition;
                if (mCaller == CALLER.SIGNATURE) {
                    remainedTimeText.setText(StringUtils.stringForHHMMSS(remainedTime, true));
                } else {
                    remainedTimeText.setText(StringUtils.stringForHHMM(remainedTime, true));
                }
                if (isNotEmpty(mListener)) {
                    mListener.onRemainedTime(StringUtils.stringForHHMM(remainedTime, true));
                }
                break;

            case EventType.SET_VIDEO_STILL :
                if (mediaInfo.isPlaying) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                break;

            case EventType.DID_SET_VIDEO_STILL :
                if (getActivity() != null && isAdded()) {
                    if (!mediaInfo.isPlaying) {
                        // ????????? post image??? ??????.
                        baseVideoView.getStillView().setImageResource(android.R.color.transparent);
                    }

                }
                break;

            case EventType.PROGRESS :
                mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                setRemainedTimeText();
                break;

            case EventType.VIDEO_SIZE_KNOWN :
                if (mediaInfo.isPlaying) {
                    baseVideoView.start();
                }
                break;

            case EventType.PLAY :
                if (!mediaInfo.isPlaying) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                if (isNotEmpty(mListener) && !skipPlayerActionEvent) {
//                    mListener.sendWiseLog(PlayerAction.PLAY, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                    skipPlayerActionEvent = true;
                }
                break;

            case EventType.BUFFERING_COMPLETED :
                if (isNotEmpty(mListener) && !mIsFinished) {
                    mListener.onVideoSizeKnown();
                }
                break;

            case EventType.ERROR :
                Map<String, Object> map = event.getProperties();
                String errCode = (String) map.get("error_code");
                if ("RESOURCE_NOT_FOUND".equals(errCode)
                        || "VIDEO_NOT_FOUND".equals(errCode)
                        || "VIDEO_NOT_PLAYABLE".equals(errCode)) {
                }
                if (isNotEmpty(mListener)) {
                    mListener.onError(new Exception());
                }
                break;

            case ShowHideController.DID_HIDE_MEDIA_CONTROLS :
                if (isNotEmpty(mListener)) {
                    mListener.onTap(false);
                }
                break;

            case ShowHideController.DID_SHOW_MEDIA_CONTROLS :
                if (isNotEmpty(mListener)) {
                    mListener.onTap(true);
                }

                // ?????? ?????? ??????.
                if (mediaInfo.currentPosition > 0 && baseVideoView.getDuration() > 0) {
                    Ln.i("did current position: " + mediaInfo.currentPosition + ", get current: " + baseVideoView.getCurrentPosition() + ", dur: " + baseVideoView.getDuration());
                    setRemainedTimeText();
                }
                break;

            case FRAGMENT_PAUSED :
                Ln.i("Bundle duration: " + baseVideoView.getDuration() + ", position: " + baseVideoView.getCurrentPosition());
                if (baseVideoView.isPlaying()) {
//                    mControllerVisible = true;
                    baseVideoView.pause();
                }
                mediaInfo.currentPosition = baseVideoView.getCurrentPosition();
                mediaInfo.isPlaying = false;
                break;

        }
    }

    public boolean getConrollerVisibility() {
        return mControllerVisible;
    }

    public void setControllerVisibility(boolean controllerVisible) {
        mControllerVisible = controllerVisible;
        if (!controllerVisible)  {
            playAreaView.setVisibility(View.GONE);
        }
    }

    private void setRemainedTimeText() {
        long remainedTime = baseVideoView.getDuration() - baseVideoView.getCurrentPosition();
        if (mCaller == CALLER.SIGNATURE) {
            remainedTimeText.setText(StringUtils.stringForHHMMSS(remainedTime, true));
        } else {
            remainedTimeText.setText(StringUtils.stringForHHMM(remainedTime, false));
        }
        if (isNotEmpty(mListener)) {
            mListener.onRemainedTime(StringUtils.stringForHHMM(remainedTime, false));
        }
    }

    @Override
    public int getVideoHeight() {
        return baseVideoView.getVideoHeight();
    }

    @Override
    public int getVideoWidth() {
        return baseVideoView.getVideoWidth();
    }

    @Override
    public void setVideoStart() {
        remainedTimeText.setVisibility(View.VISIBLE);
        mediaInfo.isPlaying = true;
        baseVideoView.start();
    }

    @Override
    public void setVideoPause() {
        mediaInfo.isPlaying = false;
        stopPlayAreaHideTask();
        baseVideoView.pause();
    }

    @Override
    public void setPlayerSize(boolean isLow) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onFragmentResume();
        }
    }

    private void sendWiseLogPrdNative(PlayerAction action, int playTime, int totalTime) {
        if(mListener != null) {
            mListener.sendWiseLogPrdNative(action, playTime, totalTime);
        }
    }
}
