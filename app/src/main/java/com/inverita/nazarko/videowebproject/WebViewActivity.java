package com.inverita.nazarko.videowebproject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import com.google.android.libraries.mediaframework.exoplayerextensions.Video;
import com.google.android.libraries.mediaframework.layeredvideo.PlaybackControlLayer;


import adplayer.ImaPlayer;

public class  WebViewActivity extends Activity implements PlaybackControlLayer.FullscreenCallback{

    WebView mWebView;
    int  webViewposution;

    private ImaPlayer imaPlayer;
    private FrameLayout videoPlayerContainer;
    AbsoluteLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JCCallBack(), "jcvd");
        mWebView.loadUrl("file:///android_asset/jcvd.html");
        videoPlayerContainer = (FrameLayout) getLayoutInflater().inflate(R.layout.frame, null);
    }

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
                                    "bf5bb2419360daf1"),
                            "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/" +
                                    "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast" +
                                    "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct" +
                                    "%3Dskippablelinear&correlator=");

                    createImaPlayer(item);

                    mWebView.addView(videoPlayerContainer,layoutParams);

                }
            });

        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * When a video has been selected, create an {@link ImaPlayer} and play the video.
     */
    public void createImaPlayer(VideoListItem videoListItem) {
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
        imaPlayer.setFullscreenCallback(this);
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
