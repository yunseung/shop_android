/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.publicusage;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.EmptyUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfoPrd2;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.AIR_BUY;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SOLD_OUT;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 *
 */
public class Prd2VH extends BaseViewHolderV2 {

    /**
     * 컨텍스트
     */
    private Context mContext;

    private final View mProduct01, mProduct02;
    private int position = -1;
    private final View mViewBottomDivider1dp;
    private final View mViewBottomDivider10dp;

    /**
     * 전체보기 뷰의 전체 레이아웃
     */
    private final View mBtnViewAll;

    private boolean isAdult = false;

    /**
     * 상품이미지
     */
    private ImageView mainImg;

    /**
     * 캐시할 이미지 리스트
     */
    private List<String> mImgListForCache = new ArrayList<String>();

    /**
     * @param itemView itemView
     */
    public Prd2VH(View itemView) {
        super(itemView);

        mProduct01 = itemView.findViewById(R.id.content_frame_1st);
        mProduct02 = itemView.findViewById(R.id.content_frame_2nd);

        mViewBottomDivider1dp = itemView.findViewById(R.id.view_bottom_divider_1dp);
        mViewBottomDivider10dp = itemView.findViewById(R.id.view_bottom_divider_10dp);

        mBtnViewAll = itemView.findViewById(R.id.view_show_all);
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        this.mContext = context;
        this.navigationId = info.naviId;

        SectionContentList item = info.contents.get(position).sectionContent;

        boolean isSameToNext = false;

        if (info.contents.size() > position + 1) {
            final SectionContentList nextItem = info.contents.get(position + 1).sectionContent;
            if (nextItem != null && !TextUtils.isEmpty(nextItem.viewType) &&
                    item != null && !TextUtils.isEmpty(item.viewType) &&
                    item.viewType.equals(nextItem.viewType)) {
                isSameToNext = true;
            }

            // 다음 뷰타입이 더보기 이면 isSameToNext 인걸로 취급
            if (nextItem != null && ("TAB_SLD_GBB_MORE".equals(nextItem.viewType) ||
                    "BAN_MORE_GBA".equals(nextItem.viewType) || "PRD_1_640".equals(nextItem.viewType))) {
                isSameToNext = true;
            }
        }

        setItems(context, item, isSameToNext);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);
        this.mContext = context;

        final ModuleList item = moduleList.get(position);
        boolean isSameToNext = false;
        if (moduleList.size() > position + 1) {
            final ModuleList nextItem = moduleList.get(position + 1);
            if (nextItem != null && !TextUtils.isEmpty(nextItem.viewType) &&
                    item != null && !TextUtils.isEmpty(item.viewType) &&
                    item.viewType.equals(nextItem.viewType)) {
                isSameToNext = true;
            }

            // 다음 뷰타입이 더보기 이면 isSameToNext 인걸로 취급
            if (nextItem != null && "TAB_SLD_GBB_MORE".equals(nextItem.viewType)) {
                isSameToNext = true;
            }
        }
        this.position = position;

