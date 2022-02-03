/**
 * indicator 그룹 표시.
 * 2개 이상부터 보여짐.
 */
package gsshop.mobile.v2.support.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class IndicatorRecyclerView extends RecyclerView {

    /**
     * indicator 갯수
     */
    private int indicatorSize = 10;
    /**
     * indicator layout id
     */
    private int indicatorLayoutId;
    /**
     * indicator view id
     */
    private int indicatorId;
    /**
     * selected indicator position
     */
    private int selectedPosition = 0;

    public IndicatorRecyclerView(Context context) {
        super(context);
    }

    public IndicatorRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IndicatorRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setIndicator(@NonNull int indicatorLayoutId, @NonNull int indicatorId, @NonNull int indicatorSize) {
        this.indicatorLayoutId = indicatorLayoutId;
        this.indicatorId = indicatorId;
        this.indicatorSize = indicatorSize;
        /**
         * indicator는 2 이상부터 표시.
         */
        if (indicatorSize < 2) {
            return;
        }
        setAdapter(new IndicatorAdapter());
    }

    public void setSelectedIndicator(int position) {
        /**
         * indicator는 2 이상부터 표시.
         */
        if (indicatorSize < 2) {
            return;
        }

        this.selectedPosition = position;
        getAdapter().notifyDataSetChanged();
    }

    private class IndicatorAdapter extends Adapter<IndicatorViewHolder> {

        @Override
        public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(indicatorLayoutId, parent, false);
            return new IndicatorViewHolder(viewItem, indicatorId);
        }

        @Override
        public void onBindViewHolder(IndicatorViewHolder holder, int position) {
            holder.indicatorRadio.setSelected(position == selectedPosition);
        }

        @Override
        public int getItemCount() {
            return indicatorSize;
        }
    }

    private static class IndicatorViewHolder extends ViewHolder {
        public final Button indicatorRadio;

        public IndicatorViewHolder(View itemView, int id) {
            super(itemView);
            indicatorRadio = (Button) itemView.findViewById(id);
        }
    }


}
