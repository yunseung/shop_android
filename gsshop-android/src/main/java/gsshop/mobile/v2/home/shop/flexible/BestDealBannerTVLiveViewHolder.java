/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.tms.sdk.common.util.StringUtil;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.main.TvLiveContent;
import gsshop.mobile.v2.home.main.TvLiveContentAction;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.tvshoping.TvLiveTimeLayout;
import gsshop.mobile.v2.home.shop.tvshoping.TvShopFragment;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.tv.VideoParameters;
import gsshop.mobile.v2.support.ui.counter.NumberCounter;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.web.WebUtils;

import static gsshop.mobile.v2.R.id.badge_1;
import static gsshop.mobile.v2.R.id.badge_2;
import static gsshop.mobile.v2.R.id.badge_3;
import static gsshop.mobile.v2.R.id.txt_price_expose;

/**
 * tv live banner.
 *
 */
@SuppressLint("NewApi")
public class BestDealBannerTVLiveViewHolder extends BaseViewHolder {

	// TV생방송 progress
	public TvLiveTimeLayout layoutTvLiveProgress;
	protected final View layoutTvGoods;
	// 제품 이름
	private final TextView txt_title;
	// 가격
	private final TextView txt_base_price;
	private final TextView txt_base_price_expose;
	private final TextView txt_price;
	private final TextView txt_price_won;

	private final TextView price_info;
	private final TextView cardRantalText;

	// 방송 스크린샷 이미지
	protected final ImageView imageView;
	protected final View dim;

	// 편성표 보기
	private final Button scheduleBtn;
	// 5%적립금
	private final View btn_live_talk;
	private final TextView live_talk_text;

	private final Button btn_pay;
	private final Button play;


	private final LinearLayout price_layout;
	private final LinearLayout price_text_layout;

	private final LinearLayout btn_layout;
	private final LinearLayout price_info_layout;

	protected final ImageView[] badge = new ImageView[3];

	public final View row_tv_goods;

	public final View root_view;
	public final View live_talk_line;
	protected ShopInfo mInfo;

	private final View counter;
	private final LinearLayout number_counter_layout;
	private final LinearLayout price_root;

	private boolean isStartAnimation = false;

	private Animation startAnimation;
	private Animation closeAnimation;
	private NumberCounter numberCounter;

	private boolean isUpdate;

