package comp4905.newsroom.Classes;

import java.util.ArrayList;

public class Group {

    private String name;
    private String description;
    private String topic;
    private String topicLink;
    private String groupAdmin;
    private String status;
    private int numMembers;
    private String uniqueKey;
    private String dateCreated;
    private ArrayList<String> members;
    private ArrayList<String> joinRequests;
    private ArrayList<String> bannedMembers;

    public Group(String title,String admin, String groupDescription, String groupStatus, String topic, String topicUrl, String creationDate) {
        name = title;
        description = groupDescription;
        this.topic = topic;
        topicLink = topicUrl;
        groupAdmin = admin;
        status = groupStatus;
        numMembers = 1;
        dateCreated = creationDate;
        members = new ArrayList<>();
        members.add(admin);
        joinRequests = new ArrayList<>();
        bannedMembers = new ArrayList<>();
    }

    //GETTERS
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTopic() { return topic; }
    public String getTopicLink() { return topicLink; }
    public String getGroupAdmin() { return groupAdmin; }
    public String getStatus() { return status; }
    public int getNumMembers() { return numMembers; }
    public String getUniqueKey() { return uniqueKey; }
    public String getDateCreated() { return dateCreated; }
    public ArrayList<String> getMembers() { return members; }
    public ArrayList<String> getBannedMembers() { return bannedMembers; }
    public ArrayList<String> getJoinRequests() { return joinRequests; }

    //SETTERS
    public void setName(String title) { name = title; }
    public void setDescription(String groupDescription) { description = groupDescription; }
    public void setTopic(String groupTopic) { topic = groupTopic; }
    public void setTopicLink(String articleReference) { topicLink = articleReference; }
    public void setGroupAdmin(String admin) { groupAdmin = admin; }
    public void setStatus(String groupStatus) { status = groupStatus; }
    public void setNumMembers(int numberOfMembers) { numMembers = numberOfMembers; }
    public void setUniqueKey(String key) { uniqueKey = key; }
    public void setDateCreated(String creationDate) { dateCreated = creationDate; }
    public void setMembers(ArrayList<String> groupMembers) { members = groupMembers; }
    public void setBannedMembers(ArrayList<String> banned) { bannedMembers = banned; }
    public void setJoinRequests(ArrayList<String> requests) { joinRequests = requests; }
}
