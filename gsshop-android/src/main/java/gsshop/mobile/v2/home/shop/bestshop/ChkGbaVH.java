/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.StringUtils;

import static gsshop.mobile.v2.web.WebUtils.BEST_NOW_CHK_IS_ALL_VIEW_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.MSEQ_PARAM_KEY;

/**
 *
 * 전체 배스트 시간 추가된 텍스트 베너
 */
public class ChkGbaVH extends BaseViewHolder {
    private View layout_checkbox;
    private TextView txt_best_now_description, txt_best_now_view_all;
    private CheckBox check_view_all;
    private final String TAG = "ChkGbaVH";

    private static final String MSEQ_IS_ALL = "A00204-ALL";

    /**
     * @param itemView itemView
     */
    public ChkGbaVH(View itemView) {
        super(itemView);
        txt_best_now_description = (TextView) itemView.findViewById(R.id.txt_best_now_description);
        txt_best_now_view_all = (TextView) itemView.findViewById(R.id.txt_best_now_view_all);
        check_view_all = (CheckBox) itemView.findViewById(R.id.check_view_all);
        check_view_all.setClickable(false);
        layout_checkbox = (View) itemView.findViewById(R.id.layout_checkbox);
        layout_checkbox.setClickable(true);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), check_view_all);
    }

    /**
     * @param context     context
     * @param position    position
     * @param info        info
     * @param action      action
     * @param label       label
     * @param sectionName sectionName
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        final SectionContentList content = info.contents.get(position).sectionContent;

        layout_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_view_all.setChecked(!check_view_all.isChecked());
            }
        });

        check_view_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String strCheck = isChecked ? "true" : "false";
                String strMseq = isChecked ? MSEQ_IS_ALL : null;

                String params = StringUtils.replaceUriParameter(
                        Uri.parse(content.linkUrl),
                        BEST_NOW_CHK_IS_ALL_VIEW_PARAM_KEY,
                        strCheck).toString();

                params = StringUtils.replaceUriParameter(Uri.parse(params),
                        MSEQ_PARAM_KEY, strMseq).toString();
//                Ln.i(TAG, params);
                EventBus.getDefault().post(new Events.FlexibleEvent.UpdateBestShopEvent(position, params, content.productName));
            }
        });

        // 전달 된 값에서 "isAllView" 값에 따라 체크 박스 여부 결정.
        String strAllViewParam = StringUtils.getUriParameter(content.linkUrl, BEST_NOW_CHK_IS_ALL_VIEW_PARAM_KEY);
        strAllViewParam = strAllViewParam != null ? strAllViewParam.trim() : strAllViewParam;

        if (!TextUtils.isEmpty(strAllViewParam)) {
            boolean isAllViewChecked = "true".equals(strAllViewParam) ? true : false;
            check_view_all.setChecked(isAllViewChecked);
        }

//        productName, promotionName
        txt_best_now_description.setText(content.productName);
        txt_best_now_view_all.setText(content.promotionName);
    }
}
