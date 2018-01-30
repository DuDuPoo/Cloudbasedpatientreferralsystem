package com.nbn.cloudbasedpatientreferralsystem.chats;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatMessage;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dudupoo on 30/1/18.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListVH>
{
    String TAG = getClass().getSimpleName();
    ArrayList<ChatMessage> msgs;
    Context context;

    public ChatListAdapter(Context context, ArrayList<ChatMessage> msgs) {
        this.context = context;
        this.msgs = msgs;
    }

    @Override
    public ChatListVH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.card_chats_list, parent, false);
        return new ChatListVH((CardView) v);
    }

    @Override
    public void onBindViewHolder(ChatListVH holder, int position)
    {
        holder.setLayout(msgs.get(position));
    }

    @Override
    public int getItemCount()
    {
        return msgs.size();
    }

    class ChatListVH extends RecyclerView.ViewHolder
    {
        TextView tvName;
        TextView tvMessage;
        TextView tvTime;
        public ChatListVH(CardView itemView)
        {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_chat_name);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_chat_lastmsg);
            tvTime = (TextView) itemView.findViewById(R.id.tv_chat_time);
        }

        void setLayout(ChatMessage msg) {
            tvName.setText(msg.getMessage());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.getLong(msg.getMessageTime()));
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            Log.d(TAG, "setLayout: "+year+"/"+month+"/"+day+"/"+hour);
        }
    }
}
