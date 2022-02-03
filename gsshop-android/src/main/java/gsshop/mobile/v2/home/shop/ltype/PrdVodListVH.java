package gsshop.mobile.v2.home.shop.ltype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.exoplayer2.Player;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfo;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.tv.LiveVideoMediaPlayerActivity;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.web.WebUtils;
import gsshop.mobile.v2.web.productDetail.ActivityDetailViewLiveVideoMediaPlayer;
import gsshop.mobile.v2.web.productDetail.video.brightcove.FragmentDetailViewBrightcoveController;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_VOD_LIST;
import static gsshop.mobile.v2.home.shop.vod.VodBannerVodSharedViewHolder.LAND_FRAME_IDS;

public class PrdVodListVH extends BaseViewHolderV2 implements OnMediaPlayerListener {
    private Context mContext;

    /**
     * 영상 영역
     */
    private ConstraintLayout mClVodImg;
    private FrameLayout mFlVideoFrame;
    private ImageView mIvVodImg;
    private ImageView mIvPlayImg;
    private FrameLayout mFlMobileData;
    private Button mBtnMobileDataOk;
    private Button mBtnMobileDataCancel;
    private LinearLayout mLlPrdInfo;

    /**
     * 미디어정보 저장
     */
    private MediaInfo mMediaInfo;
    /**
     * 플레이어 컨트롤러
     */
    private OnMediaPlayerController mPlayer;
    /**
     * 재생버튼 클릭시 첫 1회만 효율코드를 전송하기 위함
     */
    private boolean isFirst = true;
    /**
     * 전체화면에서 복귀한 경우 PLAY 효율코드를 전송안하기 위함
     */
    private boolean isBackFullScreen = false;
    /**
     * 전체화면 진입시에는 removePlayer() 호출을 스킵하기 위함
     * (전체화면에서 재생중 복귀시 인앱플레이어도 재생상태를 유지해야 하기 때문)
     */
    private boolean clickFullScreen = false;

    /**
     * 앱이 장시간 백그라운드 상태에서 포그라운드 전환시
     * 브라이트코브 SDK의 fragmentResumed에서 아래 url 호출하면서 EventType.ERROR 다수 발생
     * 에러 : error { errorMessage: onLoadError: sourceId: -1 source: Source{deliveryType: video/mpeg-dash, url: https://manifest.prod.boltdns.net...
     * 이때는 토스트를 노출하지 않기 위함.
     */
    private boolean skipToast = false;

    /**
     * 비디오 프레그먼트 아이디참조 인덱스
     */
    private static int count = 0;

    /**
     * 하단 디바이더가 다음 뷰타입과 같으면 1dp 다르면 10dp
     */
    private View mViewBottomDivider1dp;
    private View mViewBottomDivider10dp;

    /**
     * 효율코드 중복전송 방지용 (플레이어에서 중복으로 콜백이 호출되는 경우가 있음)
     */
    private PlayerAction mOldAction = PlayerAction.NONE;

    /**
     * 하단 상품 영역
     */
    private TextView mTvBrdTime;
    private ImageView mIvSubPrdImg;
    private RenewalLayoutProductInfo mLayoutProductInfo;

    /**
     * 로딩프로그레스
     */
    private ProgressBar mProgressBar;

    public PrdVodListVH(View itemView) {
        super(itemView);

        findViews(itemView);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleLists) {
        super.onBindViewHolder(context, position, moduleLists);
        mContext = context;
        //초기화
        isFirst = true;

        ModuleList moduleList = moduleLists.get(position);

        setViews(moduleList.productList.get(0));

        //비디오아이디 유무 별 UI 분기
        if (isNotEmpty(moduleList.videoid)) {
            setClickListener();

            ViewUtils.showViews(mClVodImg, mIvPlayImg);

            //브라이트코브 섬네일
            ImageUtil.loadImage(mContext, moduleList.vodImg, mIvVodImg, R.drawable.noimage_375_188);

            mIvPlayImg.setOnClickListener(v -> {
                if (!NetworkStatus.isNetworkConnected(mContext)) {
                    NetworkUtils.showUnstableAlert((Activity) mContext);
                    return;
                }
                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.videoId = moduleList.videoid;
                mediaInfo.posterImageUrl = moduleList.vodImg;
                this.mMediaInfo = mediaInfo;
                confirmMobileData();
            });
        } else {
        }

        setNextItemMargin(moduleLists, position);
    }

