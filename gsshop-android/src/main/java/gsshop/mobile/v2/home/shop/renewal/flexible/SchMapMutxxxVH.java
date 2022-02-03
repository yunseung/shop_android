/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptimize.Apptimize;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.shop.schedule.model.DirectOrdInfo;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.intro.ApptimizeCommand;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_SUBPRD_COLLAPSE;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_SUBPRD_EXPAND;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.makeWiselogUrl;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 편성표 주상품 V2
 */
public class SchMapMutxxxVH extends RenewalPriceInfoBottomViewHolder {

    /**
     * 루트뷰
     */
    private View lay_root;

	/**
	 * 상품이미지 + 가격 영역
	 */
	private View lay_img_price;

	/**
	 * 타이틀 영역 (방송시간, pgm)
	 */
	private View lay_title;

    /**
     * 생방송 여부
     */
    protected ImageView img_brd_type;

    /**
     * 방송시간
     */
    private TextView txt_broad_time;

    /**
     * pgm 레이아웃
     */
    private View lay_pgm;

    /**
     * pgm 문구
     */
    private TextView txt_pgm;

	/**
	 * 공영방송 제목
	 */
	private TextView txt_public_title;

    /**
     * 혜택정보
     */
    private TextView txt_benefit_title;

	/**
	 * 상품이미지
	 */
	private ImageView img_main_sumnail;

    /**
     * 남은시간 표시 (생방인 경우만 노출)
     */
    protected View lay_cv_remain_time;
    protected RenewalBroadTimeLayout cv_remain_time;
	protected ImageView img_cv_play;

	//부상품 토글 관련
	private final LinearLayout product_toggle_layout;
	private final TextView product_toggle_text;
	private final LinearLayout product_toggle_count_layout;
	private final TextView product_toggle_count;
	private final ImageView product_toggle_icon;
	private final LinearLayout sub_prd_list;

	private final View top_live_talk;

    private String moreText;
    private String closeText;
    private int moreIcon;
    private int closeIcon;

	protected SchedulePrd data;

	private String mLastShownImageSubData;

	// #해시태그 ex)#네일아트 #뷰티
	private TextView txt_theme;

	/**
	 * 캐시할 이미지 주소
	 */
	private String mImgForCache;

