package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.view.View
import gsshop.mobile.v2.home.main.ModuleList
import gsshop.mobile.v2.home.shop.BaseViewHolderV2
import kotlinx.android.synthetic.main.view_holder_ban_txt_sub_gba.view.*

class BanTxtSubGbaVHKt(itemView: View?) : BaseViewHolderV2(itemView) {

    override fun onBindViewHolder(context: Context, position: Int, moduleList: MutableList<ModuleList>?) {
        super.onBindViewHolder(context, position, moduleList)

        if (moduleList != null) {
            val varModuleList: ModuleList = moduleList[position]

//            if (TextUtils.isEmpty(moduleList.name)) {
//                itemView.tv_name.visibility = View.GONE
//            } else {
                itemView.tv_name.text = varModuleList.name
//            }
        }
    }
}