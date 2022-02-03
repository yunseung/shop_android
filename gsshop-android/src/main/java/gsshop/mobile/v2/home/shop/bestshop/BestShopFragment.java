/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gsshop.mocha.ui.util.ViewUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment;
import gsshop.mobile.v2.library.quickreturn.library.QuickReturnUtils;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static gsshop.mobile.v2.home.shop.ViewHolderType.BAN_CX_SLD_CATE_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_TXT_CHK_GBA;

/**
 *
 *
 */
public class BestShopFragment extends FlexibleShopFragment {
    private static final int DURATION = 400;
    /**
     * category view
     */
    private int categoryViewPosition = -1;
    @InjectView(R.id.view_best_shop_category)
    private View categoryView;

    @InjectView(R.id.view_dim_category)
    private View categoryDimView;

    @InjectView(R.id.view_dim_click_area)
    private View categoryDimClickView;


    /**
     * category title view
     */
    @InjectView(R.id.view_category_title)
    private View categoryTitleView;

    @InjectView(R.id.text_category_title)
    private TextView categoryTitleText;

    @InjectView(R.id.check_category_expand)
    private CheckBox categoryExpandCheck;

    /**
     * category list view
     */
    @InjectView(R.id.view_category_list)
    private ViewGroup categoryListView;
    private int minCategoryListViewTranslation;

    /**
     * gs x brand category view
     */
    @InjectView(R.id.frame_gs_x_brand_category)
    private View gsXBrandCategoryFrame;

    @InjectView(R.id.view_gs_x_brand_category)
    private View gsXBrandCategoryView;
    public BanCxSldCateGbaVH gsXBrandCategoryHolder;
    private int gsXBrandCategoryPosition = -1;

    // footer
    private View mFooter;
    private int footerTranslation;
    private AnimatorSet animSet;
    private int categoryTitleViewHeight;
    private ShopInfo oldShopInfo = null;

    private ShopInfo.ShopItem oldShopItemCheckBox = null;

    private String mNavigationId = null;

