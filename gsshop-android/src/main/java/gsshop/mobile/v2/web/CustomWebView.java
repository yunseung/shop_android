/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * webview의 스크롤 상태를 catch하기 위한 customizing
 * 
 *
 */
public class CustomWebView extends WebView {

    private OnScrolledListener mOnScrolledListener;

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrolledListener != null) {
            mOnScrolledListener.onScrolled(l, t, oldl, oldt);
        }
    }

    public interface OnScrolledListener {
        public void onScrolled(int l, int t, int oldl, int oldt);
    }

    public void setOnScrolledListener(OnScrolledListener listener) {
        mOnScrolledListener = listener;
    }
}
