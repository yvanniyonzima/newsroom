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
                /*
                String title = articleResult.getString("title");
                String author = articleResult.getString("author");
                String datePublished = articleResult.getString("published_date");
                String link = articleResult.getString("link");
                String summary = articleResult.getString("summary");
                String topic = articleResult.getString("topic");
                String mediaLink = articleResult.getString("media");
                String publisher = articleResult.getString("clean_url");
                String country = articleResult.getString("country");
                String language = articleResult.getString("language");
                */

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
            Log.i(TAG, "parseNewsResults() => " + jsonException.toString());
        }

        return  newsResults;
    }
}
