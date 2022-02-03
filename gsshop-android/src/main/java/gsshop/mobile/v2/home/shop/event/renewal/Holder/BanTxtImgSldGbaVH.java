package gsshop.mobile.v2.home.shop.event.renewal.Holder;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.event.renewal.Adapter.AdapterTxtImsSldGba;
import gsshop.mobile.v2.home.shop.ltype.BaseRollViewHolderLtype;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;

public class BanTxtImgSldGbaVH extends BaseRollViewHolderLtype {

    private final TextView mCuntText;
    /**
     * @param itemView
     */
    public BanTxtImgSldGbaVH(View itemView) {
        super(itemView);
        viewPager = itemView.findViewById(R.id.view_pager);
        mCuntText = itemView.findViewById(R.id.text_count);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);

        ModuleList content = moduleList.get(position);
        List<ModuleList> list = content.moduleList;

        if (list == null || !(list.size() > 0)) {
            mCuntText.setVisibility(View.GONE);
            return;
        }

        if(list.size() == 1){
            viewPager.setPagingEnabled(false);
            mCuntText.setVisibility(View.GONE);

        }else{
            viewPager.setPagingEnabled(true);
            mCuntText.setVisibility(View.VISIBLE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int item = viewPager.getCurrentItem();
                int count = ((InfinitePagerAdapter) viewPager.getAdapter()).getRealCount();

                String text = Integer.toString(item + 1) + " / " + count;
                mCuntText.setText(text);

                // TAG 따라 start 할지 내부에서 결정 할 것이다.
                startTimer();
            }
        });

        String textCnt = "1 / " + list.size();
        mCuntText.setText(textCnt);

        final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
                new AdapterTxtImsSldGba(context, list));

        viewPager.setAdapter(wrappedAdapter);
    }

}
