package gsshop.mobile.v2.web.productDetail.video.live;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.exoplayer.ExoLiveMediaPlayerControllerFragment;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.productDetail.BroadTimeLayoutDetailView;
import gsshop.mobile.v2.web.productDetail.PagerAdapterDetailView;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_PRD_LIVE_MANUAL_PLAY;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_PRD_VOD_MANUAL_PLAY;

public class FragmentDetailViewExoLivePlayerController extends ExoLiveMediaPlayerControllerFragment implements View.OnClickListener {

    //for tap event
    private View tapView;
    // 플레이 컨트롤 영역
    private View playAreaView;
    // 사운드 버튼
    private CheckBox chkMuteThis;
    // 풀스크린 버튼
    private View fullScreenView;
    // 플레이 버튼
    private ImageView playButton;
    // 일시 정지 버튼
    private View pauseButton;
    // 다시 재생 버튼
    private View replayButton;

    // 시간 측정 텍스트
    private BroadTimeLayoutDetailView mCvReaminTime;
    private TextView mRemainedTimeText;
    private TextView mEndText;
    private View mViewPlayBtnArea;
    // 전체 화면 / 음소거 버튼 영역
    private View bottomView;

    // 단품 상단에 붙는 미니 플레이어일 경우에 컨트롤러 영역이 보이지 않도록 하는 flag
    private boolean mControllerVisible = true;
    // 방송 종료 여부 확인을 위한 변수
    private boolean mBroadcastFinised = false;

    // 3초의 타이머를 이용하여 컨트롤 영역 안보이게 함.
    private TimerTask playAreaHideTimerTask;
    private final Timer playAreaHideTimer = new Timer();

    private String mEndTime;

    private static final String ARG_PARAM_BROAD_START_TIME = "_arg_param_broad_start";     // 라이브 시작 시간
    private static final String ARG_PARAM_BROAD_END_TIME = "_arg_param_broad_end";     // 라이브 종료 시간
    private static final String ARG_PARAM_VIDEO_MODE = "_arg_param_video_mode";     // 라이브 종료 시간
    private static final String ARG_PARAM_VIDEO_TYPE = "_arg_param_video_type";     // 라이브 종료 시간

    private String mPlayStartTime;
    private String mPlayEndTime;

    private boolean mIsMute = true; // 뮤트는 true일때 음소거 상태

    private int mVideoHeight = -1;
    private int mVideoWidth = -1;

    private PagerAdapterDetailView.ContentType mContentType = PagerAdapterDetailView.ContentType.TYPE_LIVE;
    private ComponentListener mComponentListener;

