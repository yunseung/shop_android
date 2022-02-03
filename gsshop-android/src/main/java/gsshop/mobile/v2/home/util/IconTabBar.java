package gsshop.mobile.v2.home.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * TODO: custom icon tab bar using universal image loader
 */
@SuppressLint("NewApi")
public class IconTabBar extends FrameLayout {

	public interface IconTabListener {
		public Drawable getSelectedTabIconDrawable(int position, boolean selected);

		public void onIconTabClicked(int position, View view);

		public int tabSize();

	}

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	private IconTabListener tabListener;

	private LinearLayout tabsContainer;
	private boolean shouldExpand = true;
	private int tabPadding = 12;

	private Boolean onClick = false;


	public IconTabBar(Context context) {
		super(context);
		init(context, null, 0);
	}

	public IconTabBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public IconTabBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {

		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setGravity(Gravity.CENTER);
		tabsContainer.setLayoutParams(
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(tabsContainer);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
	}

	private void addIconTab(final int position, ImageView tab) {
		tab.setFocusable(true);
		// instance
		Drawable drawable = tabListener.getSelectedTabIconDrawable(position, false);
		tab.setImageDrawable(drawable);

		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onClick) {
					setSelectedTab(position);
				}
			}

		});

		tab.setPadding(tabPadding, 0, tabPadding, 0);
		tabsContainer.addView(tab, position,
				shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
	}

	public void setIconTabListener(IconTabListener listener) {
		tabListener = listener;
	}

	public void setOnClick(boolean on) {
		onClick = on;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.shouldExpand = shouldExpand;
		requestLayout();
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.tabPadding = paddingPx;
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		if (tabListener == null) {
			return;
		}

		tabsContainer.removeAllViews();

		for (int i = 0; i < tabListener.tabSize(); i++) {

			ImageView tab = new ImageView(getContext());
			tab.setScaleType(ImageView.ScaleType.FIT_CENTER);
			tab.setBackgroundResource(android.R.color.transparent);
			addIconTab(i, tab);

		}

		getViewTreeObserver()
				.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {

						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							getViewTreeObserver().removeGlobalOnLayoutListener(this);
						} else {
							getViewTreeObserver().removeOnGlobalLayoutListener(this);
						}

					}
				});
	}

	public void setSelectedTab(int position) {
		if (tabListener == null || position >= tabListener.tabSize()) {
			return;
		}

		tabListener.onIconTabClicked(position, tabsContainer.getChildAt(position));
		for (int i = 0; i < tabListener.tabSize(); i++) {

			ImageView view = (ImageView) tabsContainer.getChildAt(i);
			Drawable drawable = tabListener.getSelectedTabIconDrawable(i, position == i);
			view.setImageDrawable(drawable);

		}
	}


}
