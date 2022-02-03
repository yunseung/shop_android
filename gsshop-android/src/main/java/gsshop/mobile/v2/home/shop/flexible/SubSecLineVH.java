/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 띠 이미지 배너.
 *
 */
public class SubSecLineVH extends BaseViewHolder {

	/**
	 * bind
	 */
	private static final int[] IMAGE_VIEWS = { R.id.image_01, R.id.image_02, R.id.image_03,
			R.id.image_04, R.id.image_05, R.id.image_06, R.id.image_07, R.id.image_08 };

	private final ImageView[] imageViews;

	/**
	 * @param itemView
	 */
	public SubSecLineVH(View itemView) {
		super(itemView);
		imageViews = new ImageView[IMAGE_VIEWS.length];
//		borders = new View[BORDERS.length];

		for(int i = 0; i < IMAGE_VIEWS.length; i++) {
			imageViews[i] = (ImageView) itemView.findViewById(IMAGE_VIEWS[i]);
		}
	}



	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
			final String action, final String label, String sectionName) {
		
		ShopInfo.ShopItem content = info.contents.get(position);
		List<SectionContentList> list = content.sectionContent.subProductList;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if(i < IMAGE_VIEWS.length) {
					final SectionContentList item = list.get(i);
					ImageView image = imageViews[i];
					ImageUtil.loadImageFit(context, item.imageUrl, image, R.drawable.sub_sec_line_skeleton);
					String strConDescription = item.productName;
					if (TextUtils.isEmpty(strConDescription)) {
						strConDescription = item.gsAccessibilityVariable;
					}
					if (!TextUtils.isEmpty(strConDescription)) {
						image.setContentDescription(strConDescription);
					}
					image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							WebUtils.goWeb(context, item.linkUrl);

							//GTM 클릭이벤트 전달
							String tempLabel = String.format("%s_%s", label, item.productName);
							GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
						}
					});

				}
			}
		}

	}

}
