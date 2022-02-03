/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.bestshop.BestShopFragment;
import gsshop.mobile.v2.support.image.Utils;
import gsshop.mobile.v2.util.DataUtil;
import roboguice.util.Ln;

/**
 * 날방매장
 */
public class NalbangShopFragment extends BestShopFragment {

    private static final int HASH_TAG_SLEEP = 200;
    protected int hashTagsPosition = -1;


    protected final Comparator stringComparator = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    public static NalbangShopFragment newInstance(int position) {
        NalbangShopFragment fragment = new NalbangShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public NalbangShopFragment() {
        super();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new NalbangShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getView() != null && mAdapter.getInfo() != null) {
                    //생방송 남은시간 표시용 타이머 시작을 위한 이벤트 전달
                    EventBus.getDefault().post(new Events.FlexibleEvent.NalbangTimerEvent(true));
                }
            }
        }, 1000);
    }

    @Override
    public void onStop() {
        //생방송 남은시간 표시용 타이머 정지를 위한 이벤트 전달
        EventBus.getDefault().post(new Events.FlexibleEvent.NalbangTimerEvent(false));
        super.onStop();
    }

    /**
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    public void drawFragment() {

        // setSwipeRefresh();
        // TV생방송 존재 여부에 상관없이 전체 data loading
        new NalbangShopController(getActivity(), tempSection, true).execute();
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
            } else if ("HF".equals(viewType)) {
                // manager's hashtag
                type = ViewHolderType.BANNER_TYPE_NALBANG_HASH_TAG_TAB;
            } else if ("NHP".equals(viewType)) {
                // nalbang product
                type = ViewHolderType.BANNER_TYPE_NALBANG_PRD;
            }else if("SSL".equals(viewType)) {
                // 숏방 뷰페이저
                type = ViewHolderType.VIEW_TYPE_SSL;
            }else if("SCF".equals(viewType)) {
                // 숏방 카테고리
                type = ViewHolderType.BANNER_TYPE_SHORTBANG_HASH_TAG_TAB;
            }else if ("SBT".equals(viewType)) {
                // 숏방 product
                type = ViewHolderType.VIEW_TYPE_SBT;
            }
        }

        return type;
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo,
                           int tab, int listPosition) {
        super.updateList(sectionList, contentsListInfo, tab, listPosition);

        if ("WEB".equals(sectionList.sectionType)) {
            // ;
        } else {
            if (contentsListInfo != null || getActivity() != null) {

                ShopInfo info = mAdapter.getInfo();
                if (info != null && info.contents != null) {

                    int i = 0;
                    for (ShopInfo.ShopItem content : info.contents) {
                        if (content.type == ViewHolderType.BANNER_TYPE_NALBANG_HASH_TAG_TAB && !content.sectionContent.subProductList.isEmpty()) {
                            hashTagsPosition = i;
                            int itemCount = 0;
                            for (SectionContentList product : content.sectionContent.subProductList.get(0).subProductList) {
                                // sorting the hash tags of nalbang's product
                                product.sortedProductHashTags = FluentIterable.from(product.productHashTags).toSortedSet(stringComparator);
                                // add nalbang' product
                                ShopInfo.ShopItem item = new ShopInfo.ShopItem();
                                item.type = ViewHolderType.BANNER_TYPE_NALBANG_PRD;
                                item.sectionContent = product;
                                info.contents.add(info.contents.size() - 1, item);
                                itemCount++;
                            }
                            mAdapter.notifyItemRangeInserted(hashTagsPosition + 1, itemCount);
                            break;
                        }
                        i++;

                    }


                }

            }
        }
    }


    /**
     * classes
     */

    /**
     * 섹션 업데이트
     */
    public class NalbangShopController extends GetUpdateController {


        protected NalbangShopController(Context activityContext, TopSectionList sectionList, boolean isCacheData) {
            super(activityContext, sectionList, isCacheData);
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.sectionLinkUrl,
                    tempSection.sectionLinkParams + "&reorder=true", tempSection.sectionName);
//            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
//                    isCacheData, true, "http://10.52.164.237/test/app/null_check_2.json",
//                    "", tempSection.sectionName);
        }


    }

    /**
     * nalbang product list update
     */
    // filtering tag name
    public void onEventMainThread(final Events.FlexibleEvent.NalbangHashTagUpdate event) {

        if (hashTagsPosition < 0) {
            if (mAdapter != null && mAdapter.getInfo() != null) {
                List<ShopInfo.ShopItem> contents = mAdapter.getInfo().contents;
                for (int i = 0; i < contents.size(); i++) {
                    if (contents.get(i).type == ViewHolderType.BANNER_TYPE_NALBANG_HASH_TAG_TAB) {
                        hashTagsPosition = i;
                    }
                }
            }
        }
        if (hashTagsPosition < 0) {
            return;
        }

        mAdapter.getInfo().tabIndex = -1;

        new BaseAsyncController<Integer>(getActivity()) {

            @Override
            protected Integer process() throws Exception {


                List<ShopInfo.ShopItem> contents = null;
                List<SectionContentList> newProducts = null;

                if( mAdapter != null && mAdapter.getInfo() != null)
                {
                    contents = mAdapter.getInfo().contents;
                }
                //오브젝트 접근에 대한 예외처리
                //hashTagsPosition contents.size() 범위내에 존재해야 하며
                // sectionContent.subProductList 유효해야 한다
                if(hashTagsPosition > 0 && contents != null && contents.size() > hashTagsPosition &&
                        contents.get(hashTagsPosition).sectionContent != null &&
                        contents.get(hashTagsPosition).sectionContent.subProductList != null &&
                        !contents.get(hashTagsPosition).sectionContent.subProductList.isEmpty()) {

                    newProducts = contents.get(hashTagsPosition).sectionContent.subProductList.get(0).subProductList;
                }

                TimeUnit.MILLISECONDS.sleep(HASH_TAG_SLEEP);
                // remove nalbang products
                int deletedCount = 0;
                // contents null 방어로직 해당 while 재 검토 필요  09/30
                if( contents != null ) {
                    while (hashTagsPosition + 1 < contents.size() - 1) {
                        contents.remove(hashTagsPosition + 1);
                        deletedCount++;
                    }
                }

                Ln.i("deletedCount count : " + deletedCount);
                if(!Utils.hasJellyBean()) {
                    TimeUnit.MILLISECONDS.sleep(HASH_TAG_SLEEP);
                }

                // newProducts가 맨위로직에서 null 개연성에 대한 예외처리
                if (newProducts != null && newProducts.isEmpty()) {
                    return 0;
                }

                // add nalbang hash tag tab products
                //null 방어로직
                if( newProducts != null && !newProducts.isEmpty() )
                {
                    for (SectionContentList product : newProducts) {
                        ShopInfo.ShopItem item = new ShopInfo.ShopItem();
                        item.type = ViewHolderType.BANNER_TYPE_NALBANG_PRD;
                        item.sectionContent = product;
                        contents.add(contents.size() - 1, item);
                    }
                }

                int found = 0;
                // null 방어코트
                // contents.size() 와 hashTagsPosition 사이에 방어로직은 재검토 필요 MSLEE 09/30
                if(contents != null && !contents.isEmpty() ) {
                    for (int pos = hashTagsPosition + 1; pos < contents.size() - 1; pos = hashTagsPosition + 1 + found) {
                        ImmutableSortedSet<String> tags = contents.get(pos).sectionContent.sortedProductHashTags;
                        if (tags == null || tags.isEmpty()) {
                            contents.remove(pos);
                            continue;
                        }
                        ImmutableList<String> sorted = tags.asList();
                        if (Collections.binarySearch(sorted, event.hashTag, stringComparator) < 0) {
                            contents.remove(pos);
                        } else {
                            found++;
                        }
                    }
                    mAdapter.getInfo().hashTagCount = found;
                    mAdapter.getInfo().hashTagName = event.hashTag;
                }
                return found;
            }

            @Override
            protected void onSuccess(Integer itemCount) throws Exception {
                super.onSuccess(itemCount);
                // update hashtag tab view
                Ln.i("itemCount count : " + itemCount);

//                mAdapter.notifyItemRangeChanged(hashTagsPosition, 1);
                mAdapter.notifyItemRangeChanged(0, hashTagsPosition+1);

                // update hash tag tab products
                if (itemCount > 0) {
                    mAdapter.notifyItemRangeChanged(hashTagsPosition + 1, itemCount);
                }

                Ln.i("total count : " + mAdapter.getInfo().contents.size());
                TimeUnit.MILLISECONDS.sleep(HASH_TAG_SLEEP);
                mRecyclerView.scrollToPosition(hashTagsPosition);
                ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(hashTagsPosition, 0);
            }
        }.execute();

    }

    // update hash tag tab
    public void onEventMainThread(Events.FlexibleEvent.NalbangHashTagTabUpdate event) {

        if (hashTagsPosition < 0) {
            if (mAdapter != null && mAdapter.getInfo() != null) {
                List<ShopInfo.ShopItem> contents = mAdapter.getInfo().contents;
                for (int i = 0; i < contents.size(); i++) {
                    if (contents.get(i).type == ViewHolderType.BANNER_TYPE_NALBANG_HASH_TAG_TAB) {
                        hashTagsPosition = i;
                    }
                }
            }
        }
        if (hashTagsPosition < 0) {
            return;
        }

        mAdapter.getInfo().tabIndex = event.tab;
        mAdapter.notifyItemRangeChanged(0, hashTagsPosition+1);
        new BaseAsyncController<Integer>(getActivity()) {

            @Override
            protected Integer process() throws Exception {


                List<ShopInfo.ShopItem> contents = mAdapter.getInfo().contents;
                List<SectionContentList> newProducts = contents.get(hashTagsPosition).sectionContent.subProductList.get(mAdapter.getInfo().tabIndex).subProductList;

                TimeUnit.MILLISECONDS.sleep(HASH_TAG_SLEEP);
                // remove nalbang products
                int deletedCount = 0;
                while (hashTagsPosition + 1 < contents.size() - 1) {
                    contents.remove(hashTagsPosition + 1);
                    deletedCount++;
                }

                Ln.i("deletedCount count : " + deletedCount);
                if(!Utils.hasJellyBean()) {
                    TimeUnit.MILLISECONDS.sleep(HASH_TAG_SLEEP);
                }
                if (newProducts.isEmpty()) {
                    return 0;
                }

                // add nalbang hash tag tab products
                int itemCount = 0;
                for (SectionContentList product : newProducts) {
                    ShopInfo.ShopItem item = new ShopInfo.ShopItem();
                    item.type = ViewHolderType.BANNER_TYPE_NALBANG_PRD;
                    item.sectionContent = product;
                    contents.add(contents.size() - 1, item);
                    itemCount++;

                }

                return itemCount;
            }

            @Override
            protected void onSuccess(Integer itemCount) throws Exception {
                super.onSuccess(itemCount);
                // update hash tag tab products
                Ln.i("itemCount count : " + itemCount);
                if (itemCount > 0) {
                    mAdapter.notifyItemRangeInserted(hashTagsPosition + 1, itemCount);
                }

                Ln.i("total count : " + mAdapter.getInfo().contents.size());
            }
        }.execute();

    }

}
