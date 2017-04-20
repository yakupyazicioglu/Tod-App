package com.mirrket.tod_app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Category;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public TextView category_name;
    public TextView itemCount;

    public CategoryViewHolder(View itemView) {
        super(itemView);

        category_name = (TextView) itemView.findViewById(R.id.category_name);
        itemCount = (TextView) itemView.findViewById(R.id.item_count);
    }

    public void bindToList(Category category) {
        category_name.setText(category.category_name);
        itemCount.setText(category.itemCount);
    }
}
