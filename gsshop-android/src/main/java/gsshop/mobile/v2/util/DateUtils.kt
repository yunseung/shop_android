/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    /**
     * 방송시간 포맷
     */
    const val BROAD_TIME_FORMAT = "yyyyMMddHHmmss"

    /**
     * 오늘 날짜를 리턴한다.
     *
     * @param format 리턴할 날짜 포맷(ex:yyyyMMdd)
     * @return 포맷이 적용된 오늘 날짜
     */
	@JvmStatic
	fun getToday(format: String?): String {
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        val cal = Calendar.getInstance()
        return dateFormat.format(cal.time)
    }

    /**
     * 오늘 날짜기준 n일 후 날짜를 리턴한다.
     *
     * @param format 리턴할 날짜 포맷(ex:yyyyMMdd)
     * @param interval 기간(예&gt;1:내일, 2:모래...)
     * @return 포맷이 적용된 오늘 날짜
     */
    @JvmStatic
    fun getSomeday(format: String?, interval: Int): String {
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, interval)
        return dateFormat.format(cal.time)
    }

    /**
     * 두 날짜의 차이를 일수로 리턴한다.
     *
     * @param start 시작날짜
     * @param end 종료날짜
     * @return 종료날짜-시작날짜<br></br> Exception 발생시 Long.MIN_VALUE를 리턴한다.
     */
	@JvmStatic
	fun getDifferenceDays(start: String?, end: String?): Long {
        var diffDays: Long = 0
        if (start == null || end == null) {
            return diffDays
        }
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        diffDays = try {
            val startDate = formatter.parse(start)
            val endDate = formatter.parse(end)
            val diff = endDate!!.time - startDate!!.time
            diff / (24 * 60 * 60 * 1000)
        } catch (e: ParseException) {
            Long.MIN_VALUE
        } catch (e: NullPointerException) {
            Long.MIN_VALUE
        }
        return diffDays
    }

    /**
     * 방송시간을 리턴한다.
     *
     * @param start 방송시작시간
     * @param end 방송종료시간
     * @return hh:mm:ss or mm:ss
     */
    @JvmStatic
    fun getRemainTime(start: String?, end: String?): String {
        if (start == null || end == null) {
            return "00:00"
        }
        val formatter = SimpleDateFormat(BROAD_TIME_FORMAT, Locale.getDefault())
        var startMills: Long = 0
        var endMills: Long = 0
        try {
            startMills = formatter.parse(start)!!.time
            endMills = formatter.parse(end)!!.time
        } catch (e: ParseException) {
            //ignore exception, will return 00:00
        } catch (e: NullPointerException) {
            //ignore exception, will return 00:00
        }
        val diffMills = endMills - startMills
        val hrs = TimeUnit.MILLISECONDS.toHours(diffMills).toInt()
        val min = TimeUnit.MILLISECONDS.toMinutes(diffMills).toInt() % 60
        val sec = TimeUnit.MILLISECONDS.toSeconds(diffMills).toInt() % 60
        return if (hrs > 0) {
            if (hrs > 99) {
                //표시한계를 넘을경우 고정값
                "99:59:59"
            } else {
                String.format("%02d:%02d:%02d", hrs, min, sec)
            }
        } else {
            String.format("%02d:%02d", min, sec)
        }
    }
}