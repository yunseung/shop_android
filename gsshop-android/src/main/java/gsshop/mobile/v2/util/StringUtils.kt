/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.net.Uri
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.StringUtils
import org.springframework.util.NumberUtils
import roboguice.util.Ln
import java.util.*
import java.util.regex.Pattern

object StringUtils {
    fun getVisibility(booleanStr: String?): Int {
        var visibility = View.GONE

        // not empty and "true"(case insensitive)
        if (java.lang.Boolean.parseBoolean(booleanStr)) {
            visibility = View.VISIBLE
        }
        return visibility
    }

    fun getInt(str: String?): Int {
        var n = 0
        try {
            n = NumberUtils.parseNumber(str, Int::class.java)
        } catch (e: Exception) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e)
        }
        return n
    }

    /**
     * 문자열이 numeric 타입인지 확인한다.
     *
     * @param str 확인할 문자열
     * @return numeric 타입미면 true를 리턴하고, 아니면 false를 리턴한다.
     */
    @JvmStatic
    fun isNumeric(str: String): Boolean {
        return str.matches(Regex("^[0-9]+$"))
    }

    /**
     * 문자열의 오른쪽 whitespace를 제거한다.
     *
     * @param s 원본 문자열
     * @return whitespace가 제거된 문자열
     */
    @JvmStatic
    fun rTrim(s: String): String {
        var i = s.length - 1
        while (i >= 0 && Character.isWhitespace(s[i])) {
            i--
        }
        return s.substring(0, i + 1)
    }

    /**
     * 문자열의 좌우 whitespace를 제거한다.
     *
     * @param s 원본 문자열
     * @return 제거된 문자열
     */
    @JvmStatic
    fun trim(s: String?): String {
        return s?.trim() ?: ""
    }

    /**
     * 쿼리파라미터를 오버라이트 하거나 추가한다.
     *
     * @param uri uri
     * @param key 파라미터 키
     * @param newValue 파라미터 밸류
     * @return 결과 uri
     */
    @JvmStatic
    fun replaceUriParameter(uri: Uri, key: String, newValue: String?): Uri {
        val params = uri.queryParameterNames
        val newUri = uri.buildUpon().clearQuery()
        //동일한 키값이 있으면 오버라이트
        for (param in params) {
            val addValue = if (param == key) newValue else uri.getQueryParameter(param)

            // 값이 비었으면 추가할 필요 없다.
            if (!TextUtils.isEmpty(addValue)) {
                newUri.appendQueryParameter(param, addValue)
            }
        }
        //동일한 키값이 없으면 신규추가
        if (!params.contains(key) && !TextUtils.isEmpty(newValue)) {
            newUri.appendQueryParameter(key, newValue)
        }
        return newUri.build()
    }

    /**
     * 쿼리파라미터를 추가한다.
     *
     * @param uri uri
     * @param key 파라미터 키
     * @param newValue 파라미터 밸류
     * @return 결과 uri
     */
    @JvmStatic
    fun addUriParameter(uri: Uri, key: String, newValue: String?): Uri {
        val params = uri.queryParameterNames
        val newUri = uri.buildUpon()

        //동일한 키값이 없으면 신규추가
        if (!params.contains(key)) {
            newUri.appendQueryParameter(key, newValue)
        }
        return newUri.build()
    }

    /**
     * 전달 되는 URI 에 key 의 value 가 존재하는 지 확인 후 있으면 값 리턴, 없으면 null return
     *
     * @param strUri String 형 uri
     * @param key 파라미터 키
     * @return 파라미터 키에 해당하는 값
     */
    @JvmStatic
    fun getUriParameter(strUri: String?, key: String): String? {
        if (StringUtils.isEmpty(strUri)) return null
        val uri = Uri.parse(strUri) ?: return null
        return getUriParameter(uri, key)
    }

    /**
     * 전달 되는 URI 에 key 의 value 가 존재하는 지 확인 후 있으면 'key=value' 리턴, 없으면 null return
     *
     * @param strUri String 형 uri
     * @param key 파라미터 키
     * @return 파라미터 키에 해당하는 값
     */
    @JvmStatic
    fun getUriParameterWithKey(strUri: String?, key: String): String? {
        if (StringUtils.isEmpty(strUri)) return null
        val uri = Uri.parse(strUri) ?: return null
        return if (getUriParameter(uri, key) == null) "" else key + "=" + getUriParameter(uri, key)
    }

    /**
     * 전달 되는 URI 에 key 의 value 가 존재하는 지 확인 후 있으면 값 리턴, 없으면 null return
     *
     * @param uri Uri 형 uri
     * @param key 파라미터 키
     * @return 파라미터 키에 해당하는 값
     */
    @JvmStatic
    fun getUriParameter(uri: Uri, key: String): String? {
        var strIsViewAll: String? = null
        val params = uri.queryParameterNames
        if (params.contains(key)) {
            for (param in params) {
                if (param == key) {
                    strIsViewAll = uri.getQueryParameter(param)
                    break
                }
            }
        }
        return strIsViewAll
    }

    @JvmStatic
    fun checkHexColor(color: String): Boolean {
        val regex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
        val p = Pattern.compile(regex)
        val m = p.matcher(color)
        return m.matches()
    }

    /**
     * URL에 Question Mark를 추가한다. (Question Mark 없는 경우)
     *
     * @param url url
     * @return Question Mark 추가된 url
     */
    @JvmStatic
    fun addQuestionMark(url: String): String {
        return if (url.indexOf('?') == -1) "$url?" else url
    }

    /**
     * milliseconds 를 00:00 형식으로 반환
     * @param milliseconds
     * @param up
     * @return
     */
    @JvmStatic
    fun stringForHHMM(milliseconds: Long, up: Boolean): String {
        return if (milliseconds <= 0L) {
            "00:00"
        } else {
            val totalSeconds = milliseconds / 1000L
            val seconds = totalSeconds % 60L + if (up) if (milliseconds % 1000L != 0L) 1 else 0 else 0
            val minutes = totalSeconds / 60L
            val formatter = Formatter(Locale.getDefault())
            formatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    /**
     * milliseconds 를 00:00:00 형식으로 반환
     * @param milliseconds
     * @param up
     * @return
     */
    @JvmStatic
    fun stringForHHMMSS(milliseconds: Long, up: Boolean): String {
        return if (milliseconds <= 0L) {
            "00:00"
        } else {
            val totalSeconds = milliseconds / 1000L
            val seconds = totalSeconds % 60L + if (up) if (milliseconds % 1000L != 0L) 1 else 0 else 0
            val minutes = totalSeconds / 60L
            val displayMinutes = minutes % 60L
            val hours = minutes / 60L
            val formatter = Formatter(Locale.getDefault())
            formatter.format("%02d:%02d:%02d", hours, displayMinutes, seconds).toString()
        }
    }

    /**
     * 스트링내 space 수를 리턴한다.
     *
     * @param str 확인할 스트링
     * @return 스페이스 갯수
     */
    @JvmStatic
    fun countSpaces(str: String): Int {
        var spaces = 0
        for (element in str) {
            spaces += if (Character.isWhitespace(element)) 1 else 0
        }
        return spaces
    }
}