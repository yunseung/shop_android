package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 이미지 배너 V2
 */
public class BanSldGbbMoreVH extends BaseViewHolderV2 {
    private final View goViewMore;

    public BanSldGbbMoreVH(View itemView) {
        super(itemView);
        goViewMore = itemView.findViewById(R.id.read_more_layout);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);
        ModuleList thisModulList = moduleList.get(position);

        if (goViewMore != null) {
            TextView text = goViewMore.findViewById(R.id.text_name);
            if (text != null) {
                text.setText(thisModulList.name + " ");
            }

            goViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (URLUtil.isValidUrl(thisModulList.moreBtnUrl)) {
                        WebUtils.goWeb(context, thisModulList.moreBtnUrl);

                    }
                }
            });
        }
    }
}
