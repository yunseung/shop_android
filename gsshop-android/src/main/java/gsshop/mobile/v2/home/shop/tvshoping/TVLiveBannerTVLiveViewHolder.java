/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.tvshoping;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.Player;
import com.tms.sdk.common.util.StringUtil;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.BroadType;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.tv.VideoParameters;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.R.id.badge_1;
import static gsshop.mobile.v2.R.id.badge_2;
import static gsshop.mobile.v2.R.id.badge_3;
import static gsshop.mobile.v2.R.id.txt_price_expose;

/**
 * TV쇼핑탭 tv live & data dual banner.
 *
 */
@SuppressLint("NewApi")
public class TVLiveBannerTVLiveViewHolder extends BaseViewHolder implements OnMediaPlayerListener {

	/**
	 * 생방송/데이타방송 구분
	 */
	BroadType broadType = BroadType.live;

	/**
	 * 백그라운드(동영상재생) 영역
	 */
	private final View layoutTvGoods;
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
	private final ImageView imageView;
	private final View dim;

	// 편성표 보기
	private final Button scheduleBtn;
	// 5%적립금
	private final View btn_live_talk;
	private final TextView live_talk_text;

	private final Button btn_pay;

	private final LinearLayout price_layout;
	private final LinearLayout price_text_layout;

	private final LinearLayout btn_layout;
	private final LinearLayout price_info_layout;

	private final ImageView[] badge = new ImageView[3];

	private final View root_view;
	private final View live_talk_line;
	private ShopInfo mInfo;

	private final View counter;
	private final LinearLayout number_counter_layout;
	private final LinearLayout price_root;

	private OnMediaPlayerController playerController = null;

	private View info_view;
	private View info_view_play;
	private View info_view_end;

	private Button play;
	private View close;
	private View close_play;
	private View pause;
	private View go_link;
	private View full_screen;

	private Context context;

	//GTM용 파라미터 정의
	private String sectionName;

	private boolean isMdiaPlayerResize = false;


	/**
	 * 프론트(듀얼) 영역
	 */
	private int BROAD_NUM = 2;
	private LinearLayout layFront;

	private String[] mPrevBroadEndTime = new String[BROAD_NUM];

	// TV생방송 progress
	private TvShopTimeLayout[] layoutTvProgress = new TvShopTimeLayout[BROAD_NUM];
	private TextView[] front_txt_remain_time = new TextView[BROAD_NUM];

	private LinearLayout[] front_lay = new LinearLayout[BROAD_NUM];

	private RelativeLayout[] front_lay_goods = new RelativeLayout[BROAD_NUM];

	// 방송 스크린샷 이미지
	private ImageView[] front_main_img = new ImageView[BROAD_NUM];

	private View[] front_dim = new View[BROAD_NUM];

	// 제품 이름
	private TextView[] front_txt_title = new TextView[BROAD_NUM];
	// 가격
	private TextView[] front_txt_base_price = new TextView[BROAD_NUM];

	private TextView[] front_txt_base_price_expose = new TextView[BROAD_NUM];

	private TextView[] front_txt_price = new TextView[BROAD_NUM];

	private TextView[] front_txt_price_won = new TextView[BROAD_NUM];

	private TextView[] front_price_info = new TextView[BROAD_NUM];

	private LinearLayout[] front_price_info_layout = new LinearLayout[BROAD_NUM];

	private LinearLayout[] front_price_layout = new LinearLayout[BROAD_NUM];

	private TextView[] front_card_rental_text = new TextView[BROAD_NUM];

	private Button[] front_play = new Button[BROAD_NUM];

	private LinearLayout[] front_time_lay = new LinearLayout[BROAD_NUM];

	private TextView[] front_brd_type = new TextView[BROAD_NUM];

	private LinearLayout[] front_end = new LinearLayout[BROAD_NUM];

	private final ImageView[][] front_badge = new ImageView[2][3];

	// 편성표
	private TextView[] front_btn_schedule = new TextView[BROAD_NUM];

	// 라이브톡
	private TextView[] front_btn_livetalk = new TextView[BROAD_NUM];

	// 구매하기
	private TextView[] front_btn_pay = new TextView[BROAD_NUM];

	//전체상품보기
	private LinearLayout[] front_product_more = new LinearLayout[BROAD_NUM];

	//뱃지수
	private static final int BADGE_NUM = 3;

