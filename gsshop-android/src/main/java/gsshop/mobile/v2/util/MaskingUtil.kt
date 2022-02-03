package gsshop.mobile.v2.util

/**
 * Created by subin on 2016-03-29.
 */
object MaskingUtil {
    /**
     * loginId 를 마스킹 하여 돌려준다.
     * @param loginId 아이디
     * @param isSkipDomain 도메인 비노출여부, true 이면 생략
     * @return 마스킹된 결과
     */
    @JvmStatic
    @JvmOverloads
    fun maskLoginId(loginId: String?, isSkipDomain: Boolean = false): String {
        var maskedStr = ""
        if (loginId != null && loginId.isNotEmpty()) {
            val tmpIdx = loginId.lastIndexOf('@')
            if (tmpIdx == -1) {
                //id 전체길이
                var idLen = loginId.length
                //표시할 문자수
                val dspLen: Int
                when {
                    idLen in 4..5 -> {
                        // 뒤 3자리만 마스킹
                        dspLen = idLen - 3
                        maskedStr = loginId.substring(0, dspLen)
                    }
                    loginId.length >= 6 -> {
                        //앞 3자리만 노출하고 마스킹
                        dspLen = 3
                        maskedStr = loginId.substring(0, dspLen)
                    }
                    else -> {
                        //마스킹만 3자리 (변경전 룰 유지)
                        idLen = 3
                        dspLen = 0
                    }
                }
                for (i in 0 until idLen - dspLen) {
                    maskedStr += "*"
                }
            } else {
                //email ID
                var bfTxt = loginId.substring(0, tmpIdx)
                //id 전체길이
                val bfLen = bfTxt.length
                //표시할 문자수
                var dspLen = 0
                when {
                    bfLen in 2..3 -> {
                        //앞 1자리만 노출
                        dspLen = 1
                        bfTxt = bfTxt.substring(0, dspLen)
                    }
                    bfLen >= 4 -> {
                        //앞 2자리만 노출
                        dspLen = 2
                        bfTxt = bfTxt.substring(0, dspLen)
                    }
                    else -> {
                        //마스킹만 1자리 (변경전 룰 유지)
                        bfTxt = ""
                    }
                }
                for (i in 0 until bfLen - dspLen) {
                    bfTxt += "*"
                }
                maskedStr = if (isSkipDomain) {
                    //도메인 앞부분만 노출하는 경우 (모바일라이브)
                    bfTxt
                } else {
                    val afTxt = loginId.substring(tmpIdx + 1)
                    "$bfTxt@$afTxt"
                }
            }
        }
        return maskedStr
    }

    /**
     * loginId 를 마스킹 하여 돌려준다. (쇼핑라이브 용)
     * @param loginId 아이디
     * @param isSkipDomain 도메인 비노출여부, true 이면 생략
     * @return 마스킹된 결과
     */
    @JvmStatic
    @JvmOverloads
    fun maskLoginIdForShoppyLive(loginId: String?, isSkipDomain: Boolean = false): String {
        var maskedStr = ""
        if (loginId != null && loginId.isNotEmpty()) {
            val tmpIdx = loginId.lastIndexOf('@')
            if (tmpIdx == -1) {
                //id 전체길이
                var idLen = loginId.length
                //표시할 문자수
                val dspLen: Int
                when {
                    loginId.length >= 4 -> {
                        // 뒤 3자리만 마스킹
                        dspLen = idLen - 3
                        maskedStr = loginId.substring(0, dspLen)
                    }
                    else -> {
                        //마스킹만 3자리 (변경전 룰 유지)
                        idLen = 3
                        dspLen = 0
                    }
                }
                for (i in 0 until idLen - dspLen) {
                    maskedStr += "*"
                }
            } else {
                //email ID
                var bfTxt = loginId.substring(0, tmpIdx)
                //id 전체길이
                val bfLen = bfTxt.length
                //표시할 문자수
                var dspLen = 0
                when {
                    bfLen >= 4 -> {
                        // 뒤 3자리만 마스킹
                        dspLen = bfLen - 3
                        bfTxt = bfTxt.substring(0, dspLen)
                    }
                    else -> {
                        dspLen = 0
                        bfTxt = ""
                    }
                }
                for (i in 0 until bfLen - dspLen) {
                    bfTxt += "*"
                }
                maskedStr = if (isSkipDomain) {
                    //도메인 앞부분만 노출하는 경우 (모바일라이브)
                    bfTxt
                } else {
                    val afTxt = loginId.substring(tmpIdx + 1)
                    "$bfTxt@$afTxt"
                }
            }
        }
        return maskedStr
    }

    /**
     * Hyphen("-")이 있는 전화번호 를 마스킹 하여 돌려준다.
     * @param telNo
     * @return
     */
    fun maskTelNoWithHyphen(telNo: String?): String {
        var maskedStr = ""
        if (telNo != null) {
            val tel = telNo.split("-").toTypedArray()
            if (tel.size == 3) {
                maskedStr = tel[0] + "-"
                for (i in tel[1].indices) {
                    maskedStr += "*"
                }
                maskedStr += "-" + tel[2]
            }
        }
        return maskedStr
    }
}