/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.publicusage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.GlobalTimer;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SOLD_OUT;
import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 기본형 단품 item
 *
 */
public class Prd1_640VH extends BaseViewHolderV2 {

	/**
	 * 컨텍스트
	 */
	private Context mContext;

	protected final LinearLayout rowGoods;
	private LinearLayout right_bottom_brd_time; //09.24(토) 9:20 방송 + 재생표시버튼
	private TextView txt_broad_time; //09.24(토) 9:20 방송
	private ImageView btn_hasvod; // 재생표시버튼
	protected final ImageView mainImg;
	protected final RelativeLayout main_layout;
	protected final ImageView top_badge; //왼쪽 위에 표시될 이미지
	protected final RenewalLayoutProductInfo layoutProductInfo;
	protected  boolean isAdult;
	//재생버튼 추가 hasVod

	private FlexibleShopAdapter mAdapter;
	private int position = -1;

	/**
	 * 하단 디바이더가 다음 뷰타입과 같으면 1dp 다르면 10dp
	 */
	private final View mViewBottomDivider1dp;
	private final View mViewBottomDivider10dp;

	private final TextView mTxtComment;

	/**
	 * 캐시할 이미지 주소
	 */
	private String mImgForCache;

	/**
	 * @param itemView
	 */
	public Prd1_640VH(View itemView) {
		super(itemView);

		rowGoods = (LinearLayout) itemView.findViewById(R.id.row_tv_goods);
		top_badge = (ImageView) itemView.findViewById(R.id.top_badge);
		main_layout  = (RelativeLayout) itemView.findViewById(R.id.main_layout);
		mainImg = (ImageView) itemView.findViewById(R.id.main_img);
		layoutProductInfo = (RenewalLayoutProductInfo) itemView.findViewById(R.id.layout_product_info);
		right_bottom_brd_time = itemView.findViewById(R.id.right_bottom_brd_time);
		txt_broad_time = itemView.findViewById(R.id.txt_broad_time);
		btn_hasvod = itemView.findViewById(R.id.btn_hasvod);

		mViewBottomDivider1dp = itemView.findViewById(R.id.view_bottom_divider_1dp);
		mViewBottomDivider10dp = itemView.findViewById(R.id.view_bottom_divider_10dp);


		if(main_layout != null) {
			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_layout);
		}
		if(mainImg != null) {
			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), mainImg);
		}

		mTxtComment = (TextView) itemView.findViewById(R.id.txt_comment);
	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {
		this.mContext = context;
		this.navigationId = info.naviId;

		final SectionContentList item = info.contents.get(position).sectionContent;
		boolean isSameToNext = false;
		if (info.contents.size() > position + 1) {
			final SectionContentList nextItem = info.contents.get(position + 1).sectionContent;
			if (nextItem != null && !TextUtils.isEmpty(nextItem.viewType) &&
					item != null &&  !TextUtils.isEmpty(item.viewType) &&
					item.viewType.equals(nextItem.viewType)) {
				isSameToNext = true;
			}
		}
		this.position = position;
		bindItem(context, item, action, label, isSameToNext);
	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList){
		super.onBindViewHolder(context, position, moduleList);

		this.mContext = context;

		final ModuleList item = moduleList.get(position);
//		final SectionContentList item = info.contents.get(position).sectionContent;

		boolean isSameToNext = false;
		if (moduleList.size() > position + 1) {
			final ModuleList nextItem = moduleList.get(position + 1);
			// 다음에 오는 아이템이 같을 경우에만 isSameToNext = true 였는데, PRD_1_640 이후에 PRD_VOD_LIST 가 오는 경우에도 true 한다.
			if ((nextItem != null && !TextUtils.isEmpty(nextItem.viewType) &&
					item != null &&  !TextUtils.isEmpty(item.viewType)) &&
					(item.viewType.equals(nextItem.viewType) || "PRD_VOD_LIST".equals(nextItem.viewType) ||
							"BAN_MORE_GBA".equals(nextItem.viewType) || "PRD_2".equals(nextItem.viewType))) {

				isSameToNext = true;
			}
		}
		this.position = position;

		if (item.isInPList && item.productList != null && item.productList.size() > 0) {
			bindItem(context, item.productList.get(0), null, null, isSameToNext);
		}
		else {
			bindItem(context, item, null, null, isSameToNext);
		}
	}

	public void bindItem(final Context context, final SectionContentList item, final String action, final String label){
		bindItem(context, item, action, label, false);
	}

	public void bindItem(final Context context, final SectionContentList item, final String action, final String label, boolean isSameToNext){
		mImgForCache = item.imageUrl;

		String adult = CookieUtils.getAdult(context);

		if ("true".equals(adult) || "temp".equals(adult)) {
			isAdult = true;
		}

		if (isSameToNext) {
			mViewBottomDivider1dp.setVisibility(View.VISIBLE);
			mViewBottomDivider10dp.setVisibility(View.GONE);
		}
		else {
			mViewBottomDivider1dp.setVisibility(View.GONE);
			mViewBottomDivider10dp.setVisibility(View.VISIBLE);
		}

		//가격표시용 공통모듈에 맞게 데이타 변경
		ProductInfo info = SetDtoUtil.setDto(item);

		// 해당 타입은 640 타입. 타입 정의 따로 해놓을까.
		layoutProductInfo.setViews(info, SetDtoUtil.BroadComponentType.product_640);

		top_badge.setVisibility(View.GONE);

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
				//2019_11_28 diskCacheStrategy(DiskCacheStrategy.NONE) 적용하면서 사용않함
				//item.imageUrl = ImageUtil.makeImageUrlWithSize(context,item.imageUrl);

				ImageUtil.loadImage(context, item.imageUrl, mainImg, R.drawable.noimg_tv);
			}
		}

		/**
		 * 왼쪽 상단 뱃지(URL만 처리함) 1개의 아이템만 표현
		 */
		if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()  && !isAdultViewVisible){
			final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
			if(badge != null && badge.imgUrl != null && !"".equals(badge.imgUrl)){
				//ImageUtil 뱃지의 경우 글라이드 기본룰을 따른 혹시라도 변경되게 되면 &_=1 같은 해쉬값으로 추가
				ImageUtil.loadImageBadge(context, badge.imgUrl, top_badge, R.drawable.transparent, HD);
				top_badge.setVisibility(View.VISIBLE);
			}
		}


		/**
		 * 방송편성시간
		 */
		if("".equals(item.broadTimeText) || item.broadTimeText == null){
			txt_broad_time.setVisibility(View.GONE);
		}else{
			right_bottom_brd_time.setVisibility(View.VISIBLE);
			txt_broad_time.setVisibility(View.VISIBLE);
			txt_broad_time.setText(item.broadTimeText);
		}

		/**
		 * 재생표시버튼
		 */
		//홈에서는 재생표시버튼 안뜨도록 요청 옴. 11/21
		item.hasVod = null;

		if(DisplayUtils.isTrue(item.hasVod)){
			right_bottom_brd_time.setVisibility(View.VISIBLE);
			btn_hasvod.setVisibility(View.VISIBLE);
			if("".equals(item.broadTimeText) || item.broadTimeText == null){
				//편성시간 없고 재생표시버튼만 있는경우 감싸고있는 배경색을 투명하게 + 재생버튼 크게
				right_bottom_brd_time.setBackgroundColor(Color.parseColor("#00ffffff"));
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);
				btn_hasvod.setLayoutParams(layoutParams);
			}
		}else{
			btn_hasvod.setVisibility(View.GONE);
		}

		/**
		 * 방송편성시간 + 재생표시버튼 한번에
		 */
		if(DisplayUtils.isValidString(item.broadTimeText) && DisplayUtils.isTrue(item.hasVod)){
			right_bottom_brd_time.setVisibility(View.VISIBLE);
			btn_hasvod.setVisibility(View.VISIBLE);
			txt_broad_time.setText(item.broadTimeText);
			txt_broad_time.setVisibility(View.VISIBLE);

		} else if (!DisplayUtils.isValidString(item.broadTimeText) && !DisplayUtils.isTrue(item.hasVod)) {
			right_bottom_brd_time.setVisibility(View.GONE);
		}

		// row 클릭하면 상품으로 이동
		rowGoods.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Keys.INTENT.IMAGE_URL, isNotEmpty(mImgForCache) ? mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO) : "");
				intent.putExtra(Keys.INTENT.HAS_VOD, item.hasVod);
				WebUtils.goWeb(context, item.linkUrl, intent);
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
			}
		});


		// 재생버튼 클릭시 상세화면 이동 (자동재생 플래그 추가)
		if (isNotEmpty(item.playUrl)) {
			btn_hasvod.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String linkUrl = StringUtils.addUriParameter(Uri.parse(item.playUrl),
							Keys.PARAM.VOD_PLAY, "VOD").toString();
					WebUtils.goWeb(context, linkUrl);
				}
			});
		}

		// 일시품절 처리
		if (SOLD_OUT.equalsIgnoreCase(item.imageLayerFlag)) {
			mTxtComment.setVisibility(View.VISIBLE);
			mTxtComment.setText(R.string.layer_flag_sold_out);
		} else {
			mTxtComment.setVisibility(View.GONE);
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
		GlobalTimer.getInstance().startTimer();
	}

	@Override
	public void onViewDetachedFromWindow() {
		super.onViewDetachedFromWindow();
		EventBus.getDefault().unregister(this);
	}


	//onEventMainThread가 없으면 onViewAttachedToWindow에서 에러발생, 확인예정
	public void onEventMainThread(Events.TimerEvent event) {
		if(position > -1 && mAdapter != null && mAdapter.getInfo() != null && mAdapter.getInfo().contents != null && position <= mAdapter.getInfo().contents.size()-1 && mAdapter.getInfo().contents.get(position) != null){
			SectionContentList item = mAdapter.getInfo().contents.get(position).sectionContent;
			if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()){
				final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
				//setTimeDeal(badge);
			}
		}
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
		if (DisplayUtils.isVisible(mainImg)) {
			if (isNotEmpty(mImgForCache) && mImgForCache.contains(IMG_CACHE_RPL_FROM)) {
				String imgUrl = mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO);
				Glide.with(mContext).load(trim(imgUrl)).downloadOnly(IMG_CACHE_WIDTH, IMG_CACHE_HEIGHT);
			}
		}
	}
}
