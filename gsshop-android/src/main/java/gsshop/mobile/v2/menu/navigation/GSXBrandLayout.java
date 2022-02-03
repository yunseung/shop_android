package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.menu.navigation.Views.CardViewReduceWhenSelected;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * 네비게이션 카테고리 레이아웃 뷰
 */
public class GSXBrandLayout extends LinearLayout {

    private Context mContext;

    /**
     * GS X 카드 리스트
     */
    private RecyclerView mRecyclerView;

    /**
     * GS X 카드 리스트 어댑터
     */
    private AdapterGSXBrand mAdapterGSXBrand;

    private static int mScrolledPosition = 0;

    public GSXBrandLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public GSXBrandLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, null);
    }

    public GSXBrandLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView(context, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRecyclerView = (RecyclerView)findViewById(R.id.list_navi_gs_x_title);
        mRecyclerView.setLayoutManager(new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        mAdapterGSXBrand = new AdapterGSXBrand();
        mRecyclerView.setAdapter(mAdapterGSXBrand);
    }

    private void initView(Context context, ArrayList<CateContentList> itemList) {
        mContext = context;
    }

    public void setView(CateContentList item) {
//        mAdapterGSXBrand = new AdapterGSXBrand(itemList);
        mAdapterGSXBrand.setItem(item.subContentList);

        if (mRecyclerView == null) {
            mRecyclerView = (RecyclerView) findViewById(R.id.list_navi_gs_x_title);
            mRecyclerView.setAdapter(mAdapterGSXBrand);
        }

     try {
            TextView textTitle = (TextView) findViewById(R.id.txt_navi_gs_x_title);
            textTitle.setText(item.title);
        }
            catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        mAdapterGSXBrand.notifyDataSetChanged();
    }

    private class AdapterGSXBrand extends RecyclerView.Adapter<GSXViewHolder> {

        private List<CateContentList> mItemList = null;

        public AdapterGSXBrand() {}

        public AdapterGSXBrand(List<CateContentList> itemList) {
            mItemList = itemList;
        }

        @Override
        public GSXViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_navigation_gs_x_brand, parent, false);
            return new GSXViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(GSXViewHolder holder, int position) {
            if (isEmpty(mItemList)) {
                return;
            }

            if (position == 0) {
                holder.mViewTerm.setVisibility(VISIBLE);
            }
            else {
                holder.mViewTerm.setVisibility(GONE);
            }

            ImageUtil.loadImageFit(mContext, mItemList.get(position).imageUrl, holder.mitemImage, 0);

            String strConDescription = !TextUtils.isEmpty(mItemList.get(position).title) ? mItemList.get(position).title : "";

            if (TextUtils.isEmpty(strConDescription)) {
                strConDescription = !TextUtils.isEmpty(mItemList.get(position).description) ? mItemList.get(position).description : "";
            }

            if (!TextUtils.isEmpty(strConDescription)) {
                holder.mCardView.setContentDescription(strConDescription);
            }

            holder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    goWeb(mItemList.get(position).linkUrl);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (isEmpty(mItemList)) {
                return 0;
            }
            return mItemList.size();
        }

        public void setItem(List<CateContentList> itemList) {
            mItemList = itemList;
        }

        public CateContentList getItem(int position) {
            try {
                return mItemList.get(position);
            }
            catch (NullPointerException e) {
                return null;
            }
        }
    }

    private static class GSXViewHolder extends RecyclerView.ViewHolder {

        public final CardViewReduceWhenSelected mCardView;
        public final ImageView mitemImage;
        public final View mViewTerm;

        public GSXViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardViewReduceWhenSelected)itemView.findViewById(R.id.cardview_gs_x_brand);
            mitemImage = (ImageView)itemView.findViewById(R.id.imageview_gs_x_brand);
            mViewTerm = itemView.findViewById(R.id.view_left_margin);
        }
    }

    public RecyclerView getGSXBrandRecyclerView() {
        return mRecyclerView;
    }

    private void goWeb(String Url) {
        WebUtils.goWeb(mContext, Url);
        EventBus.getDefault().post(new Events.NavigationCloseEvent());
    }

    public void setPosition(int position) {
        mScrolledPosition = position;
    }

    public void savePosition() {
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager)mRecyclerView.getLayoutManager();
        mScrolledPosition = layoutManager.findFirstVisibleItemPositions(null)[0];
    }

    public void setSavedPosition() {
        if (!(mScrolledPosition < 0 )) {
            try {
                mRecyclerView.scrollToPosition(mScrolledPosition + 1);
            }
            catch (IndexOutOfBoundsException e) {
                Ln.e(e.getMessage());
            }
        }
    }
}
