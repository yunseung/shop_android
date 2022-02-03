/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import roboguice.util.Ln
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * 암호화 유틸
 *
 */
object EncryptUtils {
    /**
     * Hash코드 생성후 Hex 문자열을 취득한다.
     *
     * @param str
     * @param encType
     * @return
     */
    @JvmStatic
    fun encrypt(str: String, encType: String): String {
        return bin2hex(getHash(str, encType))
    }

    /**
     * Hash코드를 취득한다.
     *
     * @param str source string
     * @param encType hash algorithm
     * @return final hash value
     */
    private fun getHash(str: String, encType: String): ByteArray? {
        var digest: MessageDigest? = null
        try {
            digest = MessageDigest.getInstance(encType)
        } catch (e: NoSuchAlgorithmException) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e)
        }
        //10/19 품질팀 요청
        if (digest != null) {
            digest.reset()
            return digest.digest(str.toByteArray())
        }
        return null
    }

    /**
     * 바이트 배열을 HEX 문자열로 변환한다.
     *
     * @param data source data
     * @return hex string
     */
    private fun bin2hex(data: ByteArray?): String {
        return if (data != null) {
            String.format("%0" + data.size * 2 + "x", BigInteger(1, data))
        } else {
            ""
        }
    }

    /**
     * src를 encType으로 인코딩한다.
     *
     * @param src 인코딩할 원본 문자열
     * @param encType 인코딩방식(euc-kr, utf-8,...)
     * @return 인코딩된 스트링, 오류 발생시 원본 스트링 반환
     */
    fun encode(src: String, encType: String?): String {
        return try {
            URLEncoder.encode(src, encType)
        } catch (e: UnsupportedEncodingException) {
            Ln.e(e)
            src
        }
    }

    /**
     * src를 인코딩한다.
     *
     * @param src 인코딩할 원본 문자열
     * @return 인코딩된 스트링, 오류 발생시 "" 반환
     */
    @JvmStatic
    fun urlEncode(src: String?): String {
        return try {
            URLEncoder.encode(src, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e)
            ""
        }
    }
}