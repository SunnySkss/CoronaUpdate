package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.CoronaPojo;
import com.example.myapplication.GetDataService;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClientInstance;
import com.example.myapplication.adapter.CountryNameRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView myRecycler;
    private CountryNameRecyclerAdapter adapter;
    private ArrayList<String> countryName;//=new ArrayList<String>();
    private ArrayList<String> searchCountry;
    private ProgressDialog progressDoalog;
    private TextView totalInfect, totalRecover, totalDeath;
    private Toolbar toolbar;
    boolean search = false;
    private static final String FB_RC_KEY_LATEST_VERSION = "app_current_version";
    private static final String FB_RC_KEY_APP_URL = "app_play_store_url";
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private SearchBox mSearchbox;
    private ImageView mShowSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myRecycler = findViewById(R.id.recycler);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        totalInfect = findViewById(R.id.totalInfect);
        totalRecover = findViewById(R.id.totalRecover);
        totalDeath = findViewById(R.id.totalDeath);
        countryName = new ArrayList<String>();
        toolbar = findViewById(R.id.toolbar);
        mSearchbox = findViewById(R.id.searchbox);
        mSearchbox.setLogoText("Search for country");
       // mSearchbox.revealFromMenuItem(R.id.appSearchBar, this);
        mShowSearch  = findViewById(R.id.showSearch);
        mShowSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchbox.setVisibility(View.VISIBLE);
            }
        });
        mSearchbox.setAnimateDrawerLogo(false);
        mSearchbox.setDrawerLogo(R.mipmap.ic_launcher);
        mSearchbox.setOverflowMenu(R.menu.overflow_menu);
        mSearchbox.setOverflowMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.test_menu_item:
                        shareApp();
                        return true;
                }
                return false;
            }
        });
        mSearchbox.setSearchListener(new SearchBox.SearchListener(){

            @Override
            public void onSearchOpened() {

            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
                Log.e("Search ","Close");
            }

            @Override
            public void onSearchTermChanged(String s) {
                if (s.length() > 0) {
                    searchCountry = new ArrayList<>();
                    for (int i = 0; i < countryName.size(); i++) {
                        if (((ArrayList) ((Object) countryName.get(i))).get(0).toString().toLowerCase().contains(s.toLowerCase())) {
                            searchCountry.add(countryName.get(i));
                        }
                    }
                    adapter.filterList(searchCountry);
                }
                if (s.length() <= 0) {
                    adapter.filterList(countryName);
                }
            }




            @Override
            public void onSearch(String searchTerm) {
                Log.e("Search ","Search");
            }

            @Override
            public void onResultClick(SearchResult searchResult) {

            }




            @Override
            public void onSearchCleared() {
                Log.e("Search ","Cleared");

            }

        });

    }

    public void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Corona Live");
            String shareMessage = "\nCorona Live Update.\n\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {

        }
    }

    public void fetchData() {
        progressDoalog = new ProgressDialog(HomeActivity.this);
        progressDoalog.setMessage("Live Corona Data.....");
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<CoronaPojo> call = service.getCoronaData();
        call.enqueue(new Callback<CoronaPojo>() {
            @Override
            public void onResponse(Call<CoronaPojo> call, Response<CoronaPojo> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    totalDeath.setText(response.body().getCorona_cases().get(1).trim());
                    totalInfect.setText(response.body().getCorona_cases().get(0).trim());
                    totalRecover.setText(response.body().getCorona_cases().get(2).trim());
                    setAdapter(((ArrayList) response.body().getCorona()));
                }
            }

            @Override
            public void onFailure(Call<CoronaPojo> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(HomeActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean dataExists = false;

    public void setAdapter(ArrayList<String> country_list) {
        try {
            countryName.add(country_list.get(0));
            for (int i = 0; i < country_list.size(); i++) {
                dataExists = false;
                for (int country = 0; country < countryName.size(); country++) {
                    if (((ArrayList) ((Object) country_list.get(i))).get(0).toString().trim().toLowerCase().
                        equals(((ArrayList) ((Object) countryName.get(country))).get(0).toString().trim().toLowerCase())) {
                        dataExists = true;
                    }
                }
                if (!dataExists) {
                    countryName.add(country_list.get(i));
                }
            }
            adapter = new CountryNameRecyclerAdapter(this, countryName);
            myRecycler.setAdapter(adapter);
            progressDoalog.dismiss();
        } catch (Exception ex) {
            progressDoalog.dismiss();
            Toast.makeText(this, "Please try after some time.", Toast.LENGTH_SHORT).show();
        }

    }

    private void forceUpdateConfig() {
        progressDoalog = new ProgressDialog(HomeActivity.this);
        progressDoalog.setMessage("Checking for app update...");
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.show();
        final int versionCode = BuildConfig.VERSION_CODE;

        final HashMap<String, Object> defaultMap = new HashMap<>();
        defaultMap.put(FB_RC_KEY_LATEST_VERSION, "" + versionCode);
        defaultMap.put(FB_RC_KEY_APP_URL, FB_RC_KEY_APP_URL);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build());

        mFirebaseRemoteConfig.setDefaults(defaultMap);

        Task<Void> fetchTask = mFirebaseRemoteConfig.fetch(BuildConfig.DEBUG ? 0 : TimeUnit.HOURS.toSeconds(4));

        fetchTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDoalog.dismiss();
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched();

                    int latestAppVersion = Integer.parseInt(getValue(FB_RC_KEY_LATEST_VERSION, defaultMap));
                    String appUrl = getValue(FB_RC_KEY_APP_URL, defaultMap);

                    if (latestAppVersion > versionCode) {
                        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("App Update")
                            .setCancelable(false)
                            .setMessage("Please Update app for latest update.")
                            .setPositiveButton("Update",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                    }
                                }).setNegativeButton("No, thanks",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                            .show();
                    } else {
                        fetchData();
                    }

                } else {
                    Toast.makeText(HomeActivity.this, "Fetch Failed",
                        Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String getValue(String parameterKey, HashMap<String, Object> defaultMap) {
        String value = mFirebaseRemoteConfig.getString(parameterKey);
        if (TextUtils.isEmpty(value))
            value = (String) defaultMap.get(parameterKey);

        return value;
    }

    @Override
    protected void onResume() {
        super.onResume();
        forceUpdateConfig();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
