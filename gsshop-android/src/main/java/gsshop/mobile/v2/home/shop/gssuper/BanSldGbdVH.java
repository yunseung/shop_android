package gsshop.mobile.v2.home.shop.gssuper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.flexible.BanSldGbbVH;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

public class BanSldGbdVH extends BanSldGbbVH {
    private ToggleButton mBtnToggleAuto;
    private View mBtnAuto;
    private View mBtnCount;

    private ImageView mImgOne;

    private static boolean isRollingAuto = true;

    /**
     * @param itemView
     */
    public BanSldGbdVH(View itemView) {
        super(itemView);
        mBtnToggleAuto = (ToggleButton) itemView.findViewById(R.id.btn_auto);
        mBtnToggleAuto.setClickable(false);
        mBtnAuto = (View) itemView.findViewById(R.id.layout_btn_auto);
        mBtnCount = (View) itemView.findViewById(R.id.layout_btn_count);
//        btnAuto.setOnCheckedChangeListener(checkedChangeListener);
        isCrop = true;

        mImgOne = itemView.findViewById(R.id.imageview_1_img);
    }

    @Override
    public void onViewAttachedToWindow() {
        setAutoRolling();
        EventBus.getDefault().register(this);
    }

    // 롤링 배너.
    @Override
    public void onBindViewHolder(final Context context, int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        setBannerHeight(context.getResources().getDimensionPixelSize(R.dimen.gssuper_roll_banner_item_height));
        setScaleType(ImageView.ScaleType.FIT_XY);

        /*
        ======================================================
        테스트용 코드 delay가 설정 안되어서 내려와서 자동 롤링이 안된다.
        ======================================================
         */
        final ShopInfo.ShopItem content = info.contents.get(position);
        if (!(content.sectionContent.rollingDelay > 0)) {
            content.sectionContent.rollingDelay = 2;
            info.contents.set(position, content);
        }
//      ======================================================

        super.onBindViewHolder(context, position, info, action, label, sectionName);

        final List<SectionContentList> list = content.sectionContent.subProductList;

        if (list == null || !(list.size() > 0)) {
            ViewUtils.hideViews(mBtnAuto, mBtnCount, mBtnToggleAuto);
            return;
        }
        else if (list.size() == 1) {
            ViewUtils.hideViews(mBtnAuto, mBtnCount, mBtnToggleAuto);
            ViewUtils.showViews(mImgOne);
            ImageUtil.loadImageResize(context, list.get(0).imageUrl, mImgOne, R.drawable.noimage_375_188);
            mImgOne.setOnClickListener(v -> {
                WebUtils.goWeb(context, list.get(0).linkUrl);

                String tempLabel = String.format("%s_%s_%s", label, position, list.get(0).linkUrl);
                GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
            });
            return;
        }
        else {
            ViewUtils.showViews(mBtnAuto, mBtnCount, mBtnToggleAuto);
            ViewUtils.hideViews(mImgOne);
        }

//        final ShopInfo.ShopItem content = info.contents.get(position);

        // 자동
//        if(content.sectionContent.rollingDelay > 0 && content.sectionContent.subProductList != null && content.sectionContent.subProductList.size() > 1) {
//        현재 자동 롤링 가능 여부 받아오는 함수
        if (isRollingAuto) {
            mBtnToggleAuto.setChecked(true);
        }
        else {
            mBtnToggleAuto.setChecked(false);
        }

        mBtnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnToggleAuto.setChecked(!mBtnToggleAuto.isChecked());
                setAutoRolling();
            }
        });
        mBtnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllImagePopup(context, content);
            }
        });
    }

    private void setAutoRolling() {
        isRollingAuto = mBtnToggleAuto.isChecked();
        setRollingIsAuto(isRollingAuto);
        if (isRollingAuto) {
            startTimer();
            mBtnToggleAuto.setContentDescription("일시정지");
        }
        else {
            stopTimer();
            mBtnToggleAuto.setContentDescription("시작");
        }
    }

    private void showAllImagePopup(Context context, ShopInfo.ShopItem content) {
        Intent i = new Intent(context, GSSuperFullScreenBannerPopup.class);

        ArrayList<GSSuperPopupProductsInfo> arrayListGSSuperPopupInfo =
                new ArrayList<GSSuperPopupProductsInfo>();
        for (SectionContentList list : content.sectionContent.subProductList) {
            arrayListGSSuperPopupInfo.add(new GSSuperPopupProductsInfo(list.imageUrl, list.linkUrl));
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Keys.INTENT.INTENT_DATA_GSSUPER_POPUP,
                arrayListGSSuperPopupInfo);

        i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(i);
    }
}
