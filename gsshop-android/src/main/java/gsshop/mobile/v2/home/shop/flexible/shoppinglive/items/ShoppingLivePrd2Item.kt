/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.shoppinglive.items

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.recycler_item_shopping_live_prd_2.view.*
import roboguice.util.Ln
import java.lang.NullPointerException

/**
 * 쇼핑라이브 2단 뷰
 */
class ShoppingLivePrd2Item : LinearLayout{
    private var mContext: Context = context

    private lateinit var mLlRoot: LinearLayout

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.recycler_item_shopping_live_prd_2, this, true)

        mLlRoot = item_root
    }

    fun onBindViewHolder(item: SectionContentList?) {
        setItems(item)
    }

    private fun setItems(item: SectionContentList?) {
        if (item == null) {
            return
        }
        try {
            if (!TextUtils.isEmpty(item.imageUrl)) {
                ImageUtil.loadImageResize(context, item.imageUrl, item_root.img_main, R.drawable.noimage_164_246)
            }

            if (!TextUtils.isEmpty(item.videoTime)) {
                item_root.txt_time.text = item.videoTime
            }

            if (!TextUtils.isEmpty(item.promotionName)) {
                item_root.txt_main.text = item.promotionName
            }

            if (!TextUtils.isEmpty(item.streamViewCount)) {
                item_root.txt_view_counter.text = item.streamViewCount
            }

            if (!TextUtils.isEmpty(item.linkUrl)) {
                item_root.setOnClickListener {
                    WebUtils.goWeb(context, item.linkUrl)
                }
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }

    }
}