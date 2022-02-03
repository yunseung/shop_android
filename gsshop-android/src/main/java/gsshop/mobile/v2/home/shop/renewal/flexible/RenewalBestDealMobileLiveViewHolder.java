/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.MobileLiveBanner;
import gsshop.mobile.v2.home.main.TvLiveContent;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.HomeGroupInfoAction.getHomeGroupInfo;
import static gsshop.mobile.v2.util.DateUtils.BROAD_TIME_FORMAT;

/**
 * 모바일라이브 뷰홀더 V2
 *
 * 1.뷰홀더 노출기준
 * 1) 방송시작시간 <= 현재시간 <= 방송종료시간
 *    ->노출
 * 2) 현재시간 < 방송시작시간
 *    -> 방송안내뷰 노출 (mobileLiveDefaultBanner)
 *    -> 방송시작시간에 API호출 후 갱신된 데이타로 노출
 * 3) 현재시간 > 방송종료시간
 *    -> 방송안내뷰 노출 (mobileLiveDefaultBanner)
 *
 * 2.방송종료 처리
 * 1) 방송시청중 또는 백그라운드 상태에서 방송이 종료되면 뷰홀더 노출 유지
 * 2) API 호출 후 다음방송 데이터 취득
 * 3) 위 1번 항목 프로세스 진행
 */
@SuppressLint("NewApi")
public class RenewalBestDealMobileLiveViewHolder extends RenewalPriceInfoBottomViewHolder {

    /**
     * 생방송/재방송 구분
     */
    private enum MLBroadType {
        live(MainApplication.getAppContext().getResources().getString(R.string.home_tv_live_view_on_the_air)),
        replay(MainApplication.getAppContext().getResources().getString(R.string.home_tv_live_view_replay_air)),
        schbrd("SCHBRD");

        private String label = "";

        MLBroadType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * 방송시작시간에 대한 현재시간의 위치
     */
    private enum MLBroadState {
        BEFORE_BROAD,	//현재시간이 방송 이전
        AFTER_BROAD,	//현재시간이 방송 이후
        ON_BROAD		//방송중
    }
    private static MLBroadState broadState;

    /**
     * 루트뷰
     */
    private View lay_root;

    /**
     * 생방송 뷰
     */
    private View lay_live;

    /**
     * 상품이미지
     */
    private ImageView img_main_sumnail;

    /**
     * 남은시간 표시
     */
    private View lay_cv_remain_time;
    private RenewalBroadTimeLayout cv_remain_time;

    /**
     * 방송예정 표시
     */
    private View lay_schedule;
    private TextView txt_schedule;
    private ImageView iv_people;

    /**
     * 방송타입 (onair or best)
     */
    private ImageView img_brd_type;

    /**
     * 방송안내 뷰
     */
    private View lay_guide;

    /**
     * 방송안내 뷰 > 상품이미지
     */
    private ImageView img_default_main_sumnail;

    /**
     * 방송안내 뷰 > 방송시간
     */
    private TextView txt_default_broad_time;

    /**
     * 방송안내 뷰 > 상품설명
     */
    private TextView txt_default_title;

    /**
     * 방송안내 뷰 > 자세히 보러가기
     */
    private TextView txt_default_show_detail;

    /**
     * 방송시작시간에 API 호출을 위한 task
     */
    private TimerTask updateTask = null;

    /**
     * 타이머에 의한 방송종료 처리가 수행되었는지 여부.
     * 매장 스크롤시 onViewAttachedToWindow 실행되면서 타이머를 시작하고 이때 OnTvLiveFinished 콜백이 수행되기때문에
     * 이 부분에 대한 반복호출을 방지하기 위함.
     */
    private boolean isCalledOnTvLiveFinished = false;

    /**
     * 생성자
     *
     * @param itemView 레이아웃뷰
     */
    public RenewalBestDealMobileLiveViewHolder(View itemView) {
        super(itemView);

        lay_root = itemView.findViewById(R.id.lay_root);
        lay_live = itemView.findViewById(R.id.lay_live);

        img_brd_type = itemView.findViewById(R.id.img_brd_type);

        // 남은시간
        lay_cv_remain_time = itemView.findViewById(R.id.lay_cv_remain_time);
        cv_remain_time = itemView.findViewById(R.id.cv_remain_time);

        // 방송예정
        lay_schedule = itemView.findViewById(R.id.lay_schedule);
        txt_schedule = itemView.findViewById(R.id.txt_schedule);
        iv_people = itemView.findViewById(R.id.iv_people);

        //상품 이미지
        img_main_sumnail = itemView.findViewById(R.id.img_main_sumnail);
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), img_main_sumnail);

