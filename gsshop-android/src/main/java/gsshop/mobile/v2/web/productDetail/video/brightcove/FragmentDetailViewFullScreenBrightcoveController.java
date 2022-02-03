package gsshop.mobile.v2.web.productDetail.video.brightcove;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.display.VideoDisplayComponent;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.support.media.brightcove.LiveMediaPlayerControllerFragment;
import gsshop.mobile.v2.support.tv.LiveVideoMediaPlayerActivity;
import gsshop.mobile.v2.util.SoundUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

public class FragmentDetailViewFullScreenBrightcoveController extends LiveMediaPlayerControllerFragment {

    public static Fragment newInstance(String videoId, int startTime, boolean isPlaying, int orientation) {
        FragmentDetailViewFullScreenBrightcoveController fragment = new FragmentDetailViewFullScreenBrightcoveController();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_VIDEO_ID, videoId);
        args.putInt(ARG_PARAM_START_TIME, startTime);
        args.putBoolean(ARG_PARAM_IS_PLAYING, isPlaying);
        args.putInt(ARG_PARAM_ORIENTATION, orientation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CurrentPosition 처음 진입시 ARG_PARAM_START_TIME 으로 하면 MP4와 혼동이 옴.
        if (mediaInfo != null) {
            mediaInfo.currentPosition = MainApplication.gVideoCurrentPosition;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //사운드 On/Off
        chkMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.nativeProductIsMute = chkMute.isChecked();
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
                setMute(chkMute.isChecked());
                if (isNotEmpty(mListener)) {
                    mListener.onMute(MainApplication.nativeProductIsMute);
                    PlayerAction playerAction = PlayerAction.SOUND_OFF;
                    if (chkMute.isChecked()) {
                        playerAction = PlayerAction.SOUND_ON;
                    }
                    mListener.sendWiseLog(playerAction, baseVideoView.getCurrentPosition(), baseVideoView.getDuration());
                }
            }
        });

        eventEmitter.on(EventType.DID_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Ln.i("currentPosition: " + baseVideoView.getCurrentPosition() + ", duration: " + baseVideoView.getDuration() + ", isPlaying: " + mediaInfo.isPlaying);
                //다른 앱 사운드 중지
                if (!MainApplication.nativeProductIsMute) {
                    SoundUtils.requestAudioFocus();
                }
                controller.setHideControllerEnable(true);

                // hide 시간을 1초 후에 사라지게 한다.
                mHandlerHide.removeCallbacks(mRunnableHide);
                mHandlerHide.postDelayed(mRunnableHide, 1000);

                initMuteFromGlobal();
                mPlayButton.setVisibility(View.GONE);
                mPauseButton.setVisibility(View.VISIBLE);
                mReplayButton.setVisibility(View.GONE);
                mediaInfo.isPlaying = true;
            }
        });

        // 리플레이 버튼 재설정.
        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveVideoMediaPlayerActivity.mActionChangeFlag = true;
                new Handler().postDelayed(() -> LiveVideoMediaPlayerActivity.mActionChangeFlag = false, 3000);
                baseVideoView.seekTo(0);
                mediaInfo.isPlaying = true;
                baseVideoView.start();
            }
        });
    }

    @Override
    public long getCurrentPosition() {
        if (baseVideoView != null) {
            return baseVideoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public boolean getIsPlaying() {
        if (baseVideoView != null) {
            return baseVideoView.isPlaying();
        }
        return false;
    }

    /**
     * 사운드를 재생/뮤트 시킨다.
     *
     * @param mute - on 이면 mute
     */
    @Override
    public void setMute(boolean mute) {
        // audio
        VideoDisplayComponent videoDisplayComponent = baseVideoView.getVideoDisplay();
        if (videoDisplayComponent instanceof ExoPlayerVideoDisplayComponent) {
            // Get ExoPlayer
            ExoPlayer exoPlayer = ((ExoPlayerVideoDisplayComponent) videoDisplayComponent).getExoPlayer();
            if (!isEmpty(exoPlayer)) {
                // audio
                float volume = !mute ? 1 : 0;
                ((SimpleExoPlayer) exoPlayer).setVolume(volume);
                MainApplication.nativeProductIsMute = mute;

                //다른 앱 사운드 중지
                if (!mute && baseVideoView.isPlaying()) {
                    SoundUtils.requestAudioFocus();
                }
            }
        }
    }

    @Override
    protected void initMuteFromGlobal() {
        chkMute.setChecked(MainApplication.nativeProductIsMute);
        setMute(MainApplication.nativeProductIsMute);
    }
}
