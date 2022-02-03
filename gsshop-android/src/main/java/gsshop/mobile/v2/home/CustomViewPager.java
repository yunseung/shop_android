/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 
 * CustomViewPager
 *
 */
public class CustomViewPager extends ViewPager{

	/**
	 * 캐로셀 좌우 플리킹시 매장 스와이프 방지용 플래그
	 */
	public static boolean isHorizontalScrollDisallow = false;

	/**
	 * 생성자
	 * @param context context
     */
	public CustomViewPager(Context context) {
		super(context);
	}

	/**
	 * CustomViewPager
	 *
	 * @param context context
	 * @param attrs AttributeSet
     */
	public CustomViewPager(Context context,AttributeSet attrs) {
		super(context,attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return !this.isHorizontalScrollDisallow && super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return !this.isHorizontalScrollDisallow && super.onTouchEvent(event);
	}
}
