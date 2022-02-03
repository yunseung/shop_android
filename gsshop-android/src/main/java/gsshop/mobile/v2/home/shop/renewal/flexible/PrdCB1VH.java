package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.flexible.vip.PrdCB1VIPGbaAdapter;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import gsshop.mobile.v2.util.SwipeUtils;

public class PrdCB1VH extends BaseViewHolderV2 {
    private static final int DIVIDER_SPACE_01 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_side);
    private static final int DIVIDER_SPACE_02 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_between);

    private RecyclerView mProductRecyclerView;
    private RecyclerView.ItemDecoration itemDecoration;

    private final CommonTitleLayout mCommonTitleLayout;
    private View mIvBottomLine;
    private View itemView;

    public PrdCB1VH(View itemView, String naviId) {
        super(itemView);

        this.itemView = itemView;

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);

        mProductRecyclerView = itemView.findViewById(R.id.recycler_list);
        mIvBottomLine = itemView.findViewById(R.id.iv_bottom_line);
        navigationId = naviId;
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        final SectionContentList item = info.contents.get(position).sectionContent;
        setView(context, item, action, label, C_TYPE);
    }


    // L Type
    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        final SectionContentList item = moduleList.get(position);
        setView(context, item, null, null, L_TYPE);
    }

    public View getView(Context context, SectionContentList item, int type) {
        setView(context, item, null, null, type);
        return itemView;
    }

    public void setView(final Context context, SectionContentList item, final String action, final String label, int type) {
        mCommonTitleLayout.setCommonTitle(this, item);

        if (itemDecoration == null) {            //아이템 간격 세팅
            itemDecoration = new SpacesItemDecoration();
            mProductRecyclerView.addItemDecoration(itemDecoration);
        }

        mProductRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        mProductRecyclerView.setLayoutManager(llm);



        RenewalFlexiblePrdCB1Adapter adapter;
        // C, L Type 각각 DTO 형태가 달라서 분기해줌.

        if (C_TYPE_PRD_CB1_VIP == type) {
            //C 타입 중 PRD_CB1_VIP는 예외처리 필요
            adapter = new PrdCB1VIPGbaAdapter(context, item.subProductList, null, null, navigationId);
            ViewUtils.hideViews(mCommonTitleLayout, mIvBottomLine);
        } else if (C_TYPE == type) {
            adapter = new RenewalFlexiblePrdCB1Adapter(context, item.subProductList, null, null, navigationId);
        } else {
            adapter = new RenewalFlexiblePrdCB1Adapter(context, item.productList, null, null, navigationId);
        }
        mProductRecyclerView.setAdapter(adapter);

        mProductRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        SwipeUtils.INSTANCE.disableSwipe();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        setOnTouchUp();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void setOnTouchUp() {
        for (int i = 0; i < mProductRecyclerView.getChildCount(); i++) {
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
