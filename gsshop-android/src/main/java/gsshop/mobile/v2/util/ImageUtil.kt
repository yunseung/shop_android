/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.annotation.FloatRange
import com.blankj.utilcode.util.EmptyUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import com.bumptech.glide.request.target.SquaringDrawable
import com.gsshop.mocha.network.rest.RestClient
import com.gsshop.mocha.pattern.mvc.BaseAsyncController
import com.gsshop.mocha.ui.util.ImageUtils
import gsshop.mobile.v2.attach.FileAttachUtils
import gsshop.mobile.v2.util.StringUtils.trim
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import roboguice.RoboGuice
import roboguice.util.Ln
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutionException

/**
 * 이미지 처리관련 클래스
 */
object ImageUtil {
    /**
     * 갤러리이미지의 경우 서버로 전송하기 전에 적당히 축소된 이미지파일로
     * 별도로 만들어둔다. (단, 원본 갤러리 이미지는 변경하지 않음)
     *
     * @param imagePath 이미지경로
     */
    @JvmStatic
    fun resizeGalleryImage(context: Context, imagePath: String?): File? {
        // inSampleSize 는 2 의 배수로 하면 decode*() 에서 연산이 빨라진다
        val srBitmap = ImageUtils.getSampleSizedBitmap(imagePath,
                FileAttachUtils.ATTACH_IMAGE_WIDTH)
        try {
            return ImageUtils.bitmapToFile(srBitmap,
                    FileAttachUtils.getTempAttachImage(context))
        } catch (e: IOException) {
            Ln.e(e)
        }
        return null
    }

    /**
     * 파일이 존재하는지 여부를 반환한다.
     *
     * @param path 파일명을 포함한 전체경로
     * @return 파일이 존재하면 true
     */
    fun isExistFile(path: String?): Boolean {
        var ret = false
        try {
            val file = File(path)
            if (file.exists()) {
                ret = true
            }
        } catch (e: Exception) {
            Ln.e(e)
        }
        return ret
    }

