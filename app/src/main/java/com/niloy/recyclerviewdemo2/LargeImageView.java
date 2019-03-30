package com.niloy.recyclerviewdemo2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import dmax.dialog.SpotsDialog;


public class LargeImageView extends AppCompatActivity {

    ImageView image;
    TextView text;

    private final int REQUEST_ASK_WRITE_ACCESS = 44654;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image_view);

        //Fast Android Networking
        AndroidNetworking.initialize(getApplicationContext());

        image = findViewById(R.id.image);
        text = findViewById(R.id.tag);

        final Button downloadImage = findViewById(R.id.button);

        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });


        if(getIntent().hasExtra("imageUrl") && getIntent().hasExtra("imageTag"))    {

            setImageAndTag(getIntent().getExtras().getString("imageUrl") , getIntent().getExtras().getString("imageTag"));

        }

    }

    public void setImageAndTag(String imageUrl, String imageTag) {

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(70f);
        circularProgressDrawable.setColorSchemeColors(Color.parseColor("#0288D1"));
        circularProgressDrawable.start();

        Glide.with(this).asBitmap().load(imageUrl)
                .apply(new RequestOptions().placeholder(circularProgressDrawable)).into(image);

        text.setText(imageTag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ASK_WRITE_ACCESS:
                downloadImage();
                break;
        }
    }

    public void downloadImage() {

        String imageUrl = getIntent().getExtras().getString("imageUrl");

        String downloadedImageName = generateRandomSting() + ".jpg";

        if (Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ASK_WRITE_ACCESS);
        }

        Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();

        final android.app.AlertDialog dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.setMessage("Downloading...");

        AndroidNetworking.download(imageUrl, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),downloadedImageName)
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {

                        dialog.show();

                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Toast.makeText(LargeImageView.this, "Download Completed!", Toast.LENGTH_SHORT).show();
                        if(dialog.isShowing())
                            dialog.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                    }
                });

    }

    public String generateRandomSting()   {
        String ALPHABETS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        while (str.length() < 10) {
            int index = (int)(random.nextFloat()*ALPHABETS.length());   //Used float for better random
            str.append(ALPHABETS.charAt(index));
        }
        return str.toString();
    }
}
