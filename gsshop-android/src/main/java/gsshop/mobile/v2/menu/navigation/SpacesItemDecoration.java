package gsshop.mobile.v2.menu.navigation;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;
    private final int leftMargin;
    private final int rightMargin;

    public SpacesItemDecoration(int leftMargin, int rightMargin, int space) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect.left = leftMargin;
            return;
        }

        if(parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            outRect.left = space;
            outRect.right = rightMargin;
            return;
        }

        outRect.left = space;
    }
}
