package com.nbn.cloudbasedpatientreferralsystem.chats.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.PatientProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatMessage;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatUID;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;
import com.nbn.cloudbasedpatientreferralsystem.utils.PrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * Created by dudupoo on 23/2/18.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListVH>
{
    private Context context;
    private ArrayList<ChatUID> chats;

    public ChatListAdapter(Context context, ArrayList<ChatUID> chats)
    {
        this.context = context;
        this.chats = chats;
    }

    @Override
    public ChatListVH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ChatListVH((CardView) LayoutInflater.from(context)
                .inflate(R.layout.card_chats_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatListVH holder, int position)
    {
        holder.setLayout(chats.get(position));
    }

    @Override
    public int getItemCount()
    {
        return chats.size();
    }

    class ChatListVH extends RecyclerView.ViewHolder
    {
        TextView tvChatName;
        TextView tvLastMsg;
        TextView tvChatTime;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference rootDatabaseReference;

        public ChatListVH(CardView itemView)
        {
            super(itemView);
            tvChatName = (TextView) itemView.findViewById(R.id.tv_chat_name);
            tvLastMsg = (TextView) itemView.findViewById(R.id.tv_chat_lastmsg);
            tvChatTime = (TextView) itemView.findViewById(R.id.tv_chat_time);
        }

        void setLayout(ChatUID chatUID)
        {
            String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String senderUID = "";
            if (chatUID.getMembers().get(0).equals(myUID))
            {
                senderUID = chatUID.getMembers().get(1);
            } else
            {
                senderUID = chatUID.getMembers().get(0);
            }
            ChatMessage msg = chatUID.getLastMessageSent();
            String lastMsg = msg.getMessage();
            long yourmilliseconds = Long.parseLong(msg.getMessageTime());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(yourmilliseconds);
            String time = sdf.format(resultdate);
            tvChatName.setText(senderUID);
            tvChatTime.setText(time);
            tvLastMsg.setText(lastMsg);
            PrefManager prefManager = new PrefManager(context);
            firebaseDatabase = FirebaseDatabase.getInstance();
            rootDatabaseReference = firebaseDatabase.getReference();
            if (prefManager.getProfile().equals(Constants.VALUE_LOGIN_INTENT_DOCTOR))
            {
                rootDatabaseReference
                        .child(Constants.ROOT_PATIENTS)
                        .child(senderUID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        PatientProfile patientProfile = dataSnapshot.getValue(PatientProfile.class);
                        tvChatName.setText(patientProfile.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

            } else
            {
                rootDatabaseReference
                        .child(Constants.ROOT_DOCTORS)
                        .child(senderUID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        DoctorProfile doctorProfile = dataSnapshot.getValue(DoctorProfile.class);
                        tvChatName.setText(doctorProfile.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }

            final String finalSenderUID = senderUID;
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, ChatScreenActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KEY_PROFILE, finalSenderUID);
                    intent.putExtra(Constants.KEY_BUNDLE, bundle);
                    context.startActivity(intent);
                }
            });
        }
    }
}
