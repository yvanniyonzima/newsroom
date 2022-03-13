package comp4905.newsroom.Classes.NewsSearchRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import comp4905.newsroom.Classes.ArticleCardItem;
import comp4905.newsroom.R;

public class NewsSearchRecyclerAdapter extends RecyclerView.Adapter<NewsSearchRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<ArticleCardItem> mArticleCardList;
    private OnItemClickListener mClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onSummaryClicked(int position);
        void onChatBubbleClick(int position);
        void onExternalLinkClick(int position);
        void onLikeClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){mClickListener = listener;}

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mArticleTitle;
        public TextView mArticleCategory;
        public TextView mArticleSummary;
        public TextView mArticlePublisherAndDate;
        public ImageView mChatBubble;
        public ImageView mExternalLink;
        public ImageView mLikeArticle;

        public RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);

            mArticleTitle = itemView.findViewById(R.id.article_title_view);
            mArticleCategory = itemView.findViewById(R.id.article_category_view);
            mArticleSummary = itemView.findViewById(R.id.article_summary_view);
            mArticlePublisherAndDate = itemView.findViewById(R.id.publisher_date_view);
            mChatBubble = itemView.findViewById(R.id.news_search_chat);
            mExternalLink = itemView.findViewById(R.id.news_search_external_link);
            mLikeArticle = itemView.findViewById(R.id.news_search_empty_heart);

            /*=========================ON CLICK LISTENERS BEGIN============================*/
            /*
            when the user clicks on the summary, the TextView will extend the height
            to wrap_content if the height was not enough already
            */
            mArticleTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onSummaryClicked(position);
                        }
                    }
                }
            });

            /*on click to start a new chat from the article*/
            mChatBubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onChatBubbleClick(position);
                        }
                    }
                }
            });

            /*on click to visit the url of the article (uses the devices browser)*/
            mExternalLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onExternalLinkClick(position);
                        }
                    }
                }
            });

            /*on click to like the article. This will save the article to the database*/
            mLikeArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onLikeClick(position);
                        }
                    }
                }
            });
            /*=========================ON CLICK LISTENERS END============================*/
        }
    }

    public NewsSearchRecyclerAdapter(ArrayList<ArticleCardItem> articleList)
    {
        mArticleCardList = new ArrayList<>();
        mArticleCardList = articleList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_item, parent, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view, mClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        ArticleCardItem currentItem = mArticleCardList.get(position);

        //set the image using picasso
        /*TODO:
           - add image view for article media
           - add imageURL to ArticleCardItem and then use it to set the image
        */
        holder.mArticleTitle.setText(currentItem.getTitle());
        holder.mArticleCategory.setText(currentItem.getCategory());
        holder.mArticleSummary.setText(currentItem.getSummary());
        String publisherAndDate = currentItem.getPublisher() + " ( " + currentItem.getDate() + " )";
        holder.mArticlePublisherAndDate.setText(publisherAndDate);
    }

    @Override
    public int getItemCount() { return mArticleCardList.size(); }
}
