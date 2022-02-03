package gsshop.mobile.v2.home.shop.vod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.ui.util.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.brightcove.VodMediaPlayerControllerFragment;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.ServerUrls.WEB.VOD_BRIGHTCOVE_PLAYER;
import static gsshop.mobile.v2.ServerUrls.WEB.VOD_GOPRD_CLICK;

/**
 * landscape vod view holder
 */
public class VodBannerVodSharedViewHolder extends VodBannerVodViewHolder implements OnMediaPlayerListener {

    // 영상의 형태.
    public enum VodBannerShape {
        VOD_SHAPE_LANDSCAPE,  // 가로 영상
        VOD_SHAPE_PORTRAIT,   // 세로 영상
        VOD_SHAPE_SQUARE    // 정사각 영상
    }
    protected VodBannerShape mVodShape = VodBannerShape.VOD_SHAPE_LANDSCAPE;

    public static final int[] LAND_FRAME_IDS = {R.id.vod_land_video_frame_00, R.id.vod_land_video_frame_01, R.id.vod_land_video_frame_02, R.id.vod_land_video_frame_03, R.id.vod_land_video_frame_04, R.id.vod_land_video_frame_05, R.id.vod_land_video_frame_06, R.id.vod_land_video_frame_07, R.id.vod_land_video_frame_08, R.id.vod_land_video_frame_09,
            R.id.vod_land_video_frame_10, R.id.vod_land_video_frame_11, R.id.vod_land_video_frame_12, R.id.vod_land_video_frame_13, R.id.vod_land_video_frame_14, R.id.vod_land_video_frame_15, R.id.vod_land_video_frame_16, R.id.vod_land_video_frame_17, R.id.vod_land_video_frame_18, R.id.vod_land_video_frame_19,
            R.id.vod_land_video_frame_20, R.id.vod_land_video_frame_21, R.id.vod_land_video_frame_22, R.id.vod_land_video_frame_23, R.id.vod_land_video_frame_24, R.id.vod_land_video_frame_25, R.id.vod_land_video_frame_26, R.id.vod_land_video_frame_27, R.id.vod_land_video_frame_28, R.id.vod_land_video_frame_29,
            R.id.vod_land_video_frame_30, R.id.vod_land_video_frame_31, R.id.vod_land_video_frame_32, R.id.vod_land_video_frame_33, R.id.vod_land_video_frame_34, R.id.vod_land_video_frame_35, R.id.vod_land_video_frame_36, R.id.vod_land_video_frame_37, R.id.vod_land_video_frame_38, R.id.vod_land_video_frame_39};

    protected static final int[] PORT_FRAME_IDS = {R.id.vod_port_video_frame_00, R.id.vod_port_video_frame_01, R.id.vod_port_video_frame_02, R.id.vod_port_video_frame_03, R.id.vod_port_video_frame_04, R.id.vod_port_video_frame_05, R.id.vod_port_video_frame_06, R.id.vod_port_video_frame_07, R.id.vod_port_video_frame_08, R.id.vod_port_video_frame_09};

    protected static final int[] SQUARE_FRAME_IDS = {R.id.vod_square_video_frame_00, R.id.vod_square_video_frame_01, R.id.vod_square_video_frame_02, R.id.vod_square_video_frame_03, R.id.vod_square_video_frame_04, R.id.vod_square_video_frame_05, R.id.vod_square_video_frame_06, R.id.vod_square_video_frame_07, R.id.vod_square_video_frame_08, R.id.vod_square_video_frame_09};

    // default land
//    protected int[] FRAME_IDS = LAND_FRAME_IDS;

    private final CheckBox chkMute;
    private final View zoomView;

    private final View mobileDataView;
    private final View mobileDataCancelButton;
    private final View viewDummyThumb, viewDummyPlayer;

    private Context context;

    protected final ImageView videoImage, videoImageFitToH;
    private final ImageView playButton, replayButton;
    private final FrameLayout videoFrame;
    private final TextView remainedTimeText;
    private final View controllerView;
    private final View viewVideoImage, viewVideoImageFitToH;
    private final View goPrdLayout;
    private ImageView close_vod;
    private LinearLayout tag_container;


    private final TextView promotionName;

    private MediaInfo media = null;

    private int abTestPlayButton;

