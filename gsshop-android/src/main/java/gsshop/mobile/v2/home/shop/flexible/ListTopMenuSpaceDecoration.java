package gsshop.mobile.v2.home.shop.flexible;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by subin on 2015-10-29.
 */
public class ListTopMenuSpaceDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public ListTopMenuSpaceDecoration(int space) {
        this.space = space;
    }

    /**
     * 0번째 리스트 아이템에 top offset을 적용한다.
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
        }
    }
}
