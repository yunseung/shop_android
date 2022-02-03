package gsshop.mobile.v2.support.media.exoplayer;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * bestdeal mobile live video play controller fragment
 */
public class ExoBestdealMobileLiveMediaPlayerControllerFragment extends Fragment implements OnMediaPlayerController {
    private float currentVolume;

    // full screen listener
    private OnMediaPlayerListener callback;

    //for tap event
    private View tapView;

    // keep screen on when playing
    private View rootView;

    // video loading bar
    private ProgressBar loadingProgress;

    // media info
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

    private int currentWindow;
    private boolean playWhenReady = true;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    public ExoBestdealMobileLiveMediaPlayerControllerFragment() {
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
        return inflater.inflate(R.layout.fragment_exo_best_deal_mobile_live_media_player_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // keep screen on
        rootView = view.findViewById(R.id.root_view);
        playerView = view.findViewById(R.id.exoplayer_video_view);

        tapView = view.findViewById(R.id.tapview);
        loadingProgress = view.findViewById(R.id.mc_webview_progress_bar);

        tapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTap();
            }
        });

        playerView.setUseController(false);
        //The height is fixed and the width is increased or decreased to obtain the desired aspect ratio.
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);

        showLoadingProgress(false);
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
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer(mediaInfo.contentUri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) {
            initializePlayer(mediaInfo.contentUri);
        }
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

    private void initializePlayer(String videoUrl) {
        if (simpleExoPlayer == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // using a DefaultTrackSelector with an adaptive video selection factory
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(MainApplication.getAppContext()),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());
            simpleExoPlayer.addListener(componentListener);
            playerView.setPlaybackPreparer(componentListener);
            playerView.setControllerVisibilityListener(componentListener);
            playerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.seekTo(currentWindow, 0);
        }
        /*영역화면과 전체화면 전환시 플레이상태를 유지하기 위해 세팅
          영역화면 재생상태에서 전체화면 전환시 전체화면도 재생(반대도 동일)*/
        if (mediaInfo != null) {
            playWhenReady = mediaInfo.isPlaying;
        }
        simpleExoPlayer.setPlayWhenReady(playWhenReady);
        if (isNotEmpty(videoUrl)) {
            MediaSource mediaSource = buildMediaSource(videoUrl);
            simpleExoPlayer.prepare(mediaSource, true, false);
        }
        initMuteFromGlobal();
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


    /**
     * 화면 탭처리
     */
    private void onTap() {
        // main 화면에서는 보여주지 않음.
        if (simpleExoPlayer == null || isPause() || isEnded()) {
            return;
        }

        if (callback != null) {
            callback.onTap();
        }

    }

    /**
     * 미디어를 재생한다.
     */
    private void play() {
        mediaInfo.currentPosition = 0;
        mediaInfo.isPlaying = true;
        initializePlayer(mediaInfo.contentUri);
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
        if (isEmpty(simpleExoPlayer) || simpleExoPlayer.getDuration() == C.TIME_UNSET) return true;
        return !simpleExoPlayer.isCurrentWindowDynamic();
    }

    @Override
    public MediaInfo getMediaInfo() {
        if (mediaInfo == null/* || TextUtils.isEmpty(mediaInfo.contentUri)*/) {
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
    public void setMediaInfo(MediaInfo media) {
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
            initializePlayer(mediaInfo.contentUri);
            //Ln.i("video : " + mediaInfo.contentUri);
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
                if(simpleExoPlayer.getVolume() > 0) {
                    currentVolume = simpleExoPlayer.getVolume();
                }
                simpleExoPlayer.setVolume(0f);
            } else {
                if (currentVolume > 0) {
                    simpleExoPlayer.setVolume(currentVolume);
                }
            }
            if(isNotEmpty(callback)) {
                callback.onMute(on);
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
                    if (callback != null) {
                        callback.onBuffering(true);
                    }
                    showLoadingProgress(true);
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    if (callback != null) {
                        callback.onBuffering(false);
                        if(!playWhenReady) {
                            callback.onPaused();
                        }
                    }
                    showLoadingProgress(false);

                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    if (callback != null) {
                        callback.onFinished(getMediaInfo());
                    }
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

            //Ln.d("changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            //Ln.e(e);
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
}