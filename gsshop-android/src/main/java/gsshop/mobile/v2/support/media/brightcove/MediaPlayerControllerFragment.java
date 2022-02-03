package gsshop.mobile.v2.support.media.brightcove;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brightcove.player.appcompat.BrightcovePlayerFragment;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.Video;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.net.URI;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * mp4 video play controller fragment
 */
@SuppressLint("NewApi")
public class MediaPlayerControllerFragment extends BrightcovePlayerFragment implements OnMediaPlayerController {

    private float currentVolume;
    private int duration = -1;

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

    // 사운드 버튼
    private CheckBox chkMute;

    // title
    private View titleView;
    private TextView titleText;
    private ImageView backImage;

    private SimpleExoPlayer simpleExoPlayer;

    private MediaInfo mediaInfo;

    // 닫기 버튼
    protected View btnClose;
    protected LinearLayout lay_btn_close;

    public MediaPlayerControllerFragment() {
        mediaInfo = new MediaInfo();
        mediaInfo.currentPosition = -1;
        mediaInfo.lastPlaybackState = Player.STATE_IDLE;
        mediaInfo.playerMode = MediaInfo.MODE_SIMPLE;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_media_player_controller, container, false);
        baseVideoView = result.findViewById(R.id.brightcove_video_view);
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        //음소거
        chkMute = view.findViewById(R.id.check_vod_player_mute);

        //닫기버튼
        btnClose = view.findViewById(R.id.btn_close);
        lay_btn_close = view.findViewById(R.id.lay_btn_close);

