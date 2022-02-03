package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

public class AdaptorJBCategory extends RecyclerView.Adapter<ViewHolderJBItem> {

    /**
     * description이 new이면 N 뱃지 visible
     */
    private static final String ARG_TEXT_NEW = "new";

    private Context mContext;

    /**
     * JB 카테고리 리스트
     */
    private List<CateContentList> mList;

    AdaptorJBCategory(Context context, List<CateContentList> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolderJBItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_navigation_jb_category, parent, false);
        return new ViewHolderJBItem(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolderJBItem holder, int position) {
        if (isEmpty(mList)) {
            return;
        }
        ImageUtil.loadImageFit(mContext, mList.get(position).imageUrl, holder.mItemImage, R.drawable.brand_no_android);
//        holder.mItemImage.setBackground(new ShapeDrawable(new OvalShape()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mItemImage.setClipToOutline(true);
        }

        holder.mItemText.setText(mList.get(position).title);

        if(ARG_TEXT_NEW.equalsIgnoreCase(mList.get(position).description)) {
            holder.mItemTextNew.setVisibility(View.VISIBLE);
        }
        else {
            holder.mItemTextNew.setVisibility(View.GONE);
        }

        if (position < 2) {
            holder.mViewLeftMargin.setVisibility(View.VISIBLE);
        }
        else {
            holder.mViewLeftMargin.setVisibility(View.GONE);
        }

        String strConDescription = mList.get(position).title;
        if (TextUtils.isEmpty(strConDescription)) {
            strConDescription = mList.get(position).description;
        }
        if (!TextUtils.isEmpty(strConDescription)) {
            holder.mItemImage.setContentDescription(strConDescription);
        }

        holder.mItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "JB 이동! : " + mList.get(position).gb, Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new Events.NavigationCloseEvent());
                WebUtils.goWeb(mContext, mList.get(position).linkUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return isNotEmpty(mList) ? mList.size() : 0;
    }

    public void setData (List<CateContentList> list) {
        mList = list;
    }
}
