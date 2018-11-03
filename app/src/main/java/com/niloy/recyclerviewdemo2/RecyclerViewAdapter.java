package com.niloy.recyclerviewdemo2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>   {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImageTexts = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext ;

    //This is the constructor
    public RecyclerViewAdapter(Context mContext,ArrayList<String> mImageTexts, ArrayList<String> mImages) {
        this.mImageTexts = mImageTexts;
        this.mImages = mImages;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Log.d(TAG,"onBindViewHolder: called.");

        //Loading progress bar
        /*final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(15f);
        circularProgressDrawable.setCenterRadius(70f);
        circularProgressDrawable.setColorSchemeColors(Color.parseColor("#D32F2F"), Color.parseColor("#009688") , Color.parseColor("#0288D1"));
        circularProgressDrawable.start();*/

        viewHolder.wheel.spin();

        //Load image from url and set it to imageview
        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(i))
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
        viewHolder.imageText.setText(mImageTexts.get(i));

        viewHolder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Clicked On : " + mImageTexts.get(i));

                Intent intent = new Intent(mContext , LargeImageView.class);
                intent.putExtra("imageUrl" , mImages.get(i));
                intent.putExtra("imageTag" , mImageTexts.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageTexts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
}