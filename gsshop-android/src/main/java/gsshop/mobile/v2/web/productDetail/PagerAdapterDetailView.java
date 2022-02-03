package gsshop.mobile.v2.web.productDetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apptimize.Apptimize;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.util.Util;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.ui.util.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.intro.ApptimizeCommand;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;
import gsshop.mobile.v2.web.productDetail.video.brightcove.FragmentDetailViewBrightcoveController;
import gsshop.mobile.v2.web.productDetail.video.live.FragmentDetailViewExoLivePlayerController;
import roboguice.util.Ln;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_PRD_LIVE_AUTO_PLAY;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_PRD_LIVE_MANUAL_PLAY;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_PRD_VOD_AUTO_PLAY;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_PRD_VOD_MANUAL_PLAY;

public class PagerAdapterDetailView extends PagerAdapter implements OnMediaPlayerListener{

    private Context mContext;

    /**
     * 전달 받는 Native Product data
     */
    private NativeProductV2 mData;

    /**
     * 전달 받은 NativeProduct data를 이용하여 만들 리스트 데이터
     */
    private ArrayList<DetailViewListData> mList = new ArrayList<>();

    /**
     * 영상 데이터
     */
    private DetailViewListData mVideoData = null;

    /**
     * 플레이어 컨트롤러 (라이브 / VOD)
     */
    private OnMediaPlayerController mPlayer;

    /**
     * 플레이어 컨트롤러를 replace 할 뷰
     */
    private View mViewPlayer = null;

    /**
     * 플레이어 전체 View, destroy 할때에 따로 저장해 놓기 위함.
     */
    private View mViewAll = null;

    /**
     * 플레이어 컨틀롤러 뷰가 페이저에서 Destroy 될 때에 잠시 붙여넣을 뷰. (다시 돌아왔을 때에 이어 재생되도록 재사용 하기 위함)
     */
    private LinearLayout mLayoutAddTemp;

    /**
     * 캐시된 이미지가 있을 경우 mViewAll 재사용을 스킵하기 위한 플래그
     */
    private boolean isSkip = false;

    // Content 타입
    public enum ContentType{
        TYPE_IMAGE,
        TYPE_VOD,
        TYPE_MP4,
        TYPE_LIVE,
        TYPE_DATA
    }

    private OnMediaPlayerListener mListener;

    // 영상 관련 UI
    private ImageView mViewBtnPlay;                 // 영상 플레이 버튼
    private View mViewVideoController;              // 영상 컨트롤 영역
    private ImageView mViewVideoImage;              // 영상 default 이미지
    private View mViewPlayerMain;                   // 플레이어 뷰
    private View mViewBottomController;             // 하단 컨트롤러 영역
    private View mViewNoticeDataFee;                // 데이터 부과 UI
    private BroadTimeLayoutDetailView mViewBroadTime;   // 시간 표시 UI

//    private ControllerFragmentDetailView mControllerFragmentDetailView = new ControllerFragmentDetailView();

    // 영상이 하나이라는것을 가정 하고 해당 변수 설정. 추후 영상이 여러개 일때는 전면 수정 필요.
    protected boolean mIsLive = true;


    public PagerAdapterDetailView(Context context) {
        this.mContext = context;
        this.mListener = null;
        mLayoutAddTemp = new LinearLayout(context);
    }

    public PagerAdapterDetailView(Context context, NativeProductV2 data) {
        this.mContext = context;
        this.mListener = null;
        mLayoutAddTemp = new LinearLayout(context);
        if (isNotEmpty(data)) {
            setData(data);
        }
    }

    public void setListener(OnMediaPlayerListener listener) {
        mListener = listener;
    }

    // 비디오 존재 여부 체크
    protected boolean isVideoExist = false;

