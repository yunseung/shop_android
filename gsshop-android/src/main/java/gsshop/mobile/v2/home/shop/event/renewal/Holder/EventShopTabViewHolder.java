package gsshop.mobile.v2.home.shop.event.renewal.Holder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.ClickUtils;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * GS fresh에서 사용하는 탭뷰홀더와 비슷한 모양. 해당 뷰홀더 재사용 시도 하려 했으나. 그냥 문제가 있을것 같아. 상속 받지 않기로 함.
 */
public class EventShopTabViewHolder extends BaseViewHolderV2 {

    /**
     * 최상위뷰
     */
    private final View lay_root;

    /**
     * 리사이클뷰
     */
    private final RecyclerView mRecycler;

    /**
     * 뷰홀더가 헤더 여부
     */
    private boolean mIsHeader = false;

    /**
     * 어댑터
     */
    private Adapter mAdapter;

    /**
     * 선택된 아이템 인덱스 (해더인 경우 사용)
     */
    private int selectedItemIndex = 0;

    /**
     * 내 뷰홀더의 아이템 인덱스 (뷰홀더 상단인 경우 사용)
     */
    private int myItemIndex = 0;

    /**
     * 레이아웃 매니저
     */
    private StaggeredGridLayoutManager mLayoutManager;

    /**
     * 섹션코드에 대응하는 위치 저장
     */
    private Map<String, Integer> sectionPosMap = new HashMap<String, Integer>();

    /**
     * 섹션코드
     */
    private String sectionCode;

    /**
     * 현재 매장의 네비게이션 ID
     */
    private String mNavigationId;

    /**
     * 생성자
     *
     * @param itemView View
     */
    public EventShopTabViewHolder(View itemView, String navigationId) {
        super(itemView);

        //이벤트버스 등록
        EventBus.getDefault().register(this);

        lay_root = itemView.findViewById(R.id.lay_root);
        mRecycler = itemView.findViewById(R.id.recycler_event_tab);

        mNavigationId = navigationId;

        mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecycler.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        onBindViewHolder(context, moduleList.get(position));
    }

    @Override
    public View onBindViewHolder(final Context context, ModuleList content) {
        super.onBindViewHolder(context, content);

//        final ModuleList content = contents.get(position);

        if (content.moduleList == null) {
            return null;
        }

        // 어댑터 설정
        mAdapter = new Adapter(context, content.moduleList);
        mRecycler.setAdapter(mAdapter);

        sectionCode = content.tabSeq;

        for (int i = 0; i < content.moduleList.size(); i++) {
            String sCode = content.moduleList.get(i).tabSeq;
            if(isEmpty(sCode)) {
                continue;
            }

            sectionPosMap.put(sCode, i);
            if(sCode.equals(content.tabSeq)) {
                //뷰홀더 상단에 활성화할 카테고리 위치 저장
                myItemIndex = i;
                //해더를 처음 노출시 활성화할 카테고리 위치 저장
                selectedItemIndex = i;
            }
        }
        return null;
    }

    /**
     * 섹션코드 활성화영역을 변경한다. (해더로 사용된 경우만 해당됨)
     *
     * @param event GSSuperSectionEvent
     */
    public void onEvent(Events.EventOnEvent.EventSectionSyncEvent event) {
        Events.EventOnEvent.EventSectionSyncEvent syncPositionEvent =
                EventBus.getDefault().getStickyEvent(Events.EventOnEvent.EventSectionSyncEvent.class);
        if (syncPositionEvent != null) {
            EventBus.getDefault().removeStickyEvent(syncPositionEvent);
        }

        //해더가 아니면 카테고리 변경 하지 않음
        if (!mIsHeader) {
            return;
        }

        // 여러개 존재할 때에 navigation ID로 비교.
        if (mNavigationId != null && !mNavigationId.equals(event.navigationId)) {
            return;
        }

        //스크롤 이벤트에서 전달받은 섹션코드
        String evCode = event.sectionCode;
        evCode = evCode == null ? "" : evCode;

        for (int i = 0; i < mRecycler.getChildCount(); i++) {
            View view = mRecycler.getChildAt(i);
            EventShopTabViewHolder.ItemViewHolder vh = (EventShopTabViewHolder.ItemViewHolder) mRecycler.getChildViewHolder(view);
            Object vhTag = vh.itemView.getTag();
            if (isEmpty(vhTag)) {
                continue;
            }
            String vhCode = String.valueOf(vhTag);
            if (evCode.equals(vhCode)) {
                selectedItemIndex = sectionPosMap.get(evCode);
                setItemStyle(vh);
            }
        }
    }

