/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.main.TvLiveContentAction;
import gsshop.mobile.v2.home.main.TvLiveDealBanner;
import gsshop.mobile.v2.home.main.TvLiveDealContent;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.tvshoping.TvLiveTimeLayout;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.GlobalTimer;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * 날방 live banner.
 *
 */
@SuppressLint("NewApi")
public class NalbangBannerLiveViewHolder extends BaseViewHolder {

	// 날방생방송 API 호출 딜레이 타임 (DB정보 늦게 갱신되는 경우 대응)
	private static final long LIVE_API_CALL_DELAY_MILLIS = 5000;

	private static final long DELAY_MILLIS = 1000; // remainTimer interval
	private long noticeStartTime = 0L;  //방송시작 3시간전 시간
	private long broadStartTime = 0L;   //방송시작시간
	private long broadCloseTime = 0L;  //방송종료시간
	private static final int NOTICE_BEFORE_TIME = 3;	//생방송 남은시간 표시는 3시간 전부터
	private static final String BROAD_TIME_FORMAT = "yyyyMMddHHmmss";

	//생방송 상단영역
	private LinearLayout layLiveOnairTop;
	private LinearLayout lay3hAfterTop;

	//생방송 컨텐츠영역 우하단 재생시간
	private TextView txtPlayTime;

	//생방송 하단영역
	private LinearLayout layBottomPrd;
	private LinearLayout layBottomNaltalk;

	//생방송 남은시간
	public TvLiveTimeLayout layoutTvLiveProgress;

	//방송시작 3시간 전부터 생방송 시작시간까지 남은시간 표시
	private TextView txtRemainTime;
	//생방송 상단영역 프로모션 텍스트
	private TextView txtLiveOnairTop;

	// 방송 스크린샷 이미지
	protected final ImageView imageView;

	protected final View layoutTvGoods;

	protected ShopInfo mInfo;

