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
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.SwipeUtils;

/**
 * todays hot 배너.
 *
 */
public class SpcVH extends BaseViewHolder {

	private final View divider;
	private final RecyclerView list_recommend;

	private TextView program_text;

	private RecyclerView.ItemDecoration itemDecoration;

	/**
	 * @param itemView
	 */
	public SpcVH(View itemView) {
		super(itemView);

		//스와이프 허용각도 확대 적용
		increaseSwipeAngle = true;

		divider = itemView.findViewById(R.id.view_divider);
		program_text = (TextView)itemView.findViewById(R.id.program_text);
		list_recommend = (RecyclerView) itemView.findViewById(R.id.list_recommend);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), list_recommend);
		// resize
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), divider);

	}

	// .
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {


		// 테두리선.
//		int imageHeight = context.getResources()
//				.getDimensionPixelSize(R.dimen.best_deal_no1_deal_image_height);
		final ShopItem content = info.contents.get(position);
		ArrayList<SectionContentList> list = content.sectionContent.subProductList;

		if(list == null){
			return;
		}
		program_text.setText(content.sectionContent.productName);
		if(itemDecoration == null){
			//아이템 간격 세팅
			itemDecoration = new SpacesItemDecoration(DisplayUtils.convertDpToPx(context, (float) 16.87));
			list_recommend.addItemDecoration(itemDecoration);
		}

		list_recommend.setHasFixedSize(true);

		LinearLayoutManager llm = new LinearLayoutManager(context);
		llm.setOrientation(LinearLayoutManager.HORIZONTAL);

		list_recommend.setLayoutManager(llm);
		FlexibleProgramBannerAdapter adapter = new FlexibleProgramBannerAdapter(context, list, action,label);
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
