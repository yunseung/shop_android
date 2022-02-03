package gsshop.mobile.v2.web.productDetail.video.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.Player;
import com.gsshop.mocha.ui.util.ViewUtils;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.exoplayer.ExoLiveMediaPlayerControllerFragment;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.productDetail.BroadTimeLayoutDetailView;
import roboguice.util.Ln;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

public class FragmentDetailViewFullScreenLiveController extends ExoLiveMediaPlayerControllerFragment implements View.OnClickListener {
    // 플레이 버튼
    private ImageView playButton;
    // 일시 정지 버튼
    private ImageView pauseButton;
    // 다시 재생 버튼
    private ImageView replayButton;

    private View mViewButtonController;

    // 시간 측정 텍스트
    private BroadTimeLayoutDetailView mCvReaminTime;
    private TextView mRemainedTimeText;

    // 시간 표시 위한 타임
    private long mStartTime = 0;
    private long mEndTime = 0;

    // 영상 방향
    private int mOrientation =  SCREEN_ORIENTATION_LANDSCAPE;

    private static final String ARG_PARAM_START_TIME = "_arg_param_start_time";     // 비디오 시작 시간 밀리세컨드
    private static final String ARG_PARAM_END_TIME = "_arg_param_end_time";     // 비디오 종료 시간 밀리세컨드
    private static final String ARG_PARAM_ORIENTATION = "_arg_param_orientation";     // 비디오 종료 시간 밀리세컨드

    // 위치 조정 seekbar
    private View mViewSeekbarPort, mViewSeekbarLand;
    private SeekBar mSeekbarPort, mSeekbarLand;
    private TextView mTxtCurrentTimePort, mTxtCurrentTimeLand;
    private TextView mTxtEndTimePort, mTxtEndTimeLand;

    protected boolean isBroadcastFinished = false;
    private View mViewBroadFinishTxt;