	/**
	 * @param itemView
	 */
	public BestDealBannerTVLiveViewHolder(View itemView) {
		super(itemView);

		// TV생방송 progress
		layoutTvLiveProgress = (TvLiveTimeLayout) itemView.findViewById(R.id.tv_live_progress);
		row_tv_goods = itemView.findViewById(R.id.row_tv_goods);
		// 생방송 영역을 터치하면 버튼(편성표,적립금) 영역만 제외하고는 모두 이동.
		// 방송중이면 TV생방송 페이지로로, 아니면 단품화면으로 이동함(서버 linkUrl에서 알아서 처리됨).
		layoutTvGoods = itemView.findViewById(R.id.image_layout);
		// 제품 이름
		txt_title = (TextView) itemView.findViewById(R.id.txt_title);

		// 가격
		txt_base_price = (TextView) itemView.findViewById(R.id.txt_base_price);
		txt_base_price_expose = (TextView) itemView.findViewById(R.id.txt_base_price_expose);
		txt_price = (TextView) itemView.findViewById(R.id.txt_price);
		txt_price_won = (TextView) itemView.findViewById(txt_price_expose);

		//청구할인
		cardRantalText = (TextView) itemView.findViewById(R.id.cardRantalText);
		price_info = (TextView) itemView.findViewById(R.id.price_info);

		// 방송 스크린샷 이미지
		imageView = (ImageView) itemView.findViewById(R.id.main_img);
		dim = itemView.findViewById(R.id.dim);

		badge[2] = (ImageView) itemView.findViewById(badge_1);
		badge[1] = (ImageView) itemView.findViewById(badge_2);
		badge[0] = (ImageView) itemView.findViewById(badge_3);

		// 편성표 보기
		scheduleBtn = (Button) itemView.findViewById(R.id.btn_schedule);
		// 5%적립금
		btn_live_talk = itemView.findViewById(R.id.btn_live_talk);
		live_talk_text = (TextView)itemView.findViewById(R.id.live_talk_text);
		btn_pay = (Button) itemView.findViewById(R.id.btn_pay);
		play = (Button) itemView.findViewById(R.id.play);
		price_root = (LinearLayout) itemView.findViewById(R.id.price_root);
		price_layout = (LinearLayout) itemView.findViewById(R.id.price_layout);
		price_text_layout = (LinearLayout) itemView.findViewById(R.id.price_text_layout);
		btn_layout = (LinearLayout) itemView.findViewById(R.id.btn_layout);
		price_info_layout = (LinearLayout) itemView.findViewById(R.id.price_info_layout);
		root_view = itemView.findViewById(R.id.root_view);
		live_talk_line = itemView.findViewById(R.id.live_talk_line);
		counter = itemView.findViewById(R.id.counter);
		number_counter_layout = (LinearLayout) itemView.findViewById(R.id.number_counter_layout);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), imageView);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), dim);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), counter);
	}

	/**
	 *  bind
	 */
	@Override
	public void onBindViewHolder(Context context, int position, ShopInfo mInfo, String action, String label, String sectionName) {
		this.mInfo = mInfo;
		bindViewHolder(context, position, mInfo.tvLiveBanner, mInfo.sectionList.sectionCode, action, label, sectionName);
	}


	private static final String SECTION_CODE_PREFIX = "#";
	private String mPrevBroadEndTime = null;
	private String tvLiveUrl;

	public void bindViewHolder(final Context context, final int position, final TvLiveBanner tvLiveBanner, final String sectionCode, final String action, final String label, final String sectionName) {

		//생방송을 새로 고침 했을때 Tv정보를 사용하기위해
		if(this.mInfo != null && tvLiveBanner != null && mInfo.tvLiveBanner != null) {
			boolean isTvShoping = this.mInfo.tvLiveBanner.isTvShoping;
			String tvLiveUrl = this.mInfo.tvLiveBanner.tvLiveUrl;
			TvShopFragment.SubTabMenu subMenu = this.mInfo.tvLiveBanner.currentSubTabMenu;
			tvLiveBanner.isTvShoping = isTvShoping;
			tvLiveBanner.tvLiveUrl = tvLiveUrl;
			tvLiveBanner.currentSubTabMenu = subMenu;
		}
		mInfo.tvLiveBanner = tvLiveBanner;

		tvLiveUrl = mInfo.tvLiveBanner.tvLiveUrl;

		// tv방송 정보가 유효한지 체크
		if (verifyTvInfo(tvLiveBanner)) {
			mPrevBroadEndTime = tvLiveBanner.broadCloseTime;
		}

		if(tvLiveBanner.broadType != null && tvLiveBanner.broadType.equals(context.getResources().getString(R.string.home_tv_live_view_on_the_air))){
			layoutTvLiveProgress.setBackgroundResource(R.drawable.tv_tag_onair_android);
		}else if(tvLiveBanner.broadType != null && tvLiveBanner.broadType.equals(context.getResources().getString(R.string.home_tv_live_view_best))){
			layoutTvLiveProgress.setBackgroundResource(R.drawable.tv_tag_best_android);
		}

//		// TV생방송 progress
		layoutTvLiveProgress.setOnTvLiveFinishedListener(new TvLiveTimeLayout.OnTvLiveFinishedListener() {

			@Override
			public void onTvLiveFinished() {
				// 방송이 종료되면 data 다시 loading. 불러온 데이타가 유효하지 않으면
				// viewPager 이동이나 activity resume시 다시 불러옴.
				counter.setVisibility(View.GONE);
				if(startAnimation != null){
					startAnimation.cancel();
				}
				if(closeAnimation != null){
					closeAnimation.cancel();
				}
				if(numberCounter != null){
					numberCounter.cancel();
				}
				//방송정보가 변경되면 캐시를 초기화한다.
				MainApplication.clearCache();

				new GetTvLiveContentUpdateController(context, action, label, sectionName).execute(false, sectionCode);
			}

		});

		// 방송 스크린샷 이미지
		ImageUtil.loadImageTvLive(context.getApplicationContext(),
				!TextUtils.isEmpty(tvLiveBanner.imageUrl) ? tvLiveBanner.imageUrl.trim() : "", imageView, R.drawable.noimg_tv);

		// 생방송 영역을 터치하면 버튼(편성표,적립금) 영역만 제외하고는 모두 이동.
		// 방송중이면 TV생방송 페이지로로, 아니면 단품화면으로 이동함(서버 linkUrl에서 알아서 처리됨).
		if (!TextUtils.isEmpty(tvLiveBanner.linkUrl)) {
			layoutTvGoods.setTag(DisplayUtils.getFullUrl(tvLiveBanner.linkUrl));
			layoutTvGoods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString(), ((Activity) context).getIntent());
					// GTM AB Test 클릭이벤트 전달
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
				}
			});
		}

		//가격 영역 클릭시 이동
		price_root.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(tvLiveBanner.linkUrl)) {
					WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveBanner.linkUrl), ((Activity) context).getIntent());
				}
			}
		});

		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				layoutTvGoods.performClick();
			}
		});

		if(row_tv_goods != null) {
			//10/19 품질팀 요청
			if(tvLiveBanner != null && tvLiveBanner.linkUrl != null) {
				row_tv_goods.setTag(DisplayUtils.getFullUrl(tvLiveBanner.linkUrl));
			}
			row_tv_goods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString(), ((Activity) context).getIntent());
					// GTM AB Test 클릭이벤트 전달
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
				}
			});
		}

