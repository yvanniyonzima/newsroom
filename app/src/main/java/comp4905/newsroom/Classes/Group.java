package comp4905.newsroom.Classes;

import java.util.ArrayList;

public class Group {

    private String mTitle;
    private String mDescription;
    private NewsArticle mArticleReference;
    private String mGroupAdmin;
    private ArrayList<String> mMembers;
    private ArrayList<Message> mGroupMessages;
    private String mStatus;

    public Group(String title, String admin, String status) {
        mTitle = title;
        mGroupAdmin = admin;
        mStatus = status;
    }

    public Group(String title, String description, String admin, String status) {
        mTitle = title;
        mDescription = description;
        mGroupAdmin = admin;
        mStatus = status;
    }

    public Group(String title, String description, NewsArticle articleReference, String admin, String status) {
        mTitle = title;
        mDescription = description;
        mArticleReference = articleReference;
        mGroupAdmin = admin;
        mStatus = status;
    }

    //GETTERS
    public String getTitle() { return mTitle; }
    public String getDescription() { return mDescription; }
    public NewsArticle getArticleReference() { return mArticleReference; }
    public String getGroupAdmin() { return mGroupAdmin; }
    public ArrayList<String> getMembers() { return mMembers; }
    public ArrayList<Message> getGroupMessages() { return mGroupMessages; }
    public String getStatus() { return mStatus; }

    //SETTERS
    public void setTitle(String title) { mTitle = title; }
    public void setDescription(String description) { mDescription = description; }
    public void setArticleReference(NewsArticle articleReference) { mArticleReference = articleReference; }
    public void setGroupAdmin(String groupAdmin) { mGroupAdmin = groupAdmin; }
    public void setMembers(ArrayList<String> members) { mMembers = members; }
    public void setGroupMessages(ArrayList<Message> groupMessages) { mGroupMessages = groupMessages; }
    public void setStatus(String status) { mStatus = status; }

    //
}