        //방송안내 관련 뷰
        lay_guide = itemView.findViewById(R.id.lay_guide);
        img_default_main_sumnail = itemView.findViewById(R.id.img_default_main_sumnail);
        txt_default_broad_time = itemView.findViewById(R.id.txt_default_broad_time);
        txt_default_title = itemView.findViewById(R.id.txt_default_title);
        txt_default_show_detail = itemView.findViewById(R.id.txt_default_show_detail);
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), img_default_main_sumnail);

        broadComponentType = BroadComponentType.mobilelive;

        EventBus.getDefault().register(this);
    }

    @Override
    public void onBindViewHolder(Context context, int position, final ShopInfo shopInfo, String action, String label, String sectionName) {
        this.context = context;

        //방송 갱신된 상태에서 스크롤하여 다시 onBind될때 갱신된 데이타 사용을 위함
        if (isEmpty(mInfo)) {
            this.mInfo = shopInfo;
        }

        //방송데이타가 없는 경우 또는 상품이미지가 없는 경우 본 뷰홀더 노출 안함
        if (isEmpty(mInfo.mobileLiveBanner) && isEmpty(mInfo.mobileLiveDefaultBanner)) {
            hideViewHolder();
            return;
        }

        classifyByBroadTime(mInfo, position);
    }

    /**
     * 방송시간에 따라 수행할 작업을 분류한다.
     *
     * @param shopInfo MobileLiveBanner
     * @param position 뷰홀더 위치
     */
    private void classifyByBroadTime(ShopInfo shopInfo, int position) {
        long broadStartTime = 0L;   //방송시작시간
        long broadCloseTime = 0L;  //방송종료시간
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(BROAD_TIME_FORMAT, Locale.getDefault());

        MobileLiveBanner mobileLiveBanner = shopInfo.mobileLiveBanner;
        MobileLiveBanner mobileLiveDefaultBanner = shopInfo.mobileLiveDefaultBanner;

        try {
            broadStartTime = sdf.parse(mobileLiveBanner.broadStartTime).getTime();
            broadCloseTime = sdf.parse(mobileLiveBanner.broadCloseTime).getTime();
        } catch (ParseException e) {
            if (isEmpty(mobileLiveDefaultBanner)) {
                //뷰홀더 비노출
                hideViewHolder();
            } else {
                //방송시간이 유효하지 않고 디폴트배너 정보 있으면
                broadState = MLBroadState.AFTER_BROAD;
                showDefaultViewHolder();
                drawDefaultViewHolder(mobileLiveDefaultBanner, position);
            }
            return;
        }

        if (currentTime >= broadStartTime && currentTime <= broadCloseTime) {
            //현재 시간이 방송중
            broadState = MLBroadState.ON_BROAD;
            //뷰홀더 노출
            showLiveViewHolder();
            drawLiveViewHolder(mobileLiveBanner, position);
            isCalledOnTvLiveFinished = false;
        } else if (currentTime < broadStartTime) {
            //현재 시간이 방송 이전
            broadState = MLBroadState.BEFORE_BROAD;
            //디폴트 뷰홀더 노출
            showDefaultViewHolder();
            drawDefaultViewHolder(mobileLiveDefaultBanner, position);
            //방송시작시간에 API 호출 후 뷰홀더 노출
            broadStartTrace(broadStartTime - currentTime);
            isCalledOnTvLiveFinished = false;
        } else {
            //현재 시간이 방송 이후
            broadState = MLBroadState.AFTER_BROAD;
            //디퐅트 뷰홀더 노출
            showDefaultViewHolder();
            drawDefaultViewHolder(mobileLiveDefaultBanner, position);
        }
    }

    /**
     * 방송시작시간에 API 호출을 위한 task를 세팅한다.
     *
     * @param delay 방송시작시간까지 남은시간
     */
    private void broadStartTrace(long delay) {
        //초기화
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        //세팅
        updateTask = new TimerTask() {
            @Override
            public void run() {
                updateViewHolderData();
            }
        };

        new Timer().schedule(updateTask, delay + 2000);
    }

    /**
     * 방송정보를 갱신한다.
     */
    private void updateViewHolderData() {
        ((Activity)context).runOnUiThread(() -> {
            //방송정보가 변경되면 캐시를 초기화한다.
            MainApplication.clearCache();
            new GetTvLiveContentUpdateController(context).execute();
        });
    }

    /**
     * 라이브방송 뷰홀더를 노출한다.
     */
    private void showLiveViewHolder() {
        ViewUtils.showViews(lay_root, img_brd_type, lay_live, mViewProduct);
        ViewUtils.hideViews(lay_guide);
    }

    /**
     * 방송안내 뷰홀더를 노출한다.
     */
    private void showDefaultViewHolder() {
        ViewUtils.showViews(lay_root, lay_guide);
        ViewUtils.hideViews(img_brd_type, lay_live, mViewProduct);
    }

    /**
     * 뷰홀더를 숨긴다.
     */
    private void hideViewHolder() {
        ViewUtils.hideViews(lay_root);
    }

    /**
     * 라이브 뷰홀더를 노출한다.
     *
     * @param mobileLiveBanner MobileLiveBanner
     * @param position 위치
     */
    private void drawLiveViewHolder(MobileLiveBanner mobileLiveBanner, int position) {
        //상품이미지 없는 경우는 뷰홀더 비노출
        if (isEmpty(mobileLiveBanner.imageUrl)) {
            hideViewHolder();
            return;
        }

        //디폴트 셋 라이브
        ViewUtils.showViews(lay_cv_remain_time, img_brd_type);
        ViewUtils.hideViews(lay_schedule, iv_people);
        if (MLBroadType.replay.getLabel().equalsIgnoreCase(mobileLiveBanner.broadType)) {
            //재방송인 경우
            ViewUtils.hideViews(img_brd_type);
        } else if (MLBroadType.schbrd.toString().equalsIgnoreCase(mobileLiveBanner.broadType)) {
            //방송예정
            ViewUtils.hideViews(lay_cv_remain_time, img_brd_type);
            if (isNotEmpty(mobileLiveBanner.broadInfoText)) {
                ViewUtils.showViews(lay_schedule);
                txt_schedule.setText(mobileLiveBanner.broadInfoText);
            } else {
                ViewUtils.hideViews(lay_schedule);
            }
        } else if (MLBroadType.live.getLabel().equalsIgnoreCase(mobileLiveBanner.broadType)) {
            if (isNotEmpty(mobileLiveBanner.streamViewCount)) {
                ViewUtils.showViews(lay_schedule, iv_people);
                txt_schedule.setText(mobileLiveBanner.streamViewCount);
            }
        }

        // 방송종료시 호출되는 콜백
        cv_remain_time.setOnTvLiveFinishedListener(() -> {
            //방송종료시 API 호출하여 다음방송 정보를 받아옴
            if (!isCalledOnTvLiveFinished) {
                isCalledOnTvLiveFinished = true;
                broadStartTrace(0);
            }
        });

        // 방송 스크린샷 이미지
        ImageUtil.loadImageTvLive(context.getApplicationContext(),
                !TextUtils.isEmpty(mobileLiveBanner.imageUrl) ? mobileLiveBanner.imageUrl.trim() : "", img_main_sumnail, R.drawable.noimage_375_188);

        // 상세화면 이동
        lay_root.setOnClickListener(v -> WebUtils.goWeb(context, mobileLiveBanner.linkUrl));

        // 모바일라이브 바로가기
        if (isNotEmpty(mobileLiveBanner.playUrl)) {
            img_main_sumnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, mobileLiveBanner.playUrl);
                }
            });
        }

        super.bindViewHolder(convertLivePriceData(mobileLiveBanner), position, null);
    }

    /**
     * 디폴트 뷰홀더를 노출한다.
     *
     * @param mobileLiveBanner MobileLiveBanner
     * @param position 위치
     */
    private void drawDefaultViewHolder(MobileLiveBanner mobileLiveBanner, int position) {
        //상품이미지 없는 경우는 뷰홀더 비노출
        if (isEmpty(mobileLiveBanner) || isEmpty(mobileLiveBanner.imageUrl)) {
            hideViewHolder();
            return;
        }

        // 방송 스크린샷 이미지
        // x:center, y:top
        ImageUtil.loadImagePositionedCrop(context.getApplicationContext(),
                !TextUtils.isEmpty(mobileLiveBanner.imageUrl) ? mobileLiveBanner.imageUrl.trim() : "",
                img_default_main_sumnail, R.drawable.noimage_375_188, 0.5f, 0);

        ViewUtils.hideViews(txt_default_broad_time, txt_default_title, txt_default_show_detail);
        // 방송시간
        if (isNotEmpty(mobileLiveBanner.broadStartTime)) {
            ViewUtils.showViews(txt_default_broad_time);
            txt_default_broad_time.setText(mobileLiveBanner.broadStartTime);
        }
        // 상품설명 2줄
        if (isNotEmpty(mobileLiveBanner.productName)) {
            ViewUtils.showViews(txt_default_title);
            txt_default_title.setText(mobileLiveBanner.productName);
        }
        // 자세히 보러가기
        if (isNotEmpty(mobileLiveBanner.btnInfo3)
                && isNotEmpty(mobileLiveBanner.btnInfo3.linkUrl)
                && isNotEmpty(mobileLiveBanner.btnInfo3.text)) {
            ViewUtils.showViews(txt_default_show_detail);
            txt_default_show_detail.setText(mobileLiveBanner.btnInfo3.text);
            txt_default_show_detail.setOnClickListener(v -> {
                WebUtils.goWeb(context, mobileLiveBanner.btnInfo3.linkUrl);
            });
        }
        // 상세화면 이동
        lay_root.setOnClickListener(v -> WebUtils.goWeb(context, mobileLiveBanner.linkUrl));

        super.bindViewHolder(convertLivePriceData(mobileLiveBanner), position, null);
    }

    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        //Ln.i("registerSticky - unregister");
        EventBus.getDefault().unregister(this);
    }

    /**
     * 뷰가 화면에 노출될때 발생
     */
    @Override
    public void onViewAttachedToWindow() {
        //생방송 남은시간 표시용 타이머를 시작한다.
        startTvLiveTimer();
    }

    /**
     * 뷰가 화면에서 사라질때 발생
     */
    @Override
    public void onViewDetachedFromWindow() {
        //생방송 남은시간 표시용 타이머를 정지한다.
        stopTvLiveTimer();
    }

    /**
     * 생방송 남은시간 노출용 타이머를 시작한다.
     */
    protected void startTvLiveTimer() {
        cv_remain_time.isLiveTalk = true;
        if (mInfo != null && mInfo.mobileLiveBanner != null) {
            cv_remain_time.updateTvLiveTime(null, mInfo.mobileLiveBanner.broadStartTime, mInfo.mobileLiveBanner.broadCloseTime);
        }
    }

    /**
     * 생방송 남은시간 노출용 타이머를 정지한다.
     */
    protected void stopTvLiveTimer() {
        cv_remain_time.stopTimer();
    }

    /**
     * 생방송 남은시간 노출용 타이머를 시작/정지 시킨다.
     * (BestDealShopFragment, TvShopFragment onStart/onStop에서 호출됨)
     *
     * @param event TvLiveTimerEvent
     */
    public void onEvent(Events.FlexibleEvent.TvLiveTimerEvent event) {
        if (event.start) {
            //생방송 남은시간 표시용 타이머 시작
            startTvLiveTimer();
        } else {
            //생방송 남은시간 표시용 타이머 정지
            stopTvLiveTimer();
        }
    }

    /**
     * 방송정보 업데이트
     */
    private class GetTvLiveContentUpdateController extends BaseAsyncController<TvLiveContent> {
        @Inject
        private RestClient restClient;

        protected GetTvLiveContentUpdateController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
        }

        @Override
        protected TvLiveContent process() throws Exception {
            String url = getHomeGroupInfo().appUseUrl.homeMobileliveUrl;
            return (TvLiveContent) DataUtil.getData(context, restClient, TvLiveContent.class, false, true, url);
        }

        @Override
        protected void onSuccess(TvLiveContent tvLiveContent) throws Exception {
            if (isEmpty(tvLiveContent) || isEmpty(tvLiveContent.mobileLiveBanner)) {
                hideViewHolder();
                return;
            }

            //방송정보 데이타 갱신
            mInfo.mobileLiveBanner = tvLiveContent.mobileLiveBanner;
            mInfo.mobileLiveDefaultBanner = tvLiveContent.mobileLiveDefaultBanner;

            //context가 유효한 경우만 화면 갱신 (메인 상단아이콘 다수 클릭 후 생방송 종료시 context finishing 발생)
            if (!((Activity) context).isFinishing()) {
                onBindViewHolder(context, getAdapterPosition(), new ShopInfo(), "", "", "");
                startTvLiveTimer();
            }
        }

        @Override
        protected void onError(Throwable e) {
            //에러발생하면 팝업창 띄우지 않고 무시
            Ln.e(e);
        }
    }
}
