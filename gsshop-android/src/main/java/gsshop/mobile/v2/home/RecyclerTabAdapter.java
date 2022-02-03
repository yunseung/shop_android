/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */

package gsshop.mobile.v2.home;

import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.amplitude.api.Amplitude;
import com.blankj.utilcode.util.EmptyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.library.viewpager.InfiniteFragmentAdapter;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.ImageUtil;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static gsshop.mobile.v2.home.HomeActivity.lastSelectedPosition;

/**
 * 상단 탭메뉴 어댑터
 */
public class RecyclerTabAdapter extends RecyclerTabLayout.Adapter<RecyclerTabAdapter.ViewHolder> {

    private static final int IS_NOT_SET = -1;

    /**
     * 문자열 구분자 (제어문자 사용)
     */
    private char separator = (char)0x1E;

    private int mTextSize = IS_NOT_SET;

    private int mTextSizeSub = IS_NOT_SET;

    private int mTextColor = IS_NOT_SET;

    private int mTextColorSub = IS_NOT_SET;

    private int mTextStyle = Typeface.NORMAL;

    /**
     * 매장 뷰페이저 어댑터
     */
    private PagerAdapter mAdapater;

    /**
     * 탭레이아웃
     */
    private RecyclerTabLayout mRecyclerTabLayout;

    /**
     * 문자열 구분자 (제어문자 사용)
     */
    public static Map<Integer, Integer> tabViews = new HashMap<>();

    public RecyclerTabAdapter(ViewPager viewPager) {
        super(viewPager);
        mAdapater = mViewPager.getAdapter();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_tab, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = mAdapater.getPageTitle(position).toString();
        String[] titles = title.split(Character.toString(separator));
        if (titles[0] != null) {
            holder.title.setText(titles[0]);
        }
        try {
            holder.titleSub.setVisibility(View.INVISIBLE);
            holder.titleSub.setText("");
            holder.imgIcon.setVisibility(View.GONE);
            holder.imgMore.setVisibility(View.GONE);

            if (titles.length > 1 && EmptyUtils.isNotEmpty(titles[1].trim())) {
                holder.titleSub.setVisibility(View.VISIBLE);
                holder.titleSub.setText(titles[1]);
            }
            else if (titles.length > 2 && EmptyUtils.isNotEmpty(titles[2].trim())) {
                holder.imgIcon.setVisibility(View.VISIBLE);
                ImageUtil.loadImage(MainApplication.getAppContext(),
                        titles[2], holder.imgIcon, android.R.color.transparent);
            }
            if (holder.itemView.getContext().getString(R.string.common_more).equals(titles[0])) {
                holder.imgMore.setImageResource(R.drawable.ic_tab_more);
                holder.imgMore.setVisibility(View.VISIBLE);
            }
            if (holder.itemView.getContext().getString(R.string.more_close).equals(titles[0])) {
                holder.imgMore.setImageResource(R.drawable.ic_tab_hide);
                holder.imgMore.setVisibility(View.VISIBLE);
            }
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }
        catch (IndexOutOfBoundsException e) {
            Ln.e(e.getMessage());
        }

        //scrollToTab에서 바인딩전 뷰홀더 width를 알수 없기 때문에 여기서 미리 저장해둠
        holder.itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tabViews.put(position, holder.itemView.getMeasuredWidth());
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public void setSubTextSize(int textSize) {
        mTextSizeSub = textSize;
    }

    public void setTextStyle(int textStyle) {
        mTextStyle = textStyle;
    }
    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }
    public void setSubTextColor(int textColor) {
        mTextColorSub = textColor;
    }

    @Override
    public int getItemCount() {
        if (isEmpty(mAdapater)) {
            return 0;
        }
        return ((InfiniteFragmentAdapter)mAdapater).getRealCount();
    }

    /**
     * RecyclerTabLayout 참조용 객체를 세팅한다.
     *
     * @param recyclerTabLayout
     */
    public void setRecyclerTabLayout(RecyclerTabLayout recyclerTabLayout) {
        mRecyclerTabLayout = recyclerTabLayout;
    }

    /**
     * 뷰홀더
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * 매장명
         */
        public TextView title;

