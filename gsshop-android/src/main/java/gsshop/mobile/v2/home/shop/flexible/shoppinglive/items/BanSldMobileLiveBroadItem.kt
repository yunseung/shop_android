package gsshop.mobile.v2.home.shop.flexible.shoppinglive.items

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.ExoLiveMediaPlayerControllerFragmentSLive
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.ShoppingLiveShopFragment
import gsshop.mobile.v2.support.media.OnMediaPlayerController
import gsshop.mobile.v2.support.media.model.MediaInfo
import gsshop.mobile.v2.util.DisplayUtils.resizeHeightAtViewToScreenSize
import gsshop.mobile.v2.util.DisplayUtils.resizeWidthAtViewToScreenSize
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.LunaUtils.sendToLuna
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_sld_mobile_live_broad_item.view.*
import roboguice.util.Ln
import java.io.IOException

class BanSldMobileLiveBroadItem : ConstraintLayout {

    private var mContext: Context = context

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var mPlayer: OnMediaPlayerController? = null
    private var mViewPlayer: View? = null

    // 현재 선택된 포지션 비교를 위해 나의 포지션을 가지고 있는다.
    private var mThisPosition = -1
    private var isMute = true
    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_ban_sld_mobile_live_broad_item, this, true)

        resizeWidthAtViewToScreenSize(mContext, root_view)
        resizeHeightAtViewToScreenSize(mContext, root_view)
    }

    var isLive = false

    var mEndTime:Long? = 0

    fun setItem(product: SectionContentList, position: Int, pagePosition: Int) {

        if (product.linkUrl != null) {
            root_view.setOnClickListener { WebUtils.goWeb(context, product.linkUrl) }
        }

        // 해당 뷰타입이 LIVE 이면서 url이 존재하면
        if ("LIVE" == product.viewType && !product.videoid.isNullOrEmpty()) {
//        if (position == 0) {    //test

            // 시간이 종료되면 갱신을 해주면서 해당 아이템을 삭제 해야 하기 때문에 endtime을 가지고 있는다.
            mEndTime = product.endDate
//            mEndTime = System.currentTimeMillis() + 10000 //test

            mThisPosition = position
            isLive = true
            view_vod.visibility = VISIBLE
            view_img.visibility = GONE

            // 플레이어 id 재설정
            view_player.id = R.id.vod_port_video_frame_00

            // 사운드
            view_sound.setOnClickListener {
                if (mPlayer == null) return@setOnClickListener

                isMute = !isMute
                mPlayer!!.setMute(isMute)
                chk_sound.isChecked = isMute
            }

            chk_sound.isChecked = isMute

            // 일정 시간이 지난 후에 플레이 설정을 해준다.
            Handler().postDelayed({

                try {
                    (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(
                                    R.id.vod_port_video_frame_00,
                                    ExoLiveMediaPlayerControllerFragmentSLive.newInstance(
//                                        "http://livem.gsshop.com/gsshop_hd/_definst_/gsshop_hd.stream/playlist.m3u8", // test
                                        product.videoid,
                                            false
                                    )
                            ).commitNowAllowingStateLoss()
                } catch (e: IllegalStateException) {
                    Ln.e(e.message)
                    sendToLuna(mContext, e, null)

                    return@postDelayed
                }
                catch (e:IllegalArgumentException) {
                    Ln.e(e.message)
                    sendToLuna(mContext, e, null)

                    return@postDelayed
                }
//                setPlayer("http://livem.gsshop.com/gsshop_hd/_definst_/gsshop_hd.stream/playlist.m3u8") // test
                setPlayer(product.videoid)
            }, 300)
        }
        else {
            view_vod.visibility = GONE
            view_img.visibility = VISIBLE
            if (product.imageUrl != null) {
                ImageUtil.loadImageFitWithRoundCenterCrop(mContext, product.imageUrl, iv_img, 0, R.drawable.noimage_375_375)
            }

            tv_view_cnt.visibility = INVISIBLE
            iv_live.visibility = INVISIBLE
            tv_broad_time.visibility = VISIBLE
            tv_broad_time.text = product.broadTimeText
        }

        if (product.subName != null) {
            tv_sub_name.visibility = VISIBLE
            tv_sub_name.text = product.subName
        }
        else {
            tv_sub_name.visibility = INVISIBLE
        }

        if (product.name != null) {
            tv_name.visibility = VISIBLE

            // android 에서 자동으로 라인이 넘어가는 현상을 무시하고 텍스트뷰의 끝까지 텍스트를 채우기 위함.
            val sb = StringBuilder()
            for (i in product.name!!.indices) {
                sb.append(product.name!![i])
                sb.append("\u200B")
            }
            if (sb.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            product.name = sb.toString()
            tv_name.text = product.name
        } else {
            tv_name.visibility = INVISIBLE
        }

    }

    /**
     * 플레이어를 설정 Live URL
     */
    fun setPlayer(url : String?) {
        ///////////////////////// 플레이어 설정부 /////////////////////////

        mPlayer =
                (mContext as FragmentActivity).supportFragmentManager.findFragmentById(R.id.vod_port_video_frame_00) as OnMediaPlayerController

        mPlayer?.setUseMuteFromGlobal(false)
        mPlayer?.setPlayerSize(true)

        // 현재 플레이 중인 뷰는 멤버 변수에 저장.
        mViewPlayer = view_player

        var media: MediaInfo? = null
        if (mPlayer != null) {
            media = mPlayer!!.mediaInfo
        }
        if (media == null) {
            media = MediaInfo()
            media.currentPosition = -1
            media.lastPlaybackState = Player.STATE_IDLE
            media.playerMode = MediaInfo.MODE_SIMPLE
        }

        // vod 새로 시작.
        media.contentUri = url
//                "http://livem.gsshop.com/gsshop_hd/_definst_/gsshop_hd.stream/playlist.m3u8"
        media.currentPosition = 0
        media.isPlaying = true

        if (mPlayer != null) {
            mPlayer!!.mediaInfo = media
//            mPlayer!!.showPlayControllerView(false)
            mPlayer!!.setPlayerResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)
            mPlayer!!.setBackgroudnColor(Color.BLACK)
            mPlayer!!.setUseController(false)
            mPlayer!!.setPlayerWhenOnResume(true)

            // 생성 되었을 떄에 현재 포지션 하고 페이지 포지션이 일치해야 플레이 바로 실행.
            if (isLive) {
                if (ShoppingLiveShopFragment.instance.userVisibleHint) {
                    mPlayer?.playPlayer()
                }
                mPlayer?.setPlayerSize(true)
                mPlayer?.setPlayerWhenOnResume(true)
                mPlayer?.setMute(isMute)
            }
        }
    }

    fun setPlayerPlay(isPlay: Boolean) {
        if (isPlay) {
            mPlayer?.playPlayer()
            mPlayer?.setPlayerSize(true)
            mPlayer?.setPlayerWhenOnResume(true)
            mPlayer?.setMute(isMute)
            chk_sound.isChecked = isMute
        }
        else {
            mPlayer?.stopPlayer()
        }
    }

    fun setMute (isMute: Boolean) {
        this.isMute = isMute
        mPlayer?.setMute(isMute)
        chk_sound.isChecked = isMute
    }

    override fun onAttachedToWindow() {
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (isLive) {
            Ln.d("hklim onAttachedToWindow")
            mPlayer?.playPlayer()
            mPlayer?.setPlayerSize(true)
            mPlayer?.setPlayerWhenOnResume(true)
            mPlayer?.setMute(isMute)
            chk_sound.isChecked = isMute
        }
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        EventBus.getDefault().unregister(this)

        if (isLive) {
            Ln.d("hklim onDetachedFromWindow")
            mPlayer?.setPlayerWhenOnResume(false)
            isMute = true
            chk_sound.isChecked = isMute
            mPlayer?.stopPlayer()
            mPlayer?.release()
        }

        super.onDetachedFromWindow()
    }

    fun onEvent(event: Events.ShoppingLiveEvent.LivePlayEvent) {
        val curationEvent = EventBus.getDefault().getStickyEvent(Events.ShoppingLiveEvent.LivePlayEvent::class.java)
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent)
        }

        if (!isLive) {
            return
        }

        if (event.isPlay) {

            // create 에 0.3초 기다린다. 0.3초 기다린 후 플레이
            Handler().postDelayed(Runnable {
                mPlayer?.playPlayer()
                mPlayer?.setPlayerSize(true)
                mPlayer?.setPlayerWhenOnResume(true)
                mPlayer?.setMute(isMute)
                chk_sound.isChecked = isMute
            }, 300)
        }
        else {
            mPlayer?.setPlayerWhenOnResume(false)
            mPlayer?.stopPlayer()
            mPlayer?.release()
            isMute = true
            mPlayer?.setMute(isMute)
            chk_sound.isChecked = isMute
        }
    }

    fun onEventMainThread(event: Events.TimerEvent?) {
        if(isLive) {
//            Ln.d("hklim 라이브 글로벌 타이머 : $mEndTime / " + System.currentTimeMillis())
            if (mEndTime != null &&
                mEndTime!! < System.currentTimeMillis()) {
                EventBus.getDefault().postSticky(Events.ShoppingLiveEvent.RemoveLivePlayerEvent(mThisPosition))
            }
        }

    }

    // static 선언
//    companion object {
//        var isMute = true
//    }

}