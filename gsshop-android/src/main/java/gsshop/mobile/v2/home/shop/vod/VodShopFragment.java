/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.vod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.EmptyUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.inject.Inject;
import com.gsshop.mocha.device.NetworkStatus;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.nineoldandroids.view.ViewHelper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleBroadAlarmDialogFragment;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment;
import gsshop.mobile.v2.home.shop.schedule.model.BroadAlarmResult;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.CustomToast;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.Events.VodShopPlayerEvent.VodPlayerAction.VOD_MUTE;
import static gsshop.mobile.v2.home.shop.ViewHolderType.BANNER_TYPE_VOD_ORD;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_ORD_GBA_SPACE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleBroadAlarmDialogFragment.makeBroadAlarmConfirmDialog;
import static gsshop.mobile.v2.menu.BaseTabMenuActivity.TV_SCHEDULE_NAVI_ID;
import static gsshop.mobile.v2.web.WebUtils.BROADTYPE_PARAM_KEY;

/**
 * vod 매장.
 */
public class VodShopFragment extends FlexibleShopFragment {

    /**
     * 정렬타입
     */
    public enum ORDER_TYPE {
        RECOMMEND, POPULAR
    }

    /**
     * 상단 해더 (앱바)
     */
    private AppBarLayout appbar_ord;
    private LinearLayout lay_ord;

    /**
     * 타이틀 이미지
     */
    private ImageView img_title;

    /**
     * 추천순 정렬
     */
    private TextView txt_recommend;

    /**
     * 인기순 정렬
     */
    private TextView txt_popular;

    /**
     * 자동재생 여부 (API로부터 전달받은 값)
     */
    private String isAutoPlayFromAPI;

    /**
     * 최근 재생한 동영상 위치
     * (두개의 동영상이 동시에 노출된 경우 하나의 동영상이 가려지기 전까지는 기존 재생영상 그대로 재생하기 위한 변수)
     */
    public static int prevPlayingPosition = -1;

    /**
     * 카드 혜택 팝업 노출
     */
    private View m_vPopupCardNotice;

    /**
     * 카드 팝업이 정상적으로 설정되었는지 여부 확인
     */
    private boolean m_bIsCardNoticeEnable = false;

    /**
     * 카드 팝업이 한번 노출되었는지 여부 확인
     */
    private static boolean m_bIsCardNoticed = false;

    // 토스트 팝업 출력 시간
    public static final int TIMER_POPUP_CARD_MILLISEC = 5000;            // 10초