        /**
         * 매장 서브 타이틀
         */
        public TextView titleSub;

        /**
         * 우측 상단 sectionImgUrl 이 있을 때 표시 되는 아이콘
         */
        public ImageView imgIcon;

        /**
         * 매장명 좌측 더보기 아이콘
         */
        public ImageView imgMore;

        public ViewHolder(View itemView) {
            super(itemView);

            if (itemView == null) {
                return;
            }

            title = itemView.findViewById(R.id.psts_tab_title);
            titleSub = itemView.findViewById(R.id.psts_tab_title_sub);

            if (title == null) {
                title = new TextView(itemView.getContext());
            }
            if (titleSub == null) {
                titleSub = new TextView(itemView.getContext());
            }

            if (mTextSize != IS_NOT_SET) {
                title.setTextSize( mTextSize);
            }

            if (mTextSizeSub != IS_NOT_SET) {
                titleSub.setTextSize( mTextSizeSub);
            }

            title.setTypeface(null, mTextStyle);

            if (mTextColor != IS_NOT_SET) {
                title.setTextColor(mTextColor);
            }
            if (mTextColorSub != IS_NOT_SET) {
                titleSub.setTextColor(mTextColorSub);
            }

            imgIcon = itemView.findViewById(R.id.psts_tab_icon);
            imgMore = itemView.findViewById(R.id.psts_tab_more);

            if (RecyclerTabLayout.IS_SET_TOUCH_BACK) {
                itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            int position = getAdapterPosition();
                            mRecyclerTabLayout.setTouchedPosition(position);
                        }
                        return false;
                    }
                });
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //사용자가 선택한 포지션
                    int position = getAdapterPosition();

                    if (v.getContext().getString(R.string.common_more).equals(title.getText())) {
                        //Amplitude
                        try {
                            JSONObject eventProperties = new JSONObject();
                            eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.MAINTAB_MORE);
                            Amplitude.getInstance().logEvent(AMPEnum.AMP_CLICK_MAINTAB, eventProperties);
                        } catch (JSONException exception){
                            //ignore
                        }
                        //더보기
                        EventBus.getDefault().post(new Events.MainTabExpandEvent(true));
                        return;
                    } else if (v.getContext().getString(R.string.more_close).equals(title.getText())) {
                        //Amplitude
                        try {
                            JSONObject eventProperties = new JSONObject();
                            eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.MAINTAB_CLOSE);
                            Amplitude.getInstance().logEvent(AMPEnum.AMP_CLICK_MAINTAB, eventProperties);
                        } catch (JSONException exception){
                            //ignore
                        }
                        //접기
                        EventBus.getDefault().post(new Events.MainTabExpandEvent(false));
                        return;
                    }

                    //사용자가 선택한 포지션 세팅
                    mRecyclerTabLayout.setOriIndicatorPosition(position);

                    if (RecyclerTabLayout.IS_SET_TOUCH_BACK) {
                        mRecyclerTabLayout.setTouchUp();
                    }

                    if (lastSelectedPosition != position) {
                        //이동할 포지션
                        int newPosition;
                        //실제 아이템 갯수
                        int realCount = ((InfiniteFragmentAdapter)mViewPager.getAdapter()).getRealCount();
                        //사용자 선택전 포지션
                        int currentItem = mViewPager.getCurrentItem();
                        int gap = currentItem - position;
                        int block = Math.abs(gap) / realCount + 1;

                        if (gap < 0) {
                            //오른쪽 클릭
                            newPosition = position;
                        } else {
                            //왼쪽 클릭시 행걸림 현상이 발생하여 이동할 위치를 조정해줌
                            newPosition = position + block * realCount;
                        }

                        mViewPager.setCurrentItem(newPosition);

                        // 클릭하여 이동시 이벤트 TMS 인앱 메세지 이벤트
//                        InAppMessageManager.getInstance().registInAppMessageEvent(AbstractTMSService.EventInAppMessage.EVENT_MAIN_TAB);

                        new Handler().postDelayed(() -> mViewPager.requestLayout(), 500);
                    }

                }
            });
        }
    }
}
