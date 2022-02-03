/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.viewholder;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import org.springframework.http.HttpEntity;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;

import static gsshop.mobile.v2.R.id.badge_1;
import static gsshop.mobile.v2.R.id.badge_2;
import static gsshop.mobile.v2.R.id.badge_3;
import static gsshop.mobile.v2.R.id.txt_price_expose;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_LIVE_MAINPRD_ALRAM_CANCEL;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_LIVE_MAINPRD_ALRAM_REG;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_MAINPRD_ALRAM_CANCEL;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_MAINPRD_ALRAM_REG;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_SUBPRD_COLLAPSE;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_SUBPRD_EXPAND;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.makeFormData;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.makeWiselogUrl;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_MAIN;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.viewTypeMap;

public class TLinePrdViewHolder_ab_bcv extends TLineBaseViewHolder {

	private Context context;

	//SPE 배너
	protected final ImageView spe_img;

	protected final View layoutTvGoods;
	protected final TextView prdComment;
	// 제품 이름
	protected final TextView txt_title;
	// 가격
	protected final TextView txt_base_price;
	protected final TextView txt_base_price_expose;
	protected final TextView txt_price;
	protected final TextView txt_price_won;

	protected final TextView price_info;
	protected TextView price_info_dummy;
	protected final RelativeLayout cardRantalTextLay;
	protected final TextView cardRantalText;

	// 방송 스크린샷 이미지
	protected final ImageView imageView;
	protected final View dim;

	//방송알림
	protected final LinearLayout alarmLay;
	protected final View alarmOff;
	protected final View alarmOn;

	protected final TextView btn_pay;
	protected final Button play;

	protected final LinearLayout price_rental_layout;
	protected final LinearLayout price_layout;
	protected final LinearLayout price_text_layout;

	protected final LinearLayout btn_layout;
	protected final LinearLayout price_info_layout;

	//뱃지
	protected final LinearLayout badge_layout;
	protected final ImageView[] badge = new ImageView[3];

	protected final View row_tv_goods;

	protected final View root_view;

	protected final LinearLayout price_root;

	private final View prd_bottom_line;

	//부상품 토글 관련
	private final LinearLayout product_toggle_layout;
	private final TextView product_toggle_text;
	private final LinearLayout product_toggle_count_layout;
	private final TextView product_toggle_count;
	private final ImageView product_toggle_icon;
	private final LinearLayout sub_prd_list;
	private final View sub_prd_top_line;
    private String moreText;
    private String closeText;
    private int moreIcon;
    private int closeIcon;

	/**
	 * 편성표 VOD상품아이콘 노출 AB테스트 by hanna
	 */
	private LinearLayout video_time_layout;
	private TextView video_time_text;

	private SchedulePrd data;

