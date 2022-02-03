/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import roboguice.util.Ln
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 남은 시간을 계산한다.
 * 시간데이터는 모두 Asia/Seoul 기준시로 변환후 처리한다.
 */
class TimeRemaining {

    /**
     * 시작시간 (API에서 전달받는 값)
     */
    private lateinit var startDate: String

    /**
     * 종료시간 (API에서 전달받는 값)
     */
    private val endDate: String

    /**
     * 시간표현 포맷(yyyyMMddHHmmss)
     */
    private val format: SimpleDateFormat

    /**
     * 타임존(서울기준시)
     */
    private val seoulTimeZone: TimeZone

    /**
     * 현재시간
     */
    private val currentTime: Calendar

    /**
     * 종료시간
     */
    private val endTime: Calendar

    /**
     * 시작시간
     */
    private lateinit var startTime: Calendar

    /**
     * 남은시간
     */
    private val resultTime: Calendar

    enum class DisplayType {
        HOUR, MINUTE, MINUTE_MM, SECOND
    }

    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var mSecond = 0

    /**
     * 남은 시간 계산하는 펑션
     * @param endDate 종료 시간 데이터
     * @throws Exception Exception
     */
    constructor(endDate: String?) {
        if (endDate == null || "" == endDate || endDate.length != 14) {
            //호출 함수 쪽에서 처리하도록 런타임 익셉션 발생 MSLEE 20161219
            throw GsShopException("endDate not found")
        }
        this.endDate = endDate
        format = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA)
        seoulTimeZone = TimeZone.getTimeZone("Asia/Seoul")
        currentTime = Calendar.getInstance(Locale.KOREA)
        endTime = Calendar.getInstance(Locale.KOREA)
        resultTime = Calendar.getInstance(Locale.KOREA)

        //기준시는 한국으로 설정
        format.timeZone = seoulTimeZone
        currentTime.timeZone = seoulTimeZone
        endTime.timeZone = seoulTimeZone
        resultTime.timeZone = seoulTimeZone

        //종료시간 설정
        endTime.time = format.parse(endDate)
    }

    /**
     * 영상 총길이 구하는 펑션
     * @param endDate 종료 시간 데이터
     * @param startDate 시작 시간 데이터
     * @throws Exception Exception
     */
    constructor(startDate: String, endDate: String?) {
        if (endDate == null || "" == endDate || endDate.length != 14) {
            //호출 함수 쪽에서 처리하도록 런타임 익셉션 발생 MSLEE 20161219
            throw GsShopException("endDate not found")
        }
        this.endDate = endDate
        this.startDate = startDate
        format = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA)
        seoulTimeZone = TimeZone.getTimeZone("Asia/Seoul")
        currentTime = Calendar.getInstance(Locale.KOREA)
        endTime = Calendar.getInstance(Locale.KOREA)
        startTime = Calendar.getInstance(Locale.KOREA)
        resultTime = Calendar.getInstance(Locale.KOREA)

        //기준시는 한국으로 설정
        format.timeZone = seoulTimeZone
        currentTime.timeZone = seoulTimeZone
        endTime.timeZone = seoulTimeZone
        startTime.timeZone = seoulTimeZone
        resultTime.timeZone = seoulTimeZone

        //종료시간 설정 warning 발생하는데 해당 값은 null 이면 최상단에서 exception 발생 시키기 때문에 문제 없음.
        endTime.time = format.parse(endDate)

        //시작시간 설정
        startTime.time = format.parse(startDate)
    }

    /**
     * 종료시간이 시작시간보다 큰지 여부
     * @return
     */
    val isAfterTime: Boolean
        get() = endTime.after(currentTime)

    /**
     * 남은시간 구하는
     * @param type
     * @return
     */
    fun getDisplayTime(type: DisplayType): String {
        val f: NumberFormat = DecimalFormat("00")
        parseTime()
        when (type) {
            DisplayType.HOUR -> {
                //시간부터 표시
                //남은시간이 99시간 이상이면 최대값(99:59:59) 로 처리
                return if (mHour > 99) {
                    "99:59:59"
                } else "${f.format(mHour)}:${f.format(mMinute)}:${f.format(mSecond)}"
            }
            DisplayType.MINUTE -> {
                //분부터 표시
                return "$mMinute:${f.format(mSecond)}"
            }
            DisplayType.MINUTE_MM -> {
                //분부터 표시 (MM)
                return "${f.format(mMinute)}:${f.format(mSecond)}"
            }
            DisplayType.SECOND -> {
                //초부터 표시
                return f.format(mSecond)
            }
            else -> return ""
        }
    }

    /**
     * 남은시간을 시간포맷에 맞게 리턴한다.
     * @return 남은시간
     */
    fun parseTime() {
        try {
            if (isAfterTime) {
                val diffTime = endTime.timeInMillis - currentTime.timeInMillis
                resultTime.timeInMillis = diffTime
                mDay = resultTime[Calendar.DATE]
                mHour = if (mDay > 1) {
                    //며칠인지 표현 안하고 시간으로만 표시하기 위해 처리
                    (mDay - 1) * 24 + (resultTime[Calendar.HOUR_OF_DAY] - 9)
                } else {
                    resultTime[Calendar.HOUR_OF_DAY] - 9
                }
                mMinute = resultTime[Calendar.MINUTE]
                mSecond = resultTime[Calendar.SECOND]
            }
        } catch (e: Exception) {
            Ln.e(e)
        }
    }

    /**
     * get mHour
     *
     * @return mHour
     */
    fun getHour(): Int {
        return mHour
    }

    /**
     * get mMinute
     *
     * @return mMinute
     */
    fun getMinute(): Int {
        return mMinute
    }
}