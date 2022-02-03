package gsshop.mobile.v2.support.media.exoplayer;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.gsshop.mocha.core.activity.BaseFragment;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Timer;
import java.util.TimerTask;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.renewal.flexible.RenewalBroadTimeLayout;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * mp4 video play controller fragment
 */
public class ExoMediaPlayerControllerFragment extends BaseFragment implements OnMediaPlayerController {
    private float currentVolume;

    // full screen listener
    private OnMediaPlayerListener callback;

    //for tap event
    private View tapView;

    // play buttons
    private ImageButton playImage;
    private ImageButton pauseImage;
    private ProgressBar loadingProgress;

    /**
     * play controller
     */
    private View playAreaView;
    private View playControllerVod;
    private View playControllerHls;


    // fullscreen
    private ImageButton fullScreenButtonVod;
    private ImageView fullScreenButtonHls;

    private TimerTask playAreaHideTimerTask;
    private final Timer playAreaHideTimer = new Timer();


    // title
    private View titleView;
    private TextView titleText;
    private ImageView backImage;

    private MediaInfo mediaInfo;

    // exo player
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    //남은시간 표시
    private RenewalBroadTimeLayout cv_remain_time;

    // 사운드 버튼
    private CheckBox chkMute;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private ComponentListener componentListener;

    private int currentWindow;
    private boolean playWhenReady = true;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private View rootView;

