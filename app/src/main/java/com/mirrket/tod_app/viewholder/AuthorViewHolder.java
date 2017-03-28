package com.mirrket.tod_app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Author;
import com.squareup.picasso.Picasso;

/**
 * Created by yy on 25.03.2017.
 */

public class AuthorViewHolder extends RecyclerView.ViewHolder {
    public ImageView photoUrl;
    public TextView author_name;
    public TextView info;

    public AuthorViewHolder(View itemView) {
        super(itemView);

        photoUrl = (ImageView) itemView.findViewById(R.id.author_photo);
        author_name = (TextView) itemView.findViewById(R.id.author_name);
    }

    public void bindToPost(Author author) {
        Picasso.with(itemView.getContext())
                .load(author.photoUrl)
                .placeholder(R.drawable.ic_no_image)
                .fit()
                .into(photoUrl);
        author_name.setText(author.name);
    }



}
