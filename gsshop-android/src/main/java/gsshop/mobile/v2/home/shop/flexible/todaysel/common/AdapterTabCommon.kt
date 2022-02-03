package gsshop.mobile.v2.home.shop.flexible.todaysel.common

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.EmptyUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.flexible.beauty.common.AdapterMenuCommon
import kotlinx.android.synthetic.main.view_holder_gr_tab_today_sel_item.view.*
import roboguice.util.Ln
import java.lang.IndexOutOfBoundsException
import java.lang.NullPointerException

/**
 * 다른데서도 사용할 수 있게끔 만들려고 하였음 기본적으로 무리가 있긴 하다.( 위치 기억 등 )
 */
class AdapterTabCommon
constructor(private val context: Context, private val list: List<SectionContentList>,
            private val recycler: RecyclerView, private val fixedItemIndex: Int
)
    : RecyclerView.Adapter<AdapterTabCommon.ItemViewHolder>() {

    private var selectedItemIndex = fixedItemIndex

    private val mViewList: HashMap<Int, AdapterTabCommon.ItemViewHolder> = HashMap()

    private var selectedBackColor = Color.parseColor("#74bc3c")

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val viewItem: View = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.view_holder_gr_tab_today_sel_item, viewGroup, false)
        return ItemViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.titleNor.text = list[position].name
        holder.titleSel.text = list[position].name

        if (selectedItemIndex == position) {
            setEnabledStyle(holder)
        } else {
            setDefaultStyle(holder)
        }

        try {
            mViewList[position] = holder
        }
        catch (e: IndexOutOfBoundsException) {
            Ln.e(e.message)
        }
    }

    override fun getItemCount(): Int {
        return if (EmptyUtils.isNotEmpty(list)) list.size else 0
    }

    /**
     * 카테고리 뷰홀더
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleSel: TextView = itemView.rootView.card_txt_title_sel
        val titleNor: TextView = itemView.rootView.card_txt_title
        var cardSel: CardView = itemView.rootView.card_title_sel
        var cardNor: CardView = itemView.rootView.card_title
    }

    /**
     * 탭메뉴 enable/default 상태에 따른 UI를 설정한다.
     *
     * @param holder ItemViewHolder
     */
    private fun setItemStyle(holder: ItemViewHolder) {
        //모든 아이템 테두리를 디폴트색으로 세팅
        for (list in mViewList) {
            try {
                setDefaultStyle(list.value)
            }
            catch (e: NullPointerException) {
                Ln.e(e.message)
            }
            catch (e:IndexOutOfBoundsException) {
                Ln.e(e.message)
            }
        }
        //선택된 아이템 테두리 세팅, 볼드체
        setEnabledStyle(holder)
    }

    /**
     * 선택상태 UI 세팅
     *
     * @param holder ItemViewHolder
     */
    private fun setEnabledStyle(holder: ItemViewHolder) {
        holder.cardSel.visibility = View.VISIBLE
        holder.cardNor.visibility = View.GONE
    }

    /**
     * 디폴트상태 UI 세팅
     *
     * @param holder ItemViewHolder
     */
    private fun setDefaultStyle(holder: ItemViewHolder) {
        holder.cardSel.visibility = View.GONE
        holder.cardNor.visibility = View.VISIBLE
    }

    fun setSelectedItem (position: Int) {
        selectedItemIndex = position

        val view: View = recycler.getChildAt(position)
        val vh = recycler.getChildViewHolder(view) as ItemViewHolder

        setItemStyle(vh)
    }
}