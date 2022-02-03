/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.tvshoping;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 띠 이미지 배너.
 *
 */
public class FlexibleBannerTvDealCategoryViewHolder extends BaseViewHolder {

	/**
	 * @param itemView itemView
	 */
	public FlexibleBannerTvDealCategoryViewHolder(View itemView) {
		super(itemView);

	}

	/**
	 * bind
	 */
	private static final int[] BORDERS = { R.id.view_01, R.id.view_02, R.id.view_03,
			R.id.view_04, R.id.view_05, R.id.view_06, R.id.view_07, R.id.view_08 };
	private static final int[] IMAGE_VIEWS = { R.id.image_01, R.id.image_02, R.id.image_03,
			R.id.image_04, R.id.image_05, R.id.image_06, R.id.image_07, R.id.image_08 };

	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
			final String action, final String label, String sectionName) {
		
		((ViewGroup)itemView).removeAllViews();

		ShopInfo.ShopItem content = info.contents.get(position);
		List<SectionContentList> list = content.sectionContent.subProductList;
		if (list != null) {
			View view = null;
			for (int i = 0; i < list.size(); i++) {
				final SectionContentList item = list.get(i);
				int idx = i % IMAGE_VIEWS.length;
				if (idx == 0) {
					view = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.home_row_type_fx_tv_deal_category_item, null);
					((ViewGroup) itemView).addView(view);
				}
				ImageView image;
				//10/19 품질팀 요청
				if(view != null && IMAGE_VIEWS[idx] != -1) {
					image = (ImageView) view.findViewById(IMAGE_VIEWS[idx]);
					View border = view.findViewById(BORDERS[idx]);
					DisplayUtils.resizeHeightAtViewToScreenSize(context, border);
					DisplayUtils.resizeHeightAtViewToScreenSize(context, image);
					DisplayUtils.resizeWidthAtViewToScreenSize(context, image);
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
		}

	}

}
