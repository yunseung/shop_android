package gsshop.mobile.v2.support.media.exoplayer;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExoNalbangMediaPlayerControllerFragment extends BaseFragment implements OnMediaPlayerController {

    private float currentVolume;

    private OnMediaPlayerListener callback;

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

    private MediaInfo mediaInfo;

    // exo player
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private ComponentListener componentListener;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private boolean isPrepared = false;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private View rootView;

    public ExoNalbangMediaPlayerControllerFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exo_nalbang_media_player_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view.findViewById(R.id.root_view);
        playerView = view.findViewById(R.id.exoplayer_video_view);

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
                    NetworkUtils.confirmNetworkBillingAndShowPopup(getActivity(), new NetworkUtils.OnConfirmNetworkListener() {
                        @Override
                        public void isConfirmed(boolean isConfirmed) {
                            if (isConfirmed) {
                                play();
                            }
                        }

                        @Override
                        public void inCanceled() {
                        }
                    });
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

    /**
     * 미디어를 재생한다.
     */
    private void play() {
        mediaInfo.isPlaying = true;
        initializePlayer(mediaInfo.contentUri);
        pauseImage.setVisibility(View.VISIBLE);
        playImage.setVisibility(View.GONE);
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

    private void showLoadingProgress(boolean show) {
        if (show) {
            ViewUtils.showViews(loadingProgress);
        } else {
            ViewUtils.hideViews(loadingProgress);
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

    /**
     * 동영상을 재생한다.
     */
    @Override
    public void playPlayer() {
        if (isEmpty(mediaInfo)) {
            throw new IllegalArgumentException("The media info is null!!");
        }

        if (!TextUtils.isEmpty(mediaInfo.contentUri)) {
            initializePlayer(mediaInfo.contentUri);
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
                        initUI();
                        if (callback != null) {
                            callback.onSetVideoType(isVod());
                        }
                        if(isVod() && mediaInfo.currentPosition > 0) {
                            simpleExoPlayer.seekTo(mediaInfo.currentPosition);
                        }

                        isPrepared = true;
                        setMute(false);
                    }
                    showLoadingProgress(false);
                    pauseImage.setVisibility(View.VISIBLE);
                    playImage.setVisibility(View.GONE);
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    if (callback != null) {
                        callback.onFinished(getMediaInfo());
                    }
                    releasePlayer();
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
            initUI();
            pauseImage.setVisibility(View.GONE);
            playImage.setVisibility(View.VISIBLE);
            if (callback != null) {
                callback.onError(null);
            }

            playImage.setEnabled(false);

            //전체화면인 경우 액티비티 종료
            if (mediaInfo.playerMode == MediaInfo.MODE_FULL) {
                getActivity().finish();
            }
        }

        @Override
        public void preparePlayback() {

        }

        @Override
        public void onVisibilityChange(int visibility) {
            if (callback != null) {
                callback.onTap(visibility == View.VISIBLE);
            }
        }
    }



}
