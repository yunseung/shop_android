package gsshop.mobile.v2.mobilelive;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import gsshop.mobile.v2.R;


public class ChatViewHolder extends RecyclerView.ViewHolder {

    public TextView sender;
    public TextView message;
    public TextView sender_r;
    public TextView message_r;

    public ChatViewHolder(View itemView) {
        super(itemView);

        if(itemView != null) {
            sender = (TextView) itemView.findViewById(R.id.sender);
            message = (TextView) itemView.findViewById(R.id.message);
            sender_r = (TextView) itemView.findViewById(R.id.sender_r);
            message_r = (TextView) itemView.findViewById(R.id.message_r);
        }
    }
}
