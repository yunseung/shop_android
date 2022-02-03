/**
 * 양쪽 미리보기 지원 view pager
 */
package gsshop.mobile.v2.support.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PreviewRecyclerViewPager extends RecyclerView {

    private int itemMargin = 0;

    public void setItemMargin(int itemMargin) {
        this.itemMargin = itemMargin;
    }


    /**
     * 페이지 번호
     */
    private OnPositionListener positionListener = null;

    public interface OnPositionListener {
        void onSelectedPosition(int position);
    }

    public void setOnPositionListener(OnPositionListener listener) {
        positionListener = listener;
    }

    public PreviewRecyclerViewPager(Context context) {
        super(context);
        init(context);
    }

    public PreviewRecyclerViewPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PreviewRecyclerViewPager(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        /*SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);*/
        this.addOnScrollListener(new OnScrollListener() {
            //Initialize this variable on class that initialize recyclerview
            private boolean hasStarted;
            private int lastPosition = -1;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                //Scroll is not start and user is dragging his finger
                if (!hasStarted && newState == SCROLL_STATE_DRAGGING) {
                    hasStarted = true;
                }

                //If scroll starts and it finish
                if (hasStarted && newState == SCROLL_STATE_IDLE) {
                    //Restart variable for next iteration
                    hasStarted = false;
                    //Do something
                    int position = ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    if (positionListener != null && position != lastPosition) {
                        lastPosition = position;
                        positionListener.onSelectedPosition(position);
                    }
                }
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (PreviewViewPagerAdapter.class.isInstance(adapter)) {
            PreviewViewPagerAdapter metalAdapter = (PreviewViewPagerAdapter) adapter;
            metalAdapter.setItemMargin(itemMargin);
            metalAdapter.updateDisplayMetrics();
        } else {
            throw new IllegalArgumentException("Only PreviewViewPagerAdapter is allowed here");
        }
        super.setAdapter(adapter);
    }

    public static abstract class PreviewViewPagerAdapter<VH extends PreviewViewPagerViewHolder> extends Adapter<VH> {

        private DisplayMetrics metrics;
        private int itemMargin;
        private int itemWidth;

        public PreviewViewPagerAdapter(@NonNull DisplayMetrics metrics) {
            this.metrics = metrics;
        }

        void setItemMargin(int itemMargin) {
            this.itemMargin = itemMargin;
        }

        void updateDisplayMetrics() {
            itemWidth = metrics.widthPixels - itemMargin * 2;
        }

        /*@Override
        public void onBindViewHolder(VH holder, int position) {
            int currentItemWidth = itemWidth;

            if (position == 0 || position == getItemCount() - 1) {
                holder.itemView.setPadding(0, 0, 0, 0);
            }

            *//**
             * item 갯수가 1이면 preview 제거.
             *//*
            if(getItemCount() == 1) {
                currentItemWidth = holder.itemView.getLayoutParams().width;
            }

            int height = holder.itemView.getLayoutParams().height;
            holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(currentItemWidth, height));
        }*/
    }

    public static abstract class PreviewViewPagerViewHolder extends ViewHolder {
        public PreviewViewPagerViewHolder(View itemView) {
            super(itemView);
        }
    }


}
