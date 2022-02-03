/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.StringUtils.isEmpty;
import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;


/**
 * 정사각 상품 배너
 *
 */
public class BanMutGbaVH extends BaseViewHolderV2 {
	/**
	 * 뷰홀더 전체영역
	 */
	private final LinearLayout rowGoods;

	/**
	 * 상품이미지 영역
	 */
	private final RelativeLayout main_layout;

	/**
	 * 상품이미지
	 */
	private final ImageView mainImg;

	/**
	 * 좌상단 뱃지
	 */
	private final ImageView top_badge;

	/**
	 * 상단 타이틀
	 */
	private final TextView txt_title;

	/**
	 * 하단 타이틀
	 */
	private final TextView txt_title_desc;

	/**
	 * 할인률
	 */
	private final TextView txt_badge_per;

	/**
	 * 판매가격
	 */
	private final TextView txtPrice;
	private final TextView txt_price_unit;

	/**
	 * 기본가격
	 */
	private final TextView txt_base_price;
	private final TextView txt_base_price_unit;

	/**
	 * 좌하단 뱃지영역
	 */
	private LinearLayout lay_badge_corner;

	/**
	 * 구매수량
	 */
	private final TextView txt_sales_quantity;

	/**
	 * 판매완료 상품에 표시할 영역
	 */
	private final LinearLayout layoutSoldout;

	/**
	 * 내려주는 값 그대로 표시
	 */
	private final TextView valueInfo;

	/**
	 * 성인상품 여부
	 */
	private  boolean isAdult;

	/**
	 * StringBuilder 세팅
	 */
	private final SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
	private final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
	private final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
	private final AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

	/**
	 * @param itemView
	 */
	public BanMutGbaVH(View itemView) {
		super(itemView);

		rowGoods = itemView.findViewById(R.id.row_goods);
		main_layout  = itemView.findViewById(R.id.main_layout);
		mainImg = itemView.findViewById(R.id.main_img);
		layoutSoldout = itemView.findViewById(R.id.sold_out);
		top_badge = itemView.findViewById(R.id.top_badge);
		txt_title = itemView.findViewById(R.id.txt_title);
		txt_title_desc = itemView.findViewById(R.id.txt_title_desc);
		txt_badge_per = itemView.findViewById(R.id.txt_badge_per);
		txtPrice = itemView.findViewById(R.id.txt_price);
		txt_price_unit = itemView.findViewById(R.id.txt_price_unit);
		txt_base_price = itemView.findViewById(R.id.txt_base_price);
		txt_base_price_unit = itemView.findViewById(R.id.txt_base_price_unit);

		valueInfo = itemView.findViewById(R.id.valueInfo);

		lay_badge_corner = itemView.findViewById(R.id.lay_badge_corner);
		txt_sales_quantity = itemView.findViewById(R.id.txt_sales_quantity);

		String adult = CookieUtils.getAdult(itemView.getContext());
		if ("true".equals(adult) || "temp".equals(adult)) {
			isAdult = true;
		}

		if(main_layout != null) {
			//DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_layout);
		}
		if(mainImg != null) {
			//DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), mainImg);
		}
	}

	@Override
	public void onBindViewHolder(final Context context, int position, List<ModuleList> contents) {
		super.onBindViewHolder(context, position, contents);
		final ModuleList item = contents.get(position);

		//초기화
		initiateViews();

		boolean isAdultViewVisible;
		if (item.adultCertYn != null && "Y".equalsIgnoreCase(item.adultCertYn) && !isAdult) {
			mainImg.setImageResource(R.drawable.s19_550);
			isAdultViewVisible = true;
		} else {
			mainImg.setEnabled(false);
			isAdultViewVisible = false;
			//cache=file사이즈 add, 이미 있으면 원본을 반환 0226 일반 상품에만 반영
			item.imageUrl = ImageUtil.makeImageUrlWithSize(context,item.imageUrl);
			ImageUtil.loadImageResize(context, item.imageUrl, mainImg, R.drawable.no_img_550);
		}

		//좌상단 NEW 뱃지
		if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()  && !isAdultViewVisible){
			final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
			if(badge != null && badge.imgUrl != null && !"".equals(badge.imgUrl)){
				ImageUtil.loadImageBadge(context, badge.imgUrl, top_badge, R.drawable.transparent, HD);
				top_badge.setVisibility(View.VISIBLE);
			}
		}

		//좌하단 뱃지 생성
		if (item.imgBadgeCorner != null
				&& item.imgBadgeCorner.RB != null
				&& !item.imgBadgeCorner.RB.isEmpty()) {
			int loopCnt = 0;
			for (ImageBadge imageBadge : item.imgBadgeCorner.RB) {
				String badgeStr = imageBadge.text;
				if (!isEmpty(badgeStr)) {
					TextView bText = (TextView) LayoutInflater.from(context).inflate(R.layout.badge_text, null);
					ImageView bImage = (ImageView) LayoutInflater.from(context).inflate(R.layout.badge_image_dot, null);
					bText.setText(badgeStr);
					lay_badge_corner.addView(bText);
					if (loopCnt < item.imgBadgeCorner.RB.size()-1) {
						lay_badge_corner.addView(bImage);
					}
					loopCnt++;
				}
			}
		}

