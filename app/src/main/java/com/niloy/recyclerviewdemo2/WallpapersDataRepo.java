package com.niloy.recyclerviewdemo2;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WallpapersDataRepo {

    private DatabaseReference mFirebaseData;

    private ArrayList<Wallpaper> mWallpaperList = new ArrayList<>();

    private DataChangeListener dataChangeListener;

    public WallpapersDataRepo() {
        this.dataChangeListener = null;
        loadData();
    }

    public ArrayList<Wallpaper> getData() {
        return mWallpaperList;
    }

    public void setDataChangListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    public void loadData() {

        mFirebaseData = FirebaseDatabase.getInstance().getReference().child("TagAndLinks");

        mFirebaseData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mWallpaperList.add(new Wallpaper(dataSnapshot.getKey(), dataSnapshot.getValue(String.class)));

                //Telling The listeners that data has changed
                if (dataChangeListener != null) {
                    dataChangeListener.onDataChanged(mWallpaperList);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int i = getItemIndex(dataSnapshot.getKey());
                mWallpaperList.set(i, new Wallpaper(dataSnapshot.getKey(), dataSnapshot.getValue(String.class)));
                if (dataChangeListener != null) {
                    dataChangeListener.onDataChanged(mWallpaperList);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i = getItemIndex(dataSnapshot.getKey());
                mWallpaperList.remove(i);
                if (dataChangeListener != null) {
                    dataChangeListener.onDataChanged(mWallpaperList);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(String tag) {
        for (int i = 0; i < mWallpaperList.size(); i++) {
            if (mWallpaperList.get(i).getTitle().equals(tag)) {
                return i;
            }
        }
        return -1;
    }

    interface DataChangeListener {
        void onDataChanged(ArrayList<Wallpaper> wallpapersList);
    }
}
