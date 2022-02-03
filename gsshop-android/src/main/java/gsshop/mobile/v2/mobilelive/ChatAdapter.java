package gsshop.mobile.v2.mobilelive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.user.User;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private static final String CHAT_SENDER_ME = "나";
    private static final String CHAT_SENDER_ADMIN = "admin";

    /**
     * 뷰타입 정의 (내가 작성한 메시지)
     */
    public static final int VIEW_HOLDER_TYPE_ME = 0;

    /**
     * 뷰타입 (대댓글)
     */
    public static final int VIEW_HOLDER_TYPE_REPLY = 1;

    /**
     * 뷰타입 (PD 등 관리자)
     */
    public static final int VIEW_HOLDER_TYPE_PD = 2;

    /**
     * 뷰타입 (일반사용자)
     */
    public static final int VIEW_HOLDER_TYPE_USER = 3;

    Context context;
    List<MobileLiveData.ChatData> messages = new ArrayList<MobileLiveData.ChatData>();

    public void reMove()
    {
        messages.clear();
    }

    public ChatAdapter(Context context) {
        this.context = context;
    }

    public void add(MobileLiveData.ChatData message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify

    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;

        switch (viewType) {
            case VIEW_HOLDER_TYPE_ME:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mobilelive_chatlist_me_row, parent, false);
                break;
            case VIEW_HOLDER_TYPE_REPLY:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mobilelive_chatlist_reply_row, parent, false);
                break;
            case VIEW_HOLDER_TYPE_PD:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mobilelive_chatlist_pd_row, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mobilelive_chatlist_user_row, parent, false);
                break;
        }

        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        User user = User.getCachedUser();
        if (getItemViewType(position) == VIEW_HOLDER_TYPE_REPLY) {
            //대댓글
            String custNo = messages.get(position).PMO.UO.U;
            String nick = messages.get(position).PMO.UO.NI;
            String message = messages.get(position).PMO.MO.MG;
            String isShow = messages.get(position).PMO.MO.VI;

            String nick_r = messages.get(position).UO.NI;
            String message_r = messages.get(position).MO.MG;
            String isShow_r = messages.get(position).MO.VI;

            if (user != null && user.customerNumber.equals(custNo)) {
                nick = CHAT_SENDER_ME;
                holder.sender.setText(nick);
                holder.message.setText(message);
                holder.sender_r.setText(nick_r);
                holder.message_r.setText(message_r);
            }
            else {
                if ("N".equals(isShow)) {
                    holder.sender.setVisibility(View.GONE);
                    holder.message.setVisibility(View.GONE);
                }
                else {
                    holder.sender.setVisibility(View.VISIBLE);
                    holder.message.setVisibility(View.VISIBLE);
                    holder.sender.setText(nick);
                    holder.message.setText(message);
                }
                if ("N".equals(isShow_r)) {
                    holder.sender_r.setVisibility(View.GONE);
                    holder.message_r.setVisibility(View.GONE);
                }
                else {
                    holder.sender_r.setVisibility(View.VISIBLE);
                    holder.message_r.setVisibility(View.VISIBLE);
                    holder.sender_r.setText(nick_r);
                    holder.message_r.setText(message_r);
                }
            }
        } else {
            String custNo = messages.get(position).UO.U;
            String nick = messages.get(position).UO.NI;
            String isShow = messages.get(position).MO.VI;
            if (user != null && user.customerNumber.equals(custNo)) {
                nick = CHAT_SENDER_ME;
                holder.sender.setText(nick);
                holder.message.setText(messages.get(position).MO.MG);
            }
            else {
                if ("N".equals(isShow)) {
                    holder.sender.setVisibility(View.GONE);
                    holder.message.setVisibility(View.GONE);
                }
                else {
                    holder.sender.setVisibility(View.VISIBLE);
                    holder.message.setVisibility(View.VISIBLE);
                    holder.sender.setText(nick);
                    holder.message.setText(messages.get(position).MO.MG);
                }
            }
        }
    }

    public void setInfo(List<MobileLiveData.ChatData> info) {
        messages = info;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        //String type = messages.get(position).UO.NI;
        String type = messages.get(position).UO.U;  // admin

        User user = User.getCachedUser();
        if (user != null && user.customerNumber.equals(type)) {
            viewType = VIEW_HOLDER_TYPE_ME;
        } else if (isNotEmpty(messages.get(position).PMO)
                && isNotEmpty(messages.get(position).PMO.UO)
                && isNotEmpty(messages.get(position).PMO.MO)) {
            viewType = VIEW_HOLDER_TYPE_REPLY;
        } else if (CHAT_SENDER_ADMIN.equalsIgnoreCase(type)) {
            viewType = VIEW_HOLDER_TYPE_PD;
        } else {
            viewType = VIEW_HOLDER_TYPE_USER;
        }
        return viewType;
    }

    public Object getItem(int position) {
        return messages.get(position);
    }

}