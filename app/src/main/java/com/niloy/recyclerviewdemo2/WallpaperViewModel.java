package com.niloy.recyclerviewdemo2;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WallpaperViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Wallpaper>> mWallpapers;

    private WallpapersDataRepo wallpapersDataRepo = new WallpapersDataRepo();

    public LiveData<ArrayList<Wallpaper>> getWallpapers() {
        if (mWallpapers == null) {
            mWallpapers = new MutableLiveData<>();
            loadWallpapers();
        }
        return mWallpapers;
    }

    private void loadWallpapers() {

        /*
         * All above answers are correct. But one more important difference.
         * If you call postValue() on field that has no observers and after that you call getValue(),
         * you don't receive the value that you set in postValue().
         * So be careful if you work in background threads without observers.*/

        mWallpapers.setValue(wallpapersDataRepo.getData());

        wallpapersDataRepo.setDataChangListener(new WallpapersDataRepo.DataChangeListener() {
            @Override
            public void onDataChanged(ArrayList<Wallpaper> wallpapersList) {
                mWallpapers.setValue(wallpapersList);
            }
        });
    }
}
