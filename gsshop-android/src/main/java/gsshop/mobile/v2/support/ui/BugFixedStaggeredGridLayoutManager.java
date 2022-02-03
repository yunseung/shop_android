/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.ui;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.nshmura.snappysmoothscroller.SnapType;
import com.nshmura.snappysmoothscroller.SnappyStaggeredGridLayoutManager;

import roboguice.util.Ln;

/**
 * {@link StaggeredGridLayoutManager}에서 발생하는 Java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
 * 버그 무시.
 */
public class BugFixedStaggeredGridLayoutManager extends SnappyStaggeredGridLayoutManager {

    //스크롤중인 상태에서 리사이클뷰 포지션 이동이 필요한 경우
    //false로 세팅 -> 포시션 이동 -> true로 세팅
    private boolean isScrollEnabled = true;

    public BugFixedStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    public boolean getScrollEnabled() {
        return this.isScrollEnabled;
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            Ln.e("meet a IOOBE in RecyclerView");
        }
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }

    private static final int JUMP_POSITION = 6;
    public void smoothScrollToPosition(RecyclerView recyclerView, int position) {
        View topView = recyclerView.getChildAt(0);
        if (topView != null) {
            int oldPosition = recyclerView.getChildAdapterPosition(topView);
            if (Math.abs(oldPosition - position) > JUMP_POSITION) {
                ((BugFixedStaggeredGridLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(position + ((oldPosition > position) ? JUMP_POSITION : -JUMP_POSITION), 0);
            }
        }
        setSnapType(SnapType.START);
        super.smoothScrollToPosition(recyclerView, null, position);
    }
}
