/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user

import com.gsshop.mocha.pattern.mvc.Model
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.util.PrefRepositoryNamed
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import javax.annotation.ParametersAreNullableByDefault

/**
 * 가상 디바이스키 저장조
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
class VirtualDevicesKeyInfo {
    /**
     * 가상 디바이스 키
     */
    var virtualDevicesKey :String? = ""

    /**
     * 설정정보 저장.
     */
    fun save() {
        PrefRepositoryNamed.save(MainApplication.getAppContext(), VirtualDevicesKeyInfo::class.java.name, this)
    }

    companion object {
        /**
         * 조회
         * @return VirtualDevicesKeyInfo
         */
        fun get(): VirtualDevicesKeyInfo {
            var info = PrefRepositoryNamed.get(MainApplication.getAppContext(),
                    VirtualDevicesKeyInfo::class.java.name, VirtualDevicesKeyInfo::class.java)
            if (info == null) {
                info = VirtualDevicesKeyInfo()
                info.save()
            }
            return info
        }
    }
}