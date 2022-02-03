/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 네이티브 단품용 웹뷰
 */
public class ProductDetailWebView extends WebView {

    public ProductDetailWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ProductDetailWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductDetailWebView(Context context) {
        super(context);
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }
}
