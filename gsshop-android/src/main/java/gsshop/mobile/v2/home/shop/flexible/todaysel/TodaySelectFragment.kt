package gsshop.mobile.v2.home.shop.flexible.todaysel

import android.os.Bundle
import android.view.View
import gsshop.mobile.v2.home.shop.ViewHolderType
import gsshop.mobile.v2.home.shop.bestshop.BestShopFragment

class TodaySelectFragment : BestShopFragment() {

    companion object {

        @JvmField
        var instance : TodaySelectFragment? = null

        @JvmStatic
        fun getInstance(): TodaySelectFragment? {
            return instance
        }

        @JvmStatic
        fun newInstance(position: Int): TodaySelectFragment? {
            instance = newInstanceInternal(position)
            return instance
        }

        @JvmStatic
        private fun newInstanceInternal(position: Int): TodaySelectFragment? {
            val fragment = TodaySelectFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = TodaySelectAdapter(activity)
        mRecyclerView.adapter = mAdapter
    }

    override fun getFlexibleViewType(viewType: String?): Int {
        return when (viewType) {
            "BAN_SLD_ROUNDED_SMALL" -> {
                ViewHolderType.VIEW_TYPE_BAN_SLD_ROUNDED_SMALL
            }
            "BAN_SLD_ROUNDED_BIG" -> {
                ViewHolderType.VIEW_TYPE_BAN_SLD_ROUNDED_BIG
            }
            "GR_TAB_SEL_TODAY" -> {
                ViewHolderType.VIEW_TYPE_GR_TAB_SEL_TODAY
            }
            "BAN_TS_TXT_SUB_GBA" -> {
                ViewHolderType.VIEW_TYPE_BAN_TS_TXT_SUB_GBA
            }
            "GR_TAB_TODAY_SEL" -> {
                ViewHolderType.VIEW_TYPE_GR_TAB_TODAY_SEL
            }
            else -> {
                super.getFlexibleViewType(viewType)
            }
        }
    }
}