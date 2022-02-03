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
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;

/**
 * 카테고리.
 */
@SuppressLint("NewApi")
public class MapMutCategoryGbaVH extends BaseViewHolder {

    private final TextView[] category = new TextView[6];

    private TextView selectedButton;

    /**
     * @param itemView
     */
    public MapMutCategoryGbaVH(View itemView) {
        super(itemView);
        category[0] = (TextView) itemView.findViewById(R.id.category_1);
        category[1] = (TextView) itemView.findViewById(R.id.category_2);
        category[2] = (TextView) itemView.findViewById(R.id.category_3);
        category[3] = (TextView) itemView.findViewById(R.id.category_4);
        category[4] = (TextView) itemView.findViewById(R.id.category_5);
        category[5] = (TextView) itemView.findViewById(R.id.category_6);

    }

    /* 카테고리. */
    @Override
    public void onBindViewHolder(final Context context, final int position, ShopInfo info, final String action,
                                 final String label, String sectionName) {
        final List<SubMenuList> subMenuList = info.sectionList.subMenuList;
        if (subMenuList == null) {
            return;
        }
        int tab = info.tabIndex;
        int size = subMenuList.size();
        for (int i = 0; i < size && i < category.length; i++) {
            final int buttonPosition = i;
            category[i].setText(subMenuList.get(i).sectionName);

            if (i < size - 1 && i < category.length - 1) {
                if (i > 0) {
                    category[i].setBackgroundResource(R.drawable.bg_mart_category_middle_button);
                }
            }
            setButtonState(context, category[i], false);

            if (tab == i) {
                setButtonState(context, category[i], true);
            }

            category[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int size = subMenuList.size();
                    for (int i = 0; i < size && i < category.length; i++) {
                        setButtonState(context, category[i], false);
                    }

                    setButtonState(context, (TextView) view, true);
                    selectedButton = null;
                    EventBus.getDefault().post(new Events.FlexibleEvent.UpdateFlexibleShopEvent(buttonPosition, position));

                    //GTM 클릭이벤트 전달
                    String tempLabel = String.format("%s_%s", label, subMenuList.get(buttonPosition).sectionName);
                    GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
                    //와이즈로그 호출
                    ((HomeActivity) context).setWiseLog(subMenuList.get(buttonPosition).wiseLogUrl);
                }
            });
        }
    }

    /**
     * 버튼 상태를 변경한다.
     *
     * @param button
     * @param selected
     */
    private void setButtonState(Context context, TextView button, boolean selected) {
        button.setVisibility(View.VISIBLE);
        button.setSelected(selected);

        if (selected) {
            button.setTextColor(context.getResources().getColor(R.color.mart_deal_text_selected));
            button.setTextSize(context.getResources().getInteger(R.integer.mart_deal_text_size_selected));
            if (selectedButton != null && selectedButton != button) {
                setButtonState(context, selectedButton, false);
            }

            selectedButton = button;
        } else {
            button.setTextColor(context.getResources().getColor(R.color.mart_deal_text_default));
            button.setTextSize(context.getResources().getInteger(R.integer.mart_deal_text_size_default));
        }
    }

}
