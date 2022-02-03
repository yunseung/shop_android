/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestdeal;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment;
import gsshop.mobile.v2.library.objectcache.CacheManager;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 *
 *
 */
public class BestDealShopFragment extends FlexibleShopFragment {

    // 동영상이 실행되는 위치 지정.
    private int topMargin;
    private int bottomMargin;
    private static int TOP_MARGIN = 600;
    private static int BOTTOM_MARGIN = 600;
    /**
     * 현재 카운트
     */
    @InjectView(R.id.txt_current_count)
    private TextView txt_current_count;

    /**
     * 전체 카운트
     */
    @InjectView(R.id.txt_total_count)
    private TextView txt_total_count;

    /**
     * 뷰타입 define
     */
    private String PRD_C_CST_SQ_VIEWTYPE = "PRD_C_CST_SQ";

    /**
     * 개인화 R4 갱신또는 새로고침 용
     * _=132131231++ 계속 바뀌는 해쉬코드용 타임 스탬프
     */
    private int mChaceHashValue = 0;

    /**
     *  개인화 R4 갱신또는 새로고침 용  실제 캐쉬 적용 유무를 Flag
     */
    private boolean mChaceHashUse = false;

    /**
     * tensera
     * 1. Add the following field to the class:
     */
    public static BestDealShopFragment instance;

    /**
     * tensera
     * 2. Add following methods to the class:
     */
    public static BestDealShopFragment getInstance() {
        return instance;
    }

    public void performRefresh() {
        CacheManager cacheManager =
                CacheManager.getInstance(MainApplication.DISK_CACHE);
        if (cacheManager != null) {
            cacheManager.clearRuntimeCache();
            //Must clear runtime cacheotherwise it will be served from ram
        }
        drawFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    public static BestDealShopFragment newInstance(int position) {
        instance = newInstanceInternal(position);
        return instance;
    }

    private static BestDealShopFragment newInstanceInternal(int position) {
        BestDealShopFragment fragment = new BestDealShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public BestDealShopFragment() {
        super();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topMargin = getActivity().getResources().getDimensionPixelSize(R.dimen.best_deal_media_top_margin);
        bottomMargin = getActivity().getResources().getDimensionPixelSize(R.dimen.best_deal_media_bottom_margin);

        mAdapter = new BestDealShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        //애니메이션을 제거하여 속도를 높여보자
        mRecyclerView.setAnimation(null);

    }

    // tv 생방송  실행.
    public void playTVLive(boolean play) {
        //키바나 수집 로그로 앱크래쉬 방지 (flexible.ej.getInfo()' on a null object reference)
        if (isEmpty(mAdapter) || isEmpty(mAdapter.getInfo())) {
            return;
        }

        if (play) {
            // 리스트에 보여지는 viewholder 중에서 생방송 viewholder를 실행함
            boolean found = false;
            for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                View view = mRecyclerView.getChildAt(i);
                BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                if (vh.getItemViewType() == ViewHolderType.BANNER_TYPE_TV_LIVE) {
                    if (view.getTop() < mRecyclerView.getHeight() - BOTTOM_MARGIN && view.getBottom() > TOP_MARGIN) {
                        EventBus.getDefault().post(new Events.FlexibleEvent.TvBestLivePlayEvent(true, vh.getAdapterPosition()));
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                EventBus.getDefault().post(new Events.FlexibleEvent.TvBestLivePlayEvent(false, -1));
            }
        } else {
            EventBus.getDefault().post(new Events.FlexibleEvent.TvBestLivePlayEvent(false, -1));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //롤리팝 이하버전에서 홈아이콘 클릭시 onResume이 onPause보다 먼저 호출되어
        //생방송영역 타이머가 동작하지 않는 버그가 발생하여 딜레이 시간을 늘려줌
        long interval = 1000L;
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            interval = 5000L;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getView() != null && mAdapter.getInfo() != null) {
                    //생방송 남은시간 표시용 타이머 시작을 위한 이벤트 전달
                    EventBus.getDefault().post(new Events.FlexibleEvent.TvLiveTimerEvent(true));
                }
            }
        }, interval);

        // 베스티 딜 롤링 시작 - TCLIST
        if (this.getUserVisibleHint()) {
            //Ln.i("StartRollingNo1BestDealBannerEvent : start ");
            if (getView() != null && mAdapter.getInfo() != null) {
                EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
                playBestDealVideo(true);
            }


        }
    }

