package gsshop.mobile.v2.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.support.ui.CharacterWrapTextView
import gsshop.mobile.v2.util.DateUtils.getDifferenceDays
import gsshop.mobile.v2.util.DateUtils.getToday
import gsshop.mobile.v2.util.PrefRepositoryNamed.getString
import gsshop.mobile.v2.util.PrefRepositoryNamed.saveString
import gsshop.mobile.v2.web.WebUtils
import roboguice.util.Ln
import java.util.*

/**
 * Created by azota on 2016-07-20.
 */
class CustomToast(private val mContext: Context) : Toast(mContext) {

    @SuppressLint("InflateParams")
    fun showToast(message: String?, duration: Int) {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.custom_toast, null)
        val text = v.findViewById<View>(R.id.txt_message) as TextView
        text.text = message
        show(this, v, duration)
    }

    private fun show(toast: Toast, v: View, duration: Int) {
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = duration
        toast.view = v
        toast.show()
    }

    /**
     * 라이브톡 팝업
     * @param message
     * @param duration
     */
    @SuppressLint("InflateParams")
    fun showLiveTalkToast(message: String?, duration: Int, bottom: Int) {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.livetalk_custom_toast, null)
        val text = v.findViewById<View>(R.id.txt_message) as CharacterWrapTextView
        text.text = message
        showLiveTalk(this, v, duration, bottom)
    }

    private fun showLiveTalk(toast: Toast, v: View, duration: Int, bottom: Int) {
        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, bottom)
        toast.duration = duration
        toast.view = v
        toast.show()
    }

    companion object {
        /**
         * 선언된 GS Fresh 토스트 반환
         * @return 선언된 GS Fresh 토스트
         */
        private lateinit var mToast: View
        private var mGSSuperToastShowNow = false

        private var mContent : SectionContentList? = null

        // 토스트 팝업 출력 시간
        const val TIMER_GSHOP_TOAST_MILLISEC = 10000 // 10초

        private var timerTask: TimerTask? = null
        /**
         * TV 편성표 방송 알람 취소.
         */
        @SuppressLint("InflateParams")
        @JvmStatic
        fun makeTVScheduleBroadAlarmCancel(context: Context, duration: Int): Toast {
            val result = Toast(context)
            val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = inflate.inflate(R.layout.toast_tv_schedule_broad_alarm_cancel, null)
            result.view = v
            result.setGravity(Gravity.FILL, 0, 0)
            result.duration = duration
            return result
        }

        /**
         * 모바일라이브 방송 알람 등록
         */
        @SuppressLint("InflateParams")
        @JvmStatic
        fun mobildLiveAlarmOn(context: Context, duration: Int): Toast {
            val result = Toast(context)
            val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = inflate.inflate(R.layout.mobilelive_alarmon_dialog, null)
            result.view = v
            result.setGravity(Gravity.FILL, 0, 0)
            result.duration = duration
            return result
        }

        /**
         * 관심 카테고리 설정.
         */
        private val MESSAGE_COLOR = Color.parseColor("#bed730")
        @SuppressLint("InflateParams")
        @JvmStatic
        fun makeInterestCategorySetting(context: Context, isChecked: Boolean, duration: Int): Toast {
            val toast = Toast(context)
            val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflate.inflate(R.layout.toast_interest_category_dialog, null)
            val toastText = toastView.findViewById<View>(R.id.text_toast) as TextView
            toast.view = toastView
            toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.duration = duration
            val wordToSpan: Spannable
            if (isChecked) {
                wordToSpan = SpannableString("즐겨찾는 카테고리로 설정되었습니다.")
                wordToSpan.setSpan(ForegroundColorSpan(MESSAGE_COLOR), 11, wordToSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                wordToSpan = SpannableString("즐겨찾는 카테고리에서 해제되었습니다.")
                //                wordtoSpan.setSpan(new ForegroundColorSpan(MESSAGE_COLOR), 8, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            toastText.text = wordToSpan
            return toast
        }

        @JvmStatic
        fun setGSSuperIsShowNow(showNow: Boolean) {
            mGSSuperToastShowNow = showNow
        }

        /**
         * GSSuper 토스트를 보여줌.
         * @param context
         */
        @JvmStatic
        fun showGSSuperToast(context: Context, isNowShow: Boolean) {
//        지금 보여주어야 하는지 확인.
            if (!isNowShow && !mGSSuperToastShowNow) {
                return
            }
            val toDay = getToday("yyyyMMdd")
            val oldDay = getString(context, Keys.PREF.FILE_PREF_GSSUPER,
                    Keys.PREF.PREF_GS_SUPER_TOAST_CHECH_TIME)

            // 하루에 최대 한번만 보여주는 토스트이기 때문에 날짜 체크
            if (TextUtils.isEmpty(oldDay) || !TextUtils.isEmpty(oldDay) && getDifferenceDays(oldDay, toDay) > 0) {
                if (::mToast.isInitialized) {

                    mToast.visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                    animation.reset()
                    mToast.clearAnimation()
                    mToast.startAnimation(animation)

                    executeTask(TIMER_GSHOP_TOAST_MILLISEC)
                }
//                saveString(context, Keys.PREF.FILE_PREF_GSSUPER,
//                    Keys.PREF.PREF_GS_SUPER_TOAST_CHECH_TIME, toDay)
            }
        }

        private fun executeTask(delay: Int) {
            try {
                if (timerTask != null) {
                    timerTask?.cancel()
                    timerTask = null
                }
                timerTask = object : TimerTask() {
                    override fun run() {
                        ThreadUtils.runInUiThread {
                            dismissGSToast()
                        }
                    }
                }
            }
            catch (e:IllegalStateException){
                Ln.e(e.message)
            }
            catch (e:NullPointerException) {
                Ln.e(e.message)
            }
            Timer().schedule(timerTask, delay.toLong())
        }

        /**
         * 선언된 GS Fresh 토스트 반환
         * @return 선언된 GS Fresh 토스트
         */
        @JvmStatic
        fun getGSSuperToast(): View? {
            return if (::mToast.isInitialized)
                mToast
            else null
        }

        /**
         * 실제로 토스트를 생성하는 구문
         * @param context
         * @param content SectionContentList 를 받아 만드는 토스트
         * @return
         */
        @SuppressLint("SetTextI18n")
        @JvmStatic
        fun makeGSSuperToast(context: Activity?, content: SectionContentList?) {
            if (content != null)
                mContent = content

            if (mContent == null || context == null) {
                return
            }
            //        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            var viewToast: View? = null
            val txtPre: TextView
            val txtTime: TextView
            val txtDetail: TextView
            val viewClose: View
            val viewContent: View
            try {
                viewToast = context.findViewById(R.id.toast_gsfresh)
                txtPre = viewToast.findViewById<View>(R.id.txt_pre) as TextView
                txtTime = viewToast.findViewById<View>(R.id.txt_time) as TextView
                txtDetail = viewToast.findViewById<View>(R.id.txt_show_detail) as TextView
                viewClose = viewToast.findViewById(R.id.view_close) as View
                viewContent = viewToast.findViewById(R.id.view_content) as View
                txtPre.text = mContent?.productName
                txtTime.text = " " + mContent?.promotionName
                val strContent = SpannableString(mContent?.etcText1)
                strContent.setSpan(UnderlineSpan(), 0, strContent.length, 0)
                txtDetail.text = strContent
                val linkUrl = mContent?.linkUrl
                viewContent.setOnClickListener { // 시간 확인 버튼 선택시와 동일한 동작.
                    WebUtils.goWeb(context, linkUrl)
                }
                viewClose.setOnClickListener {
                    ThreadUtils.runInUiThread(
                            Runnable {
                                val toDay = getToday("yyyyMMdd")
                                saveString(context, Keys.PREF.FILE_PREF_GSSUPER,
                                        Keys.PREF.PREF_GS_SUPER_TOAST_CHECH_TIME, toDay)
                                dismissGSToast()
                            }
                    )
                }
            } catch (e: Exception) {
                Ln.e(e.message)
            }
            if (viewToast != null) {
                mToast = viewToast
            }
        }

        @JvmStatic
        fun dismissGSToast() {
            if (::mToast.isInitialized) {
                if (mToast.visibility == View.GONE) return
                mToast.visibility = View.GONE
                val animation = AnimationUtils.loadAnimation(mToast.context, R.anim.fade_out)
                animation.reset()
                mToast.clearAnimation()
                mToast.startAnimation(animation)
            }
        }
    }

}