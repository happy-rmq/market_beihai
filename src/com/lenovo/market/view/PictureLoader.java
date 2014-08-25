package com.lenovo.market.view;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class PictureLoader extends AsyncTaskLoader<ArrayList<String>> {

    public static ArrayList<String> dataResult;

    public PictureLoader(Context context) {
        super(context);
        dataResult = new ArrayList<String>();
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {
        return dataResult;
    }
}
