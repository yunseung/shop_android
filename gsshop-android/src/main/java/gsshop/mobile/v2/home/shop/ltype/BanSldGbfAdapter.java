/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;

/**
 * 추천딜 리스트 어뎁터.
 */
public class BanSldGbfAdapter extends RecyclerView.Adapter<BanSldGbfAdapter.RecommendViewHolder> {

    private int length;
    private List<SectionContentList> subProductList;
    private final Context mContext;
    private final String action;
    private final String label;
    private String category;

    /**
     * @param context
     * @param subProductList
     * @param action
     * @param label
     */
    public BanSldGbfAdapter(Context context, List<SectionContentList> subProductList, String action, String label) {
        this(context, subProductList, -1, action, label);
    }

    /**
     * 리스트이 length 추가.
     *
     * @param context
     * @param subProductList
     * @param length
     * @param action
     * @param label
     */
    public BanSldGbfAdapter(Context context, List<SectionContentList> subProductList, int length, String action, String label) {
        this.mContext = context;
        this.subProductList = subProductList;
        this.length = length;
        this.action = action;
        this.label = label;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setItem(ArrayList<SectionContentList> subProductList) {
        this.subProductList = subProductList;
    }

    @Override
    public RecommendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_gschoice_gbf_list, viewGroup, false);

        return new RecommendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecommendViewHolder holder, final int position) {
        final SectionContentList item = subProductList.get(position);

        ImageUtil.loadImageFit(mContext, item.imageUrl, holder.product_img, R.drawable.noimg_logo);

        TextView info_text = holder.info_text;
        info_text.setText(item.productName);

        TextView price = holder.price;
        price.setText(item.salePrice);

        TextView priceUnit = holder.price_unit;

        if (DisplayUtils.isValidString(item.salePrice)) {
            price.setText(item.salePrice);
            price.setVisibility(View.VISIBLE);

            priceUnit.setText(item.exposePriceText);
            price.setVisibility(View.VISIBLE);
        } else {
            price.setVisibility(View.GONE);
            priceUnit.setVisibility(View.GONE);
        }

        holder.root.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WebUtils.goWeb(mContext, item.linkUrl);

                //20151115 이민수 GA GTM 트래킹 코드 삽입 ( 베딜의 추천딜 )
//                String tempLabel = String.format("%s_%s_%s_%s", label, category, position, item.linkUrl);
//                GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
            }
        });

        final List<ImageBadge> listBadgeBottom = item.imgBadgeCorner.RB;
        if (listBadgeBottom.size() == 0) {
            ViewUtils.hideViews(holder.viewBadgeText);
        }
        else {
            ViewUtils.showViews(holder.viewBadgeText);
            if (listBadgeBottom.size() > 1) {
                holder.txtBadge1.setText(listBadgeBottom.get(0).text);
                holder.txtBadge2.setText(listBadgeBottom.get(1).text);
            }
            else {
                holder.txtBadge1.setText(listBadgeBottom.get(0).text);
                ViewUtils.hideViews(holder.viewSeperator, holder.txtBadge2);
            }
        }

        ViewUtils.hideViews(holder.badgeNew);

        if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()){
            final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
            if (badge != null && !"".equals(badge.imgUrl)) {
                ViewUtils.showViews(holder.badgeNew);
                ImageUtil.loadImageBadge(mContext, badge.imgUrl, holder.badgeNew, R.drawable.transparent, HD);
            }
        }
    }

    @Override
    public int getItemCount() {
        return subProductList == null ? 0 : length < 0 ? subProductList.size() : length;
    }

    public void setLength(int length) {
        if(this.length != length) {
            this.length = length;
            notifyDataSetChanged();
        }
    }

    /**
     * 뷰홀더
     */
    public static class RecommendViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout root;
        public ImageView product_img;
        public TextView info_text;
        public TextView price;
        public TextView price_unit;

        public View viewBadgeText;
        public TextView txtBadge1;
        public ImageView viewSeperator;
        public TextView txtBadge2;
        public ImageView badgeNew;

        public RecommendViewHolder(View itemView) {
            super(itemView);
            root = (RelativeLayout) itemView.findViewById(R.id.root);
            product_img = (ImageView) itemView.findViewById(R.id.product_img);
            info_text = (TextView) itemView.findViewById(R.id.info_text);
            price = (TextView) itemView.findViewById(R.id.price);
            price_unit = (TextView) itemView.findViewById(R.id.price_unit);

            viewBadgeText = itemView.findViewById(R.id.layout_text_badge);
            // 정보 레이아웃 : 상태값 표시 1
            txtBadge1 = (TextView) itemView.findViewById(R.id.text_badge_1);
            viewSeperator = (ImageView) itemView.findViewById(R.id.view_seperator);
            // 정보 레이아웃 : 상태값 표시 2
            txtBadge2 = (TextView) itemView.findViewById(R.id.text_badge_2);
            badgeNew = (ImageView) itemView.findViewById(R.id.top_badge);

            // resize 하면 오히려 찌그러 진다.
        }
    }
}