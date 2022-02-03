/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ltype.CommonPriceUtil;
import gsshop.mobile.v2.support.ui.TwoLineTextView;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.BaseViewHolder.IMG_CACHE_RPL3_FROM;
import static gsshop.mobile.v2.home.shop.BaseViewHolder.IMG_CACHE_RPL_TO;

/**
 * 추천딜 리스트 어뎁터.
 */
public class RtsPrdCCstSqAdapter extends RecyclerView.Adapter<RtsPrdCCstSqAdapter.RecommendViewHolder> {

    private int length;
    private List<SectionContentList> subProductList;
    private final Context mContext;
    private final String action;
    private final String label;
    private String category;
    private String naviId;

    private static final String VIEW_TYPE_READ_MORE = "_MORE";

    /**
     * @param context
     * @param subProductList
     * @param action
     * @param label
     */
    public RtsPrdCCstSqAdapter(Context context, List<SectionContentList> subProductList, String action, String label, String naviId) {
        this(context, subProductList, -1, action, label, naviId);
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
    public RtsPrdCCstSqAdapter(Context context, List<SectionContentList> subProductList, int length, String action, String label, String naviId) {
        this.mContext = context;
        this.subProductList = subProductList;
        this.length = length;
        this.action = action;
        this.label = label;
        this.naviId = naviId;
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
                    .inflate(R.layout.renewal_recycler_item_stream_recommand, viewGroup, false);
        return new RecommendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecommendViewHolder holder, final int position) {
        final SectionContentList item = subProductList.get(position);

        ImageUtil.loadImageFit(mContext, item.imageUrl, holder.product_img, R.drawable.no_img_550);

        holder.setImageForCache(item.imageUrl);

        TextView info_text = holder.info_text;
        info_text.setText(item.productName);

        //가격영역 숨기기로 함
        //CommonPriceUtil.getInstance().setPriceRule(mContext, item, holder.root, info_text, holder.price, holder.price_unit);
        holder.layPrice.setVisibility(View.GONE);


        // viewType에 _MORE 이 존재하면 그 뷰는 더보기 이다.
        if (!TextUtils.isEmpty(item.viewType) && item.viewType.contains(VIEW_TYPE_READ_MORE)) {
            holder.viewProductInfo.setVisibility(View.GONE);
            holder.textDimed.setVisibility(View.VISIBLE);
        }
        else {
            holder.viewProductInfo.setVisibility(View.VISIBLE);
            holder.textDimed.setVisibility(View.GONE);
        }

        //브랜드명 노출
        if (DisplayUtils.isValidString(item.productName) && info_text != null) {
            info_text.setText(item.productName);
            if (DisplayUtils.isValidString(item.brandNm)) {
                SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
                titleStringBuilder.append(item.brandNm);
                titleStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleStringBuilder.append(" " + item.productName);
                info_text.setText(titleStringBuilder);
            } else {
                info_text.setText(item.productName);
            }
        }

        holder.root.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.root.setScaleX(1);
                holder.root.setScaleY(1);

                Intent intent = new Intent();
                intent.putExtra(Keys.INTENT.IMAGE_URL, isNotEmpty(holder.getImageForCache()) ? holder.getImageForCache().replace(IMG_CACHE_RPL3_FROM, IMG_CACHE_RPL_TO) : "");
                WebUtils.goWeb(mContext, item.linkUrl, intent);

                // 트래킹 하는 구문 우선 뺍니다.
//                String tempLabel = String.format("%s_%s_%s_%s", label, category, position, item.linkUrl);
//                GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
            }
        });

        holder.root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    holder.root.setScaleX(0.96f);
                    holder.root.setScaleY(0.96f);
                }
                return false;
            }
        });

        //스트리밍 뷰카운트 세팅
        if(DisplayUtils.isValidString(item.streamViewCount)){
            holder.textViewCount.setText(item.streamViewCount);
            holder.layViewCount.setVisibility(View.VISIBLE);
        }else{
            holder.layViewCount.setVisibility(View.GONE);
        }

    }

    public String getPrdIdAtPosition(int position) {
        if (!TextUtils.isEmpty(subProductList.get(position).dealNo)) {
            if (!subProductList.get(position).dealNo.equals("0")) {
                return subProductList.get(position).dealNo;
            } else {
                return subProductList.get(position).prdid;
            }
        } else {
            return subProductList.get(position).prdid;
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
     * RTS_PRD_C_CST_SQ 아이템을 새로고침
     * @param rtsData 새로고침할 아이템
     */
    public synchronized void refreshInfoRTS(SectionContentList rtsData) {
        if(rtsData != null && rtsData.subProductList != null && rtsData.subProductList.size() > 0){
            subProductList = rtsData.subProductList;
            notifyDataSetChanged();
        }
    }

    /**
     * 뷰홀더
     */
    public static class RecommendViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout root;
        public View viewProductInfo;
        public ImageView product_img;
        public TextView info_text;
        public TextView price;
        public TextView price_unit;
        public TwoLineTextView textDimed;
        public String mImgForCache;
        public LinearLayout layViewCount;
        public TextView textViewCount;
        public LinearLayout layPrice;


        public RecommendViewHolder(View itemView) {
            super(itemView);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            viewProductInfo = itemView.findViewById(R.id.layout_product_info);
            product_img = (ImageView) itemView.findViewById(R.id.product_img);
            info_text = (TextView) itemView.findViewById(R.id.info_text);
            price = (TextView) itemView.findViewById(R.id.price);
            price_unit = (TextView) itemView.findViewById(R.id.price_unit);
            textDimed = (TwoLineTextView) itemView.findViewById(R.id.text_dimd);
            layViewCount = (LinearLayout)itemView.findViewById(R.id.layout_view_count);
            textViewCount = (TextView)itemView.findViewById(R.id.text_view_count);
            layPrice = (LinearLayout)itemView.findViewById(R.id.lay_price);
        }

        public void setImageForCache(String url) {
            this.mImgForCache = url;
        }

        public String getImageForCache() {
            return isNotEmpty(mImgForCache) ? mImgForCache : "";
        }
    }
}