    /**
     * Image Load from Network and set default image if failed (for CircleImageView)
     */
    @JvmStatic
    fun loadImageSetDefault(context: Context, url: String?, view: ImageView,
                            placeholderResId: Int, defaultResId: Int) {
        try {
            Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(object : GlideDrawableImageViewTarget(view) {
                        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                            super.onLoadFailed(e, errorDrawable)
                            view.setImageResource(defaultResId)
                        }
                    })
        } catch (e: Exception) {
            Ln.e(e)
        }
    }

    /**
     * Image Load from Network
     */
    @JvmStatic
    fun loadImage(context: Context, url: String?, view: ImageView?,
                  placeholderResId: Int) {
        // 2015/1106 테스트중 확인된 사항
        // leems 앱 구동로 백키 2번으로 바로 종료 할 겨야우 Glide.with(... 에서 크래쉬 발생 종료 됨
        try {
            // newer versions
            Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(view)
        } catch (e: Exception) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e)
        }
    }

    @JvmStatic
    fun loadImageFit(context: Context, url: String?, view: ImageView,
                     placeholderResId: Int) {
        view.scaleType = ImageView.ScaleType.FIT_CENTER

        // newer versions , .diskCacheStrategy(DiskCacheStrategy.NONE) 디스크 캐쉬를 쓰지 않도록
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        view.scaleType = ImageView.ScaleType.FIT_XY
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    @JvmStatic
    fun loadImageFitCenter(context: Context, url: String?, view: ImageView,
                     placeholderResId: Int) {
        view.scaleType = ImageView.ScaleType.FIT_CENTER

        // newer versions , .diskCacheStrategy(DiskCacheStrategy.NONE) 디스크 캐쉬를 쓰지 않도록
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        view.scaleType = ImageView.ScaleType.FIT_CENTER
                        super.onResourceReady(resource, animation)
                    }
                })
    }

   @JvmStatic
    fun loadImageFitWithRound(context: Context, url: String?, view: ImageView, radius: Int,
                              placeholderResId: Int) {
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .bitmapTransform(RoundedCornersTransformation(context, radius, 0))
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        view.scaleType = ImageView.ScaleType.FIT_XY
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    @JvmStatic
    fun loadImageFitWithRoundCenterCrop(context: Context, url: String?, view: ImageView, radius: Int,
                              placeholderResId: Int) {
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .bitmapTransform(RoundedCornersTransformation(context, radius, 0))
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        view.scaleType = ImageView.ScaleType.CENTER_CROP
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * 2019_11_28 diskCacheStrategy(DiskCacheStrategy.NONE) 적용
     * @param context
     * @param url
     * @param view
     * @param placeholderResId
     */
    @JvmStatic
    fun loadImageTvLive(context: Context, url: String?, view: ImageView,
                        placeholderResId: Int) {
//        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // newer versions
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(PositionedCropTransformation(context, 1f, 0.5f))
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        //                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    @JvmStatic
    fun loadImageRollBanner(context: Context, url: String?, view: ImageView,
                            placeholderResId: Int) {
//        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // newer versions
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .transform(PositionedCropTransformation(context, 0f, 0.5f))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        //                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * load cropped image
     * top-left:
     * 0, 0
     * top-right:
     * 1, 0
     * bottom-left:
     * 0, 1
     * bottom-right:
     * 1, 1
     * Use 0.5f for center
     *
     * @param context Context
     * @param url image url
     * @param view target view
     * @param placeholderResId resource id for a resource to display while a resource is loading
     * @param xPercentage x-axis value
     * @param yPercentage y-axis value
     */
    @JvmStatic
    fun loadImagePositionedCrop(context: Context, url: String?, view: ImageView,
                                placeholderResId: Int,
                                @FloatRange(from = 0.0, to = 1.0) xPercentage: Float,
                                @FloatRange(from = 0.0, to = 1.0) yPercentage: Float) {
        //placeholderResId (디폴트 이미지) 찌그러짐 방지용
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .transform(PositionedCropTransformation(context, xPercentage, yPercentage))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * 주상품에 정사각형 이미지가 들어온 경우 비율대로 확대하고 중앙정렬 한다.
     * 상하는 여백없고 좌우 여백이 생기는 상태가 된다.
     *
     * @param context 컨텍스트
     * @param url 이미지 주소
     * @param view 이미지뷰
     * @param placeholderResId 디폴트 이미지
     */
    @JvmStatic
    fun loadImageTvLiveSmallImage(context: Context, url: String?, view: ImageView,
                                  placeholderResId: Int) {
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        //아래 세팅이 없으면 fit_XY 속성이 자동 적용됨
                        view.setImageResource(R.color.transparent)
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * 뱃지 이미지 전용
     * 이미지의 크기를 그대로 적용한다.(김진희)
     *
     * @param context
     * @param url
     * @param view
     * @param placeholderResId
     * @param resolution BASE_IMAGE_RESOLUTION
     */
    @JvmStatic
    fun loadImageBadge(context: Context, url: String?, view: ImageView,
                       placeholderResId: Int, resolution: BaseImageResolution?) {
        loadImageBadge(context, url, view, placeholderResId, resolution, false);
    }

    /**
     * 뱃지 이미지 전용
     * 이미지의 크기를 그대로 적용한다.(김진희)
     *
     * @param context
     * @param url
     * @param view
     * @param placeholderResId
     * @param resolution BASE_IMAGE_RESOLUTION
     * @param hide true 이면, 이미지로딩 실패시 이미지뷰 GONE 처리
     */
    @JvmStatic
    fun loadImageBadge(context: Context, url: String?, view: ImageView,
                       placeholderResId: Int, resolution: BaseImageResolution?, hide: Boolean) {
        val baseResolution: Int = when (resolution) {
            BaseImageResolution.HD -> 2
            BaseImageResolution.FHD -> 3
            BaseImageResolution.QHD -> 4
            else -> 4
        }
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {

                        //density 값으로 이미지 크기를 fix(김진희)
                        val dm = context.resources.displayMetrics
                        val dWidth = (resource.intrinsicWidth * (dm.density / baseResolution)).toInt()
                        val dHeight = (resource.intrinsicHeight * (dm.density / baseResolution)).toInt()
                        view.post {
                            view.layoutParams.width = dWidth
                            view.layoutParams.height = dHeight
                            view.requestLayout()
                            view.scaleType = ImageView.ScaleType.FIT_XY
                        }
                        super.onResourceReady(resource, animation)
                    }

                    override fun onLoadFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                        if (hide) {
                            view.visibility = View.GONE
                        }
                    }
                })
    }

    @JvmStatic
    fun loadImageResize(context: Context, url: String?, view: ImageView,
                        placeholderResId: Int) {
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        // newer versions
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        val vWidth = view.width
                        //                        final int vHeight = view.getHeight();
                        val dWidth = resource.intrinsicWidth
                        val dHeight = resource.intrinsicHeight
                        val imageSideRatio = dHeight.toFloat() / dWidth.toFloat()
                        view.post { //                                int height = (int) (vWidth * imageSideRatio);
                            view.layoutParams.height = (vWidth * imageSideRatio).toInt()
                            view.requestLayout()
                            view.scaleType = ImageView.ScaleType.FIT_XY
                        }
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * 라운드 적용, 위의 loadImageResize 와 통합하여 사용하려 했으나 간섭 최소화 하기 위해 따로 사용
     */
    @JvmStatic
    fun loadImageResize(context: Context, url: String?, view: ImageView,
                        placeholderResId: Int, radius: Int, cornerType: RoundedCornersTransformation.CornerType?) {
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        val requestBuilder = Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        // newer versions
        if (radius > 0) {
            if (cornerType != null) {
                requestBuilder.bitmapTransform(RoundedCornersTransformation(context, radius, 0, cornerType))
            }
            else {
                requestBuilder.bitmapTransform(RoundedCornersTransformation(context, radius, 0))
            }
        }
        requestBuilder.into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        val vWidth = view.width
                        //                        final int vHeight = view.getHeight();
                        val dWidth = resource.intrinsicWidth
                        val dHeight = resource.intrinsicHeight
                        val imageSideRatio = dHeight.toFloat() / dWidth.toFloat()
                        view.post { //                                int height = (int) (vWidth * imageSideRatio);
                            view.layoutParams.height = (vWidth * imageSideRatio).toInt()
                            view.requestLayout()
                            view.scaleType = ImageView.ScaleType.FIT_XY
                        }
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * 기존 이미지를 높이에 맞추는 뷰를 사용시 height가 0으로 인식되어 정상적으로 표시 안되는 경우가 있어 loadImageBadge 와 동일하게 동작한 후에
     */
    fun loadImageResizeBadge(context: Context, url: String?, view: ImageView,
                                placeholderResId: Int, resolution: BaseImageResolution?) {

        val baseResolution: Int = when (resolution) {
            BaseImageResolution.HD -> 2
            BaseImageResolution.FHD -> 3
            BaseImageResolution.QHD -> 4
            else -> 4
        }
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {

                        var nImgHeight = view.height

                        //density 값으로 이미지 크기를 fix(김진희)
                        val dm = context.resources.displayMetrics
                        val dWidth = (resource.intrinsicWidth * (dm.density / baseResolution)).toInt()
                        val dHeight = (resource.intrinsicHeight * (dm.density / baseResolution)).toInt()

                        var nImgWidth = nImgHeight * dWidth / dHeight

                        if (nImgHeight == 0) {
                            nImgHeight = dHeight
                            nImgWidth = dWidth
                        }

//                        Ln.d("hklim width / height : " + dWidth + " / " + dHeight)
//                        Ln.d("hklim width 2 / height 2 : " + nImgWidth + " / " + nImgHeight)

                        view.post {
                            view.layoutParams.width = nImgWidth
                            view.layoutParams.height = nImgHeight
                            view.requestLayout()
                            view.scaleType = ImageView.ScaleType.FIT_XY
                        }
                        super.onResourceReady(resource, animation)
                    }

                    override fun onLoadFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                    }
                })
    }

    /**
     * 이미지를 높이에 맞춰 양쪽 사이드는 잘리거나 비어 보일 수 있다.
     * @param context
     * @param url
     * @param view
     * @param placeholderResId
     */
    @JvmStatic
    fun loadImageResizeToHeight(context: Context, url: String?, view: ImageView,
                                placeholderResId: Int) {

        view.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)

//                        final int vWidth = view.getWidth();
                        val vHeight = view.height
                        val dWidth = resource.intrinsicWidth
                        val dHeight = resource.intrinsicHeight
                        val imageSideRatio = dWidth.toFloat() / dHeight.toFloat()
                        Ln.e("### $vHeight, $dWidth, $dHeight, $imageSideRatio, ${(vHeight * imageSideRatio).toInt()}");
                        view.post {
                            view.layoutParams.width = (vHeight * imageSideRatio).toInt()
                            view.requestLayout()
                            // FIT_XY 로 하지 않으면 사라지는 경우 있어 수정.
                            view.scaleType = ImageView.ScaleType.FIT_XY
                        }
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * 편성표AB테스트 A타입일때 - mainImgUrl이 있으나 404에러가 나타났을경우 사용되는 메소드
     * 이미지뷰 크기에 맞춰서 center 정렬되어 보이도록하는 기능
     * @param context
     * @param url
     * @param view
     * @param placeholderResId
     */
    @JvmStatic
    fun loadImageResizeToHeight_AB(context: Context, url: String?, view: ImageView,
                                   placeholderResId: Int) {
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)

//                        final int vWidth = view.getWidth();
                        val vHeight = view.height
                        val dWidth = resource.intrinsicWidth
                        val dHeight = resource.intrinsicHeight
                        val imageSideRatio = dWidth.toFloat() / dHeight.toFloat()
                        view.post { // view.getLayoutParams().width = (int) (vHeight * imageSideRatio);
                            // view.requestLayout();
                            // 사실은 scaleType이 어떻든 간에 view 의 width 길이가 설정되기 떄문에 크게 관게 없다.
                            // Log.d("bbori", "url : " + url);
                            view.scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                        super.onResourceReady(resource, animation)
                    }

                    //NoImg일때만 작동한다, noImg를 이미지뷰 크기 맞춰서 FIT!
                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        view.scaleType = ImageView.ScaleType.FIT_XY
                        super.onLoadFailed(e, errorDrawable)
                    }
                })
    }

    /**
     * 이미지를 높이에 맞춰 양쪽 사이드는 잘리거나 비어 보일 수 있다. (캐시데이타 존재시 사용)
     * @param context
     * @param url
     * @param view
     * @param placeholderResId
     */
    @JvmStatic
    @JvmOverloads
    fun loadImageFromCache(context: Context, url: String?, view: ImageView,
                           placeholderResId: Int, scaleType: ScaleType = ScaleType.SCALE_TYPE_FIT_TO_HEIGHT) {
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        when (scaleType) {
                            ScaleType.SCALE_TYPE_FIT_TO_HEIGHT -> {
                                //                        final int vWidth = view.getWidth();
                                val vHeight = view.height
                                val dWidth = resource.intrinsicWidth
                                val dHeight = resource.intrinsicHeight
                                val imageSideRatio = dWidth.toFloat() / dHeight.toFloat()
                                view.post {
                                    view.layoutParams.width = (vHeight * imageSideRatio).toInt()
                                    view.requestLayout()
                                    // 사실은 scaleType이 어떻든 간에 view 의 width 길이가 설정되기 떄문에 크게 관게 없다.
                                    view.scaleType = ImageView.ScaleType.CENTER_CROP
                                }
                            }
                            ScaleType.SCALE_TYPE_FIT_XY -> {
                                view.scaleType = ImageView.ScaleType.FIT_XY
                            }
                            ScaleType.SCALE_TYPE_RESIZE_TO_WIDTH -> {
                                val vWidth = view.width
                                //                        final int vHeight = view.getHeight();
                                val dWidth = resource.intrinsicWidth
                                val dHeight = resource.intrinsicHeight
                                val imageSideRatio = dHeight.toFloat() / dWidth.toFloat()
                                view.post { //                                int height = (int) (vWidth * imageSideRatio);
                                    view.layoutParams.height = (vWidth * imageSideRatio).toInt()
                                    view.requestLayout()
                                    view.scaleType = ImageView.ScaleType.FIT_XY
                                }
                            }
                        }
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * * 이미지를 좌우폭에 맞춰 상하가 잘려 보일수 있다. 액제 크기를 잘 해야 한다.
     * ==> 좌우 픽 해질떄까지 맞춘다.
     * + 받아온 이미지 url 정보가 404와 같은 exception 을 포함한 경우 RequestListener 를 통해 callback 을 줄 수 있다.
     *
     * @param context
     * @param listener
     * @param url
     * @param view
     * @param placeholderResId
     */
    @JvmStatic
    fun loadImageResizeToWidthWithListener(context: Context, listener: RequestListener<String, GlideDrawable>?
                                           , url: String?, view: ImageView, placeholderResId: Int) {
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        val vWidth = view.width
                        //                        final int vHeight = view.getHeight();
                        val dWidth = resource.intrinsicWidth
                        val dHeight = resource.intrinsicHeight
                        val imageSideRatio = dHeight.toFloat() / dWidth.toFloat()
                        view.post { //                                int height = (int) (vWidth * imageSideRatio);
                            view.layoutParams.height = (vWidth * imageSideRatio).toInt()
                            view.requestLayout()
                            view.scaleType = ImageView.ScaleType.FIT_XY
                        }
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * 이미지를 높이에 맞춰 양쪽 사이드는 잘리거나 비어 보일 수 있다.
     * + 받아온 이미지 url 정보가 404와 같은 exception 을 포함한 경우 RequestListener 를 통해 callback 을 줄 수 있다.
     *
     * @param context
     * @param listener
     * @param url
     * @param view
     * @param placeholderResId
     */
    @JvmStatic
    fun loadImageResizeToHeightWithListener(context: Context, listener: RequestListener<String, GlideDrawable>?
                                            , url: String?, view: ImageView, placeholderResId: Int) {
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(context).load(trim(url)).placeholder(placeholderResId)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)

//                        final int vWidth = view.getWidth();
                        val vHeight = view.height
                        val dWidth = resource.intrinsicWidth
                        val dHeight = resource.intrinsicHeight
                        val imageSideRatio = dWidth.toFloat() / dHeight.toFloat()
                        view.post {
                            view.layoutParams.width = (vHeight * imageSideRatio).toInt()
                            view.requestLayout()
                            // 사실은 scaleType이 어떻든 간에 view 의 width 길이가 설정되기 떄문에 크게 관게 없다.
                            view.scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                        super.onResourceReady(resource, animation)
                    }
                })
    }

    /**
     * 프로모션 팝업 전용 이미지 로딩함수
     *
     * @param context
     * @param url
     * @param view
     * @param placeholderResId
     * @param extraView
     */
    @JvmStatic
    fun loadImagePromotion(context: Context, url: String?, view: ImageView,
                           placeholderResId: Int, extraView: View) {
        Glide.with(context).load(trim(url)).placeholder(placeholderResId).diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : GlideDrawableImageViewTarget(view) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 animation: GlideAnimation<in GlideDrawable>) {
                        view.setImageResource(R.color.transparent)
                        val vWidth = view.width
                        //                        final int vHeight = view.getHeight();
                        val dWidth = resource.intrinsicWidth
                        val dHeight = resource.intrinsicHeight
                        val imageSideRatio = dHeight.toFloat() / dWidth.toFloat()
                        view.post {
                            view.layoutParams.height = (vWidth * imageSideRatio).toInt()
                            view.requestLayout()
                            view.scaleType = ImageView.ScaleType.FIT_XY
                        }
                        Handler().postDelayed({ extraView.visibility = View.VISIBLE }, 200)
                        super.onResourceReady(resource, animation)
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                        (context as Activity).finish()
                    }
                })
    }

    @JvmStatic
    fun loadImageCacheUrl(context: Context, url: String?): String? {
        return if (TextUtils.isEmpty(url)) {
            url
        } else try {
            if (URLUtil.isValidUrl(url)) {
                Glide.with(context).load(trim(url)).downloadOnly(-1, -1).get().absolutePath
            } else {
                null
            }
        } catch (e: InterruptedException) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e)
            ""
        } catch (e: ExecutionException) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e)
            ""
        }
    }

    fun getCacheImage(pathName: String): Drawable? {
        return if (pathName.isNotEmpty()) {
            Drawable.createFromPath(pathName)
        } else null
    }

    /**
     * 이미지뷰에서 비트맵을 가져온다.
     * @param view
     * @return
     */
    @JvmStatic
    fun getImageViewBitmap(view: ImageView): Bitmap? {
        var bitmap: Bitmap? = null
        val drawable = view.drawable
        if (drawable is GlideBitmapDrawable) {
            bitmap = drawable.bitmap
        } else if (drawable is TransitionDrawable) {
            val length = drawable.numberOfLayers
            for (i in 0 until length) {
                val child = drawable.getDrawable(i)
                if (child is GlideBitmapDrawable) {
                    bitmap = child.bitmap
                    break
                } else if (child is SquaringDrawable
                        && child.getCurrent() is GlideBitmapDrawable) {
                    bitmap = (child.getCurrent() as GlideBitmapDrawable).bitmap
                    break
                }
            }
        } else if (drawable is SquaringDrawable) {
            bitmap = (drawable.getCurrent() as GlideBitmapDrawable).bitmap
        }
        return bitmap
    }

    private var restClient: RestClient? = null

    /**
     * URL + cache = filesize 붙여서 반환한다
     *
     * 이미 cache= 붙어 있는 경우에는 리턴한다.
     *
     *
     * @param context
     * @param url
     * @return 문제가 있으면 원본을 반환한다.
     */
    @JvmStatic
    fun makeImageUrlWithSize(context: Context, url: String?): String? {
        /**
         *
         * onBindViewHolder 의 Glide.with(context).load(url) url 변조를 하면 항상 헤더를 보게된다
         *
         * 아무리 생각해도 방법이 없어서
         *
         * item.imageUrl 글라이드에 넣을대 변경해서 넣는 방법으로 구상하였다.
         *
         * 꼼수 bind에서 계속 호출되지 않도록 사전에 작업
         * cache <-- url 에 붙어 있는 경우는 원본을 반환
         */
        try {
            //og.e("AAA", "url chache bfore : " + url);
            if (!TextUtils.isEmpty(url) && EmptyUtils.isNotEmpty(Uri.parse(url).getQueryParameter("cache"))) {
                //Log.e("AAA", "url chache param exist : $url");
                return url
            }
            if (restClient == null) {
                restClient = RoboGuice.getInjector(context.applicationContext).getInstance(
                        RestClient::class.java)
            }
            if (!TextUtils.isEmpty(url) && restClient != null) {

                //okhttp3 : 확인
                //헤더만 읽는 프로토콜 또는 어노테이션 확인 ex)put / 또는 다른 개념
                val headers = restClient!!.headForHeaders(url)
                val fileSize = headers.contentLength
                //Log.e("AAA", "url chache param add : $url");
                return Uri.parse(url).buildUpon().appendQueryParameter("cache", fileSize.toString()).build().toString()
            }
            //Log.e("AAA", "url chache ori : " + url);
        } catch (e: Exception) {
        }
        return url
    }

    fun saveBitmapImageToFIle(context: Context, filePath: String?, bmpImg: Bitmap?, listener: FleSaveListener?) {
        FileSaveController(context, filePath, bmpImg, listener).execute()
    }

    fun saveOutput(context: Context, filePath: String?, bitmapImg: Bitmap?): Boolean {
        if (filePath != null) {
            try {
                FileAttachUtils.bitmapToFile(bitmapImg, File(filePath), 100)
            } catch (e: Exception) {
                Ln.e("Cannot open file: " + filePath + " / error message : " + e.message)
                return false
            }
        }
        return true
    }

    enum class BaseImageResolution {
        HD, FHD, QHD
    }

    /**
     * loadImageFromCache 에서 사용될 scaleType
     */
    enum class ScaleType {
        SCALE_TYPE_FIT_TO_HEIGHT,  // 높이에 맞추고 이미지 뷰 크기 조정 없음
        SCALE_TYPE_FIT_XY,  // XY 꽉차게
        SCALE_TYPE_RESIZE_TO_WIDTH // 넓이에 맞추고 이미지 뷰 높이 조정.
    }

    interface FleSaveListener {
        fun onResult(isSuccess: Boolean, errorMessage: String?)
    }

    private class FileSaveController(private val mContext: Context, private val mFilePath: String?
                                     , private var mBitmapTemp: Bitmap?, listener: FleSaveListener?) : BaseAsyncController<Boolean>(mContext) {
        private var mListener: FleSaveListener? = null

        @Throws(Exception::class)
        override fun onPrepare(vararg params: Any) {
            super.onPrepare(*params)
            dialog.show()
        }

        @Throws(Exception::class)
        override fun process(): Boolean {
            //            boolean bResult = com.blankj.utilcode.util.ImageUtils.save(mBitmapTemp, mFilePath, Bitmap.CompressFormat.JPEG);
            return saveOutput(mContext, mFilePath, mBitmapTemp)
        }

        @Throws(Exception::class)
        override fun onSuccess(result: Boolean) {
            super.onSuccess(result)

            // 이미지 저장 결화 받아서 넘기는데.. 일부 폰이 저장이 안된다..
            Handler().postDelayed({
                dialog.dismiss()
                if (mBitmapTemp != null) {
                    mBitmapTemp!!.recycle()
                    mBitmapTemp = null
                }
                mListener?.onResult(result, null)
            }, 100)
        }

        override fun onError(e: Throwable) {
            super.onError(e)
            mListener?.onResult(false, e.message)
        }

        init {
            mListener = listener
        }
    }
}