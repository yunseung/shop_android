package gsshop.mobile.v2.menu.navigation;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gsshop.mobile.v2.R;

public class ViewHolderJBItem extends RecyclerView.ViewHolder {

    public final ImageView mItemImage;
    public final TextView mItemText;
    public final TextView mItemTextNew;
    public final View mViewLeftMargin;

    public ViewHolderJBItem(View itemView) {
        super(itemView);

        mItemImage = (ImageView)itemView.findViewById(R.id.image_jb_category_item);
        mItemText = (TextView)itemView.findViewById(R.id.text_jb_category_item);
        mItemTextNew = (TextView)itemView.findViewById(R.id.text_jb_category_item_new);
        mViewLeftMargin = itemView.findViewById(R.id.view_left_margin);
    }
}
