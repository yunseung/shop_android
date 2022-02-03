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
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfo;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SOLD_OUT;
import static gsshop.mobile.v2.util.StringUtils.trim;


/**
 * 정사각 상품 배너
 *
 */
public class Prd1_550VH extends BaseViewHolderV2 {

	/**
	 * 컨텍스트
	 */
	private Context mContext;

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
	 * 성인상품 여부
	 */
	private  boolean isAdult;


	private LinearLayout mRightBrdTime; //09.24(토) 9:20 방송 레이아웃
	private TextView mTxtBrdTime; //09.24(토) 9:20 방송

	/**
	 * 매진 영역이 변경됨.
	 */
	private TextView mTxtComment; // 일시 품절 영역

	/**
	 * 상품 정보 영역
	 */
	private RenewalLayoutProductInfo mLayoutProductInfo;

	/**
	 * 캐시할 이미지 주소
	 */
	private String mImgForCache;

	/**
	 * @param itemView
	 */
	public Prd1_550VH(View itemView) {
		super(itemView);

		rowGoods = itemView.findViewById(R.id.row_goods);
		main_layout  = itemView.findViewById(R.id.main_layout);
		mainImg = itemView.findViewById(R.id.main_img);
		mRightBrdTime = itemView.findViewById(R.id.right_bottom_brd_time);
		mTxtBrdTime = itemView.findViewById(R.id.txt_broad_time);
		mLayoutProductInfo = (RenewalLayoutProductInfo) itemView.findViewById(R.id.layout_product_info);
		mTxtComment = (TextView) itemView.findViewById(R.id.txt_comment);

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
		this.mContext = context;

		mImgForCache = item.imageUrl;

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

		/**
		 * 방송편성시간
		 */
		if(item.broadTimeText == null || TextUtils.isEmpty(item.broadTimeText.trim())){
			mRightBrdTime.setVisibility(View.GONE);
		}else{
			mRightBrdTime.setVisibility(View.VISIBLE);
			mTxtBrdTime.setText(item.broadTimeText);
		}

		//가격표시용 공통모듈에 맞게 데이타 변경
		ProductInfo info = SetDtoUtil.setDto(item);

		// 640 타입 set
		mLayoutProductInfo.setViews(info, SetDtoUtil.BroadComponentType.product_640);

		// row 클릭하면 상품으로 이동
		rowGoods.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Keys.INTENT.IMAGE_URL, item.imageUrl);
				intent.putExtra(Keys.INTENT.HAS_VOD, item.hasVod);
				WebUtils.goWeb(context, item.linkUrl, intent);
			}
		});

		if (mTxtComment!= null) {
			// 일시품절 처리
			if (SOLD_OUT.equalsIgnoreCase(item.imageLayerFlag)) {
				mTxtComment.setVisibility(View.VISIBLE);
				mTxtComment.setText(R.string.layer_flag_sold_out);
			} else {
				mTxtComment.setVisibility(View.GONE);
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
		if (DisplayUtils.isVisible(mainImg) && isNotEmpty(mImgForCache)) {
			String imgUrl = mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO);
			Glide.with(mContext).load(trim(imgUrl)).downloadOnly(IMG_CACHE_WIDTH, IMG_CACHE_HEIGHT);
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
