/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.SwipeUtils;
import roboguice.util.Ln;

/**
 * todays hot 배너.
 *
 */
public class TabSlVH extends BaseViewHolder {

	private final RecyclerView list_recommend;

	private ImageView background_img;

	private ImageView group_1;
	private ImageView group_2;
	private ImageView group_3;

	private ImageView arrow;
	private RecyclerView.ItemDecoration itemDecoration;

	private FlexibleRecommendAdapter adapter;

	private LinearLayout layout_group_3;

	private HashMap<String, String> imageBackgroundMap;

	private int defaultGroupLocation = 0;

	/**
	 * @param itemView
	 */
	public TabSlVH(View itemView) {
		super(itemView);

		//스와이프 허용각도 확대 적용
		increaseSwipeAngle = true;

//		divider = itemView.findViewById(R.id.view_divider);
		list_recommend = (RecyclerView) itemView.findViewById(R.id.list_recommend);

		background_img = (ImageView) itemView.findViewById(R.id.background_img);
		group_1 = (ImageView)itemView.findViewById(R.id.group_1);
		group_2 = (ImageView)itemView.findViewById(R.id.group_2);
		group_3 = (ImageView)itemView.findViewById(R.id.group_3);
		layout_group_3 = (LinearLayout)itemView.findViewById(R.id.layout_group_3);

		arrow = (ImageView)itemView.findViewById(R.id.arrow);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), list_recommend);

		imageBackgroundMap = new HashMap<String, String>();
		// resize
