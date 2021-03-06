package gsshop.mobile.v2.home.shop.renewal.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.ModuleList
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.ltype.BanSldGbeVH
import gsshop.mobile.v2.home.shop.ltype.BanSldGbgVH
import gsshop.mobile.v2.home.shop.renewal.flexible.MolocoPrdCSqVH
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCSqVH
import gsshop.mobile.v2.user.User
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.StringUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.layout_common_title.view.*

class CommonTitleLayout : LinearLayout {

    private val mContext: Context = context

    private lateinit var mLlRoot: LinearLayout
    private lateinit var mTvUserName: TextView
    private lateinit var mTvTitle: TextView
    private lateinit var mIvTitle: ImageView
    private lateinit var mTvDesc: TextView
    private lateinit var mLlViewMore: LinearLayout
    private lateinit var mTvMore: TextView
    private lateinit var mLlAdArea: LinearLayout
    private lateinit var mIvSeparator: ImageView

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_common_title, this, true)
        mLlRoot = ll_root
        mTvUserName = tv_user_name
        mTvTitle = tv_title
        mIvTitle = iv_title
        mTvDesc = tv_desc
        mLlViewMore = ll_view_more
        mTvMore = tv_more
        mLlAdArea = ll_ad_area
        mIvSeparator = iv_separator
    }


    @SuppressLint("SetTextI18n")
    fun setCommonTitle(viewHolder: RecyclerView.ViewHolder, obj: Any) {
        mTvUserName.visibility = View.GONE


        // ????????? ????????? ??????.

        if (((viewHolder is PrdCSqVH) && (obj is SectionContentList)) || ((viewHolder is MolocoPrdCSqVH) && (obj is SectionContentList))) {
            if (obj.viewType != null) {
                if (("SRL" in obj.viewType!! || "PRD_C_SQ" in obj.viewType!! || "MOLOCO_PRD_C_SQ" in obj.viewType!!)) {
                    val user = User.getCachedUser()

                    if ("Y" == obj.useName) {
                        mTvUserName.visibility = View.VISIBLE
                        if (user != null) {
                            mTvUserName.text = user.userName + mContext.getString(R.string.user)
                        } else {
                            //?????????????????? ?????????
                            mTvUserName.text = mContext.getString(R.string.customer) + mContext.getString(R.string.user)
                        }
                    } else {
                        mTvUserName.visibility = View.GONE
                    }
                }
            }
        }
        // BAN_SLD_GBE ??? ?????? ?????? ?????? ?????????.
        if (viewHolder is BanSldGbeVH) {
            mLlRoot.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gschoice_gray))
        }

        // BAN_SLD_GBG ??? ?????? ???????????? ??? ??????????????? ?????????.
        if (viewHolder is BanSldGbgVH) {
            val params = mLlRoot.layoutParams as LayoutParams
            params.height = DisplayUtils.convertDpToPx(mContext, 40F)
            mLlRoot.layoutParams = params
            mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
        }

        var isLtypeData = false
        if (obj is ModuleList || viewHolder is BanSldGbgVH) {
            isLtypeData =true
        }

        // L Type ??? ??????.
        if (isLtypeData) {
            obj as SectionContentList
            if (TextUtils.isEmpty(obj.badgeRTType)) {
                if (TextUtils.isEmpty(obj.tabImg)) {
                    mTvTitle.visibility = View.VISIBLE
                    mTvTitle.text = obj.name
                    if (!TextUtils.isEmpty(obj.textColor)) {
                        if (StringUtils.checkHexColor("#" + obj.textColor)) {
                            mTvTitle.setTextColor(Color.parseColor("#" + obj.textColor))
                        }
                    }
                } else {
                    mTvTitle.visibility = View.GONE
                    mIvTitle.visibility = View.VISIBLE
                    ImageUtil.loadImageBadge(mContext, obj.tabImg, mIvTitle, R.drawable.noimage_375_188, ImageUtil.BaseImageResolution.HD)
                }
                if (TextUtils.isEmpty(obj.linkUrl)) {
                    mLlViewMore.visibility = View.GONE
                } else {
                    mLlViewMore.visibility = View.VISIBLE
                    mLlViewMore.setOnClickListener { WebUtils.goWeb(mContext, obj.linkUrl) }
                }

                if (TextUtils.isEmpty(obj.subName)) {
                    mTvDesc.visibility = View.GONE
                } else {
                    mTvDesc.visibility = View.VISIBLE
                    mTvDesc.text = obj.subName
                    if (!TextUtils.isEmpty(obj.textColor)) {
                        if (StringUtils.checkHexColor("#" + obj.textColor)) {
                            mTvDesc.setTextColor(Color.parseColor("#" + obj.textColor))
                        }
                    }
                }
            } else {
                // BadgeRtType (AD, MORE) ?????? ???????????? ??????????????? ?????????????????? ????????????... L ????????? ?????? ????????? ?????? ????????? ??????????????? ?????? ????????? ?????????.
                // ????????? ?????????????????? ?????????.
                if (!TextUtils.isEmpty(obj.subName)) {
                    mTvDesc.text = obj.subName
                }

                // ???????????? ????????? ????????? ????????? ???????????? ??????.

                // ???????????? ????????? ????????? ????????? ???????????? ??????.
                if (TextUtils.isEmpty(obj.name)) {
                    if ("AD" == obj.badgeRTType) {
                        mLlRoot.visibility = View.VISIBLE
                        mLlAdArea.visibility = View.VISIBLE
                        mLlViewMore.visibility = View.GONE
                        mTvTitle.text = ""
                    } else if ("MORE" == obj.badgeRTType && !TextUtils.isEmpty(obj.moreBtnUrl)) {
                        mLlRoot.visibility = View.VISIBLE
                        mLlViewMore.visibility = View.VISIBLE
                        mTvTitle.text = ""
                        mLlAdArea.visibility = View.GONE
                    } else {
                        mLlRoot.visibility = View.GONE
                        mLlViewMore.visibility = View.GONE
                    }
                } else {
                    mLlRoot.visibility = View.VISIBLE
                    mTvTitle.text = obj.name
                    if (!TextUtils.isEmpty(obj.moreBtnUrl) && "MORE" == obj.badgeRTType) {
                        mLlAdArea.visibility = View.GONE
                        mLlViewMore.visibility = View.VISIBLE
                        mLlViewMore.setOnClickListener { WebUtils.goWeb(mContext, obj.moreBtnUrl) }
                        if (!TextUtils.isEmpty(obj.moreText)) {
                            mTvMore.text = obj.moreText
                        }
                    } else if ("AD" == obj.badgeRTType) {
                        mLlViewMore.visibility = View.GONE
                        mLlAdArea.visibility = View.VISIBLE
                    } else {
                        mLlViewMore.visibility = View.GONE
                    }
                }
            }

            // ?????? 1px border ????????? ??????.
            when (obj.viewType) {
                "BAN_TXT_IMG_LNK_GBA", "BAN_TXT_IMG_LNK_GBB" -> {
                    mIvSeparator.visibility = View.VISIBLE
                    if (!TextUtils.isEmpty(obj.bdrBottomYn)) {
                        if ("N" == obj.bdrBottomYn) {
                            mIvSeparator.visibility = View.GONE
                        }
                    }

                    //?????? ?????? ???????????? ?????? ??????????????? ????????? linkUrl ????????? ??????.
                    if (TextUtils.isEmpty(obj.linkUrl)) {
                        mLlViewMore.visibility = View.GONE
                    } else {
                        mLlViewMore.visibility = View.VISIBLE
                        mLlViewMore.setOnClickListener { WebUtils.goWeb(mContext, obj.linkUrl) }
                    }
                }
                else -> {
                    if (!TextUtils.isEmpty(obj.bdrBottomYn)) {
                        if ("Y" == obj.bdrBottomYn) {
                            mIvSeparator.visibility = View.VISIBLE
                        } else {
                            mIvSeparator.visibility = View.GONE
                        }
                    }
                }
            }

        } else if (obj is SectionContentList) {
            if (TextUtils.isEmpty(obj.badgeRTType)) {
                // ???????????? ??????????????? ???????????????.
                if (TextUtils.isEmpty(obj.imageUrl)) {
                    mIvTitle.visibility = View.GONE
                    mTvTitle.visibility = View.VISIBLE
                    if (!TextUtils.isEmpty(obj.productName)) {
                        mTvTitle.text = obj.productName
                    } else {
                        mTvTitle.text = ""
                    }
                    if (!TextUtils.isEmpty(obj.textColor)) {
                        if (StringUtils.checkHexColor("#" + obj.textColor)) {
                            mTvTitle.setTextColor(Color.parseColor("#" + obj.textColor))
                        }
                    }

                    //????????? - ??????????????? ????????? ?????? ?????? ??????
                    if ("BAN_TXT_IMG_LNK_GBB" == obj.viewType || "BAN_TXT_IMG_LNK_GBA" == obj.viewType) {
                        if ("Y" == obj.useName) {
                            if (User.getCachedUser() != null) {
                                //??????????????? OOO???
                                mTvUserName.text = "${User.getCachedUser().userName}${mContext.getString(R.string.user)}"
                                mTvUserName.visibility = View.VISIBLE
                            } else {
                                //?????????????????? ?????????
                                mTvUserName.text = mContext.getString(R.string.customer) + mContext.getString(R.string.user)
                                mTvUserName.visibility = View.VISIBLE
                            }
                        } else {
                            mTvUserName.visibility = View.GONE
                        }
                    }
                } else {
                    mIvTitle.visibility = View.VISIBLE
                    mTvTitle.visibility = View.GONE
                    ImageUtil.loadImageBadge(mContext, obj.imageUrl, mIvTitle, R.drawable.noimage_78_78, ImageUtil.BaseImageResolution.HD)
                }

                // ?????????????????? ?????????.
                if (!TextUtils.isEmpty(obj.promotionName)) {
                    mTvDesc.visibility = View.VISIBLE
                    mTvDesc.text = obj.promotionName
                    if (!TextUtils.isEmpty(obj.textColor)) {
                        if (StringUtils.checkHexColor("#" + obj.textColor)) {
                            mTvDesc.setTextColor(Color.parseColor("#" + obj.textColor))
                        }
                    }
                } else {
                    mTvDesc.visibility = View.GONE
                }

                // ????????? url
                if (TextUtils.isEmpty(obj.linkUrl)) {
                    mLlViewMore.visibility = View.GONE
                } else {
                    mLlViewMore.visibility = View.VISIBLE
                    mLlViewMore.setOnClickListener { WebUtils.goWeb(mContext, obj.linkUrl) }
                }
            } else {
                // ???????????? ????????? ????????? ????????? ???????????? ??????.
                if (TextUtils.isEmpty(obj.productName)) {
                    if ("AD" == obj.badgeRTType) {
                        mLlRoot.visibility = View.VISIBLE
                        mLlAdArea.visibility = View.VISIBLE
                        mLlViewMore.visibility = View.GONE
                        mTvTitle.text = ""
                    } else if ("MORE" == obj.badgeRTType && !TextUtils.isEmpty(obj.moreBtnUrl)) {
                        mLlRoot.visibility = View.VISIBLE
                        mLlViewMore.visibility = View.VISIBLE
                        mTvTitle.text = ""
                        mLlAdArea.visibility = View.GONE
                    } else {
                        mLlRoot.visibility = View.GONE
                        mLlViewMore.visibility = View.GONE
                    }
                } else {
                    mLlRoot.visibility = View.VISIBLE
                    mTvTitle.text = obj.productName
                    if (!TextUtils.isEmpty(obj.moreBtnUrl) && "MORE" == obj.badgeRTType) {
                        mLlAdArea.visibility = View.GONE
                        mLlViewMore.visibility = View.VISIBLE
                        mLlViewMore.setOnClickListener { WebUtils.goWeb(mContext, obj.moreBtnUrl) }
                        if (!TextUtils.isEmpty(obj.moreText)) {
                            mTvMore.text = obj.moreText
                        }
                    } else if ("AD" == obj.badgeRTType) {
                        mLlViewMore.visibility = View.GONE
                        mLlAdArea.visibility = View.VISIBLE
                    } else {
                        mLlViewMore.visibility = View.GONE
                    }
                }
            }
            when (obj.viewType) {
                "BAN_TXT_IMG_LNK_GBA", "BAN_TXT_IMG_LNK_GBB" -> {
                    mIvSeparator.visibility = View.VISIBLE
                    if (!TextUtils.isEmpty(obj.bdrBottomYn)) {
                        if ("N" == obj.bdrBottomYn) {
                            mIvSeparator.visibility = View.GONE
                        }
                    }
                    //?????? ?????? ???????????? ?????? ??????????????? ????????? linkUrl ????????? ??????.
                    if (TextUtils.isEmpty(obj.linkUrl)) {
                        mLlViewMore.visibility = View.GONE
                    } else {
                        mLlViewMore.visibility = View.VISIBLE
                        mLlViewMore.setOnClickListener { WebUtils.goWeb(mContext, obj.linkUrl) }
                    }
                }
                else -> if (!TextUtils.isEmpty(obj.bdrBottomYn)) {
                    if ("Y" == obj.bdrBottomYn) {
                        mIvSeparator.visibility = View.VISIBLE
                    } else {
                        mIvSeparator.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun setSeperateDisable (isEnable: Boolean) {
        if (isEnable) {
            mIvSeparator.visibility = View.VISIBLE
        }
        else {
            mIvSeparator.visibility = View.GONE
        }
    }

    fun setRootBackgroundColor (colorInt : Int) {
        mLlRoot.setBackgroundColor(colorInt)
    }

    fun setTitleTxtColor (colorInt: Int) {
        mTvTitle.setTextColor(colorInt)
    }
}