    protected void setData(NativeProductV2 data) {
//        설정을 하려면 components 의 크기가 0보다는 커야 한다.
        if (!(data.components.size() > 0)) {
            return;
        }
        isSkip = true;
        mData = data;
        // list 데이터 초기화
        if (mList == null)
            mList = new ArrayList<>();
        mList.clear();
        // 라이브 이면 img의 2번째 리스트 부터 이미지 취급.
        for (NativeProductV2.Component component : data.components) {
            if (component != null && component.templateType !=null) {
                if ("mediainfo".equals(component.templateType.trim().toLowerCase())) {
//                    mDataVideo = component;
                    // 비디오 리스트가 있는지 확인.
                    if (component.videoList != null && component.videoList.size() > 0) {
                        DetailViewListData videoData;
                        // 비디오 리스트의 첫번째 데이터 (모듈컨텐츠AB 대상이면서 videoPreImgUrl이 유효하면 첫번째이미지로 추가) - 실험끝나면 제거예정 bbori
                        if(ApptimizeExpManager.findExpInstance(ApptimizeExpManager.CONTENTS) != null && "Y".equals(mData.isContentAB)){ //생방송일때 videoList.size = 2, 아닐때 videoList.size = 1
                            if(DisplayUtils.isValidString(component.videoList.get(0).videoPreImgUrl)){
                                videoData = getVideoData(component.videoList.get(0), component.videoList.get(0).videoPreImgUrl);
                            }else{
                                videoData = getVideoData(component.videoList.get(0), data.chachedImgUrl);
                            }
                        }else{
                            videoData = getVideoData(component.videoList.get(0), data.chachedImgUrl);
                        }

                        // 데이터가 정상 이면 add
                        if (videoData != null) {
                            // 샤피라이브 데이터가 있으면 영상 네트워크 확인하지 않는다.
                            if (!TextUtils.isEmpty(data.shoppygateurl)) {
                                videoData.checkNetwork = false;
                            }
                            mVideoData = videoData;
                            mList.add(videoData);
                            isVideoExist = true;
                        }

                        // 비디오가 없고 다음 비디오 데이터가 존재한다면.
                        if (!isVideoExist && component.videoList.size() > 1) {
                            // 우선 2개까지만 확인, 재귀 안돌림.
                            DetailViewListData videoData2 = getVideoData(component.videoList.get(1), data.chachedImgUrl);
                            if (videoData2 != null) {
                                // 샤피라이브 데이터가 있으면 영상 네트워크 확인하지 않는다.
                                if (!TextUtils.isEmpty(data.shoppygateurl)) {
                                    videoData2.checkNetwork = false;
                                }
                                mList.add(videoData2);
                                mVideoData = videoData2;
                                isVideoExist = true;
                            }
                        }

                        // 비디오 정보가 정상적이지 않다고 판단되면 이미지 정보를 삽입.
                        if (!isVideoExist){
                            mList.add(new DetailViewListData(ContentType.TYPE_IMAGE, data.chachedImgUrl, null));
                        }
                    }

                    if (component.imgUrlList != null && component.imgUrlList.size() > 0) {
                        for (String imgInfo : component.imgUrlList) {
                            mList.add(new DetailViewListData(ContentType.TYPE_IMAGE, imgInfo, null));
                        }
                    }
                    break;
                }
            }
        }
        try {
            if (mList.size() > 1) {
                if (ContentType.TYPE_LIVE.equals(mList.get(0).contentType) &&
                        ContentType.TYPE_IMAGE.equals(mList.get(1).contentType)) {
                    // 첫번째가 live고 두번째가 이미지면 첫번째 이미지 삭제.
                    mList.remove(1);
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            Ln.e(e.getMessage());
        }
    }

    /**
     * 비디오 데이터를 추출해서 새로 사용하는 class를 만들어 반납.
     * @param videoData
     * @param ImageUrl
     * @return
     */
    private DetailViewListData getVideoData(NativeProductV2.VideoList videoData, String ImageUrl) {
        if (videoData == null) {
            videoData = new NativeProductV2.VideoList();
        }
        String videoId = videoData.videoId;
        String videoPath = videoData.videoUrl;
        if (!TextUtils.isEmpty(videoPath)) {

            Uri uri = Uri.parse(videoPath);
            int type = Util.inferContentType(uri);
            if (type != C.TYPE_HLS && type != C.TYPE_OTHER) {
                return null;
            }

            String fileExtention = videoPath.substring(videoPath.lastIndexOf(".") + 1);

            if (TextUtils.isEmpty(videoData.liveYN)) {
                videoData.liveYN = "N";
            }
            if ("y".equals(videoData.liveYN.toLowerCase()) && "m3u8".equalsIgnoreCase(fileExtention.toLowerCase())) {
                return new DetailViewListData(ContentType.TYPE_LIVE, ImageUrl, videoData);
            }
            else if ("n".equals(videoData.liveYN.toLowerCase()) && "mp4".equalsIgnoreCase(fileExtention.toLowerCase())) {
                return new DetailViewListData(ContentType.TYPE_MP4, ImageUrl, videoData);
            }
        }
        else if (!TextUtils.isEmpty(videoId) && videoId.length() > 4) {
            return new DetailViewListData(ContentType.TYPE_VOD, ImageUrl, videoData);
        }
        return null;
    }

    protected NativeProductV2 getData() {
        return mData;
    }

    protected ArrayList<DetailViewListData> getListData() {
        return mList;
    }

    // 뷰 페이저 조작이 있을 수 있음.
    private ViewPager mPager;
    public void setPager(ViewPager pager) {
        mPager = pager;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        // must be overridden else throws exception as not overridden.
        final DetailViewListData item = mList.get(position);

        collection.removeView((View) view);
        if (ContentType.TYPE_IMAGE != item.contentType) {
            mViewAll = (View)view;
            mLayoutAddTemp.addView(mViewAll);
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (mList == null) {
            return null;
        }

        //변경될 수 있음.
        final DetailViewListData item = mList.get(position);

        // 기존에 가지고 있는 비디오 뷰가 있으면 해당 뷰를 반납
        if (ContentType.TYPE_IMAGE != item.contentType && mViewAll != null && !isSkip) {
            mLayoutAddTemp.removeView(mViewAll);
            container.addView(mViewAll);
            return mViewAll;
        }

        isSkip = false;

        View view = ((Activity) mContext).getLayoutInflater()
                .inflate(R.layout.product_detail_item, null);

        ImageView imageMainView = view.findViewById(R.id.image_main);
//        ImageView imageGoToPlayer = view.findViewById(R.id.image_move_to_player);

        // 이미지 url 없으면 디폴트 이미지 노출
        ImageUtil.loadImageFromCache(mContext, item.mImageUrl, imageMainView, R.drawable.noimage_166_166, ImageUtil.ScaleType.SCALE_TYPE_FIT_XY);

        // 영상 전체 view
        FrameLayout viewVod = view.findViewById(R.id.view_vod);
        // 영상 전체 컨트롤러 (플레이 / 뮤트 / 전체화면 / 시간표시)
        View viewVodController = view.findViewById(R.id.view_vod_controller);
        // 과금 메세지 영역
        View viewNoticeDataFee = view.findViewById(R.id.view_vod_mobile_data);
        // 영상 하단 컨트롤러 영역 (뮤트 / 전체화면)
        View viewVodBottomController = view.findViewById(R.id.view_vod_bottom_controller);
        // 영상 상단 프레임.
        FrameLayout viewMedia = view.findViewById(R.id.view_media);
        // 방송 종료 메세지
        TextView textLiveEnd = view.findViewById(R.id.text_live_end);
        final FrameLayout viewPlayer = view.findViewById(R.id.view_player);
        // 음소거 버튼
        CheckBox chkMute = view.findViewById(R.id.check_vod_mute);
        // 플레이 버튼
        ImageView imageViewPlay = view.findViewById(R.id.imageview_play);
        // 전체화면 버튼
        View viewFullScreen = view.findViewById(R.id.view_vod_fullscreen);
        // 과금 메세지 확인 / 취소 버튼
        Button btnDataFeeCancel = viewNoticeDataFee.findViewById(R.id.button_mobile_data_cancel);
        Button btnDataFeeAgree = viewNoticeDataFee.findViewById(R.id.button_mobile_data_ok);
        BroadTimeLayoutDetailView cvReaminTime = view.findViewById(R.id.cv_remain_time);

        if (ContentType.TYPE_VOD == item.contentType || ContentType.TYPE_MP4 == item.contentType || ContentType.TYPE_LIVE == item.contentType) {
            ViewUtils.showViews(viewVod, viewVodController, imageMainView);
            /**
             * 영상 UI 멤버로 가지고 있게끔 변수 설정
             * 초기에 생각을 잘 못해서 영상 뷰를 따로 만들지 않고 하다 보니, 멤버 변수로 가지고 있게끔 개발 되었습니다.
             * 추 후 영상 객체를 따로 만들게끔 수정 예정입니다.
             */
            mViewVideoController = viewVodController;
            mViewBtnPlay = imageViewPlay;
            mViewVideoImage = imageMainView;
            mViewPlayerMain = viewMedia;
            mViewBottomController = viewVodBottomController;
            mViewBroadTime = cvReaminTime;
            mViewNoticeDataFee = viewNoticeDataFee;

            if (ContentType.TYPE_LIVE == item.contentType) {
                mIsLive = true;
                setLivePlayer(viewPlayer, item);
            }
            else {
                mIsLive = false;
                setPlayer(viewPlayer, item);
            }
//            imageGoToPlayer.setVisibility(View.GONE);
//            ((ProductDetailWebActivityV2) mContext).showMiniPlayButton(false);
        }
        else {
            if (isVideoExist) {
//                ((ProductDetailWebActivityV2) mContext).showMiniPlayButton(true);
            }
            imageMainView.setVisibility(View.VISIBLE);
            viewMedia.setVisibility(View.GONE);
            viewVodController.setVisibility(View.GONE);
        }

        // 클릭 리스너
        View.OnClickListener onClickBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 클릭이 일어 났을 때에 msq를 날려주기 위해 videoID 와 시간을 계산.
                // ================================================
                String videoId = null;

                int videoEndTime = 0;
                if (mVideoData.contentType == ContentType.TYPE_VOD) {
                    videoId = mVideoData.mVideoInfo.videoId;
                }

                // 전체 시간 전달.
                try {
                    videoEndTime = Integer.parseInt(mVideoData.mVideoInfo.endTime);
                }
                catch (NumberFormatException e) {
                    Ln.e(e.getMessage());
                }
                // ================================================

                switch (v.getId()) {
                    // 플레이 버튼 클릭
                    case R.id.imageview_play :
                        playButtonPerformClick(item, viewPlayer);

                        // 영상 Amplitude // 영상 mseq
                        if (ContentType.TYPE_LIVE == item.contentType) {
                            // LivePlay 버튼 선택시 AMP
                            ProductDetailWebActivityV2.prdAmpSend(AMP_PRD_LIVE_MANUAL_PLAY);
                            // LivePlay 버튼 선택시 mseq
                            sendWiseLogPrdNative(PlayerAction.PLAY,
                                    true, false, null, 0, 0);
                        }
                        else {
                            // VOD 플레이 버튼 선택시 AMP
                            ProductDetailWebActivityV2.prdAmpSend(AMP_PRD_VOD_MANUAL_PLAY);

                            // LivePlay 버튼 선택시 mseq
                            sendWiseLogPrdNative(PlayerAction.PLAY,
                                    false, false, videoId, 0, videoEndTime);
                        }

                        // 모듈화컨텐츠 AB테스트 - 대상단품페이지 영상재생시 (실험대상이면서 실험대상상품이면 효율보냄)
                        if(ApptimizeCommand.ABINFO_VALUE.contains(ApptimizeExpManager.CONTENTS)){
                            if("Y".equals(mData.isContentAB)) {
                                //앰플리튜드를 위한 코드
                                try {
                                    JSONObject eventProperties = new JSONObject();
                                    eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.AMP_CLICK_CONTENTS_PRD);

                                    eventProperties.put(AMPEnum.AMP_PRD_CODE, mData.prdCd);
                                    eventProperties.put(AMPEnum.AMP_AB_INFO, ApptimizeCommand.ABINFO_VALUE);
                                    AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_CONTENTS_PRD,eventProperties);
                                }catch (Exception e){
                                    Ln.e(e);
                                }

                                //앱티마이즈를 위한 코드
                                Apptimize.track(AMPEnum.APPTI_CLICK_CONTENTS_PRD);

                                //mseq전달
                                ((ProductDetailWebActivityV2) mContext).setWiseLogHttpClient(ServerUrls.WEB.MSEQ_CLICK_CONTENTS_AB);
                            }
                        }
                        break;

                    // 과금 메세지 캔슬
                    case R.id.button_mobile_data_cancel :
                        viewNoticeDataFee.setVisibility(View.GONE);
                        viewVodController.setVisibility(View.VISIBLE);

                        // wise log
                        if (ContentType.TYPE_LIVE == item.contentType) {
                            sendWiseLogPrdNative(PlayerAction.LTE_N,
                                    true, false, null, 0, 0);
                        }
                        else {
                            sendWiseLogPrdNative(PlayerAction.LTE_N,
                                    false, false, videoId, 0, videoEndTime);
                        }

                        break;
                    // 과금 메세지 승인.
                    case R.id.button_mobile_data_ok :
                        if (mListener != null) {
                            mListener.onNoDataWarningApproved();
                        }
                        imageMainView.setVisibility(View.GONE);
                        viewVodController.setVisibility(View.GONE);
                        viewNoticeDataFee.setVisibility(View.GONE);
                        viewMedia.setVisibility(View.VISIBLE);

                        playVideo(item, viewPlayer);

                        if (ContentType.TYPE_LIVE == item.contentType) {
                            sendWiseLogPrdNative(PlayerAction.LTE_Y,
                                    true, false, null, 0, 0);
                        }
                        else {
                            sendWiseLogPrdNative(PlayerAction.LTE_Y,
                                    false, false, videoId, 0, videoEndTime);
                        }
                        MainApplication.isNetworkApproved = true;
                        break;
                    // 첫 화면으로 이동.
                }
            }
        };