        setItems(context, item, isSameToNext);
    }

    public void setItems(final Context context, final SectionContentList item) {
        setItems(context, item, false);
    }

    public void setItems(final Context context, final SectionContentList item, boolean isSameToNext) {
        // 첫 줄 if 구문은 GS X Brand 개인화 매장만 예외적으로 유요함.
        if (item.isSameItem) {
            mViewBottomDivider1dp.setVisibility(View.VISIBLE);
            mViewBottomDivider10dp.setVisibility(View.GONE);
        } else {
            if (isSameToNext) {
                mViewBottomDivider1dp.setVisibility(View.VISIBLE);
                mViewBottomDivider10dp.setVisibility(View.GONE);
            } else {
                mViewBottomDivider1dp.setVisibility(View.GONE);
                mViewBottomDivider10dp.setVisibility(View.VISIBLE);
            }
        }

        String adult = CookieUtils.getAdult(MainApplication.getAppContext());

        if ("true".equals(adult) || "temp".equals(adult)) {
            isAdult = true;
        }

        if (item.subProductList == null) {
            return;
        }
        try {
            if (item.subProductList.size() > 0) {
                mImgListForCache.clear();
                updateProduct(context, mProduct01, item.subProductList.get(0));
            }

            View viewPrd = (LinearLayout) mProduct02.findViewById(R.id.view_product);
            if (item.subProductList.size() > 1) {
                updateProduct(context, mProduct02, item.subProductList.get(1));
                viewPrd.setVisibility(View.VISIBLE);
            } else {
                viewPrd.setVisibility(View.GONE);
            }
        } catch (IndexOutOfBoundsException e) {
            Ln.e(e.getMessage());
        }

        // 더보기 버튼 URL 있으면 보이게끔.
        if (EmptyUtils.isEmpty(item.moreBtnUrl)) {
            mBtnViewAll.setVisibility(View.GONE);
        } else {
            mBtnViewAll.setVisibility(View.VISIBLE);
            mBtnViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, item.moreBtnUrl);
                }
            });
        }
    }

    private void updateProduct(final Context context, View contentView, final SectionContentList item) {
        mImgListForCache.add(item.imageUrl);
        final String mUrl = item.imageUrl;

        View clickView = contentView.findViewById(R.id.view_product);
        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Keys.INTENT.IMAGE_URL, mUrl.replace(IMG_CACHE_RPL2_FROM, IMG_CACHE_RPL_TO));
                intent.putExtra(Keys.INTENT.HAS_VOD, item.hasVod);
                WebUtils.goWeb(context, item.linkUrl, intent);
            }
        });

        ImageView prdImage = (ImageView) contentView.findViewById(R.id.image_prd);
        ImageUtil.loadImageResize(context, item.imageUrl, prdImage, R.drawable.noimage_166_166);
        mainImg = prdImage;

        if (!TextUtils.isEmpty(item.adultCertYn) && "Y".equalsIgnoreCase(item.adultCertYn) && !isAdult) {
            prdImage.setImageResource(R.drawable.s_19_image_166_166);
        }

        //방송편성시간
        TextView txtBrdTime = contentView.findViewById(R.id.text_brd_time);

        //방송편성정보 있을경우
        if (DisplayUtils.isValidString(item.broadTimeText)) {
            txtBrdTime.setText(item.broadTimeText);
            txtBrdTime.setVisibility(View.VISIBLE);
        } else {
            txtBrdTime.setVisibility(View.GONE);
        }

        TextView prdComment = (TextView) contentView.findViewById(R.id.txt_comment);

        prdComment.setVisibility(View.GONE);
        if (item.directOrdInfo == null) {
            prdComment.setVisibility(View.VISIBLE);
            if (AIR_BUY.equalsIgnoreCase(item.imageLayerFlag)) {
                prdComment.setText(R.string.layer_flag_air_buy);
            } else if (SOLD_OUT.equalsIgnoreCase(item.imageLayerFlag)) {
                prdComment.setText(R.string.layer_flag_sold_out);
            } else {
                prdComment.setVisibility(View.GONE);
            }
        }

        // 순위 imgBadgeCorner.LT.text
        setRankText(contentView, item);
        //가격표시용 공통모듈에 맞게 데이타 변경
        ProductInfo info = SetDtoUtil.setDto(item);

        RenewalLayoutProductInfoPrd2 layoutProductInfo = contentView.findViewById(R.id.layout_product_info);
        layoutProductInfo.setViews(info, SetDtoUtil.BroadComponentType.product, navigationId);
    }

    protected void setRankText(View contentView, SectionContentList item) {
        // 순위 imgBadgeCorner.LT.text
        int rank = 0;
        TextView rankText = (TextView) contentView.findViewById(R.id.text_rank);
        if (item != null && item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()) {
            rank = Integer.parseInt(item.imgBadgeCorner.LT.get(0).text);
            rankText.setText(Integer.toString(rank));
            rankText.setVisibility(View.VISIBLE);
        } else {
            rankText.setVisibility(View.GONE);
        }
    }

    //O1 이미지는 L1 이미지와 비율이 동일하고 사이즈만 작기때문에 캐싱이 필요하지 않을 수 있음
    //지금BEST O1 이미지 사용, 새벽배송 하단은 L1 이미지 사용

    /**
     * 이미지가 화면상에 노출된 경우에 한해 이미지 캐시작업을 수행한다.
     *
     * @param event ImageCacheStartEvent
     */
    public void onEvent(Events.ImageCacheStartEvent event) {
        if (isNotEmpty(navigationId) && !navigationId.equals(event.naviId)) {
            //내가 속한 매장에 대한 이벤트가 아니면 스킵
            return;
        }
        if (DisplayUtils.isVisible(mainImg)) {
            for (int i = 0; i < mImgListForCache.size(); i++) {
                if (isNotEmpty(mImgListForCache.get(i)) && mImgListForCache.get(i).contains(IMG_CACHE_RPL2_FROM)) {
                    String imgUrl = mImgListForCache.get(i).replace(IMG_CACHE_RPL2_FROM, IMG_CACHE_RPL_TO);
                    Glide.with(mContext).load(trim(imgUrl)).downloadOnly(IMG_CACHE_WIDTH, IMG_CACHE_HEIGHT);
                }
            }
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
