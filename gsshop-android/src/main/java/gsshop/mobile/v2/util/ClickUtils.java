/*
 * Copyright(C) 2013 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util;

import android.os.Handler;
import android.os.Looper;

public final class ClickUtils {
	private static boolean isClick = false;
	private static boolean isClick2 = false;
	private static boolean isClick3 = false;
	private static boolean isClickDetailView =false;
	private static boolean isShoppingTime = false;	// 쇼핑 라이브에 이벤트가 1초 내에서 중복으로 들어오는 경우가 있어 추가
	private static boolean isRemovePrdLiveItem = false;

	//영상 refresh
	private static boolean isRefresh = false;

	private ClickUtils() {
	}


	public static boolean work(long delay) {
		if (isClick) {
			return true;
		}
		
		isClick = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				isClick = false;
			}
		}, delay);
		
		return false;
	}

	public static boolean work2(long delay) {
		if (isClick2) {
			return true;
		}

		isClick2 = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				isClick2 = false;
			}
		}, delay);

		return false;
	}

	public static boolean work3(long delay) {
		if (isClick3) {
			return true;
		}

		isClick3 = true;
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				isClick3 = false;
			}
		}, delay);

		return false;
	}

	public static boolean detailViewWrok(long delay) {
		if (isClickDetailView) {
			return true;
		}

		isClickDetailView = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				isClickDetailView = false;
			}
		}, delay);

		return false;
	}

	public static boolean refresh(long delay) {
		if (isRefresh) {
			return true;
		}

		isRefresh = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				isRefresh = false;
			}
		}, delay);

		return false;
	}

	public static boolean shoppingLiveTimeCheck(long delay) {
		if (isShoppingTime) {
			return true;
		}

		isShoppingTime = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				isShoppingTime = false;
			}
		}, delay);

		return false;
	}

	public static boolean removePrdLiveItem(long delay) {
		if (isRemovePrdLiveItem) {
			return true;
		}

		isRemovePrdLiveItem = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				isRemovePrdLiveItem = false;
			}
		}, delay);

		return false;
	}

}
