package gsshop.mobile.v2.home.shop.event.renewal.Adapter;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

public class AdapterTxtImsSldGba extends PagerAdapter {

    private final Context mContext;
    private final List<ModuleList> mList;

    public AdapterTxtImsSldGba(Context context, List<ModuleList> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        if(mList == null) { return 0; }
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (mList == null) {
            return super.instantiateItem(container, position);
        }
        View view = ((Activity) mContext).getLayoutInflater()
                .inflate(R.layout.home_row_type_l_roll_image_sld_gba_item, null);

        // 이미지 표시될 메인 이미지
        ImageView mainImg = view.findViewById(R.id.image_main);
        // 백그라운드 색상 정할 root view
        View rootView = view.findViewById(R.id.layout_root);
        // 설정될 문구들 (Main / Sub)
        TextView txtMain = view.findViewById(R.id.txt_main);
        TextView txtSub = view.findViewById(R.id.txt_sub);

        final ModuleList item = mList.get(position);

        // 색상 설정하는 로직 삭제됨.
//        if (rootView != null) {
//            try {
//                rootView.setBackgroundColor(Color.parseColor("#" + item.bgColor));
//            }
//            catch (IllegalArgumentException e) {
//                Ln.e(e.getMessage());
//            }
//        }

        // height를 너비에 맞춰서 뿌려주게 끔 변경.
        ImageUtil.loadImageResize(mContext, item.imageUrl, mainImg, R.drawable.noimage_375_188);

        txtMain.setText(item.title1);
        txtSub.setText(item.title2);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(mContext, item.linkUrl);

                ((HomeActivity)mContext).setWiseLog(item.wiseLog);
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
}
