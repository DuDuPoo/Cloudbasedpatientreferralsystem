package com.nbn.cloudbasedpatientreferralsystem.chats.messages;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.base.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatMessage;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatUID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.*;

public class ChatScreenActivity extends BaseActivity
{
    /*
     chatUIDKey is null while sending
    * */

    private FirebaseUser user;
    String TAG = getClass().getSimpleName();
    private EditText etChat;
    private ImageButton btnSend;
    private RecyclerView recyclerView;
    private DatabaseReference chatsRef;
    private DatabaseReference chatMessagesRef;
    private DatabaseReference userChatsRef;

    private String firstUID;
    private String secondUID;
    private static String chatUIDKey;
    private static boolean isActiveChat;
    final ArrayList<ChatMessage> chatMessages = new ArrayList<>();
    final ChatMessagesAdapter adapter = new ChatMessagesAdapter(this, chatMessages);
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initLayout();

        firstUID = getIntent().getBundleExtra(KEY_BUNDLE).getString(KEY_PROFILE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        secondUID = user.getUid();
        Log.d(TAG, "onCreate: First UID :: " + firstUID + " :: secondUID :: " + user.getUid());
        if (user.getUid().equals(firstUID))
        {
            finish();
        } else
        {
            createNewChatThread();
        }
    }

    private void postNewChat(ChatUID chatUID)
    {
        chatUIDKey = rootDatabaseReference
                .child(CHATS)
                .push()
                .getKey();
        rootDatabaseReference.child(CHATS).child(chatUIDKey).setValue(chatUID);
        userChatsRef.setValue(chatUIDKey);
    }


