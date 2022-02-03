package gsshop.mobile.v2.home.shop.flexible.wine

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.blankj.utilcode.util.KeyboardUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.search.SearchNavigation
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_menu_sld_wine.view.*

class MenuSldWindVH (itemView: View) : BaseViewHolder(itemView) {
//    private val mRootView = itemView.rootView

    init {
        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true
    }

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        // 2단 원형 메뉴 리사이클러에 설정할 데이터 및 뷰 아이디 설정
        itemView.rootView.recycler_rolling_menu.setItems(item, R.layout.recycler_item_menu_sld_wine)

        // 검색 에디트 바 설정
        val keywordEdit = itemView.rootView.edit_search

        if (keywordEdit != null) {

//            keywordEdit!!!!.setOnClickListener {
//                // 메인 탭 접기
//                EventBus.getDefault().post(MainTabExpandEvent(false))
//            }

            keywordEdit!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                // 키패드에서 검색키(돋보기모양)를 누른 경우 검색 처리
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyboardUtils.hideSoftInput(context as Activity)

                    val keyword = keywordEdit!!.text.toString().trim()
                    if (!TextUtils.isEmpty(keyword)) {
                        keywordEdit!!.text.clear()
                        keywordEdit!!.clearFocus()

                        // 시작부분의 %, / 특수문자 제거
                        val searchKey = keyword.replaceFirst("^[/%]+".toRegex(), "")
                        WebUtils.goWeb(context, item.linkUrl + SearchNavigation.encodingQuery(searchKey))
                    }
                    else {
                        val dialog: Dialog = CustomOneButtonDialog(context as Activity?)
                                .message(R.string.search_description_search_main).buttonClick { dialog ->
                                    dialog.dismiss()
                                    keywordEdit!!.requestFocus()
                                    Handler().postDelayed({ KeyboardUtils.showSoftInput(context as Activity?) }, 100)
                                }
                        dialog.show()
                    }
                    true
                }
                false
            })
        }
    }

    fun cleanSearchBarText(activity: Activity) {
        itemView.rootView.edit_search.text.clear()
        itemView.rootView.edit_search.clearFocus()
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(itemView.rootView.edit_search.windowToken, 0)
    }

}