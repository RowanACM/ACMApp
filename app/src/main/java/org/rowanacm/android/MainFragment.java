package org.rowanacm.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.us.acm.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import org.rowanacm.android.attendance.AttendanceActivity;

import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private WebView webView;
    private final static String ACM_ATTENDANCE_URL = "https://acm-attendance.firebaseapp.com/";


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance() {
        return new MainFragment();
    }


    private void initWebView() {
        webView = (WebView) getActivity().findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setVerticalScrollBarEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl(ACM_ATTENDANCE_URL); //initial page load

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);

        Button signInButton = (Button) rootView.findViewById(R.id.attendance_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivity(AttendanceActivity.class);
            }
        });


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String section = getActivity().getIntent().getStringExtra("section");
        if(section != null && section.equals("attendance")) {
            switchActivity(AttendanceActivity.class);
        }
        ButterKnife.bind(getActivity());
        initWebView();
    }


    private void switchActivity(Class newActivity) {
        Intent intent = new Intent(getContext(), newActivity);
        startActivity(intent);
    }
}
