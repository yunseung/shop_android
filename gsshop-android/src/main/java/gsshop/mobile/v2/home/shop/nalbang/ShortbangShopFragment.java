/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.main.VideoSectionList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

/**
 * 숏방/ 날방 매장
 */
public class ShortbangShopFragment extends NalbangShopFragment {


    /**
     * 숏방 카테고리 아이템
     */
    private  ArrayList<ArrayList<ShopInfo.ShopItem>> shortbangCategorylItem = new ArrayList<ArrayList<ShopInfo.ShopItem>>();

    /**
     * 날방 카테고리 아이템
     */
    private  ArrayList<ArrayList<ShopInfo.ShopItem>> nalbangCategorylItem = new ArrayList<ArrayList<ShopInfo.ShopItem>>();

    /**
     * 현재 선택된 숏방의 아이템 목록
     */
    private ArrayList<ShopInfo.ShopItem> currentShortbangCategoryItem = new ArrayList<ShopInfo.ShopItem>();

    /**
     * 현재 선택된 날방의 아이템 목록
     */
    private ArrayList<ShopInfo.ShopItem> currentNalbangCategoryItem  = new ArrayList<ShopInfo.ShopItem>();

    /**
     * 현재 선택된 날방의 해쉬태그 아이템
     */
    private ArrayList<ShopInfo.ShopItem> currentNalbangHashTagItem = new ArrayList<ShopInfo.ShopItem>();

    /**
     * 숏방 초기에 보여질 리스트의 갯수
     */
    private final int shortbangInitialListSize = 2;

    /**
     * 날방 초기에 보여질 리스트의 갯수
     */
    private final int nalbangInitialListSize = 3;

    /**
     * 숏방 카테고리의 위치(rows) 아이템을 추가/삭제하거나 부분적으로 업데이트 하기 위해 사용
     */
    private int shortbangCategoryReflashItemRangeStart;

    /**
     * 숏방의 상단 포지션 위치
     */
    protected int shortbangTopPosition = 0;

    /**
     * 숏방에서 한번에 보여줄 리스트의 갯수(더보기 선택시 해당 변수의 값만큼 아이템이 추가된다)
     */
    private int shortbang_list_rows = 2;

    /**
     * 숏방 더보기시 추가할 갯수
     */
    private int shortbangAppendSize = 3;

    /**
     * 날방 더보기시 추가할 갯수
     */
    private int nalbangAppendSize = 10;

    /**
     * 숏방 카테고리(선택된 카테고리)
     */
    private int shortbang_category = 0;

    /**
     * 날방 카테고리의 위치(rows) 아이템을 추가/삭제하거나 부분적으로 업데이트 하기 위해 사용
     */
    private int nalbangCategoryReflashItemRangeStart;

    /**
     * 날방에서 한번에 보여줄 리스트의 갯수(더보기 선택시 해당 변수의 값만큼 아이템이 추가된다)
     */
    private int nalbang_list_rows = 3;

    /**
     * 날방 카테고리(선택된 카테고리)
     */
    private int nalbang_category = 0;

    /**
     * 숏방 더보기
     */
    private ShopInfo.ShopItem shortbang_readmore_content;

    /**
     * 날방 더보기
     */
    private ShopInfo.ShopItem nalbang_readmore_content;

    /**
     * 숏방이 상단에 위치하는지 여부 true = 상단, false = 하단
     */
    private boolean isShortbangFirst = false;

    /**
     * 날방이 상단에 위치하는지 여부 true = 상단, false = 하단
     */
    private boolean isNalbangFirst = false;