    @Override
    public void onPause() {
        //생방송 남은시간 표시용 타이머 정지를 위한 이벤트 전달
        EventBus.getDefault().post(new Events.FlexibleEvent.TvLiveTimerEvent(false));
        // 베스트 딜 롤링 중지 : TCLIST
        if (this.getUserVisibleHint()) {
            //Ln.i("StartRollingNo1BestDealBannerEvent : stop ");
            EventBus.getDefault()
                    .post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(false));

            playBestDealVideo(false);
            playTVLive(false);
        }


        super.onPause();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
                playBestDealVideo(true);
            } else {
                playTVLive(false);
                playBestDealVideo(false);
            }
        }

    }

    /**
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    public void drawFragment() {

        // setSwipeRefresh();
        // TV생방송 존재 여부에 상관없이 전체 data loading
        new BestDealShopController(getActivity(), tempSection, true).execute();
    }

    /**
     * 퀵 리턴 메뉴 설정
     */
    @Override
    protected void setQuickReturn() {
        super.setQuickReturn();

        scrollListener.registerExtraOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 스크롤 상태가 변경되었을때
                // 베스트딜의 경우만 하단 카운트 뷰 적용함.
//                onScrollStateChanged 에서 상태값에 따라 Visible 처리가 들어가 있으면 깜빡이는 현상 발생, 이에 아래 부분 주석 처리 (20190516)
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPositions(null)[0];

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
//                        EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
//                        btnTop.setVisibility(View.VISIBLE);
                        if (btnTop.getVisibility() != View.VISIBLE && firstVisibleItemPosition > 0) {
                            btnTop.setVisibility(View.VISIBLE);
                        } else if (firstVisibleItemPosition <= 0) {
                            btnTop.setVisibility(View.GONE);
                        }
                        list_count_layout.setVisibility(View.GONE);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:

                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Ln.i("dx : " + dx + ", dy : " + dy);

                int firstVisibleItemPosition = mLayoutManager
                        .findLastCompletelyVisibleItemPositions(null)[0] + 1;

                txt_current_count.setText(String.valueOf(firstVisibleItemPosition));

                btnTop.setVisibility(View.GONE);
                if (firstVisibleItemPosition > 4) {
                    list_count_layout.setVisibility(View.VISIBLE);
                } else {
                    list_count_layout.setVisibility(View.GONE);
                }

                // 베스티 딜 동영상.
                playBestDealVideo(true);
            }
        });
    }

    // navigation api와 recycler adapter view type 매핑.
    protected int getFlexibleViewType(String viewType) {
        int type = super.getFlexibleViewType(viewType);
        if (type == ViewHolderType.BANNER_TYPE_NONE) {
            if ("ML".equals(viewType)) {
                // 동영상 딜
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    type = ViewHolderType.VIEW_TYPE_L;
                } else {
                    type = ViewHolderType.VIEW_TYPE_ML;
                }
            }
        }

        return type;
    }

    // tv 생방송  실행.
    public void playBestDealVideo(boolean play) {
        if (true || mAdapter.getInfo() == null) {
            return;
        }

        if (play) {
            // 리스트에 보여지는 viewholder 중에서 생방송 viewholder를 실행함
            boolean found = false;
            for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                View view = mRecyclerView.getChildAt(i);
                BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                if (vh.getItemViewType() == ViewHolderType.VIEW_TYPE_ML) {
                    if (view.getTop() < mRecyclerView.getHeight() - bottomMargin && view.getBottom() > topMargin) {
                        EventBus.getDefault().post(new Events.FlexibleEvent.VideoPlayEvent(true, vh.getAdapterPosition()));
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                EventBus.getDefault().post(new Events.FlexibleEvent.VideoPlayEvent(false, -1));
            }
        } else {
            EventBus.getDefault().post(new Events.FlexibleEvent.VideoPlayEvent(false, -1));
        }
    }


    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo,
                           int tab, int listPosition) {
        super.updateList(sectionList, contentsListInfo, tab, listPosition);

        ShopInfo info = mAdapter.getInfo();
        if (info != null && info.contents != null) {
            txt_total_count.setText(String.valueOf(info.contents.size() - 2));
        }


    }


    /**
     * classes
     */

    /**
     * 섹션 업데이트
     */
    public class BestDealShopController extends GetUpdateController {


        protected BestDealShopController(Context activityContext, TopSectionList sectionList, boolean isCacheData) {
            super(activityContext, sectionList, isCacheData);
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.sectionLinkUrl,
                    tempSection.sectionLinkParams + "&reorder=true" + "&reqtype=" + MainApplication.homeLoaded , tempSection.sectionName);

//            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
//                    isCacheData, true, "http://10.52.37.213:9999/app/main/bestdeal",
//                    "version=3.4&pageIdx=1&pageSize=400&naviId=54"+ "&reorder=true", tempSection.sectionName);
        }


    }

    // 해당 이벤트를 받는 곳이 TV쇼핑과 베스트딜로 각 영역에서 이벤트 처리하도록 shopInfo의 데이터로 TV쇼핑인지 베스트딜인지 구분
    public void onEvent(Events.FlexibleEvent.DirectBuyEvent event) {
        if (event.shopInfo.sectionList.viewType.equals(ShopInfo.TYPE_BESTDEAL)) {
            ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(event.position, 0);
        }
//        ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(event.getPositionByType(ViewHolderType.BANNER_TYPE_TV_LIVE), 0);

    }

    /**
     * 다이나믹 구좌 설정을 위한 이벤트(상품 -> 홈, 장바구니 -> 홈)
     * 위의 경우에만 showPsnlCuration = true
     * @param event
     */
    public void onEvent(Events.psnlCurationEvent event) {
        Events.psnlCurationEvent curationEvent = EventBus.getDefault().getStickyEvent(Events.psnlCurationEvent.class);
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent);
        }

        if(mActivity != null && event != null)
            loadDTData(mActivity, event.psnlCurationType.toString());
    }

    /**
     * PRD_C_CST_SQ 뷰타입의 인덱스 반환
     * @return
     */
    public int getCurationPos(){
        for(int i = 0 ; i< mAdapter.getItemCount() ; i++){
            SectionContentList sectionContent = ((ShopInfo.ShopItem)mAdapter.getItem(i)).sectionContent;
            if(sectionContent != null && PRD_C_CST_SQ_VIEWTYPE.equals(sectionContent.viewType)){
                return  i;
            }
        }
        return -1;
    }



    /**
     * DT (R4,R5케이스) 데이터 호출
     * @param context
     * @param loadType
     */
    private void loadDTData(Context context, String loadType) {
        new BaseAsyncController<SectionContentList>(context) {
            private String loadType;

            @Inject
            private RestClient restClient;

            @Override
            protected void onPrepare(Object... params) throws Exception {
                loadType = (String) params[0];
            }

            @Override
            protected SectionContentList process() throws Exception {
                int hashValueTemp = (int)System.currentTimeMillis();

                //상품코드 유효성 체크
                String prdParam =  "" ;
                if(MainApplication.lastPrdId != null && MainApplication.lastPrdId.length() > 7 && MainApplication.lastPrdId.length() < 10 && "PRD".equals(loadType)){
                    if(DisplayUtils.isNumeric(MainApplication.lastPrdId)){
                        prdParam = MainApplication.lastPrdId;
                    }
                }
                MainApplication.lastPrdId = "";

                String reqUrl = ServerUrls.getHttpRoot() + ServerUrls.REST.HyperPersonalizedUrl + ServerUrls.REST.RCMTYPE + loadType + "&" + ServerUrls.REST.LASTPRD + prdParam +  "&_=" + hashValueTemp;
                return (SectionContentList)DataUtil.getData(context, restClient, SectionContentList.class, false, false, reqUrl);
            }

            @Override
            protected void onSuccess(SectionContentList result) throws Exception {
                if(PRD_C_CST_SQ_VIEWTYPE.equals(result.viewType)){
                    //추천데이터 있을때만 이전꺼 제거하고 새로추가
                    ShopInfo.ShopItem removedItem = mAdapter.removeInfoDT(getCurationPos());
                    mAdapter.addInfoDT(result, getVisibleItemsPosition().get(0));
                }else{
                   //추천데이터 없으면 제거없이 이전 R시리즈 유지
                }
            }

            @Override
            protected void onError(Throwable e) {
                Ln.e(e);
            }
        }.execute(loadType);
    }

    /**
     * 현재 화면에 보여지는 아이템들의 position list 를 반환한다.
     *
     * @return items position list
     */
    private ArrayList<Integer> getVisibleItemsPosition() {
        int firstViewsIds = mLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
        int lastViewsIds = mLayoutManager.findLastCompletelyVisibleItemPositions(null)[0];

        if (firstViewsIds < 0 || lastViewsIds < 0) {
            return new ArrayList<Integer>();
        }

        ArrayList<Integer> visibleItemsPosition = new ArrayList<>();
        int visibleItemsCount = lastViewsIds - firstViewsIds + 1;
        for (int i = 0; i < visibleItemsCount; i++) {
            visibleItemsPosition.add(firstViewsIds + i);
        }
        return visibleItemsPosition;
    }
}
