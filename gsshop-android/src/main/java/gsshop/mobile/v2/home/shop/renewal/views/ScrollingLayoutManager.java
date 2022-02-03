package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView에 자동 롤링 추가
 */
public class ScrollingLayoutManager extends LinearLayoutManager {
    private int duration;

    public ScrollingLayoutManager(Context context, int orientation, boolean reverseLayout, int duration) {
        super(context, orientation, reverseLayout);
        this.duration = duration;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        View firstVisibleChild = recyclerView.getChildAt(0);
        int itemWidth = firstVisibleChild.getWidth();
        int currentPosition = recyclerView.getChildLayoutPosition(firstVisibleChild);
        int distanceInPixels = Math.abs((currentPosition - position) * itemWidth);
        if (distanceInPixels == 0) {
            distanceInPixels = (int) Math.abs(firstVisibleChild.getY());
        }

        int currnetDuration = Math.abs((currentPosition - position));

        SmoothScroller smoothScroller = new SmoothScroller(recyclerView.getContext(), distanceInPixels, currnetDuration * 1000);
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private class SmoothScroller extends LinearSmoothScroller {
        private final float distanceInPixels;
        private final float duration;

        public SmoothScroller(Context context, int distanceInPixels, int duration) {
            super(context);
            this.distanceInPixels = distanceInPixels;
            this.duration = duration;
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return ScrollingLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            float proportion = (float) dx / distanceInPixels;
            return (int) (duration * proportion);
        }
    }
}