        imageViewPlay.setOnClickListener(onClickBtnListener);
        viewFullScreen.setOnClickListener(onClickBtnListener);
        btnDataFeeCancel.setOnClickListener(onClickBtnListener);
        btnDataFeeAgree.setOnClickListener(onClickBtnListener);
        cvReaminTime.setListener(new BroadTimeLayoutDetailView.OnLiveTimeRefreshListener() {
            @Override
            public void onTimeRefresh(String remainTime) {
                if (mListener != null) {
                    mListener.onRemainedTime(remainTime);
                }
            }
            @Override
            public void onBroadCastFinished() {
                ViewUtils.hideViews(mViewBottomController, imageViewPlay, mViewBroadTime);
                if(mListener != null) {
                    MediaInfo mediaInfo = new MediaInfo();
                    mediaInfo.videoMode = NativeProductV2.VIDEO_MODE_LIVE;
                    mListener.onFinished(mediaInfo);
                }
            }
        });

        cvReaminTime.setVisibility(View.VISIBLE);
        if (item.mVideoInfo == null) {
            cvReaminTime.setVisibility(View.GONE);
        }
        else if (ContentType.TYPE_LIVE != item.contentType) {
            TextView remainTime = view.findViewById(R.id.txt_remain_time);
            try {
                long remainedTime = Long.parseLong(item.mVideoInfo.endTime) - Long.parseLong(item.mVideoInfo.startTime);
                remainTime.setText(StringUtils.stringForHHMM(remainedTime, true));
            }
            catch (NullPointerException e){
                Ln.e(e.getMessage());
                remainTime.setVisibility(View.GONE);
            }
            catch (NumberFormatException e) {
                Ln.e(e.getMessage());
                remainTime.setVisibility(View.GONE);
            }
        }
        else {
            cvReaminTime.updateTvLiveTime(null, item.mVideoInfo.startTime, item.mVideoInfo.endTime);
        }

