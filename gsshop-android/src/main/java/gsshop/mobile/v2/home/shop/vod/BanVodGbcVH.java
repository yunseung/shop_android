package gsshop.mobile.v2.home.shop.vod;

import android.content.Context;
import android.view.View;

import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.ImageUtil;

/**
 * square vod view holder
 */
public class BanVodGbcVH extends VodBannerVodSharedViewHolder {

    /**
     * @param itemView
     */
    public BanVodGbcVH(View itemView) {
        super(itemView);

        mVodShape = VodBannerShape.VOD_SHAPE_SQUARE;
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        // 뷰 크기 조절 및 뷰 위치 이동하는 함수.
        if (videoImageFitToH != null) {
            ImageUtil.loadImageResizeToHeight(context, content.imageUrl, videoImageFitToH, 0);
        }
        ImageUtil.loadImageResizeToHeight(context, content.imageUrl, videoImage, 0);
    }
}
