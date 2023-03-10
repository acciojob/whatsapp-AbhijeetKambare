package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private HashMap<String,User> usersMap;
    private HashMap<Integer,Message> messageMap;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.usersMap=new HashMap<>();
//        this.messageMap=new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public boolean isNewUser(String mobile) {
        if(usersMap.containsKey(mobile)) return false;
        return true;
    }

    public void createUser(String name, String mobile) {
        usersMap.put(mobile, new User(name, mobile));
    }
    public Group createGroup(List<User> users){
        if (users.size()==2)  return this.personalchat(users);
        this.customGroupCount++;
        String groupNmae="Group"+this.customGroupCount;
        Group group=new Group(groupNmae,users.size());
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));
        return group;
    }
    public Group personalchat(List<User> users){
        String groupName=users.get(1).getName();
        Group pernalGroup=new Group(groupName,2);
        groupUserMap.put(pernalGroup,users);
        return pernalGroup;
    }
    public int createMessage(String content){
        messageId++;
        Message message=new Message(messageId,content,new Date());
        return messageId;
    }
    public int sendMessage(Message massage,User sender,Group group) throws Exception{
        if(!groupUserMap.containsKey(group))throw new Exception("Group does not exist");
        if(!isUserExist(group,sender)) throw new Exception("You are not allowed to send message");

        List<Message> messages = new ArrayList<>();
        if(groupMessageMap.containsKey(group)) messages = groupMessageMap.get(group);

        messages.add(massage);
        groupMessageMap.put(group, messages);
        return messages.size();
    }
    public boolean isUserExist(Group group,User sender){
        List<User> ussers =groupUserMap.get(group);
        for (User user:ussers){
            if(user.equals(sender))
                return true;
        }
        return false;
    }
    public String changeAdmin(User approver,User user,Group group)throws  Exception{
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(groupUserMap.containsKey(group)){
           if(!approver.equals(adminMap.get(group))) throw new Exception("Approver does not have rights");
        }
        if(!isUserExist(group,user)) throw new Exception("User is not a participant");
        adminMap.put(group,user);
        return "SUCCESS";
    }
}
