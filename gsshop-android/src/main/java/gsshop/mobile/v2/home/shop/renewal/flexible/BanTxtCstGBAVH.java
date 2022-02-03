package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.view.View;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CustomTitleDTLayout;

public class BanTxtCstGBAVH extends BaseViewHolderV2 {

    // PRD_C_CST_SQ 뷰타입 타이틀 레이아웃
    private CustomTitleDTLayout mCustomTiltleDTLayout;

    public BanTxtCstGBAVH(View itemView) {
        super(itemView);
        mCustomTiltleDTLayout = itemView.findViewById(R.id.custom_title_dt_layout);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);
        final SectionContentList sectionContent = moduleList.get(position);

        mCustomTiltleDTLayout.setTitle(context, sectionContent);
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        final SectionContentList sectionContent = info.contents.get(position).sectionContent;

        mCustomTiltleDTLayout.setTitle(context, sectionContent);
    }
}
