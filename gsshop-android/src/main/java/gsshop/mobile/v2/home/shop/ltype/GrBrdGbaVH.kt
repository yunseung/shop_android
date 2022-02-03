package gsshop.mobile.v2.home.shop.ltype

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.blankj.utilcode.util.EmptyUtils
import com.google.android.exoplayer2.Player
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.gsshop.mocha.device.NetworkStatus
import com.gsshop.mocha.ui.util.ViewUtils
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.AbstractBaseActivity
import gsshop.mobile.v2.Events.VideoActionEvent
import gsshop.mobile.v2.Events.VodShopPlayerEvent
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.R
import gsshop.mobile.v2.ServerUrls.WEB
import gsshop.mobile.v2.home.main.ModuleList
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolderV2
import gsshop.mobile.v2.home.shop.PlayerAction
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.vod.VodBannerVodSharedViewHolder
import gsshop.mobile.v2.support.media.OnMediaPlayerController
import gsshop.mobile.v2.support.media.OnMediaPlayerListener
import gsshop.mobile.v2.support.media.model.MediaInfo
import gsshop.mobile.v2.support.tv.LiveVideoMediaPlayerActivity
import gsshop.mobile.v2.util.ClickUtils
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.NetworkUtils
import gsshop.mobile.v2.web.WebUtils
import gsshop.mobile.v2.web.productDetail.ActivityDetailViewLiveVideoMediaPlayer
import gsshop.mobile.v2.web.productDetail.video.brightcove.FragmentDetailViewBrightcoveController
import kotlinx.android.synthetic.main.mc_webview_progress_bar.view.*
import kotlinx.android.synthetic.main.view_holder_gr_brd_gba.view.*
import kotlinx.android.synthetic.main.view_mobile_data_alert_detail_view_layout.view.*

/**
 * 시그니처매장 브랜드그룹형 모듈
 *
 * 참고
 * 1.스크롤시 버벅거림 현상
 * -매장 스크롤시 버벅거림 현상은 레이아웃 xml의 뎁스가 깊고 처리작업 부하가 큰 부분이 원인임
 * -개선을 위해 xml의 구조/처리프로세스를 개션해야 하나 이는 가격영역이 포함되어 있어 충분한 검토가 필요함
 *
 * 2.동영상 동작 관련
 * -하나의 매장내에 GrBrdGbaVH(동영상포함) 뷰홀더가 다수 존해하는 경우 뷰홀더 리사이클 관련 이슈들이 발생함
 * -그래서 재생버튼 클릭시 비디오프레그먼트를 생성하고 뷰홀더 detach, fragment onStop, 좌우 플리킹 시
 * 비디오프레그먼트를 제거하는 로직 사용 (like 내일TV)
 *
 */
class GrBrdGbaVH(itemView: View) : BaseViewHolderV2(itemView), OnMediaPlayerListener {

    /**
     * 컨텍스트
     */
    private lateinit var mContext: Context

    /**
     * 브랜드명 영역
     */
    private val mLlBrand: LinearLayout = itemView.ll_brand
    private val mIvBrandImg: ImageView = itemView.iv_brand_img
    private val mTvBrandName: TextView = itemView.tv_brand_name

    /**
     * 정사각이미지/VOD 영역
     */
    private val mVideoFrame: FrameLayout = itemView.video_frame
    private val mClSquareImg: ConstraintLayout = itemView.cl_square_img
    private val mTvPageNum: TextView = itemView.tv_page_num
    private val mClVodImg: ConstraintLayout = itemView.cl_vod_img
    private val mIvVodImg: ImageView = itemView.iv_vod_img
    private val mIvPlayImg: ImageView = itemView.iv_play_img
    private val mFlMobileData: FrameLayout = itemView.fl_mobile_data
    private val mBtnMobileDataOk: Button = itemView.button_mobile_data_ok
    private val mBtnMobileDataCancel: Button = itemView.button_mobile_data_cancel

    /**
     * 타이틀
     */
    private val mLlTitle: LinearLayout = itemView.ll_title
    private val mTvTitle1Th: TextView = itemView.tv_title_1th
    private val mTvTitle2Th: TextView = itemView.tv_title_2th

