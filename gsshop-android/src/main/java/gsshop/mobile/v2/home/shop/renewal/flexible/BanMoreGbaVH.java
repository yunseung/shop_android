package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.web.WebUtils;

public class BanMoreGbaVH extends BaseViewHolder {
    private LinearLayout mRootView;
    private TextView mTvBrandName;
    private TextView mTvSubName;

    public BanMoreGbaVH(View itemView) {
        super(itemView);
        mRootView = itemView.findViewById(R.id.root_view);
        mTvBrandName = itemView.findViewById(R.id.tv_brand_name);
        mTvSubName = itemView.findViewById(R.id.tv_sub_name);
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        final SectionContentList sectionContent = info.contents.get(position).sectionContent;

        mTvBrandName.setText(setTextInfo(sectionContent.brandNm));
        mTvSubName.setText(setTextInfo(sectionContent.subName));

        if (!TextUtils.isEmpty(sectionContent.linkUrl)) {
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, sectionContent.linkUrl);
                }
            });
        }
    }

    private String setTextInfo(String text) {
        return TextUtils.isEmpty(text) ? "" : text;
    }
}
