package comp4905.newsroom.Classes.RecyclerAdapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import comp4905.newsroom.Classes.ActiveGroupCardItem;
import comp4905.newsroom.R;

public class ActiveGroupRecyclerAdapter extends RecyclerView.Adapter<ActiveGroupRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<ActiveGroupCardItem> mGroupsCardList;
    private OnItemClickListener mClickListener;

    public interface OnItemClickListener
    {
        void onExternalLinkClick(int position);
        void onGroupJoinClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){ mClickListener = listener; }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mGroupName;
        public TextView mGroupStatus;
        public TextView mGroupInfo;
        public TextView mGroupTopic;
        public ImageView mGroupExternalLink;
        public TextView mGroupJoin;

        public RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);

            //mGroupName = itemView.findViewById(R)
            mGroupName = itemView.findViewById(R.id.active_group_name);
            mGroupStatus = itemView.findViewById(R.id.active_group_status);
            mGroupInfo = itemView.findViewById(R.id.active_chat_info);
            mGroupTopic = itemView.findViewById(R.id.active_group_topic);
            mGroupJoin = itemView.findViewById(R.id.active_group_join);
            mGroupExternalLink = itemView.findViewById(R.id.active_group_link);

            mGroupExternalLink.setOnClickListener(new View.OnClickListener() {
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

            mGroupJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onGroupJoinClick(position);
                        }
                    }
                }
            });
        }
    }

    public ActiveGroupRecyclerAdapter(ArrayList<ActiveGroupCardItem> groupCardItems)
    {
        mGroupsCardList = new ArrayList<>();
        mGroupsCardList = groupCardItems;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_group_card_item, parent, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view, mClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        ActiveGroupCardItem currentGroupItem = mGroupsCardList.get(position);

        holder.mGroupName.setText(currentGroupItem.getName());
        holder.mGroupStatus.setText(currentGroupItem.getStatus());
        holder.mGroupInfo.setText(Html.fromHtml(currentGroupItem.getInfo(), Html.FROM_HTML_MODE_COMPACT));
        holder.mGroupTopic.setText(currentGroupItem.getTopic());

    }

    @Override
    public int getItemCount() { return mGroupsCardList.size(); }
}