    @Override
    public void onVideoSizeKnown() {
        ViewUtils.hideViews(mProgressBar);
    }

    @Override
    public void videoDurationChanged(int duration) {
        //전체화면에서 복귀한 경우 스킵
        if (isBackFullScreen) {
            return;
        }

        //재생버튼 클릭 시 효율코드(PLAY) 전송 용도 (효율 전송시 일괄적으로 브라이트코브 데이타를 사용하기 위함)
        //이렇게 하면 API에서 내려주는 재생시간을 사용하지 않으므로 재생시간 비동기 문제가 발생 안함
        if (isFirst && !mIvPlayImg.isShown()) {
            isFirst = false;
            sendWiseLogPrdNative(PlayerAction.PLAY, 0, duration);
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        EventBus.getDefault().unregister(this);
        removePlayer();
    }

    @Override
    public void onFragmentResume() {
        skipToast = true;
        new Handler().postDelayed(() -> {
            skipToast = false;
        }, 5000);
    }

    @Override
    public void onError(Exception e) {
        if (!skipToast) {
            Toast.makeText(mContext, R.string.video_play_error, Toast.LENGTH_SHORT).show();
        }
        removePlayer();
    }

    @Override
    public void onFullScreenClick(MediaInfo media) {
        if (ClickUtils.work(2000)) {
            return;
        }

        clickFullScreen = true;
        new Handler().postDelayed(() -> {
            clickFullScreen = false;
        }, 2000);

        Intent intent = new Intent(mContext, ActivityDetailViewLiveVideoMediaPlayer.class);
        MainApplication.gVideoCurrentPosition = media.currentPosition;
        intent.putExtra(Keys.INTENT.VIDEO_ID, media.videoId);
        intent.putExtra(Keys.INTENT.VIDEO_URL, media.contentUri);
        intent.putExtra(Keys.INTENT.VIDEO_START_TIME, media.startTime);
        intent.putExtra(Keys.INTENT.VIDEO_END_TIME, media.endTime);
        intent.putExtra(Keys.INTENT.VIDEO_IS_PLAYING, media.isPlaying);
        intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, SCREEN_ORIENTATION_LANDSCAPE);
        intent.putExtra(Keys.INTENT.FULL_SCREEN_CALLER, LiveVideoMediaPlayerActivity.FULL_SCREEN_CALLER.SIGNATURE);
        intent.putExtra(Keys.INTENT.FOR_RESULT, true);

        ((Activity) mContext).startActivityForResult(intent, Keys.REQCODE.VIDEO);
    }


