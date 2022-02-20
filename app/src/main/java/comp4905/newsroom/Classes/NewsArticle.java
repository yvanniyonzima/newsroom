package comp4905.newsroom.Classes;

public class NewsArticle
{
    private String title;
    private String author;
    private String datePublished;
    private String link;
    private String summary;
    private String topic;
    private String mediaLink;
    private String publisher;
    private String country;
    private String language;

    public NewsArticle(){}

    public NewsArticle(String articleTitle, String articleAuthor, String articleDatePublished, String articleLink, String articleSummary, String articleTopic, String articleMediaLink, String articlePublisher, String articleCountry, String articleLanguage) {
        this.title = articleTitle;
        this.author = articleAuthor;
        this.datePublished = articleDatePublished;
        this.link = articleLink;
        this.summary = articleSummary;
        this.topic = articleTopic;
        this.mediaLink = articleMediaLink;
        this.publisher = articlePublisher;
        this.country = articleCountry;
        this.language = articleLanguage;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDatePublished() { return datePublished; }
    public String getLink() { return link; }
    public String getSummary() { return summary; }
    public String getTopic() { return topic; }
    public String getMediaLink() { return mediaLink; }
    public String getPublisher() { return publisher; }
    public String getCountry() { return country; }
    public String getLanguage() { return language; }

    public void setTitle(String articleTitle) { this.title = articleTitle; }
    public void setAuthor(String articleAuthor) { this.author = articleAuthor; }
    public void setDatePublished(String articleDatePublished) { this.datePublished = articleDatePublished; }
    public void setLink(String articleLink) { this.link = articleLink; }
    public void setSummary(String articleSummary) { this.summary = articleSummary; }
    public void setTopic(String articleTopic) { this.topic = articleTopic; }
    public void setMediaLink(String articleMediaLink) { this.mediaLink = articleMediaLink; }
    public void setPublisher(String articlePublisher) { this.publisher = articlePublisher; }
    public void setCountry(String articleCountry) { this.country = articleCountry; }
    public void setLanguage(String articleLanguage) { this.language = articleLanguage; }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", datePublished='" + datePublished + '\'' +
                ", link='" + link + '\'' +
                ", summary='" + summary + '\'' +
                ", topic='" + topic + '\'' +
                ", mediaLink='" + mediaLink + '\'' +
                ", publisher='" + publisher + '\'' +
                ", country='" + country + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
