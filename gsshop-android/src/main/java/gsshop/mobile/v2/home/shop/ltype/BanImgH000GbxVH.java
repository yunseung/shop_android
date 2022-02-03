package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 이미지 배너 V2
 */
public class BanImgH000GbxVH extends BaseViewHolderV2 {
    private final ImageView main_img;
    private final View bottom_margin;

    public BanImgH000GbxVH(View itemView) {
        super(itemView);
        main_img = itemView.findViewById(R.id.main_img);
        bottom_margin = itemView.findViewById(R.id.bottom_margin);
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_img);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);
        ModuleList thisModulList = moduleList.get(position);
        int height = context.getResources()
                .getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_is_height);

        ViewUtils.hideViews(bottom_margin);

        //VIEW_TYPE_BAN_IMG_H000_GBD 타입이면 하단여백 노출
        if(thisModulList.tempViewType == ViewHolderType.VIEW_TYPE_BAN_IMG_H000_GBD) {
            ViewUtils.showViews(bottom_margin);
        }

        main_img.getLayoutParams().height = height;
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_img);

        ImageUtil.loadImageResize(context, thisModulList.imageUrl, main_img, R.drawable.noimg_logo);

        if(!TextUtils.isEmpty(thisModulList.name)) {
            main_img.setContentDescription(thisModulList.name);
        }

        main_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (thisModulList.linkUrl != null && thisModulList.linkUrl.length() > 4) {
                    WebUtils.goWeb(context, thisModulList.linkUrl);
                }
            }
        });
    }
}
