/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.GlobalTimer;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.util.TimeRemaining;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;

/**
 * 기본형 단품 item
 *
 */
public class LVH extends BaseViewHolder {
	protected final LinearLayout rowGoods;
	protected final TextView promotionName;
	protected final TextView txtTitle;
	protected final TextView txt_base_price;
	protected final TextView txt_base_price_unit;
	protected final TextView txtPrice;
	protected final TextView txt_price_unit;
	protected final TextView txt_badge_per;
	protected final TextView txt_badge_per2;
	protected final TextView txt_sales_quantity;
	protected final View btnPlay;
	protected final ImageView mainImg;
	protected final LinearLayout layoutSoldout;
	protected final LinearLayout time_deal;

	protected final TextView valueInfo;
	protected final TextView time_text;

	protected final RelativeLayout main_layout;
	protected final ImageView delivery_image;
	protected final ImageView[] footer_badge = new ImageView[5];
	protected final ImageView top_badge;
	protected final ImageView adImage;
	protected  boolean isAdult;

	protected final View tag_container;
	protected final ImageView[] badge = new ImageView[3];

	protected final SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
	protected final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
	protected final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
	protected final AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

	private FlexibleShopAdapter mAdapter;
	private int position = -1;

	//홈 -> 내일TV 구좌 AB테스트에 사용되는 변수 / 실험대상인지 아닌지 여부
	private boolean isABtestTarget = false;

	/**
	 * 뷰홀더가 홈매장 내에 노출되는지 여부
	 */
	protected boolean isHomeSection = false;