		if(item.valueInfo != null){
			valueInfo.setText(item.valueInfo);
		}

		//상단 타이틀
		txt_title.setText(item.exposPrSntncNm);

		//하단 타이틀
		if(item.infoBadge != null && item.infoBadge.TF != null && !item.infoBadge.TF.isEmpty()){
			titleStringBuilder.append(item.infoBadge.TF.get(0).text);
			titleStringBuilder.append(" ");
			try {
				if(StringUtils.checkHexColor("#" + item.infoBadge.TF.get(0).type)) {
					titleStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#" + item.infoBadge.TF.get(0).type)), 0, titleStringBuilder.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}catch(Exception e){
				Ln.e(e);
			}
		}
		if (DisplayUtils.isValidString(item.productName)) {
			titleStringBuilder.append(item.productName);
		}
		txt_title_desc.append(titleStringBuilder);

		// 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
		// base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
		int salePrice = DisplayUtils.getNumberFromString(item.salePrice);
		int basePrice = DisplayUtils.getNumberFromString(item.basePrice);
		// 할인률
		if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
			if (DisplayUtils.isValidNumberStringExceptZero(item.basePrice)
					&& (salePrice < basePrice)) {
				txt_badge_per.setVisibility(View.VISIBLE);
				txt_base_price.setText(DisplayUtils.getFormattedNumber(item.basePrice));
				txt_base_price_unit.setText(item.exposePriceText);
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price_unit.setVisibility(View.VISIBLE);
				txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			}
		}else {
			if (item.basePrice != null && !"".equals(item.basePrice)
					&& !"0".equals(item.basePrice)) {
				txt_base_price.setText(DisplayUtils.getFormattedNumber(item.basePrice));
				txt_base_price_unit.setText(item.exposePriceText);
				txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price_unit.setVisibility(View.VISIBLE);
			}
			txt_badge_per.setVisibility(View.GONE);
		}

		// price
		if (DisplayUtils.isValidString(item.salePrice)) {
			// 가격
			String salePriceText = DisplayUtils.getFormattedNumber(item.salePrice);
			txt_price_unit.setText(item.exposePriceText);
			priceStringBuilder.append(salePriceText);
			txtPrice.setVisibility(View.VISIBLE);
			txt_price_unit.setVisibility(View.VISIBLE);
		} else {
			txtPrice.setVisibility(View.GONE);
			txt_price_unit.setVisibility(View.GONE);
		}
		txtPrice.append(priceStringBuilder);
		if(item.discountRateText == null || "".equals(item.discountRateText)){
			if(item.valueText != null && !"".equals(item.valueText)){
				final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
				priceStringBuilder.append(item.valueText);
				try {
					priceStringBuilder.setSpan(valueSizeSpan, 0, item.valueText.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					priceStringBuilder.setSpan(discountColorSpan, 0, item.valueText.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}catch(Exception e){
					Ln.e(e);
				}
				txt_badge_per.append(priceStringBuilder);
				txt_badge_per.setVisibility(View.VISIBLE);

			}else{
				// 할인률
				txt_badge_per.append(ProductInfoUtil.getDiscountTextV2(context, item));
			}
		}else{
			// 할인률
			txt_badge_per.append(ProductInfoUtil.getDiscountTextV2(context, item));
		}

		// sold out & 판매수량
		if (DisplayUtils.isTrue(item.isTempOut)) {
			// sold out이면 판매수량 보여주는 layout 숨김
			layoutSoldout.setVisibility(View.VISIBLE);
		} else {
			layoutSoldout.setVisibility(View.GONE);
			txt_sales_quantity.setText(ProductInfoUtil.getSaleQuantityText(item));
		}

		// row 클릭하면 상품으로 이동
		rowGoods.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, item.linkUrl);
			}
		});
	}

	/**
	 * 뷰에대한 초기화 수행
	 */
	private void initiateViews() {
		txt_badge_per.setPadding(0, 0, 0, 0);
		lay_badge_corner.removeAllViews();
		titleStringBuilder.clear();
		priceStringBuilder.clear();
		top_badge.setVisibility(View.GONE);
		txt_title.setText("");
		txt_title_desc.setText("");
		txtPrice.setText("");
		txt_badge_per.setText("");
		valueInfo.setText("");
		txt_sales_quantity.setText("");
		txt_base_price.setVisibility(View.GONE);
		txt_base_price_unit.setVisibility(View.GONE);
	}

}
