/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.todayopen;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.GroupSortFillterInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment;
import gsshop.mobile.v2.home.shop.flexible.ListTopMenuSpaceDecoration;
import gsshop.mobile.v2.library.quickreturn.QuickReturnType;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

/**
 *
 *
 */
public class TodayOpenFragment extends FlexibleShopFragment {

    private View category_layout;
    /**
     * 카테고리 뷰
     */
    private LinearLayout category_view;

    private View all_layout;

    private View list_shadow;

    /**
     * 카테고리 이름(bold 처리를 위해 ArrayList<TextView> 로 텍스트뷰를 담아서 처리한다)
     */
    private ArrayList<TextView> categoryNameList;

    /**
     * 현재 지정되어있는 카테고리를 표시
     */
    private TextView currentCategoryTextView;

    /**
     * 현재 지정되어있는 카테고리 상품 갯수 표시
     */
    private TextView currentCategorysize;

    private  ArrayList<ArrayList<ShopInfo.ShopItem>> categorylItem = new ArrayList<ArrayList<ShopInfo.ShopItem>>();

    /**
     * 전체
     */
    private ImageView all_arrow;

    public static TodayOpenFragment newInstance(int position) {
        TodayOpenFragment fragment = new TodayOpenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

      public TodayOpenFragment() {
        super();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new FlexibleShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        category_layout = view.findViewById(R.id.category_layout);
        category_view = (LinearLayout) view.findViewById(R.id.category_view);
        all_layout = view.findViewById(R.id.all_layout);
        list_shadow = view.findViewById(R.id.list_shadow);
        currentCategoryTextView = (TextView) view.findViewById(R.id.currentCategoryTextView);
        currentCategorysize = (TextView) view.findViewById(R.id.currentCategorysize);
        all_arrow = (ImageView) view.findViewById(R.id.all_arrow);

        category_layout.setVisibility(View.VISIBLE);

        all_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list_shadow.getVisibility() == View.VISIBLE) {
                    closePanel();
                } else {
                    expandPanel();
                }

            }
        });

        list_shadow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closePanel();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new ListTopMenuSpaceDecoration(getResources().getDimensionPixelSize(R.dimen.category_layout)));

        mAdapter = new FlexibleShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        setQuickReturn();

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
    protected void setQuickReturn() {
        super.setQuickReturn();

        scrollListener.setHeader(category_layout);
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
                ViewHelper.setTranslationY(category_layout, 0);
                ViewHelper.setTranslationY(mCoordinator.getFooterLayout(), 0);
                //CSP BUTTON MSLEE
                ViewHelper.setTranslationY(mCoordinator.getCspLayout(), 0);

            }
        });
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
                            if ("SE".equals(header.viewType) && header.subProductList != null) {
                                // 슬라이드 이벤트 뷰타입 설정
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

                // 카테고리.
                if (!sectionList.subMenuList.isEmpty()) {
                    info.tabIndex = 0;
                    if (sectionList.viewType.equals(ShopInfo.TYPE_FLEXIBLE)) {
                        // 플렉서블 매장.
                        content = new ShopInfo.ShopItem();
                        content.type = ViewHolderType.VIEW_TYPE_FXCLIST;
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
                    info.contents.add(content);
                }


                //오늘오픈 필터 예외처리
                if (contentsListInfo.groupSortFillterInfo != null
                        && contentsListInfo.groupSortFillterInfo.filterInfo != null
                        && !contentsListInfo.groupSortFillterInfo.filterInfo.isEmpty()) {
                    initCreateView(contentsListInfo.groupSortFillterInfo);
                }

                for(int i = 0 ; i < contentsListInfo.productList.size() ; i++){
                    SectionContentList c = contentsListInfo.productList.get(i);
                    int type = getFlexibleViewType(c.viewType);
                    if (type != ViewHolderType.BANNER_TYPE_NONE) {
                        content = new ShopInfo.ShopItem();
//                        if(mAdapter.isFillter() && i == mAdapter.getFilterCount()){
//                            content.type = ViewHolderType.BANNER_TYPE_FOOTER;
//                        }else {
                            content.type = type;
//                        }
                        content.sectionContent = c;
                        if(content.sectionContent != null) {
                            info.contents.add(content);
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
                // 마지막 풋터 지정.
                content = new ShopInfo.ShopItem();
                content.type = ViewHolderType.BANNER_TYPE_FOOTER;
                info.contents.add(content);

                //카테고리별로 데이터를 따로 보관한다.
                //10/19 품질팀 요청
                if (contentsListInfo != null && contentsListInfo.groupSortFillterInfo != null
                        && contentsListInfo.groupSortFillterInfo.filterInfo != null
                        && !contentsListInfo.groupSortFillterInfo.filterInfo.isEmpty()) {
                    for (int i = 0; i < contentsListInfo.groupSortFillterInfo.filterInfo.size(); i++) {
                        ArrayList<ShopInfo.ShopItem> categoryContents = new ArrayList<ShopInfo.ShopItem>();
                        if (i == 0) {
                            //전체상품
                            categoryContents.addAll(info.contents);
                        } else {
                            //배너
                            if (contentsListInfo.banner != null
                                    && DisplayUtils.isValidString(contentsListInfo.banner.imageUrl)) {
                                content = new ShopInfo.ShopItem();
                                content.type = ViewHolderType.BANNER_TYPE_BAND;
                                content.sectionContent = new SectionContentList();
                                content.sectionContent.imageUrl = contentsListInfo.banner.imageUrl;
                                content.sectionContent.linkUrl = contentsListInfo.banner.linkUrl;
                                content.sectionContent.viewType = "B_PZ";
                                categoryContents.add(content);
                            }
                            //카테고리별 상품(cateGb)
                            for (int j = 0; j < info.contents.size(); j++) {
                                String[] categoryNo = contentsListInfo.groupSortFillterInfo.filterInfo.get(i).categoryNo;
                                for (String category : categoryNo) {
                                    if (info.contents.get(j).sectionContent != null
                                            && info.contents.get(j).sectionContent.cateGb != null && info.contents.get(j).sectionContent.cateGb.equals(category)) {
                                        categoryContents.add(info.contents.get(j));
                                    }
                                }
                            }
                            //하단 풋터
                            content = new ShopInfo.ShopItem();
                            content.type = ViewHolderType.BANNER_TYPE_FOOTER;
                            categoryContents.add(content);

                        }
                        categorylItem.add(categoryContents);
                    }
                }

                mAdapter.setInfo(info);
                mAdapter.notifyDataSetChanged();
                ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(0);

            }
        }


    }

    /**
     * 그룹필터뷰를 생성한다.
     */
    public void initCreateView(final GroupSortFillterInfo groupSortFillterInfo) {
        try {
            int itemSize = groupSortFillterInfo.filterInfo.size();
            category_view.removeAllViews();
            categoryNameList = new ArrayList<TextView>();

            if(itemSize > 0) {
                currentCategoryTextView.setText(groupSortFillterInfo.filterInfo.get(0).categoryName);

                //카테고리가 전체 이면서 갯수가 1일때(전체밖에 없을때) 그룹필터뷰를 숨긴다.
                //2.3에서 숨김처리가 작동하지 않아서 height를 0으로 처리함
                if (itemSize == 1 && "전체".equals(groupSortFillterInfo.filterInfo.get(0).categoryName)) {
                    ViewGroup.LayoutParams lp = category_layout.getLayoutParams();
                    lp.height = 0;
                    category_layout.requestLayout();
                }
            }

            //카테고리 갯수만큼 돌면서 그룹필터뷰에 아이템을 하나씩 추가한다.
            for (int i = 0; i < itemSize; i++) {
                final int count = i;
                View view = LayoutInflater.from(mActivity).inflate(R.layout.group_filter_item, null);

                View itemLeftLayout = view.findViewById(R.id.item_left_layout);
                View itemRightLayout = view.findViewById(R.id.item_right_layout);

                TextView item_left = (TextView) view.findViewById(R.id.item_left);
                TextView item_left_count = (TextView) view.findViewById(R.id.item_left_count);

                TextView item_right = (TextView) view.findViewById(R.id.item_right);
                TextView item_right_count = (TextView) view.findViewById(R.id.item_right_count);

                //카테고리 정보가 안넘어온경우 전체(00)로 처리한다.
                if (groupSortFillterInfo.filterInfo.get(i).categoryNo.length == 0) {
                    groupSortFillterInfo.filterInfo.get(i).categoryNo = new String[]{"00"};
                }

                //카테고리 갯수가 0이면 갯수를 노출하지 않는다.
                if ("0".equals(groupSortFillterInfo.filterInfo.get(i).totalCnt)) {
                    item_left_count.setVisibility(View.GONE);
                }

                //카테고리 이름
                item_left.setText(groupSortFillterInfo.filterInfo.get(i).categoryName);
                //카테고리 갯수
                item_left_count.setText(addBrackets(groupSortFillterInfo.filterInfo.get(i).totalCnt));
                categoryNameList.add(item_left);

                //뷰를 클릭시 해당 필터를 적용한다.
                itemLeftLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
//                        int totalCnt;
//                        try {
//                            totalCnt = Integer.parseInt(groupSortFillterInfo.filterInfo.get(count).totalCnt);
//                        } catch (NumberFormatException e) {
//                            Ln.e(e);
//                        }


                        closePanel();

                        //첫번째 아이템은 필터 적용안함(이민수)
                        mAdapter.getInfo().contents = categorylItem.get(count);


                        mAdapter.notifyDataSetChanged();
                        ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(0);

                        currentCategoryTextView.setText(groupSortFillterInfo.filterInfo.get(count).categoryName);
                        setTypeface(groupSortFillterInfo.filterInfo.get(count).categoryName);

                    }
                });

                if (i < itemSize - 1) {
                    item_right.setText(groupSortFillterInfo.filterInfo.get(i + 1).categoryName);
                    item_right_count.setText(addBrackets(groupSortFillterInfo.filterInfo.get(i + 1).totalCnt));
                    categoryNameList.add(item_right);

                    //카테고리 정보가 안넘어온경우 전체(00)로 처리한다.
                    if (groupSortFillterInfo.filterInfo.get(i + 1).categoryNo.length == 0) {
                        groupSortFillterInfo.filterInfo.get(i + 1).categoryNo = new String[]{"00"};

                    }

                    //카테고리 갯수가 0이면 갯수를 노출하지 않는다.
                    if ("0".equals(groupSortFillterInfo.filterInfo.get(i + 1).totalCnt)) {
                        item_right_count.setVisibility(View.GONE);
                    }

                    i++;
                    //뷰를 클릭시 해당 필터를 적용한다.
                    itemRightLayout.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

//                            int totalCnt = 0;
//                            try {
//                                totalCnt = Integer.parseInt(groupSortFillterInfo.filterInfo.get(count + 1).totalCnt);
//                            } catch (NumberFormatException e) {
//                                Ln.e(e);
//                            }
                            closePanel();
                            mAdapter.getInfo().contents = categorylItem.get(count + 1);
                            ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(0);
                            mAdapter.notifyDataSetChanged();

                            currentCategoryTextView.setText(groupSortFillterInfo.filterInfo.get(count + 1).categoryName);
                            setTypeface(groupSortFillterInfo.filterInfo.get(count + 1).categoryName);

                        }
                    });
                }

                category_view.addView(view);
            }

            setTypeface("전체");
        }catch (Exception e){
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * 헤당 카테고리 강조처리(bold)
     *
     * @param name
     */
    private void setTypeface(String name) {
        for (TextView tv : categoryNameList) {
            if (tv.getText().toString().equals(name)) {
                tv.setTypeface(null, Typeface.BOLD);
            } else {
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    /**
     * 양쪽에 괄호를 넣는다.
     *
     * @param str
     * @return
     */
    private String addBrackets(String str) {
        return "(" + str + ")";
    }

    //카테고리뷰를 펼친다.
    private void expandPanel() {
        list_shadow.setVisibility(View.VISIBLE);

        int translationY = getResources().getDimensionPixelSize(R.dimen.search_content_height);
        category_view.setVisibility(View.VISIBLE);
        ObjectAnimator layout_search_barAnim = ObjectAnimator.ofFloat(category_view, "translationY", -translationY, 0);
        layout_search_barAnim.setDuration(300);
        layout_search_barAnim.start();

        all_arrow.setBackgroundResource(R.drawable.arrow_click);
        currentCategoryTextView.setTextColor(getResources().getColor(R.color.group_filter_text_color_sel));
        currentCategorysize.setTextColor(getResources().getColor(R.color.group_filter_text_color_sel));
    }

    //카테고리뷰를 닫는다.
    public void closePanel() {
        list_shadow.setVisibility(View.GONE);

        int translationY = getResources().getDimensionPixelSize(R.dimen.search_content_height);
        category_view.setVisibility(View.VISIBLE);
        ObjectAnimator layout_search_barAnim = ObjectAnimator.ofFloat(category_view, "translationY", 0, -translationY);
        layout_search_barAnim.setDuration(300);
        layout_search_barAnim.start();

        all_arrow.setBackgroundResource(R.drawable.arrow);
        currentCategoryTextView.setTextColor(getResources().getColor(R.color.group_filter_text_color_default));
        currentCategorysize.setTextColor(getResources().getColor(R.color.group_filter_text_color_default));
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
                    tempSection .sectionLinkParams + "&reorder=true", tempSection.sectionName);

//            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
//                    isCacheData, true, "http://m.gsshop.com/app/main/todayopen?version=3.1&openDate=2016070113000000", "", tempSection.sectionName);


        }


    }


}
