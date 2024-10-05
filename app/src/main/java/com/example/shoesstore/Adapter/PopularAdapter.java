package com.example.shoesstore.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.shoesstore.Domain.ItemsDomain;
import com.example.shoesstore.databinding.ViewholderPopListBinding;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {

    ArrayList<ItemsDomain> itemsDomains;
    Context context;

    public PopularAdapter(ArrayList<ItemsDomain> itemsDomains) {
        this.itemsDomains = itemsDomains;
    }

    @NonNull
    @Override
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopListBinding binding = ViewholderPopListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.Viewholder holder, int position) {
        holder.binding.title.setText(itemsDomains.get(position).getTitle());
        holder.binding.reviewTxt.setText(""+itemsDomains.get(position).getReview());
        holder.binding.priceTxt.setText("$"+itemsDomains.get(position).getPrice());
        holder.binding.ratingTxt.setText("("+itemsDomains.get(position).getRating()+")");
        holder.binding.oldPriceTxt.setText("$"+itemsDomains.get(position).getOldPrice());
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.ratingBar.setRating((float) itemsDomains.get(position).getRating());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());
        Glide.with(context)
                .load(itemsDomains.get(position).getPicUrl().get(0))
                .apply(requestOptions)
                .into(holder.binding.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsDomains.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        ViewholderPopListBinding binding;

        public Viewholder(ViewholderPopListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
