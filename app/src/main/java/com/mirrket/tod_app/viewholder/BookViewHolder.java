package com.mirrket.tod_app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Book;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BookViewHolder extends RecyclerView.ViewHolder {

    public TextView bookView;
    public TextView authorView;
    public TextView publisherView;
    public TextView pageView;
    public TextView infoView;
    public ImageView readView;
    public ImageView wtReadView;
    public ImageView readingView;
    public ImageView photoView;
    public TextView numReadedsView;
    public TextView numWantToReadsView;
    public TextView numReadingsView;
    public RatingBar ratingBar;
    public ImageButton overflow;

    public BookViewHolder(View itemView) {
        super(itemView);

        bookView = (TextView) itemView.findViewById(R.id.book_name);
        authorView = (TextView) itemView.findViewById(R.id.author_name);
        publisherView = (TextView) itemView.findViewById(R.id.field_publisher);
        pageView = (TextView) itemView.findViewById(R.id.field_page);
        photoView = (ImageView) itemView.findViewById(R.id.photo_iv);
        readView = (ImageView) itemView.findViewById(R.id.readed_img);
        wtReadView = (ImageView) itemView.findViewById(R.id.wantToRead_img);
        readingView = (ImageView) itemView.findViewById(R.id.reading_img);
        numWantToReadsView = (TextView) itemView.findViewById(R.id.wantToRead_num);
        numReadingsView = (TextView) itemView.findViewById(R.id.reading_num);
        numReadedsView = (TextView) itemView.findViewById(R.id.readed_num);
        infoView = (TextView) itemView.findViewById(R.id.post_body);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        overflow = (ImageButton) itemView.findViewById(R.id.btn_read_info);
    }

    public void bindToPost(Book post) {
        Picasso.with(itemView.getContext())
                .load(post.coverUrl)
                .placeholder(R.drawable.ic_no_image)
                .fit()
                .into(photoView);
        bookView.setText(post.book);
        authorView.setText(post.author);
        ratingBar.setRating(post.rating);
    }

    public void bindToRead(Book post) {
        Picasso.with(itemView.getContext())
                .load(post.coverUrl)
                .placeholder(R.drawable.ic_no_image)
                .fit()
                .into(photoView);
        bookView.setText(post.book);
        authorView.setText(post.author);
        publisherView.setText(post.publisher);
        pageView.setText(post.page);
    }

    public void bindToReaded(Book post, View.OnClickListener readClickListener) {
        numReadedsView.setText(" ("+String.valueOf(post.readedCount)+")");
        readView.setOnClickListener(readClickListener);
    }

    public void bindToWantToRead(Book post, View.OnClickListener wtReadClickListener) {
        numWantToReadsView.setText(" ("+String.valueOf(post.wantToReadCount)+")");
        wtReadView.setOnClickListener(wtReadClickListener);
    }

    public void bindToReading(Book post, View.OnClickListener wtReadClickListener) {
        numReadingsView.setText(" ("+String.valueOf(post.readingCount)+")");
        readingView.setOnClickListener(wtReadClickListener);
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
