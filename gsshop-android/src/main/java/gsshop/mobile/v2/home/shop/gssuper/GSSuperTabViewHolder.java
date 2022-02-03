/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.ClickUtils;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment.tabPrdMap;


/**
 * gssuper 틀고정 탭메뉴.
 * MAP_CX_GBB 뷰타입 상단영역과 공통으로 사용됨.
 */
public class GSSuperTabViewHolder extends BaseViewHolderV2 {

    /**
     * 최상위뷰
     */
    private final View lay_root;

    /**
     * 리사이클뷰
     */
    private final RecyclerView recycler;

    /**
     * 어뎁터
     */
    private Adapter adapter;

    /**
     * 레이아웃 매니저
     */
    private StaggeredGridLayoutManager mLayoutManager;

    /**
     * 본 뷰홀더가 해더인지 여부
     */
    private boolean isHeader = false;

    /**
     * 선택된 아이템 인덱스 (해더인 경우 사용)
     */
    private int selectedItemIndex = 0;

    /**
     * 내 뷰홀더의 아이템 인덱스 (뷰홀더 상단인 경우 사용)
     */
    private int myItemIndex = 0;

    /**
     * 섹션코드에 대응하는 위치 저장
     */
    private Map<String, Integer> sectionPosMap = new HashMap<String, Integer>();

    /**
     * 생성자
     *
     * @param itemView View
     */
    public GSSuperTabViewHolder(View itemView) {
        super(itemView);

        //이벤트버스 등록
        EventBus.getDefault().register(this);

        lay_root = itemView.findViewById(R.id.lay_root);

        recycler = itemView.findViewById(R.id.recycler_gssuper_tab);

        mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recycler.setLayoutManager(mLayoutManager);
    }

    /**
     * 섹션코드 활성화영역을 변경한다. (해더로 사용된 경우만 해당됨)
     *
     * @param event GSSuperSectionEvent
     */
    public void onEvent(Events.FlexibleEvent.GSSuperSectionSyncEvent event) {
        //해더가 아니면 카테고리 변경 하지 않음
        if (!isHeader) {
            return;
        }

        //스크롤 이벤트에서 전달받은 섹션코드
        String evCode = event.sectionCode;
        for (int i = 0; i < recycler.getChildCount(); i++) {
            View view = recycler.getChildAt(i);
            ItemViewHolder vh = (ItemViewHolder) recycler.getChildViewHolder(view);
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

    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {
        final SectionContentList content = info.contents.get(position).sectionContent;

        ViewUtils.showViews(lay_root);
        List<SectionContentList> childList = content.subContentChild;
        if (isEmpty(childList)) {
            ViewUtils.hideViews(lay_root);
            return;
        }

        //어뎁터 세팅
        adapter = new Adapter(context, childList);
        recycler.setAdapter(adapter);

        for (int i = 0; i < childList.size(); i++) {
            String scode = childList.get(i).dealNo;
            if (isEmpty(scode)) {
                continue;
            }
            //섹션코드에 대응하는 위치 저장
            sectionPosMap.put(scode, i);
            if (scode.equals(content.dealNo)) {
                //뷰홀더 상단에 활성화할 카테고리 위치 저장
                myItemIndex = i;
                //해더를 처음 노출시 활성화할 카테고리 위치 저장
                selectedItemIndex = i;
            }
        }
    }

    /**
     * 카테고리 어뎁터
     */
    private class Adapter extends RecyclerView.Adapter<ItemViewHolder> {

        public final Context context;
        public final List<SectionContentList> list;

        private Adapter(Context context, List<SectionContentList> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_row_type_fx_gs_super_tab_item, parent, false);
            return new ItemViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.txt_title.setText(list.get(position).productName);
            holder.itemView.setTag(new String(list.get(position).dealNo));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtils.work(500)) {
                        return;
                    }
                    //매장 스크롤 정지
                    EventBus.getDefault().post(new Events.FlexibleEvent.GSSuperStopScrollEvent());

                    String sectionCode = list.get(position).dealNo;
                    if (isNotEmpty(tabPrdMap.get(sectionCode))) {
                        EventBus.getDefault().post(new Events.FlexibleEvent.GSSuperSectionClickEvent(sectionCode));

                        selectedItemIndex = position;
                        if (isHeader) {
                            //해더인 경우만 변경
                            setItemStyle(holder);
                        }
                        //와이즈로그 호출
                        ((HomeActivity) context).setWiseLog(list.get(position).wiseLog);
                    }
                }
            });

            int idx;
            if (isHeader) {
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
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView txt_title;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
        }
    }

    /**
     * 탭메뉴 enable/default 상태에 따른 UI를 설정한다.
     *
     * @param holder ItemViewHolder
     */
    private void setItemStyle(ItemViewHolder holder) {
        //모든 아이템 테두리를 디폴트색으로 세팅
        for (int i = 0; i < recycler.getChildCount(); i++) {
            View view = recycler.getChildAt(i);
            ItemViewHolder vh = (ItemViewHolder) recycler.getChildViewHolder(view);
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
        this.isHeader = isHeader;
    }

    /**
     * 해더여부를 반환한다.
     *
     * @return isHeader
     */
    public boolean getIsHeader() {
        return isHeader;
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
}
