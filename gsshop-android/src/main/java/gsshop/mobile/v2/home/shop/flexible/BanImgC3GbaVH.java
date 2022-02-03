package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

class BanImgC3GbaVH extends BaseViewHolder {

    private final TextView titleView;
    private final RecyclerView recyclerView;
    private final ThemeAdapter adapter;

    public BanImgC3GbaVH(View itemView) {
        super(itemView);
        titleView = (TextView) itemView.findViewById(R.id.text_theme_keyword_title);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_theme_keyword);
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ThemeAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = dp2px(5.5f);
                outRect.right = dp2px(5.5f);
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.left = dp2px(0f);
                } else if (position == adapter.getItemCount() - 1) {
                    outRect.right = dp2px(0f);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        SectionContentList item = info.contents.get(position).sectionContent;
        if (isEmpty(item)) {
            return;
        }

        titleView.setText(item.productName);
        adapter.updateProducts(item.subProductList);

    }

    private static class ThemeAdapter extends RecyclerView.Adapter<ThemeViewHolder> {

        private List<SectionContentList> themes;

        public void updateProducts(List<SectionContentList> themes) {
            this.themes = themes;
            notifyDataSetChanged();
        }

        @Override
        public ThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_tv_shopping_theme_keyword_theme, parent, false);
            int width = (parent.getContext().getResources().getDisplayMetrics().widthPixels - dp2px(10 * 2 + 11 * 2)) / 3;
            float ratio = (float) (135.0 / 106.0);
            rootView.getLayoutParams().width = width;
            rootView.getLayoutParams().height = (int) (width * ratio);
            rootView.requestLayout();

            return new ThemeViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ThemeViewHolder holder, int position) {
            final SectionContentList theme = themes.get(position);
            ImageUtil.loadImageFit(holder.itemView.getContext(), theme.imageUrl, holder.themeImage, R.drawable.noimg_list);
            holder.themeText.setText(theme.productName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(v.getContext(), theme.linkUrl);
                }
            });
        }

        @Override
        public int getItemCount() {
            return isEmpty(themes) ? 0 : themes.size();
        }
    }

    private static class ThemeViewHolder extends RecyclerView.ViewHolder {

        public final ImageView themeImage;
        public final TextView themeText;

        public ThemeViewHolder(View itemView) {
            super(itemView);
            View view = itemView.findViewById(R.id.view_theme);
            themeImage = (ImageView) itemView.findViewById(R.id.image_theme_keyword);
            themeText = (TextView) itemView.findViewById(R.id.text_theme_keyword_text);

        }
    }
}
