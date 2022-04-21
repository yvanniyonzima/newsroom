package comp4905.newsroom.Classes;

public class Message {
    private String text;
    private String sentBy;
    private String sentByFirstName;
    private String date;
    private String time;
    private String key;

    public Message(String message, String userSending, String userSendingFirstName, String dateSent, String timeSent, String messageKey) {
        text = message;
        sentBy = userSending;
        sentByFirstName = userSendingFirstName;
        date = dateSent;
        time = timeSent;
        key = messageKey;
    }

    //GETTERS
    public String getText() { return text; }
    public String getSentBy() { return sentBy; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getKey() { return key;}
    public String getSentByFirstName() { return sentByFirstName;}

    //SETTERS
    public void setText(String message) { text = message; }
    public void setSentBy(String userSending) { sentBy = userSending; }
    public void setDate(String dateSent) { date = dateSent; }
    public void setTime(String timeSent) { time = timeSent; }
    public void setKey(String messageKey) { key = messageKey; }
    public void setSentByFirstName(String userSendingFirstName) { sentByFirstName = userSendingFirstName; }
}
