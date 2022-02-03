package gsshop.mobile.v2.support.share;


import android.app.Activity;
import android.content.Intent;

/**
 * 공유하기 인터페이스
 */
public interface ShareInterface {

    /**
     * 공유
     * @param activity activity
     */
    void share(Activity activity);

    /**
     * 공유하기 결과
     * @param activity activity
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data data
     */
    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

}
