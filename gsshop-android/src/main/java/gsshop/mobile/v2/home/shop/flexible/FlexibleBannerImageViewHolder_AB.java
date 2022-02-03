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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams;

import org.json.JSONException;
import org.json.JSONObject;

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.ApptimizeFlexibleExp;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * 이미지 배너.
 *
 */
@SuppressLint("NewApi")
public class FlexibleBannerImageViewHolder_AB extends BaseViewHolder {
	private final LinearLayout root;
	private final RelativeLayout divider;
	private final ImageView main_img;
	private final View top_margin;

	/**
	 * @param itemView
	 */
	public FlexibleBannerImageViewHolder_AB(View itemView) {
		super(itemView);
		root = (LinearLayout) itemView.findViewById(R.id.root);
		main_img = (ImageView) itemView.findViewById(R.id.main_img);
		divider = (RelativeLayout) itemView.findViewById(R.id.divider);
		top_margin = itemView.findViewById(R.id.top_margin);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_img);
	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {
		super.onBindViewHolder(context, position, info, action, label, sectionName);
		boolean isDynamicImage = false;
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


		// image resize

		final SectionContentList item = info.contents.get(position).sectionContent;
		int height = 0;
		if ("I".equals(item.viewType) || "B_IM".equals(item.viewType)) {
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_i_height);
		} else if ("B_IXS".equals(item.viewType)) {
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_ixs_height);
		} else if ("B_IS".equals(item.viewType)) {
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_is_height);
		} else if ("B_IL".equals(item.viewType)) {
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_il_height);
		} else if ("B_IXL".equals(item.viewType)) {
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_ixl_height);
 		} else if ("B_IM440".equals(item.viewType) ) {
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_im440_height);
		} else if ("B_SIS".equals(item.viewType)) {
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_rollimage_item_image_height);
		} else if ("SE".equals(item.viewType)) {
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_rollimage_preview_item_image_height);
		}else if("B_PZ".equals(item.viewType)){
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_is_height);
			root.setPadding(0,0,0,0);
		}else if("B_INS".equals(item.viewType)){
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_ins_height);
			root.setPadding(0,0,0,0);
		}else if("B_TVH".equals(item.viewType)){
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_is_height);
			root.setPadding(0,0,0,0);
		}else if("BAN_IMG_H000_GBA".equals(item.viewType) || "PMO_T3_IMG".equals(item.viewType) ||
				"BAN_IMG_H000_GBD".equals(item.viewType)){
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_is_height);
			root.setPadding(0,0,0,context.getResources().getDimensionPixelSize(R.dimen.list_divider_height));

			// Glide에서 이미지 읽었을 때에 배너크기가 조정될 것임, 적용되어도 문제는 없음.
			if (!TextUtils.isEmpty(item.height)) {
				try {
					height = Integer.parseInt(item.height) * 3 / 2;
				}
				catch (NumberFormatException e) {
					Ln.e(e.getMessage());
				}
			}
		}else if("BAN_IMG_H000_GBB".equals(item.viewType)){
			height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_image_banner_type_b_is_height);
			root.setPadding(0,0,0,0);
		}
		if (height > 0) {
			main_img.getLayoutParams().height = height;
			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_img);
		}
//
// else{
//			main_img.getLayoutParams().height = 0;
//			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_img);
//			root.setPadding(0,0,0,0);
//		}
		//Ln.i("onBindViewHolder : " + item.imageUrl);

//		if(isDynamicImage){
//			ImageUtil.loadImage(context, item.imageUrl, main_img, R.drawable.noimg_logo);
//		}else{
			ImageUtil.loadImageResize(context, item.imageUrl, main_img, R.drawable.noimg_logo);
//		}

//		main_img.setContentDescription(item.productName);

		if(item.productName != null && !"".equals(item.productName)){
			main_img.setContentDescription(item.productName);
		}else if(item.gsAccessibilityVariable != null && !"".equals(item.gsAccessibilityVariable)){
			main_img.setContentDescription(item.gsAccessibilityVariable);
		}
		main_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// GTM AB Test 클릭이벤트 전달
				if (item.linkUrl != null && item.linkUrl.length() > 4) {
					WebUtils.goWeb(context, item.linkUrl);
				}
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);

				JSONObject eventProperties = new JSONObject();

				if(item != null) {
					//홈 내일TV AB테스트
					if(item.ampEventName != null){

						//추후 타겟 명시
						ApptimizeFlexibleExp exp = (ApptimizeFlexibleExp) ApptimizeExpManager.findExpInstance(item.ampEventName);
						//ApptimizeFlexibleExp exp = (ApptimizeFlexibleExp) ApptimizeExpManager.findExpInstance(item.ampEventName, "54");
						if(exp != null){
							try {
								eventProperties.put(AMPEnum.AMP_AB_VALUE, exp.getType());
								eventProperties.put(AMPEnum.AMP_AB_KEY, exp.getExp());

							} catch (JSONException e) {
								e.printStackTrace();
							}
							AMPAction.sendAmpEventProperties("Click-AB-" + exp.getExp(), eventProperties);
						}
					}
				}
			}
		});
	}
}

