/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * GS X Brand 배너
 */
@SuppressLint("NewApi")
public class MapCxGbbVH extends GSSuperTabViewHolder {

    private static final int BANNER_ITEM_SIZE = 20;     // 최대 갯수 20개만 노출 되게끔 수정됨.
    private static final int COLUMN_COUNT = 2;

    private final View mMoreView;
    private final View mBrandShopView;

    private final CommonTitleLayout mCommonTitleLayout;
    private final RecyclerView mRecycler;
    private final String mNavigationID;

    /**
     * 컨텍스트
     */
    private Context mContext;

    /**
     * 상품 리스트 영역
     */
    private final View mLayGoods;

    /**
     * NO DATA 표시 영역
     */
    private final View mLayNoData;

    private final AtomicBoolean isAddedItemDecoration = new AtomicBoolean(false);

    private MapCxGbbAdapter mAdapter;

    /**
     * 섹션코드
     */
    private String sectionCode;

    /**
     * @param itemView itemView
     */
    public MapCxGbbVH(View itemView, String navId, String tabURL) {
        super(itemView);
        mMoreView = itemView.findViewById(R.id.view_read_more);
        mBrandShopView = itemView.findViewById(R.id.view_go_brand_shop);
        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);
        mRecycler = itemView.findViewById(R.id.recycle_gssuper);
        mLayGoods = itemView.findViewById(R.id.lay_goods);
        mLayNoData = itemView.findViewById(R.id.lay_no_data);
        mNavigationID = navId;
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position,
                                 ShopInfo info, final String action,
                                 final String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);

        this.mContext = context;
        final SectionContentList item = info.contents.get(position).sectionContent;

//        if(item.subProductList.size() <= BANNER_ITEM_SIZE) {
//            item.isMore = false;
//        }
        item.isMore = true;

        mCommonTitleLayout.setCommonTitle(this, item);

        //섹션코드 저장
        sectionCode = item.dealNo;
        
        if (isAddedItemDecoration.compareAndSet(false, true)) {
            mRecycler.setLayoutManager(new GridLayoutManager(context, COLUMN_COUNT));

            DividerItemDecoration dividerVertical = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
            dividerVertical.setDrawable(context.getResources().getDrawable(R.drawable.divider_gs_fresh_item));

            mRecycler.addItemDecoration(dividerVertical);

//            디자인 변경.
            mRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);

                    int position = parent.getChildAdapterPosition(view);
                    if (position % 2 == 0) {
                        outRect.left = dp2px(16f);
                        outRect.right = dp2px(5.5f);
                    }
                    else {
                        outRect.left = dp2px(5.5f);
                        outRect.right = dp2px(16f);
                    }

                    outRect.top = dp2px(16f);
                    outRect.bottom = dp2px(16f);
                }
            });
        }

        if (item.isMore && !item.isGoBrandShop) {
            // 전체 보기만 나오게 끔 수정.
            ViewUtils.hideViews(mMoreView);
            ViewUtils.showViews(mBrandShopView);
            this.mAdapter = new MapCxGbbAdapter(context, item.subProductList,
                    item.subProductList.size() > 20 ? BANNER_ITEM_SIZE : item.subProductList.size(), action, label, mNavigationID);
        } else if (!item.isGoBrandShop){
            ViewUtils.hideViews(mMoreView);
            // 매장 바로가기 버튼도 보이지 않도록 수정.
            ViewUtils.hideViews(mBrandShopView);
            this.mAdapter = new MapCxGbbAdapter(context, item.subProductList,
                    item.subProductList.size(), action, label, mNavigationID);
        }

//        mAdapter.setTabIndex(info.tabIndex);
        mRecycler.setAdapter(mAdapter);


        // 더보기
        mMoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.showViews(mBrandShopView);
                ViewUtils.hideViews(mMoreView);
                item.isGoBrandShop = true;
                mAdapter.setLength(item.subProductList.size());
                ((AbstractBaseActivity)context).setWiseLogHttpClient(ServerUrls.WEB.GS_SUPER_MORE_PRD);
            }
        });

        // 매장 바로가기
        mBrandShopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AbstractBaseActivity)context).setWiseLogHttpClient(ServerUrls.WEB.GS_SUPER_GO_BRANDSHOP);

                if (!TextUtils.isEmpty(item.linkUrl)) {
                    WebUtils.goWeb(context, item.linkUrl);
                }
            }
        });

        //상품이 없는 경우 안내멘트 노출
        if (isEmpty(item.subProductList)) {
            ViewUtils.showViews(mLayNoData);
            ViewUtils.hideViews(mLayGoods);
        } else {
            ViewUtils.showViews(mLayGoods);
            ViewUtils.hideViews(mLayNoData);
        }
    }

    @Override
    public String getSectionCode() {
        return sectionCode;
    }

    /**
     * 이미지가 화면상에 노출된 경우에 한해 이미지 캐시작업을 수행한다.
     *
     * @param event ImageCacheStartEvent
     */
    public void onEvent(Events.ImageCacheStartEvent event) {
        if (isNotEmpty(mNavigationID) && !mNavigationID.equals(event.naviId)) {
            //내가 속한 매장에 대한 이벤트가 아니면 스킵
            return;
        }
        for (int i = 0; i < mRecycler.getChildCount(); i++) {
            View view = mRecycler.getChildAt(i);
            MapCxGbbAdapter.BannerViewHolder vh = (MapCxGbbAdapter.BannerViewHolder) mRecycler.getChildViewHolder(view);
            if (DisplayUtils.isVisible(vh.image_prd) || DisplayUtils.isVisible(vh.view_image)) {
                String src = vh.getImageForCache();
                if (isNotEmpty(src) && src.contains(IMG_CACHE_RPL2_FROM)) {
                    String imgUrl = src.replace(IMG_CACHE_RPL2_FROM, IMG_CACHE_RPL_TO);
                    Glide.with(mContext).load(trim(imgUrl)).downloadOnly(IMG_CACHE_WIDTH, IMG_CACHE_HEIGHT);
                }
            }
        }
    }
}