    /**
     * 서브타이틀
     */
    private val mTvSubTitle: TextView = itemView.tv_sub_title

    /**
     * 브랜드샵 더보기
     */
    private val mViewTvMore: View = itemView.layout_tv_more
    private val mTvMore: TextView = itemView.tv_more

    /**
     * 하단라인 (더보기 비노출시 표시)
     */
    private val mVwBottomLine: View = itemView.vw_bottom_line

    /**
     * 리사이킄러뷰 + 스냅헬퍼 기반  뷰페이저 (이미지 영역)
     */
    private val mViewPagerImg: ViewPager2 = itemView.viewPagerImg

    /**
     * 어뎁터
     */
    private val mAdapterImg: GrBrdGbaImgAdapter

    /**
     * 리사이킄러뷰 + 스냅헬퍼 기반  뷰페이저 (상품컴포넌트 영역)
     */
    private val mViewPager: ViewPager2 = itemView.viewPager

    /**
     * mViewPager 인디케이터
     */
    private val mTabLayout: TabLayout = itemView.tab_layout

    /**
     * 어뎁터
     */
    private val mAdapter: GrBrdGbaPrdAdapter

    /**
     * 플레이어 컨트롤러 (null 세팅이 필요하여 lateinit 사용 안함)
     */
    private var mPlayer: OnMediaPlayerController? = null

    /**
     * 미디어정보 저장
     */
    private lateinit var mMediaInfo: MediaInfo

    /**
     * 하단 디바이더가 다음 뷰타입과 같으면 1dp 다르면 10dp
     */
    private val mViewBottomDivider1dp: View = itemView.view_bottom_divider_1dp
    private val mViewBottomDivider10dp: View = itemView.view_bottom_divider_10dp

    /**
     * 효율코드 중복전송 방지용 (플레이어에서 중복으로 콜백이 호출되는 경우가 있음)
     */
    private var mOldAction = PlayerAction.NONE

    /**
     * 재생버튼 클릭시 첫 1회만 효율코드를 전송하기 위함
     */
    private var isFirst = true

    /**
     * 전체화면에서 복귀한 경우 PLAY 효율코드를 전송안하기 위함
     */
    private var isBackFullScreen = false

    /**
     * 전체화면 진입시에는 removePlayer() 호출을 스킵하기 위함
     * (전체화면에서 재생중 복귀시 인앱플레이어도 재생상태를 유지해야 하기 때문)
     */
    private var clickFullScreen = false

    /**
     * 앱이 장시간 백그라운드 상태에서 포그라운드 전환시
     * 브라이트코브 SDK의 fragmentResumed에서 아래 url 호출하면서 EventType.ERROR 다수 발생
     * 에러 : error { errorMessage: onLoadError: sourceId: -1 source: Source{deliveryType: video/mpeg-dash, url: https://manifest.prod.boltdns.net...
     * 이때는 토스트를 노출하지 않기 위함.
     */
    private var skipToast = false

