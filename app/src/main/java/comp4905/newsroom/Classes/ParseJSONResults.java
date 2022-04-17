package comp4905.newsroom.Classes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseJSONResults
{
    private static final String TAG = ParseJSONResults.class.getName();

    public ArrayList<NewsArticle> parseNewsResults(String JSONString)
    {
        ArrayList<NewsArticle> newsResults = new ArrayList<>();

        try
        {
            JSONObject newsJsonObject = new JSONObject(JSONString);

            //JSONArray articlesArray  = newsJsonObject.getJSONArray("articles");

            JSONArray articlesArray  = newsJsonObject.getJSONArray("news");
            for (int i = 0; i < articlesArray.length(); i++)
            {
                //get the json strings needed to make an article object
                JSONObject articleResult = articlesArray.getJSONObject(i);

                String title = articleResult.getString("Title");
                String author = "";
                String datePublished = articleResult.getString("PublishedOn");
                String link = articleResult.getString("Url");
                String summary = articleResult.getString("Summary");

                JSONObject categories = articleResult.getJSONObject("Categories");
                String topic = categories.getString("label");

                String mediaLink = articleResult.getString("Image");
                String publisher = articleResult.getString("Source");

                String country = "";
                JSONArray countries = articleResult.getJSONArray("Countries");
                if(countries.length() > 0)
                {
                    country = countries.getString(0);
                }
                else
                {
                    country = "";
                }


                String language = "en";

                //make new NewsArticle object
                NewsArticle newArticle = new NewsArticle(title, author, datePublished, link, summary, topic, mediaLink, publisher, country, language);

                //log article info
                //Log.i(TAG, "parseNewsResults() => \n" + newArticle);

                //add new article to newsResults
                newsResults.add(newArticle);
            }

        }
        catch(JSONException jsonException)
        {
            Log.i(TAG, "parseNewsResults() => " + jsonException);
        }

        return  newsResults;
    }

    public ArrayList<NewsArticle> parseWorldTrendingResults(String JSONString)
    {
        ArrayList<NewsArticle> trendingNewsResults = new ArrayList<>();

        try
        {

            JSONObject trendingJsonObject = new JSONObject(JSONString);

            //world trending results have clusters of news
            JSONArray trendingClusterArray = trendingJsonObject.getJSONArray("news");

            //each news clusters have an array of News
            for(int i = 0; i < trendingClusterArray.length(); i++)
            {
                //get the cluster
                JSONObject currentCluster = trendingClusterArray.getJSONObject(i);

                //log the cluster
                //Log.i(TAG,  "parseWorldTrendingResults() => cluster " + i + ": " + currentCluster);

                //from the cluster get the array of news
                JSONArray currentClusterArticles = currentCluster.getJSONArray("News");

                //call the parse cluster method and add all articles to trending news
                trendingNewsResults.addAll(parseWorldTrendingCluster(currentClusterArticles));
            }
        }
        catch (JSONException exception)
        {
            Log.e(TAG, "parseWorldTrendingResults() => " + exception.toString());
        }

        return  trendingNewsResults;

    }

    private ArrayList<NewsArticle> parseWorldTrendingCluster(JSONArray cluster) throws JSONException {
        ArrayList<NewsArticle> clusterArticles = new ArrayList<>();

        for(int i = 0; i < cluster.length(); i++)
        {
            JSONObject clusterArticleResult = cluster.getJSONObject(i);

            String title = clusterArticleResult.getString("Title");
            String author = "";
            String datePublished = clusterArticleResult.getString("PublishedOn");
            String link = clusterArticleResult.getString("Url");
            String summary = clusterArticleResult.getString("Summary");

            JSONObject categories = clusterArticleResult.getJSONObject("Categories");
            String topic = categories.getString("label");

            String mediaLink = clusterArticleResult.getString("Image");
            String publisher = clusterArticleResult.getString("Source");

            String country = "";
            JSONArray countries = clusterArticleResult.getJSONArray("Countries");
            if(countries.length() > 0)
            {
                country = countries.getString(0);
            }
            else
            {
                country = "";
            }


            String language = "en";

            //make new NewsArticle object
            NewsArticle newClusterArticle = new NewsArticle(title, author, datePublished, link, summary, topic, mediaLink, publisher, country, language);

            //add the article to the cluster articles
            clusterArticles.add(newClusterArticle);
        }

        return clusterArticles;
    }

}
