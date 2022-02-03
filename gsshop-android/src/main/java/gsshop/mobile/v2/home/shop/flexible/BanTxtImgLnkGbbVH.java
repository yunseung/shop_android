/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.view.View;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;


/**
 * 타이틀 배너2. B_TS 가 바뀐 버전. (내일도착에 이렇게 생긴애는 LNK_GBA (BanTxtImgLnkGbaVH.java) 임. 헷갈리지 말 것.
 * LNK_GBA 랑 여기 LNK_GBB 랑 타이틀 폰트 사이즈만 다르다......
 * 하나로 만들까 하다가 나누는게 더 명확하게 보일 거 같아서 따로 만듬.
 */
public class BanTxtImgLnkGbbVH extends BaseViewHolderV2 {

   private final CommonTitleLayout mCommonTitleLayout;

    /**
     * @param itemView
     * @param naviId
     */
    public BanTxtImgLnkGbbVH(View itemView, String naviId) {
        super(itemView);
        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);
        mCommonTitleLayout.setCommonTitle(this, moduleList.get(position));
    }

    /* bind */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        mCommonTitleLayout.setCommonTitle(this, info.contents.get(position).sectionContent);
    }

}
