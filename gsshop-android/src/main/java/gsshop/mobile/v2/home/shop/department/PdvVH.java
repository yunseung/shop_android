/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.department;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;

/**
 * 이미지 배너.
 *
 */
@SuppressLint("NewApi")
public class PdvVH extends BaseViewHolder {
	private final LinearLayout root;

	private final View title_layout;
	private final View title_gap;
	private final ImageView title;

	private final LinearLayout special_banner_layout;


	protected final SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
	protected final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
	protected final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
	protected final AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

	/**
	 * @param itemView itemView
	 */
	public PdvVH(View itemView) {
		super(itemView);
		root = (LinearLayout) itemView.findViewById(R.id.root);
		title_layout = itemView.findViewById(R.id.title_layout);
		title_gap = itemView.findViewById(R.id.title_gap);
		title = (ImageView)itemView.findViewById(R.id.title);
		special_banner_layout = (LinearLayout) itemView.findViewById(R.id.special_banner_layout);

	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {
		final ArrayList<SectionContentList> subProductList = info.contents.get(position).sectionContent.subProductList;

		if(subProductList == null){
			return;
		}


		ImageUtil.loadImageBadge(context, info.contents.get(position).sectionContent.imageUrl, title,0, HD);


		if(info.contents.get(position).sectionContent.imageUrl == null || "".equals(info.contents.get(position).sectionContent.imageUrl)){
			title_layout.setVisibility(View.GONE);
			title_gap.setVisibility(View.VISIBLE);
		}else{
			title_layout.setVisibility(View.VISIBLE);
			title_gap.setVisibility(View.GONE);
		}


		special_banner_layout.removeAllViews();

		for (int i = 0; i < subProductList.size(); i++) {
			final int index = i;
			final SectionContentList item = subProductList.get(index);
			titleStringBuilder.clear();
			priceStringBuilder.clear();

			View view;
			if(index % 2 == 0){
				view = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.home_row_type_fx_department_store_special_first_row, null);
				special_banner_layout.addView(view);
			}else{
				view = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.home_row_type_fx_department_store_special_second_row, null);
				special_banner_layout.addView(view);
			}

			ImageView program_Image = (ImageView)view.findViewById(R.id.program_Image);

			TextView text_category = (TextView)view.findViewById(R.id.text_category);
			TextView text_title = (TextView)view.findViewById(R.id.text_title);
			TextView text_name = (TextView)view.findViewById(R.id.text_name);
			View txt_base_price_layout = view.findViewById(R.id.txt_base_price_layout);
			View txt_value_text_layout = view.findViewById(R.id.txt_value_text_layout);
			View txt_value_info_layout = view.findViewById(R.id.txt_value_info_layout);


			TextView txt_base_price = (TextView)view.findViewById(R.id.txt_base_price);
			TextView txt_base_price_unit = (TextView)view.findViewById(R.id.txt_base_price_unit);
			TextView txtPrice = (TextView)view.findViewById(R.id.txt_price);
			TextView txt_price_unit = (TextView)view.findViewById(R.id.txt_price_unit);
			TextView txt_badge_per = (TextView)view.findViewById(R.id.txt_badge_per);
			TextView txt_value_info = (TextView)view.findViewById(R.id.txt_value_info);
			TextView txt_value_text = (TextView)view.findViewById(R.id.txt_value_text);


			txt_base_price.setVisibility(View.GONE);
			txt_base_price_unit.setVisibility(View.GONE);
			txt_base_price_layout.setVisibility(View.GONE);
			text_name.setVisibility(View.GONE);

			txt_value_text_layout.setVisibility(View.INVISIBLE);
			txt_value_info_layout.setVisibility(View.INVISIBLE);
			txt_value_info.setVisibility(View.INVISIBLE);
			txt_value_text.setVisibility(View.INVISIBLE);

			ImageUtil.loadImageResize(context, item.imageUrl, program_Image, R.drawable.noimg_tv);

			if(item.infoBadge != null && item.infoBadge.TF != null && !item.infoBadge.TF.isEmpty()){
				titleStringBuilder.append(item.infoBadge.TF.get(0).text);
				try {
					if(StringUtils.checkHexColor("#" + item.infoBadge.TF.get(0).type)) {
						titleStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#" + item.infoBadge.TF.get(0).type)), 0, titleStringBuilder.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}

				}catch(Exception e){
					Ln.e(e);
				}
				text_category.setText(titleStringBuilder);
				text_category.setVisibility(View.VISIBLE);
				titleStringBuilder.clear();
			}else{
				text_category.setVisibility(View.GONE);
			}

			if (DisplayUtils.isValidString(item.productName)) {
				titleStringBuilder.append(item.productName);
			}

			text_title.setText(titleStringBuilder);

			String etcText1 = item.etcText1;
			if (etcText1 != null && etcText1.length() > 0) {
				text_name.setText(etcText1);
				ViewUtils.showViews(text_name);
			}

			// 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
			// base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
			int salePrice = DisplayUtils.getNumberFromString(item.salePrice);
			int basePrice = DisplayUtils.getNumberFromString(item.basePrice);



			// 할인률
			if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
				if (DisplayUtils.isValidNumberStringExceptZero(item.basePrice)
						&& (salePrice < basePrice)) {
					txt_base_price.setText(DisplayUtils.getFormattedNumber(item.basePrice));
					txt_base_price_unit.setText(item.exposePriceText);
					txt_base_price.setVisibility(View.VISIBLE);
					txt_base_price_unit.setVisibility(View.VISIBLE);
					txt_base_price_layout.setVisibility(View.VISIBLE);
					txt_base_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

					txt_value_text.setVisibility(View.GONE);
					txt_value_text_layout.setVisibility(View.GONE);
				}
			}else {
				if (item.basePrice != null && !"".equals(item.basePrice)
						&& !"0".equals(item.basePrice)) {
					txt_base_price.setText(DisplayUtils.getFormattedNumber(item.basePrice));
					txt_base_price_unit.setText(item.exposePriceText);
					txt_base_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
					txt_base_price.setVisibility(View.VISIBLE);
					txt_base_price_unit.setVisibility(View.VISIBLE);
					txt_base_price_layout.setVisibility(View.VISIBLE);

					txt_value_text.setVisibility(View.GONE);
					txt_value_text_layout.setVisibility(View.GONE);
				}
                    //0% 이하일땐 숨기기
					txt_badge_per.setVisibility(View.GONE);
					txt_base_price.setVisibility(View.GONE);
					txt_base_price_unit.setVisibility(View.GONE);
					txt_base_price_layout.setVisibility(View.GONE);
			}

			// price
			if (DisplayUtils.isValidString(item.salePrice)) {
				// 가격
				String salePriceText = DisplayUtils.getFormattedNumber(item.salePrice);
				txt_price_unit.setText(item.exposePriceText);
				priceStringBuilder.append(salePriceText);
			}

			txtPrice.setText(priceStringBuilder);

			txt_badge_per.setText(ProductInfoUtil.getDepartmentDiscountText(context, item));

			// valueText
			String value = item.valueText;
			if (value != null && value.length() > 0) {
				txt_value_text.setText(value);
				ViewUtils.showViews(txt_value_text_layout);
				ViewUtils.showViews(txt_value_text);
			}

			if (DisplayUtils.isValidString(item.discountRate)) {
				// discountRateText가 없으면 discountRate(숫자 할인률) 체크
				// 할인율이 5%이상일때 유효했으나 현재는 1%이상일때만 유효
				if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
					txt_value_info_layout.setVisibility(View.GONE);
					txt_value_info.setVisibility(View.GONE);
				}
			}

			String promotionName = item.promotionName;
			if (promotionName != null && promotionName.length() > 0) {
				txt_value_info.setText("(" + promotionName + ")");
				ViewUtils.showViews(txt_value_info_layout);
				ViewUtils.showViews(txt_value_info);
			}




			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, item.linkUrl);
					String tempLabel = String.format("%s_%s_%s", label, String.valueOf(index), item.linkUrl);
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
				}
			});


		}
	}

}