        container.addView(view);
        return view;
    }

    /**
     * 영상 플레이 수행.
     * @param item
     * @param viewPlayer
     */
    private void playButtonPerformClick(DetailViewListData item, View viewPlayer) {
        mViewBtnPlay.setBackgroundResource(R.drawable.ic_play_android);

        if (!NetworkStatus.isNetworkConnected(mContext)) {
            // 네트워크 에러이면 에러 발생. 팝업 띄워 줘야 하기 때문에 해당 변수 false로 변경
            ((ProductDetailWebActivityV2)mContext).mIsErrorOccurred = false;
            onError(new Exception());
            return;
        }

        // 데이터 얼럿 발생 여부 확인. 샤피라이브 영상일 경우 network 체크 를 건너 뛰어야 함.
        if (!NetworkStatus.isWifiConnected(mContext) && !MainApplication.isNetworkApproved && item.checkNetwork) {
            try {
                mViewVideoController.setVisibility(View.GONE);
                mViewNoticeDataFee.setVisibility(View.VISIBLE);
            }
            catch (NullPointerException e) {
                Ln.e(e.getMessage());
            }
        }
        else {
            mViewVideoImage.setVisibility(View.GONE);
            mViewVideoController.setVisibility(View.GONE);
            mViewPlayerMain.setVisibility(View.VISIBLE);

            playVideo(item, viewPlayer);
        }
    }

    // 에러 발생 시에 정상적으로 onError로 안들어 오는 경우가 있어 처음 영상 시작 한 후 여도 비디오 뷰가 안보이는 경우 있으면 에러로 판단.
    private boolean mStartVideoFirst = false;
    /**
     * 영상을 재생한다.
     * @param item 영상 재생 관련 데이터
     * @param viewPlayer 영상컨트롤러를 붙일 뷰 (생방송)
     */
    private void playVideo(DetailViewListData item, View viewPlayer) {
        if (((ProductDetailWebActivityV2)mContext).mIsErrorOccurred) {
            ((ProductDetailWebActivityV2)mContext).mIsErrorOccurred = false;
            if (mPlayer != null) {
                mPlayer.reloadPlayer();
                mPlayer.setVideoStart();
            }
            return;
        }

        // item과 영상 정보가 없다면 플레이 할 수 없다.
        if (item == null || item.mVideoInfo == null) {
            return;
        }
        // 현재 플레이 중인 뷰는 멤버 변수에 저장.
        mViewPlayer = viewPlayer;

        MediaInfo media = null;
        if (mPlayer != null) {
            media = mPlayer.getMediaInfo();
        }
        if (media == null) {
            media = new MediaInfo();
            media.currentPosition = -1;
            media.lastPlaybackState = Player.STATE_IDLE;
            media.playerMode = MediaInfo.MODE_SIMPLE;
        }

        // vod 새로 시작.
        media.videoId = item.mVideoInfo.videoId;
        media.posterImageUrl = item.mImageUrl;
        media.currentPosition = 0;
        media.isPlaying = true;
        media.endTime = item.mVideoInfo.endTime;

        if (mPlayer != null) {
            mPlayer.setMediaInfo(media);
            mPlayer.playPlayer();
            mStartVideoFirst = true;
        }
    }

    /**
     * 영상 컨트롤러 replace (vod용)
     * @param contrainerView replace 할 뷰
     * @param item 영상 정보
     */
    private void setPlayer(View contrainerView, final DetailViewListData item) {

        contrainerView.setId(R.id.vod_land_video_frame_00);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isPlay = false;
                if (mData != null && ("LIVE".equals(mData.autoPLay) || "VOD".equals(mData.autoPLay))) {
                    isPlay = true;
                }
                try {
                    if (ContentType.TYPE_MP4 == item.contentType) {
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.vod_land_video_frame_00,
                                FragmentDetailViewExoLivePlayerController.newInstance(item, isPlay)).commitNowAllowingStateLoss();
                    } else {
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.vod_land_video_frame_00,
                                new FragmentDetailViewBrightcoveController()).commitNowAllowingStateLoss();
                    }
                } catch (IllegalStateException e) {
                    Ln.e(e);
                    LunaUtils.sendToLuna(mContext, e, null);
                    return;
                }
