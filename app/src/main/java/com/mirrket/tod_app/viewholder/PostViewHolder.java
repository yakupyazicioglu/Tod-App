package com.mirrket.tod_app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Post;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorTitleView;
    public TextView publisherView;
    public TextView pageView;
    public TextView bodyView;
    public ImageView readView;
    public ImageView wtReadView;
    public ImageView readingView;
    public ImageView photoView;
    public TextView numReadedsView;
    public TextView numWantToReadsView;
    public TextView numReadingsView;
    public RatingBar ratingBar;
    public ImageButton overflow;
    public float rating;
    //public TextView authorView;
    //public ImageView starView;
    //public ImageButton overflow;
    //public TextView numStarsView;
    //public TextView numRateView;
    //public TextView dateView;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.book_name);
        authorTitleView = (TextView) itemView.findViewById(R.id.author_name);
        publisherView = (TextView) itemView.findViewById(R.id.field_publisher);
        pageView = (TextView) itemView.findViewById(R.id.field_page);
        photoView = (ImageView) itemView.findViewById(R.id.photo_iv);
        readView = (ImageView) itemView.findViewById(R.id.readed_img);
        wtReadView = (ImageView) itemView.findViewById(R.id.wantToRead_img);
        readingView = (ImageView) itemView.findViewById(R.id.reading_img);
        numWantToReadsView = (TextView) itemView.findViewById(R.id.wantToRead_num);
        numReadingsView = (TextView) itemView.findViewById(R.id.reading_num);
        numReadedsView = (TextView) itemView.findViewById(R.id.readed_num);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        overflow = (ImageButton) itemView.findViewById(R.id.btn_read_info);
        //authorView = (TextView) itemView.findViewById(R.id.post_author);
        //starView = (ImageView) itemView.findViewById(R.id.star);
        //overflow = (ImageButton) itemView.findViewById(R.id.overflow);
        //numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        //numRateView = (TextView) itemView.findViewById(R.id.rate_num);
        //dateView = (TextView) itemView.findViewById(R.id.date_title);
    }

    public void bindToPost(Post post) {
        Picasso.with(itemView.getContext())
                .load(post.fileUri)
                .placeholder(R.drawable.ic_no_image)
                .fit()
                .into(photoView);
        titleView.setText(post.title);
        authorTitleView.setText(post.title_author);
        ratingBar.setRating(post.rating);
    }

    public void bindToRead(Post post) {
        Picasso.with(itemView.getContext())
                .load(post.fileUri)
                .placeholder(R.drawable.ic_no_image)
                .fit()
                .into(photoView);
        titleView.setText(post.title);
        authorTitleView.setText(post.title_author);
        publisherView.setText(post.publisher);
        pageView.setText(post.page);
    }

    public void bindToReaded(Post post, View.OnClickListener readClickListener) {
        numReadedsView.setText(" ("+String.valueOf(post.readedCount)+")");
        readView.setOnClickListener(readClickListener);
    }

    public void bindToWantToRead(Post post, View.OnClickListener wtReadClickListener) {
        numWantToReadsView.setText(" ("+String.valueOf(post.wantToReadCount)+")");
        wtReadView.setOnClickListener(wtReadClickListener);
    }

    public void bindToReading(Post post, View.OnClickListener wtReadClickListener) {
        numReadingsView.setText(" ("+String.valueOf(post.readingCount)+")");
        readingView.setOnClickListener(wtReadClickListener);
    }

    public void bindToRating(Post post, View.OnTouchListener ratingClickListener) {
        ratingBar.setRating(post.rating);
        readingView.setOnTouchListener(ratingClickListener);
    }


    public String dateControl(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yy");
        final String currentDateStr = new SimpleDateFormat("d/M/yy").format(Calendar.getInstance().getTime());
        final Date currentDate = dateFormat.parse(currentDateStr);
        final Date postDate = dateFormat.parse(date);
        final String today = "Today";
        final String yesterday = "Yesterday";

        if (postDate.equals(currentDate))
            return today;
        if (postDate.before(currentDate))
            return date;

        return date;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
