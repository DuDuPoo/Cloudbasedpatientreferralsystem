package com.nbn.cloudbasedpatientreferralsystem.pojo.chats;

import java.io.Serializable;

/**
 * Created by dudupoo on 30/1/18.
 */

public class ChatMessage implements Serializable
{
    private String sentByUID;
    private String messageTime;
    private String message;
    private boolean read;

    public ChatMessage()
    {
    }

    @Override
    public String toString()
    {
        return "ChatMessage{" +
                "sentByUID='" + sentByUID + '\'' +
                ", messageTime='" + messageTime + '\'' +
                ", message='" + message + '\'' +
                ", read=" + read +
                '}';
    }

    public boolean isRead()
    {
        return read;
    }

    public void setRead(boolean read)
    {
        this.read = read;
    }

    public String getSentByUID()
    {
        return sentByUID;
    }

    public void setSentByUID(String sentByUID)
    {
        this.sentByUID = sentByUID;
    }

    public String getMessageTime()
    {
        return messageTime;
    }

    public void setMessageTime(String messageTime)
    {
        this.messageTime = messageTime;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