    // @STEP 1
    private void createNewChatThread()
    {
        final ChatUID chatUID = new ChatUID();
        chatUID.setMembers(new ArrayList<String>(Arrays.asList(firstUID, secondUID)));

        userChatsRef = rootDatabaseReference.child(USER_CHATS).child(user.getUid()).getRef();
        userChatsRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.getChildren().iterator().hasNext())
                {
                    postNewChat(chatUID);
                } else
                {
                    final ArrayList<String> listOfChats = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        String tempChatUID = postSnapshot.getValue(String.class);
                        Log.d(TAG, "onDataChange: Key a :: " + tempChatUID);
                        listOfChats.add(tempChatUID);
                    }
                    if (listOfChats.size() > 0)
                    {
                        for (int i = 0; i < listOfChats.size(); i++)
                        {
                            final String cUID = listOfChats.get(i);
// @STEP 2
                            chatsRef = rootDatabaseReference.child(CHATS).child(cUID).getRef();
                            chatsRef.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    ChatUID tempChatUID = dataSnapshot.getValue(ChatUID.class);
                                    List<String> members = tempChatUID.getMembers();
                                    if ((members.get(0).equals(firstUID) &&
                                            members.get(1).equals(secondUID)) ||
                                            ((members.get(0).equals(secondUID) &&
                                                    members.get(1).equals(firstUID))))
                                    {
                                    /*
                                    * Found
                                    * */
//                                        chatUIDKey = cUID;
                                        setChatUIDKey(cUID);
                                        chatUID.setLastMessageSent(tempChatUID.getLastMessageSent());
                                        rootDatabaseReference.child(CHATS).child(chatUIDKey).setValue(chatUID);
                                        Log.d(TAG, "onDataChange: FINALLYY//////////");
                                        isActiveChat = true;
                                        updatUI();
                                    } else
                                    {
                                        postNewChat(chatUID);
                                    }
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

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        Log.d(TAG, "createNewChatThread: Key :: ");
    }

    private void initLayout()
    {
        etChat = (EditText) findViewById(R.id.et_chat);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(etChat.getText().toString()))
                {
                    Toast.makeText(ChatScreenActivity.this, "Please type something", Toast.LENGTH_SHORT).show();
                } else
                {
                    /*if(chatUIDKey!=null)
                        rootDatabaseReference.child(USER_CHATS).child(firstUID).push().setValue(chatUIDKey);
*/
                    rootDatabaseReference.child(USER_CHATS).child(firstUID).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (!dataSnapshot.getChildren().iterator().hasNext())
                            {
                                rootDatabaseReference.child(USER_CHATS).child(firstUID).push().setValue(chatUIDKey);
                            } else
                            {
                                ArrayList<String> allChatUIDs = new ArrayList<String>();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                {
                                    String tempChatUID = postSnapshot.getValue(String.class);
                                    allChatUIDs.add(tempChatUID);
                                }
                                if (allChatUIDs.size() > 0)
                                {

                                    for (int i = 0; i < allChatUIDs.size(); i++)
                                    {
                                        final String cUID = allChatUIDs.get(i);
// @STEP 2
                                        DatabaseReference chatsReference;
                                        chatsReference = rootDatabaseReference.child(CHATS).child(cUID).getRef();
                                        chatsReference.addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                ChatUID tempChatUID = dataSnapshot.getValue(ChatUID.class);
                                                List<String> members = tempChatUID.getMembers();
                                                if ((members.get(0).equals(firstUID) &&
                                                        members.get(1).equals(secondUID)) ||
                                                        ((members.get(0).equals(secondUID) &&
                                                                members.get(1).equals(firstUID))))
                                                {
                                    /*
                                    * Found
                                    * */

//                                                    chatUIDKey = cUID;
                                                    setChatUIDKey(cUID);
                                                    Log.d(TAG, "onDataChange: FINALLYY//////////");
                                                } else
                                                {
                                                    rootDatabaseReference.child(USER_CHATS).child(firstUID).setValue(chatUIDKey);
                                                }
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

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });

                    //@TODO Need a flag to check!

                    ChatMessage msg = new ChatMessage();
                    msg.setMessage(etChat.getText().toString());
                    msg.setSentByUID(secondUID);
                    msg.setMessageTime(System.currentTimeMillis() + "");
                    Log.d(TAG, "onClick: "+chatUIDKey+"/"+secondUID);
                    Log.d(TAG, "onClick: "+msg.toString());
                    rootDatabaseReference.child(CHAT_MESSAGES).child(chatUIDKey).push().setValue(msg);
                    ChatUID chatUID = new ChatUID();
                    chatUID.setMembers(Arrays.asList(firstUID, secondUID));
                    chatUID.setLastMessageSent(msg);
                    rootDatabaseReference.child(CHATS).child(chatUIDKey).setValue(chatUID);
                    etChat.setText("");
                    linearLayoutManager.scrollToPosition(chatMessages.size());
                    /*if(getChatUIDKey()!=null)
                    {
                        adapter.notifyItemInserted(chatMessages.size() - 1);
                    }
*/
                }
            }
        });
        Log.d(TAG, "initLayout: ChatUIDKey :: " +chatUIDKey);
    }

    private void updatUI()
    {
        rootDatabaseReference
                .child(CHAT_MESSAGES)
                .child(chatUIDKey)
                .addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Log.d(TAG, "onChildAdded: " + dataSnapshot.getValue(ChatMessage.class));
                        Log.d(TAG, "onChildAdded: "+s);
                        ChatMessage cm = dataSnapshot.getValue(ChatMessage.class);
                        chatMessages.add(cm);
                        adapter.notifyItemInserted(chatMessages.size() - 1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {

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
                        Log.d(TAG, "onCancelled: "+databaseError.getDetails());
                    }
                });

        /*recyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        rootDatabaseReference
                                .child(CHAT_MESSAGES)
                                .child(chatUIDKey)
                                .addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                        {
                                            ChatMessage message = snapshot.getValue(ChatMessage.class);
                                            Log.d(TAG, "onDataChange: " + message.toString());
                                            chatMessages.add(message);
                                            adapter.notifyItemInserted(chatMessages.size()-1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });
                    }
                }, 5000);
            }
        });*/
    }

    public static String getChatUIDKey()
    {
        return chatUIDKey;
    }

    public static void setChatUIDKey(String chatUIDKey)
    {
        ChatScreenActivity.chatUIDKey = chatUIDKey;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