//		tvLiveBanner.basePrice ="0";

		// 제품 이름
		txt_title.setText(tvLiveBanner.productName);
		imageView.setContentDescription(tvLiveBanner.productName);
		if(tvLiveBanner.basePrice != null) {
			String basePrise = tvLiveBanner.basePrice;
			if(tvLiveBanner.exposePriceText != null){
				txt_base_price_expose.setText(tvLiveBanner.exposePriceText);
			}
			txt_base_price.setText(basePrise);
			txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			txt_base_price.setVisibility(View.VISIBLE);
			if("0".equals(tvLiveBanner.basePrice)){
				txt_base_price.setVisibility(View.GONE);
				txt_base_price_expose.setVisibility(View.GONE);

//				price_text_layout.setLayoutParams(
//						new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT));
			}else{
//				price_text_layout.setLayoutParams(
//						new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price_expose.setVisibility(View.VISIBLE);
			}
		}

		txt_base_price.setTextColor(Color.parseColor("#666666"));
		cardRantalText.setVisibility(View.GONE);
		badge[0].setImageResource(R.drawable.transparent);
		badge[1].setImageResource(R.drawable.transparent);
		badge[2].setImageResource(R.drawable.transparent);
		price_info_layout.setGravity(Gravity.LEFT);
		price_info_layout.setVisibility(View.VISIBLE);

//		tvLiveBanner.priceMarkUpType = "";
//		tvLiveBanner.priceMarkUpType = "GSPRICE";

