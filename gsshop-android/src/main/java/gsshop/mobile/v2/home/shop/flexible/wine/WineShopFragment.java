package gsshop.mobile.v2.home.shop.flexible.wine;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.KeyboardHeightObserver;
import gsshop.mobile.v2.util.KeyboardHeightProvider;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.blankj.utilcode.util.KeyboardUtils.hideSoftInput;

public class WineShopFragment extends FlexibleShopFragment {

    // 매장내 검색 창에서 키보드가 올라오면 스크롤을 올려 검색창을 키보드 위에 위치한다.
    private KeyboardHeightProvider mKeyboardHeightProvider;
    private int mTotalScroll = 0;

    public static WineShopFragment newInstance(int position) {
        //Ln.i("position : " + position);
        WineShopFragment fragment = new WineShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }
    public WineShopFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentType = FRAGMENT_TYPE.HEADER;
        if (super.onCreateView(inflater, container, savedInstanceState) == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_flexible_shop_header, container, false);

        header = view.findViewById(R.id.header);
        header.setVisibility(View.GONE);

        View itemView = inflater.inflate(R.layout.view_holder_tab_anch_wine, container, false);
        headerViewHolder = new TabAnchWineVH(itemView, tempSection.navigationId, true );
        header.addView(headerViewHolder.itemView);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new WineShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // keyboard 가 화면에 보이면 스크롤 할때 키보드를 내린다.
                if (isKeyInputShow && dy != 0) {
                    clearSearchKeyword();
                }

                mTotalScroll += dy;

//                if (ClickUtils.work(300)) {
//                    return;
//                }

                traceHeaderView(dy > 0);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        mKeyboardHeightProvider = new KeyboardHeightProvider(getActivity());
        mKeyboardHeightProvider.setKeyboardHeightObserver(new KeyboardHeightObserver() {
            @Override
            public void onKeyboardHeightChanged(int height, int orientation) {
                if (!getUserVisibleHint()) return;

                /**
                 * 매장내 검색 창에서 키보드가 올라오면 스크롤을 올려 검색창을 키보드 위에 위치한다.
                 */
                if (height > 0) {
                    // 키보드가 올라옴

                    for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                        View view = mRecyclerView.getChildAt(i);
                        BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                        if (vh instanceof MenuSldWindVH) {
                            int position = vh.getAdapterPosition();
                            ((BugFixedStaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, -dp2px(30));
                        }
                    }

                    // delay를 주고 키보드 플래그 값을 설정한다.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isKeyInputShow = true;
                        }
                    }, 400);
                } else {
                    isKeyInputShow = false;
                }
            }
        });
        mKeyboardHeightProvider.start();
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int listPosition) {
        //사용변수 초기화
        isFirst = true;
        tabPrdMapWine.clear();

        super.updateList(sectionList, contentsListInfo, tab, listPosition);
    }

    /**
     * 해더 뷰홀더를 검색하여 노출/숨김 처리를 수행한다.
     *
     * @param isUp if true, scroll up
     */
    private void traceHeaderView(boolean isUp) {
        int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPositions(null)[0];

        for ( int i = firstVisiblePosition; i >= 0; i-- ) {
            String viewType = ((ShopInfo.ShopItem)mAdapter.getItem(i)).sectionContent.viewType;

            if ("TAB_ANCH_WINE".equals(viewType)){
                header.setVisibility(View.VISIBLE);
                String tempTabSeq = ((ShopInfo.ShopItem)mAdapter.getItem(i)).sectionContent.tabSeq;

                EventBus.getDefault().post(new Events.FlexibleEvent.EventWine.SectionSyncEvent(tempTabSeq, isUp));

                return;
            }
        }

        header.setVisibility(View.GONE);
    }

    protected int getFlexibleViewType(String viewType) {
        int type = ViewHolderType.BANNER_TYPE_NONE;

        if("BAN_TITLE_WINE".equals(viewType)) {
            // 와인 매장 타이틀
            type = ViewHolderType.VIEW_TYPE_BAN_TITLE_WINE;
        } else if("BAN_SLD_WINE".equals(viewType)) {
            // 와인 매장 롤링 이미지 배너
            type = ViewHolderType.VIEW_TYPE_BAN_SLD_WINE;
        } else if("MENU_SLD_WINE".equals(viewType)) {
            // 와인 매장 메뉴 롤링 배너
            type = ViewHolderType.VIEW_TYPE_MENU_SLD_WINE;
        } else if("PRD_PMO_LIST_WINE".equals(viewType)) {
            // 와인 매장 PMO 스타일 리스트
            type = ViewHolderType.VIEW_TYPE_PRD_PMO_LIST_WINE;
        } else if("PRD_C_SQ_LIST_WINE".equals(viewType)) {
            // 와인 매장 C_SQ 스타일 리스트
            type = ViewHolderType.VIEW_TYPE_PRD_C_SQ_LIST_WINE;
        } else if("BAN_TXT_EXP_WINE".equals(viewType)) {
            // 와인 매장 금주의 인기 상품
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_EXP_WINE;
        } else if("TAB_ANCH_WINE".equals(viewType)) {
            // 와인 매장 앵커 탭
            type = ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE;
        } else if("PRD_2_WINE".equals(viewType)) {
            // 와인 매장 2단 뷰
            type = ViewHolderType.VIEW_TYPE_PRD_2_WINE;
        } else if("BAN_VIEW_MORE_WINE".equals(viewType)) {
            // 와인 매장 더보기
            type = ViewHolderType.VIEW_TYPE_BAN_VIEW_MORE_WINE;
        } else {
            type = super.getFlexibleViewType(viewType);
        }

        return type;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            if (isVisibleToUser) {
                // gs fresh 매장이 보일때마다 이전에 타이핑한 검색창의 텍스트를 지운다.
                clearSearchKeyword();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        clearSearchKeyword();
    }

    @Override
    public void onStop() {
        super.onStop();
        mKeyboardHeightProvider.close();
        hideSoftInput(Objects.requireNonNull(getActivity()));
    }

    private void clearSearchKeyword() {
        try {
            ((WineShopAdapter)mAdapter).mMenuSldVH.cleanSearchBarText(Objects.requireNonNull(getActivity()));
        }
        catch (NullPointerException | ClassCastException e) {
            Ln.e(e.getMessage());
        }
    }


    /***************************  Events  ***************************/

    /**
     * 섹션 클릭시 해당 포지션으로 이동한다.
     *
     * @param event GSSuperSectionClickEvent
     */
    public void onEvent(Events.FlexibleEvent.EventWine.SectionClickEvent event) {
        if (isNotEmpty(tabPrdMapWine.get(event.tabSeq))) {
            header.setVisibility(View.VISIBLE);
            int pos = tabPrdMapWine.get(event.tabSeq);
            mLayoutManager.scrollToPositionWithOffset(pos, 0);
        }
    }

    /**
     * 매장 스크롤중 카테고리 선택시 매장스크롤을 중지한다. (정확한 위치이동 위해)
     *
     * @param event GSSuperStopScrollEvent
     */
    public void onEvent(Events.FlexibleEvent.EventWine.StopScrollEvent event) {
        mRecyclerView.stopScroll();
    }
}
