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

import java.util.List;

import gsshop.mobile.v2.MainApplication;
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
 * now hot 키워드 배너.
 *
 */
public class BMiaVH extends BaseViewHolder {

	private final ImageView imageLeft;
	private final ImageView imageTop;
	private final ImageView imageBottom;

	/**
	 * @param itemView
	 */
	public BMiaVH(View itemView) {
		super(itemView);
		imageLeft = (ImageView) itemView.findViewById(R.id.main_img_01);
		imageTop = (ImageView) itemView.findViewById(R.id.main_img_02);
		imageBottom = (ImageView) itemView.findViewById(R.id.main_img_03);
		
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), imageLeft);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), imageTop);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), imageBottom);
	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
			final String action, final String label, String sectionName) {


		if(info.contents.get(position).sectionContent.subProductList == null || info.contents.get(position).sectionContent.subProductList.size() <= 0){
			return;
		}

			final List<SectionContentList> subProductList = info.contents
					.get(position).sectionContent.subProductList.get(0).subProductList;

			ImageUtil.loadImageResize(context, subProductList.get(0).imageUrl, imageLeft,
					R.drawable.noimg_logo);

			imageLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// GTM AB Test 클릭이벤트 전달

					if (subProductList != null && subProductList.get(0) != null) {
						WebUtils.goWeb(context, subProductList.get(0).linkUrl);

						String tempLabel = String.format("%s_%s_%s", label, Integer.toString(0),
								subProductList.get(0).linkUrl);
						GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
					}

				}
			});

			ImageUtil.loadImageResize(context, subProductList.get(1).imageUrl, imageTop,
					R.drawable.noimg_logo);
			imageTop.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// GTM AB Test 클릭이벤트 전달

					if (subProductList != null && subProductList.get(1) != null) {
						WebUtils.goWeb(context, subProductList.get(1).linkUrl);

						String tempLabel = String.format("%s_%s_%s", label, Integer.toString(1),
								subProductList.get(1).linkUrl);
						GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
					}
				}
			});

			ImageUtil.loadImageResize(context, subProductList.get(2).imageUrl, imageBottom,
					R.drawable.noimg_logo);
			imageBottom.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// GTM AB Test 클릭이벤트 전달

					// WebUtils.goWeb(context, subProductList.get(2).prdUrl);

					if (subProductList != null && subProductList.get(2) != null) {
						WebUtils.goWeb(context, subProductList.get(2).linkUrl);

						String tempLabel = String.format("%s_%s_%s", label, Integer.toString(2),
								subProductList.get(2).linkUrl);
						GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
					}
				}
			});

	}

}
