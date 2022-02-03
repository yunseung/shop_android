/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 
 *
 */
public class DividerItemDecoration extends ItemDecoration {
	private final int mStaggeredInnerSpace;
	private final int mStaggeredOuterSpace;
	private final int mWideInnerSpace;
	private final int mWideOuterSpace;

	public DividerItemDecoration(int staggeredInner, int staggeredOuter, int wideInner,
			int wideOuter) {
		super();
		this.mStaggeredInnerSpace = staggeredInner;
		this.mStaggeredOuterSpace = staggeredOuter;
		this.mWideInnerSpace = wideInner;
		this.mWideOuterSpace = wideOuter;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
		super.getItemOffsets(outRect, view, parent, state);
		StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view
				.getLayoutParams();
		StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent
				.getLayoutManager();
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom;

		if (!params.isFullSpan()) {
			final int count = parent.getAdapter().getItemCount();
			final int position = parent.getChildAdapterPosition(view);
			if (layoutManager.getSpanCount() == 2) {
				left = params.getSpanIndex() == 0 ? mStaggeredOuterSpace
						: mStaggeredInnerSpace / 2;
				right = params.getSpanIndex() == 1 ? mStaggeredOuterSpace
						: mStaggeredInnerSpace / 2;
				bottom = position < count - 2 ? mStaggeredInnerSpace
						: mStaggeredOuterSpace;

			} else {
				bottom = position < count - 2 ? mStaggeredInnerSpace
						: mStaggeredOuterSpace;
			}
			outRect.set(0, 0, 0, bottom);
			// padding
			view.setPadding(left, top, right, 0);
		}
	}

}
