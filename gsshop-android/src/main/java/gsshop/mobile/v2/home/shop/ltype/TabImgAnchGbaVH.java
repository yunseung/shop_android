package gsshop.mobile.v2.home.shop.ltype;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.StringUtils.trim;

public class TabImgAnchGbaVH extends BaseViewHolderV2 {

    /**
     * list 총 갯수에 따라 보여줄 모양이 달라 두개의 layout으로 구분 해 놓음.
     */
    LinearLayout mLayoutOver3, mLayoutUnder2;

    private String mNavigationId;

    private int mSelectedColor = Color.parseColor("#111111");
    private int mDefaultColor = Color.parseColor("#ffffffff");

    /**
     * @param itemView
     */
    public TabImgAnchGbaVH(View itemView, String navigationId) {
        super(itemView);
        mLayoutOver3 = itemView.findViewById(R.id.layout_over_3);
        mLayoutUnder2 = itemView.findViewById(R.id.layout_under_2);

        mNavigationId = navigationId;
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
            if (mLayoutOver3 == null) {
                return;
            }
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
//            for (ModuleList item : list) {
            for (int i = 0; i < list.size(); i++) {
                ModuleList item = list.get(i);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        DisplayUtils.convertDpToPx(context, 80), DisplayUtils.convertDpToPx(context, 98));

                View view = ((Activity) context).getLayoutInflater()
                        .inflate(R.layout.home_row_type_l_menu_img_cx_gba_item, null);
                addItemView(context, i, view, item);

//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams.weight = 1;
                view.setTag(item.tabSeq);

                if (mLayoutOver3.getChildCount() == 0) {
                    CircleImageView imageView = view.findViewById(R.id.image_thumb);
                    if (imageView != null) {
                        imageView.setBorderColor(mSelectedColor);
                    }
                }
                mLayoutOver3.addView(view, layoutParams);
            }
        }
        else {
            if (mLayoutUnder2 == null) {
                return;
            }

            ViewUtils.showViews(mLayoutUnder2);
            ViewUtils.hideViews(mLayoutOver3);

            View clickViewL = mLayoutUnder2.findViewById(R.id.view_item_left);
            View clickViewR = mLayoutUnder2.findViewById(R.id.view_item_right);

            CircleImageView imgSumbNailL = mLayoutUnder2.findViewById(R.id.img_under_2_1st);
            CircleImageView imgSumbNailR = mLayoutUnder2.findViewById(R.id.img_under_2_2nd);

            TextView txtTitleL = mLayoutUnder2.findViewById(R.id.txt_under_2_1st);
            TextView txtTitleR = mLayoutUnder2.findViewById(R.id.txt_under_2_2nd);

            try {
                try {
                    Glide.with(context).load(trim(list.get(0).imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).dontAnimate().into(imgSumbNailL);
                    Glide.with(context).load(trim(list.get(1).imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).dontAnimate().into(imgSumbNailR);
                } catch (Exception er) {
                    Ln.e(er.getMessage());
                }

                txtTitleL.setText(list.get(0).title1);
                txtTitleR.setText(list.get(1).title1);

                imgSumbNailL.setTag(list.get(0).tabSeq);
                imgSumbNailR.setTag(list.get(1).tabSeq);
            }
            catch (NullPointerException e) {
                Ln.e(e.getMessage());
            }
            catch (IndexOutOfBoundsException e) {
                Ln.e(e.getMessage());
            }

            // 왼쪽 클릭 리스너
            if (clickViewL != null && list.size() > 0) {
                clickViewL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtils.work(1000)) {
                            return;
                        }

                        setSelectedItem(0, list.get(0).tabSeq, 88);
                        setEnabledItemLooks(0);
                    }
                });
            }
            // 오늘쪽 뷰 클릭 리스너.
            if (clickViewR != null && list.size() > 1) {
                clickViewR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtils.work(1000)) {
                            return;
                        }

                        setSelectedItem(1, list.get(1).tabSeq, 88);
                        setEnabledItemLooks(1);
                    }
                });
            }
        }

        // 최초 0번째 선택 하도록 한다.
        setEnabledItemLooks(0);
    }

    /**
     * 3개 이상일 때에 뷰들을 추가해준다.
     * @param context
     * @param view
     * @param item
     */
    private void addItemView(Context context, int position, View view, ModuleList item) {
        if (view != null) {
            // thumbnail인데... 오타입니다.
            CircleImageView imgSumbnail = view.findViewById(R.id.img_sumbnail);
            TextView txtTitle = view.findViewById(R.id.txt_title);

            Glide.with(context).load(trim(item.imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).dontAnimate().into(imgSumbnail);
            txtTitle.setText(item.title1);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtils.work(1000)) {
                        return;
                    }

                    setSelectedItem(position, item.tabSeq, 110);

                    int childIndex = mLayoutOver3.indexOfChild(v);
                    setEnabledItemLooks(childIndex);
                }
            });
        }
    }

    /**
     * 아이템 선택 시 해당 아이템에 보더 칼라 칠해준다.
     * @param position
     */
    private void setEnabledItemLooks(int position) {
        if (mLayoutUnder2.getVisibility() == View.VISIBLE) {
            CircleImageView imgSumbNailL = mLayoutUnder2.findViewById(R.id.img_under_2_1st);
            CircleImageView imgSumbNailR = mLayoutUnder2.findViewById(R.id.img_under_2_2nd);
            if (position == 0) {
                imgSumbNailL.setBorderColor(mSelectedColor);
                imgSumbNailR.setBorderColor(mDefaultColor);
            }
            else if (position == 1) {
                imgSumbNailR.setBorderColor(mSelectedColor);
                imgSumbNailL.setBorderColor(mDefaultColor);
            }
}
        else if (mLayoutOver3.getVisibility() == View.VISIBLE) {
            if (position < 0 || position >= mLayoutOver3.getChildCount()) {
                return;
            }
            for (int i=0; i<mLayoutOver3.getChildCount(); i++) {
                View view = mLayoutOver3.getChildAt(i);
                try {
                    CircleImageView thumbNail = view.findViewById(R.id.img_sumbnail);
                    if (position == i) {
                        thumbNail.setBorderColor(mSelectedColor);
                    }
                    else {
                        thumbNail.setBorderColor(mDefaultColor);
                    }
                }
                catch (NullPointerException e) {
                    Ln.e(e.getMessage());
                }
            }
        }
    }

    @Override
    public void setSelectedItem(int selctedItem) {
        setEnabledItemLooks((selctedItem));
    }

    /**
     * 선택 했을 떄에 이동을 위해.
     * @param tabSeq
     * @param offset
     */
    private void setSelectedItem(int position, String tabSeq, int offset) {

        EventBus.getDefault().post(new Events.FlexibleEvent.GSChoiceStopScrollEvent());

        EventBus.getDefault().postSticky(new Events.ClickEventLtypeAnchor(mNavigationId, position, tabSeq, offset));
    }

    /**
     * tab seq를 이용하여 선택된 녀석의 이미지 테두리 변경
     * 기존에 없던걸 요청하는거라 디자인 변경.
     * @param tabSeq
     */
    @Override
    public boolean setSelectedTabPosition(String tabSeq) {
        if (!TextUtils.isEmpty(tabSeq)) {
            if (mLayoutUnder2.getVisibility() == View.VISIBLE) {
                CircleImageView imageL = mLayoutUnder2.findViewById(R.id.img_under_2_1st);
                CircleImageView imageR = mLayoutUnder2.findViewById(R.id.img_under_2_2nd);
                if (tabSeq.equals(imageL.getTag())) {
                    imageL.setBorderColor(mSelectedColor);
                    imageR.setBorderColor(mDefaultColor);
                }
                else if (tabSeq.equals(imageR.getTag())) {
                    imageR.setBorderColor(mSelectedColor);
                    imageL.setBorderColor(mDefaultColor);
                }
            }
            else if (mLayoutOver3.getVisibility() == View.VISIBLE) {

                /**
                 * 이상한 tabSeq가 들어있을 때에는 아무 동작 하지 않도록
                 */
                boolean isExist = false;
                for (int i=0; i<mLayoutOver3.getChildCount(); i++) {
                    View view = mLayoutOver3.getChildAt(i);
                    if (tabSeq.equals(view.getTag())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    return false;
                }

                for (int i=0; i<mLayoutOver3.getChildCount(); i++) {
                    View view = mLayoutOver3.getChildAt(i);
                    CircleImageView imageView= view.findViewById(R.id.img_sumbnail);

                    if (tabSeq.equals(view.getTag())) {
                        imageView.setBorderColor(mSelectedColor);
                    }
                    else {
                        imageView.setBorderColor(mDefaultColor);
                    }
                }
            }
        }
        return true;
    }
}
