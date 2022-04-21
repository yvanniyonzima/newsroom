package comp4905.newsroom.Classes;

public class Group {

    private String name;
    private String description;
    private String topic;
    private String topicLink;
    private String groupAdmin;
    private String status;
    private int numMembers;
    private String uniqueKey;

    public Group(String title,String admin, String groupDescription, String groupStatus, String articleReferenceUrl) {
        name = title;
        description = groupDescription;
        topicLink = articleReferenceUrl;
        groupAdmin = admin;
        status = groupStatus;
        numMembers = 1;
    }

    //GETTERS
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTopic() { return topic; }
    public String getTopicLink() { return topicLink; }
    public String getGroupAdmin() { return groupAdmin; }
    public String getStatus() { return status; }
    public int getNumMembers() { return numMembers; }
    public void setUniqueKey(String key) { uniqueKey = key; }

    //SETTERS
    public void setName(String title) { name = title; }
    public void setDescription(String groupDescription) { description = groupDescription; }
    public void setTopic(String groupTopic) { topic = groupTopic; }
    public void setTopicLink(String articleReference) { topicLink = articleReference; }
    public void setGroupAdmin(String admin) { groupAdmin = admin; }
    public void setStatus(String groupStatus) { status = groupStatus; }
    public void setNumMembers(int numberOfMembers) { numMembers = numberOfMembers; }
    public String getUniqueKey() { return uniqueKey; }
}
