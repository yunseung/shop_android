package gsshop.mobile.v2.home.main

import com.gsshop.mocha.pattern.mvc.Model
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import java.util.*
import javax.annotation.ParametersAreNullableByDefault
import kotlin.collections.ArrayList

/**
 * GS Choice 에서 최초 도입된 메인 화면 상품 모듈 리스트
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
class ModuleList : SectionContentList() {
    // 상품 타입.
    @JvmField
    var tempViewType = 0
    @JvmField
    var moduleType: String? = null
    @JvmField
    var shopNumber: Long? = null // Long 형
    @JvmField
    var gdLocseq: String? = null
    @JvmField
    var gdCode: String? = null
    @JvmField
    var tabOnImg: String? = null
    @JvmField
    var tabOffImg: String? = null
    @JvmField
    var swiperYn: String? = null
    @JvmField
    var totalCnt: Long? = null // Long 형
    @JvmField
    var ajaxTabPrdListUrl: String? = null

    // GS Choice 의 카테고리가 fresh 형태로 변경됨에 따라 선택 되었을 때의 색상들 값 추가.
    @JvmField
    var activeTextColor: String? = null
    @JvmField
    var linkBgColor: String? = null
    @JvmField
    var linkText: String? = null
    @JvmField
    var moreBtnImgUrl: String? = null

    //    SectionContentList에 있는 변수들 삭제 (중복 선언 불가)
    //    public String viewType = null;
    //    public ArrayList<SectionContentList> productList = null;
    //    public String imageUrl = null;
    //    public String linkUrl = null;
    //    public String title = null;
    // 받는 값이 아닌 해당 값에 따라 값이 productList에 들어 있는지 확인을 위한 값.
    @JvmField
    var isInPList = false

}