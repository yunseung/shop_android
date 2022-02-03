/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.util.IconTabBar;
import gsshop.mobile.v2.home.util.IconTabBar.IconTabListener;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import roboguice.util.Ln;

/**
 * 카테고리.
 */
@SuppressLint("NewApi")
public class FxclistVH extends BaseViewHolder {

    private final IconTabBar tabBar;

    /**
     * @param itemView
     */
    public FxclistVH(View itemView) {
        super(itemView);
        tabBar = (IconTabBar) itemView.findViewById(R.id.tab);
    }

    /* 카테고리. */
    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action,
                                 String label, String sectionName) {
        List<SubMenuList> subMenuList = info.sectionList.subMenuList;
        if(subMenuList == null){
            return;
        }
        int tab = info.tabIndex;
        tabBar.setIconTabListener(
                new CatetoryTabListener(context, subMenuList, tab, action, label));
        tabBar.setOnClick(true);
        tabBar.notifyDataSetChanged();

        // 홈으로 설정을 한다.
        tabBar.setSelectedTab(tab);
    }

    private static class CatetoryTabListener implements IconTabListener {
        private final Context context;
        private final List<SubMenuList> category;
        private final int tab;
        private boolean first = false;
        private final String action;
        private final String label;

        public CatetoryTabListener(Context context,
                                   List<SubMenuList> category, int tab, String action, String label) {
            super();
            this.context = context;
            this.category = category;
            this.tab = tab;
            this.action = action;
            this.label = label;
            first = true;
        }

        /**
         * @param position
         * @param selected
         * @return
         */
        @Override
        public Drawable getSelectedTabIconDrawable(int position, boolean selected) {
            String pathName;

            if (selected) {
                pathName = category.get(position).sectionImgOnUrl;
            } else {
                pathName = category.get(position).sectionImgOffUrl;
            }

            Drawable drawable;
            if (pathName.length() > 0) {
                drawable = Drawable.createFromPath(pathName);
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.getDrawable(R.drawable.gsshop_default);
                } else {
                    drawable = context.getResources().getDrawable(R.drawable.gsshop_default);
                }
            }

            return drawable;
        }

        /**
         * @param position
         * @param view
         */
        @Override
        public void onIconTabClicked(int position, View view) {
            //Ln.i("onIconTabClicked : " + position);
            if (!first && tab != position) {

                EventBus.getDefault().post(new Events.FlexibleEvent.UpdateFlexibleShopEvent(position, -1));

                if (category != null && category.get(position) != null) {
                    String tempLabel = String.format("%s_%s", label,
                            category.get(position).sectionName);
                    GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);

                    try {
                        ((HomeActivity) context).setWiseLog(category.get(position).wiseLogUrl);
                    } catch (Exception e) {
                        // 위험해!!
                        // 10/19 품질팀 요청
                        // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                        // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                        // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                        Ln.e(e);
                    }

                }

            }

            first = false;
        }

        /**
         * @return
         */
        @Override
        public int tabSize() {
            return category.size();
        }

    }

}
