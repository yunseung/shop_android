package gsshop.mobile.v2.support.media.brightcove;

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
import android.widget.ImageButton;
import android.widget.ImageView;
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

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * 날방용 video play controller fragment
 */
@SuppressLint("NewApi")
public class NalbangMediaPlayerControllerFragment extends BrightcovePlayerFragment implements OnMediaPlayerController {

    private float currentVolume;
    private int duration = 0;

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
    private View playControllerView;
    private View playControllerVod;
    private View playControllerHls;


    // fullscreen
    private ImageButton fullScreenButtonVod;
    private ImageButton fullScreenButtonHls;


    // title
    private View titleView;
    private TextView titleText;
    private ImageView backImage;

    private SimpleExoPlayer simpleExoPlayer;

    private MediaInfo mediaInfo;

    public NalbangMediaPlayerControllerFragment() {
        mediaInfo = new MediaInfo();
        mediaInfo.currentPosition = -1;
        mediaInfo.lastPlaybackState = Player.STATE_IDLE;
        mediaInfo.playerMode = MediaInfo.MODE_SIMPLE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_nalbang_media_player_controller, container, false);
        baseVideoView = result.findViewById(R.id.brightcove_video_view);
        baseVideoView.setMediaController(new BrightcoveMediaController(baseVideoView, R.layout.brightcove_nalbang_media_controller));

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
        playControllerView = view.findViewById(R.id.view_play_controller);
        playControllerVod = view.findViewById(R.id.view_play_controller_vod);
        playControllerHls = view.findViewById(R.id.view_play_controller_hls);

        // fullscreen
        fullScreenButtonVod = view.findViewById(R.id.button_fullscreen_vod);
        fullScreenButtonHls = view.findViewById(R.id.button_fullscreen_hls);

        // title
        titleView = view.findViewById(R.id.view_title);
        titleText = view.findViewById(R.id.text_title);
        backImage = view.findViewById(R.id.image_back);

        tapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (baseVideoView.getBrightcoveMediaController() == null) {
                    return;
                }

