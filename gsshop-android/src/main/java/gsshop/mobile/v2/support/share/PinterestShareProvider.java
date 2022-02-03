/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.share;

import android.app.Activity;
import android.content.Intent;

import com.pinterest.pinit.PinIt;
import com.pinterest.pinit.PinItListener;

/***
 * 핀터레스트 공유하기
 */
public class PinterestShareProvider implements ShareInterface {

    ShareInfo shareInfo;

    private PinIt pinIt;

    public PinterestShareProvider(ShareInfo shareInfo){
        this.shareInfo = shareInfo;
    }


    @Override
    public void share(Activity activity) {
        PinIt.setPartnerId("1442421"); // required
        PinIt.setDebugMode(false); // optional

        pinIt = new PinIt();
        pinIt.setImageUrl(shareInfo.getImageurl());
        pinIt.setUrl(shareInfo.getLink());
        pinIt.setDescription(shareInfo.getMessage());
        pinIt.setListener(_listener);
        pinIt.doPinIt(activity);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    }

    PinItListener _listener = new PinItListener() {

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onComplete(boolean completed) {
            super.onComplete(completed);
        }

        @Override
        public void onException(Exception e) {
            super.onException(e);
        }

    };
}
