package comp4905.newsroom.Classes;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsAPIClient
{

    private static  final String TAG = NewsAPIClient.class.getName();
    private static final String KEY_NAME = "x-rapidapi-key";
    private static final String HOST_NAME = "x-rapidapi-host";

    private static final String HOST = "news67.p.rapidapi.com";
    private static final String API_KEY = "5bfa8ade7dmshd0ca52168f37411p1f1024jsnecb598fbbc8e";
    private static final String BASE_URL = "https://news67.p.rapidapi.com/v2/";

    //ENDPOINTS
    private static final String TOPIC_SEARCH = "topic-search?";
    private static final String NEWS_FEED_ENDPOINT = "feed?";
    private static final String WORLD_TRENDING_ENDPOINT = "trending?";
    private static final String COUNTRY_NEWS_ENDPOINT = "country-news?";

    private OkHttpClient okHttpClient = new OkHttpClient();

    private Request request;
    private Response response;

    public NewsAPIClient(){}


    public String searchUserPreferredNews() throws IOException
    {
        //BUILD THE URL USING PREFERRED LANGUAGE AND TOPICS
        String fullURL = BASE_URL + TOPIC_SEARCH;

        //add the language to the url (currently defaulted to english)
        fullURL += "languages=en";

        //indices to choose from for initial search
        ArrayList<Integer> indeces = new ArrayList<>();

        for(int i = 0; i < Globals.deviceUser.getFavorites().size(); i++)
        {
            indeces.add(i);
        }

        Random rand = new Random();
        //add the topic
        fullURL += "&search=";
        int searchStringLength = 0;
        for(int i = 0; i < Globals.deviceUser.getFavorites().size(); i++)
        {
            int randomIndex = rand.nextInt(indeces.size());
            indeces.remove(randomIndex);
            searchStringLength +=  Globals.deviceUser.getFavorites().get(randomIndex).length() + 1;

            if(searchStringLength > 24)
            {
                break;
            }
            else
            {
                //first topic added to url
                if((searchStringLength - Globals.deviceUser.getFavorites().get(randomIndex).length()) == 0)
                {
                    fullURL += Globals.deviceUser.getFavorites().get(randomIndex).toLowerCase();
                }
                else
                {
                    fullURL += "%20" + Globals.deviceUser.getFavorites().get(randomIndex).toLowerCase();
                }
            }

        }

        //add a batch size of 30 for every reload
        fullURL += "&batchSize=30";

        Log.i(TAG,"searchUserPreferredNews() => Request: " + fullURL);

        //build the request
        request = new Request.Builder()
                .url(fullURL)
                .get()
                .addHeader(KEY_NAME,API_KEY)
                .addHeader(HOST_NAME,HOST)
                .build();

        //get the response object
        response = okHttpClient.newCall(request).execute();

        //convert response to json String
        String responseToString = response.body().string();

        Log.i(TAG,"searchNews() => Response:" + responseToString);

        //return response
        return responseToString;

    }

    //function for news feed search
    public String searchNewsFeed(String[] languages, String sorting, String sentiment) throws IOException
    {
        //build the url
        String newsFeedUrl = BASE_URL + NEWS_FEED_ENDPOINT;

        //default batch size is 30
        newsFeedUrl += "batchSize=30";

        //get the languages
        if(languages.length > 0 && languages[0] != null && !languages[0].isEmpty())
        {
            newsFeedUrl += "&languages=";
            for(int i = 0; i < languages.length; i++)
            {
                Log.i(TAG, "searchNewsFeed => getting code for languages: " + languages[i]);
                if(i == (languages.length - 1))
                {
                    //last element
                    newsFeedUrl += Globals.languagesMap.get(languages[i]);
                }
                else
                {
                    newsFeedUrl += Globals.languagesMap.get(languages[i]) + "%2C";
                }
            }
        }
        else
        {
            //search in default languages
            newsFeedUrl += "&languages=en";

        }

        //get the sorting order
        if(!sorting.isEmpty())
        {
            newsFeedUrl += "&sortOrder=" + sorting;
        }

        //get the sentiment
        if(!sentiment.isEmpty())
        {
            //if the sentiment is not set to both, only then append to the url
            //by default, the api searches for both sentiments
            if(!sentiment.equals("both"))
            {
                newsFeedUrl += "&sentiment=" + sentiment;
            }
        }

        Log.i(TAG,"searchNewsFeed() => Request: " + newsFeedUrl);

        //build the request
        request = new Request.Builder()
                .url(newsFeedUrl)
                .get()
                .addHeader(KEY_NAME,API_KEY)
                .addHeader(HOST_NAME,HOST)
                .build();

        //get the response object
        response = okHttpClient.newCall(request).execute();

        //convert response to json String
        String responseToString = response.body().string();

        Log.i(TAG,"searchNews() => Response:" + responseToString);

        //return response
        return responseToString;
    }

    //function for world trending search
    public String searchWorldTrending(String[] trendingLanguages) throws IOException
    {

        //build the url
        String trendingUrl = BASE_URL + WORLD_TRENDING_ENDPOINT;

        //always get the top 30
        trendingUrl += "top=30";

        //add the languages
        trendingUrl += "&languages=";
        if(trendingLanguages.length > 0 && trendingLanguages[0] != null && !trendingLanguages[0].isEmpty())
        {
            for(int i = 0; i < trendingLanguages.length; i++)
            {
                Log.i(TAG, "searchWorldTrending() => getting code for languages: " + trendingLanguages[i]);
                if(i == (trendingLanguages.length - 1))
                {
                    //last element
                    trendingUrl += Globals.languagesMap.get(trendingLanguages[i]);
                }
                else
                {
                    trendingUrl += Globals.languagesMap.get(trendingLanguages[i]) + "%2C";
                }
            }

        }
        else
        {
            //search in default language
            trendingUrl += "en";
        }


        Log.i(TAG,"searchWorldTrending() => Request: " + trendingUrl);

        //build the request
        request = new Request.Builder()
                .url(trendingUrl)
                .get()
                .addHeader(KEY_NAME,API_KEY)
                .addHeader(HOST_NAME,HOST)
                .build();

        //get the response object
        response = okHttpClient.newCall(request).execute();

        //convert response to json String
        String responseToString = response.body().string();

        Log.i(TAG,"searchWorldTrending() => Response:" + responseToString);

        //return response
        return responseToString;
    }

    public String searchTopic(String[] query, String[] languages, String sorting) throws IOException
    {
        //build the url
        String topicSearchUrl = BASE_URL + TOPIC_SEARCH;

        //batchsize of 30
        topicSearchUrl += "batchSize=30";

        //add the query
        topicSearchUrl +="&search=";
        for(int i = 0; i < query.length; i++)
        {
            if(i == (query.length - 1))
            {
                topicSearchUrl += query[i];
            }
            else
            {
                topicSearchUrl += query[i] + "%20";
            }
        }

        //add the languages
        Log.i(TAG, "searchTopic() => Request:" + languages);
        topicSearchUrl += "&languages=";
        if(languages.length == 0)
        {
            topicSearchUrl += "en";
        }
        else
        {
            for(int i = 0; i < languages.length; i++)
            {
                if(i == (languages.length - 1))
                {
                    topicSearchUrl += Globals.languagesMap.get(languages[i]);
                }
                else
                {
                    topicSearchUrl += Globals.languagesMap.get(languages[i]) + "%2C";
                }
            }
        }



        //add the sorting
        if(sorting != null && sorting.length() > 0)
        {
            topicSearchUrl += "&sortBy=";

            if(sorting.equals("Relevance"))
            {
                topicSearchUrl += "relevance";
            }
            else
            {
                topicSearchUrl += "published";
            }
        }

        Log.i(TAG, "searchTopic() => Request:" + topicSearchUrl);

        //build the request
        request = new Request.Builder()
                .url(topicSearchUrl)
                .get()
                .addHeader(KEY_NAME,API_KEY)
                .addHeader(HOST_NAME,HOST)
                .build();

        //get the response object
        response = okHttpClient.newCall(request).execute();

        //convert response to json String
        String responseString = response.body().string();

        Log.i(TAG,"searchTopic() => Response:" + responseString);

        //return response
        return responseString;
    }

    //to search using country news endpoint
    public String countryNews(String[] languages, String fromCountry, String aboutCountry, String onlyInternational) throws IOException
    {
        //build the url
        String countryNewsUrl = BASE_URL + COUNTRY_NEWS_ENDPOINT;

        //batchsize of 30
        countryNewsUrl += "batchSize=30";

        //add from country
        countryNewsUrl += "&fromCountry=" + fromCountry;

        //add languages
        countryNewsUrl += "&languages=";
        for(int i = 0; i < languages.length; i++)
        {
            if(i == (languages.length - 1))
            {
                countryNewsUrl += Globals.languagesMap.get(languages[i]) ;
            }
            else
            {
                countryNewsUrl += Globals.languagesMap.get(languages[i])  + "%2C";
            }
        }

        //add about country
        countryNewsUrl += "&aboutCountry=" + aboutCountry;

        //add only international
        Log.i(TAG, "countryNews() => onlyInternational:" + onlyInternational);
        if(!onlyInternational.isEmpty() && onlyInternational != null)
        {
            countryNewsUrl += "&onlyInternational=false";
        }

        Log.i(TAG, "countryNews() => Request:" + countryNewsUrl);

        //build the request
        request = new Request.Builder()
                .url(countryNewsUrl)
                .get()
                .addHeader(KEY_NAME,API_KEY)
                .addHeader(HOST_NAME,HOST)
                .build();

        //get the response object
        response = okHttpClient.newCall(request).execute();

        //convert response to json String
        String responseString = response.body().string();

        Log.i(TAG,"countryNews() => Response:" + responseString);

        //return response
        return responseString;
    }

}
