/*
 * Copyright(C) 2014 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import gsshop.mobile.v2.util.DisplayUtils;

/**
 * 정사각형 이미지에 대한 처리를  포함한 ImageView.
 * TV 생방송 화면에서만 사용.
 *
 */
public class TvScaledLocalImageView extends ImageView {

    private final Context mContext;

    public TvScaledLocalImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();
        if (d != null) {
            int width;
            int height;
            width = MeasureSpec.getSize(widthMeasureSpec);
            if (d.getIntrinsicWidth() == d.getIntrinsicHeight()) {
                // 정사각형 이미지일 경우 160dp로 고정하고 CENTER_INSIDE
                height = DisplayUtils.convertDpToPx(mContext, 160);
                setScaleType(ScaleType.CENTER_INSIDE);
            } else {
                height = (int) Math.ceil(width * (float) d.getIntrinsicHeight()
                        / d.getIntrinsicWidth());
                setScaleType(ScaleType.FIT_XY);
            }
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
