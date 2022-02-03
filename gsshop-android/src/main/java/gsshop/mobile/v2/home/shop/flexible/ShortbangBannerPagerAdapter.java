package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.viewpager.widget.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TimerTask;

import activitytransition.ActivityTransitionLauncher;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.nalbang.ShortbangActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;


/**
 * 숏방배너 어댑터 pager adapter
 */
public class ShortbangBannerPagerAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<SectionContentList> list;
    private final String action;
    private final String label;
    private final boolean isAdult;
    private final ShopInfo info;
    private TimerTask task;


    public ShortbangBannerPagerAdapter(Context context, ShopInfo info, ArrayList<SectionContentList> list, String action, String label, TimerTask task, boolean isAdult) {
        this.context = context;
        this.info = info;
        this.list = list;
        this.action = action;
        this.label = label;
        this.isAdult = isAdult;
        this.task = task;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.home_row_type_shortbang_item, null);
        RelativeLayout image_layout = (RelativeLayout) view.findViewById(R.id.image_layout);
        final ImageView mainImage = (ImageView) view.findViewById(R.id.image_main);
        TextView text_title = (TextView) view.findViewById(R.id.text_title);
        //단품에서 플레이 버튼 제거
        View view_video = view.findViewById(R.id.view_video);
        view_video.setVisibility(View.GONE);

        DisplayUtils.resizeHeightAtViewToScreenSize(context, image_layout);
        DisplayUtils.resizeHeightAtViewToScreenSize(context, mainImage);

        final SectionContentList item = list.get(position);
        ImageUtil.loadImageFit(context, item.imageUrl, mainImage, R.drawable.noimg_logo);

        text_title.setText(item.promotionName);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    new CustomOneButtonDialog((HomeActivity) context).message(R.string.shortbang_version_old)
                            .cancelable(false)
                            .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                                @Override
                                public void onClick(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return;
                }

                final Intent intent = new Intent(context, ShortbangActivity.class);
                intent.putExtra(Keys.INTENT.SHORTBANG_LINK, item.linkUrl);
                intent.putExtra(Keys.INTENT.SHORTBANG_PRELOAD_IMAGE, item.imageUrl);
                intent.putExtra(Keys.INTENT.SHORTBANG_PRODUCT_INDEX, 0);
                intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.CATEGORY);
                intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.CATEGORY);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityTransitionLauncher.with((HomeActivity) context).from(view, "image")
                                .image(ImageUtil.getImageViewBitmap(mainImage)).launch(intent);
                    }
                }, 50);

                //와이즈로그 전송
                ((HomeActivity) context).setWiseLog(item.wiseLog);
            }
        });


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        // must be overridden else throws exception as not overridden.
        collection.removeView((View) view);
    }

    /* 자동 롤링 종료. */
    public void stopTimer() {
        // timer
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}