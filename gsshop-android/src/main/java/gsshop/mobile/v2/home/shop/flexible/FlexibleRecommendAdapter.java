/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 추천딜 리스트 어뎁터.
 */
public class FlexibleRecommendAdapter extends RecyclerView.Adapter<FlexibleRecommendAdapter.RecommendViewHolder> {

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
    public FlexibleRecommendAdapter(Context context, List<SectionContentList> subProductList, String action, String label) {
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
    public FlexibleRecommendAdapter(Context context, List<SectionContentList> subProductList, int length, String action, String label) {
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
                .inflate(R.layout.recommend_item, viewGroup, false);

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
                String tempLabel = String.format("%s_%s_%s_%s", label, category, position, item.linkUrl);
                GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
            }
        });

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

        public LinearLayout root;
        public ImageView product_img;
        public TextView info_text;
        public TextView price;
        public TextView price_unit;

        public RecommendViewHolder(View itemView) {
            super(itemView);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            product_img = (ImageView) itemView.findViewById(R.id.product_img);
            info_text = (TextView) itemView.findViewById(R.id.info_text);
            price = (TextView) itemView.findViewById(R.id.price);
            price_unit = (TextView) itemView.findViewById(R.id.price_unit);
        }
    }
}