package gsshop.mobile.v2.support.media.exoplayer;


import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

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
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExoLiveMediaPlayerControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExoLiveMediaPlayerControllerFragment extends BaseFragment implements OnMediaPlayerController {
    protected OnMediaPlayerListener mListener;

    //로딩바
    private ProgressBar loadingProgress;

    // 재생,정지 버튼
    protected CheckBox chkPlay;

    // 사운드 버튼
    protected CheckBox chkMute;
    private LinearLayout lay_chk_mute;

    // 닫기 버튼
    protected View btnClose;
    protected LinearLayout lay_btn_close;

    protected View outView;
    private LinearLayout lay_out_view;

    private float currentVolume = 0f;

    //  처음 한번 prepared 여부 체크 Nalbang에서 이렇게 한거 가져옴.
    private boolean isPrepared = false;

    // onResume 시에 플레이를 계속 할지 여부
    protected boolean isPlayWhenOnResume = false;

    private boolean useMuteFromGlobal = true;

    /**
     * 액티비티로부터 전달받는 파라미터 정의
     */
    protected static final String ARG_PARAM_VIDEO_URL = "_arg_param_video_url";   //비디오주소
    protected static final String ARG_PARAM_IS_PLAYING = "_arg_param_is_playing";     // 동영상 실행 유무

    // vidoe url
    private String videoUrl;

    // exo player
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    protected SimpleExoPlayer player;
    protected PlayerView playerView;
    protected DefaultTrackSelector mPlayerSelector;
    private ComponentListener componentListener;

    private long playbackPosition;
    private int currentWindow;
    protected boolean playWhenReady = false;

    protected static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private View rootView;
    private View mobileDataView;
    private Button mobileDataCancelButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */

    public static Fragment newInstance(String videoUrl, boolean isPlaying) {
        ExoLiveMediaPlayerControllerFragment fragment = new ExoLiveMediaPlayerControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_VIDEO_URL, videoUrl);
        args.putBoolean(ARG_PARAM_IS_PLAYING, isPlaying);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoUrl = getArguments().getString(ARG_PARAM_VIDEO_URL);
            playWhenReady = getArguments().getBoolean(ARG_PARAM_IS_PLAYING, false);
        }

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        componentListener = new ComponentListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_exo_live_media_player_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view.findViewById(R.id.root_view);
        playerView = view.findViewById(R.id.exoplayer_video_view);
        loadingProgress = view.findViewById(R.id.mc_webview_progress_bar);
        chkMute = view.findViewById(R.id.chk_mute);
        lay_chk_mute = view.findViewById(R.id.lay_chk_mute);
        outView = view.findViewById(R.id.view_controller_out);
        lay_out_view = view.findViewById(R.id.lay_view_controller_out);
        btnClose = view.findViewById(R.id.btn_close);
        lay_btn_close = view.findViewById(R.id.lay_btn_close);
        mobileDataView = view.findViewById(R.id.view_live_full_player_mobile_data);
        mobileDataCancelButton = mobileDataView.findViewById(R.id.button_mobile_data_cancel);

        ViewUtils.hideViews(mobileDataView);
        initializePlayer();

        //닫기, 음소거, 확대축소 터치영역 확대
        expandTouchArea(lay_btn_close, btnClose,100);
        expandTouchArea(lay_chk_mute, chkMute,100);
        expandTouchArea(lay_out_view, outView,100);

        setPlayerView(view);

        //사운드 On/Off
        chkMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.isMute = chkMute.isChecked();
                setMute(chkMute.isChecked());
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
            }
        });

        //닫기
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFinished(getMediaInfo());
                }
            }
        });

        //확대/축소
        outView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFinished(getMediaInfo());
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
                playerView.showController();
                chkPlay.setChecked(true);
            }
        });

        mobileDataView.findViewById(R.id.button_mobile_data_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideViews(mobileDataView);
                MainApplication.isNetworkApproved = true;
                player.setPlayWhenReady(true);
            }
        });
    }

    protected void setPlayerView(View view) {
        chkPlay = view.findViewById(R.id.chk_play);
        //재생/일시정지 버튼
        if (chkPlay == null) {
            return;
        }
        chkPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chkPlay.isChecked()) {
                    confirmNetworkBilling(getContext());
                } else {
                    player.setPlayWhenReady(false);
                }
            }
        });
    }

    /**
     * 터치영역 확대
     * @param bigView
     * @param smallView
     * @param extraPadding
     */
    public  void expandTouchArea(View bigView, View smallView, int extraPadding) {
        bigView.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                smallView.getHitRect(rect);
                rect.top -= extraPadding;
                rect.left -= extraPadding;
                rect.right += extraPadding;
                rect.bottom += extraPadding;
                bigView.setTouchDelegate(new TouchDelegate(rect, smallView));
            }
        });
    }

    private void confirmNetworkBilling(Context context) {
        if ((!NetworkStatus.isWifiConnected(context) && !MainApplication.isNetworkApproved)) {
            playerView.hideController();
            ViewUtils.showViews(mobileDataView);
        } else {
            player.setPlayWhenReady(true);
        }
    }

    private void initMuteFromGlobal() {
        if (!useMuteFromGlobal) {
            return;
        }
        if (MainApplication.isMute != chkMute.isChecked()) {
            chkMute.setChecked(MainApplication.isMute);
            setMute(MainApplication.isMute);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMediaPlayerListener) {
            this.mListener = (OnMediaPlayerListener) context;
        } else {
            // 리스너 설정 못한다고 Exception 날리는게 이해가 안간다.
            // context에 리스너가 안달릴수도 있는데 왜 이렇게 했을까..
            // 애초에 context에 달리게끔 했다고 해도 함부로 Exception을 날리면 어떻게 하나.
//            throw new ClassCastException(
//                    "Parent container must implement the OnMediaPlayerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected boolean isReleasePlayerWhenStoped = true;

    @Override
    public void onResume() {
        super.onResume();
        if (player == null) {
            playWhenReady = isPlayWhenOnResume;
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23 && isReleasePlayerWhenStoped) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23 && isReleasePlayerWhenStoped) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isReleasePlayerWhenStoped) {
            releasePlayer();
        }
    }

    protected void initializePlayer() {
        if (player == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // using a DefaultTrackSelector with an adaptive video selection factory
            mPlayerSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()),
                    mPlayerSelector, new DefaultLoadControl());
            player.addListener(componentListener);
            playerView.setControllerVisibilityListener(componentListener);
            playerView.setPlayer(player);
        }

        if (chkPlay != null) {
            chkPlay.setChecked(!playWhenReady);
        }
        initMuteFromGlobal();
        player.setPlayWhenReady(playWhenReady);
        MediaSource mediaSource = buildMediaSource(videoUrl);
        player.prepare(mediaSource, true, false);
    }

    protected void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.release();
            player = null;
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


    /**
     * 미디어 정보를 반환한다.
     *
     * @return MediaInfo
     */
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
     * 사운드를 재생/뮤트 시킨다.
     *
     * @param on on이면 mute
     */
    @Override
    public void setMute(boolean on) {
        // audio
        if (player != null) {
            if (on) {
                currentVolume = player.getVolume();
                player.setVolume(0f);
            } else {
                if (currentVolume > 0) {
                    player.setVolume(currentVolume);
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
        if (player == null) {
            return false;
        }
        int playState = player.getPlaybackState();
        boolean playWhenReady = player.getPlayWhenReady();
        return playState == Player.STATE_READY && playWhenReady;
    }

    @Override
    public void playPlayer() {
        if (player == null) {
            initializePlayer();
        }
        player.setPlayWhenReady(true);
    }

    @Override
    public void stopPlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public long getCurrentPosition() {
        if(player != null) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Internal player
     *
     * @param show show
     */
    protected void showLoadingProgress(boolean show) {
        show = false;
        if (show) {
            ViewUtils.showViews(loadingProgress);
        } else {
            ViewUtils.hideViews(loadingProgress);
        }
    }

    protected boolean isVod() {
        if(isEmpty(player) || player.getDuration() == C.TIME_UNSET) return true;
        return !player.isCurrentWindowDynamic();
    }

    private class ComponentListener extends Player.DefaultEventListener implements PlayerControlView.VisibilityListener {
        // PlaybackControlView.PlaybackPreparer implementation

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            ExoLiveMediaPlayerControllerFragment.this.playWhenReady = playWhenReady;
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
                    if (playWhenReady) {
                        if(mListener != null) {
                            mListener.onPlayed();
                        }
                    }
                    if (isNotEmpty(chkPlay)) {
                        chkPlay.setChecked(!playWhenReady);
                    }
                    showLoadingProgress(false);
                    if (MainApplication.gVideoCurrentPosition > 0 && !isPrepared && isVod()) {
                        isPrepared = true;
                        player.seekTo(MainApplication.gVideoCurrentPosition);
                    }
                    stateString = "ExoPlayer.STATE_READY     -";
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    onStateFinished();
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
            if (mListener != null) {
                mListener.onError(e);
            }
        }

        @Override
        public void onVisibilityChange(int visibility) {
            if (mListener != null) {
                mListener.onTap(visibility == View.VISIBLE);
            }
        }
    }

    protected String getVideoUrl() {
        return videoUrl;
    }

    protected void onStateFinished() {
        if (mListener != null) {
            mListener.onFinished(getMediaInfo());
        }
    }

    @Override
    public void setUseController(boolean useController) {
        playerView.setUseController(useController);
    }

    @Override
    public void setPlayerSize(boolean isLow) {
        DefaultTrackSelector.Parameters builder;
        if(isLow) {
            builder = new DefaultTrackSelector.ParametersBuilder().setMaxVideoSizeSd().build();
        }
        else {
            builder = new DefaultTrackSelector.ParametersBuilder().build();
        }
        mPlayerSelector.setParameters(builder);
    }

    @Override
    public void setPlayerResizeMode(int resizeMode) {
        playerView.setResizeMode(resizeMode);
    }

    @Override
    public void setBackgroudnColor(int color) {
        playerView.setBackgroundColor(color);
    }

    @Override
    public void setPlayerWhenOnResume(boolean isStart) {
        isPlayWhenOnResume = isStart;
    }

    @Override
    public void release() {
        releasePlayer();
    }

    @Override
    public void setUseMuteFromGlobal(boolean useMuteFromGlobal) {
        this.useMuteFromGlobal = useMuteFromGlobal;
    }
}
