package gsshop.mobile.v2

import com.gsshop.mocha.pattern.mvc.Model
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import javax.annotation.ParametersAreNullableByDefault

/**
 * GA APP 속도 측정을 위한 모델
 * Created by 이민수 on 2016-01-08.
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
class TimingInfo {
    /**
     * * 저장된 시작 시간
     */
    @JvmField
    var startTime: Long = 0

    /**
     * * 시간 값 사용 유무
     *
     * start 할 때 true
     * end 하면 false 해제
     */
    @JvmField
    var useflag = false

    /**
     * 디폴트 생성자로 생성시 사용상태가 아니다.
     */
    constructor() {
        useflag = false
        startTime = 0
    }

    /**
     * 생성하는 순간 사용가능한 상태로 만든다. useflag==true
     * @param startTime startTime
     */
    constructor(startTime: Long) {
        useflag = true
        this.startTime = startTime
    }
}