/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.chat;

import bean.ConnectedUser;
import bean.UserLogin;
import entity.Users;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

/**
 * Miaou
 * @author nemo
 */
public class Chat
{
    /**
     * Conversations with friends
     */
    private Map<Users,List<Message>> contents = new HashMap<Users, List<Message>>();
    private List<Users> conversations=new ArrayList<Users>();
    private int nbConversations=0;
    private int currentIndex=-1;
    private boolean chatOpened=false;
    private String currentMessage="";
    private String receivedMessage="";
    private int receivedUserId=-1;
    private Users user;
    
    public Chat(Users user)
    {
        this.user=user;
    }
    
    public void clear()
    {
        this.conversations.clear();
    }

    public int getCurrentIndex()
    {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex)
    {
        this.currentIndex = currentIndex;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public void setCurrentMessage(String currentMessage) {
        this.currentMessage = currentMessage;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public void setReceivedMessage(String receivedMessage) {
        this.receivedMessage = receivedMessage;
    }

    public int getReceivedUserId() {
        return receivedUserId;
    }

    public void setReceivedUserId(int receivedUserId) {
        this.receivedUserId = receivedUserId;
    }
    
    public int switchUser(Users user)
    {
        this.currentIndex=this.conversations.indexOf(user)+1;
        return this.currentIndex;
    }
    
    public int getIndexOf(Users user)
    {
        return this.conversations.indexOf(user)+1;
    }

    public boolean isChatOpened()
    {
        return chatOpened;
    }

    public void setChatOpened(boolean chatOpened)
    {
        this.chatOpened = chatOpened;
    }
    
    public String switchOpenState()
    {
        this.chatOpened^=true;
        return Boolean.toString(this.chatOpened);
    }
    
    public List<Users> getConversations()
    {
        return conversations;
    }
    
    public void createConversation(Users user)
    {
        if(!this.conversations.contains(user))
        {
            this.conversations.add(user);
            this.nbConversations++;
            this.contents.put(user, new ArrayList<Message>());
            this.currentIndex=this.getIndexOf(user);
            this.chatOpened=true;
            ConnectedUser.getUserLogin(user).getChat().createConversation(this.user);
        }
    }
    
    public void deleteConversation(Users user)
    {
        System.err.println("Eh j't'ai fermé: "+user);
    }
    
    public int getConversIndex(int userId)
    {
        for(Users u:this.conversations)
        {
            if(u.getId()==userId)
            {
                return this.conversations.indexOf(u);
            }
        }
        return -1;
    }
    
    public List<Message> getConversWith(Users user)
    {
        List<Message> messages=null;
        for(Users u:this.contents.keySet())
        {
            if(u.equals(user))
            {
                messages=this.contents.get(u);
                break;
            }
        }
        return messages;
    }
    
    public List<Message> getLastConversWith(Users user)
    {
        List<Message> convers=this.getConversWith(user);
        if(convers==null)
        {
            return null;
        }
        if(convers.size()>20)
        {
            return convers.subList(convers.size()-20, convers.size());
        }
        return convers;
    }
    
    public void addMessage()
    {
        if(this.currentMessage==null||this.currentMessage.isEmpty()||
                this.currentIndex<1||this.user==null||
                this.contents.size()<1)
        {
            return;
        }
        this.contents.get(this.conversations.get(this.currentIndex-1)).add(
                new Message(this.currentMessage,true));
        UserLogin userLogin=ConnectedUser.getUserLogin(this.conversations.get(
                this.currentIndex-1));
        userLogin.newMessage(this.user, this.currentMessage);
        Chat chat=userLogin.getChat();
        chat.contents.get(this.user).add(
                new Message(this.currentMessage,false));
        PushContext pushContext = PushContextFactory.getDefault().getPushContext();
        pushContext.push("/chat"+userLogin.getUser().getId(),
                this.currentMessage);
        this.currentMessage="";
    }
    
    public boolean getNoConversation()
    {
        return this.conversations==null||this.conversations.isEmpty();
    }
    
    public int getNbConversations()
    {
        return this.nbConversations;
    }
}
