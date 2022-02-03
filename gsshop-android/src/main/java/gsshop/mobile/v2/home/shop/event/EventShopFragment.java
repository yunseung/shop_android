/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.event;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.shop.DividerItemDecoration;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;

import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_CX_CATE_GBA;

/**
 *
 *
 */
public class EventShopFragment extends FlexibleShopFragment {

    private StickyRecyclerHeadersDecoration headersDecor;

    public static gsshop.mobile.v2.home.shop.event.EventShopFragment newInstance(int position) {
        gsshop.mobile.v2.home.shop.event.EventShopFragment fragment = new gsshop.mobile.v2.home.shop.event.EventShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public EventShopFragment() {
        super();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // flexible 리스트 매장.

        mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new EventShopStickyTabAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Add the sticky headers decoration
            headersDecor = new StickyRecyclerHeadersDecoration(
                    (StickyRecyclerHeadersAdapter) mAdapter);
            mRecyclerView.addItemDecoration(headersDecor);

            // Add touch listeners
            StickyRecyclerHeadersTouchListener touchListener = new StickyRecyclerHeadersTouchListener(
                    mRecyclerView, headersDecor);
            touchListener.setOnHeaderClickListener(
                    new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                        @Override
                        public void onHeaderClick(View header, int position, long headerId) {
                            int tabIndex = 0;
                            int selectedTabPosition = -1;
                            String selectedTabSectionName = null;
                            RecyclerView tabs = (RecyclerView) header
                                    .findViewById(R.id.recycler_event_tabs);

                            for (int i = 0; i < tabs.getChildCount(); i++) {
                                View tab = tabs.getChildAt(i);
                                if (tab instanceof TextView) {
                                    int left = tab.getLeft();
                                    int top = tab.getTop();
                                    int width = tab.getWidth();
                                    int height = tab.getHeight();
                                    Rect rect = new Rect(left, top, left + width, top + height);

                                    //aspectj 제거과정 중 stickyheadersrecyclerview lib.를 lib 폴더에서
                                    //gradle implementation으로 변경하면서 onHeaderClick 함수 파라미터가 변경되어 아래코드 주석처리
                                    /*if (rect.contains(x, y)) {
                                        selectedTabPosition = tabIndex;
                                        //Ln.i("selectedTabPosition : " + selectedTabPosition + ", tabIndex : " + tabIndex);
                                    }*/
                                    tabIndex++;
                                }

                            }

                            // 이벤트 탭 클릭 아님.
                            if (selectedTabPosition < 0) {
                                return;
                            }

                            tabIndex = 0;
                            // 탭 클릭 효과 주기.
                            for (int i = 0; i < tabs.getChildCount(); i++) {
                                View tab = tabs.getChildAt(i);
                                if (tab instanceof TextView) {
                                    if (tabIndex == selectedTabPosition) {
                                        ((TextView) tab).setTextAppearance(getActivity(),
                                                R.style.EventTabOn);
                                        selectedTabSectionName = (String) ((TextView) tab).getText();
                                        tab.setSelected(true);
                                    } else {
                                        ((TextView) tab).setTextAppearance(getActivity(),
                                                R.style.EventTabOff);
                                        tab.setSelected(false);
                                    }
                                    tabIndex++;
                                }

                            }

                            ShopInfo info = mAdapter.getInfo();
                            // 이벤트 탭 위치.
                            info.tabIndex = selectedTabPosition;

                            mAdapter.notifyDataSetChanged();

                            // GTM 클릭이벤트 전달
                            if (selectedTabSectionName != null) {
                                String action = String.format("%s_%s_%s",
                                        GTMEnum.GTM_ACTION_HEADER, selectedTabSectionName,
                                        GTMEnum.GTM_ACTION_3DEPTH_TAIL);
                                String label = String.format("%s_%s",
                                        Integer.toString(selectedTabPosition),
                                        selectedTabSectionName);
                                GTMAction.sendEvent(getActivity(), GTMEnum.GTM_AREA_CATEGORY,
                                        action, label);
                            }

                            // category link 주소
                            String url = ((EventShopStickyTabAdapter) mAdapter).getHeaderData().subProductList.get(selectedTabPosition).linkUrl;
                            new UrlUpdateController(getActivity()).execute(url, true);

                        }
                    });

            mRecyclerView.addOnItemTouchListener(touchListener);
        }

