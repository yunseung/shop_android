package gsshop.mobile.v2.menu.navigation;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 롤링배너 어댑터 pager adapter
 */
public class PartnersPagerAdapter extends PagerAdapter {

    private final Context context;
    private List<CateContentList> list;

    protected static final int PARTNER_MENU_PAGE_ADAPTER_SIZE = 10;

    public PartnersPagerAdapter(Context context, List<CateContentList> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list.isEmpty()) {
            return 0;
        }
        double dPageSize = list.size() / (double) PARTNER_MENU_PAGE_ADAPTER_SIZE;
//        Ln.d("dPageSize : " + list.size() + " / " + PARTNER_MENU_PAGE_ADAPTER_SIZE + " = " + dPageSize);
        return (int)Math.ceil(dPageSize);
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

//    int mColorTemp[] = {Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.GRAY, Color.YELLOW, Color.LTGRAY};

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.navigation_row_type_partners_layout, null);

        LinearLayout layout_grid1 = view.findViewById(R.id.layout_grid_row_1);
        LinearLayout layout_grid2 = view.findViewById(R.id.layout_grid_row_2);

        for ( int i = position * PARTNER_MENU_PAGE_ADAPTER_SIZE;
              i < (position + 1) * PARTNER_MENU_PAGE_ADAPTER_SIZE; i++ ){
            View itemView = ((Activity) context).getLayoutInflater()
                    .inflate(R.layout.navigation_row_type_partners_item, null);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            itemView.setLayoutParams(params);

            ImageView imageTemp = (ImageView) itemView.findViewById(R.id.imageview_partner);
            CardView cardView = (CardView) itemView.findViewById(R.id.cardview_partners);
            View viewClick = (View) itemView.findViewById(R.id.view_click);

            if (list.size() > i) {

                try {
                    Glide.with(context).load(trim(list.get(i).imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageTemp);
                } catch (Exception er) {
                    Ln.e(er.getMessage());
                }

                final int numClick = i;

                String strConDescription = !TextUtils.isEmpty(list.get(numClick).title) ? list.get(numClick).title : "";

                if (TextUtils.isEmpty(strConDescription)) {
                    strConDescription = !TextUtils.isEmpty(list.get(numClick).description) ? list.get(numClick).description : "";
                }

                if (!TextUtils.isEmpty(strConDescription)) {
                    viewClick.setContentDescription(strConDescription);
                }

                viewClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goWeb(list.get(numClick).linkUrl);
                    }
                });

            }
            else {
                cardView.setVisibility(View.INVISIBLE);
            }

            if ( i % 2 == 0 ) {
                layout_grid1.addView(itemView);
            }
            else {
                layout_grid2.addView(itemView);
            }
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        // must be overridden else throws exception as not overridden.
        collection.removeView((View) view);
    }

    public void setItem(List<CateContentList> itemList) {
        this.list = itemList;
    }

    private void goWeb(String Url) {
        WebUtils.goWeb(context, Url);
        EventBus.getDefault().post(new Events.NavigationCloseEvent());
    }
}