//                mPlayer = playerController;
                mPlayer = (OnMediaPlayerController) ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.vod_land_video_frame_00);
                if (mPlayer != null) {
                    mPlayer.setOnMediaPlayerListener(PagerAdapterDetailView.this);
                    if (isPlay) {
                        // VOD 자동 재생 시에 AMP
                        ProductDetailWebActivityV2.prdAmpSend(AMP_PRD_VOD_AUTO_PLAY);
//                        mViewBtnPlay.performClick();
                        playButtonPerformClick(item, contrainerView);

                        int videoEndTime = 0;
                        // 전체 시간 전달.
                        try {
                            videoEndTime = Integer.parseInt(mVideoData.mVideoInfo.endTime);
                        }
                        catch (NumberFormatException e) {
                            Ln.e(e.getMessage());
                        }

                        // wise log
                        sendWiseLogPrdNative(PlayerAction.PLAY,
                                false, true, mVideoData.mVideoInfo.videoId, 0, videoEndTime);
                    }
                }
            }
        }, 500);
    }

    /**
     * 영상 컨트롤러 replace (live용)
     * @param contrainerView replace할 뷰
     * @param item 영상 정보
     */
    private void setLivePlayer(View contrainerView, DetailViewListData item) {

        contrainerView.setId(R.id.bestdeal_live_player_area);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isPlay = false;
                if (mData != null && "LIVE".equals(mData.autoPLay)) {
                    isPlay = true;
                }

                try {
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.bestdeal_live_player_area,
                            FragmentDetailViewExoLivePlayerController.newInstance(item, isPlay)).commitNowAllowingStateLoss();
                } catch (IllegalStateException e) {
                    Ln.e(e);
                    LunaUtils.sendToLuna(mContext, e, null);
                    return;
                }
                mPlayer = (OnMediaPlayerController) ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.bestdeal_live_player_area);
                if (mPlayer != null)
                    mPlayer.setOnMediaPlayerListener(PagerAdapterDetailView.this);
                if (isPlay) {
                    // LIVE 자동 재생 시 AMP
                    ProductDetailWebActivityV2.prdAmpSend(AMP_PRD_LIVE_AUTO_PLAY);
//                    mViewBtnPlay.performClick();
                    playButtonPerformClick(item, contrainerView);

                    // wise log
                    sendWiseLogPrdNative(PlayerAction.PLAY,
                            true, true, null, 0, 0);
                }
            }
        }, 500);
    }

    /********************************
     *  영상 컨트롤 영역
     ********************************/

    /**
     * 영상 일시정지에서 재시작
     */
    protected void startPlayer() {
        if (mPlayer != null)
            mPlayer.setVideoStart();
    }

    /**
     * 영상 일시정지
     */
    protected void pausePlayer() {
        if (mPlayer != null)
            mPlayer.setVideoPause();
        try {
            ((ProductDetailWebActivityV2) mContext).goToLink("javascript:playStatus('Y')");
        }
        catch (ClassCastException e) {
            Ln.e(e.getMessage());
        }
    }

    /**
     * 영상 정지
     */
    protected void stopPlayer() {
        if (mPlayer != null)
            mPlayer.stopPlayer();
    }

    /**
     * 영상 Mute
     */
    protected void setMute(boolean isMute) {
        MainApplication.nativeProductIsMute = isMute;
        if (mPlayer != null)
            mPlayer.setMute(isMute);
    }

    /**
     * 영상 재생 중인지
     * @return isPlaying ? true : false
     */
    protected boolean isPlayingVideo() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * 첫번쨰 영상 뷰로 이동
     * @return
     */
    protected void moveToPlayerView() {
        if (mPager != null) {
            mPager.setCurrentItem(0);
        }
        if(mPlayer != null) {
            // 현재 플레이 중이라면 플레이만 시키고
            if (mViewPlayerMain.getVisibility() == View.VISIBLE) {
                if (mPlayer != null) {
                    mPlayer.setVideoStart();
                }
            }
            // 현재 플레이 중이 아니라면 처음 플레이 시킨다. 플레이 버튼 선택시 amp 수행하기 때문에 amp 추가 하지 않음.
            else {
                if (mPlayer == null) {
                    if (mViewBtnPlay != null) {
                        mViewBtnPlay.performClick();
                    }
                }
                else if (!mPlayer.isPlaying()) {
                    if (mViewBtnPlay != null) {
                        mViewBtnPlay.performClick();
                    }
                }
            }
        }
    }

    /**
     * 영상 초기화 함수.
     */
    protected void resetPlayer() {
        if (mPlayer != null)
            mPlayer.resetPlayer();
    }

    /**
     * 영상 초기화 함수.
     */
    protected void reloadPlayer() {
        if (mPlayer != null)
            mPlayer.reloadPlayer();
    }

    /********************************/

    /*************************************
     * OnMediaPlayerListener
     *************************************/

    @Override
    public void onVideoReady() {
    }

    @Override
    public void onPlayed() {
        if (mListener != null) {
            mListener.onPlayed();
        }
    }
    @Override
    public void onPaused() {
        if (mListener != null) {
            mListener.onPaused();
        }
    }
    @Override
    public void onFinished(MediaInfo media) {
        if (mListener != null) {
            mListener.onFinished(media);
        }
    }
    @Override
    public void onError(Exception e) {
        // 에러 발생 시에 영상 영역및 영상 컨트롤러 영역을 숨김.
        if (!((ProductDetailWebActivityV2) mContext).mBroadcastFinished) {
            ViewUtils.hideViews(mViewPlayerMain);
            ViewUtils.showViews(mViewVideoImage, mViewBtnPlay, mViewBroadTime);
            if (mViewNoticeDataFee != null && !mViewNoticeDataFee.isShown()) {
                ViewUtils.showViews(mViewVideoController);
            }
        }
        if (mListener != null) {
            mListener.onError(e);
        }
    }
    @Override
    public void onTap(boolean show) {
        if(mListener != null) {
            mListener.onTap(show);
        }
    }

    @Override
    public void onMute(boolean on) {
        if(mListener != null) {
            mListener.onMute(on);
        }
    }

    // remaindTime update 리스너
    @Override
    public void onRemainedTime(String remainedTime) {
        if(mListener != null) {
            mListener.onRemainedTime(remainedTime);
        }
    }

    @Override
    public void sendWiseLog(PlayerAction action, int playTime, int totalTime) {
        // 기존 sendWiseLog는 초리 하지 않는다.
    }

    @Override
    public void sendWiseLogPrdNative(PlayerAction action, int playTime, int totalTime) {
        if(mListener != null) {
            mListener.sendWiseLogPrdNative(action, mIsLive, false, mVideoData.mVideoInfo.videoId, playTime, totalTime);
        }
    }

    @Override
    public void sendWiseLogPrdNative(PlayerAction action, boolean isLive, boolean isAuto, String videoId, int playTime, int totalTime) {
        if(mListener != null) {
            mListener.sendWiseLogPrdNative(action, isLive, isAuto, videoId, playTime, totalTime);
        }
    }

    /**
     * 풀 스크린 버튼 선택 시
     * @param media
     */
    @Override
    public void onFullScreenClick(MediaInfo media) {
        if (ClickUtils.work(2000)) {
            return;
        }

        Intent intent = new Intent(mContext, ActivityDetailViewLiveVideoMediaPlayer.class);

        MainApplication.gVideoCurrentPosition = media.currentPosition;
        intent.putExtra(Keys.INTENT.VIDEO_ID, media.videoId);
        intent.putExtra(Keys.INTENT.VIDEO_URL, media.contentUri);
        intent.putExtra(Keys.INTENT.VIDEO_START_TIME, media.startTime);
        intent.putExtra(Keys.INTENT.VIDEO_END_TIME, media.endTime);
        intent.putExtra(Keys.INTENT.VIDEO_IS_PLAYING, media.isPlaying);

//        if (media.isLandscape) {
        if(getCurrentVideoHeight() > getCurrentVideoWidth()) {
            intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, SCREEN_ORIENTATION_LANDSCAPE);
        }
        intent.putExtra(Keys.INTENT.FOR_RESULT, true);

        ((Activity) mContext).startActivityForResult(intent, Keys.REQCODE.VIDEO);
    }

    /*************************************/

    /**
     * 단품 상단 미니플레이어 <-> 일반 플레이어 사이에 컨트롤러 화면을 보이고 안보이고 하는 함수.
     * @param controllerVisible controller visibility
     */
    protected void setControllerVisible(boolean controllerVisible) {
        if (mPlayer != null)
            mPlayer.setControllerVisibility(controllerVisible);
    }

    protected void showPlayControllerView(boolean controllerVisible) {
        if (mPlayer != null)
            mPlayer.showPlayControllerView(controllerVisible);
    }

    protected void showPlayerArea() {}

    /**
     * 영상 list 및 이미지 list 데이터가 각각 내려오기 떄문에 새로 list를 만들 때 쓸 데이터 클래스
     */
    public class DetailViewListData {
        public DetailViewListData(){}
//        public DetailViewListData(ContentType type, String imageUrl, String videoUrl, String videoId, NativeProduct.BroadInfo broadInfo) {
        public DetailViewListData(ContentType type, String imageUrl, NativeProductV2.VideoList moveInfo) {
            this.contentType = type;
            this.mImageUrl = imageUrl;
            this.mVideoInfo = moveInfo;
        }

        // List의 contentType
        public ContentType contentType = ContentType.TYPE_IMAGE;

        // 이미지 URL ( 있을 경우에 )
        public String mImageUrl;

        // 비디오 정보 ( 있을 경우에 )
        public NativeProductV2.VideoList mVideoInfo;

        // 네트워크 체크 여부 추가
        private boolean checkNetwork = true;
    }

    /**
     * 풀스크린 -> 일반 화면으로 돌아왔을 때
     */
    public void onFullscreenDisabled(boolean isBroadcastFinished) {
        if (mPlayer != null)
            mPlayer.onFullscreenDisabled(isBroadcastFinished);
    }

    protected View getPlayerMainView() {
        return mViewPlayer;
    }

    protected View getPlayerParentView() {
        return mViewPlayerMain;
    }

    protected void setShowBroadcastFinishView() {
        if (mViewNoticeDataFee != null && !mViewNoticeDataFee.isShown()) {
            ViewUtils.showViews(mViewVideoController);
        }
        ViewUtils.hideViews(mViewBottomController, mViewBtnPlay);
    }

    /**
     * 현재 재생 중인 비디오의 높이 반환
     * @return
     */
    public int getCurrentVideoHeight() {
        if (mPlayer == null) return 0;
        return mPlayer.getVideoHeight();
    }

    /**
     * 현재 재생 중인 비디오의 넓이 반환
     * @return
     */
    public int getCurrentVideoWidth() {
        if (mPlayer == null) return 0;
        return mPlayer.getVideoWidth();
    }

    private boolean isPlaying = false;
    private ContentType mCurrentContentType = null;
    /**
     * 플레이 여부에 따라 현재 플레이어 재생 / 멈춤 설정
     */
    protected void setVideoPlay(int position) {
        final DetailViewListData item = mList.get(position);
        if (item.contentType == ContentType.TYPE_IMAGE) {
            if (mCurrentContentType != ContentType.TYPE_IMAGE) {
                if (mPlayer != null) {
                    isPlaying = mPlayer.isPlaying();
                    if (isPlaying) {
                        pausePlayer();
                    }
                }
            }
        }
        else {
            if (isPlaying) {
                if (mPlayer != null)
                    mPlayer.setVideoStart();
            }
        }
        mCurrentContentType = item.contentType;
    }

    protected void setPlayerSize(boolean isLow) {
        if (mPlayer != null)
            mPlayer.setPlayerSize(isLow);
    }

    /**
     * 비디오가 최초 실행 되어서 보이는 상태 인지 여부 확인.
     * @return
     */
    public boolean isVideoStarted() {
        if(mViewPlayerMain == null) {
            return false;
        }
        // 비디오가 최초 실행 되었음에도 보이지 않는다면 에러 발생으로 인지
        if (mViewVideoImage.isShown() && mStartVideoFirst) {
            return false;
        }
        return true;
    }
}
