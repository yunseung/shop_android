package gsshop.mobile.v2.home.shop.flexible

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_gr_home_cate_tab_gate.view.*
import kotlinx.android.synthetic.main.view_holder_gr_home_cate_tab_gate_item.view.*

class GrHomeCateTabGateVH(itemView: View) : BaseViewHolder(itemView) {
    private val mLvItemList = itemView.lv_item_list
    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        val item = info?.contents?.get(position)?.sectionContent ?: return

        itemView.tv_title.text = item.name

        mLvItemList.adapter = GateItemAdapter(item.subProductList!!, context)
        DisplayUtils.setListViewHeightBasedOnChildren(mLvItemList)
    }

    private class GateItemAdapter(list: ArrayList<SectionContentList>, context: Context?) : BaseAdapter() {
        private val mList = list
        private val mContext = context

        override fun getCount(): Int {
            return mList.size
        }

        override fun getItem(position: Int): Any {
            return mList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var v = convertView
            val holder: ViewHolder

            if (v == null) {
                holder = ViewHolder()
                val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                v = inflater.inflate(R.layout.view_holder_gr_home_cate_tab_gate_item, parent, false)
                holder.ivImg = v.iv_img
                holder.tvName = v.tv_name
                holder.tvSubName = v.tv_sub_name
                holder.rootView = v.root_view

                v.tag = holder
            } else {
                holder = v.tag as ViewHolder
            }

            ImageUtil.loadImage(mContext!!, mList[position].imageUrl, holder.ivImg, R.drawable.noimage_375_375)
            holder.tvName.text = mList[position].name
            holder.tvSubName.text = mList[position].subName
            holder.rootView.setOnClickListener { WebUtils.goWeb(mContext, mList[position].linkUrl) }


            return v!!
        }

        private class ViewHolder {
            lateinit var rootView: LinearLayout
            lateinit var ivImg: ImageView
            lateinit var tvName: TextView
            lateinit var tvSubName: TextView
        }
    }
}