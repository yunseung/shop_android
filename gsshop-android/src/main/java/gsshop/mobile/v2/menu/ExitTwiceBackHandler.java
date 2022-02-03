/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.menu;

import android.app.Activity;

import com.gsshop.mocha.ui.back.TwiceBackExitHandler;

import gsshop.mobile.v2.util.AppFinishUtils;

/**
 * 2번 누르면 종료 된다. 
 *
 */
class ExitTwiceBackHandler extends TwiceBackExitHandler {

    
    //private final Context context;
    //private final Activity activity;
    
    public ExitTwiceBackHandler(Activity activity) {
        super(activity);
        //this.context = activity.getApplicationContext();
        //this.activity = activity;
    }
    /**
     * 
     * @return
     */
    @Override
    public boolean handle() {
    	
    	if( super.handle() )
    	{
            AppFinishUtils.finishApp(activity, false);
            return true;
    	}
    	return false;    		
    }
    
    @Override
    public void destroy() {
        super.destroy();
    }
}