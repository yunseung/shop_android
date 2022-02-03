package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.content.Context
import android.text.TextUtils
import android.view.View
import gsshop.mobile.v2.R
import gsshop.mobile.v2.ServerUrls
import gsshop.mobile.v2.home.HomeActivity
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.ClickUtils
import kotlinx.android.synthetic.main.view_holder_ban_shoppinglive_alarm.view.*
import roboguice.util.Ln

/**
 * 모바일라이브 탭매장 신설
 * 방송알림 영역
 */
class BanMLPrdAlarmMobileLive (itemView: View) : BaseViewHolder(itemView) {
    private val mMainView = itemView.root
    val mCaller = "MOBILE_LIVE_LIST" // 등록확정 팝업이 모라 생방송 플레이어 or 쇼핑라이브 탭매장 중에 어디 위에 뜰지 구분할 구분자

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        try {
            //"쇼핑라이브 방송알림" 텍스트
            if (!TextUtils.isEmpty(item.name)) {
                mMainView.txt_main.text = item.name
            }

            //알람여부에 따라 알람버튼 그린다
            if ("Y" == item.broadAlarmDoneYn){
                mMainView.img_alarm_on.setVisibility(View.VISIBLE)
                mMainView.img_alarm_off.setVisibility(View.GONE)
                mMainView.txt_main.text = context.getString(R.string.shpping_live_alarm_btn_on)
            }else{
                mMainView.img_alarm_off.setVisibility(View.VISIBLE)
                mMainView.img_alarm_on.setVisibility(View.GONE)
                mMainView.txt_main.text = context.getString(R.string.shpping_live_alarm_btn_off)
            }

            //방송알림 클릭시
            mMainView.view_main.setOnClickListener {

                if (ClickUtils.work2(300)) {
                    return@setOnClickListener
                }

                if ("Y".equals(item.broadAlarmDoneYn)){
                    //알람해제 팝업에 필요한 정보를 가져오기위해 api호출
                    MLAlarm.deleteQuery(context, mCaller)
                } else if ("N".equals(item.broadAlarmDoneYn)){
                    //알람등록 팝업에 필요한 정보를 가져오기위해 api호출
                    MLAlarm.addQuery(context, mCaller)
                }

                //효율코드
                (context as HomeActivity).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_ALARM_CLICK)
            }
        }
        catch (e:Exception) {
            Ln.e(e.message)
        }

    }
}