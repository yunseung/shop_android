/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.setting

import com.gsshop.mocha.pattern.mvc.Model
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.util.PrefRepositoryNamed
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import javax.annotation.ParametersAreNullableByDefault

/**
 * 동영상 자동 재생 관련 각종 정보.
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
class AutoPlaySettings {
    /**
     * autoplay 사용할지 여부. 기본값은 true.
     *
     */
    @JvmField
    var approve = true

    /**
     * 설정정보 저장.
     */
    fun save() {
        PrefRepositoryNamed.save(MainApplication.getAppContext(), AutoPlaySettings::class.java.name, this)
    }

    companion object {
        @JvmStatic
        fun get(): AutoPlaySettings {
            var settings = PrefRepositoryNamed.get(MainApplication.getAppContext(), AutoPlaySettings::class.java.name,
                    AutoPlaySettings::class.java)
            if (settings == null) {
                settings = AutoPlaySettings()
                settings.save()
            }
            return settings
        }
    }
}