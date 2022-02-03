/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.menu.RunningActivityManager;

/**
 * 앱 종료에 관련된 유틸 모음
 * 백버튼 2회클릭, 마켓이동 등 앱종료가 필요한 경우 finishApp 함수를 호출하면 된다.
 */
public abstract class AppFinishUtils {

	/**
	 * 앱종료시 필요한 작업을 수행한다.
	 *
	 * @param activity 액티비티
	 * @param restart 재시작여부
	 */
	public static void finishApp(final Activity activity, final boolean restart) {
		new Thread() {
			@Override
			public void run() {
				//앱을 종료해도 static 변수는 초기화되지 않기 때문에 별도로 초기화 수행
				MainApplication.introCompleted = false;
				MainApplication.isHomeCommandExecuted = false;
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

				if (restart) {
					//앱을 재시작한다.
					PackageManager packageManager = activity.getPackageManager();
					Intent intent = packageManager.getLaunchIntentForPackage( activity.getPackageName());
					ComponentName componentName = intent.getComponent();
					Intent mainIntent = Intent.makeRestartActivityTask(componentName);
					activity.startActivity(mainIntent);
					//MSLEE 구글에서 System 보다는 Runtime.getRuntime().halt 이것을 추천하더라구요 11/16
					Runtime.getRuntime().halt(0);
					//System.exit(0);
				}
				//아래 코드와 같이 프로세스를 강제로 종료하면 앱구동시 인트로 진입 전 검은화면이 2초정도 노출된다.
				//이는 프로세스 정보를 메모리에 할당하는 수행시간으로 추측되며, 불필요한 시간지연을 막기 위해 아래 코드는 사용하지 않는다.
				//Galaxy S5에서 앱 종료시 잔상이 남는 문제를 해결하기 위해 아래 코드 추가
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}.start();
	}
}
