/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.publicusage;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.flexible.RenewalPriceInfoBottomViewHolder;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * 홈 하단 개인화구좌(편성표 부상품 형태) 단품 item
 */
public class Prd1_ListVH extends RenewalPriceInfoBottomViewHolder {

	/**
	 * 섬네일과 가격 전체영역
	 */
	private final View prdView;

	/**
	 * 섬네일
	 */
	private final ImageView prdImage;

	/**
	 * 방송중구매가능, 일시품절 표시영역
	 */
	protected final TextView prdComment;

	protected TextView text_brd_time;

	private LinearLayout txt_review_area;

	private int position = -1;

	/**
	 * 하단 디바이더가 다음 뷰타입과 같으면 1dp 다르면 10dp
	 */
	private final View mViewBottomDivider1dp;
	private final View mViewBottomDivider10dp;

	protected View itemView;

	/**
	 * @param itemView
	 */
	public Prd1_ListVH(View itemView) {
		super(itemView);

		this.itemView = itemView;

		prdView = itemView.findViewById(R.id.view_prd);
		prdImage = itemView.findViewById(R.id.image_prd);
		prdComment = itemView.findViewById(R.id.txt_comment);
		text_brd_time = itemView.findViewById(R.id.text_brd_time);
		txt_review_area = itemView.findViewById(R.id.txt_review_area);

		mViewBottomDivider1dp = itemView.findViewById(R.id.view_bottom_divider_1dp);
		mViewBottomDivider10dp = itemView.findViewById(R.id.view_bottom_divider_10dp);
	}

	public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {
		this.context = context;
		this.navigationId = info.naviId;

		final SectionContentList item = info.contents.get(position).sectionContent;

		boolean isSameToNext = false;

		if (info.contents.size() > position + 1) {
			final SectionContentList nextItem = info.contents.get(position + 1).sectionContent;
			if (nextItem != null && !TextUtils.isEmpty(nextItem.viewType) &&
					item != null && !TextUtils.isEmpty(item.viewType) &&
					item.viewType.equals(nextItem.viewType)) {
				isSameToNext = true;
			}
		}

		this.position = position;
		setItems(context, item, isSameToNext);
	}

	public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
		super.onBindViewHolder(context, position, moduleList);
		this.context = context;
		ModuleList item = moduleList.get(position);

		setItems(context, item, true);
	}

	public View onBindViewHolder(Context context, SectionContentList item) {
		this.context = context;

		return setItems(context, item, true);
	}

	protected View setItems(final Context context, final SectionContentList item, boolean isSameToNext) {
		if (isSameToNext) {
			mViewBottomDivider1dp.setVisibility(View.VISIBLE);
			mViewBottomDivider10dp.setVisibility(View.GONE);
		}
		else {
			mViewBottomDivider1dp.setVisibility(View.GONE);
			mViewBottomDivider10dp.setVisibility(View.VISIBLE);
		}
		
		//상품 이미지
		ImageUtil.loadImage(context, item.imageUrl, prdImage, R.drawable.noimage_166_166);

		//방송중구매가능, 일시품절 표시
		//구매하기 버튼이 노출되는 경우는 버튼에, 아닌 경우는 섬네일 하단에 표시
		if (prdComment != null) {
			prdComment.setVisibility(View.GONE);
			if (item.directOrdInfo == null) {
				prdComment.setVisibility(View.VISIBLE);
				if (AIR_BUY.equalsIgnoreCase(item.imageLayerFlag)) {
					prdComment.setText(R.string.layer_flag_air_buy);
				} else if (SOLD_OUT.equalsIgnoreCase(item.imageLayerFlag)) {
					prdComment.setText(R.string.layer_flag_sold_out);
				}
			}
		}

		//가격표시용 공통모듈에 맞게 데이타 변경
		TvLiveBanner tvLiveBanner = new TvLiveBanner();
		tvLiveBanner.rProductName = item.productName;
		tvLiveBanner.rSalePrice = item.salePrice;
		tvLiveBanner.rBasePrice = item.basePrice;
		tvLiveBanner.rExposePriceText = item.exposePriceText;
		tvLiveBanner.rDiscountRate = item.discountRate;
		tvLiveBanner.rLinkUrl = item.linkUrl;

		//렌탈관련
		tvLiveBanner.rRentalText = item.rentalText;
		tvLiveBanner.rRentalPrice = item.mnlyRentCostVal;
		tvLiveBanner.rProductType = item.productType;

		//혜택 관련
		tvLiveBanner.rAllBenefit = item.allBenefit;
		tvLiveBanner.rSource = item.source;

		//솔드 아웃 관련 방송중 구매가능관련
		//생방송에 현재는 없다. 하지만 편성표에는 있다.
		tvLiveBanner.rImageLayerFlag = item.imageLayerFlag;

		//상품평 관련
		tvLiveBanner.rAddTextLeft = item.addTextLeft;
		tvLiveBanner.rAddTextRight = item.addTextRight;

		//브렌드 관련
		tvLiveBanner.rBrandNm = item.brandNm;

		// 딜 여부 (true/false)
		tvLiveBanner.deal = item.deal;

		super.bindViewHolder(tvLiveBanner, position,null);

		//전체영역에 대한 링크
		if (isNotEmpty(item.linkUrl)) {
			prdView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, item.linkUrl);
				}
			});
		}

		//버튼 세개 다 없으면 버튼 감싸는 레이아웃 GONE
		if (lay_live_talk.getVisibility() == View.GONE && lay_alarm.getVisibility() == View.GONE && txt_buy.getVisibility() == View.GONE) {
			lay_button_container.setVisibility(View.GONE);
		}

		if (text_brd_time != null ) {
			text_brd_time.setVisibility(View.GONE);
			if (item.broadTimeText != null && DisplayUtils.isValidString(item.broadTimeText)) {
				text_brd_time.setText(item.broadTimeText);
				text_brd_time.setVisibility(View.VISIBLE);
			}
		}
		return itemView;
	}
}
