package com.lenovo.market.view;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.lenovo.market.R;
import com.lenovo.market.activity.home.PictureViewActivity;
import com.lenovo.market.activity.setting.friendscircle.SavePicActivity;
import com.lenovo.market.adapter.GalleryAdapter;
import com.lenovo.market.common.MarketApp;

public class CustomFragment extends Fragment implements LoaderCallbacks<ArrayList<String>> {

    private CustomGallery gallery;
    private GalleryAdapter mAdapter;

    public GalleryAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_view, null);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gallery = (CustomGallery) view.findViewById(R.id.pic_gallery);
        gallery.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
        gallery.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
        gallery.setDetector(new GestureDetector(getActivity(), new MySimpleGesture()));
        mAdapter = new GalleryAdapter(gallery);
        gallery.setAdapter(mAdapter);
        gallery.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // Toast.makeText(getActivity(), "LongClick唤起复制、保存操作", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SavePicActivity.class);
                startActivity(intent);
                return false;
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    private class MySimpleGesture extends SimpleOnGestureListener {
        // 按两下的第二下Touch down时触发
        public boolean onDoubleTap(MotionEvent e) {

            View view = gallery.getSelectedView();
            if (view instanceof CustomImageView) {
                CustomImageView imageView = (CustomImageView) view;
                if (imageView.getScale() > imageView.getMiniZoom()) {
                    imageView.zoomTo(imageView.getMiniZoom());
                } else {
                    imageView.zoomTo(imageView.getMaxZoom());
                }
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            PictureViewActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
            return true;
        }
    }

    @Override
    public PictureLoader onCreateLoader(int arg0, Bundle arg1) {
        return new PictureLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> arg0, ArrayList<String> arg1) {
        mAdapter.setData(arg1);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> arg0) {
        mAdapter.setData(null);
    }
}
