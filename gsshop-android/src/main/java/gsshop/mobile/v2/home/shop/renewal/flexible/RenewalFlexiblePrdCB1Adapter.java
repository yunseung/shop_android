package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfo;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.SwipeUtils;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.BaseViewHolder.IMG_CACHE_RPL3_FROM;
import static gsshop.mobile.v2.home.shop.BaseViewHolder.IMG_CACHE_RPL_TO;

public class RenewalFlexiblePrdCB1Adapter extends RecyclerView.Adapter<RenewalFlexiblePrdCB1Adapter.ProductViewHolder> {

    private int length;
    private List<SectionContentList> subProductList;
    private final Context mContext;
    private final String action;
    private final String label;
    private String naviId;
    private boolean isAdult;

    protected SectionContentList mItem;

    /**
     * @param context
     * @param subProductList
     * @param action
     * @param label
     */
    public RenewalFlexiblePrdCB1Adapter(Context context, List<SectionContentList> subProductList, String action, String label, String naviId) {
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
    public RenewalFlexiblePrdCB1Adapter(Context context, List<SectionContentList> subProductList, int length, String action, String label, String naviId) {
        this.mContext = context;
        this.subProductList = subProductList;
        this.length = length;
        this.action = action;
        this.label = label;
        this.naviId = naviId;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.renewal_home_row_type_fx_prd_c_b1_item, viewGroup, false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        SectionContentList item = subProductList.get(position);
        this.mItem = item;

        String adult = CookieUtils.getAdult(MainApplication.getAppContext());

        holder.setImageForCache(item.imageUrl);

        if ("true".equals(adult) || "temp".equals(adult)) {
            isAdult = true;
        }


        holder.mLayoutProductInfo.setViews(SetDtoUtil.setDto(item), SetDtoUtil.BroadComponentType.product_c_b1);

        // 마지막 상품 이후에 더 많은 상품이 있으면 viewType 의 MORE 값을 보고 상품 이미지 안에 상품 더보기 텍스트를 보인다.
        if (item.viewType != null) {
            if (item.viewType.contains("MORE")) {
                holder.mTvReadMore.setVisibility(View.VISIBLE);
            }
        }

        // imageLayerFlag 의 값을 보고 일시품절 영역을 표시 할 지 안 할 지의 로직.
        if (item.imageLayerFlag != null) {
            if (item.imageLayerFlag.equalsIgnoreCase("SOLD_OUT")) {
                holder.mLlSoldOut.setVisibility(View.VISIBLE);
            } else {
                holder.mLlSoldOut.setVisibility(View.GONE);
            }
        }

        holder.mRoot.setOnClickListener(v -> {
            holder.mRoot.setScaleX(1);
            holder.mRoot.setScaleY(1);

            Intent intent = new Intent();
            intent.putExtra(Keys.INTENT.IMAGE_URL, isNotEmpty(holder.getImageForCache()) ? holder.getImageForCache().replace(IMG_CACHE_RPL3_FROM, IMG_CACHE_RPL_TO) : "");
            WebUtils.goWeb(mContext, item.linkUrl, intent);

            // 트래킹 하는 구문 우선 뺍니다.
//                String tempLabel = String.format("%s_%s_%s_%s", label, category, position, item.linkUrl);
//                GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
        });

        holder.mRoot.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                SwipeUtils.INSTANCE.disableSwipe();
                holder.mRoot.setScaleX(0.96f);
                holder.mRoot.setScaleY(0.96f);
            }
            return false;
        });

        ImageUtil.loadImageFit(mContext, item.imageUrl, holder.mIvProduct, R.drawable.noimage_375_188);

        if (!TextUtils.isEmpty(item.adultCertYn) && "Y".equalsIgnoreCase(item.adultCertYn) && !isAdult) {
            holder.mIvProduct.setImageResource(R.drawable.s_19_image_375_188);
        }
    }

    @Override
    public int getItemCount() {
        return subProductList == null ? 0 : length < 0 ? subProductList.size() : length;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public View mRoot;
        public ImageView mIvProduct;
        public TextView mTvReadMore;
        public LinearLayout mLlSoldOut;
        public RenewalLayoutProductInfo mLayoutProductInfo;
        public String mImgForCache;


        public ProductViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView.findViewById(R.id.root);
            mIvProduct = itemView.findViewById(R.id.iv_product);
            mTvReadMore = itemView.findViewById(R.id.tv_read_more);
            mLlSoldOut = itemView.findViewById(R.id.ll_sold_out);
            mLayoutProductInfo = itemView.findViewById(R.id.layout_product_info);
        }

        public void setImageForCache(String url) {
            this.mImgForCache = url;
        }

        public String getImageForCache() {
            return isNotEmpty(mImgForCache) ? mImgForCache : "";
        }
    }


}
