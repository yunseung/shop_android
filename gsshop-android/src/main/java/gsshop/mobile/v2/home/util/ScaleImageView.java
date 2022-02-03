/*
 * Copyright(C) 2014 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

public class ScaleImageView extends NetworkImageView {

    private static final int FADEIN_TIME = 300;

    private ImageLoadListener mListener;
    
    /**
     * 이미지 로딩완료 리스터 인터페이스
     */
    public interface ImageLoadListener {
    	public void onLoadComplete();
    }
    
    /**
     * ImageLoadListener를 세팅한다.
     * 
     * @param listener ImageLoadListener
     */
    public void setImageLoadListener(ImageLoadListener listener) {
    	this.mListener = listener;
    }
    
    public ScaleImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.FIT_XY);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();
        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight()
                    / d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        TransitionDrawable td = new TransitionDrawable(new Drawable[] {
                new ColorDrawable(getResources().getColor(android.R.color.transparent)),
                new BitmapDrawable(getContext().getResources(), bm) });

        setImageDrawable(td);
        td.startTransition(FADEIN_TIME);
        
        //ScaleImageView 사용하지만 콜백을 등록하지 않은 경우는 무시한다.
        //예)베스트딜 리스트에서 최상단 TV쇼핑영역 스틸이미지는 콜백을 등록할 필요가 없음
        //서비스 유료전환시까지 보류
        /*try {
        	mListener.onLoadComplete();
        } catch (Exception e) {
        	;
        }*/
    }

    /*
        @Override
        protected void onDetachedFromWindow() {
            // This has been detached from Window, so clear the drawable
            setImageDrawable(null);

            super.onDetachedFromWindow();
        }

        @Override
        public void setImageDrawable(Drawable drawable) {
            // Keep hold of previous Drawable
            final Drawable previousDrawable = getDrawable();

            // Call super to set new Drawable
            super.setImageDrawable(drawable);

            // Notify new Drawable that it is being displayed
            notifyDrawable(drawable, true);

            // Notify old Drawable so it is no longer being displayed
            notifyDrawable(previousDrawable, false);
        }

        private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
            if (drawable instanceof RecyclingBitmapDrawable) {
                // The drawable is a CountingBitmapDrawable, so notify it
                ((RecyclingBitmapDrawable) drawable).setIsDisplayed(isDisplayed);
            } else if (drawable instanceof LayerDrawable) {
                // The drawable is a LayerDrawable, so recurse on each layer
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                    notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
                }
            }
        }*/
}
