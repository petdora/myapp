package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class petAdapter extends RecyclerView.Adapter<petAdapter.ViewHolder> {
    private List<pet> mfList;
    private Context context;
    public static String petname;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        public ViewHolder(View view){
            super(view);
            image=view.findViewById(R.id.image);
            name=view.findViewById(R.id.name);
        }
    }


    public petAdapter(List<pet> bookList,Context context){
        mfList=bookList;
        this.context=context;

    }
    @Override
    public petAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_item,parent,false);
        RecyclerView.ViewHolder holder=new ViewHolder(view);
        return (ViewHolder) holder;
    }

    @Override
    public void onBindViewHolder(final petAdapter.ViewHolder holder, int position) {
        final pet friends=mfList.get(position);
        holder.name.setText(friends.getName());
        String im=friends.getImage();

        Glide.with(context).load(im)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.image);
      /*  Picasso.with(context).load(im)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.image);*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petname=friends.getName();
                v.getContext().startActivity(new Intent(v.getContext(),Petsetup.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mfList.size();
    }
}


