package com.nbn.cloudbasedpatientreferralsystem.chats.messages;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.ViewProfileActivity;
import com.nbn.cloudbasedpatientreferralsystem.base.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatMessage;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatUID;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.*;

public class ChatWindowActivity extends BaseActivity
{
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.et_chat)
    EditText etChat;
    @BindView(R.id.btn_send)
    ImageButton btnSend;
    LinearLayoutManager linearLayoutManager;
    final ArrayList<ChatMessage> chatMessages = new ArrayList<>();
    final ChatMessagesAdapter adapter = new ChatMessagesAdapter(this, chatMessages);

    String firstUID;
    String secondUID;
    String usersChatRefKey;
    String TAG = getClass().getSimpleName();
    NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        init();
    }

    private void init()
    {
        secondUID = getIntent().getBundleExtra(KEY_BUNDLE).getString(KEY_PROFILE);
        firstUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersChatRefKey = getIntent().getBundleExtra(KEY_BUNDLE).getString(KEY_USERS_CHAT_REF);
        final Context context = this;
        if (firstUID.equals(secondUID))
        {
            finish();
        } else if (usersChatRefKey != null && usersChatRefKey.length() > 0)
        {
            Log.d(TAG, "init: usersChatRefKey : " + usersChatRefKey);
            rootDatabaseReference
                    .child(CHAT_MESSAGES)
                    .child(usersChatRefKey)
                    .addChildEventListener(new ChildEventListener()
                    {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s)
                        {
                            String messageID = dataSnapshot.getKey();

                            Log.d(TAG, "onChildAdded: " + dataSnapshot.getValue(ChatMessage.class));
                            Log.d(TAG, "onChildAdded: " + s);
                            ChatMessage cm = dataSnapshot.getValue(ChatMessage.class);
                            if(!cm.getSentByUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                if(!cm.isRead()) {
                                    cm.setRead(true);
                                    rootDatabaseReference
                                            .child(CHAT_MESSAGES)
                                            .child(usersChatRefKey)
                                            .child(messageID)
                                            .setValue(cm);
                                }
                            }
                            chatMessages.add(cm);
                            adapter.notifyItemInserted(chatMessages.size() - 1);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s)
                        {
                            String messageID = dataSnapshot.getKey();
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot)
                        {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s)
                        {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            Log.d(TAG, "onCancelled: " + databaseError.getDetails());
                        }
                    });
        }
    }

    @OnClick(R.id.btn_send)
    public void OnClick(View view)
    {
        if (TextUtils.isEmpty(etChat.getText().toString()))
        {
            Toast.makeText(this, "Please type something", Toast.LENGTH_SHORT).show();
        } else
        {
            if (usersChatRefKey != null && usersChatRefKey.length() > 0)
            {
                ChatMessage msg = new ChatMessage();
                msg.setMessage(etChat.getText().toString());
                msg.setSentByUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                msg.setMessageTime(System.currentTimeMillis() + "");
                msg.setRead(false);
                Log.d(TAG, "onClick: " + msg.toString());
                rootDatabaseReference.child(CHAT_MESSAGES).child(usersChatRefKey).push().setValue(msg);
                ChatUID chatUID = new ChatUID();
                chatUID.setMembers(Arrays.asList(firstUID, secondUID));
                chatUID.setLastMessageSent(msg);
                rootDatabaseReference.child(CHATS).child(usersChatRefKey).setValue(chatUID);
                etChat.setText("");
                linearLayoutManager.scrollToPosition(chatMessages.size());

            } else
            {
                /*
                * Ohhh my gooowdd what to do now? Give up! Hands up! Heads down! Legs walking!
                * */
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_info)
        {
            if (prefManager.getProfile().equals(VALUE_LOGIN_INTENT_DOCTOR))
            {
                Intent i = new Intent(this, ViewProfileActivity.class);
                Bundle b = new Bundle();
                if (secondUID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    b.putString(KEY_VIEW_ID, firstUID);
                } else
                {
                    b.putString(KEY_VIEW_ID, secondUID);
                }
                b.putString(Constants.KEY_LOGIN_INTENT, Constants.VALUE_LOGIN_INTENT_PATIENT);
                i.putExtra(Constants.KEY_BUNDLE, b);
                startActivity(i);
            } else
            {
                Intent i = new Intent(this, ViewProfileActivity.class);
                Bundle b = new Bundle();
                if (secondUID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    b.putString(KEY_VIEW_ID, firstUID);
                } else
                {
                    b.putString(KEY_VIEW_ID, secondUID);
                }
                b.putString(Constants.KEY_LOGIN_INTENT, Constants.VALUE_LOGIN_INTENT_DOCTOR);
                i.putExtra(Constants.KEY_BUNDLE, b);
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