    public static ShortbangShopFragment newInstance(int position) {
        ShortbangShopFragment fragment = new ShortbangShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public ShortbangShopFragment() {
        super();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    public void drawFragment() {
        // TV생방송 존재 여부에 상관없이 전체 data loading
        new ShortbangShopController(getActivity(), tempSection, true).execute();
    }


    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo,
                           int tab, int listPosition) {

        if(shortbangCategorylItem != null){
            shortbangCategorylItem.clear();
        }
        if(nalbangCategorylItem != null){
            nalbangCategorylItem.clear();
        }
        shortbangCategoryReflashItemRangeStart = 0;
        nalbangCategoryReflashItemRangeStart = 0;
        if ("WEB".equals(sectionList.sectionType)) {
            // ;
        } else {
            if (contentsListInfo != null || getActivity() != null) {

                ShopInfo info = new ShopInfo();

                info.contents = new ArrayList<ShopInfo.ShopItem>();

                info.sectionList = sectionList;
                info.ajaxPageUrl = contentsListInfo.ajaxPageUrl;
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


                //브랜드딜 예외처리
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

                //get(0)에 대한 예외처리
                if(contentsListInfo.productList != null && !contentsListInfo.productList.isEmpty()) {
                    for (SectionContentList c : contentsListInfo.productList) {
                        int type = getFlexibleViewType(c.viewType);
                        if (type != ViewHolderType.BANNER_TYPE_NONE) {
                            content = new ShopInfo.ShopItem();
                            content.type = type;
                            content.sectionContent = c;

                            if (sectionList.viewType.equals(ShopInfo.TYPE_FLEXIBLE)) {
                                if (type == ViewHolderType.VIEW_TYPE_B_IG4XN) {
                                    if (content.sectionContent.imageUrl == null || "".equals(content.sectionContent.imageUrl)) {
                                        content.type = ViewHolderType.BANNER_TYPE_NONE;
                                    }
                                }
                            }

                            //B_IG4XN


                            //브랜드딜 예외처리
                            if (sectionList.viewType.equals(ShopInfo.TYPE_BRAND) && info.categoryIndex != null) {

                                if (!info.categoryIndex.isEmpty()) {
                                    ShopInfo.ShopItem dividerContent = new ShopInfo.ShopItem();
                                    dividerContent.type = ViewHolderType.BANNER_TYPE_BRAND_DIVIDER;
                                    info.contents.add(dividerContent);
                                }

                                content.type = ViewHolderType.BANNER_TYPE_BRAND_CATEGORY;
                                info.contents.add(content);
                                info.categoryIndex.add(info.contents.indexOf(content));

                                for (SectionContentList sub : c.subProductList) {
                                    ShopInfo.ShopItem subContent = new ShopInfo.ShopItem();
                                    subContent.type = ViewHolderType.BANNER_TYPE_BRAND_CONTENT;
                                    subContent.sectionContent = sub;
                                    if (content.sectionContent != null) {
                                        info.contents.add(subContent);
                                    }
                                }


                            } else {
                                if (content.sectionContent != null) {
                                    info.contents.add(content);
                                }
                            }
                        } else {
                            if ("B_SIS".equals(c.viewType)) {
                                // "슬라이드 이미지배너" = B_SIS (subProductList -> imageUrl, prdUrl)
                                content = new ShopInfo.ShopItem();
                                if (c.subProductList.size() > 1) {
                                    content.type = ViewHolderType.VIEW_TYPE_B_SIC;
                                    content.sectionContent = c;
                                    if (content.sectionContent != null) {
                                        info.contents.add(content);
                                    }
                                } else if (c.subProductList.size() == 1) {
                                    // 슬라이드 이미지 갯수가 1개이면 이미지 배너에 넣음.
                                    content.type = ViewHolderType.BANNER_TYPE_IMAGE;
                                    content.sectionContent = c.subProductList.get(0);
                                    if (content.sectionContent != null) {
                                        info.contents.add(content);
                                    }

                                }
                            }
                        }
                    }
                }



                if(contentsListInfo.videoSectionList != null && !contentsListInfo.videoSectionList.isEmpty()){

                    for(VideoSectionList list  : contentsListInfo.videoSectionList){
                        //숏방 리스트
                        if("STFCLIST".equals(list.sectionType)){
                            if(!isNalbangFirst){
                                isShortbangFirst = true;
                                //숏방이 상단에 위치하면 날방에 더보기 버튼없이 모든 리스트를 보여주기 위해 10000으로 셋팅한다.
                                nalbang_list_rows = 10000;
                            }
                            //숏방의 포지션을 기억하기 위해 설정
                            shortbangTopPosition = info.contents.size();
                            // 상단 배너. imageUrl로 배너 존재 여부 체크.
                            if (list.product != null
                                    && DisplayUtils.isValidString(list.product.imageUrl)) {
                                // 배너.
                                content = new ShopInfo.ShopItem();
                                content.type = ViewHolderType.BANNER_TYPE_BAND;
                                content.sectionContent = new SectionContentList();
                                content.sectionContent.imageUrl = list.product.imageUrl;
                                content.sectionContent.linkUrl = list.product.linkUrl;
                                content.sectionContent.viewType = "B_PZ";
                                info.contents.add(content);
                            }

                            for(SectionContentList productList : list.productList){

                                int type = getFlexibleViewType(productList.viewType);
                                if (type != ViewHolderType.BANNER_TYPE_NONE) {
                                    content = new ShopInfo.ShopItem();
                                    content.type = type;
                                    content.sectionContent = productList;

                                    //숏방 배너리스트가 1개일때 뷰페이저가 아닌 이미지로 표현한다.
                                    if(type == ViewHolderType.VIEW_TYPE_SSL && content.sectionContent != null
                                            && content.sectionContent.subProductList != null && !content.sectionContent.subProductList.isEmpty()
                                            && content.sectionContent.subProductList.size() == 1){
                                        content.type = ViewHolderType.BANNER_TYPE_SHORTBANG_BANNER;
                                    }

                                    info.contents.add(content);
                                }

                                //숏방 첫번째 카테고리를 리스트에 추가시킨다.
                                if(type == ViewHolderType.BANNER_TYPE_SHORTBANG_HASH_TAG_TAB){
                                    shortbangCategoryReflashItemRangeStart =  info.contents.size();
                                    if(productList.subProductList != null && !productList.subProductList.isEmpty()){
                                        info.categoryContentList = productList.subProductList;
                                        SectionContentList subList = null;

                                        if(productList.subProductList != null) {
                                            //카테고리 갯수만큼 반복하여 아이템을 구분지어 저장
                                            for (int i = 0; i < productList.subProductList.size(); i++) {
                                                SectionContentList category = productList.subProductList.get(i);
                                                ArrayList<ShopInfo.ShopItem> categoryContents = new ArrayList<ShopInfo.ShopItem>();
                                                shortbangCategorylItem.add(categoryContents);

                                                if (category.subProductList != null) {
                                                    for (int j = 0; j < category.subProductList.size(); j++) {
                                                        //1개의 뷰에 3개의 아이템을 셋팅한다.
                                                        if (j % 3 == 0) {
                                                            subList = new SectionContentList();
                                                            subList.subProductList = new ArrayList<SectionContentList>();
                                                            subList.subProductList.add(productList.subProductList.get(i).subProductList.get(j));
                                                            content = new ShopInfo.ShopItem();
                                                            content.type = ViewHolderType.VIEW_TYPE_SBT;
                                                            content.sectionContent = subList;
                                                            content.categoryContentList = productList.subProductList;
                                                            content.sectionContent.sbCateGb = category.sbCateGb;
                                                            categoryContents.add(content);
                                                        } else {
                                                            //10/19 품질팀 요청
                                                            if(subList != null && subList.subProductList != null) {
                                                                subList.subProductList.add(productList.subProductList.get(i).subProductList.get(j));
                                                            }
                                                        }
                                                    }

                                                    if(category.subProductList.isEmpty()){
                                                        //아이템이 없으면 empty뷰로 셋팅한다.
                                                        content = new ShopInfo.ShopItem();
                                                        content.type = ViewHolderType.BANNER_TYPE_SHORTBANG_EMPTY;
                                                        content.sectionContent = new SectionContentList();
                                                        categoryContents.add(content);
                                                    }

                                                }else{
                                                    //아이템이 없으면 empty뷰로 셋팅한다.
                                                    content = new ShopInfo.ShopItem();
                                                    content.type = ViewHolderType.BANNER_TYPE_SHORTBANG_EMPTY;
                                                    content.sectionContent = new SectionContentList();
                                                    categoryContents.add(content);
                                                }
                                            }
                                        }
                                        //숏방의 초기 카테고리를 지정한다.(기본값 0)
                                        if(!shortbangCategorylItem.isEmpty()) {
                                            currentShortbangCategoryItem = shortbangCategorylItem.get(shortbang_category);
                                        }

                                        addShortbangCategoryItem(info);
                                    }
                                }
                            }
                        }
                        //날방
                        if("NTFCLIST".equals(list.sectionType)){
                            if(!isShortbangFirst){
                                isNalbangFirst = true;
                                //날방이 상단에 위치하면 숏방에 더보기 버튼없이 모든 리스트를 보여주기 위해 10000으로 셋팅한다.
                                shortbang_list_rows = 10000;
                            }
                            // 상단 배너. imageUrl로 배너 존재 여부 체크.
                            if (list.product != null
                                    && DisplayUtils.isValidString(list.product.imageUrl)) {
                                // 배너.
                                content = new ShopInfo.ShopItem();
                                content.type = ViewHolderType.BANNER_TYPE_BAND;
                                content.sectionContent = new SectionContentList();
                                content.sectionContent.imageUrl = list.product.imageUrl;
                                content.sectionContent.linkUrl = list.product.linkUrl;
                                content.sectionContent.viewType = "B_PZ";
                                info.contents.add(content);
                            }

                            // 날방 탭 생방송영역
                            if (list.tvLiveDealBanner != null
                                    && sectionList.viewType.equals(ShopInfo.TYPE_NALBANG)) {
                                content = new ShopInfo.ShopItem();
                                content.type = ViewHolderType.BANNER_TYPE_NALBANG_LIVE;
                                info.tvLiveDealBanner = list.tvLiveDealBanner;
                                info.tvLiveDealBanner.tvLiveDealUrl = getHomeGroupInfo().appUseUrl.tvLiveDealBannerUrl;
                                info.contents.add(content);
                            }

                            for(SectionContentList productList : list.productList){
                                int type = getFlexibleViewType(productList.viewType);
                                if (type != ViewHolderType.BANNER_TYPE_NONE) {
                                    content = new ShopInfo.ShopItem();
                                    content.type = type;
                                    content.sectionContent = productList;
                                    info.contents.add(content);

                                    //날방 첫번째 카테고리를 리스트에 추가시킨다.
                                    if(type == ViewHolderType.BANNER_TYPE_NALBANG_HASH_TAG_TAB){
                                        nalbangCategoryReflashItemRangeStart =  info.contents.size();
                                        if(productList.subProductList != null && !productList.subProductList.isEmpty()){

                                            if(productList.subProductList != null) {
                                                //카테고리 갯수만큼 반복하여 아이템을 구분지어 저장
                                                for (int i = 0; i < productList.subProductList.size(); i++) {
                                                    SectionContentList category = productList.subProductList.get(i);
                                                    ArrayList<ShopInfo.ShopItem> categoryContents = new ArrayList<ShopInfo.ShopItem>();
                                                    nalbangCategorylItem.add(categoryContents);

                                                    if(category.subProductList != null) {
                                                        for (SectionContentList subProductList : category.subProductList) {
                                                            type = getFlexibleViewType(subProductList.viewType);
                                                            content = new ShopInfo.ShopItem();
                                                            content.type = type;
                                                            content.sectionContent = subProductList;
                                                            categoryContents.add(content);
                                                        }
                                                    }
                                                }
                                            }
                                            //날방의 초기 카테고리를 지정한다.(기본값 0)
                                            if(!nalbangCategorylItem.isEmpty()) {
                                                currentNalbangCategoryItem = nalbangCategorylItem.get(nalbang_category);
                                            }

                                            addNalbangCategoryItem(info);

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 마지막 풋터 지정.
                content = new ShopInfo.ShopItem();
                content.type = ViewHolderType.BANNER_TYPE_FOOTER;
                info.contents.add(content);

                mAdapter.setInfo(info);
                mAdapter.notifyDataSetChanged();
                ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(0);

            }
        }
    }

    /**
     * 숏방 카테고리 아이템을 추가시킨다.
     * @param info info
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void addShortbangCategoryItem(ShopInfo info) {
        if (info.contents != null) {
            int categoryItemSize = currentShortbangCategoryItem.size();
            int itemIndex = 0;
            for(int i=0; i < categoryItemSize ; i ++) {
                ShopInfo.ShopItem item = currentShortbangCategoryItem.get(i);
                //한번에 보여줄 갯수를 넘은경우에는 아이템을 추가하지 않는다.
                if(i < shortbang_list_rows) {
                    itemIndex = shortbangCategoryReflashItemRangeStart + i;
                    info.contents.add(itemIndex ,item);
                }
            }

            shortbang_readmore_content = new ShopInfo.ShopItem();
            shortbang_readmore_content.type = ViewHolderType.BANNER_TYPE_SHORTBANG_READ_MORE;
            SectionContentList sectionContent = new SectionContentList();


            //현재 노출 상품보다 상품이 더 많은 경우 더보기 버튼 노출
            if(shortbang_list_rows < categoryItemSize){
                sectionContent.isShow = true;
                shortbang_readmore_content.sectionContent = sectionContent;
                info.contents.add(itemIndex + 1 , shortbang_readmore_content);
            }else{
                sectionContent.isShow = false;
                shortbang_readmore_content.sectionContent = sectionContent;
                info.contents.add(itemIndex + 1 , shortbang_readmore_content);
            }
        }
    }

    /**
     * 카테고리 아이템을 제거한다.
     * @param info info
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void removeShortbangCategoryItem(ShopInfo info){
        if (info.contents != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                info.contents.removeAll(currentShortbangCategoryItem);
            } else {
                for(ShopInfo.ShopItem item : currentShortbangCategoryItem) {
                    info.contents.remove(item);
                }
            }
            info.contents.remove(shortbang_readmore_content);
        }
    }

    /**
     * 카테고리 아이템을 업데이트 한다.
     * @param index index
     */
    private void updateShortbangCategoryItem(int index, boolean isInsert){
        shortbang_category = index;
        try {
            ShopInfo info = mAdapter.getInfo();
            removeShortbangCategoryItem(info);
            currentShortbangCategoryItem = shortbangCategorylItem.get(index);
            addShortbangCategoryItem(info);

            if(currentShortbangCategoryItem != null && info.contents.size() > shortbangCategoryReflashItemRangeStart) {

                if(isInsert){
                    mAdapter.notifyItemRangeInserted(info.contents.size() - 2, info.contents.size());
                }else{
                    mAdapter.notifyItemRangeChanged(shortbangCategoryReflashItemRangeStart-1, info.contents.size());
                }

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
     * 카테고리 아이템을 추가시킨다.
     * @param info info
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void addNalbangCategoryItem(ShopInfo info) {
        if (info.contents != null) {
            int categoryItemSize = currentNalbangCategoryItem.size();
            int itemIndex = 0;
            for(int i=0; i < categoryItemSize ; i ++) {
                ShopInfo.ShopItem item = currentNalbangCategoryItem.get(i);
                //한번에 보여줄 갯수를 넘은경우에는 아이템을 추가하지 않는다.
                if(i < nalbang_list_rows) {
                    itemIndex = nalbangCategoryReflashItemRangeStart + i;
                    info.contents.add(itemIndex ,item);
                }
            }

            nalbang_readmore_content = new ShopInfo.ShopItem();
            nalbang_readmore_content.type = ViewHolderType.BANNER_TYPE_NALBANG_READ_MORE;
            SectionContentList sectionContent = new SectionContentList();


            //현재 노출 상품보다 상품이 더 많은 경우 더보기 버튼 노출
            if(nalbang_list_rows < categoryItemSize){
                sectionContent.isShow = true;
                nalbang_readmore_content.sectionContent = sectionContent;
                info.contents.add(itemIndex + 1 , nalbang_readmore_content);
            }else{
                sectionContent.isShow = false;
                nalbang_readmore_content.sectionContent = sectionContent;
                info.contents.add(itemIndex + 1 , nalbang_readmore_content);
            }
        }
    }

    /**
     * 카테고리 아이템을 제거한다.
     * @param info info
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void removeNalbangCategoryItem(ShopInfo info){
        if (info.contents != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                info.contents.removeAll(currentNalbangCategoryItem);
            } else {
                for(ShopInfo.ShopItem item : currentNalbangCategoryItem) {
                    info.contents.remove(item);
                }
            }
            info.contents.remove(nalbang_readmore_content);
        }
    }

    /**
     * 카테고리 아이템을 업데이트 한다.
     * @param index index
     */
    private void updateNalbangCategoryItem(int index, boolean isInsert){
        nalbang_category = index;
        try {
            ShopInfo info = mAdapter.getInfo();
            removeNalbangCategoryItem(info);
            currentNalbangCategoryItem = nalbangCategorylItem.get(index);
            addNalbangCategoryItem(info);

            if(currentNalbangCategoryItem != null && info.contents.size() > nalbangCategoryReflashItemRangeStart) {
                if(isInsert){
                    mAdapter.notifyItemRangeInserted(info.contents.size() - 3, info.contents.size());
                }else{
                    mAdapter.notifyItemRangeChanged(nalbangCategoryReflashItemRangeStart - 1, info.contents.size());
                }

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
     * 데이터의 추가나 삭제시 날방의 위치를 업데이트한다.
     */
    private void updateNalbangItemIndex(){
        ShopInfo info = mAdapter.getInfo();
        for(int i = 0 ; i < info.contents.size(); i++){
            ShopInfo.ShopItem content = info.contents.get(i);
            if (content.type ==  ViewHolderType.BANNER_TYPE_NALBANG_HASH_TAG_TAB) {
                nalbangCategoryReflashItemRangeStart = i + 1;
                break;

            }
        }
    }

    /**
     * 데이터의 추가나 삭제시 숏방의 위치를 업데이트한다.
     */
    private void updateShortbangItemIndex(){
        ShopInfo info = mAdapter.getInfo();
        for(int i = 0 ; i < info.contents.size(); i++){
            ShopInfo.ShopItem content = info.contents.get(i);
            if (content.type ==  ViewHolderType.BANNER_TYPE_SHORTBANG_HASH_TAG_TAB) {
                shortbangCategoryReflashItemRangeStart = i + 1;
                break;

            }
        }
    }

    /**
     * 숏방 더보기 이벤트
     * @param event
     */
    public void onEventMainThread(Events.FlexibleEvent.ShortbangReadmore event) {
        if (getUserVisibleHint()) {
            //더보기 수 만큼 데이터row를 늘린다.
            shortbang_list_rows = shortbang_list_rows + shortbangAppendSize;
            updateShortbangCategoryItem(shortbang_category, true);
            if(isShortbangFirst){
                updateNalbangItemIndex();
            }
        }
    }

    /**
     * 날방 더보기 이벤트
     * @param event
     */
    public void onEventMainThread(Events.FlexibleEvent.NalbangReadmore event) {
        if (getUserVisibleHint()) {
            //더보기 수 만큼 데이터row를 늘린다.
            nalbang_list_rows = nalbang_list_rows + nalbangAppendSize;
            if(mAdapter != null && mAdapter.getInfo().tabIndex == -1){
                try {
                    ShopInfo info = mAdapter.getInfo();
                    removeNalbangCategoryItem(info);
                    addNalbangCategoryItem(info);
                    //10/19 품질팀 요청
                    if(currentNalbangCategoryItem != null && info != null && info.contents.size() > nalbangCategoryReflashItemRangeStart) {
                        mAdapter.notifyItemRangeInserted(info.contents.size() - 3, info.contents.size());
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
            }else{
                updateNalbangCategoryItem(nalbang_category, true);
            }

            if(isNalbangFirst){
                updateShortbangItemIndex();
            }

        }
    }

    /**
     * 숏방 탭 변경 이벤트
     * @param event
     */
    public void onEventMainThread(Events.FlexibleEvent.ShortbangTabUpdate event) {
        if (getUserVisibleHint()) {
            mAdapter.getInfo().shortbangTabIndex = event.tab;
            //탭 변경시 보여지는 갯수를 초기화 한다.
            if(isShortbangFirst) {
                shortbang_list_rows = shortbangInitialListSize;
            }
            updateShortbangCategoryItem(event.tab, false);
            if(isShortbangFirst){
                updateNalbangItemIndex();
            }
        }
    }

    /**
     * 날방 탭 변경 이벤트
     * @param event
     */
    public void onEventMainThread(Events.FlexibleEvent.NalbangHashTagTabUpdate event) {
        if (getUserVisibleHint()) {
            mAdapter.getInfo().tabIndex = event.tab;

            //탭 변경시 보여지는 갯수를 초기화 한다.
            if(isNalbangFirst) {
                nalbang_list_rows = nalbangInitialListSize;
            }
            updateNalbangCategoryItem(event.tab, false);
            if(isNalbangFirst){
                updateShortbangItemIndex();
            }
        }

    }

    /**
     * 날방 해시태그 선택 이벤트
     * @param event
     */
    public void onEventMainThread(final Events.FlexibleEvent.NalbangHashTagUpdate event) {


        mAdapter.getInfo().tabIndex = -1;
        currentNalbangHashTagItem.clear();

        if(nalbangCategorylItem != null && !nalbangCategorylItem.isEmpty()){
            ArrayList<ShopInfo.ShopItem> nalbangItems = nalbangCategorylItem.get(0);

            //선택한 해시태그에 맞는 아이템을 모은다.
            for(ShopInfo.ShopItem item : nalbangItems){
                if(item.sectionContent.productHashTags != null) {
                    for (String hashTag : item.sectionContent.productHashTags) {
                        if (hashTag.equals(event.hashTag)) {
                            currentNalbangHashTagItem.add(item);
                        }
                    }
                }
            }
        }

        mAdapter.getInfo().hashTagCount = currentNalbangHashTagItem.size();
        mAdapter.getInfo().hashTagName = event.hashTag;

        nalbang_category = 0;
        //보여지는 갯수를 초기화 한다.
        if(isNalbangFirst) {
            nalbang_list_rows = nalbangInitialListSize;
        }

        try {
            ShopInfo info = mAdapter.getInfo();
            int previousSize = info.contents.size();
            removeNalbangCategoryItem(info);
            currentNalbangCategoryItem = currentNalbangHashTagItem;
            addNalbangCategoryItem(info);

            int newSize = info.contents.size();
            // 변경전과 변경 후의 아이템갯수를 체크하여 notifyItemRangeRemoved을 날리지 않으면 에러발생
            if(currentNalbangCategoryItem != null && info.contents.size() > nalbangCategoryReflashItemRangeStart) {
                if(newSize < previousSize){
                    mAdapter.notifyItemRangeRemoved(nalbangCategoryReflashItemRangeStart - 1, newSize - previousSize);
                }
                mAdapter.notifyItemRangeChanged(nalbangCategoryReflashItemRangeStart - 1, info.contents.size());
            }else{
                mAdapter.notifyDataSetChanged();
            }

            ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(nalbangCategoryReflashItemRangeStart-1, 0);
        }catch (Exception e){
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }

        if(isNalbangFirst){
            updateShortbangItemIndex();
        }
    }

    public void onEventMainThread(Events.ShortbangMoveTopEvent event) {
        ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(shortbangTopPosition, 0);
    }



    /**
     * classes
     */

    /**
     * 섹션 업데이트
     */
    public class ShortbangShopController extends GetUpdateController {


        protected ShortbangShopController(Context activityContext, TopSectionList sectionList, boolean isCacheData) {
            super(activityContext, sectionList, isCacheData);
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            //숏방데이타 초기화
            CategoryDataHolder.putCategoryData(null);
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.sectionLinkUrl,
                    tempSection.sectionLinkParams + "&reorder=true", tempSection.sectionName);
//            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
//                    isCacheData, true, "http://10.52.164.237/test/app/videobanglist2.jsp",
//                    "", tempSection.sectionName);

        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            mRefreshView.setVisibility(View.GONE);
            // 현재 액티비티 활성화 상태 체크
            if (getActivity() != null && listInfo != null) {
                updateList(tempSection, listInfo, 0, -1);
            }

            // 장바구니 카운트를 업데이트 한다.
            mActivity.setBasketcnt();

        }

    }


}
