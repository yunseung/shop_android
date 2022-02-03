package gsshop.mobile.v2.home;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.gsshop.mocha.core.activity.BaseFragment;

import gsshop.mobile.v2.library.quickreturn.QuickReturnInterface;

/**
 * QuickReturnInterface mCoordinator 처리를 위한 CustomFragment
 */
public class CustomFragment extends BaseFragment {
    /**
     * 메인 액티비티
     */
    public HomeActivity mActivity;
    
	
	/**
	 * 퀵리턴뷰 인터페이스
	 */
	public QuickReturnInterface mCoordinator;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        mActivity = ((HomeActivity)getActivity());
    }

    @Override
    public void onResume( ) {
        super.onResume();
    }
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof QuickReturnInterface) {
			mCoordinator = (QuickReturnInterface) activity;
		} else
			throw new ClassCastException("Parent container must implement the QuickReturnInterface");
	}
    
//    public HomeActivity getMainActivity(){
//    	return mActivity;
//    }
//
//	/**
//	 * 
//	 */
//	public boolean canSwipeRefreshChildScrollUp() {
//		return true;
//	}
    
    public ListView getListView(){
		return null;
	}
    
}