//		tvLiveBanner.priceMarkUpType = "RATE";
//		tvLiveBanner.priceMarkUp = "10";
//		tvLiveBanner.isRental = "true";
//		tvLiveBanner.rentalText = "월 렌탈료";
//		tvLiveBanner.rentalEtcText = "상담 전용 상품입니다.";
//		tvLiveBanner.rentalPrice = "89,000원";
//		tvLiveBanner.salePrice = "";

		if (tvLiveBanner.rwImgList != null && !tvLiveBanner.rwImgList.isEmpty()){
			for(int i=0; i< tvLiveBanner.rwImgList.size() ; i++){
				ImageUtil.loadImage(context, tvLiveBanner.rwImgList.get(i),badge[i] , 0);
			}
		}

		txt_price.setVisibility(View.VISIBLE);
		txt_price_won.setVisibility(View.VISIBLE);

		//렌탈상품
		if (DisplayUtils.isTrue(tvLiveBanner.isRental)) {
			txt_base_price.setTextColor(Color.parseColor("#111111"));

			txt_price.setText(tvLiveBanner.rentalPrice);
			txt_price_won.setVisibility(View.GONE);
			txt_base_price.setText(tvLiveBanner.rentalText);

			badge[0].setImageResource(R.drawable.transparent);
			badge[1].setImageResource(R.drawable.transparent);
			badge[2].setImageResource(R.drawable.transparent);

			txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			txt_base_price_expose.setVisibility(View.GONE);
			txt_base_price.setVisibility(View.VISIBLE);


			cardRantalText.setText(tvLiveBanner.rentalEtcText);
			cardRantalText.setVisibility(View.VISIBLE);
			cardRantalText.setGravity(Gravity.RIGHT);
			//휴대폰
		} else if (DisplayUtils.isTrue(tvLiveBanner.isCellPhone)) {
			txt_base_price.setTextColor(Color.parseColor("#111111"));
			if (DisplayUtils.isValidString(tvLiveBanner.exposePriceText)) {
				txt_price_won.setText(tvLiveBanner.exposePriceText);
			}

			txt_price.setText(tvLiveBanner.salePrice);
			if(!"0".equals(tvLiveBanner.basePrice)){
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				txt_base_price_expose.setVisibility(View.VISIBLE);
			}

			cardRantalText.setVisibility(View.VISIBLE);
			cardRantalText.setGravity(Gravity.RIGHT);

			if(!TextUtils.isEmpty(tvLiveBanner.rentalText)){
				txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
				txt_base_price.setText(tvLiveBanner.rentalText);
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price_expose.setVisibility(View.GONE);
				cardRantalText.setText(tvLiveBanner.rentalEtcText);
			}
		}else {

			// 아니면 가격 보이게
			if (DisplayUtils.isValidNumberString(tvLiveBanner.salePrice)) {
				txt_price.setText(DisplayUtils.getFormattedNumber(tvLiveBanner.salePrice));

				if (DisplayUtils.isValidString(tvLiveBanner.exposePriceText)) {
					txt_price_won.setText(tvLiveBanner.exposePriceText);
				} else {
					txt_price_won.setText(R.string.won);
				}

			} else {
				// 가격이 숫자가 아니거나 0이면 아무 표시하지 않음
				txt_price.setVisibility(View.INVISIBLE);
				txt_price_won.setVisibility(View.GONE);
			}
		}


		if("LIVEPRICE".equalsIgnoreCase(tvLiveBanner.priceMarkUpType)){
			price_info.setText(tvLiveBanner.priceMarkUp.replaceAll("/n", "\n"));
			price_info.setGravity(Gravity.CENTER_HORIZONTAL);
			price_info.setPadding(0,context.getResources().getDimensionPixelSize(R.dimen.tv_live_gs_live_top_space),0,0);
			price_info.setBackgroundResource(R.drawable.tag_price_android);

			price_layout.setPadding(context.getResources().getDimensionPixelSize(R.dimen.tv_live_price_left_space),0,0,0);
		}else if("RATE".equalsIgnoreCase(tvLiveBanner.priceMarkUpType)){
			AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(28, true);
			AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(18, true);

			SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
			priceStringBuilder.append(tvLiveBanner.priceMarkUp).append(context.getString(R.string.percent));
			priceStringBuilder.setSpan(bigSizeSpan, 0, tvLiveBanner.priceMarkUp.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			priceStringBuilder.setSpan(smalSizeSpan, tvLiveBanner.priceMarkUp.length(), tvLiveBanner.priceMarkUp.length() + 1,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			priceStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ed1f60")), 0, priceStringBuilder.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			price_info.setGravity(Gravity.CENTER);
			price_info.setPadding(0, 0,0,0);
			price_info.setBackgroundResource(android.R.color.transparent);
			price_info.setText(priceStringBuilder);

			price_layout.setPadding(context.getResources().getDimensionPixelSize(R.dimen.tv_gs_price_left_space),0,0,0);
		}else if("GSPRICE".equalsIgnoreCase(tvLiveBanner.priceMarkUpType)){
			AbsoluteSizeSpan gsSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_live_gs_TextSize), false);
			SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();

			priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_GS).append(Keys.DISCOUNT_RATE_TEXT_PRICE);
			priceStringBuilder.setSpan(gsSizeSpan, 0, priceStringBuilder.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			priceStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ed1f60")), 0, priceStringBuilder.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			price_info.setPadding(0, 0,0,0);
			if(txt_base_price.getVisibility() == View.GONE){
				price_info.setGravity(Gravity.CENTER);
//				price_info.setPadding(0,0,0,0);
				price_info_layout.setGravity(Gravity.CENTER);
			}else{
				price_info.setGravity(Gravity.CENTER);
//				price_info.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.tv_live_gs_Text_top_space),0,0);
			}


			price_info.setBackgroundResource(android.R.color.transparent);
			price_info.setText(priceStringBuilder);
			price_layout.setPadding(context.getResources().getDimensionPixelSize(R.dimen.tv_gs_price_left_space),0,0,0);

		}else{
			//가격이 없으면
			if("".equals(tvLiveBanner.salePrice) && !"".equals(tvLiveBanner.rentalEtcText)){
				price_info_layout.setVisibility(View.GONE);
				badge[0].setImageResource(R.drawable.transparent);
				badge[1].setImageResource(R.drawable.transparent);
				badge[2].setImageResource(R.drawable.transparent);
				cardRantalText.setText(tvLiveBanner.rentalEtcText);
				cardRantalText.setVisibility(View.VISIBLE);
				cardRantalText.setGravity(Gravity.CENTER);
			}
		}

		// 편성표 보기
		scheduleBtn.setTag(DisplayUtils.getFullUrl(tvLiveBanner.broadScheduleLinkUrl));
		scheduleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				WebUtils.goWeb(context, v.getTag().toString(), ((Activity) context).getIntent());
				//MSLEE 테스트
				//편성표 버튼이 링크
				//WebUtils.goWeb(context, "http://sm21.gsshop.com/main/shortbang/view", ((Activity) context).getIntent());

				//GTM 클릭이벤트 전달
				String action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
						sectionName, GTMEnum.GTM_ACTION_LIVE_SCHEDULE_TAIL);
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY,
						action,
						v.getTag().toString());
			}
		});

		// 라이브톡 버튼 노출여부
		if("Y".equalsIgnoreCase(tvLiveBanner.liveTalkYn)){
			btn_live_talk.setVisibility(View.VISIBLE);
			live_talk_line.setVisibility(View.VISIBLE);
			live_talk_text.setText(tvLiveBanner.liveTalkText );
			btn_live_talk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, tvLiveBanner.liveTalkUrl, ((Activity) context).getIntent());
				}
			});
		}else{
			btn_live_talk.setVisibility(View.GONE);
			live_talk_line.setVisibility(View.GONE);
		}

		// 편성표
		if(tvLiveBanner.brodScheduleYn != null && "Y".equalsIgnoreCase(tvLiveBanner.brodScheduleYn)){
			scheduleBtn.setText(tvLiveBanner.brodScheduleText);
			scheduleBtn.setVisibility(View.VISIBLE);
		} else {
			scheduleBtn.setVisibility(View.GONE);
		}

		// 바로구매
		if(tvLiveBanner.rightNowBuyYn != null && "Y".equalsIgnoreCase(tvLiveBanner.rightNowBuyYn)){
			// 적립금 text
			btn_pay.setVisibility(View.VISIBLE);
			btn_pay.setTag(DisplayUtils.getFullUrl(tvLiveBanner.rightNowBuyUrl));
			btn_pay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString());
					EventBus.getDefault().post(new Events.FlexibleEvent.DirectBuyEvent(position, mInfo));
				}
			});
		} else {
			btn_pay.setVisibility(View.GONE);
		}


		if(tvLiveBanner != null && tvLiveBanner.btnInfo3 != null && !StringUtil.isEmpty(tvLiveBanner.btnInfo3.text) && !StringUtil.isEmpty(tvLiveBanner.btnInfo3.linkUrl)){
			// 적립금 text
			btn_pay.setVisibility(View.VISIBLE);
			btn_pay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveBanner.btnInfo3.linkUrl));
