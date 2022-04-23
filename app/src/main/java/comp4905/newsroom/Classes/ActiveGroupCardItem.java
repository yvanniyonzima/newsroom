package comp4905.newsroom.Classes;

public class ActiveGroupCardItem {

    //member variables
    private String mName;
    private String mStatus;
    private String mInfo;
    private String mTopic;
    private String mTopicLink;
    private Group mRelatedGroup;

    public ActiveGroupCardItem(String name, String status, String info, Group relatedGroup)
    {
        mName = name;
        mStatus = status;
        mInfo = info;
        mRelatedGroup = relatedGroup;
    }

    //GETTERS
    public String getName(){ return mName; }
    public String getStatus(){ return mStatus ; }
    public String getInfo(){ return mInfo; }
    public String getTopic(){ return mTopic; }
    public String getTopicLink(){ return mTopicLink; }
    public Group getRelatedGroup() { return mRelatedGroup; }

    //SETTERS
    public void setName(String groupName) { mName = groupName; }
    public void setStatus(String groupStatus) { mStatus = groupStatus; }
    public void setInfo(String groupInfo) { mInfo = groupInfo; }
    public void setTopic(String groupTopic) { mTopic = groupTopic; }
    public void setTopicLink(String groupTopicLink) { mTopicLink = groupTopicLink; }
    public void setRelatedGroup(Group relatedGroup) { mRelatedGroup = relatedGroup; }

    public void updateNumGroupMembersInfo(int numMember, String admin, String dateCreated)
    {
        mInfo = "<ul>" +
                "<li> &nbsp; Moderator:&nbsp; " + admin + "</li>" +
                "<li>&nbsp;" + String.valueOf(numMember) + " participants </li>" +
                "<li> &nbsp;Started on:&nbsp; " + dateCreated + "</li>" +
                "</ul>";
    }
}