        // Add decoration for dividers between list items
        int staggeredInner = getActivity().getResources()
                .getDimensionPixelSize(R.dimen.event_menu_staggered_inner_space);
        int staggeredOuter = getActivity().getResources()
                .getDimensionPixelSize(R.dimen.event_menu_staggered_outer_space);
        int wideInner = getActivity().getResources()
                .getDimensionPixelSize(R.dimen.event_menu_wide_inner_space);
        int wideOuter = getActivity().getResources()
                .getDimensionPixelSize(R.dimen.event_menu_wide_outer_space);

        // resize space
        staggeredInner = DisplayUtils.getResizedPixelSizeToScreenSize(getActivity(),
                staggeredInner);

        //Ln.i("size - staggeredInner : " + staggeredInner);
        // staggeredOuter = DisplayUtils.getResizedPixelSizeToScreenSize(getActivity(),
        // staggeredOuter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(staggeredInner,
                staggeredOuter, wideInner, wideOuter));


    }

    private EventShopController mSwipeRefreshController = null;
    @Override
    protected void onSwipeRefrehsing() {
        isNowSwipeRefreshing = true;
        if (mSwipeRefreshController != null) {
            mSwipeRefreshController.cancel();
            mSwipeRefreshController = null;
        }
        mSwipeRefreshController = new EventShopController(getActivity(), tempSection, true, true);
        mSwipeRefreshController.execute(true);
    }

    /**
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    public void drawFragment() {

        // setSwipeRefresh();
        // TV생방송 존재 여부에 상관없이 전체 data loading
        new EventShopController(getActivity(), tempSection, true, false).execute();
    }

    @Override
    protected int getFlexibleViewType(String viewType) {
        int type = super.getFlexibleViewType(viewType);
        if (type == ViewHolderType.BANNER_TYPE_NONE) {
            // 이벤트 매장 category
            if ("BAN_CX_CATE_GBA".equals(viewType)) {
                type = VIEW_TYPE_BAN_CX_CATE_GBA;
            }
        }

        return type;
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int categoryPosition) {
        updateList(sectionList, contentsListInfo, tab, categoryPosition, false);
    }

    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int categoryPosition, boolean isSwipeRefresh) {
        // 당겨서 새로고침이면 category Position을 -1로 해서 처음부터 받아오게끔.
        if (isSwipeRefresh) {
            categoryPosition = 0;
        }
        else {
            categoryPosition = ((EventShopStickyTabAdapter) mAdapter).getCategoryPosition();
        }
        super.updateList(sectionList, contentsListInfo, tab, categoryPosition);

        // swipeRefresh 이면 adapter에서 존재 하지 않는 VIEW_TYPE_BAN_CX_CATE_GBA 유형을 빼주어야 한다.
        if (categoryPosition < 0 || isSwipeRefresh) {
            List<ShopInfo.ShopItem> contents = mAdapter.getInfo().contents;
            for (int i = 0; i < contents.size(); i++) {
                ShopInfo.ShopItem item = contents.get(i);
                if (item.type == VIEW_TYPE_BAN_CX_CATE_GBA) {
                    ((EventShopStickyTabAdapter) mAdapter).setHeaderData(item.sectionContent, i);
                    contents.remove(i);
                    break;
                }
            }
        }

        mAdapter.notifyDataSetChanged();

        // 위치 조절
        int firstVisiblePosition = mLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
        if(firstVisiblePosition > categoryPosition) {
            mLayoutManager.scrollToPositionWithOffset(categoryPosition, 0);
            final int finalCategoryPosition = categoryPosition;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLayoutManager.scrollToPositionWithOffset(finalCategoryPosition, 0);
                }
            }, 200);
        }
    }

    /**
     * classes
     */

    /**
     * 섹션 업데이트
     */
    public class EventShopController extends GetUpdateController {

        private boolean mSwipeRefresh = false;

        protected EventShopController(Context activityContext, TopSectionList sectionList, boolean isCacheData, boolean isSwipeRefresh) {
            super(activityContext, sectionList, isCacheData);
            mSwipeRefresh = isSwipeRefresh;
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.sectionLinkUrl,
                    tempSection.sectionLinkParams + "&reorder=true", tempSection.sectionName);
        }


        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            // 프로그래스바 안보이게.
            mRefreshView.setVisibility(View.GONE);

            // 현재 액티비티 활성화 상태 체크
            if (getActivity() != null && listInfo != null && listInfo.productList != null) {
                updateList(tempSection, listInfo, 0, -1, mSwipeRefresh);
            }

            // 장바구니 카운트를 업데이트 한다.
            mActivity.setBasketcnt();
            // SwipeRefreshing 프로그래스 바 hide
            mLayoutSwipe.setRefreshing(false);
            isNowSwipeRefreshing = false;
        }

    }


}