//					WebUtils.goWeb(context, DisplayUtils.getFullUrl("external://directOrd?http://sm21.gsshop.com/prd/directOrd/16400026"));
					// 테스트 url "external://directOrd?http://sm21.gsshop.com/prd/directOrd/19856463"   렌탈 : 16400026

					// 바로구매 이벤트 전달
					// MC_오늘추천_DirectOrd
					String action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER_APP,
							sectionName, GTMEnum.GTM_ACTION_DIRECT_ORD_TAIL);

					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY,
							action,
							v.getTag().toString());


					EventBus.getDefault().post(new Events.FlexibleEvent.DirectBuyEvent(position, mInfo));
				}
			});
			btn_pay.setText(tvLiveBanner.btnInfo3.text);
		}

		if(btn_live_talk.getVisibility() == View.GONE
				&& scheduleBtn.getVisibility() == View.GONE && btn_pay.getVisibility() == View.GONE ){
			btn_layout.setVisibility(View.GONE);
		}else{
			btn_layout.setVisibility(View.VISIBLE);
		}

		//에니메이션
//		tvLiveBanner.salesInfo.saleRateExposeYn = "Y";
//		tvLiveBanner.salesInfo.ordQty = "2211585";
		counter.setVisibility(View.GONE);
		if(tvLiveBanner.salesInfo != null && tvLiveBanner.salesInfo.saleRateExposeYn != null &&
				"Y".equalsIgnoreCase(tvLiveBanner.salesInfo.saleRateExposeYn) && !isStartAnimation){
			isStartAnimation = true;
			numberCounter = new NumberCounter((Activity) context, number_counter_layout);
			boolean isWithNumber = numberCounter.withNumber(Long.parseLong(getOnlyNumberString(tvLiveBanner.salesInfo.ordQty)));
			startAnimation = AnimationUtils.loadAnimation(context, R.anim.count_in_alpha);
			startAnimation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {

					numberCounter.start();

					closeAnimation = AnimationUtils.loadAnimation(context, R.anim.count_out_alpha);

					closeAnimation.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							counter.setVisibility(View.GONE);

							if(tvLiveBanner.isTvShoping && tvLiveBanner.livePlay != null
									&& ((!TextUtils.isEmpty(tvLiveBanner.livePlay.livePlayUrl) && "Y".equalsIgnoreCase(tvLiveBanner.livePlay.livePlayYN))
										|| (!TextUtils.isEmpty(tvLiveBanner.livePlay.videoid) && tvLiveBanner.livePlay.videoid.length() > 4))){
								play.setVisibility(View.VISIBLE);
								play.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										//플레이 버튼 클릭시 와이즈로그 전송
										if (tvLiveBanner.currentSubTabMenu == TvShopFragment.SubTabMenu.live) {
											((HomeActivity) context).setWiseLog(ServerUrls.WEB.TVSHOP_LIVE_PLAY);
										} else if (tvLiveBanner.currentSubTabMenu == TvShopFragment.SubTabMenu.data) {
											((HomeActivity) context).setWiseLog(ServerUrls.WEB.TVSHOP_DATA_PLAY);
										}
										confirmNetworkBilling((Activity)context, tvLiveBanner.currentSubTabMenu, tvLiveBanner.linkUrl);
									}
								});
							}else{
								play.setVisibility(View.GONE);
							}
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							counter.startAnimation(closeAnimation);
						}
					}, 1500);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});

			if(isWithNumber) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!isUpdate) {
							counter.startAnimation(startAnimation);
							counter.setVisibility(View.VISIBLE);
						}
					}
				}, 100);
			}
		}else{
			counter.setVisibility(View.GONE);
			if(tvLiveBanner.isTvShoping && tvLiveBanner.livePlay != null
					&& ((!TextUtils.isEmpty(tvLiveBanner.livePlay.livePlayUrl) && "Y".equalsIgnoreCase(tvLiveBanner.livePlay.livePlayYN))
					|| (!TextUtils.isEmpty(tvLiveBanner.livePlay.videoid) && tvLiveBanner.livePlay.videoid.length() > 4))){
				play.setVisibility(View.VISIBLE);
				play.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//플레이 버튼 클릭시 와이즈로그 전송
						if (tvLiveBanner.currentSubTabMenu == TvShopFragment.SubTabMenu.live) {
							((HomeActivity) context).setWiseLog(ServerUrls.WEB.TVSHOP_LIVE_PLAY);
						} else if (tvLiveBanner.currentSubTabMenu == TvShopFragment.SubTabMenu.data) {
							((HomeActivity) context).setWiseLog(ServerUrls.WEB.TVSHOP_DATA_PLAY);
						}
						confirmNetworkBilling((Activity)context, tvLiveBanner.currentSubTabMenu, tvLiveBanner.linkUrl);
					}
				});
			}else{
				play.setVisibility(View.GONE);
			}
		}

	}

	/**
	 * 문자열에서 숫자만 추출
	 * @param str String
	 * @return String
	 *
	 */
	public String getOnlyNumberString(String str){
		if(str == null) return str;

		StringBuilder sb = new StringBuilder();
		int length = str.length();
		for(int i = 0 ; i < length ; i++){
			char curChar = str.charAt(i);
			if(Character.isDigit(curChar)) sb.append(curChar);
		}

		return sb.toString();
	}

	/**
	 * 전화상담이 필요한 상품인지 체크. 현재는 핸드폰이나 렌탈 상품일 때 해당.
	 * @param tvLiveBanner tvLiveBanner
	 * @return boolean
	 */
	private boolean isNeededConsultationCall(TvLiveBanner tvLiveBanner) {
		return DisplayUtils.isTrue(tvLiveBanner.isCellPhone)
				|| DisplayUtils.isTrue(tvLiveBanner.isRental);
	}

	/**
	 * TV생방송 view가 이미 존재하는 상태에서 데이타만 update하기 위한 controller
	 *
	 */
	private class GetTvLiveContentUpdateController extends BaseAsyncController<TvLiveContent> {
		@Inject
		private TvLiveContentAction tvLiveContentAction;
		private boolean dialogFlag = false;
		@Inject
		private RestClient restClient;
		private String sectionCode;

		private final String action;
		private final String label;
		private final String sectionName;

		protected GetTvLiveContentUpdateController(Context activityContext,String action,String label,String sectionName) {
			super(activityContext);
			this.action = action;
			this.label = label;
			this.sectionName = sectionName;
			isUpdate = true;
		}

		/**
		 *
		 * @param params
		 * @throws Exception
		 */
		@Override
		protected void onPrepare(Object... params) throws Exception {
			//다이얼로그 호출
			if (dialogFlag) {
				if (dialog != null) {
					dialog.dismiss();
					dialog.setCancelable(true);
					dialog.show();
				}
			}

			dialogFlag = (Boolean) params[0];
			sectionCode = (String) params[1];
		}

		@Override
		protected TvLiveContent process() throws Exception {
			// sectionCode가 "#"으로 시작하면 "#" 삭제
			String result = sectionCode;
			if (sectionCode.startsWith(SECTION_CODE_PREFIX)) {
				result = sectionCode.replaceFirst(SECTION_CODE_PREFIX, "");
			}

			return (TvLiveContent)DataUtil.getData(context, restClient, TvLiveContent.class, false,
					true,tvLiveUrl + "?sectionCode=" + result);

		}

		@Override
		protected void onSuccess(TvLiveContent tvLiveTemp) throws Exception {
			if (tvLiveTemp != null) {
				// tv 정보가 유효한지 체크하고
				if(tvLiveTemp.appTvLiveBanner == null){
					tvLiveTemp.appTvLiveBanner = tvLiveTemp.tvLiveBanner;
				}
				if (verifyTvInfo(tvLiveTemp.appTvLiveBanner)) {
					if (mPrevBroadEndTime != null) {
						// 이전 데이타가 있고, 받아온 데이타와 다를 때에만 view update.
						if (!mPrevBroadEndTime.equals(tvLiveTemp.appTvLiveBanner.broadCloseTime)) {
							// 방송 종료시간이 같으면 동일한 데이타로 간주
							mPrevBroadEndTime = tvLiveTemp.appTvLiveBanner.broadCloseTime;

							String tempAction = GTMEnum.GTM_NONE;
							String tempLabel = GTMEnum.GTM_NONE;

							if( action !=  null )
								tempAction = action;
							if( label !=  null )
								tempLabel = label;

							//context가 유효한 경우만 화면 갱신 (메인 상단아이콘 다수 클릭 후 생방송 종료시 context finishing 발생)
							if (!((Activity)context).isFinishing()) {
								bindViewHolder(context, 0, tvLiveTemp.appTvLiveBanner, sectionCode, tempAction, tempLabel, sectionName);
								startTvLiveTimer();
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										//라이브톡 텍스트롤링 영역 갱신
										updateLiveTalk();
									}
								}, 1000);
							}
						}
					}
				}
				isUpdate = false;

				//Tv방송리스트 배너도 새로고침하기위해 아이템을 업데이트하고 이벤트를 보낸다.
				int i = 0;
				for(ShopInfo.ShopItem contents : mInfo.contents){
					if(contents.type == ViewHolderType.BANNER_TYPE_TV_ITEM){
						contents.sectionContent.subProductList = tvLiveTemp.tvLiveBannerList;
						EventBus.getDefault().post(new Events.FlexibleEvent.UpdateRowEvent(i));
						break;
					}
					i++;
				}

			}
		}
	}

	/**
	 * 라이브톡 텍스트롤링 영역 갱신 (수행로직은 TvLiveBannerTVLiveViewHolder에 있음)
	 */
	protected void updateLiveTalk() {}

	/**
	 * tv방송 정보가 유효한 지 체크해서  tv live view visible/gone
	 * broadType과 prdUrl 정도만 체크함
	 * @param tvLiveBanner tvLiveBanner
	 * @return boolean
	 */
	private boolean verifyTvInfo(TvLiveBanner tvLiveBanner) {
		if (DisplayUtils.isValidString(tvLiveBanner.broadType)
				&& DisplayUtils.isValidString(tvLiveBanner.linkUrl)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 3G 과금 안내 팝업
	 * @param activity activity
	 * @param subMenu subMenu
	 * @param productUrl productUrl
	 */
	protected void confirmNetworkBilling(final Activity activity, final TvShopFragment.SubTabMenu subMenu, final String productUrl) {

		NetworkUtils.confirmNetworkBillingAndShowPopup(activity, new NetworkUtils.OnConfirmNetworkListener() {
			@Override
			public void isConfirmed(boolean isConfirmed) {
				if (isConfirmed) {
					root_view.setVisibility(View.GONE);
					EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(true, 0));
				}
			}

			@Override
			public void inCanceled() {}
		});
	}

	protected VideoParameters buildVideoParam(String productUrl, String videoId, String livePlayUrl) {
		VideoParameters param = new VideoParameters();

		param.videoId = videoId;
		param.videoUrl = livePlayUrl;
		param.productInfoUrl = productUrl;
		param.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

		return param;
	}

	protected void playVideo(Activity activity, VideoParameters param) {
		Intent intent = new Intent(Keys.ACTION.LIVE_VIDEO_PLAYER);

		// param != null 방어로직 추가 - 비즈니스 로직에 영향이 없도록 startActivityForResult 유지하고 param 만 처리 10/05
		// if( param != null && Keys.INTENT.BASE_PLAY.equals(param.playType)) 위 로직에 있으나 param null 있을수 있다 10/05
		if(param != null) {
			intent.putExtra(Keys.INTENT.VIDEO_ID, param.videoId);
			intent.putExtra(Keys.INTENT.VIDEO_URL, param.videoUrl);
			intent.putExtra(Keys.INTENT.VIDEO_PRD_URL, param.productInfoUrl);
			intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, param.orientation);
		}
		intent.putExtra(Keys.INTENT.FOR_RESULT, true);

		activity.startActivityForResult(intent, Keys.REQCODE.VIDEO);
	}

	/**
	 * 생방송 남은시간 노출용 타이머를 시작한다.
	 */
	protected void startTvLiveTimer() {
		layoutTvLiveProgress.updateTvLiveTime(mInfo.tvLiveBanner.broadType, mInfo.tvLiveBanner.broadStartTime,
				mInfo.tvLiveBanner.broadCloseTime);
	}

	/**
	 * 생방송 남은시간 노출용 타이머를 정지한다.
	 */
	protected  void stopTvLiveTimer() {
		layoutTvLiveProgress.stopTimer();
	}

	/**
	 * 생방송 남은시간 노출용 타이머를 시작/정지 시킨다.
	 * (BestDealShopFragment, TvShopFragment onStart/onStop에서 호출됨)
	 *
	 * @param event TvLiveTimerEvent
	 */
	public void onEvent(Events.FlexibleEvent.TvLiveTimerEvent event) {
		if (event.start) {
			//생방송 남은시간 표시용 타이머 시작
			startTvLiveTimer();
		} else {
			//생방송 남은시간 표시용 타이머 정지

			stopTvLiveTimer();
		}
	}

	/**
	 * 뷰가 화면에 노출될때 발생
	 */
	@Override
	public void onViewAttachedToWindow() {
		EventBus.getDefault().registerSticky(this);
		//생방송 남은시간 표시용 타이머 시작
		startTvLiveTimer();
	}

	/**
	 * 뷰가 화면에서 사라질때 발생
	 */
	@Override
	public void onViewDetachedFromWindow() {
		EventBus.getDefault().unregister(this);
		//생방송 남은시간 표시용 타이머 정지
		stopTvLiveTimer();
	}
}
