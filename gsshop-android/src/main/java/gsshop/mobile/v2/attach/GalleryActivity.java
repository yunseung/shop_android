package gsshop.mobile.v2.attach;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.PermissionUtils;
import roboguice.inject.InjectView;

/**
 * GalleryActivity
 */
public class GalleryActivity extends AbstractBaseActivity {

	/**
	 * mPager
	 */
	@InjectView(R.id.pager)
	private NonSwipeableViewPager mPager;
	/**
	 * mPageAdapter
	 */
	private GalleryPageAdapter mPageAdapter;

	/**
	 * onCreate
	 * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().getAttributes().windowAnimations = R.style.SlideInRightAnimation;

		setContentView(R.layout.activity_gallery);

		if(PermissionUtils.isPermissionGrantedForStorageRead(this)) {
			mPageAdapter = new GalleryPageAdapter(this, getSupportFragmentManager());
			mPager.setAdapter(mPageAdapter);
		}
		else {

			// No explanation needed, we can request the permission.
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY);
			Log.d(getClass().getSimpleName(), "permission popup storage");
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY :
			case Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY_FROM_IMG_SEARCH :
				if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
					mPageAdapter = new GalleryPageAdapter(this, getSupportFragmentManager());
					mPager.setAdapter(mPageAdapter);
				}
				else {
					finish();
				}
				break;
		}
	}

	/**
	 * onDestroy
	 */
	@Override
	protected void onDestroy() {
		// NOTE Auto-generated method stub
		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		if (getDrawerLayout() != null && getDrawerLayout().isDrawerOpen(getNavigationView())) {
			closeNavigationView();
			return;
		}
		// NOTE Auto-generated method stub
		if (!isBack()) {
			// close callery
			finish();
		}
	}

	/**
	 * isBack
	 * @return boolean
     */
	public boolean isBack() {
		// NOTE Auto-generated method stub
		if (mPager.getCurrentItem() != GalleryPageAdapter.HOME_PAGE_CATEGORY) {
			mPager.setCurrentItem(GalleryPageAdapter.HOME_PAGE_CATEGORY);
			return true;
		}
		return false;
	}

	/**
	 * event listener
	 * @param event
     */
	public void onEvent(Events.GalleryEvent.LoadImageGridEvent event) {
		mPager.setCurrentItem(GalleryPageAdapter.HOME_PAGE_GRID);
		mPageAdapter.initUI(GalleryPageAdapter.HOME_PAGE_GRID, event.items);
	}

	/**
	 * close callery
	 *
	 * @param event
     */
	public void onEvent(Events.GalleryEvent.GoBackEvent event) {
		if (!isBack()) {
			// close callery
			finish();
		}
	}

	/**
	 * GalleryPageAdapter
	 */
	private static class GalleryPageAdapter extends FragmentPagerAdapter {

		/**
		 * HOME_PAGE_CATEGORY
		 */
		public static final int HOME_PAGE_CATEGORY = 0;
		/**
		 * HOME_PAGE_GRID
		 */
		public static final int HOME_PAGE_GRID = 1;

		/**
		 * fragments
		 */
		private final List<GalleryBaseFragment> fragments;

		/**
		 * GalleryPageAdapter
		 *
		 * @param activity
		 * @param fragmentManager
         */
		public GalleryPageAdapter(Activity activity, FragmentManager fragmentManager) {
			super(fragmentManager);
			fragments = new ArrayList<GalleryBaseFragment>();

			fragments.add(new GalleryCategoryPageFragment());
			fragments.add(new GalleryPhotoListPageFragment());

		}

		/**
		 * getItem
		 *
		 * @param position
         * @return Fragment
         */
		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		/**
		 * getCount
		 *
		 * @return size
         */
		@Override
		public int getCount() {
			return fragments.size();
		}

		/**
		 * initUI
		 *
		 * @param position
         * @param obj
         */
		public void initUI(int position, Object obj) {
			fragments.get(position).initUI(obj);
		}

		/**
		 * destroyItem
		 *
		 * @param container
		 * @param position
         * @param object
         */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// NOTE Auto-generated method stub
			// super.destroyItem(container, position, object);
		}

	}

}
