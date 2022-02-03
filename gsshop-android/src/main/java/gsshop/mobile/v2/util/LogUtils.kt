/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.content.Context
import roboguice.util.Ln
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object LogUtils {
    /**
     * 모듈의 실행시작 시간을 전달받아 실행시간을 표시한다.<br></br>
     * 함수 첫 부분에 startTime을 세팅하고, 함수 마지막 부분에 본 함수를 호출하면 됨
     *
     * @param tag 태그
     * @param moduleName 모듈명
     * @param startTime 모듈 시작시간
     */
	@JvmStatic
	fun printExeTime(moduleName: String?, startTime: Long) {
        Ln.i(System.currentTimeMillis().toString() + "(" + (System.currentTimeMillis()-startTime) + "ms) " + moduleName);
    }

    /**
     * 로그를 파일로 저장한다.(자동로그인 풀림현상 분석을 위해 임시 사용)
     *
     * @param content 파일에 저장할 내용
     */
    @Throws(IOException::class)
    fun saveLogToFile(context: Context, content: String?) {
        var bw: BufferedWriter? = null
        var fw: FileWriter? = null
        try {
            val dir = File(context.getExternalFilesDir(null)?.absolutePath, "GSSHOP_LOG")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, DateUtils.getToday("yyyy-MM-dd kk:00:00") + ".txt")
            fw = FileWriter(file, true)
            bw = BufferedWriter(fw)
            bw.write(content)
            bw.newLine()
            bw.flush()
        } catch (e: Exception) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e)
        } finally {
            bw?.close()
            fw?.close()
        }
    }

    /**
     * 로그파일을 저장한다. (파일내용에 저장시간 추가)
     *
     * @param context 컨텍스트
     * @param fileName 파일명
     * @param content 내용
     */
    fun saveLogToFileV2(context: Context?, fileName: String, content: String?) {
        if (context == null) {
            //Toast.makeText(MainApplication.getAppContext(), "context is null", Toast.LENGTH_SHORT).show()
            return
        }
        val saveFile = File(context.getExternalFilesDir(null)?.absolutePath + "/gsshop")
        if (!saveFile.exists()) {
            saveFile.mkdir()
        }
        try {
            val now = System.currentTimeMillis() // 현재시간 받아오기
            val date = Date(now) // Date 객체 생성
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val nowTime = sdf.format(date)
            val buf = BufferedWriter(FileWriter("$saveFile/$fileName", true))
            buf.append("$nowTime ")
            buf.append(content)
            buf.newLine()
            buf.close()
        } catch (e: FileNotFoundException) {
            //saveLogToFileV2(MainApplication.getAppContext(), FILE_NAME, "saveLogToFileV2 > e1: ${e.toString()}")
            Ln.e(e)
        } catch (e: IOException) {
            //saveLogToFileV2(MainApplication.getAppContext(), FILE_NAME, "saveLogToFileV2 > e2: ${e.toString()}")
            Ln.e(e)
        } catch (e: Exception) {
            //saveLogToFileV2(MainApplication.getAppContext(), FILE_NAME, "saveLogToFileV2 > e3: ${e.toString()}")
            Ln.e(e)
        }
    }
}