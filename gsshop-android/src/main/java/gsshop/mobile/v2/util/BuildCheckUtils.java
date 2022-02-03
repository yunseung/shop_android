/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import com.gsshop.mocha.device.AppInfo;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import gsshop.mobile.v2.MainApplication;
import roboguice.util.Ln;

/**
 * 앱 위변조 방지를 위한 유틸 모음
 */
public abstract class BuildCheckUtils {

	/**
	 * 키스토어 인증서 지문을 추출한다.
	 * 앱 위변조를 이해 다른 인증서로 본 앱을 사이닝할 경우 함수 리턴값은 변경된다.
	 *
	 * @param context 컨텍스트
	 * @return Base64로 인코딩된 키스토어 인증서 지문값
     */
	public static String getKeystoreFingerprint(Context context) {
		String ksFingerprint = null;

		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA1");
				md.update(signature.toByteArray());
				ksFingerprint = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
			}
		} catch (PackageManager.NameNotFoundException e) {
			Ln.e(e);
		} catch (NoSuchAlgorithmException e) {
			Ln.e(e);
		} catch (Exception e) {
			Ln.e(e);
		}

		return ksFingerprint;
	}

	/**
	 * classes.dex파일의 해시코드를 추출한다.
	 * 앱 위변조를 위해 소스코드 또는 리소스를 변경할 경우 함수 리턴값은 변경된다.
	 *
	 * @param context 컨텍스트
	 * @return classes.dex파일의 해시코드
	 */
	public static String getDexfileHashcode(Context context) throws IOException {
		String dexHashcode = null;

		//class.dex SHA256(빌드 환경에 따라 다름) 취득
		ApplicationInfo ai = context.getApplicationInfo();
		String source = ai.sourceDir;
		JarFile jar = null;
		Manifest mf = null;

		try {
			jar = new JarFile(source);
			mf = jar.getManifest();
			Map<String, Attributes> map = mf.getEntries();
			Attributes a = map.get("classes.dex");
			dexHashcode = a.getValue("SHA1-Digest");
		} catch (Exception e) {
			Ln.e(e);
		} finally {
			if(jar != null ){
				jar.close();
			}
		}

		return dexHashcode;
	}

	/**
	 * key앱버전 >= 내 앱버전
	 * 위 경우에만 Apptimize AB테스트 실험대상으로
	 * ApptimizeCommand를 add한다.
	 * @return 대상이면 true, 아니면 false
	 */
	public static boolean isApptimizeApply(String ab_appversion){
		int appver = 999;
		PackageInfo pi = null;

		try {
			appver = Integer.parseInt(ab_appversion); //앱티마이즈 실험 앱버전
			pi = AppInfo.getPackageInfo(MainApplication.getAppContext()); //내 앱버전

			// 앱티마이즈 실험 버전 <= 내 앱버전 (버전 같을때만 실험대상 되도록! 여러개의 버전이 실험대상 되어야하면 앱티마이즈 실험을 여러개 만드는방법으로 진행)
			if (appver <= AppInfo.getAppVersionCode(pi)) {
				return true;
			}
		}catch (Exception e){

			Ln.e(e);
		}
		return false;
	}
}
