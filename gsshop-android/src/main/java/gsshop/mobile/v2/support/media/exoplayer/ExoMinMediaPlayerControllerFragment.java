package gsshop.mobile.v2.support.media.exoplayer;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
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
import com.gsshop.mocha.ui.util.ViewUtils;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.ImageUtil;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExoMinMediaPlayerControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExoMinMediaPlayerControllerFragment extends BaseFragment implements OnMediaPlayerController {
    private static final String ARG_PARAM_VIDEO_ID = "_arg_param_video_id";   //비디오번호
    private static final String ARG_PARAM_VIDEO_URL = "_arg_param_video_url";
    private static final String ARG_PARAM_THUMBNAIL_URL = "_arg_param_thumbnail_url";
    private static final String ARG_PARAM_A_URL = "_a";

    private float currentVolume = 0f;

    private boolean playerNeedsPrepare;

    private OnMediaPlayerListener callback;


    private ProgressBar loadingProgress;
    private ImageView thumbImage;
    private TextView positionText;
    private View rootView;

    private boolean isPrepared = false;

    // vidoe id
    private String videoId;

    // vidoe url
    private String videoUrl;

    // thumnail Url
    private String thumbUrl;

    // play Position
    private String mPosition;


    public static boolean isMute = true;

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

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static ExoMinMediaPlayerControllerFragment newInstance(String videoId, String videoUrl, String thumbUrl, String position) {
        ExoMinMediaPlayerControllerFragment fragment = new ExoMinMediaPlayerControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_VIDEO_ID, videoId);
        args.putString(ARG_PARAM_VIDEO_URL, videoUrl);
        args.putString(ARG_PARAM_THUMBNAIL_URL, thumbUrl);
        args.putString(ARG_PARAM_A_URL, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoId = getArguments().getString(ARG_PARAM_VIDEO_ID);
            videoUrl = getArguments().getString(ARG_PARAM_VIDEO_URL);
            thumbUrl = getArguments().getString(ARG_PARAM_THUMBNAIL_URL);
            mPosition = getArguments().getString(ARG_PARAM_A_URL);
        }
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        componentListener = new ComponentListener();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exo_min_media_player_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view.findViewById(R.id.root_view);
        playerView = view.findViewById(R.id.exoplayer_video_view);

        loadingProgress = view.findViewById(R.id.mc_webview_progress_bar);
        thumbImage = view.findViewById(R.id.image_thumb);
        positionText = view.findViewById(R.id.text_position);

        playerView.setUseController(false);

        // thumbnail
        ImageUtil.loadImageTvLive(getActivity(), thumbUrl, thumbImage, R.drawable.noimg_logo_sb);

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
    public void onPause() {
        super.onPause();
        // background pause
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null && !isVisibleToUser) {
            // background pause
            releasePlayer();
        }
    }

    private void initializePlayer() {
        if (simpleExoPlayer == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // using a DefaultTrackSelector with an adaptive video selection factory
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());
            simpleExoPlayer.addListener(componentListener);
            playerView.setControllerVisibilityListener(componentListener);
            playerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.setPlayWhenReady(playWhenReady);
            simpleExoPlayer.seekTo(currentWindow, playbackPosition);
        }
        MediaSource mediaSource = buildMediaSource(videoUrl);
        simpleExoPlayer.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            playbackPosition = simpleExoPlayer.getCurrentPosition();
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

    @Override
    public MediaInfo getMediaInfo() {
        return null;
    }

    @Override
    public void setMediaInfo(MediaInfo media) {

    }

    @Override
    public View getPlayerView() {
        return getView();
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
    public void playPlayer() {
        initializePlayer();
    }

    @Override
    public void stopPlayer() {
        if (isNotEmpty(simpleExoPlayer)) {
            simpleExoPlayer.stop();
        }
    }


    private void showLoadingProgress(boolean show) {
        if (show) {
            ViewUtils.showViews(loadingProgress);
        } else {
            ViewUtils.hideViews(loadingProgress);
        }
    }

    /**
     * 사운드를 재생/뮤트 시킨다.
     *
     * @param on on이면 mute
     */
    @Override
    public void setMute(boolean on) {
        isMute = on;
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

    public boolean isPause() {
        if (simpleExoPlayer == null) {
            return false;
        }
        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_READY && !playWhenReady;
    }

    public boolean isBuffering() {
        if (simpleExoPlayer == null) {
            return false;
        }

        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_BUFFERING && playWhenReady;
    }

    public boolean isEnded() {
        if (simpleExoPlayer == null) {
            return false;
        }

        int playState = simpleExoPlayer.getPlaybackState();
//        boolean playWhenReady = player.getPlayWhenReady();
        return playState == Player.STATE_ENDED;
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

    private class ComponentListener extends Player.DefaultEventListener implements PlayerControlView.VisibilityListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    if (playWhenReady) {
                        showLoadingProgress(true);
                        ViewUtils.hideViews(thumbImage);
                    }
                    break;
                case Player.STATE_READY:
                    if (!isPrepared) {
                        setMute(isMute);
                        isPrepared = true;
                    }
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    stateString = "ExoPlayer.STATE_READY     -";
                    if (playWhenReady) {
                        // play
                        ViewUtils.hideViews(thumbImage);
                        showLoadingProgress(false);
                        if (callback != null) {
                            callback.onPlayed();
                        }
                    }
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    ViewUtils.showViews(thumbImage);
                    if (callback != null) {
                        callback.onFinished(null);
                    }
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
            Ln.e(e);
            if (isNotEmpty(callback)) {
                callback.onError(e);
            }
        }


        @Override
        public void onVisibilityChange(int visibility) {

        }
    }
}
