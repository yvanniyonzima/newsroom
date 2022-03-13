package comp4905.newsroom.Classes;

public class ArticleCardItem {

    //member variables
    private String mTitle;
    private String mCategory;
    private String mSummary;
    private String mPublisher;
    private String mDate;
    private String mURL;

    //constructor
    public ArticleCardItem(String title, String category, String summary, String publisher, String date, String URL)
    {
        mTitle = title;
        mCategory = category;
        mSummary = summary;
        mPublisher = publisher;
        mDate = date;
        mURL = URL;
    }

    //getters
    public String getTitle() { return mTitle; }
    public String getCategory() { return mCategory; }
    public String getSummary() { return mSummary; }
    public String getPublisher() { return mPublisher; }
    public String getDate() { return mDate; }
    public String getURL() {return mURL; }

    @Override
    public String toString() {
        return "ArticleCardItem{" +
                "mTitle='" + mTitle + '\'' +
                ", mCategory='" + mCategory + '\'' +
                ", mSummary='" + mSummary + '\'' +
                ", mPublisher='" + mPublisher + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mURL='" + mURL + '\'' +
                '}';
    }
}
