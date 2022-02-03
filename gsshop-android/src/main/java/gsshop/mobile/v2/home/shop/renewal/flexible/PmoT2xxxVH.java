package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.SwipeUtils;
import gsshop.mobile.v2.web.WebUtils;

public class PmoT2xxxVH extends BaseViewHolder {
    private static final int DIVIDER_SPACE_01 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_side);
    private static final int DIVIDER_SPACE_02 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_between);

    private RecyclerView mProductRecyclerView;
    private ImageView mPromotionImage;
    private RecyclerView.ItemDecoration itemDecoration;

    public PmoT2xxxVH(View itemView) {
        super(itemView);

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        mPromotionImage = itemView.findViewById(R.id.iv_promotion_image);
        mProductRecyclerView = itemView.findViewById(R.id.recycler_list);
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        final SectionContentList item = info.contents.get(position).sectionContent;

        if (!TextUtils.isEmpty(item.imageUrl)) {
            ImageUtil.loadImageResize(context, item.imageUrl, mPromotionImage, R.drawable.noimage_375_188);
            mPromotionImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WebUtils.goWeb(context, item.linkUrl);
                }
            });
        }

        if ("PMO_T2_IMG".equals(item.viewType)) {
            mProductRecyclerView.setVisibility(View.GONE);
        } else if ("PMO_T2_PREVIEW".equals(item.viewType)) {
            mProductRecyclerView.setVisibility(View.VISIBLE);

            if (itemDecoration == null) {            //아이템 간격 세팅
                itemDecoration = new SpacesItemDecoration();
                mProductRecyclerView.addItemDecoration(itemDecoration);
            }

            mProductRecyclerView.setHasFixedSize(true);

            LinearLayoutManager llm = new LinearLayoutManager(context);
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);

            mProductRecyclerView.setLayoutManager(llm);
            RenewalFlexiblePmoT2PreviewAdapter adapter = new RenewalFlexiblePmoT2PreviewAdapter(context, item.subProductList, action, label);
            mProductRecyclerView.setAdapter(adapter);

            mProductRecyclerView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            SwipeUtils.INSTANCE.disableSwipe();
                            break;
                        case MotionEvent.ACTION_UP :
                            setOnTouchUp();
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void setOnTouchUp() {
        for ( int i=0; i < mProductRecyclerView.getChildCount(); i++) {
            mProductRecyclerView.getChildAt(i).setScaleX(1);
            mProductRecyclerView.getChildAt(i).setScaleY(1);
        }
    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.left = DIVIDER_SPACE_01;
            }

            if (position == parent.getAdapter().getItemCount() - 1) {
                outRect.right = DIVIDER_SPACE_01;
            } else {
                outRect.right = DIVIDER_SPACE_02;
            }
        }
    }
}
