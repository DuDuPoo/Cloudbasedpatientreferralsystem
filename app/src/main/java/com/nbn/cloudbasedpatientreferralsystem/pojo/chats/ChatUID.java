package com.nbn.cloudbasedpatientreferralsystem.pojo.chats;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dudupoo on 30/1/18.
 */

public class ChatUID implements Serializable
{
    private List<String> members;
    private String lastMessageSent;

    public ChatUID(){}

    @Override
    public String toString()
    {
        return "ChatUID{" +
                "members=" + members +
                ", lastMessageSent='" + lastMessageSent + '\'' +
                '}';
    }

    public List<String> getMembers()
    {
        return members;
    }

    public void setMembers(List<String> members)
    {
        this.members = members;
    }

    public String getLastMessageSent()
    {
        return lastMessageSent;
    }

    public void setLastMessageSent(String lastMessageSent)
    {
        this.lastMessageSent = lastMessageSent;
    }
}
