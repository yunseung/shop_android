/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.menu;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;

import com.gsshop.mocha.ui.back.AbstractBackKeyHandler;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.FinishButtonNormalDialog;

/**
 * back 키 누른 경우 사용자에게 앱 종료여부를 묻는다.
 *
 */
class ConfirmExitBackHandler extends AbstractBackKeyHandler {

    private Dialog dialog;
    private final Context context;
    private final Activity activity;
    
    public ConfirmExitBackHandler(Activity activity) {
        super(activity);
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    /**
     * OK 선택시 모든 액티비티를 종료한다.
     *
     * @return 무조건 true.
     */
    @Override
    public boolean handle() {
    	setNormalDialog();
	    
        dialog.show();
        return true;
    }
    
    /**
     * 추천상품이 없는 종료팝업을 생성한다.
     */
    private void setNormalDialog() {
        dialog = new FinishButtonNormalDialog(activity).message(R.string.app_confirm_exit)
            .positiveButtonClick(new ButtonClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    dialog.dismiss();
                    positiveButtonClick();
                }
            }).negativeButtonClick(new ButtonClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    dialog.dismiss();
                }
        });    	
    }
    
    /**
     * 확인버튼 클릭 이벤트 함수
     */
    private void positiveButtonClick() {
        // 팝업이 빨리 사라지지 않아, activity들 종료하는 루틴은 다른 thread로 실행
        new Thread() {
            @Override
            public void run() {
                activity.finish();

                MainApplication.introCompleted = false;
                RunningActivityManager.finishActivities();

                 /** 비주기적으로 앱 종료이후 매인매장이 보이는현상 발생
                 * System.exit() 나 Process.killProcess() 로 죽이는 방법도 있는데,
                 * 위의 방법들은 특정상황에서 다시 부활하곤 한다. 그 상황은 Service 의 onStartCommand() 함수의 
                 * return 값이 START_NOT_STICKY 가 아닌 경우.
                 * 두번째는 Activity 의 Stack 이 남아있는 경우. 이는 Process 가 activity stack 을 
                 * 관리하는 것이 아니라 system 에서 관리하기 때문.*/
                 
                ActivityManager am = (ActivityManager) activity
                        .getSystemService(Activity.ACTIVITY_SERVICE);
                am.killBackgroundProcesses(activity.getPackageName());

                //Galaxy S5에서 앱 종료시 잔상이 남는 문제를 해결하기 위해 아래 코드 추가
                android.os.Process.killProcess(android.os.Process.myPid());
            }

        }.start();
    }
    
    @Override
    public void destroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        super.destroy();
    }
}