    /**
     * View Holder를 하나로 합침에 따라 count를 따로 두게끔 수정
     */
    protected static int countLand = 0;
    protected static int countPort = 0;
    protected static int countSquare = 0;

    /**
     * 현재 영상 재생 준비중 여부 판단을 위한 값.
     */
    private static boolean isPlayingNow = false;

    /**
     * 현재 플레이 중인 뷰의 위치 값
     */
    private static int mPlayingPosition = -1;

    /**
     * 다음 재생 영상의 최대 대기 시간을 확인하기 위한 count
     */
    private int cntPlayTimeout = 0;

    /**
     * 프로그레스바
     */
    private ProgressBar loadingProgress;

    /**
     * 최대 100 * 10 ms (1초) 동안 대기하면서 전 영상의 준비가 끝났는지 확인 후 준비가 끝났을 때 지금 플레이 하는 영상 재생
     */
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlayingNow) {
                if (cntPlayTimeout < 10) {
                    Ln.d("VodBannerVodSharedViewHolder_play delay : " + cntPlayTimeout);
                    cntPlayTimeout ++;
                    mHandler.postDelayed(this, 100);
                }
                else {
                    Ln.d("VodBannerVodSharedViewHolder_play timeout");
                    cntPlayTimeout = 0;
                    isPlayingNow = true;
                    buttonPlay();
                }
            }
            else {
                Ln.d("VodBannerVodSharedViewHolder_play ready prev vod");
                isPlayingNow = true;
                buttonPlay();
            }
        }
    };

    /**
     * 내일TV  AB TEST 색상변경
     */
    private final String schInfoEndCursor = "방송";

    /**
     * 재생클릭 > 버퍼링 중 검은화면 노출을 숨기기 위한 뷰
     * -노출시점 : 재생클릭시
     * -숨김시점 : BUFFERING_COMPLETED, DID_PLAY 이벤트 수신시
     */
