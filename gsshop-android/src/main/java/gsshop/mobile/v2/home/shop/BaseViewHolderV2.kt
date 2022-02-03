package gsshop.mobile.v2.home.shop

import android.content.Context
import android.view.View
import gsshop.mobile.v2.home.main.ModuleList

//**
/**
 * 기본 view holder version2
 */
abstract class BaseViewHolderV2
/**
 * @param itemView
 */
(itemView: View?) : BaseViewHolder(itemView) {

    // 앵커용 tabSeq 구분용
    var tabSeq: String? = null
    /**
     * 신규 뷰타입 용
     * 모든 유형에 대해서 super 를 호출해야 tabSeq를 지정할 수 있다.
     */
    open fun onBindViewHolder(context: Context, position: Int, moduleList: MutableList<ModuleList>?) {
        if (moduleList != null) {
            val item = moduleList[position]
            tabSeq = item.tabSeq
        }
    }

    /**
     * 신규 뷰타입 용 v2
     * moduleList를 넘겨주고 position을 같이 넘겨줘서 안에서 꺼내 쓰는 함수와 다르게 이건 꺼내서 줌.
     */
    open fun onBindViewHolder(context: Context, moduleList: ModuleList?):View? {
        tabSeq = moduleList?.tabSeq
        return null
    }

    /***********************
    헤더 및 앵커 관련 함수들
     ***********************/
    override fun setIsHeader(isHeader: Boolean) {}

    open fun setSelectedItem(selctedItem: Int) {}

    open fun setSelectedTabPosition(tabSeq: String):Boolean {return true}
    /***********************/

}