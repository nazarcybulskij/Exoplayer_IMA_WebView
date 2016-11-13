package com.inverita.nazarko.videowebproject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import com.google.android.libraries.mediaframework.exoplayerextensions.Video;
import com.google.android.libraries.mediaframework.layeredvideo.PlaybackControlLayer;

import java.util.HashMap;

import adplayer.ImaPlayer;

public class  WebViewActivity extends Activity{

    WebView mWebView;
    int  webViewposution;

    HashMap<PlaybackControlLayer.FullscreenCallback,ImaPlayer>  playersSet = new HashMap<>();

   // private ImaPlayer imaPlayer;
    //private FrameLayout videoPlayerContainer;
    AbsoluteLayout.LayoutParams layoutParams;

    public static final String CSS_STYLE = "<style>img{display: inline;height: auto;max-width: 100%%;}</style>"+
            "<style>iframe{display: inline;height: %d ;max-width: 100%%;}p{font-size:18;color:#212121}a{color:#2397f3}</style>"+
            "<style>video{display: inline}</style>";





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JCCallBack(), "jcvd");
       // mWebView.loadUrl("file:///android_asset/jcvd.html");

//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int heigh =convertPixelsToDp((int) ((width * 9 / 16)), WebViewActivity.this);
//        String style = String.format(CSS_STYLE, heigh);
        mWebView.loadUrl("file:///android_asset/jcvd.html");
        //mWebView.loadDataWithBaseURL("jcvd", style+str, "text/html", "UTF-8", null);
        //videoPlayerContainer = (FrameLayout) getLayoutInflater().inflate(R.layout.frame, null);
    }

    public static int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }

    public class JCCallBack {

        @JavascriptInterface
        public void adViewJieCaoVideoPlayer(final int width, final int height, final int top, final int left,final String url) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    ViewGroup.LayoutParams ll = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams = new AbsoluteLayout.LayoutParams(ll);
                    layoutParams.y = WebViewActivity.dip2px(WebViewActivity.this, top);
                    layoutParams.x = WebViewActivity.dip2px(WebViewActivity.this, left);
                    layoutParams.height = WebViewActivity.dip2px(WebViewActivity.this, height);
                    layoutParams.width = WebViewActivity.dip2px(WebViewActivity.this, width);

                    VideoListItem item = new VideoListItem("",
                            new Video(url,
                                    Video.VideoType.MP4,
                                    "bf5bb2419360daf1"), "");

                    FrameLayout  videoPlayerContainer =  (FrameLayout) getLayoutInflater().inflate(R.layout.frame, null);
                    createImaPlayer(item,videoPlayerContainer);


                    mWebView.addView(videoPlayerContainer,layoutParams);

                }
            });

        }
    }

    @Override
    protected void onStop() {
        for(ImaPlayer temp:playersSet.values()){
            temp.pause();
        }
        super.onStop();
    }


    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);

        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dpValue * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    /**
     * When a video has been selected, create an {@link ImaPlayer} and play the video.
     */
    public void createImaPlayer(VideoListItem videoListItem,FrameLayout videoPlayerContainer) {
        ImaPlayer imaPlayer = null;

        if (imaPlayer != null) {
            imaPlayer.release();
        }

        videoPlayerContainer.removeAllViews();
        videoPlayerContainer.setLayoutParams(layoutParams);
        String adTagUrl = videoListItem.adUrl;
        String videoTitle = videoListItem.title;

        imaPlayer = new ImaPlayer(this,
                videoPlayerContainer,
                videoListItem.video,
                videoTitle,
                adTagUrl);

        PlaybackControlLayer.FullscreenCallback  callback = new PlaybackControlLayer.FullscreenCallback() {
            @Override
            public void onGoToFullscreen() {
                webViewposution = mWebView.getScrollY();
                getActionBar().hide();
                mWebView.scrollTo(0,0);
            }

            @Override
            public void onReturnFromFullscreen() {
                getActionBar().show();
                mWebView.scrollTo(0,webViewposution);
            }
        };
        playersSet.put(callback,imaPlayer);
        imaPlayer.setFullscreenCallback(callback);
        imaPlayer.play();
    }


    public static class VideoListItem {

        /**
         * The title of the video.
         */
        public final String title;

        /**
         * The actual content video (contains its URL, media type - either DASH or mp4,
         * and an optional media type).
         */
        public final Video video;

        /**
         * The URL of the VAST document which represents the ad.
         */
        public final String adUrl;

        /**
         * @param title The title of the video.
         * @param video The actual content video (contains its URL, media type - either DASH or mp4,
         *                  and an optional media type).
         * @param adUrl The URL of the VAST document which represents the ad.
         */
        public VideoListItem(String title, Video video, String adUrl) {
            this.title = title;
            this.video = video;
            this.adUrl = adUrl;
        }
    }
}
