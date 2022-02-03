package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

public class BanNoPrdVH extends BaseViewHolder {
    private TextView mTvName;
    private TextView mTvSubName;

    public BanNoPrdVH(View itemView) {
        super(itemView);
        mTvName = itemView.findViewById(R.id.tv_name);
        mTvSubName = itemView.findViewById(R.id.tv_sub_name);
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        final SectionContentList sectionContent = info.contents.get(position).sectionContent;

        mTvName.setText(setTextInfo(sectionContent.name));
        mTvSubName.setText(setTextInfo(sectionContent.subName));
    }

    private String setTextInfo(String text) {
        return TextUtils.isEmpty(text) ? "" : text;
    }
}