	/**
	 * @param itemView
	 */
	public LVH(View itemView, FlexibleShopAdapter adapter, String navigationId) {
		super(itemView);
		mAdapter = adapter;
		main_layout  = (RelativeLayout) itemView.findViewById(R.id.main_layout);
		rowGoods = (LinearLayout) itemView.findViewById(R.id.row_tv_goods);
		txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
		promotionName = (TextView) itemView.findViewById(R.id.promotionName);
		txt_badge_per = (TextView) itemView.findViewById(R.id.txt_badge_per);
		txt_badge_per2 = (TextView) itemView.findViewById(R.id.txt_badge_per2);
		btnPlay = itemView.findViewById(R.id.btn_play);
		mainImg = (ImageView) itemView.findViewById(R.id.main_img);
		layoutSoldout = (LinearLayout) itemView.findViewById(R.id.sold_out);
		time_deal = (LinearLayout) itemView.findViewById(R.id.time_deal);
		valueInfo = (TextView) itemView.findViewById(R.id.valueInfo);
		time_text = (TextView) itemView.findViewById(R.id.time_text);

		txt_base_price = (TextView) itemView.findViewById(R.id.txt_base_price);
		txt_base_price_unit = (TextView) itemView.findViewById(R.id.txt_base_price_unit);

		txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
		txt_price_unit = (TextView) itemView.findViewById(R.id.txt_price_unit);
		txt_sales_quantity = (TextView) itemView.findViewById(R.id.txt_sales_quantity);

		//혜택 태그
		tag_container = itemView.findViewById(R.id.tag_container);
		badge[0] = (ImageView) itemView.findViewById(R.id.tag_save);
		badge[1] = (ImageView) itemView.findViewById(R.id.tag_cash);
		badge[2] = (ImageView) itemView.findViewById(R.id.tag_gift);

		//뱃지
		footer_badge[0] = (ImageView) itemView.findViewById(R.id.footer_badge_1);
		footer_badge[1] = (ImageView) itemView.findViewById(R.id.footer_badge_2);
		footer_badge[2] = (ImageView) itemView.findViewById(R.id.footer_badge_3);
		footer_badge[3] = (ImageView) itemView.findViewById(R.id.footer_badge_4);
		footer_badge[4] = (ImageView) itemView.findViewById(R.id.footer_badge_5);

		delivery_image = (ImageView) itemView.findViewById(R.id.delivery_image);
		// ad 구좌
		adImage = (ImageView) itemView.findViewById(R.id.image_item_ad);
		final ImageView adInfoImage = (ImageView) itemView.findViewById(R.id.image_item_ad_info);
		ViewUtils.hideViews(adInfoImage);

		/*
		add tooltip 출력 동작 삭제 (20190515)
		adImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(adInfoImage.getVisibility() != View.VISIBLE) {
					ViewUtils.showViews(adInfoImage);
				} else {
					ViewUtils.hideViews(adInfoImage);
				}
			}
		});

		adInfoImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewUtils.hideViews(adInfoImage);
			}
		});
		*/

		top_badge = (ImageView) itemView.findViewById(R.id.top_badge);

		String adult = CookieUtils.getAdult(adapter.getContext());

		if ("true".equals(adult) || "temp".equals(adult)) {
			isAdult = true;
		}

		if(main_layout != null) {
			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_layout);
		}
		if(mainImg != null) {
			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), mainImg);
		}

	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,final String action,final String label, String sectionName) {
		final SectionContentList item = info.contents.get(position).sectionContent;
		this.position = position;
		bindItem(context, item, action, label);
	}

	public void bindItem(final Context context, final SectionContentList item, final String action, final String label){
		txt_badge_per.setPadding(0, 0, 0, 0);
		txt_badge_per2.setPadding(0, 0, 0, 0);

		titleStringBuilder.clear();
		priceStringBuilder.clear();
		top_badge.setVisibility(View.GONE);
		txtTitle.setText("");
		txtPrice.setText("");
		txt_badge_per.setText("");
		txt_badge_per2.setText("");
		valueInfo.setText("");
		txt_sales_quantity.setText("");
		txt_base_price.setVisibility(View.GONE);
		txt_base_price_unit.setVisibility(View.GONE);
		boolean isAdultViewVisible;
		if (item.adultCertYn != null && "Y".equalsIgnoreCase(item.adultCertYn) && !isAdult) {
            if(item.viewType.equals("BAN_IMG_SQUARE_GBA")) {
                // ai 매장
                mainImg.setImageResource(R.drawable.s19_550);
            } else {
                mainImg.setImageResource(R.drawable.s19_720);
            }
			isAdultViewVisible = true;
		} else {
			mainImg.setEnabled(false);
			isAdultViewVisible = false;
			if(item.viewType.equals("BAN_IMG_SQUARE_GBA")) {
				//cache=file사이즈 add, 이미 있으면 원본을 반환 0226 일반 상품에만 반영
				item.imageUrl = ImageUtil.makeImageUrlWithSize(context,item.imageUrl);

				// ai 매장
				ImageUtil.loadImage(context, item.imageUrl, mainImg, R.drawable.no_img_550);
			} else {
				//cache=file사이즈 add, 이미 있으면 원본을 반환 0226 일반 상품에만 반영
				item.imageUrl = ImageUtil.makeImageUrlWithSize(context,item.imageUrl);

				ImageUtil.loadImage(context, item.imageUrl, mainImg, R.drawable.noimg_tv);
			}
		}

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

		if(time_deal != null) {
			time_deal.setVisibility(View.GONE);
		}

		// ad 구좌
		if(item.adDealYn != null && item.adDealYn.equals("Y")) {
			ViewUtils.showViews(adImage);
			final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
			if(badge != null && badge.imgUrl != null && !"".equals(badge.imgUrl)) {
				ImageUtil.loadImageFit(context, badge.imgUrl, adImage, R.drawable.ic_ad_and);
			}
		} else {
			ViewUtils.hideViews(adImage);
			/**
			 * 왼쪽 상단 뱃지(URL만 처리함) 1개의 아이템만 표현
			 */
			if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()  && !isAdultViewVisible){
				final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
				if(badge != null && badge.imgUrl != null && !"".equals(badge.imgUrl)){
					ImageUtil.loadImageBadge(context, badge.imgUrl, top_badge, R.drawable.transparent, HD);
					top_badge.setVisibility(View.VISIBLE);
				}
				//타입딜
				if(badge != null && badge.type != null && "timeDeal".equals(badge.type) && time_deal != null){
					time_deal.setVisibility(View.VISIBLE);
					setTimeDeal(badge);
				}
			}
		}

		if(item.valueInfo != null){
			valueInfo.setText(item.valueInfo);
		}

		for (int i=0; i<badge.length; i++) {
			badge[i].setVisibility(View.GONE);
		}

		if (item.rwImgList != null && !item.rwImgList.isEmpty()){
			for(int i=0; i< item.rwImgList.size() ; i++) {
				//3개를 초과하는 뱃지는 무시
				if (i >= badge.length) {
					break;
				}
				//TODO 확인필요(테블릿에서 상하 스크롤시 resource.getIntrinsicWidth()값이 작아져서 이미지도 작아짐)
//				//ImageUtil.loadImageBadge(context, data.product.rwImgList.get(i),badge[i] , 0, QHD);
//				ImageUtil.loadImageFit(context, item.rwImgList.get(i),badge[i] , 0);

				badge[i].layout(0, 0, 0, 0);
				ImageUtil.loadImage(context, item.rwImgList.get(i),badge[i] , 0);

				badge[i].setVisibility(View.VISIBLE);
			}
			tag_container.setVisibility(View.VISIBLE);
		} else {
			tag_container.setVisibility(View.GONE);
		}

		//상품 명 옆 이미지(1개만 표현)
		// 총알배송 "quickDlv"
		//해외직배송 "worldDlv"
		delivery_image.setVisibility(View.GONE);
		if(item.infoBadge != null && item.infoBadge.VT != null && !item.infoBadge.VT.isEmpty()){
			if("quickDlv".equals(item.infoBadge.VT.get(0).type)){
				delivery_image.setVisibility(View.VISIBLE);
				delivery_image.setImageResource(R.drawable.q_delivery_720);
			}else if("worldDlv".equals(item.infoBadge.VT.get(0).type)){
				delivery_image.setVisibility(View.VISIBLE);
				delivery_image.setImageResource(R.drawable.f_delivery_720);
			}
		}

		//오른쪽 하단 뱃지(최대 5개 이민수)
		footer_badge[0].setVisibility(View.GONE);
		footer_badge[1].setVisibility(View.GONE);
		footer_badge[2].setVisibility(View.GONE);
		footer_badge[3].setVisibility(View.GONE);
		footer_badge[4].setVisibility(View.GONE);

		if (item.imgBadgeCorner != null && item.imgBadgeCorner.RB != null && !item.imgBadgeCorner.RB.isEmpty() && !isAdultViewVisible){

			int index = 0;
			for( ImageBadge badge : item.imgBadgeCorner.RB){
				if("freeDlv".equals(badge.type)){
					footer_badge[index].setVisibility(View.VISIBLE);
					footer_badge[index].setBackgroundResource(R.drawable.dc_badge_01_720);
				}else if("freeInstall".equals(badge.type)){
					footer_badge[index].setVisibility(View.VISIBLE);
					footer_badge[index].setBackgroundResource(R.drawable.dc_badge_05_720);
				}else if("todayClose".equals(badge.type)){
					footer_badge[index].setVisibility(View.VISIBLE);
					footer_badge[index].setBackgroundResource(R.drawable.dc_badge_02_720);
				}else if("Reserves".equals(badge.type)){
					footer_badge[index].setVisibility(View.VISIBLE);
					footer_badge[index].setBackgroundResource(R.drawable.dc_badge_04_720);
				}else if("interestFree".equals(badge.type)){
					footer_badge[index].setVisibility(View.VISIBLE);
					footer_badge[index].setBackgroundResource(R.drawable.dc_badge_03_720);
				}
				if(index == 4){
					break;
				}
				index++;
			}
		}
		// vod icon
		if(btnPlay != null) {
			if (DisplayUtils.isTrue(item.hasVod)) {
				btnPlay.setVisibility(View.VISIBLE);
			} else {
				btnPlay.setVisibility(View.GONE);
			}
		}
		if (DisplayUtils.isValidString(item.productName)) {
			titleStringBuilder.append(item.productName);
		}

		txtTitle.append(titleStringBuilder);
		promotionName.setText(item.promotionName);
		// 보험상품인 경우

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
				txt_badge_per2.append(priceStringBuilder);

				//txt_badge_per.setPadding(0, 0, 0, context.getResources().getDimensionPixelSize(R.dimen.badge_text_padding));
				txt_badge_per.setVisibility(View.VISIBLE);

				isABtestTarget = false;

			}else{
				// 할인률
				txt_badge_per.append(ProductInfoUtil.getDiscountText(context, item));
				txt_badge_per2.append(ProductInfoUtil.getDiscountText_ab(context, item));//25% gone
				isABtestTarget = true;
			}
		}else{
			// 할인률
			txt_badge_per.append(ProductInfoUtil.getDiscountText(context, item));
			txt_badge_per2.append(ProductInfoUtil.getDiscountText(context, item)); //25% gone
			isABtestTarget = false;

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
		rowGoods.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, item.linkUrl);

				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
			}
		});

	}

	@Override
	public void onViewAttachedToWindow() {
		super.onViewAttachedToWindow();
		try {
			EventBus.getDefault().register(this);
		}catch(Exception e){
			Ln.e(e);
		}
		GlobalTimer.getInstance().startTimer();
	}

	@Override
	public void onViewDetachedFromWindow() {
		super.onViewDetachedFromWindow();
		EventBus.getDefault().unregister(this);
	}


	/**
	 * 타임딜 적용
	 * @param badge
	 */
	private void setTimeDeal(ImageBadge badge){
		if(badge != null && "timeDeal".equals(badge.type)){
			String endDate = badge.text;
			//종료시간이 없으면 타임딜을 보여주지 않음
			if(endDate == null || "".equals(endDate)){
				return;
			}
			time_deal.setVisibility(View.VISIBLE);

			try{
				TimeRemaining timeRemaining = new TimeRemaining(endDate);
				if(timeRemaining.isAfterTime()){
					time_text.setText(timeRemaining.getDisplayTime(TimeRemaining.DisplayType.HOUR));
				}else{
					time_deal.setVisibility(View.GONE);
					return;
				}
			} catch (Exception e) {
				//Ln.e("[ParseException]" + e.getLocalizedMessage());
				// 10/19 품질팀 요청
				// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
				// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
				// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
				Ln.e(e);
			}

		}
	}

	public void onEventMainThread(Events.TimerEvent event) {
		if(position > -1 && mAdapter != null && mAdapter.getInfo() != null && mAdapter.getInfo().contents != null && position <= mAdapter.getInfo().contents.size()-1 && mAdapter.getInfo().contents.get(position) != null){
			SectionContentList item = mAdapter.getInfo().contents.get(position).sectionContent;
			if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()){
				final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
				setTimeDeal(badge);
			}
		}
	}

}
