import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
class User{

private String username;
public User(String username){
this.username = username;
}

public String getUsername(){
  return username;
}
}

class Message{
private User sender;
private String content;

public Message(User sender, String content){
this.sender = sender;
this.content = content;
}

public User getSender(){
  return sender;
}

public String getContent(){
  return content;
 }
}

class textMessage extends Message{
 public textMessage(User sender, String content){
super(sender,content);
}
}

class ChatRoom extends Observable{
  private String chatid;
  private Map<String,User> activeUsers = new ConcurrentHashMap<>();
  private List<Message> messages = new ArrayList<>();

public ChatRoom(String chatid)
{
this.chatid=chatid;
}

public void addUser(User user){
 activeUsers.put(user.getUsername(),user);
}

public void removeUser(User user)
{
activeUsers.remove(user.getUsername());
}

public void addMessage(Message message){
  messages.add(message);
  setChanged();
notifyObservers(messages);
}

public String getchatid(){
 return chatid;
}
public Map<String,User> getActiveUsers(){
  return activeUsers;
}
public List<Message> getMessages(){
return messages;
}
}
class ChatRoomManager{
  private static Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
  private ChatRoomManager(){}
  public static synchronized ChatRoom getChatRoom(String chatid){
    if (!chatRooms.containsKey(chatid)){
        chatRooms.put(chatid, new ChatRoom(chatid));
}
return chatRooms.get(chatid);
}
}

interface Adapter{
  void connecttoChatRoom(ChatRoom chatRoom);
  void sendMessage(String message,User sender);  
  void receiveMessage(Message message);
}

class ChatAdapter implements Adapter{
    private ChatRoom chatRoom;
    public void connecttoChatRoom(ChatRoom chatRoom){
      this.chatRoom = chatRoom;
}

public void sendMessage(String message,User sender)
{
  chatRoom.addMessage(new textMessage(sender, message));
}

public void receiveMessage(Message message){
    chatRoom.addMessage(message);
}
}
public class Main{
  public static void main(String[] args)
{
User user1 = new User("Alice");
User user2 = new User("Bob");
User user3 = new User("Charlie");

ChatRoom chatRoom = ChatRoomManager.getChatRoom("Room123");

chatRoom.addUser(user1);
chatRoom.addUser(user2);
chatRoom.addUser(user3);
ChatAdapter adapter = new ChatAdapter();
adapter.connecttoChatRoom(chatRoom);

adapter.sendMessage("Hello , Bob", user1);
adapter.sendMessage("Hi , Alice", user3);
adapter.sendMessage("Good Morning , Alice , Bob", user2);

Map<String, User> activeUsers = chatRoom.getActiveUsers();
System.out.println("Active Users in " + chatRoom.getchatid() + ":");
for(User user :activeUsers.values()){
  System.out.println(user.getUsername());
}

List<Message> messages = chatRoom.getMessages();
System.out.println("\nMessages in " + chatRoom.getchatid() + ":");
for(Message message : messages){
  System.out.println(message.getSender().getUsername() + " : " + message.getContent());
}
}
}