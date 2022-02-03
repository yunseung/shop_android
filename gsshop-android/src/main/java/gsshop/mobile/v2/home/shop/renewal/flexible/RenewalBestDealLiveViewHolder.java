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
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.main.TvLiveContent;
import gsshop.mobile.v2.home.shop.BroadType;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.AbstractBaseActivity.getHomeGroupInfo;
import static gsshop.mobile.v2.home.shop.ViewHolderType.BANNER_TYPE_HOME_TV_DATA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.BANNER_TYPE_HOME_TV_LIVE;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 생방송, 데이타방송 뷰홀더 V2
 */
@SuppressLint("NewApi")
public class RenewalBestDealLiveViewHolder extends RenewalPriceInfoBottomViewHolder {

    /**
     * 방송종류 (live or data)
     */
    private BroadType broadType;

    /**
     * 루트뷰
     */
    private View lay_root;

    /**
     * 방송종료시간
     */
    private String mPrevBroadEndTime;

    /**
     * 남은시간 표시
     */
    private View lay_cv_remain_time;
    private RenewalBroadTimeLayout cv_remain_time;

    /**
     * 방송타입 (onair or best)
     */
    private ImageView img_brd_type;

    /**
     * 방송종류 (live or myshop)
     */
    private ImageView img_brd_kind;

    /**
     * 상품이미지
     */
    private ImageView img_main_sumnail;

    /**
     * 캐시할 이미지 주소
     */
    private String mImgForCache;

    private View top_live_talk;

    /**
     * 홈생방 or 편성표 A타입 영역 구분
     */
    //private String SCHEDULE_TYPE = "SCHEDULE";
    private String LIVE_TYPE = "LIVE";

    /**
     * 부상품
     */
    private LinearLayout product_toggle_layout;
    private LinearLayout product_toggle_count_layout;
    private LinearLayout sub_prd_list; //나머지 부상품
    private LinearLayout sub_prd_list_1; //첫번째 부상품
    private TextView product_toggle_text; //펼치기 영역
    private TextView txt_homesub; //함께 방송하는 상품 문구
    private TextView product_toggle_count;
    private ImageView product_toggle_icon;
    private View homesub_top_line; //함께 방송하는 상품 상단 회색라인
    private View homesub_bottom_line; //함께 방송하는 상품 하단 회색라인
    private String moreText;
    private String closeText;
    private int moreIcon;
    private int closeIcon;
    private boolean expandState = false; // false = 닫힌상태, true = 열린상태