                boolean isShow = baseVideoView.getBrightcoveMediaController().isShowing();
                if (isShow) {
                    baseVideoView.getBrightcoveMediaController().show();
                    if (isPlaying()) {
                        //재생중인 상태일때만 호출
                        onTap();
                    }
                } else {
                    if (isPlaying()) {
                        baseVideoView.getBrightcoveMediaController().hide();
                        onTap();
                    }
                }
            }
        });

        fullScreenButtonVod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFullScreenClick(isPlaying());
            }
        });

        fullScreenButtonHls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFullScreenClick(isPlaying());
            }
        });

        // play
        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPlayButtonClicked();

                //라이브톡, 날방의 경우 플레이버튼 클릭시마다 네트워크 체크를 수행한다.
                if (!NetworkStatus.isWifiConnected(getActivity())) {
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
                pauseImage.setVisibility(View.GONE);
                playImage.setVisibility(View.VISIBLE);

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

        baseVideoView.getEventEmitter().on(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                //비디오 세팅시 컨트롤러 노출 비활성화 (최초 구동시 노출 안시킴)
                baseVideoView.getBrightcoveMediaController().setShowControllerEnable(false);
                setUI(true, true);

                ExoPlayerVideoDisplayComponent displayComponent = (ExoPlayerVideoDisplayComponent) baseVideoView.getVideoDisplay();
                ExoPlayer exoPlayer = displayComponent.getExoPlayer();
                if (exoPlayer instanceof SimpleExoPlayer) {
                    simpleExoPlayer = (SimpleExoPlayer) exoPlayer;
                }
            }
        });

        baseVideoView.getEventEmitter().on(EventType.VIDEO_DURATION_CHANGED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                int tempDuration = (int) event.getProperties().get("duration");
                if(duration == tempDuration) {
                    return;
                }
                
                duration = tempDuration;
                initUI();

                if (isVod() && mediaInfo.currentPosition > 0) {
                    baseVideoView.pause();
                    baseVideoView.seekTo((int) mediaInfo.currentPosition);
                    baseVideoView.start();
                } else {
                    baseVideoView.start();
                }

                if (callback != null) {
                    callback.onSetVideoType(isVod());
                }
            }
        });

        baseVideoView.getEventEmitter().on(EventType.DID_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                setUI(true, false);

                if (mediaInfo.isPlaying) {
                    setMute(false);
                } else {
                    setMute(true);
                    stopPlayer();
                }
            }
        });


        baseVideoView.getEventEmitter().on(EventType.DID_SEEK_TO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (!mediaInfo.isPlaying) {
                    setMute(true);
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

                setUI(false, false);
            }
        });

        baseVideoView.getEventEmitter().on(EventType.BUFFERING_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                showLoadingProgress(true);
            }
        });

        baseVideoView.getEventEmitter().on(EventType.BUFFERING_COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                showLoadingProgress(false);
            }
        });

        baseVideoView.getEventEmitter().on(EventType.COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
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
                    initUI();
                    setUI(false, false);
                    if (callback != null) {
                        callback.onError(null);
                    }

                    playImage.setEnabled(false);

                    //전체화면인 경우 액티비티 종료
                    if (mediaInfo.playerMode == MediaInfo.MODE_FULL) {
                        getActivity().finish();
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
            callback.onTap(playControllerView.isShown());
        }
    }

    /**
     * 미디어를 재생한다.
     */
    private void play() {
        mediaInfo.currentPosition = -1;
        mediaInfo.isPlaying = true;
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
                        dialog.dismiss();
                    }
                }).negativeButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    private boolean isVod() {
        return duration >= 0;
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
                    duration = 0;
                    baseVideoView.clear();
                    baseVideoView.add(video);

                }
            });
            Ln.i("video : " + mediaInfo.videoId);
        } else if (!TextUtils.isEmpty(mediaInfo.contentUri)) {
            Video video = Video.createVideo(mediaInfo.contentUri);
            duration = 0;
            baseVideoView.clear();
            baseVideoView.add(video);
            Ln.i("video : " + mediaInfo.contentUri);
        } else {
            //비디오정보가 없는 경우
            Toast.makeText(getActivity(), R.string.video_play_error, Toast.LENGTH_SHORT)
                    .show();
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
     * 컨트롤러 노출/숨김 처리
     *
     * @param isHide if true, 컨트롤러 숨김
     * @param isFirst if true, 화면진입후 최초 재생시 컨트롤로 숨김
     */
    private void setUI(boolean isHide, boolean isFirst) {
        if (isHide) {
            pauseImage.setVisibility(View.VISIBLE);
            playImage.setVisibility(View.GONE);
            baseVideoView.getBrightcoveMediaController().setHideControllerEnable(true);
            baseVideoView.getBrightcoveMediaController().hide();
            //재생시작되면 컨트롤러 노출 활성화
            if (!isFirst) {
                baseVideoView.getBrightcoveMediaController().setShowControllerEnable(true);
            }
        } else {
            //딜레이를 안줄경우 컨트롤러 노출이 안되는 경우가 발생
            setUIWithDelay();
        }
    }

    private void setUIWithDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            pauseImage.setVisibility(View.GONE);
            playImage.setVisibility(View.VISIBLE);
            baseVideoView.getBrightcoveMediaController().setShowControllerEnable(true);
            baseVideoView.getBrightcoveMediaController().show();
            baseVideoView.getBrightcoveMediaController().setHideControllerEnable(false);
        }, 300);
    }

    /**
     * 풀화면인지 여부에 따라 UI를 달리 세팅한다.
     */
    private void initUI() {
        if (mediaInfo.playerMode == MediaInfo.MODE_FULL) {
            titleView.setVisibility(View.VISIBLE);
            titleText.setText(mediaInfo.title);
            if (isVod()) {
                // vod
                fullScreenButtonVod.setImageResource(R.drawable.btn_resizing2_720);
            } else {
                // hls
                fullScreenButtonHls.setImageResource(R.drawable.btn_resizing2_720);
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
}
