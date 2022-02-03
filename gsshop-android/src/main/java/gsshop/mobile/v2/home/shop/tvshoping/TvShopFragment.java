/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.tvshoping;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment;
import gsshop.mobile.v2.library.quickreturn.QuickReturnType;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 *
 *
 */
public class TvShopFragment extends FlexibleShopFragment {

    private static int TOP_MARGIN = 600;
    private static int BOTTOM_MARGIN = 600;

    private  ArrayList<ArrayList<ShopInfo.ShopItem>> categorylItem = new ArrayList<ArrayList<ShopInfo.ShopItem>>();

    private ArrayList<ShopInfo.ShopItem> currentCategoryItem = null;

    private int categoryReflashItemRangeStart;

    /**
     * 현재 선택된 탭메뉴 상태 저장
     */
    private SubTabMenu currentSubTabMenu;

    public static TvShopFragment newInstance(int position) {
        TvShopFragment fragment = new TvShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public TvShopFragment() {
        super();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new TvShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TvShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        setTvSubMenu(SubTabMenu.live);
        setQuickReturn();

    }

    private void setTvSubMenu(SubTabMenu menu) {
        currentSubTabMenu = menu;
        EventBus.getDefault().post(new Events.FlexibleEvent.ResetItemEvent());
    }


    /**
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    @Override
    public void drawFragment() {

        // setSwipeRefresh();
        // TV생방송 존재 여부에 상관없이 전체 data loading
        new BestDealShopController(getActivity(), tempSection, true).execute();
    }

    /**
     * 퀵 리턴 메뉴 설정
     */
    protected void setQuickReturn() {
        super.setQuickReturn();

        scrollListener.setQuickReturnViewType(QuickReturnType.HEADER_FOOTER);

        // //상단으로 버튼 클릭시
        btnTop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // mListView.setSelection(0);
                ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(0);
                btnTop.setVisibility(View.GONE);

                //탑버튼 클릭시 해더영역 노출
                ((HomeActivity) getContext()).expandHeader();
                ViewHelper.setTranslationY(btnTop, 0);
                ViewHelper.setTranslationY(mCoordinator.getFooterLayout(), 0);
                //CSP BUTTON MSLEE
                ViewHelper.setTranslationY(mCoordinator.getCspLayout(), 0);

            }
        });
    }

    // tv 생방송  실행.
    public void playTVLive(boolean play) {
        if (mAdapter.getInfo() == null) {
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
                        EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(true, vh.getAdapterPosition()));
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(false, -1));
            }
        } else {
            EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(false, -1));
        }
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int listPosition) {
        //Ln.i("FlexibleShopFragment setInitUI");
        if ("WEB".equals(sectionList.sectionType)) {
            // ;
        } else {
            if (contentsListInfo != null || getActivity() != null) {

                ShopInfo info = new ShopInfo();

                info.contents = new ArrayList<ShopItem>();

                info.sectionList = sectionList;
                isPageLoading = false;
                if(contentsListInfo.ajaxPageUrl != null && !"".equals(contentsListInfo.ajaxPageUrl)){
                    info.ajaxPageUrl = contentsListInfo.ajaxPageUrl;
                }else if(contentsListInfo.ajaxfullUrl != null && !"".equals(contentsListInfo.ajaxfullUrl)){
                    info.ajaxPageUrl = contentsListInfo.ajaxfullUrl;
                }
                // 배너.
                ShopItem content;

                // 상단 배너. imageUrl로 배너 존재 여부 체크.
                //10/19 품질팀 요청
                if (contentsListInfo.banner != null && contentsListInfo.banner.imageUrl != null && contentsListInfo.banner.linkUrl != null
                        && DisplayUtils.isValidString(contentsListInfo.banner.imageUrl)
                        ) {
                    content = new ShopItem();
                    content.type = ViewHolderType.BANNER_TYPE_BAND;
                    content.sectionContent = new SectionContentList();
                    content.sectionContent.imageUrl = contentsListInfo.banner.imageUrl;
                    content.sectionContent.linkUrl = contentsListInfo.banner.linkUrl;
                    content.sectionContent.height = contentsListInfo.banner.height;
                    content.sectionContent.viewType = "B_TVH";

                    info.contents.add(content);
                }

                // 이벤트 메뉴. 헤더.
                if (contentsListInfo.headerList != null && !contentsListInfo.headerList.isEmpty()) {
                    for (SectionContentList header : contentsListInfo.headerList) {
                        int type = getFlexibleViewType(header.viewType);
                        if (type != ViewHolderType.BANNER_TYPE_NONE) {
                            content = new ShopItem();
                            content.type = type;
                            content.sectionContent = header;
                            if(content.sectionContent != null) {
                                info.contents.add(content);
                            }
                        } else {
                            if ("SE".equals(header.viewType)) {
                                // 슬라이드 이벤트 뷰타입 설정
                                content = new ShopItem();
                                if (header.subProductList.size() > 1) {
                                    content.type = ViewHolderType.VIEW_TYPE_SE;
                                    content.sectionContent = header;
                                } else if (header.subProductList.size() == 1) {
                                    // 슬라이드 이미지 갯수가 1개이면 이미지 배너에 넣음.
                                    content.type = ViewHolderType.BANNER_TYPE_ROLL_ONE_IMAGE;
                                    content.sectionContent = header.subProductList.get(0);
                                }
                                if(content.sectionContent != null) {
                                    info.contents.add(content);
                                }
                            }
                        }
                    }

                }

                // 카테고리.
                if (!sectionList.subMenuList.isEmpty()) {
                    info.tabIndex = 0;
                    if ("FXCLIST".equals(sectionList.viewType)) {
                        // 플렉서블 매장.
                        content = new ShopItem();
                        content.type = ViewHolderType.VIEW_TYPE_FXCLIST;
                        info.tabIndex = tab;
                        info.contents.add(content);
                    }
                }

                // 베스트딜 탭 TV생방송영역 AB Test (TV쇼핑 탭에는 항상 노출)
                // 베스트딜 && 매크로값 B가 아닌 경우 노출, 베스트딜 && 매크로값 B인 경우 미노출
                // 생방송역역 노출 기준 (broadType, prdUrl 두 값 모두 없는 경우만 그리지 않음)
                // 생방송, 데이타방송 중 한개 이상 데이타가 유효하면 방송영역 그린다.
                if ((contentsListInfo.tvLiveBanner != null
                        && (!TextUtils.isEmpty(contentsListInfo.tvLiveBanner.broadType)
                        || !TextUtils.isEmpty(contentsListInfo.tvLiveBanner.linkUrl)))
                        || (contentsListInfo.dataLiveBanner != null
                        && (!TextUtils.isEmpty(contentsListInfo.dataLiveBanner.broadType)
                        || !TextUtils.isEmpty(contentsListInfo.dataLiveBanner.linkUrl)))
                        ) {
                    // tv live header view 생성
                    content = new ShopItem();
                    content.type = ViewHolderType.BANNER_TYPE_TV_LIVE_DUAL;
                    info.tvLiveBanner = contentsListInfo.tvLiveBanner;
                    info.dataLiveBanner = contentsListInfo.dataLiveBanner;

                    info.contents.add(content);

                    //이게 먼지 모르겠음
                    if (info.tvLiveBanner != null
                            && info.tvLiveBanner.banner != null
                            && DisplayUtils.isValidString(info.tvLiveBanner.banner.imageUrl)) {
                        content = new ShopItem();
                        content.type = ViewHolderType.BANNER_TYPE_BAND;
                        content.sectionContent = new SectionContentList();
                        content.sectionContent.imageUrl = info.tvLiveBanner.banner.imageUrl;
                        content.sectionContent.linkUrl = info.tvLiveBanner.banner.linkUrl;
                        content.sectionContent.viewType = "B_IS";
                        info.contents.add(content);
                    }

                }

                if (contentsListInfo.tvLiveBannerList != null && !contentsListInfo.tvLiveBannerList.isEmpty()) {
                    content = new ShopItem();
                    content.type = ViewHolderType.BANNER_TYPE_TV_ITEM;
                    content.sectionContent = new SectionContentList();
                    content.sectionContent.subProductList = contentsListInfo.tvLiveBannerList;
                    info.contents.add(content);
                }

                for (int i = 0; i < contentsListInfo.productList.size(); i++) {
                    SectionContentList c = contentsListInfo.productList.get(i);
                    int type = getFlexibleViewType(c.viewType);
                    if (type != ViewHolderType.BANNER_TYPE_NONE) {
                        content = new ShopItem();
                        content.type = type;
                        content.sectionContent = c;
                        if(content.sectionContent != null) {
                            //인기프로그램 아이템이 5개 미만이면 보여주지 않는다. 이전룰
                            //인그프로그램 아이템이 6개가 아니면 보여주지 않는다. 신규룰
                            if(content.type == ViewHolderType.VIEW_TYPE_PC){
                                if(isEmpty(content.sectionContent.subProductList)){
                                    continue;
                                }
                            }

                            info.contents.add(content);

                            if(type == ViewHolderType.VIEW_TYPE_TCF){

                                //새로고침 시작 시점을 기억하기 위해 지정
                                categoryReflashItemRangeStart =  info.contents.size();
                                categorylItem.clear();

                                for(SectionContentList category : content.sectionContent.subProductList){
                                    ArrayList<ShopInfo.ShopItem> categoryContents = new ArrayList<ShopInfo.ShopItem>();
                                    categorylItem.add(categoryContents);

                                    for(SectionContentList item : category.subProductList){
                                        content = new ShopInfo.ShopItem();
                                        content.type = getFlexibleViewType(item.viewType);
                                        content.sectionContent = item;

                                        categoryContents.add(content);
                                    }

                                    for(int j=i+1 ;j< contentsListInfo.productList.size(); j++) {
                                        SectionContentList subContent = contentsListInfo.productList.get(j);
                                        int subType = getFlexibleViewType(subContent.viewType);
                                        if (subType != ViewHolderType.BANNER_TYPE_NONE) {
                                            content = new ShopItem();
                                            content.type = type;
                                            content.sectionContent = c;
                                            categoryContents.add(content);
                                        }
                                    }

                                    // 마지막 풋터 지정.
                                    content = new ShopItem();
                                    content.type = ViewHolderType.BANNER_TYPE_FOOTER;
                                    categoryContents.add(content);
                                }
                                if(!categorylItem.isEmpty()) {
                                    currentCategoryItem = categorylItem.get(0);
                                }
                                addCategoryItem(info);
                            }
                        }
                    } else {
                        if ("B_SIS".equals(c.viewType)) {
                            // "슬라이드 이미지배너" = B_SIS (subProductList -> imageUrl, prdUrl)
                            content = new ShopItem();
                            if (c.subProductList.size() > 1) {
                                content.type = ViewHolderType.VIEW_TYPE_B_SIC;
                                content.sectionContent = c;
                                if(content.sectionContent != null) {
                                    info.contents.add(content);
                                }
                            } else if (c.subProductList.size() == 1) {
                                // 슬라이드 이미지 갯수가 1개이면 이미지 배너에 넣음.
                                content.type = ViewHolderType.BANNER_TYPE_IMAGE;
                                content.sectionContent = c.subProductList.get(0);
                                if(content.sectionContent != null) {
                                    info.contents.add(content);
                                }

                            }
                        }
                    }
                }
//                // 마지막 풋터 지정.
                if(categorylItem.isEmpty()) {
                    content = new ShopItem();
                    content.type = ViewHolderType.BANNER_TYPE_FOOTER;
                    info.contents.add(content);
                }

                //default live로 세팅
                info.tabIndex = 0;

                mAdapter.setInfo(info);
                mAdapter.notifyDataSetChanged();
                ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(0);

            }
        }


    }

    /**
     * 카테고리 아이템을 추가시킨다.
     * @param info info
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void addCategoryItem(ShopInfo info) {
        if (info.contents != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                info.contents.addAll(currentCategoryItem);
            } else {
                for(ShopInfo.ShopItem item : currentCategoryItem) {
                    info.contents.add(item);
                }
            }
        }
    }

    /**
     * 카테고리 아이템을 제거한다.
     * @param info info
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void removeCategoryItem(ShopInfo info){
        if (info.contents != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                info.contents.removeAll(currentCategoryItem);
            } else {
                for(ShopInfo.ShopItem item : currentCategoryItem) {
                    info.contents.remove(item);
                }
            }
        }
    }

    /**
     * 카테고리 아이템을 업데이트 한다.
     * @param index index
     */
    private void updateCategoryItem(int index){
        try {
            ShopInfo info = mAdapter.getInfo();
            removeCategoryItem(info);
            currentCategoryItem = categorylItem.get(index);
            addCategoryItem(info);
            if(currentCategoryItem != null && currentCategoryItem.size() > categoryReflashItemRangeStart) {
                mAdapter.notifyItemRangeChanged(categoryReflashItemRangeStart, info.contents.size());
            }else{
                mAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * 중카테고리 업데이트
     */
    public void onEvent(Events.FlexibleEvent.UpdateFlexibleShopEvent event) {
        if (getUserVisibleHint()) {
            updateCategoryItem(event.tab);
        }
    }

    /**
     *
     * @param event event
     */
    public void onEventMainThread(Events.FlexibleEvent.UpdateBestShopEvent event) {
        if (getUserVisibleHint()) {
            updateCategoryItem(event.tab);
        }
    }

    /**
     * 섹션 업데이트
     */
    public class BestDealShopController extends GetUpdateController {


        protected BestDealShopController(Context activityContext, TopSectionList sectionList, boolean isCacheData) {
            super(activityContext, sectionList, isCacheData);
        }


        @Override
        protected ContentsListInfo process() throws Exception {
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.sectionLinkUrl,
                    tempSection.sectionLinkParams + "&reorder=true", tempSection.sectionName);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        //롤리팝 이하버전에서 홈아이콘 클릭시 onResume이 onPause보다 먼저 호출되어
        //생방송영역 타이머가 동작하지 않는 버그가 발생하여 딜레이 시간을 늘려줌
        long interval = 3000L;
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            interval = 5000L;
        }

        //라이브톡 텍스트롤링 타이머 구동을 위한 이벤트 전달
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getView() != null && mAdapter.getInfo() != null) {
                    //생방송 남은시간 표시용 타이머 시작을 위한 이벤트 전달
                    EventBus.getDefault().post(new Events.FlexibleEvent.TvLiveTimerEvent(true));
                    //라이브톡 텍스트롤링 타이머 시작을 위한 이벤트 전달
                    EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingLiveTalkTextEvent(true));
//                    playTVLive(true);
                }
            }
        }, interval);
    }

    @Override
    public void onStop() {
        super.onStop();

        //생방송 남은시간 표시용 타이머 정지를 위한 이벤트 전달
        EventBus.getDefault().post(new Events.FlexibleEvent.TvLiveTimerEvent(false));
        //라이브톡 텍스트롤링 타이머 정지를 위한 이벤트 전달
        EventBus.getDefault()
                .post(new Events.FlexibleEvent.StartRollingLiveTalkTextEvent(false));
        if (this.getUserVisibleHint()) {

            playTVLive(false);

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            // shop swiping stops tv live
            if (!isVisibleToUser) {
                playTVLive(false);
            }else{
                EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
            }
        }

    }

    /**
     * 해당 포지션 아이템을 새로고침하는 이벤트 event
     * @param event event
     */
    public void onEvent(Events.FlexibleEvent.UpdateRowEvent event) {
        //Ln.i("StartRollingNo1BestDealBannerEvent : "+ event.start);
        if ( mAdapter != null) {
            mAdapter.notifyItemChanged(event.row);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.getUserVisibleHint()) {
            if (getView() != null && mAdapter.getInfo() != null) {
                EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault()
                .post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(false));
    }

    /**
     * 탭메뉴 구분
     * 선택된 방송을 확인하는 용도로 사용하자
     */
    public enum SubTabMenu {
        live, data
    }


    // 해당 이벤트를 받는 곳이 TV쇼핑과 베스트딜로 각 영역에서 이벤트 처리하도록 shopInfo의 데이터로 TV쇼핑인지 베스트딜인지 구분
    public void onEventMainThread(Events.FlexibleEvent.DirectBuyEvent event) {
        if(event.shopInfo.sectionList.viewType.equals(ShopInfo.TYPE_TV_SHOPPING)){
            ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(event.position, 0);
        }
    }
}
