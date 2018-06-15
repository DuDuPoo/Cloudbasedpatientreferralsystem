package com.nbn.cloudbasedpatientreferralsystem.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.nbn.cloudbasedpatientreferralsystem.chats.messages.ChatScreenActivity;
import com.nbn.cloudbasedpatientreferralsystem.chats.messages.ChatWindowActivity;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatUID;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.*;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by dudupoo on 30/1/18.
 */

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorListVH>
{
    Context context;
    ArrayList<DoctorProfile> doctors;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference rootDatabaseReference;
    String TAG = getClass().getSimpleName();
    HashMap<String, ArrayList<String>> userChatRefs;
    ArrayList<String> myUserChatRefs;

    public DoctorListAdapter(Context context, ArrayList<DoctorProfile> doctors)
    {
        this.context = context;
        this.doctors = doctors;
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabaseReference = firebaseDatabase.getReference();
        userChatRefs = new HashMap<>();
        myUserChatRefs = new ArrayList<>();
        rootDatabaseReference
                .child(USER_CHATS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            // Get Patient Profile here
                            myUserChatRefs.add(postSnapshot.getValue(String.class));
                        }
//                        myUserChatRefs.add(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
        for (final DoctorProfile d : doctors)
        {
            final ArrayList<String> list = new ArrayList<>();
            rootDatabaseReference
                    .child(USER_CHATS)
                    .child(d.getFirebaseId())
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            Log.d(TAG, "Adapter onDataChange: " + dataSnapshot.getChildrenCount());
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                String userChatRef = postSnapshot.getValue(String.class);
                                list.add(userChatRef);
                            }
                            userChatRefs.put(d.getFirebaseId(), list);
                            Log.d(TAG, "Adapter onDataChange: " + userChatRefs.toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
        }
    }

    @Override
    public DoctorListVH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.card_doctor_list, parent, false);
        return new DoctorListVH((CardView) v);
    }

    @Override
    public void onBindViewHolder(DoctorListVH holder, int position)
    {
        holder.setLayout(doctors.get(position));
    }

    @Override
    public int getItemCount()
    {
        return doctors.size();
    }

    class DoctorListVH extends RecyclerView.ViewHolder
    {

        TextView tvName;
        TextView tvEmail;
        CardView cardView;

        public DoctorListVH(CardView itemView)
        {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_doctor_name);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_doctor_email);
            cardView = itemView;
        }

        void setLayout(final DoctorProfile d)
        {
            tvName.setText(d.getName());
            tvEmail.setText(d.getEmail());
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ArrayList<String> docChats = new ArrayList<String>();
                    docChats = userChatRefs.get(d.getFirebaseId());
                    boolean isFound = false;
                    int location = 0;
                    if (docChats.size() > 0 && myUserChatRefs.size() > 0)
                    {
                        for (int i = 0; i < docChats.size(); i++)
                        {
                            for (int j = 0; j < myUserChatRefs.size(); j++)
                            {
                                if(docChats.get(i).equals(myUserChatRefs.get(j))) {
                                    isFound = true;
                                    location = j;
                                    break;
                                }
                            }
                            if(isFound)
                                break;
                        }
                        goAhead(d,myUserChatRefs.get(location));
                    } else
                    {
                        createNewChatThread(d);
                    }
                }
            });
        }

        void createNewChatThread(DoctorProfile d)
        {
            ChatUID chatUID = new ChatUID();
            chatUID.setMembers(
                    new ArrayList<String>(
                            Arrays.asList(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    d.getFirebaseId())));
            String chatUIDKey = rootDatabaseReference
                    .child(CHATS)
                    .push()
                    .getKey();
            chatUID.setKey(chatUIDKey);
            rootDatabaseReference
                    .child(USER_CHATS)
                    .child(d.getFirebaseId())
                    .push()
                    .setValue(chatUIDKey);
            rootDatabaseReference
                    .child(USER_CHATS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .push()
                    .setValue(chatUIDKey);
            rootDatabaseReference.child(CHATS).child(chatUIDKey).setValue(chatUID);
            goAhead(d, chatUIDKey);
        }

        void goAhead(DoctorProfile d, String usersChatRef)
        {
            Intent intent = new Intent(context, ChatWindowActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(KEY_USERS_CHAT_REF, usersChatRef);
            bundle.putString(KEY_PROFILE, d.getFirebaseId());
            intent.putExtra(KEY_BUNDLE, bundle);
            context.startActivity(intent);
        }
    }
}
