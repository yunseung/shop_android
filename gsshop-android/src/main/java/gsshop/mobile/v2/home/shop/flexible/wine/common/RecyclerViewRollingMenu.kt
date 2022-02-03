package gsshop.mobile.v2.home.shop.flexible.wine.common

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.EmptyUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.support.ui.RecyclerItemClickListener
import gsshop.mobile.v2.util.StringUtils.trim
import gsshop.mobile.v2.util.SwipeUtils.disableSwipe
import gsshop.mobile.v2.web.WebUtils
import roboguice.util.Ln

/**
 * wine 용 뿐 아니라 공통으로 쓸 수 있는데 2단 원형 메뉴 뷰
 */
class RecyclerViewRollingMenu : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init{
        initLayout()
    }

    private fun initLayout() {
        layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
    }

    fun setItems(item: SectionContentList, viewId: Int?) {
        if (item.subProductList == null) {
            return
        }
        // 카테고리 리스트
        adapter = Adapter(context, item.subProductList!!, viewId)

        this.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> disableSwipe()
            }
            false
        }

        addOnItemTouchListener(
                RecyclerItemClickListener(context, RecyclerItemClickListener.OnItemClickListener {
                    view: View?, position: Int ->
                    WebUtils.goWeb(context,
                            Uri.parse(item.subProductList!!.get(position).linkUrl).buildUpon().appendQueryParameter(
                                    "_", System.currentTimeMillis().toString()).build().toString())
                })
        )
    }

    private class Adapter(val context: Context, val list: ArrayList<SectionContentList>, var viewID: Int?) : RecyclerView.Adapter<ItemViewHolder?>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            if (viewID == null) {
                viewID = R.layout.recycler_item_menu_sld_wine
            }

            val viewItem: View = LayoutInflater.from(parent.context)
                    .inflate(viewID!!, parent, false)
            return ItemViewHolder(viewItem)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            try {
                Glide.with(context).load(trim(list[position].imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).into(holder.itemImage)
                holder.itemText.text = list[position].name
            } catch (er: Exception) {
                Ln.e(er.message)
            }
        }

        override fun getItemCount(): Int {
            return if (EmptyUtils.isNotEmpty(list)) list.size else 0
        }

    }

    // 아이템에 무조건 image_circle, text_main 있어야 한다.
    private class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.image_circle)
        val itemText: TextView = itemView.findViewById(R.id.text_main)
    }
}