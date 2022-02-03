package gsshop.mobile.v2.home.shop.event.renewal.Holder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.StringUtils.trim;

public class BanImgCxGbaVH extends BaseViewHolderV2 {

    /**
     * list 총 갯수에 따라 보여줄 모양이 달라 두개의 layout으로 구분 해 놓음.
     */
    LinearLayout mLayoutOver3, mLayoutUnder2;

    /**
     * @param itemView
     */
    public BanImgCxGbaVH(View itemView) {
        super(itemView);
        mLayoutOver3 = itemView.findViewById(R.id.layout_over_3);
        mLayoutUnder2 = itemView.findViewById(R.id.layout_under_2);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);

        ModuleList content = moduleList.get(position);
        List<ModuleList> list = content.moduleList;

        if (list == null || !(list.size() > 1)) {
            return;
        }

        // 리스트가 3개 이상일 때와 2개일 때 다른 뷰를 사용한다.
        if (list.size() > 2) {
            ViewUtils.showViews(mLayoutOver3);
            ViewUtils.hideViews(mLayoutUnder2);

            // 3개 일 때 좌우 패딩을 14.5 를 주어야 디자인 가이드 대로 표시 된다.
            if (list.size() == 3) {
                mLayoutOver3.setPadding(DisplayUtils.convertDpToPx(context, 14.5f),
                        mLayoutOver3.getPaddingTop(),
                        DisplayUtils.convertDpToPx(context, 14.5f),
                        mLayoutOver3.getPaddingBottom());
            }
            // 4개 일 때 좌우 패딩을 5.5 를 주어야 디자인 가이드 대로 표시 된다.
            // 5애 일 떄의 디자인 가이드는 없으니 뭐...
            else {
                mLayoutOver3.setPadding(DisplayUtils.convertDpToPx(context, 5.5f),
                        mLayoutOver3.getPaddingTop(),
                        DisplayUtils.convertDpToPx(context, 5.5f),
                        mLayoutOver3.getPaddingBottom());
            }

            mLayoutOver3.removeAllViews();
            for (ModuleList item : list) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        DisplayUtils.convertDpToPx(context, 80), DisplayUtils.convertDpToPx(context, 98));

                View view = ((Activity) context).getLayoutInflater()
                        .inflate(R.layout.home_row_type_l_menu_img_cx_gba_item, null);
                addItemView(context, view, item);

//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams.weight = 1;
                mLayoutOver3.addView(view, layoutParams);
            }
        }
        else {
            ViewUtils.showViews(mLayoutUnder2);
            ViewUtils.hideViews(mLayoutOver3);

            View clickViewL = mLayoutUnder2.findViewById(R.id.view_item_left);
            View clickViewR = mLayoutUnder2.findViewById(R.id.view_item_right);

            // 왼쪽 클릭 리스너
            if (clickViewL != null && list.size() > 0) {
                clickViewL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // wise log
                        ((HomeActivity) context).setWiseLog(list.get(0).wiseLog);
                        // 클릭시 이동할 링크
                        WebUtils.goWeb(context, list.get(0).linkUrl);
                    }
                });
            }
            // 오늘쪽 뷰 클릭 리스너.
            if (clickViewR != null && list.size() > 1) {
                clickViewR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // wise log
                        ((HomeActivity) context).setWiseLog(list.get(1).wiseLog);
                        // 클릭시 이동할 링크
                        WebUtils.goWeb(context, list.get(1).linkUrl);
                    }
                });
            }

            ImageView imgSumbNailL = mLayoutUnder2.findViewById(R.id.img_under_2_1st);
            ImageView imgSumbNailR = mLayoutUnder2.findViewById(R.id.img_under_2_2nd);

            TextView txtTitleL = mLayoutUnder2.findViewById(R.id.txt_under_2_1st);
            TextView txtTitleR = mLayoutUnder2.findViewById(R.id.txt_under_2_2nd);

            try {
                try {
                    Glide.with(context).load(trim(list.get(0).imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).dontAnimate().into(imgSumbNailL);
                    Glide.with(context).load(trim(list.get(1).imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).dontAnimate().into(imgSumbNailR);
                } catch (IllegalArgumentException er) {
                    Ln.e(er.getMessage());
                }

                txtTitleL.setText(list.get(0).title1);
                txtTitleR.setText(list.get(1).title1);
            }
            catch (NullPointerException e) {
                Ln.e(e.getMessage());
            }
            catch (IndexOutOfBoundsException e) {
                Ln.e(e.getMessage());
            }
        }
    }

    private void addItemView(Context context, View view, ModuleList item) {
        if (view != null) {
            ImageView imgSumbnail = view.findViewById(R.id.img_sumbnail);
            TextView txtTitle = view.findViewById(R.id.txt_title);

            try {
                Glide.with(context).load(trim(item.imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).dontAnimate().into(imgSumbnail);
            } catch (IllegalArgumentException er) {
                Ln.e(er.getMessage());
            }
            txtTitle.setText(item.title1);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // wise log
                    ((HomeActivity) context).setWiseLog(item.wiseLog);
                    // 클릭시 이동할 링크
                    WebUtils.goWeb(context, item.linkUrl);
                }
            });
        }
    }
}
