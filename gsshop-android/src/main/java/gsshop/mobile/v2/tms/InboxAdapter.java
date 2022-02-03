/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.tms;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import gsshop.mobile.v2.R;

import static gsshop.mobile.v2.tms.InboxActivity.VIEWHOLDER_TYPE_EMPTY;
import static gsshop.mobile.v2.tms.InboxActivity.VIEWHOLDER_TYPE_GRADE;
import static gsshop.mobile.v2.tms.InboxActivity.VIEWHOLDER_TYPE_MSG;

/**
 * Created by azota on 2017-01-06.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxBaseViewHolder> {

    /**
     * 컨텍스트
     */
    protected final Context mContext;

    /**
     * 리스트에 표시할 데이타
     */
    protected ArrayList<InboxMsg> mInfo;

    /**
     * 생성자
     * @param context 컨텍스트
     */
    public InboxAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * 리스트 데이타 세팅
     *
     * @param info 리스트 데이타
     */
    public void setInfo(ArrayList<InboxMsg> info) {
        mInfo = info;
    }

    /**
     * 리스트 데이타를 반환한다.
     *
     * @return 리스트 데이타
     */
    public ArrayList<InboxMsg> getInfo() {
        return mInfo;
    }

    @Override
    public int getItemViewType(int position) {
        //onCreateViewHolder 파라미터로 사용되는 viewType은 본 함수의 리턴값이 사용됨
        return mInfo.get(position).type;
    }

    @Override
    public InboxBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        InboxBaseViewHolder viewHolder = null;
        View itemView;

        switch (viewType) {
            case VIEWHOLDER_TYPE_GRADE:
                // 사용자 등급정보
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pms_grade_item, parent, false);
                viewHolder = new InboxGradeViewHolder(itemView);
                break;
            case VIEWHOLDER_TYPE_MSG:
                //PMS, 고객센터 메시지
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pms_msg_item, parent, false);
                viewHolder = new InboxMsgViewHolder(itemView);
                break;
            case VIEWHOLDER_TYPE_EMPTY:
                //PMS, 고객센터 메시지가 없는 경우 알림영역
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pms_empty_item, parent, false);
                viewHolder = new InboxEmptyViewHolder(itemView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InboxBaseViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEWHOLDER_TYPE_GRADE:
                ((InboxGradeViewHolder)holder).onBindViewHolder(mContext, position, mInfo);
                break;
            case VIEWHOLDER_TYPE_MSG:
                ((InboxMsgViewHolder)holder).onBindViewHolder(mContext, position, mInfo);
                break;
            case VIEWHOLDER_TYPE_EMPTY:
                ((InboxEmptyViewHolder)holder).onBindViewHolder(mContext, position, mInfo);
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mInfo != null) {
            count = mInfo.size();
        }
        return count;
    }
}