    public ExoMediaPlayerControllerFragment() {
        mediaInfo = new MediaInfo();
        mediaInfo.currentPosition = 0;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exo_media_player_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view.findViewById(R.id.root_view);
        playerView = view.findViewById(R.id.exoplayer_video_view);

        tapView = view.findViewById(R.id.tapview);

        // play buttons
        playImage = view.findViewById(R.id.image_play);
        pauseImage = view.findViewById(R.id.image_pause);
        loadingProgress = view.findViewById(R.id.mc_webview_progress_bar);

        //play controller
        playAreaView = view.findViewById(R.id.view_play_area);
        playControllerVod = view.findViewById(R.id.view_play_controller_vod);
        playControllerHls = view.findViewById(R.id.view_play_controller_hls);

        // fullscreen
        fullScreenButtonVod = view.findViewById(R.id.button_fullscreen_vod);
        fullScreenButtonHls = view.findViewById(R.id.button_fullscreen_hls);

        // title
        titleView = view.findViewById(R.id.view_title);
        titleText = view.findViewById(R.id.text_title);
        backImage = view.findViewById(R.id.image_back);

        //남은시간
        cv_remain_time = view.findViewById(R.id.cv_remain_time);
        //음소거
        chkMute = view.findViewById(R.id.check_vod_player_mute);

        tapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTap();
            }
        });

        fullScreenButtonVod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFullScreenClick(isPlaying());
                showPlayControllerView(false);
            }
        });

        fullScreenButtonHls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFullScreenClick(isPlaying());
                showPlayControllerView(false);
            }
        });

        // play
        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPlayButtonClicked();

                //라이브톡, 날방의 경우 플레이버튼 클릭시마다 네트워크 체크를 수행한다.
                if ((mediaInfo.channel == MediaInfo.CHANNEL_LIVE_TALK || mediaInfo.channel == MediaInfo.CHANNEL_NALBANG)
                        && !NetworkStatus.isWifiConnected(getActivity()) && !MainApplication.isNetworkApproved) {
                    confirmNetworkBilling();
                } else {
                    if(NetworkStatus.isNetworkConnected(getActivity())) {
                        play();
                    }
                    else {
                        NetworkUtils.alertNetworkDisconnected(getActivity());
                    }
                }
            }
        });

        // pause
        pauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseImage.setVisibility(View.GONE);
                playImage.setVisibility(View.VISIBLE);
                callback.onPaused();
                releasePlayer();
            }
        });


        // title 백버튼.
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onFinished(getMediaInfo());
                }
            }
        });

        playAreaView.setVisibility(View.GONE);

        playerView.setUseController(false);

        showLoadingProgress(false);

        chkMute.setChecked(MainApplication.isLiveTalkMute); //true -> 음소거된 버튼, false -> 음소거 안된 버튼
        chkMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //음소거된 상태에서 클릭
                if(MainApplication.isLiveTalkMute){
                    MainApplication.isLiveTalkMute = false;
                    chkMute.setChecked(MainApplication.isLiveTalkMute);
                    setMute(MainApplication.isLiveTalkMute);
                }else{ //음소거 해제 상태면
                    MainApplication.isLiveTalkMute = true;
                    chkMute.setChecked(MainApplication.isLiveTalkMute);
                    setMute(MainApplication.isLiveTalkMute);

                }
            }
        });
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
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    /**
     * Internal player
     *
     * @param show show
     */
    private void showLoadingProgress(boolean show) {
        if (show) {
            ViewUtils.showViews(loadingProgress);
        } else {
            ViewUtils.hideViews(loadingProgress);
        }
    }

    /**
     * 풀화면인지 여부에 따라 UI를 달리 세팅한다.
     */
    private void initUI() {
        if (mediaInfo.channel == MediaInfo.CHANNEL_MAIN_LIVE) {
            return;
        }

        if (!mediaInfo.isPlaying) {
            playAreaView.setVisibility(View.VISIBLE);
            showPlayButtons(true);
        } else {
            playAreaView.setVisibility(View.GONE);
        }
        if (mediaInfo.playerMode == MediaInfo.MODE_FULL) {
            titleView.setVisibility(View.VISIBLE);
            titleText.setText(mediaInfo.title);
            if (isVod()) {
                // vod
                fullScreenButtonVod.setImageResource(R.drawable.livetalk_full);
            } else {
                // hls
                fullScreenButtonHls.setImageResource(R.drawable.livetalk_full);
            }
        } else {
            titleView.setVisibility(View.GONE);
            titleText.setText(mediaInfo.title);
            if (isVod()) {
                // vod
                fullScreenButtonVod.setImageResource(R.drawable.livetalk_full);
            } else {
                // hls
                fullScreenButtonHls.setImageResource(R.drawable.livetalk_full);
            }
        }

        if (isVod()) {
            // vod
            playControllerVod.setVisibility(View.VISIBLE);
            playControllerHls.setVisibility(View.GONE);
        } else {
            // hls
            playControllerHls.setVisibility(View.VISIBLE);
            playControllerVod.setVisibility(View.GONE);

        }

        setMute(MainApplication.isLiveTalkMute);
        chkMute.setChecked(MainApplication.isLiveTalkMute);
    }

    private void initializePlayer(String videoUrl, long currentPosition) {
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
            if(isVod() && currentPosition > 0) {
                simpleExoPlayer.seekTo(currentPosition);
            } else {
                simpleExoPlayer.seekTo(currentWindow, 0);
            }
        }
        /*영역화면과 전체화면 전환시 플레이상태를 유지하기 위해 세팅
          영역화면 재생상태에서 전체화면 전환시 전체화면도 재생(반대도 동일)*/
        if (mediaInfo != null) {
            playWhenReady = mediaInfo.isPlaying;
        }
        simpleExoPlayer.setPlayWhenReady(playWhenReady);
        MediaSource mediaSource = buildMediaSource(videoUrl);
        simpleExoPlayer.prepare(mediaSource, true, false);
        initMuteFromGlobal();

        cv_remain_time.updateTvLiveTime_LiveTalk(null, mediaInfo.startTime, mediaInfo.endTime);
    }

    private void initMuteFromGlobal() {
        setMute(MainApplication.isMute);
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            mediaInfo.currentPosition = simpleExoPlayer.getCurrentPosition();
            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.removeListener(componentListener);
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
        mediaInfo.isPlaying = false;
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
     * 데이타 요금 안내팝업을 띄운다.
     */
    private void confirmNetworkBilling() {
        new CustomTwoButtonDialog(this.getActivity())
                .message(R.string.network_billing_confirm)
                .positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        play();
                        MainApplication.isNetworkApproved = true;
                        dialog.dismiss();
                    }
                }).negativeButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * play controller view
     *
     * @param show show
     */
    @Override
    public void showPlayControllerView(final boolean show) {
        if (isEmpty(simpleExoPlayer)) {
            return;
        }
        final boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        final int playbackState = simpleExoPlayer.getPlaybackState();
        Ln.i("showPlayControllerView : " + show + ", playAreaView.isShown() : " + playAreaView.isShown() + ", playWhenReady : " + playWhenReady + ", playbackState : " + playbackState);

        // tv 쇼핑 메인에서만 보여줌.
        if (mediaInfo.channel == MediaInfo.CHANNEL_MAIN_LIVE) {
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    if (playWhenReady) {
                        // onBuffering
                        showLoadingProgress(true);
                    }
                    break;
                default:
                    showLoadingProgress(false);
                    break;

            }

            return;
        }

        if (show) {
            stopPlayAreaHideTask();
            if (!playAreaView.isShown()) {
                playAreaView.setVisibility(View.VISIBLE);
                if (isVod()) {
                    // vod
                    playControllerVod.animate().translationY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            showPlayButtons(show);
                        }
                    });
                } else {
                    // hls
                    playControllerHls.animate().translationY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            showPlayButtons(show);
                        }
                    });
                }

            } else {
                showPlayButtons(show);
            }

            startPlayAreaHideTimerTask();

        } else {
            stopPlayAreaHideTask();
            if (playAreaView.isShown()) {
                if (isVod()) {
                    // vod
                    int height = playControllerVod.getHeight();
                    playControllerVod.animate().translationY(height).setDuration(100).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (simpleExoPlayer.getPlaybackState() != Player.STATE_ENDED) {
                                // 다시실행 상태가 아니면 숨기기.
                                playAreaView.setVisibility(View.GONE);
                                showPlayButtons(show);
                            }
                        }
                    });
                } else {
                    // hls
                    int height = playControllerHls.getHeight();
                    playControllerHls.animate().translationY(height).setDuration(100).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (simpleExoPlayer.getPlaybackState() != Player.STATE_ENDED) {
                                // 다시실행 상태가 아니면 숨기기.
                                playAreaView.setVisibility(View.GONE);
                                showPlayButtons(show);
                            }
                        }
                    });
                }


            } else {
                showPlayButtons(show);
            }

        }
    }

    private void showPlayButtons(boolean show) {
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        int playbackState = simpleExoPlayer.getPlaybackState();
        Ln.i("showPlayButtons - show : " + show + ", playWhenReady :  " + playWhenReady + ", playbackState : " + playbackState);
        showLoadingProgress(false);
        playImage.setVisibility(View.GONE);
        pauseImage.setVisibility(View.GONE);
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                if (playWhenReady) {
                    // onBuffering
                    showLoadingProgress(true);
                }
                break;
            case Player.STATE_READY:
                if (playWhenReady) {
                    // play
                    pauseImage.setVisibility(show ? View.VISIBLE : View.GONE);
                } else {
                    // pause
                    playImage.setVisibility(View.VISIBLE);
                }
                break;
            case Player.STATE_IDLE:
                playImage.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void startPlayAreaHideTimerTask() {
        playAreaHideTimerTask = new TimerTask() {
            public void run() {
                updatePlayArea();
            }

            public void updatePlayArea() {
                ThreadUtils.INSTANCE.runInUiThread(() -> {
                    if (isPause() || isEnded()) {
                        return;
                    }
                    showPlayControllerView(false);
                });
            }
        };

        // public void schedule (TimerTask task, long delay, long period)
        playAreaHideTimer.schedule(playAreaHideTimerTask, 2400);
    }

    private void stopPlayAreaHideTask() {
        if (playAreaHideTimerTask != null) {

            playAreaHideTimerTask.cancel();
            playAreaHideTimerTask = null;
        }
    }

    /**
     * 화면 탭처리
     */
    private void onTap() {
        // main 화면에서는 보여주지 않음.
        if (simpleExoPlayer == null || isPause() || isEnded()) {
            return;
        }

        if (callback != null) {
            callback.onTap(!playAreaView.isShown());
        }

        if (mediaInfo.channel != MediaInfo.CHANNEL_MAIN_LIVE) {
            showPlayControllerView(!playAreaView.isShown());
        }
    }

    /**
     * 미디어를 재생한다.
     */
    private void play() {
        mediaInfo.currentPosition = 0;
        mediaInfo.isPlaying = true;
        initializePlayer(mediaInfo.contentUri, mediaInfo.currentPosition);
        showPlayControllerView(false);
        callback.onPlayed();
    }

    private boolean isPause() {
        if (simpleExoPlayer == null) {
            return false;
        }
        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_READY && !playWhenReady;
    }

    private boolean isEnded() {
        if (simpleExoPlayer == null) {
            return false;
        }

        int playState = simpleExoPlayer.getPlaybackState();
        return playState == Player.STATE_ENDED;
    }

    private boolean isVod() {
        if(isEmpty(simpleExoPlayer) || simpleExoPlayer.getDuration() == C.TIME_UNSET) return true;
        return !simpleExoPlayer.isCurrentWindowDynamic();
    }

    @Override
    public MediaInfo getMediaInfo() {
        if (mediaInfo == null || TextUtils.isEmpty(mediaInfo.contentUri)) {
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

    @Override
    public boolean isPlaying() {
        if (simpleExoPlayer == null) {
            return false;
        }
        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_READY && playWhenReady;
    }

    /**
     * @param callback callback
     */
    @Override
    public void setOnMediaPlayerListener(OnMediaPlayerListener callback) {
        if (callback != null) {
            this.callback = callback;
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
            //callback.onBuffering(false);
            initializePlayer(mediaInfo.contentUri, mediaInfo.currentPosition);
            Ln.i("video : " + mediaInfo.contentUri);
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
        mediaInfo.isPlaying = false;
        releasePlayer();
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
                currentVolume = simpleExoPlayer.getVolume();
                simpleExoPlayer.setVolume(0f);
            } else {
                if (currentVolume > 0) {
                    simpleExoPlayer.setVolume(currentVolume);
                }
            }
        }
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
                    callback.onBuffering(true);
                    showLoadingProgress(true);
                    break;
                case Player.STATE_READY:
                    initUI();
                    stateString = "ExoPlayer.STATE_READY     -";
                    callback.onBuffering(false);
                    showLoadingProgress(false);
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    callback.onFinished(getMediaInfo());
                    break;
                default:
                    showLoadingProgress(false);
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
            Ln.e(e);
            if (isNotEmpty(callback)) {
                callback.onError(e);
            }
        }

        @Override
        public void preparePlayback() {

        }

        @Override
        public void onVisibilityChange(int visibility) {

        }
    }

    @Override
    public void setVideoPause()
    {
        pauseImage.setVisibility(View.GONE);
        playImage.setVisibility(View.VISIBLE);

        callback.onPaused();

        releasePlayer();
    }
}
