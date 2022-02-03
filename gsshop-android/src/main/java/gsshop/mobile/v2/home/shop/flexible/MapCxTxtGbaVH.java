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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams;

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
import roboguice.util.Ln;

/**
 * 이미지 배너.
 *
 */
@SuppressLint("NewApi")
public class MapCxTxtGbaVH extends BaseViewHolder {
	private final String SEPERATE_PRUDUCT_NAME = " & ";

	private final LinearLayout root;
	private final RelativeLayout main_layout, sub_layout, layout_info;
	private final FrameLayout layout_discount_schedule;
	private final ImageView img_card, img_arrow, main_img;

	private final TextView txt_card_date, txt_prefix, txt_percentage, txt_percentage_lastfix, txt_card_lastfix;
	private final ArrayList<FrameLayout> layout_future;
	private final ArrayList<View> txt_future_date;

	private final View top_margin;
	/**
	 * @param itemView
	 */
	public MapCxTxtGbaVH(View itemView) {
		super(itemView);

		// 배너 내부에 값에 따라 UI가 설정됨.

		// UI 초기 설정
		root = (LinearLayout) itemView.findViewById(R.id.root);
		main_img = (ImageView) itemView.findViewById(R.id.main_img);
		img_card = (ImageView) itemView.findViewById(R.id.img_card);
		img_arrow = (ImageView) itemView.findViewById(R.id.img_arrow);

		main_layout = (RelativeLayout) itemView.findViewById(R.id.main_layout);
		sub_layout = (RelativeLayout) itemView.findViewById(R.id.sub_layout);
		layout_info = (RelativeLayout) itemView.findViewById(R.id.layout_info);
		layout_discount_schedule = (FrameLayout) itemView.findViewById(R.id.layout_discount_schedule);

		txt_card_date = (TextView) itemView.findViewById(R.id.txt_card_date);
		txt_prefix = (TextView) itemView.findViewById(R.id.txt_card_prefix);
		txt_percentage = (TextView) itemView.findViewById(R.id.txt_percentage);
		txt_card_lastfix = (TextView) itemView.findViewById(R.id.txt_card_lastfix);
		txt_percentage_lastfix = (TextView) itemView.findViewById(R.id.txt_percentage_lastfix);

//		divider = (RelativeLayout) itemView.findViewById(R.id.divider);
		top_margin = itemView.findViewById(R.id.top_margin);

		layout_future = new ArrayList<FrameLayout>();
		layout_future.add((FrameLayout) itemView.findViewById(R.id.layout_future_1));
		layout_future.add((FrameLayout) itemView.findViewById(R.id.layout_future_2));
		layout_future.add((FrameLayout) itemView.findViewById(R.id.layout_future_3));

		txt_future_date = new ArrayList<View>();

		txt_future_date.add(itemView.findViewById(R.id.view_future_1));
		txt_future_date.add(itemView.findViewById(R.id.view_future_2));
		txt_future_date.add(itemView.findViewById(R.id.view_future_3));

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_layout);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), sub_layout);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), layout_info);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), layout_discount_schedule);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), img_card);
		DisplayUtils.resizeWidthAtViewToScreenSize(MainApplication.getAppContext(), img_card);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), img_arrow);
	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {
		super.onBindViewHolder(context, position, info, action, label, sectionName);
		root.setPadding(0,0,0, context.getResources().getDimensionPixelSize(R.dimen.list_divider_height));

		LayoutParams params = (LayoutParams) itemView.getLayoutParams();

		if (params.isFullSpan()) {
			params.setFullSpan(false);
		}

		// image resize
		final SectionContentList item = info.contents.get(position).sectionContent;

		// item 이 없을 경우에 noimg_logo를 보여주도록 설정 후 return;
		main_img.setVisibility(View.GONE);
		if (item == null) {
			main_img.setVisibility(View.VISIBLE);
			ImageUtil.loadImageResize(context, null, main_img, R.drawable.no_img_550);
			main_layout.setVisibility(View.GONE);
			return;
		}

		// 카드 이미지 설정
		ImageUtil.loadImage(context, item.imageUrl, img_card, R.drawable.no_img_550);

		txt_card_date.setText(item.promotionName);
		String txtProductName = item.productName;

		String strConDescription = "";
		if (!TextUtils.isEmpty(item.promotionName)) {
			strConDescription = item.promotionName;
		}

		// 카드 할인 문구를 ' & ' 구분자로 나눈 후 출력
		try {
			String[] arrProductName = txtProductName.split(SEPERATE_PRUDUCT_NAME);
			if (arrProductName.length > 0) {
				txt_prefix.setText(arrProductName[0].trim());
				strConDescription += arrProductName[0].trim();
			}

			if (!TextUtils.isEmpty(item.discountRate)) {
				strConDescription += item.discountRate;
			}

			if (!TextUtils.isEmpty(item.discountRateText)) {
				strConDescription += item.discountRateText;
			}

			if (arrProductName.length > 1) {
				txt_card_lastfix.setText(arrProductName[1].trim());
				strConDescription += arrProductName[1].trim();
			}
		}
		catch (NullPointerException e) {
			Ln.e(e.getMessage());
		}

		root.setContentDescription(strConDescription);

		txt_percentage.setText(item.discountRate);
		txt_percentage_lastfix.setText(item.discountRateText);

		// Color 값 설정.
		try {
			txt_percentage.setTextColor(Color.parseColor("#" + item.etcText1));
			txt_percentage_lastfix.setTextColor(Color.parseColor("#" + item.etcText1));
		}
		catch (Exception e) {
			Ln.e(e.getMessage());
		}

		if (item.subProductList == null || item.subProductList.isEmpty() || item.subProductList.size() == 0) {
			layout_discount_schedule.setVisibility(View.GONE);
		}
		else {
			layout_discount_schedule.setVisibility(View.VISIBLE);

			for (int nSubProductIndex = 0; nSubProductIndex < layout_future.size(); nSubProductIndex ++) {
				if (layout_future.size() > nSubProductIndex) {
					FrameLayout layoutTemp = layout_future.get(nSubProductIndex);
					if (item.subProductList.size() > nSubProductIndex) {
						layoutTemp.setVisibility(View.VISIBLE);
					} else {
						layoutTemp.setVisibility(View.GONE);
					}
				}
				try {
					if (txt_future_date.size() > nSubProductIndex &&
							item.subProductList.size() > nSubProductIndex) {
						View viewTemp = txt_future_date.get(nSubProductIndex);
						TextView txtTemp1 = (TextView) viewTemp.findViewById(R.id.txt_future_date_1);
						TextView txtTemp2 = (TextView) viewTemp.findViewById(R.id.txt_future_rate_1_1);
						TextView txtTemp3 = (TextView) viewTemp.findViewById(R.id.txt_future_rate_1_2);
						txtTemp1.setText(item.subProductList.get(nSubProductIndex).promotionName);
						txtTemp2.setText(item.subProductList.get(nSubProductIndex).productName);
						txtTemp3.setText(" "  + item.subProductList.get(nSubProductIndex).discountRate +
								item.subProductList.get(nSubProductIndex).discountRateText);
					}
				}
				catch (Exception e) {
					Ln.e(e.getMessage());
				}
			}
		}

		// 배너에 대한 설명.
		main_layout.setContentDescription(item.productName);

		if(item.productName != null && !"".equals(item.productName)){
			main_layout.setContentDescription(item.productName);
		}else if(item.gsAccessibilityVariable != null && !"".equals(item.gsAccessibilityVariable)){
			main_layout.setContentDescription(item.gsAccessibilityVariable);
		}

		main_layout.setContentDescription(strConDescription);
		main_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// GTM AB Test 클릭이벤트 전달
				if (item.linkUrl != null && item.linkUrl.length() > 4) {
					WebUtils.goWeb(context, item.linkUrl);
				}
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
				// 배너 모양 종류 테스트를 위한 부분
//				if (layout_discount_schedule.getVisibility() == View.GONE) {
//					layout_discount_schedule.setVisibility(View.VISIBLE);
//					layout_future.get(0).setVisibility(View.VISIBLE);
//				}
//				else {
//					if (layout_future.get(2).getVisibility() == View.VISIBLE) {
//						layout_future.get(0).setVisibility(View.GONE);
//						layout_future.get(1).setVisibility(View.GONE);
//						layout_future.get(2).setVisibility(View.GONE);
//
//						layout_discount_schedule.setVisibility(View.GONE);
//					}
//					else if (layout_future.get(1).getVisibility() == View.VISIBLE) {
//						layout_future.get(2).setVisibility(View.VISIBLE);
//					}
//					else if (layout_future.get(0).getVisibility() == View.VISIBLE) {
//						layout_future.get(1).setVisibility(View.VISIBLE);
//					}
//				}
			}
		});
	}
}