    public static VodShopFragment newInstance(int position) {
        VodShopFragment fragment = new VodShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new VodShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        appbar_ord = view.findViewById(R.id.appbar_ord);
        lay_ord = view.findViewById(R.id.lay_ord);
        img_title = view.findViewById(R.id.img_title);
        txt_recommend = view.findViewById(R.id.txt_recommend);
        txt_popular = view.findViewById(R.id.txt_popular);

        m_vPopupCardNotice = view.findViewById(R.id.popup_card_notice);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isNotEmpty(getView())) {
            if (!isVisibleToUser) {
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_PAUSE));
            } else {
                // 눈에 보이는 순간 첫번째 영상 재생
                initPrevPlayingPosition();
                traceAutoPlayTarget(false);
                initGlobalMute();
            }

            if (!m_bIsCardNoticed && m_bIsCardNoticeEnable) {
                m_bIsCardNoticed = true;

                showCardNoticePopup();
            }
        }
    }

    private void initGlobalMute() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        Ln.d("NetworkStatus.isWifiConnected(getContext()) : " + NetworkStatus.isWifiConnected(getContext()));
        if (!MainApplication.isVodShopLoaded) {
            if ("Y".equalsIgnoreCase(isAutoPlayFromAPI) && NetworkStatus.isWifiConnected(getContext())) {
                MainApplication.isMute = true;
            } else {
                MainApplication.isMute = false;
            }
            MainApplication.isVodShopLoaded = true;
        }

        EventBus.getDefault().post(new Events.VodShopPlayerEvent(VOD_MUTE, MainApplication.isMute));
    }

    /**
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    @Override
    public void drawFragment() {

        // setSwipeRefresh();
        // TV생방송 존재 여부에 상관없이 전체 data loading
        new VodShopController(getActivity(), tempSection, true).execute();
    }


    /**
     * 퀵 리턴 메뉴 설정
     */
    @Override
    protected void setQuickReturn() {
        super.setQuickReturn();

        // 동영상을 보여주게 수정.
        btnTop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mRecyclerView.stopScroll();
                mLayoutManager.scrollToPosition(0);
                btnTop.setVisibility(View.GONE);

                //탑버튼 클릭시 해더영역 노출
                ((HomeActivity) getContext()).expandHeader();

                ViewHelper.setTranslationY(btnTop, 0);

                ViewHelper.setTranslationY(mCoordinator.getFooterLayout(), 0);
                //CSP BUTTON MSLEE
                ViewHelper.setTranslationY(mCoordinator.getCspLayout(), 0);

                appbar_ord.setExpanded(true, true);

                firstLoadAction();
            }
        });

        scrollListener.registerExtraOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    Ln.i("Top of list");
                    //한화면 두개 동영상 노출시 재생정책때문에 TOP에서 다시 자동재생 프로세스를 실행함
                    initPrevPlayingPosition();
                    traceAutoPlayTarget(false);
                } else if (!recyclerView.canScrollVertically(1)) {
                    Ln.i("End of list");
                } else {
                    Ln.i("idle");
                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 스크롤 인지하면 바로 카드 NoticePopup 해제
                dismissCardNoticePopup();
                //버퍼링중 상하 스크롤시 레이아웃 겹침/벌어짐 현상 개선
                //스크롤시 child view의 위치를 다시 잡아줌
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    mRecyclerView.requestLayout();
                }

                Ln.i("scroll dx: " + dx + ", dy: " + dy);
                int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPositions(null)[0];
                int lastVisiblePosition = mLayoutManager.findLastVisibleItemPositions(null)[0];
                Ln.i("scroll first: " + firstVisiblePosition + ", last: " + lastVisiblePosition);

                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_SCROLLING, firstVisiblePosition, lastVisiblePosition));

                traceAutoPlayTarget(dy > 0);
            }

        });
    }

    /**
     * 자동재생할 뷰홀더를 검색하여 재생한다.
     *
     * @param isUp if true, scroll up
     */
    private void traceAutoPlayTarget(boolean isUp) {
        //API로 부터 받은 값이 "Y"가 아닌 경우 자동재생 안함
        if (!"Y".equalsIgnoreCase(isAutoPlayFromAPI)) {
            return;
        }

        int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPositions(null)[0];
        int lastVisiblePosition = mLayoutManager.findLastVisibleItemPositions(null)[0];

        if (!isUp) {
            //아래로 스크롤할 경우 위에서부터 대상 검색 (동시노출시 위영상 재생)
            for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                traceAutoPlayTargetProc(i);
            }
        } else {
            //위로 스크롤할 경우 아래서부터 대상 검색 (동시노출시 아래영상 재생)
            for (int i = lastVisiblePosition; i >= firstVisiblePosition; i--) {
                traceAutoPlayTargetProc(i);
            }
        }
    }

    /**
     * 자동재생할 뷰홀더를 검색하여 재생한다. (실행로직)
     *
     * @param i 뷰홀더 포지션
     */
    private void traceAutoPlayTargetProc(int i) {
        View v = mLayoutManager.findViewByPosition(i);
        BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(v);
        if (vh.getItemViewType() == ViewHolderType.VIEW_TYPE_BAN_VOD_GBA
                || vh.getItemViewType() == ViewHolderType.VIEW_TYPE_BAN_VOD_GBB
                || vh.getItemViewType() == ViewHolderType.VIEW_TYPE_BAN_VOD_GBC) {

            //리사이클러뷰 영역 측정
            Rect rvRect = new Rect();
            mRecyclerView.getGlobalVisibleRect(rvRect);

            //비디오프레임 영역 측정
            Rect vfRect = new Rect();

            //lay_video_frame이 AB테스트 xml에만 존재한다. AB테스트 xml정리하면서 에러생겨서 일단 주석. 대체할 변수 vod_video_frame으로 씀
            //v.findViewById(R.id.lay_vod_video_frame).getGlobalVisibleRect(vfRect);
            v.findViewById(R.id.vod_video_frame).getGlobalVisibleRect(vfRect);

            int visiblePercent;
            //비디오프레임 높이

            //lay_video_frame이 AB테스트 xml에만 존재한다. AB테스트 xml정리하면서 에러생겨서 일단 주석. 대체할 변수 vod_video_frame으로 씀
            //int vfHeight = v.findViewById(R.id.lay_vod_video_frame).getHeight();
            int vfHeight = v.findViewById(R.id.vod_video_frame).getHeight();

            if (vfRect.bottom >= rvRect.bottom) {
                //case1 비디오프레임 하단이 리사이클러뷰 하단 아래에 있는 경우
                int visibleHeight = rvRect.bottom - vfRect.top;
                visiblePercent = (visibleHeight * 100) / vfHeight;
            } else {
                //case2 그외
                int visibleHeight = vfRect.bottom - rvRect.top;
                visiblePercent = (visibleHeight * 100) / vfHeight;
            }

            //case2 경우 100 이상의 값은 모두 노출된 경우이므로 100으로 세팅
            if (visiblePercent > 100) {
                visiblePercent = 100;
            }
            //case1, case2 에서 측정된 음수값은 미노출 상태이므로 0으로 세팅
            if (visiblePercent < 0) {
                visiblePercent = 0;
            }

            //뷰홀더에 노출 퍼센트값 세팅
            ((VodBannerVodViewHolder)vh).setVisiblePercent(visiblePercent);
            if (visiblePercent == 100) {
                int prevVisiblePercent = 0;
                //최근재생한 비디오 영역의 노출퍼센트 구하기
                View prevView = mLayoutManager.findViewByPosition(prevPlayingPosition);
                if (isNotEmpty(prevView)) {
                    VodBannerVodViewHolder prevViewHolder = (VodBannerVodViewHolder) mRecyclerView.getChildViewHolder(prevView);
                    prevVisiblePercent = prevViewHolder.getVisiblePercent();
                }

                //한화면에 두개 동영상이 동시에 있을 경우 현재 재생중인 동영상을 계속 재생시킴
                //재생중인 동영상의 노출퍼센트가 100미만으로 변경될 경우만 다음 동영상 재생
                if (prevVisiblePercent < 100) {
                    postAutoPlay(i);
                }
            }
        }
    }

    private static TimerTask task = null;

    private synchronized void postAutoPlay(int autoPosition) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        if (!NetworkStatus.isWifiConnected(getActivity())) {
            return;
        }

        //todo mslee
        //같은 태스크가 이미 돌고 있으면
        //같은 타이머가 이미 돌고 있으면 예외처리 해보자
        if (task != null) {
            task.cancel();
            task = null;
        }
        task = new TimerTask() {
            @Override
            public void run() {
                Ln.i("scroll auto position: " + autoPosition);
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_AUTO_PLAY, autoPosition));
            }
        };

        new Timer().schedule(task, 50);
    }


    // navigation api와 recycler adapter view type 매핑.
    @Override
    protected int getFlexibleViewType(String viewType) {
        int type = super.getFlexibleViewType(viewType);
        if (type == ViewHolderType.BANNER_TYPE_NONE) {
            if ("BRD_VOD".equals(viewType)) {
                // renewal vod
                type = ViewHolderType.VIEW_TYPE_BRD_VOD;
            }
            if ("BAN_VOD_GBA".equals(viewType)) {
                // 가로 vod
                type = ViewHolderType.VIEW_TYPE_BAN_VOD_GBA;
            }
            if ("BAN_VOD_GBB".equals(viewType)) {
                // 세로 vod
                type = ViewHolderType.VIEW_TYPE_BAN_VOD_GBB;
            }
            if ("BAN_VOD_GBC".equals(viewType)) {
                // 정사각 vod
                type = ViewHolderType.VIEW_TYPE_BAN_VOD_GBC;
            }


            if ("BAN_ORD_GBA".equals(viewType)) {
                // 해더 영역 (추천순, 인기순 정렬)
                type = BANNER_TYPE_VOD_ORD;
            }

            if ("BAN_ORD_GBA_SPACE".equals(viewType)) {
                // 해더 영역 밑 여백
                type = VIEW_TYPE_BAN_ORD_GBA_SPACE;
            }

            /**
             * 카드 혜택 뷰 (변경될 수 있음)
             */
            if("MAP_CX_TXT_TOOLTIP".equals(viewType)) {
                type = ViewHolderType.BANNER_TYPE_VOD_CARD_POPUP;
            }

            if ("MAP_CX_TXT_GBB".equals(viewType)) {
                // 19.08.29 yun. 내일 TV 카드 혜택 뷰타입 추가.
                type = ViewHolderType.VIEW_TYPE_MAP_CX_TXT_GBB;
            }

        }

        return type;
    }

    private void initiateAppBar(SectionContentList item) {

        lay_ord.setVisibility(View.GONE);

        if (item == null) {
            return;
        }

        if (item.subProductList != null && item.subProductList.size() >= 2
                && item.subProductList.get(0) != null && item.subProductList.get(1) != null) {

            lay_ord.setVisibility(View.VISIBLE);

            ImageUtil.loadImage(getContext(), item.imageUrl, img_title, 0);

            SectionContentList recommendItem = item.subProductList.get(0);
            SectionContentList popularItem = item.subProductList.get(1);

            txt_recommend.setText(recommendItem.productName);
            txt_recommend.setContentDescription(recommendItem.productName);
            txt_popular.setText(popularItem.productName);
            txt_popular.setContentDescription(popularItem.productName);

            txt_recommend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempSection.sectionLinkUrl = item.subProductList.get(0).linkUrl;
                    tempSection.sectionLinkParams = "";
                    drawFragment();
                    toggleOrderTextColor(ORDER_TYPE.RECOMMEND);
                    ((HomeActivity) getContext()).setWiseLog(item.subProductList.get(0).wiseLog);
                }
            });

            txt_popular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempSection.sectionLinkUrl = item.subProductList.get(1).linkUrl;
                    tempSection.sectionLinkParams = "";
                    drawFragment();
                    toggleOrderTextColor(ORDER_TYPE.POPULAR);
                    ((HomeActivity) getContext()).setWiseLog(item.subProductList.get(1).wiseLog);
                }
            });
        }
    }

    /**
     * 텍스트색을 토글한다.
     */
    private void toggleOrderTextColor(ORDER_TYPE orderType) {
        String enabledColor = "#111111";
        String disabledColor = "#888888";

        if (orderType == ORDER_TYPE.POPULAR) {
            txt_popular.setTextColor(Color.parseColor(enabledColor));
            txt_recommend.setTextColor(Color.parseColor(disabledColor));
        } else {
            txt_recommend.setTextColor(Color.parseColor(enabledColor));
            txt_popular.setTextColor(Color.parseColor(disabledColor));
        }
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo,
                           int tab, int listPosition) {

        if (contentsListInfo != null || getActivity() != null) {
            isAutoPlayFromAPI = contentsListInfo.brigCoveAutoPlayYn;
            if (contentsListInfo.productList != null) {
                SectionContentList result = null;
                for (SectionContentList c : contentsListInfo.productList) {
                    int type = getFlexibleViewType(c);
                    if (type == ViewHolderType.BANNER_TYPE_VOD_ORD) {
                        result = c;
                        SectionContentList space = new SectionContentList();
                        space.viewType = "BAN_ORD_GBA_SPACE";
                        for (SectionContentList s : contentsListInfo.productList) {
                            //추천순, 인기순 반복 클릭할 경우 해당 뷰타입 중복 삽입되는 현상 개선
                            if (space.viewType.equals(s.viewType)) {
                                contentsListInfo.productList.remove(s);
                                break;
                            }
                        }
                        contentsListInfo.productList.add(0, space);
                        break;
                    }
                }
                // BANNER_TYPE_VOD_CARD_POPUP 경우일 때에 팝업 그리기 실행.
                for (SectionContentList c : contentsListInfo.productList) {
                    int type = getFlexibleViewType(c);
                    if (type == ViewHolderType.BANNER_TYPE_VOD_CARD_POPUP) {
                        setCardNoticePopup(c);
                        break;
                    }
                }

                initiateAppBar(result);
            }
        }

        super.updateList(sectionList, contentsListInfo, tab, listPosition);

        //인기순, 추천순 클릭시 자동재생
        firstLoadAction();
    }

    /**
     * 인기순, 추천순 클릭시 자동재생
     */
    private void firstLoadAction() {
        new Handler().postDelayed(() -> {
            if (isNotEmpty(getView()) && getUserVisibleHint()) {
                initPrevPlayingPosition();
                traceAutoPlayTarget(false);
                initGlobalMute();
            }
        }, 500);
    }

    /**
     * 섹션 업데이트
     */
    private class VodShopController extends GetUpdateController {


        protected VodShopController(Context activityContext, TopSectionList sectionList, boolean isCacheData) {
            super(activityContext, sectionList, isCacheData);
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.sectionLinkUrl,
                    tempSection.sectionLinkParams + "&reorder=true", tempSection.sectionName);

        }
    }

    /**
     * 최근재생포지션 변수 초기화
     */
    private void initPrevPlayingPosition() {
        prevPlayingPosition = -1;
    }


    /**
     * 방송알림 등록 수행
     */
    public void onEvent(Events.AlarmRegistEvent event) {
        if(event.caller != null){
            if(TVScheduleBroadAlarmDialogFragment.TOMORROWTV.equals(event.caller)){
                String url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_ADD;
                HttpEntity<Object> requestEntity = makeFormData(event.prdId, event.prdName, event.period, event.times);
                new VodShopFragment.BroadAlarmUpdateController(getContext()).execute(url, requestEntity, "add");
            }
        }

    }

    /**
     * 방송알림 API 호출용 FormData를 생성한다.
     *
     * @param prdId   상품아이디
     * @param prdName 상품명
     * @param period  기간
     * @param times   횟수
     * @return HttpEntity<Object>
     */
    public static HttpEntity<Object> makeFormData(String prdId, String prdName, String period, String times) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.APPLICATION_JSON);

        TVScheduleShopFragment.BroadAlarmParam param = new TVScheduleShopFragment.BroadAlarmParam();
        param.type = "PRDID";
        param.prdId = prdId;
        param.prdName = prdName;
        param.period = period;
        param.alarmCnt = times;

        return new HttpEntity<Object>(param, headers);
    }


    /**
     * 내일TV내 방송알람 신청 및 취소
     */
    public static class BroadAlarmUpdateController extends BaseAsyncController<BroadAlarmResult> {
        private Context context;
        private String url;
        private HttpEntity<Object> requestEntity;
        private String type;

        @Inject
        protected RestClient restClient;

        public BroadAlarmUpdateController(Context activityContext) {
            super(activityContext);
            this.context = activityContext;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            if (dialog != null) {
                dialog.setCancelable(true);
            }
            this.url = (String) params[0];
            this.requestEntity = (HttpEntity<Object>) params[1];
            this.type = (String) params[2];
        }

        @Override
        protected BroadAlarmResult process() throws Exception {
            return restClient.postForObject(new URI(url), requestEntity, BroadAlarmResult.class);
        }

        @Override
        protected void onSuccess(BroadAlarmResult result) throws Exception {
            super.onSuccess(result);



            BroadAlarmResult.ALARM_RESULT_TYPE resultType = BroadAlarmResult.ALARM_RESULT_TYPE.valueOf(result.errMsg);
            switch (resultType) {
                case MSG_SUCCESS:
                    if ("query".equals(type)) {
                        //조회
                        TVScheduleBroadAlarmDialogFragment cateDialog = TVScheduleBroadAlarmDialogFragment.newInstance(
                                result.imgUrl, result.prdId, result.prdName, result.phoneNo, result.infoTextList, TVScheduleBroadAlarmDialogFragment.TOMORROWTV );
                        cateDialog.show(((FragmentActivity) context).getSupportFragmentManager(), TVScheduleBroadAlarmDialogFragment.class.getSimpleName());
                        break;
                    }
                case ERR_DUPLICATE_PRODUCT: //이미 방송알림 등록된 상품
                    if ("query".equals(type)) {
                        new CustomOneButtonDialog((Activity) context).message(result.errMsgText).buttonClick(CustomOneButtonDialog.DISMISS).show();
                        EventBus.getDefault().post(new Events.AlarmUpdatetEvent(result.prdId, true));
                        break;
                    }
                case ERR_NOT_ALARM_PRODUCT: //이미 방송알림 해제된 상품
                    if ("query".equals(type)) {
                        CustomToast.makeTVScheduleBroadAlarmCancel(context, Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new Events.AlarmUpdatetEvent(result.prdId, false));
                        break;
                    }
                    if ("delete".equals(type)) {
                        CustomToast.makeTVScheduleBroadAlarmCancel(context, Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new Events.AlarmUpdatetEvent(result.prdId, false));
                    } else if ("add".equals(type)) {
                        makeBroadAlarmConfirmDialog(context, result.phoneNo, result.linkUrlText, result.linkUrl).show();
                        EventBus.getDefault().post(new Events.AlarmUpdatetEvent(result.prdId, true));
                    }

                    break;
                case ERR_NOT_LOGIN: //로그인 필요
                    String param;
                    Intent intent = new Intent(Keys.ACTION.LOGIN);
                    if (TVScheduleShopFragment.scheduleBroadType == TVScheduleShopFragment.ScheduleBroadType.DATA) {
                        param = TVScheduleShopFragment.ScheduleBroadType.DATA.name();
                    } else {
                        param = TVScheduleShopFragment.ScheduleBroadType.LIVE.name();
                    }
                    intent.putExtra(Keys.INTENT.WEB_URL,
                            ServerUrls.WEB.MOVE_SHOP_FROM_TABID_URL + TV_SCHEDULE_NAVI_ID + "&" + BROADTYPE_PARAM_KEY + "=" + param);
                    context.startActivity(intent);
                    break;
                case ERR_PHONE_EMPTY:   //핸드폰 번호 필요
                case ERR_MAX_ALARM_SIZE:    //방송알림 최대 등록 개수 초과
                case ERR_SERVER_SAVE_FAIL:  //방송알림 등록 실패
                case ERR_SERVER_DELETE_FAIL:    //방송알림 취소 실패
                    new CustomOneButtonDialog((Activity) context).message(result.errMsgText).buttonClick(CustomOneButtonDialog.DISMISS).show();
                    break;
                default:
                    //정의되지 않은 에러타입인 경우
                    new CustomOneButtonDialog((Activity) context).message(R.string.undefined_error).buttonClick(CustomOneButtonDialog.DISMISS).show();
                    break;
            }
        }

        @Override
        protected void onError(Throwable e) {
            super.onError(e);
        }
    }

    /**
     * 카드혜택 팝업 내용을 지정
     * @param item 카드 팝업을 설정하기 위한 SectionContentList item
     */
    private void setCardNoticePopup(SectionContentList item) {
        // item이 비어있을 경우 카드 팝업을 만들 수 없기 때문에 return;
        if (EmptyUtils.isEmpty(item)) {
            return;
        }

        TextView txtCardTitle = m_vPopupCardNotice.findViewById(R.id.txt_card_notice_title);
        TextView txtCardDate = m_vPopupCardNotice.findViewById(R.id.txt_card_notice_date);

        View viewCardSecond = m_vPopupCardNotice.findViewById(R.id.view_card_second);

        TextView txtCardFirst = m_vPopupCardNotice.findViewById(R.id.txt_card_first);
        TextView txtCardSecond = m_vPopupCardNotice.findViewById(R.id.txt_card_second);

        Button btnClosePopup = m_vPopupCardNotice.findViewById(R.id.btn_popup_close);
        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissCardNoticePopup();
            }
        });

        if (!EmptyUtils.isEmpty(item.productName) && !TextUtils.isEmpty(item.productName)) {
            txtCardTitle.setText(item.productName);
        }
        if (EmptyUtils.isEmpty(item.promotionName)) {
            item.promotionName = "";
        }
        txtCardDate.setText(item.promotionName);

        // 카드 할인 리스트가 비어 있으면 return;
        if (item.subProductList.size() <= 0) {
            return;
        }

        if (item.subProductList.size() == 1) {
            if (viewCardSecond != null) {
                viewCardSecond.setVisibility(View.GONE);
            }
        } else {
            if (viewCardSecond != null) {
                viewCardSecond.setVisibility(View.VISIBLE);
            }
        }

        AbsoluteSizeSpan sizeSpanCardMain = new AbsoluteSizeSpan(16, true);
        AbsoluteSizeSpan sizeSpanCardDiscount = new AbsoluteSizeSpan(22, true);

        for (int i=0; i<item.subProductList.size(); i++) {

            // 카드 혜택 최대 표현 갯수
            if (i >= 2) {
                break;
            }

            SpannableStringBuilder spannableCard = new SpannableStringBuilder();
            SectionContentList listTemp = item.subProductList.get(i);

            try {
                String[] arrProductName = listTemp.productName.split("&");

                if (arrProductName.length > 0 && arrProductName[0] != null) {
                    spannableCard.append(arrProductName[0].trim() + " ");
                    spannableCard.setSpan(sizeSpanCardMain, 0, spannableCard.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                spannableCard.append(listTemp.discountRate);
                int nStart = spannableCard.length() - listTemp.discountRate.length();
                spannableCard.setSpan(sizeSpanCardDiscount, nStart, spannableCard.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannableCard.append(listTemp.discountRateText);
                spannableCard.setSpan(sizeSpanCardMain, spannableCard.length() - listTemp.discountRateText.length(),
                        spannableCard.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (item.subProductList.size() < 2 && arrProductName.length > 1 && arrProductName[1] != null) {
                    spannableCard.append(" " + arrProductName[1].trim());
                    spannableCard.setSpan(sizeSpanCardMain, spannableCard.length() - arrProductName[1].length(),
                            spannableCard.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (StringUtils.checkHexColor("#" + listTemp.etcText1)) {
                    spannableCard.setSpan(new ForegroundColorSpan(Color.parseColor("#" + listTemp.etcText1)),
                            0, spannableCard.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (i == 0) {
                    txtCardFirst.setText(spannableCard);
                }
                else if (i == 1) {
                    txtCardSecond.setText(spannableCard);
                }
            }
            catch (NullPointerException e) {
                Ln.e(e.getMessage());
            }

        }

        m_bIsCardNoticeEnable = true;
    }

    /**
     * 설정된 카드 팝업을 보여준다.
     */
    private void showCardNoticePopup() {
        m_vPopupCardNotice.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom_long);
        animation.reset();
        m_vPopupCardNotice.clearAnimation();

        // Holder 와 겹쳐서 애니메이션이 정상 표시안되는 문제로 0.5초 후에 출력.
        new Handler().postDelayed(() -> {
            m_vPopupCardNotice.setVisibility(View.VISIBLE);
            m_vPopupCardNotice.startAnimation(animation);
        }, 500);

        new Handler().postDelayed(() -> dismissCardNoticePopup(), TIMER_POPUP_CARD_MILLISEC + 500);
    }

    /**
     * 카드 팝업을 사라지게 한다.
     */
    private void dismissCardNoticePopup() {
        if (m_vPopupCardNotice.getVisibility() == View.GONE) {
            return;
        }
        m_vPopupCardNotice.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom_long);
        animation.reset();
        m_vPopupCardNotice.clearAnimation();
        m_vPopupCardNotice.startAnimation(animation);
    }

    /**
     * 방송알림 업데이트 수행 (동일한 prdId는 모두 업데이트)
     * 편성표에서 EventBus날리면 이부분도 받게되므로 주석처리
     */
    /*
    public void onEvent(Events.AlarmUpdatetEvent event) {

        if (!TextUtils.isEmpty(event.prdId)) {

           //ShopInfo shopInfo = ((VodShopAdapter) mRecyclerView.getAdapter()).getInfo();

            //내일TV 상품 목록중에서 상품번호 같은것 찾는다
            for (int i = 0; i <mRecyclerView.getChildCount(); i++) {

               //View view = mRecyclerView.getLayoutManager().getChildAt(i);
                ShopInfo shopInfo = ((VodShopAdapter) mRecyclerView.getAdapter()).getInfo();

                if(event.prdId.equals(shopInfo.contents.get(i).sectionContent.dealNo)){

                    shopInfo.contents.get(i).sectionContent.broadAlarmDoneYn = event.isRegisted ? "Y" : "N";
                }
            }

            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

    }*/


}
