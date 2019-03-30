package com.niloy.recyclerviewdemo2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pnikosis.materialishprogress.ProgressWheel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends ListAdapter<Wallpaper, RecyclerViewAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView imageText;
        RelativeLayout relative;
        ProgressWheel wheel;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            imageText = itemView.findViewById(R.id.image_text);
            relative = itemView.findViewById(R.id.relative);
            wheel = itemView.findViewById(R.id.progress_wheel);
        }
    }

    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext ;

    //This is the constructor
    public RecyclerViewAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }
    /*public RecyclerViewAdapter(Context mContext,ArrayList<Wallpaper> mWallpaperList) {
        this.mContext = mContext;
        this.mWallpaperList = mWallpaperList;
    }*/

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Log.d(TAG,"onBindViewHolder: called.");

        viewHolder.wheel.spin();

        final Wallpaper wallpaper = getItem(i);

        //Load image from url and set it to imageview
        Glide.with(mContext)
                .asBitmap()
                .load(wallpaper.getUrl())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            viewHolder.wheel.stopSpinning();
                            viewHolder.wheel.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions().centerCrop())
                .into(viewHolder.image);

        //Set te text for the image loaded from url
        viewHolder.imageText.setText(wallpaper.getTitle());

        viewHolder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext , LargeImageView.class);
                intent.putExtra("imageUrl", wallpaper.getUrl());
                intent.putExtra("imageTag", wallpaper.getTitle());
                mContext.startActivity(intent);

            }
        });
    }

    public static final DiffUtil.ItemCallback<Wallpaper> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Wallpaper>() {
                @Override
                public boolean areItemsTheSame(Wallpaper oldItem, Wallpaper newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle());
                }

                @Override
                public boolean areContentsTheSame(Wallpaper oldItem, Wallpaper newItem) {
                    return (oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getUrl().equals(newItem.getUrl()));
                }
            };
}