package com.niloy.recyclerviewdemo2;


import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mFirebaseData;

    private static final String TAG = "MainActivity";

    private static long backKeyPressedTime;

    private RecyclerViewAdapter adapter;

    private ArrayList<String> imageTexts = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseData = FirebaseDatabase.getInstance().getReference().child("TagAndLinks");

        addItemNameAndImage();

    }

    private void addItemNameAndImage()  {

        Log.d(TAG , "Adding Image And Texts");

        mFirebaseData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                imageTexts.add(dataSnapshot.getKey());
                mImageUrls.add(dataSnapshot.getValue(String.class));
                addToRecyclerView();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int i = imageTexts.indexOf(dataSnapshot.getKey());
                mImageUrls.set(i,dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i = imageTexts.indexOf(dataSnapshot.getKey());
                imageTexts.remove(i);
                mImageUrls.remove(i);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        imageTexts.add("Havasu Falls");

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        imageTexts.add("Trondheim");

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        imageTexts.add("Portugal");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        imageTexts.add("Rocky Mountain");


        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        imageTexts.add("Mahahual");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        imageTexts.add("Frozen Lake");


        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
        imageTexts.add("White Sands Desert");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        imageTexts.add("Austrailia");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        imageTexts.add("Washington");*/

    }

    private void addToRecyclerView()    {

        RecyclerView recyclerView = findViewById(R.id.recycler);
        adapter = new RecyclerViewAdapter(this,imageTexts,mImageUrls);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
}
