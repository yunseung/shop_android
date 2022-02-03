/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.vip

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.google.inject.Inject
import com.gsshop.mocha.network.rest.RestClient
import com.gsshop.mocha.pattern.mvc.BaseAsyncController
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonReadMore
import gsshop.mobile.v2.util.DataUtil
import kotlinx.android.synthetic.main.view_holder_gr_prd_2_vip_list.view.*
import roboguice.util.Ln

class GrPrd2VIPListGbbVH(itemView: View) : BaseViewHolder(itemView) {
    // 상단 공통 VIP 타이틀
    private val mTitleVip = itemView.view_title_vip
    // 하단 공통 더보기
    private val mMoreView = itemView.view_read_more
    // 추가될 뷰
    private val mViewPrd2 = itemView.view_prd_2

    private val mRootView = itemView.root

    private val mViewBack = itemView.view_back

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewBack.setNew("Y".equals(item.newYN, ignoreCase = true))

        setItems(context, item)
    }

    private fun setItems(context: Context, item: SectionContentList) {
        mTitleVip.setItems(item)
        mMoreView.setItems(item, VipCommonReadMore.ENUM_TYPE_MORE.TYPE_RENEWAL)

        // 갱신 동작
        mMoreView.setOnClickListener {
            if (!TextUtils.isEmpty(item.moreBtnUrl)) {

                RenewalController(context, false).execute(item.moreBtnUrl, object : OnRenewalListener {
                    override fun onSuccess(data: SectionContentList?) {
                        if (data?.productList != null) {
                            setItems(context, data.productList!![0])
                        }
                    }
                })
            }
        }
        mRootView.contentDescription = mTitleVip.getTitleTxt()

        mViewPrd2.setItems(item)
    }


    interface OnRenewalListener {
        fun onSuccess(data: SectionContentList?)
    }

    /**
     *
     */
    private class RenewalController(mContext: Context, private val isDialog: Boolean) : BaseAsyncController<SectionContentList?>(mContext) {

        @Inject
        private val restClient: RestClient? = null

        private var mUrl: String? = null

        private var mListener: OnRenewalListener? = null

        @Throws(Exception::class)
        override fun onPrepare(vararg params: Any) {
            if (dialog != null && isDialog) {
                dialog.dismiss()
                dialog.setCancelable(true)
                dialog.show()
            }

            try {
                mUrl = params[0].toString()
                mListener = params[1] as OnRenewalListener
            }
            catch (e: IndexOutOfBoundsException) {
                Ln.e(e.message)
            }
            catch (e: NullPointerException) {
                Ln.e(e.message)
            }
            catch (e: ClassCastException) {
                Ln.e(e.message)
            }
        }

        @Throws(Exception::class)
        override fun process(): SectionContentList {
            return DataUtil.getData(context, restClient, SectionContentList::class.java,
                    false, false, mUrl) as SectionContentList
        }

        @Throws(Exception::class)
        override fun onSuccess(result: SectionContentList?) {
            if (mListener != null) {
                mListener!!.onSuccess(result)
            }
        }

        override fun onError(e: Throwable) {
            Ln.e(e)
        }

        override fun onFinally() {
            super.onFinally()
            if (dialog != null) {
                dialog.dismiss()
            }
        }
    }

}