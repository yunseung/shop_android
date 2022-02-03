/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;

/**
 * 활성화된 액티비티 목록을 관리한다.
 *
 * Intent를 통한 액티비티 네비게이션 중에 어떤 경우에는
 * 의도한 바와 다르게 액티비티가 종료되지 않는 경우가 존재하며
 * 앱 종료 작업시 목록에 남아있는 액티비티를 강제로 finish시키기
 * 위한 목적.
 */
public abstract class RunningActivityManager {

    /**
     * 활성화된 액티비티 목록.
     */
    private static List<Activity> runningActivities = Collections
            .synchronizedList(new ArrayList<Activity>());

    /**
     * 생성된 액티비티를 활성 액티비티 목록에 추가한다.
     * activity의 onCreate()에서 호출한다.
     *
     * @param activity activity
     */
    public static void addActivity(Activity activity) {
        runningActivities.add(activity);
    }

    /**
     * 액티비티를 활성 액티비티 목록에서 제거한다.
     * activity의 onDestroy()에서 호출한다.
     *
     * @param activity activity
     */
    public static void removeActivity(Activity activity) {
        runningActivities.remove(activity);
    }

    /**
     * 활성 액티비티 목록에 남아있는 액티비티들을 모두 finish()시킨다.
     */
    public static void finishActivities() {
        //액티비티의 onDestroy()에서 removeActivity()를 호출하는 경우
        //runningActivities 아이템이 동적으로 변경됨.
        ArrayList<Activity> clone = new ArrayList<Activity>(runningActivities);

        for (Activity a : clone) {
            if (!a.isFinishing()) {
                a.finish();
            }
        }

        runningActivities.clear();
    }
}