//		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), divider);

	}

	// .
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {


		// 테두리선.
//		int imageHeight = context.getResources()
//				.getDimensionPixelSize(R.dimen.best_deal_no1_deal_image_height);
		final ShopItem content = info.contents.get(position);
		final ArrayList<SectionContentList> list = content.sectionContent.subProductList;

		//get(0) 에 대한 방어로직
		if(list != null && !list.isEmpty()){
			//이미지 로드시 깜박거림을 방지하기 위해 미리 이미지 로드
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(SectionContentList item : list){
						if(item.imageList != null && item.imageList.bg != null){
							imageBackgroundMap.put(item.imageList.bg, ImageUtil.loadImageCacheUrl(context, item.imageList.bg));
						}
					}
				}
			}).start();
		}else{
			return;
		}

		try {
			if (!list.isEmpty()) {
				ImageUtil.loadImageResize(context, list.get(0).imageList.on, group_1, R.drawable.transparent);
			}
			if (list.size() > 1) {
				ImageUtil.loadImageResize(context, list.get(1).imageList.off, group_2, R.drawable.transparent);
			}
			if (list.size() > 2) {
				ImageUtil.loadImageResize(context, list.get(2).imageList.off, group_3, R.drawable.transparent);
			} else {
				layout_group_3.setVisibility(View.GONE);
			}

			decorationImage(context, list, 0);

			if (itemDecoration == null) {
				//아이템 간격 세팅
				itemDecoration = new SpacesItemDecoration(DisplayUtils.convertDpToPx(context, (float) 16.87));
				list_recommend.addItemDecoration(itemDecoration);
			}

			group_1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					decorationImage(context, list, 0);
					startArrowAnimation(group_1);

					adapter.setCategory("0");
					adapter.setItem(list.get(0).subProductList);
					adapter.notifyDataSetChanged();
					((LinearLayoutManager) list_recommend.getLayoutManager()).scrollToPosition(0);

					//GTM 클릭이벤트 전달
					String tempLabel = String.format("%s_%s_%s", label, "0", list.get(0).productName);
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
				}
			});
			group_2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					decorationImage(context, list, 1);
					startArrowAnimation(group_2);

					adapter.setCategory("1");
					adapter.setItem(list.get(1).subProductList);
					adapter.notifyDataSetChanged();
					((LinearLayoutManager) list_recommend.getLayoutManager()).scrollToPosition(0);

					//GTM 클릭이벤트 전달
					String tempLabel = String.format("%s_%s_%s", label, "1", list.get(1).productName);
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
				}
			});
			group_3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					decorationImage(context, list, 2);
					startArrowAnimation(group_3);

					adapter.setCategory("2");
					adapter.setItem(list.get(2).subProductList);
					adapter.notifyDataSetChanged();
					((LinearLayoutManager) list_recommend.getLayoutManager()).scrollToPosition(0);

					//GTM 클릭이벤트 전달
					String tempLabel = String.format("%s_%s_%s", label, "2", list.get(2).productName);
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
				}
			});

			if (defaultGroupLocation > 0) {
				ViewHelper.setTranslationX(arrow, defaultGroupLocation);
			}

			ViewTreeObserver viewTreeObserver = group_1.getViewTreeObserver();
			viewTreeObserver
					.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							int[] location = new int[2];
							group_1.getLocationOnScreen(location);
							if (defaultGroupLocation == 0) {
								defaultGroupLocation = location[0] + (group_1.getWidth() / 2) - (arrow.getWidth() / 2);
							}
							ViewHelper.setTranslationX(arrow, defaultGroupLocation);
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
								group_1.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							else
								group_1.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
						}
					});

			list_recommend.setHasFixedSize(true);

			LinearLayoutManager llm = new LinearLayoutManager(context);
			llm.setOrientation(LinearLayoutManager.HORIZONTAL);

			list_recommend.setLayoutManager(llm);
			adapter = new FlexibleRecommendAdapter(context, list.get(0).subProductList, action, label);
			list_recommend.setAdapter(adapter);

			list_recommend.setOnTouchListener(new View.OnTouchListener() {


				@Override
				public boolean onTouch(View v, MotionEvent event) {


					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
						case MotionEvent.ACTION_MOVE:
							SwipeUtils.INSTANCE.disableSwipe();
							break;
					}
					return false;
				}
			});
		}catch (Exception e){
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}
	}

	private Drawable getCacheImage(Context context, String pathName){
		Drawable drawable;
		if (pathName.length() > 0) {
			return Drawable.createFromPath(pathName);
		}

		return null;
	}

	private void startArrowAnimation(ImageView GroupView){
		int[] location = new int[2];
		GroupView.getLocationOnScreen(location);
		ObjectAnimator layout_search_barAnim = ObjectAnimator
				.ofFloat(arrow, "translationX", ViewHelper.getTranslationX(arrow),
						location[0] + (GroupView.getWidth() / 2) - (arrow.getWidth() / 2));
		layout_search_barAnim.setDuration(300);
		layout_search_barAnim.start();
	}

	private void decorationImage(Context context, ArrayList<SectionContentList> list, int selectedIndex){

		try {
			if (imageBackgroundMap != null && !imageBackgroundMap.isEmpty() && imageBackgroundMap.get(list.get(selectedIndex).imageList.bg) != null) {
				background_img.setImageDrawable(getCacheImage(context, imageBackgroundMap.get(list.get(selectedIndex).imageList.bg)));
			} else {
				ImageUtil.loadImage(context, list.get(selectedIndex).imageList.bg, background_img, R.drawable.transparent);
			}
		}catch(Exception e){
			ImageUtil.loadImage(context, list.get(selectedIndex).imageList.bg, background_img, R.drawable.transparent);
		}

		if(!list.isEmpty() ) {

			ImageUtil.loadImage(context, list.get(0).imageList.off, group_1, R.drawable.transparent);

			if (list.size() > 1) {
				ImageUtil.loadImage(context, list.get(1).imageList.off, group_2, R.drawable.transparent);
			}
			if (list.size() > 2) {
				ImageUtil.loadImage(context, list.get(2).imageList.off, group_3, R.drawable.transparent);
			} else {
				layout_group_3.setVisibility(View.GONE);
			}
			switch (selectedIndex) {
				case 0:
					ImageUtil.loadImage(context, list.get(0).imageList.on, group_1, R.drawable.transparent);
					break;
				case 1:
					ImageUtil.loadImage(context, list.get(1).imageList.on, group_2, R.drawable.transparent);
					break;
				case 2:
					ImageUtil.loadImage(context, list.get(2).imageList.on, group_3, R.drawable.transparent);
					break;
			}

		}

	}

	/**
	 * 아이템간격을 데코레이션 한다.
	 *
	 *
	 */
	public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
		private final int space;

		public SpacesItemDecoration(int space) {
			this.space = space;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			if(parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
				outRect.right = space;
			}

			outRect.left = space;
		}
	}

}
