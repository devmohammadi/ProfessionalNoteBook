package com.mohammadi.dashti.professionalnotebook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.model.Category;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Category> mCategory;

    public CategoryAdapter(Context mContext, List<Category> mCategory) {
        this.mContext = mContext;
        this.mCategory = mCategory;
    }

    private CategoryAdapter.OnRecyclerItemClick onRecyclerItemClick;

    public interface OnRecyclerItemClick {
        void onClick(Category mCategory);
    }

    public void setOnRecyclerItemClick(OnRecyclerItemClick onRecyclerItemClick) {
        this.onRecyclerItemClick = onRecyclerItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.category_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleCategory.setText(mCategory.get(position).getTitle());


        Picasso.get()
                .load(mCategory.get(position).getImage())
                .fit()
                .placeholder(R.drawable.ic_category_image)
                .error(R.drawable.ic_category_image_error)
                .into(holder.imageCategory);

    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageCategory;
        private TextView titleCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageCategory = itemView.findViewById(R.id.ivImageCategory);
            titleCategory = itemView.findViewById(R.id.tvTitleCategory);

            itemView.setOnClickListener(itemViewClick -> getCategoryPosition());
        }

        private void getCategoryPosition() {
            if (onRecyclerItemClick != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onRecyclerItemClick.onClick(mCategory.get(pos));
                }
            }
        }
    }

}
