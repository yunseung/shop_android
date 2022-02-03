package gsshop.mobile.v2

class ApptimizeTabNameExp(appver: String?, target: String?, exp: String?, type: String?) : ApptimizeBaseExp() {
    /**
     * ApptimizeTabNameExp 에만 있는 변수
     */
    var apptiVer: String? = null //실험시작할 최소 앱버전

    var apptiTarget: String? = null //실험대상 탭의 네비아이디

    var apptiMainTabName: String? = null //실험대상 탭의 메인탭명

    var apptiSubTabName: String? = null //실험대상 탭의 서브탭명

    init {
        this.appver = appver
        this.target = target
        this.exp = exp
        this.type = type
    }
}