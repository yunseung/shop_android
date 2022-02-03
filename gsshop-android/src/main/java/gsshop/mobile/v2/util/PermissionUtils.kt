package gsshop.mobile.v2.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.PermissionChecker
import roboguice.util.Ln

/** 마시맬로우 권한 강화에 따른 permission 상태 가져오는 util
 * Created by jhkim on 2015-10-21.
 */
object PermissionUtils {
    @JvmStatic
    fun isPermissionGrantedForStorageWrite(context: Context): Boolean {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
    }

    @JvmStatic
    fun isPermissionGrantedForStorageRead(context: Context): Boolean {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
    }

    @JvmStatic
    fun isPermissionGrantedForCamera(context: Context): Boolean {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED
    }

    @JvmStatic
    fun isPermissionGrantedForLocation(context: Context): Boolean {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED
    }

    @JvmStatic
    fun isPermissionGrantedForPhone(context: Context): Boolean {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED
    }

    fun isPermissionGrantedForSMS(context: Context): Boolean {
        return PermissionChecker.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PermissionChecker.PERMISSION_GRANTED
    }

    /**
     * 퍼미션이 허용상태인지 확인한다.
     *
     * @param context 컨텍스트
     * @param perms   확인대상 퍼미션
     * 예) { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS };
     * @return 모든 퍼미션이 허용상태이면 true 리턴, 그외 false
     */
    private fun hasPermissions(context: Context, vararg perms: String): Boolean {
        for (perm in perms) {
            //PermissionChecker는 targetSdkVersion 21, 23 모두 정상 동작
            val hasPerm = PermissionChecker.checkSelfPermission(context, perm) == PermissionChecker.PERMISSION_GRANTED
            //ContextCompat은 targetSdkVersion 23에서만 정상 동작
            //boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false
            }
        }
        return true
    }

    /**
     * 사용자가 선택한 퍼미션이 허용인지 거부인지 확인한다.
     *
     * @param context 컨텍스트
     * @param permissions 퍼미션 종류
     * @param grantResults 사용자 선택값(허용:0 또는 거부:-1)
     * @return 모두 허용했으면 true 리턴, 그외 false
     */
    @JvmStatic
    fun verifyPermissions(context: Context, permissions: Array<String>, grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) return false
        for (i in permissions.indices) {
            val perm = permissions[i]
            //targetSdkVersion 21인 경우 권한을 거부한 경우에도 grantResults값이 항상 0(승은)으로 넘어옴
            //따라서 권한을 다시 확인해서 거부한 경우에 대해 값을 PERMISSION_DENIED로 세팅해줌
            //targetSdkVersion 23이상인 경우 아래코드는 있어도 무방하나, 필요없으니 제거 요망
            if (!hasPermissions(context, perm)) {
                grantResults[i] = PackageManager.PERMISSION_DENIED
            }
        }
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 앱의 TargetSdkVersion버전을 구한다.
     *
     * @param context 컨텍스트
     * @return TargetSdkVersion버전, 익셉션 발생시 -1 리턴
     */
    fun getTargetSdkVersion(context: Context): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.applicationInfo.targetSdkVersion
        } catch (e: Exception) {
            Ln.e(e)
            -1
        }
    }
}