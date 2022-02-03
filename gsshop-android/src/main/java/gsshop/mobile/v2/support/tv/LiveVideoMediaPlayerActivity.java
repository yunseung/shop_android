/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.tv;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.gsshop.mocha.core.util.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.brightcove.LiveMediaPlayerControllerFragment;
import gsshop.mobile.v2.support.media.exoplayer.ExoLiveMediaPlayerControllerFragment;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.blankj.utilcode.util.StringUtils.isEmpty;
import static gsshop.mobile.v2.ServerUrls.WEB.VOD_BRIGHTCOVE_PLAYER;

/**
 * 생방송/VOD 동영상 플레이어.
 * <p>
 * 필수 인텐트 전달사항
 * - 키 : Keys.INTENT.VIDEO_PARAM
 * - 타입 : {@link VideoParameters}
 * </p>
 * 생방송 스트리밍의 경우 Media Controller(stop/pause, rev, fwd 등)는 설정하지 않음.
 */
public class LiveVideoMediaPlayerActivity extends AbstractBaseActivity implements OnMediaPlayerListener {

    /**
     * 전체화면이 어디서 호출되었는지 구분자
     */
    public enum FULL_SCREEN_CALLER { PRODUCT, SIGNATURE }
    protected FULL_SCREEN_CALLER fullScreenCaller = FULL_SCREEN_CALLER.PRODUCT;

    /**
     * 비디오 아이디
     */
    protected String videoId = "";

    /**
     * 지금상품바로보기 버튼 클릭시 이동할 웹페이지 주소
     */
    public String prdUrl = "";

    /**
     * 상품보기 섬네일 이미지
     */
    public String imageUrl = "";

    /**
     * 와이즈로그 주소
     */
    public String wiseLogUrl = "";

    @InjectView(R.id.view_go_prd)
    protected View btnViewProductInfo;

    @InjectView(R.id.image_video_live_prd)
    protected CircleImageView prdImage;

    /**
     * 소프트 네비게이션 제거용
     */
    protected View decorView;
    protected int	uiOption;

    /**
     * 동영상 전체화면 "다시재생" 버튼을 클릭한 경우 액션을 "PLAY" -> "RESUME" 변경해야 함
     */
    public static boolean mActionChangeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoId = getIntent().getStringExtra(Keys.INTENT.VIDEO_ID);

        prdUrl = getIntent().getStringExtra(Keys.INTENT.VIDEO_PRD_URL);
        imageUrl = getIntent().getStringExtra(Keys.INTENT.VIDEO_IMAGE_URL);
        wiseLogUrl = getIntent().getStringExtra(Keys.INTENT.VIDEO_PRD_WISELOG_URL);

        String videoUrl = getIntent().getStringExtra(Keys.INTENT.VIDEO_URL);
        int orientation = getIntent().getIntExtra(Keys.INTENT.VIDEO_ORIENTATION,
                SCREEN_ORIENTATION_LANDSCAPE);

        if(orientation == SCREEN_ORIENTATION_PORTRAIT) {
            setContentView(R.layout.video_live);
        } else {
            setContentView(R.layout.video_live_land);
        }