        //닫기
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onFinished(getMediaInfo());
                }
            }
        });

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
                    play();
                }
            }
        });

        // pause
        pauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPaused();
                stopPlayer();
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

        chkMute.setChecked(MainApplication.isLiveTalkMute);
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

        playAreaView.setVisibility(View.GONE);
        baseVideoView.setMediaController((BrightcoveMediaController) null);
        baseVideoView.getEventEmitter().on(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (isVod() && mediaInfo.currentPosition > 0) {
                    baseVideoView.pause();
                    baseVideoView.seekTo((int) mediaInfo.currentPosition);
                } else {
                    baseVideoView.start();
                }

                ExoPlayerVideoDisplayComponent displayComponent = (ExoPlayerVideoDisplayComponent) baseVideoView.getVideoDisplay();
                ExoPlayer exoPlayer = displayComponent.getExoPlayer();
                if (exoPlayer instanceof SimpleExoPlayer) {
                    simpleExoPlayer = (SimpleExoPlayer) exoPlayer;
                }
                showPlayControllerView(false);
                showPlayControllerView(playAreaView.isShown());
            }
        });

        baseVideoView.getEventEmitter().on(EventType.VIDEO_DURATION_CHANGED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                duration = (int) event.getProperties().get("duration");
                initUI();
            }
        });

        baseVideoView.getEventEmitter().on(EventType.DID_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (mediaInfo.isPlaying) {
                    setMute(MainApplication.isLiveTalkMute);
                    // play
                    showPlayControllerView(false);
                } else {
                    setMute(MainApplication.isLiveTalkMute);
                    stopPlayer();
                }

            }
        });

        baseVideoView.getEventEmitter().on(EventType.DID_SEEK_TO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (!mediaInfo.isPlaying) {
                    setMute(MainApplication.isLiveTalkMute);
                    stopPlayer();
                } else {
                    baseVideoView.start();
                }
            }
        });

        /**
         * 동영상 실행중에 pause 이벤트를 받으면 동영상 상태 정보를 실행으로 유지
         */
        baseVideoView.getEventEmitter().on(EventType.DID_PAUSE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // pause
                // 초기 플레이가 불러질 때 시간 설정.
                showPlayControllerView(true);
                if (isVod()) {
                    long position = (long) event.getProperties().get("playheadPosition");
                    mediaInfo.currentPosition = position;
                }
            }
        });

        baseVideoView.getEventEmitter().on(EventType.DID_STOP, new EventListener() {
            @Override
            public void processEvent(Event event) {
                mediaInfo.isPlaying = false;
                if (isVod()) {
                    long position = (long) event.getProperties().get("playheadPosition");
                    mediaInfo.currentPosition = position;
                }
            }
        });

        baseVideoView.getEventEmitter().on(EventType.BUFFERING_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                showPlayControllerView(playAreaView.isShown());
                callback.onBuffering(true);
            }
        });

        baseVideoView.getEventEmitter().on(EventType.BUFFERING_COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                showPlayControllerView(false);
                callback.onBuffering(false);
            }
        });

        baseVideoView.getEventEmitter().on(EventType.COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                showPlayControllerView(true);
                if (callback != null) {
                    callback.onFinished(getMediaInfo());
                }
            }
        });

        baseVideoView.getEventEmitter().on(EventType.SET_VIDEO_STILL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                URI videoStillUrl = (URI) baseVideoView.getCurrentVideo().getProperties().get("stillImageUri");
                if (isEmpty(videoStillUrl)) {
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        baseVideoView.getEventEmitter().on(EventType.ERROR, new EventListener() {
            @Override
            public void processEvent(Event event) {
                //비디오번호가 유효하지 않는 등 에러 발생시
                Map<String, Object> map = event.getProperties();
                String errCode = (String) map.get("error_code");
                if ("RESOURCE_NOT_FOUND".equals(errCode) || "VIDEO_NOT_FOUND".equals(errCode)) {
                    Toast.makeText(getActivity(), R.string.video_play_error, Toast.LENGTH_SHORT)
                            .show();
                    showLoadingProgress(false);

                    if (callback != null) {
                        callback.onError(null);
                    }
                }
            }
        });
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
        mediaInfo.currentPosition = -1;
        mediaInfo.isPlaying = true;
        showPlayControllerView(false);
        baseVideoView.start();
        callback.onPlayed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (callback != null) {
            return;
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            callback = (OnMediaPlayerListener) context;
        } catch (ClassCastException e) {
            callback = null;
        }
    }

    private void onFullScreenClick(boolean isPlaying) {
        new Handler().postDelayed(() -> {
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

    private boolean isVod() {
        return duration >= 0;
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
        }
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

        //videoId가 videoUrl보다 우선순위
        if (!TextUtils.isEmpty(mediaInfo.videoId) && mediaInfo.videoId.length() > 4) {
            // Get the event emitter from the SDK and create a catalog request to fetch a video from the
            // Brightcove Edge service, given a video id, an account id and a policy key.
            EventEmitter eventEmitter = baseVideoView.getEventEmitter();
            Catalog catalog = new Catalog(eventEmitter, getString(R.string.account), getString(R.string.policy));

            catalog.findVideoByID(mediaInfo.videoId, new VideoListener() {

                // Add the video found to the queue with add().
                // Start playback of the video with start().
                @Override
                public void onVideo(Video video) {
                    duration = -1;
                    baseVideoView.clear();
                    baseVideoView.add(video);

                }
            });
            Ln.i("video : " + mediaInfo.videoId);
        } else if (!TextUtils.isEmpty(mediaInfo.contentUri)) {
            Video video = Video.createVideo(mediaInfo.contentUri);
            duration = -1;
            baseVideoView.clear();
            baseVideoView.add(video);
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

    /**
     * 동영상 재생을 정지한다.
     */
    @Override
    public void stopPlayer() {
        baseVideoView.pause();
        baseVideoView.stopPlayback();
    }

    /**
     * 풀화면인지 여부에 따라 UI를 달리 세팅한다.
     */
    public void initUI() {
        if (mediaInfo.playerMode == MediaInfo.MODE_FULL) {
           // titleView.setVisibility(View.VISIBLE);
            titleText.setText(mediaInfo.title);
            if (isVod()) {
                // vod
                fullScreenButtonVod.setImageResource(R.drawable.btn_resizing2_720);
            } else {
                // hls
                fullScreenButtonHls.setImageResource(R.drawable.ic_out_android);

            }
        } else {
            titleView.setVisibility(View.GONE);
            titleText.setText(mediaInfo.title);
            if (isVod()) {
                // vod
                fullScreenButtonVod.setImageResource(R.drawable.btn_resizing1_720);
            } else {
                // hls
                fullScreenButtonHls.setImageResource(R.drawable.btn_resizing1_720);
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

    @Override
    public void setMediaInfo(MediaInfo media) {
        mediaInfo = media;
    }

    @Override
    public View getPlayerView() {
        return getView();
    }

    @Override
    public MediaInfo getMediaInfo() {
        if (mediaInfo == null
                || (TextUtils.isEmpty(mediaInfo.contentUri) && TextUtils.isEmpty(mediaInfo.videoId))) {
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

    /**
     * player state
     *
     * @return boolean
     */
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

    private boolean isPause() {
        if (simpleExoPlayer == null) {
            return false;
        }
        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_READY && !playWhenReady;
    }

    private boolean isBuffering() {
        if (simpleExoPlayer == null) {
            return false;
        }

        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_BUFFERING && playWhenReady;
    }

    private boolean isEnded() {
        if (simpleExoPlayer == null) {
            return false;
        }

        int playState = simpleExoPlayer.getPlaybackState();
        return playState == Player.STATE_ENDED;
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

    private void startPlayAreaHideTimerTask() {
        playAreaHideTimerTask = new TimerTask() {
            public void run() {
                updatePlayArea();
            }

            public void updatePlayArea() {
                new Handler(Looper.getMainLooper()).post(() -> {
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
}
