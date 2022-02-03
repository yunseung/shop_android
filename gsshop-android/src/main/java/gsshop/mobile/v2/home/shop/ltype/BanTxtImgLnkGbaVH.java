package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.view.View;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;

/**
 * 해당 ViewHolder 는 Flexible, GSChoice 두 곳에서 다 사용됨.
 * 따라서 BaseViewHolderV2 가 BaseViewHolder 를 상속받도록 만들어서
 * productList, moduleList 별로 각각 onBindViewHolder 를 처리하도록 만들어놨음.
 * Flexible, GSChoice 둘 중에 무슨 타입이냐에 따라서 아래 각각의 onBideViewHolder 를 탄다. 아래 두 메서드 확인.
 * 2019.12.03 yun
 */
public class BanTxtImgLnkGbaVH extends BaseViewHolderV2 {

    private final CommonTitleLayout mCommonTitleLayout;

    public BanTxtImgLnkGbaVH(View itemView, String naviId) {
        super(itemView);
        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);

    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);
        mCommonTitleLayout.setCommonTitle(this, moduleList.get(position));
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        mCommonTitleLayout.setCommonTitle(this, info.contents.get(position).sectionContent);
    }
}