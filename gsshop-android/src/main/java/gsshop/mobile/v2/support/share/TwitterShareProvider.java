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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.Html;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.EncryptUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.RoboGuice;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 트위터 공유하기
 */
public class TwitterShareProvider implements ShareInterface {

    ShareInfo shareInfo;
    private File sharedFile;

    public TwitterShareProvider(ShareInfo shareInfo){
        this.shareInfo = shareInfo;
        RoboGuice.getInjector(MainApplication.getAppContext()).injectMembers(this);
    }

    @Override
    public void share(Activity activity) {
        ThreadUtils.INSTANCE.runInBackground(() -> {
            Intent tweetIntent = new Intent(Intent.ACTION_SEND);
            tweetIntent.setType("*/*");
            tweetIntent.putExtra(Intent.EXTRA_SUBJECT, shareInfo.getSubject());
            tweetIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<p>" + shareInfo.getMessage() +"</p>")+ "\n" + shareInfo.getLink());


            sharedFile = activity.getFilesDir(); // 임시파일 저장 경로

            String fileName = "share_image_" + System.currentTimeMillis() + ".png";

            sharedFile = new File(sharedFile, fileName);
            Uri uri = null;
            try {
                copyFile(Glide.with(activity).load(trim(shareInfo.getImageurl())).downloadOnly(-1, -1).get(), sharedFile);
                uri = FileProvider.getUriForFile(activity, "gsshop.mobile.fileprovider", sharedFile);
            }catch(Exception e){
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }

            tweetIntent.putExtra(Intent.EXTRA_STREAM, uri);
            tweetIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            tweetIntent.setDataAndType(uri, "*/*");

            PackageManager packManager = activity.getPackageManager();
            List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

            boolean resolved = false;
            for(ResolveInfo resolveInfo: resolvedInfoList){
                if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                    tweetIntent.setClassName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name );
                    resolved = true;
                    break;
                }
            }
            //트위터 앱이 있으면 트위터 앱으로 공유하기를 수행한다.
            if(resolved){
                activity.startActivity(tweetIntent);
            }else{
                //트위터 앱이 없으면 web 인터페이스로 공유한다.
                Intent i = new Intent();
                i.putExtra(Intent.EXTRA_TEXT, "GSSHOP");
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://twitter.com/intent/tweet?url=" + EncryptUtils.urlEncode(shareInfo.getLink()) + "&text="
                        + EncryptUtils.urlEncode(shareInfo.getMessage()) + "&via=GSSHOP"));
                activity.startActivity(i);
            }
        });
    }

    public void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
    }

    private void copyFile(File src, File dst) throws IOException{
        FileInputStream fis = null;
        FileChannel ic = null;

        FileOutputStream fos = null;
        FileChannel oc = null;

        try {
            fis = new FileInputStream(src);
            ic = fis.getChannel();

            fos = new FileOutputStream(dst);
            oc = fos.getChannel();

            long size = ic.size();
            ic.transferTo(0, size, oc);
        } catch (Exception e) {
            Ln.e(e);
        } finally {
            if(oc != null){
                oc.close();
            }
            if(ic != null) {
                ic.close();
            }
            if(fos != null) {
                fos.close();
            }
            if(fis != null) {
                fis.close();
            }
        }
    }
}