	/**
	 * @param itemView
	 */
	public TLinePrdViewHolder_ab_bcv(View itemView) {
		super(itemView);

		//SPE 배너
		spe_img = (ImageView) itemView.findViewById(R.id.spe_img);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), spe_img);

		row_tv_goods = itemView.findViewById(R.id.row_tv_goods);
		// 생방송 영역을 터치하면 버튼(편성표,적립금) 영역만 제외하고는 모두 이동.
		// 방송중이면 TV생방송 페이지로로, 아니면 단품화면으로 이동함(서버 linkUrl에서 알아서 처리됨).
		layoutTvGoods = itemView.findViewById(R.id.image_layout);

		prdComment = (TextView) itemView.findViewById(R.id.txt_comment);

		// 제품 이름
		txt_title = (TextView) itemView.findViewById(R.id.txt_title);

		// 가격
		txt_base_price = (TextView) itemView.findViewById(R.id.txt_base_price);
		txt_base_price_expose = (TextView) itemView.findViewById(R.id.txt_base_price_expose);
		txt_price = (TextView) itemView.findViewById(R.id.txt_price);
		txt_price_won = (TextView) itemView.findViewById(txt_price_expose);

		//청구할인
		cardRantalTextLay = (RelativeLayout) itemView.findViewById(R.id.cardRantalText_layout);
		cardRantalText = (TextView) itemView.findViewById(R.id.cardRantalText);
		price_info = (TextView) itemView.findViewById(R.id.price_info);
		price_info_dummy = (TextView) itemView.findViewById(R.id.price_info_dummy);

		// 방송 스크린샷 이미지
		imageView = (ImageView) itemView.findViewById(R.id.main_img);
		dim = itemView.findViewById(R.id.dim);

		//뱃지
		badge_layout = (LinearLayout) itemView.findViewById(R.id.badge_layout);
		badge[0] = (ImageView) itemView.findViewById(badge_1);
		badge[1] = (ImageView) itemView.findViewById(badge_2);
		badge[2] = (ImageView) itemView.findViewById(badge_3);

		//방송알림
		alarmOff = (View) itemView.findViewById(R.id.alarm_off);
		alarmOn = (View) itemView.findViewById(R.id.alarm_on);
		alarmLay = (LinearLayout) itemView.findViewById(R.id.lay_alarm);

		//구매하기
		btn_pay = (TextView) itemView.findViewById(R.id.btn_pay);

		play = (Button) itemView.findViewById(R.id.play);
		price_root = (LinearLayout) itemView.findViewById(R.id.price_root);
		price_rental_layout = (LinearLayout) itemView.findViewById(R.id.price_rental_layout);
		price_layout = (LinearLayout) itemView.findViewById(R.id.price_layout);
		price_text_layout = (LinearLayout) itemView.findViewById(R.id.price_text_layout);
		btn_layout = (LinearLayout) itemView.findViewById(R.id.btn_layout);
		price_info_layout = (LinearLayout) itemView.findViewById(R.id.price_info_layout);
		root_view = itemView.findViewById(R.id.root_view);

		prd_bottom_line = (View) itemView.findViewById(R.id.prd_bottom_line);

		//부상품
		product_toggle_layout = (LinearLayout) itemView.findViewById(R.id.product_toggle_layout);
		product_toggle_text = (TextView) itemView.findViewById(R.id.product_toggle_text);
		product_toggle_icon = (ImageView) itemView.findViewById(R.id.product_toggle_icon);
		sub_prd_list = (LinearLayout) itemView.findViewById(R.id.sub_prd_list);
		sub_prd_top_line = (View) itemView.findViewById(R.id.sub_prd_top_line);
		product_toggle_count_layout = (LinearLayout) itemView.findViewById(R.id.product_toggle_count_layout);
		product_toggle_count = (TextView) itemView.findViewById(R.id.product_toggle_count);
        moreIcon = R.drawable.bt_arrow1_an;
        closeIcon = R.drawable.bt_arrow2_an;

		/**
		 * 편성표 VOD상품아이콘 노출 AB테스트 by hanna
		 */
		video_time_layout = itemView.findViewById(R.id.video_time_layout);
		video_time_text = itemView.findViewById(R.id.video_time_text);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), imageView);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), dim);
	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, final int position, final SchedulePrd data) {
		this.context = context;
		this.data = data;

		//spe 배너
		if (data.specialPgmInfo != null && !TextUtils.isEmpty(data.specialPgmInfo.imageUrl)) {
			ImageUtil.loadImageResize(context, data.specialPgmInfo.imageUrl, spe_img, R.drawable.noimg_logo);
			spe_img.setVisibility(View.VISIBLE);
			spe_img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String finalImgLink = data.specialPgmInfo.linkUrl;
					if (finalImgLink != null && finalImgLink.length() > 4) {
						WebUtils.goWeb(context, finalImgLink);
					}
				}
			});
		} else {
			spe_img.setVisibility(View.GONE);
		}

		// 방송 스크린샷 이미지
		if (data.product.mainPrdImgUrl != null && !TextUtils.isEmpty(data.product.mainPrdImgUrl.trim())) {
			//cache=file사이즈 add, 이미 있으면 원본을 반환 0226 일반 상품에만 반영
			data.product.mainPrdImgUrl = ImageUtil.makeImageUrlWithSize(context,data.product.mainPrdImgUrl.trim());
			ImageUtil.loadImageTvLive(context.getApplicationContext(), data.product.mainPrdImgUrl.trim(), imageView, R.drawable.noimg_tv);
		} else {
			//cache=file사이즈 add, 이미 있으면 원본을 반환 0226 일반 상품에만 반영
			data.product.subPrdImgUrl = ImageUtil.makeImageUrlWithSize(context,data.product.subPrdImgUrl.trim());
			//200x200 직사각형 이미지, 이미지 비율로 스케일링하고 중앙정렬함(좌우여백만 생기는 형태)
			ImageUtil.loadImageTvLiveSmallImage(context.getApplicationContext(), data.product.subPrdImgUrl.trim(), imageView, R.drawable.noimg_tv);
		}

		prdComment.setVisibility(View.VISIBLE);
		if(AIR_BUY.equalsIgnoreCase(data.product.imageLayerFlag)){
			prdComment.setText(R.string.layer_flag_air_buy);
		}else if(SOLD_OUT.equalsIgnoreCase(data.product.imageLayerFlag)){
			prdComment.setText(R.string.layer_flag_sold_out);
		}else{
			prdComment.setVisibility(View.GONE);
		}

		// 생방송 영역을 터치하면 버튼(편성표,적립금) 영역만 제외하고는 모두 이동.
		// 방송중이면 TV생방송 페이지로로, 아니면 단품화면으로 이동함(서버 linkUrl에서 알아서 처리됨).
		if (!TextUtils.isEmpty(data.product.linkUrl)) {
			layoutTvGoods.setTag(DisplayUtils.getFullUrl(data.product.linkUrl));
			layoutTvGoods.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString(), ((Activity) context).getIntent());
				}
			});
		}

		//가격 영역 클릭시 이동
		price_root.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(data.product.linkUrl)) {
					WebUtils.goWeb(context, DisplayUtils.getFullUrl(data.product.linkUrl), ((Activity) context).getIntent());

				}
			}
		});

		imageView.setContentDescription(data.product.exposPrdName);

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layoutTvGoods.performClick();
			}
		});

		if(row_tv_goods != null) {
			//10/19 품질팀 요청
			if(data != null && data.product.linkUrl != null) {
				row_tv_goods.setTag(DisplayUtils.getFullUrl(data.product.linkUrl));
			}
			row_tv_goods.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString(), ((Activity) context).getIntent());
				}
			});
		}

		// 제품 이름
		txt_title.setText(data.product.exposPrdName);
		if(data.product.salePrice != null) {
			String basePrise = data.product.salePrice;
			if(data.product.exposePriceText != null){
				txt_base_price_expose.setText(data.product.exposePriceText);
			}
			txt_base_price.setText(DisplayUtils.getFormattedNumber(basePrise));
			txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			txt_base_price.setVisibility(View.VISIBLE);

			if("0".equals(data.product.salePrice)){
				txt_base_price.setVisibility(View.GONE);
				txt_base_price_expose.setVisibility(View.GONE);
			}else{
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price_expose.setVisibility(View.VISIBLE);
			}
		}

		txt_base_price.setTextColor(Color.parseColor("#666666"));
		txt_base_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.tv_scd_broadPriceTextSize));
		cardRantalTextLay.setVisibility(View.GONE);

		for (int i=0; i<badge.length; i++) {
			badge[i].setVisibility(View.GONE);
		}

		//초기화

		price_info_layout.setGravity(Gravity.LEFT);
		price_info_layout.setVisibility(View.VISIBLE);
		price_info.setGravity(Gravity.CENTER_HORIZONTAL);
		root_view.setVisibility(View.VISIBLE);
		price_rental_layout.setVisibility(View.VISIBLE);
		badge_layout.setVisibility(View.VISIBLE);
		btn_layout.setVisibility(View.VISIBLE);
		price_root.setPadding(price_root.getPaddingLeft(), price_root.getPaddingTop(),
				price_root.getPaddingRight(), context.getResources().getDimensionPixelSize(R.dimen.tv_scd_price_bottom));
		prd_bottom_line.setVisibility(View.VISIBLE);

		//부상품 expand/collapse
		product_toggle_layout.setVisibility(View.GONE);
		sub_prd_list.setVisibility(View.GONE);
		sub_prd_top_line.setVisibility(View.GONE);
		if (data.product.subProductList != null && data.product.subProductList.size() > 0) {
			moreText = context.getString(R.string.related_product);
			closeText = context.getString(R.string.common_close);
			product_toggle_count.setText("" + data.product.subProductList.size() + context.getString(R.string.product_description_sale_tail_text));

			sub_prd_list.removeAllViews();
			product_toggle_layout.setVisibility(View.VISIBLE);

			for (int i=0; i<data.product.subProductList.size(); i++) {
				View itemView = LayoutInflater.from(context).inflate(R.layout.recycler_item_tv_schedule_main_sub, null);
				TLineSubViewHolder subPrd = new TLineSubViewHolder(itemView);
				View rsltView = subPrd.onBindViewHolder(context, position, data.product.subProductList.get(i), data.product.pgmLiveYn);
				sub_prd_list.addView(rsltView);
			}
			//onBind 초기상태 플래그 false : 초기 , true 2번째,
			if(data.expandInitState) {
				if (data.expandState) {
					//onBind 호출시 펼친상태 유지
					setExpandUI();
				} else {
					setCollapseUI();
				}
			}
			else{
				setCollapseUI();
			}
			product_toggle_layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (data.expandState) {
                        //닫기상태로 변경
						//collapse(sub_prd_list, 250, 0);
						setCollapseUI();
                        ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_SUBPRD_COLLAPSE));
					} else {
                        //펼친상태로 변경
						sub_prd_list.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
						int h = sub_prd_list.getMeasuredHeight();
						//Log.e("TAG", "h : " + h);
						//expand(sub_prd_list, 250, h);
						setExpandUI();
                        ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_SUBPRD_EXPAND));
					}
					data.expandState = !data.expandState;
				}
			});
		}

		//공영상품
		if ("Y".equals(data.publicBroadYn)) {
			if ("Y".equals(data.pgmLiveYn)) {
				//생방송이면 상품이미지 + 상품명 노출 (가격, 버튼 노출안함)
			} else {
				//생방송 아니면 상품명만 노출
				root_view.setVisibility(View.GONE);
			}
			//상품명 노출시 하단 여백조정이 필요함
			price_root.setPadding(price_root.getPaddingLeft(), price_root.getPaddingTop(), price_root.getPaddingRight(), 0);
			//가격 노출안함
			price_rental_layout.setVisibility(View.GONE);
			//뱃지 노출안함
			badge_layout.setVisibility(View.GONE);
			//버튼 노출안함
			btn_layout.setVisibility(View.GONE);
			//하단라인 노출안함
			prd_bottom_line.setVisibility(View.GONE);

			return;

			//보험상품
		} else if ("Y".equals(data.product.insuYn)) {
			//상품명 노출시 하단 여백조정이 필요함
			price_root.setPadding(price_root.getPaddingLeft(), price_root.getPaddingTop(), price_root.getPaddingRight(), 0);
			//가격 노출안함
			price_rental_layout.setVisibility(View.GONE);
			//뱃지 노출안함
			data.product.rwImgList = null;
		}

		if (data.product.rwImgList != null && !data.product.rwImgList.isEmpty()){
			for(int i=0; i< data.product.rwImgList.size() ; i++) {
				//3개를 초과하는 뱃지는 무시
				if (i >= badge.length) {
					break;
				}
				//TODO 확인필요(테블릿에서 상하 스크롤시 resource.getIntrinsicWidth()값이 작아져서 이미지도 작아짐)
				//ImageUtil.loadImageBadge(context, data.product.rwImgList.get(i),badge[i] , 0, QHD);
				badge[i].layout(0, 0, 0, 0);
				ImageUtil.loadImage(context, data.product.rwImgList.get(i),badge[i] , 0);
				badge[i].setVisibility(View.VISIBLE);
			}
			badge_layout.setVisibility(View.VISIBLE);
		} else {
			badge_layout.setVisibility(View.GONE);
		}

		txt_price.setVisibility(View.VISIBLE);
		txt_price_won.setVisibility(View.VISIBLE);
		price_info_dummy.setVisibility(View.INVISIBLE);

		//렌탈상품
		if ("Y".equals(data.product.rentalYn)) {
			txt_base_price.setTextColor(Color.parseColor("#111111"));
			txt_base_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.tv_scd_rentalTextSize));

			txt_price.setText(data.product.rentalPrice);
			txt_price_won.setVisibility(View.GONE);
			txt_base_price.setText(data.product.rentalText);

			badge_layout.setVisibility(View.GONE);

			txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			txt_base_price_expose.setVisibility(View.GONE);
			txt_base_price.setVisibility(View.VISIBLE);

			cardRantalText.setText(data.product.rentalEtcText);
			cardRantalTextLay.setVisibility(View.VISIBLE);
			cardRantalText.setGravity(Gravity.LEFT);

			//휴대폰
		} else if ("Y".equals(data.product.cellPhoneYn)) {
			txt_base_price.setTextColor(Color.parseColor("#111111"));
			txt_base_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.tv_scd_rentalTextSize));
			if (DisplayUtils.isValidString(data.product.exposePriceText)) {
				txt_price_won.setText(data.product.exposePriceText);
			}

			badge_layout.setVisibility(View.GONE);

			txt_price.setText(data.product.broadPrice);
			if(!"0".equals(data.product.salePrice)){
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				txt_base_price_expose.setVisibility(View.VISIBLE);
			}

			cardRantalTextLay.setVisibility(View.VISIBLE);
			cardRantalText.setGravity(Gravity.LEFT);

			if(!TextUtils.isEmpty(data.product.rentalText)){
				txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
				txt_base_price.setText(data.product.rentalText);
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price_expose.setVisibility(View.GONE);
				cardRantalText.setText(data.product.rentalEtcText);
			}

		}else {
			// 아니면 가격 보이게
			if (DisplayUtils.isValidNumberString(data.product.broadPrice)) {
				txt_price.setText(DisplayUtils.getFormattedNumber(data.product.broadPrice));

				if (DisplayUtils.isValidString(data.product.exposePriceText)) {
					txt_price_won.setText(data.product.exposePriceText);
				} else {
					txt_price_won.setText(R.string.won);
				}

			} else {
				// 가격이 숫자가 아니거나 0이면 아무 표시하지 않음
				txt_price.setVisibility(View.INVISIBLE);
				txt_price_won.setVisibility(View.GONE);
			}
		}

		if ("RATE".equalsIgnoreCase(data.product.priceMarkUpType)) {
			//AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_scd_bigTextSize), true);
			//AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_scd_smallTextSize), true);

			AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(27, true);
			AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(16, true);
			SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();


			//priceMarkUP이 유효한지 체크
			if(data.product!= null && DisplayUtils.isValidString(data.product.priceMarkUp))
			{
					//priceMarkUp이 String형태의 숫자인지 체크 && priceMarkUp이 0보다 큰지 체크
					if(DisplayUtils.isNumeric(data.product.priceMarkUp) && Integer.parseInt(data.product.priceMarkUp) > Keys.ZERO_DISCOUNT_RATE){
						priceStringBuilder.append(data.product.priceMarkUp).append(context.getString(R.string.percent));
						priceStringBuilder.setSpan(bigSizeSpan, 0, data.product.priceMarkUp.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						priceStringBuilder.setSpan(smalSizeSpan, data.product.priceMarkUp.length(), data.product.priceMarkUp.length() + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						priceStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ed1f60")), 0, priceStringBuilder.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

						price_info.setVisibility(View.VISIBLE);
						price_info.setGravity(Gravity.CENTER);
						price_info.setBackgroundResource(android.R.color.transparent);
						price_info.setText(priceStringBuilder);

					}else{ //priceMarkUP이 0보다 작을경우 + 문자열일 경우
						price_info.setVisibility(View.GONE);
						price_info_dummy.setVisibility(View.GONE);
					}

			}else{ //priceMarkUP이 null인 경우 + 공백(length = 0)인 경우
						price_info.setVisibility(View.GONE);
						price_info_dummy.setVisibility(View.GONE);
			}

			/*//갤럭시S6(5.1.1)에서 할인율 글자가 일부 짤리는 현상 발생하여 추가
			price_info.requestLayout();
			price_info_dummy.setText(priceStringBuilder);
			price_info_dummy.setBackgroundResource(android.R.color.transparent);*/

		}
		else {

			//가격이 없으면
			if ("".equals(data.product.broadPrice) && !"".equals(data.product.rentalEtcText)) {
				price_info_layout.setVisibility(View.GONE);
				badge_layout.setVisibility(View.GONE);
				cardRantalText.setText(data.product.rentalEtcText);
				price_info_dummy.setVisibility(View.GONE);
				cardRantalTextLay.setVisibility(View.VISIBLE);
				cardRantalText.setGravity(Gravity.CENTER);
			}

            price_info.setVisibility(View.GONE);
            price_info_dummy.setVisibility(View.GONE);
		}

		//방송알림
		alarmLay.setVisibility(View.VISIBLE);
		if ("Y".equals(data.product.broadAlarmDoneYn)) {
			alarmOff.setVisibility(View.GONE);
			alarmOn.setVisibility(View.VISIBLE);
		} else if ("N".equals(data.product.broadAlarmDoneYn)) {
			alarmOff.setVisibility(View.VISIBLE);
			alarmOn.setVisibility(View.GONE);
		} else {
			//"Y", "N" 이외 값은 알람영역 숨김
			alarmLay.setVisibility(View.INVISIBLE);
		}

		alarmLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "";
				String type = "";
				String mseqUrl= "";
                HttpEntity<Object> requestEntity = makeFormData(
                		data.product.prdId, data.product.exposPrdName, null, null);

				// 방송 알림 등록 / 취소 효율 코드
				if ("Y".equals(data.product.broadAlarmDoneYn)) {
					//방송알림 취소
                    url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_DELETE;
					type = "delete";
					//방송 알림 취소 효율 코드
					mseqUrl = "Y".equals(data.pgmLiveYn) ? makeWiselogUrl(SCH_LIVE_MAINPRD_ALRAM_CANCEL) : makeWiselogUrl(SCH_MAINPRD_ALRAM_CANCEL);
				} else if ("N".equals(data.product.broadAlarmDoneYn)) {
					//방송알림 설정 팝업
                    url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_QUERY;
					type = "query";
					//방송 알림 등록 효율 코드
					mseqUrl = "Y".equals(data.pgmLiveYn) ? makeWiselogUrl(SCH_LIVE_MAINPRD_ALRAM_REG) : makeWiselogUrl(SCH_MAINPRD_ALRAM_REG);
				}
				new TVScheduleShopFragment.BroadAlarmUpdateController(context).execute(url, requestEntity, type);
				((HomeActivity) context).setWiseLogHttpClient(mseqUrl);
			}
		});

		// 바로구매
		if(data.product.directOrdInfo != null){
			btn_pay.setVisibility(View.VISIBLE);
			if (TextUtils.isEmpty(data.product.directOrdInfo.text)) {
				//서버에서 텍스트가 안내려 온 경우 디폴트 문구 세팅
				btn_pay.setText(context.getString(R.string.home_tv_live_btn_tv_pay_text));
			} else {
				btn_pay.setText(data.product.directOrdInfo.text);
			}
			btn_pay.setTag(DisplayUtils.getFullUrl(data.product.directOrdInfo.linkUrl));
			btn_pay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString());
				}
			});
		} else {
			btn_pay.setVisibility(View.GONE);
		}

		//3가지 버튼이 모두 비노출인 경우 버큰영역을 숨김
		if (viewTypeMap.get(data.viewType) == TYPE_PRD_MAIN) {
			if (alarmLay.getVisibility() == View.INVISIBLE && btn_pay.getVisibility() == View.GONE) {
				btn_layout.setVisibility(View.GONE);
			} else {
				//공영상품일때는 버튼 노출 안함
				if (!"Y".equals(data.publicBroadYn)) {
					btn_layout.setVisibility(View.VISIBLE);
				}
			}
		}

		/**
		 * TV편성표 VOD상품아이콘 노출의 건 AB 테스트 by hanna
		 */
		if(DisplayUtils.isValidString(data.product.videoTime)){
			video_time_layout.setVisibility(View.VISIBLE);
			video_time_text.setText(data.product.videoTime);
		}else{
			video_time_layout.setVisibility(View.GONE);
		}
	}

	/**
	 * 부상품 펼친상태로 UI를 변경한다.
	 */
	private void setExpandUI() {
        product_toggle_text.setText(closeText);
        product_toggle_icon.setBackgroundResource(closeIcon);
        sub_prd_list.setVisibility(View.VISIBLE);
        sub_prd_top_line.setVisibility(View.VISIBLE);
		product_toggle_count_layout.setVisibility(View.GONE);
    }

	/**
	 * 부상품 닫힌상태로 UI를 변경한다.
	 */
    private void setCollapseUI() {
        product_toggle_text.setText(moreText);
        product_toggle_icon.setBackgroundResource(moreIcon);
        sub_prd_list.setVisibility(View.GONE);
		sub_prd_top_line.setVisibility(View.GONE);
		product_toggle_count_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 편성상품 정보를 반환한다.
     *
     * @return SchedulePrd
     */
	public SchedulePrd getPrdData() {
		return data;
	}

    /**
	 * 부상품을 펼친다.
	 *
	 * @param v 대상뷰
	 * @param duration 애니메이션 시간
	 * @param targetHeight 펼칠 높이
	 */
	private void expand(final View v, int duration, int targetHeight) {
		int prevHeight  = v.getHeight();

		v.setVisibility(View.VISIBLE);
		ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				v.getLayoutParams().height = (int) animation.getAnimatedValue();
				v.requestLayout();
			}
		});
		valueAnimator.setInterpolator(new DecelerateInterpolator());
		valueAnimator.setDuration(duration);
		valueAnimator.start();
	}

	/**
	 * 부상품을 접는다.
	 *
	 * @param v 대상뷰
	 * @param duration 애니메이션 시간
	 * @param targetHeight 닫을 높이
	 */
	private void collapse(final View v, int duration, int targetHeight) {
		int prevHeight  = v.getHeight();
		ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
		valueAnimator.setInterpolator(new DecelerateInterpolator());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				v.getLayoutParams().height = (int) animation.getAnimatedValue();
				v.requestLayout();
			}
		});
		valueAnimator.setInterpolator(new DecelerateInterpolator());
		valueAnimator.setDuration(duration);
		valueAnimator.start();
	}
}
