package gsshop.mobile.v2.home.shop.flexible.beauty.common

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
import kotlinx.android.synthetic.main.view_holder_item_tab_anch_wine.view.*
import roboguice.util.Ln
import java.lang.IndexOutOfBoundsException
import java.lang.NullPointerException

/**
 * 다른데서도 사용할 수 있게끔 만들려고 하였음 기본적으로 무리가 있긴 하다.( 위치 기억 등 )
 */
class AdapterMenuCommon
constructor(private val context: Context?, private val list: List<SectionContentList>,
                private val recycler: RecyclerView)
    : RecyclerView.Adapter<AdapterMenuCommon.ItemViewHolder>() {

    private var selectedItemIndex = 0

    private var selectedBackColor = Color.parseColor("#dea35e")

    private val mViewList: HashMap<Int, ItemViewHolder> = HashMap()

    private var mSelectedItemResourceId = -1

    private var mSelectedItemColor = -1

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        val viewItem: View = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.view_holder_item_tab_anch_wine, viewGroup, false)
        val holder = ItemViewHolder(viewItem)
//        mViewList.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.txtTitle.text = list[position].name
        holder.cardTxtTitle.text = list[position].name
        holder.cardTxtTitle.setBackgroundColor(selectedBackColor)

        holder.itemView.setOnClickListener {
            if (selectedItemIndex == position) {
                return@setOnClickListener
            }

            selectedItemIndex = position

            setItemStyle(holder)
        }

        if (selectedItemIndex == position) {
            setEnabledStyle(holder)
        } else {
            setDefaultStyle(holder)
        }
        try {
            mViewList[position] = holder
        }
        catch (e:IndexOutOfBoundsException) {
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

        for (list in mViewList) {
            try {
//                val view: View = recycler.getChildAt(i)
//                val vh = recycler.getChildViewHolder(view) as ItemViewHolder
                setDefaultStyle(list.value)
            }
            catch (e:NullPointerException) {
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
        if (mSelectedItemResourceId > 0) {
            holder.cardTxtTitle.setBackgroundResource(mSelectedItemResourceId)
        }
        else if (mSelectedItemColor > 0) {

            holder.cardTxtTitle.setBackgroundColor(mSelectedItemColor)
        }
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
//
//        val view: View = recycler.getChildAt(position)
//        val vh = recycler.getChildViewHolder(view) as ItemViewHolder
//
//        setItemStyle(vh)
    }

    fun getSelectedItem() :Int {
        return selectedItemIndex
    }

    fun setSelectedItemBackground(resourceId :Int) {
        mSelectedItemResourceId = resourceId
    }

    fun setSelectedItemColor(color: Int) {
        mSelectedItemColor = color
    }

}