package gsshop.mobile.v2.home.shop.tvshoping;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

public class TvLivePagerAdapter extends PagerAdapter {
	private final Context mActivity;
	private final int mPageCount;

	private final GestureDetector gestureDetector;

	private final ArrayList<SectionContentList> liveItem;

	private final String action;
	private final String label;

	public TvLivePagerAdapter(Context activity,String action,String label, ArrayList<SectionContentList> list) {
		mActivity = activity;
		this.action = action;
		this.label = label;
		mPageCount = list.size();
		liveItem = list;
		gestureDetector = new GestureDetector(activity, new SingleTapConfirm());
	}

	@Override
	public int getCount() {
		return mPageCount;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return (view ==(View)obj);
	}

	@Override
	public Object instantiateItem(ViewGroup container,final int position) {
		ViewGroup viewGroup;


		viewGroup = (ViewGroup) ((Activity) mActivity).getLayoutInflater().inflate(
				R.layout.tv_live_item_view, null);

//		LinearLayout product_layout = (LinearLayout)viewGroup.findViewById(R.id.product_layout);
//		DisplayUtils.resizeHeightAtViewToScreenPageWidth(MainApplication.getAppContext(), product_layout);

		ImageView product_img = (ImageView)viewGroup.findViewById(R.id.product_img);
//		ImageUtil.loadImageFit(context, item.imageUrl, mainImage, R.drawable.noimg_logo);
//		product_img.setEnabled(false);
//		product_img.setDefaultImageResId(R.drawable.noimg_tv);
//		product_img.setImageUrl(, MainApplication.mImageLoader);
//		product_img.setTag(liveItem.get(position).prdUrl);

		View broadScheduleLink = viewGroup.findViewById(R.id.broadScheduleLink);

		if(position == mPageCount-1){
			broadScheduleLink.setVisibility(View.VISIBLE);
		}else{
			broadScheduleLink.setVisibility(View.GONE);
		}
		
//		viewGroup.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (gestureDetector.onTouchEvent(event)) {
//					WebUtils.goWeb(mActivity, liveItem.get(position).prdUrl);
//					//GTM 이벤트 전달용 로직 공통이 될려나 ? by leems 20150804
//					String action = GTMEnum.GTM_NONE;
//					String label = GTMEnum.GTM_NONE;						
//					if( categoryName != null && tabName != null)
//					{
//						//내가 누구인지 알수가 없는 상태입니다.
//						//후에 로직이 변경되서 .live .data 변경하게 되면 생방송 / 데이터 홈쇼핑 변경되어야 한다. 
//						action = String.format("%s_%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,categoryName,tabName,GTMEnum.GTM_ACTION_TVTAB_NEXTV_TAIL);
//						label = String.format("%s_%s",Integer.toString(position), liveItem.get(position).productName);
//								
//					}
//					GTMAction.sendEvent(mActivity, GTMEnum.GTM_AREA_CATEGORY, action, label);		
//				}
//				return false;
//			}
//		});
		
		viewGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					WebUtils.goWeb(mActivity, liveItem.get(position).linkUrl);
					//GTM 이벤트 전달용 로직 공통이 될려나 ? by leems 20150804
					//String action = GTMEnum.GTM_NONE;
					String tempLabel = label;
					if( action != null && tempLabel != null)
					{
						//내가 누구인지 알수가 없는 상태입니다.
						//후에 로직이 변경되서 .live .data 변경하게 되면 생방송 / 데이터 홈쇼핑 변경되어야 한다. 
						//action = String.format("%s_%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,categoryName,tabName,GTMEnum.GTM_ACTION_TVTAB_NEXTV_TAIL);
						tempLabel = String.format("%s_%s_%s",label,Integer.toString(position), liveItem.get(position).productName);
								
					}
					GTMAction.sendEvent(mActivity, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
			}
		});

		broadScheduleLink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WebUtils.goWeb(mActivity, liveItem.get(position).broadScheduleLinkUrl);

				//GTM 이벤트 전달용 로직 공통이 될려나 ? by leems 20150804
				String tempLabel = label;
				if( action != null && tempLabel != null)
				{
					//내가 누구인지 알수가 없는 상태입니다.
					//후에 로직이 변경되서 .live .data 변경하게 되면 생방송 / 데이터 홈쇼핑 변경되어야 한다.
					//action = String.format("%s_%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,categoryName,tabName,GTMEnum.GTM_ACTION_TVTAB_NEXTV_TAIL);
					tempLabel = String.format("%s_%s_%s",label,Integer.toString(position),liveItem.get(position).broadScheduleLinkUrl);

					
				}
				GTMAction.sendEvent(mActivity, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
			}
		});

		TextView liveBenefits = (TextView)viewGroup.findViewById(R.id.liveBenefits);

		if(liveItem.get(position).liveBenefitsYn != null && "Y".equalsIgnoreCase(liveItem.get(position).liveBenefitsYn)){
			liveBenefits.setText(liveItem.get(position).liveBenefitsText);
			liveBenefits.setTextColor(mActivity.getResources().getColor(R.color.benefit_red));
			liveBenefits.setVisibility(View.VISIBLE);
		}else if(liveItem.get(position).liveBenefitsYn != null && "B".equalsIgnoreCase(liveItem.get(position).liveBenefitsYn)){
			liveBenefits.setText(liveItem.get(position).liveBenefitsText);
			liveBenefits.setTextColor(mActivity.getResources().getColor(R.color.benefit_blue));
			liveBenefits.setVisibility(View.VISIBLE);
		}else{
			liveBenefits.setVisibility(View.GONE);
		}

		View tag_container;
		ImageView[] tagBadge = new ImageView[3];

		tag_container = viewGroup.findViewById(R.id.tag_container);
		tagBadge[0] = (ImageView) viewGroup.findViewById(R.id.tag_save);
		tagBadge[1] = (ImageView) viewGroup.findViewById(R.id.tag_cash);
		tagBadge[2] = (ImageView) viewGroup.findViewById(R.id.tag_gift);

		TextView text_air_buy = (TextView)viewGroup.findViewById(R.id.text_air_buy);
		if(liveItem.get(position).imageLayerFlag != null && "AIR_BUY".equalsIgnoreCase(liveItem.get(position).imageLayerFlag)){
			text_air_buy.setText(R.string.layer_flag_air_buy);
			ViewUtils.showViews(text_air_buy);
		}else if(liveItem.get(position).imageLayerFlag != null && "SOLD_OUT".equalsIgnoreCase(liveItem.get(position).imageLayerFlag)){
			text_air_buy.setText(R.string.layer_flag_sold_out);
			ViewUtils.showViews(text_air_buy);
		}else{
			ViewUtils.hideViews(text_air_buy);
		}

		if (liveItem.get(position).rwImgList != null && !liveItem.get(position).rwImgList.isEmpty()){
			for(int i=0; i< liveItem.get(position).rwImgList.size() ; i++) {
				//3개를 초과하는 뱃지는 무시
				if (i >= tagBadge.length) {
					break;
				}
				//TODO 확인필요(테블릿에서 상하 스크롤시 resource.getIntrinsicWidth()값이 작아져서 이미지도 작아짐)
//				//ImageUtil.loadImageBadge(context, data.product.rwImgList.get(i),badge[i] , 0, QHD);
//				ImageUtil.loadImageFit(context, item.rwImgList.get(i),badge[i] , 0);

				tagBadge[i].layout(0, 0, 0, 0);
				ImageUtil.loadImage(mActivity, liveItem.get(position).rwImgList.get(i),tagBadge[i] , 0);

				tagBadge[i].setVisibility(View.VISIBLE);
			}
			tag_container.setVisibility(View.VISIBLE);
		}else{
			tag_container.setVisibility(View.INVISIBLE);
		}

		TextView broadType = (TextView)viewGroup.findViewById(R.id.broadType);
		broadType.setText(liveItem.get(position).broadType);

		TextView broadTime = (TextView)viewGroup.findViewById(R.id.broadTime);

		String startTime = liveItem.get(position).broadStartTime.substring(8, 12);
		String endTime = liveItem.get(position).broadCloseTime.substring(8, 12);

		StringBuilder startTimeBuilder = new StringBuilder(startTime);
		StringBuilder endTimeBuilder = new StringBuilder(endTime);

		startTimeBuilder.insert(2, ":" );
		endTimeBuilder.insert(2, ":" );

		broadTime.setText(startTimeBuilder + " ~ " + endTimeBuilder);


		TextView txt_title = (TextView)viewGroup.findViewById(R.id.txt_title);
		txt_title.setText(liveItem.get(position).productName);

		TextView txt_price = (TextView)viewGroup.findViewById(R.id.txt_price);
		TextView txt_price_expose = (TextView)viewGroup.findViewById(R.id.txt_price_expose);
		TextView txt_base_price = (TextView)viewGroup.findViewById(R.id.txt_base_price);
		TextView txt_base_price_unit = (TextView)viewGroup.findViewById(R.id.txt_base_price_unit);

		txt_base_price.setVisibility(View.GONE);
		txt_base_price_unit.setVisibility(View.GONE);

		txt_price.setText(liveItem.get(position).salePrice);
		txt_price_expose.setText(liveItem.get(position).exposePriceText);

		if (isNeededConsultationCall(liveItem.get(position))) {
			// 핸드폰이나 렌탈 상품이면 "상담전용 상품입니다"
			txt_price.setText(R.string.home_tvlive_prd_rental);

			// font size 14sp, not bold, color #636363, layout top margin 3dp
			txt_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			txt_price.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
			txt_price.setTextColor(Color.parseColor("#636363"));
			txt_price.setPadding(0, mActivity.getResources().getDimensionPixelSize(R.dimen.badge_text_padding),0,0);
			txt_price_expose.setVisibility(View.GONE);
		} else {
			// 아니면 가격 보이게
			if (DisplayUtils.isValidNumberString(liveItem.get(position).salePrice)) {
				txt_price.setText(liveItem.get(position).salePrice );
				txt_price_expose.setVisibility(View.VISIBLE);

				if (liveItem.get(position).basePrice != null && !"".equals(liveItem.get(position).basePrice)
						&& !"0".equals(liveItem.get(position).basePrice)) {
					txt_base_price.setText(liveItem.get(position).basePrice);
					txt_base_price_unit.setText(liveItem.get(position).exposePriceText);
					txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					txt_base_price.setVisibility(View.VISIBLE);
					txt_base_price_unit.setVisibility(View.VISIBLE);
//			txt_base_price.setPadding(0, mActivity.getResources().getDimensionPixelSize(R.dimen.badge_text_discount_double_padding), 0, 0);
				}
			} else {
				// 가격이 숫자가 아니거나 0이면 아무 표시하지 않음
				txt_price.setVisibility(View.INVISIBLE);
				txt_price_expose.setVisibility(View.GONE);
			}
		}

		ImageUtil.loadImageFit(mActivity, liveItem.get(position).imageUrl, product_img, R.drawable.noimg_tv);
		container.addView(viewGroup);
		return viewGroup;
	}

	@Override
	public void destroyItem(ViewGroup collection, int position, Object view) {
		//must be overridden else throws exception as not overridden.
		collection.removeView((View) view);
	}

	@Override public float getPageWidth(int position) { 
		if(position == mPageCount - 1){
			return 1;
		}
		return(0.8f); 
	}

	//	@Override
	//	public float getPageWidth(int position) {
	//		return 0.8f;
	//	}

	private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {
			return true;
		}
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return true;
		}
	}

	private boolean isNeededConsultationCall(SectionContentList tvLiveBanner) {
		return DisplayUtils.isTrue(tvLiveBanner.isCellPhone)
				|| DisplayUtils.isTrue(tvLiveBanner.isRental);
	}
}
