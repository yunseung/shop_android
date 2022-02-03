package gsshop.mobile.v2.home.shop.vod;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.ImageUtil;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * landscape vod view holder
 */
public class BanVodGbaVH extends VodBannerVodSharedViewHolder {

    /**
     * @param itemView
     */
    public BanVodGbaVH(View itemView) {
        super(itemView);

        mVodShape = VodBannerShape.VOD_SHAPE_LANDSCAPE;
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        // 뷰 크기 조절 및 뷰 위치 이동하는 함수.
        if (videoImageFitToH != null) {
            ImageUtil.loadImageResizeToHeight(context, content.imageUrl, videoImageFitToH, 0);
        }
        Glide.with(context).load(trim(content.imageUrl)).placeholder(0).diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new GlideDrawableImageViewTarget(videoImage) {
                    @Override
                    public void onResourceReady(final GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> animation) {
                        view.setImageResource(android.R.color.transparent);
                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        super.onResourceReady(resource, animation);

                    }
                });
    }
}
