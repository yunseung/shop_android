package gsshop.mobile.v2

/**
 * 앱티마이즈 실험 구조(TABNAME제외)
 */
class ApptimizeFlexibleExp(appver: String?, target: String?, exp: String?, type: String?, all: String?) : ApptimizeBaseExp() {
    init {
        this.appver = appver
        this.target = target
        this.exp = exp
        this.type = type
        this.all = all
    }
}