    /**
     * 로딩프로그레스
     */
    private val mProgressBar: ProgressBar = itemView.mc_webview_progress_bar

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(context: Context, position: Int, moduleList: MutableList<ModuleList>?) {
        val varModuleList = moduleList?.get(position) ?: return

        setData(context, varModuleList)
        setAdapterArea(varModuleList)
        setClickListener()
        setNextItemMargin(moduleList, position)
    }

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        // shopItem에는 왜넣어가지고... 에효
        val item = info?.contents?.get(position)?.sectionContent ?: return
        if (context == null) {
            return
        }
        setData(context, item)
        setAdapterAreaFxc(item)
        setClickListener()
        setNextItemMarginShopItem(info?.contents, position)
    }

    private fun setData(context: Context, varModuleList: SectionContentList) {
        //초기화
        isFirst = true
        mContext = context
        ViewUtils.hideViews(mProgressBar)

        //브랜드 이름
        ViewUtils.hideViews(mLlBrand)
        if (EmptyUtils.isNotEmpty(varModuleList.brandNm)) {
            mTvBrandName.text = varModuleList.brandNm
            ViewUtils.showViews(mLlBrand)
        }

        //브랜드 이미지
        ViewUtils.hideViews(mIvBrandImg)
        if (EmptyUtils.isNotEmpty(varModuleList.brandImg)) {
            ImageUtil.loadImage(mContext, varModuleList.brandImg, mIvBrandImg, 0)
            ViewUtils.showViews(mIvBrandImg)
        }

        //메인타이틀, 서브타이틀 세팅
        ViewUtils.hideViews(mTvTitle1Th, mTvTitle1Th, mTvTitle2Th)
        if (EmptyUtils.isNotEmpty(varModuleList.title1)) {
            mTvTitle1Th.text = varModuleList.title1
            ViewUtils.showViews(mTvTitle1Th)
        }
        if (EmptyUtils.isNotEmpty(varModuleList.title2)) {
            mTvTitle2Th.text = varModuleList.title2
            ViewUtils.showViews(mTvTitle2Th)
        }
        if (EmptyUtils.isNotEmpty(varModuleList.subName)) {
            mTvSubTitle.text = varModuleList.subName
            ViewUtils.showViews(mTvSubTitle)
        }

        //브랜드명
        val brandName = (if (EmptyUtils.isEmpty(varModuleList.brandNm)) "" else varModuleList.brandNm)
        //더보기 텍스트
        val moreText = (if (EmptyUtils.isEmpty(varModuleList.moreText)) "" else varModuleList.moreText)
        ViewUtils.hideViews(mViewTvMore, mVwBottomLine)
        if (EmptyUtils.isNotEmpty(brandName) && EmptyUtils.isNotEmpty(moreText)) {
            ViewUtils.showViews(mViewTvMore)
            val sBuilder = SpannableStringBuilder()
            sBuilder.append("$brandName $moreText")
            sBuilder.setSpan(StyleSpan(Typeface.BOLD), 0, brandName!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mTvMore.text = sBuilder
            mViewTvMore.setOnClickListener { WebUtils.goWeb(mContext, varModuleList.moreBtnUrl) }
        } else {
            //하단바 노출
            ViewUtils.showViews(mVwBottomLine)
        }
    }

    /**
     * SQUARE/VOD & 상품컴포넌트 영역을 세팅한다.
     *
     * @param moduleList ModuleList
     */
    private fun setAdapterArea(moduleList: ModuleList) {
        //상품컴포넌트 영역
        ViewUtils.hideViews(mTabLayout)
        mAdapter.updateProducts(moduleList.productList)
        val prdSize = moduleList.productList?.size ?: 0
        if (prdSize > GrBrdGbaPrdAdapter.PRD_LIMIT_NUM) {
            ViewUtils.showViews(mTabLayout)
            mViewPager.currentItem = 0
        }

        //비디오아이디 유무 별 UI 분기
        if (EmptyUtils.isNotEmpty(moduleList.videoid)) {
            val params = mLlTitle.layoutParams as LinearLayout.LayoutParams
            params.topMargin = DisplayUtils.convertDpToPx(mContext, 0f)
            mLlTitle.layoutParams = params
            mLlTitle.background = null
            ViewUtils.showViews(mClVodImg, mIvPlayImg)
            ViewUtils.hideViews(mClSquareImg, mFlMobileData)

            //브라이트코브 섬네일
            ImageUtil.loadImage(mContext, moduleList.vodImg, mIvVodImg, R.drawable.noimage_375_188)
            mIvPlayImg.setOnClickListener {
                if (!NetworkStatus.isNetworkConnected(mContext)) {
                    NetworkUtils.showUnstableAlert(mContext as Activity)
                    return@setOnClickListener
                }
                val mediaInfo = MediaInfo()
                mediaInfo.videoId = moduleList.videoid
                mediaInfo.posterImageUrl = moduleList.vodImg
                mMediaInfo = mediaInfo
                confirmMobileData()
            }
        } else {
            val params = mLlTitle.layoutParams as LinearLayout.LayoutParams
            params.topMargin = DisplayUtils.convertDpToPx(mContext, -54f)
            mLlTitle.layoutParams = params
            mLlTitle.setBackgroundResource(R.drawable.bg_radius_left_top_40)
            ViewUtils.showViews(mClSquareImg)
            ViewUtils.hideViews(mClVodImg)

            //상품이미지/VOD 영역
            val imgList: List<ModuleList>? = moduleList.moduleList
            mAdapterImg.updateProducts(imgList)
            if (EmptyUtils.isEmpty(imgList)) {
                ViewUtils.hideViews(mTvPageNum)
                return
            }
            //정사각이미지 영역 페이지 정보 노출
            val totalPage = imgList?.size ?: 0
            if (totalPage < 2) {
                ViewUtils.hideViews(mTvPageNum)
            } else {
                ViewUtils.showViews(mTvPageNum)
            }
            mViewPagerImg.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                @SuppressLint("SetTextI18n")
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mTvPageNum.text = (position + 1).toString() + "/" + totalPage
                }
            })
        }
    }

    /**
     * FXC 타입일 경우 SQUARE/VOD & 상품컴포넌트 영역을 세팅한다.
     * @param contentList SectionContentList
     */
    private fun setAdapterAreaFxc(contentList: SectionContentList) {
        //상품컴포넌트 영역
        ViewUtils.hideViews(mTabLayout)
        mAdapter.updateProducts(contentList.subProductList)
        val prdSize = contentList.subProductList?.size ?: 0
        if (prdSize > GrBrdGbaPrdAdapter.PRD_LIMIT_NUM) {
            ViewUtils.showViews(mTabLayout)
            mViewPager.currentItem = 0
        }

        //비디오아이디 유무 별 UI 분기
        if (EmptyUtils.isNotEmpty(contentList.videoid)) {
            val params = mLlTitle.layoutParams as LinearLayout.LayoutParams
            params.topMargin = DisplayUtils.convertDpToPx(mContext, 0f)
            mLlTitle.layoutParams = params
            mLlTitle.background = null
            ViewUtils.showViews(mClVodImg, mIvPlayImg)
            ViewUtils.hideViews(mClSquareImg, mFlMobileData)

            //브라이트코브 섬네일
            ImageUtil.loadImage(mContext, contentList.vodImg, mIvVodImg, R.drawable.noimage_375_188)
            mIvPlayImg.setOnClickListener {
                if (!NetworkStatus.isNetworkConnected(mContext)) {
                    NetworkUtils.showUnstableAlert(mContext as Activity)
                    return@setOnClickListener
                }
                val mediaInfo = MediaInfo()
                mediaInfo.videoId = contentList.videoid
                mediaInfo.posterImageUrl = contentList.vodImg
                mMediaInfo = mediaInfo
                confirmMobileData()
            }
        } else {
            val params = mLlTitle.layoutParams as LinearLayout.LayoutParams
            params.topMargin = DisplayUtils.convertDpToPx(mContext, -54f)
            mLlTitle.layoutParams = params
            mLlTitle.setBackgroundResource(R.drawable.bg_radius_left_top_40)
            ViewUtils.showViews(mClSquareImg)
            ViewUtils.hideViews(mClVodImg)

            //상품이미지/VOD 영역
            val imgList: List<SectionContentList>? = contentList.subContentChild
            mAdapterImg.updateProducts(imgList)
            if (EmptyUtils.isEmpty(imgList)) {
                ViewUtils.hideViews(mTvPageNum)
                return
            }
            //정사각이미지 영역 페이지 정보 노출
            val totalPage = imgList?.size ?: 0
            if (totalPage < 2) {
                ViewUtils.hideViews(mTvPageNum)
            } else {
                ViewUtils.showViews(mTvPageNum)
            }
            mViewPagerImg.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                @SuppressLint("SetTextI18n")
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mTvPageNum.text = (position + 1).toString() + "/" + totalPage
                }
            })
        }
    }
    
    /**
     * 과금안내 팝업을 노출한다.
     */
    private fun confirmMobileData() {
        if (!NetworkStatus.isWifiConnected(mContext) && !MainApplication.isNetworkApproved) {
            //팝업노출
            ViewUtils.hideViews(mIvPlayImg)
            ViewUtils.showViews(mFlMobileData)
        } else {
            //WIFI or 기 승인
            ViewUtils.hideViews(mIvVodImg, mIvPlayImg)
            ViewUtils.showViews(mVideoFrame)
            playVideo()
        }
    }

    /**
     * 클릭리스너를 등록한다.
     */
    private fun setClickListener() {
        mBtnMobileDataOk.setOnClickListener {
            ViewUtils.hideViews(mFlMobileData, mIvVodImg, mIvPlayImg)
            ViewUtils.showViews(mVideoFrame)
            playVideo()
            MainApplication.isNetworkApproved = true
            sendWiseLogPrdNative(PlayerAction.LTE_Y, 0, 0)
        }
        mBtnMobileDataCancel.setOnClickListener {
            ViewUtils.hideViews(mFlMobileData)
            ViewUtils.showViews(mIvPlayImg)
            sendWiseLogPrdNative(PlayerAction.LTE_N, 0, 0)
        }
    }

    /**
     * 미디어정보를 세팅 후 영상을 재생한다.
     */
    private fun playVideo() {
        ViewUtils.showViews(mProgressBar)

        //동영상 재생 시 다른 재생중인 영상은 초기화
        EventBus.getDefault().post(VodShopPlayerEvent(VodShopPlayerEvent.VodPlayerAction.VOD_INIT_OTHERS, adapterPosition))
        if (EmptyUtils.isEmpty(mPlayer)) {
            setPlayer()
        }
        var media = mPlayer!!.mediaInfo
        if (EmptyUtils.isEmpty(media)) {
            media = MediaInfo()
            media.currentPosition = -1
            media.lastPlaybackState = Player.STATE_IDLE
            media.playerMode = MediaInfo.MODE_SIMPLE
        }
        media.videoId = mMediaInfo.videoId
        media.posterImageUrl = mMediaInfo.posterImageUrl
        media.currentPosition = 0
        media.isPlaying = true
        mPlayer!!.mediaInfo = media
        mPlayer!!.playPlayer()
    }

    /**
     * mVideoFrame에 플레이어를 추가한다.
     */
    private fun setPlayer() {
        val playerFrameId = VodBannerVodSharedViewHolder.LAND_FRAME_IDS[count]
        count++
        count %= VodBannerVodSharedViewHolder.LAND_FRAME_IDS.size
        mVideoFrame.id = playerFrameId
        (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(playerFrameId,
                FragmentDetailViewBrightcoveController.newInstance(FragmentDetailViewBrightcoveController.CALLER.SIGNATURE)).commitNowAllowingStateLoss()
        mPlayer = (mContext as FragmentActivity).supportFragmentManager.findFragmentById(playerFrameId) as OnMediaPlayerController?
        mPlayer!!.setOnMediaPlayerListener(this@GrBrdGbaVH)
    }

    /**
     * mVideoFrame에서 플레이어를 제거한다.
     */
    private fun removePlayer() {
        if (EmptyUtils.isNotEmpty(mPlayer)) {
            try {
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().remove((mPlayer as Fragment)).commitNowAllowingStateLoss()
                (mContext as FragmentActivity).supportFragmentManager.executePendingTransactions()
            } catch (e: Exception) {
                //동작에 이상은 없으나 해결방법은 찾아보자
                //IllegalStateException : FragmentManager is already executing transactions
                //Ln.e(e);
            }
            ViewUtils.hideViews(mProgressBar)
            ViewUtils.showViews(mClVodImg, mIvVodImg, mIvPlayImg)
            isFirst = true
            mPlayer = null
        }
    }

    override fun onFragmentResume() {
        skipToast = true
        Handler().postDelayed({ skipToast = false }, 5000)
    }

    override fun onError(e: Exception) {
        if (!skipToast) {
            Toast.makeText(mContext, R.string.video_play_error, Toast.LENGTH_SHORT).show()
        }
        removePlayer()
    }

    override fun onFullScreenClick(media: MediaInfo) {
        if (ClickUtils.work(2000)) {
            return
        }
        if (!::mContext.isInitialized) {
            return
        }
        clickFullScreen = true
        Handler().postDelayed({ clickFullScreen = false }, 2000)
        val intent = Intent(mContext, ActivityDetailViewLiveVideoMediaPlayer::class.java)
        MainApplication.gVideoCurrentPosition = media.currentPosition
        intent.putExtra(Keys.INTENT.VIDEO_ID, media.videoId)
        intent.putExtra(Keys.INTENT.VIDEO_URL, media.contentUri)
        intent.putExtra(Keys.INTENT.VIDEO_START_TIME, media.startTime)
        intent.putExtra(Keys.INTENT.VIDEO_END_TIME, media.endTime)
        intent.putExtra(Keys.INTENT.VIDEO_IS_PLAYING, media.isPlaying)
        intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        intent.putExtra(Keys.INTENT.FULL_SCREEN_CALLER, LiveVideoMediaPlayerActivity.FULL_SCREEN_CALLER.SIGNATURE)
        intent.putExtra(Keys.INTENT.FOR_RESULT, true)
        (mContext as Activity).startActivityForResult(intent, Keys.REQCODE.VIDEO)
    }

    /**
     * 다음 아이템과 동일한 뷰타입인 경우는 1dp, 아인 경우는 10dp의 마진을 설정한다.
     *
     * @param moduleLists List<ModuleList>
     * @param position 뷰홀더 포지션
    </ModuleList> */
    private fun setNextItemMargin(moduleLists: List<SectionContentList>, position: Int) {
        var isSameToNext = false
        val moduleList = moduleLists[position]
        if (moduleLists.size > position + 1) {
            val nextItem = moduleLists[position + 1]
            //브랜드 더보기 뷰홀더는 10dp의 마진을 적용 안함
            if (nextItem.viewType == "BAN_API_MORE_GBA") {
                isSameToNext = true
            } else {
                if (EmptyUtils.isNotEmpty(nextItem) && EmptyUtils.isNotEmpty(nextItem.viewType) &&
                        EmptyUtils.isNotEmpty(moduleList) && EmptyUtils.isNotEmpty(moduleList.viewType) && moduleList.viewType == nextItem.viewType) {
                    isSameToNext = true
                }
            }
        }
        if (isSameToNext) {
            mViewBottomDivider1dp.visibility = View.VISIBLE
            mViewBottomDivider10dp.visibility = View.GONE
        } else {
            mViewBottomDivider1dp.visibility = View.GONE
            mViewBottomDivider10dp.visibility = View.VISIBLE
        }
    }

    private fun setNextItemMarginShopItem(list: List<ShopInfo.ShopItem>, position: Int) {
        var isSameToNext = false
        val moduleList = list[position].sectionContent
        if (list.size > position + 1) {
            val nextItem = list[position + 1].sectionContent
            //브랜드 더보기 뷰홀더는 10dp의 마진을 적용 안함
            if (nextItem.viewType == "BAN_API_MORE_GBA") {
                isSameToNext = true
            } else {
                if (EmptyUtils.isNotEmpty(nextItem) && EmptyUtils.isNotEmpty(nextItem.viewType) &&
                        EmptyUtils.isNotEmpty(moduleList) && EmptyUtils.isNotEmpty(moduleList.viewType) && moduleList.viewType == nextItem.viewType) {
                    isSameToNext = true
                }
            }
        }
        if (isSameToNext) {
            mViewBottomDivider1dp.visibility = View.VISIBLE
            mViewBottomDivider10dp.visibility = View.GONE
        } else {
            mViewBottomDivider1dp.visibility = View.GONE
            mViewBottomDivider10dp.visibility = View.VISIBLE
        }
    }

    /**
     * 매장 좌우 플리킹시 재생중인 동영상을 정지한다.
     * 동영상 재생 시 재생중인 다른 동영상을 정지한다.
     *
     * @param event Events.VodShopPlayerEvent
     */
    fun onEvent(event: VodShopPlayerEvent) {
        if (EmptyUtils.isEmpty(mPlayer)) {
            return
        }
        if (clickFullScreen) {
            return
        }
        if (event.action == VodShopPlayerEvent.VodPlayerAction.VOD_STOP) {
            removePlayer()
        }
        if (event.action == VodShopPlayerEvent.VodPlayerAction.VOD_INIT_OTHERS
                && adapterPosition != event.position) {
            removePlayer()
        }
    }

    /**
     * 동영상 전체화면의 액션을 전달받아 와이즈로그로 전송한다.
     *
     * @param event Events.VideoActionEvent
     */
    fun onEvent(event: VideoActionEvent) {
        //매장내 동영상이 복수개이므로 videoId 비교하여 내것만 보낸다.
        if (::mMediaInfo.isInitialized && mMediaInfo.videoId == event.videoId) {
            sendWiseLogPrdNative(event.playerAction, event.playTime, event.totalTime)
            if (EmptyUtils.isNotEmpty(mPlayer) && (event.playerAction == PlayerAction.MINI || event.playerAction == PlayerAction.EXIT)) {
                mPlayer!!.onFullscreenDisabled(false)
                isBackFullScreen = true
                Handler().postDelayed({ isBackFullScreen = false }, 1000)
            }
        }
    }

    override fun onVideoSizeKnown() {
        ViewUtils.hideViews(mProgressBar)
    }

    override fun videoDurationChanged(duration: Int) {
        //전체화면에서 복귀한 경우 스킵
        if (isBackFullScreen) {
            return
        }

        //재생버튼 클릭 시 효율코드(PLAY) 전송 용도 (효율 전송시 일괄적으로 브라이트코브 데이타를 사용하기 위함)
        //이렇게 하면 API에서 내려주는 재생시간을 사용하지 않으므로 재생시간 비동기 문제가 발생 안함
        if (isFirst && !mIvPlayImg.isShown) {
            isFirst = false
            sendWiseLogPrdNative(PlayerAction.PLAY, 0, duration)
        }
    }

    override fun sendWiseLogPrdNative(action: PlayerAction, playTime: Int, totalTime: Int) {
        //동일한 액션이 중복 호출될 경우 스킵
        if (ClickUtils.work2(300) && action == mOldAction) {
            return
        }
        mOldAction = action
        val videoId = if (::mMediaInfo.isInitialized) mMediaInfo.videoId else ""
        val url = WEB.MSEQ_SIGNATURE_VOD.replace("#PT", (playTime / 1000).toString())
                .replace("#TT", (totalTime / 1000).toString())
                .replace("#VID", videoId)
                .replace("#ACTION", action.toString())
        (mContext as AbstractBaseActivity).setWiseLogHttpClient(url)
    }

    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        EventBus.getDefault().register(this)
    }

    override fun onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow()
        EventBus.getDefault().unregister(this)
        removePlayer()
    }

    companion object {
        /**
         * 비디오 프레그먼트 아이디참조 인덱스
         */
        private var count = 0
    }

    init {
        val pageMarginPx = itemView.resources.getDimensionPixelOffset(R.dimen.signature_carousel_pageMargin)
        val offsetPx = itemView.resources.getDimensionPixelOffset(R.dimen.signature_carousel_offset)
        mAdapterImg = GrBrdGbaImgAdapter()
        mViewPagerImg.adapter = mAdapterImg
        mViewPagerImg.offscreenPageLimit = 1
        mAdapter = GrBrdGbaPrdAdapter()
        mViewPager.adapter = mAdapter
        mViewPager.clipToPadding = false
        mViewPager.clipChildren = false
        mViewPager.offscreenPageLimit = 1
        mViewPager.setPageTransformer { page: View, position: Float ->
            val offset = position * -(2 * offsetPx + pageMarginPx)
            page.translationX = offset
        }

        //뷰페이저와 인디케이터 연결
        TabLayoutMediator(mTabLayout, mViewPager, TabConfigurationStrategy { _: TabLayout.Tab?, _: Int -> }).attach()

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true
    }
}