	/**
	 * @param itemView
	 */
	public SchMapMutxxxVH(View itemView) {
		super(itemView);

		broadComponentType = BroadComponentType.schedule;

        lay_root = itemView.findViewById(R.id.lay_root);

        lay_title = itemView.findViewById(R.id.lay_title);
		lay_img_price = itemView.findViewById(R.id.lay_img_price);
        img_brd_type = itemView.findViewById(R.id.img_brd_type);
        txt_broad_time = itemView.findViewById(R.id.txt_broad_time);
        lay_pgm = itemView.findViewById(R.id.lay_pgm);
        txt_pgm = itemView.findViewById(R.id.txt_pgm);
		txt_public_title = itemView.findViewById(R.id.txt_public_title);
		txt_benefit_title = itemView.findViewById(R.id.txt_benefit_title);
		txt_theme = itemView.findViewById(R.id.txt_theme);

        // 남은시간
        lay_cv_remain_time = itemView.findViewById(R.id.lay_cv_remain_time);
        cv_remain_time = itemView.findViewById(R.id.cv_remain_time);
		img_cv_play = itemView.findViewById(R.id.img_cv_play);

		//상품 이미지
		img_main_sumnail = itemView.findViewById(R.id.img_main_sumnail);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), img_main_sumnail);

		//부상품
		product_toggle_layout = itemView.findViewById(R.id.product_toggle_layout);
		product_toggle_text = itemView.findViewById(R.id.product_toggle_text);
		product_toggle_icon = itemView.findViewById(R.id.product_toggle_icon);
		sub_prd_list = itemView.findViewById(R.id.sub_prd_list);
		product_toggle_count_layout = itemView.findViewById(R.id.product_toggle_count_layout);
		product_toggle_count = itemView.findViewById(R.id.product_toggle_count);
		top_live_talk = itemView.findViewById(R.id.top_live_talk);
        moreIcon = R.drawable.ic_expend_more;
        closeIcon = R.drawable.ic_expend_less;
	}

	@Override
	public void onBindViewHolder(final Context context, final int position, final SchedulePrd data) {
		this.context = context;
		this.data = data;

		//생방송인 경우만 노출하는 뷰 숨김
        img_brd_type.setVisibility(View.GONE);
		lay_cv_remain_time.setVisibility(View.GONE);

        //타이틀 영역 (방송시간, PGM Text)
        lay_pgm.setVisibility(View.GONE);
		lay_title.setVisibility(View.GONE);

        if (isNotEmpty(data.startTime)) {
        	//타이틀 영역은 startTime 값이 존재하는 경우만 노출
			txt_broad_time.setText(data.startTime);
			lay_title.setVisibility(View.VISIBLE);
			//pgm 영역은 startTime 값이 존재하는 경우만 체크해서 노출
			if (isNotEmpty(data.specialPgmInfo) && isNotEmpty(data.specialPgmInfo.text)) {
				txt_pgm.setText(data.specialPgmInfo.text);
				lay_pgm.setVisibility(View.VISIBLE);
				lay_pgm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						WebUtils.goWeb(context, data.specialPgmInfo.linkUrl);
					}
				});
			}
		}

		// 방송 스크린샷 이미지
		if (!TextUtils.isEmpty(data.product.mainPrdImgUrl)) {
			mImgForCache = data.product.mainPrdImgUrl;
			ImageUtil.loadImageResizeToHeightWithListener(context.getApplicationContext(), mRequestListener, data.product.mainPrdImgUrl.trim(), img_main_sumnail, R.drawable.noimg_tv);
			mLastShownImageSubData = data.product.subPrdImgUrl.trim();
		} else {
			mImgForCache = data.product.subPrdImgUrl;
			ImageUtil.loadImageResizeToHeightWithListener(context.getApplicationContext(), mRequestListener, data.product.subPrdImgUrl.trim(), img_main_sumnail, R.drawable.noimage_375_188);
		}


		// 상세화면 이동
		lay_root.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Keys.INTENT.IMAGE_URL, isNotEmpty(mImgForCache) ? mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO) : "");
				intent.putExtra(Keys.INTENT.HAS_VOD, data.product.hasVod);
				WebUtils.goWeb(context, data.product.linkUrl, intent);

				// TV편성표 AB테스트 주상품 효율
				// AB대상이면서 O타입, AB대상이면서 A타입만 해당되도록
				if(ApptimizeCommand.ABINFO_VALUE.contains(ApptimizeExpManager.SCHEDULE)){
					//앰플리튜드를 위한 코드
					try {
						JSONObject eventProperties = new JSONObject();
						eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.AMP_CLICK_SCHEDULE_PRD);

						if(data.product != null) {
							eventProperties.put(AMPEnum.AMP_PRD_CODE, data.product.prdId);
							eventProperties.put(AMPEnum.AMP_PRD_NAME, data.product.prdName);
							eventProperties.put(AMPEnum.AMP_AB_DETAIL_TYPE, ApptimizeExpManager.MAIN_PRD);
							eventProperties.put(AMPEnum.AMP_AB_INFO, ApptimizeCommand.ABINFO_VALUE);
						}

						AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_SCHEDULE_PRD,eventProperties);
					} catch (JSONException exception){

					}
					//앱티마이즈를 위한 코드
					Apptimize.track(AMPEnum.AMP_CLICK_SCHEDULE_PRD);
				}
			}
		});
		img_main_sumnail.setContentDescription(data.product.exposPrdName);

		//hanna 공영상품 노출 확인 필요
		//공영방송
		txt_public_title.setVisibility(View.GONE);
		lay_img_price.setVisibility(View.VISIBLE);
		if ("Y".equals(data.publicBroadYn)) {
			if ("Y".equals(data.pgmLiveYn)) {
				//생방송이면 상품이미지 + 상품명 노출 (가격, 버튼 노출안함)
			} else {
				//생방송 아니면 상품명만 노출
				lay_img_price.setVisibility(View.GONE);
				txt_public_title.setText(data.product.exposPrdName);
				txt_public_title.setVisibility(View.VISIBLE);
			}
		}

        //혜택정보
        String promotion = "";
        txt_benefit_title.setVisibility(View.GONE);
        if (isNotEmpty(data.liveBenefitLText)) {
            promotion += data.liveBenefitLText;
        }
        if (isNotEmpty(data.liveBenefitRText)) {
            promotion = promotion + " " + data.liveBenefitRText;
        }

        //#테마 #테마 #테마 세팅
        if(DisplayUtils.isValidString(data.product.subName)){
			txt_theme.setText(data.product.subName);
			txt_theme.setVisibility(View.VISIBLE);
		}else{
			txt_theme.setVisibility(View.GONE);
		}

		//가격표시용 공통모듈에 맞게 데이타 변경
		TvLiveBanner tvLiveBanner = new TvLiveBanner();

        //상품 정보
		tvLiveBanner.rPrdId = data.product.prdId;
		tvLiveBanner.rProductName = data.product.exposPrdName;
		tvLiveBanner.rLinkUrl = data.product.linkUrl;

		//가격 정보
		tvLiveBanner.rSalePrice = data.product.broadPrice;
		tvLiveBanner.rBasePrice = data.product.salePrice;
		tvLiveBanner.rExposePriceText = data.product.exposePriceText;
		tvLiveBanner.rDiscountRate = data.product.priceMarkUp;

		//렌탈관련
		//방송 구조체에는 있습니다 렌탈 프라이스가 렌탈 상품 관
		tvLiveBanner.rProductType = "Y".equals(data.product.rentalYn) ? "R" : "P";
		tvLiveBanner.rRentalText = data.product.rentalText;
		tvLiveBanner.rentalPrice = data.product.rentalPrice;

		//바로구매 여부 확인
		//편성표에서 공통
		if (isNotEmpty(data.product.directOrdInfo)) {
			tvLiveBanner.rDirectOrdInfo = new DirectOrdInfo();
			tvLiveBanner.rDirectOrdInfo.text = data.product.directOrdInfo.text;
			tvLiveBanner.rDirectOrdInfo.linkUrl = data.product.directOrdInfo.linkUrl;
		}

		//라이브톡 여부 확인
		//편성표에서 공통
		if (isNotEmpty(data.liveTalkInfo)) {
			tvLiveBanner.rLiveTalkYn = "Y";
			tvLiveBanner.rLiveTalkText = data.liveTalkInfo.text;
			tvLiveBanner.rLiveTalkUrl = data.liveTalkInfo.linkUrl;
		}
		
		// RenewalPriceInfoBottomViewHolder 에 있는 내용 복붙
		if (tvLiveBanner.rLiveTalkYn != null && "Y".equals(tvLiveBanner.rLiveTalkYn) ) {
			if (isNotEmpty(top_live_talk)) {

				if(broadComponentType == BroadComponentType.live){
					top_live_talk.setVisibility(View.GONE);
					if (isNotEmpty(promotion)) {
						txt_benefit_title.setText(promotion);
						txt_benefit_title.setVisibility(View.VISIBLE);
					}
				}else{
					top_live_talk.setVisibility(View.VISIBLE);
					txt_benefit_title.setVisibility(View.GONE);
				}

				top_live_talk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						WebUtils.goWeb(context, tvLiveBanner.rLiveTalkUrl, ((Activity) context).getIntent());

						//라이브톡 버튼 효율 코드
						if (broadComponentType == BroadComponentType.live) {
							//생방송
							((HomeActivity) context).setWiseLogHttpClient(ServerUrls.WEB.RN_BESTSHOP_LIVE_TALK);
						} else if (broadComponentType == BroadComponentType.schedule) {
							//편성표
							((HomeActivity) context).setWiseLogHttpClient(ServerUrls.WEB.RN_SCH_LIVE_TALK);
						}
					}
				});
			}
		} else {
			if (isNotEmpty(top_live_talk)) {
				top_live_talk.setVisibility(View.GONE);
				if (isNotEmpty(promotion)) {
					txt_benefit_title.setText(promotion);
					txt_benefit_title.setVisibility(View.VISIBLE);
				}
			}
		}
		
		//방송알림 여부 확인
		//편성표에서 공통
		tvLiveBanner.rBroadAlarmDoneYn = data.product.broadAlarmDoneYn;     //방송 알림 여부
		tvLiveBanner.rExposPrdName = data.product.exposPrdName;			    //방송 알림에 필요

		//방송 구조체에는 있습니다 렌탈 프라이스가 렌탈 상품 관련
		tvLiveBanner.rRentalText = data.product.rentalText;		// 이부분이 "월 렌탈료" 그런데 가격이 엉망이면?? rValueText / rentalText  월 렌탈료가 들어 있음
		tvLiveBanner.rRentalPrice = data.product.rentalPrice;

		//편성표는 보험 어찌 처리한대???
		tvLiveBanner.rProductType = "Y".equals(data.product.insuYn) ? "I" : "P";
		tvLiveBanner.rProductType = "Y".equals(data.product.rentalYn) ? "R" : tvLiveBanner.rProductType;

		//혜택 관련
		tvLiveBanner.rAllBenefit = data.product.allBenefit;
		tvLiveBanner.rSource = data.product.source;

		//솔드 아웃 관련 방송중 구매가능관련
		//생방송에 현재는 없다. 하지만 편성표에는 있다.
		tvLiveBanner.rImageLayerFlag = data.product.imageLayerFlag;

		//상품평 관련
		tvLiveBanner.rAddTextLeft = data.product.addTextLeft;
		tvLiveBanner.rAddTextRight = data.product.addTextRight;

		//브렌드 관련
		tvLiveBanner.rBrandNm = data.product.brandNm;

		// 딜 여부 편성표는 무조건 상품
		tvLiveBanner.deal = "false";

		//LIVE or VOD 여부
		tvLiveBanner.rPgmLiveYn = data.pgmLiveYn;

		super.bindViewHolder(tvLiveBanner, position,null);

		//부상품 expand/collapse
		product_toggle_layout.setVisibility(View.GONE);
		sub_prd_list.setVisibility(View.GONE);
		if (data.product.subProductList != null && data.product.subProductList.size() > 0) {
			moreText = context.getString(R.string.related_product_space);
			closeText = context.getString(R.string.related_product_close_space);
			product_toggle_count.setText("" + data.product.subProductList.size() + context.getString(R.string.product_description_sale_tail_text));

			sub_prd_list.removeAllViews();
			product_toggle_layout.setVisibility(View.VISIBLE);

			for (int i=0; i<data.product.subProductList.size(); i++) {
				View itemView = LayoutInflater.from(context).inflate(R.layout.renewal_recycler_item_tv_schedule_main_sub, null);
				RenewalTLineSubViewHolder subPrd = new RenewalTLineSubViewHolder(itemView);
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

	}

	private RequestListener<String, GlideDrawable> mRequestListener = new RequestListener<String, GlideDrawable>() {
		@Override
		public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
			// 여기까지 왔다는건 mainPrdImgUrl 호출하다가 실패했다는 것이다. mLastShownImageSubData 는 mainPrdImgUrl 을 셋팅할때 무조건 전역변수로 저장해둔다.
			img_main_sumnail.post(new Runnable() {
				@Override
				public void run() {
					ImageUtil.loadImageResizeToHeight(context.getApplicationContext(), mLastShownImageSubData, img_main_sumnail, R.drawable.noimg_tv);
				}
			});
			return false;
		}

		@Override
		public boolean onResourceReady(GlideDrawable resouorce, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
			// 이미지 로드 완료됬을 때 처리
			return false;
		}
	};

	/**
	 * 부상품 펼친상태로 UI를 변경한다.
	 */
	private void setExpandUI() {
        product_toggle_text.setText(closeText);
        product_toggle_icon.setBackgroundResource(closeIcon);
        sub_prd_list.setVisibility(View.VISIBLE);
		product_toggle_count_layout.setVisibility(View.GONE);
    }

	/**
	 * 부상품 닫힌상태로 UI를 변경한다.
	 */
    private void setCollapseUI() {
        product_toggle_text.setText(moreText);
        product_toggle_icon.setBackgroundResource(moreIcon);
        sub_prd_list.setVisibility(View.GONE);
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

	/**
	 * 이미지가 화면상에 노출된 경우에 한해 이미지 캐시작업을 수행한다.
	 *
	 * @param event ImageCacheStartEvent
	 */
	public void onEvent(Events.ImageCacheStartEvent event) {
		if (isNotEmpty(navigationId) && !navigationId.equals(event.naviId)) {
			//내가 속한 매장에 대한 이벤트가 아니면 스킵
			return;
		}
		if (DisplayUtils.isVisible(img_main_sumnail)) {
			if (isNotEmpty(mImgForCache) && mImgForCache.contains(IMG_CACHE_RPL_FROM)) {
				String imgUrl = mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO);
				Glide.with(context).load(trim(imgUrl)).downloadOnly(IMG_CACHE_WIDTH, IMG_CACHE_HEIGHT);
			}
		}
	}

	@Override
	public void onViewAttachedToWindow() {
		super.onViewAttachedToWindow();
		try {
			EventBus.getDefault().register(this);
		}catch(Exception e){
			Ln.e(e);
		}
	}

	@Override
	public void onViewDetachedFromWindow() {
		super.onViewDetachedFromWindow();
		EventBus.getDefault().unregister(this);
	}
}
