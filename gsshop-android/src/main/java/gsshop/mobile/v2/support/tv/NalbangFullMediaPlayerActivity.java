/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.tv;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.ThreadUtils;

/**
 * 날방 전체 화면.
 * <p>
 * 필수 인텐트 전달사항
 * - 키 : Keys.INTENT.VIDEO_PARAM
 * - 타입 : {@link VideoParameters}
 * </p>
 * 생방송 스트리밍의 경우 Media Controller(stop/pause, rev, fwd 등)는 설정하지 않음.
 */
public class NalbangFullMediaPlayerActivity extends AbstractBaseActivity implements OnMediaPlayerListener {

    private OnMediaPlayerController playerController;
    private MediaInfo mediaInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_nalbang_fullscreen);

        // playerController에 null exception이 발생하여 layout xml 파일에 fragment 삽입
        playerController = (OnMediaPlayerController) getSupportFragmentManager().findFragmentById(R.id.container_media);


        Intent intent = getIntent();

        String param = intent.getStringExtra(Keys.INTENT.VIDEO_URL);
        mediaInfo = new Gson().fromJson(param, MediaInfo.class);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // forground
        if (playerController != null) {
            mediaInfo.playerMode = MediaInfo.MODE_FULL;
            playerController.setMediaInfo(mediaInfo);
            playerController.playPlayer();
        }
    }

    /**
     * OnMediaPlayerListener Methods
     */
    @Override
    public void onFullScreenClick(MediaInfo media) {
        finish();
    }

    @Override
    public void onPlayed() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onFinished(MediaInfo media) {
        finish();
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTap(boolean show) {

    }

    @Override
    public void onBackPressed() {
        if (playerController != null) {
            //playerController.showPlayControllerView(false);
        }
        backPressed();
    }

    public void backPressed() {
        ThreadUtils.INSTANCE.runInUiThread(() -> finish(), 200);
    }

    @Override
    public void finish() {
        MediaInfo media = playerController.getMediaInfo();
        media.playerMode = MediaInfo.MODE_SIMPLE;
        String param = new Gson().toJson(media);
        Intent intent = new Intent(Keys.ACTION.NALBANG_WEB);
        intent.putExtra(Keys.INTENT.VIDEO_URL, param);
        setResult(RESULT_OK, intent);
        super.finish();
    }
}