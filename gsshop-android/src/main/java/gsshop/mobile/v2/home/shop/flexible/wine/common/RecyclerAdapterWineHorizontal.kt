package gsshop.mobile.v2.home.shop.flexible.wine.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList

class RecyclerAdapterWineHorizontal (val context: Context, val subProductList: ArrayList<SectionContentList>,
                                     var viewID: Int?, var isRankVisible: Boolean?)
    : RecyclerView.Adapter<RecyclerAdapterWineHorizontal.ItemViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        if (viewID == null) {
            viewID = R.layout.banner_item_prd_pmo_list_wine
        }

        val itemView = ViewWineItem(context)
        itemView.setView(viewID)

        return ItemViewHolder(itemView, isRankVisible)
    }

    override fun getItemCount(): Int {
        return subProductList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = subProductList[position]

        holder.setItem(item, position)
    }

    /**
     * 뷰홀더
     */
    class ItemViewHolder(itemView: View, isRankVisible: Boolean?) : RecyclerView.ViewHolder(itemView) {
        val rankVisible = isRankVisible
        fun setItem(sectionContentList: SectionContentList, position: Int) {
            (itemView as ViewWineItem).setItems(sectionContentList, position, rankVisible)
        }
//        val imageMain: ImageView = itemView.findViewById(R.id.image_main)
//        val textCnt : TextView = itemView.findViewById(R.id.txt_count)
//        val layoutAdd : LinearLayout = itemView.findViewById(R.id.layout_add_sub)
//        val textMain : TextView = itemView.findViewById(R.id.txt_main)
//        val textSub : TextView = itemView.findViewById(R.id.txt_sub)
//        val textPrice : TextView = itemView.findViewById(R.id.txt_price)
//        val textPriceUnit : TextView = itemView.findViewById(R.id.txt_price_unit)
//        val textReviewAvr : TextView = itemView.findViewById(R.id.txt_review_avr)
//        val textReviewCnt : TextView = itemView.findViewById(R.id.txt_review_count)
    }
}