    /**
     * 생성자
     *
     * @param itemView 레이아웃뷰
     */
    public RenewalBestDealLiveViewHolder(View itemView) {
        super(itemView);

        lay_root = itemView.findViewById(R.id.lay_root);

        img_brd_type = itemView.findViewById(R.id.img_brd_type);
        img_brd_kind = itemView.findViewById(R.id.img_brd_kind);

        // 남은시간
        lay_cv_remain_time = itemView.findViewById(R.id.lay_cv_remain_time);
        cv_remain_time = itemView.findViewById(R.id.cv_remain_time);

        //상품 이미지
        img_main_sumnail = itemView.findViewById(R.id.img_main_sumnail);

        top_live_talk = itemView.findViewById(R.id.top_live_talk);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), img_main_sumnail);

        EventBus.getDefault().register(this);

        //부상품
        product_toggle_layout = itemView.findViewById(R.id.product_toggle_layout);
        product_toggle_text = itemView.findViewById(R.id.product_toggle_text);
        product_toggle_icon = itemView.findViewById(R.id.product_toggle_icon);
        sub_prd_list = itemView.findViewById(R.id.sub_prd_list);
        sub_prd_list_1 = itemView.findViewById(R.id.sub_prd_list_1);
        product_toggle_count_layout = itemView.findViewById(R.id.product_toggle_count_layout);
        product_toggle_count = itemView.findViewById(R.id.product_toggle_count);
        txt_homesub = itemView.findViewById(R.id.txt_homesub);
        homesub_top_line = itemView.findViewById(R.id.homesub_top_line);
        homesub_bottom_line = itemView.findViewById(R.id.homesub_bottom_line);
        moreIcon = R.drawable.ic_expend_more;
        closeIcon = R.drawable.ic_expend_less;
    }

    @Override
    public void onBindViewHolder(Context context, int position, final ShopInfo shopInfo, String action, String label, String sectionName) {
        this.context = context;
        this.navigationId = shopInfo.naviId;

        //방송 갱신된 상태에서 스크롤하여 다시 onBind될때 갱신된 데이타 사용을 위함
        if (isEmpty(mInfo)) {
            this.mInfo = shopInfo;
        }

        if (BANNER_TYPE_HOME_TV_LIVE == mInfo.contents.get(position).type) {
            broadComponentType = BroadComponentType.live;
            broadType = BroadType.live;
            bindViewHolder(context, position, broadType, convertLivePriceData(mInfo.tvLiveBanner));
        } else if (BANNER_TYPE_HOME_TV_DATA == mInfo.contents.get(position).type) {
            broadComponentType = BroadComponentType.data;
            broadType = BroadType.data;
            bindViewHolder(context, position, broadType, convertLivePriceData(mInfo.dataLiveBanner));
        }
    }

    /**
     * 프론트영역 세팅
     *
     * @param context      컨텍스트
     * @param position     포지션
     * @param tvLiveBanner 방송정보(생방송 or 데이타방송)
     */
    public void bindViewHolder(final Context context, final int position, final BroadType bType, final TvLiveBanner tvLiveBanner) {
        //방송이 없는 경우, 혹은 보여줄 image URL 이 없는 경우 역시 보여주지 않도록 추가.
        if (tvLiveBanner == null
                || TextUtils.isEmpty(tvLiveBanner.broadType)
                || TextUtils.isEmpty(tvLiveBanner.linkUrl)
                || TextUtils.isEmpty(tvLiveBanner.imageUrl)) {

            lay_root.setVisibility(View.GONE);

            return;
        }

        if (bType == BroadType.live) {
            mInfo.tvLiveBanner = tvLiveBanner;
            img_brd_kind.setBackgroundResource(R.drawable.tit_live_android);
        } else {
            mInfo.dataLiveBanner = tvLiveBanner;
            img_brd_kind.setBackgroundResource(R.drawable.tit_myshop_android);
        }

        mImgForCache = tvLiveBanner.imageUrl;

        // 방송 스크린샷 이미지
        ImageUtil.loadImageTvLive(context.getApplicationContext(),
                !TextUtils.isEmpty(tvLiveBanner.imageUrl) ? tvLiveBanner.imageUrl.trim() : "", img_main_sumnail, R.drawable.noimg_tv);

        // tv방송 정보가 유효한지 체크
        if (verifyTvInfo(tvLiveBanner)) {
            mPrevBroadEndTime = tvLiveBanner.broadCloseTime;
        }

        lay_cv_remain_time.setVisibility(View.VISIBLE);

        if (tvLiveBanner.broadType != null
                && tvLiveBanner.broadType.equals(context.getResources().getString(R.string.home_tv_live_view_on_the_air))) {
            //생방송인 경우
            img_brd_type.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(tvLiveBanner.broadCloseTime)) {
                //남은시간 숨김
                lay_cv_remain_time.setVisibility(View.GONE);
            }
        } else if (tvLiveBanner.broadType != null
                && tvLiveBanner.broadType.equals(context.getResources().getString(R.string.home_tv_live_view_best))) {
            //베스트인 경우
            img_brd_type.setVisibility(View.GONE);
            //남은시간 숨김
            lay_cv_remain_time.setVisibility(View.GONE);
        }

        // 상세화면 이동
        lay_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Keys.INTENT.IMAGE_URL, isNotEmpty(mImgForCache) ? mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO) : "");
                intent.putExtra(Keys.INTENT.HAS_VOD, "Y");
                WebUtils.goWeb(context, tvLiveBanner.linkUrl, intent);
            }
        });

        // 재생버튼 클릭시 상세화면 이동 (자동재생 플래그 추가)
        if (isNotEmpty(tvLiveBanner.playUrl)) {
            lay_cv_remain_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String linkUrl = StringUtils.addUriParameter(Uri.parse(tvLiveBanner.playUrl),
                            Keys.PARAM.VOD_PLAY, "LIVE").toString();
                    Intent intent = new Intent();
                    intent.putExtra(Keys.INTENT.IMAGE_URL, isNotEmpty(mImgForCache) ? mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO) : "");
                    intent.putExtra(Keys.INTENT.HAS_VOD, "Y");
                    WebUtils.goWeb(context, linkUrl, intent);
                }
            });
        }

        // TV생방송 progress
        cv_remain_time.setOnTvLiveFinishedListener(new RenewalBroadTimeLayout.OnTvLiveFinishedListener() {
            @Override
            public void onTvLiveFinished() {
                doLiveFinished(bType);
            }
        });


         //상단으로 라이브톡 옮김 그래서 공통영역 라이브톡 로직 가져옴
        if (tvLiveBanner.rLiveTalkYn != null && "Y".equals(tvLiveBanner.rLiveTalkYn)){
            top_live_talk.setVisibility(View.VISIBLE);

            top_live_talk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, tvLiveBanner.rLiveTalkUrl, ((Activity) context).getIntent());
                    ((HomeActivity) context).setWiseLogHttpClient(ServerUrls.WEB.RN_BESTSHOP_LIVE_TALK);
                }
            });

        } else {
            top_live_talk.setVisibility(View.GONE);
        }

        //부상품영역 초기화
        product_toggle_layout.setVisibility(View.GONE);
        sub_prd_list.setVisibility(View.GONE);
        sub_prd_list_1.setVisibility(View.GONE);
        homesub_top_line.setVisibility(View.GONE);
        homesub_bottom_line.setVisibility(View.GONE);
        txt_homesub.setVisibility(View.GONE);


        /**
         * 홈 생방 부상품 세팅 AB test
         */
        if (bType == BroadType.live) {
            setSubProduct(mInfo.tvLiveBanner, position);
        }else if(bType == BroadType.data){
            setSubProduct(mInfo.dataLiveBanner, position);
        }

        //주상품 가격등 정보 업데이트
        super.bindViewHolder(tvLiveBanner, position, null);
    }

    /**
     * 라이브/데이터 타입에 따른 부상품 세팅
     * @param tvLiveBanner
     * @param position
     */
    private void setSubProduct(TvLiveBanner tvLiveBanner, int position){
        if (tvLiveBanner.currBroadSubPrdList != null && tvLiveBanner.currBroadSubPrdList.size() > 0) {
            moreText = context.getString(R.string.home_sub_more);
            closeText = context.getString(R.string.home_sub_close);
            product_toggle_layout.setVisibility(View.VISIBLE);
            homesub_top_line.setVisibility(View.VISIBLE);
            homesub_bottom_line.setVisibility(View.VISIBLE);
            txt_homesub.setVisibility(View.VISIBLE);
            product_toggle_text.setText(moreText);
            product_toggle_icon.setBackgroundResource(moreIcon);

            sub_prd_list.setVisibility(View.GONE);
            sub_prd_list_1.setVisibility(View.GONE);
            sub_prd_list.removeAllViews();
            sub_prd_list_1.removeAllViews();

            product_toggle_count_layout.setVisibility(View.VISIBLE);
            product_toggle_count.setText("" + Integer.toString(tvLiveBanner.currBroadSubPrdList.size()-1) + context.getString(R.string.product_description_sale_tail_text));
            product_toggle_count.setTextSize(16);

            //부상품 addView하고 다 숨기기
            for (int i=0; i<tvLiveBanner.currBroadSubPrdList.size(); i++) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.best_deal_sub_main_sub, null);
                BestDealSubViewHolder subPrd = new  BestDealSubViewHolder(itemView, context,LIVE_TYPE);
                View rsltView = subPrd.onBindViewHolder(position, tvLiveBanner.currBroadSubPrdList.get(i));
                if(i == 0){
                    //첫번째 상품은 별도로 삽입
                    sub_prd_list_1.addView(rsltView);
                }else{
                    //첫번째 이외의 상품들 삽입
                    sub_prd_list.addView(rsltView);
                }
            }

            //첫번째 부상품만 보이고 나머지는 숨기기
            sub_prd_list_1.setVisibility(View.VISIBLE);
            sub_prd_list.setVisibility(View.GONE);

            //부상품이 한개면 펼치기 영역 제거
            if(tvLiveBanner.currBroadSubPrdList.size() == 1){
                product_toggle_layout.setVisibility(View.GONE);
                homesub_bottom_line.setVisibility(View.GONE);
            }else{
                product_toggle_layout.setVisibility(View.VISIBLE);
                homesub_bottom_line.setVisibility(View.VISIBLE);
            }


            //더보기 클릭시
            product_toggle_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandState) {
                        setCollapseUI(); //닫기상태로 변경
                        sub_prd_list_1.requestFocus(); //닫았을때 맨 위에 포커스이동
                    } else {
                        setExpandUI(); //펼친상태로 변경
                    }
                    expandState = !expandState;
                }
            });
        }else{
            //부상품영역 안보이도록(오리지널 형태)
            product_toggle_layout.setVisibility(View.GONE);
            sub_prd_list.setVisibility(View.GONE);
            sub_prd_list_1.setVisibility(View.GONE);
            homesub_top_line.setVisibility(View.GONE);
            homesub_bottom_line.setVisibility(View.GONE);
            txt_homesub.setVisibility(View.GONE);
        }
    }

    /**
     * 부상품 펼칠때
     */
    private void setExpandUI() {
        product_toggle_text.setText(closeText);
        product_toggle_icon.setBackgroundResource(closeIcon);
        product_toggle_count_layout.setVisibility(View.GONE);
        sub_prd_list.setVisibility(View.VISIBLE);
    }

    /**
     * 부상품 닫을때
     */
    private void setCollapseUI() {
        product_toggle_text.setText(moreText);
        product_toggle_icon.setBackgroundResource(moreIcon);
        product_toggle_count_layout.setVisibility(View.VISIBLE);
        sub_prd_list.setVisibility(View.GONE);
    }

    /**
     * tv방송 정보가 유효한 지 체크해서  tv live view visible/gone
     * broadType과 prdUrl 정도만 체크함
     *
     * @param tvLiveBanner tvLiveBanner
     * @return boolean
     */
    private boolean verifyTvInfo(TvLiveBanner tvLiveBanner) {
        if (DisplayUtils.isValidString(tvLiveBanner.broadType)
                && DisplayUtils.isValidString(tvLiveBanner.linkUrl)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 방송종료 콜백 호출시 화면 UI를 변경한다.
     *
     * @param broadType 방송종류
     */
    private void doLiveFinished(BroadType broadType) {
        //방송정보가 변경되면 캐시를 초기화한다.
        MainApplication.clearCache();

        new GetTvLiveContentUpdateController(context).execute(broadType);
    }

    /**
     * 생방송 남은시간 노출용 타이머를 시작한다.
     */
    protected void startTvLiveTimer() {
        if (isEmpty(mInfo)) {
            return;
        }

        if (broadType == BroadType.live) {
            cv_remain_time.updateTvLiveTime(null, mInfo.tvLiveBanner.broadStartTime, mInfo.tvLiveBanner.broadCloseTime);
        } else {
            cv_remain_time.updateTvLiveTime(null, mInfo.dataLiveBanner.broadStartTime, mInfo.dataLiveBanner.broadCloseTime);
        }
    }

    /**
     * 생방송 남은시간 노출용 타이머를 정지한다.
     */
    protected void stopTvLiveTimer() {
        cv_remain_time.stopTimer();
    }

    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        EventBus.getDefault().unregister(this);
    }

    /**
     * 이미지가 화면상에 노출된 경우에 한해 이미지 캐시작업을 수행한다.
     *
     * @param event ImageCacheStartEvent
     */
    public void onEvent(Events.ImageCacheStartEvent event) {
        if (isNotEmpty(navigationId) && !navigationId.equals(event.naviId)) {
            //내가 속한 매장에 대한 이벤트가 아니면 스킵
            return;
        }
        if (DisplayUtils.isVisible(img_main_sumnail)) {
            if (isNotEmpty(mImgForCache) && mImgForCache.contains(IMG_CACHE_RPL_FROM)) {
                String imgUrl = mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO);
                Glide.with(context).load(trim(imgUrl)).downloadOnly(IMG_CACHE_WIDTH, IMG_CACHE_HEIGHT);
            }
        }
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
     * 생방송 정보 업데이트
     */
    private class GetTvLiveContentUpdateController extends BaseAsyncController<TvLiveContent> {
        private BroadType broadType;

        @Inject
        private RestClient restClient;

        protected GetTvLiveContentUpdateController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            this.broadType = (BroadType) params[0];
        }

        @Override
        protected TvLiveContent process() throws Exception {
            String url;
            if (broadType == BroadType.live) {
                url = getHomeGroupInfo().appUseUrl.homeLiveBrodUrl;
            } else {
                url = getHomeGroupInfo().appUseUrl.homeDataBrodUrl;
            }
            return (TvLiveContent) DataUtil.getData(context, restClient, TvLiveContent.class, false, true, url);
        }

        @Override
        protected void onSuccess(TvLiveContent tvLiveContent) throws Exception {
            if (tvLiveContent == null || !verifyTvInfo(tvLiveContent.tvLiveBanner)) {
                return;
            }

            if (mPrevBroadEndTime != null) {
                // 이전 데이타가 있고, 받아온 데이타와 다를 때에만 view update.
                if (!mPrevBroadEndTime.equals(tvLiveContent.tvLiveBanner.broadCloseTime)) {
                    // 방송 종료시간이 같으면 동일한 데이타로 간주
                    mPrevBroadEndTime = tvLiveContent.tvLiveBanner.broadCloseTime;

                    //context가 유효한 경우만 화면 갱신 (메인 상단아이콘 다수 클릭 후 생방송 종료시 context finishing 발생)
                    if (!((Activity) context).isFinishing()) {
                        bindViewHolder(context, -1, broadType, convertLivePriceData(tvLiveContent.tvLiveBanner));
                        startTvLiveTimer();
                    }
                }
            }
        }

        @Override
        protected void onError(Throwable e) {
            //에러발생하면 팝업창 띄우지 않고 무시
            Ln.e(e);
        }
    }
}
