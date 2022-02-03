/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import roboguice.util.Ln;

/**
 * 롤링 베이스 뷰 홀더
 *
 */
public class BaseRollViewHolder extends BaseViewHolderV2 {

	protected Context mContext;

	protected InfiniteViewPager viewPager;
	protected TimerTask task;

	/**
	 * @param itemView
	 */
	public BaseRollViewHolder(View itemView) {
		super(itemView);
	}

	@Override
	public void onViewAttachedToWindow() {
		super.onViewAttachedToWindow();
		startTimer();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onViewDetachedFromWindow() {
		super.onViewDetachedFromWindow();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent event) {
		//Ln.i("StartRollingNo1BestDealBannerEvent : "+ event.start);
		if (event.start) {
			startTimer();
		} else {
			stopTimer();
		}
	}

	/**
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {

		this.mContext = context;

		final ShopItem content = info.contents.get(position);

		if(content.sectionContent.subProductList == null){
			return;
		}

		final List<SectionContentList> list = content.sectionContent.subProductList;
		final int indicator = content.indicator;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			viewPager.setAccessibilityDelegate(new View.AccessibilityDelegate() {
				@Override
				public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
					super.onPopulateAccessibilityEvent(host, event);

					if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED ||
							event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
						event.setContentDescription(list.get(indicator).productName);
					}
				}
			});
		}

		if(content.sectionContent.rollingDelay > 0 && content.sectionContent.subProductList != null && content.sectionContent.subProductList.size() > 1) {
			setAutoRollingStart(content.sectionContent.rollingDelay);
		}else{
			setAutoRollingStop();
		}
	}

	protected void setRandom(ShopItem content){
		//랜덤
		if("Y".equalsIgnoreCase(content.sectionContent.randomYn) && !content.sectionContent.isRandomComplete){
//			Collections.shuffle(content.sectionContent.subProductList, new Random(System.nanoTime()));
			content.sectionContent.isRandomComplete = true;
			viewPager.setCurrentItem(new Random().nextInt(content.sectionContent.subProductList.size()), true);
		}
	}

	protected void setAutoRollingStart(float delayMillisecond) {
		RollingData data = new RollingData();
		data.isAuto = true;
		data.rollingDelayMillisecond = (int)(delayMillisecond * 1000);
		viewPager.setTag(data);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					startTimer();
				} catch (Exception e) {
					Ln.e(e);
				}
			}
		}, data.rollingDelayMillisecond);
	}

	protected void setAutoRollingStop() {
		RollingData data = new RollingData();
		data.isAuto = false;
		data.rollingDelayMillisecond = 0;
		viewPager.setTag(data);
		stopTimer();
	}

	/* 자동 롤링 실행. */
	protected void startTimer() {
		RollingData data = null;
		if (viewPager != null) {
			data = (RollingData)viewPager.getTag();
		}else {
			return;
		}
		if (data != null && data.isAuto) {
			stopTimer();
			try {
				task = new TimerTask() {
					@Override
					public void run() {
						((Activity) mContext).runOnUiThread(() -> {
							if (viewPager != null) {
								if (viewPager != null && viewPager.getAdapter() != null) {
									int nextItem = viewPager.getRealCurrentItem() + 1;
									if (nextItem < viewPager.getAdapter().getCount()) {
										viewPager.setRealCurrentItem(nextItem, true);
									}
								}
							}
						});
					}
				};
				new Timer().schedule(task, data.rollingDelayMillisecond);
			}
			catch (OutOfMemoryError e) {
				Ln.e(e.getMessage());
			}
		}
	}

	/* 자동 롤링 종료. */
	public void stopTimer() {
		// timer
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	private class RollingData {
		public boolean isAuto;
		public int rollingDelayMillisecond;
	}

	// FlexibleShopFragment를 상속 받은 fragment는 onResume, onPause 시에 롤링을 컨트롤 하는 event를 post한다.
	// 이에 롤링 여부 설정용 함수를 두어 롤링 여부 컨트롤 할 수 있게끔 선언함
	public void setRollingIsAuto(boolean isRolling) {
		RollingData data = null;
		if (viewPager != null) {
			data = (RollingData)viewPager.getTag();
			data.isAuto = isRolling;
			viewPager.setTag(data);
		}
	}

	public boolean getRollingIsAuto() {
		RollingData data = null;
		if (viewPager != null) {
			data = (RollingData)viewPager.getTag();
			return data.isAuto;
		}
		else {
			return false;
		}
	}
}
