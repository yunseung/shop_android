/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import activitytransition.ActivityTransitionLauncher;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.nalbang.ShortbangActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.ImageUtil;

/**
 * 숏방 단품 item
 */
@SuppressLint("NewApi")
public class SbtVH extends BaseViewHolder {

    private final View[] product_layout = new View[3];
    private final ImageView[] image_thumb = new ImageView[3];
    private final TextView[] text_title = new TextView[3];
    private final View[] view_video = new View[3];


    /**
     * @param itemView
     */
    public SbtVH(View itemView) {
        super(itemView);

        product_layout[0] = itemView.findViewById(R.id.product_item_1);
        product_layout[1] = itemView.findViewById(R.id.product_item_2);
        product_layout[2] = itemView.findViewById(R.id.product_item_3);


        //이미지 비율을 맞추기 위해 사이즈를 계산한다.
        DisplayMetrics metrics = MainApplication.getAppContext().getResources().getDisplayMetrics();
        int deviceWidth = metrics.widthPixels;
        float previewWidth = deviceWidth - convertPixel(deviceWidth, 42,  MainApplication.getAppContext());
        int itemHeight = (int)((previewWidth / 3) * 1.78);

        for(int i = 0 ; i < product_layout.length ; i ++){
            image_thumb[i] = (ImageView)product_layout[i].findViewById(R.id.image_thumb);
            text_title[i] = (TextView)product_layout[i].findViewById(R.id.text_title);
            view_video[i] = product_layout[i].findViewById(R.id.view_video);

            image_thumb[i].getLayoutParams().height = (int)itemHeight;
            image_thumb[i].requestLayout();
        }
    }


    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info, final String action, final String label, String sectionName) {

        final SectionContentList item = info.contents.get(position).sectionContent;

        for(int i = 0 ; i < product_layout.length ; i ++){
            product_layout[i].setVisibility(View.INVISIBLE);
        }

        for(int i = 0 ; i < item.subProductList.size() ; i ++){
            final int index = i;
            ImageUtil.loadImageFit(context, item.subProductList.get(i).imageUrl, image_thumb[i], R.drawable.noimg_logo);
            //단품에서 플레이 버튼 제거
            view_video[i].setVisibility(View.GONE);
            text_title[i].setText(item.subProductList.get(i).promotionName);
            product_layout[i].setVisibility(View.VISIBLE);

            product_layout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //안드로이드 버전이 젤리빈 이하일때는 숏방 이동 제한
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        new CustomOneButtonDialog((HomeActivity)context) .message(R.string.shortbang_version_old)
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
                    intent.putExtra(Keys.INTENT.SHORTBANG_LINK, item.subProductList.get(index).linkUrl);
                    intent.putExtra(Keys.INTENT.SHORTBANG_PRELOAD_IMAGE, item.subProductList.get(index).imageUrl);
                    intent.putExtra(Keys.INTENT.SHORTBANG_PRODUCT_INDEX, index);
                    intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.CATEGORY);
                    intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.CATEGORY);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ActivityTransitionLauncher.with((HomeActivity)context).from(product_layout[index],"image")
                                    .image(ImageUtil.getImageViewBitmap(image_thumb[index])).launch(intent);
                        }
                    }, 50);

                    //와이즈로그 전송
                    ((HomeActivity) context).setWiseLog(item.subProductList.get(index).wiseLog);
                }
            });
        }
    }

    public float convertPixel(int baseWidth, float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float height = px;
        float ratio = height / baseWidth;

        px = (int) (metrics.widthPixels * ratio);

        return px;
    }


}