        if (TextUtils.isEmpty(videoId) && TextUtils.isEmpty(videoUrl)) {
            Toast.makeText(this, R.string.video_no_param, Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        //TV생방송, 편성표, 홈 탭에서 들어온 경우는 가로 고정 (사용자 설정 무시)
        //가로모드 180도 회전이 필요한 경우 SCREEN_ORIENTATION_SENSOR_LANDSCAPE 사용하면 됨
        setRequestedOrientation(orientation);

        setControllerFragment();

        // 클릭리스너 등록
        setClickListener();

        // product image
        if (isNotEmpty(imageUrl)) {
            //CircleImageView 경우 placeholder에 디폴트 이미지를 지정할 경우 처음 호출시 디폴트 이미지가 표시되고
            //두번째 호출시 url에서 다운받은 이미지가 표시됨. 그래서 onLoadFailed에서 디폴트 이미지 세팅
            ImageUtil.loadImageSetDefault(this, getModifiedImageUrl(imageUrl), prdImage, 0
                    , R.drawable.full_product_noimg);
        } else {
            btnViewProductInfo.setVisibility(View.GONE);
        }

        //소프트 네비게이션 제거
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }

    protected void setControllerFragment() {
        String videoUrl = getIntent().getStringExtra(Keys.INTENT.VIDEO_URL);
        String startTime = getIntent().getStringExtra(Keys.INTENT.VIDEO_START_TIME);
        boolean isPlaying = getIntent().getBooleanExtra(Keys.INTENT.VIDEO_IS_PLAYING, false);
        int orientation = getIntent().getIntExtra(Keys.INTENT.VIDEO_ORIENTATION,
                SCREEN_ORIENTATION_LANDSCAPE);

        // playerController에 null exception이 발생하여 layout xml 파일에 fragment 삽입
        // videoId 존재하면 brightcove player, 아니면 exo player에서 재생
        if (TextUtils.isEmpty(videoId)) {
            Ln.i("Exo - ExoLiveMediaPlayerControllerFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.video_frame,
                    ExoLiveMediaPlayerControllerFragment.newInstance(videoUrl, isPlaying)).commitNow();
        } else {
            Ln.i("Brightcove - LiveMediaPlayerControllerFragment");
            int nStartTime = 0;
            try {
                Integer.parseInt(startTime);
            }
            catch (NumberFormatException e) {
                Ln.e(e.getMessage());
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.video_frame,
                    LiveMediaPlayerControllerFragment.newInstance(videoId, nStartTime, isPlaying, orientation)).commitNow();
        }
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.view_go_prd).setOnClickListener((View v) -> {
                    viewProductInfo();
                }
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if( hasFocus ) {
            decorView.setSystemUiVisibility(uiOption);
        }
    }

    /**
     * 파일이름의 끝 2자리를 "V1"으로 치환하여 반환한다.
     *
     * @param url 이미지 주소
     * @return 치환된 주소
     */
    protected String getModifiedImageUrl(String url) {
        if (isEmpty(url) || url.lastIndexOf(".") <= 0) {
            return url;
        }

        int lastIdx = url.lastIndexOf(".");
        String fileName = url.substring(0, lastIdx);
        if (fileName.length() < 2) {
            return url;
        }

        String fileExtention = url.substring(lastIdx+1);
        String tmpfileName = fileName.substring(0, fileName.length()-2);

        return tmpfileName + "V1." + fileExtention;
    }

    private String getPrdIdFromUrl() {
        if (TextUtils.isEmpty(prdUrl)) {
            return "";
        }
        Uri uri = Uri.parse(prdUrl);
        String prdId = uri.getQueryParameter("prdid");
        return prdId == null ? uri.getLastPathSegment() : prdId;
    }

    /**
     * 현재 재생중인 상품 바로보기 웹페이지로 이동.
     */
    @SuppressWarnings("unused")
    private void viewProductInfo() {

        //현재 재생중인 상품 바로보기 웹페이지로 이동. for Amplitude
        try{
            //내일 TV에만 동작 해야 하는 Amplitude  이벤트 입니다
            try {
                JSONObject eventProperties = new JSONObject();
                eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.TOMORROW_TV_FULL_PRD_CLICK);
                //if(content != null) {
                eventProperties.put(AMPEnum.AMP_PRD_CODE, getPrdIdFromUrl());
                //eventProperties.put(AMPEnum.AMP_PRD_NAME, videoId);
                eventProperties.put(AMPEnum.AMP_VIDEO_ID, videoId);
                //eventProperties.put(AMPEnum.AMP_VIDEO_DUR, content.videoTime);
                //}
                AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_TOMORROW_TV,eventProperties);
            } catch (JSONException exception){

            }
        }catch (Exception e){

        }

        //와이즈로그 전송
        if (!TextUtils.isEmpty(wiseLogUrl)) {
            setWiseLogHttpClient(wiseLogUrl);
        }

        boolean appRunning = getIntent().getBooleanExtra(Keys.INTENT.FOR_RESULT, false);
        if (appRunning) {
            // 이전 액티비티에서 해당 페이지로 이동
            Intent intent = new Intent();
            intent.putExtra(Keys.INTENT.WEB_URL, prdUrl);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }

        // app을 런치시켜 해당 페이지로 이동
        Intent intent = ActivityUtils.getMainActivityIntent(getApplicationContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
        intent.putExtra(Keys.INTENT.WEB_URL, prdUrl);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTap(boolean show) {
        if (isEmpty(imageUrl)) {
            return;
        }
        btnViewProductInfo.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onFullScreenClick(MediaInfo media) {

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
        Toast.makeText(getApplicationContext(), R.string.video_play_error,
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void sendWiseLog(PlayerAction action, int playTime, int totalTime) {
        //시그니처 매장에서 전체화면을 오픈한 경우
        if (fullScreenCaller == FULL_SCREEN_CALLER.SIGNATURE) {
            //전체화면 동영상 액션 이벤트 전송 (시그니처매장에서 사용)
            if (mActionChangeFlag && PlayerAction.PLAY == action) {
                //다시재생 버튼 클릭시 PLAY 이벤트가 들어오는데 이를 RESUME으로 변경
                action = PlayerAction.RESUME;
            }
            EventBus.getDefault().post(new Events.VideoActionEvent(action, videoId, playTime, totalTime));
            return;
        }

        String url = VOD_BRIGHTCOVE_PLAYER.replace("#ACTION", action.toString())
                .replace("#VID", videoId)
                .replace("#AUTOPLAY", MainApplication.isAutoPlay)
                .replace("#PT", Integer.toString(playTime/1000))
                .replace("#TT", Integer.toString(totalTime/1000))
                .replace("#PRDID", getPrdIdFromUrl());

        if (action == PlayerAction.LTE_Y) {
            //LTE_Y와 PLAY 액션이 30ms 이내에 같이 호출되어 먼저 호출된 액션(LTE_Y)이 누락되는 현상 발생
            //LTE_Y는 일정시간 딜레이 후 호출
            sendWiseLogWithDelay(url);
        } else {
            setWiseLogHttpClient(url);
        }
    }

    public void sendWiseLogWithDelay(String url) {
        ThreadUtils.INSTANCE.runInUiThread(() -> setWiseLogHttpClient(url), 2000);
    }
}
