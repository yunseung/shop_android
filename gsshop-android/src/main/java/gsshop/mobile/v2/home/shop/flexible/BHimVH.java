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
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

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

import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.FHD;

/**
 * 이미지 배너.
 *
 */
@SuppressLint("NewApi")
public class BHimVH extends BaseViewHolder {
	private final LinearLayout root;

	private final LinearLayout promotion_layout_1;
	private final LinearLayout promotion_layout_2;
	private final LinearLayout promotion_layout_3;

	private final LinearLayout promotion_group;

	private final ImageView icon_promotion;


	private final ImageView[] promotion_item = new ImageView[3];

	private final View line_1;
	private final View line_2;

	private final TextView program_text;

	private final LinearLayout banner_group;

	/**
	 * @param itemView
	 */
	public BHimVH(View itemView) {
		super(itemView);
		root = (LinearLayout) itemView.findViewById(R.id.root);

		promotion_layout_1 = (LinearLayout)itemView.findViewById(R.id.promotion_layout_1);
		promotion_layout_2 = (LinearLayout)itemView.findViewById(R.id.promotion_layout_2);
		promotion_layout_3 = (LinearLayout)itemView.findViewById(R.id.promotion_layout_3);

		promotion_group = (LinearLayout)itemView.findViewById(R.id.promotion_group);
        icon_promotion = (ImageView)itemView.findViewById(R.id.icon_promotion);

		promotion_item[0] = (ImageView)itemView.findViewById(R.id.promotion_item_1);
		promotion_item[1] = (ImageView)itemView.findViewById(R.id.promotion_item_2);
		promotion_item[2] = (ImageView)itemView.findViewById(R.id.promotion_item_3);

		program_text = (TextView)itemView.findViewById(R.id.program_text);

		line_1 = itemView.findViewById(R.id.line_1);
		line_2 = itemView.findViewById(R.id.line_2);

		banner_group = (LinearLayout)itemView.findViewById(R.id.banner_group);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), root);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), promotion_group);

	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {

		if(info.contents.get(position).sectionContent.subProductList == null
				|| info.contents.get(position).sectionContent.subProductList.size() == 0
				|| info.contents.get(position).sectionContent.subProductList.get(0).subProductList == null){
			return;
		}

		final ArrayList<SectionContentList> item = info.contents.get(position).sectionContent.subProductList.get(0).subProductList;
		int itemSize = item.size();
        ImageUtil.loadImageBadge(context, info.contents.get(position).sectionContent.imageUrl, icon_promotion, R.drawable.icon_tv_promotion, FHD);

		if(info.contents.get(position).sectionContent != null && info.contents.get(position).sectionContent.productName != null
				&& !"".equals(info.contents.get(position).sectionContent.productName)) {
			program_text.setText(info.contents.get(position).sectionContent.productName);
		}else{
			program_text.setText(R.string.home_tv_live_benefit);
		}
		if(itemSize == 0){
			ViewGroup.LayoutParams lp = promotion_group.getLayoutParams();
			lp.height = 0;
			promotion_group.requestLayout();
		}

		/**
		 * 혜택영역
		 */
		for(int i=0 ; i < itemSize ; i++){
			if (itemSize > 2) {
				promotion_item[i].getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
				promotion_item[i].requestLayout();
			} else {
				promotion_item[i].getLayoutParams().width = DisplayUtils.getResizeWidthAtViewToScreenSize(context, context.getResources().getDimensionPixelSize(R.dimen.promotion_item_width));
				promotion_item[i].requestLayout();
			}
			final int index = i;
			ImageUtil.loadImageResize(context, item.get(index).imageUrl, promotion_item[index], R.drawable.noimg_tv);
			promotion_item[i].setContentDescription(item.get(index).productName);
			promotion_item[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, item.get(index).linkUrl);
					String tempLabel = String.format("%s_%s_%s", label, String.valueOf(index), item.get(index).linkUrl);
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
				}
			});
		}

		if (itemSize == 1) {
			promotion_layout_2.setVisibility(View.GONE);
			promotion_layout_3.setVisibility(View.GONE);
			line_1.setVisibility(View.GONE);
			line_2.setVisibility(View.GONE);
		}else if (itemSize > 2) {
			promotion_layout_3.setVisibility(View.VISIBLE);
			line_2.setVisibility(View.VISIBLE);
		} else {
			promotion_layout_3.setVisibility(View.GONE);
			line_2.setVisibility(View.GONE);
		}

		/**
		 * 배너영역
		 */
		int banIdx = 1;
		banner_group.removeAllViews();
		//데이타 유무 체크
		if (info.contents.get(position).sectionContent.subProductList.size() < 2) {
			return;
		}
		final ArrayList<SectionContentList> itemBanner = info.contents.get(position).sectionContent.subProductList.get(1).subProductList;
		if (itemBanner == null) {
			return;
		}
		for(int i=0 ; i < itemBanner.size() ; i++){
			//배너는 최대 5개까지만 노출
			if (banIdx++ <= 5) {
				//라인추가
				int lineHeight = context.getResources()
						.getDimensionPixelSize(R.dimen.flexible_tv_live_benefit_banner_line_height);
				View line = new View(context);
				line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,lineHeight));
				line.setBackgroundColor(Color.parseColor("#e5e5e5"));
				banner_group.addView(line);

				//배너추가
				ImageView banner = new ImageView(context);
				//높이는 어떤값을 세팅해도 화면에 노출되는 결과는 동일하나
				//사이즈를 최대한 정확히 지정해야 스크롤시 덜거덕 거리는 현상이 최소화됨
				int height = context.getResources()
						.getDimensionPixelSize(R.dimen.flexible_tv_live_benefit_banner_height);
				banner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
				banner.setScaleType(ImageView.ScaleType.FIT_XY);
				banner_group.addView(banner);
				//resizeHeightAtViewToScreenSize 수행안하면 테블릿에서 noimg_logo가 화면에 맞게 스케일 안됨
				DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), banner);
				ImageUtil.loadImageResize(context, itemBanner.get(i).imageUrl, banner, R.drawable.noimg_logo);
				final int index = i;
				banner.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (itemBanner.get(index).linkUrl != null && itemBanner.get(index).linkUrl.length() > 4) {
							WebUtils.goWeb(context, itemBanner.get(index).linkUrl);
						}
					}
				});
			}
		}
	}
}
