/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * CSP 이미지 배너.
 *
 * 참고
 * - root를 gone 처리시 영역이 그대로 유지되기 때문에 layout_content를 사용하여 영역을 노출/숨김 처리함
 */
@SuppressLint("NewApi")
public class BanImgF80XGbaVH extends BaseViewHolderV2 {

	private Context context;
	private final ImageView imgMain;
	private final FrameLayout layoutMain;
	/**
	 * @param itemView
	 */
	public BanImgF80XGbaVH(View itemView) {
		super(itemView);
		imgMain = itemView.findViewById(R.id.main_img);
		layoutMain = itemView.findViewById(R.id.root);
	}

	@Override
	public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
		super.onBindViewHolder(context, position, moduleList);

		this.context = context;
		final ModuleList content = moduleList.get(position);

		if (!TextUtils.isEmpty(content.productName)) {
			imgMain.setContentDescription(content.productName);
		}

		try {
			FrameLayout.LayoutParams parmas = (FrameLayout.LayoutParams) imgMain.getLayoutParams();
			if (parmas != null) {
				if (content.tempViewType == ViewHolderType.BANNER_TYPE_FIXED_80_IMG_L_GBA) {
					parmas.gravity = Gravity.LEFT;
				} else if (content.tempViewType == ViewHolderType.BANNER_TYPE_FIXED_80_IMG_R_GBA) {
					parmas.gravity = Gravity.RIGHT;
				} else if (content.tempViewType == ViewHolderType.BANNER_TYPE_FIXED_80_IMG_C_GBA) {
					parmas.gravity = Gravity.CENTER_HORIZONTAL;
				}
				imgMain.setLayoutParams(parmas);
			}
		}
		catch (ClassCastException e) {
			Ln.e(e.getMessage());
		}

		ImageUtil.loadImageResizeToHeight(context, content.imageUrl, imgMain, R.drawable.noimg_logo);

		imgMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(content.linkUrl)) {
					WebUtils.goWeb(context, content.linkUrl);
				}
			}
		});
	}
}
