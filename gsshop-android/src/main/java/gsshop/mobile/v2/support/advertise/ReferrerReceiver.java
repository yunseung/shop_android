package gsshop.mobile.v2.support.advertise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.net.URLDecoder;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.PARAM;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import roboguice.util.Ln;

public class ReferrerReceiver extends BroadcastReceiver {
	private static final String TAG = "ReferrerReceiver";
	//private static final String FILE_NAME = "referrer.txt";
	
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null && intent != null
                && "com.android.vending.INSTALL_REFERRER".equals(intent.getAction())) {
        	
        	//referrer값을 파일 및 프리퍼런스에 저장
            try {
                String referrer = intent.getStringExtra("referrer");
                if (referrer != null) { 
                	
                	//referrer 값을 파일로 저장한다.
                	saveReferrerInfo(context, referrer);
                	
                    //Datahub에서 사용할 media값 추출 (본 값은 ecid의 mediatype값에 우선한다.)
                    if (!TextUtils.isEmpty(referrer)) {
                    	String decReferrer = "";
                		try {
                			decReferrer = "?" + URLDecoder.decode(referrer, "UTF-8");
                	        String media = Uri.parse(decReferrer).getQueryParameter(PARAM.MEDIA);
                	        if (!TextUtils.isEmpty(media)) {
                	        	DatahubAction.MEDIA_TYPE = media;
                	        }
                		} catch (Exception e) {
                            // 10/19 품질팀 요청
                            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                            Ln.e(e);
                		}
                    }
                }
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }

            //Google conversiontracking (SDK v1.1 -> v2.0 업그레이드 하면서 신규로 추가함)
            try {
            	com.google.ads.conversiontracking.InstallReceiver receiver4 = new com.google.ads.conversiontracking.InstallReceiver();
                receiver4.onReceive(context, intent);
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }

            //clevertap
            /*
            try {
                com.clevertap.android.sdk.InstallReferrerBroadcastReceiver receiver5 = new com.clevertap.android.sdk.InstallReferrerBroadcastReceiver();
                receiver5.onReceive(context, intent);
            } catch (Exception e) {
                Ln.e(e);
            }
            */
        }
    }
    
    /**
     * referrer 값을 파일과 프리퍼런스에 저장한다.
     * 
     * @param context 컨텍스트
     * @param content 파일에 저장할 내용
     */
    private void saveReferrerInfo(Context context, String content) {
    	try {
    		//프리퍼런스에 저장
    		//PrefRepositoryNamed.save(Keys.CACHE.REFERRER, content);
    		PrefRepositoryNamed.save(context, Keys.CACHE.REFERRER, Keys.CACHE.REFERRER, content);
    		
    		/*File refDir = new File(Utils.getExternalCacheDir(context), "referrer");
    		//디렉토리 생성
            if (!refDir.exists()) {
            	refDir.mkdirs();
            }
            //파일로 저장
            File refFile = new File(refDir, FILE_NAME);
        	FileOutputStream fos = new FileOutputStream(refFile);
        	fos.write(content.getBytes());
        	fos.close();*/
        } catch(Exception e) {
            Ln.e(e);
        }
    }
}