	//하단 상품정보
	protected final RelativeLayout layBottomPrdVp;
	protected final RelativeLayout layBottomPrdVnp;
	protected final TextView txtTitle;
	protected final TextView txtTitleVnp;
	protected final TextView txt_base_price;
	protected final TextView txt_base_price_unit;
	protected final TextView txtPrice;
	protected final TextView txt_price_unit;
	protected final TextView txt_badge_per;
	protected final TextView txt_sales_quantity;
	protected final ImageView mainImg;
	protected final TextView valueInfo;
	protected final RelativeLayout main_layout;
	protected final ImageView delivery_image;
	protected final SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
	protected final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
	protected final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
	protected final AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);
	private static final String VOD_PRODUCT = "VP";
	private static final String VOD_NO_PRODUCT = "VNP";

	//날톡 텍스트 롤링을 위한 변수 세팅
	private static final long TEXT_ROLLING_OFFSET_PERIOD = 2000;	//타이머 최초 구동시까지 대기 시간(2초)
	private static final long TEXT_ROLLING_INTERVAL = 3000;	//문구 교체 주기(3초)
	private Timer nalTalkTimer = null;
	private String[] nalTalkText;	//날톡 문구
	private int nalTalkIdx = 0;	//현재 노출된 인덱스
	private int nalTalkCnt = 0;	//문구 총 갯수
	private TextSwitcher nalTalkSwitcher;
	private ViewSwitcher.ViewFactory nalTalkFactory;

	//3h부터 카운팅을 위한 핸들러
	private Handler m3hHandler;
	private Runnable m3hRunnable;

	//컨텍스트
	private Context mContext;

	private static final String SECTION_CODE_PREFIX = "#";

	//날방 생방송정보 API주소
	private String nalbangLiveUrl;

	//GTM
	private String action;
	private String label;
	private String sectionName;
	private String sectionCode;

	/**
	 * 생방송 영역 상태
	 */
	public enum LiveState {
		THREE_HOUR_BEFORE,	//생방송 3시간전 이전
		THREE_HOUR_AFTER,	//생방송 3시간전 ~ 생방송직전
		LIVE_ONAIR			//생방송중
	}

	private static LiveState mLiveState;
	private static LiveState prevLiveState;

	/**
	 * 뷰홀더
	 *
	 * @param itemView 인플레이트된 레이아웃
	 */
	public NalbangBannerLiveViewHolder(View itemView) {
		super(itemView);

		//생방송 영역 상단
		layLiveOnairTop = (LinearLayout) itemView.findViewById(R.id.lay_live_onair_top);
		lay3hAfterTop = (LinearLayout) itemView.findViewById(R.id.lay_3h_after_top);

		//생방송 영역 우하단 재생시간
		txtPlayTime = (TextView) itemView.findViewById(R.id.txt_play_time);

		//생방송 영역 하단
		layBottomPrd = (LinearLayout) itemView.findViewById(R.id.lay_bottom_prd);
		layBottomNaltalk = (LinearLayout) itemView.findViewById(R.id.lay_bottom_naltalk);

		//생방송 3h전부터 남은시간 표시용
		txtRemainTime = (TextView) itemView.findViewById(R.id.txt_before_remain_time);

		//생방송 상단 텍스트 (샘플 : "날톡에 지금 참여하시면 아메리카노 드려요!")
		txtLiveOnairTop = (TextView) itemView.findViewById(R.id.txt_live_onair_top);

		nalTalkSwitcher = (TextSwitcher) itemView.findViewById(R.id.switcher);

		// TV생방송 progress
		layoutTvLiveProgress = (TvLiveTimeLayout) itemView.findViewById(R.id.tv_live_progress);
		// 방송 스크린샷 이미지
		imageView = (ImageView) itemView.findViewById(R.id.main_img);

		layoutTvGoods = itemView.findViewById(R.id.image_layout);

		layBottomPrdVp = (RelativeLayout) itemView.findViewById(R.id.lay_bottom_prd_vp);
		layBottomPrdVnp = (RelativeLayout) itemView.findViewById(R.id.lay_bottom_prd_vnp);

		main_layout  = (RelativeLayout) itemView.findViewById(R.id.main_layout);
		txtTitle = (TextView) itemView.findViewById(R.id.txt_title_vp);
		txtTitleVnp = (TextView) itemView.findViewById(R.id.txt_title_vnp);
		txt_badge_per = (TextView) itemView.findViewById(R.id.txt_badge_per);
		mainImg = (ImageView) itemView.findViewById(R.id.main_img);
		valueInfo = (TextView) itemView.findViewById(R.id.valueInfo);

		txt_base_price = (TextView) itemView.findViewById(R.id.txt_base_price);
		txt_base_price_unit = (TextView) itemView.findViewById(R.id.txt_base_price_unit);

		txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
		txt_price_unit = (TextView) itemView.findViewById(R.id.txt_price_unit);
		txt_sales_quantity = (TextView) itemView.findViewById(R.id.txt_sales_quantity);

		delivery_image = (ImageView) itemView.findViewById(R.id.delivery_image);

		if(main_layout != null) {
			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_layout);
		}
		if(mainImg != null) {
			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), mainImg);
		}
	}

	/**
	 *  bind
	 */
	@Override
	public void onBindViewHolder(Context context, int position, ShopInfo mInfo, String action, String label, String sectionName) {
		this.mInfo = mInfo;
		this.mContext = context;
		this.action = action;
		this.label = label;
		this.sectionName = sectionName;
		this.sectionCode = mInfo.sectionList.sectionCode;

		bindViewHolder(context, mInfo.tvLiveDealBanner, mInfo.sectionList.sectionCode, action, label, sectionName);
	}

	private void bindViewHolder(final Context context, final TvLiveDealBanner tvLiveDealBanner, final String sectionCode, final String action, final String label, final String sectionName) {
		//날톡 텍스트 데이타 초기화
		nalTalkText = null;

		//생방송 상태를 확인한다.
		setLiveState(tvLiveDealBanner.broadStartTime, tvLiveDealBanner.broadCloseTime, tvLiveDealBanner.liveBroadStartTime, tvLiveDealBanner.onAirYn);

		adjustLayout();

		//생방송을 새로고침을 위한 생방송API 세팅
		if(this.mInfo != null && tvLiveDealBanner != null && mInfo.tvLiveDealBanner != null) {
			tvLiveDealBanner.tvLiveDealUrl = this.mInfo.tvLiveDealBanner.tvLiveDealUrl;
		}
		mInfo.tvLiveDealBanner = tvLiveDealBanner;

		nalbangLiveUrl = mInfo.tvLiveDealBanner.tvLiveDealUrl;

		if (!TextUtils.isEmpty(mInfo.tvLiveDealBanner.videoTime)) {
			txtPlayTime.setText(mInfo.tvLiveDealBanner.videoTime);
		}

		if (!TextUtils.isEmpty(mInfo.tvLiveDealBanner.promotionName)) {
			txtLiveOnairTop.setText(mInfo.tvLiveDealBanner.promotionName);
		} else {
			//promotionName 정보가 없으면 생방송 상단영역 숨김
			layLiveOnairTop.setVisibility(View.GONE);
		}

		// tv방송 정보가 유효한지 체크
		if (verifyTvInfo(tvLiveDealBanner)) {
			prevLiveState = mLiveState;
		}

		//생방송 progress
		layoutTvLiveProgress.setOnTvLiveFinishedListener(new TvLiveTimeLayout.OnTvLiveFinishedListener() {
			@Override
			public void onTvLiveFinished() {
				new GetTvLiveContentUpdateController(context, action, label, sectionName).execute(false, sectionCode);
			}

		});


		//날톡 관련 정보가 모두 유효한 경우만 화면에 노출하고 텍스트 롤링을 시작한다.
		if (mLiveState == LiveState.LIVE_ONAIR
				&& mInfo.tvLiveDealBanner.nalTalkBanner != null
				&& !TextUtils.isEmpty(mInfo.tvLiveDealBanner.nalTalkBanner.linkUrl)
				&& mInfo.tvLiveDealBanner.nalTalkBanner.talkList != null
				&& !mInfo.tvLiveDealBanner.nalTalkBanner.talkList.isEmpty()) {

			nalTalkCnt = mInfo.tvLiveDealBanner.nalTalkBanner.talkList.size();
			nalTalkText = mInfo.tvLiveDealBanner.nalTalkBanner.talkList.toArray(
					new String[nalTalkCnt]);

			if (nalTalkFactory == null) {
				nalTalkFactory = new ViewSwitcher.ViewFactory() {
					@Override
					public View makeView() {
						return (TextView) ((Activity) context).getLayoutInflater().inflate(R.layout.naltalk_text, null);
					}
				};
				nalTalkSwitcher.setFactory(nalTalkFactory);
			}

			nalTalkSwitcher.setInAnimation(context, R.anim.livetalk_up_in);
			nalTalkSwitcher.setOutAnimation(context, R.anim.livetalk_up_out);

			//TV쇼핑탭 로딩시 날톡 텍스트영역 공백상태를 최소화 하기 위해 타이머 수행전에 테스트 먼저 세팅
			if (nalTalkTimer == null) {
				setLiveTalkRollingText();
			}

			//날톡 레이아웃 클릭시 날톡으로 이동 (아이콘+텍스트영역)
			layBottomNaltalk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, mInfo.tvLiveDealBanner.nalTalkBanner.linkUrl, ((Activity) context).getIntent());

					//GTM 클릭이벤트 전달
					String label = GTMEnum.GTM_NONE;
					String action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
							sectionName, GTMEnum.GTM_ACTION_LIVE_NALTALK_TAIL);
					try {
						label = Uri.parse(mInfo.tvLiveDealBanner.nalTalkBanner.linkUrl).getQuery();
					} catch (Exception e) {
						// 10/19 품질팀 요청
						// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
						// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
						// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
						Ln.e(e);
					}
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
				}
			});

			//날톡 영역을 노출한다.
			ViewUtils.showViews(layBottomNaltalk);

		} else {
			//API에서 날톡 관련 정보가 없거나 비정상적이면 날톡 영역을 노출하지 않는다.
			ViewUtils.hideViews(layBottomNaltalk);
			stopRollingTextTimer();
		}

		//상품이미지 노출
		ImageUtil.loadImageTvLive(context.getApplicationContext(), tvLiveDealBanner.imageUrl.trim(), imageView, R.drawable.noimg_tv);

		final SectionContentList item = tvLiveDealBanner.product;

		titleStringBuilder.clear();
		priceStringBuilder.clear();
		txtTitle.setText("");
		txtPrice.setText("");
		txt_badge_per.setText("");
		valueInfo.setText("");
		txt_sales_quantity.setText("");
		txt_base_price.setVisibility(View.GONE);
		txt_base_price_unit.setVisibility(View.GONE);

		//상품 이미지 영역 클릭
		layoutTvGoods.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveDealBanner.linkUrl), ((Activity) context).getIntent());
				// GTM AB Test 클릭이벤트 전달
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
			}
		});

		//product 정보 없으면 하단 상품정보 영역 비노출
		if (item == null) {
			layBottomPrd.setVisibility(View.GONE);
			return;
		}

		//상품 가격영역 클릭
		layBottomPrd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, item.linkUrl);
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
			}
		});

		//상품명 세팅
		if (DisplayUtils.isValidString(item.productName)) {
			titleStringBuilder.append(item.productName);
		}
		txtTitle.append(titleStringBuilder);
		txtTitleVnp.append(titleStringBuilder);

		//VOD만 등록된 상품의 경우
		if (VOD_NO_PRODUCT.equalsIgnoreCase(item.viewType)) {
			//상품명만 노출함
			layBottomPrdVp.setVisibility(View.GONE);
			layBottomPrdVnp.setVisibility(View.VISIBLE);
			return;
		} else if (VOD_PRODUCT.equalsIgnoreCase(item.viewType)) {
			layBottomPrdVp.setVisibility(View.VISIBLE);
			layBottomPrdVnp.setVisibility(View.GONE);
		} else {
			layBottomPrd.setVisibility(View.GONE);
			return;
		}

		if(item.valueInfo != null){
			valueInfo.setText(item.valueInfo);
		}

		//상품 명 옆 이미지(1개만 표현)
		//총알배송 "quickDlv"
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

		// 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
		// base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
		int salePrice = DisplayUtils.getNumberFromString(item.salePrice);
		int basePrice = DisplayUtils.getNumberFromString(item.basePrice);
		// 할인률
		if (item.discountRate == null) {
			item.discountRate = "0";
		}
		if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
			if (DisplayUtils.isValidNumberStringExceptZero(item.basePrice)
					&& (salePrice < basePrice)) {
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
				txt_badge_per.setPadding(0, 0, 0, context.getResources().getDimensionPixelSize(R.dimen.badge_text_padding));
			}else{
				// 할인률
				txt_badge_per.append(ProductInfoUtil.getDiscountText(context, item));
			}
		}else{
			// 할인률
			txt_badge_per.append(ProductInfoUtil.getDiscountText(context, item));
		}

		txt_sales_quantity.setText(ProductInfoUtil.getSaleQuantityText(item));
	}

	/**
	 * 방송시작, 종료시간을 가지고 현재 생방송영역 상태를 세팅한다.
	 *
	 * @param bStart 방송시작시간 (VOD or 생방송, If onAirYn="Y" 생방송시작시간, Else VOD시작시간)
	 * @param bClose 방송종료시간 (VOD or 생방송)
	 * @param liveStart 생방송 시작시간
	 * @param onAirYn   생방송 여부 ("Y" or "N")
	 */
	private void setLiveState(String bStart, String bClose, String liveStart, String onAirYn) {
		long currentTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(BROAD_TIME_FORMAT, Locale.getDefault());
		long mills3h = TimeUnit.HOURS.toMillis(NOTICE_BEFORE_TIME);

		/*-----------------------------------------------------------------------
		1.onAirYn=Y 인 경우
		  생방송 시작/종료시간은 API broadStartTime/broadCloseTime 필드에 저장되고
		2.onAirYn=N 인 경우
		  VOD 시작/종료시간은 API broadStartTime/broadCloseTime 필드에 저장되고
		  생방송 시작시간만 API liveBroadStartTime 필드에 저장됨
		------------------------------------------------------------------------*/
		if ("Y".equalsIgnoreCase(onAirYn)) {
			try {
				broadStartTime = sdf.parse(bStart).getTime();
				broadCloseTime = sdf.parse(bClose).getTime();
			} catch (Exception e) {
				//안내화면
				mLiveState = LiveState.THREE_HOUR_BEFORE;
				// 10/19 품질팀 요청
				// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
				// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
				// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
				Ln.e(e);
				return;
			}

			if (currentTime >= broadStartTime && currentTime <= broadCloseTime) {
				//생방송 화면
				mLiveState = LiveState.LIVE_ONAIR;
			} else {
				//안내화면
				mLiveState = LiveState.THREE_HOUR_BEFORE;
			}

		} else {
			try {
				broadStartTime = sdf.parse(liveStart).getTime();
				broadCloseTime = 0;
			} catch (Exception e) {
				//생방송이 없는 경우 API에서 ""값 들어옴, 혹시 null값이 들어와도 여기에 해당
				//안내화면
				mLiveState = LiveState.THREE_HOUR_BEFORE;
				// 10/19 품질팀 요청
				// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
				// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
				// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
				Ln.e(e);
				return;
			}

			//남은시간 표시를 위한 3시간전 시간
			noticeStartTime = broadStartTime - mills3h;

			if (noticeStartTime <= currentTime && currentTime < broadStartTime) {
				//남은시간 표시 화면
				mLiveState = LiveState.THREE_HOUR_AFTER;
			} else {
				//안내화면
				mLiveState = LiveState.THREE_HOUR_BEFORE;
			}
		}

		Ln.i("setLiveState : " + mLiveState.toString());
	}

	/**
	 * 생방송영역 상태별로 UI를 세팅한다.
	 */
	private void adjustLayout() {
		layLiveOnairTop.setVisibility(View.GONE);
		lay3hAfterTop.setVisibility(View.GONE);

		layoutTvLiveProgress.setVisibility(View.GONE);
		txtPlayTime.setVisibility(View.GONE);

		layBottomPrd.setVisibility(View.GONE);
		layBottomNaltalk.setVisibility(View.GONE);

		switch (mLiveState) {
			case THREE_HOUR_BEFORE:
				txtPlayTime.setVisibility(View.VISIBLE);
				layBottomPrd.setVisibility(View.VISIBLE);
				break;
			case THREE_HOUR_AFTER:
				lay3hAfterTop.setVisibility(View.VISIBLE);
				txtPlayTime.setVisibility(View.VISIBLE);
				layBottomPrd.setVisibility(View.VISIBLE);
				startRemainTimer();
				break;
			case LIVE_ONAIR:
				layLiveOnairTop.setVisibility(View.VISIBLE);
				layoutTvLiveProgress.setVisibility(View.VISIBLE);
				layBottomNaltalk.setVisibility(View.VISIBLE);
				break;
		}

		Ln.i("adjustLayout : " + mLiveState.toString());
	}

	/**
	 * 생방송 시작까지 남은시간을 반환한다.
	 *
	 * @param nowTime
	 * @return
	 */
	private String getRemaintTimeText(long nowTime) {
		long remainTimeL = broadStartTime - nowTime;
		int  hours = (int)(remainTimeL/(1000*60*60)) % 24;
		int  minutes = (int)(remainTimeL/(1000*60)) % 60;
		int seconds = (int)(remainTimeL/1000) % 60;

		return String.format(Locale.getDefault(), "%01d시간 %02d분 %02d초", hours, minutes, seconds);
	}

	/**
	 * 현재 시간을 체크해서 남은 시간을 표시한다.(생방송 3시간전 ~ 생방송시작)
	 * 남은시간이 없는 경우 날방생방송 API를 호출하여 생방송영역을 갱신한다.
	 */
	private void updateRemainTime() {
		((Activity)mContext).runOnUiThread(() -> {
			long nowTime = System.currentTimeMillis();
			if (nowTime >= noticeStartTime && nowTime <= broadStartTime) {
				// 생방송 3시간전 ~ 생방송시작
				txtRemainTime.setText(getRemaintTimeText(nowTime));
			} else {
				// 생방송 시작
				stopRemainTimer();

				if (nowTime > 0) {
					new Handler().postDelayed(() -> new GetTvLiveContentUpdateController(mContext, action, label, sectionName).execute(false, sectionCode), LIVE_API_CALL_DELAY_MILLIS);
				}
			}
		});
	}

	/**
	 * 3h 타이머를 시작한다.
	 */
	public void startRemainTimer() {
		try {
			EventBus.getDefault().register(this);
		}catch(Exception e){
			Ln.e(e);
		}
		GlobalTimer.getInstance().startTimer();
		updateRemainTime();

//		stopRemainTimer();
//		remainTimer = new Timer();
//		remainTimer.schedule(new TimerTask() {
//			public void run() {
//				updateRemainTime();
//			}
//		}, 0, DELAY_MILLIS);
	}

	/**
	 * 3h 타이머를 정지한다.
	 */
	public void stopRemainTimer() {
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(Events.TimerEvent event) {
		updateRemainTime();
	}

	/**
	 * 날방생방송 view가 이미 존재하는 상태에서 데이타만 update하기 위한 controller
	 *
	 */
	private class GetTvLiveContentUpdateController extends BaseAsyncController<TvLiveDealContent> {
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
		protected TvLiveDealContent process() throws Exception {
			// sectionCode가 "#"으로 시작하면 "#" 삭제
			String result = sectionCode;
			if (sectionCode.startsWith(SECTION_CODE_PREFIX)) {
				result = sectionCode.replaceFirst(SECTION_CODE_PREFIX, "");
			}

			return (TvLiveDealContent)DataUtil.getData(context, restClient, TvLiveDealContent.class, false,
					true, nalbangLiveUrl + "?sectionCode=" + result);

		}

		@Override
		protected void onSuccess(TvLiveDealContent tvLiveTemp) throws Exception {
			if (tvLiveTemp != null) {
				if (verifyTvInfo(tvLiveTemp.tvLiveDealBanner)) {

					setLiveState(tvLiveTemp.tvLiveDealBanner.broadStartTime, tvLiveTemp.tvLiveDealBanner.broadCloseTime,
							tvLiveTemp.tvLiveDealBanner.liveBroadStartTime, tvLiveTemp.tvLiveDealBanner.onAirYn);

					if (prevLiveState != mLiveState) {
						Ln.i("prevLiveState != mLiveState : " + prevLiveState.toString() + ", " + mLiveState.toString());

						prevLiveState = mLiveState;

						String tempAction = GTMEnum.GTM_NONE;
						String tempLabel = GTMEnum.GTM_NONE;

						if( action !=  null )
							tempAction = action;
						if( label !=  null )
							tempLabel = label;

						//context가 유효한 경우만 화면 갱신 (메인 상단아이콘 다수 클릭 후 생방송 종료시 context finishing 발생)
						if (!((Activity)context).isFinishing()) {
							bindViewHolder(context, tvLiveTemp.tvLiveDealBanner, sectionCode, tempAction, tempLabel, sectionName);

							startAllTimer();

							registerRunnable();
						}
					} else {
						Ln.i("prevLiveState == mLiveState : " + prevLiveState.toString() + ", " + mLiveState.toString());
					}
				}
			}
		}
	}

	/**
	 * 날톡 문구 교체를 위한 타이머를 시작한다.
	 */
	private void startRollingTextTimer() {
		stopRollingTextTimer();
		nalTalkTimer = new Timer();
		nalTalkTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				setLiveTalkRollingText();
			}
		}, TEXT_ROLLING_OFFSET_PERIOD, TEXT_ROLLING_INTERVAL);
	}

	/**
	 * 날톡 문구를 롤링시킨다.
	 */
	private void setLiveTalkRollingText() {
		((Activity)mContext).runOnUiThread(() -> {
			nalTalkIdx = nalTalkIdx < nalTalkCnt ? nalTalkIdx : 0;
			if (nalTalkCnt > 1) {
				//다음에 표시될 textview에 텍스트를 세팅하고 애니메이션을 수행하면서 textview가 교체된다.
				nalTalkSwitcher.setText(nalTalkText[nalTalkIdx]);
			} else {
				//현재 노출된 textview에 텍스트를 세팅한다. 이때 애니메이션은 수행되지 않는다.
				nalTalkSwitcher.setCurrentText(nalTalkText[nalTalkIdx]);
			}
			nalTalkIdx++;
		});
	}

	/**
	 * 날톡 문구 교체를 위한 타이머를 중지한다.
	 */
	private void stopRollingTextTimer() {
		if (nalTalkTimer != null) {
			nalTalkTimer.cancel();
			// 대기중이던 취소된 행위가 있는 경우 모두 제거
			nalTalkTimer.purge();
			nalTalkTimer = null;
		}
	}

	/**
	 * 날방생방송 정보가 유효한 지 체크한다.
	 *
	 * @param tvLiveDealBanner 날방생방송정보
	 * @return 유효하면 true 리턴
	 */
	private boolean verifyTvInfo(TvLiveDealBanner tvLiveDealBanner) {
		//유효성 체크로직 정의되면 추가 요망
		return true;
	}

	/**
	 * 생방송 남은시간 노출용 타이머를 시작/정지 시킨다.
	 * (NalbangShopFragment onStart/onStop에서 호출됨)
	 *
	 * @param event NalbangTimerEvent
	 */
	public void onEvent(Events.FlexibleEvent.NalbangTimerEvent event) {
		if (event.start) {
			startAllTimer();
		} else {
			stopAllTimer();
		}
	}

	public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
	}


	/**
	 * 생방송 남은시간 노출용 타이머를 시작한다.
	 */
	protected void startTvLiveTimer() {
		layoutTvLiveProgress.updateTvLiveTime(null, mInfo.tvLiveDealBanner.broadStartTime,
				mInfo.tvLiveDealBanner.broadCloseTime);
	}

	/**
	 * 생방송 남은시간 노출용 타이머를 정지한다.
	 */
	protected  void stopTvLiveTimer() {
		layoutTvLiveProgress.stopTimer();
	}

	/**
	 * 날방에서 사용중인 모든 타이머를 시작한다.
	 */
	private void startAllTimer() {
		if (mLiveState == LiveState.LIVE_ONAIR) {
			//생방송 남은시간 표시용 타이머 시작
			startTvLiveTimer();

			//날톡 텍스트롤링 타이머 시작
			if (nalTalkText != null && nalTalkText.length > 1) {
				startRollingTextTimer();
			}
		}

		//3h부터 생방송 시작시간까지 남은시간 표시용 타이머 시작
		if (mLiveState == LiveState.THREE_HOUR_AFTER) {
			startRemainTimer();
		}
	}

	/**
	 * 날방에서 사용중인 모든 타이머를 중지한다.
	 */
	private void stopAllTimer() {
		//생방송 남은시간 표시용 타이머 정지
		stopTvLiveTimer();

		//날톡 텍스트롤링 타이머 정지
		stopRollingTextTimer();

		//3h부터 생방송 시작시간까지 남은시간 표시용 타이머 정지
		stopRemainTimer();
	}

	/**
	 * 러너블을 등록한다.
	 */
	private void registerRunnable() {
		//안내화면에서는 생방송 3h 전까지 남은시간을 구해서 postDelayed를 수행한다.
		if (mLiveState == LiveState.THREE_HOUR_BEFORE) {
			//메시지큐에서 러너블 제거
			removeRunnable();

			//생방송 3시간 전이 되면 남은시간 표시화면으로 변경
			m3hHandler = new Handler();
			m3hRunnable = new Runnable() {
				@Override
				public void run() {
					mLiveState = LiveState.THREE_HOUR_AFTER;
					adjustLayout();
				}
			};
			long afterFromNow = noticeStartTime - System.currentTimeMillis();
			if (afterFromNow > 0) {
				m3hHandler.postDelayed(m3hRunnable, afterFromNow);
				Ln.i("m3hHandler.postDelayed ms: " + afterFromNow + ", min:" + (afterFromNow)/(1000*60));
			}
		}
	}

	/**
	 * 러너블을 제거한다.
	 */
	private void removeRunnable() {
		//메시지큐에서 러너블 제거
		if (m3hHandler != null) {
			m3hHandler.removeCallbacks(m3hRunnable);
			Ln.i("m3hHandler.removeCallbacks");
		}
	}

	/**
	 * 뷰가 화면에 노출될때 발생
	 */
	@Override
	public void onViewAttachedToWindow() {
		EventBus.getDefault().register(this);

		startAllTimer();
		registerRunnable();
	}

	/**
	 * 뷰가 화면에서 사라질때 발생
	 */
	@Override
	public void onViewDetachedFromWindow() {
		EventBus.getDefault().unregister(this);

		stopAllTimer();
		removeRunnable();
	}
}
