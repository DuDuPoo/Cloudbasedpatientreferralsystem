package com.nbn.cloudbasedpatientreferralsystem.chats;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatMessage;

import java.util.ArrayList;
import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.*;

public class NotificationService extends IntentService
{
    String TAG = getClass().getSimpleName();
     FirebaseDatabase firebaseDatabase;
     DatabaseReference rootDatabaseReference;
    NotificationManager mNotificationManager;

    public NotificationService()
    {
        super("Notification Service");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabaseReference = firebaseDatabase.getReference();
        ArrayList<String> userChats = workIntent.getStringArrayListExtra(NOTIFICATION_SERVICE_ARRAY);
        Log.d(TAG, "onHandleIntent: "+dataString);
        final Context context = this;

        if(userChats!=null) {
            if(userChats.size()>0) {
                for(int i=0; i<userChats.size(); i++) {
                    rootDatabaseReference
                            .child(CHAT_MESSAGES)
                            .child(userChats.get(i))
                            .addChildEventListener(new ChildEventListener()
                            {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                                {
                                    String messageID = dataSnapshot.getKey();
                                    ChatMessage cm = dataSnapshot.getValue(ChatMessage.class);
                                    if(!cm.getSentByUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        if(!cm.isRead()) {
                                            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                                    .setSmallIcon(R.drawable.ic_message_black_24dp)
                                                    .setContentTitle("New Message from Patient")
                                                    .setContentText(cm.getMessage())
                                                    .setOnlyAlertOnce(true)
                                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                            mBuilder.setAutoCancel(true);
                                            mBuilder.setLocalOnly(false);


                                            mNotificationManager.notify(100, mBuilder.build());

                                        }
                                    }
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

                                }
                            });
                }
            }
        }
    }
}