package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

public class RenewalFlexiblePmoT2PreviewAdapter extends RecyclerView.Adapter<RenewalFlexiblePmoT2PreviewAdapter.ProductViewHolder>{

    private int length;
    private List<SectionContentList> subProductList;
    private final Context mContext;
    private final String action;
    private final String label;
    private String category;

    public RenewalFlexiblePmoT2PreviewAdapter(Context context, List<SectionContentList> subProductList, String action, String label) {
        this(context, subProductList, -1, action, label);
    }

    public RenewalFlexiblePmoT2PreviewAdapter(Context context, List<SectionContentList> subProductList, int length, String action, String label) {
        this.mContext = context;
        this.subProductList = subProductList;
        this.length = length;
        this.action = action;
        this.label = label;
    }
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.renewal_home_row_type_fx_pmo_t2_preview_item, viewGroup, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final SectionContentList item = subProductList.get(position);

        ImageUtil.loadImageFit(mContext, item.imageUrl, holder.mIvProduct, R.drawable.noimage_166_166);

        if (!TextUtils.isEmpty(item.viewType)) {
            if (item.viewType.toUpperCase().contains("MORE")) {
                holder.mTvMore.setVisibility(View.VISIBLE);
            } else {
                holder.mTvMore.setVisibility(View.GONE);
            }
        }

        holder.mIvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebUtils.goWeb(mContext, item.linkUrl);
            }
        });
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setItem(ArrayList<SectionContentList> subProductList) {
        this.subProductList = subProductList;
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

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIvProduct;
        public TextView mTvMore;

        public ProductViewHolder(View itemView) {
            super(itemView);

            mIvProduct = itemView.findViewById(R.id.iv_product);
            mTvMore = itemView.findViewById(R.id.tv_more);
        }
    }
}
