package comp4905.newsroom.Classes;

public class ActiveGroupCardItem {

    //member variables
    private String mName;
    private String mStatus;
    private String mInfo;
    private String mTopic;
    private String mTopicLink;

    public ActiveGroupCardItem(String name, String status, String info)
    {
        mName = name;
        mStatus = status;
        mInfo = info;
    }

    //GETTERS
    public String getName(){ return mName; }
    public String getStatus(){ return mStatus ; }
    public String getInfo(){ return mInfo; }
    public String getTopic(){ return mTopic; }
    public String getTopicLink(){ return mTopicLink; }

    //SETTERS
    public void setName(String groupName) { mName = groupName; }
    public void setStatus(String groupStatus) { mStatus = groupStatus; }
    public void setInfo(String groupInfo) { mInfo = groupInfo; }
    public void setTopic(String groupTopic) { mTopic = groupTopic; }
    public void setTopicLink(String groupTopicLink) { mTopicLink = groupTopicLink; }
}
