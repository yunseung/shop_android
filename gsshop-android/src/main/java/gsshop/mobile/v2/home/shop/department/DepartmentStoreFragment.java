/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.department;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
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
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 *
 *
 */
public class DepartmentStoreFragment extends FlexibleShopFragment {
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

    @InjectView(R.id.text_category_icon)
    private ImageView categoryIcon;


    @InjectView(R.id.check_category_expand)
    private CheckBox categoryExpandCheck;

    /**
     * category list view
     */
    @InjectView(R.id.view_category_list)
    private ViewGroup categoryListView;
    private int minCategoryListViewTranslation;

    private int categoryIndex = 0;

    // footer
    private View mFooter;
    private int footerTranslation;
    private AnimatorSet animSet;
    private int categoryTitleViewHeight;

    private  ArrayList<ShopInfo.ShopItem> categorylItem = new ArrayList<ShopInfo.ShopItem>();

    private int categoryReflashItemRangeStart;

    private FpcSVH categotyView;

    private boolean isFooterViewAdded = false;

    private boolean isFlexibleCategory = false;

    public static DepartmentStoreFragment newInstance(int position) {
        DepartmentStoreFragment fragment = new DepartmentStoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public DepartmentStoreFragment() {
        super();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewUtils.hideViews(categoryDimView);
        ViewUtils.hideViews(categoryIcon);

        categoryTitleText.setPadding(getActivity().getResources().getDimensionPixelSize(R.dimen.department_store_title_left_space),0,0,0);
        mAdapter = new DepartmentStoreAdapter(getActivity());
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
                // FlexshopFragment에서 popupWindow 체크, 해당 부분에 필요하지 않음.
//                EventBus.getDefault().post(new Events.FlexibleEvent.AdTooltipEvent(null,false));
                if (categoryViewPosition < 0) {
                    if (mAdapter != null && mAdapter.getInfo() != null) {
                        List<ShopInfo.ShopItem> contents = mAdapter.getInfo().contents;
                        for (int i = 0; i < contents.size(); i++) {
                            if (contents.get(i).type == ViewHolderType.VIEW_TYPE_FPC_S || contents.get(i).type == ViewHolderType.VIEW_TYPE_FPC_P) {
                                categoryViewPosition = i;
                            }
                        }
                    }
                }
                if (categoryViewPosition < 0) {
                    return;
                }

                int position = mLayoutManager.findFirstVisibleItemPositions(null)[0];

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
        });

        // top 버튼 override - 카테고리 뷰 숨기기 기능 추가.
        btnTop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mRecyclerView.stopScroll();
                ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(0);
                btnTop.setVisibility(View.GONE);

                //탑버튼 클릭시 해더영역 노출
                ((HomeActivity) getContext()).expandHeader();

                ViewHelper.setTranslationY(btnTop, 0);

                ViewHelper.setTranslationY(mCoordinator.getFooterLayout(), 0);

