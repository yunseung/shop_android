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
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 흰색 배경 중앙에 노출되는 이미지 배너.
 *
 */
@SuppressLint("NewApi")
public class BIssVH extends BaseViewHolder {
	private final LinearLayout root;
	private final LinearLayout layout_main_img;
	private final ImageView main_img;
	private final View top_margin;

	/**
	 * @param itemView
	 */
	public BIssVH(View itemView) {
		super(itemView);
		root = (LinearLayout) itemView.findViewById(R.id.root);
		layout_main_img = (LinearLayout) itemView.findViewById(R.id.layout_main_img);
		main_img = (ImageView) itemView.findViewById(R.id.main_img);
		top_margin = itemView.findViewById(R.id.top_margin);
		//이미지를 포함하고 있는 레이아웃 사이즈 조정 (내부 이미지는 고정 사이즈)
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), layout_main_img);
		//이미지 사이즈 조정
		DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), main_img);
	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {

		root.setPadding(0,0,0, context.getResources().getDimensionPixelSize(R.dimen.list_divider_height));

		LayoutParams params = (LayoutParams) itemView.getLayoutParams();
		if (info.sectionList.viewType.equals(ShopInfo.TYPE_EVENT)) {
			// 이벤트 메뉴.
			if (!params.isFullSpan()) {
				params.setFullSpan(true);
			}
	 		if(info.contents.get(position).type == ViewHolderType.BANNER_TYPE_ROLL_ONE_IMAGE) {
				top_margin.setVisibility(View.VISIBLE);
				root.setPadding(0,0,0,0);
			}
		} else {
			//날방매장은 bottom 10
			if (info.sectionList.viewType.equals(ShopInfo.TYPE_NALBANG)) {
				root.setPadding(0,0,0, context.getResources().getDimensionPixelSize(R.dimen.vh_blank_space_middle));
			}
			if (params.isFullSpan()) {
				params.setFullSpan(false);
			}
		}


		final SectionContentList item = info.contents.get(position).sectionContent;
		ImageUtil.loadImage(context, item.imageUrl, main_img, R.drawable.noimg_logo);

		main_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// GTM AB Test 클릭이벤트 전달
				if (item.linkUrl != null && item.linkUrl.length() > 4) {
					WebUtils.goWeb(context, item.linkUrl);
				}

				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
			}
		});

	}

}
