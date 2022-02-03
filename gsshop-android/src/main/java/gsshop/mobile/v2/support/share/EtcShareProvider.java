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

import gsshop.mobile.v2.R;

/***
 * 기타 공유하기
 */
public class EtcShareProvider implements ShareInterface {

    ShareInfo shareInfo;

    public EtcShareProvider(ShareInfo shareInfo){
        this.shareInfo = shareInfo;
    }

    @Override
    public void share(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.putExtra(Intent.EXTRA_SUBJECT, shareInfo.getSubject());

        //getlink()없으면 줄바꿈을 하지 않는 로직 추가
        if(shareInfo.getLink()!=null){
            intent.putExtra(Intent.EXTRA_TEXT,shareInfo.getMessage() +"\n"+  shareInfo.getLink());
        }else{
            intent.putExtra(Intent.EXTRA_TEXT,shareInfo.getMessage());
        }


        Intent chooser = new Intent();
        //shared_title값이 있으면 가져와서 쓰기, 없으면 원래거
        if(this.shareInfo.getShare_title() != null){
             chooser = Intent.createChooser(intent, this.shareInfo.getShare_title());
        }else{
             chooser = Intent.createChooser(intent, activity.getString(R.string.shere));
        }
        activity.startActivity(chooser);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    }
}