    public static Fragment newInstance(String videoUrl, String startTime, String endTime, boolean isPlaying, int orientation) {
        FragmentDetailViewFullScreenLiveController fragment = new FragmentDetailViewFullScreenLiveController();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_VIDEO_URL, videoUrl);
        args.putString(ARG_PARAM_START_TIME, startTime);
        args.putString(ARG_PARAM_END_TIME, endTime);
        args.putBoolean(ARG_PARAM_IS_PLAYING, isPlaying);
        args.putInt(ARG_PARAM_ORIENTATION, orientation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String strTimeStart = getArguments().getString(ARG_PARAM_START_TIME);
            String strTimeEnd = getArguments().getString(ARG_PARAM_END_TIME);
            try {
                mStartTime = Long.parseLong(strTimeStart);
                mEndTime = Long.parseLong(strTimeEnd);
            }
            catch (NumberFormatException e) {
                Ln.e(e.getMessage());
            }
            mOrientation = getArguments().getInt(ARG_PARAM_ORIENTATION, SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_view_full_screen_live_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (player != null) {
            player.addListener(new ComponentListener());
        }

        if (playerView != null) {
            playerView.setControllerShowTimeoutMs(1000);
        }

        //사운드 On/Off
        chkMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.nativeProductIsMute = chkMute.isChecked();
                setMute(chkMute.isChecked());
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
            }
        });

        //닫기
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    player.setPlayWhenReady(false);
                    mListener.onClosed();
                }
            }
        });

        //확대/축소
        outView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onZoomed();
                }
            }
        });

        if (!isVod()) {
            // 준비 안되어도 생방송은 시간이 계속 흘러야 한다.
            mCvReaminTime.updateTvLiveTime(null,
                    Long.toString(mStartTime), Long.toString(mEndTime));
            mCvReaminTime.startTimer();
        }
        initMuteFromGlobal();
    }

    @Override
    protected void setPlayerView(View view) {
        /**
         * controller buttons
         */
        playButton = view.findViewById(R.id.image_vod_controller_play);
        pauseButton = view.findViewById(R.id.image_vod_controller_pause);
        replayButton = view.findViewById(R.id.image_vod_controller_replay);
        mViewButtonController = view.findViewById(R.id.view_player_controller);
        mViewBroadFinishTxt = view.findViewById(R.id.view_live_end);

        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        replayButton.setOnClickListener(this);

        mRemainedTimeText = view.findViewById(R.id.txt_remain_time);
        mCvReaminTime = view.findViewById(R.id.cv_remain_time);
        mCvReaminTime.setVisibility(View.GONE);
        // 리스너.
        mCvReaminTime.setListener(new BroadTimeLayoutDetailView.OnLiveTimeRefreshListener() {
            @Override
            public void onTimeRefresh(String remainTime) {
                if (isVod() && player != null) {
                    // vod 일때는 textView 설정을 해준다.
//                    long remainedTime = player.getDuration() - player.getCurrentPosition();

                    String strReminedTime = StringUtils.stringForHHMM(player.getCurrentPosition(), true);
                    int currentSec = (int)(player.getCurrentPosition() / 1000L);
                    currentSec += (player.getCurrentPosition() % 1000L > 499 ? 1 : 0);
                    mSeekbarLand.setProgress(currentSec);
                    mSeekbarPort.setProgress(currentSec);

                    mRemainedTimeText.setText(strReminedTime);
                    mTxtCurrentTimeLand.setText(strReminedTime);
                    mTxtCurrentTimePort.setText(strReminedTime);

                    mListener.onRemainedTime(strReminedTime);
                }
                if (mListener != null) {
                    mListener.onRemainedTime(remainTime);
                }
            }
            @Override
            public void onBroadCastFinished() {
                if (!isVod()) {
                    setBroadcastFinished();
                }
            }
        });

        setSeekBar(view);
    }

    /**
     * 방송 종료 했을때에 처리해야할 작업.
     */
    private void setBroadcastFinished() {
        try {
            isBroadcastFinished = true;
            playerView.showController();
            playerView.setControllerHideOnTouch(false);

            ViewUtils.showViews(mViewBroadFinishTxt);
            ViewUtils.hideViews(mViewButtonController, playButton, pauseButton, replayButton);

            player.setPlayWhenReady(false);
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }
    }

    private void setSeekBar(View view) {
        mViewSeekbarLand = view.findViewById(R.id.view_seek_bar_land);
        mViewSeekbarPort = view.findViewById(R.id.view_seek_bar_portrait);

        mSeekbarLand = view.findViewById(R.id.seek_bar_land);
        mSeekbarPort = view.findViewById(R.id.seek_bar_portrait);

        int totalEndSeconds = (int) (mEndTime / 1000L);
        int totalStartSeconds = (int) (mStartTime / 1000L);
        mSeekbarPort.setMax(totalEndSeconds - totalStartSeconds);
        mSeekbarLand.setMax(totalEndSeconds - totalStartSeconds);

        mTxtCurrentTimeLand = view.findViewById(R.id.current_time_land);
        mTxtCurrentTimePort = view.findViewById(R.id.current_time_portrait);
        mTxtEndTimeLand = view.findViewById(R.id.end_time_land);
        mTxtEndTimePort = view.findViewById(R.id.end_time_portrait);

        String strEndTime = StringUtils.stringForHHMM(mEndTime, true);
        mTxtEndTimeLand.setText(strEndTime);
        mTxtEndTimePort.setText(strEndTime);

        mSeekbarPort.setOnSeekBarChangeListener(SeekBarChangeListener);
        mSeekbarLand.setOnSeekBarChangeListener(SeekBarChangeListener);
    }

    private SeekBar.OnSeekBarChangeListener SeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (ClickUtils.refresh(500)) {
                    return;
                }
                player.seekTo(progress * 1000);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int seekBarProgress = seekBar.getProgress();
            player.seekTo(seekBarProgress * 1000);
        }
    };

    private class ComponentListener extends Player.DefaultEventListener{
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_READY:

                    if (isVod()) {
//                        mCvReaminTime.setVisibility(View.GONE);
//                        mRemainedTimeText.setVisibility(View.GONE);

                        if(mOrientation == SCREEN_ORIENTATION_LANDSCAPE) {
                            mViewSeekbarLand.setVisibility(View.VISIBLE);
                            mViewSeekbarPort.setVisibility(View.GONE);
                        }
                        else {
                            mViewSeekbarLand.setVisibility(View.GONE);
                            mViewSeekbarPort.setVisibility(View.VISIBLE);
                        }

                        String strMax = StringUtils.stringForHHMM(player.getDuration(), true);
                        int maxSec = (int)(player.getDuration() / 1000L);
                        mSeekbarLand.setMax(maxSec);
                        mSeekbarPort.setMax(maxSec);
                        mTxtEndTimeLand.setText(strMax);
                        mTxtEndTimePort.setText(strMax);

//                        long remainedTime = player.getDuration() - player.getCurrentPosition();
//                        mRemainedTimeText.setText(StringUtils.stringForHHMM(remainedTime, true));
//                        mListener.onRemainedTime(StringUtils.stringForHHMM(remainedTime, true));

                        if (playWhenReady) {
                                mCvReaminTime.startMP4Timer();
                        }
                        else {
                            mCvReaminTime.stopTimer();
                        }
                    }
                    else {
//                        mCvReaminTime.setVisibility(View.VISIBLE);
//                        mRemainedTimeText.setVisibility(View.VISIBLE);
                        mViewSeekbarLand.setVisibility(View.GONE);
                        mViewSeekbarPort.setVisibility(View.GONE);

                        mCvReaminTime.updateTvLiveTime(null,
                                Long.toString(mStartTime), Long.toString(mEndTime));
                        mCvReaminTime.startTimer();
                    }

                    replayButton.setVisibility(View.GONE);
                    if (playWhenReady) {
                        playButton.setVisibility(View.GONE);
                        pauseButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        playButton.setVisibility(View.VISIBLE);
                        pauseButton.setVisibility(View.GONE);
                    }
                    break;

                case Player.STATE_ENDED:
                    if (isVod()) {
                        playButton.setVisibility(View.GONE);
                        pauseButton.setVisibility(View.GONE);
                        replayButton.setVisibility(View.VISIBLE);
                        mCvReaminTime.stopTimer();
//                        mCvReaminTime.setVisibility(View.GONE);
                        playerView.showController();
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_vod_controller_play:
                if (isVod()) {
                    player.setPlayWhenReady(true);
                    setPlayBtn(true);
                }
                else {
                    // 잠시 멈췄다 플레이 할 경우 영상에 문제가 생기는 경우 있음.
                    resetPlayer();
                    setMute(MainApplication.nativeProductIsMute);
                    player.setPlayWhenReady(true);
                    setPlayBtn(true);
                }

                break;

            case R.id.image_vod_controller_pause:
                player.setPlayWhenReady(false);
                setPlayBtn(false);
                break;

            case R.id.image_vod_controller_replay:
                player.seekTo(0);
                player.setPlayWhenReady(true);
                break;
        }
    }

    @Override
    public boolean getIsPlaying() {
        if (isEmpty(player)) {
            return false;
        }
        return player.getPlayWhenReady();
    }

    @Override
    protected void onStateFinished() {
        mTxtCurrentTimeLand.setText(mTxtEndTimeLand.getText().toString());
        mTxtCurrentTimePort.setText(mTxtEndTimePort.getText().toString());
    }

    @Override
    public void stopPlayer() {
        if (isNotEmpty(player)) {
            player.setPlayWhenReady(false);
        }
        releasePlayer();
        initMuteFromGlobal();
    }

    @Override
    public void resetPlayer() {
        releasePlayer();
        initializePlayer();
        initMuteFromGlobal();
    }

    private void initMuteFromGlobal() {
        if (MainApplication.nativeProductIsMute != chkMute.isChecked()) {
            chkMute.setChecked(MainApplication.nativeProductIsMute);
            setMute(MainApplication.nativeProductIsMute);
        }
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

    @Override
    public boolean getIsBroadcastFinished() {
        return isBroadcastFinished;
    }
}
