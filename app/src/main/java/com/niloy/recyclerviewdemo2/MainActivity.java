package com.niloy.recyclerviewdemo2;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private volatile boolean programFinished = false;

    ImageView noInterntImage;

    private static final String TAG = "MainActivity";

    private static long backKeyPressedTime;

    private RecyclerViewAdapter adapter;

    WallpaperViewModel mWallpaperViewModel;

    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noInterntImage = findViewById(R.id.no_internet);

        progressBar = findViewById(R.id.progress_bar_recycler);

        mWallpaperViewModel = ViewModelProviders.of(this).get(WallpaperViewModel.class);

        mWallpaperViewModel.getWallpapers().observe(this, new Observer<ArrayList<Wallpaper>>() {
            @Override
            public void onChanged(ArrayList<Wallpaper> wallpapers) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        initRecyclerView();

        /*if(adapter!=null && adapter.getItemCount()==0 && !isInternetAvailable()) {
            noInterntImage.setVisibility(View.VISIBLE);
        }
        else {
            noInterntImage.setVisibility(View.INVISIBLE);
        }*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        programFinished = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        programFinished = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        programFinished = false;
        checkInternetConnectionInBackground();
    }

    private void initRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(this);

        adapter.submitList(mWallpaperViewModel.getWallpapers().getValue());

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {

        if(backKeyPressedTime+2000>System.currentTimeMillis())  {
            super.onBackPressed();
            finish();
        }

        else {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isInternetAvailable() {

        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void checkInternetConnectionInBackground() {
        InternetCheckerRunnable runnable = new InternetCheckerRunnable();
        new Thread(runnable).start();
    }

    /*private int getItemIndex(String tag)  {
        for(int i = 0 ; i < mWallpaperList.size() ; i++)    {
            if(mWallpaperList.get(i).getTitle().equals(tag))   {
                return i;
            }
        }
        return -1;
    }*/

    class InternetCheckerRunnable implements Runnable {

        Handler handler = new Handler(Looper.getMainLooper());
        boolean connectionInturrupted;

        @Override
        public void run() {

            while (true) {

                if (programFinished) {
                    return;
                }

                boolean connectionIsAvailable = isInternetAvailable();

                if (!connectionIsAvailable && !connectionInturrupted) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                            connectionInturrupted = true;
                        }
                    });
                } else if (connectionIsAvailable) {
                    connectionInturrupted = false;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isInternetAvailable() {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(sockaddr, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}

