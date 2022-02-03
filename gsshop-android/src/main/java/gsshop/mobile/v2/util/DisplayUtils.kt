package gsshop.mobile.v2.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import com.blankj.utilcode.util.EmptyUtils
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.ServerUrls
import roboguice.util.Ln

//Utility 정리(모카 포함)
object DisplayUtils {
    /**
     * api 에서 받은 color 값에 # 포함 여부에 따른 처리와
     * human error 인 경우 무조건 검정색으로 반환해주는 함수
     */
    @JvmStatic
    fun parseColor(color: String): Int {
        return try {
            if (color.contains("#")) {
                Color.parseColor(color)
            } else {
                Color.parseColor("#$color")
            }
        } catch (e: IllegalArgumentException) {
            Color.parseColor("#111111")
        } catch (e: NullPointerException) {
            Color.parseColor("#111111")
        }
    }

    /**
     * dp to px
     */
    @JvmStatic
    fun convertDpToPx(context: Context, dp: Float): Int {
        val metrics = context.resources.displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return px.toInt()
    }

    /**
     * px to dp
     */
    @JvmStatic
    fun convertPxToDp(context: Context, px: Float): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return dp.toInt()
    }

    /**
     * 해상도에 따라 view의 높이 조절.
     * 해당 함수에 문제가 많음, 되도록 쓰지 않는게 좋을듯.
     */
    @JvmStatic
    fun resizeHeightAtViewToScreenSize(context: Context, view: View) {
        if (EmptyUtils.isEmpty(context) || EmptyUtils.isEmpty(view)) {
            return
        }
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val height = view.layoutParams.height.toFloat()
        val ratio = height / baseWidth

        // view의 height 조절하기.
        val metrics = context.resources.displayMetrics
        view.layoutParams.height = (metrics.widthPixels * ratio).toInt()
        view.requestLayout()
    }

    @JvmStatic
    fun resizeHeightAtViewToScreen(context: Context, viewHeight: Float): Int {
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val ratio = viewHeight / baseWidth

        // view의 height 조절하기.
        val metrics = context.resources.displayMetrics
        return (metrics.widthPixels * ratio).toInt()
    }

    /**
     * 해상도에 따라 view의 높이 조절.(TV 쇼핑 Viewpager용)
     */
    @JvmStatic
    fun resizeHeightAtViewToScreenPageWidth(context: Context, view: View) {
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val height = view.layoutParams.height.toFloat()
        val ratio = height / baseWidth

        // view의 height 조절하기.
        val metrics = context.resources.displayMetrics
        view.layoutParams.height = (metrics.widthPixels * ratio).toInt()
        view.requestLayout()
    }

    /**
     * 해상도에 따라 view의 가로/세로 높이 조절.
     */
    @JvmStatic
    fun resizeAtViewToScreen(context: Context, view: View) {
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val height = view.layoutParams.height.toFloat()
        val width = view.layoutParams.width.toFloat()
        val ratioHeight = height / baseWidth
        val ratioWidth = width / baseWidth

        // view의 height 조절하기.
        val metrics = context.resources.displayMetrics
        view.layoutParams.height = (metrics.widthPixels * ratioHeight).toInt()
        view.layoutParams.width = (metrics.widthPixels * ratioWidth).toInt()
        view.requestLayout()
    }

    /**
     * 해상도에 따라 view의 너비 조절.
     */
    @JvmStatic
    fun resizeWidthAtViewToScreen(context: Context, view: View) {
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val width = view.layoutParams.width.toFloat()
        val ratioWidth = width / baseWidth

        // view의 height 조절하기.
        val metrics = context.resources.displayMetrics
        view.layoutParams.width = (metrics.widthPixels * ratioWidth).toInt()
        view.requestLayout()
    }

    /**
     * 해상도에 따라 view의 높이 조절.
     */
    @JvmStatic
    fun resizeHeightAtViewToScreenSize(context: Context, view: View, height: Int) {
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val ratio = height / baseWidth

        // view의 height 조절하기.
        val metrics = context.resources.displayMetrics
        view.layoutParams.height = (metrics.widthPixels * ratio).toInt()
        view.requestLayout()
    }

    /**
     * 해상도에 따라 view의 넓이 조절.
     */
    @JvmStatic
    fun getResizeWidthAtViewToScreenSize(context: Context, width: Int): Int {
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val ratio = width / baseWidth

        // view의 width 조절하기.
        val metrics = context.resources.displayMetrics
        return (metrics.widthPixels * ratio).toInt()
    }

    /**
     * 해상도에 따라 view의 넓이 조절.
     */
    @JvmStatic
    fun resizeWidthAtViewToScreenSize(context: Context, view: View) {
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val width = view.layoutParams.width.toFloat()
        val ratio = width / baseWidth

        // view의 width 조절하기.
        val metrics = context.resources.displayMetrics
        view.layoutParams.width = (metrics.widthPixels * ratio).toInt()
        view.requestLayout()
    }

    /**
     * 해상도에 따라 px 크기 조절.
     */
    @JvmStatic
    fun getResizedPixelSizeToScreenSize(context: Context, px: Int): Int {
        // origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
        val baseWidth = convertDpToPx(context, 360f).toFloat()
        val ratio = px / baseWidth

        // px 조절하기.
        val metrics = context.resources.displayMetrics
        return (metrics.widthPixels * ratio).toInt()
    }

    /**
     * 숫자로 이루어진 string에 천단위 마다 콤마 추가
     * 포함되겠지만 안 되어 있을 경우에 대한 예외 처리 포함.
     *
     * @param number
     * @return
     */
    @JvmStatic
    fun getFormattedNumber(number: String?): String {
        return if (number == null) {
            ""
        } else {
            if (number.contains(",")) {
                number
            } else {
                // 뒤에서부터 3자리씩 잘라온 다음에
                val tokens = splitInParts(number)

                // 마지막 자리는 빼고 콤마 붙이기
                val sb = StringBuilder()
                val size = tokens.size
                for (i in 0 until size) {
                    sb.append(tokens[i])
                    if (i < size - 1) {
                        sb.append(",")
                    }
                }
                sb.toString()
            }
        }
    }

    @JvmStatic
    fun isNumeric(s: String): Boolean {
        var numberString = s
        numberString = numberString.replace(",".toRegex(), "")
        return numberString.matches("[-+]?\\d*\\.?\\d+".toRegex())
    }

    /**
     * 숫자로 이루어진 string을 뒤에서부터 세 자리씩 잘라서 string array로 return.
     *
     * @param str
     * @param partLength
     * @return
     */
    private fun splitInParts(str: String, partLength: Int = 3): Array<String?> {
        val rStr = StringBuilder(str).reverse().toString()
        val len = rStr.length

        // Number of parts
        val nparts = (len + partLength - 1) / partLength
        val parts = arrayOfNulls<String>(nparts)

        // Break into parts
        var offset = 0
        var i = nparts - 1
        while (i >= 0) {
            val rPart = StringBuilder(
                    rStr.substring(offset, (offset + partLength).coerceAtMost(len))).reverse()
                    .toString()
            parts[i] = rPart
            offset += partLength
            i--
        }
        return parts
    }

    /**
     * string이 유효한지 체크. 공백만 포함할 경우에는 invalid.
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isValidString(str: String?): Boolean {
        return !(str == null || str.isEmpty() || org.springframework.util.StringUtils.trimAllWhitespace(str).isEmpty())
    }

    /**
     * string이 "true"인지 체크
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isTrue(str: String?): Boolean {
        return isValidString(str) && java.lang.Boolean.parseBoolean(str)
    }

    /**
     * 가격이나 개수 string이 유효한지 체크
     * string에 콤마가 들어 있으면 삭제 마이너스값에 대해서도 처리
     *
     * @param str 체크대상 스트링
     * @return 0~9로 이루어진 스트링이면 true 리턴
     */
    @JvmStatic
    fun isValidNumberString(str: String?): Boolean {
        return if (isValidString(str)) {
            val result = str!!.replace(",".toRegex(), "")

            // jhkim
            // result가 정수가 아닐 경우, 예) abc NumberFormatException 발생
            // (코드 리뷰) 예외 처리 대신 StringUtils의 isNumeric 사용
            StringUtils.isNumeric(result)
        } else {
            false
        }
    }

    /**
     * isValidNumberString 체크 + "0" 체크 ("0"은 invalid number)
     *
     * @param str 체크대상 스트링
     * @return 0~9로 이루어진 스트링이고 0이 아니면 true 리턴
     */
    @JvmStatic
    fun isValidNumberStringExceptZero(str: String?): Boolean {
        return if (isValidString(str)) {
            val result = str!!.replace(",".toRegex(), "")
            StringUtils.isNumeric(result) && result.toInt() != 0
        } else {
            false
        }
    }

    /**
     * string에서 integer값 가져오기. 마이너스값도 유효.
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun getNumberFromString(str: String?): Int {
        if (str == null || !isNumeric(str)) {
            return 0
        }
        return if (isValidString(str)) {
            try {
                val result = str.replace(",".toRegex(), "")
                return result.toInt()
            } catch (e: Exception) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e)
            }
            0
        } else {
            0
        }
    }

    /**
     * url이 host 정보를 포함하고 있지 않으면 붙여서 return
     *
     * @param linkUrl
     * @return
     */
    @JvmStatic
    fun getFullUrl(linkUrl: String): String {
        return if (!TextUtils.isEmpty(linkUrl) && linkUrl.startsWith("/")) {
            ServerUrls.getHttpRoot() + linkUrl
        } else {
            linkUrl
        }
    }

    /**
     * 상태바 높이를 구한다.
     *
     * @param context 컨텍스트
     * @return 상태바 높이
     */
    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 뷰들에 대한 높이를 스크린 크기에 맞춘다.
     *
     * @param views
     */
    @JvmStatic
    fun resizeHeightAtViewsToScreenSize(vararg views: View) {
        val arrViews: Array<View> = arrayOf(*views)
        for (viewTemp in arrViews) {
            if (EmptyUtils.isNotEmpty(viewTemp)) {
                resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), viewTemp)
            }
        }
    }

    /**
     * 뷰의 가로, 세로 크기를 변경해서 반환해준다.
     *
     * @param context  Context
     * @param view     View
     * @param dpWidth  width
     * @param dpHeight height
     * @return view
     */
    @JvmStatic
    fun changeViewSizeInDp(context: Context, view: View, dpWidth: Int, dpHeight: Int): View {
        val widthInDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth.toFloat(), context.resources.displayMetrics).toInt()
        val heightInDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight.toFloat(), context.resources.displayMetrics).toInt()
        view.layoutParams.width = widthInDp
        view.layoutParams.height = heightInDp
        view.requestLayout()
        return view
    }

    /**
     * 뷰가 스크린 안에 보이는 상태인지 벗어난 상태인지 반환해준다.
     *
     * @param view
     * @return visibility on screen
     */
    @JvmStatic
    fun isVisible(view: View?): Boolean {
        if (view == null) {
            return false
        }
        if (!view.isShown) {
            return false
        }
        val actualPosition = Rect()
        view.getGlobalVisibleRect(actualPosition)
        val screen = Rect(0, 0, screenWidth, screenHeight)
        return actualPosition.intersect(screen)
    }

    /**
     * 스크린의 가로 크기를 반환
     *
     * @return screen widthPixels
     */
    @JvmStatic
    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    /**
     * 스크린의 세로 크기를 반환
     *
     * @return screen heightPixels
     */
    @JvmStatic
    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    @JvmStatic
    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter: ListAdapter = listView.adapter?: // pre-condition
                return
        var totalHeight = 0
        val desiredWidth: Int = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.AT_MOST)
        for (i in 0 until listAdapter.count) {
            val listItem: View = listAdapter.getView(i, null, listView)
            //listItem.measure(0, 0);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }
        val params: ViewGroup.LayoutParams = listView.layoutParams
        params.height = totalHeight
        listView.layoutParams = params
        listView.requestLayout()
    }
}