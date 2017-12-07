package smoochie.multitaskphc;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.yalantis.phoenix.PullToRefreshView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements  ParentRequestInterface, NavigationView.OnNavigationItemSelectedListener {



    private SectionsPagerAdapter mSectionsPagerAdapter;


    private CustomViewPager mViewPager;
    private CustomWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),mViewPager, this);


        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            webView.loadUrl("http://facebook.com");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setViewPagerStatus(Boolean b) {
        mViewPager.setPagingEnabled(b);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Handle the camera action
            webView.loadUrl("https://m.facebook.com/");
        }

        return false;
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        String current_page_url = "";
        int sectionNumber;
        MainActivity activity;
        CustomViewPager viewpager;
        MainActivity parentActivity;
        private PullToRefreshView mPullToRefreshView;



        public void setActivity(MainActivity activity) {
            this.activity = activity;
        }

        public void setPager(CustomViewPager viewpager) {
            this.viewpager = viewpager;
        }

        public PlaceholderFragment() {

        }
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);

            fragment.setArguments(args);



            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle arguments = getArguments();
            sectionNumber = arguments.getInt(ARG_SECTION_NUMBER);
            parentActivity = (MainActivity) getActivity();
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_to_refresh);
            final CustomWebView webView = rootView.findViewById(R.id.webView);
            webView.setFragment(this);
            AdBlocker.init(this);
            //load test url
            final String[] current_page_url = {""};
            switch (sectionNumber) {
                case 1:
                    current_page_url[0] = "https://www.phcorner.net/forums";
                    break;
                case 2:
                    current_page_url[0] = "http://www.phcorner.net";
                    break;
                case 3:
                    current_page_url[0] = "https://www.phcorner.net/activity";
                    break;
            }


            WebSettings settings = webView.getSettings();
           // webView.setWebChromeClient(new WebChromeClient());
            //webView.setWebViewClient(new WebViewClient());
            settings.setJavaScriptEnabled(true);
            webView.setScrollContainer(false);
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            settings.setBuiltInZoomControls(true);
            settings.setSupportZoom(true);
            settings.setDisplayZoomControls(false);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            webView.loadUrl(current_page_url[0]);
            if (getActivity().getIntent().getExtras() != null) {
                current_page_url[0] =getActivity().getIntent().getStringExtra("url");
            }


            final String[] finalCurrent_page_url = {current_page_url[0]};
            mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {


                @Override
                public void onRefresh() {
                    mPullToRefreshView.setRefreshing(false);
                    webView.loadUrl(finalCurrent_page_url[0]);
                }
            });

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    finalCurrent_page_url[0] = url;

                    Toast.makeText(getContext(), "test if working", Toast.LENGTH_SHORT).show();

                }

                private Map<String, Boolean> loadedUrls = new HashMap<>();

                @SuppressWarnings("deprecation")
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    boolean ad;
                    if (!loadedUrls.containsKey(url)) {
                        ad = AdBlocker.isAd(url);
                        loadedUrls.put(url, ad);
                    } else {
                        ad = loadedUrls.get(url);
                    }
                    return ad ? AdBlocker.createEmptyResource() :
                            super.shouldInterceptRequest(view, url);
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    //  webView.loadUrl(url);
                }
            });
            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String
                        mimeType, long contentLength) {



                    try {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setMimeType(mimeType);
                        //------------------------COOKIE!!------------------------
                        String cookies = CookieManager.getInstance().getCookie(url);
                        request.addRequestHeader("cookie", cookies);
                        //------------------------COOKIE!!------------------------
                        request.addRequestHeader("User-Agent", userAgent);
                        request.setDescription("Downloading file...");
                        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                        DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        Toast.makeText(getContext(), "Downloading File", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        if (ContextCompat.checkSelfPermission(getContext(),
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        110);
                            } else {

                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        110);
                            }
                        }
                    }





                }
            });





            return rootView;
        }



    public void setViewPager(boolean b) {
        parentActivity.setViewPagerStatus(b);
    }
}


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        CustomViewPager viewPager;
        MainActivity activity;
        public SectionsPagerAdapter(FragmentManager fm, CustomViewPager viewPager, MainActivity activity) {
            super(fm);
            this.viewPager = viewPager;
            this.activity = activity;
        }
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment fragment=PlaceholderFragment.newInstance(position + 1);
            fragment.setActivity(activity);
            fragment.setPager(viewPager);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