                //CSP BUTTON MSLEE
                ViewHelper.setTranslationY(mCoordinator.getCspLayout(), 0);

//                //hide
                if (categoryView.getVisibility() == View.VISIBLE) {
                    ViewUtils.hideViews(categoryView);
                }

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
    protected int getFlexibleViewType(String viewType) {
        int type = super.getFlexibleViewType(viewType);
        if (type == ViewHolderType.BANNER_TYPE_NONE) {
            if ("VIEW_TYPE_B_TS2".equals(viewType)) {
                // 텍스트 + 기준 시간
                type = ViewHolderType.VIEW_TYPE_B_TS2;
            } else if ("FPC".equals(viewType)) {
                // 플로팅 셀타입 카테고리
                type = ViewHolderType.VIEW_TYPE_FPC_S;
            } else if ("VIEW_TYPE_RPS".equals(viewType)) {
                // 실시간 인기 검색어
                type = ViewHolderType.VIEW_TYPE_RPS;
            } else if ("B_TSC".equals(viewType)) {
                // 실시간 인기 검색어
                type = ViewHolderType.VIEW_TYPE_B_TSC;
            } else if ("B_IT".equals(viewType)) {
                type = ViewHolderType.VIEW_TYPE_B_IT;
            }
        }

        return type;
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int listPosition) {
        //Ln.i("FlexibleShopFragment setInitUI");
        if ("WEB".equals(sectionList.sectionType)) {
            // ;
        } else {
            //10/19 품질팀 요청
            if (contentsListInfo != null && getActivity() != null) {

                ShopInfo info = new ShopInfo();

                info.contents = new ArrayList<ShopInfo.ShopItem>();
                isPageLoading = false;
                info.sectionList = sectionList;
                if(contentsListInfo.ajaxPageUrl != null && !"".equals(contentsListInfo.ajaxPageUrl)){
                    info.ajaxPageUrl = contentsListInfo.ajaxPageUrl;
                }else if(contentsListInfo.ajaxfullUrl != null && !"".equals(contentsListInfo.ajaxfullUrl)){
                    info.ajaxPageUrl = contentsListInfo.ajaxfullUrl;
                }
                // 배너.
                ShopInfo.ShopItem content;

                // 상단 배너. imageUrl로 배너 존재 여부 체크.
                if (contentsListInfo.banner != null
                        && DisplayUtils.isValidString(contentsListInfo.banner.imageUrl)) {
                    content = new ShopInfo.ShopItem();
                    content.type = ViewHolderType.BANNER_TYPE_BAND;
                    content.sectionContent = new SectionContentList();
                    content.sectionContent.imageUrl = contentsListInfo.banner.imageUrl;
                    content.sectionContent.linkUrl = contentsListInfo.banner.linkUrl;
                    content.sectionContent.viewType = "B_PZ";
                    info.contents.add(content);
                }

                // 이벤트 메뉴. 헤더.
                if (contentsListInfo.headerList != null && !contentsListInfo.headerList.isEmpty()) {
                    for (SectionContentList header : contentsListInfo.headerList) {
                        int type = getFlexibleViewType(header.viewType);
                        if (type != ViewHolderType.BANNER_TYPE_NONE) {
                            content = new ShopInfo.ShopItem();
                            content.type = type;
                            content.sectionContent = header;
                            if(content.sectionContent != null) {
                                info.contents.add(content);
                            }
                        } else {
                            if ("SE".equals(header.viewType)) {
                                // 슬라이드 이벤트 뷰타입 설정
                                if (header.subProductList != null && !header.subProductList.isEmpty()) {
                                    content = new ShopInfo.ShopItem();
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

                }

                // 카테고리.
                if (sectionList.subMenuList!= null && !sectionList.subMenuList.isEmpty()) {
                    info.tabIndex = 0;
                    if (sectionList.viewType.equals(ShopInfo.TYPE_FLEXIBLE)) {
                        // 플렉서블 매장.
                        content = new ShopInfo.ShopItem();
                        if ("SUB_PRD_LIST_TEXT".equals(sectionList.subMenuList.get(0).viewType.trim())) {
                            content.type = ViewHolderType.VIEW_TYPE_SUB_PRD_LIST_TEXT;
                        } else {
                            content.type = ViewHolderType.VIEW_TYPE_FXCLIST;
                        }

                        info.tabIndex = tab;
                        info.contents.add(content);
                    }
                }

                // 베스트딜 탭 TV생방송영역 AB Test (TV쇼핑 탭에는 항상 노출)
                // 베스트딜 && 매크로값 B가 아닌 경우 노출, 베스트딜 && 매크로값 B인 경우 미노출
                if (contentsListInfo.tvLiveBanner != null
                        && sectionList.viewType.equals(ShopInfo.TYPE_BESTDEAL)) {
                    // tv live header view 생성

                    content = new ShopInfo.ShopItem();
                    content.type = ViewHolderType.BANNER_TYPE_TV_LIVE;
                    info.tvLiveBanner = contentsListInfo.tvLiveBanner;
                    info.tvLiveBanner.tvLiveUrl = getHomeGroupInfo().appUseUrl.tvLiveUrl;
                    info.contents.add(content);
                }

                //브랜드딜 예외처리 get(0) 에 대한 예외처리 필요 없는 로직
                if (sectionList.viewType.equals(ShopInfo.TYPE_BRAND) && contentsListInfo.brandBanner != null) {
                    content = new ShopInfo.ShopItem();
                    if (contentsListInfo.brandBanner.size() > 1) {
                        content.type = ViewHolderType.BANNER_TYPE_BRAND_ROLL_IMAGE;

                        SectionContentList brandList = new SectionContentList();
                        brandList.subProductList = new ArrayList<SectionContentList>();
                        brandList.subProductList.addAll(contentsListInfo.brandBanner);

                        content.sectionContent = brandList;
                        if(content.sectionContent != null) {
                            info.contents.add(content);
                        }
                    } else if (contentsListInfo.brandBanner.size() == 1) {
                        // 슬라이드 이미지 갯수가 1개이면 이미지 배너에 넣음.
                        content.type = ViewHolderType.BANNER_TYPE_IMAGE;
                        content.sectionContent = contentsListInfo.brandBanner.get(0);
                        if(content.sectionContent != null) {
                            info.contents.add(content);
                        }
                    }
                }


                for (int i = 0; i < contentsListInfo.productList.size(); i++) {
                    SectionContentList c = contentsListInfo.productList.get(i);
                    int type = getFlexibleViewType(c.viewType);

                    if (type != ViewHolderType.BANNER_TYPE_NONE) {

                        content = new ShopInfo.ShopItem();
                        content.type = type;
                        content.sectionContent = c;

                        try {
                            // 슬라이드 이미지 갯수가 1개이면 이미지 배너에 넣음.
                            if ("B_SIC".equals(c.viewType) && c.subProductList != null && c.subProductList.size() < 1) {
                                content.type = ViewHolderType.BANNER_TYPE_NONE;
                            }else if ("B_SIC".equals(c.viewType) && c.subProductList != null && c.subProductList.size() == 1) {
                                content.sectionContent = c.subProductList.get(0);
                                content.type = ViewHolderType.BANNER_TYPE_IMAGE;
                            }
                        }catch(Exception e){
                            Ln.e(e);
                        }


                        if (sectionList.viewType.equals(ShopInfo.TYPE_FLEXIBLE)) {
                            if (type == ViewHolderType.VIEW_TYPE_B_IG4XN) {
                                if (content.sectionContent.imageUrl == null || "".equals(content.sectionContent.imageUrl)) {
                                    content.type = ViewHolderType.BANNER_TYPE_NONE;
                                }
                            }
                        }

                        if(content.sectionContent != null && content.type != ViewHolderType.BANNER_TYPE_NONE) {
                            info.contents.add(content);
                        }
                        if (content.type == ViewHolderType.VIEW_TYPE_FPC_S || content.type == ViewHolderType.VIEW_TYPE_FPC_P) {
                            if(content.type == ViewHolderType.VIEW_TYPE_FPC_P){
                                isFlexibleCategory = true;
                            }


                            if(content.sectionContent.subProductList != null && !content.sectionContent.subProductList.isEmpty()){
                                categoryTitleText.setText(content.sectionContent.subProductList.get(0).promotionName);
                                //새로고침 시작 시점을 기억하기 위해 지정
                                categoryReflashItemRangeStart =  info.contents.size();
                                categorylItem.clear();

                                for (int j = categoryReflashItemRangeStart ; j < contentsListInfo.productList.size(); j++) {
                                    SectionContentList subContent = contentsListInfo.productList.get(j);
                                    int subType = getFlexibleViewType(subContent.viewType);
                                    if (subType != ViewHolderType.BANNER_TYPE_NONE) {
                                        content = new ShopInfo.ShopItem();
                                        content.type = subType;
                                        content.sectionContent = subContent;
                                        categorylItem.add(content);
                                        info.contents.add(content);
                                    }
                                }

                                // 마지막 풋터 지정.
                                content = new ShopInfo.ShopItem();
                                content.type = ViewHolderType.BANNER_TYPE_FOOTER;
                                categorylItem.add(content);
                                info.contents.add(content);
                                isFooterViewAdded = true;
                            }

                            /**
                             * category list
                             */
                            if (categoryListView.getChildCount() > 0) {
                                categoryListView.removeAllViews();
                            }
                            final View category = LayoutInflater.from(getActivity()).inflate(R.layout.home_row_type_fx_best_shop_category_banner, null);
                            //카테고리 틀고정 들어 갔을때에 배경 예외처리 : 최초 호출ffffff
//                            category.setBackgroundColor(Color.parseColor("#ffffff"));
                            categotyView = new FpcSVH(category);
                            categotyView.onBindViewHolder(getActivity(), categoryReflashItemRangeStart -1 , info, null, null, null);
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
                            break;
                        }

                    } else {
                        if ("B_SIS".equals(c.viewType)) {
                            // "슬라이드 이미지배너" = B_SIS (subProductList -> imageUrl, prdUrl)
                            content = new ShopInfo.ShopItem();
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

                if(!isFooterViewAdded) {
                    // 마지막 풋터 지정.
                    content = new ShopInfo.ShopItem();
                    content.type = ViewHolderType.BANNER_TYPE_FOOTER;
                    categorylItem.add(content);
                    info.contents.add(content);

                }
                mAdapter.setInfo(info);
                mAdapter.notifyDataSetChanged();
                ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(0);

            }
        }

        // init category view
        ViewUtils.hideViews(categoryView, categoryDimView);
        categoryExpandCheck.setChecked(false);

    }


    public void showCategoryTable(boolean show) {
        if(show) {
            mRecyclerView.stopScroll();
            // show category table
            if (animSet != null && animSet.isRunning()) {
                animSet.cancel();
            }
            ViewUtils.showViews(categoryDimView);
            animSet = new AnimatorSet();
            animSet.playTogether(
                    ObjectAnimator.ofFloat(categoryDimView, "alpha", 0f, 1f),
                    ObjectAnimator.ofFloat(categoryListView, "translationY",  ViewHelper.getTranslationY(categoryListView), 0)

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
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    public void drawFragment() {

        // setSwipeRefresh();
        // TV생방송 존재 여부에 상관없이 전체 data loading
        new EventShopController(getActivity(), tempSection, true).execute();
    }

    /**
     * event bus
     * @param event event
     */
    public void onEventMainThread(Events.FlexibleEvent.UpdateBestShopEvent event) {
        if (getUserVisibleHint()) {
            categoryIndex = event.tab;
            categotyView.setIndex(event.tab);

            if(isFlexibleCategory){
                ShopInfo.ShopItem content = null;
                for(int i = 0 ; i <mAdapter.getInfo().contents.size() ; i ++){
                    if(ViewHolderType.VIEW_TYPE_FPC_P == mAdapter.getInfo().contents.get(i).type){
                        content = mAdapter.getInfo().contents.get(i);
                        content.sectionContent.index = event.tab;
                        break;
                    }
                }
                mAdapter.getInfo().contents.clear();
                mAdapter.getInfo().contents.add(content);

                if(mRecyclerView != null) {
                    for (int j = 0; j < mRecyclerView.getChildCount(); j++) {
                        View view = mRecyclerView.getChildAt(j);
                        BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                        if (vh.getItemViewType() == ViewHolderType.VIEW_TYPE_FPC_P) {
                            DepartmentFpcPVH categoryViewHolder = (DepartmentFpcPVH)vh;
                            categoryViewHolder.setCategory(event.tab);
                            break;
                        }
                    }
                }
            }


            new UrlUpdateController(getActivity()).execute(event.url, false, event.tabName);
        }
    }

    public class UrlUpdateController extends BaseAsyncController<ContentsListInfo> {

        @Inject
        private RestClient restClient;

        private String url;
        private boolean isCacheData;
        private String tabName;
        private Context context;

        public UrlUpdateController(Context activityContext) {
            super(activityContext);
            this.context = activityContext;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            url = (String) params[0];
            isCacheData = (boolean) params[1];
            tabName = (String) params[2];
            isPageLoading = true;
            if (dialog != null) {
                dialog.setCancelable(false);
            }
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.


            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, url + "&reorder=true", null);
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            mRefreshView.setVisibility(View.GONE);
            categoryExpandCheck.setChecked(false);
            isPageLoading = false;
            // 현재 액티비티 활성화 상태 체크
            if (getActivity() != null && listInfo != null && listInfo.productList != null) {
                List<ShopInfo.ShopItem> contents = mAdapter.getInfo().contents;
                if (contents == null || contents.isEmpty()) {
                    return;
                }
                mAdapter.getInfo().ajaxPageUrl = listInfo.ajaxfullUrl;
                if(listInfo.ajaxPageUrl != null && !"".equals(listInfo.ajaxPageUrl)){
                    mAdapter.getInfo().ajaxPageUrl = listInfo.ajaxPageUrl;
                }else if(listInfo.ajaxfullUrl != null && !"".equals(listInfo.ajaxfullUrl)){
                    mAdapter.getInfo().ajaxPageUrl = listInfo.ajaxfullUrl;
                }
                try{
                    removeCategoryItem(mAdapter.getInfo());
                    categorylItem.clear();
                    ShopInfo.ShopItem subContents;
                    categoryTitleText.setText(tabName);
                    categotyView.setCategory(getContext(), categoryIndex);


//                    if(listInfo.productList == null || listInfo.productList.size() == 0) {
//                        subContents = new ShopInfo.ShopItem();
//                        SectionContentList emptyItem = new SectionContentList();
//                        emptyItem.productName = tabName;
//                        subContents.type = ViewHolderType.VIEW_TYPE_BAN_TXT_NODATA;
//                        subContents.sectionContent = emptyItem;
//                        categorylItem.add(subContents);
//                    }


                    for(SectionContentList item : listInfo.productList){
                        subContents = new ShopInfo.ShopItem();
                        subContents.type = getFlexibleViewType(item.viewType);
                        subContents.sectionContent = item;
                        mAdapter.getInfo().tabIndex = categoryIndex;
                        if(subContents.type == ViewHolderType.VIEW_TYPE_FPC_S) {
                            categoryTitleText.setText(tabName);

                            if (categoryListView.getChildCount() > 0) {
                                categoryListView.removeAllViews();
                            }
                            final View category = LayoutInflater.from(getActivity()).inflate(R.layout.home_row_type_fx_best_shop_category_banner, null);
                            //카테고리 틀고정 들어 갔을때에 배경 예외처리 : 이놈은 최초 이후 다시 할때
//                            category.setBackgroundColor(Color.parseColor("#ffffff"));
                            categotyView = new FpcSVH(category);
                            categotyView.onBindViewHolder(getActivity(), categoryReflashItemRangeStart -1 , mAdapter.getInfo(), null, null, null);
                            categoryListView.addView(category);
                            categoryExpandCheck.setChecked(false);


                        }else{
                            categorylItem.add(subContents);
                        }
                    }

                    // 마지막 풋터 지정.
                    subContents = new ShopInfo.ShopItem();
                    subContents.type = ViewHolderType.BANNER_TYPE_FOOTER;
                    categorylItem.add(subContents);
                    int categoryPosition = categoryReflashItemRangeStart-2;
                    if(categoryPosition < 1){
                        categoryPosition = 0;
                    }
                    ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(categoryPosition, getActivity().getResources().getDimensionPixelSize(R.dimen.department_store_title_left_space));
                    ViewUtils.hideViews(categoryView);

                    addCategoryItem(mAdapter.getInfo());

                    if(categorylItem != null && categorylItem.size() > categoryReflashItemRangeStart) {
                        mAdapter.notifyItemRangeChanged(categoryReflashItemRangeStart-1, contents.size());
                    }else{
                        mAdapter.notifyDataSetChanged();
                    }

                    showCategoryTable(false);
                }catch(Exception e){
                    Ln.e(e);
                }
            }
            // 장바구니 카운트를 업데이트 한다.
            mActivity.setBasketcnt();

        }

        /**
         * 카테고리 아이템을 추가시킨다.
         * @param info info
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
        private void addCategoryItem(ShopInfo info) {
            if (info.contents != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    info.contents.addAll(categorylItem);
                } else {
                    for(ShopInfo.ShopItem item : categorylItem) {
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
                    info.contents.removeAll(categorylItem);
                } else {
                    for(ShopInfo.ShopItem item : categorylItem) {
                        info.contents.remove(item);
                    }
                }
            }
        }

        @Override
        protected void onError(Throwable e) {
            if (mRecyclerView != null && mAdapter.getItemCount() > 0) {
                mRefreshView.setVisibility(View.GONE);
            } else {
                mRefreshView.setVisibility(View.VISIBLE);
            }

            // 네트워크 에러팝업 중복으로 뜨는 현상 개선 (에러팝업을 SetABTestController에서 띄우지 않고 본 컨트롤러에서 띄운다.)
            super.onError(e);
        }
    }

    /**
     * classes
     */

    /**
     * 섹션 업데이트
     */
    public class EventShopController extends GetUpdateController {


        protected EventShopController(Context activityContext, TopSectionList sectionList, boolean isCacheData) {
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


}