    public static BestShopFragment newInstance(int position) {
        BestShopFragment fragment = new BestShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public BestShopFragment() {
        super();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewUtils.hideViews(categoryDimView);
//        position은 static으로 선언되지 않고 각자 가지고 있어야 한다.
//        BanCxSldCateGbaVH.selectedPosition = 0;

        mAdapter = new BestShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        categoryDimClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryExpandCheck.setChecked(false);
            }
        });

        categoryTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryExpandCheck.setChecked(categoryExpandCheck.isChecked() == false);
            }
        });

        categoryExpandCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showCategoryTable(true);
                } else {
                    showCategoryTable(false);

                }
            }
        });

        scrollListener.registerExtraOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (categoryViewPosition < 0) {
                    if (mAdapter != null && mAdapter.getInfo() != null) {
                        List<ShopInfo.ShopItem> contents = mAdapter.getInfo().contents;
                        for (int i = 0; i < contents.size(); i++) {
                            if (contents.get(i).type == ViewHolderType.VIEW_TYPE_FPC_S) {
                                categoryViewPosition = i;
                            }
                        }
                    }
                }

                int position = mLayoutManager.findFirstVisibleItemPositions(null)[0];

                /**
                 * gs x brand category
                 */
                if (gsXBrandCategoryPosition > -1) {
                    if (position >= gsXBrandCategoryPosition) {
                        if (gsXBrandCategoryFrame.getVisibility() != View.VISIBLE) {
                            ViewUtils.showViews(gsXBrandCategoryFrame);
                            gsXBrandCategoryHolder.updateList(mNavigationId);
                        }
                    } else {
                        if (gsXBrandCategoryFrame.getVisibility() == View.VISIBLE) {
                            ViewUtils.hideViews(gsXBrandCategoryFrame);
                        }
                    }
                }


                /**
                 * category view
                 */
                if (categoryViewPosition > -1) {

                    if (position == categoryViewPosition) {
                        BaseViewHolder vh = (BaseViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        if (vh.itemView.getBottom() <= categoryTitleViewHeight) {
                            // show
                            if (categoryView.getVisibility() == View.GONE) {
                                ViewUtils.showViews(categoryView);
                            }
                        } else {
                            //hide
                            if (categoryView.getVisibility() == View.VISIBLE) {
                                ViewUtils.hideViews(categoryView);
                            }
                        }
                    } else if (position > categoryViewPosition) {
                        // show
                        if (categoryView.getVisibility() == View.GONE) {
                            ViewUtils.showViews(categoryView);
                        }
                    }
                    // TODO: 2016. 10. 6.
                    // 해당 조건은 항상 true 이다. 그런데 고치지 않겠다 10/05 ( 문제가 되면 그떄 지우자 ) 이민수
                    else if (position < categoryViewPosition) {
                        //hide
                        if (categoryView.getVisibility() == View.VISIBLE) {
                            ViewUtils.hideViews(categoryView);
                        }
                    }
                }

            }
        });

        // top 버튼 override - 카테고리 뷰 숨기기 기능 추가.
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

                //hide
                if (categoryView.getVisibility() == View.VISIBLE) {
                    ViewUtils.hideViews(categoryView);
                }
                /*
                // GSX 브랜드 카테고리를 숨겼다가 다시 보이는 과정에서 Recycle 위치가 변경되는 문제 발생, hide 할 이유가 없기 때문에 해당 부분 주석 처리
                if (gsXBrandCategoryFrame.getVisibility() == View.VISIBLE) {
                    ViewUtils.hideViews(gsXBrandCategoryFrame);
                }
                */
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFooter = mCoordinator.getFooterLayout();
        // hide bottom footer
        int footerHeight = getResources().getDimensionPixelSize(R.dimen.main_footer_height);
        int indicatorHeight = QuickReturnUtils.dp2px(activity, 4);
        footerTranslation = (-footerHeight + indicatorHeight) * -1;

        categoryTitleViewHeight = getResources().getDimensionPixelSize(R.dimen.best_shop_category_title_view_height);
    }

    @Override
    protected void onSwipeRefrehsing() {
        isNowSwipeRefreshing = true;

        if (gsXBrandCategoryHolder != null) {
            try {
                ShopInfo.ShopItem shopItem = (ShopInfo.ShopItem) mAdapter.getItem(gsXBrandCategoryPosition);
                final List<SectionContentList> items = shopItem.sectionContent.subProductList;

                gsXBrandCategoryHolder.setListPosition(mAdapter.getInfo().sectionList.navigationId, 0, items.get(0), true);
            }
            catch (NullPointerException | IndexOutOfBoundsException e) {
                mLayoutSwipe.setRefreshing(false);
                isNowSwipeRefreshing = false;
                Ln.e(e.getMessage());
            }
        }
        else {
            super.onSwipeRefrehsing();
        }
    }

    @Override
    protected int getFlexibleViewType(String viewType) {
        int type = super.getFlexibleViewType(viewType);
        if (type == ViewHolderType.BANNER_TYPE_NONE) {
            if ("VIEW_TYPE_B_TS2".equals(viewType)) {
                // 텍스트 + 기준 시간
                type = ViewHolderType.VIEW_TYPE_B_TS2;
            } else if ("VIEW_TYPE_FPC_S".equals(viewType)) {
                // 플로팅 셀타입 카테고리
                type = ViewHolderType.VIEW_TYPE_FPC_S;
            } else if ("BFP".equals(viewType)) {
                // 단품 2개
                type = ViewHolderType.VIEW_TYPE_BFP;
            } else if ("VIEW_TYPE_BAN_IMG_C2_GBB".equals(viewType)) {
                // 단품 2개 new
                type = ViewHolderType.VIEW_TYPE_BAN_IMG_C2_GBB;
            } else if ("VIEW_TYPE_RPS".equals(viewType)) {
                // 실시간 인기 검색어
                type = ViewHolderType.VIEW_TYPE_RPS;
            } else if ("BP_O".equals(viewType)) {
                // 기획전 2 - 주차별 테마에 따른 기획전 (배너 + 상품1개 조합)
                type = ViewHolderType.VIEW_TYPE_BP_O;
            } else if ("TP_S".equals(viewType)) {
                // 모바일 전용 기획전
                type = ViewHolderType.VIEW_TYPE_TP_S;
            } else if ("TP_SA".equals(viewType)) {
                // 플렉서블 E 모바일 전용 기획전
                type = ViewHolderType.VIEW_TYPE_TP_SA;
            } else if ("B_IT".equals(viewType)) {
                // 플렉서블 E
                type = ViewHolderType.VIEW_TYPE_B_IT;
            } else if ("MAP_CX_GBA".equals(viewType)) {
                // gs x brand 배너
                type = ViewHolderType.VIEW_TYPE_MAP_CX_GBA;
            } else if ("BAN_CX_SLD_CATE_GBA".equals(viewType)) {
                // gs x brand 배너
                type = BAN_CX_SLD_CATE_GBA;
            } else if ("BAN_TXT_CHK_GBA".equals(viewType)) {
                type = VIEW_TYPE_BAN_TXT_CHK_GBA;
            } else if ("GR_PMO_T2_MORE".equals(viewType)) {
                type = ViewHolderType.VIEW_TYPE_GR_PMO_T2_MORE;
            } else if ("PMO_T2_A".equals(viewType)) {
                type = ViewHolderType.VIEW_TYPE_PMO_T2_A;
            }
        }

        return type;
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int listPosition) {
        super.updateList(sectionList, contentsListInfo, tab, listPosition);

        if (mAdapter.getInfo() == null) {
            return;
        }
        List<ShopInfo.ShopItem> contents = mAdapter.getInfo().contents;
        if (contents == null || contents.isEmpty()) {
            return;
        }

        mNavigationId = sectionList.navigationId;

        // init gs x brand category
        if(oldShopInfo != null && gsXBrandCategoryPosition >= 0) {
            for(int i=0; i <=gsXBrandCategoryPosition; i++) {
                // 카테고리가 동일하게 두개 연속 추가되는 경우 막기 위함
                if (contents.get(i).type == BAN_CX_SLD_CATE_GBA && oldShopInfo.contents.get(i).type == BAN_CX_SLD_CATE_GBA) {
                    continue;
                }

                contents.add(i, oldShopInfo.contents.get(i));
            }
        } else {
            for (int position = 0; position < contents.size(); position++) {
                if (contents.get(position).type == BAN_CX_SLD_CATE_GBA) {
                    this.gsXBrandCategoryPosition = position;
                    this.gsXBrandCategoryHolder = new BanCxSldCateGbaVH(gsXBrandCategoryView);
                    //탭명을 이름을 넘김

                    BanCxSldCateGbaVH.mMapSelectedPosition.put(mAdapter.getInfo().sectionList.navigationId, 0);
                    gsXBrandCategoryHolder.onBindViewHolder(getContext(), position, mAdapter.getInfo(), GTMEnum.GTM_NONE, GTMEnum.GTM_NONE, sectionList.sectionName);

                    break;
                }
            }
        }

        if(gsXBrandCategoryPosition == 0) {
            ViewUtils.showViews(gsXBrandCategoryFrame);
            gsXBrandCategoryHolder.updateList(mNavigationId);

            if (ShopInfo.NAVIGATION_BEST_NOW.equals(sectionList.navigationId)) {
                if (gsXBrandCategoryHolder.getSelectedPosition(mNavigationId) == 0) {
                    boolean isCheckExists = false;
                    // VIEW_TYPE_BAN_TXT_CHK_GBA 타입이 존재하면 추가 할 필요가 없음
                    for (int position = 0; position < contents.size(); position++) {
                        if (contents.get(position).type == VIEW_TYPE_BAN_TXT_CHK_GBA) {
                            oldShopItemCheckBox = contents.get(position);
                            isCheckExists = true;
                            break;
                        }
                    }
                    // VIEW_TYPE_BAN_TXT_CHK_GBA 타입이 존재 하지 않는다면 BAN_CX_SLD_CATE_GBA 배너 다음 배너로 CheckBox 배너 삽입
                    if (!isCheckExists) {
                        for (int position = 0; position < contents.size(); position++) {
                            if (contents.get(position).type == BAN_CX_SLD_CATE_GBA) {
                                // 기존 체크박스 정보 존재시에 설정.
                                if (oldShopItemCheckBox != null) {
                                    // isAllView 값이 변경되면 안되기 때문에 해당 값은 빈칸으로 변경{
                                    oldShopItemCheckBox.sectionContent.linkUrl =
                                            StringUtils.replaceUriParameter(
                                                    Uri.parse(oldShopItemCheckBox.sectionContent.linkUrl),
                                                    WebUtils.BEST_NOW_CHK_IS_ALL_VIEW_PARAM_KEY, "").toString();
                                    contents.add(position + 1, oldShopItemCheckBox);
                                }
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }

        } else {
            ViewUtils.hideViews(gsXBrandCategoryFrame);
        }

        // init category view
        ViewUtils.hideViews(categoryView, categoryDimView);
        categoryExpandCheck.setChecked(false);

        for (int i = 0; i < contents.size(); i++) {
            ShopInfo.ShopItem item = contents.get(i);
            if (item.type == ViewHolderType.VIEW_TYPE_FPC_S) {

                categoryTitleText.setText(item.sectionContent.promotionName);

                /**
                 * category list
                 */
                if (categoryListView.getChildCount() > 0) {
                    categoryListView.removeAllViews();
                }
                final View category = LayoutInflater.from(getActivity()).inflate(R.layout.home_row_type_fx_best_shop_category_banner_new, null);
                BaseViewHolder vh = new BestShopFpcSVH(category);
                vh.onBindViewHolder(getActivity(), i, mAdapter.getInfo(), null, null, null);
                categoryListView.addView(category);

                // get height of category table
                category.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    @SuppressWarnings("deprecation")
                    public void onGlobalLayout() {

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            category.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            category.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        minCategoryListViewTranslation = category.getHeight() * -1;

                        ObjectAnimator.ofFloat(categoryListView, "translationY", ViewHelper.getTranslationX(categoryListView), minCategoryListViewTranslation).start();

                    }

                });

            }
        }

    }


    public void showCategoryTable(boolean show) {
        if (show) {
            // show category table
            if (animSet != null && animSet.isRunning()) {
                animSet.cancel();
            }
            ViewUtils.showViews(categoryDimView);
            animSet = new AnimatorSet();
            animSet.playTogether(
                    ObjectAnimator.ofFloat(categoryDimView, "alpha", 0f, 1f),
                    ObjectAnimator.ofFloat(categoryListView, "translationY", ViewHelper.getTranslationY(categoryListView), 0)

            );
            animSet.setDuration(DURATION);
            animSet.start();
        } else {
            // hide category table
            if (animSet != null && animSet.isRunning()) {
                animSet.cancel();
            }
            animSet = new AnimatorSet();
            animSet.playTogether(
                    ObjectAnimator.ofFloat(categoryDimView, "alpha", 1f, 0f),
                    ObjectAnimator.ofFloat(categoryListView, "translationY", ViewHelper.getTranslationY(categoryListView), minCategoryListViewTranslation)
            );
            animSet.setDuration(DURATION);
            animSet.start();
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ViewUtils.hideViews(categoryDimView);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }

    /**
     * event bus
     *
     * @param event event
     */
    public void onEventMainThread(Events.FlexibleEvent.UpdateBestShopEvent event) {
        if (getUserVisibleHint()) {
            this.oldShopInfo = mAdapter.getInfo();
            new UrlUpdateController(getActivity()).execute(event.url, event.isCache, event.isSwipeRefresh);
        }
    }

    /**
     * GS X Brand 탭 설정 이벤트
     * @param event
     */
    public void onEvent(Events.FlexibleEvent.MoveSubGroupCdEvent event) {

        if( event == null || event.mGroupCd == null ) return;

        if (gsXBrandCategoryHolder != null && gsXBrandCategoryPosition >= 0) {
            try {
                ShopInfo.ShopItem shopItem = (ShopInfo.ShopItem) mAdapter.getItem(gsXBrandCategoryPosition);
                final List<SectionContentList> items = shopItem.sectionContent.subProductList;

                for (int i=0; i < items.size(); i++) {
                    if ( event.mGroupCd.equalsIgnoreCase(items.get(i).dealNo) ) {
                        gsXBrandCategoryHolder.setListPosition(mAdapter.getInfo().sectionList.navigationId, i, items.get(i));
                        break;
                    }
                }
            }
            catch (NullPointerException e){
                Ln.e(e.getMessage());
            }
        }
    }
}