    /**
     * 카테고리 어뎁터
     */
    private class Adapter extends RecyclerView.Adapter<ItemViewHolder> {

        private final Context context;
        private final List<ModuleList> list;

        private Adapter(Context context, List<ModuleList> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View viewItem = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.home_row_type_l_sticky_type_event_tab_item, viewGroup, false);
            return new ItemViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            holder.txt_title.setText(list.get(position).name);
            holder.itemView.setTag(list.get(position).tabSeq);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 현재 위치면 아무 동작 안함.
                    if (selectedItemIndex == position) {
                        return;
                    }

                    if (ClickUtils.work(500)) {
                        return;
                    }
                    //매장 스크롤 정지
                    EventBus.getDefault().post(new Events.FlexibleEvent.GSChoiceStopScrollEvent());

                    String sectionCode = list.get(position).tabSeq;

                    // 클릭 이벤트 전달. (해당 부분으로 이동 위함)
                    EventBus.getDefault().post(new Events.EventOnEvent.EventSectionClickEvent(mNavigationId, sectionCode));

                    selectedItemIndex = position;
                    if (mIsHeader) {
                        //해더인 경우만 변경
                        setItemStyle(holder);
                    }
                    //와이즈로그 호출
                    ((HomeActivity) context).setWiseLog(list.get(position).wiseLog);

                }
            });

            int idx;
            if (mIsHeader) {
                idx = selectedItemIndex;
            } else {
                idx = myItemIndex;
            }
            if (idx == position) {
                setEnabledStyle(holder);
            } else {
                setDefaultStyle(holder);
            }
        }

        @Override
        public int getItemCount() {
            return isNotEmpty(list) ? list.size() : 0;
        }
    }

    /**
     * 카테고리 뷰홀더
     */
    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView txt_title;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
        }
    }

    /**************************************************************************************************
     *      GS Fresh 에서 복사 붙여 넣기 함. 기존에 GS Fresh는 Flexible 이라 상속에 문제가 있음.
     ***************************************************************************************************/

    /**
     * 탭메뉴 enable/default 상태에 따른 UI를 설정한다.
     *
     * @param holder ItemViewHolder
     */
    private void setItemStyle(ItemViewHolder holder) {
        //모든 아이템 테두리를 디폴트색으로 세팅
        for (int i = 0; i < mRecycler.getChildCount(); i++) {
            View view = mRecycler.getChildAt(i);
            ItemViewHolder vh = (ItemViewHolder) mRecycler.getChildViewHolder(view);
            setDefaultStyle(vh);
        }
        //선택된 아이템 테두리 세팅, 볼드체
        setEnabledStyle(holder);
    }

    /**
     * 선택상태 UI 세팅
     *
     * @param holder ItemViewHolder
     */
    private void setEnabledStyle(ItemViewHolder holder) {
        holder.txt_title.setSelected(true);
        holder.txt_title.setTypeface(null, Typeface.BOLD);
        holder.txt_title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gs_super_tab_selected));
    }

    /**
     * 디폴트상태 UI 세팅
     *
     * @param holder ItemViewHolder
     */
    private void setDefaultStyle(ItemViewHolder holder) {
        holder.txt_title.setSelected(false);
        holder.txt_title.setTypeface(Typeface.SANS_SERIF);
        holder.txt_title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gs_super_tab_default));
    }

    /**
     * 해더여부를 세팅한다.
     * @param isHeader if true, header
     */
    public void setIsHeader(boolean isHeader) {
        this.mIsHeader = isHeader;
    }

    /**
     * 해더여부를 반환한다.
     *
     * @return isHeader
     */
    public boolean getIsHeader() {
        return mIsHeader;
    }

    /**
     * 이벤트버스 unregister
     *
     * @param event TvLiveUnregisterEvent
     */
    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        selectedItemIndex = 0;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getSectionCode() {
        return sectionCode;
    }
}
