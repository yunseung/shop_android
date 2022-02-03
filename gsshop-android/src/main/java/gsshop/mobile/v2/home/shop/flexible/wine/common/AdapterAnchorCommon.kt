package gsshop.mobile.v2.home.shop.flexible.wine.common

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.EmptyUtils
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.HomeActivity
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment
import gsshop.mobile.v2.util.ClickUtils
import kotlinx.android.synthetic.main.view_holder_item_tab_anch_wine.view.*

/**
 * 다른데서도 사용할 수 있게끔 만들려고 하였음 기본적으로 무리가 있긴 하다.( 위치 기억 등 )
 */
class AdapterAnchorCommon
constructor(private val context: Context, private val list: List<SectionContentList>,
                private val recycler: RecyclerView, private val navigationId: String,
            private val isHeader: Boolean, private val fixedItemIndex: Int)
    : RecyclerView.Adapter<AdapterAnchorCommon.ItemViewHolder>() {

    private var selectedItemIndex = fixedItemIndex

    private var selectedBackColor = Color.parseColor("#74bc3c")

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val viewItem: View = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.view_holder_item_tab_anch_wine, viewGroup, false)
        return ItemViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.txtTitle.text = list[position].name
        holder.cardTxtTitle.text = list[position].name

        holder.itemView.tag = list[position].tabSeq
        holder.itemView.setOnClickListener{
            if (selectedItemIndex == position) {
                return@setOnClickListener
            }
            if (ClickUtils.work(500)) {
                return@setOnClickListener
            }
            //매장 스크롤 정지
            EventBus.getDefault().post(Events.FlexibleEvent.EventWine.StopScrollEvent())
            val sectionCode = list[position].tabSeq

            if (EmptyUtils.isNotEmpty(FlexibleShopFragment.tabPrdMapWine[sectionCode])) {
                // 클릭 이벤트 전달. (해당 부분으로 이동 위함)
                EventBus.getDefault().post(Events.FlexibleEvent.EventWine.SectionClickEvent(sectionCode))
                selectedItemIndex = position
                if (isHeader) {
                    //해더인 경우만 변경
                    setItemStyle(holder)
                }
                //와이즈로그 호출
                (context as HomeActivity).setWiseLog(list[position].wiseLog)
            }
        }

        val idx = if (isHeader) {
            selectedItemIndex
        } else {
            fixedItemIndex
        }

        if (idx == position) {
            setEnabledStyle(holder)
        } else {
            setDefaultStyle(holder)
        }
    }

    override fun getItemCount(): Int {
        return if (EmptyUtils.isNotEmpty(list)) list.size else 0
    }

    /**
     * 카테고리 뷰홀더
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.rootView.txt_title
        val cardTxtTitle: TextView = itemView.rootView.card_txt_title
        var cardTitle: CardView = itemView.rootView.card_title
    }

    /**
     * 탭메뉴 enable/default 상태에 따른 UI를 설정한다.
     *
     * @param holder ItemViewHolder
     */
    private fun setItemStyle(holder: ItemViewHolder) {
        //모든 아이템 테두리를 디폴트색으로 세팅
        for (i in 0 until recycler.childCount) {
            val view: View = recycler.getChildAt(i)
            val vh = recycler.getChildViewHolder(view) as ItemViewHolder
            setDefaultStyle(vh)
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
        holder.cardTitle.visibility = View.VISIBLE
        holder.txtTitle.visibility = View.GONE
    }

    /**
     * 디폴트상태 UI 세팅
     *
     * @param holder ItemViewHolder
     */
    private fun setDefaultStyle(holder: ItemViewHolder) {
        holder.cardTitle.visibility = View.GONE
        holder.txtTitle.visibility = View.VISIBLE
    }

    fun setSelectedItem (position: Int) {
        selectedItemIndex = position

        val view: View = recycler.getChildAt(position)
        val vh = recycler.getChildViewHolder(view) as ItemViewHolder

        setItemStyle(vh)
    }
}