package gsshop.mobile.v2.web.productDetail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.tv.LiveVideoMediaPlayerActivity;
import gsshop.mobile.v2.web.productDetail.video.brightcove.FragmentDetailViewFullScreenBrightcoveController;
import gsshop.mobile.v2.web.productDetail.video.live.FragmentDetailViewFullScreenLiveController;
import roboguice.util.Ln;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

/**
 * 단품에서 전체화면 이동 시 기존 UI 재사용.
 */
public class ActivityDetailViewLiveVideoMediaPlayer extends LiveVideoMediaPlayerActivity {

    private OnMediaPlayerController mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlayer = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.video_frame);

        if (btnViewProductInfo != null) {
            btnViewProductInfo.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setControllerFragment() {
        String videoUrl = getIntent().getStringExtra(Keys.INTENT.VIDEO_URL);
        String startTime = getIntent().getStringExtra(Keys.INTENT.VIDEO_START_TIME);
        String endTime = getIntent().getStringExtra(Keys.INTENT.VIDEO_END_TIME);
        boolean isPlaying = getIntent().getBooleanExtra(Keys.INTENT.VIDEO_IS_PLAYING, false);
        int orientation = getIntent().getIntExtra(Keys.INTENT.VIDEO_ORIENTATION,
                SCREEN_ORIENTATION_LANDSCAPE);
        fullScreenCaller = (FULL_SCREEN_CALLER) getIntent().getSerializableExtra(Keys.INTENT.FULL_SCREEN_CALLER);

        if (TextUtils.isEmpty(videoId)) {
            Ln.i("Exo - FragmentDetailViewFullScreenLiveController");
            getSupportFragmentManager().beginTransaction().replace(R.id.video_frame,
                    FragmentDetailViewFullScreenLiveController.newInstance(videoUrl, startTime, endTime, isPlaying, orientation)).commitNowAllowingStateLoss();
        } else {
            Ln.i("Brightcove - FragmentDetailViewFullScreenBrightcoveController");
            int nStartTime = 0;
            try {
                nStartTime = Integer.parseInt(startTime);
            }
            catch (NumberFormatException e) {
                Ln.e(e.getMessage());
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.video_frame,
                    FragmentDetailViewFullScreenBrightcoveController.newInstance(videoId, nStartTime, isPlaying, orientation)).commitNowAllowingStateLoss();
        }
    }

    // 기존에는 onFinish 받으면 종료 했지만 종료하지 않고 영상을 멈춘다.
    @Override
    public void onFinished(MediaInfo media) {
        if (mPlayer == null) {
            mPlayer = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.video_frame);
        }
        // 종료하지 않는다.
        if (mPlayer != null) {
            mPlayer.stopPlayer();
        }
        setIntentBroadcastFinished();
    }

    // onClosed를 추가해서 해당 callback 이 불렸을 때에 종료.
    @Override
    public void onClosed() {
        if (mPlayer == null) {
            mPlayer = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.video_frame);
        }
        if (mPlayer != null) {
            MainApplication.gVideoCurrentPosition = mPlayer.getCurrentPosition();
            MainApplication.gVideoIsPlaying = false;
            mPlayer.stopPlayer();
        }
        setIntentBroadcastFinished();
        finish();
    }

    @Override
    public void onZoomed() {
        if (mPlayer == null) {
            mPlayer = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.video_frame);
        }
        if (mPlayer != null) {
            MainApplication.gVideoCurrentPosition = mPlayer.getCurrentPosition();
            MainApplication.gVideoIsPlaying = mPlayer.getIsPlaying();
            mPlayer.stopPlayer();
        }
        setIntentBroadcastFinished();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mPlayer == null) {
            mPlayer = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.video_frame);
        }
        if (mPlayer != null) {
            MainApplication.gVideoCurrentPosition = mPlayer.getCurrentPosition();
            MainApplication.gVideoIsPlaying = mPlayer.getIsPlaying();
            mPlayer.stopPlayer();
        }

        setIntentBroadcastFinished();
        super.onBackPressed();
    }

    private void setIntentBroadcastFinished() {
        Intent intent = new Intent();
        if(mPlayer.getIsBroadcastFinished()) {
            intent.putExtra("BRD_FINISH", true);
        }
        setResult(RESULT_OK, intent);
    }
}
