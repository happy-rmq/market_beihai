package com.lenovo.xjpsd.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.lenovo.market.R;
import com.lenovo.xjpsd.activity.WebViewActivity;
import com.lenovo.xjpsd.utils.CommonUtils;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that contain this fragment must implement the {@link ItemFragment.OnFragmentInteractionListener} interface to handle interaction events. Use the {@link ItemFragment#newInstance} factory method to create an instance of this fragment.
 */
@SuppressLint({"SetJavaScriptEnabled", "HandlerLeak"})
public class ItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TITLE = "title";
    private static final String URL = "url";

    // TODO: Rename and change types of parameters
    private String mUrl;

    private OnFragmentInteractionListener mListener;
    private WebView mWebView;
    private ImageView fragment_item_img;
    private RelativeLayout fragment_item_rl;
    private ProgressBar progressBar1;
    private Timer timer;
    private boolean blean;
    private int num;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            progressBar1.setVisibility(View.GONE);
            fragment_item_rl.setVisibility(View.VISIBLE);
            System.out.println("newProgress=" + num);
            mWebView.stopLoading();
        }

        ;
    };

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param title 标题.
     * @param url   内容url.
     * @return A new instance of fragment ItemFragment.
     */
    public static ItemFragment newInstance(String title, String url) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public ItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView", "----------------");
        timer = new Timer();
        View view = inflater.inflate(R.layout.psd_fragment_item, container, false);
        mWebView = (WebView) view.findViewById(R.id.webView);
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
        fragment_item_img = (ImageView) view.findViewById(R.id.fragment_item_img);
        fragment_item_rl = (RelativeLayout) view.findViewById(R.id.fragment_item_rl);
        fragment_item_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(mUrl) && CommonUtils.isOpenNetwork(getActivity())) {
                    mWebView.loadUrl(mUrl);
                    progressBar1.setVisibility(View.VISIBLE);
                    fragment_item_rl.setVisibility(View.GONE);
                } else {
                    fragment_item_rl.setVisibility(View.VISIBLE);
                    CommonUtils.showToast(getActivity(), "网络不给力，请调整好您的网络！");
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("onActivityCreated", "----------------");
        initWebView();
        if (!TextUtils.isEmpty(mUrl) && CommonUtils.isOpenNetwork(getActivity())) {
            mWebView.loadUrl(mUrl);
        } else {
            fragment_item_rl.setVisibility(View.VISIBLE);
            CommonUtils.showToast(getActivity(), "网络不给力，请调整好您的网络！");
        }
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        // 加载含有JS界面
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(RenderPriority.HIGH);
        mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("shouldOverrideUrlLoading", "----------------url");
                System.out.println("url++=" + ItemFragment.this.toString());
                if (url.equals("http://wapbaike.baidu.com/?adapt=1&")) {
                    view.loadUrl(url);
                } else {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    String authname = "";
                    // if (null != CommonUtils.UMODEL) {
                    // authname = CommonUtils.UMODEL.getUserName();
                    // }
                    intent.putExtra("url", url + "&authname=" + authname);
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("url=" + url);
                // Intent intent=new Intent(getActivity(),WebViewActivity.class);
                // intent.putExtra("url", url);
                // startActivity(intent);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                Log.d("onProgressChanged", "----------------" + newProgress);
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar1.setVisibility(View.GONE);
                    fragment_item_rl.setVisibility(View.GONE);
                    blean = true;
                }
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (!blean) {
                            num = newProgress;
                            handler.sendEmptyMessage(0);
                        }
                    }
                }, 10000);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an interaction in this fragment to be communicated to the activity and potentially other fragments contained in that activity.
     * <p/>
     * See the Android Training lesson <a href= "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
