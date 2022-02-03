package gsshop.mobile.v2.home.shop.flexible.exclusive

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.RadioButton
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.renewal.views.TextViewWithListenerWhenOnDraw
import gsshop.mobile.v2.util.ThreadUtils
import kotlinx.android.synthetic.main.view_holder_ban_tab_sel.view.*
import roboguice.util.Ln

class BanTabSelVH(itemView : View?) : BaseViewHolder(itemView) {

    var radios : List<Views>? = null

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        val item = info?.contents?.get(position)?.sectionContent ?: return
        setView(context, item)

    }

    private fun setView(context: Context?, item: SectionContentList) {

        // 사이즈가 2보다 작으면
        if (item.subProductList!!.size < 2) {
            // 해당 뷰 빼자.
            return
        }

        radios = listOf<Views>(
            Views(
                item.subProductList!![0],
                itemView.btn_01,
                itemView.txt_btn_01,
                itemView.underline_btn_01
            ),
            Views(
                item.subProductList!![1],
                itemView.btn_02,
                itemView.txt_btn_02,
                itemView.underline_btn_02
            )
        )

        try {
            for (radio in radios!!) {
                val itemData = radio.data
                radio.text.text = itemData.name
                radio.text.setOnDrawListener {
                    val lineCnt: Int = radio.text.lineCount
                    if (lineCnt > 1) {
                        val textFull: String = radio.text.text.toString()
                        //str0 = 0번째 줄의 텍스트
                        //str0 = 0번째 줄의 텍스트
                        val str0 = textFull.substring(
                            radio.text.layout.getLineStart(0),
                            radio.text.layout.getLineEnd(0)
                        )
                        radio.text.text = str0
                    }
                }
                radio.radio.setOnClickListener {
                    if(radio.bar.visibility == View.VISIBLE) {
                        // 현재 선택되어 있는 상태이면 실행하지 않음
                        return@setOnClickListener
                    }
                    EventBus.getDefault().postSticky(Events.GSExcusiveEvent.TabRefreshEvent(itemData.linkUrl))
                    ThreadUtils.runInUiThread ( Runnable {
                        for (radio2 in radios!!) {
                            radio2.text.setTextColor(Color.parseColor("#b3ffffff"))
                            radio2.bar.visibility = View.GONE
                        }
                        radio.text.setTextColor(Color.WHITE)
                        radio.bar.visibility = View.VISIBLE
                    })
                }
            }

        } catch (e: NullPointerException) {
            Ln.e(e.message)
        } catch (e: IndexOutOfBoundsException) {
            Ln.e(e.message)
        }
    }

    inner class Views(_data : SectionContentList, _radio : RadioButton, _text : TextViewWithListenerWhenOnDraw, _bar : View) {
        var data : SectionContentList = _data
        var radio : RadioButton = _radio
        var text : TextViewWithListenerWhenOnDraw = _text
        var bar : View = _bar
    }

    fun setSel(index: Int) {
        if (radios == null) return
        for (radio2 in radios!!) {
            radio2.radio.isChecked = false
            radio2.text.setTextColor(Color.parseColor("#b3ffffff"))
            radio2.bar.visibility = View.GONE
        }

        radios!![index].radio.isChecked = true
        radios!![index].text.setTextColor(Color.WHITE)
        radios!![index].bar.visibility = View.VISIBLE
    }
}