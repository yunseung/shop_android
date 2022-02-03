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
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class BIg4xnVH extends BaseViewHolder {

	private static final int[] BORDERS = { R.id.view_01, R.id.view_01, R.id.view_02, R.id.view_03,
			R.id.view_04, R.id.view_05, R.id.view_06, R.id.view_07, R.id.view_08, R.id.view_09
			, R.id.view_10, R.id.view_11, R.id.view_12};
	private static final int[] IMAGE_VIEWS = {R.id.image_01, R.id.image_01, R.id.image_02, R.id.image_03,
			R.id.image_04, R.id.image_05, R.id.image_06, R.id.image_07, R.id.image_08 , R.id.image_09
			, R.id.image_10, R.id.image_11, R.id.image_12};

	private final ImageView[] imageViews;

	private final ImageView top_image;
	private final TextView bottom_button;
	private final LinearLayout bottom_layout;
	private final LinearLayout item_group_1;
	private final LinearLayout item_group_2;
	private final LinearLayout item_group_3;
	private final View line_group_1;
	private final View line_group_2;
	private final View line_group_3;

	/**
	 * @param itemView
	 */
	public BIg4xnVH(View itemView) {
		super(itemView);

		top_image = (ImageView)itemView.findViewById(R.id.top_img);
		bottom_layout = (LinearLayout)itemView.findViewById(R.id.bottom_layout);
		bottom_button = (TextView)itemView.findViewById(R.id.bottom_button);

		item_group_1 = (LinearLayout)itemView.findViewById(R.id.item_group_1);
		item_group_2 = (LinearLayout)itemView.findViewById(R.id.item_group_2);
		item_group_3 = (LinearLayout)itemView.findViewById(R.id.item_group_3);

		line_group_1 = itemView.findViewById(R.id.line_group_1);
		line_group_2 = itemView.findViewById(R.id.line_group_2);
		line_group_3 = itemView.findViewById(R.id.line_group_3);

		imageViews = new ImageView[IMAGE_VIEWS.length];

		for(int i = 0; i < IMAGE_VIEWS.length; i++) {
			imageViews[i] = (ImageView) itemView.findViewById(IMAGE_VIEWS[i]);
		}
	}

	/**
	 * bind
	 */


	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
			final String action, final String label, String sectionName) {

		final ShopInfo.ShopItem content = info.contents.get(position);

		ImageUtil.loadImageResize(context, content.sectionContent.imageUrl, top_image, 0);

		final List<SectionContentList> list = content.sectionContent.subProductList;
		if (list != null && !list.isEmpty()) {
			if(list.get(0).linkUrl == null || "".equals(list.get(0).linkUrl)){
				bottom_layout.setVisibility(View.GONE);
			}else{
				bottom_button.setText(list.get(0).productName);
				bottom_layout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						WebUtils.goWeb(context, list.get(0).linkUrl);
						//GTM 클릭이벤트 전달
						String tempLabel = String.format("%s_%s", label, list.get(0).productName);
						GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
					}
				});
			}

			int itemSize = list.size();

			for (int i = 1; i < itemSize; i++) {
				if(i < IMAGE_VIEWS.length) {
					final SectionContentList item = list.get(i);

					ImageView image = imageViews[i];
					ImageUtil.loadImage(context, item.imageUrl, image, 0);
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

			if(itemSize - 1 <= 8){
				item_group_3.setVisibility(View.GONE);
				line_group_3.setVisibility(View.GONE);
				line_group_2.setVisibility(View.GONE);
			}
			if(itemSize - 1 <= 4){
				item_group_2.setVisibility(View.GONE);
				line_group_2.setVisibility(View.GONE);
			}

		}


	}

}
