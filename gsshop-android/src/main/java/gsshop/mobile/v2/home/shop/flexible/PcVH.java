package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

class PcVH extends BaseViewHolder {

    private final TextView titleView;
    private final RecyclerView recyclerView;
    private final ProgramAdapter adapter;
    private final LinearLayout root;

    public PcVH(View itemView) {
        super(itemView);
        root = (LinearLayout) itemView.findViewById(R.id.root);
        titleView = (TextView) itemView.findViewById(R.id.text_popular_title);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_popluar);
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ProgramAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = dp2px(11.5f);
                outRect.right = dp2px(11.5f);
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.left = dp2px(10f);
                } else if (position == adapter.getItemCount() - 1) {
                    outRect.right = dp2px(10f);
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
        root.setContentDescription(item.productName);
        adapter.updateProducts(item.subProductList);

    }

    private static class ProgramAdapter extends RecyclerView.Adapter<ProgramViewHolder> {

        private List<SectionContentList> programs;

        public void updateProducts(List<SectionContentList> programs) {
            this.programs = programs;
            notifyDataSetChanged();
        }

        @Override
        public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ProgramViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_tv_shopping_popular_program_schedule, parent, false));
        }

        @Override
        public void onBindViewHolder(ProgramViewHolder holder, int position) {
            final SectionContentList program = programs.get(position);
            ImageUtil.loadImageFit(holder.itemView.getContext(), program.imageUrl, holder.scheduleImage, R.drawable.noimg_list);
            holder.dateText.setText(program.promotionName);
            holder.titleText.setText(program.productName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(v.getContext(), program.linkUrl);
                }
            });
        }

        @Override
        public int getItemCount() {
            return isEmpty(programs) ? 0 : programs.size();
        }
    }

    private static class ProgramViewHolder extends RecyclerView.ViewHolder {

        public final ImageView scheduleImage;
        public final TextView dateText;
        public final TextView titleText;

        public ProgramViewHolder(View itemView) {
            super(itemView);
            scheduleImage = (ImageView) itemView.findViewById(R.id.image_popular_schedule);
            dateText = (TextView) itemView.findViewById(R.id.text_popular_schedule_date);
            titleText = (TextView) itemView.findViewById(R.id.text_popular_schedule_title);

            DisplayUtils.resizeWidthAtViewToScreenSize(itemView.getContext(), scheduleImage);
            DisplayUtils.resizeHeightAtViewToScreenSize(itemView.getContext(), scheduleImage);
        }
    }
}
