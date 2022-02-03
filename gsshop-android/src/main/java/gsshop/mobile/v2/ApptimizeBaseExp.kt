package gsshop.mobile.v2

/**
 * 앱티마이즈 실험들의 공통속성
 */
abstract class ApptimizeBaseExp {
    var appver //앱버전
            : String? = null
    var target //탭의 네비아이디
            : String? = null
    var exp //실험명
            : String? = null
    var type //A or B or O
            : String? = null
    var all //앱버전_네비아이디_실험명_A
            : String? = null
}