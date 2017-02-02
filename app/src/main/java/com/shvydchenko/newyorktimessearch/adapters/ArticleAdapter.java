package com.shvydchenko.newyorktimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shvydchenko.newyorktimessearch.R;
import com.shvydchenko.newyorktimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.shvydchenko.newyorktimessearch.R.id.ivThumbnail;

/**
 * Created by shvydchenko on 1/30/17.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {


    public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView headline;
        public TextView publishDate;
        public TextView byline;
        public ImageView thumbnail;


        public ArticleViewHolder(final View itemView) {
            super(itemView);

            headline = (TextView) itemView.findViewById(R.id.tvHeadline);
            publishDate = (TextView) itemView.findViewById(R.id.tvPublishDate);
            byline = (TextView) itemView.findViewById(R.id.tvByline);
            thumbnail = (ImageView) itemView.findViewById(ivThumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
            }
        }
    }

    private List<Article> articles;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ArticleAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View articleView = inflater.inflate(R.layout.item_article, parent, false);

        ArticleViewHolder articleViewHolder = new ArticleViewHolder(articleView);
        return articleViewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder articleViewHolder, int position) {
        Article article = articles.get(position);

        TextView tvHeadline = articleViewHolder.headline;
        tvHeadline.setText(article.getHeadline());

        TextView tvPublishDate = articleViewHolder.publishDate;
        tvPublishDate.setText(article.getPublishDate());

        TextView tvByline = articleViewHolder.byline;
        tvByline.setText(article.getByline());

        ImageView ivThumbnail = articleViewHolder.thumbnail;
        ivThumbnail.setImageResource(0);
        Picasso.with(getContext()).load(article.getFormattedThumbnailUrl()).into(ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    public void clear() {
        int size = this.articles.size();
        this.articles.clear();
        notifyItemRangeRemoved(0, size);
    }
}
