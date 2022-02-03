package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;

public class BanTxtSubGbaVH extends BaseViewHolderV2 {
    private TextView mTvName;
    private TextView mTvSubName;

    public BanTxtSubGbaVH(View itemView) {
        super(itemView);

        mTvName = itemView.findViewById(R.id.tv_name);
        mTvSubName = itemView.findViewById(R.id.tv_sub_name);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleLists) {
        super.onBindViewHolder(context, position, moduleLists);

        ModuleList moduleList = moduleLists.get(position);

        if (TextUtils.isEmpty(moduleList.name)) {
            mTvName.setVisibility(View.GONE);
        } else {
            mTvName.setText(moduleList.name);
        }

        if (TextUtils.isEmpty(moduleList.subName)) {
            mTvSubName.setVisibility(View.GONE);
        } else {
            mTvSubName.setText(moduleList.subName);
        }
    }
}