    /**
     * 미디어정보를 세팅 후 영상을 재생한다.
     */
    private void playVideo() {
        ViewUtils.showViews(mProgressBar);

        //동영상 재생 시 다른 재생중인 영상은 초기화
        EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_INIT_OTHERS, getAdapterPosition()));

        if (isEmpty(mPlayer)) {
            setPlayer();
        }

        MediaInfo media = mPlayer.getMediaInfo();
        if (isEmpty(media)) {
            media = new MediaInfo();
            media.currentPosition = -1;
            media.lastPlaybackState = Player.STATE_IDLE;
            media.playerMode = MediaInfo.MODE_SIMPLE;
        }

        media.videoId = mMediaInfo.videoId;
        media.posterImageUrl = mMediaInfo.posterImageUrl;
        media.currentPosition = 0;
        media.isPlaying = true;

        mPlayer.setMediaInfo(media);
        mPlayer.playPlayer();
    }

    /**
     * mVideoFrame에 플레이어를 추가한다.
     */
    private void setPlayer() {
        int playerFrameId = LAND_FRAME_IDS[count];
        count++;
        count = count % LAND_FRAME_IDS.length;
        mFlVideoFrame.setId(playerFrameId);

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(playerFrameId,
                FragmentDetailViewBrightcoveController.newInstance(FragmentDetailViewBrightcoveController.CALLER.SIGNATURE)).commitNowAllowingStateLoss();
        mPlayer = (OnMediaPlayerController) ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentById(playerFrameId);
        mPlayer.setOnMediaPlayerListener(PrdVodListVH.this);
    }

    /**
     * mVideoFrame에서 플레이어를 제거한다.
     */
    private void removePlayer() {
        if (isNotEmpty(mPlayer)) {
            try {
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().remove((Fragment) mPlayer).commitNowAllowingStateLoss();
                ((FragmentActivity) mContext).getSupportFragmentManager().executePendingTransactions();
            } catch (Exception e) {
                //동작에 이상은 없으나 해결방법은 찾아보자
                //IllegalStateException : FragmentManager is already executing transactions
                //Ln.e(e);
            }

            ViewUtils.hideViews(mProgressBar);
            ViewUtils.showViews(mClVodImg, mIvVodImg, mIvPlayImg);
            isFirst = true;
            mPlayer = null;
        }
    }

    /**
     * 과금안내 팝업을 노출한다.
     */
    private void confirmMobileData() {
        if (!NetworkStatus.isWifiConnected(mContext) && !MainApplication.isNetworkApproved) {
            //팝업노출
            ViewUtils.hideViews(mIvPlayImg);
            ViewUtils.showViews(mFlMobileData);
        } else {
            //WIFI or 기 승인
            ViewUtils.hideViews(mIvVodImg, mIvPlayImg);
            ViewUtils.showViews(mFlVideoFrame);
            playVideo();
        }
    }

    /**
     * 클릭리스너를 등록한다.
     */
    private void setClickListener() {
        mBtnMobileDataOk.setOnClickListener(v -> {
            ViewUtils.hideViews(mFlMobileData, mIvVodImg, mIvPlayImg);
            ViewUtils.showViews(mFlVideoFrame);
            playVideo();
            MainApplication.isNetworkApproved = true;
            sendWiseLogPrdNative(PlayerAction.LTE_Y, 0, 0);
        });

        mBtnMobileDataCancel.setOnClickListener(v -> {
            ViewUtils.hideViews(mFlMobileData);
            ViewUtils.showViews(mIvPlayImg);
            sendWiseLogPrdNative(PlayerAction.LTE_N, 0, 0);
        });
    }


    /////////////////////////////////////////////////////////////////////////       Event
    /**
     * 매장 좌우 플리킹시 재생중인 동영상을 정지한다.
     * 동영상 재생 시 재생중인 다른 동영상을 정지한다.
     *
     * @param event Events.VodShopPlayerEvent
     */
    public void onEvent(Events.VodShopPlayerEvent event) {
        if (isEmpty(mPlayer)) {
            return;
        }
        if (clickFullScreen) {
            return;
        }
        if (event.action == Events.VodShopPlayerEvent.VodPlayerAction.VOD_STOP) {
            removePlayer();
        }

        if (event.action == Events.VodShopPlayerEvent.VodPlayerAction.VOD_INIT_OTHERS
                && getAdapterPosition() != event.position) {
            removePlayer();
        }
    }

    /**
     * 동영상 전체화면의 액션을 전달받아 와이즈로그로 전송한다.
     *
     * @param event Events.VideoActionEvent
     */
    public void onEvent(Events.VideoActionEvent event) {
        //매장내 동영상이 복수개이므로 videoId 비교하여 내것만 보낸다.
        if (isNotEmpty(mMediaInfo) && mMediaInfo.videoId.equals(event.videoId)) {
            sendWiseLogPrdNative(event.playerAction, event.playTime, event.totalTime);
            if (isNotEmpty(mPlayer)  && (event.playerAction == PlayerAction.MINI || event.playerAction == PlayerAction.EXIT)) {
                mPlayer.onFullscreenDisabled(false);
                isBackFullScreen = true;
                new Handler().postDelayed(() -> {
                    isBackFullScreen = false;
                }, 1000);
            }
        }
    }

    @Override
    public void sendWiseLogPrdNative(PlayerAction action, int playTime, int totalTime) {
        //동일한 액션이 중복 호출될 경우 스킵
        if (ClickUtils.work2(300) && action == mOldAction) {
            return;
        }

        mOldAction = action;

        String videoId = isNotEmpty(mMediaInfo) ? mMediaInfo.videoId : "";
        String url = MSEQ_PRD_VOD_LIST.replace("#PT", Integer.toString(playTime / 1000))
                .replace("#TT", Integer.toString(totalTime / 1000))
                .replace("#VID", videoId)
                .replace("#ACTION", action.toString());

        ((AbstractBaseActivity) mContext).setWiseLogHttpClient(url);
    }

    /////////////////////////////////////////////////////////////////////////       View setting
    /**
     * 영상 하단 부상품? 영역을 공통 모듈 사용하여 셋팅.
     *
     * @param item ProductInfo (productList 에 무조건 1개)
     */
    private void setViews(SectionContentList item) {
        //가격표시용 공통모듈에 맞게 데이타 변경
        ProductInfo info = SetDtoUtil.setDto(item);
        mLayoutProductInfo.setViews(info, SetDtoUtil.BroadComponentType.tv_new);

        //섬네일 로딩
        ImageUtil.loadImage(mContext, item.imageUrl, mIvSubPrdImg, R.drawable.noimage_166_166);

        //방송시간 표시
        mTvBrdTime.setVisibility(View.GONE);
        if (item.broadTimeText != null && DisplayUtils.isValidString(item.broadTimeText)) {
            mTvBrdTime.setText(item.broadTimeText);
            mTvBrdTime.setVisibility(View.VISIBLE);
        }

        mLlPrdInfo.setOnClickListener(v -> WebUtils.goWeb(mContext, item.linkUrl));
    }

    /**
     * 다음 아이템과 동일한 뷰타입인 경우는 1dp, 아인 경우는 10dp의 마진을 설정한다.
     *
     * @param moduleLists List<ModuleList>
     * @param position 뷰홀더 포지션
     */
    private void setNextItemMargin(List<ModuleList> moduleLists, int position) {
        boolean isSameToNext = false;
        ModuleList moduleList = moduleLists.get(position);
        if (moduleLists.size() > position + 1) {
            ModuleList nextItem = moduleLists.get(position + 1);
            // 다음에 오는 아이템이 같을 경우에만 isSameToNext = true 였는데, PRD_VOD_LIST 이후에 PRD_1_640 이 오는 경우에도 true 한다.
            if ((isNotEmpty(nextItem) && isNotEmpty(nextItem.viewType) &&
                    isNotEmpty(moduleList) && isNotEmpty(moduleList.viewType)) &&
                    (moduleList.viewType.equals(nextItem.viewType) || "PRD_1_640".equals(nextItem.viewType))) {
                isSameToNext = true;
            }
        }
        if (isSameToNext) {
            mViewBottomDivider1dp.setVisibility(View.VISIBLE);
            mViewBottomDivider10dp.setVisibility(View.GONE);
        } else {
            mViewBottomDivider1dp.setVisibility(View.GONE);
            mViewBottomDivider10dp.setVisibility(View.VISIBLE);
        }
    }

    private void findViews(View itemView) {
        mFlVideoFrame = itemView.findViewById(R.id.fl_video_frame);
        mClVodImg = itemView.findViewById(R.id.cl_vod_img);
        mIvPlayImg = itemView.findViewById(R.id.iv_play_img);
        mIvVodImg = itemView.findViewById(R.id.iv_vod_img);
        mFlMobileData = itemView.findViewById(R.id.fl_mobile_data);
        mBtnMobileDataOk = itemView.findViewById(R.id.button_mobile_data_ok);
        mBtnMobileDataCancel = itemView.findViewById(R.id.button_mobile_data_cancel);
        mViewBottomDivider1dp = itemView.findViewById(R.id.view_bottom_divider_1dp);
        mViewBottomDivider10dp = itemView.findViewById(R.id.view_bottom_divider_10dp);
        mProgressBar = itemView.findViewById(R.id.mc_webview_progress_bar);
        mLlPrdInfo = itemView.findViewById(R.id.ll_prd_info);
        mTvBrdTime = itemView.findViewById(R.id.tv_brd_time);
        mLayoutProductInfo = itemView.findViewById(R.id.layout_product_info);
        mIvSubPrdImg = itemView.findViewById(R.id.iv_prd_img);
    }
}