	/**
	 * 생성자
	 *
	 * @param itemView 레이아웃뷰
	 */
	public TVLiveBannerTVLiveViewHolder(View itemView) {
		super(itemView);

		/**
		 * 백그라운드(동영상재생) 영역
		 */
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

		//뱃지
		badge[2] = (ImageView) itemView.findViewById(badge_1);
		badge[1] = (ImageView) itemView.findViewById(badge_2);
		badge[0] = (ImageView) itemView.findViewById(badge_3);

		// 편성표 보기
		scheduleBtn = (Button) itemView.findViewById(R.id.btn_schedule);
		// 5%적립금
		btn_live_talk = itemView.findViewById(R.id.btn_live_talk);
		live_talk_text = (TextView)itemView.findViewById(R.id.live_talk_text);
		btn_pay = (Button) itemView.findViewById(R.id.btn_pay);
		price_root = (LinearLayout) itemView.findViewById(R.id.price_root);
		price_layout = (LinearLayout) itemView.findViewById(R.id.price_layout);
		price_text_layout = (LinearLayout) itemView.findViewById(R.id.price_text_layout);
		btn_layout = (LinearLayout) itemView.findViewById(R.id.btn_layout);
		price_info_layout = (LinearLayout) itemView.findViewById(R.id.price_info_layout);
		root_view = itemView.findViewById(R.id.root_view);
		live_talk_line = itemView.findViewById(R.id.live_talk_line);
		counter = itemView.findViewById(R.id.counter);
		number_counter_layout = (LinearLayout) itemView.findViewById(R.id.number_counter_layout);

		info_view = itemView.findViewById(R.id.info_view);
		info_view_play = itemView.findViewById(R.id.info_view_play);
		info_view_end = itemView.findViewById(R.id.info_view_end);
		play = (Button) itemView.findViewById(R.id.play);
		close = itemView.findViewById(R.id.close);
		close_play = itemView.findViewById(R.id.close_play);
		pause = itemView.findViewById(R.id.pause);
		go_link = itemView.findViewById(R.id.go_link);
		full_screen = itemView.findViewById(R.id.full_screen);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), info_view);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), info_view_play);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), imageView);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), dim);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), counter);

		/**
		 * front 영역
		 */
		layFront = (LinearLayout) itemView.findViewById(R.id.lay_front);

		front_lay[0] = (LinearLayout) itemView.findViewById(R.id.front_lay_live);
		front_lay[1] = (LinearLayout) itemView.findViewById(R.id.front_lay_data);

		front_play[0] = (Button) itemView.findViewById(R.id.front_live_play);
		front_play[1] = (Button) itemView.findViewById(R.id.front_data_play);

		front_time_lay[0] = (LinearLayout) itemView.findViewById(R.id.front_live_time_lay);
		front_time_lay[1] = (LinearLayout) itemView.findViewById(R.id.front_data_time_lay);

		front_brd_type[0] = (TextView) itemView.findViewById(R.id.front_live_brd_type);
		front_brd_type[1] = (TextView) itemView.findViewById(R.id.front_data_brd_type);

		// TV생방송 progress
		layoutTvProgress[0] = (TvShopTimeLayout) itemView.findViewById(R.id.front_tv_live_progress);
		layoutTvProgress[1] = (TvShopTimeLayout) itemView.findViewById(R.id.front_tv_data_progress);
		front_txt_remain_time[0] = (TextView) itemView.findViewById(R.id.front_live_txt_remain_time);
		front_txt_remain_time[1] = (TextView) itemView.findViewById(R.id.front_data_txt_remain_time);

		front_end[0] = (LinearLayout) itemView.findViewById(R.id.front_live_end);
		front_end[1] = (LinearLayout) itemView.findViewById(R.id.front_data_end);

		//상단 상품영역
		front_lay_goods[0] = (RelativeLayout) itemView.findViewById(R.id.front_lay_live_goods);
		front_lay_goods[1] = (RelativeLayout) itemView.findViewById(R.id.front_lay_data_goods);

		//상품 이미지
		front_main_img[0] = (ImageView) itemView.findViewById(R.id.front_live_main_img);
		front_main_img[1] = (ImageView) itemView.findViewById(R.id.front_data_main_img);

		front_dim[0] = itemView.findViewById(R.id.front_live_dim);
		front_dim[1] = itemView.findViewById(R.id.front_data_dim);

		// 제품 이름
		front_txt_title[0] = (TextView) itemView.findViewById(R.id.front_live_txt_title);
		front_txt_title[1] = (TextView) itemView.findViewById(R.id.front_data_txt_title);

		// 가격
		front_txt_base_price[0] = (TextView) itemView.findViewById(R.id.front_live_txt_base_price);
		front_txt_base_price[1] = (TextView) itemView.findViewById(R.id.front_data_txt_base_price);
		front_txt_base_price_expose[0] = (TextView) itemView.findViewById(R.id.front_live_txt_base_price_expose);
		front_txt_base_price_expose[1] = (TextView) itemView.findViewById(R.id.front_data_txt_base_price_expose);
		front_txt_price[0] = (TextView) itemView.findViewById(R.id.front_live_txt_price);
		front_txt_price[1] = (TextView) itemView.findViewById(R.id.front_data_txt_price);
		front_txt_price_won[0] = (TextView) itemView.findViewById(R.id.front_live_txt_price_expose);
		front_txt_price_won[1] = (TextView) itemView.findViewById(R.id.front_data_txt_price_expose);

		//청구할인
		front_price_info[0] = (TextView) itemView.findViewById(R.id.front_live_price_info);
		front_price_info[1] = (TextView) itemView.findViewById(R.id.front_data_price_info);

		front_badge[0][2] = (ImageView) itemView.findViewById(R.id.front_live_badge_1);
		front_badge[0][1] = (ImageView) itemView.findViewById(R.id.front_live_badge_2);
		front_badge[0][0] = (ImageView) itemView.findViewById(R.id.front_live_badge_3);
		front_badge[1][2] = (ImageView) itemView.findViewById(R.id.front_data_badge_1);
		front_badge[1][1] = (ImageView) itemView.findViewById(R.id.front_data_badge_2);
		front_badge[1][0] = (ImageView) itemView.findViewById(R.id.front_data_badge_3);

		front_price_info_layout[0] = (LinearLayout) itemView.findViewById(R.id.front_live_price_info_layout);
		front_price_info_layout[1] = (LinearLayout) itemView.findViewById(R.id.front_data_price_info_layout);
        front_price_layout[0] = (LinearLayout) itemView.findViewById(R.id.front_live_price_layout);
		front_price_layout[1] = (LinearLayout) itemView.findViewById(R.id.front_data_price_layout);

		front_card_rental_text[0] = (TextView) itemView.findViewById(R.id.front_live_card_rental_text);
		front_card_rental_text[1] = (TextView) itemView.findViewById(R.id.front_data_card_rental_text);

		front_btn_schedule[0] = (TextView) itemView.findViewById(R.id.front_live_btn_schedule);
		front_btn_schedule[1] = (TextView) itemView.findViewById(R.id.front_data_btn_schedule);
		front_btn_livetalk[0] = (TextView) itemView.findViewById(R.id.front_live_btn_live_talk);
		front_btn_livetalk[1] = (TextView) itemView.findViewById(R.id.front_data_btn_live_talk);
		front_btn_pay[0] = (TextView) itemView.findViewById(R.id.front_live_btn_pay);
		front_btn_pay[1] = (TextView) itemView.findViewById(R.id.front_data_btn_pay);

		front_product_more[0] = (LinearLayout) itemView.findViewById(R.id.front_live_product_more);
		front_product_more[1] = (LinearLayout) itemView.findViewById(R.id.front_data_product_more);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), front_main_img[0]);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), front_main_img[1]);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), front_dim[0]);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), front_dim[1]);

		EventBus.getDefault().register(this);
	}

	@Override
	public void onBindViewHolder(Context context, int position, final ShopInfo mInfo, String action, String label, String sectionName) {
		this.mInfo = mInfo;
		bindViewHolderFront(context, position, BroadType.live, mInfo.tvLiveBanner, mInfo.sectionList.sectionCode, action, label, sectionName);
		bindViewHolderFront(context, position, BroadType.data, mInfo.dataLiveBanner, mInfo.sectionList.sectionCode, action, label, sectionName);

		this.context = context;
		this.mInfo = mInfo;
		this.sectionName = sectionName;
	}

	/**
	 * 방송시간 종료 시 백그라운드영역 화면 UI를 변경한다.
	 */
	private void callBroadInfo() {
		//방송종료시 안내문구 노출
		pause.performClick();
		info_view_play.setVisibility(View.GONE);
		info_view_end.setVisibility(View.VISIBLE);
		layoutTvGoods.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showBroadEndMsg(context);
			}
		});
	}

	/**
	 * 방송종료 안내팝업을 노출한다.
	 *
	 * @param context 컨텍스트
	 */
	private void showBroadEndMsg(Context context) {
		String msgTitle = context.getResources().getString(R.string.home_tv_live_view_close_da_text) + "\n";
		String msgContent = context.getResources().getString(R.string.home_tv_live_next_brd_guide2);
		new CustomOneButtonDialog((Activity) context).message(msgTitle + msgContent)
				.buttonClick(CustomOneButtonDialog.DISMISS).show();
	}

	/**
	 * 방송종료 콜백 호출시 화면 UI를 변경한다.
	 *
	 * @param brdIndex 방송종류 인덱스
	 */
	private void doLiveFinished(int brdIndex) {
		front_play[brdIndex].setVisibility(View.INVISIBLE);
		front_time_lay[brdIndex].setVisibility(View.INVISIBLE);
		front_end[brdIndex].setVisibility(View.VISIBLE);
		front_lay_goods[brdIndex].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showBroadEndMsg(context);
			}
		});

		//생방송을 보고 있는 경우 방송종료멘트 표시
		if (layFront.getVisibility() == View.GONE) {
			//동영상 재생화면을 두 상품이 공유하기 때문에 방송종료처리는 아래 조건이 필요함
			if ((broadType == BroadType.live && brdIndex == 0)
					|| (broadType == BroadType.data && brdIndex == 1)) {
				callBroadInfo();
			}
		}
	}

	/**
	 * 프론트영역(듀얼) 세팅
	 *
	 * @param context 컨텍스트
	 * @param position 포지션
	 * @param tvLiveBanner 방송정보(생방송 or 데이타방송)
	 * @param sectionCode 섹션코드
	 * @param action GTM용 액션값
	 * @param _label GTM용 라벨값
	 * @param sectionName 섹션이름
	 */
	public void bindViewHolderFront(final Context context, final int position, final BroadType bType, final TvLiveBanner tvLiveBanner, final String sectionCode, final String action, String _label, final String sectionName) {

		final int brdIndex;
		if (bType == BroadType.live) {
			brdIndex = 0;
		} else {
			brdIndex = 1;
		}

		//방송이 없는 경우
		if (tvLiveBanner == null
				|| TextUtils.isEmpty(tvLiveBanner.broadType)
				|| TextUtils.isEmpty(tvLiveBanner.linkUrl)) {

			front_lay[brdIndex].removeAllViews();

			final int otherIdx = brdIndex == 0 ? 1 : 0;
			layFront.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

				@Override
				@SuppressWarnings("deprecation")
				public void onGlobalLayout() {
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
						front_play[otherIdx].getViewTreeObserver().removeOnGlobalLayoutListener(this);
					} else {
						front_play[otherIdx].getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}

					//재생버튼과 동일한 TOP 위치 구하기
					Rect rect = new Rect();
					front_play[otherIdx].getDrawingRect(rect);
					front_lay_goods[otherIdx].offsetDescendantRectToMyCoords(front_play[otherIdx], rect);

					//"방송준비중입니다" 이미지 노출
					ImageView iv = new ImageView(context);
					iv.setImageResource(R.drawable.preparation_1);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(0, rect.top, 0, 0);
					iv.setLayoutParams(lp);
					front_lay[brdIndex].addView(iv);
				}
			});

			return;
		}

		final String label = tvLiveBanner.productName;

		// tv방송 정보가 유효한지 체크
		if (verifyTvInfo(tvLiveBanner)) {
			mPrevBroadEndTime[brdIndex] = tvLiveBanner.broadCloseTime;
		}

		//생방송과 베스트 구분
		front_time_lay[brdIndex].setVisibility(View.VISIBLE);
		front_play[brdIndex].setVisibility(View.VISIBLE);
		front_brd_type[brdIndex].setVisibility(View.VISIBLE);
		front_txt_remain_time[brdIndex].setVisibility(View.VISIBLE);
		if (tvLiveBanner.broadType != null
				&& tvLiveBanner.broadType.equals(context.getResources().getString(R.string.home_tv_live_view_on_the_air))){
			//생방송인 경우
			front_play[brdIndex].setVisibility(View.VISIBLE);
			front_brd_type[brdIndex].setText(R.string.home_tv_live_view_on_air);
			if(TextUtils.isEmpty(tvLiveBanner.broadCloseTime)) {
				//남은시간 숨김
				front_txt_remain_time[brdIndex].setVisibility(View.INVISIBLE);
			}
		} else if (tvLiveBanner.broadType != null
				&& tvLiveBanner.broadType.equals(context.getResources().getString(R.string.home_tv_live_view_best))){
			//베스트인 경우
			front_play[brdIndex].setVisibility(View.INVISIBLE);
			front_brd_type[brdIndex].setText(R.string.home_tv_live_view_best_en);
			if(TextUtils.isEmpty(tvLiveBanner.broadCloseTime)) {
				//남은시간 & "베스트" 숨김
				front_txt_remain_time[brdIndex].setVisibility(View.INVISIBLE);
				front_brd_type[brdIndex].setVisibility(View.INVISIBLE);
			}
		}

		front_end[brdIndex].setVisibility(View.GONE);

		// TV생방송 progress
		if (brdIndex == 0) {
			layoutTvProgress[brdIndex].setOnTvLiveFinishedListener(new TvShopTimeLayout.OnTvLiveFinishedListener() {
				@Override
				public void onTvLiveFinished() {
					doLiveFinished(brdIndex);
				}
			});
		} else {
			layoutTvProgress[brdIndex].setOnDataLiveFinishedListener(new TvShopTimeLayout.OnDataLiveFinishedListener() {
				@Override
				public void onDataLiveFinished() {
					doLiveFinished(brdIndex);
				}
			});
		}

		// 방송 스크린샷 이미지
		ImageUtil.loadImageTvLive(context.getApplicationContext(),
				!TextUtils.isEmpty(tvLiveBanner.imageUrl) ? tvLiveBanner.imageUrl.trim() : "", front_main_img[brdIndex], R.drawable.noimg_list);

		// 제품 이름
		front_txt_title[brdIndex].setText(tvLiveBanner.productName);
		front_txt_title[brdIndex].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveBanner.linkUrl), ((Activity) context).getIntent());
			}
		});

		if(tvLiveBanner.basePrice != null) {
			String basePrise = tvLiveBanner.basePrice;
			if(tvLiveBanner.exposePriceText != null){
				front_txt_base_price_expose[brdIndex].setText(tvLiveBanner.exposePriceText);
			}
			front_txt_base_price[brdIndex].setText(basePrise);
			front_txt_base_price[brdIndex].setPaintFlags(front_txt_base_price[brdIndex].getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			front_txt_base_price[brdIndex].setVisibility(View.VISIBLE);
			if("0".equals(tvLiveBanner.basePrice)){
				front_txt_base_price[brdIndex].setVisibility(View.GONE);
				front_txt_base_price_expose[brdIndex].setVisibility(View.GONE);
			}else{
				front_txt_base_price[brdIndex].setVisibility(View.VISIBLE);
				front_txt_base_price_expose[brdIndex].setVisibility(View.VISIBLE);
			}
		}

		front_txt_base_price[brdIndex].setTextColor(Color.parseColor("#666666"));
		front_card_rental_text[brdIndex].setVisibility(View.INVISIBLE);

		for (int i=0; i<BADGE_NUM; i++) {
			front_badge[brdIndex][i].setVisibility(View.GONE);
		}

		front_price_info_layout[brdIndex].setGravity(Gravity.LEFT);
		front_price_info_layout[brdIndex].setVisibility(View.VISIBLE);
		front_price_info_layout[brdIndex].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveBanner.linkUrl), ((Activity) context).getIntent());
			}
		});

		if (tvLiveBanner.rwImgList != null && !tvLiveBanner.rwImgList.isEmpty()){
			for(int i=0; i< tvLiveBanner.rwImgList.size() ; i++){
				ImageUtil.loadImage(context, tvLiveBanner.rwImgList.get(i), front_badge[brdIndex][i] , 0);
				front_badge[brdIndex][i].setVisibility(View.VISIBLE);
			}
		}

		front_txt_price[brdIndex].setVisibility(View.VISIBLE);
		front_txt_price_won[brdIndex].setVisibility(View.VISIBLE);

		//렌탈상품
		if (DisplayUtils.isTrue(tvLiveBanner.isRental)) {
            front_txt_base_price[brdIndex].setTextColor(Color.parseColor("#111111"));
            front_txt_price[brdIndex].setText(tvLiveBanner.rentalPrice);
            front_txt_price_won[brdIndex].setVisibility(View.GONE);
            front_txt_base_price[brdIndex].setText(tvLiveBanner.rentalText);

            front_txt_base_price[brdIndex].setPaintFlags(txt_base_price.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            front_txt_base_price_expose[brdIndex].setVisibility(View.GONE);
            front_txt_base_price[brdIndex].setVisibility(View.VISIBLE);

			front_card_rental_text[brdIndex].setText(tvLiveBanner.rentalEtcText);
			front_card_rental_text[brdIndex].setVisibility(View.VISIBLE);
			front_card_rental_text[brdIndex].setGravity(Gravity.LEFT);
			//휴대폰
		} else if (DisplayUtils.isTrue(tvLiveBanner.isCellPhone)) {
            front_txt_base_price[brdIndex].setTextColor(Color.parseColor("#111111"));
			if (DisplayUtils.isValidString(tvLiveBanner.exposePriceText)) {
                front_txt_price_won[brdIndex].setText(tvLiveBanner.exposePriceText);
			}

            front_txt_price[brdIndex].setText(tvLiveBanner.salePrice);
			if(!"0".equals(tvLiveBanner.basePrice)){
                front_txt_base_price[brdIndex].setVisibility(View.VISIBLE);
                front_txt_base_price[brdIndex].setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                front_txt_base_price_expose[brdIndex].setVisibility(View.VISIBLE);
			}

			front_card_rental_text[brdIndex].setVisibility(View.VISIBLE);
			front_card_rental_text[brdIndex].setGravity(Gravity.LEFT);

			if(!TextUtils.isEmpty(tvLiveBanner.rentalText)){
                front_txt_base_price[brdIndex].setPaintFlags(txt_base_price.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                front_txt_base_price[brdIndex].setText(tvLiveBanner.rentalText);
                front_txt_base_price[brdIndex].setVisibility(View.VISIBLE);
                front_txt_base_price_expose[brdIndex].setVisibility(View.GONE);
				front_card_rental_text[brdIndex].setText(tvLiveBanner.rentalEtcText);
			}
		}else {

			// 아니면 가격 보이게
			if (DisplayUtils.isValidNumberString(tvLiveBanner.salePrice)) {
                front_txt_price[brdIndex].setText(DisplayUtils.getFormattedNumber(tvLiveBanner.salePrice));

				if (DisplayUtils.isValidString(tvLiveBanner.exposePriceText)) {
                    front_txt_price_won[brdIndex].setText(tvLiveBanner.exposePriceText);
				} else {
                    front_txt_price_won[brdIndex].setText(R.string.won);
				}

			} else {
				// 가격이 숫자가 아니거나 0이면 아무 표시하지 않음
                front_txt_price[brdIndex].setVisibility(View.INVISIBLE);
                front_txt_price_won[brdIndex].setVisibility(View.GONE);
			}
		}

		//생방송
		if("LIVEPRICE".equalsIgnoreCase(tvLiveBanner.priceMarkUpType)){
            front_price_info[brdIndex].setText(tvLiveBanner.priceMarkUp.replaceAll("/n", "\n"));
            front_price_info[brdIndex].setGravity(Gravity.CENTER_HORIZONTAL);
            front_price_info[brdIndex].setPadding(0,context.getResources().getDimensionPixelSize(R.dimen.tv_live_dual_gs_live_top_space),0,0);
            front_price_info[brdIndex].setBackgroundResource(R.drawable.tag_bg_an);
            front_price_layout[brdIndex].setPadding(context.getResources().getDimensionPixelSize(R.dimen.tv_live_price_left_space),0,0,0);
		}else if("RATE".equalsIgnoreCase(tvLiveBanner.priceMarkUpType)){
			//AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_live_bigTextSize), true);
			//AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_live_smallTextSize), true);
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
            front_price_info[brdIndex].setGravity(Gravity.CENTER);
            front_price_info[brdIndex].setPadding(0, 0,0,0);
            front_price_info[brdIndex].setBackgroundResource(android.R.color.transparent);
            front_price_info[brdIndex].setText(priceStringBuilder);

            front_price_layout[brdIndex].setPadding(context.getResources().getDimensionPixelSize(R.dimen.tv_live_price_left_space),0,0,0);
		}else if("GSPRICE".equalsIgnoreCase(tvLiveBanner.priceMarkUpType)){
			AbsoluteSizeSpan gsSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_live_dual_gs_TextSize), false);
			SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();

			priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_GS).append(Keys.DISCOUNT_RATE_TEXT_PRICE);
			priceStringBuilder.setSpan(gsSizeSpan, 0, priceStringBuilder.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			priceStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ed1f60")), 0, priceStringBuilder.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            front_price_info[brdIndex].setPadding(0, 0,0,0);
			if(front_txt_base_price[brdIndex].getVisibility() == View.GONE){
                front_price_info[brdIndex].setGravity(Gravity.CENTER);
                front_price_info_layout[brdIndex].setGravity(Gravity.CENTER);
			}else{
                front_price_info[brdIndex].setGravity(Gravity.CENTER);
			}

            front_price_info[brdIndex].setBackgroundResource(android.R.color.transparent);
            front_price_info[brdIndex].setText(priceStringBuilder);
            front_price_layout[brdIndex].setPadding(context.getResources().getDimensionPixelSize(R.dimen.tv_live_price_left_space),0,0,0);

		}else{
			//가격이 없으면
			if("".equals(tvLiveBanner.salePrice) && !"".equals(tvLiveBanner.rentalEtcText)){
                front_price_info_layout[brdIndex].setVisibility(View.GONE);
				front_card_rental_text[brdIndex].setText(tvLiveBanner.rentalEtcText);
				front_card_rental_text[brdIndex].setVisibility(View.VISIBLE);
				front_card_rental_text[brdIndex].setGravity(Gravity.LEFT);
			}
		}

		//편성표
		front_btn_schedule[brdIndex].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveBanner.broadScheduleLinkUrl), ((Activity) context).getIntent());
				//GTM 클릭이벤트 전달
				String action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
						sectionName, GTMEnum.GTM_ACTION_LIVE_SCHEDULE_TAIL);
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY,
						action,
						DisplayUtils.getFullUrl(tvLiveBanner.broadScheduleLinkUrl));
			}
		});

		// 라이브톡 버튼 노출여부 (생방송)
		if("Y".equalsIgnoreCase(tvLiveBanner.liveTalkYn)){
			front_btn_livetalk[brdIndex].setVisibility(View.VISIBLE);
			front_btn_livetalk[brdIndex].setText(tvLiveBanner.liveTalkText);
			front_btn_livetalk[brdIndex].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, tvLiveBanner.liveTalkUrl, ((Activity) context).getIntent());
				}
			});
		}else{
			front_btn_livetalk[brdIndex].setVisibility(View.GONE);
		}

		// 편성표 (생방송)
		if(tvLiveBanner.brodScheduleYn != null && "Y".equalsIgnoreCase(tvLiveBanner.brodScheduleYn)){
			front_btn_schedule[brdIndex].setText(tvLiveBanner.brodScheduleText);
			front_btn_schedule[brdIndex].setVisibility(View.VISIBLE);
		} else {
			front_btn_schedule[brdIndex].setVisibility(View.GONE);
		}

		// 바로구매 (생방송)
		if(tvLiveBanner.rightNowBuyYn != null && "Y".equalsIgnoreCase(tvLiveBanner.rightNowBuyYn)){
			// 적립금 text
			front_btn_pay[brdIndex].setVisibility(View.VISIBLE);
			front_btn_pay[brdIndex].setTag(DisplayUtils.getFullUrl(tvLiveBanner.rightNowBuyUrl));
			front_btn_pay[brdIndex].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString());
					EventBus.getDefault().post(new Events.FlexibleEvent.DirectBuyEvent(position, mInfo));
				}
			});
		} else {
			front_btn_pay[brdIndex].setVisibility(View.GONE);
		}

		if(tvLiveBanner != null && tvLiveBanner.btnInfo3 != null && !StringUtil.isEmpty(tvLiveBanner.btnInfo3.text) && !StringUtil.isEmpty(tvLiveBanner.btnInfo3.linkUrl)){
			// 적립금 text
			front_btn_pay[brdIndex].setVisibility(View.VISIBLE);
			front_btn_pay[brdIndex].setOnClickListener(new View.OnClickListener() {
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
			front_btn_pay[brdIndex].setText(tvLiveBanner.btnInfo3.text);
		}

		//에니메이션
		counter.setVisibility(View.GONE);
		if(tvLiveBanner.livePlay != null
				&& ((!TextUtils.isEmpty(tvLiveBanner.livePlay.livePlayUrl)
				&& "Y".equalsIgnoreCase(tvLiveBanner.livePlay.livePlayYN))
					|| (!TextUtils.isEmpty(tvLiveBanner.livePlay.videoid) && tvLiveBanner.livePlay.videoid.length() > 4))){
			front_play[brdIndex].setVisibility(View.VISIBLE);
			front_play[brdIndex].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					broadType = bType;

					//백그라운드뷰 세팅
					setBackArea(context, position, tvLiveBanner, action, label);

					layFront.setVisibility(View.GONE);
					//플레이 버튼 클릭시 와이즈로그 전송
					((HomeActivity) context).setWiseLog(
							brdIndex == 0 ? ServerUrls.WEB.TVSHOP_DUAL_LIVE_PLAY : ServerUrls.WEB.TVSHOP_DUAL_DATA_PLAY);
					confirmNetworkBilling((Activity)context);
				}
			});
		}else{
			//front_play.setVisibility(View.GONE);
			front_play[brdIndex].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					broadType = bType;

					//백그라운드뷰 세팅
					setBackArea(context, position, tvLiveBanner, action, label);

					//플레이 버튼 클릭시 와이즈로그 전송
					((HomeActivity) context).setWiseLog(
							brdIndex == 0 ? ServerUrls.WEB.TVSHOP_DUAL_LIVE_PLAY : ServerUrls.WEB.TVSHOP_DUAL_DATA_PLAY);
					WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveBanner.linkUrl), ((Activity) context).getIntent());
				}
			});
		}

		front_lay_goods[brdIndex].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				front_play[brdIndex].performClick();
			}
		});

		//전체상품보기
		front_product_more[brdIndex].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, tvLiveBanner.totalPrdViewLinkUrl);
			}
		});
	}

	/**
	 * 백그라운드영역(동영상재생영역) 세팅
	 *
	 * @param context 컨텍스트
	 * @param position 포지션
	 * @param tvLiveBanner 방송정보(생방송 or 데이타방송)
	 * @param action GTM용 액션값
	 * @param label GTM용 라벨값
	 */
	public void setBackArea(final Context context, final int position, final TvLiveBanner tvLiveBanner, final String action, final String label) {

		// 방송 스크린샷 이미지
		ImageUtil.loadImageTvLive(context.getApplicationContext(),
				!TextUtils.isEmpty(tvLiveBanner.imageUrl) ? tvLiveBanner.imageUrl.trim() : "", imageView, R.drawable.noimg_tv);

		layoutTvGoods.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				play.performClick();
			}
		});

		//가격 영역 클릭시 이동
		price_root.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(tvLiveBanner.linkUrl)) {
					WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveBanner.linkUrl), ((Activity) context).getIntent());
				}
			}
		});

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layoutTvGoods.performClick();
			}
		});

		// 제품 이름
		txt_title.setText(tvLiveBanner.productName);

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
			}else{
				txt_base_price.setVisibility(View.VISIBLE);
				txt_base_price_expose.setVisibility(View.VISIBLE);
			}
		}

		txt_base_price.setTextColor(Color.parseColor("#666666"));
		cardRantalText.setVisibility(View.GONE);

		for (int i=0; i<BADGE_NUM; i++) {
			badge[i].setImageResource(R.drawable.transparent);
		}
		price_info_layout.setGravity(Gravity.LEFT);
		price_info_layout.setVisibility(View.VISIBLE);

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

		//생방송
		if("LIVEPRICE".equalsIgnoreCase(tvLiveBanner.priceMarkUpType)){
			price_info.setText(tvLiveBanner.priceMarkUp.replaceAll("/n", "\n"));
			price_info.setGravity(Gravity.CENTER_HORIZONTAL);
			price_info.setPadding(0,context.getResources().getDimensionPixelSize(R.dimen.tv_live_gs_live_top_space),0,0);
			price_info.setBackgroundResource(R.drawable.tag_price_android);
			price_layout.setPadding(context.getResources().getDimensionPixelSize(R.dimen.tv_live_price_left_space),0,0,0);
		}else if("RATE".equalsIgnoreCase(tvLiveBanner.priceMarkUpType)){
			//AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_live_bigTextSize), true);
			//AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_live_smallTextSize), true);

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
				price_info_layout.setGravity(Gravity.CENTER);
			}else{
				price_info.setGravity(Gravity.CENTER);
			}

			price_info.setBackgroundResource(android.R.color.transparent);
			price_info.setText(priceStringBuilder);
			price_layout.setPadding(context.getResources().getDimensionPixelSize(R.dimen.tv_gs_price_left_space),0,0,0);

		}else{
			//가격이 없으면
			if("".equals(tvLiveBanner.salePrice) && !"".equals(tvLiveBanner.rentalEtcText)){
				price_info_layout.setVisibility(View.GONE);
				cardRantalText.setText(tvLiveBanner.rentalEtcText);
				cardRantalText.setVisibility(View.VISIBLE);
				cardRantalText.setGravity(Gravity.CENTER);
			}
		}

		// 편성표
		scheduleBtn.setTag(DisplayUtils.getFullUrl(tvLiveBanner.broadScheduleLinkUrl));
		scheduleBtn.setOnClickListener(new View.OnClickListener() {
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

		// 라이브톡 버튼 노출여부 (생방송)
		if("Y".equalsIgnoreCase(tvLiveBanner.liveTalkYn)){
			btn_live_talk.setVisibility(View.VISIBLE);
			live_talk_line.setVisibility(View.VISIBLE);
			live_talk_text.setText(tvLiveBanner.liveTalkText);
			btn_live_talk.setOnClickListener(new View.OnClickListener() {
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
			btn_pay.setOnClickListener(new View.OnClickListener() {
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
			btn_pay.setOnClickListener(new View.OnClickListener() {
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

		//JELLY_BEAN 이상 버전에서는 동영상플레이어 노출
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			playerController = (OnMediaPlayerController) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.media_player);
			playerController.setOnMediaPlayerListener(this);
			MediaInfo media = playerController.getMediaInfo();
			if(media != null && tvLiveBanner.livePlay != null){
				media.videoId = tvLiveBanner.livePlay.videoid;
				media.contentUri = tvLiveBanner.livePlay.livePlayUrl;
				media.title = tvLiveBanner.productName;
			}

			// TODO: 2016. 10. 6.
			// 해당 조건은 항상 true 이다. 그런데 고치지 않겠다 10/05 ( 문제가 되면 그떄 지우자 ) 이민수
			if(!isMdiaPlayerResize && playerController != null){
				isMdiaPlayerResize = true;
				DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), playerController.getPlayerView());
			}

			// TODO: 2016. 10. 6.
			// 해당 조건은 항상 true 이다. 그런데 고치지 않겠다 10/05 ( 문제가 되면 그떄 지우자 ) 이민수
			if(playerController != null && playerController.isPlaying()){
				root_view.setVisibility(View.GONE);
			}else{
				root_view.setVisibility(View.VISIBLE);
			}
		}

		//방송이 끝났을때 다음방송(livePlayYN)이 N 이면 동영상재생을 중지한다.
		if(tvLiveBanner.livePlay != null && !"".equals(tvLiveBanner.livePlay.livePlayUrl)
				&& "N".equalsIgnoreCase(tvLiveBanner.livePlay.livePlayYN)){
			EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(false, -1));
		}

		info_view.setVisibility(View.GONE);
		info_view_play.setVisibility(View.GONE);
		info_view_end.setVisibility(View.GONE);

		info_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tvLiveBanner.livePlay != null && "N".equals(tvLiveBanner.livePlay.livePlayYN)){
					layoutTvGoods.performClick();
				}
				info_view.setVisibility(View.GONE);
			}
		});

		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				info_view_play.setVisibility(View.GONE);
				//플레이 버튼 클릭시 와이즈로그 전송
				((HomeActivity) context).setWiseLog(
						broadType == BroadType.live ? ServerUrls.WEB.TVSHOP_LIVE_PLAY : ServerUrls.WEB.TVSHOP_DATA_PLAY);
				confirmNetworkBilling((Activity)context);
			}
		});

		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				info_view.setVisibility(View.GONE);
				info_view_play.setVisibility(View.GONE);
				EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(false, -1));
				layFront.setVisibility(View.VISIBLE);
			}
		});
		close_play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close.performClick();
			}
		});
		pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				info_view.setVisibility(View.GONE);
				info_view_play.setVisibility(View.VISIBLE);
				EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(false, -1));
			}
		});
		go_link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, DisplayUtils.getFullUrl(tvLiveBanner.linkUrl), ((Activity) context).getIntent());
				// GTM AB Test 클릭이벤트 전달
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
			}
		});

		full_screen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				info_view.setVisibility(View.GONE);
				info_view_play.setVisibility(View.VISIBLE);
				playVideo((Activity)context, buildVideoParam( broadType, tvLiveBanner.linkUrl, tvLiveBanner.livePlay.videoid, tvLiveBanner.livePlay.livePlayUrl));
			}
		});

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layoutTvGoods.performClick();
			}
		});
	}

	/**
	 * 동영상플레이 영역을 숨긴다.
	 * 딜레이를 주는 이유는 상품상세페이지 이동시 듀얼화면이 미리 노출되기 때문
	 */
	private void closeClickWithDelay() {
		new Handler().postDelayed(() -> close.performClick(), 500);
	}

	/**
	 * 생방송 실행.
	 *
	 * @param event event
	 */
	public void onEvent(Events.FlexibleEvent.TvLivePlayEvent event) {
		// auto play 설정 값은 무시한다. - 생방송 영역과 auto play 설정 영역은 서로 무관한 영역이에요 (임성남)
		if(!event.play){
			root_view.setVisibility(View.VISIBLE);
			info_view.setVisibility(View.GONE);
			if (info_view_end.getVisibility() != View.VISIBLE) {
				info_view_play.setVisibility(View.VISIBLE);
			}
		}
		if (playerController != null) {
			MediaInfo media = playerController.getMediaInfo();

			if (media != null) {
				media.isPlaying = event.play;
				playerController.setMediaInfo(media);
				if(media.isPlaying) {
					playerController.playPlayer();
				} else {
					playerController.stopPlayer();
				}
			} else {
				media = new MediaInfo();
				if (broadType == BroadType.data) {
					if (mInfo.dataLiveBanner.livePlay == null) {
						return;
					}
					media.videoId = mInfo.dataLiveBanner.livePlay.videoid;
					media.contentUri = mInfo.dataLiveBanner.livePlay.livePlayUrl;
					media.title = mInfo.dataLiveBanner.productName;
				} else {
					if (mInfo.tvLiveBanner.livePlay == null) {
						return;
					}
					media.videoId = mInfo.tvLiveBanner.livePlay.videoid;
					media.contentUri = mInfo.tvLiveBanner.livePlay.livePlayUrl;
					media.title = mInfo.tvLiveBanner.productName;
				}
				media.playerMode = MediaInfo.MODE_SIMPLE;
				media.currentPosition = 0;
				media.channel = MediaInfo.CHANNEL_MAIN_LIVE;
				media.isPlaying = event.play;
				media.lastPlaybackState = Player.STATE_IDLE;
				playerController.setMediaInfo(media);
				if(media.isPlaying) {
					playerController.playPlayer();
				} else {
					playerController.stopPlayer();
				}
			}
		}
	}

	public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
		Ln.i("registerSticky - unregister");
		EventBus.getDefault().unregister(this);
		root_view.setVisibility(View.GONE);
	}

	/**
	 * 뷰가 화면에 노출될때 발생
	 */
	@Override
	public void onViewAttachedToWindow() {

		// tv 쇼핑 위아래 스크롤을 하면 hardware codec 때문에 버퍼일이 발생
		// play를 중지하고 buffer를 클리어한 후에 생방송 실행.
        if(playerController != null) {
			MediaInfo info = playerController.getMediaInfo();
			if(info != null && info.isPlaying) {
				playerController.playPlayer();
			}
        }

		//생방송 남은시간 표시용 타이머를 시작한다.
		startTvLiveTimer();
	}

	/**
	 * 뷰가 화면에서 사라질때 발생
	 */
	@Override
	public void onViewDetachedFromWindow() {
		//생방송 남은시간 표시용 타이머를 정지한다.
		stopTvLiveTimer();
	}

	/**
	 * OnMediaPlayerListener Methods
	 */
	@Override
	public void onTap(boolean show) {

		if(info_view.getVisibility() == View.GONE){
			info_view.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onFullScreenClick(MediaInfo media) {

	}


	@Override
	public void onPlayed() {

	}

	@Override
	public void onPaused() {

	}

	@Override
	public void onFinished(MediaInfo media) {

	}

	@Override
	public void onError(Exception e) {
		info_view.setVisibility(View.GONE);
		info_view_play.setVisibility(View.VISIBLE);
	}

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
	 */
	protected void confirmNetworkBilling(final Activity activity) {

		NetworkUtils.confirmNetworkBillingAndShowPopup(activity, new NetworkUtils.OnConfirmNetworkListener() {
			@Override
			public void isConfirmed(boolean isConfirmed) {
				if(isConfirmed) {
					root_view.setVisibility(View.GONE);
					EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(true, 0));
				}
				else {
					info_view_play.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void inCanceled() {
				info_view_play.setVisibility(View.VISIBLE);
			}
		});
	}

	protected VideoParameters buildVideoParam(BroadType broadType, String productUrl, String videoId, String livePlayUrl) {
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
		if (mInfo != null && mInfo.tvLiveBanner != null) {
			layoutTvProgress[0].updateTvLiveTime(BroadType.live, mInfo.tvLiveBanner.broadStartTime,
					mInfo.tvLiveBanner.broadCloseTime);
		}
		if (mInfo != null && mInfo.dataLiveBanner != null) {
			layoutTvProgress[1].updateTvLiveTime(BroadType.data, mInfo.dataLiveBanner.broadStartTime,
					mInfo.dataLiveBanner.broadCloseTime);
		}
	}

	/**
	 * 생방송 남은시간 노출용 타이머를 정지한다.
	 */
	protected  void stopTvLiveTimer() {
		layoutTvProgress[0].stopTimer();
		layoutTvProgress[1].stopTimer();
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
}
