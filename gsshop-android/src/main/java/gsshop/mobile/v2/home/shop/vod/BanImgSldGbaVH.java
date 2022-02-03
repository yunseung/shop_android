/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.vod;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.SwipeUtils;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;


/**
 * 내일TV 핑퐁 이미지 슬라이드 배너
 *
 */
public class BanImgSldGbaVH extends BaseViewHolderV2 {

	/**
	 * 루트뷰 (접근성 설정 용도)
	 */
	private final LinearLayout mLlRootView;

	/**
	 * 공통 타이틀 배너
	 */
	private final CommonTitleLayout mCommonTitleLayout;

	/**
	 * 리사이킄러 뷰
	 */
	private final RecyclerView mRecyclerView;

	/**
	 * 어뎁터
	 */
	private final ProgramAdapter adapter;

	public BanImgSldGbaVH(View itemView) {
		this(itemView, null);
	}

	public BanImgSldGbaVH(View itemView, String navId) {
		super(itemView);

		//스와이프 허용각도 확대 적용
		increaseSwipeAngle = true;

		mLlRootView = itemView.findViewById(R.id.lay_root);
		mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);

		mRecyclerView = itemView.findViewById(R.id.recycler_popular);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
		adapter = new ProgramAdapter();
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				super.getItemOffsets(outRect, view, parent, state);
				//CardView에서 마진 4dp를 주었기 때문에 아이템간 간격은 8dp이고  처음과 마지막 아이템만 마진을 10dp로 늘려주기 위해 6dp 추가함
				int position = parent.getChildAdapterPosition(view);
				if (position == 0) {
					outRect.left = dp2px(8f);
				} else if (position == adapter.getItemCount() - 1) {
					outRect.right = dp2px(8f);
				}
			}
		});
		mRecyclerView.setOnTouchListener((v, event) -> {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					SwipeUtils.INSTANCE.disableSwipe();
					break;
			}
			return false;
		});
	}

	@Override
	public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
		SectionContentList item = info.contents.get(position).sectionContent;
		if (isEmpty(item)) {
			return;
		}

		mCommonTitleLayout.setCommonTitle(this, item);
		mLlRootView.setContentDescription(item.productName);
		adapter.updateProducts(item.subProductList);
	}

	private static class ProgramAdapter extends RecyclerView.Adapter<ProgramViewHolder> {

		private List<SectionContentList> mPrograms;

		public void updateProducts(List<SectionContentList> programs) {
			this.mPrograms = programs;
			notifyDataSetChanged();
		}

		@Override
		public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ProgramViewHolder(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.recycler_item_vod_img_sld_item, parent, false));
		}

		@Override
		public void onBindViewHolder(ProgramViewHolder holder, int position) {
			final SectionContentList program = mPrograms.get(position);
			ImageUtil.loadImageFit(holder.itemView.getContext(), program.imageUrl, holder.mScheduleImage, R.drawable.noimg_list);

			holder.mDateText.setText(program.promotionName);
			holder.mTitleText.setText(program.productName);
			holder.itemView.setOnClickListener(v -> WebUtils.goWeb(v.getContext(), program.linkUrl));

			holder.itemView.setOnTouchListener((view, motionEvent) -> {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					SwipeUtils.INSTANCE.disableSwipe();
				}
				return false;
			});
		}

		@Override
		public int getItemCount() {
			return isEmpty(mPrograms) ? 0 : mPrograms.size();
		}
	}

	private static class ProgramViewHolder extends RecyclerView.ViewHolder {

		public final ImageView mScheduleImage;
		public final TextView mDateText;
		public final TextView mTitleText;

		public ProgramViewHolder(View itemView) {
			super(itemView);
			mScheduleImage = itemView.findViewById(R.id.img_popular_schedule);
			mDateText = itemView.findViewById(R.id.txt_popular_schedule_date);
			mTitleText = itemView.findViewById(R.id.txt_popular_schedule_title);

			DisplayUtils.resizeWidthAtViewToScreenSize(itemView.getContext(), mScheduleImage);
			DisplayUtils.resizeHeightAtViewToScreenSize(itemView.getContext(), mScheduleImage);
		}
	}

}
