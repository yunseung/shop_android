package gsshop.mobile.v2.attach;

import com.gsshop.mocha.core.activity.BaseFragment;

/**
 * GalleryBaseFragment
 */
public abstract class GalleryBaseFragment extends BaseFragment {
	/**
	 * abstract goBack
	 * @return boolean
     */
	public abstract boolean goBack();

	/**
	 * abstract initUI
	 * @param obj
     */
	public abstract void initUI(Object obj);

}
