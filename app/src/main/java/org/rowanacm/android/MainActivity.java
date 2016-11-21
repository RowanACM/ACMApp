package org.rowanacm.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.us.acm.R;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * The main activity of the app
 * @author Rowan University Mobile Application Development
 */
public class MainActivity extends AppCompatActivity {

    private final static String ACM_ATTENDANCE_URL = "https://acm-attendance.firebaseapp.com/";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String section = getIntent().getStringExtra("section");
        if(section != null && section.equals("attendance")) {
            switchActivity(AttendanceActivity.class);
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initWebView();
    }

    @OnClick(R.id.attendance_button)
    protected void switchToAttendanceActivity() {
        switchActivity(AttendanceActivity.class);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(ACM_ATTENDANCE_URL); //initial page load

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void switchActivity(Class newActivity) {
        Intent intent = new Intent(this, newActivity);
        startActivity(intent);
    }
    
}