    /**
     * 자기 자신 fragment 의 instance 를 생성하여 반환
     * @param item
     * @param isPlaying
     * @return
     */
    public static FragmentDetailViewExoLivePlayerController newInstance(PagerAdapterDetailView.DetailViewListData item, boolean isPlaying) {
        Bundle args = new Bundle();

        FragmentDetailViewExoLivePlayerController fragment = new FragmentDetailViewExoLivePlayerController();

        if(item.mVideoInfo != null) {
            args.putString(ARG_PARAM_VIDEO_URL, item.mVideoInfo.videoUrl);
            args.putString(ARG_PARAM_BROAD_START_TIME, item.mVideoInfo.startTime);
            args.putString(ARG_PARAM_BROAD_END_TIME, item.mVideoInfo.endTime);
            args.putString(ARG_PARAM_VIDEO_MODE, item.mVideoInfo.videoMode);
            args.putSerializable(ARG_PARAM_VIDEO_TYPE, item.contentType);
        }
        // 무조건 autoPlay 여기서 안하고 단품 adpater에서 결정 할것.
        args.putBoolean(ARG_PARAM_IS_PLAYING, false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mPlayStartTime = arguments.getString(ARG_PARAM_BROAD_START_TIME);
            mPlayEndTime = arguments.getString(ARG_PARAM_BROAD_END_TIME);
            mContentType = (PagerAdapterDetailView.ContentType) arguments.getSerializable(ARG_PARAM_VIDEO_TYPE);
        }

        isReleasePlayerWhenStoped = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_detail_view_exo_live_player_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tapView = view.findViewById(R.id.tapview);
        playerView.setUseController(false);

        playAreaView = view.findViewById(R.id.view_player_controller_area);
        bottomView = view.findViewById(R.id.view_vod_player_bottom_controller);
        /**
         * controller buttons
         */
        playButton = view.findViewById(R.id.image_vod_controller_play);
        pauseButton = view.findViewById(R.id.image_vod_controller_pause);
        replayButton = view.findViewById(R.id.image_vod_controller_replay);
        chkMuteThis = view.findViewById(R.id.check_vod_player_mute);
        fullScreenView = view.findViewById(R.id.view_vod_player_zoom);

        ViewUtils.hideViews(playAreaView, playButton);

        tapView.setOnClickListener(this);
        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        replayButton.setOnClickListener(this);
        fullScreenView.setOnClickListener(this);
        chkMuteThis.setOnClickListener(this);

        mRemainedTimeText = view.findViewById(R.id.txt_remain_time);
        mCvReaminTime = view.findViewById(R.id.cv_remain_time);
        mEndText = view.findViewById(R.id.txt_live_end);

        mViewPlayBtnArea = view.findViewById(R.id.view_player_controller);
        mCvReaminTime.setListener(new BroadTimeLayoutDetailView.OnLiveTimeRefreshListener() {
            @Override
            public void onTimeRefresh(String remainTime) {
                if (isVod() && player != null) {
                    long remainedTime = player.getDuration() - player.getCurrentPosition();
                    mRemainedTimeText.setText(StringUtils.stringForHHMM(remainedTime, true));
                    if (mListener != null) {
                        mListener.onRemainedTime(StringUtils.stringForHHMM(remainedTime, true));
                    }
                }
                if (mListener != null) {
                    mListener.onRemainedTime(remainTime);
                }
            }
            @Override
            public void onBroadCastFinished() {
                if (isVod()) {
                    mCvReaminTime.setVisibility(View.GONE);
                    replayButton.setVisibility(View.VISIBLE);
                }
                else {
                    setBroadcastFinishUI();
                }
            }
        });
        if (mContentType == PagerAdapterDetailView.ContentType.TYPE_LIVE) {
            mCvReaminTime.updateTvLiveTime(null, mPlayStartTime, mPlayEndTime);
        }
        // 최초 들어왔을 때에 Mute 설정 초기화.
        initMuteFromGlobal();
        setMute(mIsMute);

        mComponentListener = new ComponentListener();
        player.addListener(mComponentListener);
        player.addVideoListener(new com.google.android.exoplayer2.video.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                mVideoHeight = height;
                mVideoWidth = width;
            }

            @Override
            public void onRenderedFirstFrame() {}
        });
        playAreaHideTimerTask = new TimerTask() {
            public void run() {
                updatePlayArea();
            }

            public void updatePlayArea() {
                ThreadUtils.INSTANCE.runInUiThread(() -> showPlayControllerView(false));
            }
        };
    }

    private void startPlayAreaHideTimerTask() {
        // 플레이 버튼이 보여지고 있는 상태면 자동 숨기기 하지 않는다.
        if (playButton.isShown()) {
            return;
        }
        // public void schedule (TimerTask task, long delay, long period)
        playAreaHideTimerTask = new TimerTask() {
            public void run() {
                updatePlayArea();
            }

            public void updatePlayArea() {
                ThreadUtils.INSTANCE.runInUiThread(() -> showPlayControllerView(false));
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

    @Override
    public void onTapClicked() {
        Ln.d("onTapClicked");
        // 방송 종료시에는 탭 클릭시 반응이 없어여 한다.
        if ( mBroadcastFinised ) {
            return;
        }

        // 컨트롤러 영역이 보이는 상태에서 플레이 중일 경우 영역 노출 시간 연장
        if (playAreaView.isShown()) {
            if (isPlaying()) {
                stopPlayAreaHideTask();
                startPlayAreaHideTimerTask();
            }
            return;
        }

        if (mListener != null) {
            mListener.onTap(!playAreaView.isShown());
        }

        showPlayControllerView(!playAreaView.isShown());
    }

    @Override
    public void setControllerVisibility(boolean isVisible) {
        mControllerVisible = isVisible;
        if (!isVisible)  {
            playAreaView.setVisibility(View.GONE);
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
            if (!mControllerVisible) {
                return;
            }
            stopPlayAreaHideTask();
            if (!playAreaView.isShown()) {
                playAreaView.setVisibility(View.VISIBLE);
            }
            // 현재 pasuse버튼 노출 상태이면서 (플레이 중) 방송 종료 가 아니라면
            if (pauseButton.isShown() && !mBroadcastFinised) {
                startPlayAreaHideTimerTask();
            }
        } else {
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

    @Override
    public void onPause() {
        super.onPause();
        if (mListener != null) {
            mListener.onPaused();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.onStoped();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_vod_controller_play:
//                playWhenReady = true;
                if (!isVod()) {

                    // 잠시 멈췄다 플레이 할 경우 영상에 문제가 생기는 경우 있음.
                    resetPlayer();
                    initMuteFromGlobal();
                    setPlayBtn(true);

                    // Live 플레이 버튼 선택시 AMP
                    ProductDetailWebActivityV2.prdAmpSend(AMP_PRD_LIVE_MANUAL_PLAY);
                }
                else {
                    // VOD 플레이 버튼 선택시 AMP
                    ProductDetailWebActivityV2.prdAmpSend(AMP_PRD_VOD_MANUAL_PLAY);
                }

                // Wiselog
                sendWiseLogPrdNative(PlayerAction.RESUME,
                        (int)player.getCurrentPosition(), (int)player.getDuration());

                setVideoStart();
                showPlayControllerView(false);
                break;

            case R.id.image_vod_controller_pause:
                player.setPlayWhenReady(false);
                setPlayBtn(false);
                showPlayControllerView(true);
                // Wiselog
                sendWiseLogPrdNative(PlayerAction.PAUSE,
                        (int)player.getCurrentPosition(), (int)player.getDuration());
                break;

            case R.id.image_vod_controller_replay :
                replayButton.setVisibility(View.GONE);
                player.seekTo(0);
                setVideoStart();
                showPlayControllerView(false);
                if (!isVod()) {
                    setPlayBtn(true);
                }
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

            case R.id.view_vod_player_zoom:
                MainApplication.gVideoIsPlaying = isPlaying();
                player.setPlayWhenReady(false);
                mControllerVisible = true;
                if (isNotEmpty(mListener)) {
                    MediaInfo mediaInfo = getMediaInfo();
                    mediaInfo.isPlaying = MainApplication.gVideoIsPlaying;
                    mListener.onFullScreenClick(mediaInfo);
                }
                // Wiselog
                sendWiseLogPrdNative(PlayerAction.FULL,
                        (int)player.getCurrentPosition(), (int)player.getDuration());
                break;

            case R.id.check_vod_player_mute:
                mIsMute = MainApplication.nativeProductIsMute = !chkMuteThis.isChecked();
                if (mListener != null) {
                    mListener.onMute(MainApplication.nativeProductIsMute);
                }
                setMute(mIsMute);
                PlayerAction playerAction = PlayerAction.SOUND_ON;
                if (chkMute.isChecked()) {
                    playerAction = PlayerAction.SOUND_OFF;
                }

                if (mListener != null) {
                    mListener.sendWiseLogPrdNative(playerAction,
                            (int) player.getCurrentPosition(), (int) player.getDuration());
                }
                break;
        }
    }

    @Override
    public void setOnMediaPlayerListener(OnMediaPlayerListener callback) {
        if (callback != null) {
            this.mListener = callback;
        }
    }

    private class ComponentListener extends Player.DefaultEventListener implements PlayerControlView.VisibilityListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_READY:
                    if (mListener != null) {
                        mListener.onVideoReady();
                    }

                    mCvReaminTime.setVisibility(View.VISIBLE);
                    if (isVod()) {
                        long remainedTime = player.getDuration() - player.getCurrentPosition();
                        mRemainedTimeText.setText(StringUtils.stringForHHMM(remainedTime, true));
                        if (mListener != null) {
                            mListener.onRemainedTime(StringUtils.stringForHHMM(remainedTime, true));
                        }
                        if (playWhenReady) {
                            showLoadingProgress(false);
                            mCvReaminTime.startMP4Timer();
                        }
                        else {
                            mCvReaminTime.stopTimer();
                        }
                    }

                    replayButton.setVisibility(View.GONE);
                    if (playWhenReady) {
                        playButton.setVisibility(View.GONE);
                        pauseButton.setVisibility(View.VISIBLE);
                        showPlayControllerView(false);
                        setMute(MainApplication.nativeProductIsMute);
                    }
                    else {
                        playButton.setVisibility(View.VISIBLE);
                        pauseButton.setVisibility(View.GONE);
                        setMute(true);
                    }
                    break;
                case Player.STATE_ENDED:
                    if (isVod()) {
                        playButton.setVisibility(View.GONE);
                        pauseButton.setVisibility(View.GONE);
                        replayButton.setVisibility(View.VISIBLE);
                        mCvReaminTime.stopTimer();
                        mCvReaminTime.setVisibility(View.GONE);
                        showPlayControllerView(true);
                    }
                    else {
                        playAreaView.setVisibility(View.GONE);
                    }

                    if (mListener != null) {
                        mListener.onFinished(getMediaInfo());
                    }
                    break;
            }
        }
        @Override
        public void onVisibilityChange(int visibility) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            super.onPlayerError(error);
            if(mListener != null) {
                mListener.onError(error);
            }
        }
    }

    @Override
    public void playPlayer() {
        player.seekToDefaultPosition();
        initMuteFromGlobal();
        setVideoStart();
    }

    @Override
    public void stopPlayer() {
        super.stopPlayer();
        player.setPlayWhenReady(false);
        setPlayBtn(false);
        showPlayControllerView(true);
    }

    @Override
    public void reloadPlayer() {
        super.reloadPlayer();
        releasePlayer();
        playWhenReady = false;
        initializePlayer();
        if (!playWhenReady) {
            // 멈춤 상태에서 setPlayWhenReady 가 false로 불리면 STATE_READY 안탄다.
            // 버튼 설정 해주어야 한다.
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
        initMuteFromGlobal();
    }

    @Override
    public void onFullscreenDisabled(boolean isBroadcastFinished) {
        // VOD 일 경우 공용 영상 위치로 이동.
        if (isVod()) {
            if (MainApplication.gVideoCurrentPosition > -1) {
                player.seekTo(MainApplication.gVideoCurrentPosition);
            }
        }
        else {
            if (isBroadcastFinished) {
                setBroadcastFinishUI();
                return;
            }
            else {
                resetPlayer();
            }
        }

        // 재생 중이 아닐경우에는 스타트 해주지 않는다.
        playWhenReady = MainApplication.gVideoIsPlaying;
        initMuteFromGlobal();

        if (playWhenReady) {
            playAreaView.setVisibility(View.GONE);
            setVideoStart();
        }
        else {
            setPlayBtn(false);
            showPlayControllerView(true);
        }

        // Wiselog
        sendWiseLogPrdNative(PlayerAction.MINI, (int)player.getCurrentPosition(), (int)player.getDuration());
    }

    private void initMuteFromLocal() {
        chkMuteThis.setChecked(!mIsMute);
        setMute(mIsMute);
    }

    private void initMuteFromGlobal() {
        mIsMute = MainApplication.nativeProductIsMute;
        chkMuteThis.setChecked(!mIsMute);
        setMute(MainApplication.nativeProductIsMute);
    }

    @Override
    public void setMute(boolean on) {
        // audio
        if (player != null) {
            if (on) {
                player.setVolume(0);
            } else {
                player.setVolume(1);
            }
        }
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
    }

    @Override
    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    public void setVideoStart() {
        if (isVod()) {
            showLoadingProgress(true);
        }
        setPlayBtn(true);
        showPlayControllerView(false);
        player.setPlayWhenReady(true);
    }

    @Override
    public void setVideoPause() {
        player.setPlayWhenReady(false);
        setPlayBtn(false);
        showPlayControllerView(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public MediaInfo getMediaInfo() {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.isPlaying = isPlaying();

        mediaInfo.isLandscape = true;
        if (getVideoHeight() >= getVideoWidth()) {
            mediaInfo.isLandscape = false;
        }
        mediaInfo.startTime = mPlayStartTime;
        mediaInfo.endTime = mPlayEndTime;
        mediaInfo.currentPosition = MainApplication.gVideoCurrentPosition = player.getCurrentPosition();
        mediaInfo.videoId = null;
        mediaInfo.contentUri = getVideoUrl();
        if (isVod()) {
            mediaInfo.videoMode = NativeProductV2.VIDEO_MODE_MP4;
        }
        else {
            mediaInfo.videoMode = NativeProductV2.VIDEO_MODE_LIVE;
        }
        return mediaInfo;
    }

    @Override
    public void resetPlayer() {
        releasePlayer();
        initializePlayer();
        initMuteFromGlobal();
    }

    private void setPlayBtn(boolean isPlay) {
        if(isPlay) {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        }
        else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
    }

    /**
     * 미니 플레이어에서 size를 작게 보여 주기 위함.
     * @param isLow
     */
    @Override
    public void setPlayerSize(boolean isLow) {
        if(isLow) {
            DefaultTrackSelector.Parameters builder = new DefaultTrackSelector.ParametersBuilder().setMaxVideoSizeSd().build();
            mPlayerSelector.setParameters(builder);
        }
        else {
            DefaultTrackSelector.Parameters builder = new DefaultTrackSelector.ParametersBuilder().build();
            mPlayerSelector.setParameters(builder);
        }
    }

    /**
     * 효율코드
     * @param action
     * @param playTime
     * @param totalTime
     */
    private void sendWiseLogPrdNative(PlayerAction action, int playTime, int totalTime) {
        if(mListener != null) {
            mListener.sendWiseLogPrdNative(action, playTime, totalTime);
        }
    }

    /**
     *  방송 종료 되었을 때에 UI 설정.
     */
    private void setBroadcastFinishUI() {
        ViewUtils.hideViews(mCvReaminTime, mRemainedTimeText, mViewPlayBtnArea, fullScreenView, chkMute, chkMuteThis);
        ViewUtils.showViews(mEndText);
        player.setPlayWhenReady(false);
        if (mListener != null) {
            MediaInfo mediaInfo = getMediaInfo();
            mListener.onFinished(mediaInfo);
        }
        mBroadcastFinised = true;
        showPlayControllerView(true);
    }
}
