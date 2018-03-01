package com.nbn.cloudbasedpatientreferralsystem.chats.messages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.PatientProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatMessage;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;
import com.nbn.cloudbasedpatientreferralsystem.utils.PrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * Created by dudupoo on 30/1/18.
 */

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ChatListVH>
{
    String TAG = getClass().getSimpleName();
    ArrayList<ChatMessage> msgs;
    Context context;

    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference rootDatabaseReference;


    public ChatMessagesAdapter(Context context, ArrayList<ChatMessage> msgs)
    {
        this.context = context;
        this.msgs = msgs;
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabaseReference = firebaseDatabase.getReference();
    }

    @Override
    public ChatListVH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.card_chat_message, parent, false);
        return new ChatListVH(v);
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
        TextView tvMessage;
        TextView tvTime;
        TextView tvTalker;
        LinearLayout linearLayout;
        PrefManager prefManager;

        public ChatListVH(View itemView)
        {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_chat_lastmsg);
            tvTime = (TextView) itemView.findViewById(R.id.tv_chat_time);
            tvTalker = (TextView) itemView.findViewById(R.id.tv_talker);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
            prefManager = new PrefManager(context);
        }

        void setLayout(ChatMessage msg)
        {

            /*if(user.getUid().equals(msg.getSentByUID())) {
                tvMessage.setBackground(context.getDrawable(R.drawable.rounded_rectangle_accent));
                linearLayout.setGravity(Gravity.END);
            } else {
                linearLayout.setGravity(Gravity.START);
            }
*/
//            Log.d(TAG, "setLayout: "+msg.toString());
            tvMessage.setText(msg.getMessage());
            long yourmilliseconds = Long.parseLong(msg.getMessageTime());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(yourmilliseconds);
            String time = sdf.format(resultdate);
            tvTime.setText(time);
            String sentId = msg.getSentByUID();

            if (sentId.equals(user.getUid()))
            {
                tvTalker.setText("You");
            } else
            {
                Log.d(TAG, "setLayout: Profile Type : "+prefManager.getProfile());
                if (prefManager.getProfile().equals(Constants.VALUE_LOGIN_INTENT_DOCTOR))
                {
                    rootDatabaseReference
                            .child(Constants.ROOT_PATIENTS)
                            .child(sentId)
                            .child(Constants.PATIENT_INFO)
                            .getRef().addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            Log.d(TAG, "onDataChange: " + dataSnapshot.getValue(PatientProfile.class));
                            PatientProfile dp = dataSnapshot.getValue(PatientProfile.class);
                            tvTalker.setText(dp.getName());
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
                            .child(sentId)
                            .child(Constants.DOCTOR_INFO)
                            .getRef().addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            Log.d(TAG, "onDataChange: " + dataSnapshot.getValue(DoctorProfile.class));
                            DoctorProfile dp = dataSnapshot.getValue(DoctorProfile.class);
                            tvTalker.setText("Dr. "+dp.getName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
                }
            }

        }
    }
}