//    private final View forBufferingView;

    /**
     * @param itemView
     */
    public VodBannerVodSharedViewHolder(View itemView) {
        super(itemView);
        videoImage = itemView.findViewById(R.id.image_vod_video);
        videoImageFitToH = itemView.findViewById(R.id.image_vod_video_fit_to_height);

        viewVideoImage = itemView.findViewById(R.id.layout_vod_video);
        viewVideoImageFitToH = itemView.findViewById(R.id.layout_vod_video_to_height);

        videoFrame = itemView.findViewById(R.id.vod_video_frame);
//        forBufferingView = itemView.findViewById(R.id.view_vod_for_buffering);
        playButton = itemView.findViewById(R.id.image_vod_play);
        replayButton = itemView.findViewById(R.id.image_vod_controller_replay);
        remainedTimeText = itemView.findViewById(R.id.text_vod_remained_time);
        chkMute = itemView.findViewById(R.id.check_vod_mute);
        zoomView = itemView.findViewById(R.id.view_vod_zoom);
        controllerView = itemView.findViewById(R.id.view_vod_controller);
        loadingProgress = itemView.findViewById(R.id.mc_vod_progress_bar);
        promotionName = itemView.findViewById(R.id.promotionName);

        mobileDataView = itemView.findViewById(R.id.view_vod_mobile_data);
        mobileDataCancelButton = mobileDataView.findViewById(R.id.button_mobile_data_cancel);
        goPrdLayout = itemView.findViewById(R.id.goPrdLayout);

        close_vod = itemView.findViewById(R.id.close_vod);
        tag_container = itemView.findViewById(R.id.tag_container);

        viewDummyThumb = itemView.findViewById(R.id.lay_vod_video_dummy_thumb);
        viewDummyPlayer = itemView.findViewById(R.id.lay_vod_video_dummy_player);

        View viewDim = itemView.findViewById(R.id.dim_vod_video);
        View viewDimFitToH = itemView.findViewById(R.id.dim_vod_video_to_height);
        // 비디오영역 리사이징
        DisplayUtils.resizeHeightAtViewsToScreenSize(videoFrame, videoImage, videoImageFitToH, viewDummyThumb, viewDummyPlayer, viewDim, viewDimFitToH);

        // 초기 보이는 영역 설정
        ViewUtils.hideViews(mobileDataView, videoFrame, viewDummyPlayer, viewVideoImageFitToH);
        ViewUtils.showViews(viewVideoImage, chkMute, zoomView, remainedTimeText, playButton, viewDummyThumb);

        zoomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNotEmpty(player)) {
                    media.isPlaying = player.isPlaying();
                } else {
                    media = new MediaInfo();
                    media.videoId = content.videoid;
                    media.currentPosition = 0;
                    media.isPlaying = false;
                }

                onFullScreenClick(media);
                sendWiseLog(PlayerAction.FULL, 0, timeToMills(content.videoTime));
            }
        });

        //사운드 On/Off
        chkMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE, chkMute.isChecked()));
                PlayerAction playerAction = PlayerAction.SOUND_ON;
                if (chkMute.isChecked()) {
                    playerAction = PlayerAction.SOUND_OFF;
                }
                sendWiseLog(playerAction, 0, timeToMills(content.videoTime));
            }
        });

        /**
         * mobile data
         */
        mobileDataCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMobileDataView();
                sendWiseLog(PlayerAction.LTE_N, 0, timeToMills(content.videoTime));
            }
        });

        mobileDataView.findViewById(R.id.button_mobile_data_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideViews(mobileDataView);
                ViewUtils.showViews(playButton, remainedTimeText, chkMute, zoomView);
                MainApplication.isNetworkApproved = true;
                buttonPlay();
                sendWiseLog(PlayerAction.LTE_Y, 0, timeToMills(content.videoTime));
            }
        });

        if (goPrdLayout != null) {
            goPrdLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onProductInfo();
                }
            });
        }

        controllerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playButton.getVisibility() == View.GONE) {
                    return;
                }
                determinePlay();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                determinePlay();
            }
        });
        if (replayButton != null) {
            replayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtils.hideViews(controllerView, viewDummyThumb);
                    ViewUtils.showViews(videoFrame, viewDummyPlayer);

                    player.replayPlayer();
                }
            });
        }


        if (close_vod != null) {
            close_vod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VodBannerVodSharedViewHolder.super.onViewDetachedFromWindow();
                    Ln.i("vod scrolling detached get adapter: " + getAdapterPosition());
                    stopPlayer();

                }
            });
        }
    }



    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        this.context = context;
        ViewUtils.showViews(controllerView, playButton, remainedTimeText);

        chkMute.setChecked(MainApplication.isMute);
        remainedTimeText.setText(content.videoTime);
        hideMobileDataView();

    }

    private void determinePlay() {
        if ((!NetworkStatus.isWifiConnected(context) && !MainApplication.isNetworkApproved)) {
            ViewUtils.hideViews(playButton, remainedTimeText, chkMute, zoomView, goPrdLayout, replayButton);
            ViewUtils.showViews(mobileDataView);
            EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_MOBILE_DATA, getAdapterPosition()));
        } else {
            cntPlayTimeout = 0;
            mHandler.removeCallbacks(mRunnable);
            mHandler.post(mRunnable);
        }
    }

    private void buttonPlay() {

        if (isEmpty(player)) {
            setPlayer();
        }

        mPlayingPosition = getAdapterPosition();

        ViewUtils.hideViews(playButton, remainedTimeText, goPrdLayout);
        videoFrame.setVisibility(View.INVISIBLE);

        MainApplication.isAutoPlay = "N";
        media = player.getMediaInfo();
        if (mVodShape == VodBannerShape.VOD_SHAPE_LANDSCAPE) {
            media.isLandscape = true;
        }
        else {
            media.isLandscape = false;
        }
        // vod 새로 시작.
        media.videoId = content.videoid;
        media.posterImageUrl = content.imageUrl;
        media.currentPosition = 0;
        media.listPosition = getAdapterPosition();
        media.isPlaying = true;
        player.setMediaInfo(media);
        player.playPlayer();

        showLoadingProgress(true);

        //내일 TV에만 동작 해야 하는 Amplitude  이벤트 입니다
        try {
            JSONObject eventProperties = new JSONObject();
            eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.TOMORROW_TV_BUTTONPLAY);
            if(content != null) {
                if(prdItem != null) {
                    eventProperties.put(AMPEnum.AMP_PRD_CODE, prdItem.dealNo);
                    eventProperties.put(AMPEnum.AMP_PRD_NAME, prdItem.productName);
                }
                eventProperties.put(AMPEnum.AMP_VIDEO_ID, content.videoid);
                eventProperties.put(AMPEnum.AMP_VIDEO_DUR, content.videoTime);
            }
            AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_TOMORROW_TV,eventProperties);
        } catch (JSONException exception){

        }
    }

    private void autoPlay() {
        if (isEmpty(player)) {
            setPlayer();
        }

        media = player.getMediaInfo();
        Ln.i("scroll position: " + media.listPosition + ", get position: " + getAdapterPosition() + ", current position: " + media.currentPosition + ", isPlaying: " + media.isPlaying);
        if (media.listPosition != getAdapterPosition() || !media.isPlaying) {
            MainApplication.isAutoPlay = "Y";
            if (mVodShape == VodBannerShape.VOD_SHAPE_LANDSCAPE) {
                media.isLandscape = true;
            }
            else {
                media.isLandscape = false;
            }
            // vod 새로 시작.
            media.videoId = content.videoid;
            media.posterImageUrl = content.imageUrl;
            media.currentPosition = 0;
            media.listPosition = getAdapterPosition();
            media.isPlaying = true;
            player.setMediaInfo(media);
            player.playPlayer();
            showLoadingProgress(true);

            //내일 TV에만 동작 해야 하는 Amplitude  이벤트 입니다
            /*
            try {
                JSONObject eventProperties = new JSONObject();
                eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.TOMORROW_TV_AUTOPLAY);
                if(content != null) {
                    if(prdItem != null) {
                        eventProperties.put(AMPEnum.AMP_PRD_CODE, prdItem.dealNo);
                        eventProperties.put(AMPEnum.AMP_PRD_NAME, prdItem.productName);
                    }
                    eventProperties.put(AMPEnum.AMP_VIDEO_ID, content.videoid);
                    eventProperties.put(AMPEnum.AMP_VIDEO_DUR, content.videoTime);
                }
                Amplitude.getInstance().logEvent( AMPEnum.AMP_CLICK_TOMORROW_TV, eventProperties);
            } catch (JSONException exception){

            }
            */
        }
    }

    private void setPlayer() {
//        if (FRAME_IDS == null) {
//            if (mVodShape == VodBannerShape.VOD_SHAPE_PORTRAIT) {
//                FRAME_IDS = PORT_FRAME_IDS;
//            }
//            else if (mVodShape == VodBannerShape.VOD_SHAPE_SQUARE) {
//                FRAME_IDS = SQUARE_FRAME_IDS;
//            }
//            else {
//                FRAME_IDS = LAND_FRAME_IDS;
//            }
//        }
        int playerFrameId;
        if (mVodShape == VodBannerShape.VOD_SHAPE_PORTRAIT) {
            playerFrameId = PORT_FRAME_IDS[countPort];
            countPort++;
            countPort = countPort % PORT_FRAME_IDS.length;
        }
        else if (mVodShape == VodBannerShape.VOD_SHAPE_SQUARE) {
            playerFrameId = SQUARE_FRAME_IDS[countSquare];
            countSquare++;
            countSquare = countSquare % SQUARE_FRAME_IDS.length;
        }
        else {
            playerFrameId = LAND_FRAME_IDS[countLand];
            countLand++;
            countLand = countLand % LAND_FRAME_IDS.length;
        }

        videoFrame.setId(playerFrameId);
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(playerFrameId, new VodMediaPlayerControllerFragment()).commitNow();
        player = (OnMediaPlayerController) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(playerFrameId);
        player.setOnMediaPlayerListener(VodBannerVodSharedViewHolder.this);
    }

    public void onEventMainThread(Events.VodShopPlayerEvent event) {
        switch (event.action) {
            case VOD_SCROLLING:
                Ln.i("vod scrolling get adapter: " + getAdapterPosition() + ", first: " + event.firstPosition + ", last: " + event.lastPosition);
                if (getAdapterPosition() < event.firstPosition || getAdapterPosition() > event.lastPosition) {
                    stopPlayer();
                }
                break;
            case VOD_MUTE:
                MainApplication.isMute = event.on;
                chkMute.setChecked(event.on);
                break;
            case VOD_STOP:
                if (getAdapterPosition() == event.position) {
                    stopPlayer();
                }
                break;
            case VOD_MOBILE_DATA:
                if (getAdapterPosition() != event.position) {
                    hideMobileDataView();
                }
                break;

            case VOD_AUTO_PLAY:
                Ln.i("scroll get position: " + getAdapterPosition() + ", event position: " + event.position);
                if (getAdapterPosition() != event.position) {
                    // 다른 동여상은 다 종료.
                    stopPlayer();
                } else {
                    autoPlay();
                }
                break;

            case VOD_PAUSE:
                if (getAdapterPosition() != event.position && controllerView.getVisibility() == View.VISIBLE) {
                    // 로딩 중 삭제.
                    stopPlayer();
                }
                break;

            case VOD_INIT_OTHERS :
                if (getAdapterPosition() != event.position) {
                    Ln.d("VOD_INIT_OTHERS, ViewHolder");
                    onStoped();
                }
                break;
        }

    }

    private void stopPlayer() {
        Ln.d("VOD_INIT_OTHERS, stopPlayer ViewHolder");
        showLoadingProgress(false);
        hideMobileDataView();
        ViewUtils.showViews(controllerView, playButton, remainedTimeText, tag_container, viewVideoImage, viewDummyThumb);
        ViewUtils.hideViews(close_vod, viewVideoImageFitToH, viewDummyPlayer);
        remainedTimeText.setText(content.videoTime);

        if (isNotEmpty(player)) {
            try {
                sendWiseLog(PlayerAction.PAUSE, player.getBaseVideoView().getCurrentPosition(), player.getBaseVideoView().getDuration());
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().remove((Fragment) player).commitNow();
                ((FragmentActivity) context).getSupportFragmentManager().executePendingTransactions();
            }
            catch ( IllegalStateException e ) {
                Ln.e(e.getMessage());
            }
            catch ( IllegalArgumentException e) {
                Ln.e(e.getMessage());
            }
            player = null;
        }

    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        Ln.i("vod scrolling detached get adapter");
        // 기존에 수행하는 runnable이 있으면 삭제
        mHandler.removeCallbacks(mRunnable);
        Ln.i("VodBannerVodSharedViewHolder_play compare pos : " + mPlayingPosition + " / " + getAdapterPosition());
        if (mPlayingPosition == getAdapterPosition()) {
            isPlayingNow = false;
        }
        stopPlayer();
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        ViewUtils.showViews(controllerView, playButton);
        chkMute.setChecked(MainApplication.isMute);
        remainedTimeText.setText(content.videoTime);
        hideMobileDataView();
    }

    @Override
    public int getListPosition() {
        return getAdapterPosition();
    }

    @Override
    public void onPlayed() {
        //재생이 완료되면 다음 재생부터는 BUFFERING_COMPLETED 이벤트가 발생하지 않기때문에
        //DID_PLAY 이벤트 발생시에도 버퍼링동안 표시하는 뷰를 비노출하는 로직이 필요함
        ViewUtils.hideViews(controllerView, tag_container);
        ViewUtils.showViews(videoFrame, viewDummyPlayer);
    }

    @Override
    public void onVideoSizeKnown() {
        showLoadingProgressWithDelay();
    }

    private void showLoadingProgressWithDelay() {
        new Handler().postDelayed(() -> {
            isPlayingNow = false;
            Ln.d("VodBannerVodSharedViewHolder_play ready current vod");
            showLoadingProgress(false);
            ViewUtils.hideViews(controllerView, tag_container);
            ViewUtils.showViews(videoFrame, viewDummyPlayer);
        }, 500);
    }

    @Override
    public void onPaused() {
    }

    @Override
    public void onStoped() {
        stopPlayer();
        hideViewWhenStoped();
    }

    @Override
    public void onFinished(MediaInfo media) {
        ViewUtils.hideViews(videoFrame, playButton, viewVideoImage, viewDummyThumb);
        ViewUtils.showViews(controllerView, remainedTimeText, chkMute, zoomView, viewVideoImageFitToH, goPrdLayout, replayButton, close_vod, viewDummyPlayer);
    }

    @Override
    public void onError(Exception e) {
        //비디오정보가 없는 경우
        Toast.makeText(context, R.string.video_play_error, Toast.LENGTH_SHORT)
                .show();
        stopPlayer();
    }

    @Override
    public void onDetached() {
//        Ln.d("VodBannerVodSharedViewHolder_play onDetached");
        mHandler.removeCallbacks(mRunnable);
        Ln.d("VOD_INIT_OTHERS, onDetached ViewHolder");
        hideViewWhenStoped();
    }

    @Override
    public void onTap(boolean show) {

    }

    @Override
    public void onRemainedTime(String remainedTime) {
        if(isNotEmpty(player) && isNotEmpty(remainedTime)) {
            remainedTimeText.setText(remainedTime);
        }
    }

    @Override
    public void onFullScreenClick(MediaInfo media) {
        if (ClickUtils.work(2000)) {
            return;
        }

        Intent intent = new Intent(Keys.ACTION.LIVE_VIDEO_PLAYER);

        intent.putExtra(Keys.INTENT.VIDEO_PRD_URL, isNotEmpty(prdItem) ? prdItem.linkUrl : "");
        intent.putExtra(Keys.INTENT.VIDEO_IMAGE_URL, isNotEmpty(prdItem) ? prdItem.imageUrl : "");
        intent.putExtra(Keys.INTENT.VIDEO_ID, media.videoId);
        intent.putExtra(Keys.INTENT.VIDEO_START_TIME, media.currentPosition);
        intent.putExtra(Keys.INTENT.VIDEO_IS_PLAYING, media.isPlaying);

        if (mVodShape == VodBannerShape.VOD_SHAPE_PORTRAIT) {
            intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, SCREEN_ORIENTATION_PORTRAIT);
        }
        else if (mVodShape == VodBannerShape.VOD_SHAPE_SQUARE) {
            intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, SCREEN_ORIENTATION_LANDSCAPE);
        }
        intent.putExtra(Keys.INTENT.FOR_RESULT, true);

        ((Activity) context).startActivityForResult(intent, Keys.REQCODE.VIDEO);
    }

    @Override
    public void sendWiseLog(PlayerAction action, int playTime, int totalTime) {
        String url = VOD_BRIGHTCOVE_PLAYER.replace("#ACTION", action.toString())
                .replace("#VID", content.videoid)
                .replace("#AUTOPLAY", MainApplication.isAutoPlay)
                .replace("#PT", Integer.toString(playTime / 1000))
                .replace("#TT", Integer.toString(totalTime / 1000))
                .replace("#PRDID", isNotEmpty(prdItem) ? prdItem.dealNo : "");

        if (action == PlayerAction.LTE_Y) {
            //LTE_Y와 PLAY 액션이 30ms 이내에 같이 호출되어 먼저 호출된 액션(LTE_Y)이 누락되는 현상 발생
            //LTE_Y는 별도 웹뷰로 전송
            ((HomeActivity) context).setWiseLogHttpClient(url);
        } else {
            ((HomeActivity) context).setWiseLog(url);
        }
    }

    /**
     * 프로그레스바 노출/비노출 처리
     *
     * @param show show
     */
    private void showLoadingProgress(boolean show) {
        if (show) {
            //플레이어 이미지 변경되면서 프로그래스바 숨기도록 변경
            //ViewUtils.showViews(loadingProgress);
            ViewUtils.hideViews(playButton, replayButton);
        } else {
            //ViewUtils.hideViews(loadingProgress);
        }
    }


    /**
     * 과금팝업 숨김 및 디폴트 UI 노출
     */
    private void hideMobileDataView() {
        ViewUtils.hideViews(mobileDataView, chkMute, zoomView);
        ViewUtils.showViews(playButton, remainedTimeText);
    }

    /**
     * 상품상세 페이지로 이동
     */
    @Override
    public void onProductInfo() {
        try {
            ((HomeActivity) context).setWiseLog(VOD_GOPRD_CLICK);

        }catch (Exception e){

        }
        WebUtils.goWeb(context, prdItem.linkUrl);
    }

    private void hideViewWhenStoped() {
        ViewUtils.hideViews(videoFrame, chkMute, zoomView, viewVideoImageFitToH, goPrdLayout, replayButton, viewDummyPlayer);
        ViewUtils.showViews(controllerView, playButton, viewVideoImage, remainedTimeText, viewDummyThumb);
    }

        
}
